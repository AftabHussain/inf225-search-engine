package edu.uci.ics.inf225.searchengine.index.postings;

import java.util.Comparator;

public class DocIDPostingComparator implements Comparator<Posting> {

	@Override
	public int compare(Posting o1, Posting o2) {
		return o1.getDocID() - o2.getDocID();
	}

}
