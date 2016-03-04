<!-------------------------------------------------------------------------------
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
------------------------------------------------------------------------------->
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr id="assessmentRow" >
						<td class="greybox" width="5%">&nbsp;</td>
						<td class="greybox" width="25%"><s:text name="assessmentno"></s:text>
							<span class="mandatory1">*</span> :</td>
						<td class="greybox" width="25%"><s:textfield name="upicNo"
								id="upicNo" size="12" maxlength="12"></s:textfield></td>
						<td class="greybox" width="25%">&nbsp;</td>
						<td class="greybox" width="20%"></td>
					</tr>
	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="ownership.type"></s:text>
			<span class="mandatory1">*</span> :</td>
		<td class="greybox" width=""><s:select headerKey="-1" headerValue="%{getText('default.select')}" name="propTypeId"
				id="propTypeId" listKey="id" listValue="type" list="dropdownData.PropTypeMaster" value="%{propTypeId}"
				cssClass="selectnew" onchange="populatePropTypeCategory();toggleFloorDetails();enableFieldsForPropType();" title="Type of ownership"/></td>
				
		<td class="greybox" width="25%"><s:text name="property.type"></s:text>
			<span class="mandatory1" id="prntMandatory">*</span> :</td>
		<egov:ajaxdropdown id="propTypeCategoryId" fields="['Text','Value']" dropdownId="propTypeCategoryId"
			url="/common/ajaxCommon-propTypeCategoryByPropType.action" />
		<td class="greybox">
		   <s:select headerKey="" headerValue="%{getText('default.select')}" name="propertyDetail.categoryType"
				id="propTypeCategoryId" listKey="key" listValue="value" list="propTypeCategoryMap" value="%{propertyDetail.categoryType}"
				cssClass="selectnew" onchange="populateUsages();" title="Different types of properties"/>
		   <s:hidden name="propertyCategory" id="propertyCategory"/>
		</td>
	</tr>
	
	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox apartmentRow" width="25%"><s:text name="apartcomplex.name"></s:text> :</td>
		<td class="greybox apartmentRow" width=""><s:select headerKey=""
				headerValue="%{getText('default.select')}" 	name="propertyDetail.apartment" id="apartment"
				listKey="id" listValue="name" value="%{propertyDetail.apartment.id}"
				list="dropdownData.apartments" cssClass="selectnew"  title="In Which property belongs to"/></td>
		<td class="greybox" width="25%"><s:text name="exemptioncategory"/> :</td>
		<td class="greybox">
		<s:select name="taxExemptionId" id="taxExemptionId" headerValue="select"
				headerKey="" list="dropdownData.taxExemptionReasonList" value="%{taxExemptionId}" 
				listKey="id" listValue="name" cssClass="selectnew" data-optional="1">
			</s:select>
		</td>
	</tr>

	<!-- Owner details section -->
	<tr>
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="ownerdetails.title"></s:text></span>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="5">
			<div id="OwnerNameDiv">
				<%@ include file="../common/OwnerNameForm.jsp"%>
			</div>
		</td>
	</tr>
	

<!-- property address section -->

	<tr>
		<td>
			<div id="PropAddrDiv">
				<%@ include file="../common/PropAddressForm.jsp"%>
			</div>
		</td>
	</tr>

	<tr>
		<td colspan="5">
			<div id="CorrAddrDiv">
				<%@ include file="../common/CorrAddressForm.jsp"%>
			</div>
		</td>
	</tr>

	<!-- assessment details section -->
	
	<tr>
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="assessmentDetails.title"/></span>
			</div>
		</td>
	</tr>
	<tr>
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="rsnForCreatin" /> <span
			class="mandatory1">*</span> :</td>
		<td class="greybox"><s:select headerKey="-1" headerValue="%{getText('default.select')}" name="mutationId"
				id="mutationId" listKey="id" listValue="mutationName" list="dropdownData.MutationList" value="%{mutationId}"
				cssClass="selectnew" onchange="makeMandatory();" /></td>
		<td class="greybox parentIndexText"><s:text name="prntPropAssessmentNum" /> <span
			class="mandatory1" id="prntMandatory">*</span> :</td>
		<td class="greybox parentIndexText"><s:textfield name="parentIndex" id="parentIndex" size="12" maxlength="10" onblur="validNumber(this);checkZero(this,'Parent Index');"></s:textfield></td>

	</tr>

	<tr class="extentSite">
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="extent.site"></s:text> <span
			class="mandatory1">*</span> :</td>
		<td class="greybox" width=""><s:textfield name="areaOfPlot" title="Extent of Site of the Property" id="areaOfPlot" size="12" maxlength="8" value="%{areaOfPlot}" 
		onblur="trim(this,this.value);checkForTwoDecimals(this,'extent of site');checkZero(this,'extent of site');"></s:textfield></td>
		
	</tr>
 
   <tr class="appurtenant">
		<td class="greybox">&nbsp;</td>
		<td class="bluebox"><s:text name="isextent.appurtntland" /> : 
		<td class="bluebox"><s:checkbox name="propertyDetail.appurtenantLandChecked" title="If property belongs to Appurtenent land" id="appurtenantLandChecked"
				value="%{propertyDetail.appurtenantLandChecked}" onclick="enableAppartnaumtLandDetails();" onfocus="makePropertyTypeMandatory();" />
		</td>
		<td class="greybox"><s:text name="certificationNumber"></s:text>:</td>
		<td class="greybox"><s:textfield maxlength="64" title="Property certification number" name="propertyDetail.occupancyCertificationNo" id="propertyDetail.occupancyCertificationNo" value="%{propertyDetail.occupancyCertificationNo}"></s:textfield></td>
	</tr>

	<tr id="appurtenantRow">
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="extent.appurtntland"></s:text>
			<span class="mandatory1">*</span> :</td>
		<td class="greybox"><s:textfield name="propertyDetail.extentAppartenauntLand" id="extentAppartenauntLand"
				value="%{propertyDetail.extentAppartenauntLand}" size="12"	maxlength="8" onchange="trim(this,this.value);"
				onblur="validNumber(this);checkZero(this);"></s:textfield></td>
	</tr>
	
	<tr class="superStructureRow">
		<td class="greybox">&nbsp;</td>
		<td class="bluebox"><s:text name="superstructure"></s:text> :</td>
		<td class="bluebox">
		 <s:checkbox name="propertyDetail.structure" title="Select if property is super structure" id="propertyDetail.structure"
			value="%{propertyDetail.structure}" onclick="enableOrDisableSiteOwnerDetails(this);" />
		</td>
		<td class="greybox siteowner"><s:text name="siteowner"></s:text>
			<span class="mandatory1">*</span>:
		</td>
		<td class="greybox siteowner"><s:textfield maxlength="32" value="%{propertyDetail.siteOwner}"
				name="propertyDetail.siteOwner" id="siteOwner"></s:textfield></td>
	</tr>
	
	<tr>
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="reg.docno"></s:text><span class="mandatory1">*</span> :</td>
		<td class="greybox"><s:textfield name="basicProperty.regdDocNo" id="regdDocNo"
				value="%{basicProperty.regdDocNo}" size="16" maxlength="16"
				onchange="trim(this,this.value);" onblur="checkZero(this);validateRegDocNumber(this,'Registration Doc No')"></s:textfield>
		</td>
		<td class="greybox"><s:text name="reg.docdate"></s:text><span class="mandatory1">*</span> :</td>
		<td class="greybox"><s:date name="basicProperty.regdDocDate" var="docDate" format="dd/MM/yyyy" />
		 <s:textfield name="basicProperty.regdDocDate" title="Document dated" id="basicProperty.regdDocDate" value="%{#docDate}" size="12" autocomplete="off"
				maxlength="12" cssClass="datepicker"></s:textfield></td>
	</tr>

	<!-- Amenities section -->

	<tr class="amenities">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"> <s:text name="amenities"></s:text>
				</span>
			</div>
		</td>
	</tr>

	<tr class="amenities">
		<td colspan="5">
			<div id="AmenitiesDiv">
				<%@ include file="../common/amenitiesForm.jsp"%>
			</div>
		</td>
	</tr>

	<!-- Floor type details -->

	<tr class="construction">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="title.constructiontypes" /></span>
			</div>
		</td>
	</tr>
	<%@ include file="../common/constructionForm.jsp"%>

	<tr class="floordetails">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="FloorDetailsHeader" /> </span>
			</div>
		</td>
	</tr>

	<!-- Floor Details Section -->

	<tr class="floordetails">
		<td colspan="5">
			<div align="center">
				<%@ include file="../common/FloorForm.jsp"%>
			</div>
		</td>
	</tr>

	<tr id="vacantLandRow" class="vacantlanddetaills">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="VacantLandDetailsHeader" /> </span>
			</div>
		</td>
	</tr>

	<tr class="vacantlanddetaills">
		<td colspan="5">
			<div align="center">
				<%@ include file="../common/vacantLandForm.jsp"%>
			</div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function showDocumentManager() {
			var docNum = document.getElementById("docNumber").value;
			var url;
			if (docNum == null || docNum == '' || docNum == 'To be assigned') {
				url = "/egi/docmgmt/basicDocumentManager.action?moduleName=ptis";
			} else {
				url = "/egi/docmgmt/basicDocumentManager!editDocument.action?docNumber="
						+ docNum + "&moduleName=ptis";
			}
			window.open(url, 'docupload', 'width=1000,height=400');
		}
		
	function populateWard() {
		populatewardId({
			zoneId : document.getElementById("zoneId").value
		});
		document.getElementById("areaId").options.length = 0;
		document.getElementById("areaId").value = "select";
	}
	function populateArea() {
		populateareaId({
			wardId : document.getElementById("wardId").value
		});
	}
	function populatePropTypeCategory() {
		populatepropTypeCategoryId({
			propTypeId : document.getElementById("propTypeId").value
		});
	}
 	function populateLocationFactors() {
		populatelocationFactor({
			wardId : document.getElementById("wardId").value
		});
	} 
    
</script>
