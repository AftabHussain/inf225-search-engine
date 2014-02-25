package edu.uci.ics.inf225.searchengine.tokenizer;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class PageToken {

	private String term;

	private int position;

	public PageToken() {
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(term);
		builder.append(position);
		return builder.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		PageToken anotherToken = (PageToken) obj;
		return this.term.equals(anotherToken.term) && this.position == anotherToken.position;
	}
}