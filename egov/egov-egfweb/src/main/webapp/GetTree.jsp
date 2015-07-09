<!--  #-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#      accountability and the service delivery of the government  organizations.
#   
#       Copyright (C) <2015>  eGovernments Foundation
#   
#       The updated version of eGov suite of products as by eGovernments Foundation 
#       is available at http://www.egovernments.org
#   
#       This program is free software: you can redistribute it and/or modify
#       it under the terms of the GNU General Public License as published by
#       the Free Software Foundation, either version 3 of the License, or
#       any later version.
#   
#       This program is distributed in the hope that it will be useful,
#       but WITHOUT ANY WARRANTY; without even the implied warranty of
#       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#       GNU General Public License for more details.
#   
#       You should have received a copy of the GNU General Public License
#       along with this program. If not, see http://www.gnu.org/licenses/ or 
#       http://www.gnu.org/licenses/gpl.html .
#   
#       In addition to the terms of the GPL license to be adhered to in using this
#       program, the following additional terms are to be complied with:
#   
#   	1) All versions of this program, verbatim or modified must carry this 
#   	   Legal Notice.
#   
#   	2) Any misrepresentation of the origin of the material is prohibited. It 
#   	   is required that all modified versions of this material be marked in 
#   	   reasonable ways as different from the original version.
#   
#   	3) This license does not grant any rights to any user of the program 
#   	   with regards to rights under trademark law for use of the trade names 
#   	   or trademarks of eGovernments Foundation.
#   
#     In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#-------------------------------------------------------------------------------  -->
<%@page language = "java"
   import = "com.exilant.exility.common.DataCollection"
   import = "com.exilant.exility.pagemanager.*"
 %>
<%


	DataCollection dc = new DataCollection();
	SecurityGuard guard = new SecurityGuard();
	guard.clearedSecurity(dc, request);

	String treeName   = request.getParameter("treeName");
	String divName   = request.getParameter("divName");

	if (treeName == null)  
	{
		dc.addMessage("exilServerError", "Tree service not invoked with a treeName ");		
	} 
	else
	{
		dc.addValue("treeName",treeName);
		dc.addValue("divName",divName);
		ServiceAgent agent = ServiceAgent.getAgent();
		agent.deliverService("TreeService", dc);
	}
	PageMap map = new PageMap();
	String js = map.toJavaScript(dc);

%>
<HTML>
<head>
</head>
<body>
<script laguage="javascript">
<%= js %>
var win=window;
while (true){
	if(win.PageManager) break;
	if(win.opener) win = win.opener;
	else if(win.parent && win.parent != win) win = win.parent;
	else break;
}
if (!win.PageManager) alert ('View source to see what the server returned');
else win.PageManager.TreeService.displayTree(dc);
</script>
</body>
</html>
