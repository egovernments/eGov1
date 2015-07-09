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
    <title><s:text name="cancelVoucher.title" /></title>
    
</head>
<script>

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

function loadNamesForSelectedType()
{
	if(document.getElementById('type').value!=-1)
		{
		loadVoucherNames(document.getElementById('type').value);
		}
	}
		
</script>
	<body onload="loadNamesForSelectedType()" >  
	<div class="formmainbox"><div class="subheadnew"><s:text name="cancel.voucher.search"/></div>
    	<div style="color: red">
		<s:actionerror/>  
		<s:fielderror />
		</div>
	            
		<div style="color: green">
		<s:actionmessage theme="simple"/>
		</div>
		<s:form action="cancelVoucher" name="cancelVoucher" theme="simple" >
		<table align="center" width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td class="greybox" ><s:text name="voucher.number"/> </td>
					<td class="greybox"><s:textfield name="voucherNumber" id="voucherNumber" maxlength="25" value="%{voucherNumber}"/></td>
					<td class="greybox" ></td>
					<td class="greybox" ></td>
				</tr>
				<tr>
					<jsp:include page="../voucher/voucher-filter.jsp"/>
					                               
				</tr>                                                                          
				<tr>
				<td class="greybox" ><s:text name="voucher.type"/><span class="mandatory">*</span></td>                                    
				<td class="greybox"><s:select name="type" id="type" list="dropdownData.typeList" headerKey="-1" headerValue="----Choose----" onchange="loadVoucherNames(this.value)" /></td>
				<td class="greybox" ><s:text name="voucher.name"/><span class="mandatory">*</span></td>
				<td class="greybox"><s:select name="name" id="name" list="%{nameMap}" headerKey="-1" headerValue="----Choose----" /></td>
				</tr>  
				<tr>
					<td class="bluebox" ><s:text name="voucher.fromdate"/><span class="mandatory">*</span> </td>
					<s:date name="fromDate" format="dd/MM/yyyy" var="tempFromDate"/>
					<td class="bluebox"><s:textfield name="fromDate" id="fromDate" maxlength="20" onkeyup="DateFormat(this,this.value,event,false,'3')" value="%{tempFromDate}"/><a href="javascript:show_calendar('forms[0].fromDate');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a></td>
					<td class="bluebox"><s:text name="voucher.todate"/><span class="mandatory">*</span></td>
					<s:date name="toDate" format="dd/MM/yyyy" var="tempToDate"/>
					<td class="bluebox"><s:textfield name="toDate" id="toDate" maxlength="20" onkeyup="DateFormat(this,this.value,event,false,'3')" value="%{tempToDate}"/><a href="javascript:show_calendar('forms[0].toDate');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
				</tr>
			</table>
			<div  class="buttonbottom">
				<s:submit method="search" value="Search"  cssClass="buttonsubmit" onclick="return loadSearch()"/>                   
				<s:submit method="beforeSearch" value="Cancel"  cssClass="button" />
				<input type="button" value="Close" onclick="javascript:window.close()" class="button" />
			</div>
			<div id="loading" class="loading" style="width: 700; height: 700;display: none " align="center" >
			<blink style="color: red">Searching processing, Please wait...</blink>
			</div>                      
			<s:if test="%{voucherSearchList.size!=0}">
			<div id="listid" >
			<script>             
				document.getElementById('loading').style.display ='none';	
			</script>              
					<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="tablebottom">
			        <tr>  
			            <th class="bluebgheadtd">Sl.No.</th>  
			            <th class="bluebgheadtd">Voucher Number</th>
			            <th class="bluebgheadtd">Voucher Date</th> 
			            <th class="bluebgheadtd">Fund Name</th>
			            <th class="bluebgheadtd">Department Name</th>
			            <th class="bluebgheadtd">Type-Name</th>
						<th class="bluebgheadtd">Narration</th>
						<th class="bluebgheadtdnew">Select</th>
					</tr>  
			        <c:set var="trclass" value="greybox"/>           
			        
				    <s:iterator var="p" value="voucherSearchList" status="s">  
				    <tr>  
				    	<s:hidden id="voucherId" name="voucherId" value="%{id}" />
				    	<td style="text-align:center" class="<c:out value="${trclass}"/>">  
				            <s:property value="#s.index+1" />
				        </td>
						<td style="text-align:center"  class="<c:out value="${trclass}"/>">  
				         <a href="javascript:void(0);" onclick='viewVoucher(<s:property value="%{id}"/>);'><s:property value="%{voucherNumber}" /> </a>&nbsp;</div></td>
				        </td>                  
				        <td  style="text-align:center" class="<c:out value="${trclass}"/>">  
				            <s:date name="%{voucherDate}" format="dd/MM/yyyy"/>  
				        </td>
				        <td style="text-align:center" class="<c:out value="${trclass}"/>">  
				            <s:property value="%{fundId.name}" />
				        </td>
				        <td style="text-align:center" class="<c:out value="${trclass}"/>">  
				            <s:property value="%{vouchermis.departmentid.deptName}" />
				        </td>
				        <td style="text-align:center" class="<c:out value="${trclass}"/>">  
				            <s:property value="%{type}" />-<s:property value="%{name}" />
				        </td>
				        <td style="text-align:center" width="10%" class="<c:out value="${trclass}"/>">  
				      	    <s:property value="%{description}" />
				        </td>
				       
				        <td style="text-align:center" class="<c:out value="${trclass}"/>"><div align="center">
				     <input type="checkbox" name="selectVhs" value='<s:property value="%{id}"/>' id='chbox_<s:property value="#s.index+1" />' /> &nbsp;</div></td>
				                                                          
				        <c:choose>                              
					        <c:when test="${trclass=='greybox'}"><c:set var="trclass" value="bluebox"/></c:when>
					        <c:when test="${trclass=='bluebox'}"><c:set var="trclass" value="greybox"/></c:when>
				        </c:choose>
				    </tr>  
				    </s:iterator>
				  </table>
				
				 <td><s:submit  Class="buttonsubmit" value="Submit"  onclick="return validateVouchers();"/></td>
			                       
			</div>
			</s:if>
			<s:else>
				<s:if test="%{voucherList.size==0 && voucherList!=null}">
					No data found
				</s:if>
			</s:else>
			
		<s:token/>	
		</s:form>  
		<script>

	function validateVouchers()
	{
		var objLen=<s:property value="%{voucherSearchList.size()}"/>;
		var queryParams="";
		var index;
		 <s:iterator var="p" value="voucherSearchList" status="s">
		  index= <s:property value="#s.index+1" />;
		 var checkBox = document.getElementById('chbox_'+index);
		 if(checkBox.checked){
			var voucherId=checkBox.value;
			if(index!=1)
				queryParams+="&selectedVhs="+voucherId;
			else
			 	queryParams="selectedVhs="+voucherId;
		}                                      
		</s:iterator> 
		if(queryParams==""){
			alert("Alert please select atleast one voucher to cancel");
			return false;
		}else{            
			document.cancelVoucher.action = "${pageContext.request.contextPath}/voucher/cancelVoucher!update.action?"+queryParams;
		/*	var url = '../voucher/cancelVoucher!ajaxCancelVoucher.action?'+queryParams;
		  	YAHOO.util.Connect.asyncRequest('POST', url, callback, null);*/
		}          
	 return true;
    }                     
    
function viewVoucher(vid){
		var url = '../voucher/preApprovedVoucher!loadvoucherview.action?vhid='+vid;
		window.open(url,'Search','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');
}

function doAfterSubmit(){
		document.getElementById('loading').style.display ='block';
}

var callback = {
		success: function(o){
			alert("Vouchers cancelled succesfully");
			document.getElementById('listid').style.display ='none';
			},
		failure: function(o) {
			document.getElementById('loading').style.display ='none';
			alert("Search failed! Please try again");
			}
}
function loadSearch(){
	document.getElementById('loading').style.display ='block';
	return true;
	}

	
		</script>
	</body>  
</html>
