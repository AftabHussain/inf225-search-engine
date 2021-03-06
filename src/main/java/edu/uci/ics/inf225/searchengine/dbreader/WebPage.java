package edu.uci.ics.inf225.searchengine.dbreader;

import gnu.trove.map.hash.TObjectFloatHashMap;

import java.util.HashMap;
import java.util.Map;

public class WebPage {

	private String url;

	private String title;

	private String content;

	private String htmlContent;

	private TObjectFloatHashMap<String> euclideanLength;

	private byte slashes;

	private Map<String, String> links;

	public WebPage() {
		links = new HashMap<>();
		euclideanLength = new TObjectFloatHashMap<>(4);
	}

	public String getUrl() {
		return url;
	}

	public byte getSlashes() {
		return slashes;
	}

	public void setSlashes(byte slashes) {
		this.slashes = slashes;
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
	public float getEuclideanLength(String field) {
		return euclideanLength.get(field);
	}

	public void setEuclideanLength(TObjectFloatHashMap<String> el) {
		this.euclideanLength = el;
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
		this.euclideanLength = new TObjectFloatHashMap<>();
	}
}