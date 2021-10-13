package com.jb.coupons_project.custom_exceptions;

public class DBCustomerOperationException extends DBOperationException
{
	private static final long serialVersionUID = 1L;
	// customer identifier
	private String identifier;
	
	/**
	 * Constructor with no arguments.
	 */
	public DBCustomerOperationException() 
	{
		super();
		this.identifier = "N/A";
	}
	
	/**
	 * Constructor with error message.
	 * @param error message
	 */
	public DBCustomerOperationException(String message) 
	{
		super(message);
		this.identifier = "";
	}
	
	/**
	 * Constructor with error message and customer identifier.
	 * @param error message
	 * @param customer identifier
	 */
	public DBCustomerOperationException(String message, String identifier) 
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
	 * customer identifier getter
	 * @return customer identifier
	 */
	public String getIdentifier() 
	{
		return this.identifier;
	}
}
