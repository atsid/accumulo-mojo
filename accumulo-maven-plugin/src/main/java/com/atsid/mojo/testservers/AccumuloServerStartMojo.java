package com.atsid.mojo.testservers;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * <pre>
 * Starts up accumulo so it can be run in a test environment.
 * 
 * This starts up the following services:
 * </pre>
 * <ol>
 * <li>Zookeeper</li>
 * <li>HDFS</li>
 * <li>Accumulo master server</li>
 * <li>Accumulo tablet server</li>
 * <li>Accumulo gc server</li>
 * <li>Accumulo logging server</li>
 * </ol>
 * 
 * @goal start-accumulo
 * @phase package
 * @requiresProject true
 */
public class AccumuloServerStartMojo extends BaseAccumuloServerMojo implements
		MojoMXBean {

	private static final String STOPPED = "stopped";
	private static final String STARTED = "started";

	/**
	 * If true then the output of the accumulo server will be dropped. If false
	 * then the output of the accumulo server will be written to the console.
	 * Default is false.
	 * 
	 * @parameter property="accumuloQuiet" default-value="false"
	 */
	private boolean accumuloQuiet;

	/**
	 * If true then the output of the zookeeper server will be dropped. If false
	 * then the output of the zookeeper server will be written to the console.
	 * Default is false.
	 * 
	 * @parameter property="zookeeperQuiet" default-value="false"
	 */
	private boolean zookeeperQuiet;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		this.executeInternal();

        getLog().info("*********************************************");
        getLog().info("Accumulo started");
        getLog().info("*********************************************");
    }

	@Override
	protected boolean getAccumuloQuiet() {
		return this.accumuloQuiet;
	}

	@Override
	protected boolean getZookeeperQuiet() {
		return this.zookeeperQuiet;
	}
}
