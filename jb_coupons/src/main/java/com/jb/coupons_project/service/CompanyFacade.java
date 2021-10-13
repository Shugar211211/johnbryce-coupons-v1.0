package com.jb.coupons_project.service;

import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBCompanyOperationException;
import com.jb.coupons_project.custom_exceptions.DBCouponOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Category;
import com.jb.coupons_project.java_beans.Company;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.utils.DataValidator;

public class CompanyFacade extends ClientFacade 
{
	private int companyID;
	
	// operation status message - used for client feedback
	private String clientMsg;
	
	// Create validation object for user input data
		DataValidator dataValidator;
	
	/**
	 * Constructor.
	 * @throws DBOperationException in case of database operation.
	 */
	public CompanyFacade() throws DBOperationException 
	{
		super();
		this.dataValidator = new DataValidator();
		this.clientMsg="";
	}
	
	/**
	 * This method returns client message.
	 * @return client message
	 */
	public String getClientMsg()
	{
		return this.clientMsg;
	}
	
	@Override
	/**
	 * Method checks login credentials.
	 * @param email, password.
	 * @return true if credentials match company in database or false otherwise.
	 * @throws DBOperationException in case of database error.
	 */
	public boolean login(String email, String password) throws DBOperationException 
	{
		if(super.companiesDAO.isCompanyExists(email, password))
		{
			Company company = companiesDAO.getCompanyByEmail(email);
			companyID = company.getId();
			this.clientMsg = company.getName() + ": logged in as company.";
			return true;
		}
		else
		{
			this.clientMsg = "user " + email + " not found.";
			return false;
		}
	}
	
	/**
	 * Method adds new coupon to database.
	 * @param coupon object that represents coupon to add.
	 * @throws DBCouponOperationException in case of database error while adding coupon.
	 */
	public void addCoupon(Coupon coupon) throws DBOperationException
	{
		if(dataValidator.validateCoupon(coupon) == null)
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		
		if(couponsDAO.getOneCoupon(coupon.getId()) != null)
		{
			this.clientMsg = "Cannot add coupon. Coupon with this id already registered";
			return;
		}
		
		ArrayList<Coupon> coupons = couponsDAO.getCouponsByCompany(companyID); 
		for(Coupon currCoupon : coupons)
			if(currCoupon.getTitle().equals(coupon.getTitle()))
			{
				this.clientMsg = "Cannot add coupon. Coupon with this title already "
						+ "registered for this company.";
				return;
			}
		
		couponsDAO.addCoupon(coupon);
		this.clientMsg = "Coupon added successfully.";
	}
	
	/**
	 * Method updates coupon entry in database.
	 * @param coupon object that represents new coupon to replace old one by id.
	 * @throws DBCouponOperationException in case of database error while updating coupon.
	 */
	public void updateCoupon(Coupon coupon) throws DBOperationException
	{
		if(dataValidator.validateCoupon(coupon) == null)
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		
		Coupon oldCoupon = couponsDAO.getOneCoupon(coupon.getId());
		if(oldCoupon == null)
		{
			this.clientMsg = "Cannot update coupon. Coupon with this id was not found in database";
			return;
		}
		
		if(coupon.getId() != oldCoupon.getId() || coupon.getCompanyID() != oldCoupon.getCompanyID())
		{
			this.clientMsg = "Cannot update coupon. Coupon id and company id can not be changed.";
			return;
		}
		
		couponsDAO.updateCoupon(coupon);
		this.clientMsg = "Coupon updated successfully.";
	}
	
	/**
	 * Method deletes coupon entry from database.
	 * @param id of coupon to delete.
	 * @throws DBCouponOperationException in case of database error while deleting coupon.
	 */
	public void deleteCoupon(int couponID) throws DBOperationException
	{
		if( ! dataValidator.validateID(couponID) )
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		if(couponsDAO.getOneCoupon(couponID) == null)
		{
			this.clientMsg = "Cannot delete coupon. Coupon with this id was not found in database";
			return;
		}
		
		couponsDAO.deleteCoupon(couponID);
		this.clientMsg = "Coupon deteted.";
	}
	
	/**
	 * Method retrieves all coupons of this company.
	 * @return ArrayList of coupon objects or null if company not found.
	 * @throws DBCompanyOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCompanyCoupons() throws DBOperationException
	{
		if(companiesDAO.getOneCompany(companyID) == null)
		{
			this.clientMsg = "Coupons cannot be retrieved. This company was not found in database.";
			return null;
		}
		ArrayList<Coupon> companyCoupons = couponsDAO.getCouponsByCompany(companyID);
		if(companyCoupons.size() == 0)
			this.clientMsg = "Company has no coupons.";
		else
			this.clientMsg = companyCoupons.size() + " coupons were retrieved";
		return companyCoupons;
	}
	
	/**
	 * Method retrieves all coupons of this company by category.
	 * @param category to filter by.
	 * @return ArrayList of coupon objects or null if company not found.
	 * @throws DBCompanyOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCompanyCoupons(Category category) throws DBOperationException
	{
		if(category == null || 
			(category.compareTo(Category.ARTS) < 1 || category.compareTo(Category.VIDEOGAMES) > 1))
		{
			this.clientMsg = "Coupons cannot be retrieved, no such category.";
			return null;
		}
		if(companiesDAO.getOneCompany(companyID) == null)
		{
			this.clientMsg = "Coupons cannot be retrieved "
					+ "because this company was not found in database.";
			return null;
		}
		ArrayList<Coupon> companyCoupons = couponsDAO.getCouponsByCompany(companyID, category);
		if(companyCoupons.size() == 0)
			this.clientMsg = "Company has no coupons in this category.";
		else
			this.clientMsg = companyCoupons.size() + " coupons were retrieved";
		return companyCoupons;
	}
	
	/**
	 * Method retrieves all coupons of this company up to maximum price.
	 * @param maximum price to filter by.
	 * @return ArrayList of coupon objects or null if company not found.
	 * @throws DBCompanyOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCompanyCoupons(double maxPrice) throws DBOperationException
	{
		if(companiesDAO.getOneCompany(companyID) == null)
		{
			this.clientMsg = "Coupons cannot be retrieved. This company was not found in database.";
			return null;
		}
		ArrayList<Coupon> companyCoupons = couponsDAO.getCouponsByCompany(companyID, maxPrice);
		if(companyCoupons.size() == 0)
			this.clientMsg = "Company has no coupons under this price.";
		else
			this.clientMsg = companyCoupons.size() + " coupons were retrieved";
		return companyCoupons;
	}
	
	/**
	 * Method retrieves company details by its id.
	 * @return Company object if found or null otherwise.
	 * @throws DBCompanyOperationException in case of database error while retrieving company.
	 */
	public Company getCompanyDetails() throws DBOperationException
	{
		Company company = this.companiesDAO.getOneCompany(companyID);
		if(company == null)
			this.clientMsg = "Company was not found";
		else
			this.clientMsg = "Company retrieved";
		return company;
	}
}
