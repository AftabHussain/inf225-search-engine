package edu.uci.ics.inf225.searchengine.index.docs;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.MultiFieldTermIndex;

public interface DocumentIndex {

	public int addDoc(WebPage page);

	public int count();

	public WebPage getDoc(int docID);

	public void prepare(MultiFieldTermIndex multiTermIndex);

	public void shutdown();

	public void setTerms(int docID, String field, int[] termIDs);

	public int[] getTerms(int docID, String field);
}
