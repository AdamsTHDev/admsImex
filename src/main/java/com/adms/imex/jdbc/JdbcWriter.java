/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.jdbc;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.adms.imex.BaseWriter;
import com.adms.imex.BatchIOException;
import com.adms.imex.DataRecord;

public class JdbcWriter extends BaseWriter {

	private Connection con;
	private DataSource dataSource;
	private String entityName;
	private int bufferSize = 1000;
	private boolean connectionInternal;
	private String[] primaryKeys;
	private boolean debug;

	// private int txCnt;

	public JdbcWriter()
	{
	}

	public void write(ResultSet rs)
			throws BatchIOException
	{
		RSDataRecordIterable iterable = new RSDataRecordIterable(rs);
		try
		{
			iterable.setTypeMap(getTypeMap());

			write(iterable);
		}
		finally
		{
			if (null != iterable)
			{
				if (!iterable.isEof())
				{
					iterable.close();
				}
			}
			else
			{
				disposeResultSet(rs);
			}
		}
	}

	public void write(Iterable<DataRecord> records)
			throws BatchIOException
	{
		Connection aCon = null;
		JdbcWriterContext ctx = null;
		try
		{
			aCon = acquireConnection();
			ctx = new JdbcWriterContext(aCon);

			for (DataRecord record : records)
			{
				if (isDebug())
				{
					for (String name : record.getFieldNames())
					{
						System.out.println(name + "=" + record.getFieldValue(name));
					}

					System.out.println("-----------------------------");
				}

				ctx.writeDataRecord(record);
			}

			ctx.flush();

			if (connectionInternal)
			{
				aCon.commit();
			}
		}
		catch (Exception ex)
		{
			if (null != aCon)
			{
				try
				{
					if (!aCon.isClosed() && connectionInternal)
					{
						aCon.rollback();
					}
				}
				catch (Exception ignore)
				{
					// Do nothing
				}
			}

			throw new BatchIOException(ex);
		}
		finally
		{
			if (null != ctx)
			{
				ctx.close();
			}

			if (connectionInternal)
			{
				JdbcUtils.close(con);
			}
		}
	}

	protected Connection acquireConnection()
			throws SQLException
	{
		if (null != con && !con.isClosed())
		{
			connectionInternal = false;

			return con;
		}
		else if (null != dataSource)
		{
			Connection aCon = dataSource.getConnection();

			connectionInternal = true;
			con = aCon;

			return aCon;
		}
		else
		{
			throw new SQLException("Neither connection nor dataSource are provided");
		}
	}

	protected String generateSqlSelect(String[] fieldNames)
	{
		StringBuilder buffer = new StringBuilder().append("SELECT ");

		int cnt = 0;

		for (String name : fieldNames)
		{
			if (0 < cnt++)
			{
				buffer.append(", ");
			}

			buffer.append(name);
		}

		buffer.append(" FROM ").append(entityName);

		return buffer.toString();
	}

	protected String generateSqlUpdate(String[] fieldNames, Map<String, Integer> columnTypes)
			throws BatchIOException
	{
		StringBuilder buffer = new StringBuilder().append("UPDATE ").append(entityName).append(" SET ");

		if (null == primaryKeys || primaryKeys.length == 0)
		{
			throw new BatchIOException("No primary key found for " + entityName);
		}

		int cnt = 0;
		for (String name : fieldNames)
		{
			if (!isPrimaryKey(name))
			{
				if (0 < cnt++)
				{
					buffer.append(", ");
				}

				buffer.append(name).append(" = ?");
			}
		}

		buffer.append(" WHERE ");

		cnt = 0;
		for (String name : primaryKeys)
		{
			if (0 < cnt++)
			{
				buffer.append(", ");
			}

			buffer.append(name).append(" = ?");
		}

		return buffer.toString();
	}

	protected String generateSqlDelete(String[] fieldNames, Map<String, Integer> columnTypes)
			throws BatchIOException
	{
		StringBuilder buffer = new StringBuilder().append("DELETE FROM ").append(entityName).append(" WHERE ");

		if (null == primaryKeys || primaryKeys.length > 0)
		{
			throw new BatchIOException("No primary key found for " + entityName);
		}

		int cnt = 0;
		for (String name : primaryKeys)
		{
			if (0 < cnt++)
			{
				buffer.append(", ");
			}

			buffer.append(name).append(" = ?");
		}

		return buffer.toString();
	}

	protected String generateSqlInsert(String[] fieldNames, Map<String, Integer> columnTypes)
	{
		StringBuilder buffer = new StringBuilder().append("INSERT INTO ").append(entityName).append('(');

		int cnt = 0;

		for (String name : fieldNames)
		{
			if (0 < cnt++)
			{
				buffer.append(", ");
			}

			buffer.append(name);
		}

		buffer.append(") VALUES (");

		for (int i = 0; i < cnt; ++i)
		{
			if (i > 0)
			{
				buffer.append(", ");
			}

			buffer.append('?');
		}

		buffer.append(')');

		return buffer.toString();
	}

	/**
	 * @return the con
	 */
	public Connection getConnection()
	{
		return con;
	}

	/**
	 * @param con
	 *            the con to set
	 */
	public void setConnection(Connection con)
	{
		this.con = con;
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource()
	{
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName()
	{
		return entityName;
	}

	/**
	 * @param entityName
	 *            the entityName to set
	 */
	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

	/**
	 * @return the bufferSize
	 */
	public int getBufferSize()
	{
		return bufferSize;
	}

	/**
	 * @param bufferSize
	 *            the bufferSize to set
	 */
	public void setBufferSize(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}

	protected void retrieveColumnTypes(ResultSet rs, Map<String, Integer> columnTypes)
			throws SQLException
	{
		ResultSetMetaData meta = rs.getMetaData();

		columnTypes.clear();

		for (int i = 1; i <= meta.getColumnCount(); ++i)
		{
			String colName = meta.getColumnName(i);
			int colType = meta.getColumnType(i);

			columnTypes.put(colName, colType);
		}
	}

	protected Object transformValueIfNecessary(Object value)
	{
		if (value instanceof java.util.Date)
		{
			java.sql.Date date = new java.sql.Date(((java.util.Date) value).getTime());

			return date;
		}
		else
		{
			return value;
		}
	}

	protected void mapParamsInsert(DataRecord record, PreparedStatement stm, Map<String, Integer> columnTypes)
			throws BatchIOException, SQLException
	{
		for (int i = 0; i < record.getFieldNames().length; ++i)
		{
			String name = record.getFieldNames()[i];
			Object value = record.getFieldValue(i);

			try
			{
				if (null == value)
				{
					stm.setNull(i + 1, columnTypes.get(name));
				}
				else
				{
					value = transformValueIfNecessary(value);
					stm.setObject(i + 1, value);
				}
			}
			catch (Throwable ex)
			{
				System.out.println(getClass() + ".mapParamsInsert() - i = " + i + ", name = " + name + ", value = " + value);
				ex.printStackTrace();

				throw new BatchIOException(ex);
			}
		}
	}

	protected void mapParamsDelete(DataRecord record, PreparedStatement stm, Map<String, Integer> columnTypes)
			throws BatchIOException, SQLException
	{
		int idx = 1;
		for (String name : primaryKeys)
		{
			Object value = record.getFieldValue(name);

			if (null == value)
			{
				stm.setNull(idx++, columnTypes.get(name));
			}
			else
			{
				value = transformValueIfNecessary(value);
				stm.setObject(idx++, value);
			}
		}
	}

	protected void mapParamsUpdate(DataRecord record, PreparedStatement stm, Map<String, Integer> columnTypes)
			throws BatchIOException, SQLException
	{
		int idx = 1;
		for (String name : record.getFieldNames())
		{
			if (!isPrimaryKey(name))
			{
				Object value = record.getFieldValue(name);

				if (null == value)
				{
					stm.setNull(idx++, columnTypes.get(name));
				}
				else
				{
					value = transformValueIfNecessary(value);
					stm.setObject(idx++, value);
				}
			}
		}

		for (String name : primaryKeys)
		{
			Object value = record.getFieldValue(name);

			if (null == value)
			{
				stm.setNull(idx++, columnTypes.get(name));
			}
			else
			{
				value = transformValueIfNecessary(value);
				stm.setObject(idx++, value);
			}
		}
	}

	@Override
	public boolean isUpdatable()
	{
		return true;
	}

	private boolean isPrimaryKey(String name)
	{
		for (String pk : primaryKeys)
		{
			if (name.equalsIgnoreCase(pk))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug()
	{
		return debug;
	}

	/**
	 * @param debug
	 *            the debug to set
	 */
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	private class JdbcWriterContext {
		private final int SQL_TYPE_INSERT = 0;
		private final int SQL_TYPE_UPDATE = 1;
		private final int SQL_TYPE_DELETE = 2;
		private final int maxType = 3;

		private final Connection con;
		private final PreparedStatement[] stms = new PreparedStatement[maxType];
		private Map<String, Integer>[] columnTypes;
		private int[] txCounts = new int[maxType];

		JdbcWriterContext(Connection con)
		{
			this.con = con;
			columnTypes = (Map<String, Integer>[]) Array.newInstance(Map.class, maxType);
			for (int i = 0; i < maxType; ++i)
			{
				columnTypes[i] = new HashMap<String, Integer>(maxType);
			}
		}

		void flush()
				throws SQLException
		{
			for (int i = 0; i < txCounts.length; ++i)
			{
				if (txCounts[i] > 0)
				{
					stms[i].executeBatch();
				}
			}
		}

		void close()
		{
			for (PreparedStatement stm : stms)
			{
				if (null != stm)
				{
					JdbcUtils.close(stm);
				}
			}
		}

		int getSqlTypeIndex(DataRecord record)
		{
			if (record.markedDelete())
			{
				return SQL_TYPE_DELETE;
			}
			else if (record.markedUpdate())
			{
				return SQL_TYPE_UPDATE;
			}
			else
			{
				return SQL_TYPE_INSERT;
			}
		}

		void writeDataRecord(DataRecord record)
				throws BatchIOException, SQLException
		{
			int sqlType = getSqlTypeIndex(record);
			PreparedStatement stm = acquireStatement(record, sqlType);

			switch (sqlType) {
			case SQL_TYPE_DELETE:
				mapParamsDelete(record, stm, columnTypes[sqlType]);
				break;
			case SQL_TYPE_UPDATE:
				mapParamsUpdate(record, stm, columnTypes[sqlType]);
				break;
			default:
				mapParamsInsert(record, stm, columnTypes[sqlType]);
			}

			stm.addBatch();
			increaseTxCount(sqlType);

			if (getTxCount(sqlType) >= bufferSize)
			{
				stm.executeBatch();
				resetTxCount(sqlType);
			}

		}

		private PreparedStatement acquireStatement(DataRecord record, int sqlType)
				throws SQLException, BatchIOException
		{

			if (null == stms[sqlType])
			{
				String sql = generateSqlSelect(record.getFieldNames());
				ResultSet rs = con.createStatement().executeQuery(sql);

				retrieveColumnTypes(rs, columnTypes[sqlType]);

				sql = generateSqlModify(record, sqlType);
				// System.out.println(sql);
				stms[sqlType] = con.prepareStatement(sql);

				resetTxCount(sqlType);

				removeOtherStatement(sqlType);

				return stms[sqlType];
			}

			return stms[sqlType];
		}

		private String generateSqlModify(DataRecord record, int sqlType)
				throws BatchIOException
		{

			switch (sqlType) {
			case SQL_TYPE_DELETE:
				return generateSqlDelete(record.getFieldNames(), columnTypes[sqlType]);
			case SQL_TYPE_UPDATE:
				return generateSqlUpdate(record.getFieldNames(), columnTypes[sqlType]);
			default:
				return generateSqlInsert(record.getFieldNames(), columnTypes[sqlType]);
			}
		}

		private int getTxCount(int sqlType)
		{
			return txCounts[sqlType];
		}

		private void increaseTxCount(int sqlType)
		{
			++txCounts[sqlType];
		}

		private void removeOtherStatement(int sqlType)
		{
			// Do nothing
		}

		private void resetTxCount(int sqlType)
		{
			txCounts[sqlType] = 0;
		}
	}

}