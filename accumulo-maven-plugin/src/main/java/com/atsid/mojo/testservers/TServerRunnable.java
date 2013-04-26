/**
 * 
 */
package com.atsid.mojo.testservers;

import java.io.File;
import java.util.List;

import com.atsid.runner.AccumuloTabletServerRunner;

/**
 * @author jamesm
 * 
 */
public class TServerRunnable implements ServerTestRunnerAwareRunnable<AccumuloTabletServerRunner> {

	private AccumuloTabletServerRunner tabletServer;
	private String hostname;
	private File baseDirectory;
	private List<String> classpathEntries;

	public TServerRunnable(String hostname, File baseDirectory, List<String> classpathEntries) {
		super();
		this.hostname = hostname;
		this.baseDirectory = baseDirectory;
		this.classpathEntries = classpathEntries;
	}

	public AccumuloTabletServerRunner getTestRunner() {
		return tabletServer;
	}

	public void run() {

		try {
			tabletServer = new AccumuloTabletServerRunner(hostname, baseDirectory, classpathEntries);
			tabletServer.startupServer();
		} catch (Exception e) {
			throw new RuntimeException("Error running tablet server", e);
		}
	}
}
