package edu.uci.ics.inf225.searchengine.index.docs;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.TermIndex;

public interface DocumentIndex {

	public int addDoc(WebPage page);

	public int count();

	public WebPage getDoc(int docID);

	public void prepare(TermIndex termIndex);

	public void shutdown();

	public void setTerms(int docID, int[] termIDs);

	public int[] getTerms(int docID);
}
