package com.nr.instrumentation.mule.http;

import java.lang.reflect.Method;
import java.util.logging.Level;

import com.newrelic.api.agent.ExtendedResponse;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;

public class NRExtendedResponse extends ExtendedResponse {
	
	Object response = null;
	
	public NRExtendedResponse(Object r) {
		response = r;
	}

	@Override
	public int getStatus() throws Exception {
		int status = 0;
		Method method = response.getClass().getMethod("getStatus");
		if(method != null) {
			status =  (int) method.invoke(response);
		}
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to NRExtendedResponse.getStatus(),returning {0}",status);
		return status;
	}

	@Override
	public String getStatusMessage() throws Exception {
		String reason = null;
		Method method = response.getClass().getMethod("getReasonPhrase");
		if(method != null) {
			reason = (String) method.invoke(response);
		}
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to NRExtendedResponse.getStatusMessage(),returning {0}",reason);
		return reason;
	}

	@Override
	public String getContentType() {
		String contentType = null;
		try {
			Method method = response.getClass().getMethod("getContentType");
			if(method != null) {
				contentType = (String) method.invoke(response);
			}
		} catch (Exception e) {
			
		}
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to NRExtendedResponse.getContentType(),returning {0}",contentType);
		return contentType;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public void setHeader(String name, String value) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to NRExtendedResponse.setHeader({0},{1})",name,value);
		try {
			Method method = response.getClass().getMethod("setHeader",String.class,String.class);
			if(method != null) {
				method.invoke(response,name,value);
			}
		} catch (Exception e) {
			
		}
	}

	@Override
	public long getContentLength() {
		long length = 0;
		try {
			Method method = response.getClass().getMethod("getContentLength");
			if(method != null) {
				length = (long) method.invoke(response);
			}
		} catch (Exception e) {
		} 
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to NRExtendedResponse.getContentLength(),returning {0}",length);
		return length;
	}

}
