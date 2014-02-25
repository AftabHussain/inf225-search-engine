package edu.uci.ics.inf225.searchengine.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public class IndexLoaderTest {

	private static final Logger log = LoggerFactory.getLogger(IndexLoaderTest.class);

	private DocumentIndex docIndex;

	private AtomicTermIndex termIndex;

	@Test
	public void loadIndexFromDisk() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(Indexer.INDEX_FILENAME));

		log.info("Loading Document index...");
		docIndex = (DocumentIndex) inputStream.readObject();

		log.info("Loading Term index...");
		termIndex = (AtomicTermIndex) inputStream.readObject();

		inputStream.close();

		Indexer indexer = new Indexer(termIndex, docIndex);

		indexer.printIndexStats();

		PostingsList postingsList = termIndex.postingsList("software");

		printPostingsList(postingsList);
	}

	private void printPostingsList(PostingsList postingsList) {
		Iterator<Posting> iterator = postingsList.iterator();

		while (iterator.hasNext()) {
			Posting posting = iterator.next();

			System.out.println(posting);
		}
	}
}
