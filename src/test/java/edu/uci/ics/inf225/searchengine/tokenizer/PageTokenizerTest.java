package edu.uci.ics.inf225.searchengine.tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.inf225.searchengine.index.Indexer;

public class PageTokenizerTest {

	private static final String URL = "http://www.informationretrieval.com";
	private PageTokenizer tokenizer = new PageTokenizer();
	private IndexerTokensCaptor indexer;

	@Before
	public void setup() {
		indexer = new IndexerTokensCaptor();
		tokenizer.setIndexer(indexer);
	}

	@Test
	public void testTXTPageIsProperlyTokenized() throws IOException {
		final String title = "Information Retrieval";
		final String content = "We love Information Retrieval in Winter 2014. It's our today's opinion";
		final Token[] expectedTokens = { token("love", 2), token("information", 1), token("retrieval", 1), token("winter", 2), token("2014", 1), token("today", 3), token("opinion", 1) };

		assertTokenizedContentIsRight(title, content, expectedTokens);
	}

	private void assertTokenizedContentIsRight(final String title, final String content, final Token[] expectedTokens) throws IOException {
		final String parsedContent = preProcess(content);
		tokenizer.tokenize(URL, title, parsedContent);
		System.out.println("Obtained tokens: " + indexer.observedTokens);
		indexer.assertTokens(Arrays.asList(expectedTokens));
	}

	@Test
	public void testHTMLPageIsProperlyTokenized() throws IOException {
		final String title = "Information Retrieval";
		final String content = "<html><title>" + title + "</title><body>We love Information Retrieval in Winter 2014. It's our today's opinion</body></html>";
		final Token[] expectedTokens = { token("love", 2), token("information", 1), token("retrieval", 1), token("winter", 2), token("2014", 1), token("today", 3), token("opinion", 1) };

		assertTokenizedContentIsRight(title, content, expectedTokens);
	}

	private String preProcess(final String content) {
		Document doc = Jsoup.parse(content);
		Element body = doc.body();
		if (body != null) {
			return body.text();
		} else {
			return "";
		}
	}

	private Token token(String term, int deltapos) {
		return new Token(term, deltapos);
	}

	private class Token {
		private String token;
		private int deltapos;

		public Token(String token, int deltapos) {
			this.token = token;
			this.deltapos = deltapos;
		}

		@Override
		public String toString() {
			return token;
		}

		@Override
		public boolean equals(Object obj) {
			Token anotherToken = (Token) obj;

			return this.token.equals(anotherToken.token) && this.deltapos == anotherToken.deltapos;
		}
	}

	private class IndexerTokensCaptor extends Indexer {

		private List<Token> observedTokens = new ArrayList<Token>();

		public IndexerTokensCaptor() {
		}

		@Override
		public void indexTerm(String term, long docID, int position) {
			observedTokens.add(new Token(term, position));
		}

		@Override
		public void indexDoc(String url, List<String> terms, List<Integer> positions) {
			Iterator<String> termsIt = terms.iterator();
			Iterator<Integer> positionsIt = positions.iterator();

			while (termsIt.hasNext()) {
				this.indexTerm(termsIt.next(), 1, positionsIt.next());
			}
		}

		public void assertTokens(List<Token> expectedTokens) {
			Assert.assertEquals("Received tokens are different from expected tokens", expectedTokens, observedTokens);
		}
	}
}