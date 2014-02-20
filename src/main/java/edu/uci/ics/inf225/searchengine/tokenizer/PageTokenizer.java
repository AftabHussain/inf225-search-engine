package edu.uci.ics.inf225.searchengine.tokenizer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.index.Indexer;

public class PageTokenizer {

	private static final Logger log = LoggerFactory.getLogger(PageTokenizer.class);

	private static final String CONTENT_FIELD = "content";

	private Indexer indexer;

	private Analyzer analyzer;

	public PageTokenizer() {
		analyzer = createLuceneAnalyzer();
	}

	public Indexer getIndexer() {
		return indexer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	public void init() {
		// Nothing to do, so far.
	}

	public void start() {
		// Nothing to do, so far.
	}

	public void stop() {
		// Nothing to do, so far.
	}

	public void tokenize(String url, String title, String content) throws IOException {
		if (this.isHTML(content)) {
			Document doc = Jsoup.parse(content);

			content = doc.body().text();
		}

		TokenStream ts;

		ts = analyzer.tokenStream(CONTENT_FIELD, new StringReader(content));

		CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute posAtt = ts.addAttribute(PositionIncrementAttribute.class);

		try {
			ts.reset(); // Resets this stream to the beginning.
			while (ts.incrementToken()) {
				this.indexer.indexTerm(termAtt.toString(), url, posAtt.getPositionIncrement());
			}
			ts.end(); // Perform end-of-stream operations, e.g. set
						// the
						// final
						// offset.
		} finally {
			ts.close(); // Release resources associated with this
						// stream.
		}
	}

	private boolean isHTML(String content) {
		String trimmedContent = content.trim();
		return trimmedContent.length() > 5 && trimmedContent.substring(0, 5).equalsIgnoreCase("<html");
	}

	private Analyzer createLuceneAnalyzer() {
		return new SimpleAnalyzer(loadStopWords());
	}

	private List<String> loadStopWords() {
		try {
			List<String> readLines = FileUtils.readLines(new File("cfg/stop_words.txt"));
			readLines.addAll(Arrays.asList(StringUtils.split("0 1 2 3 4 5 6 7 8 9 a b c d e f g h i j k l m n o p q r s t u v w x y z", " ")));
			return readLines;
		} catch (IOException e) {
			log.error("Stop Words could not be loaded.");
			return new ArrayList<String>();
		}
	}
}