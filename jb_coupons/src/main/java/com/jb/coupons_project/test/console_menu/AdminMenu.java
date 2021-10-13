package com.jb.coupons_project.test.console_menu;

import java.util.ArrayList;
import java.util.Scanner;

import com.jb.coupons_project.custom_exceptions.DBCompanyOperationException;
import com.jb.coupons_project.custom_exceptions.DBCustomerOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Company;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.java_beans.Customer;
import com.jb.coupons_project.service.AdminFacade;
import com.jb.coupons_project.service.ClientFacade;
import com.jb.coupons_project.service.LoginManager;

public class AdminMenu 
{
	private Scanner scanner;
	
	public AdminMenu(ClientFacade clientFacade)
	{
		scanner = new Scanner(System.in);
		this.adminMenu(clientFacade);
	}
	
	/**
	 * This method gets from user choice and call corresponding method.  
	 * @param admin email
	 * @param admin password
	 */
	private void adminMenu(ClientFacade clientFacade)
	{
//		scanner = new Scanner(System.in);
		AdminFacade adminFacade = (AdminFacade) clientFacade;
		String userChoice = "";
		while( ! userChoice.equals("11") )
		{
			System.out.println("------------------------------------------------------------");
			System.out.println("\nAdmin menu:");
			System.out.println("\n1: Show all companies");
			System.out.println("2: See company\'s coupons");
			System.out.println("3: Add company");
			System.out.println("4: Update company");
			System.out.println("5: Delete company");
			System.out.println("\n6: Show all customers");
			System.out.println("7: See customer\'s coupons");
			System.out.println("8: Add customer");
			System.out.println("9: Update customer");
			System.out.println("10: Delete customer");
			System.out.println("\n11: Log out\n");
			System.out.println("------------------------------------------------------------");
			
			userChoice = scanner.nextLine();
			try
			{
				if(userChoice.equals("1")) {printAllCompanies(adminFacade);}
				if(userChoice.equals("2")) {printCompanyCoupons(adminFacade);}
				if(userChoice.equals("3")) {addCompany(adminFacade);}
				if(userChoice.equals("4")) {updateCompany(adminFacade);}
				if(userChoice.equals("5")) {deleteCompany(adminFacade);}
				if(userChoice.equals("6")) {printAllCustomers(adminFacade);}
				if(userChoice.equals("7")) {printCustomerCoupons(adminFacade);}
				if(userChoice.equals("8")) {addCustomer(adminFacade);}
				if(userChoice.equals("9")) {updateCustomer(adminFacade);}
				if(userChoice.equals("10")) {deleteCustomer(adminFacade);}
			}
			catch(DBOperationException e)
			{
//				e.printStackTrace(); // used for debugging
				System.out.println(e.getMessage());
			}
		}
		LoginManager loginManager = LoginManager.getInstance();
		loginManager.logout(adminFacade);
	}
	
	/**
	 * Delete customer menu.
	 * @param adminFacade
	 * @throws DBOperationException in case of database operation error.
	 */
	private void deleteCustomer(AdminFacade adminFacade) throws DBOperationException
	{
		System.out.println("Enter ID of customer to delete: ");
		String idStr = scanner.nextLine();
		int id;
		try
		{
			id = Integer.parseInt(idStr);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid input");
			return;
		}
		Customer customer= adminFacade.getOneCustomer(id);
		if(customer == null)
		{
			System.out.println("Customer with this ID was not found");
			return;
		}
		System.out.println(customer);
		System.out.println("DELETE THIS CUSTOMER? Y/N");
		String ans = scanner.nextLine();
		if(ans.equalsIgnoreCase("Y"))
		{
			adminFacade.deleteCustomer(id);
			System.out.println(adminFacade.getClientMsg());
		}
		else
			return;
	}
	
	/**
	 * Update customer menu.
	 * @param adminFacade
	 * @throws DBOperationException in case of database operation error.
	 */
	private void updateCustomer(AdminFacade adminFacade) throws DBOperationException
	{
		System.out.println("Enter ID of customer to update: ");
		String idStr = scanner.nextLine();
		int id;
		try
		{
			id = Integer.parseInt(idStr);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid input");
			return;
		}
		Customer customer = adminFacade.getOneCustomer(id);
		if(customer == null)
		{
			System.out.println("Customer with this ID was not found");
			return;
		}
		System.out.println(customer);
		System.out.println("Enter new first name: ");
		String firstName = scanner.nextLine();
		System.out.println("Enter new last name: ");
		String lastName = scanner.nextLine();
		System.out.println("Enter new email: ");
		String email = scanner.nextLine();
		System.out.println("Enter new password: ");
		String password = scanner.nextLine();
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setEmail(email);
		customer.setPassword(password);
		adminFacade.updateCustomer(customer);
		System.out.println(adminFacade.getClientMsg());
	}
	
	/**
	 * Print all coupons of a customer menu.
	 * @param adminFacade
	 * @throws DBCustomerOperationException in case of database operation error.
	 */
	private void printCustomerCoupons(AdminFacade adminFacade) throws DBOperationException
	{
		System.out.println("Enter customer ID to see his coupons: ");
		String idStr = scanner.nextLine();
		int id = Integer.parseInt(idStr);
		Customer customer= adminFacade.getOneCustomer(id);
		if(customer == null)
		{
			System.out.println(adminFacade.getClientMsg());
			return;
		}
		ArrayList<Coupon> coupons = customer.getCoupons();
		System.out.println(coupons.size() + " coupons found ");
		for(Coupon currCoupon : coupons)
		{
			System.out.println("  id: "+currCoupon.getId()
							+", category: "+currCoupon.getCategory().getCategoryDescription()
							+", title: "+currCoupon.getTitle()
							+", description: "+currCoupon.getDecription()
							+", start date: "+currCoupon.getStartDate()
							+", end date: "+currCoupon.getEndDate()
							+", amount: "+currCoupon.getAmount()
							+", price: "+currCoupon.getPrice());
		}
	}
	
	/**
	 * Add new customer menu.
	 * @param adminFacade
	 * @throws DBOperationException in case of database operation error.
	 */
	private void addCustomer(AdminFacade adminFacade) throws DBOperationException
	{
		
		System.out.println("Enter customer first name: ");
		String firstName = scanner.nextLine();
		System.out.println("Enter customer last name: ");
		String lastName = scanner.nextLine();
		System.out.println("Enter customer email: ");
		String email = scanner.nextLine();
		System.out.println("Enter customer password: ");
		String password = scanner.nextLine();
		Customer customer = new Customer(firstName, lastName, email, password, new ArrayList<Coupon>());
		adminFacade.addCustomer(customer);
		System.out.println(adminFacade.getClientMsg());
	}
	
	/**
	 * Print all customers menu.
	 * @param adminFacade
	 * @throws DBOperationException in case of database operation error.
	 */
	private void printAllCustomers(AdminFacade adminFacade) throws DBOperationException
	{
		ArrayList<Customer> customers = adminFacade.getAllCustomers();
		System.out.println(customers.size()+" customers registered");
		for(Customer currCustomer : customers)
			System.out.println("\t"+currCustomer);
	}
	
	/**
	 * Delete company menu.
	 * @param adminFacade
	 * @throws DBOperationException in case of database operation error.
	 */
	private void deleteCompany(AdminFacade adminFacade) throws DBOperationException
	{
		System.out.println("Enter ID of company to delete: ");
		String idStr = scanner.nextLine();
		int id;
		try
		{
			id = Integer.parseInt(idStr);
			
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid input");
			return;
		}
		Company company = adminFacade.getOneCompany(id);
		if(company == null)
		{
			System.out.println("Company with this ID was not found");
			return;
		}
		System.out.println(company);
		System.out.println("DELETE THIS COMPANY? Y/N");
		String ans = scanner.nextLine();
		if(ans.equalsIgnoreCase("Y"))
		{
			adminFacade.deleteCompany(id);
			System.out.println(adminFacade.getClientMsg());
		}
		else
			return;
	}
	
	/**
	 * Update company menu.
	 * @param adminFacade
	 * @throws DBOperationException in case of database operation error.
	 */
	private void updateCompany(AdminFacade adminFacade) throws DBOperationException
	{
		System.out.println("Enter ID of company to update: ");
		String idStr = scanner.nextLine();
		int id = Integer.parseInt(idStr);
		Company company = adminFacade.getOneCompany(id);
		if(company == null)
		{
			System.out.println("Company with this ID was not found");
			return;
		}
		System.out.println(company);
		System.out.println("Enter new email: ");
		String email = scanner.nextLine();
		System.out.println("Enter new password: ");
		String password = scanner.nextLine();
		company.setEmail(email);
		company.setPassword(password);
		adminFacade.updateCompany(company);
		System.out.println(adminFacade.getClientMsg());
	}
	
	/**
	 * Add new company menu.
	 * @param adminFacade
	 * @throws DBOperationException in case of database operation error.
	 */
	private void addCompany(AdminFacade adminFacade) throws DBOperationException
	{
		
		System.out.println("Enter company name: ");
		String name = scanner.nextLine();
		System.out.println("Enter company email: ");
		String email = scanner.nextLine();
		System.out.println("Enter company password: ");
		String password = scanner.nextLine();
		Company company = new Company(name, email, password, new ArrayList<Coupon>());
		adminFacade.addCompany(company);
		System.out.println(adminFacade.getClientMsg());
	}
	
	private void printAllCompanies(AdminFacade adminFacade) throws DBOperationException
	{
		ArrayList<Company> companies = adminFacade.getAllCompanies();
		System.out.println(companies.size()+" companies registered");
		for(Company currCompany : companies)
			System.out.println("    id: "+currCompany.getId()
							+", name: "+currCompany.getName()
							+", email: "+currCompany.getEmail()
							+", password: "+currCompany.getPassword());
	}
	
	/**
	 * Print company coupons.
	 * @param adminFacade
	 * @throws DBCompanyOperationException 
	 */
	private void printCompanyCoupons(AdminFacade adminFacade) throws DBOperationException
	{
		System.out.println("Enter company ID to see his coupons: ");
		String idStr = scanner.nextLine();
		int id = Integer.parseInt(idStr);
		Company company= adminFacade.getOneCompany(id);
		if(company == null)
		{
			System.out.println(adminFacade.getClientMsg());
			return;
		}
		ArrayList<Coupon> coupons = company.getCoupons();
		System.out.println(company);
		System.out.println(coupons.size() + " coupons found ");
		for(Coupon currCoupon : coupons)
		{
			System.out.println("  id: "+currCoupon.getId()
							+", category: "+currCoupon.getCategory().getCategoryDescription()
							+", title: "+currCoupon.getTitle()
							+", description: "+currCoupon.getDecription()
							+", start date: "+currCoupon.getStartDate()
							+", end date: "+currCoupon.getEndDate()
							+", amount: "+currCoupon.getAmount()
							+", price: "+currCoupon.getPrice());
		}
	}
}
