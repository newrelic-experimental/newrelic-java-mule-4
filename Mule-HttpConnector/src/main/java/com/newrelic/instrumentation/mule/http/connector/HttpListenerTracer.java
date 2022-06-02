package com.newrelic.instrumentation.mule.http.connector;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.newrelic.agent.Agent;
import com.newrelic.agent.MetricNames;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.deps.org.objectweb.asm.Opcodes;
import com.newrelic.agent.stats.ResponseTimeStats;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.trace.TransactionGuidFactory;
import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.tracers.AbstractTracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.SkipTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.TracerFlags;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.util.Strings;

public class HttpListenerTracer extends AbstractTracer {

	public static final MetricNameFormat NULL_METRIC_NAME_FORMATTER = new SimpleMetricNameFormat(null);

	private final long startTime;
	private long duration;
	private long exclusiveDuration;
	private MetricNameFormat metricNameFormat;
	private int childCount = 0;
	private boolean isParent;
	private Tracer parentTracer;
	private byte tracerFlags;
	private String guid;
	private final ClassMethodSignature classMethodSignature;
	private long endTime;
	private Object invocationTarget;

	private boolean childHasStackTrace;

	public HttpListenerTracer(Transaction transaction, ClassMethodSignature sig, Object object, MetricNameFormat format) {
		this(transaction, sig, object, format, DefaultTracer.DEFAULT_TRACER_FLAGS);
	}

	public HttpListenerTracer(Transaction transaction, ClassMethodSignature sig, Object object, MetricNameFormat format, int tracerflags) {
		super(transaction);
		startTime = System.nanoTime();
		metricNameFormat = format;
		guid = TransactionGuidFactory.generate16CharGuid();
		classMethodSignature = sig;
		invocationTarget = object;
		parentTracer = transaction.getTransactionActivity().getLastTracer();
	}

	public HttpListenerTracer(Transaction transaction, ClassMethodSignature sig, Object object) {
		this(transaction,sig,object,new SimpleMetricNameFormat("Custom/"+object.getClass().getSimpleName()+"/"+sig.getMethodName()),DefaultTracer.DEFAULT_TRACER_FLAGS);
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public TracedMethod getParentTracedMethod() {
		return parentTracer;
	}

	@Override
	public long getStartTimeInMillis() {
		return getStartTimeInMilliseconds();
	}

	@Override
	public long getStartTimeInMilliseconds() {
		return TimeUnit.MILLISECONDS.convert(startTime, TimeUnit.NANOSECONDS);
	}

	@Override
	public long getEndTime() {
		if(endTime == 0) endTime = System.nanoTime();
		return endTime;
	}

	@Override
	public long getEndTimeInMilliseconds() {
		return TimeUnit.MILLISECONDS.convert(getEndTime(), TimeUnit.NANOSECONDS);
	}

	@Override
	public long getExclusiveDuration() {
		return exclusiveDuration;
	}

	@Override
	public long getRunningDurationInNanos() {
		return duration > 0 ? duration : Math.max(0, System.nanoTime() - getStartTime());
	}

	@Override
	public String getMetricName() {
		return metricNameFormat == null ? null : metricNameFormat.getMetricName();
	}

	@Override
	public String getTransactionSegmentName() {
		return metricNameFormat == null ? null : metricNameFormat.getTransactionSegmentName();
	}

	@Override
	public String getTransactionSegmentUri() {
		return metricNameFormat == null ? null : metricNameFormat.getTransactionSegmentUri();
	}

	@Override
	public void childTracerFinished(Tracer child) {
		if (child.isMetricProducer() && !(child instanceof SkipTracer)) {
			childCount++;
			exclusiveDuration -= child.getDuration();
			if (isTransactionSegment() && child.isTransactionSegment()) {
				isParent = true;
				if (child.isChildHasStackTrace()) {
					childHasStackTrace = true;
				}
			}
		}
	}

	@Override
	protected final Object getInvocationTarget() {
		return invocationTarget;
	}

	@Override
	public int getChildCount() {
		return childCount;
	}

	@Override
	public Tracer getParentTracer() {
		return parentTracer;
	}

	@Override
	public void setParentTracer(Tracer tracer) {
		parentTracer = tracer;
	}

	@Override
	public boolean isParent() {
		return isParent;
	}

	@Override
	public boolean isTransactionSegment() {
//		return (tracerFlags & TracerFlags.TRANSACTION_TRACER_SEGMENT) == TracerFlags.TRANSACTION_TRACER_SEGMENT;
		return true;
	}

	@Override
	public TransactionSegment getTransactionSegment(TransactionTracerConfig ttConfig, SqlObfuscator sqlObfuscator,
			long startTime, TransactionSegment lastSibling) {
		return new TransactionSegment(ttConfig, sqlObfuscator, startTime, this);
	}

	@Override
	public void removeTransactionSegment() {
		this.tracerFlags = (byte) TracerFlags.clearSegment(this.tracerFlags);
	}

	@Override
	public String getGuid() {
		return guid;
	}

	@Override
	public long getDurationInMilliseconds() {
		return TimeUnit.MILLISECONDS.convert(getDuration(), TimeUnit.NANOSECONDS);
	}

	@Override
	public long getDuration() {
		if(duration <= 0) {
			if(endTime >= startTime) {
				duration = endTime - startTime;
			} else {
				duration = System.nanoTime() - startTime;
			}
		}
		return duration;
	}

	@Override
	public void finish(Throwable throwable) {

		Transaction tx = getTransaction();
		if(tx == null) {
			// This is either a serious internal error, or the application
			// used "@Trace(async = true)" and never called startAsyncActivity()
			// to associate with a Transaction. Either way, don't leak the
			// Activity as a stale ThreadLocal.
			TransactionActivity.clear();
			return;
		}
		setThrownException(throwable);
		tx.noticeTracerException(throwable, getGuid());

		if (!tx.getTransactionState().finish(tx, this)) {
			return;
		}
		try {
			getTransactionActivity().lockTracerStart();
		} catch (Throwable t) {
			String msg = MessageFormat.format("An error occurred finishing tracer for class {0} : {1}",
					classMethodSignature.getClassName(), t);
			if (Agent.LOG.isLoggable(Level.FINER)) {
				Agent.LOG.log(Level.WARNING, msg, t);
			} else {
				Agent.LOG.warning(msg);
			}
		} finally {
			getTransactionActivity().unlockTracerStart();
		}

		finish(Opcodes.ATHROW, null);

		if (Agent.isDebugEnabled()) {
			Agent.LOG.log(Level.FINE, "(Debug) Tracer.finish(Throwable)");
		}

	}

	@Override
	public void finish(int opcode, Object returnValue) {
		Agent.LOG.log(Level.FINE, "Call to HttpListenerTracer.finish({0},{1})", opcode,returnValue);
		endTime = System.nanoTime();
		TransactionActivity txa = getTransactionActivity();
		if (txa == null) {
			// Internal error - null txa is permitted for
			// a weird legacy Play instrumentation case
			Agent.LOG.log(Level.FINER, "Transaction activity for {0} was null", this);
			return;
		}

		// Get transaction from this tracer's txa.
		Transaction tx = getTransaction();
		if (tx != null && !tx.getTransactionState().finish(tx, this)) {
			return;
		}

		performFinishWork(endTime, opcode, returnValue);
	}

	public void performFinishWork(long finishTime, int opcode, Object returnValue) {
		// Believe it or not, it's possible to get a negative value!
		// (At least on some old, broken Linux kernels is is.)
		duration = Math.max(0, finishTime - getStartTime());
		exclusiveDuration += duration;
		if (exclusiveDuration < 0 || exclusiveDuration > duration) {
			Agent.LOG.log(Level.FINE, "Invalid exclusive time {0} for tracer {1}", exclusiveDuration,
					this.getClass().getName());
			exclusiveDuration = duration;
		}

		getTransactionActivity().lockTracerStart();
		try {
			try {
				attemptToStoreStackTrace();
			} catch (Throwable t) {
				if (Agent.LOG.isFinestEnabled()) {
					String msg = MessageFormat.format("An error occurred getting stack trace for class {0} : {1}",
							classMethodSignature.getClassName(), t.toString());
					Agent.LOG.log(Level.FINEST, msg, t);
				}
			}

			if (impactsParent(parentTracer)) {
				parentTracer.childTracerFinished(this);
			}

			try {
				recordMetrics(getTransactionActivity().getTransactionStats());
			} catch (Throwable t) {
				String msg = MessageFormat.format("An error occurred recording tracer metrics for class {0} : {1}",
						classMethodSignature.getClassName(), t.toString());
				Agent.LOG.severe(msg);
				Agent.LOG.log(Level.FINER, msg, t);
			}

			try {
				if (!(this instanceof SkipTracer)) {
					getTransactionActivity().tracerFinished(this, opcode);
				}
			} catch (Throwable t) {
				String msg = MessageFormat.format(
						"An error occurred calling Transaction.tracerFinished() for class {0} : {1}",
						classMethodSignature.getClassName(), t.toString());
				Agent.LOG.severe(msg);
				Agent.LOG.log(Level.FINER, msg, t);
			}
			reset();
		} finally {
			getTransactionActivity().unlockTracerStart();
		}
	}

	protected void reset() {
		invocationTarget = null;
	}


	protected void recordMetrics(TransactionStats transactionStats) {
		Agent.LOG.log(Level.FINE, "HttpListenerTracer - Call to recordMetrics");
		if (getTransaction() == null || getTransaction().isIgnore()) {
			return;
		}
		if (isMetricProducer()) {
			String metricName = getMetricName();
			Agent.LOG.log(Level.FINE, "HttpListenerTracer - Recording metrics for {0}", metricName);
			if (metricName != null) {
				// record the scoped metrics
				ResponseTimeStats stats = transactionStats.getScopedStats().getOrCreateResponseTimeStats(metricName);
				stats.recordResponseTimeInNanos(getDuration(), getExclusiveDuration());

				// there is now an unscoped metric for every scoped metric
				// the unscoped metric is created in the StatsEngineImpl

			}
			if (getRollupMetricNames() != null) {
				for (String name : getRollupMetricNames()) {
					ResponseTimeStats stats = transactionStats.getUnscopedStats().getOrCreateResponseTimeStats(name);
					stats.recordResponseTimeInNanos(getDuration(), getExclusiveDuration());
				}
			}
			if (getExclusiveRollupMetricNames() != null) {
				for (String name : getExclusiveRollupMetricNames()) {
					ResponseTimeStats stats = transactionStats.getUnscopedStats().getOrCreateResponseTimeStats(name);
					stats.recordResponseTimeInNanos(getExclusiveDuration(), getExclusiveDuration());
				}
			}
		}
	}


	private boolean impactsParent(Tracer parent) {
		return (parent != null && parent.getTransactionActivity() == this.getTransactionActivity());
	}

	protected boolean shouldStoreStackTrace() {
		return isTransactionSegment();
	}

	public void storeStackTrace() {
		setAgentAttribute(DefaultTracer.BACKTRACE_PARAMETER_NAME, Thread.currentThread().getStackTrace());
	}


	private void attemptToStoreStackTrace() {
		if (getTransaction() != null && shouldStoreStackTrace()) {
			TransactionTracerConfig transactionTracerConfig = getTransaction().getTransactionTracerConfig();
			double stackTraceThresholdInNanos = transactionTracerConfig.getStackTraceThresholdInNanos();
			int stackTraceMax = transactionTracerConfig.getMaxStackTraces();
			// you must be over the duration and either child has taken a stack trace or we are under the stack trace
			// count
			if ((getDuration() > stackTraceThresholdInNanos)
					&& (childHasStackTrace || (getTransaction().getTransactionCounts().getStackTraceCount() < stackTraceMax))) {
				storeStackTrace();
				// only increment the stack trace count if there are no children which have taken a stack trace
				if (!childHasStackTrace) {
					getTransaction().getTransactionCounts().incrementStackTraceCount();
					// this property is used to tell parents not to increment the stack trace count
					childHasStackTrace = true;
				}
			}
		}
	}


	@Override
	public void markFinishTime() {
		endTime = System.nanoTime();
	}

	@Override
	public boolean isMetricProducer() {
		//return (tracerFlags & TracerFlags.GENERATE_SCOPED_METRIC) == TracerFlags.GENERATE_SCOPED_METRIC;
		return true;
	}

	public void setMetricNameFormat(MetricNameFormat nameFormat) {
		metricNameFormat = nameFormat;
	}

	@Override
	public void setMetricNameFormatInfo(String metricName, String transactionSegmentName, String transactionSegmentUri) {
		MetricNameFormat format = new SimpleMetricNameFormat(metricName, transactionSegmentName, transactionSegmentUri);
		setMetricNameFormat(format);
	}

	@Override
	public void setMetricName(String... metricNameParts) {
		String metricName = Strings.join(MetricNames.SEGMENT_DELIMITER, metricNameParts);
		if (metricName != null) {
			setMetricNameFormat(new SimpleMetricNameFormat(metricName));
		}
		MetricNames.recordApiSupportabilityMetric(MetricNames.SUPPORTABILITY_API_SEGMENT_SET_METRIC_NAME);
	}

	@Override
	public ClassMethodSignature getClassMethodSignature() {
		return classMethodSignature;
	}

}
