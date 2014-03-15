package edu.uci.ics.inf225.searchengine.search.scoring;

public class CosineSimilarityBuilder {

	private double dotProduct;

	private double cachedCosineSimilary;

	public CosineSimilarityBuilder() {
	}

	public double getCachedCosineSimilary() {
		return cachedCosineSimilary;
	}

	public void setCachedCosineSimilary(double cachedCosineSimilary) {
		this.cachedCosineSimilary = cachedCosineSimilary;
	}

	public void addWeights(double w1, double w2) {
		dotProduct += w1 * w2;
	}

	public void addWeights(float w1, float w2) {
		this.addWeights((double) w1, (double) w2);
	}

	public double calculate(double ed1, double ed2) {
		if (ed1 == 0 || ed2 == 0) {
			this.setCachedCosineSimilary(0d);
		} else {
			this.setCachedCosineSimilary(dotProduct / (ed1 * ed2));
		}
		return this.getCachedCosineSimilary();
	}

	@Override
	public String toString() {
		return String.valueOf(this.getCachedCosineSimilary());
	}

}
