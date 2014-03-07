package edu.uci.ics.inf225.searchengine.index.docs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;

public class HSQLDocumentIndexTest {

	private static final double COMPARISON_DELTA = 0.0001;
	private HSQLDocumentIndex docIndex;

	@Before
	public void setup() throws ClassNotFoundException, SQLException {
		docIndex = new HSQLDocumentIndex();
	}

	@After
	public void teardown() {
		docIndex.shutdown();
	}

	@Test
	public void testEuclideanDistance() {
		WebPage p1 = page("Doc1", "Title1", "Desc1");
		WebPage p2 = page("Doc2", "Title2", "Desc2");
		WebPage p3 = page("Doc3", "Title3", "Desc3");
		int doc1 = docIndex.addDoc(p1);
		int doc2 = docIndex.addDoc(p2);
		int doc3 = docIndex.addDoc(p3);

		TermIndex termIndex = mock(TermIndex.class);

		when(termIndex.postingsForDoc(doc1)).thenReturn(postings(doc1, 2.1f));
		when(termIndex.postingsForDoc(doc2)).thenReturn(postings(doc2, 3.2f, 3.3f, 3.4f));
		when(termIndex.postingsForDoc(doc3)).thenReturn(postings(doc3, 4.5f, 4.6f));

		docIndex.prepare(termIndex);

		Assert.assertEquals("Wrong Euclidean Distance for doc1", 2.1f, docIndex.getDoc(doc1).getEuclideanLength(), COMPARISON_DELTA);
		Assert.assertEquals("Wrong Euclidean Distance for doc2", Math.sqrt(Math.pow(3.2f, 2) + Math.pow(3.3f, 2) + Math.pow(3.4f, 2)), docIndex.getDoc(doc2).getEuclideanLength(), COMPARISON_DELTA);
		Assert.assertEquals("Wrong Euclidean Distance for doc3", Math.sqrt(Math.pow(4.5f, 2) + Math.pow(4.6f, 2)), docIndex.getDoc(doc3).getEuclideanLength(), COMPARISON_DELTA);
	}

	private List<Posting> postings(int docID, Float... tfidfs) {
		List<Posting> postings = new LinkedList<>();
		for (int i = 0; i < tfidfs.length; i++) {
			postings.add(posting(docID, tfidfs[i]));
		}
		return postings;
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
