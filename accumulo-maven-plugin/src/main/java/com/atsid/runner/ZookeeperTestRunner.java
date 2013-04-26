package com.atsid.runner;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ZookeeperTestRunner extends AbstractServerTestRunner {

	private List<String> classpathEntries;
	private Integer zooPort = 2181;

	public static void main(String[] args) throws Exception {
		ZookeeperTestRunner runner = new ZookeeperTestRunner();
		runner.registerAsMBean();
		Thread.sleep(Integer.MAX_VALUE);
	}

	public ZookeeperTestRunner() {

	}
	
	

	public ZookeeperTestRunner(List<String> classpathEntries, Integer zooPort) {
		super();
		this.classpathEntries = classpathEntries;
		this.zooPort = zooPort;
	}

	@Override
	protected Process initServer() throws Exception {
		File tempDir = File.createTempFile("zoo", "");
		tempDir.delete();
		tempDir.mkdir();
		tempDir.deleteOnExit();
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath;
		if (classpathEntries == null) {
			classpath = System.getProperty("java.class.path");
		} else {
			classpath = StringUtils.join(classpathEntries, File.pathSeparatorChar);
		}
		ProcessBuilder processBuilder = new ProcessBuilder(javaBin, "-cp", classpath,
				"org.apache.zookeeper.server.ZooKeeperServerMain", zooPort.toString(), tempDir.getAbsolutePath());
		return processBuilder.start();

	}

	public List<String> getClasspathEntries() {
		return classpathEntries;
	}

	public void setClasspathEntries(List<String> classpathEntries) {
		this.classpathEntries = classpathEntries;
	}

	public Integer getZooPort() {
		return zooPort;
	}

	public void setZooPort(Integer zooPort) {
		this.zooPort = zooPort;
	}
}
