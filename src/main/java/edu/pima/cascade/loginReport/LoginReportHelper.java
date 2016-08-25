package edu.pima.cascade.loginReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***************************************
 * Helper for the loginReport JSP.
 * For this audience, prefer to show blank results instead of any error
 * so if problem occurs, log it, absorb any ex, and return blank results.
 * @author leinecker
 *
 */

public class LoginReportHelper 
{
	public static final String BLOCKED_USERNAME = "* USERNAME BLOCKED *";
	public static final int DEFAULT_NUM_DAYS = 7;
	
	private static final Logger log = LoggerFactory.getLogger(LoginReportHelper.class);
	
	protected static DataSource ds = null;
	static
	{
		try
		{
			Context initCtx = new InitialContext();
			Context envCtx = (Context)initCtx.lookup("java:comp/env");
			ds = (DataSource)envCtx.lookup("jdbc/CascadeDS");
		}
		catch(Exception e)
		{
			log.error("Could not init datasource, expect future calls to fail", e);
		}
	}


	/*******************************************************************************
	 * Get the successful/failed login count, by user, for the last X days.
	 * Don't accept any literal SQL parms so we don't have to sanity check that.
	 * Intentionally absorbs exceptions if problem occurs.
	 * @param wantFails	True if you want to count login failures.  False to count successful logins
	 * @param days	You get results for the last &lt;days&gt; days
	 * @return	TreeMap of &lt;username, logincount&gt;
	 * @throws SQLException
	 */
	protected static TreeMap<String, Integer> loginQuery(boolean wantFails, int days) 
	{	
		log.debug("loginQuery with days: " + days + " and wantFails " + wantFails);
		TreeMap<String, Integer> logins = new TreeMap<String, Integer>();
		log.debug("found this many results)" + logins.size());
		
		if (days < 1)	// duh	
		{
			log.info("loginQuery attempted with silly number of days: " + days + " ...should be a positive integer.");
			return logins; 
		}	
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		// try-with-resources would be nice when we get to java 7
		try
		{
			conn = ds.getConnection();		
			String sql = "select username, count(username) from cxml_audit  "
					+ "where action like ? and "
					+ "tstamp > ((unix_timestamp(now()) - ? * 24 * 60 * 60 ) * 1000) "
					+ "group by username";
	          		
			ps = conn.prepareStatement(sql);
	        ps.setString(1, wantFails ? "login_failed" : "login");
	        ps.setInt(2, days);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				String user = rs.getString(1);
				int count = rs.getInt(2);
				
				if (!user.matches("^[\\w\\s\\@\\.\\-]*$"))
				{									
					log.info("an unsafe username was blocked from results.  You can find the original value in the cxml_audit table.  A sanitized version of the username, which may be different from the original, is: " + user.replaceAll("[^\\w]", "_"));
					user = BLOCKED_USERNAME;
				}
				logins.put(user,  count);
			}
		}
		catch(SQLException e)
		{
			log.error("Problem executing loginQuery.  Parms were wantFails: " + wantFails + ", days: " + days, e);
		}
		finally
		{			
			try
			{
				if (ps != null)	{ ps.close(); }
				if (conn != null)	{ conn.close(); }
			}
			catch (Exception e)
			{
				log.error("Ex when trying to cleanup from successful(?) query", e);
			}			
		}
		return logins;
	}
	
	/*************************************************************
	 * Turn a string into a boolean for if you want to report login fails
	 * If your input is garbage, get default result of false.
	 * @param boolString hopefully this is "true" or "false" 
	 * @return a boolean of whether or not to count failed logins
	 */
	public static boolean parseWantFails(String boolString)
	{
		boolean wantFails = false;
		try
		{
			wantFails = Boolean.parseBoolean(boolString);
		}
		catch(Exception e) {}
		
		return wantFails;
	}
	
	/******************************************************************
	 * Examines untrusted user input for a number of days.
	 * If value is legit, return it.
	 * Otherwise return a default value.
	 * Guaranteed to return a positive integer.
	 * @param dayString	Untrusted user input of number of days to report on, or null
	 * @return	Positive integer for number of days
	 */
	public static int parseDays(String dayString)
	{
		log.debug("parsing day string for: '" + dayString + "'");
		int days = -1;
		try
		{
			days = Integer.valueOf(dayString);
			log.debug("parsed to value: " + days);
		}
		catch (Exception e) 
		{
			log.debug("exception when parsing days", e);
		}
				
		if (days < 1)	
		{
			days = LoginReportHelper.DEFAULT_NUM_DAYS;
			log.debug("set bogus days to default value.");
		}
		
		return days;
	}
	
}
