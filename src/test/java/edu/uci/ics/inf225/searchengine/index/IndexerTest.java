package edu.uci.ics.inf225.searchengine.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.map.MultiKeyMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.docs.SimpleDocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public class IndexerTest {

	private static final String preffix = "term";

	private Indexer indexer;

	private TermIndex termIndex;

	private PositionIterator positionIterator = new PositionIterator();

	private MultiKeyMap positions = new MultiKeyMap();

	private DocumentIndex docIndex;

	@Before
	public void setup() {
		docIndex = new SimpleDocumentIndex();
		termIndex = new AtomicTermIndex(docIndex);
		indexer = new Indexer(termIndex, docIndex);
	}

	@Test
	public void testIndex() {
		index(doc("doc1", 1, 6)); // Terms: 1, 2, 3, 4, 5
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
		Assert.assertEquals("Wrong number of documents", 4, docIndex.count());

		// Assert.assertEquals("Wrong number of terms in document", 5,
		// index.docFrequency("doc1"));
		// Assert.assertEquals("Wrong number of terms in document", 7,
		// index.docFrequency("doc2"));
		// Assert.assertEquals("Wrong number of terms in document", 3,
		// index.docFrequency("doc3"));
		// Assert.assertEquals("Wrong number of terms in document", 5,
		// index.docFrequency("doc4"));

		assertDocForTerm("term1", 1);
		assertDocForTerm("term2", 1);
		assertDocForTerm("term3", 1, 2);
		assertDocForTerm("term4", 1, 2);
		assertDocForTerm("term5", 1, 2);
		assertDocForTerm("term6", 2);
		assertDocForTerm("term7", 2);
		assertDocForTerm("term8", 2);
		assertDocForTerm("term9", 2, 3);
		assertDocForTerm("term10", 3);
		assertDocForTerm("term11", 3);
		assertDocForTerm("term12", 4);
		assertDocForTerm("term13", 4);
		assertDocForTerm("term14", 4);
		assertDocForTerm("term15", 4);
		assertDocForTerm("term16", 4);

		Assert.assertEquals("Number of unique terms is wrong", 16, termIndex.count());
	}

	private void assertDocForTerm(String term, Integer... docIDs) {
		PostingsList postingsList = termIndex.postingsList(term);

		PostingsList expectedTermInDocs = createPostingsList(term, docIDs);

		System.out.println(postingsList);
		System.out.println(expectedTermInDocs);

		if (postingsList.size() != expectedTermInDocs.size()) {
			Assert.fail("Wrong number of postings");
		}

		Iterator<Posting> observedPostingsIterator = postingsList.iterator();
		Iterator<Posting> expectedPostingsIterator = expectedTermInDocs.iterator();
		Assert.assertEquals("Wrong post for term " + term, expectedPostingsIterator.next(), observedPostingsIterator.next());

	}

	@SuppressWarnings("unchecked")
	private PostingsList createPostingsList(String term, Integer[] docIDs) {
		PostingsList postings = new PostingsList();

		for (int i = 0; i < docIDs.length; i++) {
			postings.addPosting(new Posting(docIDs[i], 1, (List<Integer>) this.positions.get(term, docIDs[i])));
		}

		return postings;
	}

	@SuppressWarnings("unchecked")
	private void index(Doc doc1) {
		int docID = indexer.startDoc(doc1.getURL());
		doc1.id = docID;
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

		private int id;

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
