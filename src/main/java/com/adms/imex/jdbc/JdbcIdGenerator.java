/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import com.adms.shared.IdGenerator;

public class JdbcIdGenerator implements IdGenerator {

	private DataSource dataSource;
	private String sql;

	public String getNextId()
	{
		Connection con = null;
		ResultSet rs = null;
		try
		{
			con = dataSource.getConnection();
			rs = con.createStatement().executeQuery(sql);

			rs.next();

			return rs.getObject(1).toString();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
		finally
		{
			JdbcUtils.close(rs);
			JdbcUtils.close(con);
		}
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
	 * @return the sql
	 */
	public String getSql()
	{
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

}
