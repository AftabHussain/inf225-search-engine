package edu.uci.ics.inf225.searchengine.index;

import java.util.Comparator;
import java.util.Map;

public class docComparator implements Comparator<String> {
	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	Map<String, DocMap> base;

	public docComparator(Map<String, DocMap> base) {
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