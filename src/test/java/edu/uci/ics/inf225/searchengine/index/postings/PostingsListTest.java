package edu.uci.ics.inf225.searchengine.index.postings;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class PostingsListTest {

	@Test
	public void testAddAndGet() {
		PostingsList list = new PostingsList();

		Posting p1 = posting(1);
		Posting p2 = posting(2);
		Posting p3 = posting(3);

		list.addPosting(p1);
		list.addPosting(p2);
		list.addPosting(p3);

		Iterator<Posting> iterator = list.iterator();

		Assert.assertEquals("Element is wrong", p3, iterator.next());
		Assert.assertEquals("Element is wrong", p2, iterator.next());
		Assert.assertEquals("Element is wrong", p1, iterator.next());

		Assert.assertEquals("Get is wrong", p3, list.get(3));
		Assert.assertEquals("Get is wrong", p2, list.get(2));
		Assert.assertEquals("Get is wrong", p1, list.get(1));
	}

	private Posting posting(int docID) {
		Posting p1 = new Posting();
		p1.setDocID(docID);
		return p1;
	}
}
