package com.atsid.runner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AccumuloLoggerRunner extends BaseAccumuloRunner {

	public AccumuloLoggerRunner() {
		super();
	}

	public AccumuloLoggerRunner(File baseDirectory,
			List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
	}

	@Override
	protected Process initServer() throws Exception {
		return initProcessBuilder(null, Arrays.asList("logger")).start();
	}

}
