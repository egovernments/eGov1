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
<html>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<%@ taglib prefix="EGF" tagdir="/WEB-INF/tags"%>
<%@ page language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><s:text name="bankreconciliation"/></title>
<script type="text/javascript">


	function validate() {
		if (document.getElementById("bankId").value == "") {
			alert("Select Bank");
			return false;
		}
		if (document.getElementById("branchId").value == "") {
			alert("Select Branch");
			return false;
		}
		if (document.getElementById("accountId").value == "") {
			alert("Select Account");
			return false;
		}
		var toDateStr=document.getElementById("toDate").value;
		
		var reconDateStr =document.getElementById("reconciliationDate").value;
		if (reconDateStr == "") {
			alert("Select <s:text name='reconciliationdate'/>");
			return false;
		}
		if (document.getElementById("fromDate").value == "") {
			alert("Select <s:text name='fromdate'/>");
			return false;
		}
		if (toDateStr == "") {
			alert("Select <s:text name='todate'/>");
			return false;
		}
		
		if(toDateStr!=null && reconDateStr!=null)
		{
		
		var toDateParts=	toDateStr.split("/");
		if(toDateParts.length!=3)
		{
		alert("Enter date is 'DD/MM/YYYY' format only");
		return false;
		}
		var toDate=new Date(toDateParts[1]+"/"+toDateParts[0]+"/"+toDateParts[2]);
		var reconDateParts=	reconDateStr.split("/");
		
		if(reconDateParts.length!=3)
		{
		alert("Enter date is 'DD/MM/YYYY' format only");
		return false;
		}
		var reconDate=new Date(reconDateParts[1]+"/"+reconDateParts[0]+"/"+reconDateParts[2]);
		//alert(reconDate.toString('MM-dd-yyyy'));
		if(reconDate<toDate)
		{
		alert("<s:text name='reconciliationdate'/> must be higher or equal to <s:text name='todate'/>");
		return false;
		}
		}
		
		
		return true;
	}
	function populatebranch(obj) {
		var bid = document.getElementById("bankId").value;
		populatebranchId( {
			bankId : bid
		})
	}

	function populateaccount(obj) {
		var bid = document.getElementById("branchId").value;
		populateaccountId( {
			branchId : bid
		})
	}
</script>
</head>
<body>
<s:form  action="autoReconciliation" theme="simple" name="arform">
<jsp:include page="../budget/budgetHeader.jsp">
<jsp:param value="Auto Bank Reconciliation" name="heading"/>
</jsp:include>
<div class="formmainbox">
<div class="formheading"></div>
<div class="subheadnew"><s:text name="autobankreconciliation"/></div>
</div>
<div align="center">
<font  style='color: red ;'> 
<p class="error-block" id="lblError" ></p>
</font>
</div>
<span class="mandatory" >
				<div id="Errors" ><s:actionerror /><s:fielderror /></div>
				<s:actionmessage />
</span>
<center>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr>
		<td  class="greybox"></td>
		<td class="greybox"><s:text name="bank"/>
		<span class="greybox"><span class="mandatory">*</span></span></td>
		<egov:ajaxdropdown id="branchId" fields="['Text','Value']" dropdownId="branchId" url="/voucher/common!ajaxLoadBankBranchesByBank.action" />
		<td class="greybox"><s:select name="bankId" id="bankId" list="dropdownData.bankList" listKey="id" listValue="name" headerKey="" headerValue="----Choose----" onchange="populatebranch(this);" value="%{bankId}" /></td>
	 	<td class="greybox"><s:text name="bankbranch"/>
		<span class="greybox"><span class="mandatory">*</span></span></td>
		<egov:ajaxdropdown id="accountId" fields="['Text','Value']" dropdownId="accountId" url="/voucher/common!ajaxLoadBankAccountsByBranch.action" />
		<td class="greybox"><s:select name="branchId" id="branchId" list="dropdownData.branchList" listKey="id" listValue="name" headerKey="" headerValue="----Choose----" onchange="populateaccount(this);"  /></td>
</tr>
<tr>
		<td  class="bluebox"></td>
		<td class="bluebox"><s:text name="bankaccount"/>
		<span class="bluebox"><span class="mandatory">*</span></span></td>
		<td class="bluebox"><s:select name="accountId" id="accountId" list="dropdownData.accountList" listKey="id" listValue="accountnumber"   headerKey="" headerValue="----Choose----"/></td>
	 	<td class="bluebox"><s:text name="reconciliationdate"/>
		<span class="bluebox"><span class="mandatory">*</span></span></td>
		<td class="bluebox"><s:textfield name="reconciliationDate" id="reconciliationDate" onkeyup="DateFormat(this,this.value,event,false,'3')" value="%{asOnDate}"/>
		<a href="javascript:show_calendar('arform.reconciliationDate');"	style="text-decoration: none">&nbsp;<img tabIndex="-1"
										src="/egi/resources/erp2/images/calendaricon.gif"		border="0" /></A>
	
		</td>
</tr>
<tr>
		<td  class="greybox"></td>
		<td class="greybox"><s:text name="fromdate"/>
		<span class="greybox"><span class="mandatory">*</span></span></td>
		<td class="greybox"><s:textfield name="fromDate" id="fromDate" onkeyup="DateFormat(this,this.value,event,false,'3')" value="%{fromDate}"/>
		<a href="javascript:show_calendar('arform.fromDate');"	style="text-decoration: none">&nbsp;<img tabIndex="-1"
										src="/egi/resources/erp2/images/calendaricon.gif"		border="0" /></A>
	
		</td>
		<td class="greybox"><s:text name="todate"/>
		<span class="greybox"><span class="mandatory">*</span></span></td>
		<td class="greybox"><s:textfield name="toDate" id="toDate" onkeyup="DateFormat(this,this.value,event,false,'3')" value="%{toDate}"/>
		<a href="javascript:show_calendar('arform.toDate');"	style="text-decoration: none">&nbsp;<img tabIndex="-1"
										src="/egi/resources/erp2/images/calendaricon.gif"		border="0" /></A>
	
		</td>
</tr>

</table>
	
	<div class="buttonbottom" id="buttondiv" >
	<table>
	<tr>
		<td><s:submit type="submit" cssClass="buttonsubmit" value="Process"  name="Schedule" method="schedule" onclick="return validate();"/></td>	
		<td><input type="button" value="Close"  onclick="javascript:window.close()" class="buttonsubmit"/></td>
	</tr>
	</table>
	</div>
</center>
</s:form>
</body>
</html>
