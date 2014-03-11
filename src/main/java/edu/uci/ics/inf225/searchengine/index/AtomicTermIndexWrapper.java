package edu.uci.ics.inf225.searchengine.index;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;

public class AtomicTermIndexWrapper extends CompoundTermIndex {

	private TermIndex termIndex;

	public AtomicTermIndexWrapper(TermIndex termIndex) {
		this.termIndex = termIndex;
	}

	@Override
	protected TermIndex getIndexFor(int termID) {
		return termIndex;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Iterator<TermIndex> getAllIndices() {
		return IteratorUtils.singletonIterator(termIndex);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(termIndex);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		termIndex = (TermIndex) in.readObject();
	}

	@Override
	public void prepare(DocumentIndex docIndex) {
		this.termIndex.prepare(docIndex);
	}

	@Override
	public double idf(int termID) {
		return this.termIndex.idf(termID);
	}
}