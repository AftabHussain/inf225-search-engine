package edu.uci.ics.inf225.searchengine.dbreader;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.collections.Predicate;

public class ClobSizePredicate implements Predicate {

	private long size; // bytes

	private int columnIndex;

	public ClobSizePredicate(long size, int columnIndex) {
		this.size = size;
		this.columnIndex = columnIndex;
	}

	@Override
	public boolean evaluate(Object object) {
		ResultSet rs = (ResultSet) object;

		try {
			Clob clob = rs.getClob(columnIndex);

			return clob.length() < size;
		} catch (SQLException e) {
			return false;
		}
	}

}
