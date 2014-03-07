package edu.uci.ics.inf225.searchengine.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryResult {

	private long executionTime;

	private int totalPages;

	private List<QueryResultEntry> resultEntries;

	private static final long serialVersionUID = 1L;

	public QueryResult() {
		this(10);
	}

	public QueryResult(int resultSize) {
		resultEntries = new ArrayList<>(resultSize);
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public void addEntry(QueryResultEntry entry) {
		resultEntries.add(entry);
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public Iterator<QueryResultEntry> iterator() {
		return resultEntries.iterator();
	}

	public int size() {
		return resultEntries.size();
	}
}