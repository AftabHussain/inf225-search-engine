package edu.uci.ics.inf225.searchengine.tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public void testPageIsProperlyTokenized() throws IOException {
		final String title = "Information Retrieval";
		final String content = "We love Information Retrieval in Winter 2014. It's our today's opinion";

		tokenizer.tokenize(URL, title, content);

		final Token[] expectedTokens = { token("love", 2), token("information", 1), token("retrieval", 1), token("winter", 2), token("2014", 1), token("today", 3), token("opinion", 1) };

		indexer.assertTokens(Arrays.asList(expectedTokens));
	}

	private Token token(String term, int deltapos) {
		return new Token(URL, term, deltapos);
	}

	private class Token {
		private String url;
		private String token;
		private int deltapos;

		public Token(String url, String token, int deltapos) {
			this.url = url;
			this.token = token;
			this.deltapos = deltapos;
		}

		@Override
		public boolean equals(Object obj) {
			Token anotherToken = (Token) obj;

			return this.url.equals(anotherToken.url) && this.token.equals(anotherToken.token) && this.deltapos == anotherToken.deltapos;
		}
	}

	private class IndexerTokensCaptor extends Indexer {

		private List<Token> observedTokens = new ArrayList<Token>();

		public IndexerTokensCaptor() {
		}

		@Override
		public void indexTerm(String term, String url, int position) {
			observedTokens.add(new Token(url, term, position));
		}

		public void assertTokens(List<Token> expectedTokens) {
			Assert.assertEquals("Received tokens are different from expected tokens", expectedTokens, observedTokens);
		}
	}
}