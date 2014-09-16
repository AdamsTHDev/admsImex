/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex;

public interface DataRecord {
	String[] getFieldNames();

	Object getFieldValue(String fieldName)
			throws BatchIOException;

	Object getFieldValue(int index)
			throws BatchIOException;

	void setFieldValue(String fieldName, Object value)
			throws BatchIOException;

	void setFieldValue(int index, Object value)
			throws BatchIOException;

	void markUpdate();

	void clearUpdate();

	void markDelete();

	void clearDelete();

	boolean markedUpdate();

	boolean markedDelete();
}