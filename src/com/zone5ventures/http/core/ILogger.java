package com.zone5ventures.http.core;

/** A simple logger interface - can be expanded in the future */
public interface ILogger {

	public void info(String fmt, Object ...args);
	public void error(Throwable t);
}
