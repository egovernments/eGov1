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

<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title><s:text name='CreateAck.title' /></title>
<script type="text/javascript">
	function onSubmit(action) {
		document.forms[0].action = action;
		document.forms[0].submit;
		return true;
	}
</script>
</head>

<body onload=" refreshParentInbox();">
	<s:form name="CreatePropertyAckForm" theme="simple">
		<s:push value="model">
			<s:token />
			<div class="formmainbox">
				<div class="headingbg">
					<s:text name="CreatePropertyAckHeader" />
				</div>
				<br />
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<s:hidden name="modelId" id="modelId" value="%{id}" />
					<tr>
						<s:if test="mode == 'create' && @org.egov.ptis.constants.PropertyTaxConstants@ASSISTANT_DESGN.equalsIgnoreCase(userDesgn)">
							<s:if test="%{basicProperty.applicationNo != null && !basicProperty.applicationNo.isEmpty()}">
								<td colspan="5" style="font-size: 15px;" align="center"><span
									class="bold"><s:property value="%{ackMessage}" /> <s:property
											value="%{basicProperty.applicationNo}" /></span></td>
							</s:if>
						</s:if>
						<s:elseif test="%{(@org.egov.ptis.constants.PropertyTaxConstants@WFLOW_ACTION_STEP_COMMISSIONER_APPROVED.equalsIgnoreCase(model.state.nextAction) ||
						       @org.egov.ptis.constants.PropertyTaxConstants@WFLOW_ACTION_STEP_REVENUE_OFFICER_APPROVED.equalsIgnoreCase(model.state.nextAction)) && approved == true}">
							<span class="bold"><s:property value="%{ackMessage}" /> <s:property
									value="%{basicProperty.upicNo}" /> </span>
						</s:elseif>
						<s:else>
							<td colspan="5" style="font-size: 15px;" align="center">
							<span class="bold"><s:property value="%{ackMessage}" /><s:property value="%{basicProperty.applicationNo}" /></span></td>
						</s:else>
					</tr>
					<tr>
						<td>
							<div class="buttonbottom" align="center">
								<s:if test="mode == 'create'">
									<s:if test="%{userDesgn.equalsIgnoreCase(@org.egov.ptis.constants.PropertyTaxConstants@ASSISTANT_DESGN)}">
										<s:submit value="Print" name="PrintAck" id="PrintAck"
											method="printAck" cssClass="buttonsubmit" align="center"
											onclick="return onSubmit('createProperty-printAck.action');" />
									</s:if>
								</s:if>
								<input type="button" class="button" onclick="window.close();" value="Close">
							</div>
						</td>
					</tr>
				</table>
				<br />
			</div>
		</s:push>
	</s:form>
</body>
</html>
