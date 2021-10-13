package com.jb.coupons_project.test.console_menu;

import java.util.ArrayList;
import java.util.Scanner;

import com.jb.coupons_project.custom_exceptions.DBCompanyOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.custom_exceptions.InvalidInputException;
import com.jb.coupons_project.java_beans.Category;
import com.jb.coupons_project.java_beans.Company;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.java_beans.Customer;
import com.jb.coupons_project.service.AdminFacade;
import com.jb.coupons_project.service.ClientFacade;
import com.jb.coupons_project.service.ClientType;
import com.jb.coupons_project.service.CustomerFacade;
import com.jb.coupons_project.service.LoginManager;

public class CustomerMenu 
{
	private Scanner scanner;
	
	public CustomerMenu(ClientFacade clientFacade) throws InvalidInputException
	{
		scanner = new Scanner(System.in);
		this.customerMenu(clientFacade);
	}
	
	/**
	 * This method gets from user choice and call corresponding method. 
	 * @param customer email
	 * @param customer password
	 * @throws InvalidInputException in case of invalid input.
	 */
	private void customerMenu(ClientFacade clientFacade) throws InvalidInputException
	{
		CustomerFacade customerFacade = (CustomerFacade) clientFacade;
		String userChoice = "";
		while( ! userChoice.equals("11") )
		{
			System.out.println("------------------------------------------------------------");
			System.out.println("\nCustomer menu:");
			System.out.println("\n1: Show customer details");
			System.out.println("2: Show all coupons");
			System.out.println("3: Filter coupons by category ");
			System.out.println("4: Filter coupons by maximum price ");
			System.out.println("5: Buy coupon");
			System.out.println("6: Delete coupon purchase");
			System.out.println("\n11: Log out\n");
			System.out.println("------------------------------------------------------------");
			
			userChoice = scanner.nextLine();
			
			try
			{
				if(userChoice.equals("1")) {printCustomerDetails(customerFacade);}
				if(userChoice.equals("2")) {printCustomerCoupons(customerFacade);}
				if(userChoice.equals("3")) {printCustomerCouponsByCategory(customerFacade);}
				if(userChoice.equals("4")) {printCustomerCouponsByMaxPrice(customerFacade);}
				if(userChoice.equals("5")) {buyCoupon(customerFacade);}
				if(userChoice.equals("6")) {deletePurchase(customerFacade);}
			}
			catch(DBOperationException e)
			{
				System.out.println(e.getMessage());
			}
			
		}
		LoginManager loginManager = LoginManager.getInstance();
		loginManager.logout(customerFacade);
	}
	
	/**
	 * Delete coupon purchase menu.
	 * @param customerFacade object.
	 * @throws DBOperationException in case of database operation.
	 */
	private void deletePurchase(CustomerFacade customerFacade) throws DBOperationException
	{
		System.out.println("To delete coupon purchase enter coupon ID");
		String idStr = scanner.nextLine();
		int id;
		try
		{
			id=Integer.parseInt(idStr);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid id");
			return;
		}
		
		customerFacade.deletePurchase(id);
		System.out.println(customerFacade.getClientMsg());
	}
	
	/**
	 * Buy coupon menu.
	 * @param customerFacade object.
	 * @throws DBOperationException in case of database operation.
	 */
	private void buyCoupon(CustomerFacade customerFacade) throws DBOperationException
	{
		System.out.println("------------------------------------------------------------");
		System.out.println("Awailable coupons:");
		LoginManager lm = LoginManager.getInstance();
		AdminFacade adminFacade = (AdminFacade)lm.login("admin@admin.com", "admin", ClientType.ADMINISTRATOR);
		ArrayList<Company> companies;
		try 
		{
			companies = adminFacade.getAllCompanies();
		} 
		catch (DBCompanyOperationException e) 
		{
			System.out.println(e.getMessage());
			return;
		}
		for(Company currCompany : companies)
			for(Coupon currCoupon : currCompany.getCoupons())
				System.out.println(currCoupon);
		System.out.println("------------------------------------------------------------");
		System.out.println("Enter coupon ID to purchase coupon");
		String idStr = scanner.nextLine();
		int id;
		try
		{
			id=Integer.parseInt(idStr);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid id");
			return;
		}
		
		Coupon coupon = null;
		for(Company currCompany : companies)
			for(Coupon currCoupon : currCompany.getCoupons())
				if(id == currCoupon.getId())
				{
					coupon = currCoupon;
					break;
				}
		if(coupon != null)
			customerFacade.purchaseCoupon(coupon);
		System.out.println(customerFacade.getClientMsg());
	}
	
	/**
	 * This method read from customer maximum price 
	 * and prints all customer coupons under this price.
	 * @param customerFacade object.
	 * @throws DBOperationException in case of database operation.
	 */
	private void printCustomerCouponsByMaxPrice(CustomerFacade customerFacade) 
						throws DBOperationException
	{
		ArrayList<Coupon> coupons;
		System.out.println("------------------------------------------------------------");
		System.out.println("Enter maximum price to filter coupons:");
		String maxPriceStr = scanner.nextLine();
		double maxPrice;
		try
		{
			maxPrice = Double.parseDouble(maxPriceStr);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid input");
			return;
		}
		coupons = customerFacade.getCustomerCoupons(maxPrice);
		System.out.println("------------------------------------------------------------");
		System.out.println(customerFacade.getClientMsg());
		if(coupons == null)
			return;
		for(Coupon currCoupon : coupons)
			System.out.println(currCoupon);
	}
	
	/**
	 * This method read from customer category 
	 * and prints all customer coupons in this category.
	 * @param customerFacade object.
	 * @throws DBOperationException in case of database operation.
	 */
	private void printCustomerCouponsByCategory(CustomerFacade customerFacade) 
						throws DBOperationException
	{
		Category category = this.getCategory();
		ArrayList<Coupon> coupons = customerFacade.getCustomerCoupons(category);
		System.out.println(customerFacade.getClientMsg());
		if(coupons == null)
			return;
		for(Coupon currCoupon : coupons)
			System.out.println(currCoupon);
		System.out.println("------------------------------------------------------------");
	}
	
	/**
	 * This method prints all coupons that belong to this customer.
	 * @param customerFacade object.
	 * @throws DBOperationException in case of database operation.
	 */
	private void printCustomerCoupons(CustomerFacade customerFacade) 
						throws DBOperationException
	{
		ArrayList<Coupon> coupons = customerFacade.getCustomerCoupons();
		System.out.println(customerFacade.getClientMsg());
		if(coupons == null)
			return;
		for(Coupon currCoupon : coupons)
			System.out.println(currCoupon);
	}
	
	/**
	 * This method prints customer details.
	 * @param customerFacade object.
	 * @throws DBOperationException in case of database operation.
	 */
	private void printCustomerDetails(CustomerFacade customerFacade) 
						throws DBOperationException
	{
		Customer customer = customerFacade.getCustomerDetails();
		System.out.println(customerFacade.getClientMsg());
		System.out.println(customer);
	}
	
	/**
	 * This method reads from user category choice.
	 * @return category.
	 */
	private Category getCategory()
	{
		System.out.println("------------------------------------------------------------");
		System.out.println("Choose category:");
		int i = 1;
		for(Category category : Category.values())
		{
			System.out.println(i + ": "+category.getCategoryDescription());
			i++;
		}
		System.out.println("------------------------------------------------------------");
		String userChoiceStr = scanner.nextLine();
		int userChoice;
		try
		{
			userChoice = Integer.parseInt(userChoiceStr);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid input");
			return null;
		}
		
		if(userChoice<1 || userChoice>25)
		{
			System.out.println("Invalid category code");
			return null;
		}
		Category category;
		switch (userChoice) 
		{
			case 1: category = Category.ARTS; break;
			case 2: category = Category.AUTOMOTIVE; break;
			case 3: category = Category.BABY; break;
			case 4: category = Category.BEAUTY; break;
			case 5: category = Category.BOOKS; break;
			case 6: category = Category.COMPUTERS; break;
			case 7: category = Category.CLOTHING;break;
			case 8: category = Category.ELECTRONICS; break;
			case 9: category = Category.FASHION; break;
			case 10: category = Category.FINANCE; break;
			case 11: category = Category.FOOD; break;
			case 12: category = Category.HEALTH; break;
			case 13: category = Category.HOME; break;
			case 14: category = Category.LIFESTYLE; break;
			case 15: category = Category.MOVIES; break;
			case 16: category = Category.MUSIC; break;
			case 17: category = Category.OUTDOORS; break;
			case 18: category = Category.PETS; break;
			case 19: category = Category.RESTAURANTS; break;
			case 20: category = Category.SHOES; break;
			case 21: category = Category.SOFTWARE; break;
			case 22: category = Category.SPORTS; break;
			case 23: category = Category.TOOLS; break;
			case 24: category = Category.TRAVEL; break;
			case 25: category = Category.VIDEOGAMES; break;
			default: category = null;System.out.println("No such category"); break;
		}
		return category;
	}
}
