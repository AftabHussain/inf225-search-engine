package edu.uci.ics.inf225.searchengine.search.scoring;

import java.util.ArrayList;
import java.util.List;

public class EuclideanLengthBuilder {

	private List<Double> weights;

	public EuclideanLengthBuilder() {
		weights = new ArrayList<>();
	}

	public void addWeight(double w) {
		weights.add(w);
	}

	public void addWeight(float w) {
		this.addWeight((double) w);
	}

	public double build() {
		float ed = 0f;

		for (Double eachWeight : weights) {
			ed += eachWeight * eachWeight;
		}
		return Math.sqrt(ed);
	}
}
