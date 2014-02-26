package edu.uci.ics.inf225.searchengine.search;

public interface SearchEngine {

	public QueryResult query(String query) throws QueryException;
}