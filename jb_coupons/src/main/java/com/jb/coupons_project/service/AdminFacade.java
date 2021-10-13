package com.jb.coupons_project.service;

import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBCompanyOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Company;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.java_beans.Customer;
import com.jb.coupons_project.utils.DataValidator;

public class AdminFacade extends ClientFacade 
{
	// admin credentials
	private final String ADMIN_EMAIL="admin@admin.com";
	private final String ADMIN_PASSWORD="admin";
	
	// Operation status message - used for client feedback
	private String clientMsg;
	
	// Create validation object for user input data
	DataValidator dataValidator;
	
	/**
	 * Constructor. Initialized dataValidator object.
	 * @throws DBOperationException in case of database operation error.
	 */
	public AdminFacade() throws DBOperationException 
	{
		super();
		dataValidator = new DataValidator();
		clientMsg="";
	}
	
	/**
	 * Method returns client message.
	 * @return client message
	 */
	public String getClientMsg()
	{
		return this.clientMsg;
	}

	/**
	 * Checks login credentials.
	 * @param email - administrator email
	 * @param password - administrator password
	 * @return true if credentials approved or false otherwise.
	 * @throws DBOperationException in case of database error while checking credentials.
	 */
	@Override
	public boolean login(String email, String password) throws DBOperationException
	{
		if(ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password))
		{
			this.clientMsg = this.ADMIN_EMAIL + ": logged in as admin.";
			return true;
		}
		else
		{
			this.clientMsg = "user " + email + " not found.";
			return false;
		}
	}
	
	/**
	 * Method adds new company to database.
	 * @param company - company object to add.
	 * @throws DBCompanyOperationException in case company of database error while adding company.
	 */
	public void addCompany(Company company) throws DBOperationException
	{
		if(dataValidator.validateCompany(company) == null)
		{
			this.clientMsg = "Company was not added. " + dataValidator.getClientMsg();
			return;
		}
		if(companiesDAO.getCompanyByName(company.getName()) != null)
		{
			this.clientMsg = "Company was not added "
					+ "because company with this name already exists";
			return;
		}
		if(companiesDAO.getCompanyByEmail(company.getEmail()) != null)
		{
			this.clientMsg = "Company was not added "
					+ "because company with this email already exists";
			return;
		}
		companiesDAO.addCompany(company);
		this.clientMsg = "Company added successfully";
		
		// if company has coupons
		if(company.getCoupons() != null && company.getCoupons().size()>0)
		{
			// login as this company
			LoginManager loginManager = LoginManager.getInstance();
			ClientFacade clientFacade = loginManager.login(company.getEmail(), 
														   company.getPassword(), 
														   ClientType.COMPANY);
//			CompanyFacade companyFacade = (CompanyFacade) clientFacade;
			int companyID = companiesDAO.getCompanyByEmail(company.getEmail()).getId();
			ArrayList<Coupon> coupons = company.getCoupons();
			for(Coupon currCoupon : coupons)
			{
				currCoupon.setCompanyID(companyID);
				couponsDAO.addCoupon(currCoupon);
			}
			loginManager.logout(clientFacade);
		}
	}
	
	/**
	 * Updates company in database.
	 * @param company - company object that represents updated company
	 * @throws DBCompanyOperationException in case of database error while updating company. 
	 */
	public void updateCompany(Company company) throws DBOperationException
	{
		if(dataValidator.validateCompany(company) == null)
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		if(companiesDAO.getOneCompany(company.getId()) == null)
		{
			this.clientMsg = "Company cannot be updated "
					+ "because company with this id was not found in database";
			return;
		}
		if( ! companiesDAO.getOneCompany(company.getId()).getName().equals(company.getName()) )
		{
			this.clientMsg = "Company cannot be updated because its name was changed.";
			return;
		}
		companiesDAO.updateCompany(company);
		this.clientMsg = "Company updated successfully.";
	}
	
	/**
	 * Deletes company from database.
	 * @param companyID - id of company to delete.
	 * @throws DBCompanyOperationException in case of database error while deleting company.
	 */
	public void deleteCompany(int companyID) throws DBOperationException
	{
		if( ! dataValidator.validateID(companyID) )
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		if(companiesDAO.getOneCompany(companyID) == null)
		{
			this.clientMsg = "Company cannot be deleted "
					+ "because this company was not found in database";
			return;
		}
		companiesDAO.deleteCompany(companyID);
		this.clientMsg = "Company deleted successfully.";
	}
	
	/**
	 * Retrieves all companies from database.
	 * @return ArrayList of Company objects that represent companies.
	 * @throws DBCompanyOperationException in case of database error while retrieving companies.
	 */
	public ArrayList<Company> getAllCompanies() throws DBOperationException
	{
		ArrayList<Company> companies = this.companiesDAO.getAllCompanies();
		
		// the code should never reach here
		if(companies == null)
			this.clientMsg = "Unknown error.";
		else if(companies.size()==0)
			this.clientMsg = "No companies registered yet.";
		else 
			this.clientMsg = companies.size() + " companies were retrieved";
		return companies;
	}
	
	/**
	 * Retrieves company by company id.
	 * @param companyID - company id to search for
	 * @return company object if found or null if company not found.
	 * @throws DBCompanyOperationException in case of database error while searching for company.
	 */
	public Company getOneCompany(int companyID) throws DBOperationException
	{
		if( ! dataValidator.validateID(companyID) )
		{
			this.clientMsg = dataValidator.getClientMsg();
			return null;
		}
		Company company = this.companiesDAO.getOneCompany(companyID);
		if(company == null)
			this.clientMsg = "Company was not found";
		else
			this.clientMsg = "Company retrieved";
		return company;
	}
	
	/**
	 * Adds new customer to database.
	 * @param customer - customer object to add that represents customer.
	 * @throws DBOperationException in case of database error while adding customer.
	 */
	public void addCustomer(Customer customer) throws DBOperationException
	{
		if(dataValidator.validateCustomer(customer) == null)
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		if(customersDAO.getOneCustomer(customer.getEmail()) != null)
		{
			this.clientMsg = "Customer was not added "
					+ "because customer with this email already exists.";
			return;
		}
		customersDAO.addCustomer(customer);
		this.clientMsg = "Customer added successfully.";
	}
	
	/**
	 * Updates customer in database.
	 * @param customer - Customer object that represents updated customer.
	 * @throws DBOperationException in case of database error while updating customer.  
	 */
	public void updateCustomer(Customer customer) throws DBOperationException
	{
		if(dataValidator.validateCustomer(customer) == null)
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		
		if(customersDAO.getOneCustomer(customer.getId()) == null)
		{
			this.clientMsg = "Customer was not updated. Customer with this id was not found.";
			return;
		}
		
		// if customer email is being changed (check for new email to be unique)
		if( ! customersDAO.getOneCustomer(customer.getId()).getEmail().equals(customer.getEmail()))
		{
			// if new email is not unique
			if(customersDAO.getOneCustomer(customer.getEmail()) != null)
			{
				this.clientMsg = "Customer was not updated. "
						+ "Email address may not be updated to this email "
						+ "because it is already used by another customer.";
				return;
			}
		}
		
		customersDAO.updateCustomer(customer);
		this.clientMsg = "Customer updated successfully";
	}
	
	/**
	 * Deletes customer from database.
	 * @param customerID to find and delete customer with this id.
	 * @throws DBOperationException in case of database error while deleting customer.
	 */
	public void deleteCustomer(int customerID) throws DBOperationException
	{
		if( ! dataValidator.validateID(customerID) )
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		if(customersDAO.getOneCustomer(customerID) == null)
		{
			this.clientMsg = "Customer cannot be deleted "
					+ "because this customer was not found in database";
			return;
		}
		customersDAO.deleteCustomer(customerID);
		this.clientMsg = "Customer deleted successfully.";
	}
	
	/**
	 * Retrieves all customers from database.
	 * @return list of all customers.
	 * @throws DBOperationException in case of database error while retrieving customers.
	 */
	public ArrayList<Customer> getAllCustomers() throws DBOperationException
	{
		ArrayList<Customer> customers = this.customersDAO.getAllCustomers();
		
		// the code should never reach here
		if(customers == null)
			this.clientMsg = "Unknown error.";
		else if(customers.size()==0)
			this.clientMsg = "No customers registered in database.";
		else 
			this.clientMsg = customers.size() + " customers were retrieved";
		return customers;
	}
	
	/**
	 * Finds one customer by customer id.
	 * @param customerID to search for.
	 * @return customer object if found or null if not found.
	 * @throws DBOperationException in case of database error while looking for customer.
	 */
	public Customer getOneCustomer(int customerID) throws DBOperationException
	{
		if( ! dataValidator.validateID(customerID) )
		{
			this.clientMsg = dataValidator.getClientMsg();
			return null;
		}
		Customer customer = this.customersDAO.getOneCustomer(customerID);
		if(customer == null)
			this.clientMsg = "Customer was not found";
		else
			this.clientMsg = "Customer found";
		return customer;
	}
}
