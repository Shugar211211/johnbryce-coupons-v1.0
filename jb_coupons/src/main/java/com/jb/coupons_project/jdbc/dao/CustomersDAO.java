package com.jb.coupons_project.jdbc.dao;

import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Customer;

public interface CustomersDAO 
{
	/**
	 * Method checks if customer with provided arguments email and password exists in database.
	 * @param customer email, customer password
	 * @return true if customer exists or false otherwise.
	 * @throws DBOperationException in case of database operating while searching for customer.
	 */
	boolean isCustomerExists(String email, String password) throws DBOperationException;
	
	/**
	 * Method adds new company to database.
	 * @param company object that represents company to add.
	 * @throws DBOperationException in case of database error while adding new company.
	 */
	void addCustomer(Customer customer) throws DBOperationException;
	
	/**
	 * Method updates customer entry in database.
	 * @param customer object that represents updated customer.
	 * @throws DBOperationException in case of database error or invalid fields in customer object.
	 */
	void updateCustomer(Customer customer) throws DBOperationException;
	
	/**
	 * Method deletes customer entry from database.
	 * @param customerID - id of customer to delete.
	 * @throws DBOperationException in case of database error while deleting customer. 
	 */
	void deleteCustomer(int customerID) throws DBOperationException;
	
	/**
	 * Method finds all customers from database.
	 * @return array list of customer objects that represents customers.
	 * @throws DBOperation exception in case of database error while looking for customers.
	 */
	ArrayList<Customer> getAllCustomers() throws DBOperationException;
	
	/**
	 * Method retrieves customer by customer id.
	 * @param int - customer id to search for.
	 * @return Customer object.
	 * @throws DBOperationException in case of database error while searching for customer.
	 */
	Customer getOneCustomer(int customerID) throws DBOperationException;
	
	/**
	 * Method retrieves customer by email.
	 * @param email - customer email to search for.
	 * @return Customer object.
	 * @throws DBOperationException in case of database error while searching for customer.
	 */
	Customer getOneCustomer(String email) throws DBOperationException;
}
