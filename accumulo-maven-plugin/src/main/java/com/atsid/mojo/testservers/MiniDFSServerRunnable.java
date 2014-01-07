package com.atsid.mojo.testservers;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;

public class MiniDFSServerRunnable implements
		ServerTestRunnerAwareRunnable<MiniDFSCluster> {

	private int dfsRPCPort;
	private MiniDFSCluster miniCluster;

	public MiniDFSServerRunnable(int dfsRPCPort) {
		super();
		this.dfsRPCPort = dfsRPCPort;
	}

	public MiniDFSCluster getTestRunner() {
		return miniCluster;
	}

	public void run() {
		try {
			miniCluster = new MiniDFSCluster(dfsRPCPort, new Configuration(),
					1, true, true, StartupOption.FORMAT, null);
			miniCluster.waitClusterUp();
		} catch (IOException e) {
			throw new RuntimeException("Error setting up dfs", e);
		}
	}

}
