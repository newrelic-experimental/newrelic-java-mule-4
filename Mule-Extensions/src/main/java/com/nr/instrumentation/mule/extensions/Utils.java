package com.nr.instrumentation.mule.extensions;

import java.util.ArrayList;
import java.util.List;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.source.MessageSource;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;

public class Utils {
	
	private static List<String> appNames = new ArrayList<String>();
	private static Boolean autoNamingEnabled = null;
	
	
	public static void addAppName(String appName) {
		if(autoNamingEnabled == null) {
			Config config = NewRelic.getAgent().getConfig();
			if(config != null && config instanceof AgentConfig) {
				AgentConfig aConfig = (AgentConfig)config;
				autoNamingEnabled = aConfig.isAutoAppNamingEnabled();
			}
		}
		if(appName == null || autoNamingEnabled == null || !autoNamingEnabled || appNames.contains(appName)) return;
		
		appNames.add(appName);
		AgentBridge.getAgent().getTransaction(false).setApplicationName(ApplicationNamePriority.REQUEST_ATTRIBUTE, appName);
		
	}

	public static String getLocationName(ComponentLocation location) {
		if(location == null) return null;
		
		String locationName = location.getLocation();
		if(locationName !=null && !locationName.isEmpty()) {
			if(locationName.endsWith("/source")) {
				return locationName.replace("/source", "");
			}
			return locationName;
		}
		return null;
	}

	public static String getMessageLocation(MessageSource msgSource) {
		if(msgSource == null) return  null;
		
		ComponentLocation componentLoc = msgSource.getLocation();
		return getLocationName(componentLoc);
	}

}
