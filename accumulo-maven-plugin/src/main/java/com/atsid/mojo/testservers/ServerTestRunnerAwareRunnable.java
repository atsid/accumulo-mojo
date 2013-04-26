package com.atsid.mojo.testservers;

public interface ServerTestRunnerAwareRunnable<T> extends Runnable {

	public T getTestRunner();
}
