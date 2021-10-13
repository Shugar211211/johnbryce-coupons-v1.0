package com.jb.coupons_project.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.jb.coupons_project.custom_exceptions.DBCustomerOperationException;
import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.java_beans.Customer;
import com.jb.coupons_project.jbbc.ConnectionPool;

public class CustomersDBDAO implements CustomersDAO 
{
	private ConnectionPool pool;
	
	/**
	 * Constructor.
	 * @throws DBOperationException in case of database error.
	 */
	public CustomersDBDAO() throws DBOperationException {
		super();
		this.pool = ConnectionPool.getInstance();
	}

	/**
	 * Method checks if customer with provided arguments email and password exists in database.
	 * @param customer email, customer password
	 * @return true if customer exists or false otherwise.
	 * @throws DBOperationException in case of database operating while searching for customer.
	 */
	public boolean isCustomerExists(String email, String password) 
						throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs;
		String query = "SELECT EMAIL, PASSWORD FROM CUSTOMERS WHERE EMAIL = ? AND PASSWORD = ?";
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
			throw new DBCustomerOperationException("Can not search for customer: " + e.getMessage(), 
											"customer email: "+email);
		}
		finally {pool.restoreConnection(connection);}
		return exists;
	}
	
	/**
	 * Method adds new company to database.
	 * @param company object that represents company to add.
	 * @throws DBOperationException in case of database error while adding new company.
	 */
	public void addCustomer(Customer customer) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?);";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, customer.getFirstName());
			ps.setString(2, customer.getLastName());
			ps.setString(3, customer.getEmail());
			ps.setString(4, customer.getPassword());
			if(ps.executeUpdate() == 0)
				throw new DBCustomerOperationException("Error: can not add customer");
		}
		catch(SQLException e) 
		{
			throw new DBCustomerOperationException("Can not add customer: "+e.getMessage(), 
											"customer email: "+customer.getEmail());
		}
		finally {pool.restoreConnection(connection);}
	}
	
	/**
	 * Method updates customer entry in database.
	 * @param customer object that represents updated customer.
	 * @throws DBOperationException in case of database error or invalid fields in customer object.
	 */
	public void updateCustomer(Customer customer) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
//		// update customer coupons first
//		this.updateCustomerCoupons(customer);
		String query = "UPDATE CUSTOMERS SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, PASSWORD = ? WHERE ID = ?;";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, customer.getFirstName());
			ps.setString(2, customer.getLastName());
			ps.setString(3, customer.getEmail());
			ps.setString(4, customer.getPassword());
			ps.setInt(5, customer.getId());
			ps.executeUpdate();
		}
		catch(SQLException e) 
		{
			throw new DBCustomerOperationException("Can not update customer: "+e.getMessage(), 
											"customer email: "+customer.getEmail());
		}
		finally {pool.restoreConnection(connection);}
	}
	
	/**
	 * Method deletes customer entry from database.
	 * @param customerID - id of customer to delete.
	 * @throws DBOperationException in case of database error while deleting customer. 
	 */
	public void deleteCustomer(int customerID) throws DBOperationException 
	{

		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		
		String couponUpdateQuery = "UPDATE COUPONS "
										+ "SET AMOUNT = "
										+ "AMOUNT + 1 "
										+ "WHERE ID "
				+ "IN (SELECT COUPON_ID FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ?);";
		
		String customersVsCouponsUpdate = "DELETE FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ?;";
		
		String customerDeleteQuery = "DELETE FROM CUSTOMERS WHERE ID = ?;";
		try
		{
			connection.setAutoCommit(false);
			
			ps = connection.prepareStatement(couponUpdateQuery);
			ps.setInt(1, customerID);
			ps.executeUpdate();
			
			ps = connection.prepareStatement(customersVsCouponsUpdate);
			ps.setInt(1, customerID);
			ps.executeUpdate();
			
			ps = connection.prepareStatement(customerDeleteQuery);
			ps.setInt(1, customerID);
			if(ps.executeUpdate() == 0)
				throw new DBCustomerOperationException("Error: customer was not deleted.");
		}
		catch(SQLException e) 
		{
			try 
			{
				connection.rollback();
			} 
			catch (SQLException e1) 
			{
				throw new DBCustomerOperationException("Can not delete customer: "+
															e1.getMessage(), 
													 "customer id: "+customerID);
			}
			throw new DBCustomerOperationException("Can not delete customer: "+e.getMessage(), 
								"cusomer id: "+customerID);
		}
		finally 
		{
			try 
			{
				connection.setAutoCommit(true);
			} 
			catch (SQLException e) 
			{
				throw new DBCustomerOperationException("Can not delete customer: "+
															e.getMessage(), 
													 "customer id: "+customerID);
			}
			pool.restoreConnection(connection);
		}
	}

	/**
	 * Method finds all customers from database.
	 * @return array list of customer objects that represents customers.
	 * @throws DBOperation exception in case of database error while looking for customers.
	 */
	public ArrayList<Customer> getAllCustomers() throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		Statement stmt = null;
		String query = "SELECT * FROM CUSTOMERS;";
		ResultSet rs=null;
		ArrayList<Customer> customers = new ArrayList<Customer>();
		CouponsDBDAO couponsDBDAO = new CouponsDBDAO();
		try
		{
			stmt=connection.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				customers.add(new Customer(rs.getInt("ID"), 
										   rs.getString("FIRST_NAME"), 
										   rs.getString("LAST_NAME"), 
										   rs.getString("EMAIL"), 
										   rs.getString("PASSWORD"), 
										   couponsDBDAO.getCouponsByCustomer(rs.getInt("ID"))));
			}
		}
		catch(SQLException e) 
		{
			throw new DBCustomerOperationException("Can not retrieve customers: "+e.getMessage());
		}
		finally {pool.restoreConnection(connection);}
		return customers;
	}
	
	/**
	 * Method retrieves customer by customer id.
	 * @param int - customer id to search for.
	 * @return Customer object.
	 * @throws DBOperationException in case of database error while searching for customer.
	 */
	public Customer getOneCustomer(int customerID) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT * FROM CUSTOMERS WHERE ID = ?";
		CouponsDBDAO couponsDBDAO = new CouponsDBDAO();
		ResultSet rs=null;
		Customer customer=null;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, customerID);
			rs = ps.executeQuery();
			if(rs.next())
				customer = new Customer (rs.getInt("ID"), 
						   			     rs.getString("FIRST_NAME"), 
						   			     rs.getString("LAST_NAME"), 
						   			     rs.getString("EMAIL"), 
						   			     rs.getString("PASSWORD"),
						   			     couponsDBDAO.getCouponsByCustomer(rs.getInt("ID")));	
			else
				return null;
		}
		catch(SQLException e) 
		{
			throw new DBCustomerOperationException("Can not search for customer: "+e.getMessage(),
											"customer id: "+customerID);
		}
		finally {pool.restoreConnection(connection);}
		return customer;
	}
	
	/**
	 * Method retrieves customer by email.
	 * @param email - customer email to search for.
	 * @return Customer object.
	 * @throws DBOperationException in case of database error while searching for customer.
	 */
	public Customer getOneCustomer(String email) throws DBOperationException 
	{
		Connection connection = pool.getConnection();
		PreparedStatement ps = null;
		String query = "SELECT * FROM CUSTOMERS WHERE EMAIL = ?";
		CouponsDBDAO couponsDBDAO = new CouponsDBDAO();
		ResultSet rs=null;
		Customer customer=null;
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();
			if(rs.next())
				customer = new Customer (rs.getInt("ID"), 
						   			     rs.getString("FIRST_NAME"), 
						   			     rs.getString("LAST_NAME"), 
						   			     rs.getString("EMAIL"), 
						   			     rs.getString("PASSWORD"),
						   			     couponsDBDAO.getCouponsByCustomer(rs.getInt("ID")));		
		}
		catch(SQLException e) 
		{
			throw new DBCustomerOperationException("Can not retrieve customer: "+e.getMessage(), 
											"customer email: "+email);
		}
		finally {pool.restoreConnection(connection);}
		return customer;
	}
}
