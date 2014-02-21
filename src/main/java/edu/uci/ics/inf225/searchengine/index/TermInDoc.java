package edu.uci.ics.inf225.searchengine.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TermInDoc {

	private int _termFrequency = 0;
	private float _tfidf = 0;
	private List<Integer> positions = new ArrayList<Integer>();
	private String term;

	public TermInDoc(String term, int termFrequency, List<Integer> positions) {
		this._termFrequency = termFrequency;
		this.positions = positions;
		this.term = term;
	}

	public void addPosition(int position) {
		this.positions.add(position);
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public void settermFrequency(Integer termFrequency) {
		_termFrequency = termFrequency;
	}

	public void settfidf(float tfidf) {
		_tfidf = tfidf;
	}

	public Integer gettermFrequency() {
		return _termFrequency;
	}

	public float gettfidf() {
		return _tfidf;
	}

	public List<Integer> getpositions() {
		return positions;
	}

	public String toString() {
		String str = "";

		for (Integer position : positions) {
			str = str + "," + position.toString();
		}
		return term + ": " + _termFrequency + "," + _tfidf + "{" + str + "}";
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.term);
		builder.append(this._termFrequency);
		return builder.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TermInDoc)) {
			return false;
		}
		TermInDoc another = (TermInDoc) obj;

		return this._termFrequency == another._termFrequency && this._tfidf == another._tfidf && term.equals(another.term) && ListUtils.isEqualList(positions, another.positions);
	}
}
