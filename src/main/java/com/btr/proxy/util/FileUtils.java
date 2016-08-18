package com.btr.proxy.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A utility class for file handling.
 * 
 * @author Rohtash Singh Lakra (rohtash.singh@gmail.com)
 */
public final class FileUtils {
	
	/**
	 * Reads the whole content available into a String.
	 * 
	 * @param bufferedReader
	 * @param close
	 * @return
	 * @throws IOException
	 */
	public static final String readAllContent(BufferedReader bufferedReader, boolean close) throws IOException {
		StringBuilder contents = new StringBuilder();
		if(bufferedReader != null) {
			try {
				String line = null;
				while((line = bufferedReader.readLine()) != null) {
					contents.append(line).append("\n");
				}
			} finally {
				// close stream, if requested.
				if(close) {
					bufferedReader.close();
				}
			}
		}
		
		return contents.toString();
	}
	
	/**
	 * Reads the whole content available into a String.
	 * 
	 * @param bufferedReader
	 * @return
	 * @throws IOException
	 */
	public static final String readAllContent(BufferedReader bufferedReader) throws IOException {
		return readAllContent(bufferedReader, false);
	}
	
	/**
	 * Reads the whole content available into a String.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static final String readAllContent(File file) throws IOException {
		return readAllContent(new BufferedReader(new FileReader(file)), true);
	}
	
}
