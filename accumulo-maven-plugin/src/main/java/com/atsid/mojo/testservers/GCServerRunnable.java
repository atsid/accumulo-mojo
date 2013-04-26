package com.atsid.mojo.testservers;

import java.io.File;
import java.util.List;

import com.atsid.runner.AccumuloGCRunner;

public class GCServerRunnable implements ServerTestRunnerAwareRunnable<AccumuloGCRunner> {
	private AccumuloGCRunner gc;
	private File baseDirectory;
	private List<String> classpath;

	public GCServerRunnable(File baseDirectory, List<String> classpath) {
		super();
		this.baseDirectory = baseDirectory;
		this.classpath = classpath;
	}

	public AccumuloGCRunner getTestRunner() {
		return gc;
	}

	public void run() {
		try {
			gc = new AccumuloGCRunner(baseDirectory, classpath);
			gc.startupServer();
		} catch (Exception e) {
			throw new RuntimeException("Error running GC server", e);
		}
	}
}
