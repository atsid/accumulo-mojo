package com.atsid.mojo.testservers;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal stop-accumulo
 * @phase verify
 * @author jamesm
 * 
 */
public class AccumuloServerStopMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		String className = AccumuloServerStartMojo.class.getSimpleName();

		ObjectName objectName;
		try {
			objectName = ObjectName.getInstance(String.format("%s:type=%s",
					AccumuloServerStartMojo.class.getPackage().getName(),
					className));
			server.invoke(objectName, "shutdown", null, null);
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Error shutting down accumulo mojo", e);
		}

	}
}
