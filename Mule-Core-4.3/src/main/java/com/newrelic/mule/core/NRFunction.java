package com.newrelic.mule.core;

import java.util.function.Function;

public class NRFunction<T,R> implements Function<T, R> {

	private Function<T, R> actual = null;
	
	public NRFunction(Function<T, R> a) {
		actual = a;
	}
	
	@Override
	public R apply(T t) {
		return actual.apply(t);
	}

}
