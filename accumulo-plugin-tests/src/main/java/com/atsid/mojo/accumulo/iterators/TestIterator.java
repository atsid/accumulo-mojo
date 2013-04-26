package com.atsid.mojo.accumulo.iterators;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.accumulo.core.data.ByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.IteratorEnvironment;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import org.apache.accumulo.core.iterators.WrappingIterator;
import org.apache.hadoop.io.Text;

public class TestIterator extends WrappingIterator {

	public TestIterator deepCopy(IteratorEnvironment env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Key getTopKey() {
		Key sourceKey = getSource().getTopKey();
		return new Key(sourceKey.getRow(), sourceKey.getColumnFamily(),
				new Text(sourceKey.getColumnQualifier().toString()
						+ "-TestIterator"), sourceKey.getColumnVisibility(),
				sourceKey.getTimestamp());
	}

	@Override
	public Value getTopValue() {
		Value sourceValue = getSource().getTopValue();
		String str = new String(sourceValue.get());
		Value value = new Value((str + "-TestIterator").getBytes());
		return value;
	}

	@Override
	public boolean hasTop() {
		boolean b = getSource().hasTop();
		return b;
	}

	@Override
	public void seek(Range range, Collection<ByteSequence> columnFamilies,
			boolean inclusive) throws IOException {
		getSource().seek(range, columnFamilies, inclusive);
	}

	@Override
	public void init(SortedKeyValueIterator<Key, Value> source,
			Map<String, String> options, IteratorEnvironment env)
			throws IOException {
		super.init(source, options, env);
	}
}