package com.atsid.mojo.testservers;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal start-dfs
 * @phase compile
 * @author jamesm
 * 
 */
public class HDFSTestServerMojo extends AbstractMojo {

	/**
     * Port to run the HDFS NameNode on
     *
	 * @parameter property="dfs.namenodeport" default-value="9000"
	 */
	private int nameNodePort;

	public void execute() throws MojoExecutionException, MojoFailureException {

		Runnable clusterRunnable = new Runnable() {
			public void run() {
				try {
					System.setProperty("test.build.data",
							"target/accumuloPlugin/testData");
					MiniDFSCluster miniCluster = new MiniDFSCluster.Builder(new Configuration())
							.nameNodePort(nameNodePort).numDataNodes(1).format(true).manageDataDfsDirs(true)
							.manageNameDfsDirs(true).manageNameDfsSharedDirs(true).startupOption(StartupOption.FORMAT)
							.build();
					miniCluster.waitClusterUp();
				} catch (IOException e) {
					throw new RuntimeException("Error setting up dfs", e);
				}
			}
		};
		Thread miniClusterThread = new Thread(clusterRunnable);
		miniClusterThread.setName("HDFS Minicluster-"
				+ System.currentTimeMillis());
		miniClusterThread.setDaemon(true);
		miniClusterThread.start();
		try {
			miniClusterThread.join();
		} catch (InterruptedException e) {
			throw new MojoExecutionException("Error running minicluster", e);
		}
	}
}
