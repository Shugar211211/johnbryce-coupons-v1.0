package com.jb.coupons_project.utils;

import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jb.coupons_project.java_beans.Company;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.java_beans.Customer;

public class DataValidator 
{
	private static final String PWD_REGEX = "[^A-Za-z0-9]";
	private static final String EMAIL_REGEX = "[^A-Za-z0-9@._-]";
	private static final String NAME_REGEX = "[^A-Za-z0-9()%\\s-]";
	private static final String TITLE_REGEX = "[^A-Za-z0-9().,;:!?%$/\\s-]";
	private static final String URL_REGEX = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
//	private static final String URL_REGEX = "";
	
	// operation status message - used for client feedback
	private String clientMsg;
	
	/**
	 * Constructor.
	 */
	public DataValidator()
	{
		super();
		this.clientMsg = "Done.";
	}
	
	/**
	 * This method returns client message.
	 */
	public String getClientMsg()
	{
		return this.clientMsg;
	}
	
	/**
	 * This method checks if password contains invalid characters and sets clientMsg accordingly.
	 * @param password to validate.
	 * @return password if password is validated, or null if password is not validated.
	 */
	public String validatePassword(String password)
	{
		if(password.equals("") || password==null)
		{
			this.clientMsg = "Password is required to log in.";
			return null;
		}

		Pattern pwdPattern = Pattern.compile(PWD_REGEX);
		Matcher pwdMatcher = pwdPattern.matcher(password);
		if(pwdMatcher.find())
		{
			this.clientMsg = "Password contains invalid characters. "
					+ "Only upper/lower case letters and numbers are alowed in password field.";
			return null;
		}
		
		return password;
	}
	
	/**
	 * This method checks if email contains invalid characters and sets clientMsg accordingly.
	 * @param email to validate.
	 * @return email if email is validated, or null if email is not validated.
	 */
	public String validateEmail(String email)
	{
		if(email.equals("") || email==null)
		{
			this.clientMsg = "Email is required to log in.";
			return null;
		}
		
		Pattern emPattern = Pattern.compile(EMAIL_REGEX);
		Matcher emMatcher = emPattern.matcher(email);
		if(emMatcher.find())
		{
			this.clientMsg = "Email is invalid. Valid email is required to log in.";
			return null;
		}
		
		return email;
	}
	
	/**
	 * This method checks if name contains invalid characters and sets clientMsg accordingly.
	 * It is used to validate first and last names.
	 * @param name to validate.
	 * @return name if name is validated, or null if name is not validated.
	 */
	public String validateName(String name)
	{
		if(name.equals("") || name==null)
		{
			this.clientMsg = "Can not complete operation: Name field is empty.";
			return null;
		}
		
		Pattern emPattern = Pattern.compile(NAME_REGEX);
		Matcher emMatcher = emPattern.matcher(name);
		if(emMatcher.find())
		{
			this.clientMsg = "Can not complete operation: Name contains invalid characters.";
			return null;
		}
		
		return name;
	}
	
	/**
	 * This method checks if string contains invalid characters and sets clientMsg accordingly.
	 * It is used to validate coupons titles and descriptions.
	 * @param string to validate.
	 * @return string if string is validated, or null if string is not validated.
	 */
	private String validateString(String str)
	{
		if(str.equals("") || str==null)
		{
			this.clientMsg = "is empty.";
			return null;
		}
		
		Pattern emPattern = Pattern.compile(TITLE_REGEX);
		Matcher emMatcher = emPattern.matcher(str);
		if(emMatcher.find())
		{
			this.clientMsg = " contains invalid characters.";
			return null;
		}
		
		return str;
	}
	
	/**
	 * This method checks if id is invalid and sets clientMsg accordingly.
	 * It is used to validate coupon id, client id and company id.
	 * This method does not check if id is real and belongs to actual entity in database. 
	 * @param id to validate.
	 * @return true if id is valid or false if id is not valid.
	 */
	public boolean validateID(int id)
	{
//		if (id < 0 || id > Integer.MAX_VALUE)
//		{
//			this.clientMsg = "ID is not valid.";
//			return false;
//		}
		if(id != (int)id)
		{
			this.clientMsg = "Can not complete operation: ID is not valid.";
			return false;
		}
		return true;
	}
	
	/**
	 * This method checks if all fields of company object are valid 
	 * and sets clientMsg accordingly.
	 * @param company object to validate.
	 * @return company object if it was validated, or null if company object was not validated.
	 */
	public Company validateCompany(Company company)
	{
		if(company == null)
		{
			this.clientMsg = "Can not complete operation: company was not found";
			return null;
		}
		if( ! validateID(company.getId()) )
		{
			this.clientMsg = "Can not complete operation: company ID is not valid.";
			return null;
		}
		if(validateName(company.getName()) == null)
		{
//			this.clientMsg = "Company name contains invalid characters.";
			return null;
		}
		if(validateEmail(company.getEmail()) == null)
		{
//			this.clientMsg = "Can not complete operation: email contains invalid characters.";
			return null;
		}
		if(validatePassword(company.getPassword()) == null)
		{
//			this.clientMsg = "Password contains invalid characters.";
			return null;
		}
		return company;
	}
	
	/**
	 * This method checks if all fields of customer object are valid 
	 * and sets clientMsg accordingly.
	 * @param customer object to validate.
	 * @return customer object if it was validated, or null if customer object was not validated.
	 */
	public Customer validateCustomer(Customer customer) 
	{
		if(customer == null)
		{
			this.clientMsg = "Can not complete operation: customer was not found";
			return null;
		}
		if( ! validateID(customer.getId()) )
		{
//			this.clientMsg = "Can not complete operation: ID is not valid.";
			return null;
		}
		if(validateName(customer.getFirstName()) == null)
		{
//			this.clientMsg = "Customer first name contains invalid characters.";
			return null;
		}
		if(validateName(customer.getLastName()) == null)
		{
//			this.clientMsg = "Customer last name contains invalid characters.";
			return null;
		}
		if(validateEmail(customer.getEmail()) == null)
		{
//			this.clientMsg = "Customer email contains invalid characters.";
			return null;
		}
		if(validatePassword(customer.getPassword()) == null)
		{
//			this.clientMsg = "Customer password contains invalid characters.";
			return null;
		}
		return customer;
	}
	
	/**
	 * This method checks if all fields of coupon object are valid 
	 * and sets clientMsg accordingly.
	 * @param coupon object to validate.
	 * @return coupon object if it was validated, or null if coupon object was not validated.
	 */
	public Coupon validateCoupon(Coupon coupon)
	{
		Date currentDate = new Date(System.currentTimeMillis());
		
		if(coupon == null)
		{
			this.clientMsg = "Cannot complete operation: coupon was not found";
			return null;
		}
		if( ! validateID(coupon.getId()) )
		{
			this.clientMsg = "Can not complete operation: coupon ID is not valid."; 
			return null;
		}
		
		if( ! validateID(coupon.getCompanyID()) )
		{
			this.clientMsg = "Can not complete operation: company ID is not valid.";
			return null;
		}
		
		if(validateString(coupon.getCategory().getCategoryDescription()) == null)
		{
			this.clientMsg = "Can not complete operation: coupon category description" + this.clientMsg;
			return null;
		}
		
		if(validateString(coupon.getTitle()) == null)
		{
			this.clientMsg = "Can not complete operation: coupon title " + this.clientMsg;
			return null;
		}
		
		if(validateString(coupon.getDecription()) == null)
		{
			this.clientMsg = "Can not complete operation: coupon description " + this.clientMsg;
			return null;
		}
		
		if(coupon.getStartDate() == null)
		{
			this.clientMsg = "Can not complete operation: "
					+ "coupon does not have valid start date.";
			return null;
		}
		
		if(coupon.getEndDate() == null)
		{
			this.clientMsg = "Can not complete operation: coupon does not have valid end date.";
			return null;
		}
		
		if(currentDate.compareTo(coupon.getEndDate()) > 0)
		{
			this.clientMsg = "Can not complete operation: coupon end date is expired.";
			return null;
		}
		
		if(coupon.getStartDate().compareTo(coupon.getEndDate()) > 0)
		{
			this.clientMsg = "Can not complete operation: "
					+ "coupon can not have end date before start date.";
			return null;
		}
		
//		if(coupon.getAmount() == null)
//		{
//			this.clientMsg = "Coupon amount field can not be empty.";
//			return null;
//		}
		
		if(coupon.getAmount() != (int)(coupon.getAmount()) || coupon.getAmount() < 0)
		{
			this.clientMsg = "Can not complete operation: coupon amount value is invalid.";
			return null;
		}
		
//		if(coupon.getPrice() == null)
//		{
//			this.clientMsg = "Coupon price field can not be empty.";
//			return null;
//		}
		
		if(coupon.getPrice() != (double)(coupon.getPrice()) || coupon.getPrice() < 0)
		{
			this.clientMsg = "Can not complete operation: coupon price is invalid.";
			return null;
		}
		
		if(coupon.getImage() == null || coupon.getImage().equals(""))
		{
//			this.clientMsg = "Coupon doesn't have an image link.";
			coupon.setImage("N/A");
		}
		else
		{
			Pattern urlPattern = Pattern.compile(URL_REGEX);
			Matcher urlMatcher = urlPattern.matcher(coupon.getImage());
			if( ! urlMatcher.find())
			{
				this.clientMsg = "Can not complete operation: coupon image link is invalid.";
				return null;
			}
		}
		return coupon;
	}
}
