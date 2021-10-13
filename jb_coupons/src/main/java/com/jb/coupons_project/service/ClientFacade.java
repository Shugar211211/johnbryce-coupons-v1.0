package com.jb.coupons_project.service;

import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.jdbc.dao.CompaniesDAO;
import com.jb.coupons_project.jdbc.dao.CompaniesDBDAO;
import com.jb.coupons_project.jdbc.dao.CouponsDAO;
import com.jb.coupons_project.jdbc.dao.CouponsDBDAO;
import com.jb.coupons_project.jdbc.dao.CustomersDAO;
import com.jb.coupons_project.jdbc.dao.CustomersDBDAO;

public abstract class ClientFacade 
{
	protected CompaniesDAO companiesDAO;
	protected CustomersDAO customersDAO;
	protected CouponsDAO couponsDAO;
	
	/**
	 * Checks login credentials.
	 * @param email - administrator email
	 * @param password - administrator password
	 * @return true if credentials approved or false otherwise.
	 * @throws DBOperationException in case of database error while checking credentials.
	 */
	public abstract boolean login(String email, String password) throws DBOperationException;

	/**
	 * Constructor.
	 * @throws DBOperationException
	 */
	public ClientFacade() throws DBOperationException 
	{
		super();
		companiesDAO = new CompaniesDBDAO();
		couponsDAO = new CouponsDBDAO();
		customersDAO = new CustomersDBDAO();
	}
	
}
