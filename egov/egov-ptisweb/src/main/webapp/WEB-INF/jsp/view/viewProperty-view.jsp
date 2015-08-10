<!---------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#------------------------------------------------------------------------------- -->
<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<title><s:text name="viewPropDet.title" /></title>
		<script type="text/javascript">
		
			function loadOnStartup () {
				var btnCheckbox = document.getElementById('taxEnsureCheckbox');
				var btnPayTax = document.getElementById('payBill');
				if (btnCheckbox != null && btnPayTax != null) {
					btnPayTax.disabled = (btnCheckbox.checked) ? false : true;
				}
			}
			
			function switchPayTaxButton (ensureCheckbox) {
				jQuery('#payBill').attr('disabled', ensureCheckbox.checked ? false : true);			
			}

			jQuery(document).ready( function () {
				jQuery('#payBill').click( function () {
					var propertyId = '<s:property value="%{basicProperty.upicNo}"/>';
					window.location = '/../ptis/collection/collectPropertyTax-generateBill.action?propertyId='+propertyId;
				});
			});
		</script>
	</head>
	<body onload="loadOnStartup(); ">
		<s:form action="searchProperty" method="post" name="indexform"
				theme="simple" >
			<div class="formmainbox">
				<div class="headingbg"><s:text name="PropertyDetail" /></div>
				<br/>
				<jsp:include page="viewProperty.jsp"/>
				<div class="buttonbottom" align="center">
				<s:if test="%{isUserOperator}">
					<div align="center">
						<s:checkbox name="taxEnsureCheckbox" id="taxEnsureCheckbox" onclick="switchPayTaxButton(this);" required="true" />
						<span style="font-size:15px; color:red">
							<s:text name="msg.payBill.verification" /> <br><br>
							<s:text name="msg.activeDemand" />	
						</span> 
					</div><br>
					<div align="center">
						<table>
							<tr>
								<td align="center">
									<input type="button" name="payBill" id="payBill" value="Pay Bill" class="buttonsubmit" />
								</td>
							</tr>
						</table>
					</div><br>
				</s:if>
				
				<br>	
				<s:if test="%{roleName.contains(@org.egov.ptis.constants.PropertyTaxConstants@ROLE_ULB_OPERATOR.toUpperCase())}">
					<s:if test="%{isDemandActive}">
						<input type="button" class="buttonsubmit" name="btnModifyProperty"
							id="btnModifyProperty" value="Addition/Alteration of Assessment"
							onclick="window.location='../modify/modifyProperty-modifyForm.action?modifyRsn=MODIFY&indexNumber=<s:property value="%{basicProperty.upicNo}"/>';" />
						<input type="button" class="buttonsubmit" name="btnTrnsProperty"
							id="btnTrnsProperty" value="Transfer Ownership"
							onclick="window.location='../transfer/new.action?assessmentNo=<s:property value="%{basicProperty.upicNo}" />';" />
						<input type="button" name="generateBill" id="generateBill" value="Generate Bill" class="buttonsubmit"
							onclick="window.location='../bills/billGeneration-generateBill.action?indexNumber=<s:property value="%{basicProperty.upicNo}" />';" />
					</s:if>
					<%-- <s:else> --%>
						<input type="button" class="buttonsubmit" name="objection" id="objection"
							value="Create Revision Petition"
							onclick="window.location='../revPetition/revPetition-newForm.action?propertyId=<s:property value="%{basicProperty.upicNo}" />';" />
					<%-- </s:else> --%>
				</s:if>		
									
				<input type="button" class="buttonsubmit" name="SearchProperty"
					id="SearchProperty" value="Search Property" onclick="window.location='../search/searchProperty-searchForm.action';" />
				<input type="button" class="buttonsubmit" name="btnViewDCB"
					id="btnViewDCB" value="View DCB"
					onclick="window.location='../view/viewDCBProperty-displayPropInfo.action?propertyId=<s:property value="%{basicProperty.upicNo}" />';" />
				<input type="button" class="buttonsubmit" name="revPetition"
					id="revPetition" value="Create Revision Petition"
					onclick="window.location='../revPetition/revPetition-newForm.action?propertyId=<s:property value="%{basicProperty.upicNo}" />';" />
			    <input type="button" name="btnPrint" id="btnPrint" value="Print"
					class="buttonsubmit" onclick="window.print();" />
				<input type="button" name="button2" id="button2" value="Close"
					class="button" onclick="window.close();" />
				<s:hidden label="upicNo" id="upicNo" name="upicNo"
					value="%{basicProperty.upicNo}" />
				</div>				
			</div>
		</s:form>
	</body>
</html>
