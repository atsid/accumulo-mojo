package com.atsid.mojo.testservers;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;

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

    /**
     * If true then the output of the zookeeper server will be dropped. If false
     * then the output of the zookeeper server will be written to the console.
     * Default is false.
     *
     * @parameter property="skipTests" default-value="false"
     */
    private boolean skipTests;

    /**
     * Set this to "true" to skip running integration tests, but still compile them. Its use is NOT RECOMMENDED, but
     * quite convenient on occasion.
     *
     * @parameter property="skipITs" default-value="false"
     */
    private boolean skipITs;

    /**
     * List of tables to create after Accumulo has been initialized. Default is an empty
     * list.
     *
     * <pre>
     * {@code
     * <configuration>
     *  <defaultTables>
     *   <param>tableOne</param>
     *   <param>tableTwo</param>
     *  </defaultTables>
     * </configuration>
     * }
     * </pre>
     *
     * @parameter property="defaultTables"
     */
    private List<String> defaultTables;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
        if(!this.isSkip()) {
            this.executeInternal();

            getLog().info("*********************************************");
            getLog().info("Accumulo started");
            getLog().info("*********************************************");
        } else {
            getLog().info( "Tests are skipped.  Not starting Accumulo." );
        }
    }

    private boolean isSkip() {
        return this.skipTests || this.skipITs;
    }

	@Override
	protected boolean getAccumuloQuiet() {
		return this.accumuloQuiet;
	}

	@Override
	protected boolean getZookeeperQuiet() {
		return this.zookeeperQuiet;
	}

    @Override
    protected List<String> getDefaultTables() {
        return this.defaultTables;
    }
}
