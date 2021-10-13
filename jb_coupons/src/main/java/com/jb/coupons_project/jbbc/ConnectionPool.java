package com.jb.coupons_project.jbbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jb.coupons_project.custom_exceptions.DBOperationException;

public class ConnectionPool
{
	private static String url = "jdbc:mysql://localhost:3306/jb_coupons?serverTimezone=UTC";
	private static String user = "jbcp";
	private static String password = "admin";
	
	private Set<Connection> connections;
	private static final int POOL_SIZE=10;
	private static ConnectionPool instance = null;
	
	/**
	 * private constructor that creating ConnectonPool singleton
	 * and initializes set of connections.
	 * @throws DBOperationException 
	 */
	private ConnectionPool() throws DBOperationException
	{
		connections = new HashSet<Connection>();
		try
		{
			for(int i=0; i<POOL_SIZE; i++)
			{
				Connection connection = DriverManager.getConnection(url, user, password);
				connections.add(connection);
			}
		}
		catch (SQLException e)  
		{
//			e.printStackTrace();
//			System.out.println(e.getMessage());
			throw new DBOperationException("Problem while establishing connection to database: " 
					+ e.getMessage());
		}
	}
	
	/**
	 * public getter for the ConnectionPool instance
	 * @return ConnectionPool object (instance)
	 * @throws DBOperationException 
	 */
	public static ConnectionPool getInstance() throws DBOperationException
	{
		if (instance == null)
			instance = new ConnectionPool();
        return instance; 
	}
	
	/**
	 * method to get free connection from the pool if it has one, or wait otherwise
	 * @return Connection object - connection to database.
	 * @throws DBOperationException 
	 */
	public synchronized Connection getConnection() throws DBOperationException
	{
		Iterator<Connection> iterator = connections.iterator();
		
		if(connections.isEmpty()) 
		{	
			try 
			{
				wait();
			} 
			catch (InterruptedException e)  
			{
				throw new DBOperationException("Error connecting todatabase");
//				e.printStackTrace();
			}
		}
		Connection connection = iterator.next();
		connections.remove(connection);
		return connection;
	}
	
	/**
	 * method to return a used connection to the pool
	 * @param connection - connection that no longer needed 
	 * and should be returned back to the pool.
	 */
	public synchronized void restoreConnection(Connection connection)
	{
		connections.add(connection);
		notify();
	}
	
	/**
	 * method to close all connections in the pool.
	 * @throws DBOperationException 
	 */
	public void closeAllConnections() throws DBOperationException
	{
		Iterator<Connection> iterator = connections.iterator();
		while(iterator.hasNext())
		{
			Connection connection = iterator.next();
			try {
				connection.close();
			} 
			catch (SQLException e) 
			{
				throw new DBOperationException(e.getMessage());
//				e.printStackTrace();
			}
		}
	}
}
