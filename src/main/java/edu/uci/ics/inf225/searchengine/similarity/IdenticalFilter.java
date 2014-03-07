package edu.uci.ics.inf225.searchengine.similarity;

import java.util.HashSet;
import java.util.Set;

public class IdenticalFilter implements SimilarityFilter {

	private Set<Integer> hashes;

	private static Simhash simhash = new Simhash(new BinaryWordSeg());

	public IdenticalFilter() {
		this(100000);
	}

	public IdenticalFilter(int initialCapacity) {
		hashes = new HashSet<>();
	}

	@Override
	public boolean isDuplicate(String url, String pageContent) {
		int hashcode = hashcode(pageContent);
		synchronized (this) {
			return !hashes.add(hashcode);
		}
	}

	private static int hashcode(String pageContent) {
		return javaHash(pageContent);
	}

	private static int javaHash(String pageContent) {
		return pageContent.hashCode();
	}

	private static int simhash(String pageContent) {
		return simhash.simhash32(pageContent);
	}

}
