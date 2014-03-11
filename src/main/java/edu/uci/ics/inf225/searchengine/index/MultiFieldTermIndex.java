package edu.uci.ics.inf225.searchengine.index;

import java.util.HashMap;
import java.util.Map;

public class MultiFieldTermIndex {

	private Map<String, TermIndex> indices;

	public MultiFieldTermIndex() {
		indices = new HashMap<>();
	}

	public TermIndex getIndex(String field) {
		return indices.get(field);
	}

	public void putIndex(String field, TermIndex index) {
		indices.put(field, index);
	}

}
