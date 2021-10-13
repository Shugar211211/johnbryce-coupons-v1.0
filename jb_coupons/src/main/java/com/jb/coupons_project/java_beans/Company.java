package com.jb.coupons_project.java_beans;

import java.util.ArrayList;

public class Company
{
	private int id;
	private String name;
	private String email;
	private String password;
	ArrayList<Coupon> couponsOfCompany;
	
//	public Company()
//	{
//		super();
//		this.id = -1;
//		this.name = "";
//		this.email = "";
//		this.password = "";
//		this.couponsOfCompany = new ArrayList<Coupon>(couponsOfCompany);
//	}
	
	/**
	 * Constructor for creating company object. 
	 * Id this constructor companyID is set to temporary default value, 
	 * to be changed later by database.
	 * @param name
	 * @param email
	 * @param password
	 * @param couponsOfCompany
	 */
	public Company(String name, 
				   String email, 
				   String password, 
				   ArrayList<Coupon> couponsOfCompany) {
		super();
		this.id = -1;
		this.name = name;
		this.email = email;
		this.password = password;
		this.couponsOfCompany = new ArrayList<Coupon>(couponsOfCompany);
	}
	
	/**
	 * Constructor for creating company object
	 * @param id
	 * @param name
	 * @param email
	 * @param password
	 * @param couponsOfCompany
	 */
	public Company(int id, 
			   		String name, 
			   		String email, 
			   		String password, 
			   		ArrayList<Coupon> couponsOfCompany) {
	super();
	this.id = id;
	this.name = name;
	this.email = email;
	this.password = password;
	this.couponsOfCompany = new ArrayList<Coupon>(couponsOfCompany);
}
	
	/**
	 * id getter
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * id setter
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * name getter
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * name setter
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * email getter
	 * @return email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * email setter
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * password getter
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * password setter
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * coupons getter
	 * @return coupons
	 */
	public ArrayList<Coupon> getCoupons() {
		return couponsOfCompany;
	}

	/**
	 * coupons setter
	 * @param couponsOfCompany
	 */
	public void setCoupons(ArrayList<Coupon> couponsOfCompany) {
		this.couponsOfCompany = couponsOfCompany;
	}

	/**
	 * This method compares company to other company object by id
	 */
	@Override
	public boolean equals(Object obj) {
		return (this.id==((Company)obj).getId());
	}

	/**
	 * Standard hashCode method
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * returns company is string.
	 * @return company data 
	 */
	@Override
	public String toString() {
		return "Id: "+this.id+
				", Name: "+this.name+
				", Email: "+this.email+
				", Password: "+this.password;/*+
				" Coupons: "+couponsOfCompany.toString();*/
	}
}
