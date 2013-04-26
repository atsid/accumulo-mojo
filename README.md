accumulo-mojo
=============

Maven Mojo for Accumulo.  Useful for integration testing.

This project is composed of two maven modules.  The module accumulo-maven-plugin is a maven plugin that enables integration test to use an Accumulo instance.  In addition to testing this project, accumulo-plugin-tests provide an example of using accumulo-maven-plugin.

To try accumulo-mojo out, execute the following maven commands :

  ???? (I am not a maven expert, I suspected mvn install, but that failed)

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
