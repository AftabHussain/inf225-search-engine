package edu.uci.ics.inf225.searchengine.index;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public interface TermIndex {

	public void newTerm(int docID, int termID, byte tokenType);

	public int count();

	public PostingsList postingsList(int termID);

	public void prepare(DocumentIndex docIndex);

	public double idf(int term);

}
