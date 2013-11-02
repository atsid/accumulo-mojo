package com.atsid.mojo.accumulo;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.accumulo.core.client.*;
import org.junit.Test;

public class ITDefaultTables {
	@Test
	public void defaultTablesExist() throws TableExistsException,
			TableNotFoundException, IOException, AccumuloException,
			AccumuloSecurityException {
		String tableName = "testTable";

		ZooKeeperInstance instance = new ZooKeeperInstance("accumulo",
				"localhost:2181");

		Connector connector = instance.getConnector("root", "password");

		// tableOne and tableTwo are defined in the pom.xml
		Assert.assertTrue("tableOne does not exist", connector
				.tableOperations().exists("tableOne"));
		Assert.assertTrue("tableTwo does not exist", connector
				.tableOperations().exists("tableTwo"));
	}
}
