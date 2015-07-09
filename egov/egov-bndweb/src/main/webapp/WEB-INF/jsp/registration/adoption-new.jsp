#-------------------------------------------------------------------------------
# /*******************************************************************************
#  *    eGov suite of products aim to improve the internal efficiency,transparency,
#  *    accountability and the service delivery of the government  organizations.
#  *
#  *     Copyright (C) <2015>  eGovernments Foundation
#  *
#  *     The updated version of eGov suite of products as by eGovernments Foundation
#  *     is available at http://www.egovernments.org
#  *
#  *     This program is free software: you can redistribute it and/or modify
#  *     it under the terms of the GNU General Public License as published by
#  *     the Free Software Foundation, either version 3 of the License, or
#  *     any later version.
#  *
#  *     This program is distributed in the hope that it will be useful,
#  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
#  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  *     GNU General Public License for more details.
#  *
#  *     You should have received a copy of the GNU General Public License
#  *     along with this program. If not, see http://www.gnu.org/licenses/ or
#  *     http://www.gnu.org/licenses/gpl.html .
#  *
#  *     In addition to the terms of the GPL license to be adhered to in using this
#  *     program, the following additional terms are to be complied with:
#  *
#  * 	1) All versions of this program, verbatim or modified must carry this
#  * 	   Legal Notice.
#  *
#  * 	2) Any misrepresentation of the origin of the material is prohibited. It
#  * 	   is required that all modified versions of this material be marked in
#  * 	   reasonable ways as different from the original version.
#  *
#  * 	3) This license does not grant any rights to any user of the program
#  * 	   with regards to rights under trademark law for use of the trade names
#  * 	   or trademarks of eGovernments Foundation.
#  *
#  *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#  ******************************************************************************/
#-------------------------------------------------------------------------------
<%@ taglib prefix="s" uri="/WEB-INF/struts-tags.tld" %>  
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sx" uri="/WEB-INF/struts-dojo-tags.tld"%>
<%@ include file="/includes/taglibs.jsp" %>
<% 
   response.setHeader("Cache-Control","no-cache"); //HTTP 1.1 
   response.setHeader("Pragma","no-cache"); //HTTP 1.0 
   response.setDateHeader ("Expires", 0); //prevents caching at the proxy server 
%>

<html>
  <head>  
    <title><s:text name="birthRegistration.form"/></title>
    <sx:head />
    <jsp:include page='/WEB-INF/jsp/registration/adoption.jsp'/>
  </head>
  
<body onload="bodyonlaod();refreshInbox();">
<div class="errorstyle" id="birthRegistration_error" style="display:none;">
</div>
<s:if test="%{hasErrors()}">
		<div class="errorstyle">
			<s:actionerror />
			<s:fielderror/>	
		</div>
</s:if>
	
	<s:if test="%{hasActionMessages()}">
		<div class="errorstyle">
			<s:actionmessage />
		</div>
	</s:if>
 <s:form action="adoption" theme="css_xhtml" name="adoption" 
 		validate="true" onKeyPress="return disableEnterKey(event);" onsubmit="enablingFields();">
	<div class="formheading"/></div>
	<s:token />
	 <s:push value="model">
	 <s:hidden id="defaultState" name="defaultState" value="%{defaultState.id}"/>
	  <s:hidden id="idTemp" name="idTemp" value="%{idTemp}"/>
	   <s:hidden id="mode" name="mode" value="%{mode}"/>
	 	<div class="blueshadow"></div>
     	<h1 class="subhead" ><s:text name="regform.heading.part1"/></h1>
     	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tablebottom" border="1">
         <tr>
	 			<td class="bluebox" width="19%">&nbsp;</td>
				<td class="bluebox" width="15%"><s:text name="registration.unit"/></td>
	   			<td class="bluebox" width="20%">
	   				<s:select name="registrationUnit" id ="registrationUnit" list="dropdownData.registrationUnitList" listKey="id" listValue="regUnitDesc" headerKey="-1" headerValue="----choose----" value="%{registrationUnit.id}" disabled="true"/>
	   			</td>
	   			<td class="bluebox" width="15%"><s:text name="registration.date"/><span class="mandatory">*</span></td>
				<td class="bluebox" width="15%" >
				    <s:date name="registrationDate" format="dd/MM/yyyy" var="registrationDateTemp" />
				 	<s:textfield id="registrationDate" name="registrationDate" value="%{registrationDateTemp}" readonly="true"/>
				</td>
				<td class="bluebox" width="18%">&nbsp;</td>
	     </tr>
	     <s:if test = "%{mode!=null}">
	     <tr>
	 			<td class="greybox">&nbsp;</td>
				<td class="greybox"><s:text name="registration.no"/><span class="mandatory">*</span> </td>
	   			<td class="greybox">
	   				<s:textfield name="registrationNo" id="registrationNo" value="%{registrationNo}" onchange="isBirthRegNumUnique();"/>
	   			</td>
	   			<td class="greybox">&nbsp;</td>
				<td class="greybox">&nbsp;</td>
				<td class="greybox">&nbsp;</td>
	     </tr>
	     </s:if>
	     <tr>
	 			<td colspan="6">
	 				<div class="blueshadow"></div>
	 				<div align="center">
	 					<h1 class="subhead" ><s:text name="child.details.heading"/></h1>
	 				</div>
	 			</td>
	     </tr>
	     <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox"><s:text name="dateOfbirth"/><span class="mandatory">*</span></td>
	   			<td class="bluebox">
	   			    <s:date name="dateOfEvent" format="dd/MM/yyyy" var="dateOfEventTemp" />
				 	<s:textfield id="dateOfEvent" name="dateOfEvent" value="%{dateOfEventTemp}" readonly="true"/>
                </td>
	   			<td class="bluebox"><s:text name="sex.lbl"/><span class="mandatory">*</span></td>
				<td class="bluebox"><s:select id="citizen.sex" name="citizen.sex" list="dropdownData.sexTypeList" listKey="code" listValue="code" headerKey="" headerValue="-----choose----"/></td>
				<td class="bluebox">&nbsp;</td>
	     </tr>
     	 <tr>
	 			<td class="greybox">&nbsp;</td>
				<td class="greybox"><s:text name="nameofchild.lbl"/></td>
	   			<td class="greybox">
	   				<s:radio list="optionMap" value="%{nameOfchildFlag}" name="nameOfchildFlag" id="nameOfchildFlag"  onclick="populateCitizenName();"/>
	   				<s:hidden id="citizen.citizenID" name="citizen.citizenID" value="%{citizen.citizenID}"/>
	   			</td>
	   			<td class="greybox">&nbsp;</td>
				<td class="greybox">&nbsp;</td>
				<td class="greybox">&nbsp;</td>
	     </tr>
	      <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="firstName.lbl"/><span class="mandatory">*</span><br/>
	   				<s:textfield name="citizen.firstName" id="citizen.firstName" value="%{citizen.firstName}"/><br/>
	   				<font color="blue"><s:text name="fornodatalbl"/></font>
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="middleName.lbl"/><br>
	   				<s:textfield name="citizen.middleName" id="citizen.middleName" value="%{citizen.middleName}"/>
	   			</td>
				<td class="bluebox">
					<s:text name="lastName.lbl"/><br>
					<s:textfield name="citizen.lastName" id="citizen.lastName" value="%{citizen.lastName}"/>
				</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td class="greybox">&nbsp;</td>
				<td class="greybox"><s:text name="permanentAddress"/></td>
	   			<td class="greybox">
	   				<s:radio list="optionMap" value="%{permanentAddressFlag}"	name="permanentAddressFlag" id="permanentAddressFlag" onclick="populateAddress(this);" />
	   			</td>
	   			<td class="greybox">&nbsp;</td>
				<td class="greybox">&nbsp;</td>
				<td class="greybox">&nbsp;</td>
	     </tr>
	     <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="streetAddress1"/>:<span class="mandatory">*</span>
	   			</td>
	   			<td class="bluebox" colspan="2">
	   			    <s:hidden id="permanentCitizenAddress.addressID" name="permanentCitizenAddress.addressID" value="%{permanentCitizenAddress.addressID}"/>
	   				<s:textarea name="permanentCitizenAddress.streetAddress1" id="permanentCitizenAddress.streetAddress1" value="%{permanentCitizenAddress.streetAddress1}" rows="1" cols="46" />
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="streetAddress2"/>:
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:textarea name="permanentCitizenAddress.streetAddress2" id="permanentCitizenAddress.streetAddress2" value="%{permanentCitizenAddress.streetAddress2}" rows="1" cols="46"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="taluk"/><br/>
	   				<s:textfield name="permanentCitizenAddress.taluk" id="permanentCitizenAddress.taluk" value="%{permanentCitizenAddress.taluk}"/>
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="city.town.vill.lbl"/><span class="mandatory">*</span><br>
	   				<s:textfield name="permanentCitizenAddress.cityTownVillage" id="permanentCitizenAddress.cityTownVillage" value="%{permanentCitizenAddress.cityTownVillage}"/>
	   			</td>
				<td class="bluebox">
					<s:text name="pincode.lbl"/><br>
					<s:textfield name="permanentCitizenAddress.pinCode" id="permanentCitizenAddress.pinCode" value="%{permanentCitizenAddress.pinCode}" maxlength="6"/>
				</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="district.lbl"/><span class="mandatory">*</span><br/>
	   				<s:textfield name="permanentCitizenAddress.district" id="permanentCitizenAddress.district" value="%{permanentCitizenAddress.district}"/>
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:text name="state.lbl"/>:<span class="mandatory">*</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   				<s:select name="permanentCitizenAddress.state" id="permanentCitizenAddress.state" list="dropdownData.stateList" value="%{permanentCitizenAddress.state}" listKey="id" listValue="name" headerKey="" headerValue="---choose---"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td class="greybox">&nbsp;</td>
				<td class="greybox"><s:text name="place.of.birth.lbl"/>:<span class="mandatory">*</span></td>
	   			<td class="greybox" colspan="3">
	   				<s:hidden value="%{placeType.id}" name="placeType" id="placeType" />
	   				<s:radio list="dropdownData.placeTypeList" value="%{placeTypeTemp}" name="placeTypeTemp" id="placeTypeTemp" listKey="desc" listValue="desc" />
	   			</td>
				<td class="greybox">&nbsp;</td>
	     </tr>
	      <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="hospital.type.lbl"/><span class="mandatory">*</span>
	   				<s:select list="dropdownData.hospitalTypeList" name="hospitalType" id="hospitalType"  listKey="id"  onchange="populateHospitalName();" 
	   				      listValue="desc" value="%{establishment.type.id}" headerKey="-1" headerValue="---------choose----------"/>
	   				</td>
	   			<td class="bluebox" colspan="2">
	   				<s:text name="hospital.name.lbl"/><span class="mandatory">*</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   				<s:select name="establishment" id="establishment" list="dropdownData.hospitalList" value="%{establishment.id}" listKey="id" listValue="name" headerKey="-1" headerValue="---choose---" onchange="populateEventAddress(this);"/>
	   			</td>
	   			<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox"><s:text name="address"/>:</td>
	   			<td class="bluebox">
	   				<s:text name="streetAddress1"/>:<span class="mandatory">*</span>
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:textarea name="eventAddress.streetAddress1" id="eventAddress.streetAddress1" value="%{eventAddress.streetAddress1}" rows="1" cols="46"/>
	   				<s:hidden id="eventAddress.addressID" name="eventAddress.addressID" value="%{eventAddress.addressID}"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="streetAddress2"/>:
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:textarea name="eventAddress.streetAddress2" id="eventAddress.streetAddress2" value="%{eventAddress.streetAddress2}" rows="1" cols="46"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="taluk"/><br/>
	   				<s:textfield name="eventAddress.taluk" id="eventAddress.taluk" value="%{eventAddress.taluk}"/>
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="city.town.vill.lbl"/><span class="mandatory">*</span><br>
	   				<s:textfield name="eventAddress.cityTownVillage" id="eventAddress.cityTownVillage" value="%{eventAddress.cityTownVillage}"/>
	   			</td>
				<td class="bluebox">
					<s:text name="pincode.lbl"/><br>
					<s:textfield name="eventAddress.pinCode" id="eventAddress.pinCode" value="%{eventAddress.pinCode}" maxlength="6" onblur="validateAddressPincode(this);"/>
				</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="district.lbl"/><span class="mandatory">*</span><br/>
	   				<s:textfield name="eventAddress.district" id="eventAddress.district" value="%{eventAddress.district}"/>
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:text name="state.lbl"/>:<span class="mandatory">*</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   				<s:select name="eventAddress.state" id="eventAddress.state" list="dropdownData.stateList" value="%{eventAddress.state}" listKey="id" listValue="name" headerKey="" headerValue="---choose---"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td colspan="6">
	 				<div class="blueshadow"></div>
	 				<div align="center">
	 					<h1 class="subhead" ><s:text name="parent.details.lbl"/></h1>
	 				</div>
	 			</td>
	     </tr>
	     <tr>
	 			<td class="greybox">&nbsp;</td>
				<td class="greybox"><b><s:text name="motherdetails.lbl"/>:</b></td>
	   			<td class="greybox" colspan="4">&nbsp;</td>
		 </tr>
	     <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox"><s:text name="mothername.lbl"/></td>
	   			<td class="bluebox">
	   				<s:text name="firstName.lbl"/><span class="mandatory">*</span><br/>
	   				<s:textfield name="mother.firstName" id="mother.firstName" value="%{mother.firstName}" />
	   				<s:hidden name="mother.citizenID" id="mother.citizenID" value="%{mother.citizenID}"/>
	   				<br/>
	   				<font color="blue"><s:text name="fornodatalbl"/></font>
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="middleName.lbl"/><br>
	   				<s:textfield name="mother.middleName" id="mother.middleName" value="%{mother.middleName}" />
	   			</td>
				<td class="bluebox">
					<s:text name="lastName.lbl"/><br>
					<s:textfield name="mother.lastName" id="mother.lastName" value="%{mother.lastName}" />
				</td>
				<td class="bluebox">&nbsp;</td>
	     </tr>
	     <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="mobileno.lbl"/><br/> 
	   				<s:textfield name="mother.mobilePhone" id="mother.mobilePhone" maxlength="10" value="%{mother.mobilePhone}" />
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="email.lbl"/><br>
	   				<s:textfield name="mother.emailAddress" id="mother.emailAddress" value="%{mother.emailAddress}" />
	   			</td>
				<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td class="greybox">&nbsp;</td>
				<td class="greybox"><b><s:text name="fatherdetails.lbl"/>:</b></td>
	   			<td class="greybox" colspan="4">&nbsp;</td>
		 </tr>
	     <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox"><s:text name="fathername.lbl"/></td>
	   			<td class="bluebox">
	   				<s:text name="firstName.lbl"/><span class="mandatory">*</span><br/>
	   				<s:textfield name="father.firstName" id="father.firstName" value="%{father.firstName}" />
	   				<s:hidden name="father.citizenID" id="father.citizenID" value="%{father.citizenID}"/>
	   				<br/>
	   				<font color="blue"><s:text name="fornodatalbl"/></font>
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="middleName.lbl"/><br>
	   				<s:textfield name="father.middleName" id="father.middleName" value="%{father.middleName}" />
	   			</td>
				<td class="bluebox">
					<s:text name="lastName.lbl"/><br>
					<s:textfield name="father.lastName" id="father.lastName" value="%{father.lastName}" />
				</td>
				<td class="bluebox">&nbsp;</td>
	     </tr>
	     <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="mobileno.lbl"/><br/>
	   				<s:textfield name="father.mobilePhone" id="father.mobilePhone" maxlength="10" value="%{father.mobilePhone}" />
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="email.lbl"/><br>
	   				<s:textfield name="father.emailAddress" id="father.emailAddress" value="%{father.emailAddress}" />
	   			</td>
				<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td class="greybox">&nbsp;</td>
				<td class="greybox"><b><s:text name="address.of.parent.at.birth"/>:</b></td>
	   			<td class="greybox">
	   				<s:radio list="optionMap" value="%{parentAddressFlag}" name="parentAddressFlag" id="parentAddressFlag"  onclick="populateAddress(this);"/>
	   			</td>
	   			<td class="greybox" colspan="3">&nbsp;</td>
		 </tr>
		 <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox"><s:text name="address"/>:</td>
	   			<td class="bluebox">
	   				<s:text name="streetAddress1"/>:<span class="mandatory">*</span>
	   			</td>
	   			<td class="bluebox" colspan="2">
	   			    <s:hidden id="parentAddress.addressID" name="parentAddress.addressID" value="%{parentAddress.addressID}"/>
	   				<s:textarea name="parentAddress.streetAddress1" id="parentAddress.streetAddress1" value="%{parentAddress.streetAddress1}" rows="1" cols="46"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="streetAddress2"/>:
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:textarea name="parentAddress.streetAddress2" id="parentAddress.streetAddress2" value="%{parentAddress.streetAddress2}" rows="1" cols="46"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="taluk"/><br/>
	   				<s:textfield name="parentAddress.taluk" id="parentAddress.taluk" value="%{parentAddress.taluk}"/>
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="city.town.vill.lbl"/><span class="mandatory">*</span><br>
	   				<s:textfield name="parentAddress.cityTownVillage" id="parentAddress.cityTownVillage" value="%{parentAddress.cityTownVillage}"/>
	   			</td>
				<td class="bluebox">
					<s:text name="pincode.lbl"/><br>
					<s:textfield name="parentAddress.pinCode" id="parentAddress.pinCode" value="%{parentAddress.pinCode}" maxlength="6" />
				</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="district.lbl"/><span class="mandatory">*</span><br/>
	   				<s:textfield name="parentAddress.district" id="parentAddress.district" value="%{parentAddress.district}"/>
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:text name="state.lbl"/>:<span class="mandatory">*</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   				<s:select name="parentAddress.state" id="parentAddress.state" list="dropdownData.stateList" value="%{parentAddress.state}" listKey="id" listValue="name" headerKey="" headerValue="---choose---"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td colspan="6">
	 				<div class="blueshadow"></div>
	 				<div align="center">
	 					<h1 class="subhead" ><s:text name="informat.details.lbl"/></h1>
	 				</div>
	 			</td>
	     </tr>
	      <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox"><s:text name="informat.relations.child"/>:<span class="mandatory">*</span></td>
	   			<td class="bluebox" colspan="3"><s:radio list="informantRelationList" id="informantFlag" name="informantFlag" value="informantFlag" onclick="populateInformant();"></s:radio></td>
				<td class="bluebox">&nbsp;</td>
	     </tr>
	     <tr>
	 			<td class="greybox">&nbsp;</td>
				<td class="greybox"><s:text name="informat.name.lbl"/>:</td>
	   			<td class="greybox">
	   				<s:text name="firstName.lbl"/><span class="mandatory">*</span><br/>
	   				<s:hidden name="informantCitizen.citizenID" id="informantCitizen.citizenID" value="%{informantCitizen.citizenID}"/>
	   				<s:textfield name="informantCitizen.firstName" id="informantCitizen.firstName" value="%{informantCitizen.firstName}"/>
	   				
	   				<br/>
	   				<font color="blue"><s:text name="fornodatalbl"/></font>
	   			</td>
	   			<td class="greybox">
	   				<s:text name="middleName.lbl"/><br>
	   				<s:textfield name="informantCitizen.middleName" id="informantCitizen.middleName" value="%{informantCitizen.middleName}" />
	   			</td>
				<td class="greybox">
					<s:text name="lastName.lbl"/><br>
					<s:textfield name="informantCitizen.lastName" id="informantCitizen.lastName" value="%{informantCitizen.lastName}"/>
				</td>
				<td class="greybox">&nbsp;</td>
	     </tr>
	      <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox"><s:text name="address"/>:</td>
	   			<td class="bluebox">
	   				<s:text name="streetAddress1"/>:<span class="mandatory">*</span>
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:textarea name="informantAddress.streetAddress1" id="informantAddress.streetAddress1" value="%{informantAddress.streetAddress1}" rows="1" cols="46"/>
	   				<s:hidden name="informantAddress.addressID" id="informantAddress.addressID" value="%{informantAddress.addressID}" />
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="streetAddress2"/>:
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:textarea name="informantAddress.streetAddress2" id="informantAddress.streetAddress2" value="%{informantAddress.streetAddress2}" rows="1" cols="46"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	      <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="taluk"/><br/>
	   				<s:textfield name="informantAddress.taluk" id="informantAddress.taluk" value="%{informantAddress.taluk}"/>
	   			</td>
	   			<td class="bluebox">
	   				<s:text name="city.town.vill.lbl"/><span class="mandatory">*</span><br>
	   				<s:textfield name="informantAddress.cityTownVillage" id="informantAddress.cityTownVillage" value="%{informantAddress.cityTownVillage}"/>
	   			</td>
				<td class="bluebox">
					<s:text name="pincode.lbl"/><br>
					<s:textfield name="informantAddress.pinCode" id="informantAddress.pinCode" value="%{informantAddress.pinCode}" maxlength="6" onblur="validateAddressPincode(this);"/>
				</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
	       <tr>
	 			<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
	   			<td class="bluebox">
	   				<s:text name="district.lbl"/><span class="mandatory">*</span><br/>
	   				<s:textfield name="informantAddress.district" id="informantAddress.district" value="%{informantAddress.district}"/>
	   			</td>
	   			<td class="bluebox" colspan="2">
	   				<s:text name="state.lbl"/>:<span class="mandatory">*</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   				<s:select name="informantAddress.state" id="informantAddress.state" list="dropdownData.stateList" value="%{informantAddress.state}" listKey="id" listValue="name" headerKey="" headerValue="---choose---"/>
	   			</td>
				<td class="bluebox">&nbsp;</td>
	      </tr>
     	</table>
     	<s:if test="%{birthAdoptionFlag==1}">
     		<jsp:include page='/WEB-INF/jsp/registration/birthAdoptionDetails.jsp'/>
     	</s:if>
     	<s:if test="%{birthStatisticsInfoFlag == 1}">
    		<jsp:include page='/WEB-INF/jsp/registration/birthStatisticalInformation.jsp'/>
    	</s:if> 
    	
		
    	<div class="buttonbottom" align="center" id="workFlowButtons">
			<table>
					<tr>
					    <s:if test="%{mode!='view'}">
					    <td>
							<s:submit cssClass="buttonsubmit" id="save" name="save" value="Save" method="create" onclick="return checkAdoptionDetails();"/>
						</td>
						</s:if>
						<td>
							<input type="button" name="close" id="close" class="button" value="Close" onclick="window.close();" />
						</td>
					</tr>
			</table>
			<br>
			<div align="center"><font color="red"><s:text name="warning.lbl"/></font></div>
		</div>
		
     </s:push>
 </s:form>
</body>
</html>
