package edu.uci.ics.inf225.searchengine.index;

import java.io.Externalizable;
import java.util.Iterator;

import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public abstract class CompoundTermIndex implements TermIndex, Externalizable {

	private static final long serialVersionUID = 1L;

	public CompoundTermIndex() {
	}

	@Override
	public void newTerm(int docID, int termID) {
		TermIndex termIndex = getIndexFor(termID);

		termIndex.newTerm(docID, termID);
	}

	protected abstract TermIndex getIndexFor(int term);

	protected abstract Iterator<TermIndex> getAllIndices();

	@Override
	public int count() {
		int count = 0;
		Iterator<TermIndex> allIndicesIterator = this.getAllIndices();
		while (allIndicesIterator.hasNext()) {
			count += allIndicesIterator.next().count();
		}
		return count;
	}

	@Override
	public double idf(int term) {
		return this.getIndexFor(term).idf(term);
	}

	@Override
	public PostingsList postingsList(int termID) {
		TermIndex termIndex = this.getIndexFor(termID);

		return termIndex.postingsList(termID);
	}
}
