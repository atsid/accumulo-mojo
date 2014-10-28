package com.atsid.runner;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.google.common.collect.Lists;
import com.google.common.io.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServerTestRunner implements ServerTestRunnerMXBean {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private boolean quiet;
	protected Process process;
	private final List<OutputRedirector> outputRedirectors = Lists.newLinkedList();

	public void startupServer() throws Exception {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (process != null) {
					try {
						stopOutputRedirection();
					} catch (IOException | InterruptedException e) {
						logger.error("Error stopping output stream redirection", e);
					}

					process.destroy();
					logger.info("Shutdown hook called, child process has halted");
				}
			}
		});

		this.process = initServer();

		if (quiet) {
			logger.info("Test runner 'quiet' mode has been enabled, suppressing console output");
			redirectInputStream(process.getErrorStream(), null);
			redirectInputStream(process.getInputStream(), null);
		} else {
			logger.info("Test runner 'quiet' mode is disabled");
			redirectInputStream(process.getErrorStream(), System.err);
			redirectInputStream(process.getInputStream(), System.out);
		}
		int returnStatus = process.waitFor();
		logger.debug("Process exited with status " + returnStatus);
	}

	protected abstract Process initServer() throws Exception;

	/**
	 * Stops all output redirection threads. This is to prevent reading from
	 * closed streams.
	 */
	private void stopOutputRedirection() throws IOException, InterruptedException {
		for (OutputRedirector outputRedirector : this.outputRedirectors) {
			outputRedirector.stop();
		}
	}

	public void registerAsMBean() throws Exception {
		String className = this.getClass().getSimpleName();
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = ObjectName.getInstance(String.format("%s:type=%s", getClass().getPackage().getName(),
				className));
		logger.info("Preparing to register as MBean");
		mbeanServer.registerMBean(this, objectName);
		logger.info("Started MBean service for " + this.getClass().getSimpleName());
	}

	public void shutdownServer() throws Exception {
		if (process != null) {
			logger.info("Shutting down " + this.getClass().getSimpleName());
			stopOutputRedirection();

			process.destroy();
			process.waitFor();
		} else {
			logger.info("No process to shutdown, returning");
		}
	}

	protected void redirectInputStream(InputStream source, OutputStream destination) {
		OutputRedirector outputRedirector = new OutputRedirector(source, destination);
		Thread runnerThread = new Thread(outputRedirector);
		runnerThread.setDaemon(true);
		runnerThread.start();

		outputRedirectors.add(outputRedirector);
	}

	public static class OutputRedirector implements Runnable {
		private InputStream inputStream;
		private OutputStream outputStream;
		private boolean continueOutputRedirection = true;
		private Thread runningThread;
		private static AtomicInteger redirectorId = new AtomicInteger(0);
		private Logger logger = LoggerFactory.getLogger(this.getClass());

		public OutputRedirector(InputStream inputStream, OutputStream outputStream) {
			this.inputStream = new BufferedInputStream(inputStream);

			if (outputStream != null) {
				this.outputStream = outputStream;
			} else {
				this.outputStream = new NullOutputStream();
			}
		}

		public void run() {
			byte[] readBuffer = new byte[256];
			Thread.currentThread().setName("OutputRedirector #" + Integer.toString(redirectorId.getAndIncrement()));
			try {
				this.runningThread = Thread.currentThread();

				while (this.continueOutputRedirection) {
					if (this.inputStream.available() > 0) {
						// Read what is available (do not wait for EOF)
						int length = inputStream.read(readBuffer);
						if (length == -1) {
							break;
						}

						outputStream.write(readBuffer, 0, length);
						outputStream.flush();
					} else {
						// No data available. Wait a bit and try again.
						Thread.sleep(100);
					}
				}
			} catch (Throwable e) {
				logger.error("Error redirecting input stream", e);
			}
		}

		public void stop() throws InterruptedException, IOException {
			this.continueOutputRedirection = false;

			if (this.runningThread != null) {
				this.runningThread.join(10000);
			}

			if (inputStream != null) {
				this.inputStream.close();
			}

			// Do not close the output stream since it is System.out or
			// System.err.
		}
	}

	public boolean isQuiet() {
		return quiet;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
}
