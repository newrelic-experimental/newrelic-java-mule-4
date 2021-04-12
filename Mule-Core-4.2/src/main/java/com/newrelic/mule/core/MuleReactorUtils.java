package com.newrelic.mule.core;

import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

public class MuleReactorUtils {

	private static final String NRHOOKKEY = MuleReactorUtils.class.getName();

	public static boolean initialized = false;

	public static void init() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Hooks.resetOnLastOperator(NRHOOKKEY);
			}
		});
		setHook();
		initialized = true;
	}

	private static void setHook() {
		Hooks.onLastOperator(NRHOOKKEY, Operators.lift((scannable,coreSubscriber) -> {
			String name = scannable.name();
			if(!(coreSubscriber instanceof NRCoreSubscriber)) {
				return new NRCoreSubscriber<>(coreSubscriber,name);
			}
			return coreSubscriber;
		}
		));
	}

}
