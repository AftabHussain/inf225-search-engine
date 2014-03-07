package edu.uci.ics.inf225.searchengine.search;

public class QueryResultEntry {

	private String url;
	
	private String title;
	
	private String description;

	public QueryResultEntry() {
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return description;
	}

	public void setContent(String content) {
		this.description = content;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}