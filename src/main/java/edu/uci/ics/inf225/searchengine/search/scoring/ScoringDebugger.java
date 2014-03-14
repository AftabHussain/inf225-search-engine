package edu.uci.ics.inf225.searchengine.search.scoring;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;

public class ScoringDebugger {

	public String dump(DocumentIndex docIndex, QueryScorer queryScorer) {
		StringBuilder builder = new StringBuilder();

		List<DocScorer> scorers = queryScorer.sortedScorers();

		for (DocScorer scorer : scorers) {
			WebPage doc = docIndex.getDoc(scorer.getDocID());
			builder.append("URL: ").append(doc.getUrl()).append(" (").append(scorer.score()).append(")").append("\n");
			builder.append("Txt Cos Sim=[").append(scorer.getTextCosineSimilarity()).append("] Slashes=[").append(scorer.getNumberOfSlashes()).append("] ");
			builder.append("Title Cos Sim=[").append(scorer.getTitleCosineSimilarity()).append("]");
			builder.append("\n");
		}

		return builder.toString();
	}

	public void dump(DocumentIndex docIndex, QueryScorer queryScorer, String filename) throws IOException {
		FileWriter writer = new FileWriter(filename);

		writer.write(this.dump(docIndex, queryScorer));

		writer.close();
	}
}
