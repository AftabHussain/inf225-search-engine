package edu.uci.ics.inf225.searchengine.index.docs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.Lexicon;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public class HSQLDocumentIndexTest {

	private static final double COMPARISON_DELTA = 0.0001;
	private HSQLDocumentIndex docIndex;

	private TermIndex termIndex;
	private Lexicon lexicon;

	@Before
	public void setup() throws ClassNotFoundException, SQLException {
		lexicon = new Lexicon();
		termIndex = mock(TermIndex.class);
		docIndex = new HSQLDocumentIndex();
		docIndex.destroyDatabase();
	}

	@After
	public void teardown() {
		docIndex.shutdown();
	}

	private Integer id(String term) {
		return lexicon.getTermID(term);
	}

	private static <T> Set<T> set(T... elements) {
		return new HashSet<>(Arrays.asList(elements));
	}

	@Test
	public void testEuclideanDistance() {
		WebPage p1 = page("Doc1", "Title1", "Desc1 Desc2 Desc3");
		WebPage p2 = page("Doc2", "Title2", "Desc3");
		WebPage p3 = page("Doc3", "Title3", "Desc2 Desc3");
		int doc1 = docIndex.addDoc(p1);
		int doc2 = docIndex.addDoc(p2);
		int doc3 = docIndex.addDoc(p3);

		docIndex.addTerms(doc1, set(id("Desc1"), id("Desc2"), id("Desc3")));
		docIndex.addTerms(doc2, set(id("Desc3")));
		docIndex.addTerms(doc3, set(id("Desc2"), id("Desc3")));

		when(termIndex.postingsList(id("Desc1"))).thenReturn(postings(posting(doc1, 2.1f)));
		when(termIndex.postingsList(id("Desc2"))).thenReturn(postings(posting(doc1, 3.2f), posting(doc3, 3.3f)));
		when(termIndex.postingsList(id("Desc3"))).thenReturn(postings(posting(doc1, 4.3f), posting(doc2, 4.4f), posting(doc3, 4.5f)));

		docIndex.prepare(termIndex);

		Assert.assertEquals("Wrong Euclidean Distance for doc1", ed(2.1f, 3.2f, 4.3f), docIndex.getDoc(doc1).getEuclideanLength(), COMPARISON_DELTA);
		Assert.assertEquals("Wrong Euclidean Distance for doc2", ed(4.4f), docIndex.getDoc(doc2).getEuclideanLength(), COMPARISON_DELTA);
		Assert.assertEquals("Wrong Euclidean Distance for doc3", ed(3.3f, 4.5f), docIndex.getDoc(doc3).getEuclideanLength(), COMPARISON_DELTA);
	}

	private static double ed(Float... ws) {
		double ed = 0d;

		for (int i = 0; i < ws.length; i++) {
			ed += Math.pow(ws[i], 2);
		}

		return Math.sqrt(ed);
	}

	private PostingsList postings(Posting... postings) {
		PostingsList list = new PostingsList();
		for (int i = 0; i < postings.length; i++) {
			list.addPosting(postings[i]);
		}

		return list;
	}

	private Posting posting(int docID, float tfidf) {
		Posting p = new Posting();
		p.setDocID(docID);
		p.setTfidf(tfidf);
		return p;
	}

	@Test
	public void testDocIndex() {
		WebPage p1 = page("Doc1", "Title1", "Desc1");
		WebPage p2 = page("Doc2", "Title2", "Desc2");
		WebPage p3 = page("Doc3", "Title3", "Desc3");
		int doc1 = docIndex.addDoc(p1);
		int doc2 = docIndex.addDoc(p2);
		int doc3 = docIndex.addDoc(p3);

		Assert.assertEquals("Wrong doc count", 3, docIndex.count());

		Assert.assertEquals("Wrong retrieved doc", p1.getContent(), docIndex.getDoc(doc1).getContent());
		Assert.assertEquals("Wrong retrieved doc", p2.getContent(), docIndex.getDoc(doc2).getContent());
		Assert.assertEquals("Wrong retrieved doc", p3.getContent(), docIndex.getDoc(doc3).getContent());
		Assert.assertEquals("Wrong retrieved doc", p1.getHtmlContent(), docIndex.getDoc(doc1).getHtmlContent());
		Assert.assertEquals("Wrong retrieved doc", p2.getHtmlContent(), docIndex.getDoc(doc2).getHtmlContent());
		Assert.assertEquals("Wrong retrieved doc", p3.getHtmlContent(), docIndex.getDoc(doc3).getHtmlContent());
		Assert.assertEquals("Wrong retrieved doc", p1.getTitle(), docIndex.getDoc(doc1).getTitle());
		Assert.assertEquals("Wrong retrieved doc", p2.getTitle(), docIndex.getDoc(doc2).getTitle());
		Assert.assertEquals("Wrong retrieved doc", p3.getTitle(), docIndex.getDoc(doc3).getTitle());
		Assert.assertEquals("Wrong retrieved doc", p1.getUrl(), docIndex.getDoc(doc1).getUrl());
		Assert.assertEquals("Wrong retrieved doc", p2.getUrl(), docIndex.getDoc(doc2).getUrl());
		Assert.assertEquals("Wrong retrieved doc", p3.getUrl(), docIndex.getDoc(doc3).getUrl());
	}

	private WebPage page(String url, String title, String desc) {
		WebPage page = new WebPage();

		page.setUrl(url);
		page.setTitle(title);
		page.setContent(desc);

		return page;
	}
}
