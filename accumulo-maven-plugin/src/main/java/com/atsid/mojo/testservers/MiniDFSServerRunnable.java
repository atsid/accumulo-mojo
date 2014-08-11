package com.atsid.mojo.testservers;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;

public class MiniDFSServerRunnable implements
		ServerTestRunnerAwareRunnable<MiniDFSCluster> {

	private int dfsRPCPort;
	private MiniDFSCluster miniCluster;
    private final File tempDirectory;

	public MiniDFSServerRunnable(int dfsRPCPort, File tempDirectory) {
		super();
		this.dfsRPCPort = dfsRPCPort;
        this.tempDirectory = tempDirectory;
	}

	public MiniDFSCluster getTestRunner() {
		return miniCluster;
	}

	public void run() {
		try {
            Configuration configuration = new Configuration();
            configuration.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, tempDirectory.getAbsolutePath());
			miniCluster = new MiniDFSCluster.Builder(configuration).nameNodePort(dfsRPCPort).numDataNodes(1)
					.format(true).manageDataDfsDirs(true).manageNameDfsDirs(true).manageNameDfsSharedDirs(true)
					.startupOption(StartupOption.FORMAT).build();
			miniCluster.waitClusterUp();
		} catch (IOException e) {
			throw new RuntimeException("Error setting up dfs", e);
		}
	}

}
