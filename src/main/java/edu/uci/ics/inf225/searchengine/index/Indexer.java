/**
 * 
 */
package edu.uci.ics.inf225.searchengine.index;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author matias
 * 
 */
public class Indexer {

	private static final String INDEX_FILENAME = "index.arm";

	private static final Logger console = LoggerFactory.getLogger("console");

	private Index index;

	public void indexTerm(String term, String url, int position) {
		// TODO To be implemented.
	}

	public void save() throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(INDEX_FILENAME));

		console.info("Saving index to {}...", INDEX_FILENAME);
		stream.writeObject(this.index);
		console.info("Index has been saved.");
		stream.close();
	}

	public void load() throws ClassNotFoundException, IOException {
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(INDEX_FILENAME));

		console.info("Loading index from {}...", INDEX_FILENAME);
		this.index = (Index) stream.readObject();
		console.info("Index has been loaded.");
		stream.close();
	}
}
