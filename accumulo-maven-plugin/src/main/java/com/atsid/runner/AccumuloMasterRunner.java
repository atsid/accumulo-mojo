package com.atsid.runner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AccumuloMasterRunner extends BaseAccumuloRunner {

	private String hostname;

	public AccumuloMasterRunner(String hostname, File baseDirectory,
			List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
		this.hostname = hostname;
	}

	public AccumuloMasterRunner(File baseDirectory,
			List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
	}

	@Override
	protected Process initServer() throws Exception {
		ProcessBuilder processBuilder = initProcessBuilder(null,
				Arrays.asList("master"));
		if (hostname != null) {
			processBuilder.command().add("-a");
			processBuilder.command().add(hostname);
		}
		return processBuilder.start();
	}
}
