package edu.uci.ics.inf225.searchengine.index.docs;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class DocIDGenerator implements Iterator<Integer> {

	private static DocIDGenerator instance;

	static {
		instance = new DocIDGenerator();
	}

	private AtomicInteger integer;

	private DocIDGenerator() {
		integer = new AtomicInteger(1);
	}

	public void reset() {
		integer.set(1);
	}

	public static DocIDGenerator getInstance() {
		return instance;
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Integer next() {
		return integer.getAndIncrement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}