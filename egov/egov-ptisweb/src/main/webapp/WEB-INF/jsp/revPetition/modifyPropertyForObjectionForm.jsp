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
	<tr>
		<td class="bluebox2" width="5%">
			&nbsp;
		</td>
		<td class="bluebox" width="20%">
			<s:text name="prop.Id" />
			:
		</td>
		<td class="bluebox" width="15%">
			<span class="bold"><s:property default="N/A"
					value="%{basicProp.upicNo}" /> </span>						
		</td>
		<td class="bluebox" width="20%">
			&nbsp;
		</td>
		<td class="bluebox" width="20%">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td class="greybox">
			&nbsp;
		</td>
		<td class="greybox">
			<s:text name="OwnerName" />
			:
		</td>
		<td class="greybox">
			<span class="bold"><s:property default="N/A"
					value="%{ownerName}" /> </span>
		</td>
		<td class="greybox">
			<s:text name="MobileNumber" />:
		</td>
		<td class="greybox">
			<span class="bold"><s:property value="%{basicProp.mobileNumber}" default="N/A"/></span>
		</td>
	</tr>
<tr>
		<td class="bluebox2">
			&nbsp;
		</td>
		<td class="bluebox" width="8%">
			<s:text name="PropertyAddress" />
			:
		</td>
		<td class="bluebox">
			<span class="bold"><s:property default="N/A"
					value="%{propAddress}" /> </span>
		</td>
		<td class="bluebox">
			&nbsp;
		</td>
		<td class="bluebox">
			&nbsp;
		</td>
	</tr>
<tr>
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="assessmentDetails.title"/></span>
			</div>
		</td>
	</tr>
	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="extent.site"/> :</td>
		<td class="greybox" width="">
		   <span class="bold">
		   <s:hidden id="property.propertyDetail.id" name="property.propertyDetail.id" value="%{property.propertyDetail.id}" />
			 <s:hidden id="property.id" name="property.id" value="%{property.id}" />
							
		   <s:property value="%{property.propertyDetail.extentSite}" default="N/A"/></span>
		</td>
		<td class="greybox" width="25%"></td>
		<td class="greybox">
		</td>
	</tr>

	<tr>
		<td class="bluebox" width="5%">&nbsp;</td>
		<td class="bluebox" width="25%"><s:text name="reg.docno"/> :</td>
		<td class="bluebox" width="">
			<span class="bold"><s:property value="%{basicProp.regdDocNo}" default="N/A"/></span>
		</td>
		<td class="bluebox" width="25%"><s:text name="reg.docdate"/> :</td>
		<td class="bluebox">
			<span class="bold"><s:property value="%{basicProp.regdDocDate}" default="N/A"/></span>
		</td>
	</tr>
	
	<tr>
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="superstructure"></s:text> :</td>
		<td class="greybox">
			<s:checkbox name="property.propertyDetail.structure" id="property.propertyDetail.structure" disabled="true"/>
			<s:hidden name="property.propertyDetail.structure" value="%{property.propertyDetail.structure}"/>
		</td>
		<td class="greybox siteowner"><s:text name="siteowner"></s:text>:</td>
		<td class="greybox siteowner">
			<span class="bold"><s:property value="%{property.propertyDetail.siteOwner}" default="N/A"/></span>
			<s:hidden name="property.propertyDetail.siteOwner" value="%{property.propertyDetail.siteOwner}"/>
		</td>
	</tr>
	
	<tr>
		<td class="bluebox">&nbsp;</td>
		<td class="bluebox" width="25%"><s:text name="ModifyReason"></s:text> <span
			class="mandatory1">*</span> :</td>
		<td class="bluebox" width=""><s:select headerKey="-1"
				headerValue="%{getText('default.select')}" name="reasonForModify"
				id="reasonForModify" listKey="code" listValue="mutationName"
				list="dropdownData.MutationList" value="%{reasonForModify}"
				cssClass="selectnew"/>
		</td>
		<td class="bluebox" colspan="2">
			&nbsp;
		</td>
	</tr>
	
	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="ownership.type"></s:text>
			<span class="mandatory1">*</span> :</td>
		<td class="greybox" width=""><s:select headerKey="-1" headerValue="%{getText('default.select')}" name="propTypeId"
				id="propTypeId" listKey="id" listValue="type" list="dropdownData.PropTypeMaster" value="%{property.propertyDetail.propertyTypeMaster.id}"
				cssClass="selectnew" onchange="populatePropTypeCategory();toggleFloorDetails();enableFieldsForPropType();" /></td>
			<s:hidden id="property.propertyDetail.propertyTypeMaster.type" name="property.propertyDetail.propertyTypeMaster.type" value="%{property.propertyDetail.propertyTypeMaster.type}" />
				
		<td class="greybox" width="25%"><s:text name="property.type"></s:text>
			<span class="mandatory1" id="prntMandatory">*</span> :</td>
		<egov:ajaxdropdown id="propTypeCategoryId" fields="['Text','Value']" dropdownId="propTypeCategoryId"
			url="/common/ajaxCommon-propTypeCategoryByPropType.action" />
		<td class="greybox">
		   <s:select headerKey="-1"	headerValue="%{getText('default.select')}" name="property.propertyDetail.categoryType"
				id="propTypeCategoryId" listKey="key" listValue="value" list="propTypeCategoryMap" value="%{property.propertyDetail.categoryType}"
				cssClass="selectnew"/>
		</td>
	</tr>
	<tr id="apartmentRow">
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="apartcomplex.name"></s:text> :</td>
		<td class="greybox"><s:select headerKey=""
				headerValue="%{getText('default.select')}" 	name="property.propertyDetail.apartment" id="property.propertyDetail.apartment.id"
				listKey="id" listValue="name" value="%{property.propertyDetail.apartment.id}"
				list="dropdownData.apartments" cssClass="selectnew" /></td>
		<td class="greybox" colspan="2">
			&nbsp;			
		</td>
	</tr>
	
	<tr class="appurtenant">
		<td class="bluebox">&nbsp;</td>
		<td class="bluebox"><s:text name="extent.appurtntland" /> : 
		<td class="bluebox"><s:checkbox name="property.propertyDetail.appurtenantLandChecked" id="appurtenantLandChecked"
				value="%{property.propertyDetail.appurtenantLandChecked}" onclick="enableAppartnaumtLandDetails();" />
		</td>
		<td class="bluebox"><s:text name="certificationNumber"></s:text>:</td>
		<td class="bluebox"><s:textfield maxlength="64" name="property.propertyDetail.occupancyCertificationNo" id="certificationNumber" value="%{property.propertyDetail.occupancyCertificationNo}"/></td>
	</tr>
	<tr id="appurtenantRow">
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="extent.appurtntland"></s:text>
			<span class="mandatory1">*</span> :</td>
		<td class="greybox"><s:textfield name="property.propertyDetail.extentAppartenauntLand" id="property.propertyDetail.extentAppartenauntLand"
				value="%{property.propertyDetail.extentAppartenauntLand}" size="12"	maxlength="12" onchange="trim(this,this.value);"
				onblur="validNumber(this);checkZero(this);"></s:textfield>
		</td>
		<td class="bluebox" colspan="2">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td class="bluebox">&nbsp;</td>
		<td class="bluebox"><s:text name="builidingdetails"></s:text> :</td>
		<td class="bluebox">
		 <s:checkbox name="property.propertyDetail.buildingPlanDetailsChecked" id="buildingPlanDetailsChecked"
			value="%{property.propertyDetail.buildingPlanDetailsChecked}" onclick="enableOrDisableBPADetails(this);" />
		</td>
		<td class="bluebox" colspan="2">
			&nbsp;
		</td>
	</tr>
	<tr class="bpddetails">
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="building.permNo"></s:text> :</td>
		<td class="greybox"><s:textfield name="property.propertyDetail.buildingPermissionNo" id="property.propertyDetail.buildingPermissionNo" size="12" maxlength="12"
				onchange="trim(this,this.value);" onblur="checkZero(this);" value="%{property.propertyDetail.buildingPermissionNo}"></s:textfield>
		</td>
		<td class="greybox"><s:text name="buildingpermdate"></s:text> :</td>
		<td class="greybox"><s:date name="property.propertyDetail.buildingPermissionDate" var="buildingPermDate" format="dd/MM/yyyy" /> 
		<s:textfield name="property.propertyDetail.buildingPermissionDate" cssClass="datepicker" value="%{#buildingPermDate}" autocomplete="off"
				id="property.propertyDetail.buildingPermissionDate" size="12" maxlength="12"></s:textfield>
		</td>

	</tr>
	
	  <tr class="bpddetails">
		<td class="bluebox">&nbsp;</td>
		<td class="bluebox"><s:text name="deviationper"></s:text> :</td>
		<td class="bluebox"><s:select headerKey="-1" headerValue="%{getText('default.select')}" name="property.propertyDetail.deviationPercentage"
				id="property.propertyDetail.deviationPercentage" listKey="key" listValue="value" list="deviationPercentageMap" value="%{property.propertyDetail.deviationPercentage}"
				cssClass="selectnew"/>
		</td>
		<td class="bluebox" colspan="2">
			&nbsp;
		</td>
   </tr>
   
	
	
	<!-- Amenities section -->
	
	
	<tr id="amenitiesHeaderRow" class="amenities">
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
				<%@ include file="../common/amenitiesFormForRevPetition.jsp"%>
			</div>
		</td>
	</tr>
			<!-- Floor type details -->
	
	<tr id="constructionHeaderRow" class="construction">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="title.constructiontypes"/></span>
			</div>
		</td> 
	</tr>
	
	<tr class="construction">
		<td colspan="5">
			<div id="AmenitiesDiv">
				<%@ include file="../common/constructionFormForRevPetition.jsp"%>
			</div>
		</td>
	</tr>

	<tr class="floordetails">
		<td colspan="5">
			<div align="center">
				<%@ include file="../common/FloorFormForRevisionPetition.jsp"%>
				<br/>
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
				<%@ include file="../common/vacantLandFormForRevisionPetition.jsp"%>
			</div>
		</td>
	</tr>
	 <tr class="vacantlanddetaills">
		<td colspan="5">
			<%@ include file="../common/DocumentUploadForm.jsp"%>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function populatePropTypeCategory() {
		populatepropTypeCategoryId({
			propTypeId : document.getElementById("propTypeId").value
		});
	}
	function enableAppartnaumtLandDetailsView() {
		if (document.forms[0].appurtenantLandChecked.checked == true) {
			jQuery('tr.vacantlanddetaills').show();
			jQuery('#appurtenantRow').show();
			jQuery('tr.floordetails').show();
			jQuery('tr.extentSite').hide();
		} else {
			enableFieldsForPropTypeView();
		}
	}

	function enableFieldsForPropTypeView() {
		var propType = '<s:property value="%{property.propertyDetail.propertyTypeMaster.type}"/>';
		if (propType != "select") {
			//onChangeOfPropertyTypeFromMixedToOthers(propType);
			if (propType == "Vacant Land") {
				
				jQuery('tr.floordetails').hide();
				jQuery('tr.vacantlanddetaills').show();
				jQuery('tr.construction').hide();
				jQuery('tr.amenities').hide();
				jQuery('#appurtenantRow').hide();
				jQuery('tr.extentSite').hide();
				jQuery('tr.appurtenant').hide();
			} else {
				
				jQuery('tr.floordetails').show();
				jQuery('tr.vacantlanddetaills').hide();
				jQuery('tr.construction').show();
				jQuery('tr.amenities').show();
				jQuery('#appurtenantRow').hide();
				jQuery('tr.extentSite').show();
				jQuery('tr.appurtenant').show();
			}
		}
	}

	function toggleFloorDetailsView() {
		var propType = '<s:property value="%{property.propertyDetail.propertyTypeMaster.type}"/>';
		
		if (propType == "Vacant Land") {
			jQuery('tr.floordetails').hide();
		} else {
			jQuery('tr.floordetails').show();
		}
		if (propType == "Apartments") {
			alert("Please select Apartment/Complex Name");
		}
	}
	
	//hide rows and columns of fields
	jQuery('td.siteowner').hide();
	jQuery('tr.bpddetails').hide();
	jQuery('tr.vacantlanddetaills').hide();
    
</script>