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
<%@ taglib prefix="s" uri="/WEB-INF/taglib/struts-tags.tld" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="5">
			<div class="headingsmallbg">
				<s:text name="objection.add.hearingDate" />
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="5">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{hearings == null}"> <s:set var="hearingIdx" value="0"/></s:if>
				<s:elseif test="%{state.text1.equalsIgnoreCase(@org.egov.ptis.constants.PropertyTaxConstants@OBJECTION_HEARINGDATE_SAVED)}">  <s:set var="hearingIdx" value="%{hearings.size()-1}"/> </s:elseif>
				<s:else>  <s:set var="hearingIdx" value="%{hearings.size()}"/>  </s:else>
				<tr>
					<td class="bluebox" width="10%">
						<s:text name="objection.planned.hearingDate" />
						<span class="mandatory1">*</span>
					</td>
					<td class="bluebox" width="25%">
						<s:date name="objection.hearings[%{hearingIdx}].plannedHearingDt"
							id="plannedHearingDtId" format="dd/MM/yyyy" />
						<s:textfield name="objection.hearings[%{hearingIdx}].plannedHearingDt"
							id="plannedHearingDt" value="%{plannedHearingDtId}" maxlength="10"
							onkeyup="DateFormat(this,this.value,event,false,'3')" size="10" />
						<s:hidden name="objection.hearings[%{hearingIdx}].id" id="objection.hearings[%{hearingIdx}].id"/>	
						<a
							href="javascript:show_calendar('objectionViewForm.plannedHearingDt',null,null,'DD/MM/YYYY');"
							style="text-decoration: none">&nbsp;<img
								src="/egi/resources/erp2/images/calendaricon.gif"
								border="0" /> </a>(dd/mm/yyyy)
					</td>
					<td class="bluebox" width="25%" colspan="3"></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
