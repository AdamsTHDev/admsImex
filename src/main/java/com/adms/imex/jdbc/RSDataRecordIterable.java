/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.adms.imex.DataRecord;
import com.adms.imex.RIDataRecord;

public class RSDataRecordIterable implements Iterable<DataRecord> {

	private final ResultSet resultSet;
	private final RSDataRecordIterator iterator = new RSDataRecordIterator();

	private Map<String, Class<?>> typeMap;
	private boolean closeConnection;
	private boolean eof;

	public RSDataRecordIterable(ResultSet resultSet)
	{
		this.resultSet = resultSet;
	}

	public void close()
	{
		if (!eof)
		{
			eof = true;

			if (null != resultSet)
			{
				try
				{
					if (closeConnection)
					{
						Statement stm = resultSet.getStatement();
						Connection con = resultSet.getStatement().getConnection();

						stm.close();
						con.close();
					}
					resultSet.close();
				}
				catch (Exception ignore)
				{
					// Do nothing
				}
			}
		}
	}

	public Iterator<DataRecord> iterator()
	{
		return iterator;
	}

	/**
	 * @return the resultSet
	 */
	public ResultSet getResultSet()
	{
		return resultSet;
	}

	/**
	 * @return the typeMap
	 */
	public Map<String, Class<?>> getTypeMap()
	{
		return typeMap;
	}

	/**
	 * @param typeMap
	 *            the typeMap to set
	 */
	public void setTypeMap(Map<String, Class<?>> typeMap)
	{
		this.typeMap = typeMap;
	}

	/**
	 * @return the eof
	 */
	public boolean isEof()
	{
		return eof;
	}

	/**
	 * @return the closeConnection
	 */
	public boolean isCloseConnection()
	{
		return closeConnection;
	}

	/**
	 * @param closeConnection
	 *            the closeConnection to set
	 */
	public void setCloseConnection(boolean closeConnection)
	{
		this.closeConnection = closeConnection;
	}

	protected class RSDataRecordIterator implements Iterator<DataRecord> {

		private DataRecord record;

		public boolean hasNext()
		{
			if (null == record && !eof)
			{
				try
				{
					record = extractDataRecord(getResultSet());
				}
				catch (SQLException ex)
				{
					throw new RuntimeException(ex);
				}
			}

			// FALL THROUGH
			return (null != record);
		}

		public DataRecord next()
		{
			try
			{
				if (hasNext())
				{
					return record;
				}
				else
				{
					throw new NoSuchElementException();
				}
			}
			finally
			{
				record = null;
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		protected DataRecord extractDataRecord(ResultSet rs)
				throws SQLException
		{
			if (rs.next())
			{
				return new RIDataRecord(rs, typeMap);
			}
			else
			{
				close();
				return null;
			}
		}

	}

}