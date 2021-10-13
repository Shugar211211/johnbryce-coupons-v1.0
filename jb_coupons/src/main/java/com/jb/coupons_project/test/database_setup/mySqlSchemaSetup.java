package com.jb.coupons_project.test.database_setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jb.coupons_project.custom_exceptions.DBCouponOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Category;

public class mySqlSchemaSetup 
{
	private String urlSchema = "jdbc:mysql://localhost:3306/jb_coupons?serverTimezone=UTC";
	private String url = "jdbc:mysql://localhost:3306?serverTimezone=UTC";
	private String user = "jbcp";
	private String password = "admin";
	private Connection connection;
	private Connection connectionWithSchema;
	
	/**
	 * Constructor with no arguments 
	 * @throws DBOperationException in case of errors related to database connection
	 */
	public mySqlSchemaSetup() throws DBOperationException 
	{
		super();
		try 
		{
			connection = DriverManager.getConnection(url, user, password);
		} 
		catch (SQLException e) 
		{
			throw new DBOperationException("Error connecting to database: "+e.getMessage());
		}
	}
	
	/**
	 * This method checks if 'jb_coupons' schema exists on MYSQL server.
	 * If schema was not found, it call method 'createSchema' to setup new schema, 
	 * and then copies categories from enum to new table.
	 * If schema was found, it call method to synchronize categories table of this 
	 * schema with enum.  
	 * @throws DBOperationException in case of errors related to database connection
	 */
	public void prepareCouponsSchema() throws DBOperationException
	{
		System.out.println("*** Checking SQL database.");
		PreparedStatement ps = null;
		ResultSet rs = null;
		String schemaCheck = "SELECT SCHEMA_NAME "
				+ "FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'jb_coupons';";
		try 
		{
			ps = connection.prepareStatement(schemaCheck);
			rs = ps.executeQuery();
			if( ! rs.next())
			{
				System.out.println("*** schema \'jb_coupons\' not found ");
				createSchema();
				synchronizeCategoriesTable();
			}
			else
			{
				System.out.println("*** found existing schema \'jb_coupons\'.");
				synchronizeCategoriesTable();
			}
		} 
		catch (SQLException e) 
		{
			throw new DBOperationException("Error connecting to database: " + e.getMessage());
		}
	}
	
	/**
	 * This method creates 'jb_coupons' schema on server.
	 * @throws DBOperationException in case of errors related to database connection.
	 */
	private void createSchema() throws DBOperationException 
	{
		System.out.println("*** Creating new schema.");
		PreparedStatement ps = null;
		String createSchema = "CREATE SCHEMA jb_coupons;";
		String useSchema = "USE jb_coupons;";
		String createCompanies = "CREATE TABLE COMPANIES ("
				+ "ID INT UNSIGNED NOT NULL AUTO_INCREMENT, "
				+ "NAME VARCHAR(128) UNIQUE, "
				+ "EMAIL VARCHAR(128) UNIQUE, "
				+ "PASSWORD VARCHAR(128), "
				+ "PRIMARY KEY(ID), "
				+ "INDEX (NAME));";
		
		String createCustomers = "CREATE TABLE CUSTOMERS ("
				+ "ID INT UNSIGNED NOT NULL AUTO_INCREMENT, "
				+ "FIRST_NAME VARCHAR(128), "
				+ "LAST_NAME VARCHAR(128), "
				+ "EMAIL VARCHAR(128) UNIQUE, "
				+ "PASSWORD VARCHAR(128), "
				+ "PRIMARY KEY(ID), "
				+ "INDEX (EMAIL));";
		
		String createCategories = "CREATE TABLE CATEGORIES ("
				+ "ID INT UNSIGNED NOT NULL AUTO_INCREMENT, "
				+ "NAME VARCHAR(64), "
				+ "PRIMARY KEY(ID), "
				+ "INDEX (NAME));";
		
		String createCoupons = "CREATE TABLE COUPONS ("
				+ "ID INT UNSIGNED NOT NULL AUTO_INCREMENT, "
				+ "COMPANY_ID INT UNSIGNED, "
				+ "CATEGORY_ID INT UNSIGNED, "
				+ "TITLE VARCHAR(128), "
				+ "DESCRIPTION VARCHAR(255), "
				+ "START_DATE DATE, "
				+ "END_DATE DATE, "
				+ "AMOUNT INT, "
				+ "PRICE DOUBLE, "
				+ "IMAGE VARCHAR(255), "
				+ "PRIMARY KEY(ID), "
				+ "INDEX (TITLE), "
				+ "CONSTRAINT FOREIGN KEY (COMPANY_ID) "
				+ "REFERENCES COMPANIES (ID) ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "CONSTRAINT FOREIGN KEY (CATEGORY_ID) "
				+ "REFERENCES CATEGORIES (ID) ON DELETE CASCADE ON UPDATE CASCADE);";
		
		String createCustomersVsCoupons = "CREATE TABLE CUSTOMERS_VS_COUPONS ("
				+ "CUSTOMER_ID INT UNSIGNED, "
				+ "COUPON_ID INT UNSIGNED, "
				+ "PRIMARY KEY(CUSTOMER_ID, COUPON_ID), "
				+ "CONSTRAINT FOREIGN KEY (CUSTOMER_ID) "
				+ "REFERENCES CUSTOMERS (ID) ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "CONSTRAINT FOREIGN KEY (COUPON_ID) "
				+ "REFERENCES COUPONS (ID) ON DELETE CASCADE ON UPDATE CASCADE);";
		
		try
		{
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(createSchema);
			ps.execute();
			
			ps = connection.prepareStatement(useSchema);
			ps.execute();
			
			ps = connection.prepareStatement(createCompanies);
			ps.execute();
			
			ps = connection.prepareStatement(createCustomers);
			ps.execute();
			
			ps = connection.prepareStatement(createCategories);
			ps.execute();
			
			ps = connection.prepareStatement(createCoupons);
			ps.execute();
			
			ps = connection.prepareStatement(createCustomersVsCoupons);
			ps.execute();
			
			connection.commit();
		}
		catch(SQLException e) 
		{
			try 
			{
				connection.rollback();
			} 
			catch (SQLException e1) 
			{
				throw new DBCouponOperationException("Error creating schema/database: "+
															e.getMessage());
			}
			e.printStackTrace();
			throw new DBCouponOperationException("Error creating schema/database: "+
														e.getMessage());
		}
		finally 
		{
			try 
			{
				connection.setAutoCommit(true);
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
				throw new DBCouponOperationException("Error creating schema/database: "+
															e.getMessage());
			}
		}
		System.out.println("*** New schema created successfully.");
	}
	
	/**
	 * Synchronizes NAME column of CATEGORIES table in JB_COUPONS db with Category enum.
	 * Entries in db that correspond to entries in enum are left unchanged.
	 * Entries that are present only in enum but not present in db are added to db.
	 * Entries that are present only in db but were deleted from enum get deleted from db.
	 * @throws DBOperationException in case of errors related to database connection.
	 */
	private void synchronizeCategoriesTable() throws DBOperationException
	{
		System.out.println("*** Synchronizing categories table, please wait.");
		String categoryDescription=null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM CATEGORIES;";
		try 
		{
			connectionWithSchema = DriverManager.getConnection(urlSchema, user, password);
			stmt = connectionWithSchema.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
											  ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
			
			// if resultset is empty, 
			if(! rs.next())
			{
				// copy all entries from Categories enum to Categories table in database
				for(Category currCategory : Category.values())
				{
					rs.moveToInsertRow();
					rs.updateString("NAME", currCategory.getCategoryDescription());
					rs.insertRow();
				}
			}
			
			// the resultset is not empty:
			else
			{
				// delete rows from Categories tablse in database 
				// that are not present in Categories enum
				boolean deleteRowFlag=false;
				while(rs.next())
				{
					categoryDescription = rs.getString("NAME");
					for(Category currCategory : Category.values())
					{
						if(currCategory.getCategoryDescription().equals(categoryDescription))
						{
							deleteRowFlag=false;
							break;
						}
						deleteRowFlag=true;
					}
					if(deleteRowFlag)
					{
						rs.deleteRow();
						rs.beforeFirst();
						deleteRowFlag=false;
					}
				}
				
				// now insert new values from Categories enum to Categories table in database
				boolean insertRowFlag=false;
				for(Category currCategory : Category.values())
				{
					rs.beforeFirst();
					while(rs.next())
					{
						categoryDescription = rs.getString("NAME");
						if(currCategory.getCategoryDescription().equals(categoryDescription)) 
						{
							insertRowFlag=false;
							break;
						}
						insertRowFlag=true;
					}
					if(insertRowFlag)
					{
						rs.moveToInsertRow();
						rs.updateString("NAME", currCategory.getCategoryDescription());
						rs.insertRow();
						rs.beforeFirst();
						insertRowFlag=false;
					}	
				}
			}
		} 
		catch (SQLException e) 
		{
			throw new DBOperationException("Categories table could not be synchronized: "
											+e.getMessage());
		}
		System.out.println("*** Synchronization done!");
	}
}
