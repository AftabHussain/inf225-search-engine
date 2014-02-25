package edu.uci.ics.inf225.searchengine.tokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.utils.HTMLUtils;

public class PageTokenizerTest {

	private static final String URL = "http://www.informationretrieval.com";
	private PageTokenizer tokenizer = new PageTokenizer();

	@Before
	public void setup() {
		tokenizer.start();
	}

	@After
	public void tearDown() {
		tokenizer.stop();
	}

	@Test
	public void testTXTPageIsProperlyTokenized() throws IOException {
		final String title = "Information Retrieval";
		final String content = "We love Information Retrieval in Winter 2014. It's our today's opinion";
		final PageToken[] expectedTokens = { token("love", 2), token("information", 1), token("retrieval", 1), token("winter", 2), token("2014", 1), token("today", 3), token("opinion", 1) };

		WebPage page = new WebPage();
		page.setContent(HTMLUtils.parse(content));
		page.setHtmlContent(content);
		page.setTitle(title);
		page.setUrl(URL);

		assertTokenizedContentIsRight(page, Arrays.asList(expectedTokens));
	}

	private PageTokenStream assertTokenizedContentIsRight(WebPage page, final List<PageToken> expectedTokens, PageToken pageToken) throws IOException {
		PageTokenStream tokenStream = tokenizer.tokenize(page, pageToken);

		Iterator<PageToken> expectedTokenIterator = expectedTokens.iterator();

		while (tokenStream.increment()) {
			PageToken observedToken = tokenStream.next();
			Assert.assertEquals("Tokens are different", expectedTokenIterator.next(), observedToken);
		}

		return tokenStream;
	}

	private PageTokenStream assertTokenizedContentIsRight(WebPage page, final List<PageToken> expectedTokens) throws IOException {
		return assertTokenizedContentIsRight(page, expectedTokens, null);
	}

	private PageToken token(String term, int position) {
		PageToken token = new PageToken();
		token.setTerm(term);
		token.setPosition(position);
		return token;
	}

	@Test
	public void testHTMLPageIsProperlyTokenized() throws IOException {
		final String title = "Information Retrieval";
		final String content = "<html><title>" + title + "</title><body>We love Information Retrieval in Winter 2014. It's our today's opinion</body></html>";
		final PageToken[] expectedTokens = { token("love", 2), token("information", 1), token("retrieval", 1), token("winter", 2), token("2014", 1), token("today", 3), token("opinion", 1) };

		WebPage page = new WebPage();
		page.setContent(HTMLUtils.parse(content));
		page.setHtmlContent(content);
		page.setTitle(title);
		page.setUrl(URL);

		assertTokenizedContentIsRight(page, Arrays.asList(expectedTokens));
	}
}