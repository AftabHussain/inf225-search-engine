package edu.uci.ics.inf225.searchengine.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.MultiKeyMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IndexerTest {

	private static final String preffix = "term";

	private Indexer indexer;

	private Index index;

	private PositionIterator positionIterator = new PositionIterator();

	private MultiKeyMap positions = new MultiKeyMap();

	@Before
	public void setup() {
		indexer = new Indexer();
		index = indexer.getIndex();
	}

	@Test
	public void testIndex() {
		index(doc("doc1", 1, 6)); // Terms: 1, 2, 3, 4, 5
		index(doc("doc1", 2, 4)); // Terms: 2, 3
		index(doc("doc2", 3, 10)); // Terms: 3, 4, 5, 6, 7, 8, 9
		index(doc("doc3", 9, 12)); // Terms: 9, 10, 11
		index(doc("doc4", 12, 17)); // Terms: 12, 13, 14, 15, 16

		/*
		 * Term1: doc1 Term2: doc1#2 Term3: doc1, doc2 Term4: doc1, doc2 Term5:
		 * doc1, doc2 Term6: doc2 Term7: doc2 Term8: doc2 Term9: doc2, doc3
		 * Term10: doc3 Term11: doc3 Term12: doc4 Term13: doc4 Term14: doc4
		 * Term15: doc4 Term16: doc4
		 */

		assertIndexHasCorrectContent();
	}

	private void assertIndexHasCorrectContent() {
		Assert.assertEquals("Wrong number of documents", 4, index.numberOfDocuments());

		Assert.assertEquals("Wrong number of terms in document", 7, index.docFrequency("doc1"));
		Assert.assertEquals("Wrong number of terms in document", 7, index.docFrequency("doc2"));
		Assert.assertEquals("Wrong number of terms in document", 3, index.docFrequency("doc3"));
		Assert.assertEquals("Wrong number of terms in document", 5, index.docFrequency("doc4"));

		assertDocForTerm("term1", "doc1");
		assertDocForTerm("term2", "doc1");
		assertDocForTerm("term3", "doc1", "doc2");
		assertDocForTerm("term4", "doc1", "doc2");
		assertDocForTerm("term5", "doc1", "doc2");
		assertDocForTerm("term6", "doc2");
		assertDocForTerm("term7", "doc2");
		assertDocForTerm("term8", "doc2");
		assertDocForTerm("term9", "doc2", "doc3");
		assertDocForTerm("term10", "doc3");
		assertDocForTerm("term11", "doc3");
		assertDocForTerm("term12", "doc4");
		assertDocForTerm("term13", "doc4");
		assertDocForTerm("term14", "doc4");
		assertDocForTerm("term15", "doc4");
		assertDocForTerm("term16", "doc4");
	}

	private void assertDocForTerm(String term, String... docs) {
		Collection<TermInDoc> observedTermInDocs = index.docsForTerm(term).values();

		List<TermInDoc> expectedTermInDocs = createTermInDocs(term, docs);

		Assert.assertTrue("Wrong postings for term " + term, CollectionUtils.isEqualCollection(observedTermInDocs, expectedTermInDocs));

	}

	@SuppressWarnings("unchecked")
	private List<TermInDoc> createTermInDocs(String term, String[] docs) {
		List<TermInDoc> termInDocs = new ArrayList<>(docs.length);

		for (int i = 0; i < docs.length; i++) {
			termInDocs.add(new TermInDoc(docs[i], 1, (List<Integer>) this.positions.get(term, docs[i])));
		}

		return termInDocs;
	}

	@SuppressWarnings("unchecked")
	private void index(Doc doc1) {
		for (Iterator<String> it = doc1.termIterator(); it.hasNext();) {
			Integer position = this.positionIterator.next();
			String term = it.next();
			indexer.indexTerm(term, doc1.getURL(), position);

			if (this.positions.containsKey(term, doc1.getURL())) {
				((List<Integer>) this.positions.get(term, doc1.getURL())).add(position);
			} else {
				List<Integer> list = new ArrayList<>();
				list.add(position);
				this.positions.put(term, doc1.getURL(), list);
			}
		}
	}

	private Doc doc(String url, int start, int end) {
		return new Doc(url, start, end);
	}

	private static class PositionIterator implements Iterator<Integer> {

		private Random random = new Random(System.currentTimeMillis());

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Integer next() {
			return random.nextInt(4) + 1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static class Doc {
		private String url;

		private List<String> terms;

		public Doc(String url, int start, int end) {
			this.url = url;
			this.terms = generateTerms(start, end);
		}

		public Iterator<String> termIterator() {
			return this.terms.iterator();
		}

		public String getURL() {
			return url;
		}

		public static List<String> generateTerms(int i, int j) {
			List<String> list = new ArrayList<>(j - i + 1);
			for (; i < j; i++) {
				list.add(preffix + i);
			}
			return list;
		}
	}
}
