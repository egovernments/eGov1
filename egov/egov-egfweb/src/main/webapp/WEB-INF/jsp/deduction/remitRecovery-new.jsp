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
<%@ page language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

<head>
<script type="text/javascript" src="/EGF/commonjs/ajaxCommonFunctions.js"></script>
<script type="text/javascript" src="/EGF/resources/javascript/calender.js"></script>
<script type="text/javascript" src="/EGF/resources/javascript/calendar.js" ></script>
<script type="text/javascript" src="/EGF/resources/javascript/dateValidation.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title> <s:text name="remit.recovery.create.title"/></title>
<script>
function loadBank(obj)
{

}
</script>
</head>
<body>
	<s:form action="remitRecovery" theme="simple" name="remitRecoveryForm" >
			<jsp:include page="../budget/budgetHeader.jsp">
        			<jsp:param name="heading" value='Remittance Recovery' />
			</jsp:include>
			
			<span class="mandatory">
			<font  style='color: red ; font-weight:bold '> 
				<s:actionerror/>  
				<s:fielderror />
				<s:actionmessage /></font>
			</span>
		<div class="formheading"/><div class="subheadnew"><s:text name="remit.recovery.new.title"/></div>
		<br/>
<div align="center">
<font  style='color: red ; font-weight:bold '> 
<p class="error-block" id="lblError" ></p></font>
    <table border="0" width="100%">
	<tr>
	<td class="greybox"></td>
	    <td class="greybox"><s:text name="remit.recovery.search.code"/><span class="mandatory">*</span></td>
	    <td class="greybox"><s:select name="remittanceBean.recoveryId" id="recoveryId" list="dropdownData.recoveryList" listKey="id" listValue="type" headerKey="-1" headerValue="----Choose----"  value="%{remittanceBean.recoveryId}"/></td>
		<td  class="greybox" width="10%">
		<td  class="greybox">
	   
	<tr>
		<td class="bluebox" ></td>
		<td  class="bluebox" > <s:text name="remit.recovery.search.fromdate" /></td>
		<td  class="bluebox" ><s:date name="remittanceBean.fromVhDate" id="fromVhDateId" format="dd/MM/yyyy" />
		<s:textfield name="remittanceBean.fromVhDate" id="fromVhDate" value="%{remittanceBean.fromVhDateId}"  maxlength="10" onkeyup="DateFormat(this,this.value,event,false,'3')"/>
		<a href="javascript:show_calendar('remitRecoveryForm.fromVhDate',null,null,'DD/MM/YYYY');" style="text-decoration:none">&nbsp;<img  src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
		
		<td  class="bluebox" ><s:text name="remit.recovery.search.todate" /><span class="mandatory">*</span></td>
		<td  class="bluebox">
		<s:date name="voucherDate" id="voucherDateId" format="dd/MM/yyyy"/>
		<s:textfield name="voucherDate" id="voucherDate" value="%{voucherDateId}"  maxlength="10" onkeyup="DateFormat(this,this.value,event,false,'3')"/>
		<a href="javascript:show_calendar('remitRecoveryForm.voucherDate',null,null,'DD/MM/YYYY');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
	</tr>
		
	</tr>
	<%@ include file="../voucher/vouchertrans-filter-new.jsp" %>
	
    </table>
	<jsp:include page="remitRecovery-form.jsp"/>
	<label style="text-align: right;" ></label>
	
<div class="buttonbottom" style="padding-bottom:10px;">
		<s:submit type="submit" cssClass="buttonsubmit" value="Search" id="search" name="search" method="search" onclick="return validateSearch();"/> 
		<s:submit type="submit" cssClass="buttonsubmit" value="Cancel"   method="newform"/>
		<input type="button" id="Close" value="Close" onclick="javascript:window.close()" class="button"/>
	</div>
<s:if test='%{listRemitBean != null }'>
<s:if test="%{ listRemitBean.size()>0}">
<div align="center">
	<font  style='color: red ; font-weight:bold '> 
	<p class="error-block" id="remitlblError" ></p></font>
</div>
	     <div id="labelAD" align="center">
	 		<table width="100%" border=0 id="recoveryDetails"><th>Recovery Details</th></table>
	</div>
	<table align="center" id="totalAmtTable">
		<tr >
			<td width="900"></td>
			<td><s:text name="remit.SelectDeSelectAll" /></td>
			<td ><s:checkbox id="selectAll" name="selectAll" onclick="selectAllORNone(this);"></s:checkbox>
			</td>
		</tr>
	</table>
	
	
	<div class="yui-skin-sam" align="center">
       <div id="recoveryDetailsTable"></div>
     </div>
     <script>
		
		populateRecoveryDetails();
		document.getElementById('recoveryDetailsTable').getElementsByTagName('table')[0].width="80%"
	 </script><br>
	<table align="center" id="totalAmtTable">
		<tr >
			<td width="773"></td>
			<td >Total Amount</td>
			<td ><s:textfield name="remittanceBean.totalAmount" id="totalAmount"  style="width:90px;text-align:right" readonly="true" value="0"/></td>
		</tr>
	</table>
	<s:hidden type="hidden" id="selectedrRemit" name="remittanceBean.selectedrRemit"/>
	<div class="buttonbottom" style="padding-bottom:10px;">
				<s:submit type="submit" cssClass="buttonsubmit" value="Generate Payment" id="genPayment" name="save&genPayment" method="remit" onclick="return validateRemit()"/>
</s:if>
<s:else>
<div class="error">
				<span class="bluebgheadtd" colspan="7"><s:text name="no.data.found"/></span>
</div>
</s:else>
</s:if>			
</div>
</s:form>
</body>
</html>
