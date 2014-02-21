package edu.uci.ics.inf225.searchengine.similarity;

import java.util.ArrayList;
import java.util.List;

public class DuplicateFilter {
	private static final int DISTANCE_THRESHOLD = 15;

	static List<Long> pageHashes = new ArrayList<Long>();
	private Simhash simHash;

	public DuplicateFilter() {
		// Creates SimHash object.
		simHash = new Simhash(new BinaryWordSeg());
	}

	public boolean isDuplicate(String url, String pageContent) {
		long docHash0 = simHash.simhash64(pageContent);

		for (int i = 0; i < pageHashes.size(); i++) {
			long docHash1 = pageHashes.get(i);
			int dist = simHash.hammingDistance(docHash0, docHash1);
			if (dist < DISTANCE_THRESHOLD) {
				return true;
			}
		}
		pageHashes.add(docHash0);

		return false;
	}

	public void shutdown() {
		this.pageHashes = null; // GC friendly.
	}
}
