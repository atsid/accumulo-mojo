package com.atsid.mojo.testservers;

import java.io.File;
import java.util.List;

import com.atsid.runner.AccumuloGCRunner;

public class GCServerRunnable implements ServerTestRunnerAwareRunnable<AccumuloGCRunner> {
	private AccumuloGCRunner gc;
	private File baseDirectory;
	private List<String> classpath;
    private boolean quiet;

	public GCServerRunnable(File baseDirectory, List<String> classpath, boolean quiet) {
		super();
		this.baseDirectory = baseDirectory;
		this.classpath = classpath;
        this.quiet = quiet;
	}

	public AccumuloGCRunner getTestRunner() {
		return gc;
	}

	public void run() {
		try {
			gc = new AccumuloGCRunner(baseDirectory, classpath);
            gc.setQuiet(this.quiet);
			gc.startupServer();
		} catch (Exception e) {
			throw new RuntimeException("Error running GC server", e);
		}
	}
}
