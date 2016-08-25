package edu.pima.cascade.loginReport;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class SafeDaysTag extends SimpleTagSupport {
	private String days;
	
	////////////////////////////////////////////////////
	@Override
	public void doTag() throws JspException, IOException
	{
		JspWriter out = getJspContext().getOut();
		out.print(LoginReportHelper.parseDays(days));
	}

	/////////////////////////////////////////////////////////
	
	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}	
}