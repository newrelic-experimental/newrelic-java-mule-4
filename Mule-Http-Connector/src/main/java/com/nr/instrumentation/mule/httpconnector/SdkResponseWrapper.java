package com.nr.instrumentation.mule.httpconnector;

import org.mule.sdk.api.http.domain.message.response.HttpResponse;

import com.newrelic.api.agent.ExtendedResponse;
import com.newrelic.api.agent.HeaderType;

public class SdkResponseWrapper extends ExtendedResponse {

	private final HttpResponse response;

	public SdkResponseWrapper(HttpResponse response) {
		this.response = response;
	}

	@Override
	public String getContentType() {
		return response.getHeaderValue("Content-Type");
	}

	@Override
	public int getStatus() throws Exception {
		return response.getStatusCode();
	}

	@Override
	public String getStatusMessage() throws Exception {
		return response.getReasonPhrase();
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public void setHeader(String name, String value) {
		// HttpResponse is immutable by the time responseReady() fires - no-op.
	}

	@Override
	public long getContentLength() {
		String contentLength = response.getHeaderValue("Content-Length");
		if (contentLength != null) {
			try {
				return Long.parseLong(contentLength);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		return -1;
	}

}
