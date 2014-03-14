package edu.uci.ics.inf225.searchengine.index.postings;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.lang.builder.HashCodeBuilder;

import edu.uci.ics.inf225.searchengine.search.scoring.ScoringUtils;

public class Posting implements Externalizable {

	private static final long serialVersionUID = 1L;

	private int tf = 0;
	private float tfidf = 0;
	private int docID;

	public Posting() {
	}

	public Posting(int docID, int termFrequency) {
		this.tf = termFrequency;
		this.docID = docID;
	}

	public void merge(Posting another) {
		if (this.docID == another.docID) {
			this.tf += another.tf;
			this.tfidf = 0f; // TF-IDF needs to be recalculated.
		}
	}

	public void calculateTFIDF(int docFreq, int collectionSize) {
		this.setTfidf(ScoringUtils.tfidf(this.tf, collectionSize, docFreq));
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(tf);
		out.writeFloat(tfidf);
		out.writeInt(docID);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		tf = in.readInt();
		tfidf = in.readFloat();
		docID = in.readInt();
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

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("doc#").append(docID).append(": TF=").append(tf).append(", TFIDF=").append(tfidf);

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

		return this.tf == another.tf && this.tfidf == another.tfidf && docID == another.docID;
	}
}