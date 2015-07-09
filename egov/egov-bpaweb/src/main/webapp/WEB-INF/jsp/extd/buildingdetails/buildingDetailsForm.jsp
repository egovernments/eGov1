#-------------------------------------------------------------------------------
# <!-- #-------------------------------------------------------------------------------
# # eGov suite of products aim to improve the internal efficiency,transparency, 
# #    accountability and the service delivery of the government  organizations.
# # 
# #     Copyright (C) <2015>  eGovernments Foundation
# # 
# #     The updated version of eGov suite of products as by eGovernments Foundation 
# #     is available at http://www.egovernments.org
# # 
# #     This program is free software: you can redistribute it and/or modify
# #     it under the terms of the GNU General Public License as published by
# #     the Free Software Foundation, either version 3 of the License, or
# #     any later version.
# # 
# #     This program is distributed in the hope that it will be useful,
# #     but WITHOUT ANY WARRANTY; without even the implied warranty of
# #     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# #     GNU General Public License for more details.
# # 
# #     You should have received a copy of the GNU General Public License
# #     along with this program. If not, see http://www.gnu.org/licenses/ or 
# #     http://www.gnu.org/licenses/gpl.html .
# # 
# #     In addition to the terms of the GPL license to be adhered to in using this
# #     program, the following additional terms are to be complied with:
# # 
# # 	1) All versions of this program, verbatim or modified must carry this 
# # 	   Legal Notice.
# # 
# # 	2) Any misrepresentation of the origin of the material is prohibited. It 
# # 	   is required that all modified versions of this material be marked in 
# # 	   reasonable ways as different from the original version.
# # 
# # 	3) This license does not grant any rights to any user of the program 
# # 	   with regards to rights under trademark law for use of the trade names 
# # 	   or trademarks of eGovernments Foundation.
# # 
# #   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
# #------------------------------------------------------------------------------- -->
#-------------------------------------------------------------------------------
<%@ include file="/includes/taglibs.jsp" %>
<div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="bluebox" width="13%">&nbsp;</td>
			<td class="bluebox" width="13%" id="mandatoryfields"><s:text name="housingUnitType" /> : <span class="mandatory" >*</span></td>
			<td class="bluebox" width="27%" >
				<div id="unitType"><s:select name="unitClassification" id="unitClassification" value="%{unitClassification}" 
				list="unitClsfnList" listKey="key" listValue="value" headerKey="-1" headerValue="--------Choose--------" onblur="onchangeofdropdown()" /></div></td>
			<td class="bluebox">&nbsp;</td>
			<td class="bluebox" width="13%" id="mandatoryfields"><s:text name="NoOfHousingUnit" /> :</td>
			<td class="bluebox" "><s:textfield id="unitCount" name="unitCount" value="%{unitCount}" onblur="validateIsNan(this)"/></td>
		</tr> 	
		<tr>
			<td class="greybox" width="13%">&nbsp;</td>
			<td class="greybox" width="13%" id="mandatoryfields"><s:text name="NoOfStorey" /> : <span class="mandatory" >*</span></td>
			<td class="greybox" width="27%" >
				<s:textfield id="floorCount" name="floorCount" value="%{floorCount}"  onchange="validateIsNan(this);checkZero(this,'floorCount');loadFloorDetailJs(value);" />
			</td>
			<td class="greybox">&nbsp;</td>
			<td class="greybox" width="13%"><s:text name="isBasementHousingUnit" /> : </td>
			<td class="greybox">
				<s:checkbox name="isBasementUnit" id="isBasementUnit" value="%{isBasementUnit}" onchange="loadFloorDetailJsforBasement()" />
			</td>
		</tr> 
		<tr>
			<td class="bluebox" width="13%">&nbsp;</td>
			<td class="bluebox" width="13%" id="mandatoryfields"><s:text name="HtOfBldg" /> : <span class="mandatory" >*</span></td>
			<td class="bluebox" width="27%" >
				<s:textfield id="buildingHeight" name="buildingHeight" value="%{buildingHeight}" onblur="validateIsNan(this)"  />
			</td>
			<td class="bluebox">&nbsp;</td>
		 	<td class="bluebox" width="13%" id="mandatoryfields"><s:text name="totalFlrArea" /> : <span class="mandatory" >*</span></td>
			<td class="bluebox" ><s:textfield id="totalFloorArea"  name="totalFloorArea" value="%{totalFloorArea}" onblur="validateIsNan(this)"/></td>
		</tr>
		<tr><td colspan="6"><div class="headingsmallbg">
          	<span class="bold"><s:text name="floorDetails"/>
        </div></td></tr>
    <tr>
      <td colspan="6"><div align="center">
     <table width="100%" border="0" cellpadding="0" cellspacing="0" class="tablebottom" id="floorDetails" >
      
        <tr>
   			<th  class="bluebgheadtd" width="3%"><div align="center">Sl No</div></th>
		 	<th class="bluebgheadtd" width="9%"><s:text name="floorNum" /><span class="mandatory" id="floorNoReqd">*</span></th>
          	<th class="bluebgheadtd" width="3%"><s:text name="existBldgArea" /><span class="mandatory" id="existBldgAreaReqd">*</span></th>
         	<th class="bluebgheadtd" width="15%" ><s:text name="existBldgUsage" /><span class="mandatory" id="existBldgUsgReqd">*</span></th>
          	<th class="bluebgheadtd" width="15%"><s:text name="bldgArea" /><span class="mandatory" id="propBldgAreaReqd">*</span></th>
          	<th class="bluebgheadtd" width="17%"><s:text name="bldgUsage" /><span class="mandatory" id="propBldgUsgReqd">*</span></th>
     		<s:if test="%{mode != 'view'}">      
     		<th class="bluebgheadtd" width="10%" id="AddRemoveFloorLabel">Add/ Delete</th>    	
    		</s:if>
        </tr>

   		<s:iterator value="builflorlsList" status="row_status" >
	   		<tr id="Floorinfo"   >
	     		<td  class="blueborderfortd"><s:textfield name="builflorlsList[%{#row_status.index}].srlNo" id="srlNo" readonly="true"  cssClass="tablerow" value="%{#row_status.count}" cssStyle="text-align:center"/></td>
				<td class="blueborderfortd" ><s:select name="builflorlsList[%{#row_status.index}].floorNum" id="floorNum"  list="floorNoMap" listKey="key" listValue="value" headerKey="-10" headerValue="----Choose------"/></td>
	 			<td class="blueborderfortd"><s:textfield name="builflorlsList[%{#row_status.index}].existingBldgArea" id="existingBldgArea" cssClass="tablerow" maxlength="10" onblur="validateIsNan(this);" /></td>
				<td class="blueborderfortd" ><s:select id="existBldgUsage" name="builflorlsList[%{#row_status.index}].existingBldgUsage.id" listKey="id" listValue="description" list="dropdownData.bldgUsageMstrList" headerKey="-1" headerValue="----Choose------"/></td>
	 			<td class="blueborderfortd"><s:textfield name="builflorlsList[%{#row_status.index}].proposedBldgArea" id="proposedBldgArea" cssClass="tablerow" maxlength="10" onblur="validateIsNan(this);" /></div></td>
				<td class="blueborderfortd" ><s:select id="propBldgUsage" name="builflorlsList[%{#row_status.index}].proposedBldgUsage.id" listKey="id" listValue="description" list="dropdownData.bldgUsageMstrList" headerKey="-1" headerValue="----Choose------"/></td>
				<s:if test="%{mode != 'view'}">        
	            <td class="blueborderfortd"  id = "AddRemoveFloor" >
	                <img id="addF" name="addF" src="${pageContext.request.contextPath}/images/addrow.gif" alt="Add" onclick="javascript:addFloor(); return false;" width="18" height="18" border="0"  />
	                <img id="dDelF" name="dDelF" src="${pageContext.request.contextPath}/images/removerow.gif" alt="Remove" onclick="javascript:delFloor(this);return false;" width="18" height="18" border="0" />
	             </td>         	
	          	</s:if>
	          	<s:hidden id="cmdaddListId" name="builflorlsList[%{#row_status.index}].id" />
	        </tr>
   		</s:iterator>
   	
    
</table>
</div>

