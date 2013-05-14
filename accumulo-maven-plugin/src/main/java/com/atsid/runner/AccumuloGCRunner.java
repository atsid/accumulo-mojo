package com.atsid.runner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AccumuloGCRunner extends BaseAccumuloRunner {

	private String hostname;

	public AccumuloGCRunner(File baseDirectory, List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
	}

	public AccumuloGCRunner(String hostname, File baseDirectory,
			List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
		this.hostname = hostname;
	}

	@Override
	protected Process initServer() throws Exception {
		ProcessBuilder builder = initProcessBuilder(null, Arrays.asList("gc"));
		if (hostname != null) {
			builder.command().add("-a");
			builder.command().add(hostname);
		}
		return builder.start();
	}
}
