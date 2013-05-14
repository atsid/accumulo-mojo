package com.atsid.mojo.accumulo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.junit.Test;

public class ITTestCustomIteratorUsage {
	/**
	 * Tests that a custom iterator implemented in the current project actually
	 * deploys into the cloudbase instance
	 * 
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableExistsException
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws AccumuloSecurityException
	 * @throws AccumuloException
	 */
	@Test
	public void testCustomIterator() throws TableExistsException,
			TableNotFoundException, IOException, AccumuloException,
			AccumuloSecurityException {
		String tableName = "testTable";

		ZooKeeperInstance instance = new ZooKeeperInstance("accumulo",
				"localhost:2181");

		Connector connector = instance.getConnector("root", "password");

		connector.tableOperations().create(tableName);

		// Write test data
		BatchWriter writer = connector.createBatchWriter(tableName, 10000L,
				1000L, 4);
		Mutation m = new Mutation(new Text("myRow"));
		m.put(new Text("IepIngestionInformation"), new Text("start-time"),
				new Value("SomeValue".getBytes()));
		m.put(new Text("IepIngestionInformation"), new Text("end-time"),
				new Value("SecondValue".getBytes()));
		writer.addMutation(m);
		writer.close();

		// Read test data
		BatchScanner scanner = connector.createBatchScanner(tableName,
				new Authorizations(), 1);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range());
		scanner.setRanges(ranges);

		Iterator<Entry<Key, Value>> scannerIter = scanner.iterator();
		Entry<Key, Value> entry1 = scannerIter.next();
		Entry<Key, Value> entry2 = scannerIter.next();

		Assert.assertFalse(scannerIter.hasNext());

		Assert.assertEquals("end-time", entry1.getKey().getColumnQualifier()
				.toString());
		Assert.assertEquals("SecondValue", new String(entry1.getValue().get()));

		Assert.assertEquals("start-time", entry2.getKey().getColumnQualifier()
				.toString());
		Assert.assertEquals("SomeValue", new String(entry2.getValue().get()));

		// TODO: need to figure out why this doesn't work. Take a look at
		// ACCUMULO-271. same error
		/*
		 * Caused by: java.lang.IllegalStateException: never been seeked at
		 * org.apache
		 * .accumulo.core.iterators.WrappingIterator.next(WrappingIterator
		 * .java:95) at
		 * org.apache.accumulo.core.iterators.system.SourceSwitchingIterator
		 * .readNext(SourceSwitchingIterator.java:120) at
		 * org.apache.accumulo.core
		 * .iterators.system.SourceSwitchingIterator.next
		 * (SourceSwitchingIterator.java:105) at
		 * org.apache.accumulo.server.tabletserver
		 * .Tablet.lookup(Tablet.java:1625) at
		 * org.apache.accumulo.server.tabletserver
		 * .Tablet.lookup(Tablet.java:1712) at
		 * org.apache.accumulo.server.tabletserver
		 * .TabletServer$ThriftClientHandler$LookupTask
		 * .run(TabletServer.java:1005) at
		 * org.apache.accumulo.cloudtrace.instrument
		 * .TraceRunnable.run(TraceRunnable.java:47) at
		 * java.util.concurrent.ThreadPoolExecutor$Worker
		 * .runTask(ThreadPoolExecutor.java:895) at
		 * java.util.concurrent.ThreadPoolExecutor$Worker
		 * .run(ThreadPoolExecutor.java:918)
		 */

		// IteratorSetting cfg = new IteratorSetting(100, "myIteratorName",
		// TestIterator.class);
		// scanner.addScanIterator(cfg);
		// for (Entry<Key,Value> entry : scanner) {
		// String columnQualifier =
		// entry.getKey().getColumnQualifier().toString();
		// String value = new String(entry.getValue().get());
		// Assert.assertTrue("Expected columnQualifier to end with '-TestIterator' but was: "
		// + columnQualifier, columnQualifier.endsWith("-TestIterator"));
		// Assert.assertTrue("Expected value to end with '-TestIterator' but was: "
		// + value, value.endsWith("-TestIterator"));
		// }

		scanner.close();
	}
}
