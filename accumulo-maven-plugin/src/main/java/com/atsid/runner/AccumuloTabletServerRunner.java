package com.atsid.runner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AccumuloTabletServerRunner extends BaseAccumuloRunner {

	private String hostname;

	public AccumuloTabletServerRunner(String hostname, File baseDirectory,
			List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
		this.hostname = hostname;

	}

	public AccumuloTabletServerRunner(File baseDirectory,
			List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
	}

	@Override
	protected Process initServer() throws Exception {
		ProcessBuilder processBuilder = initProcessBuilder(
				Arrays.asList("-Xmx1500M"), Arrays.asList("tserver"));
		if (hostname != null) {
			processBuilder.command().add("-a");
			processBuilder.command().add(hostname);
		}
		return processBuilder.start();
	}
}
