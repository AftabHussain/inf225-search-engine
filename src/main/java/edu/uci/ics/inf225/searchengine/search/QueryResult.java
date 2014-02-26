package edu.uci.ics.inf225.searchengine.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryResult {

	private List<QueryResultEntry> resultEntries;

	public QueryResult() {
		this(10);
	}

	public void addEntry(QueryResultEntry entry) {
		resultEntries.add(entry);
	}

	public QueryResult(int resultSize) {
		resultEntries = new ArrayList<>(resultSize);
	}

	public Iterator<QueryResultEntry> iterator() {
		return resultEntries.iterator();
	}

	public int size() {
		return resultEntries.size();
	}

}
