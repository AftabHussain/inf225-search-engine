package edu.uci.ics.inf225.searchengine.tokenizer;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;

public class PageTokenStream {

	private TokenStream ts;

	private static final Logger log = LoggerFactory.getLogger(PageTokenStream.class);

	private static final String CONTENT_FIELD = "content";

	private PageToken pageToken;

	public PageTokenStream(Analyzer analyzer, WebPage page) throws IOException {
		this(analyzer, page, new PageToken());
	}

	public PageTokenStream(Analyzer analyzer, WebPage page, PageToken pageToken) throws IOException {
		if (pageToken == null) {
			pageToken = new PageToken();
		}
		this.pageToken = pageToken;
		this.ts = analyzer.tokenStream(CONTENT_FIELD, new StringReader(page.getContent()));
		this.ts.reset();
	}

	public boolean increment() {
		try {
			return ts.incrementToken();
		} catch (IOException e) {
			log.error("Unexpected error incrementing token.", e);
			return false;
		}
	}

	public PageToken next() {
		CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute posAtt = ts.addAttribute(PositionIncrementAttribute.class);

		this.pageToken.setTerm(termAtt.toString());
		this.pageToken.setPosition(posAtt.getPositionIncrement());
		return this.pageToken;
	}

	public void close() throws IOException {
		this.ts.end();
		this.ts.close();
	}

}
