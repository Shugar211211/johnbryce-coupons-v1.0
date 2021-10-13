package com.jb.coupons_project.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBCompanyOperationException;
import com.jb.coupons_project.custom_exceptions.DBCouponOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Category;
import com.jb.coupons_project.java_beans.Company;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.jbbc.ConnectionPool;

public class CompaniesDBDAO implements CompaniesDAO 
{
	private ConnectionPool pool;
	
	/**
	 * Constructor.
	 * @throws DBOperationException in case of database error.
	 */
	public CompaniesDBDAO() throws DBOperationException {
		super();
		this.pool = ConnectionPool.getInstance();
	}
	
	/**
	 * Method checks if company with such email and password exists in database.
	 * @param company email, company password
	 * @return true if company exists or false otherwise
	 * @throws DBOperationException in case of database error while performing check.
	 */
	public boolean isCompanyExists(String email, String password) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs;
		String query = "SELECT EMAIL, PASSWORD FROM COMPANIES WHERE EMAIL = ? AND PASSWORD = ?";
		boolean exists = false;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, email);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if(rs.next())
				exists = true;
		}
		catch(SQLException e) 
		{
			throw new DBCompanyOperationException("Can not verify company: "+e.getMessage(), 
												  "company email: "+email);
		}
		finally {pool.restoreConnection(connection);}
		return exists;
	}
	
	/**
	 * Method adds new company to database.
	 * @param company object that represents company to add.
	 * @throws DBCompanyOperationException in case of database error while adding new company.
	 */
	public void addCompany(Company company) throws DBOperationException
	{	
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
//		int companyID;
//		ResultSet rs;
		
		// Add company to database 
		String query = "INSERT INTO COMPANIES (NAME, EMAIL, PASSWORD) VALUES (?, ?, ?);";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, company.getName());
			ps.setString(2, company.getEmail());
			ps.setString(3, company.getPassword());
			if(ps.executeUpdate() == 0)
				throw new DBCompanyOperationException("Error: Company was not registered", 
						"CompanyID: "+company.getId());
			
			// if company has coupons
//			ArrayList<Coupon> coupons = company.getCoupons();
//			if(coupons != null && coupons.size()>0)
//			{
//				CouponsDAO cdao = new CouponsDBDAO();
//				// get id of company that was just inserted
//				// https://stackoverflow.com/questions/14170656/get-last-inserted-auto-increment-id-in-mysql
//				ps = connection.prepareStatement("SELECT LAST_INSERT_ID() AS COMPANY_ID FROM COMPANIES;");
//				rs = ps.executeQuery();
//				companyID = rs.getInt("COMPANY_ID");
//				for(Coupon currCoupon : coupons)
//				{
//					currCoupon.setCompanyID(companyID);
//					cdao.addCoupon(currCoupon);
//				}
//			}
		}
		catch(SQLException e) 
		{
			throw new DBCompanyOperationException("Company could not be registered: "
													+e.getMessage(), 
													"CompanyID: "+company.getId());
		}
		finally {pool.restoreConnection(connection);}
	}
	
	/**
	 * Method updates company entry in database.
	 * @param company object that represents updated company.
	 * @throws DBOperationException in case of invalid fields of company object 
	 * or database error while updating company. 
	 */
	public void updateCompany(Company company) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		
		// update company coupons first
		this.updateCompanysCoupons(company);
		
		// now update company
		String query = "UPDATE COMPANIES SET EMAIL= ?, PASSWORD = ? WHERE ID = ?;";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, company.getEmail());
			ps.setString(2, company.getPassword());
			ps.setInt(3, company.getId());
			if(ps.executeUpdate() == 0)
				throw new DBCompanyOperationException("Error: Company was not updated", 
						"CompanyID: "+company.getId());
		}
		catch(SQLException e) 
		{
			throw new DBCompanyOperationException("Company could not be updated: "
													+e.getMessage(), 
													"CompanyID: "+company.getId());
		}
		finally {pool.restoreConnection(connection);}
	}
	
	/**
	 * Helper method that updates coupons of a company.
	 * @param company object that represents company with new list of coupons to replace old list.
	 * @throws DBOperationException in case of datavase error.
	 */
	private void updateCompanysCoupons(Company company) throws DBOperationException
	{
		Connection connection = pool.getConnection();
		CouponsDBDAO couponsDBDAO = new CouponsDBDAO();
		// at first delete from db coupons that are no longer present in company coupons list:
		// removeCoupon = true
		boolean removeCoupon;
		boolean addCoupon;
		ArrayList<Coupon> couponsNewList = company.getCoupons();
		// loop over company coupons in db 
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "SELECT * FROM COUPONS WHERE COMPANY_ID = ?;";
		try
		{
			ps = connection.prepareStatement(query,
											 ResultSet.TYPE_SCROLL_SENSITIVE, 
											 ResultSet.CONCUR_UPDATABLE);
			ps.setInt(1, company.getId());
			rs = ps.executeQuery();
			while(rs.next())
			{
				removeCoupon = true;
				// loop over arrayList coupons 
				for(Coupon currCoupon : couponsNewList)
				{
					// if id of db coupon match id of arrayList coupon 
					if(currCoupon.getId() == rs.getInt("ID"))
					{
						// removeCoupon = false
						removeCoupon = false;
						// break
						break;
					}
				}
				// if removeCoupon == true
				if(removeCoupon == true)
				{
					// call method to delete coupon from db
					couponsDBDAO.deleteCoupon(rs.getInt("ID"));
				}
			}
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Company coupons could not be updated: "
													+e.getMessage(), 
													"CompanyID: "+company.getId());
		}
		// now add coupons that are in company list but not yet in db, and update coupons that are in list and in db:
		// loop over arrayList coupons
		for(Coupon currCoupon : couponsNewList)
		{
			addCoupon = true;
			try
			{
				rs.beforeFirst();
				// loop over company coupons in db
				while(rs.next())
				{
					// if id of db coupon match id of arrayList coupon 
					if(currCoupon.getId() == rs.getInt("ID"))
					{
						// addCoupon = false
						addCoupon = false;
						couponsDBDAO.updateCoupon(currCoupon);
						break;
					}
				}
			}
			catch(SQLException e) 
			{
				throw new DBCompanyOperationException("Company coupons could not be updated: "
														+e.getMessage(), 
														"CompanyID: "+company.getId());
			}
			finally {pool.restoreConnection(connection);}
			// if addCoupon == true
			if(addCoupon == true)
			{
				// call method to add coupon from arrayList to db
				couponsDBDAO.addCoupon(currCoupon);
			}	
		}
		pool.restoreConnection(connection);
	}
	
	/**
	 * Method deletes company entry in database.
	 * @param companyID - id of company to delete.
	 * @throws DBOperationException in case of database error while deleting company.
	 */
	public void deleteCompany(int companyID) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "DELETE FROM COMPANIES WHERE ID = ?";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, companyID);
			if(ps.executeUpdate() == 0)
				throw new DBCompanyOperationException("Error: Company was not deleted", 
						"CompanyID: "+companyID);
		}
		catch(SQLException e) 
		{
			throw new DBCompanyOperationException("Could not delete company: "+e.getMessage(),
					"CompanyID: "+companyID);
		}
		finally {pool.restoreConnection(connection);}
	}
	
	/**
	 * Method retrieves list of all companies from database.
	 * @return ArrayList of company objects.
	 * @throws DBOperationException in case of database error while retrieving companies; 
	 */
	public ArrayList<Company> getAllCompanies() throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT COMP.ID AS COMP_ID, "
							+ "COMP.NAME, "
							+ "COMP.EMAIL, "
							+ "COMP.PASSWORD, "
							  + "COUP.ID AS COUPON_ID, "
							  + "COUP.COMPANY_ID, "
							  	+ "CATG.NAME AS CATEGORY_NAME,"
							  + "COUP.TITLE, "
							  + "COUP.DESCRIPTION, "
							  + "COUP.START_DATE, "
							  + "COUP.END_DATE, "
							  + "COUP.AMOUNT, "
							  + "COUP.PRICE, "
							  + "COUP.IMAGE "
					+ "FROM "
							+ "COMPANIES COMP "
						+ "LEFT OUTER JOIN COUPONS COUP "
							+ "ON COMP.ID = COUP.COMPANY_ID "
						+ "LEFT OUTER JOIN CATEGORIES CATG "
							+ "ON COUP.CATEGORY_ID = CATG.ID "
					+ "ORDER BY COMP.ID;";
		ResultSet rs=null;
		// create empty companiesList
		ArrayList<Company> companies = new ArrayList<Company>();
		Company company = null;
		try
		{
			ps=connection.prepareStatement(query);
			rs = ps.executeQuery(query);
			// newCompanyId = -1
			int newCompanyID = -1;
			// create empty couponsList
			ArrayList<Coupon> coupons = new ArrayList<Coupon>();
			// loop over all rows in rs
			while(rs.next())
			{
				// if row is new company
				if(rs.getInt("COMP_ID") != newCompanyID)
				{
					// if newCompanyID != -1
					if(newCompanyID != -1)
					{
						// add couponsList to company
						company.setCoupons(coupons);
						// add company to companiesList
						companies.add(company);
						// couponsList = new empty couponsList
						coupons = new ArrayList<Coupon>();
					}
					// retrieve company from rs
					company = new Company(rs.getInt("COMP_ID"), 
							  			  rs.getString("NAME"),
							  			  rs.getString("EMAIL"),
							  			  rs.getString("PASSWORD"),
							  			  new ArrayList<Coupon>(0));
					// newCompanyID = companyID
					newCompanyID = company.getId();
				}
				// if newCouponID is not null
				if(rs.getInt("COUPON_ID") != 0)
				{
					// retrieve coupon
					Coupon coupon = new Coupon(rs.getInt("COUPON_ID"), 
			   				   				   rs.getInt("COMPANY_ID"), 
			   				   				   Category.getCategoryTitle(rs.getString("CATEGORY_NAME")),
			   				   				   rs.getString("TITLE"),
			   				   				   rs.getString("DESCRIPTION"),
			   				   				   rs.getDate("START_DATE"),
			   				   				   rs.getDate("END_DATE"),
			   				   				   rs.getInt("AMOUNT"),
			   				   				   rs.getDouble("PRICE"),
			   				   				   rs.getString("IMAGE"));
					// add coupon to couponsList
					coupons.add(coupon);
				}
			}
			if(newCompanyID != -1)
			{
				// add couponsList to company
				company.setCoupons(coupons);
				// add company to companiesList
				companies.add(company);
			}
		}
		catch(SQLException e) 
		{
			throw new DBCompanyOperationException("Could not retrieve companies: "
													+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return companies;
	}
	
	/**
	 * Method retrieves company by companyID.
	 * @param companyID - id to search for.
	 * @return company object.
	 * @throws DBOperationException in case of database error while retrieving company.
	 */
	public Company getOneCompany(int companyID) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query1 = "SELECT * FROM COMPANIES WHERE ID = ?";
		String query2 = "SELECT COUP.ID, "
							 + "COUP.COMPANY_ID, "
							 + "CATG.NAME, "
							 + "COUP.TITLE, "
							 + "COUP.DESCRIPTION, "
							 + "COUP.START_DATE, "
							 + "COUP.END_DATE, "
							 + "COUP.AMOUNT, "
							 + "COUP.PRICE, "
							 + "COUP.IMAGE "
					  + "FROM COUPONS COUP "
					  	+ "INNER JOIN CATEGORIES CATG "
					  		+ "ON COUP.CATEGORY_ID = CATG.ID "
					  + "WHERE COUP.COMPANY_ID = ?;";
		Company company=null;
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		ResultSet rs=null;
		try
		{
			ps = connection.prepareStatement(query1);
			ps.setInt(1, companyID);
			rs = ps.executeQuery();
			if(rs.next())
			{
				company = new Company (companyID, 
						   			   rs.getString("NAME"), 
						   			   rs.getString("EMAIL"), 
						   			   rs.getString("PASSWORD"),
						   			   new ArrayList<Coupon>(0));
				ps = connection.prepareStatement(query2);
				ps.setInt(1, companyID);
				rs = ps.executeQuery();
				while(rs.next())
				{
					Coupon coupon = new Coupon(rs.getInt("ID"), 
			   				   				   rs.getInt("COMPANY_ID"), 
			   				   				   Category.getCategoryTitle(rs.getString("NAME")),
			   				   				   rs.getString("TITLE"),
			   				   				   rs.getString("DESCRIPTION"),
			   				   				   rs.getDate("START_DATE"),
			   				   				   rs.getDate("END_DATE"),
			   				   				   rs.getInt("AMOUNT"),
			   				   				   rs.getDouble("PRICE"),
			   				   				   rs.getString("IMAGE"));
					coupons.add(coupon);
				}
			}
			else
			{
				return null;
			}
		}
		catch(SQLException e) 
		{
			throw new DBCompanyOperationException("Could not retrieve companies list: "
													+e.getMessage(), 
													"error while retrieving company: "
													+company.getName());
		}
		finally {pool.restoreConnection(connection);}
		company.setCoupons(coupons);
		return company;
	}
	
	/**
	 * Method retrieves company by its name.
	 * @param name - company name to search for.
	 * @return Company object.
	 * @throws DBOperationException in case of database error while retrieving company.
	 */
	public Company getCompanyByName(String name) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT * FROM COMPANIES WHERE NAME = ?";
		ResultSet rs=null;
		Company company=null;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, name);
			rs = ps.executeQuery();
			if(rs.next())
				company = new Company (rs.getInt("ID"), 
						   			   rs.getString("NAME"), 
						   			   rs.getString("EMAIL"), 
						   			   rs.getString("PASSWORD"),
						   			   new ArrayList<Coupon>(0));			
		}
		catch(SQLException e) 
		{
			throw new DBCompanyOperationException("Can not look for company: "+e.getMessage(), 
												  "company name: "+name);
		}
		finally {pool.restoreConnection(connection);}
		return company;
	}
	
	/**
	 * Method retrieves company by its email.
	 * @param email - company email to search for.
	 * @return Company object.
	 * @throws DBOperationException in case of database error while retrieving company.
	 */
	public Company getCompanyByEmail(String email) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT * FROM COMPANIES WHERE EMAIL = ?";
		ResultSet rs=null;
		Company company=null;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();
			if(rs.next())
				company = new Company (rs.getInt("ID"), 
						   			   rs.getString("NAME"), 
						   			   rs.getString("EMAIL"), 
						   			   rs.getString("PASSWORD"),
						   			   new ArrayList<Coupon>(0));		
		}
		catch(SQLException e) 
		{
			throw new DBCompanyOperationException("Can not look for company: "+e.getMessage(), 
												  "company email: "+email);
		}
		finally {pool.restoreConnection(connection);}
		return company;
	}
}
