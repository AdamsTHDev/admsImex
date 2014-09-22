/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex;

import java.io.Reader;

public interface BatchReader {

	/**
	 * @return the encoding
	 */
	String getEncoding();

	/**
	 * @return the xmlFile
	 */
	String getFile();

	/**
	 * @return the writer
	 */
	Reader getReader();

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	void setEncoding(String encoding);

	/**
	 * @param xmlFile
	 *            the xmlFile to set
	 */
	void setFile(String outputFile);

	/**
	 * @param writer
	 *            the writer to set
	 */
	void setReader(Reader reader);

	Iterable<DataRecord> read(Object... params)
			throws BatchIOException;

}