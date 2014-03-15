package edu.uci.ics.inf225.searchengine.index;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TwoGramsTester {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEqual() {
		Lexicon lexicon = new Lexicon(10);

		TwoGram tg1 = new TwoGram();
		tg1.setTerms("testA", "testB");

		TwoGram tg2 = new TwoGram();
		tg2.setTerms("testA", "testB");

		int id1 = lexicon.getTermID(tg1);
		int id2 = lexicon.getTermID(tg2);

		Assert.assertEquals("TwoGrams are not properly recovered", id1, id2);
	}

}
