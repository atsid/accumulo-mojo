package com.atsid.runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.plexus.util.FileUtils;

public class AccumuloInitRunner extends BaseAccumuloRunner {

	String instanceName = "accumulo";

	String password = "password";

	private int dfsRPCPort = 9000;

	private int zooPort = 2181;

	public AccumuloInitRunner(File baseDirectory, List<String> classPathEntries) {
		super(baseDirectory, classPathEntries);
	}

	@Override
	protected Process initServer() throws Exception {

		ProcessBuilder processBuilder = initProcessBuilder(null,
				Arrays.asList("init"));
		buildLibDir(getClasspathEntries(), getBaseDirectory()
				.getCanonicalPath());
		createDefaultHadoopConfig(getBaseDirectory());
		createDefaultAccumuloConfig(getBaseDirectory());

		Process proc = processBuilder.start();
		OutputStream prompt = proc.getOutputStream();
		prompt.write(String.format("%s\n", instanceName).getBytes());
		prompt.flush();
		prompt.write(String.format("%s\n", password).getBytes());
		prompt.flush();
		prompt.write(String.format("%s\n", password).getBytes());
		prompt.flush();

		return proc;
	}

	public AccumuloInitRunner(File baseDirectory,
			List<String> classpathEntries, String instanceName,
			String password, int dfsRPCPort, int zooPort) {
		this(baseDirectory, classpathEntries);
		this.instanceName = instanceName;
		this.password = password;
		this.dfsRPCPort = dfsRPCPort;
		this.zooPort = zooPort;
	}

	void buildLibDir(List<String> sourceFiles, String destination)
			throws IOException {
		for (String sourceFile : sourceFiles) {
			/* create the "HOME" directories for the various components */
			FileUtils.copyFileToDirectory(sourceFile, destination);
		}
	}

	private void writeProperties(File outputFile, Map<String, String> properties)
			throws XMLStreamException, IOException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		XMLStreamWriter writer = outputFactory
				.createXMLStreamWriter(new FileOutputStream(outputFile));
		writer.writeStartElement("configuration");
		for (Map.Entry<String, String> property : properties.entrySet()) {
			writer.writeStartElement("property");
			writer.writeStartElement("name");
			writer.writeCharacters(property.getKey());
			writer.writeEndElement();
			writer.writeStartElement("value");
			writer.writeCharacters(property.getValue());
			writer.writeEndElement();
			writer.writeEndElement();
		}
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();
	}

	void createDefaultHadoopConfig(File baseDirectory)
			throws XMLStreamException, IOException {
		String confPath = baseDirectory.getCanonicalPath() + File.separator
				+ "conf";
		FileUtils.mkdir(confPath);
		File output = new File(confPath, "core-site.xml");
		Map<String, String> props = new HashMap<String, String>();
		props.put("fs.default.name",
				String.format("hdfs://localhost:%d", dfsRPCPort));
		writeProperties(output, props);
	}

	void createDefaultAccumuloConfig(File baseDirectory)
			throws XMLStreamException, IOException {
		String confPath = baseDirectory.getCanonicalPath() + File.separator
				+ "conf";
		FileUtils.mkdir(confPath);
		String walogPath = baseDirectory.getCanonicalPath() + File.separator
				+ "walog";
		File walogDir = new File(walogPath);
		walogDir.mkdir();
		File output = new File(confPath, "accumulo-site.xml");
		Map<String, String> props = new HashMap<String, String>();
		props.put("instance.zookeeper.host",
				String.format("localhost:%d", zooPort));
		props.put("instance.dfs.dir", "/accumulo");
		props.put("logger.dir.walog", walogPath);
		props.put("tserver.memory.maps.native.enabled", "false");
		props.put("tserver.memory.maps.max", "256M");

		writeProperties(output, props);
	}

	public int getDfsRPCPort() {
		return dfsRPCPort;
	}

	public void setDfsRPCPort(int dfsRPCPort) {
		this.dfsRPCPort = dfsRPCPort;
	}

	public int getZooPort() {
		return zooPort;
	}

	public void setZooPort(int zooPort) {
		this.zooPort = zooPort;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
