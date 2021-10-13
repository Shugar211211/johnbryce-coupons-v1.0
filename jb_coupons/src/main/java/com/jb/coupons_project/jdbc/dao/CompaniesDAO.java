package com.jb.coupons_project.jdbc.dao;

import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBCompanyOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Company;

public interface CompaniesDAO 
{
	/**
	 * Method checks if company with such email and password exists in database.
	 * @param company email, company password
	 * @return true if company exists or false otherwise
	 * @throws DBOperationException in case of database error while performing check.
	 */
	boolean isCompanyExists(String email, String password) throws DBOperationException;
	
	/**
	 * Method adds new company to database.
	 * @param company object that represents company to add.
	 * @throws DBCompanyOperationException in case of database error while adding new company.
	 */
	void addCompany(Company company) throws DBOperationException;
	
	/**
	 * Method updates company entry in database.
	 * @param company object that represents updated company.
	 * @throws DBOperationException in case of invalid fields of company object 
	 * or database error while updating company. 
	 */
	void updateCompany(Company company) throws DBOperationException;
	
	/**
	 * Method deletes company entry in database.
	 * @param companyID - id of company to delete.
	 * @throws DBOperationException in case of database error while deleting company.
	 */
	void deleteCompany(int companyID) throws DBOperationException;
	
	/**
	 * Method retrieves list of all companies from database.
	 * @return ArrayList of company objects.
	 * @throws DBOperationException in case of database error while retrieving companies; 
	 */
	ArrayList<Company> getAllCompanies() throws DBOperationException;
	
	/**
	 * Method retrieves company by companyID.
	 * @param companyID - id to search for.
	 * @return company object.
	 * @throws DBOperationException in case of database error while retrieving company.
	 */
	Company getOneCompany(int companyID) throws DBOperationException;
	
	/**
	 * Method retrieves company by its name.
	 * @param name - company name to search for.
	 * @return Company object.
	 * @throws DBOperationException in case of database error while retrieving company.
	 */
	Company getCompanyByName(String name) throws DBOperationException;
	
	/**
	 * Method retrieves company by its email.
	 * @param email - company email to search for.
	 * @return Company object.
	 * @throws DBOperationException in case of database error while retrieving company.
	 */
	Company getCompanyByEmail(String email) throws DBOperationException; 
}
