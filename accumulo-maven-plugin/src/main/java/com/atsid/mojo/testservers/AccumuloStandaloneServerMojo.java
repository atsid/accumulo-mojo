package com.atsid.mojo.testservers;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * <pre>
 * Starts up accumulo in a command-line environment.
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
 * @goal standalone
 * @phase package
 * @requiresProject false
 */
public class AccumuloStandaloneServerMojo extends BaseAccumuloServerMojo
		implements MojoMXBean {

	/**
	 * If true then the output of the accumulo server will be dropped. If false
	 * then the output of the accumulo server will be written to the console.
	 * Default is true.
	 * 
	 * @parameter property="accumuloQuiet" default-value="true"
	 */
	private boolean accumuloQuiet;

	/**
	 * If true then the output of the zookeeper server will be dropped. If false
	 * then the output of the zookeeper server will be written to the console.
	 * Default is true.
	 * 
	 * @parameter property="zookeeperQuiet" default-value="true"
	 */
	private boolean zookeeperQuiet;

	/**
	 * If true then a heartbeat message that says
	 * "Accumulo running ... press CTRL-C to quit" will be output to the
	 * command-line every thirty seconds. The original "Accumulo started"
	 * message may get pushed off the screen by the server log messages. Default
	 * value is "true."
	 * 
	 * @parameter property="loopShutdownMessage" default-value="true"
	 */
	private boolean loopShutdownMessage;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		this.executeInternal();

		getLog().info("*********************************************");
		getLog().info("Accumulo started ... press CTRL-C to quit");
		getLog().info("*********************************************");

		try {
			runUntilCanceled();
		} catch (InterruptedException e) {
			// Swallow exception since
		}
	}

	@Override
	protected boolean getAccumuloQuiet() {
		return this.accumuloQuiet;
	}

	@Override
	protected boolean getZookeeperQuiet() {
		return this.zookeeperQuiet;
	}

	/**
	 * Method waits until the user presses "CTRL-C" before returning. This is
	 * useful to easily start an Accumulo server from the command-line.
	 * 
	 * @throws InterruptedException
	 */
	private void runUntilCanceled() throws InterruptedException {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				getLog().info("Interrupt received, killing Accumulo server…");
				try {
					shutdown();
				} catch (Exception e) {
				}
			}
		});

		while (true) {
			Thread.sleep(30000);

			if (this.loopShutdownMessage) {
				// Continuously output this message. This plugin can be a bit
				// noisy on the command-line so we want to output this
				// "how-to-quit" message every so often.
				getLog().info("*********************************************");
				getLog().info("Accumulo running ... press CTRL-C to quit");
				getLog().info("*********************************************");
			}
		}
	}
}
