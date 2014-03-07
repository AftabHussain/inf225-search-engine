package edu.uci.ics.inf225.searchengine.index;

import java.util.List;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public interface TermIndex {

	public void newTerm(int docID, String term, byte tokenType);

	public int count();

	public PostingsList postingsList(String term);

	public List<Posting> postingsForDoc(int docID);

	public void prepare(DocumentIndex docIndex);

	public double idf(String term);

}
