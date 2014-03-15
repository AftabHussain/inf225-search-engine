package edu.uci.ics.inf225.searchengine.search.scoring;

public class DocScorer implements Comparable<DocScorer> {

	public static double TEXT_COSINE_WEIGHT = 1d;
	public static double TITLE_COSINE_WEIGHT = 1d;
	public static double SLASHES_WEIGHT = 0.3d;

	private int docID;

	private double textCosineSimilarity;

	private byte numberOfSlashes;

	private double titleCosineSimilarity;

	private double anchorCosineSimilarity;

	public DocScorer() {
	}

	public double getTextCosineSimilarity() {
		return textCosineSimilarity;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	public void setTextCosineSimilarity(double textCosineSimilarity) {
		this.textCosineSimilarity = textCosineSimilarity;
	}

	public byte getNumberOfSlashes() {
		return numberOfSlashes;
	}

	public void setNumberOfSlashes(byte numberOfSlashes) {
		this.numberOfSlashes = numberOfSlashes;
	}

	public double getTitleCosineSimilarity() {
		return titleCosineSimilarity;
	}

	public void setTitleCosineSimilarity(double titleCosineSimilarity) {
		this.titleCosineSimilarity = titleCosineSimilarity;
	}

	public double getAnchorCosineSimilarity() {
		return anchorCosineSimilarity;
	}

	public void setAnchorCosineSimilarity(double anchorCosineSimilarity) {
		this.anchorCosineSimilarity = anchorCosineSimilarity;
	}

	public double score() {
		// The following is BETTER:
		// Higher cosine similarity.
		// Lower number of slashes.
		return TEXT_COSINE_WEIGHT * this.textCosineSimilarity + SLASHES_WEIGHT * (1d / (double) this.numberOfSlashes) + TITLE_COSINE_WEIGHT * this.titleCosineSimilarity;
	}

	/**
	 * This object could be re-used. Call this method before reusing it.
	 */
	public void reset() {
		this.textCosineSimilarity = 0d;
		this.titleCosineSimilarity = 0d;
		this.anchorCosineSimilarity = 0d;
		this.numberOfSlashes = 0;
	}

	@Override
	public int compareTo(DocScorer anotherDocScorer) {
		return Double.compare(this.score(), anotherDocScorer.score());
	}
}
