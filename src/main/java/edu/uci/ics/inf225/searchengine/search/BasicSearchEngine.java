package edu.uci.ics.inf225.searchengine.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.tokenizer.PageToken;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenStream;
import edu.uci.ics.inf225.searchengine.tokenizer.TextTokenizer;

public class BasicSearchEngine implements SearchEngine {

	private DocumentIndex docIndex;

	private TermIndex termIndex;

	private TextTokenizer tokenizer;

	public BasicSearchEngine() {
		tokenizer = createTokenizer();
	}

	public void start(String indexFilename) throws IOException {
		readIndexFromDisk(indexFilename);
		tokenizer.start();
	}

	public void start() throws IOException {
		start(Indexer.INDEX_FILENAME);
	}

	private TextTokenizer createTokenizer() {
		return new TextTokenizer();
	}

	public void shutdown() {
		tokenizer.stop();
	}

<<<<<<< 0f2f59a4ebb57ea9f05134145e2530ab4a851e0b
	private void readIndexFromDisk() throws IOException {
		ObjectInputStream inputStream = new ObjectInputStream(
				new FileInputStream(Indexer.INDEX_FILENAME));
=======
	private void readIndexFromDisk(String filename) throws IOException {
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
>>>>>>> b861bf5c598cfde83330b2667fb74a8a7d475e48

		try {
			docIndex = (DocumentIndex) inputStream.readObject();
			termIndex = (TermIndex) inputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			inputStream.close();
		}
	}

	private List<Integer> rank(List<Posting> postings, int num) {
		// TODO Aftab to completely rewrite this method.
		List<Integer> docIDs_final=new ArrayList<>(postings.size());
		List<Integer> docIDs = new ArrayList<>(postings.size());
		List<Double> tfIdfs =new ArrayList<>(postings.size());
		Map docID_map=new HashMap();
		
		for (Posting posting : postings) {
			if (docIDs.contains(posting.getDocID())){
				int marked_idx=docIDs.indexOf(posting.getDocID());
				double current_tfidf=tfIdfs.get(marked_idx);
				double new_tfidf=current_tfidf+posting.getTfidf();
				tfIdfs.set(marked_idx, new_tfidf);
			}
			else {
				docIDs.add(posting.getDocID());
				tfIdfs.add(posting.getTfidf());
			}
			//System.out.println("DocumentID#"+posting.getDocID()+" "+posting.getTfidf()+"check");
		}
		
		for (int i=0;i<docIDs.size();i++){
		docID_map.put(docIDs.get(i), tfIdfs.get(i));}
		
		 List list = new LinkedList(docID_map.entrySet());

	        // sort list based on comparator
	        Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
	            }
	        });

	        // put sorted list into map again
	        //LinkedHashMap preserves order in which keys were inserted
	        String[] arr1 = new String[list.size()], arr2 = new String[list.size()];
	        int i = 0;

	     //   Map freqSorted = new LinkedHashMap();
	        for (Iterator it = list.iterator(); it.hasNext();) {
	            Map.Entry entry = (Map.Entry) it.next();
	           // freqSorted.put(entry.getKey(), entry.getValue());
	            arr1[i] = entry.getKey().toString();
	            arr2[i] = entry.getValue().toString();
	            i++;
	        }

	        //obtaining the list in descending order
	         Map docID_mapD = new LinkedHashMap();
	        for (int j = arr1.length - 1; j > -1; j--) {
	        	docID_mapD.put(arr1[j], arr2[j]);
	        }
	        
	        	System.out.println(docID_mapD);

	        	Iterator iter = docID_mapD.entrySet().iterator();
	  	        for (int j = 0; j < num; j++) {
	  	            Map.Entry mEntry = (Map.Entry) iter.next();
	  	           docIDs_final.add(Integer.parseInt(mEntry.getKey().toString()));
	  	        }
	  	        System.out.println(docIDs_final);
		return docIDs_final;
	}

	public QueryResult query(String query) throws QueryException {
		long start = System.nanoTime();
		PageTokenStream stream = null;
		try {
			stream = tokenizer.tokenize(query);

			List<Posting> postings = new LinkedList<>();
			while (stream.increment()) {
				PageToken token = stream.next();
				System.out.println("#" + token.getTerm());
				postings.addAll(this.termIndex.postingsList(token.getTerm())
						.postings());
			}

			QueryResult queryResult = createQueryResult(rank(postings,10));
			queryResult.setExecutionTime(TimeUnit.NANOSECONDS.toMillis(System
					.nanoTime() - start));
			return queryResult;
		} catch (IOException e) {
			throw new QueryException(e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new QueryException(e);
			}
		}
	}

	private QueryResult createQueryResult(List<Integer> rank) {
		QueryResult queryResult = new QueryResult();

		for (Integer docID : rank) {
			queryResult.addEntry(createQueryResultEntry(docID));
		}

		return queryResult;
	}

	private QueryResultEntry createQueryResultEntry(Integer docID) {
		QueryResultEntry entry = new QueryResultEntry();
		entry.setUrl(docIndex.getDoc(docID));

		return entry;
	}
}