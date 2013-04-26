package com.atsid.mojo.testservers;

import java.io.File;
import java.util.List;

import com.atsid.runner.AccumuloLoggerRunner;

public class LoggerServerRunnable implements ServerTestRunnerAwareRunnable<AccumuloLoggerRunner> {

	private AccumuloLoggerRunner runner;
	private File baseDirectory;
	private List<String> classpath;

	public LoggerServerRunnable(File baseDirectory, List<String> classpath) {
		super();
		this.baseDirectory = baseDirectory;
		this.classpath = classpath;
	}

	public AccumuloLoggerRunner getTestRunner() {
		return runner;
	}

	public void run() {
		try {
			runner = new AccumuloLoggerRunner(baseDirectory, classpath);
			runner.startupServer();
		} catch (Exception e) {
			throw new RuntimeException("Error running log server", e);
		}
	};

}
