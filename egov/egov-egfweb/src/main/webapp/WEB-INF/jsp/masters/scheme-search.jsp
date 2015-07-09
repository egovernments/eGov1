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
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>

<%@ page language="java"%>
<html>
  <head>
    <title>
    	<s:text name="scheme.search.title"/> 
    </title>
    <SCRIPT type="text/javascript">
   
    function validateFund(){
    	var fund = document.getElementById('fundId').value;
    	if(fund == "-1"){
    		alert("Please select a Fund");
    		return false;
    	}   
    	document.schemeForm.action='${pageContext.request.contextPath}/masters/scheme-search.action';
    	document.schemeForm.submit();
    	return true;
    }               
    </SCRIPT>
  </head>
  <body >
   <s:form name="schemeForm" action="scheme" theme="simple" validate="true">
    <div class="formmainbox"><div class="subheadnew"><s:text name="scheme.search.title"/></div>
    <s:hidden name="mode" id="mode" value="%{mode}" />
  		<table width="100%" border="0" cellspacing="0" cellpadding="0">                   
    		<tr>
    				<td style="width:10%"></td>
			        <td class="bluebox"><s:text name="scheme.fund"/><span class="mandatory1"> *</span></td>
				    <td class="bluebox">
					<s:select name="fund" id="fundId" list="dropdownData.fundDropDownList" listKey="id" listValue="name" headerKey="-1" headerValue="----Select----"  value="scheme.fund.id" />
					</td>
			</tr>
			<tr>
					<td style="width:10%"></td>
					<td class="greybox" > <s:text name="scheme.startDate" /></td>
					<td  class="greybox" ><s:date name="validfrom" id="validfromId" format="dd/MM/yyyy" />
					<s:textfield name="validfrom" id="validfromId" value="%{validfrom}"  maxlength="10" onkeyup="DateFormat(this,this.value,event,false,'3')"/>
					<a href="javascript:show_calendar('schemeForm.validfrom',null,null,'DD/MM/YYYY');" style="text-decoration:none">&nbsp;<img  src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
					
					<td  class="greybox" ><s:text name="scheme.endDate" /></td>
					<td  class="greybox">
					<s:date name="validto" id="validtoId" format="dd/MM/yyyy"/>
					<s:textfield name="validto" id="validtoId" value="%{validto}"  maxlength="10" onkeyup="DateFormat(this,this.value,event,false,'3')"/>
					<a href="javascript:show_calendar('schemeForm.validto',null,null,'DD/MM/YYYY');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)</td>
			</tr>
			        
    	</table>    
    	<br/>            
    	
	  	<s:if test="%{schemeList.size!=0}">
		<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="tablebottom">
		<tr>   
     		<th class="bluebgheadtd"  style="width:2%;text-align:center" align="center">
				Sl No.
			</th>
			<th class="bluebgheadtd" style="width:4%;text-align:center" align="center">
				Scheme Code
			</th>
			<th class="bluebgheadtd"  style="width:8%;text-align:center" align="center">
				Scheme Name
			</th>
			<th class="bluebgheadtd"  style="width:2%;text-align:center" align="center">
				Start Date
			</th>
			<th class="bluebgheadtd" style="width:4%;text-align:center" align="center">
				End Date
			</th>
			<th class="bluebgheadtd"  style="width:4%;text-align:center" align="center">
				IsActive
			</th>
		</tr>
		<c:set var="trclass" value="greybox"/>
		<s:iterator var="scheme" value="schemeList" status="f">
			<tr>
			
				<td  class="<c:out value="${trclass}"/>"style="text-align:center" align="center"><s:property value="#f.index+1" /></td>
				<td  class="<c:out value="${trclass}"/>"style="text-align:center" align="center"><a href="#" onclick="urlLoad('<s:property value="%{id}" />','<s:property value="%{mode}" />');"
									id="sourceLink" /> <s:label value="%{code}" /> </a></td>
				<td  class="<c:out value="${trclass}"/>"style="text-align:center" align="center"><s:property value="name" /></td>    
				<td  class="<c:out value="${trclass}"/>"style="text-align:center" align="center"><s:date name="%{validfrom}" format="dd/MM/yyyy"/>	</td>
				<td  class="<c:out value="${trclass}"/>"style="text-align:center" align="center"><s:date name="%{validto}" format="dd/MM/yyyy"/></td>
				<td  class="<c:out value="${trclass}"/>"style="text-align:center" align="center"><s:property value="isactive" />	</td>
				<c:choose>
					        <c:when test="${trclass=='greybox'}"><c:set var="trclass" value="bluebox"/></c:when>
					        <c:when test="${trclass=='bluebox'}"><c:set var="trclass" value="greybox"/></c:when>
			  </c:choose>
			</tr>
		</s:iterator>
		
		</table>
		</s:if>              
		 
		</div>  
		<div class="buttonbottom" >
    	<table align="center">  
    	 <tr>  
			<td><input type="submit" class="buttonsubmit" value="Search" id="search" name="button" onclick="return validateFund();" />&nbsp;</td>
		    <td><input type="button" id="Close" value="Close"  onclick="javascript:window.close()" class="button"/></td>
	  </table>
	  </div>
		<s:if test="%{schemeList.size==0}">
			<div id="msgdiv" style="display: block">
				<table align="center" class="tablebottom" width="80%">
					<tr>
						<th class="bluebgheadtd" colspan="7">
							No Records Found
						</td>
					</tr>
				</table>
			</div>
		</s:if>        
	  </s:form>
	 
	
	<script type="text/javascript">
	function urlLoad(id,showMode) {
		if(showMode=='edit')
			 url = "../masters/scheme-beforeEdit.action?id="+id+"&mode=edit";
		else          
			 url = "../masters/scheme-beforeView.action?id="+id+"&mode=view"; 
		window.open(url,'schemeView','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');
	}
	</script>
</script>
  </body>
</html>
