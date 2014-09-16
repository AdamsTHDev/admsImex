/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

public class RIDataRecord implements DataRecord {

	private final String[] fieldNames;
	private final Object[] fieldValues;
	private final Map<String, Class<?>> typeMap;
	private boolean updateFlag;
	private boolean deleteFlag;

	public RIDataRecord(String[] fieldNames, Object[] fieldValues)
	{
		this.fieldNames = fieldNames;
		this.fieldValues = fieldValues;

		typeMap = Collections.emptyMap();
	}

	public RIDataRecord(Map<String, Object> map)
	{
		int colCnt = map.size();

		fieldNames = new String[colCnt];
		fieldValues = new Object[colCnt];

		int idx = 0;

		for (Map.Entry<String, Object> entry : map.entrySet())
		{
			String name = entry.getKey();
			Object value = entry.getValue();

			fieldNames[idx] = name;
			fieldValues[idx++] = value;
		}

		typeMap = Collections.emptyMap();
	}

	public RIDataRecord(ResultSet rs, Map<String, Class<?>> typeMap)
			throws SQLException
	{
		this.typeMap = typeMap;

		ResultSetMetaData meta = rs.getMetaData();
		int colCnt = meta.getColumnCount();

		fieldNames = new String[colCnt];
		fieldValues = new Object[colCnt];

		for (int i = 0; i < colCnt; ++i)
		{
			int sqlIdx = i + 1;

			String name = meta.getColumnName(sqlIdx);
			Object value;

			if (null == typeMap)
			{
				value = rs.getObject(sqlIdx);
			}
			else
			{
				value = rs.getObject(sqlIdx, typeMap);
			}

			fieldNames[i] = name;
			fieldValues[i] = value;
		}
	}

	public String[] getFieldNames()
	{
		return fieldNames;
	}

	public Object getFieldValue(String fieldName)
			throws BatchIOException
	{
		int idx = findFieldIndex(fieldName);

		if (-1 == idx)
		{
			throw new BatchIOException("Unknow field - " + fieldName);
		}

		return getFieldValue(idx);
	}

	private int findFieldIndex(String fieldName)
	{
		for (int i = 0; i < fieldNames.length; ++i)
		{
			if (fieldNames[i].equalsIgnoreCase(fieldName))
			{
				return i;
			}
		}

		return -1;
	}

	public Object getFieldValue(int idx)
			throws BatchIOException
	{
		try
		{
			return fieldValues[idx];
		}
		catch (Exception ex)
		{
			throw new BatchIOException(ex);
		}
	}

	public void setFieldValue(String fieldName, Object value)
			throws BatchIOException
	{
		int idx = findFieldIndex(fieldName);

		if (-1 == idx)
		{
			throw new BatchIOException("Unknow field - " + fieldName);
		}

		setFieldValue(idx, value);
	}

	public void setFieldValue(int index, Object value)
			throws BatchIOException
	{
		try
		{
			fieldValues[index] = value;
		}
		catch (Exception ex)
		{
			throw new BatchIOException(ex);
		}
	}

	public void markUpdate()
	{
		updateFlag = true;
	}

	public void markDelete()
	{
		deleteFlag = true;
	}

	public boolean markedUpdate()
	{
		return updateFlag;
	}

	public boolean markedDelete()
	{
		return deleteFlag;
	}

	public void clearUpdate()
	{
		updateFlag = false;
	}

	public void clearDelete()
	{
		deleteFlag = false;
	}

}
