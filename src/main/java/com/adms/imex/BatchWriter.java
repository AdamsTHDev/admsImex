/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex;

import java.io.Writer;
import java.sql.ResultSet;

public interface BatchWriter {

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
	Writer getWriter();

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
	void setWriter(Writer writer);

	void write(ResultSet rs)
			throws BatchIOException;

	public void write(java.lang.Iterable<com.adms.imex.DataRecord> records)
			throws BatchIOException;

	boolean isUpdatable();

}
