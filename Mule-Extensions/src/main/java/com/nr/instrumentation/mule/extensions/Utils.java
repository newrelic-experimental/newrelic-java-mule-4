package com.nr.instrumentation.mule.extensions;

import java.util.ArrayList;
import java.util.List;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.source.MessageSource;

public class Utils {
	
	private static List<String> appNames = new ArrayList<String>();
	private static Boolean autoNamingEnabled = null;
	
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
