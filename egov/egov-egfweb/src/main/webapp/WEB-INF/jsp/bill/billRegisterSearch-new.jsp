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
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<html>  
<head>  
    <title><s:text name="bill.search.heading"></s:text></title>
</head>
	<body >  
		<s:form name="billRegisterForm" id ="billRegisterForm" action="billRegisterSearch" theme="simple"  method="post" onsubmit="javascript:doAfterSubmit()" >
			<jsp:include page="../budget/budgetHeader.jsp">
        		<jsp:param name="heading" value="Voucher Search" />
			</jsp:include>
<font  style='color: red ; font-weight:bold '> 
<p class="error-block" id="lblError" ></p></font>
			<span class="mandatory">
				<s:actionerror/>  
				<s:fielderror />
				<s:actionmessage />
			</span>
			<div class="subheadnew"><s:text name="bill.search.heading"></s:text></div>
			<table align="center" width="100%" cellpadding="0" cellspacing="0">
				<tr>
				<td class="bluebox" ><s:text name="bill.search.expType"/> <span class="mandatory">*</span></td>
				<td class="bluebox"><s:select name="expType" id="expType" list="dropdownData.expType" headerKey="-1" headerValue="----Choose----" value="%{expType}"/></td>
				</tr>
				<tr>
					<td class="greybox" ><s:text name="bill.search.dateFrom"/> <span class="mandatory">*</span></td>
					<td class="greybox"><s:textfield name="billDateFrom" id="billDateFrom" cssStyle="width:100px" value='%{billDateFrom}' onkeyup="DateFormat(this,this.value,event,false,'3')"/><a href="javascript:show_calendar('billRegisterForm.billDateFrom');" style="text-decoration:none"><img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
					<td class="greybox"><s:text name="bill.search.dateTo"/> <span class="mandatory">*</span></td>
					<td class="greybox"><s:textfield name="billDateTo" id="billDateTo" cssStyle="width:100px" value='%{billDateTo}' onkeyup="DateFormat(this,this.value,event,false,'3')"/><a href="javascript:show_calendar('billRegisterForm.billDateTo');" style="text-decoration:none"><img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
				</tr>
				<jsp:include page="billSearchCommon-filter.jsp"/>
				<tr>
					<td class="greybox"><s:text name="bill.search.billnumber"/> </td>
					<td class="greybox"><s:textfield name="billnumber" id="billnumber" maxlength="25" value="%{billnumber}" /></td>
					<td class="greybox">
					<td class="greybox">
				</tr>
			</table>
	<div class="subheadsmallnew" id="savebuttondiv1"/></div>
	<div class="mandatory" align="left" id="mandatorymarkdiv">* Mandatory Fields</div>
			<div  class="buttonbottom">
				<s:submit method="search" value="Search" cssClass="buttonsubmit" onclick="return validate()"/>
				<input type="submit" value="Close" onclick="javascript:window.close()" class="buttonsubmit"/>
			</div>
			<br/>
			
			<div id="listid" style="display:block">
					<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="tablebottom">
			        <tr>  
			        	<th class="bluebgheadtd">Sl. No.</th>
			            <th class="bluebgheadtd">Expenditure Type</th>  
			            <th class="bluebgheadtd">Bill Type</th>  
			            <th class="bluebgheadtd">Bill Number</th>  
			            <th class="bluebgheadtd">Bill Date</th>
			            <th class="bluebgheadtd">Bill Amount</th>  
			            <th class="bluebgheadtd">Passed Amount</th>
			            <th class="bluebgheadtd">Bill Status</th>  
			            <th class="bluebgheadtd">Owner Name</th> 
			        </tr>  
			       
				    <s:iterator var="p" value="billList" status="s">  
					
				    <tr>
					 
				    	<td>  
				            <s:property value="#s.index+1" />  
				        </td>
				        <td>  
				            <s:property value="%{expendituretype}" />  
				        </td>
				        <td>  
				            <s:property value="%{billtype}" />  
				        </td>
						<td>  
				            <a href="<s:property value='%{sourcepath}' />"><s:property value="%{billnumber}" /> </a> 
				        </td>
				        <td>  
				            <s:date name="%{billdate}" format="dd/MM/yyyy"/>  
				        </td>
				        <td style="text-align:right">  
				            <s:text name="bill.format.number" ><s:param value="%{billamount}"/></s:text>
				        </td>
				        <td  style="text-align:right">  
				           <s:text name="bill.format.number" ><s:param value="%{passedamount}"/></s:text>
				        </td>
				        
				         <td  style="text-align:center">  
				            <s:property value="%{billstatus}" />  
				        </td>
				        <td  style="text-align:center">  
				            <s:property value="%{ownerName}" />  
				        </td>
				    </tr>  
				    </s:iterator>
				    </table>  
			</div>
				    <div id="msgdiv" style="display:none">
						<table align="center" class="tablebottom" width="80%">
							<tr><th class="bluebgheadtd" colspan="7">No Records Found</td></tr>
						</table>
					</div>
					<div id="loading" class="loading" style="width: 700; height: 700;display:none" align="center" >
						<blink style="color: red">Searching processing, Please wait...</blink>
					</div>
				   
				
		
		</s:form>  
		<script>
	function validate(){
	
		document.getElementById('lblError').innerHTML ="";
		if(document.getElementById('expType').value == -1){
			document.getElementById('lblError').innerHTML = "Please select expenditure type";
			return false;
		}
		if(document.getElementById('billDateFrom').value.trim().length == 0){
			document.getElementById('lblError').innerHTML = "Please bill from date";
			return false;
		}
		if(document.getElementById('billDateTo').value.trim().length == 0){
			document.getElementById('lblError').innerHTML = "Please bill to date";
			return false;
		}
		 <s:if test="%{isFieldMandatory('fund')}"> 
				 if(null != document.getElementById('fundId') && document.getElementById('fundId').value == -1){

					document.getElementById('lblError').innerHTML = "Please Select a fund";
					return false;
				 }
			 </s:if>
			<s:if test="%{isFieldMandatory('department')}"> 
				 if(null!= document.getElementById('vouchermis.departmentid') && document.getElementById('vouchermis.departmentid').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a department";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('scheme')}"> 
				 if(null!=document.getElementById('schemeid') &&  document.getElementById('schemeid').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a scheme";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('subscheme')}"> 
				 if(null!= document.getElementById('subschemeid') && document.getElementById('subschemeid').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a subscheme";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('functionary')}"> 
				 if(null!=document.getElementById('vouchermis.functionary') &&  document.getElementById('vouchermis.functionary').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a functionary";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('fundsource')}"> 
				 if(null !=document.getElementById('fundsourceId') &&  document.getElementById('fundsourceId').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a fundsource";
					return false;
				}
			</s:if>
			<s:if test="%{isFieldMandatory('field')}"> 
				 if(null!= document.getElementById('vouchermis.divisionid') && document.getElementById('vouchermis.divisionid').value == -1){

					document.getElementById('lblError').innerHTML = "Please select a field";
					return false;
				 }
			</s:if>
			
			
		return true;
	}
	
function doAfterSubmit(){
		document.getElementById('loading').style.display ='block';
		dom.get('msgdiv').style.display='none';
		dom.get('listid').style.display='none';
	}
 
			
String.prototype.trim = function () {
    return this.replace(/^\s*/, "").replace(/\s*$/, "");
}

	<s:if test="%{billList.size<=0}">
				dom.get('msgdiv').style.display='block';
			</s:if>
	<s:if test="%{billList.size!=0}">
				dom.get('msgdiv').style.display='none';
				document.getElementById('loading').style.display ='none';
				dom.get('listid').style.display='block';
	</s:if>	
</script>
		
	</body>  

</html>
