package edu.uci.ics.inf225.searchengine.search.scoring;

public class ScoringUtils {

	public static float tfidf(int tf, int collectionSize, int docFreq) {
		if (tf == 0) {
			return 0f;
		} else {

			return (1f + (float) Math.log10((double) tf)) * (float) Math.log10((double) collectionSize / (double) docFreq);
		}
	}
}
