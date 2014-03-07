package edu.uci.ics.inf225.searchengine.search.scoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CosineSimilarityBuilder {

	private List<Double> weights1;

	private List<Double> weights2;

	private double cachedCosineSimilary;

	public CosineSimilarityBuilder() {
		weights1 = new ArrayList<>();
		weights2 = new ArrayList<>();
	}

	public double getCachedCosineSimilary() {
		return cachedCosineSimilary;
	}

	public void setCachedCosineSimilary(double cachedCosineSimilary) {
		this.cachedCosineSimilary = cachedCosineSimilary;
	}

	public void addWeights(double w1, double w2) {
		weights1.add(w1);
		weights2.add(w2);
	}

	public void addWeights(float w1, float w2) {
		this.addWeights((double) w1, (double) w2);
	}

	public double calculate() {
		Iterator<Double> it1 = weights1.iterator();
		Iterator<Double> it2 = weights2.iterator();

		double dotProduct = 0d;

		while (it1.hasNext()) {
			dotProduct += it1.next() * it2.next();
		}

		this.setCachedCosineSimilary(dotProduct / (euclideanDistance(weights1) * euclideanDistance(weights2)));
		return this.getCachedCosineSimilary();
	}

	private double euclideanDistance(List<Double> weights) {
		Iterator<Double> iterator = weights.iterator();
		double ed = 0d;

		while (iterator.hasNext()) {
			ed = Math.pow(iterator.next(), 2);
		}

		return ed;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[1:{");
		for (Iterator<Double> iterator = weights1.iterator(); iterator.hasNext();) {
			Double weight = iterator.next();
			builder.append(weight);
			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		builder.append("} ");
		builder.append("2:{");
		for (Iterator<Double> iterator = weights2.iterator(); iterator.hasNext();) {
			Double weight = iterator.next();
			builder.append(weight);
			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		builder.append("}]");

		return builder.toString();
	}

}
