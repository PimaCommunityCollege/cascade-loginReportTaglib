package edu.pima.cascade.loginReport;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DrawLoginsTag extends SimpleTagSupport {
	private int days = 0;
	private String wantFailed = "";

	////////////////////////////////////////////////////
	@Override
	public void doTag() throws JspException, IOException
	{
		boolean fail = LoginReportHelper.parseWantFails(wantFailed);
		
		TreeMap<String, Integer> logins = LoginReportHelper.loginQuery(fail, days);
		
		JspWriter out = getJspContext().getOut();
		
		out.println("<table>");
		out.print("<caption>");
		out.print((fail ? "Failed" : "Successful") + " logins for the last " + days + " days");
		out.println("</caption>");
		out.println("<thead>");
		out.println("<th>Username</th>");
		out.println("<th>" + (fail ? "Failures" : "Successes") + "</th>");
		out.println("</thead>");
		out.println("<tbody>");
		Iterator<String> iter = logins.keySet().iterator();
		if (iter.hasNext())
		{
			while (iter.hasNext())
			{
				String username = iter.next();
				int num = logins.get(username);
				
				// don't panic: the usernames are sanitized in LoginReportHelper
				out.println("<tr>");
				out.println("<td>" + username + "</td>");
				out.println("<td>" + num + "</td>");
				out.println("</tr>");
			}
		}
		else
		{
			out.println("<tr><td colspan=\"2\">No results found</td></tr>");
		}
		out.println("</tbody>");
		out.println("</table>");
	}
	

	
	////////////////////////////////////////////////////////
	
	public int getDays() {
		return days;
	}

	public void setDays(int d) {
		this.days = d;
	}

	public String getWantFailed() {
		return wantFailed;
	}

	public void setWantFailed(String wantFailed) {
		this.wantFailed = wantFailed;
	}		
}
