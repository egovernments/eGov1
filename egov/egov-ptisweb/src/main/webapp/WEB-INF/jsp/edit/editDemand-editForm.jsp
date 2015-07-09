#-------------------------------------------------------------------------------
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
#-------------------------------------------------------------------------------
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="/includes/taglibs.jsp"%>
<%@ page import="com.opensymphony.xwork2.ActionContext" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><s:text name="editDemand" /> </title>
	<script type="text/javascript">
		jQuery.noConflict();
		jQuery("#loadingMask").remove();
	    var newInstallmentCount = 0;
	    var instDetailsRowIndex = 2;
	    var newDemandRsnsCount = 10;	    
	    var lastIndex;
	    var lastIndexOnError;
	    var noOfDemandRsns = 10;
	    var isFirstInstVisible = true;
		
	    function addNewInstallment() {
			
			var rowIndex = document.getElementById("newInstallmentRow").rowIndex;
			var trClones = new Array();
			var instDetailsTable = document.getElementById("instDetails");
			
			if (newInstallmentCount == 0) {
				for (var i = 0; i < noOfDemandRsns; i++) {
					var row = instDetailsTable.rows[i+instDetailsRowIndex];
					row.style.display = "table-row";
				}
				newInstallmentCount++;
				isFirstInstVisible = true;
			} else {
				for (var i = rowIndex, k = 0; i < (rowIndex + newDemandRsnsCount); i++, k++) {				
					trClones[k] = instDetailsTable.rows[i].cloneNode(true);	
				}
				
				for (var j = 0; j < k; j++) {
					if (j == 0) {					
						instDetailsTable.tBodies[0].insertBefore(trClones[j], instDetailsTable.rows[rowIndex]);	
					} else {
						instDetailsTable.tBodies[0].insertBefore(trClones[j], instDetailsTable.rows[rowIndex+j]);
					}
				}
				newInstallmentCount++;
			}
		}
		
		function deleteRecentInstallment() {
			var instDetailsTable = document.getElementById("instDetails");
			
			if (newInstallmentCount > 1) {
				for (var i = 0; i < newDemandRsnsCount; i++) {				
					instDetailsTable.tBodies[0].deleteRow(instDetailsRowIndex);
					lastIndex--;
				}				
			} else if (newInstallmentCount == 1) {
				isFirstInstVisible = false;
				var instDetailsTable = document.getElementById("instDetails");
				for (var i = 0; i < noOfDemandRsns; i++) {
					var row = instDetailsTable.rows[i+instDetailsRowIndex];
					row.style.display = "none";
					lastIndex--;
				}
			}
			
			newInstallmentCount--;			
		}
		
		function rearrangeIndexes() {
			var instDetailsTable = document.getElementById("instDetails");
			// New index for textfield name & value attribute	
			var newInstallments = newInstallmentCount;
			var z = 0;
			var li = (parseInt(lastIndex) + 1);
			for (var i = 0; i < noOfDemandRsns; i++) {
				var row = instDetailsTable.rows[i+instDetailsRowIndex];				
				var attrValueInstallment = "demandDetailBeanList["+li+"].installment.id";
				var attrNameReasonMaster = "demandDetailBeanList["+li+"].reasonMaster";
				var attrValueActAmnt = "demandDetailBeanList["+li+"].actualAmount";
				var attrValueRevsdAmnt = "demandDetailBeanList["+li+"].revisedAmount";
				var attrValueActColl = "demandDetailBeanList["+li+"].actualCollection";
				var attrValueRevsdColl = "demandDetailBeanList["+li+"].revisedCollection";
				var isNew = "demandDetailBeanList["+li+"].isNew";		
				//row.cells[0].childNodes[1].childNodes[1].setAttribute("name", attrValueInstallment);
				row.cells[1].childNodes[1].setAttribute("name", attrNameReasonMaster);
				
				/* if (newInstallments == 0) {
					row.cells[3].childNodes[1].childNodes[1].setAttribute("name", attrValueRevsdAmnt);
					row.cells[5].childNodes[1].childNodes[1].setAttribute("name", attrValueRevsdColl);
				} */
				
				row.cells[2].childNodes[1].childNodes[1].setAttribute("name", attrValueActAmnt);
				//row.cells[2].childNodes[1].childNodes[1].setAttribute("value", "");
				row.cells[2].childNodes[1].childNodes[1].value = "";				
				row.cells[4].childNodes[1].childNodes[1].setAttribute("name", attrValueActColl);
				//row.cells[4].childNodes[1].childNodes[1].setAttribute("value", "");
				row.cells[4].childNodes[1].childNodes[1].value = "";			
				
				if (newInstallments == 0) {
					row.cells[0].childNodes[1].childNodes[5].setAttribute("name", attrValueInstallment);
					row.cells[0].childNodes[1].childNodes[7].setAttribute("name", isNew);
				} else {
					row.cells[0].childNodes[1].childNodes[1].setAttribute("name", attrValueInstallment);	
					if (z == 0){
						row.cells[0].childNodes[3].setAttribute("name", isNew);
						row.cells[0].childNodes[3].setAttribute("value", true);	
					} else {
						row.cells[0].childNodes[1].childNodes[3].setAttribute("name", isNew);
						row.cells[0].childNodes[1].childNodes[3].setAttribute("value", true);	
					}
					
				}
				
				if (newInstallments >= 1 && (z != 0 && (z % newDemandRsnsCount) == 0)) {
					newInstallments--;
					z = 0;
				} else {
					z++;
				}
				li++;
			}
			lastIndex = parseInt(li) - 1;
		}
		
		function assignInstallmentId(obj, id) {
			var instDetailsTable = document.getElementById("instDetails");
			var selRowIndex = obj.parentNode.parentNode.parentNode.rowIndex;
			for (var i = 1; i <= 9; i++) {
				var row = instDetailsTable.rows[i+selRowIndex];
				row.cells[0].childNodes[1].childNodes[1].setAttribute("value", id);
			}
		}
		
	</script>
</head>
<body>	
	<div align="left">
  		<s:actionerror/>
  	</div>
	<div class="formmainbox">
  	<div class="formheading"></div>
		<div class="headingbg"><s:text name="editDemand"/></div>
		<s:form name="editDemandForm" action="editDemand" theme="simple">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="bluebox" ></td>
						<td class="bluebox" colspan="2">
							 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  <s:text name="prop.Id" />:
						</td>
						<td class="bluebox" colspan="2">
							<span class="bold"> <s:property value="%{propertyId}" />
							<s:hidden name="propertyId" value="%{propertyId}" /> </span>
						</td>
					</tr>					
					<tr>
						<td class="greybox" ></td>
						<td class="greybox" colspan="2">
							 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  <s:text name="OwnerName" />:
						</td>
						<td class="greybox" colspan="2">
							<span class="bold"> <s:property value="%{ownerName}" /> <s:hidden
									name="ownerName" value="%{ownerName}" /> </span>
						</td>
					</tr>
					<tr>
						<td class="bluebox"></td>
						<td class="bluebox"  colspan="2">
							 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  <s:text name="PropertyAddress" />:
						</td>
						<td class="bluebox" colspan="2">
							<span class="bold"> <s:property value="%{propertyAddress}" />
								<s:hidden name="propertyAddress" value="%{propertyAddress}" /> </span>
						</td>
					</tr>
					<tr>
						<td colspan="5" class="greybox">
							<div align="center">
								<table width="80%" border="0" cellpadding="0" cellspacing="0"
									class="tablebottom" id="instDetails">									
									<tr>
										<th class="bluebgheadtd" width="2%">
											<s:text name="installment" />
										</th>
										<th class="bluebgheadtd" width="3%">
											<s:text name="taxName" />
										</th>
										<th class="bluebgheadtd" width="2%">
											<s:text name="actualTax" />
										</th>
										<th class="bluebgheadtd" width="2%">
											<s:text name="revisedTax" />
										</th>		
										<th class="bluebgheadtd" width="2%">
											<s:text name="actualCollection" />
										</th>
										<th class="bluebgheadtd" width="2%">
											<s:text name="revisedCollection" />
										</th>								
									</tr>
									<tr>										
										<td colspan="8" align="right">
										   Add/Remove Installment
											<img id="addInstallment" name="addInstallment"
												src="${pageContext.request.contextPath}/image/addrow.gif"
												alt="Add Installment" onclick="javascript: addNewInstallment(); rearrangeIndexes();" width="18"
												height="18" border="0" />
											<img id="delInstallment"
												name="delInstallment"
												src="${pageContext.request.contextPath}/image/removerow.gif"
												alt="Remove Installment" onclick="javascript: deleteRecentInstallment();"
												width="18" height="18" border="0" />
										</td>
									</tr> 						
									<s:if test="%{hasActionErrors() == false}">
									<s:set
										value="{@org.egov.ptis.constants.PropertyTaxConstants@DEMANDRSN_STR_WARRANT_FEE, @org.egov.ptis.constants.PropertyTaxConstants@DEMANDRSN_STR_NOTICE_FEE, @org.egov.ptis.constants.PropertyTaxConstants@DEMANDRSN_STR_COURT_FEE, @org.egov.ptis.constants.PropertyTaxConstants@DEMANDRSN_STR_PENALTY_FINES}"
										var="demandRsnToExclude" />
									<s:iterator value="@org.egov.ptis.constants.PropertyTaxConstants@DMDRSN_CODE_MAP" status="itrStatus" var="rsn">
											<s:if test="%{#demandRsnToExclude.contains(key) == false}" >
											<tr id="newInstallmentRow">
												<s:if test="%{#itrStatus.count == 1}" >												
												<td class="blueborderfortd">												
													<div align="center">
														<s:select id="installments" name="installments"
														list="dropdownData.allInstallments" headerKey="-1"
														headerValue="%{getText('default.select')}" listKey="id"
														listValue="description" cssStyle="width:80px" onchange="assignInstallmentId(this, this.value)"/>
													</div>
													<s:hidden />
												</td>
												</s:if>
												<s:else>
													<td class="blueborderfortd">
														<div align="center">
															<s:hidden />
															<s:hidden />
														</div>
													</td>
												</s:else>
												<td class="blueborderfortd">												
													<%-- <s:iterator value="@org.egov.ptis.constants.PropertyTaxConstants@DMDRSN_CODE_MAP" status="itrCodeMapStatus">
														<s:if test="%{value == #rsn}"> --%>
															<s:property value="key" />
															<s:hidden value="%{key}" />
														<%-- </s:if>
													</s:iterator> --%>
												</td>											
												<td class="blueborderfortd">
													<div align="right">
														<s:textfield
															name="demandDetailBeanList[%{#idx}].actualAmount"
															id="revisedTax" size="10" maxlength="10"
															onblur="trim(this,this.value); checkNumber(this); isPositiveNumber(this, 'Actual Tax');"
															value="%{demandDetailBeanList[#idx].actualAmount}"													
															style="text-align: right" />
													</div>
												</td>
												<td class="blueborderfortd">
													<div align="center">
														N/A
													</div>
												</td>
												<td class="blueborderfortd">
													<div align="right">
														<s:textfield
														name="demandDetailBeanList[%{#idx}].actualCollection"
														id="revisedCollection" size="10" maxlength="10"
														onblur="trim(this,this.value); checkNumber(this); isPositiveNumber(this, 'Actual Collection');"
														style="text-align: right" 
														value="%{demandDetailBeanList[#idx].actualCollection}"/>
													</div>
												</td>
												<td class="blueborderfortd" align="center">
													<div align="center">
														N/A
													</div>
												</td>
											</tr>
											</s:if>
										</s:iterator>
										<script type="text/javascript">
											if (isFirstInstVisible == true) {
												isFirstInstVisible = false;
												var instDetailsTable = document.getElementById("instDetails");
												for (var i = 0; i < noOfDemandRsns; i++) {
													var row = instDetailsTable.rows[i+instDetailsRowIndex];
													row.style.display = "none";
												}
											}
										</script>	
									</s:if>								
									<s:if test="%{hasActionErrors() == true}">
										<script type="text/javascript">
											var newInstCountOnError = 0;
										</script>				
										<s:set value="%{demandDetailBeanList.size()}" var="listSize" />										
										<s:set value="0" var="count" />						
										<s:set value="#listSize" var="j" />
										<%-- j is the each new installment start index --%>
										<s:set value="%{#j - 10}" var="j" />
										<%-- idx index value for the installmentss demand reason --%>
										<s:set value="%{#j}" var="idx" />
										<s:iterator value="demandDetailBeanList" status="demandInfoStatus">
										<!-- #idx > 0 && ((#idx % 10) == 0) && demandDetailBeanList[#idx].installment != demandDetailBeanList[#idx - 1].installment -->
										<s:if
											test="%{demandDetailBeanList[#idx].isNew == true && demandDetailBeanList[#idx - 1].isNew == true}">											
											<tr id="newInstallmentRow">
												<s:if
													test="%{demandDetailBeanList[#idx].reasonMaster == @org.egov.ptis.constants.PropertyTaxConstants@DEMANDRSN_STR_GENERAL_TAX}">
													<td class="blueborderfortd">
														<div align="center">
															<s:select id="installments"
																name="demandDetailBeanList[%{#idx}].installment.id"
																value="%{demandDetailBeanList[#idx].installment.id}"
																list="dropdownData.allInstallments" headerKey="-1"
																headerValue="%{getText('default.select')}" listKey="id"
																listValue="description" cssStyle="width:80px"
																onchange="assignInstallmentId(this, this.value)" />
														</div> <s:hidden
															name="demandDetailBeanList[%{#idx}].isNew"
															value="%{demandDetailBeanList[#idx].isNew}" />
													</td>
													<script type="text/javascript">
														newInstCountOnError++;
														isFirstInstVisible = true;														
													</script>
												</s:if>
												<s:else>
													<td class="blueborderfortd">
														<div align="center">
															<s:hidden
																name="demandDetailBeanList[%{#idx}].installment.id"
																value="%{demandDetailBeanList[#idx].installment.id}" />
															<s:hidden
																name="demandDetailBeanList[%{#idx}].isNew"
																value="%{demandDetailBeanList[#idx].isNew}" />
														</div>
													</td>
												</s:else>
												<td class="blueborderfortd">
													<div align="left">
														<s:property
															value="%{demandDetailBeanList[#idx].reasonMaster}" />
														<s:hidden
															name="demandDetailBeanList[%{#idx}].reasonMaster"
															value="%{demandDetailBeanList[#idx].reasonMaster}" />
													</div>
												</td>
												<td class="blueborderfortd">
													<div align="right">
														<s:textfield
															name="demandDetailBeanList[%{#idx}].actualAmount"
															id="revisedTax" size="10" maxlength="10"
															onblur="trim(this,this.value); checkNumber(this); isPositiveNumber(this, 'Actual Tax');"
															value="%{demandDetailBeanList[#idx].actualAmount}"
															style="text-align: right" />
													</div>
												</td>
												<td class="blueborderfortd">
													<div align="center">N/A</div>
												</td>
												<td class="blueborderfortd">
													<div align="right">
														<s:textfield
															name="demandDetailBeanList[%{#idx}].actualCollection"
															id="revisedCollection" size="10" maxlength="10"
															onblur="trim(this,this.value); checkNumber(this); isPositiveNumber(this, 'Actual Collection');"
															style="text-align: right"
															value="%{demandDetailBeanList[#idx].actualCollection}" />
													</div>
												</td>
												<td class="blueborderfortd" align="center">
													<div align="center">N/A</div>
												</td>
											</tr>
											<s:if test="%{#count == 9}" >
												<s:set value="0" var="count" />
												<s:set value="%{#j - 10}" var="j" />
												<s:set value="#j" var="idx" />
											</s:if>
											<s:else>
												<s:set value="%{#count + 1}" var="count" />
												<s:set value="%{#idx + 1}" var="idx" />
											</s:else>
											
										</s:if>
										</s:iterator>	
										<s:iterator value="demandDetailBeanList" status="demandInfoStatus">
											<s:if
												test="%{demandDetailBeanList[#demandInfoStatus.index].isNew == false || (demandDetailBeanList[#demandInfoStatus.index - 1].isNew == false && demandDetailBeanList[#demandInfoStatus.index].reasonMaster == @org.egov.ptis.constants.PropertyTaxConstants@DEMANDRSN_STR_CHQ_BOUNCE_PENALTY)}">
												<%@ include file="editDemandInstallmentDetail.jsp"%>
											</s:if>
										</s:iterator>
										<script type="text/javascript">
											lastIndex = lastIndexOnError;
											newInstallmentCount = newInstCountOnError;
										</script>
									</s:if>
									<s:else>										
										<s:iterator value="demandDetailBeanList" status="demandInfoStatus">											
												<%@ include file="editDemandInstallmentDetail.jsp" %>																				
										</s:iterator>
									</s:else>
																														
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td class="bluebox"></td>
						<td class="bluebox"  colspan="2">
							&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  <s:text name="remarks.head"></s:text><span
			class="mandatory1">*</span> :
						</td>
						<td class="bluebox" colspan="2">
							<s:textarea name="remarks" id="remarks" cols="60" onkeypress="checkTextAreaLength(this, 256)"></s:textarea>
						</td>
					</tr> 
				</table>
			</div>
			<div id="loadingMask" style="display:none"><p align="center"><img src="/egi/images/bar_loader.gif"> <span id="message"><p style="color: red">Please wait....</p></span></p></div>
		 	<div class="buttonbottom" align="center">
				<s:submit name="Update" value="Update" cssClass="buttonsubmit" method="update" onclick="doLoadingMask();"/>				
				<input class="button" type="button" name="close" value="Close"
					onclick="window.close();" />
			</div> 
		</s:form>
	</div>	
	<s:if test="%{hasActionErrors() == false || isFirstInstVisible == false}">
		<script type="text/javascript">
			//alert('isFirstInstVisible' + isFirstInstVisible);
			//rearrangeIndexes();	
		</script>
	</s:if>
</body>
</html>
