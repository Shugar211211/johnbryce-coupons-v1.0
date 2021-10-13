package com.jb.coupons_project.java_beans;

import java.util.ArrayList;

public class Customer
{
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private ArrayList<Coupon> couponsOfCustomer;
	
	/**
	 * Constructor that creates new customer. This constructor does not set customer id.
	 * Customer id is to be set later by database.
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 * @param couponsOfCustomer
	 */
	public Customer(String firstName, 
					String lastName, 
					String email, 
					String password, 
					ArrayList<Coupon> couponsOfCustomer) {
		super();
		this.id = -1;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.couponsOfCustomer = new ArrayList<Coupon>(couponsOfCustomer);
	}
	
	/**
	 * Constructor that initializes all attributes of customer object.
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 * @param couponsOfCustomer
	 */
	public Customer(int id,
			String firstName, 
			String lastName, 
			String email, 
			String password, 
			ArrayList<Coupon> couponsOfCustomer) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.couponsOfCustomer = new ArrayList<Coupon>(couponsOfCustomer);
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
	 * first name getter
	 * @return first name
	 */
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * last name getter
	 * @return last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * last name setter
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	 * customer coupons getter
	 * @return coupons
	 */
	public ArrayList<Coupon> getCoupons() {
		return couponsOfCustomer;
	}

	/**
	 * coupons setter
	 * @param couponsOfCustomer
	 */
	public void setCoupons(ArrayList<Coupon> couponsOfCustomer) {
		this.couponsOfCustomer = couponsOfCustomer;
	}

	/**
	 * compares this customer to another customer by customer id 
	 */
	@Override
	public boolean equals(Object obj) {
		return (this.id==((Customer)obj).getId());
	}

	/**
	 * standard hashCode method
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * returns customer data as string
	 * @return customer as string
	 */
	@Override
	public String toString() {
		return "ID: "+this.id
				+", First name: "+this.firstName
				+", Last name: "+this.lastName
				+", Email: "+this.email
				+", Password: "+this.password;//+" "+this.couponsOfCustomer;
	}
}
