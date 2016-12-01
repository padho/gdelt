package com.teslagov.gdelt;

/**
 * @author Kevin Chen
 */
public class DefaultGdeltConfiguration implements GdeltConfiguration {
	@Override
	public String getBaseURL() {
		return "http://data.gdeltproject.org/gdeltv2";
	}

	@Override
	public String getV2ServerURL() {
		return getBaseURL() + "/lastupdate.txt";
	}
}
