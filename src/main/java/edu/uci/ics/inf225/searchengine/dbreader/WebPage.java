package edu.uci.ics.inf225.searchengine.dbreader;

import java.util.HashMap;
import java.util.Map;

public class WebPage {

	private String url;

	private String title;

	private String content;

	private String htmlContent;

	private float euclideanLength;

	private Map<String, String> links;

	public WebPage() {
		links = new HashMap<>();
	}

	public String getUrl() {
		return url;
	}

	public void addLink(String anchor, String url) {
		this.links.put(anchor, url);
	}

	/**
	 * This is only used for searches when cosine similarity is calculated. It
	 * is just the document's Euclidean Length pre-computed.
	 * 
	 * @return
	 */
	public float getEuclideanLength() {
		return euclideanLength;
	}

	public void setEuclideanLength(float euclideanLength) {
		this.euclideanLength = euclideanLength;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Since this object could be re-used for performance, there is a
	 * possibility to reset it to the initial state.
	 */
	public void reset() {
		this.content = null;
		this.htmlContent = null;
		this.url = null;
		this.title = null;
		this.links.clear();
		this.euclideanLength = 0f;
	}
}