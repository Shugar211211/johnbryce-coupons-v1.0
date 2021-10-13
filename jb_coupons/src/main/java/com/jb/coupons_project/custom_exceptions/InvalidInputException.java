package com.jb.coupons_project.custom_exceptions;

public class InvalidInputException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor with no arguments.
	 */
	public InvalidInputException() 
	{
		super("Invalid input");
	}

	/**
	 * Constructor with message argument
	 * @param error message
	 */
	public InvalidInputException(String message) 
	{
		super(message);
	}

	/**
	 * getter for error message
	 * @return error message
	 */
	public String getMessage() 
	{
		return super.getMessage();
	}
}
