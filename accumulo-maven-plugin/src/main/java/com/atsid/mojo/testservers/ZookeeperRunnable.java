package com.atsid.mojo.testservers;

import java.util.List;

import com.atsid.runner.ZookeeperTestRunner;

public class ZookeeperRunnable implements
		ServerTestRunnerAwareRunnable<ZookeeperTestRunner> {

	private ZookeeperTestRunner zookeeperTestRunner;

	private List<String> classpath;

	private int zookeeperPort;

	private boolean quiet;

	public ZookeeperRunnable(List<String> classpath, int zookeeperPort,
			boolean quiet) {
		super();
		this.classpath = classpath;
		this.zookeeperPort = zookeeperPort;
		this.quiet = quiet;
	}

	public ZookeeperTestRunner getTestRunner() {
		return zookeeperTestRunner;
	}

	public void run() {
		zookeeperTestRunner = new ZookeeperTestRunner(classpath, zookeeperPort);
		zookeeperTestRunner.setQuiet(quiet);
		try {
			zookeeperTestRunner.startupServer();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
