package com.jb.coupons_project.jdbc.dao;

import java.sql.Date;
import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Category;
import com.jb.coupons_project.java_beans.Coupon;

public interface CouponsDAO 
{
	/**
	 * Method adds new coupon to database.
	 * @param Coupon object that represents new coupon to add.
	 * @throws DBOperationException in case of database error while adding coupon.
	 */
	void addCoupon(Coupon coupon) throws DBOperationException;
	
	/**
	 * Method updates coupon in database
	 * @param Coupon object that represents updated coupon.
	 * @throws DBOperationException in case of database error while updating coupon.
	 */
	void updateCoupon(Coupon coupon) throws DBOperationException;
	
	/**
	 * Method deletes coupon from database
	 * @param couponID - integer that represents id of coupon to delete
	 * @throws DBOperationException in case of database error while deleting coupon.
	 */
	void deleteCoupon(int CouponID) throws DBOperationException;
	
	/**
	 * Method retrieves all coupons from database.
	 * @return array list of Coupon objects that represent coupons .
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	ArrayList<Coupon>getAllCoupons() throws DBOperationException;
	
	/**
	 * Method retrieves all coupons of company by company id
	 * @param int - company id 
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	ArrayList<Coupon> getCouponsByCompany(int companyID) throws DBOperationException;
	
	/**
	 * Method retrieves all coupons of company in certain category
	 * @param int - company id, Category - a category to filter by
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	ArrayList<Coupon> getCouponsByCompany(int companyID, Category category) throws DBOperationException;
	
	/**
	 * Method retrieves all coupons of company which are lower than certain price.
	 * @param int - company id, double upper price bound.
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	ArrayList<Coupon> getCouponsByCompany(int companyID, double maxPrice) throws DBOperationException;
	
	/**
	 * Method retrieves all coupons of customer whose id provided.
	 * @param int - customer id.
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	ArrayList<Coupon> getCouponsByCustomer(int customerID) throws DBOperationException;
	
	/**
	 * Method retrieves all coupons in given category, 
	 * which belong to customer whose id provided.
	 * @param int - customer id, Category - category to filter by.
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while  retrieving coupons.
	 */
	ArrayList<Coupon> getCouponsByCustomer(int customerID, Category category) throws DBOperationException;
	
	/**
	 * Method retrieves all coupons which are below maximum price 
	 * and belong to certain customer.
	 * @param int - customer id, double - upper price bound.
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	ArrayList<Coupon> getCouponsByCustomer(int customerID, double maxPrice) throws DBOperationException;
	
	/**
	 * Method retrieves one coupons from database by coupon id 
	 * @param coupon id to search for
	 * @return Coupon object if found, or null if such object was not found in database.
	 * @throws DBOperationException in case of database error while retrieving coupon.
	 */
	Coupon getOneCoupon(int couponID) throws DBOperationException;
	
	/**
	 * Method checks if coupon is purchased by customer.
	 * @param int - customer id, int - coupon id
	 * @return true is this coupon is purchased by this customer, or false otherwise.
	 * @throws DBOperationException in case of database error.
	 */
	boolean isPurchased(int customerID, int couponID) throws DBOperationException; 
	
	/**
	 * Method performs coupon purchase.
	 * @param customer id for whom to make purchase, coupon id to purchase.
	 * @throws DBOperationException in case of database error.
	 */
	void addCouponPurchase(int customerID, int couponID) throws DBOperationException;
	
	/**
	 * Method deletes coupon purchase (does not delete coupon itself).
	 * @param customer id for whom to delete purchase, coupon id - coupon to delete purchase to.
	 * @throws DBOperationException in case of database error.
	 */
	void deleteCouponPurchase(int customerID, int couponID) throws DBOperationException;
	
	/**
	 * Method deletes coupons older than provided date.
	 * @param date.
	 * @return number of coupons deleted. 
	 * @throws DBOperationException in case of database error while deleting coupons.
	 */
	int deleteCouponsOlderThan(Date currentDate) throws DBOperationException;
}
