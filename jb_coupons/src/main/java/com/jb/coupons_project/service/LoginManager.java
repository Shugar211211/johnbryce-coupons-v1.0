package com.jb.coupons_project.service;

import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.utils.DataValidator;

public class LoginManager 
{
	// The only instance of login manager
	private static LoginManager instance = null;
	
	ClientFacade clientFacade = null;
	
	// Operation status message - used for client feedback
	private String clientMsg = "";
		
	// Create validation object for user input data
	DataValidator dataValidator;
	
	/**
	 * Private constructor
	 */
	private LoginManager()
	{
		super();
		dataValidator = new DataValidator();
	}
	
	/**
	 * This method returns client message.
	 * @return client message
	 */
	public String getClientMsg()
	{
		return this.clientMsg;
	}
	
	/**
	 * This method used to obtain reference to the only instance of login manager.
	 * @return instance of login manager.
	 */
	public static LoginManager getInstance()
	{
		if(instance==null)
			instance = new LoginManager();
		return instance;
	}
	
	/**
	 * This method is used to login client into system. It checks client type
	 * and creates appropriate client facade. Then it calls login method of client facade.
	 * @param email - client email
	 * @param password - client password
	 * @param clientType - client type
	 * @return client facade object corresponding to client type if login data is valid, 
	 * or null if login data is invalid.
	 * @throws DBOperationException in case of database operation error
	 */
	public ClientFacade login(String email, String password, ClientType clientType) throws DBOperationException
	{
		if(dataValidator.validateEmail(email) == null || dataValidator.validatePassword(password) == null)
		{
			this.clientMsg = dataValidator.getClientMsg();
			return null;
		}
		
		switch (clientType) {
		case ADMINISTRATOR:
			clientFacade = new AdminFacade(); break;
		case COMPANY:
			clientFacade = new CompanyFacade(); break;
		case CUSTOMER:
			clientFacade = new CustomerFacade(); break;
		default:
			System.out.println("Wrong client type"); return null;
		}
		
		if(clientFacade.login(email, password))
		{
			this.clientMsg = email + ": logged in as " + clientType.getClientType();
			return clientFacade;
		}
		else
		{
			this.clientMsg = "Wrong email or password";
			return null;
		}
	}
	
	/**
	 * This method is used to logout client and destroy client facade.
	 * @param clientFacade - client facade object to log out.
	 */
	public void logout(ClientFacade clientFacade)
	{
		clientFacade = null;
		this.clientMsg = "Logged out.";
	}
}
