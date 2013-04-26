package com.atsid.runner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AccumuloMonitorRunner extends BaseAccumuloRunner {

	public AccumuloMonitorRunner() {
		super();
	}

	public AccumuloMonitorRunner(File baseDirectory,
			List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
	}

	@Override
	protected Process initServer() throws Exception {

		return initProcessBuilder(null, Arrays.asList("monitor")).start();
	}

}
