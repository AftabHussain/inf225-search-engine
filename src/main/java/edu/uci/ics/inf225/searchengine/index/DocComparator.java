package edu.uci.ics.inf225.searchengine.index;

import java.util.Comparator;
import java.util.Map;

public class DocComparator implements Comparator<String> {
	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	Map<String, TermInDoc> base;

	public DocComparator(Map<String, TermInDoc> base) {
		this.base = base;
	}

	public int compare(String a, String b) {
		if (base.get(a).gettfidf() > base.get(b).gettfidf()) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}