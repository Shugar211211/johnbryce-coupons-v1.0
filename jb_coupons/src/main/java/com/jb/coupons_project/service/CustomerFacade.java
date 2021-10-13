package com.jb.coupons_project.service;

import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBCouponOperationException;
import com.jb.coupons_project.custom_exceptions.DBCustomerOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Category;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.java_beans.Customer;
import com.jb.coupons_project.utils.DataValidator;

public class CustomerFacade extends ClientFacade 
{	
	private int customerID;
	
	// operation status message - used for client feedback
	private String clientMsg;
	
	// Create validation object for user input data
	DataValidator dataValidator;
	
	/**
	 * Constructor.
	 * @throws DBOperationException in case of database operation.
	 */
	public CustomerFacade() throws DBOperationException 
	{
		super();
		this.dataValidator = new DataValidator();
		this.clientMsg="";
	}
	
	/**
	 * Method returns client message.
	 * @return client message.
	 */
	public String getClientMsg()
	{
		return this.clientMsg;
	}

	/**
	 * Method checks login credentials.
	 * @param email, password.
	 * @return true if customer with this email and password found in database, 
	 * or false otherwise.
	 * @throws DBOperationException in case of database operation error 
	 * while searching for customer.
	 */
	@Override
	public boolean login(String email, String password) throws DBOperationException 
	{
		if(customersDAO.isCustomerExists(email, password))
		{
			Customer customer = customersDAO.getOneCustomer(email);
			customerID = customer.getId();
			this.clientMsg = customer.getFirstName() + " " + customer.getLastName()
						+ ": logged in as customer.";
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Method performs coupon purchase.
	 * @param coupon object that represents coupon to be purchased.
	 * @throws DBCouponOperationException in case of database operation error 
	 * while performing coupon purchase.
	 */
	public void purchaseCoupon(Coupon coupon) throws DBOperationException
	{
		if(dataValidator.validateCoupon(coupon) == null)
		{
			this.clientMsg = dataValidator.getClientMsg();
			return;
		}
		
		if(coupon.getAmount()<1)
		{
			this.clientMsg = "Cannot make purchase. This coupon is not available.";
			return;
		}
		
		if(couponsDAO.getOneCoupon(coupon.getId()) == null)
		{
			this.clientMsg = "Cannot make purchase. This coupon was not found.";
			return;
		}
		
		if(couponsDAO.isPurchased(customerID, coupon.getId()))
		{
			this.clientMsg = "Cannot make purchase. "
					+ "This coupon is already purchsed by this customer.";
			return;
		}
		
		super.couponsDAO.addCouponPurchase(customerID, coupon.getId());
		this.clientMsg = "Coupon purchased.";
	}
	
	/**
	 * Method deletes coupon from database.
	 * @param id of coupon to delete.
	 * @throws DBOperationException in case of database operation error while deleting coupon.
	 */
	public void deletePurchase(int couponID) throws DBOperationException
	{
		if( ! dataValidator.validateID(couponID) )
		{
			this.clientMsg = "Coupon ID is not valid";
			return;
		}
		if( ! couponsDAO.isPurchased(couponID, couponID))
		{
			this.clientMsg = "This customer does not own this coupon.";
			return;
		}	
		couponsDAO.deleteCouponPurchase(couponID, couponID);
		this.clientMsg = "Coupon purchase deleted.";
	}
	
	/**
	 * Method finds all coupons of this customer.
	 * @return ArrayList of coupon objects or that represent coupons, 
	 * or null if customer not found.
	 * @throws DBCustomerOperationException in case of database error while retrieving coupons. 
	 */
	public ArrayList<Coupon> getCustomerCoupons() throws DBOperationException
	{
		if(customersDAO.getOneCustomer(customerID) == null)
		{
			this.clientMsg = "Coupons cannot be retrieved. "
					+ "This customer was not found in database.";
			return null;
		}
		ArrayList<Coupon> customerCoupons = couponsDAO.getCouponsByCustomer(customerID);
		if(customerCoupons.size() == 0)
			this.clientMsg = "Customer has no coupons.";
		else
			this.clientMsg = customerCoupons.size() + " coupons were retrieved";
		return customerCoupons;
	}
	
	/**
	 * Method finds all coupons of this customer filtered by category.
	 * @param Category title
	 * @return ArrayList of coupon objects or that represent coupons, 
	 * or null if customer not found.
	 * @throws DBCustomerOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCustomerCoupons(Category category) throws DBOperationException
	{
		if(category == null || 
				(category.compareTo(Category.ARTS) < 1 || category.compareTo(Category.VIDEOGAMES) > 1))
			{
				this.clientMsg = "Coupons cannot be retrieved, no such category.";
				return null;
			}
		if(customersDAO.getOneCustomer(customerID) == null)
		{
			this.clientMsg = "Coupons cannot be retrieved. "
					+ "This customer was not found in database.";
			return null;
		}
		ArrayList<Coupon> customerCoupons = couponsDAO.getCouponsByCustomer(customerID, category);
		if(customerCoupons.size() == 0)
			this.clientMsg = "Customer has no coupons in this category.";
		else
			this.clientMsg = customerCoupons.size() + " coupons were retrieved";
		return customerCoupons;
	}
	
	/**
	 * Method finds all coupons of this customer filtered by max price.
	 * @param maxPrice - upper price bound
	 * @return ArrayList of coupon objects or that represent coupons, 
	 * or null if customer not found.
	 * @throws DBCustomerOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCustomerCoupons(double maxPrice) throws DBOperationException
	{
		if(customersDAO.getOneCustomer(customerID) == null)
		{
			this.clientMsg = "Coupons cannot be retrieved. "
					+ "This customer was not found in database.";
			return null;
		}
		ArrayList<Coupon> customerCoupons = couponsDAO.getCouponsByCustomer(customerID, maxPrice);
		if(customerCoupons.size() == 0)
			this.clientMsg = "Customer has no coupons under this price.";
		else
			this.clientMsg = customerCoupons.size() + " coupons were retrieved";
		return customerCoupons;
	}
	
	/**
	 * Method retrieves customer details by customer id.
	 * @return Customer object if found or null if not found.
	 * @throws DBCustomerOperationException in case of database error while retrieving coupons.
	 */
	public Customer getCustomerDetails() throws DBOperationException
	{
		Customer customer = this.customersDAO.getOneCustomer(customerID);
		if(customer == null)
			this.clientMsg = "Customer was not found";
		else
			this.clientMsg = "Customer details:";
		return customer;
	}
}
