package edu.uci.ics.inf225.searchengine.index.serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.AtomicTermIndex;
import edu.uci.ics.inf225.searchengine.index.Lexicon;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.docs.SimpleDocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public class TestSerialization {

	private ObjectOutputStream outputStream;

	private ObjectInputStream inputStream;

	private Random random = new Random();

	@Before
	public void setup() throws IOException {
		PipedOutputStream pipedOutputStream = new PipedOutputStream();
		PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
		outputStream = new ObjectOutputStream(pipedOutputStream);
		inputStream = new ObjectInputStream(pipedInputStream);
	}

	@Test
	public void testPostingSerialization() throws IOException, ClassNotFoundException {
		Posting posting = createRandomPosting();

		Assert.assertEquals("Problem occurred when serializing Posting", posting, marshalled(posting));
	}

	@SuppressWarnings("unchecked")
	private <T> T marshalled(T object) throws IOException, ClassNotFoundException {
		outputStream.writeObject(object);

		T object2 = (T) inputStream.readObject();

		return object2;
	}

	private WebPage page(String url) {
		WebPage page = new WebPage();
		page.setUrl(url);
		return page;
	}

	@Test
	public void testAtomicTermIndexSerialization() throws ClassNotFoundException, IOException {
		DocumentIndex docIndex = new SimpleDocumentIndex();
		AtomicTermIndex termIndex = new AtomicTermIndex();

		int docA = docIndex.addDoc(page("A"));
		int docB = docIndex.addDoc(page("B"));
		int docC = docIndex.addDoc(page("C"));

		termIndex.newTerm(docA, 1);
		termIndex.newTerm(docA, 2);
		termIndex.newTerm(docA, 3);

		termIndex.newTerm(docB, 1);
		termIndex.newTerm(docB, 2);
		termIndex.newTerm(docB, 3);

		termIndex.newTerm(docC, 1);
		termIndex.newTerm(docC, 2);
		termIndex.newTerm(docC, 3);

		Assert.assertEquals("Problem occurred when serializing AtomicTermIndex", termIndex, marshalled(termIndex));
	}

	@Test
	public void testSimpleDirectoryIndex() throws ClassNotFoundException, IOException {
		DocumentIndex docIndex = new SimpleDocumentIndex();

		docIndex.addDoc(page("A"));
		docIndex.addDoc(page("B"));
		docIndex.addDoc(page("C"));

		Assert.assertEquals("Problem occurred when serializing SimpleDirectoryIndex", docIndex, marshalled(docIndex));
	}

	@Test
	public void testLexicon() throws ClassNotFoundException, IOException {
		Lexicon lexicon = new Lexicon();

		lexicon.getTermID("A");
		lexicon.getTermID("B");
		lexicon.getTermID("C");

		Assert.assertEquals("Problem occurred when serializing Lexicon", lexicon, marshalled(lexicon));
	}

	@Test
	public void testPostingsList() throws ClassNotFoundException, IOException {
		PostingsList postingsList = new PostingsList();
		postingsList.addPosting(createRandomPosting());
		postingsList.addPosting(createRandomPosting());

		Assert.assertEquals("Problem occurred when serializing PostingsList", postingsList, marshalled(postingsList));
	}

	private int random() {
		return random(1, 5);
	}

	private int random(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	private Posting createRandomPosting() {
		Posting posting = new Posting(random(), random());
		return posting;
	}
}
