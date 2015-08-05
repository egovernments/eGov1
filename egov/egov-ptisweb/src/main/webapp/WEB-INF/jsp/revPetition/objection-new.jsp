<!--
	eGov suite of products aim to improve the internal efficiency,transparency, 
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

<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>


<html>
	<head>
		<script src="<c:url value='/resources/javascript/objection.js' context='/ptis'/>"></script>
		<title><s:text name="recordObjection.title"></s:text></title>
		<script type="text/javascript">
			jQuery.noConflict();
			jQuery("#loadingMask").remove();

			function validateRecordObjection(){
				document.getElementById("lblError").style.display='none';
			//	alert(dom.get('recievedOn').value);
				if(dom.get('recievedOn').value==''){
					//document.getElementById("lblError").style.display='block';
					//document.getElementById("lblError").innerHTML  = "Please enter Objection Received Date";
					alert('Please enter Revision Petition Received Date');
					return false;
				}
				else if(dom.get('recievedBy').value==''){
					//document.getElementById("lblError").style.display='block';
					//document.getElementById("lblError").innerHTML  = "Please enter Objection Received By";
					alert('Please enter Revision Petition Received By');
					return false;
				}
				else if(dom.get('details').value==''){
					//document.getElementById("lblError").style.display='block';
					//document.getElementById("lblError").innerHTML  = "Please enter Objection Details";
					alert('Please enter Revision Petition Details');
					return false;
				}
				return true;
			}
			function onSubmit() {
				var actionName = document.getElementById('workFlowAction').value;
				var action = null;

				if(validateRecordObjection()){
					action = 'revPetition.action';
					document.forms[0].action = action;
					document.forms[0].submit;
					return true;
					}
				else
					return false;
			
				return true;
			}
			
		</script>
		<link href="<c:url value='/resources/css/headertab.css'/>" rel="stylesheet" type="text/css" />
	
	</head>
	<body class="yui-skin-sam">
	<s:form action="revPetition" method="post" name="objectionViewForm" theme="simple">
	<s:push value="model">
	<s:token />
	<div class="errorstyle" id="lblError" style="display:none;"></div>
	<s:actionerror/>  <s:fielderror />
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tabs-tableparent">
        <tr>
            <td><div id="header">
				<ul id="Tabs">
					<li id="propertyHeaderTab" class="First Active"><a id="header_1" href="javascript:void(0);" onclick="showPropertyHeaderTab();"><s:text name="propDet"></s:text></a></li>
					<li id="objectionDetailTab" class=""><a id="header_2" href="javascript:void(0);" onclick="showObjectionHeaderTab();"><s:text name="objection.details.heading"></s:text></a></li>
		<%-- 			<li id="approvalTab" class="Last"><a id="header_3" href="javascript:void(0);" onclick="showApprovalTab();"><s:text name="approval.details.title"></s:text></a></li>
 --%>				</ul>
            </div></td>
          </tr>
     
           
          <tr>
            <td>
            <div id="property_header">
            <br/>
         			<jsp:include page="../view/viewProperty.jsp"/>
         			 <s:hidden name="basicProperty"   id="basicProperty" value="%{basicProperty.id}"></s:hidden>
          			
            </div>            
            </td> 
          </tr>            
          <tr>
            <td>
            <div id="objection_header" style="display:none;"> 
        	
				<jsp:include page="recordObjection.jsp"/>
            </div>
            </td>
          </tr>
          <tr>
            <td>
            <div > 
         		<jsp:include page="../workflow/commonWorkflowMatrix.jsp"/>
             <%-- 		<jsp:include page="../workflow/commonWorkflowMatrix.jsp"/>  --%>
         		<br/>
            </div>
            </td>
          </tr>
	  </table> 
	  <div id="loadingMask" style="display:none"><p align="center"><img src="/egi/resources/erp2/images/bar_loader.gif"> <span id="message"><p style="color: red">Please wait....</p></span></p></div>
	  <div class="buttonbottom" align="center">
						<%@ include file="../workflow/commonWorkflowMatrix-button.jsp" %>
			
	  <%-- 	<table>
		<tr>
		    	<td><s:submit value="Forward" name="forward" id="forward"  method="create" cssClass="buttonsubmit" onClick="return validateRecordObjection(this);doLoadingMask();"/></td>
		    	<td><s:submit value="Save" name="save" id="save"  method="create" cssClass="buttonsubmit"  onClick="return validateRecordObjection(this);doLoadingMask();"/></td>
		    	<td><input type="button" name="button2" id="button2" value="Close" class="button" onclick="window.close();"/></td>
		</tr>             
		</table> --%></div>
		<s:hidden name="model.id" id="model.id"/>    
		<s:hidden name="egwStatus.code" id="egwStatuscode" value="%{egwStatus.code}"/>      
		
		</s:push>
	</s:form>
</body>
</html>
