package edu.uci.ics.inf225.searchengine.index;

import java.util.Comparator;
import java.util.Map;

import edu.uci.ics.inf225.searchengine.index.postings.Posting;

public class DocComparator implements Comparator<Integer> {
	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	Map<Integer, Posting> base;

	public DocComparator(Map<Integer, Posting> base) {
		this.base = base;
	}

	public int compare(Integer a, Integer b) {
		if (base.get(a).getTfidf() > base.get(b).getTfidf()) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}