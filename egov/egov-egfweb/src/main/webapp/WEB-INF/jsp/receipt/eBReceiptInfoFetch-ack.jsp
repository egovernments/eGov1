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
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sx" uri="/WEB-INF/struts-dojo-tags.tld" %>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>
<s:text name="receiptInfoFetch.title"/> 
</title>
<sx:head/>
</head>
<script type="text/javascript" src="/EGF/resources/javascript/helper.js"></script>
<body> 
	<s:form name="receiptInfoFetchForm" id="receiptInfoFetchForm"
		action="eBReceiptInfoFetch" theme="css_xhtml"
		validate="true">
		<s:token />
		<div class="formmainbox"></div>
		<div class="formheading" />
		<div class="subheadnew">
			<s:text name="receiptInfoFetch.title" />
		</div>
		<br />
		<table align="center" width="100%" cellspacing="0">
			<tr>
				<td width="25%"></td>
				<td width="50%" colspan="2" style="text-align: center;">
					<s:property value="%{ackMessage}" />
				</td>
				<td width="25">					
				</td>

			</tr>
		</table>
		<div class="buttonbottom" align="center">
			<table border="0px" cellpadding="0" cellspacing="10"
				class="buttonbottom">
				<tr align="center">					
					<td style="padding: 0px"><input type="button" value="Close"
						onclick="javascript:window.close();" class="button" /></td>
				</tr>
			</table>
		</div>
		</div>
	</s:form>
	<script>
		paintAlternateColorForRows();
</body>
</html>
