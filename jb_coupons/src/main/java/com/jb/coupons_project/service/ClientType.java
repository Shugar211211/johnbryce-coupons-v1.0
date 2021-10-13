package com.jb.coupons_project.service;

public enum ClientType 
{
	ADMINISTRATOR ("administrator"),
	COMPANY ("company"), 
	CUSTOMER("customer");
	
	/**
	 * Constructor.
	 * @param clientType
	 */
	private ClientType(String clientType) 
	{
		this.clientType = clientType;
	}

	private String clientType;
	
	/**
	 * clientType getter
	 * @return clientType
	 */
	public String getClientType()
	{
		return clientType;
	}
}
