/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtils {

	public static void close(ResultSet rs)
	{
		if (null != rs)
		{
			try
			{
				rs.close();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace(System.out);
			}
		}
	}

	public static void close(Statement stm)
	{
		if (null != stm)
		{
			try
			{
				stm.close();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace(System.out);
			}
		}
	}

	public static void close(Connection con)
	{
		if (null != con)
		{
			try
			{
				if (!con.isClosed())
				{
					con.close();
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace(System.out);
			}
		}
	}

	public static Exception commit(Connection con)
	{
		try
		{
			con.commit();

			return null;
		}
		catch (Exception ex)
		{
			return ex;
		}
	}

	public static Exception rollback(Connection con)
	{
		try
		{
			con.rollback();

			return null;
		}
		catch (Exception ex)
		{
			return ex;
		}
	}

}