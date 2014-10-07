package com.atsid.runner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServerTestRunner implements
		ServerTestRunnerMXBean {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private boolean quiet;
	protected Process process;

	public void startupServer() throws Exception {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (process != null) {
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

	public void registerAsMBean() throws Exception {
		String className = this.getClass().getSimpleName();
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = ObjectName.getInstance(String.format(
				"%s:type=%s", getClass().getPackage().getName(), className));
		logger.info("Preparing to register as MBean");
		mbeanServer.registerMBean(this, objectName);
		logger.info("Started MBean service for "
				+ this.getClass().getSimpleName());
	}

	public void shutdownServer() throws Exception {
		if (process != null) {
			logger.info("Shutting down " + this.getClass().getSimpleName());
			process.destroy();
			process.waitFor();
		} else {
			logger.info("No process to shutdown, returning");
		}
	}

	protected void redirectInputStream(InputStream source,
			OutputStream destination) {
		OutputRedirector outputRedirector = new OutputRedirector(source,
				destination);
		Thread runnerThread = new Thread(outputRedirector);
		runnerThread.setDaemon(true);
		runnerThread.start();
	}

	public static class OutputRedirector implements Runnable {
		private InputStream inputStream;
		private OutputStream outputStream;

		public OutputRedirector(InputStream inputStream,
				OutputStream outputStream) {
			this.inputStream = inputStream;
			this.outputStream = outputStream;
		}

		public void run() {
			byte[] readBuffer = new byte[256];
			try {
                while (true) {
                    int length = inputStream.read(readBuffer);
                    if (length == -1) {
                        break;
                    }
                    if (outputStream != null) {
                        outputStream.write(readBuffer, 0, length);
                        outputStream.flush();
                    }
                }
            } catch (IOException e) {
                if(!e.getMessage().startsWith("Stream closed")) {
                    // If the input stream or output stream is not closed then re-throw the exception.
                    // If the stream is closed then the process has ended and we should swallow the exception.
                    throw new RuntimeException(e);
                }
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public boolean isQuiet() {
		return quiet;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
}
