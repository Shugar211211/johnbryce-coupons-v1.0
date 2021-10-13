package com.jb.coupons_project.java_beans;

import java.sql.Date;

public class Coupon
{
	private int id;
	private int companyID;
	private Category category;
	private String title;
	private String decription;
	private Date startDate;
	private Date endDate;
	private int amount;
	private double price;
	private String image;
	
	/**
	 * Constructor for creating new coupon while adding new company. 
	 * This constructor doesn't require coupon id and company id, 
	 * as those will be provided by database.
	 * @param category
	 * @param title
	 * @param decription
	 * @param startDate
	 * @param endDate
	 * @param amount
	 * @param price
	 * @param image
	 */
	public Coupon(Category category, 
			  	String title, 
			  	String decription, 
			  	Date startDate, 
			  	Date endDate,
			  	int amount, 
			  	double price, 
			  	String image) {
	super();
	this.id = -1;
	this.companyID = -1;
	this.category = category;
	this.title = title;
	this.decription = decription;
	this.startDate = startDate;
	this.endDate = endDate;
	this.amount = amount;
	this.price = price;
	this.image = image;
}
	/**
	 * Constructor for creating new coupon for company already registered in database.
	 * This constructor doesn't require coupon id, as it will be provided by database.
	 * @param companyID
	 * @param category
	 * @param title
	 * @param decription
	 * @param startDate
	 * @param endDate
	 * @param amount
	 * @param price
	 * @param image
	 */
	public Coupon(int companyID, 
				  Category category, 
				  String title, 
				  String decription, 
				  Date startDate, 
				  Date endDate,
				  int amount, 
				  double price, 
				  String image) {
		super();
		this.id = -1;
		this.companyID = companyID;
		this.category = category;
		this.title = title;
		this.decription = decription;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.price = price;
		this.image = image;
	}
	
	/**
	 * Constructor for creating coupon by retrieving it from database.
	 * This constructor requires all attributes of coupon.
	 * This constructor initializes all attributes of coupon.
	 * @param id
	 * @param companyID
	 * @param category
	 * @param title
	 * @param decription
	 * @param startDate
	 * @param endDate
	 * @param amount
	 * @param price
	 * @param image
	 */
	public Coupon(int id,
				  int companyID, 
				  Category category, 
				  String title, 
				  String decription, 
				  Date startDate, 
				  Date endDate,
				  int amount, 
				  double price, 
				  String image) {
	super();
	this.id = id;
	this.companyID = companyID;
	this.category = category;
	this.title = title;
	this.decription = decription;
	this.startDate = startDate;
	this.endDate = endDate;
	this.amount = amount;
	this.price = price;
	this.image = image;
}

	/**
	 * coupon id getter
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * coupon id setter
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * company id getter
	 * @return company id
	 */
	public int getCompanyID() {
		return companyID;
	}

	/**
	 * company id setter
	 * @param companyID
	 */
	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public Category getCategory() {
		return category;
	}

	/**
	 * category id setter
	 * @param categoryID
	 */
	public void setCategoryID(Category categoryID) {
		this.category = categoryID;
	}

	/**
	 * category id getter
	 * @return category id
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * title setter
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * title getter
	 * @return title
	 */
	public String getDecription() {
		return decription;
	}

	/**
	 * coupon description setter
	 * @param decription
	 */
	public void setDecription(String decription) {
		this.decription = decription;
	}

	/**
	 * coupon description getter
	 * @return coupon description
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * start date setter
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * start date getter
	 * @return start date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * end date setter
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * amount getter
	 * @return amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * amount setter
	 * @param amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * price getter
	 * @return price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * price setter
	 * @param price
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * image getter
	 * @return image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * image setter
	 * @param image
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * compares coupon to other coupon by id
	 */
	@Override
	public boolean equals(Object arg0) {
		return (this.id==((Coupon)arg0).getId());
	}

	/**
	 * standard hashCode method
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * returns coupon data as string
	 * @return coupon data as string
	 */
	@Override
	public String toString() {
		return "Title: "+this.title+
			   ", Description: "+this.decription+
			   ", Category: "+this.category.getCategoryDescription()+
			   ", Coupon ID: "+ ""+this.id+
			   ", Company ID: "+this.companyID+
			   ", From: "+this.startDate+
			   ", Expires at: "+this.endDate+
			   ", Quantity: "+this.amount+
			   ", Price: "+this.price;
			//+", Image: "+this.image;
	}
}
