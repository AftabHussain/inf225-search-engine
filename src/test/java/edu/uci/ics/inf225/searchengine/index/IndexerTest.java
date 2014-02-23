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

import edu.uci.ics.inf225.searchengine.index.Indexer.DocIDGenerator;

public class IndexerTest {

	private static final String preffix = "term";

	private Indexer indexer;

	private Index index;

	private PositionIterator positionIterator = new PositionIterator();

	private MultiKeyMap positions = new MultiKeyMap();

	private DocIDGenerator docIDGenerator = new DocIDGenerator();

	@Before
	public void setup() {
		indexer = new Indexer();
		index = indexer.getIndex();
	}

	@Test
	public void testIndex() {
		index(doc("doc1", 1, 6)); // Terms: 1, 2, 3, 4, 5
		// index(doc("doc1", 2, 4)); // Terms: 2, 3
		index(doc("doc2", 3, 10)); // Terms: 3, 4, 5, 6, 7, 8, 9
		index(doc("doc3", 9, 12)); // Terms: 9, 10, 11
		index(doc("doc4", 12, 17)); // Terms: 12, 13, 14, 15, 16

		/*
		 * Term1: doc1 Term2: doc1#2 Term3: doc1, doc2 Term4: doc1, doc2 Term5:
		 * doc1, doc2 Term6: doc2 Term7: doc2 Term8: doc2 Term9: doc2, doc3
		 * Term10: doc3 Term11: doc3 Term12: doc4 Term13: doc4 Term14: doc4
		 * Term15: doc4 Term16: doc4
		 */

		// index.postProcess(); //FIXME !!!
		assertIndexHasCorrectContent();
	}

	private void assertIndexHasCorrectContent() {
		Assert.assertEquals("Wrong number of documents", 4, index.numberOfDocuments());

		// Assert.assertEquals("Wrong number of terms in document", 5,
		// index.docFrequency("doc1"));
		// Assert.assertEquals("Wrong number of terms in document", 7,
		// index.docFrequency("doc2"));
		// Assert.assertEquals("Wrong number of terms in document", 3,
		// index.docFrequency("doc3"));
		// Assert.assertEquals("Wrong number of terms in document", 5,
		// index.docFrequency("doc4"));

		assertDocForTerm("term1", 1L);
		assertDocForTerm("term2", 1L);
		assertDocForTerm("term3", 1L, 2L);
		assertDocForTerm("term4", 1L, 2L);
		assertDocForTerm("term5", 1L, 2L);
		assertDocForTerm("term6", 2L);
		assertDocForTerm("term7", 2L);
		assertDocForTerm("term8", 2L);
		assertDocForTerm("term9", 2L, 3L);
		assertDocForTerm("term10", 3L);
		assertDocForTerm("term11", 3L);
		assertDocForTerm("term12", 4L);
		assertDocForTerm("term13", 4L);
		assertDocForTerm("term14", 4L);
		assertDocForTerm("term15", 4L);
		assertDocForTerm("term16", 4L);

		Assert.assertEquals("Number of unique terms is wrong", 16, index.numberOfUniqueTerms());
	}

	private void assertDocForTerm(String term, Long... docIDs) {
		Collection<TermInDoc> observedTermInDocs = index.docsForTerm(term).values();

		List<TermInDoc> expectedTermInDocs = createTermInDocs(term, docIDs);

		Assert.assertTrue("Wrong postings for term " + term, CollectionUtils.isEqualCollection(observedTermInDocs, expectedTermInDocs));

	}

	@SuppressWarnings("unchecked")
	private List<TermInDoc> createTermInDocs(String term, Long[] docIDs) {
		List<TermInDoc> termInDocs = new ArrayList<>(docIDs.length);

		for (int i = 0; i < docIDs.length; i++) {
			termInDocs.add(new TermInDoc(docIDs[i], 1, (List<Integer>) this.positions.get(term, docIDs[i])));
		}

		return termInDocs;
	}

	@SuppressWarnings("unchecked")
	private void index(Doc doc1) {
		long docID = 0;
		try {
			docID = indexer.startDoc(doc1.getURL());
			for (Iterator<String> it = doc1.termIterator(); it.hasNext();) {
				Integer position = this.positionIterator.next();
				String term = it.next();
				indexer.indexTerm(term, docID, position);

				if (this.positions.containsKey(term, doc1.id)) {
					((List<Integer>) this.positions.get(term, doc1.id)).add(position);
				} else {
					List<Integer> list = new ArrayList<>();
					list.add(position);
					this.positions.put(term, doc1.id, list);
				}
			}
		} finally {
			indexer.endDoc(docID);
		}
	}

	private Doc doc(String url, int start, int end) {
		return new Doc(docIDGenerator.next(), url, start, end);
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

		private long id;

		public Doc(long id, String url, int start, int end) {
			this.id = id;
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
