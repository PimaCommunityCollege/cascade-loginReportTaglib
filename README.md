# Cascade login report support taglib

This taglib contains the cheesy goodness of the login report for Cascade Server.  There are 2 main parts:

*loginReportHelper* has a utility method parseDays(String shouldBeANumberOfDays).  It returns a integer which is the parsed number of days, or a sensible default if the input was bogus.

The *drawLogins* tag generates HTML showing usernames and the matching number of logins.  Parameters:   
* days:  the report will show the last X days of logins
* wantFailed:  do you want to see the failed logins (true) or the successful ones (false)?

## Examples

`<%@ taglib uri="http://pcc-logins" prefix="pcc" %>`   
`<%@ page import="edu.pima.cascade.loginReport.LoginReportHelper" %>`   
`<% int days = LoginReportHelper.parseDays(request.getParameter("days")); %>`   

…

`<h2>Failed Logins</h2>`   
`<pcc:drawLogins days="<%= days %>" wantFailed="true" />`   
`<h2>Successful Logins</h2>`   
`<pcc:drawLogins days="<%= days %>" wantFailed="false" />`   

## Installing the taglib

Build the JAR, or grab the binary from the bin folder. Put the file in:
[CASCADE_HOME]/tomcat/webapps/ROOT/WEB-INF/lib/

Restart the Cascade app to pick up the jar file.
