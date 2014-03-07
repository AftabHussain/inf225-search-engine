package edu.uci.ics.inf225.searchengine.index;

import java.io.Externalizable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public abstract class CompoundTermIndex implements TermIndex, Externalizable {

	public CompoundTermIndex() {
	}

	@Override
	public void newTerm(int docID, String term, byte tokenType) {
		TermIndex termIndex = getIndexFor(term);

		termIndex.newTerm(docID, term, tokenType);
	}

	protected abstract TermIndex getIndexFor(String term);

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
		TermIndex termIndex = this.getIndexFor(term);

		return termIndex.postingsList(term);
	}

	@Override
	public List<Posting> postingsForDoc(int docID) {
		List<Posting> postings = new LinkedList<>();
		Iterator<TermIndex> allIndicesIterator = this.getAllIndices();

		while (allIndicesIterator.hasNext()) {
			TermIndex termIndex = allIndicesIterator.next();
			postings.addAll(termIndex.postingsForDoc(docID));
		}
		return postings;
	}
}
