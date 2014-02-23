package edu.uci.ics.inf225.searchengine.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TermInDoc implements Externalizable {

	private int tf = 0;
	private float tfidf = 0;
	private List<Integer> positions = new ArrayList<Integer>();
	private long docID;

	public TermInDoc(long docID, int termFrequency, List<Integer> positions) {
		this.tf = termFrequency;
		this.positions = positions;
		this.docID = docID;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(tf);
		out.writeFloat(tfidf);
		out.writeLong(docID);
		out.writeInt(positions.size());
		for (Integer position : positions) {
			out.writeInt(position.intValue());
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	public void increaseTF() {
		this.tf++;
	}

	public int getTf() {
		return tf;
	}

	public void setTf(int tf) {
		this.tf = tf;
	}

	public float getTfidf() {
		return tfidf;
	}

	public void setTfidf(float tfidf) {
		this.tfidf = tfidf;
	}

	public List<Integer> getPositions() {
		return positions;
	}

	public void setPositions(List<Integer> positions) {
		this.positions = positions;
	}

	public long getDocID() {
		return docID;
	}

	public void setDocID(long docID) {
		this.docID = docID;
	}

	public void addPosition(int position) {
		this.positions.add(position);
	}

	public String toString() {
		String str = "";

		for (Integer position : positions) {
			str = str + "," + position.toString();
		}
		return docID + ": " + tf + "," + tfidf + "{" + str + "}";
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.docID);
		builder.append(this.tf);
		return builder.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TermInDoc)) {
			return false;
		}
		TermInDoc another = (TermInDoc) obj;

		return this.tf == another.tf && this.tfidf == another.tfidf && docID == another.docID && ListUtils.isEqualList(positions, another.positions);
	}
}