package com.atsid.runner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.accumulo.server.master.state.SetGoalState;

public class SetGoalStateRunner extends BaseAccumuloRunner {

	public SetGoalStateRunner(File baseDirectory, List<String> classpathEntries) {
		super(baseDirectory, classpathEntries);
	}

	@Override
	protected Process initServer() throws Exception {

		ProcessBuilder processBuilder = initProcessBuilder(null,
				Arrays.asList(SetGoalState.class.getCanonicalName(), "NORMAL"));
		return processBuilder.start();
	}
}
