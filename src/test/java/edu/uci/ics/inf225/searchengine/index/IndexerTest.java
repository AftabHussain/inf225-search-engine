package edu.uci.ics.inf225.searchengine.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.docs.DocIDGenerator;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.docs.SimpleDocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.tokenizer.PageToken;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenStream;
import edu.uci.ics.inf225.searchengine.tokenizer.TextTokenizer;

public class IndexerTest {

	private static final double DELTA = 0.0001;

	private Indexer indexer;

	private MultiFieldTermIndex termIndex;

	private DocumentIndex docIndex;

	private Lexicon lexicon;

	private TextTokenizer tokenizer;

	private static final String TEST_FIELD = "testfield";

	@Before
	public void setup() throws IOException {
		DocIDGenerator.getInstance().reset();
		docIndex = new SimpleDocumentIndex();
		termIndex = new MultiFieldTermIndex();
		lexicon = new Lexicon();
		indexer = new Indexer(termIndex, docIndex, lexicon);
		tokenizer = new TextTokenizer();
		tokenizer.start();

		insertInitialData();
	}

	@Test
	public void testTFIDF() {
		termIndex.prepare(docIndex);
		assertTFIDFInPostings("term1", tfidf(1, 4, 1));
		assertTFIDFInPostings("term2", tfidf(1, 4, 1));
		assertTFIDFInPostings("term3", tfidf(1, 4, 2));
		assertTFIDFInPostings("term4", tfidf(1, 4, 2));
		assertTFIDFInPostings("term5", tfidf(1, 4, 2));
		assertTFIDFInPostings("term6", tfidf(1, 4, 1));
		assertTFIDFInPostings("term7", tfidf(1, 4, 1));
		assertTFIDFInPostings("term8", tfidf(1, 4, 1));
		assertTFIDFInPostings("term9", tfidf(1, 4, 2), tfidf(2, 4, 2));
		assertTFIDFInPostings("term10", tfidf(2, 4, 1));
		assertTFIDFInPostings("term11", tfidf(2, 4, 1));
		assertTFIDFInPostings("term12", tfidf(1, 4, 1));
		assertTFIDFInPostings("term13", tfidf(1, 4, 1));
		assertTFIDFInPostings("term14", tfidf(1, 4, 1));
		assertTFIDFInPostings("term15", tfidf(1, 4, 1));
		assertTFIDFInPostings("term16", tfidf(1, 4, 1));
	}

	private void assertTFIDFInPostings(String term, Double... tfidfs) {
		int termID = lexicon.getTermID(term);
		PostingsList term1PostingList = termIndex.getIndex(TEST_FIELD).postingsList(termID);
		Iterator<Posting> termPostingIterator = term1PostingList.iterator();

		for (int i = 0; i < tfidfs.length; i++) {
			if (!termPostingIterator.hasNext()) {
				Assert.fail("Wrong number of postings in " + term);
			}
			Assert.assertEquals("Wrong TF-IDF for " + term, tfidfs[i], termPostingIterator.next().getTfidf(), DELTA);
		}
	}

	private double tfidf(int tf, int collectionSize, int docFreq) {
		if (tf > 0) {
			return (1.0 + Math.log10((double) tf)) * Math.log10((double) collectionSize / (double) docFreq);
		} else {
			return 0.0;
		}
	}

	private void insertInitialData() throws IOException {
		String doc1 = generateDoc("term", 1, 6);// Terms: 1, 2, 3, 4, 5
		String doc2 = generateDoc("term", 3, 10);// Terms: 3, 4, 5, 6, 7, 8, 9
		String doc3 = generateDoc("term", 9, 12);// Terms: 9, 10, 11
		String doc4 = generateDoc("term", 12, 17);// Terms: 12, 13, 14, 15, 16
		index("doc1", doc1);
		index("doc2", doc2);
		index("doc3", doc3 + " " + doc3);
		index("doc4", doc4);

		/*
		 * Term1: doc1 Term2: doc1#2 Term3: doc1, doc2 Term4: doc1, doc2 Term5:
		 * doc1, doc2 Term6: doc2 Term7: doc2 Term8: doc2 Term9: doc2, doc3
		 * Term10: doc3 Term11: doc3 Term12: doc4 Term13: doc4 Term14: doc4
		 * Term15: doc4 Term16: doc4
		 */
	}

	@Test
	public void testIndex() {
		Assert.assertEquals("Wrong number of documents", 4, docIndex.count());

		assertDocForTerm("term1", posting(1, 1));
		assertDocForTerm("term2", posting(1, 1));
		assertDocForTerm("term3", posting(1, 1), posting(2, 1));
		assertDocForTerm("term4", posting(1, 1), posting(2, 1));
		assertDocForTerm("term5", posting(1, 1), posting(2, 1));
		assertDocForTerm("term6", posting(2, 1));
		assertDocForTerm("term7", posting(2, 1));
		assertDocForTerm("term8", posting(2, 1));
		assertDocForTerm("term9", posting(2, 1), posting(3, 2));
		assertDocForTerm("term10", posting(3, 2));
		assertDocForTerm("term11", posting(3, 2));
		assertDocForTerm("term12", posting(4, 1));
		assertDocForTerm("term13", posting(4, 1));
		assertDocForTerm("term14", posting(4, 1));
		assertDocForTerm("term15", posting(4, 1));
		assertDocForTerm("term16", posting(4, 1));

		Assert.assertEquals("Number of unique terms is wrong", 16, lexicon.size());
	}

	private Posting posting(int docID, int tf) {
		return new Posting(docID, tf);
	}

	private void assertDocForTerm(String term, Posting... postings) {
		int termID = lexicon.getTermID(term);
		PostingsList postingsList = termIndex.getIndex(TEST_FIELD).postingsList(termID);

		PostingsList expectedTermInDocs = createPostingsList(postings);

		Assert.assertEquals("Wrong number of postings in term " + term, expectedTermInDocs.size(), postingsList.size());

		Assert.assertTrue("Wrong posting list for term " + term, ListUtils.isEqualList(expectedTermInDocs.postings(), postingsList.postings()));

		Iterator<Posting> observedPostingsIterator = postingsList.iterator();
		Iterator<Posting> expectedPostingsIterator = expectedTermInDocs.iterator();
		Assert.assertEquals("Wrong post for term " + term, expectedPostingsIterator.next(), observedPostingsIterator.next());

	}

	private PostingsList createPostingsList(Posting[] postings) {
		PostingsList postingsList = new PostingsList();

		for (int i = 0; i < postings.length; i++) {
			postingsList.addPosting(postings[i]);
		}

		return postingsList;
	}

	private WebPage page(String url, String content) {
		WebPage page = new WebPage();
		page.setUrl(url);
		page.setContent(content);
		return page;
	}

	private void index(String url, String doc) throws IOException {
		int docID = indexer.startDoc(page(url, doc));

		PageTokenStream stream = tokenizer.tokenize(doc);

		try {

			List<String> terms = new LinkedList<>();
			while (stream.increment()) {
				PageToken token = stream.next();
				terms.add(token.getTerm());
			}
			indexer.indexTerms(terms, docID, TEST_FIELD);
		} finally {
			stream.close();
		}
	}

	public static String generateDoc(String preffix, int i, int j) {
		StringBuilder builder = new StringBuilder();
		for (; i < j; i++) {
			builder.append(preffix);
			builder.append(i);
			if (i + 1 < j) {
				builder.append(" ");
			}
		}
		return builder.toString();
	}
}
