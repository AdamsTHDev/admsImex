/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.adms.imex.BaseReader;
import com.adms.imex.BatchIOException;
import com.adms.imex.DataRecord;
import com.adms.imex.IOUtils;

public class JdbcReader extends BaseReader {

	private Connection connection;
	private DataSource dataSource;
	private String sql;
	private String sqlFile;
	private String sqlCharset;

	public Iterable<DataRecord> read(Object... params)
			throws BatchIOException
	{
		Connection con = null;
		ResultSet rs = null;
		try
		{
			con = acquireConnection();
			rs = acquireResultSet(con, params);

			RSDataRecordIterable iterable = new RSDataRecordIterable(rs);

			iterable.setTypeMap(getTypeMap());
			iterable.setCloseConnection(true);

			return iterable;
		}
		catch (Exception ex)
		{
			disposeResultSet(rs);
			if (null != con)
			{
				try
				{
					if (!con.isClosed())
					{
						con.close();
					}
				}
				catch (Exception ignore)
				{
					// Do nothing
				}
			}
			throw new BatchIOException(ex);
		}
	}

	protected ResultSet acquireResultSet(Connection con, Object... params)
			throws SQLException
	{

		PreparedStatement stm = con.prepareStatement(getSql());

		int idx = 1;
		for (Object param : params)
		{
			stm.setObject(idx++, param);
		}

		return stm.executeQuery();
	}

	protected Connection acquireConnection()
			throws SQLException
	{
		if (null != connection && !connection.isClosed())
		{

			return connection;
		}
		else if (null != getDataSource())
		{
			Connection aCon = getDataSource().getConnection();

			return aCon;
		}
		else
		{
			throw new SQLException("Neither connection nor dataSource are provided");
		}
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection()
	{
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public void setConnection(Connection connection)
	{
		this.connection = connection;
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
	 * @return the sqlFile
	 */
	public String getSqlFile()
	{
		return sqlFile;
	}

	/**
	 * @param sqlFile
	 *            the sqlFile to set
	 */
	public void setSqlFile(String sqlFile)
	{
		this.sqlFile = sqlFile;
	}

	/**
	 * @return the sql
	 */
	public String getSql()
	{
		if (null == sql && null != sqlFile)
		{
			try
			{
				sql = IOUtils.readFileContent(sqlFile, sqlCharset);
			}
			catch (IOException ex)
			{
				throw new RuntimeException("Unable to reader file - " + sqlFile, ex);
			}
		}
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(String sql)
	{
		this.sql = sql;
	}

	/**
	 * @return the sqlCharset
	 */
	public String getSqlCharset()
	{
		return sqlCharset;
	}

	/**
	 * @param sqlCharset
	 *            the sqlCharset to set
	 */
	public void setSqlCharset(String sqlCharset)
	{
		this.sqlCharset = sqlCharset;
	}

}