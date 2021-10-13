package com.jb.coupons_project.custom_exceptions;

public class DBCouponOperationException extends DBOperationException
{
	private static final long serialVersionUID = 1L;
	// coupon identifier
	private String identifier;
	
	/**
	 * Constructor with no arguments.
	 */
	public DBCouponOperationException() 
	{
		super();
		this.identifier = "N/A";
	}
	
	/**
	 * Constructor with error message.
	 * @param error message
	 */
	public DBCouponOperationException(String message) 
	{
		super(message);
		this.identifier = "";
	}
	
	/**
	 * Constructor with error message and coupon identifier.
	 * @param error message
	 * @param object identifier
	 */
	public DBCouponOperationException(String message, String identifier) 
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
	 * coupon identifier getter
	 * @return coupon identifier
	 */
	public String getIdentifier() 
	{
		return this.identifier;
	}
}
