#-------------------------------------------------------------------------------
# /*******************************************************************************
#  * eGov suite of products aim to improve the internal efficiency,transparency,
#  *    accountability and the service delivery of the government  organizations.
#  *
#  *     Copyright (C) <2015>  eGovernments Foundation
#  *
#  *     The updated version of eGov suite of products as by eGovernments Foundation
#  *     is available at http://www.egovernments.org
#  *
#  *     This program is free software: you can redistribute it and/or modify
#  *     it under the terms of the GNU General Public License as published by
#  *     the Free Software Foundation, either version 3 of the License, or
#  *     any later version.
#  *
#  *     This program is distributed in the hope that it will be useful,
#  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
#  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  *     GNU General Public License for more details.
#  *
#  *     You should have received a copy of the GNU General Public License
#  *     along with this program. If not, see http://www.gnu.org/licenses/ or
#  *     http://www.gnu.org/licenses/gpl.html .
#  *
#  *     In addition to the terms of the GPL license to be adhered to in using this
#  *     program, the following additional terms are to be complied with:
#  *
#  * 	1) All versions of this program, verbatim or modified must carry this
#  * 	   Legal Notice.
#  *
#  * 	2) Any misrepresentation of the origin of the material is prohibited. It
#  * 	   is required that all modified versions of this material be marked in
#  * 	   reasonable ways as different from the original version.
#  *
#  * 	3) This license does not grant any rights to any user of the program
#  * 	   with regards to rights under trademark law for use of the trade names
#  * 	   or trademarks of eGovernments Foundation.
#  *
#  *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#  ******************************************************************************/
#-------------------------------------------------------------------------------
<%@ tag body-content="empty"  isELIgnored="false" pageEncoding="UTF-8" description="To show Bread Crumb for each and every Screen" %>
<%@tag import="org.egov.infra.admin.master.entity.Module"%>
<%@tag import="org.egov.infra.utils.EgovThreadLocals" %>
<%@tag import="org.egov.lib.rrbac.model.Action"%>
<%@tag import="java.util.Date"%>
<%@tag import="org.egov.lib.rrbac.dao.ActionHibernateDAO"%>
<%@tag import="org.egov.infstr.utils.HibernateUtil"%>
<%@tag import="org.egov.infstr.services.SessionFactory"%>
<%
String contextPath  = request.getContextPath().replace("/","");
String applicationName = null;
try { applicationName = application.getContext("/egi").getAttribute(contextPath).toString();} catch (Exception e) {applicationName = "";}
/*
String requestURI   = request.getRequestURI();
String requestParam = request.getQueryString();
ActionHibernateDAO actionHibernateDAO = new ActionHibernateDAO(org.egov.lib.rrbac.model.Action.class,HibernateUtil.getCurrentSession());
Action action = actionHibernateDAO.findActionByURL(contextPath, requestURI.replaceFirst(contextPath,"")+(requestParam != null && !requestParam.trim().equals("") ? "?"+requestParam : ""));
StringBuffer breadCrumb = new StringBuffer();
String appName = "";
if (action != null) {
	Module module = null;
	module = action.getModule();
	if (module != null) {
		Module parent  = module.getParent();
		while (parent != null) {
			breadCrumb.append(parent.getModuleDescription()).append("<span class='commonbcarrow'> &gt; </span>");
			appName = parent.getModuleDescription();
			parent = parent.getParent();
		}
		breadCrumb.append(module.getModuleDescription()).append("<span class='commonbcarrow'> &gt; </span>");
	}
	breadCrumb.append(action.getDisplayName());
	session.setAttribute(contextPath,appName);
} 
*/
%>
<%="<div class=\"commontopyellowbg\">"%><%=applicationName == null ? "" : applicationName%><%="</div>"%>
<%="<div class=\"commontopbluebg\"><div class=\"commontopdate\">Today is: <span class=\"bold\" style=\"color:black\">"%>
<%=new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date())%>
<%="</span></div>Welcome <span class=\"bold\" style=\"color:#cccccc\">"%>
<%=session.getAttribute("com.egov.user.LoginUserName")%><%="</span></div>"%>
<%="<div class=\"commontopbreadc\" id=\"breadcrumb\">"%>&nbsp;<!--%=breadCrumb%> --><%="</div>"%>
<!--  script>
	if (document.getElementById('breadcrumb').innerHTML == '') {
		document.getElementById('breadcrumb').innerHTML = '<%=session.getAttribute(request.getContextPath()) == null ? "" : session.getAttribute(request.getContextPath())%>'+  " > "+document.title;
	}
</script -->
	
 
