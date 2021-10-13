package com.jb.coupons_project.test;

import java.sql.Date;
import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Category;
import com.jb.coupons_project.java_beans.Company;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.java_beans.Customer;
import com.jb.coupons_project.jdbc.dao.CouponsDAO;
import com.jb.coupons_project.jdbc.dao.CouponsDBDAO;
import com.jb.coupons_project.service.AdminFacade;
import com.jb.coupons_project.service.ClientFacade;
import com.jb.coupons_project.service.ClientType;
import com.jb.coupons_project.service.CompanyFacade;
import com.jb.coupons_project.service.CustomerFacade;
import com.jb.coupons_project.service.LoginManager;
import com.jb.coupons_project.test.console_menu.MainMenu;
import com.jb.coupons_project.test.database_setup.mySqlSchemaSetup;
import com.jb.coupons_project.utils.CouponExpirationDailyJob;

public class Test 
{	
	/**
	 * This method starts database check routine and daily job routine, 
	 * then starts series of hard-coded tests to check all logical operations of client facades.
	 * After hard-coded tests are completed, this method starts interactive 
	 * console menu for further testing. 
	 * When interactive menus exits, this method stops daily job.
	 */
	public static void testAll()
	{
		MainMenu menu = new MainMenu();
		CouponExpirationDailyJob couponExpirationDailyJob;
		Thread dailyJob = null;
		try
		{
			// setup SQL database
			mySqlSchemaSetup databaseSetupObject = new mySqlSchemaSetup();
			databaseSetupObject.prepareCouponsSchema();
			
			// create daily job
			couponExpirationDailyJob = new CouponExpirationDailyJob(new CouponsDBDAO());
			dailyJob = new Thread(couponExpirationDailyJob);
			
			// synchronize categories table
			// should not be used
//			CouponsDBDAO cdao =  new CouponsDBDAO();
//			cdao.synchronizeCategoriesTable();
			
			// start daily job
			System.out.println("*** Start daily job.");
			dailyJob.start();
			Thread.sleep(500L); // for testing purposes
			
			// uncomment 1 line below to enable hard-coded AdminFacade tests
			testAdminFacade();
			
			// uncomment 1 line below to enable hard-coded CompanyFacade tests
			testCompanyFacace();
			
			// uncomment 1 line below to enable hard-coded CustomerFacade tests
			testCustomerFacade();
			
			// comment out 1 line below to disable all console menus
			System.out.println("\n\n*** Launching interactive menus:");
			menu.loginMenu();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
//			e.printStackTrace(); // for debugging purposes
		}
		finally
		{
			try
			{
				// request to stop daily job
				dailyJob.interrupt();
			}
			catch(NullPointerException e)
			{
				System.out.println("Daily job error.");
			}
		}
	}
	
	/**
	 * This method runs tests of all operations of customerFacade.
	 * @throws DBOperationException in case of database related error
	 */
	private static void testCustomerFacade() throws DBOperationException 
	{
		System.out.println("\n*** Testing customer facade:");
		String email = "k@gmail.com";
		String password = "java123";
		ClientType clientType = ClientType.CUSTOMER;
		LoginManager loginManager = LoginManager.getInstance();
		ClientFacade clientFacade = loginManager.login(email, password, clientType);
		CustomerFacade customerFacade = (CustomerFacade) clientFacade;
		if(customerFacade == null)
		{
			System.out.println(loginManager.getClientMsg());
			return;
		}
		System.out.println(customerFacade.getClientMsg());
		
		// purchaseCoupon test
		System.out.println("*** Purchase coupon test: should pass");
		CouponsDAO cdb = new CouponsDBDAO();
		Coupon coupon = cdb.getOneCoupon(3);
		customerFacade.purchaseCoupon(coupon);
		System.out.println(customerFacade.getClientMsg());
		
		// purchaseCoupon test
		System.out.println("*** Purchase coupon test: should pass");
		CouponsDAO cdb1 = new CouponsDBDAO();
		Coupon coupon1 = cdb1.getOneCoupon(6);
		customerFacade.purchaseCoupon(coupon1);
		System.out.println(customerFacade.getClientMsg());
		
		// purchaseCoupon test
		System.out.println("*** Purchase coupon test: should fail because coupon was not found");
		CouponsDAO cdb2 = new CouponsDBDAO();
		Coupon coupon2 = cdb2.getOneCoupon(7);
		customerFacade.purchaseCoupon(coupon2);
		System.out.println(customerFacade.getClientMsg());
		
		// purchaseCoupon test
		System.out.println("*** Purchase coupon test: should pass");
		CouponsDAO cdb3 = new CouponsDBDAO();
		Coupon coupon3 = cdb3.getOneCoupon(1);
		customerFacade.purchaseCoupon(coupon3);
		System.out.println(customerFacade.getClientMsg());
		
		// purchaseCoupon test
		System.out.println("*** Purchase coupon test: should fail "
				+ "due to attept purchase coupon that was already purchased");
		CouponsDAO cdb4 = new CouponsDBDAO();
		Coupon coupon4 = cdb4.getOneCoupon(3);
		customerFacade.purchaseCoupon(coupon4);
		System.out.println(customerFacade.getClientMsg());
		
		// purchaseCoupon test
		System.out.println("*** Purchase coupon test: should fail "
				+ "because coupon is not available");
		CouponsDAO cdb5 = new CouponsDBDAO();
		Coupon coupon5 = cdb5.getOneCoupon(1);
		customerFacade.purchaseCoupon(coupon5);
		System.out.println(customerFacade.getClientMsg());
		
		// getCustomerCoupons test
		System.out.println("*** Get all coupons of this customer test: should retrieve 3 coupons");
		ArrayList<Coupon> customerCoupons = customerFacade.getCustomerCoupons();
		System.out.println(customerFacade.getClientMsg());
		if(customerCoupons != null)
			for(Coupon currCoupon : customerCoupons)
				System.out.println(currCoupon);
		else
			System.out.println(customerFacade.getClientMsg());
		
		// getCustomerCoupons by category test
		System.out.println("*** Get all customer coupons by category test: should retrieve 1 coupon");
		ArrayList<Coupon> customerCoupons2 = customerFacade.getCustomerCoupons(Category.COMPUTERS);
		System.out.println(customerFacade.getClientMsg());
		if(customerCoupons2 != null)
			for(Coupon currCoupon : customerCoupons2)
				System.out.println(currCoupon);
		else
			System.out.println(customerFacade.getClientMsg());
		
		// getCustomerCoupons by max price test
		System.out.println("*** Get all customer coupons under certain price test: "
				+ "should retrieve 1 coupon");
		ArrayList<Coupon> customerCoupons3 = customerFacade.getCustomerCoupons(45.0);
		System.out.println(customerFacade.getClientMsg());
		if(customerCoupons3 != null)
			for(Coupon currCoupon : customerCoupons3)
				System.out.println(currCoupon);
		else
			System.out.println(customerFacade.getClientMsg());
		
		// getCustomerDetails test
		System.out.println("*** get customer details test: should pass");
		System.out.println(customerFacade.getCustomerDetails() != null ? customerFacade.getCustomerDetails() : customerFacade.getClientMsg());
		
		// deleteCouponPurchase test
		CouponsDAO cd = new CouponsDBDAO();
		System.out.println("*** delete coupon purchase test: should pass.");
		cd.deleteCouponPurchase(3, 1);
		// add message here
		System.out.println("*** Customer facade test completed");
	}
	
	/**
	 * This method runs tests of all operations of companyFacade.
	 * @throws DBOperationException in case of database related error
	 */
	private static void testCompanyFacace() throws DBOperationException 
	{
		System.out.println("\n*** Testing company facade:");
		String email = "sales@ksp.co.il";
		String password = "ksp";
		ClientType clientType = ClientType.COMPANY;
		LoginManager loginManager = LoginManager.getInstance();
		ClientFacade clientFacade = loginManager.login(email, password, clientType);
		CompanyFacade companyFacade = (CompanyFacade) clientFacade;
		if(companyFacade == null)
		{
			System.out.println(loginManager.getClientMsg());
			return;
		}
		System.out.println(companyFacade.getClientMsg());
		Date startDate = Date.valueOf("2020-07-01");
		Date endDate = Date.valueOf("2020-10-01");
		
		// addCoupon test
		System.out.println("*** Add coupon test: should pass.");
		//String startDateFormatted = "2020-06-01";
		//String endDateFormatted = "2020-07-01";
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//long startDateMillis = startDate.getTime();
		//long endDateMillis = endDate.getTime();
		Coupon coupon1 = new Coupon(1, 
					Category.COMPUTERS, 
					"HP all-in-one", "HP ENVY 32-A0000NJ / 9CT26EA - All-in-One computer",
					startDate, 
					endDate,
					50, 
					2999.0,
					"https://img.ksp.co.il/item/99245/b_1.jpg?noCash");
		companyFacade.addCoupon(coupon1);
		System.out.println(companyFacade.getClientMsg());
		
		// addCoupon test
		System.out.println("*** Add coupon test: should fail due to expired endDate.");
		startDate = Date.valueOf("2020-03-01");
		endDate = Date.valueOf("2020-04-01");
		Coupon coupon2 = new Coupon(1, 
				Category.COMPUTERS, 
				"Asus monitor", "Asus MG278Q 27 LED gaming monitor",
				startDate, 
				endDate,
				50, 
				1999.0,
				"https://img.ksp.co.il/item/31845/b_1.jpg?noCash");
		companyFacade.addCoupon(coupon2);
		System.out.println(companyFacade.getClientMsg());
		
		// updateCoupon test
		System.out.println("*** Update coupon test: should pass.");
		startDate = Date.valueOf("2020-08-01");
		endDate = Date.valueOf("2020-10-01");
		// couponID and companyID should be fixed by current IDs in database 
		Coupon coupon3 = new Coupon(6, 1, 
				Category.FOOD, 
				"HP all-in-one", "HP ENVY 32-A0000NJ / 9CT26EA - All-in-One computer",
				startDate, 
				endDate,
				0, 
				2999.0,
				"https://img.ksp.co.il/item/99245/b_1.jpg?noCash");
		companyFacade.updateCoupon(coupon3);
		System.out.println(companyFacade.getClientMsg());
		
		// updateCoupon test
		System.out.println("*** Update coupon test: should fail due to attempt to update company id");
		startDate = Date.valueOf("2020-06-01");
		endDate = Date.valueOf("2020-07-01");
		// couponID and companyID should be fixed by current IDs in database 
		Coupon coupon4 = new Coupon(1, 5, 
				Category.FOOD, 
				"Asus monitor", "Asus MG278Q 27 LED gaming monitor",
				startDate, 
				endDate,
				400, 
				7.0,
				"https://img.ksp.co.il/item/31845/b_1.jpg?noCash");
		companyFacade.updateCoupon(coupon4);
		System.out.println(companyFacade.getClientMsg());
				
		// deleteCoupon test
		System.out.println("*** Delete coupon test: should pass");
		companyFacade.deleteCoupon(4);
		System.out.println(companyFacade.getClientMsg());
		
		// deleteCoupon test
		System.out.println("*** Delete coupon test: should fail due "
				+ "to attempt to delete non-existent coupon");
		companyFacade.deleteCoupon(51);
		System.out.println(companyFacade.getClientMsg());
		
		// test getCompanyCoupons
		System.out.println("*** Test of retieving all company coupons: should pass");
		ArrayList<Coupon> companyCoupons = companyFacade.getCompanyCoupons();
		System.out.println(companyFacade.getClientMsg());
		if(companyCoupons != null)
			for(Coupon currCoupon : companyCoupons)
				System.out.println(currCoupon);
		else
			System.out.println(companyFacade.getClientMsg());
		
		// test getCompanyCouponsByCategory
		System.out.println("*** Test of retieving all company coupons in certain category: "
				+ "should return 3 coupons");
		ArrayList<Coupon> companyCoupons2 = companyFacade.getCompanyCoupons(Category.COMPUTERS);
		System.out.println(companyFacade.getClientMsg());
		if(companyCoupons2 != null)
			for(Coupon currCoupon : companyCoupons2)
				System.out.println(currCoupon);
		else
			System.out.println(companyFacade.getClientMsg());
		
		// test getCompanyCoupons by max price
		System.out.println("*** Test of retieving all company coupons under certain price: "
				+ "should return 2 coupons");
		ArrayList<Coupon> companyCoupons3 = companyFacade.getCompanyCoupons(50.0);
		System.out.println(companyFacade.getClientMsg());
		if(companyCoupons3 != null)
			for(Coupon currCoupon : companyCoupons3)
				System.out.println(currCoupon);
		else
			System.out.println(companyFacade.getClientMsg());
		
		// test getCompanyDetails
		System.out.println("*** Test of retrieving company details: should pass");
		System.out.println(companyFacade.getCompanyDetails() != null ? companyFacade.getCompanyDetails() : companyFacade.getClientMsg());
		System.out.println("*** Company facade test completed");
	}
	
	/**
	 * This method runs tests of all operations of adminFacade.
	 * @throws DBOperationException in case of database related error
	 */
	private static void testAdminFacade() throws DBOperationException 
	{	
		System.out.println("\n*** Testing admin facade:");
		String email = "admin@admin.com";
		String password = "admin";
		ClientType clientType = ClientType.ADMINISTRATOR;
		
		LoginManager loginManager = LoginManager.getInstance();
		ClientFacade clientFacade = loginManager.login(email, password, clientType);
		AdminFacade adminFacade = (AdminFacade) clientFacade;
		if(adminFacade == null)
		{
			System.out.println(loginManager.getClientMsg());
			return;
		}
		System.out.println(adminFacade.getClientMsg());
		// addCompany with coupons test. 
		// This test should pass successfully.
		System.out.println("*** Add new company test: should pass");
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		Coupon ksp1 = new Coupon(Category.COMPUTERS, 
								   "Lenovo discount 20%", 
								   "20% discount on all Lenovo products",
								   Date.valueOf("2020-04-01"),
								   Date.valueOf("2020-06-01"),
								   0,
								   29.99,
								   "https://external-content.duckduckgo.com/iu/?u=https%"
								   + "3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.3W4xOV-cI"
								   + "tf35Qbh9yM-OAHaDJ%26pid%3DApi&f=1");
		coupons.add(ksp1);
		
		Coupon ksp2 = new Coupon(Category.COMPUTERS, 
									"KSP Special offer Skullcandy 40% discount.", 
									"40% discount on all Skullcandy products",
									Date.valueOf("2020-04-01"),
									Date.valueOf("2020-07-01"),
									80,
									19.99,
									"https://external-content.duckduckgo.com/iu/?u="
									+ "https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP."
									+ "OzDTZghgVopBI-r5ln4UwQHaEr%26pid%3DApi&f=1");
		coupons.add(ksp2);
		
		Coupon ksp3 = new Coupon(Category.COMPUTERS, 
									"SAMSUNG galaxy S20 Ultra.", 
									"SAMSUNG galaxy S20 Ultra sale.",
									Date.valueOf("2020-04-26"),
									Date.valueOf("2020-05-30"),
									100,
									799.0,
									"https://external-content.duckduckgo.com/iu/?u=https"
									+ "%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.izseQry"
									+ "IJPOWeIVe5Ibp4AHaEK%26pid%3DApi&f=1");
		coupons.add(ksp3);
		
		Company ksp = new Company("KSP computers", "sales@ksp.co.il", "ksp", coupons);
		adminFacade.addCompany(ksp);
		System.out.println(adminFacade.getClientMsg());
		
		// addCompany test.
		// Should pass successfully.
		System.out.println("*** Add new company test: should pass");
		Company issta = new Company("ISSTA", "ic@issta.com", "issta321", new ArrayList<Coupon>());
		adminFacade.addCompany(issta);
		System.out.println(adminFacade.getClientMsg());
		
		// addCompany test.
		// Should pass successfully.
		System.out.println("*** Add new company test: should pass");	
		ArrayList<Coupon> coupons2 = new ArrayList<Coupon>();
		Coupon pizza1 = new Coupon(Category.FOOD, 
								   "Family pizza for 40 nis", 
								   "1 Family pizza including delivery for 40 nis",
								   Date.valueOf("2020-06-15"),
								   Date.valueOf("2020-12-15"),
								   0,
								   40.00,
								   "");
		coupons2.add(pizza1);
		
		Coupon pizza2 = new Coupon(Category.FOOD, 
									"XL Pizza for 50 nis", 
									"XL Pizza including delivery for 50 nis",
									Date.valueOf("2020-04-01"),
									Date.valueOf("2020-08-01"),
									80,
									50.00,
									"");
		coupons2.add(pizza2);
		Company pizza = new Company("Dominos Pizza", "pizza@pizza.com", "pizza123", coupons2);
		adminFacade.addCompany(pizza);
		System.out.println(adminFacade.getClientMsg());
		
		// addCompany test.
		// Should fail due to email duplicate
		System.out.println("*** Add new company test: should fail due to email duplicate");
		Company tivtaam = new Company("Tiv Taam", "ic@issta.com", "tivtaam999", new ArrayList<Coupon>());
		adminFacade.addCompany(tivtaam);
		System.out.println(adminFacade.getClientMsg());
		
		// updateCompany test
		// Should pass successfully
		System.out.println("*** Update company test: should pass successfully");
		Company pizzaUpdate = adminFacade.getOneCompany(3);
		if(pizzaUpdate != null)
		{
			pizzaUpdate.setEmail("p@pizza.com");
			pizzaUpdate.setPassword("pizza");
			pizzaUpdate.setCoupons(new ArrayList<Coupon>());
			adminFacade.updateCompany(pizzaUpdate);
		}
		System.out.println(adminFacade.getClientMsg());
		
		// updateCompany test
		// Should fail due to name change
		System.out.println("*** Update company test: "
				+ "should fail due to attempt to change company name");
		Company pizzaUpdate2 = adminFacade.getOneCompany(1);
		if(pizzaUpdate2 != null)
		{
			pizzaUpdate2.setName("Pizza Hut");
			pizzaUpdate2.setPassword("pizza");
			pizzaUpdate2.setCoupons(new ArrayList<Coupon>());
			adminFacade.updateCompany(pizzaUpdate2);
		}
		System.out.println(adminFacade.getClientMsg());
		
		// deleteCompany test
		// should pass successfully
		System.out.println("*** Delete company test: should pass");
		adminFacade.deleteCompany(2);
		System.out.println(adminFacade.getClientMsg());
		
		// deleteCompany test
		// should fail
		System.out.println("*** Delete company test: should fail due to inexisting company id");
		adminFacade.deleteCompany(18);
		System.out.println(adminFacade.getClientMsg());
		
		// getAllCompanies test: should pass successfully
		System.out.println("*** getAllCompanies test: should pass successfully");
		ArrayList<Company> companies = adminFacade.getAllCompanies();
		System.out.println(adminFacade.getClientMsg());
		for(Company currCompany : companies)
		{
			System.out.println(currCompany.toString());
		}
		
		// getOneCompany test: should pass successfully
		System.out.println("*** getOneCompany test: should pass successfully");
		System.out.println(adminFacade.getOneCompany(1) != null ? adminFacade.getOneCompany(1) : adminFacade.getClientMsg());
		System.out.println(adminFacade.getClientMsg());
		
		// getOneCompany test: should pass successfully
		System.out.println("*** getOneCompany test: should fail due to wrong id");
		System.out.println(adminFacade.getOneCompany(-16) != null ? adminFacade.getOneCompany(-16) : adminFacade.getClientMsg());
		System.out.println(adminFacade.getClientMsg());
		
		// addCustomer test
		// add customer 1
		System.out.println("*** addCustomer test: should pass successfully");
		Customer customer1 = new Customer("FirstName", 
										  "LastName", 
										  "c@gmail.com", 
										  "java123", 
										  new ArrayList<Coupon>());
		adminFacade.addCustomer(customer1);
		System.out.println(adminFacade.getClientMsg());
		
		// add customer 2
		System.out.println("*** addCustomer test: should pass successfully");
		Customer customer3 = new Customer("Ana", 
										  "Lk", 
										  "aka@gmail.com", 
										  "java345", 
										  new ArrayList<Coupon>());
		adminFacade.addCustomer(customer3);
		System.out.println(adminFacade.getClientMsg());
		
		// add customer 3
		System.out.println("*** addCustomer test: should fail dut to email duplicate");
		Customer customer2 = new Customer("Vadim", 
										  "Sando", 
										  "aka@gmail.com", 
										  "java234", 
										  new ArrayList<Coupon>());
		adminFacade.addCustomer(customer2);
		System.out.println(adminFacade.getClientMsg());
		
		// add customer 4
		System.out.println("*** addCustomer test: should pass successfully");
		Customer customer4 = new Customer("Arye", 
										  "Kanas", 
										  "k@gmail.com", 
										  "java123", 
										  new ArrayList<Coupon>());
		adminFacade.addCustomer(customer4);
		System.out.println(adminFacade.getClientMsg());
		
		// test updateCustomer
		System.out.println("*** updateCustomer test: should pass successfully");
		Customer customerUpdated1 = adminFacade.getOneCustomer(1);
		if(customerUpdated1 != null)
		{
			customerUpdated1.setEmail("ks@gmail.com");
			customerUpdated1.setFirstName("Eugeny");
			customerUpdated1.setLastName("K");
			adminFacade.updateCustomer(customerUpdated1);
		}
		System.out.println(adminFacade.getClientMsg());
		
		// test updateCustomer
		System.out.println("*** updateCustomer test: should fail due to attempt to change ID");
		Customer customerUpdated2 = adminFacade.getOneCustomer(01);
		if(customerUpdated2 != null)
		{
			customerUpdated2.setId(10);
			adminFacade.updateCustomer(customerUpdated2);
		}
		System.out.println(adminFacade.getClientMsg());
		
		// test deleteCustomer
		System.out.println("*** deleteCustomer test: should pass successfully");
		adminFacade.deleteCustomer(1);
		System.out.println(adminFacade.getClientMsg());
		
		// test deleteCustomer
		System.out.println("*** updateCustomer test: should fail due to inexisting ID");
		adminFacade.deleteCustomer(52);
		System.out.println(adminFacade.getClientMsg());
		
		// test getAllCustomers
		System.out.println("*** getAllCustomersTest: should pass successfully");
		ArrayList<Customer> customers = adminFacade.getAllCustomers();
		System.out.println(adminFacade.getClientMsg());
		for(Customer currCustomer : customers)
		{
			System.out.println(currCustomer.toString());
		}
		
		// test getOneCustomer
		System.out.println("*** getOneCustomer test: should pass successfully");
		System.out.println(adminFacade.getOneCustomer(3) != null ? adminFacade.getOneCustomer(3) : loginManager.getClientMsg());
		
		// log out test
		System.out.println("Admin log out: ");
		loginManager.logout(clientFacade);
		System.out.println(loginManager.getClientMsg());
		System.out.println("*** Admin facade test completed");
	}
}
