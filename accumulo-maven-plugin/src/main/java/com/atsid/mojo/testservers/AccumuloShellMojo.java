package com.atsid.mojo.testservers;

import java.io.IOException;
import java.util.List;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
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
public class AccumuloShellMojo extends AbstractTestServerMojo
		implements MojoMXBean {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Shell shell = new Shell();
            shell.updateUser("root", new PasswordToken("password"));
            shell.start();
        } catch (IOException e) {
            throw new MojoExecutionException("Error starting Accumulo shell", e);
        } catch (AccumuloSecurityException e) {
            throw new MojoExecutionException("Error starting Accumulo shell", e);
        } catch (AccumuloException e) {
            throw new MojoExecutionException("Error starting Accumulo shell", e);
        }

	}

    @Override
    public void shutdown() throws Exception {

    }
}
