package com.atsid.runner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

public class ZookeeperTestRunner extends AbstractServerTestRunner {

	private List<String> classpathEntries;
	private Integer zooPort = 2181;
	private File zookeeperTemporaryDirectory;

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
		zookeeperTemporaryDirectory = File.createTempFile("zoo", "");
		zookeeperTemporaryDirectory.delete();
		zookeeperTemporaryDirectory.mkdir();

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator
				+ "java";
		String classpath;
		if (classpathEntries == null) {
			classpath = System.getProperty("java.class.path");
		} else {
			classpath = StringUtils.join(classpathEntries,
					File.pathSeparatorChar);
		}
		ProcessBuilder processBuilder = new ProcessBuilder(javaBin, "-cp",
				classpath, "org.apache.zookeeper.server.ZooKeeperServerMain",
				zooPort.toString(),
				zookeeperTemporaryDirectory.getAbsolutePath());
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

	@Override
	public void shutdownServer() throws Exception {
		super.shutdownServer();

		try {
			// We cannot use the normal file.deleteOnExit() because it will not
			// delete a non-empty directory. The Apache version will delete a
			// non-empty directory. We have to wait until after the scheduled
			// shutdown to invoke it because it traverses the directory tree and
			// schedules each file for deletion individually.
			FileUtils.forceDeleteOnExit(this.zookeeperTemporaryDirectory);
		} catch (IOException e) {
			throw new MojoExecutionException("Error deleting Zookeeper logs", e);
		}
	}
}
