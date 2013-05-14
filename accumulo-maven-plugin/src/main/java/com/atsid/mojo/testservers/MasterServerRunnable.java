package com.atsid.mojo.testservers;

import java.io.File;
import java.util.List;

import com.atsid.runner.AccumuloMasterRunner;

public class MasterServerRunnable implements ServerTestRunnerAwareRunnable<AccumuloMasterRunner> {

	private AccumuloMasterRunner masterServer;
	private String hostname;
	private File baseDirectory;
	private List<String> classpath;
    private boolean quiet;

	public MasterServerRunnable(String hostname, File baseDirectory, List<String> classpath, boolean quiet) {
		super();
		this.hostname = hostname;
		this.baseDirectory = baseDirectory;
		this.classpath = classpath;
        this.quiet = quiet;
	}

	public AccumuloMasterRunner getTestRunner() {
		return masterServer;
	}

	public void run() {
		try {

			masterServer = new AccumuloMasterRunner(hostname, baseDirectory, classpath);
            masterServer.setQuiet(quiet);
			masterServer.startupServer();
		} catch (Exception e) {
			throw new RuntimeException("Error running master server", e);
		}
	}
}
