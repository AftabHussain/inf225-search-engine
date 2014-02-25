package edu.uci.ics.inf225.searchengine.index;

import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;

public class AtomicTermIndexWrapper extends CompoundTermIndex {

	private TermIndex termIndex;

	public AtomicTermIndexWrapper(TermIndex termIndex) {
		this.termIndex = termIndex;
	}

	@Override
	protected TermIndex getCurrentIndexFor(String term) {
		return termIndex;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Iterator<TermIndex> getAllIndicesFor(String term) {
		return IteratorUtils.singletonIterator(termIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Iterator<TermIndex> getAllIndices() {
		return IteratorUtils.singletonIterator(termIndex);
	}
}