package com.jb.coupons_project.custom_exceptions;

public class DBCompanyOperationException extends DBOperationException
{
	private static final long serialVersionUID = 1L;
	// company identifier
	private String identifier;
	
	/**
	 * Constructor with no arguments.
	 */
	public DBCompanyOperationException() 
	{
		super();
		this.identifier = "N/A";
	}
	
	/**
	 * Constructor with error message.
	 * @param error message
	 */
	public DBCompanyOperationException(String message) 
	{
		super(message);
		this.identifier = "";
	}
	
	/**
	 * Constructor with error message and company identifier.
	 * @param error message
	 * @param company identifier
	 */
	public DBCompanyOperationException(String message, String identifier) 
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
	 * company identifier getter
	 * @return company identifier
	 */
	public String getIdentifier() 
	{
		return this.identifier;
	}
}
