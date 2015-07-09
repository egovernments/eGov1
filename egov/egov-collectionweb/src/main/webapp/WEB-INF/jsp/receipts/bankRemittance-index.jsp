
<!-- eGov suite of products aim to improve the internal efficiency,transparency, 
    accountability and the service delivery of the government  organizations.
 
     Copyright (C) <2015>  eGovernments Foundation
 
     The updated version of eGov suite of products as by eGovernments Foundation 
     is available at http://www.egovernments.org
 
     This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     any later version.
 
     This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.
 
     You should have received a copy of the GNU General Public License
     along with this program. If not, see http://www.gnu.org/licenses/ or 
     http://www.gnu.org/licenses/gpl.html .
 
     In addition to the terms of the GPL license to be adhered to in using this
     program, the following additional terms are to be complied with:
 
 	1) All versions of this program, verbatim or modified must carry this 
 	   Legal Notice.
 
 	2) Any misrepresentation of the origin of the material is prohibited. It 
 	   is required that all modified versions of this material be marked in 
 	   reasonable ways as different from the original version.
 
 	3) This license does not grant any rights to any user of the program 
 	   with regards to rights under trademark law for use of the trade names 
 	   or trademarks of eGovernments Foundation.
 
   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
-->

<%@ include file="/includes/taglibs.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><s:text name="bankRemittance.title"/></title>
</head>
<body >
<s:form theme="simple" name="bankRemittanceForm" action="bankRemittance">


<table width="100%" cellpadding="0" cellspacing="0" border="0" class="main" align="center">
<tr>
<td class="mainheading" colspan="6" align="center"><s:text name="bankremittance.confirmation"/><br/></td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
	<td align="center"><table width="100%" border="0" cellpadding="0" cellspacing="0" class="tablebottom">
		<tr>
			<th class="bluebgheadtd" width="20%" ><s:text name="bankremittance.vouchernumber"/></th>
			<th class="bluebgheadtd" width="20%" ><s:text name="bankremittance.voucherdate"/></th>
			<th class="bluebgheadtd" width="20%" ><s:text name="bankremittance.vouchertype"/></th>
			<th class="bluebgheadtd" width="20%" ><s:text name="bankremittance.fund"/></th>
			<th class="bluebgheadtd" width="20%" ><s:text name="bankremittance.department"/></th>
		</tr>
		<s:iterator value="%{voucherHeaderValues}"> 
		<tr>
			<td class="blueborderfortd"><div align="center"><s:property value="%{voucherNumber}" /></div></td>
			<td class="blueborderfortd"><div align="center"><s:date name="voucherDate" var="cdFormat" format="dd/MM/yyyy"/><s:property value="%{cdFormat}" /></div></td>
			<td class="blueborderfortd"><div align="center"><s:property value="%{name}" /></div></td>
			<td class="blueborderfortd"><div align="center"><s:property value="%{fundId.name}" /></div></td>
			<td class="blueborderfortd"><div align="center"><s:property value="%{vouchermis.departmentid.deptName}" /></div></td>	
		</s:iterator>
	</table></td>
</table>
<br/>
<div class="buttonbottom">
<input name="button2" type="button" class="button" id="button" onclick="window.close()" value="Close"/>
</div>
</s:form>
</body>
</html>
