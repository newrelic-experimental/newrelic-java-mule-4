package com.newrelic.mule.core4_3.tracers;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.trace.TransactionGuidFactory;
import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.tracers.AbstractTracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.TracerFlags;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.api.agent.NewRelic;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class MuleThresholdTracer extends AbstractTracer {
	
	public static final int DEFAULT_TRACER_FLAGS = 6;

	public static final String BACKTRACE_PARAMETER_NAME = "backtrace";

	private final long startTime;

	private final long endTime;

	private final long duration;

	private Tracer parentTracer;

	private final TransactionActivity txa;

	private boolean isParent;

	private byte tracerFlags;

	private MetricNameFormat metricNameFormat;

	private long exclusiveDuration = 0L;

	private int childCount;

	private boolean childHasStackTrace;

	private final ClassMethodSignature sig;

	private String guid;

	public MuleThresholdTracer(Transaction transaction, ClassMethodSignature sig, Object object, long start, long end, String metricName) {
		super(transaction);
		this.startTime = start;
		this.endTime = end;
		this.duration = (this.endTime >= this.startTime) ? (this.endTime - this.startTime) : 0L;
		setMetricName(new String[] { metricName });
		this.txa = transaction.getTransactionActivity();
		this.parentTracer = this.txa.getLastTracer();
		this.tracerFlags = 6;
		this.guid = TransactionGuidFactory.generate16CharGuid();
		this.metricNameFormat = (MetricNameFormat)new SimpleMetricNameFormat(metricName);
		this.sig = sig;
		this.txa.tracerStarted((Tracer)this);
	}

	public long getEndTime() {
		return this.endTime;
	}

	public void finish(Throwable throwable) {
		doFinish(191);
	}

	public void finish(int opcode, Object returnValue) {
		doFinish(opcode);
	}

	public long getDuration() {
		return this.duration;
	}

	private void doFinish(int opcode) {
		TransactionActivity txa = getTransactionActivity();
		if (txa == null)
			return; 
		Transaction transaction = txa.getTransaction();
		if (transaction != null && !transaction.getTransactionState().finish(transaction, (Tracer)this))
			return; 
		txa.lockTracerStart();
		try {
			attemptToStoreStackTrace();
			Tracer parentTracer = getParentTracer();
			if (parentTracer != null)
				parentTracer.childTracerFinished((Tracer)this); 
			if (transaction == null || transaction.isIgnore())
				return; 
			txa.getTransactionStats().getScopedStats().getOrCreateResponseTimeStats(this.metricNameFormat.getMetricName()).recordResponseTimeInNanos(getDuration(), getExclusiveDuration());
		} catch (Throwable t) {
			NewRelic.getAgent().getLogger().log(Level.FINE, t, "Failed in PegaActivityTracer.doFinish due to error");
		} finally {
			txa.tracerFinished((Tracer)this, opcode);
			txa.unlockTracerStart();
		} 
	}

	public void storeStackTrace() {
		setAgentAttribute("backtrace", Thread.currentThread().getStackTrace());
	}

	protected boolean shouldStoreStackTrace() {
		return isTransactionSegment();
	}

	private void attemptToStoreStackTrace() {
		if (getTransaction() != null && shouldStoreStackTrace()) {
			TransactionTracerConfig transactionTracerConfig = getTransaction().getTransactionTracerConfig();
			double stackTraceThresholdInNanos = transactionTracerConfig.getStackTraceThresholdInNanos();
			int stackTraceMax = transactionTracerConfig.getMaxStackTraces();
			if (getDuration() > stackTraceThresholdInNanos && (this.childHasStackTrace || 
					getTransaction().getTransactionCounts().getStackTraceCount() < stackTraceMax)) {
				storeStackTrace();
				if (!this.childHasStackTrace) {
					getTransaction().getTransactionCounts().incrementStackTraceCount();
					this.childHasStackTrace = true;
				} 
			} 
		} 
	}

	public long getStartTime() {
		return this.startTime;
	}

	public long getStartTimeInMilliseconds() {
		return TimeUnit.MILLISECONDS.convert(this.startTime, TimeUnit.NANOSECONDS);
	}

	public long getEndTimeInMilliseconds() {
		return TimeUnit.MILLISECONDS.convert(this.endTime, TimeUnit.NANOSECONDS);
	}

	public long getExclusiveDuration() {
		return this.exclusiveDuration;
	}

	public long getRunningDurationInNanos() {
		return (this.duration > 0L) ? this.duration : Math.max(0L, this.endTime - this.startTime);
	}

	public String getMetricName() {
		return this.metricNameFormat.getMetricName();
	}

	public String getTransactionSegmentName() {
		return this.metricNameFormat.getTransactionSegmentName();
	}

	public String getTransactionSegmentUri() {
		return this.metricNameFormat.getMetricName();
	}

	public void childTracerFinished(Tracer child) {
		if (child.isMetricProducer() && !(child instanceof com.newrelic.agent.tracers.SkipTracer)) {
			this.childCount++;
			this.exclusiveDuration -= child.getDuration();
			if (isTransactionSegment() && child.isTransactionSegment()) {
				this.isParent = true;
				if (child.isChildHasStackTrace())
					this.childHasStackTrace = true; 
			} 
		} 
	}

	public int getChildCount() {
		return this.childCount;
	}

	public Tracer getParentTracer() {
		return this.parentTracer;
	}

	public void setParentTracer(Tracer tracer) {
		this.parentTracer = tracer;
	}

	public boolean isParent() {
		return this.isParent;
	}

	public boolean isTransactionSegment() {
		return ((this.tracerFlags & 0x4) == 4);
	}

	public TransactionSegment getTransactionSegment(TransactionTracerConfig ttConfig, SqlObfuscator sqlObfuscator, long startTime, TransactionSegment lastSibling) {
		return new TransactionSegment(ttConfig, sqlObfuscator, startTime, (Tracer)this);
	}

	public void removeTransactionSegment() {
		this.tracerFlags = (byte)TracerFlags.clearSegment(this.tracerFlags);
	}

	public String getGuid() {
		return this.guid;
	}

	public long getDurationInMilliseconds() {
		return TimeUnit.MILLISECONDS.convert(this.duration, TimeUnit.NANOSECONDS);
	}

	public boolean isMetricProducer() {
		return ((this.tracerFlags & 0x2) == 2);
	}

	public void setMetricNameFormatInfo(String metricName, String transactionSegmentName, String transactionSegmentUri) {
		this.metricNameFormat = (MetricNameFormat)new SimpleMetricNameFormat(metricName, transactionSegmentName, transactionSegmentUri);
	}

	public void setMetricName(String... metricNameParts) {
		this.metricNameFormat = (MetricNameFormat)new SimpleMetricNameFormat(String.join("/", (CharSequence[])metricNameParts));
	}

	public ClassMethodSignature getClassMethodSignature() {
		return this.sig;
	}
}
