/**
 * 
 */
package edu.uci.ics.inf225.searchengine.tokenizer;

import java.io.Reader;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

/**
 * Lucene Analyzer implementation written to match the requirements of INF-225.
 * See {@link SimpleAnalyzer#createComponents(String, Reader)} for details.
 * 
 * @author mgiorgio
 * 
 */
public class SimpleAnalyzer extends Analyzer {

	private List<String> stopWords;

	/**
	 * 
	 */
	public SimpleAnalyzer(List<String> stopWords) {
		this.stopWords = stopWords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.lucene.analysis.Analyzer#createComponents(java.lang.String,
	 * java.io.Reader)
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		/*
		 * Splits input by white space.
		 */
		WhitespaceTokenizer source = new WhitespaceTokenizer(Version.LUCENE_46, reader);

		TokenStream result = null;

		/*
		 * Removes possessive 's from words. Also, splits words by
		 * non-alphanumeric characters such as slash, hyphens, etc.
		 */
		int configurationFlags = WordDelimiterFilter.STEM_ENGLISH_POSSESSIVE | WordDelimiterFilter.GENERATE_WORD_PARTS | WordDelimiterFilter.GENERATE_NUMBER_PARTS;
		result = new WordDelimiterFilter(source, configurationFlags, null);

		/*
		 * Normalize accents and strange characters.
		 */
		result = new ASCIIFoldingFilter(result);

		/*
		 * Normalize tokens to lower-case.
		 */
		result = new LowerCaseFilter(Version.LUCENE_46, result);
		result = new StopFilter(Version.LUCENE_46, result, new CharArraySet(Version.LUCENE_46, stopWords, false));

		TokenStreamComponents components = new TokenStreamComponents(source, result);

		return components;
	}
}
