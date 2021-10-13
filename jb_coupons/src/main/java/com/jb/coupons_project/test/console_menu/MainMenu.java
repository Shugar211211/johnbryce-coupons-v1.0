package com.jb.coupons_project.test.console_menu;

import java.util.Scanner;

import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.custom_exceptions.InvalidInputException;
import com.jb.coupons_project.service.ClientFacade;
import com.jb.coupons_project.service.ClientType;
import com.jb.coupons_project.service.LoginManager;

public class MainMenu 
{
	private LoginManager loginManager;
	private ClientType clientType;
	private ClientFacade clientFacade;
	private Scanner scanner = new Scanner(System.in);
	
	/**
	 * Constructor
	 */
	public MainMenu()
	{
		super();
		loginManager = LoginManager.getInstance();
		clientType = null;
		clientFacade = null;
	}
	
	/**
	 * This method scans user login details and calls client menu according to client type.
	 * @throws InvalidInputException in case of invalid user input.
	 */
	public void loginMenu() throws InvalidInputException 
	{
//		scanner = new Scanner(System.in);
		String loginType = "";
		System.out.println("\nWellcome to JB_Coupons testing menus");
		while( ! loginType.equals("4") )
		{
			System.out.println("\n------------------------------------------------------------");
			System.out.println("Login menu:");
			System.out.println("\n1: login as administrator");
			System.out.println("2: login as company");
			System.out.println("3: login as customer");
			System.out.println("4: quit application");
			System.out.println("------------------------------------------------------------");
			loginType = scanner.nextLine();
			if(loginType.equals("4"))
			{
				System.out.println("bye");
				scanner.close();
				return;
			}
			else if(loginType.equals("1"))
				clientType = ClientType.ADMINISTRATOR;
			else if(loginType.equals("2"))
				clientType = ClientType.COMPANY;
			else if(loginType.equals("3"))
				clientType = ClientType.CUSTOMER;
			else
			{
				System.out.println("Invalid input");
				continue;
			}
			
			System.out.println("Enter email: ");
			String email = scanner.nextLine();
			System.out.println("Enter password: ");
			String password = scanner.nextLine();
			try 
			{
				clientFacade = loginManager.login(email, password, clientType);
			} 
			catch (DBOperationException e) 
			{
				System.out.println(e.getMessage());
//				e.printStackTrace();
			}
			if(clientFacade == null)
			{
				System.out.println(loginManager.getClientMsg());
			}
			else if(clientType == ClientType.ADMINISTRATOR)
			{
				System.out.println(loginManager.getClientMsg());
				AdminMenu adminMenu = new AdminMenu(clientFacade);
			}
			else if(clientType == ClientType.COMPANY)
			{
				System.out.println(loginManager.getClientMsg());
				CompanyMenu companyMenu = new CompanyMenu(clientFacade);
			}
			else if(clientType == ClientType.CUSTOMER)
			{
				System.out.println(loginManager.getClientMsg());
				CustomerMenu customerMenu = new CustomerMenu(clientFacade);
			}
			else
				throw new InvalidInputException("Login error");
		}
	}
}
