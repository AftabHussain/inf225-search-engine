package edu.uci.ics.inf225.searchengine.index.postings;

import javolution.util.function.Equality;

public class DocIDPostingsComparator implements Equality<Posting> {

	public DocIDPostingsComparator() {
	}

	@Override
	public int compare(Posting o1, Posting o2) {
		return o1.getDocID() - o2.getDocID();
	}

	@Override
	public int hashCodeOf(Posting object) {
		return object.hashCode();
	}

	@Override
	public boolean areEqual(Posting left, Posting right) {
		return left.equals(right);
	}

}
