package com.atsid.utilities;

import java.util.Collection;

import org.apache.accumulo.core.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes new tables.
 */
public class TableInitializer {

	protected Logger logger = LoggerFactory.getLogger(TableInitializer.class);

	private final String instance;
	private final int zookeeperPort;
	private final String password;

	public TableInitializer(String instance, int zookeeperPort, String password) {
		this.instance = instance;
		this.zookeeperPort = zookeeperPort;
		this.password = password;
	}

	/**
	 * Creates tables in the test Accumulo instance if they do not already exist
	 * 
	 * @param tableNames
	 *            Names of the tables to create
	 */
	public void addTables(Collection<String> tableNames)
			throws AccumuloSecurityException, AccumuloException {

		if (tableNames != null && tableNames.size() > 0) {

			ZooKeeperInstance instance = new ZooKeeperInstance(this.instance,
					String.format("localhost:%s", this.zookeeperPort));

			Connector connector = instance.getConnector("root", this.password);

			for (String table : tableNames) {
				if (!connector.tableOperations().exists(table)) {
					logger.info("Creating table '{}'", table);
					try {
						connector.tableOperations().create(table);
					} catch (TableExistsException e) {
						logger.error("Error creating table " + table, e);
					}
				} else {
					logger.info("Table '{}' already exists", table);
				}
			}
		} else {
			logger.info("No default tables configured.  Not creating any default tables.");
		}
	}
}
