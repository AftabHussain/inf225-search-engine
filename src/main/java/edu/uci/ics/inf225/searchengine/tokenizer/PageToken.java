package edu.uci.ics.inf225.searchengine.tokenizer;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class PageToken {

	private String term;

	public PageToken() {
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(term);
		return builder.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		PageToken anotherToken = (PageToken) obj;
		return this.term.equals(anotherToken.term);
	}
}