package edu.uci.ics.inf225.searchengine.index.postings;

import java.util.Comparator;

public class TFIDFPostingComparator implements Comparator<Posting> {

	@Override
	public int compare(Posting o1, Posting o2) {
		if (o2.getTfidf() < o1.getTfidf()) {
			return -1;
		}
		if (o2.getTfidf() == o1.getTfidf()) {
			return 0;
		} else {
			return 1;
		}
	}
}
