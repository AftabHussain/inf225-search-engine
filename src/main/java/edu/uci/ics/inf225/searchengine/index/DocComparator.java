package edu.uci.ics.inf225.searchengine.index;

import java.util.Comparator;
import java.util.Map;

public class DocComparator implements Comparator<Long> {
	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	Map<Long, TermInDoc> base;

	public DocComparator(Map<Long, TermInDoc> base) {
		this.base = base;
	}

	public int compare(Long a, Long b) {
		if (base.get(a).getTfidf() > base.get(b).getTfidf()) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}