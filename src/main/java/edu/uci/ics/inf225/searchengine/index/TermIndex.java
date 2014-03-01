package edu.uci.ics.inf225.searchengine.index;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public interface TermIndex {

	public void newTerm(int docID, String term, int position);

	public int count();

	public PostingsList postingsList(String term);

	public void prepare(DocumentIndex docIndex);

}
