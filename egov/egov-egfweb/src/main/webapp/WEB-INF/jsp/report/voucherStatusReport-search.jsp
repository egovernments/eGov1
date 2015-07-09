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
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ include file="/includes/taglibs.jsp" %>
<link href="<egov:url path='/resources/css/displaytagFormatted.css'/>" rel="stylesheet" type="text/css" />
<html>  
<head>  

    <title><s:text name="voucher.title" /></title>
    
</head>
	<body onload="activeModeOfPayment()">  
		<s:form action="voucherStatusReport" name="voucherStatusReport" theme="simple" >
		<!--	<jsp:include page="../budget/budgetHeader.jsp">
	        		<jsp:param name="heading" value="Voucher Search" />
				</jsp:include>		-->
			<span class="mandatory">
				<s:actionerror/>  
				<s:fielderror />
				<s:actionmessage />
			</span>
			
				<div class="formmainbox">
					<div class="subheadnew">
						<s:text name="voucher.title" />
					</div>
				</div>
			<table align="center" width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<jsp:include page="../voucher/voucher-filter.jsp"/>
				</tr>                                                                                                    
				<tr>
				<td class="greybox" ><s:text name="voucher.type"/> </td>
				<td class="greybox"><s:select name="type" id="type" list="dropdownData.typeList" headerKey="-1" headerValue="----Choose----" onchange="loadVoucherNames(this.value);activeModeOfPayment()" /></td>
				<td class="greybox" ><s:text name="voucher.name"/></td>
				<td class="greybox"><s:select name="name" id="name" list="%{nameMap}" headerKey="-1" headerValue="----Choose----" /></td>
				</tr>  
				<tr id = "modeofpayment">
				<td class="bluebox" ><s:text name="voucher.modeOfPayment" /> </td>
				<td class="bluebox"><s:select name="modeOfPayment" id="modeOfPayment" list="dropdownData.modeOfPaymentList" headerKey="-1" headerValue="----Choose----"/></td>
				<td class="bluebox" ></td>
				<td class="bluebox"></td>
				</tr>
				<tr>
					<td class="greybox" ><s:text name="voucher.fromdate"/><span class="mandatory">*</span></td>
					<s:date name="fromDate" format="dd/MM/yyyy" var="tempFromDate"/>
					<td class="greybox"><s:textfield name="fromDate" id="fromDate" maxlength="20" onkeyup="DateFormat(this,this.value,event,false,'3')" value="%{tempFromDate}"/><a href="javascript:show_calendar('forms[0].fromDate');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a></td>
					<td class="greybox"><s:text name="voucher.todate"/><span class="mandatory">*</span></td>
					<td class="greybox"><s:textfield name="toDate" id="toDate" maxlength="20" onkeyup="DateFormat(this,this.value,event,false,'3')" value="%{toDate}"/><a href="javascript:show_calendar('forms[0].toDate');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
				</tr>
				<tr>
					<td class="bluebox">
						<s:text name="voucher.status" />
					</td>
					<td class="bluebox">
						<s:select  name="status" id="status" list="%{statusMap}" headerKey="-1"
							headerValue="----Choose----" value="%{status}" />
					</td>
					<td class="bluebox">
					
					</td>
					<td class="bluebox">
					</td>
				</tr>
			</table>
			<div  class="buttonbottom"> 
				<s:submit method="search" value="Search"  cssClass="buttonsubmit" />
				<s:submit method="beforeSearch" value="Cancel"  cssClass="button" />
				<input type="button" value="Close" onclick="javascript:window.close()" class="button" />
				
			</div>
		
	
		 <table width="100%" border="0" cellspacing="0" cellpadding="0">
		 <s:if test="%{pagedResults!=null}">
          		 <tr>
				<td width="100%">
          			 	  <display:table name="pagedResults" uid="currentRowObject" cellpadding="0" cellspacing="0" 
          			 	  requestURI="" class="its"  style=" border-left: 1px solid #C5C5C5; border-top: 1px solid #C5C5C5;border-right: 1px solid #C5C5C5;border-bottom: 1px solid #C5C5C5;">	
							 <display:column  title=" Sl No" style="text-align:center;"  >
 						 	 <s:property value="%{#attr.currentRowObject_rowNum+ (page-1)*pageSize}"/></display:column>
          			 	   	<display:column    title="Department Name"  style="text-align:center;">
 						 	<s:property value="%{#attr.currentRowObject.deptName}" /> </display:column>
          			 	    <display:column    title="Voucher Number"  style="text-align:center;">
          			 	    <s:property value="%{#attr.currentRowObject.vouchernumber}" /> </display:column>
          			 	    <display:column   title="Voucher Type" style="text-align:center;">
          			 	    <s:property value="%{#attr.currentRowObject.type}" /> </display:column>
          			 	    <display:column    title="Voucher Name" style="text-align:center;" >
          			 	    <s:property value="%{#attr.currentRowObject.name}" /> </display:column>
          			 	    <display:column      title="Voucher Date" style="text-align:center;" >
          			 	    <s:date name="%{#attr.currentRowObject.voucherdate}" format="dd/MM/yyyy"/>
          			 	    </display:column>
          			 	    <display:column    title="Source" style="text-align:center;">
          			 	    <s:property value="%{#attr.currentRowObject.source}" /> </display:column>
          			 	    <display:column      title="Total Amount" style="text-align:right;" >
          			 	    <s:property value="%{#attr.currentRowObject.amount}" /> </display:column>
          			 	    <display:column    title="Owner" style="text-align:center;">
          			 	    <s:property value="%{#attr.currentRowObject.owner}" /> </display:column>
          			 	    <display:column      title="Status" style="text-align:center;">
          			 	    <s:property value="%{#attr.currentRowObject.status}" /> </display:column>
          			 	   
          			 	  </display:table>
          			 	  </td>
          			 	  <tr>
          		<td>
          		<div id="exportButton" class="buttonbottom">
					<s:submit method="generatePdf" value="Save As Pdf" cssClass="buttonsubmit" id="generatePdf" />
					<s:submit method="generateXls" value="Save As Xls" cssClass="buttonsubmit" id="generateXls" />
				</div>
				
				</td>
				</tr>
          			 	
         </s:if>    
   </table>
    
		
		                 
		<script>

	
		<s:if test="%{voucherList.size==0}">
			dom.get('exportButton').style.display='none';	
		</s:if>


		function loadVoucherNames(selected)
		{
			var s="";  
			if(selected==-1)
				{
				document.getElementById('name').options.length=0;
				document.getElementById('name').options[0]= new Option('--------Choose--------','0');
				}
		<s:iterator value="voucherTypes" var="obj">
		  s='<s:property value="#obj"/>';
		 if(selected==s)
		 {
		document.getElementById('name').options.length=0;
		document.getElementById('name').options[0]= new Option('--------Choose--------','0');
		
		 <s:iterator value="voucherNames[#obj]" status="stat" var="names">
		 document.getElementById('name').options[<s:property value="#stat.index+1"/>]= new Option('<s:property value="#names"/>','<s:property value="#names"/>');
		 </s:iterator>   
		 }
		 </s:iterator>
			  
			
		}
		function activeModeOfPayment()
		{
			var selected = document.getElementById('type').value;
		if(selected=="Payment")
			{
			document.getElementById('modeofpayment').style.display = "";
			}
		else{
			document.getElementById('modeofpayment').style.display = "none";
			}
		}
		
		
		</script>
		
	</s:form>  	
		
	</body>  
</html>
