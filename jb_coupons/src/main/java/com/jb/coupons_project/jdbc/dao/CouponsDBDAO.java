package com.jb.coupons_project.jdbc.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBCouponOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Category;
import com.jb.coupons_project.java_beans.Coupon;
import com.jb.coupons_project.jbbc.ConnectionPool;

public class CouponsDBDAO implements CouponsDAO
{	
	private ConnectionPool pool;
	
	/**
	 * Constructor.
	 * @throws DBOperationException in case of database error.
	 */
	public CouponsDBDAO () throws DBOperationException
	{
		super();
		pool = ConnectionPool.getInstance();
	}
	
	/**
	 * Method adds new coupon to database.
	 * @param Coupon object that represents new coupon to add.
	 * @throws DBOperationException in case of database error while adding coupon.
	 */
	public void addCoupon(Coupon coupon) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "INSERT INTO COUPONS ("
						+ "COMPANY_ID, "
						+ "CATEGORY_ID, "
						+ "TITLE, "
						+ "DESCRIPTION, "
						+ "START_DATE, "
						+ "END_DATE, "
						+ "AMOUNT, "
						+ "PRICE, "
						+ "IMAGE) VALUES "
						+ "(?, (SELECT ID FROM CATEGORIES WHERE NAME = ?), ?, ?, ?, ?, ?, ?, ?)";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, coupon.getCompanyID());
			ps.setString(2, coupon.getCategory().getCategoryDescription());
			ps.setString(3, coupon.getTitle());
			ps.setString(4, coupon.getDecription());
			ps.setDate(5, coupon.getStartDate());
			ps.setDate(6, coupon.getEndDate());
			ps.setInt(7, coupon.getAmount());
			ps.setDouble(8, coupon.getPrice());
			ps.setString(9, coupon.getImage());
			ps.executeUpdate();
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not add coupon"+e.getMessage(), 
								"coupon id: "+coupon.getId());
		}
		finally {pool.restoreConnection(connection);}
	}
	
	/**
	 * Method updates coupon in database
	 * @param Coupon object that represents updated coupon.
	 * @throws DBOperationException in case of database error while updating coupon.
	 */
	public void updateCoupon(Coupon coupon) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "UPDATE COUPONS SET "
				+ "COMPANY_ID = ?, "
				+ "CATEGORY_ID = (SELECT ID FROM CATEGORIES WHERE NAME = ?), "
				+ "TITLE = ?, "
				+ "DESCRIPTION = ?, "
				+ "START_DATE = ?, "
				+ "END_DATE = ?, "
				+ "AMOUNT = ?, "
				+ "PRICE = ?, "
				+ "IMAGE = ?  WHERE ID = ?;";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, coupon.getCompanyID());
			ps.setString(2, coupon.getCategory().getCategoryDescription());
			ps.setString(3, coupon.getTitle());
			ps.setString(4, coupon.getDecription());
			ps.setDate(5, coupon.getStartDate());
			ps.setDate(6, coupon.getEndDate());
			ps.setInt(7, coupon.getAmount());
			ps.setDouble(8, coupon.getPrice());
			ps.setString(9, coupon.getImage());
			ps.setInt(10, coupon.getId());
			ps.executeUpdate();
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not update coupon"+e.getMessage(), 
								"coupon id: "+coupon.getId());
		}
		finally {pool.restoreConnection(connection);}
	}
	
	/**
	 * Method deletes coupon from database
	 * @param couponID - integer that represents id of coupon to delete
	 * @throws DBOperationException in case of database error while deleting coupon.
	 */
	public void deleteCoupon(int couponID) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		try
		{
			String query = "DELETE FROM COUPONS WHERE ID = ?";
			ps = connection.prepareStatement(query);
			ps.setInt(1, couponID);
			if(ps.executeUpdate() == 0)
				throw new DBCouponOperationException("Error: coupon was not deleted");
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not delete coupon: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
	}
	
	/**
	 * Method retrieves all coupons from database.
	 * @return array list of Coupon objects that represent coupons .
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getAllCoupons() throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		Statement stmt = null;
		String query = "SELECT T1.ID, "
					 		+ "COMPANY_ID, "
					 		+ "TITLE, "
					 		+ "DESCRIPTION, "
					 		+ "START_DATE, "
					 		+ "END_DATE, "
					 		+ "AMOUNT, "
					 		+ "PRICE, "
					 		+ "IMAGE, "
					 		+ "NAME "
					 	+ "FROM COUPONS T1 "
					 	+ "INNER JOIN CATEGORIES T2 "
					 		+ "ON T1.CATEGORY_ID = T2.ID;";
		ResultSet rs=null;
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		
		try
		{
			stmt=connection.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				coupons.add(new Coupon(rs.getInt("ID"), 
									   rs.getInt("COMPANY_ID"), 
									   Category.getCategoryTitle(rs.getString("NAME")),
									   rs.getString("TITLE"), 
									   rs.getString("DESCRIPTION"),
									   rs.getDate("START_DATE"),
									   rs.getDate("END_DATE"),
									   rs.getInt("AMOUNT"),
									   rs.getDouble("PRICE"),
									   rs.getString("IMAGE")
									   ));
			}
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not retrieve coupons: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return coupons;
	}
	
	/**
	 * Method retrieves all coupons of company by company id
	 * @param int - company id 
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCouponsByCompany(int companyID) throws DBOperationException
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT T1.ID, "
							+ "COMPANY_ID, "
							+ "TITLE, "
							+ "DESCRIPTION, "
							+ "START_DATE, "
							+ "END_DATE, "
							+ "AMOUNT, "
							+ "PRICE, "
							+ "IMAGE, "
							+ "NAME "
						+ "FROM COUPONS T1 "
						+ "INNER JOIN CATEGORIES T2 "
						+ "ON T1.CATEGORY_ID = T2.ID WHERE COMPANY_ID = ?;";
		ResultSet rs=null;
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, companyID);
			rs = ps.executeQuery();
			while(rs.next())
			{
				coupons.add(new Coupon(rs.getInt("ID"), 
									   rs.getInt("COMPANY_ID"), 
									   Category.getCategoryTitle(rs.getString("NAME")),
									   rs.getString("TITLE"), 
									   rs.getString("DESCRIPTION"),
									   rs.getDate("START_DATE"),
									   rs.getDate("END_DATE"),
									   rs.getInt("AMOUNT"),
									   rs.getDouble("PRICE"),
									   rs.getString("IMAGE")
									   ));
			}
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not retrieve coupons: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return coupons;
	}
	
	/**
	 * Method retrieves all coupons of company in certain category
	 * @param int - company id, Category - a category to filter by
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCouponsByCompany(int companyID, Category category) 
						throws DBOperationException
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT T1.ID, "
							+ "COMPANY_ID, "
							+ "TITLE, "
							+ "DESCRIPTION, "
							+ "START_DATE, "
							+ "END_DATE, "
							+ "AMOUNT, "
							+ "PRICE, "
							+ "IMAGE, "
							+ "NAME "
						+ "FROM COUPONS T1 "
						+ "INNER JOIN CATEGORIES T2 "
						+ "ON T1.CATEGORY_ID = T2.ID WHERE T1.COMPANY_ID = ? AND T2.NAME = ?;";
		ResultSet rs=null;
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, companyID);
			ps.setString(2, category.getCategoryDescription());
			rs = ps.executeQuery();
			while(rs.next())
			{
				coupons.add(new Coupon(rs.getInt("ID"), 
									   rs.getInt("COMPANY_ID"), 
									   Category.getCategoryTitle(rs.getString("NAME")),
									   rs.getString("TITLE"), 
									   rs.getString("DESCRIPTION"),
									   rs.getDate("START_DATE"),
									   rs.getDate("END_DATE"),
									   rs.getInt("AMOUNT"),
									   rs.getDouble("PRICE"),
									   rs.getString("IMAGE")
									   ));
			}
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not retrieve coupons: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return coupons;
	}
	
	/**
	 * Method retrieves all coupons of company which are lower than certain price.
	 * @param int - company id, double upper price bound.
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCouponsByCompany(int companyID, double maxPrice) 
						throws DBOperationException
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT T1.ID, "
							+ "COMPANY_ID, "
							+ "TITLE, "
							+ "DESCRIPTION, "
							+ "START_DATE, "
							+ "END_DATE, "
							+ "AMOUNT, "
							+ "PRICE, "
							+ "IMAGE, "
							+ "NAME "
						+ "FROM COUPONS T1 "
						+ "INNER JOIN CATEGORIES T2 "
						+ "ON T1.CATEGORY_ID = T2.ID WHERE T1.COMPANY_ID = ? AND T1.PRICE <= ?;";
		ResultSet rs=null;
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, companyID);
			ps.setDouble(2, maxPrice);
			rs = ps.executeQuery();
			while(rs.next())
			{
				coupons.add(new Coupon(rs.getInt("ID"), 
									   rs.getInt("COMPANY_ID"), 
									   Category.getCategoryTitle(rs.getString("NAME")),
									   rs.getString("TITLE"), 
									   rs.getString("DESCRIPTION"),
									   rs.getDate("START_DATE"),
									   rs.getDate("END_DATE"),
									   rs.getInt("AMOUNT"),
									   rs.getDouble("PRICE"),
									   rs.getString("IMAGE")
									   ));
			}
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not retrieve coupons: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return coupons;
	}
	
	/**
	 * Method retrieves all coupons of customer whose id provided.
	 * @param int - customer id.
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCouponsByCustomer(int customerID) throws DBOperationException
	{
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT T1.ID, "
							+ "T1.COMPANY_ID, "
							+ "T1.TITLE, "
							+ "T1.DESCRIPTION, "
							+ "T1.START_DATE, "
							+ "T1.END_DATE, "
							+ "T1.AMOUNT, "
							+ "T1.PRICE, "
							+ "T1.IMAGE, "
							+ "T3.NAME "
					+ "FROM "
							+ "COUPONS T1 INNER JOIN CUSTOMERS_VS_COUPONS T2 "
							+ "ON T1.ID = T2.COUPON_ID "
					+ "INNER JOIN CATEGORIES T3 ON T1.CATEGORY_ID = T3.ID "
					+ "WHERE T2.CUSTOMER_ID = ?;";
		ResultSet rs=null;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, customerID);
			rs = ps.executeQuery();
			while(rs.next())
			{
				coupons.add(new Coupon(rs.getInt("ID"), 
									   rs.getInt("COMPANY_ID"), 
									   Category.getCategoryTitle(rs.getString("NAME")),
									   rs.getString("TITLE"), 
									   rs.getString("DESCRIPTION"),
									   rs.getDate("START_DATE"),
									   rs.getDate("END_DATE"),
									   rs.getInt("AMOUNT"),
									   rs.getDouble("PRICE"),
									   rs.getString("IMAGE")
									   ));
			}
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not retrieve coupons: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return coupons;
	}
	
	/**
	 * Method retrieves all coupons in given category, 
	 * which belong to customer whose id provided.
	 * @param int - customer id, Category - category to filter by.
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while  retrieving coupons.
	 */
	public ArrayList<Coupon> getCouponsByCustomer(int customerID, Category category) 
						throws DBOperationException
	{
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT T1.ID, "
							+ "T1.COMPANY_ID, "
							+ "T1.TITLE, "
							+ "T1.DESCRIPTION, "
							+ "T1.START_DATE, "
							+ "T1.END_DATE, "
							+ "T1.AMOUNT, "
							+ "T1.PRICE, "
							+ "T1.IMAGE, "
							+ "T3.NAME "
					+ "FROM "
							+ "COUPONS T1 INNER JOIN CUSTOMERS_VS_COUPONS T2 "
							+ "ON T1.ID = T2.COUPON_ID "
					+ "INNER JOIN CATEGORIES T3 ON T1.CATEGORY_ID = T3.ID "
					+ "WHERE T2.CUSTOMER_ID = ? "
							+ "AND T3.NAME = ?;";
		ResultSet rs=null;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, customerID);
			ps.setString(2, category.getCategoryDescription());
			rs = ps.executeQuery();
			while(rs.next())
			{
				coupons.add(new Coupon(rs.getInt("ID"), 
									   rs.getInt("COMPANY_ID"), 
									   Category.getCategoryTitle(rs.getString("NAME")),
									   rs.getString("TITLE"), 
									   rs.getString("DESCRIPTION"),
									   rs.getDate("START_DATE"),
									   rs.getDate("END_DATE"),
									   rs.getInt("AMOUNT"),
									   rs.getDouble("PRICE"),
									   rs.getString("IMAGE")
									   ));
			}
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not retrieve coupons: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return coupons;
	}
	
	/**
	 * Method retrieves all coupons which are below maximum price 
	 * and belong to certain customer.
	 * @param int - customer id, double - upper price bound.
	 * @return array list of Coupon objects that represent coupons 
	 * @throws DBOperationException in case of database error while retrieving coupons.
	 */
	public ArrayList<Coupon> getCouponsByCustomer(int customerID, double maxPrice) 
			throws DBOperationException
	{
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT T1.ID, "
							+ "T1.COMPANY_ID, "
							+ "T1.TITLE, "
							+ "T1.DESCRIPTION, "
							+ "T1.START_DATE, "
							+ "T1.END_DATE, "
							+ "T1.AMOUNT, "
							+ "T1.PRICE, "
							+ "T1.IMAGE, "
							+ "T3.NAME "
					+ "FROM "
							+ "COUPONS T1 INNER JOIN CUSTOMERS_VS_COUPONS T2 "
							+ "ON T1.ID = T2.COUPON_ID "
					+ "INNER JOIN CATEGORIES T3 ON T1.CATEGORY_ID = T3.ID "
					+ "WHERE T2.CUSTOMER_ID = ? "
							+ "AND T1.PRICE <= ?;";
		ResultSet rs=null;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, customerID);
			ps.setDouble(2, maxPrice);
			rs = ps.executeQuery();
			while(rs.next())
			{
				coupons.add(new Coupon(rs.getInt("ID"), 
									   rs.getInt("COMPANY_ID"), 
									   Category.getCategoryTitle(rs.getString("NAME")),
									   rs.getString("TITLE"), 
									   rs.getString("DESCRIPTION"),
									   rs.getDate("START_DATE"),
									   rs.getDate("END_DATE"),
									   rs.getInt("AMOUNT"),
									   rs.getDouble("PRICE"),
									   rs.getString("IMAGE")
									   ));
			}
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not retrieve coupons: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return coupons;
	}
	
	/**
	 * Method retrieves one coupons from database by coupon id 
	 * @param coupon id to search for
	 * @return Coupon object if found, or null if such object was not found in database.
	 * @throws DBOperationException in case of database error while retrieving coupon.
	 */
	public Coupon getOneCoupon(int couponID) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT T1.ID, "
							+ "COMPANY_ID, "
							+ "TITLE, "
							+ "DESCRIPTION, "
							+ "START_DATE, "
							+ "END_DATE, "
							+ "AMOUNT, "
							+ "PRICE, "
							+ "IMAGE, "
							+ "NAME "
					+ "FROM COUPONS T1 "
					+ "INNER JOIN CATEGORIES T2 "
					+ "ON T1.CATEGORY_ID = T2.ID "
					+ "WHERE T1.ID = ?;";
		ResultSet rs=null;
		Coupon coupon=null;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, couponID);
			rs = ps.executeQuery();
			if(rs.next())
				coupon = new Coupon(rs.getInt("ID"), 
						   			rs.getInt("COMPANY_ID"), 
						   			Category.getCategoryTitle(rs.getString("NAME")),
						   			rs.getString("TITLE"), 
						   			rs.getString("DESCRIPTION"),
						   			rs.getDate("START_DATE"),
						   			rs.getDate("END_DATE"),
						   			rs.getInt("AMOUNT"),
						   			rs.getDouble("PRICE"),
						   			rs.getString("IMAGE")
						   			);
			else
				return null;
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not retrieve coupon: "+e.getMessage(), 
													"coupon id: "+couponID);
		}
		finally {pool.restoreConnection(connection);}
		return coupon;
	}
	
	/**
	 * Method checks if coupon is purchased by customer.
	 * @param int - customer id, int - coupon id
	 * @return true is this coupon is purchased by this customer, or false otherwise.
	 * @throws DBOperationException in case of database error.
	 */
	public boolean isPurchased(int customerID, int couponID) throws DBOperationException
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT * FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ? AND COUPON_ID = ?;";
		ResultSet rs=null;
		boolean purchased = false;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, customerID);
			ps.setInt(2, couponID);
			rs = ps.executeQuery();
			if(rs.next())
				purchased = true;
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Can not verify purchase: "+e.getMessage(), 
								"coupon id: "+couponID);
		}
		finally {pool.restoreConnection(connection);}
		return purchased;
	}
	
	/**
	 * Method performs coupon purchase.
	 * @param customer id for whom to make purchase, coupon id to purchase.
	 * @throws DBOperationException in case of database error.
	 */
	public void addCouponPurchase(int customerID, int couponID) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String insertRecord = "INSERT INTO CUSTOMERS_VS_COUPONS (CUSTOMER_ID, COUPON_ID) VALUES (?, ?);";
		String updateCoupons = "UPDATE COUPONS SET AMOUNT = (("
				+ "SELECT AMOUNT FROM (SELECT * FROM COUPONS) AS TEMP_COUPONS "
				+ "WHERE ID = ?) - 1) WHERE ID = ?;";
		try
		{
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(insertRecord);
			ps.setInt(1, customerID);
			ps.setInt(2, couponID);
			if(ps.executeUpdate() != 0)
			{
				ps = connection.prepareStatement(updateCoupons);
				ps.setInt(1, couponID);
				ps.setInt(2, couponID);
				ps.executeUpdate();
			}
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
				throw new DBCouponOperationException("Can not complete coupon purchase: "+
															e1.getMessage(), 
													 "coupon id: "+couponID);
			}
			throw new DBCouponOperationException("Can not complete coupon purchase: "+
														e.getMessage(), 
												 "coupon id: "+couponID);
		}
		finally 
		{
			try 
			{
				connection.setAutoCommit(true);
			} 
			catch (SQLException e) 
			{
				throw new DBCouponOperationException("Can not complete coupon purchase: "+
															e.getMessage(), 
													 "coupon id: "+couponID);
			}
			pool.restoreConnection(connection);
		}
	}
	
	/**
	 * Method deletes coupon purchase (does not delete coupon itself).
	 * @param customer id for whom to delete purchase, coupon id - coupon to delete purchase to.
	 * @throws DBOperationException in case of database error.
	 */
	public void deleteCouponPurchase(int customerID, int couponID) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String deleteRecord = "DELETE FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ? AND COUPON_ID = ?;";
		String updateCoupons = "UPDATE COUPONS SET AMOUNT = (("
				+ "SELECT AMOUNT FROM (SELECT * FROM COUPONS) AS TEMP_COUPONS "
				+ "WHERE ID = ?) + 1) WHERE ID = ?;";
		try
		{
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(deleteRecord);
			ps.setInt(1, customerID);
			ps.setInt(2, couponID);
			if(ps.executeUpdate() != 0)
			{
				ps = connection.prepareStatement(updateCoupons);
				ps.setInt(1, couponID);
				ps.setInt(2, couponID);
				ps.executeUpdate();
			}
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
//				e1.printStackTrace();
				throw new DBCouponOperationException("Can not delete coupon purchase: "+
															e.getMessage(), 
													 "coupon id: "+couponID);
			}
			e.printStackTrace();
			throw new DBCouponOperationException("Can not delete coupon purchase: "+
														e.getMessage(), 
												 "coupon id: "+couponID);
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
				throw new DBCouponOperationException("Can not delete coupon purchase"+
															e.getMessage(), 
													 "coupon id: "+couponID);
			}
			pool.restoreConnection(connection);
		}
	}

	/**
	 * Method deletes coupons older than provided date.
	 * @param date.
	 * @return number of coupons deleted. 
	 * @throws DBOperationException in case of database error while deleting coupons.
	 */
	public int deleteCouponsOlderThan(Date currentDate) throws DBOperationException 
	{
		int numCouponsDeleted = -1;
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
//		String query = "DELETE "
//					 + "FROM "
//					 		+ "COUPONS COUP "
//					 	+ "LEFT OUTER JOIN CUSTOMERS_VS_COUPONS CVSC "
//					 		+ "ON COUP.ID = CVSC.COUPON_ID "
//					 + "WHERE COUP.DATE > ?;";
		String query = "DELETE FROM COUPONS WHERE END_DATE < ?;";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setDate(1, currentDate);
			numCouponsDeleted = ps.executeUpdate();
		}
		catch(SQLException e) 
		{
			throw new DBCouponOperationException("Problem while deleting expired coupons: "
											+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return numCouponsDeleted;
	}
}
