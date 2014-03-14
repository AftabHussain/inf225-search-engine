/**
 * 
 */
package edu.uci.ics.inf225.searchengine.index;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * @author matias
 * 
 */
public class Indexer {

	public static final String INDEX_FILENAME = "index.arm";

	private static final Logger console = LoggerFactory.getLogger("console");

	private MultiFieldTermIndex multiFieldTermIndex;

	private DocumentIndex docIndex;

	private Lexicon lexicon;

	public Indexer(MultiFieldTermIndex multiFieldTermIndex, DocumentIndex docIndex, Lexicon lexicon) {
		this.multiFieldTermIndex = multiFieldTermIndex;
		this.docIndex = docIndex;
		this.lexicon = lexicon;
	}

	public DocumentIndex getDocIndex() {
		return docIndex;
	}

	public int startDoc(WebPage page) {
		return this.docIndex.addDoc(page);
	}

	public void indexTerms(List<String> terms, int docID, String field) {
		TIntSet termIDs = new TIntHashSet(terms.size() * 2);

		TermIndex termIndex = this.getTermIndexForField(field);

		for (String term : terms) {
			int termID = lexicon.getTermID(term);
			termIndex.newTerm(docID, termID);
			termIDs.add(termID);
		}

		List<TwoGram> twoGrams = index2Grams(docID, terms, termIndex);

		for (TwoGram twoGram : twoGrams) {
			Integer termID = lexicon.getTermID(twoGram);
			termIndex.newTerm(docID, termID);
			termIDs.add(termID);
		}

		this.docIndex.setTerms(docID, termIDs.toArray());
	}

	private List<TwoGram> index2Grams(int docID, List<String> terms, TermIndex termIndex) {
		TwoGram[] twoGrams = new TwoGram[terms.size() - 1];

		String previousTerm = null;
		int i = 0;
		for (Iterator<String> iterator = terms.iterator(); iterator.hasNext();) {
			String term = iterator.next();

			if (previousTerm != null) {
				TwoGram tg = new TwoGram();
				tg.setTerms(previousTerm, term);

				twoGrams[i++] = tg;
			}
			previousTerm = term;
		}

		return Arrays.asList(twoGrams);
	}

	private TermIndex getTermIndexForField(String field) {
		TermIndex termIndex = this.multiFieldTermIndex.getIndex(field);

		if (termIndex == null) {
			termIndex = createTermIndex();
			this.multiFieldTermIndex.putIndex(field, termIndex);
		}

		return termIndex;
	}

	protected TermIndex createTermIndex() {
		return new AtomicTermIndex(10000000);
	}

	public void save(String filename) throws IOException {
		printIndexStats();

		console.info("Storing index to {}...", filename);
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
		console.info("Storing Lexicon...");
		stream.writeObject(this.lexicon);
		console.info("Storing Document Index...");
		stream.writeObject(this.docIndex);
		console.info("Storing Term Index...");
		stream.writeObject(this.multiFieldTermIndex);
		stream.close();
		console.info("Index has been saved.");
	}

	public void save() throws IOException {
		this.save(INDEX_FILENAME);
	}

	public void printIndexStats() {
		console.info("-----------------------------------------------");
		console.info("Starts at " + new Date());
		console.info("Number of documents: " + docIndex.count());
		console.info("Number of unique words: " + lexicon.size());
		console.info("-----------------------------------------------");
	}

	public void load() throws ClassNotFoundException, IOException {
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(INDEX_FILENAME));

		console.info("Loading index from {}...", INDEX_FILENAME);
		this.multiFieldTermIndex = (MultiFieldTermIndex) stream.readObject();
		console.info("Index has been loaded.");
		stream.close();
	}
}