package com.atsid.mojo.testservers;

import java.io.IOException;

import org.apache.accumulo.core.util.shell.Shell;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * <pre>
 * Runs an Accumulo shell.
 * 
 * @goal shell
 * @requiresProject false
 */
public class AccumuloShellMojo extends AbstractTestServerMojo implements MojoMXBean {

	/**
	 * Name of the Accumulo instance. Default value is "accumulo"
	 * 
	 * @parameter property="accumuloInstanceName" default-value="accumulo"
	 */
	private String accumuloInstanceName = null;

	/**
	 * Zookeeper server port. Default value is 2181.
	 * 
	 * @parameter property="zookeeperPort" default-value="2181"
	 */
	private int zookeeperPort = 0;

	/**
	 * Zookeeper host server name. Default value is "localhost."
	 * 
	 * @parameter property="zookeeperHost" default-value="localhost"
	 */
	private String zookeeperHost = null;

	/**
	 * Password for the Accumulo user "root." Default value is "password."
	 * 
	 * @parameter property="accumuloPassword" default-value="password"
	 */
	private String accumuloPassword = null;

	/**
	 * Username to use when connecting to Accumulo. Default value is "root."
	 * 
	 * @parameter property="accumuloUser" default-value="root"
	 */
	private String accumuloUser = null;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
            getLog().info("*********************************************");
            getLog().info("Starting Accumulo shell");
            getLog().info("*********************************************");

            String zookeeper = String.format("%s:%s", this.zookeeperHost, this.zookeeperPort);
			Shell shell = new Shell();
			shell.config("-u", this.accumuloUser, "-p", this.accumuloPassword, "-z", this.accumuloInstanceName,
					zookeeper);
			shell.start();
		} catch (IOException e) {
            getLog().error("Error running Accumulo shell", e);
			throw new MojoExecutionException("Error starting Accumulo shell", e);
		}
	}

	@Override
	public void shutdown() throws Exception {

	}
}
