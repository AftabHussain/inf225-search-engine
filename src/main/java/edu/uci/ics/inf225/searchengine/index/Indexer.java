/**
 * 
 */
package edu.uci.ics.inf225.searchengine.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author matias
 * 
 */
public class Indexer {

	private static final int STATS_THRESHOLD = 10000;

	private static final String INDEX_SNAPSHOT_PREFIX = "indexsnapshot";

	private static final int SNAPSHOT_THRESHOLD = 50000;

	private static final String INDEX_FILENAME = "index.arm";

	private static final Logger console = LoggerFactory.getLogger("console");

	private Index index;

	private int counter = 0;

	private DocIDGenerator docIDGenerator;

	public Indexer() {
		this.index = new Index();
		docIDGenerator = new DocIDGenerator();
	}

	public Index getIndex() {
		return index;
	}

	public void indexDoc(String url, List<String> terms, List<Integer> positions) {
		long docID = this.startDoc(url);
		this.index.indexDoc(docID, terms, positions);
		this.endDoc(docID);
	}

	public long startDoc(String url) {
		Long nextID = docIDGenerator.next();
		this.index.newDocument(url, nextID);
		return nextID;
	}

	public synchronized void endDoc(long docID) {
		counter++;
		if(counter == STATS_THRESHOLD) {
			this.printIndexStats();
			counter = 0;
		}
//		if (counter == SNAPSHOT_THRESHOLD) {
//			try {
//				this.snapshot();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	public void indexTerm(String term, long docID, int position) {
		this.index.indexTerm(docID, term, position);
	}

	public void sort() {
		this.index.sort();
	}

	public void snapshot() throws IOException {
		console.info("Taking snapshot...");
		try {
			removeLastSnapshot();
			save(generateSnapshotFilename());
			console.info("Snapshot taken.");
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	private String generateSnapshotFilename() {
		return INDEX_SNAPSHOT_PREFIX + System.currentTimeMillis() + ".arm";
	}

	private void removeLastSnapshot() throws IOException {
		Collection<File> files = FileUtils.listFiles(new File("."), new PrefixFileFilter(INDEX_SNAPSHOT_PREFIX), TrueFileFilter.INSTANCE);

		for (File file : files) {
			console.info("Removing {}...", file.getName());
			FileUtils.forceDelete(file);
			console.info("Removed.");
		}
	}

	public void save(String filename) throws IOException {
		printIndexStats();

		console.info("Saving index to {}...", filename);
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
		stream.writeObject(this.index);
		stream.close();
		console.info("Index has been saved.");
	}

	public void save() throws IOException {
		this.save(INDEX_FILENAME);
	}

	private void printIndexStats() {
		console.info("-----------------------------------------------");
		console.info("Starts at " + new Date());
		console.info("Number of documents: " + index.numberOfDocuments());
		console.info("Number of unique words: " + index.numberOfUniqueTerms());
		console.info("-----------------------------------------------");
	}

	public void load() throws ClassNotFoundException, IOException {
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(INDEX_FILENAME));

		console.info("Loading index from {}...", INDEX_FILENAME);
		this.index = (Index) stream.readObject();
		console.info("Index has been loaded.");
		stream.close();
	}

	protected static class DocIDGenerator implements Iterator<Long> {

		private volatile long nextId;

		public DocIDGenerator() {
			nextId = 1;
		}

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Long next() {
			return nextId++;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
