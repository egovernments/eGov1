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
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tags/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tags/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tags/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tags/struts-nested.tld" prefix="nested"%>
<%@ taglib uri="/WEB-INF/tags/c.tld" prefix="c" %>

<html>  
<head>  
    <title>Cancel Bills - Search</title>
</head>
<SCRIPT LANGUAGE="javascript" SRC="../resources/javascript/jsCommonMethods.js"></Script>
<script type="text/javascript">
function validate()
{
	var fundValue=document.getElementById("fund.id").value;
	var strtDate=document.getElementById('fromDate').value;
	var endDate=document.getElementById('toDate').value;
	if(fundValue!=null && fundValue!=-1)
	{	
		if(strtDate.length !=0 && endDate.length !=0)
		{
			if( compareDate(formatDateToDDMMYYYY1(strtDate),formatDateToDDMMYYYY1(endDate)) == -1 )
			{
				alert('From Date cannot be greater than To Date');
				return false;
		    }
		 }
		return true;
	}
	else
	{
		alert("Please select a fund");
		return false;
	}
}
function update(obj)
{
	if(obj.checked)
		document.getElementById('selectedRows').value=parseInt(document.getElementById('selectedRows').value)+1;
	else
		document.getElementById('selectedRows').value=parseInt(document.getElementById('selectedRows').value)-1;
}
function resetSelectedRows()
{
	document.getElementById('selectedRows').value="0";
}
function validateCancel()
{
	var rows=parseInt(document.getElementById('selectedRows').value);
	if(rows==0)
	{
		alert("Please select atleast one bill");
		return false;
	}
	
	return true;
}
</script>
	<body onload="resetSelectedRows()">  
		<s:form action="cancelBill" theme="simple" >
			<jsp:include page="../budget/budgetHeader.jsp">
				<jsp:param name="heading" value="Bill Cancellation" />
			</jsp:include>
			<span class="mandatory" id="errorSpan">
				<s:actionerror/>  
				<s:fielderror />
				<s:actionmessage />
			</span>
			<div class="formmainbox">
				<div class="subheadnew">Cancel Bills - Search</div>
			</div>
			<table  width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluebox" ><s:text name="bill.Number"/> </td>
					<td class="bluebox"><s:textfield name="billNumber" id="billNumber" maxlength="25" value="%{billNumber}" /></td>
					<td class="bluebox" ><s:text name="voucher.fund"/><span class="mandatory">*</span> </td>
					<td class="bluebox" ><s:select name="fund.id" id="fund.id" list="dropdownData.fundList" listKey="id" listValue="name" headerKey="-1" headerValue="----Choose----"  value="%{fund.id}"/></td>
				</tr>
				<tr>
					<td class="greybox" ><s:text name="voucher.department"/></td>
					<td class="greybox" ><s:select name="deptImpl.id" id="deptImpl.id" list="dropdownData.DepartmentList" listKey="id" listValue="deptName" headerKey="-1" headerValue="----Choose----"  value="%{deptImpl.id}"/></td>
					<td class="greybox" ></td>
					<td class="greybox" ></td>
				</tr>
				<tr>
					<td class="bluebox" ><s:text name="from.date"/> </td>
					<td class="bluebox"><s:textfield name="fromDate" id="fromDate" maxlength="20" value="%{fromDate}" onkeyup="DateFormat(this,this.value,event,false,'3')"/><a href="javascript:show_calendar('forms[0].fromDate');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
					<td class="bluebox" ><s:text name="to.date"/> </td>
					<td class="bluebox"><s:textfield name="toDate" id="toDate" maxlength="20" value="%{toDate}" onkeyup="DateFormat(this,this.value,event,false,'3')"/><a href="javascript:show_calendar('forms[0].toDate');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
				</tr>
				<tr>
					<td class="greybox" ><s:text name="payment.expendituretype"/> </td>
					<!--	Remove the disabled attribute to make screen generic				-->
					<td class="greybox"><s:select name="expType" id="expType"  list="dropdownData.expenditureList" disabled="true" value="%{expType}"/></td>
					<td class="greybox" ></td>
					<td class="greybox" ></td>
				</tr>
			</table>
			<div  class="buttonbottom">
				<s:submit method="search" value="Search" id="searchBtn" onclick="return validate()" cssClass="buttonsubmit" />
				<input type="submit" value="Close" onclick="javascript:window.close()" class="button"/>
			</div>
			<s:if test="%{billListDisplay.size()!=0}">
				<table width="100%"  cellpadding="0" cellspacing="0" >
		        <tr>  
					<th class="bluebgheadtd"><s:text name="bill.cancelation.serialno" /></th>
		            <th class="bluebgheadtd"><s:text name="bill.Number" /></th>
		            <th class="bluebgheadtd"><s:text name="bill.Dept.Name" /></th>  
		            <th class="bluebgheadtd"><s:text name="bill.Date" /></th>  
		            <th class="bluebgheadtd"><s:text name="bill.cancelation.billamount" /></th>
		            <th class="bluebgheadtd"><s:text name="Select" /></th>
		        </tr>  
		        <c:set var="trclass" value="greybox"/>
		        
			    <s:iterator var="p" value="billListDisplay" status="s">
			    <tr>  
			    	<td style="text-align:center" class="<c:out value="${trclass}"/>">  
			            <s:property value="#s.index+1" /><s:hidden id="id" name="billListDisplay[%{#s.index}].id" value="%{id}"/>
			        </td>
			        <td style="text-align:center"  class="<c:out value="${trclass}"/>">  
			            <s:hidden id="billNumber" name="billListDisplay[%{#s.index}].billNumber" value="%{billNumber}"/><s:property value="%{billNumber}" /> 
			        </td>
			        <td style="text-align:center"  class="<c:out value="${trclass}"/>">  
			            <s:hidden id="billDeptName" name="billListDisplay[%{#s.index}].billDeptName" value="%{billDeptName}"/><s:property value="%{billDeptName}" /> 
			        </td>
			        <td style="text-align:center" class="<c:out value="${trclass}"/>">  
			            <s:hidden id="billDate" name="billListDisplay[%{#s.index}].billDate" value="%{billDate}"/><s:property value="%{billDate}" />
			        </td>
			        <td style="text-align:right" class="<c:out value="${trclass}"/>">  
			            <s:hidden id="billAmount" name="billListDisplay[%{#s.index}].billAmount" value="%{billAmount}"/><s:property value="%{billAmount}" />
			        </td>
			        <td  style="text-align:center" class="<c:out value="${trclass}"/>">
			       		<s:checkbox name="billListDisplay[%{#s.index}].isSelected" id="isSelected%{#s.index}" onclick="update(this);" />
			        </td>
			        <c:choose>
				        <c:when test="${trclass=='greybox'}"><c:set var="trclass" value="bluebox"/></c:when>
				        <c:when test="${trclass=='bluebox'}"><c:set var="trclass" value="greybox"/></c:when>
			        </c:choose>
			    </tr>  
			    </s:iterator>
			</table>
			<div  class="buttonbottom">
				<s:submit method="cancelBill" value="Cancel Bill" id="cancelBill" onclick="return validateCancel();" cssClass="buttonsubmit" />
			</div>
			</s:if>
			<s:elseif test="%{billListDisplay.size() == 0 && afterSearch}">
					<tr><td colspan="7" align="center"><font color="red">No record Found.</font></td>
					</tr>
			</s:elseif>
		<input type="hidden" id="selectedRows" name="selectedRows" />
		</s:form>
	</body>  
</html>
