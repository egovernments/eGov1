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
<html>
	<head>
		<title>
				<s:if test="%{showMode=='edit'}">
					<s:text name="fund.modify" />
				</s:if>
				<s:if test="%{showMode=='view'}">
					<s:text name="fund.view" />
				</s:if>
		</title>
		<script type="text/javascript">
		
	function openSearch(obj, val) {
							var a = new Array(3);
							var str = "Search.html?purposeId=" + val;
							var sRtn = showModalDialog(str, "",
									"dialogLeft=300;dialogTop=210;dialogWidth=305pt;dialogHeight=300pt;status=no;");

							if (sRtn != '') {
								a = sRtn.split("`~`");

								document.getElementById('chartofaccountsByPayglcodeid').value = a[2];
								document.getElementById('pay_name').value = a[1];

				}
		}

	function disableControls(isDisable) {
		for ( var i = 0; i < document.fundForm.length; i++)
			document.fundForm.elements[i].disabled = isDisable;
		document.getElementById('Close').disabled = false;
	}	

	function onLoadTask() {
		var close = '<s:property value="close"/>';
		var isactive = '<s:property value="isactive"/>';
		var showMode = '<s:property value="showMode"/>';
		var clearVal = '<s:property value="clearValues"/>';
		var success = '<s:property value="success"/>';

		if (success == 'yes') {
			alert("Fund Modified Successfully");
			} else if((success == 'no')){
				alert("Fund Could Not be Modified");
				}
		
		if (close == 'true') {
			window.close();
		}
		
		if (isactive == 'true') {
			document.getElementById("isactive").checked="checked";
		}
		
		if (showMode == 'edit') {
			disableControls(false);
		} else {
			disableControls(true);
		}
		
		if (clearVal == 'true') {
			document.getElementById('code').value = "";
			document.getElementById('fundNameActual').value = "";
			document.getElementById('fund.fund.id').value = "";
			document.getElementById('identifier').value = "";
			document.getElementById('chartofaccountsByPayglcodeid').value = "";
			document.forms[0].isactive.checked=false;
		}
	}
	
	function validate(){
		if(document.getElementById('code').value == null || document.getElementById('code').value==''){
			alert("Please enter Code");
			return false;
		}
		if(document.getElementById('fundNameActual').value == null || document.getElementById('fundNameActual').value==''){
			alert("Please enter Name");
			return false;
		}
		return true;
	}
	function setClose() {
		var close = document.getElementById('close');    
		   close.value = true;
		   return true;
		}
</script>

	</head>
	<body onload="onLoadTask();">
		<s:actionmessage theme="simple" />
		<div class="formmainbox">
			<div class="subheadnew">
				<s:if test="%{showMode=='edit'}">
					<s:text name="fund.modify" />
				</s:if>
				<s:if test="%{showMode=='view'}">
					<s:text name="fund.view" />
				</s:if>
			</div>
		</div>
		<s:actionerror />
		<s:fielderror />
		<s:form name="fundForm" action="fund"  theme="simple">
		
		<s:push value="model">
		<s:hidden name="showMode" />
			<s:hidden name="id" />
				<%@include file="fund-viewform.jsp"%>
				<div class="buttonbottom">
					<s:if test="%{showMode=='edit'}">
						<s:submit name="edit" value="Modify And View" method="edit" cssClass="buttonsubmit" onclick="javascript: return validate();"/>
						<s:submit name="edit" value="Modify And Close" method="edit" cssClass="buttonsubmit" onclick="validate();setClose();"/>
						<s:hidden name="close" id="close" />
					</s:if>
					<input type="button" id="Close" value="Close"
						onclick="javascript:window.close()" class="button" />
				</div>
			</s:push>
			<s:token/>
		</s:form>
	</body>
</html>
