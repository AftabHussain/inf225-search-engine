package edu.uci.ics.inf225.searchengine.utils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SerializationUtils {

	public static void writeString(String ascii, ObjectOutput out) throws IOException {
		out.writeUTF(ascii);
	}

	public static String readString(ObjectInput in) throws IOException {
		return in.readUTF();
	}
}
