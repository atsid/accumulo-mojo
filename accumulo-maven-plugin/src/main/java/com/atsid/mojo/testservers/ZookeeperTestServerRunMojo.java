package com.atsid.mojo.testservers;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.atsid.runner.ZookeeperTestRunner;

/**
 * Goal which starts up a local zookeeper instance
 * 
 * @goal start-zookeeper
 * @requiresDependencyResolution runtime
 * @phase pre-integration-test
 */
public class ZookeeperTestServerRunMojo extends AbstractTestServerMojo {

	/**
	 * parameter property="zookeeper.daemon" default-value="false"
	 */
	private boolean daemon;

	/**
	 * @parameter property="zookeeper.quiet" default-value="false"
	 */
	private boolean zookeeperQuiet;

	public void execute() throws MojoExecutionException, MojoFailureException {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				getLog().info("Starting zookeeper server");
				try {
					ZookeeperTestRunner zookeeperTestRunner = new ZookeeperTestRunner();
					zookeeperTestRunner.registerAsMBean();
					zookeeperTestRunner.setClasspathEntries(resolveClasspath());
					zookeeperTestRunner.setQuiet(zookeeperQuiet);
					zookeeperTestRunner.startupServer();
				} catch (Exception e) {
					getLog().error(e);
				}
			}
		});

		thread.setName("Zookeeper test server-" + System.currentTimeMillis());
		thread.start();
		getLog().info("Server started");
		if (daemon) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new MojoExecutionException("Error joining thread", e);
			}
		}
	}
}
