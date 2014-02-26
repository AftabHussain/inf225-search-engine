package edu.uci.ics.inf225.searchengine.tokenizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;

public class TextTokenizer {

	private static final Logger log = LoggerFactory.getLogger(TextTokenizer.class);

	private Analyzer analyzer;

	public TextTokenizer() {
	}

	public void init() {
		// Nothing to do, so far.
	}

	public void start() {
		analyzer = createLuceneAnalyzer();
	}

	public void stop() {
		analyzer.close();
	}

	public PageTokenStream tokenize(WebPage page) throws IOException {
		return tokenize(page, new PageToken());
	}

	public PageTokenStream tokenize(String text) throws IOException {
		return createPageTokenStream(text, new PageToken());
	}

	private PageTokenStream createPageTokenStream(String text, PageToken pageToken) throws IOException {
		return new PageTokenStream(analyzer, text, pageToken);
	}

	public PageTokenStream tokenize(WebPage page, PageToken pageToken) throws IOException {
		return createPageTokenStream(page.getContent(), pageToken);
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