/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex;

public class BatchIOException extends Exception {

	private static final long serialVersionUID = 5717344866150807242L;

	public BatchIOException(Throwable cause)
	{
		super(cause);
	}

	public BatchIOException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public BatchIOException(String message)
	{
		super(message);
	}

	public BatchIOException()
	{
	}

}
