package com.atsid.mojo.testservers;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.Arrays;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.atsid.runner.AbstractServerTestRunner;
import com.atsid.runner.BaseAccumuloRunner;
import com.atsid.runner.AccumuloInitRunner;
import com.atsid.runner.SetGoalStateRunner;

/**
 * Starts up accumulo so it can be run in a test environment This starts up the
 * following services
 * 
 * zookeeper hdfs master server tablet server gc server logging server
 * 
 * @goal start-accumulo
 * @phase package
 */
public class AccumuloServerStartMojo extends AbstractTestServerMojo implements
		MojoMXBean {

	private static final String STOPPED = "stopped";
	private static final String STARTED = "started";

	/**
	 * @parameter expression="${accumulo.instanceName}"
	 *            default-value="accumulo"
	 */
	private String instanceName;

	/**
	 * @parameter expression="${hadoop.dfsRPCPort}" default-value="9000"
	 */

	private int dfsRPCPort;

	/**
	 * @parameter expression="${zookeeper.port}" default-value="2181"
	 */
	private int zookeeperPort;

	/**
	 * @parameter expression="${accumulo.password}" default-value="password"
	 */
	private String password;

	private MiniDFSServerRunnable dfsService;

	private ZookeeperRunnable zookeeperRunnable;

	private TServerRunnable tabletServerRunnable;

	private MasterServerRunnable masterServerRunnable;

	private LoggerServerRunnable loggerServerRunnable;

	private GCServerRunnable gcServerRunner;

	public void execute() throws MojoExecutionException, MojoFailureException {
		String state = project.getProperties().getProperty(
				this.getClass().toString());
		if (state == null || state.equals(STOPPED)) {
			project.getProperties().put(this.getClass().toString(), STARTED);
		} else {
			getLog().info(
					"Mojo has been previously executed in current project, exiting");
			return;
		}
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			registerAsMBean();
			File baseDirectory = File.createTempFile("accumulo", "");
			baseDirectory.deleteOnExit();
			baseDirectory.delete();
			baseDirectory.mkdir();
			startDFS();
			startZookeeper();
			initCloudbase(baseDirectory);
			startLogger(baseDirectory);
			startTServer(hostname, baseDirectory);
			setGoalState(baseDirectory);
			startMasterServer(hostname, baseDirectory);
			startGC(baseDirectory);
		} catch (Exception e) {
			throw new MojoExecutionException("Error running accumulo", e);
		}
	}

	protected void setGoalState(File baseDirectory) throws Exception {
		getLog().info("Setting goal state to normal");
		SetGoalStateRunner runner = new SetGoalStateRunner(baseDirectory,
				resolveClasspath());
		runner.startupServer();
	}

	protected void startDFS() throws Exception {
		dfsService = new MiniDFSServerRunnable(dfsRPCPort);
		Thread dfsCluster = new Thread(dfsService);
		dfsCluster.setName("HDFS " + System.currentTimeMillis());
		dfsCluster.setDaemon(true);
		dfsCluster.start();
		try {
			dfsCluster.join();
		} catch (InterruptedException e) {
			throw new MojoExecutionException(
					"Unexpected interruption during dfs startup", e);
		}
		getLog().info("Started DFS");
	}

	protected void startZookeeper() throws Exception {
		zookeeperRunnable = new ZookeeperRunnable(resolveClasspath(),
				zookeeperPort);
		Thread zookeeperThread = new Thread(zookeeperRunnable);
		zookeeperThread.setName("zookeeper " + System.currentTimeMillis());
		zookeeperThread.setDaemon(true);
		zookeeperThread.start();

	}

	protected void initCloudbase(File baseDirectory) throws Exception {
		BaseAccumuloRunner initProcess = new AccumuloInitRunner(
				baseDirectory, resolveClasspath(), instanceName, password,
				dfsRPCPort, zookeeperPort);
		initProcess.startupServer();
	}

	protected void startTServer(String hostname, final File baseDirectory)
			throws Exception {
		tabletServerRunnable = new TServerRunnable(hostname, baseDirectory,
				resolveClasspath());
		Thread tabletServerThread = new Thread(tabletServerRunnable);
		getLog().info("Starting tablet server");
		tabletServerThread.setDaemon(true);
		tabletServerThread.setName("Tablet Server "
				+ System.currentTimeMillis());
		tabletServerThread.start();
	}

	protected void startMasterServer(String hostname, final File baseDirectory)
			throws Exception {
		masterServerRunnable = new MasterServerRunnable(hostname,
				baseDirectory, resolveClasspath());
		Thread masterThread = new Thread(masterServerRunnable);
		masterThread.setDaemon(true);
		masterThread.setName("Master Server " + System.currentTimeMillis());
		masterThread.start();
	}

	protected void startLogger(final File baseDirectory) throws Exception {

		loggerServerRunnable = new LoggerServerRunnable(baseDirectory,
				resolveClasspath());
		Thread logThread = new Thread(loggerServerRunnable);
		logThread.setDaemon(true);
		logThread.setName("Logger " + System.currentTimeMillis());
		logThread.start();
	}

	protected void startGC(final File baseDirectory) throws Exception {
		gcServerRunner = new GCServerRunnable(baseDirectory, resolveClasspath());
		Thread gcThread = new Thread(gcServerRunner);
		gcThread.setDaemon(true);
		gcThread.setName("GC Server " + System.currentTimeMillis());
		gcThread.start();
	}

	public void registerAsMBean() throws Exception {
		String className = this.getClass().getSimpleName();
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = ObjectName.getInstance(String.format(
				"%s:type=%s", getClass().getPackage().getName(), className));
		getLog().info("Preparing to register as MBean");
		mbeanServer.registerMBean(this, objectName);
		getLog().info(
				"Started MBean service for " + this.getClass().getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public void shutdown() throws Exception {
		getLog().info("Shutdown call received, stopping services");

		for (ServerTestRunnerAwareRunnable<? extends AbstractServerTestRunner> runner : Arrays
				.asList(masterServerRunnable, tabletServerRunnable,
						loggerServerRunnable, gcServerRunner, zookeeperRunnable)) {
			try {
				shutdownCloudbaseTestRunner(runner);
			} catch (Exception e) {
				getLog().warn(
						String.format(
								"Error during shutdown of service %s\nException is: %s",
								runner.getClass().getName(), e.toString()));
			}
		}

		if (dfsService != null && dfsService.getTestRunner() != null) {
			getLog().info("Shutting down HDFS");
			dfsService.getTestRunner().shutdown();

			getLog().info("Waiting for Hadoop to shutdown");
			
			// Sleep while we wait for hadoop to shutdown. If we do not sleep
			// then we can get into a situation where the NameNode has been
			// shutdown but the DataNode has not finished cleaning up its
			// connections by the time the mojo is executed again. Since the
			// connections are static then the new DataNode will use a socket
			// that already existed and get an EOF exception.
			
			// See:
			// org.apache.hadoop.ipc.RPC$ClientCache.class
			// org.apache.hadoop.server.datanode.DataNode.class line 758 ( "RPC.stopProxy(namenode)" )
			java.lang.Thread.sleep(10000);
		}

		project.getProperties().put(this.getClass().toString(), STOPPED);
		getLog().info("Unregistering MBean service");
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		String className = this.getClass().getSimpleName();
		ObjectName objectName = ObjectName.getInstance(String.format(
				"%s:type=%s", getClass().getPackage().getName(), className));
		mbeanServer.unregisterMBean(objectName);
		getLog().info("Done shutting down services");
	}

	protected void shutdownCloudbaseTestRunner(
			ServerTestRunnerAwareRunnable<? extends AbstractServerTestRunner> runner)
			throws Exception {
		if (runner == null || runner.getTestRunner() == null)
			return;
		runner.getTestRunner().shutdownServer();
	}
}
