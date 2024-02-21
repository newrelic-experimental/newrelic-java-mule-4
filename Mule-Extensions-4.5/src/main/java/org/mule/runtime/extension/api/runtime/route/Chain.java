package org.mule.runtime.extension.api.runtime.route;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.mule.runtime.extension.api.runtime.operation.Result;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;

@SuppressWarnings("rawtypes")
@Weave(type=MatchType.Interface)
public abstract class Chain {

	@Trace(dispatcher=true)
	public abstract void process(final Consumer<Result> p0, final BiConsumer<Throwable, Result> p1);

	@Trace(dispatcher=true)
	public abstract void process(final Object p0, final Object p1, final Consumer<Result> p2, final BiConsumer<Throwable, Result> p3);

	@Trace(dispatcher=true)
	public abstract void process(final Result p0, final Consumer<Result> p1, final BiConsumer<Throwable, Result> p2);

}
