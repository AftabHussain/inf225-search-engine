/**
 * 
 */
package edu.uci.ics.inf225.searchengine.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author matias
 * 
 */
public class TwoGram implements Externalizable {

	private String term1;

	private String term2;

	private static final String separator = " ";

	// private static final long serialVersionUID = 1L;
	private static final long serialVersionUID = 5854571193521361902L;

	public TwoGram() {
	}

	public void setTerms(String term1, String term2) {
		this.term1 = term1;
		this.term2 = term2;

	}

	public int length() {
		return term1.length() + separator.length() + term2.length();
	}

	public char charAt(int index) {
		if (index >= this.length()) {
			throw new IndexOutOfBoundsException();
		}
		if (index < this.term1.length()) {
			return this.term1.charAt(index);
		} else if (index < this.term1.length() + separator.length()) {
			return separator.charAt(index - this.term1.length());
		} else {
			return this.term2.charAt(index);
		}
	}

	@Override
	public int hashCode() {
		return term1.hashCode() * 31 + term2.hashCode() * 1009;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TwoGram)) {
			return false;
		}
		TwoGram twoGram2 = (TwoGram) obj;

		return this.term1.equals(twoGram2.term1) && this.term2.equals(twoGram2.term2);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(term1);
		out.writeUTF(term2);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		term1 = in.readUTF();
		term2 = in.readUTF();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.length());
		builder.append(term1).append(separator).append(term2);
		return builder.toString();
	}
}