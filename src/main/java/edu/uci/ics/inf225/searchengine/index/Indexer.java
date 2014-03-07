/**
 * 
 */
package edu.uci.ics.inf225.searchengine.index;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;

/**
 * @author matias
 * 
 */
public class Indexer {

	public static final String INDEX_FILENAME = "index.arm";

	private static final Logger console = LoggerFactory.getLogger("console");

	private TermIndex termIndex;

	private DocumentIndex docIndex;

	public Indexer(TermIndex termIndex, DocumentIndex docIndex) {
		this.termIndex = termIndex;
		this.docIndex = docIndex;
	}

	public TermIndex getTermIndex() {
		return termIndex;
	}

	public DocumentIndex getDocIndex() {
		return docIndex;
	}

	public int startDoc(WebPage page) {
		return this.docIndex.addDoc(page);
	}

	public void indexTerm(String term, int docID, byte tokenType) {
		this.termIndex.newTerm(docID, term, tokenType);
	}

	public void save(String filename) throws IOException {
		printIndexStats();

		console.info("Storing index to {}...", filename);
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
		console.info("Storing Document Index...");
		stream.writeObject(this.docIndex);
		console.info("Storing Term Index...");
		stream.writeObject(this.termIndex);
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
		console.info("Number of unique words: " + termIndex.count());
		console.info("-----------------------------------------------");
	}

	public void load() throws ClassNotFoundException, IOException {
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(INDEX_FILENAME));

		console.info("Loading index from {}...", INDEX_FILENAME);
		this.termIndex = (TermIndex) stream.readObject();
		console.info("Index has been loaded.");
		stream.close();
	}
}