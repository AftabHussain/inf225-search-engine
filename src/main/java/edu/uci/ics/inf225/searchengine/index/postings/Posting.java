package edu.uci.ics.inf225.searchengine.index.postings;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Posting implements Externalizable {

	private static final long serialVersionUID = 1L;

	private int tf = 0;
	private double tfidf = 0;
	private List<Integer> positions = new LinkedList<Integer>();
	private int docID;

	public Posting() {
	}

	public Posting(int docID, int termFrequency, List<Integer> positions) {
		this.tf = termFrequency;
		this.positions = positions;
		this.docID = docID;
	}

	public void merge(Posting another) {
		if (this.docID == another.docID) {
			this.tf += another.tf;
			this.positions.addAll(another.getPositions());
			this.tfidf = 0; // TF-IDF needs to be recalculated.
		}
	}

	public void calculateTFIDF(int docFreq, int collectionSize) {
		if (this.tf == 0) {
			this.setTfidf(0.0);
		} else {
			this.setTfidf((1.0 + Math.log10((double) this.tf)) * Math.log10((double) collectionSize / (double) docFreq));
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(tf);
		out.writeDouble(tfidf);
		out.writeInt(docID);
		out.writeInt(positions.size());
		for (Integer position : positions) {
			out.writeInt(position.intValue());
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		tf = in.readInt();
		tfidf = in.readDouble();
		docID = in.readInt();
		int numberOfPositions = in.readInt();
		this.positions = new ArrayList<>(numberOfPositions);
		for (int i = 0; i < numberOfPositions; i++) {
			this.positions.add(in.readInt());
		}
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

	public double getTfidf() {
		return tfidf;
	}

	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}

	public List<Integer> getPositions() {
		return positions;
	}

	public void setPositions(List<Integer> positions) {
		this.positions = positions;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	public void addPosition(int position) {
		this.positions.add(position);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("doc#").append(docID).append(": TF=").append(tf).append(", TFIDF=").append(tfidf).append("Positions={");

		for (Integer position : positions) {
			builder.append(position.toString()).append(", ");
		}
		builder.delete(builder.length() - 2, builder.length());

		builder.append("}");

		return builder.toString();
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
		if (obj == null || !(obj instanceof Posting)) {
			return false;
		}
		Posting another = (Posting) obj;

		return this.tf == another.tf && this.tfidf == another.tfidf && docID == another.docID && ListUtils.isEqualList(positions, another.positions);
	}
}