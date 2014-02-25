package edu.uci.ics.inf225.searchengine.index;

import java.util.Iterator;

import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public abstract class CompoundTermIndex implements TermIndex {

	public CompoundTermIndex() {
	}

	@Override
	public void newTerm(int docID, String term, int position) {
		TermIndex termIndex = getCurrentIndexFor(term);

		termIndex.newTerm(docID, term, position);
	}

	protected abstract TermIndex getCurrentIndexFor(String term);

	protected abstract Iterator<TermIndex> getAllIndicesFor(String term);

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
	public PostingsList postingsList(String term) {
		PostingsList postings = new PostingsList();
		Iterator<TermIndex> allIndicesIterator = this.getAllIndicesFor(term);

		while (allIndicesIterator.hasNext()) {
			postings.merge(allIndicesIterator.next().postingsList(term));
		}

		return postings;
	}

}
