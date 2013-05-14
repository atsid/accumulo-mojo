package com.atsid.runner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.accumulo.start.Main;
import org.apache.commons.lang.StringUtils;

public abstract class BaseAccumuloRunner extends AbstractServerTestRunner {

	private List<String> classpathEntries;
	private File baseDirectory;

	public BaseAccumuloRunner() {
		super();
	}

	public BaseAccumuloRunner(File baseDirectory, List<String> classpathEntries) {
		super();
		this.classpathEntries = classpathEntries;
		this.baseDirectory = baseDirectory;
	}

	protected ProcessBuilder initProcessBuilder(Collection<String> jvmArgs,
			Collection<String> args) throws Exception {
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
		classpath = enhanceClasspathWithConfig(classpath);
		ProcessBuilder processBuilder = new ProcessBuilder(javaBin);
		if (jvmArgs != null) {
			processBuilder.command().addAll(jvmArgs);
		}
		processBuilder.command().addAll(
				Arrays.asList("-cp", classpath, Main.class.getCanonicalName()));
		processBuilder.command().addAll(args);
		processBuilder.environment().put("ACCUMULO_HOME",
				baseDirectory.getCanonicalPath());
		processBuilder.environment().put("HADOOP_HOME",
				baseDirectory.getCanonicalPath());
		processBuilder.environment().put("ZOOKEEPER_HOME",
				baseDirectory.getCanonicalPath());
		processBuilder.environment().put("tserver.memory.maps.max", "1024M");
		return processBuilder;
	}

	protected String enhanceClasspathWithConfig(String classpath)
			throws IOException {
		String configDirectory = baseDirectory.getCanonicalPath()
				+ File.separator + "conf" + File.separator;
		StringBuilder builder = new StringBuilder(classpath);
		builder.append(File.pathSeparator).append(configDirectory);
		return builder.toString();
	}

	public List<String> getClasspathEntries() {
		return classpathEntries;
	}

	public void setClasspathEntries(List<String> classpathEntries) {
		this.classpathEntries = classpathEntries;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

}
