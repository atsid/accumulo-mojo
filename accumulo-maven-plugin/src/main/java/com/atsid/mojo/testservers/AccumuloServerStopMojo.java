package com.atsid.mojo.testservers;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Stops a local Accumulo server that was started by the "start-accumulo" goal.
 * 
 * @goal stop-accumulo
 * @phase verify
 * @requiresProject true
 * @author jamesm
 * 
 */
public class AccumuloServerStopMojo extends AbstractMojo {

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

	public void execute() throws MojoExecutionException, MojoFailureException {

        if(!this.isSkip()) {
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

    private boolean isSkip() {
        return this.skipTests || this.skipITs;
    }
}
