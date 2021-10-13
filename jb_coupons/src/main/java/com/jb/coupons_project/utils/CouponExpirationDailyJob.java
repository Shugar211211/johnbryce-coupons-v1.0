package com.jb.coupons_project.utils;

import java.sql.Date;

import com.jb.coupons_project.custom_exceptions.DBOperationException;
import com.jb.coupons_project.jdbc.dao.CouponsDAO;

public class CouponExpirationDailyJob implements Runnable 
{

	private CouponsDAO couponsDAO;
	private String clientMsg;
	private boolean quit = false;
	
	public CouponExpirationDailyJob(CouponsDAO couponsDAO)
	{
		this.couponsDAO = couponsDAO;
		clientMsg = "";
	}
	
	/**
	 * This method returns client message.
	 * @return client message
	 */
	public String getClientMsg()
	{
		return this.clientMsg;
	}
	
	/**
	 * This method sets daily task stop flag to true.
	 */
	public synchronized void stopDailyJob()
	{
		this.quit = true;
	}
	
	/**
	 * This method is daily loop routine condition
	 * @return true if quit is set to true, which is flag to quit daily routine.
	 */
	private synchronized boolean keepRunning()
	{
		return this.quit == false;
	}
	
	/**
	 * This method runs daily routine once a day.
	 */
	public void run() // throws DBOperationException 
	{
		while( ! Thread.interrupted() && keepRunning() )
		{
			try 
			{
				Date currentDate = new Date(System.currentTimeMillis());
				this.clientMsg = "Cleaning coupons by date "+currentDate;
				System.out.println("--------------------------------------------------");
				System.out.println(clientMsg); // used for testing
				int numDeletedCoupons = couponsDAO.deleteCouponsOlderThan(currentDate);	
				this.clientMsg = "Done cleaning: "+numDeletedCoupons+" coupons deleted";
				System.out.println(clientMsg); // used for testing
				System.out.println("--------------------------------------------------");
				Thread.sleep(24L*60L*60L*1000L);
			} 
			catch (InterruptedException e) 
			{
				this.stopDailyJob();
				this.clientMsg = "Daily task stopped";
			}
			catch (DBOperationException e) 
			{
				this.stopDailyJob();
				this.clientMsg = "Daily task is terminated because of database error: "
								+ e.getMessage();
			}
		}
		System.out.println(clientMsg);  // used for testing
	}
}
