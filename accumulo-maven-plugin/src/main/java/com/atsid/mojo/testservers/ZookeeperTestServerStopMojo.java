package com.atsid.mojo.testservers;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.atsid.runner.ZookeeperTestRunner;

/**
 * @author jamesm
 * 
 * @goal stop-zookeeper
 * @phase post-integration-test
 */
public class ZookeeperTestServerStopMojo extends AbstractMojo {

	protected int servicePort;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName zookeeperName = new ObjectName(ZookeeperTestRunner.class
					.getPackage().getName()
					+ ":type="
					+ ZookeeperTestRunner.class.getSimpleName());
			server.invoke(zookeeperName, "shutdownServer", null, null);
			getLog().info("Zookeeper server shutdown");
		} catch (Exception e) {
			throw new MojoExecutionException("Error running shutdown", e);
		}
	}

}
