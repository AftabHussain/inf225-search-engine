package edu.uci.ics.inf225.searchengine.dbreader;

public class WebPage {

	private String url;

	private String title;

	private String content;

	private String htmlContent;

	private float euclideanLength;

	public WebPage() {
	}

	public String getUrl() {
		return url;
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
}