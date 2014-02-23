package edu.uci.ics.inf225.searchengine.similarity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuplicateFilter implements SimilarityFilter {
	private static final int DISTANCE_THRESHOLD = 1;

	private static final Logger dupsLogger = LoggerFactory.getLogger("duplogger");

	private List<Long> pageHashes = new ArrayList<Long>();
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
			if (dist <= DISTANCE_THRESHOLD) {
				dupsLogger.info("Duplicate: [{}] [{}] [{}] [{}]", url, docHash0, docHash1, dist);
				return true;
			}
		}
		pageHashes.add(docHash0);

		dupsLogger.info("Original: [{}] [{}]", url, docHash0);
		return false;
	}

	public void shutdown() {
		this.pageHashes = null; // GC friendly.
	}
}
