package com.jb.coupons_project.custom_exceptions;

public class DBOperationException extends Exception 
{
	private static final long serialVersionUID = 1L;
	private String identifier;
	
	/**
	 * Constructor with no arguments.
	 */
	public DBOperationException() 
	{
		super();
		this.identifier = "Database error.";
	}
	
	/**
	 * Constructor with error message.
	 * @param error message
	 */
	public DBOperationException(String message) 
	{
		super(message);
		this.identifier = "";
	}
	
	/**
	 * Constructor with error message and object identifier.
	 * @param error message
	 * @param object identifier
	 */
	public DBOperationException(String message, String identifier) 
	{
		super(message);
		this.identifier=identifier;
	}

	/**
	 * error message getter
	 * @return error message
	 */
	public String getMessage() 
	{
		return super.getMessage();
	}

	/**
	 * identifier getter
	 * @return identifier
	 */
	public String getIdentifier() 
	{
		return this.identifier;
	}
}
