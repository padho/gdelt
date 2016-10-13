package com.teslagov.gdelt;

/**
 * @author Kevin Chen
 */
public class GDELTException extends RuntimeException
{
	public GDELTException( String message )
	{
		super( message );
	}

	public GDELTException( String message, Throwable cause )
	{
		super( message, cause );
	}
}
