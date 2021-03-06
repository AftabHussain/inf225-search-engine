package edu.uci.ics.inf225.searchengine.index.docs;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.MultiKeyMap;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.MultiFieldTermIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import gnu.trove.map.hash.TObjectFloatHashMap;

public class HSQLDocumentIndex implements DocumentIndex, Externalizable {

	private static final int NUMBER_OF_METADATA_FIELDS = 2;

	/*
	 * Field indices.
	 */
	public static final int EUCLIDEAN_LENGTH_INDEX = 0;
	public static final int SLASHES_INDEX = 1;

	private static final int MAX_DESC_LENGTH = 500;

	private static final String DOCS_TABLE = "docs";

	private static final String DB_PATH = "db/docindex";

	private static final int MAX_TITLE_LENGTH = 300;

	transient private Connection conn = null;

	transient private PreparedStatement insertPS;

	transient private PreparedStatement selectPS;

	/**
	 * This map contains all of the data associated to each document. It would
	 * be similar {@link WebPage} but we use an array of Objects to save space.
	 */
	private Map<Integer, Object[]> docsData;

	private MultiKeyMap termsPerDoc;

	private int counter;

	private static final long serialVersionUID = 1L;

	public HSQLDocumentIndex() throws ClassNotFoundException, SQLException {
		docsData = new HashMap<Integer, Object[]>();
		termsPerDoc = new MultiKeyMap();
		connect();
		// destroyDatabase();
		createInsertPreparedStatement();
		createSelectPreparedStatement();
	}

	public void destroyDatabase() throws SQLException {
		deleteTable();
		createtables();
	}

	private void createInsertPreparedStatement() throws SQLException {
		this.insertPS = conn.prepareStatement("INSERT INTO " + DOCS_TABLE + " (docid,url,title,desc) VALUES (?,?,?,?)");
	}

	private void createSelectPreparedStatement() throws SQLException {
		this.selectPS = conn.prepareStatement("SELECT * FROM " + DOCS_TABLE + " WHERE docid=?");
	}

	private void createtables() throws SQLException {
		Statement st = null;
		st = conn.createStatement();
		st.execute("CREATE TABLE IF NOT EXISTS " + DOCS_TABLE + " ( docid INTEGER, url VARCHAR(1000), title VARCHAR(" + MAX_TITLE_LENGTH + "), desc VARCHAR(" + MAX_DESC_LENGTH + "))");
		st.execute("CREATE INDEX idx_docid ON " + DOCS_TABLE + " (docid)");

		st.close();
	}

	private void deleteTable() throws SQLException {
		Statement st = null;
		ResultSet rs = null;

		try {
			st = conn.createStatement();
			rs = st.executeQuery("DROP TABLE if exists " + DOCS_TABLE);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		}
	}

	private void connect() throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbcDriver");
		conn = DriverManager.getConnection("jdbc:hsqldb:file:" + DB_PATH, "sa", "");
		conn.setAutoCommit(false);
	}

	private void insert(int docID, WebPage page) throws SQLException {
		insertPS.setInt(1, docID);
		insertPS.setString(2, page.getUrl());

		if (page.getTitle() != null) {
			insertPS.setString(3, page.getTitle().substring(0, Math.min(page.getTitle().length(), MAX_TITLE_LENGTH)));
		} else {
			insertPS.setNull(3, Types.NULL);
		}

		if (page.getContent() != null) {
			insertPS.setString(4, page.getContent().substring(0, Math.min(MAX_DESC_LENGTH, page.getContent().length())));
		} else {
			insertPS.setNull(4, Types.NULL);
		}

		insertPS.addBatch();
		insertPS.executeBatch();
		conn.commit();
	}

	@Override
	public int addDoc(WebPage page) {
		Integer docID = DocIDGenerator.getInstance().next();

		try {
			this.insert(docID, page);
			initDocData(page, docID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		counter++;

		return docID;
	}

	private void initDocData(WebPage page, Integer docID) {
		Object[] docData = createEmptyDocData();
		docData[SLASHES_INDEX] = computeSlashes(page.getUrl());
		docData[EUCLIDEAN_LENGTH_INDEX] = new TObjectFloatHashMap<String>();
		this.docsData.put(docID, docData);
	}

	private byte computeSlashes(String url) {
		// FIXME URL should be canonicalized to prevent double slashes and
		// things like that.
		byte slashes = 0;
		for (int i = 0; i < url.length(); i++) {
			if (url.charAt(i) == '/' && i + 1 < url.length()) { // Don't count
																// last slash.
				slashes++;
			}
		}
		return slashes;
	}

	private Object[] createEmptyDocData() {
		return new Object[NUMBER_OF_METADATA_FIELDS];
	}

	@Override
	public int count() {
		return counter;
	}

	@Override
	public WebPage getDoc(int docID) {
		try {
			this.selectPS.setInt(1, docID);
			ResultSet resultSet = this.selectPS.executeQuery();

			WebPage page = null;
			if (resultSet.next()) {
				page = new WebPage();
				page.setUrl(resultSet.getString(2));
				page.setTitle(resultSet.getString(3));
				page.setContent(resultSet.getString(4));
				Object[] docData = this.docsData.get(docID);
				if (docData != null) {
					if (docData[EUCLIDEAN_LENGTH_INDEX] != null) {
						page.setEuclideanLength((TObjectFloatHashMap<String>) docData[EUCLIDEAN_LENGTH_INDEX]);
					}

					if (docData[SLASHES_INDEX] != null) {
						page.setSlashes((byte) docData[SLASHES_INDEX]);
					}
				}
			}
			resultSet.close();
			return page;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void shutdown() {
		try {
			Statement st = conn.createStatement();
			st.execute("SHUTDOWN");
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(counter);
		out.writeInt(this.docsData.size());
		for (Entry<Integer, Object[]> eachDocData : this.docsData.entrySet()) {
			out.writeInt(eachDocData.getKey());
			out.writeObject(eachDocData.getValue());
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		counter = in.readInt();
		int count = in.readInt();

		this.docsData = new HashMap<>(count);

		for (int i = 0; i < count; i++) {
			this.docsData.put(in.readInt(), (Object[]) in.readObject());
		}
	}

	@Override
	public void prepare(MultiFieldTermIndex multiTermIndex) {
		for (String field : multiTermIndex.fields()) {

			for (int i = 1; i <= counter; i++) {
				// Taking advantage of predictable doc IDs...
				List<Posting> postings = getPostingsForDoc(i, multiTermIndex, field);

				/*
				 * Compute metadata.
				 */
				Object[] aDocData = docsData.get(i);

				/*
				 * Calculate Eucledian Length.
				 */
				((TObjectFloatHashMap<String>) aDocData[EUCLIDEAN_LENGTH_INDEX]).put(field, calculateEuclideanLength(postings));
			}
		}
	}

	private List<Posting> getPostingsForDoc(int docID, MultiFieldTermIndex multiTermIndex, String field) {
		List<Posting> postings = new LinkedList<>();

		int[] terms = this.getTerms(docID, field);

		for (int i = 0; i < terms.length; i++) {
			PostingsList postingsList = multiTermIndex.getIndex(field).postingsList(terms[i]);

			Posting posting = postingsList.get(docID);
			if (posting != null) {
				postings.add(posting);
			}
		}

		return postings;
	}

	private float calculateEuclideanLength(List<Posting> postings) {
		float sumOfWeights = 0f;

		for (Posting posting : postings) {
			sumOfWeights += Math.pow(posting.getTfidf(), 2);
		}
		return (float) Math.sqrt(sumOfWeights);
	}

	@Override
	public void setTerms(int docID, String field, int[] termIDs) {
		// Map<String, int[]> termsPerField = this.termsPerDoc.get(docID);
		// if (termsPerField == null) {
		// termsPerField = new HashMap<>(4);
		// this.termsPerDoc.put(docID, termsPerField);
		// }
		// termsPerField.put(field, termIDs);
		this.termsPerDoc.put(docID, field, termIDs);
	}

	@Override
	public int[] getTerms(int docID, String field) {
		// Map<String, int[]> termsPerField = this.termsPerDoc.get(docID);
		// int[] termIDs = termsPerField.get(key);
		int[] termIDs = (int[]) this.termsPerDoc.get(docID, field);

		if (termIDs != null) {
			return termIDs;
		} else {
			return new int[0];
		}
	}
}