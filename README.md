accumulo-mojo
=============

Maven Mojo for Accumulo.  Useful for integration testing.

This project is composed of two maven modules.  The module accumulo-maven-plugin is a maven plugin that enables integration test to use an Accumulo instance.  In addition to testing this project, accumulo-plugin-tests provide an example of using accumulo-maven-plugin.

To try accumulo-mojo out, execute the following maven commands :

 * Run 'mvn clean install'  
 * If all assertions pass in the integ test, then Output should be something like:
 
<pre>
	[INFO] ------------------------------------------------------------------------
	[INFO] Reactor Summary:
	[INFO] 
	[INFO] Accumulo Mojo Parent .............................. SUCCESS [0.509s]
	[INFO] Maven Accumulo Plugin ............................. SUCCESS [3.693s]
	[INFO] Accumulo Plugin Integration Tests ................. SUCCESS [25.665s]
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 30.369s
	[INFO] Finished at: Fri Apr 26 17:09:17 PDT 2013
	[INFO] Final Memory: 32M/639M
	[INFO] ------------------------------------------------------------------------
</pre>

Usage
=============

### Add dependency plugin

```xml
  		<plugin>
				<groupId>com.atsid.mojo</groupId>
				<artifactId>accumulo-maven-plugin</artifactId>
				<version>0.2.1-SNAPSHOT</version>
				<executions>
					<execution>
						<goals>
							<goal>start-accumulo</goal>
							<goal>stop-accumulo</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
```
      
### Use in IT

  
```java
    @Test
    public void testCustomIterator() throws TableExistsException, TableNotFoundException, IOException, AccumuloException, AccumuloSecurityException {
      String tableName = "testTable";
    
      ZooKeeperInstance instance = new ZooKeeperInstance("accumulo", "localhost:2181");
    
      Connector connector = instance.getConnector("root", "password");
    
      connector.tableOperations().create(tableName);
    
      // Write test data
      BatchWriter writer = connector.createBatchWriter(tableName, 10000L, 1000L, 4);
      Mutation m = new Mutation(new Text("myRow"));
      m.put(new Text("IepIngestionInformation"), new Text("start-time"), new Value("SomeValue".getBytes()));
      m.put(new Text("IepIngestionInformation"), new Text("end-time"), new Value("SecondValue".getBytes()));
      writer.addMutation(m);
      writer.close();
    
      // Read test data
      BatchScanner scanner = connector.createBatchScanner(tableName, new Authorizations(), 1);
      List<Range> ranges = new ArrayList<Range>();
      ranges.add(new Range());
      scanner.setRanges(ranges);
     
      Iterator<Entry<Key,Value>> scannerIter = scanner.iterator();
    
    ...

```

### Use from the command line
 * Run 'mvn com.atsid.mojo:accumulo-maven-plugin:0.2.8-SNAPSHOT:start-accumulo -DdefaultTables=myTable -Ddaemon=false -DaccumuloQuiet=true -DzookeeperQuiet=true'
 * A log message will be written to the console when Accumulo is initialized
 * Default connection information:
 ** Instance: accumulo
 ** Zookeeper: localhost:2181
 ** User: root
 ** Password: password
 * When ready to shut down the Accumulo instance press CTRL-C
