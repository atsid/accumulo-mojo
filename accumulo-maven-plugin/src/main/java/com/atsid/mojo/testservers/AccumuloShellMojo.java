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

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Shell shell = new Shell();
			shell.config("-u", "root", "-p", "password", "-z", "accumulo", "localhost:2181");
			shell.start();
		} catch (IOException e) {
			throw new MojoExecutionException("Error starting Accumulo shell", e);
		}
	}

	@Override
	public void shutdown() throws Exception {

	}
}
