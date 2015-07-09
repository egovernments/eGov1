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
<%@ taglib prefix="s" uri="/WEB-INF/struts-tags.tld"%>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="sj" uri="/WEB-INF/struts-jquery-tags.tld"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp" %>

<html>
<title>
	<s:text name="lettertoparty.title"/>
</title>

<head>
<sj:head jqueryui="true" jquerytheme="redmond"  loadAtOnce="true" />
<script type="text/javascript">


jQuery.noConflict();

jQuery(document).ready(function(){

if(document.getElementById('Save LettertoParty')!=null){

jQuery('#save').attr('hidden','true');
}
	//document.getElementById("sentDate").disabled=true;
	if( jQuery('#mode').val()=="view"){
		for(var i=0;i<document.forms[0].length;i++)
					{
						document.forms[0].elements[i].disabled =true;
					}
		jQuery("#close").removeAttr('disabled');
				if( jQuery('#fromreg').val()=="true"){
		             window.opener.callLetterToPartyDetails();
		              }  
		}
	if( jQuery('#mode').val()=="enterSentDate"){
		for(var i=0;i<document.forms[0].length;i++)
					{
						document.forms[0].elements[i].disabled =true;
					}
		jQuery("#close").removeAttr('disabled');
		jQuery("#save").removeAttr('disabled');
				if( jQuery('#fromreg').val()=="true"){
		             window.opener.callLetterToPartyDetails();
		              }  
	             // document.getElementById("sentDate").disabled=false;
		}
	  jQuery.subscribe('beforeDiv', function(event,data) {		
	    });
	    
	    
	 jQuery.subscribe('completeDiv', function(event,data) { 	
	 	disable();
	    });
	 var dept='<s:property value="departmentForLoggedInUser"/>';
	 if(dept!=null){
	jQuery("#approverDepartment option[value!='<s:property value="departmentForLoggedInUser"/>']").each(function() {
	if(jQuery(this).val()!=-1)
	    jQuery(this).remove();
});
	 }
});

function disable(){
	if( jQuery('#mode').val()=="view"){
	for(var i=0;i<document.forms[0].length;i++)
				{
					document.forms[0].elements[i].disabled =true;
				}
	jQuery("#close").removeAttr('disabled');
	}

	}
	
function validateForm(obj){
	enableAllFields();
	var count =0;
jQuery("[id=mandatorycheck]").each(function(index) {	
   		jQuery(this).find(':checkbox').css('outline-color', '');
		jQuery(this).find(':checkbox').css('outline-style', '');
		jQuery(this).find(':checkbox').css('outline-width', '');
   		jQuery(this).next("td").find('textarea').css("border", '');
   		});	
   		
   		
	dom.get("lp_error").style.display='none';
	if(document.getElementById('workFlowAction')!=null)
	document.getElementById('workFlowAction').value=obj;
	//document.getElementById("sentDate").disabled=true;
	if(document.getElementById('letterToPartyReason').value==null || document.getElementById('letterToPartyReason').value=="-1"){
		dom.get("lp_error").style.display = '';
		document.getElementById("lp_error").innerHTML = '<s:text name="letterToParty.reason.required" />';
		return false;
	}
	/*if( jQuery('#mode').val()=="enterSentDate"){
	if( jQuery('#sentDate').val()==""){
		   showerrormsg(jQuery('#sentDate'),'<s:text name="lp.sentdate.required" />');
		  count++;
		   }
	var todaysDate=getTodayDate();
	if(compareDate(document.getElementById("sentDate").value,todaysDate) == -1)
	{						  	 	
		dom.get("lp_error").style.display = '';
		document.getElementById("lp_error").innerHTML = '<s:text name="sentdate.greaterthan.todaysdate" />';
			return false;
	}
		   if(count!=0)
				return false;
	}  */
	   jQuery("[id=mandatorycheck]").each(function(index) {	
  
	var values=(jQuery(this).find('input').attr("value"));
	
	if(values=='true'){
   		if(jQuery(this).find(':checkbox').prop('checked')){
   		}else{ 		  		
   		dom.get("lp_error").style.display = '';
   			
   		jQuery(this).find(':checkbox').css('outline-color', 'red');
		jQuery(this).find(':checkbox').css('outline-style', 'solid');
		jQuery(this).find(':checkbox').css('outline-width', 'thin');
		document.getElementById("lp_error").innerHTML = 'Please select the mandatory check boxes';
		
	    count++;
   		}
   		}
   		});		
   		
   		if(count!=0)
		return false;



		
   		jQuery("[id=mandatorycheck]").each(function(index) {	
			if(jQuery(this).find(':checkbox').prop('checked')){
							var rem=jQuery(this).next("td").find('textarea').attr("value");
   							if(rem==""){
   								dom.get("lp_error").style.display = '';
   									document.getElementById("lp_error").innerHTML = 'Please enter the remarks for the checked fields';
   									jQuery(this).next("td").find('textarea').css("border", "1px solid red");
   									count++;
   									}
   					}
   		});	
   		if(count!=0)
			return false;
   var buttonclickedname=obj;
return validateBPAWorkFlowApprover(buttonclickedname,'lp_error')
}

function validateBPAWorkFlowApprover(name,errorDivId)
{
	document.getElementById(errorDivId).style.display='none';
	if(document.getElementById('workFlowAction')!=null)
    document.getElementById('workFlowAction').value=name;
	
    if((name=="Forward" || name=="Approve" || name=="approve" || name=="forward") && 
    		document.getElementById('approverPositionId').value=="-1")
    {
        document.getElementById(errorDivId).style.display='';
        document.getElementById(errorDivId).innerHTML = "Please Select the Approver";
		return false;
    } 
  
    
}


/*
function viewRegisterBpa(registrationId){

window.open("${pageContext.request.contextPath}/extd/register/registerBpaExtn!viewForm.action?registrationId="+registrationId+"&fromreg=true","simple","height=650,width=900,scrollbars=yes,left=20,top=20,status=yes");
				
	 }	
	 */

function enableAllFields(){
	for(var i=0;i<document.forms[0].length;i++)
				{
					document.forms[0].elements[i].disabled =false;
				}

	}

function showerrormsg(obj,msg){
	dom.get("lp_error").style.display = '';
	document.getElementById("lp_error").innerHTML =msg;
	jQuery(obj).css("border", "1px solid red");		
	return false;
	} 
</script>
</head>



<body>  
<div align="left">
  	<s:actionerror/>
  </div>
<div class="errorstyle" id="lp_error" style="display:none;" >
</div>
<s:if test="%{hasErrors()}">
		<div class="errorstyle" id="fieldError">
			<s:actionerror />
			<s:fielderror />
		</div>
</s:if>

<s:if test="%{hasActionMessages()}">
		<div class="errorstyle">
				<s:actionmessage />
		</div>
</s:if>
<s:form name="LetterToPartyform"  action="registerBpaExtn" theme="simple">
<s:push value="model">
  <s:token/>
 <s:hidden id="registrationId" name="registrationId" value="%{id}"/>
<s:hidden id="letterToPartyId" name="letterToPartyId" value="%{letterToPartyId}"/>
<s:hidden id="id" name="id" value="%{id}"/>
<s:hidden id="letterToParty.id" name="letterToParty.id" value="%{letterToParty.id}"/>
<s:hidden id="letterToParty.letterToPartyNumber" name="letterToParty.letterToPartyNumber" value="%{letterToParty.letterToPartyNumber}"/>
			<s:hidden id="createdBy" name="createdBy" value="%{createdBy.id}"/>
	   		<s:hidden id="modifiedBy" name="modifiedBy" value="%{modifiedBy.id}"/>
	   		<s:hidden id="createdDate" name="createdDate" value="%{createdDate}"/>
	   		<s:hidden id="modifiedDate" name="modifiedDate" value="%{modifiedDate}"/>
		     <s:hidden id="mode" name="mode" value="%{mode}"/>
		       <s:hidden id="egwStatus" name="egwStatus" value="%{egwStatus.id}" />
		       <s:hidden id="egDemand" name="egDemand" value="%{egDemand.id}" />
		       <s:hidden id="state" name="state" value="%{state.id}" />
		      <s:hidden id="additionalRule" name="additionalRule" value="%{registration.additionalRule}"></s:hidden>
			<s:hidden id="additionalState" name="additionalState" value="%{registration.additionalState}"  />
			
			<div  id="regdetails" class="formmainbox">
<h1 class="subhead" ><s:text name="Registration Details"/></h1>
 <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
	   	 	<tr>
	 			<td class="bluebox" width="20%">&nbsp;</td>
				<td class="bluebox" width="13%"><s:text name="inspectionlbl.applicationdate"/></td> 
	   			<td class="bluebox"> <sj:datepicker value="%{registration.planSubmissionDate}" id="applicationDate" name="registration.planSubmissionDate" displayFormat="dd/mm/yy" disabled="true" showOn="focus"/></td>   			
			  <td class="bluebox"><s:text name="planSubmissionNum" /></td>
			  <td class="bluebox"><s:textfield id="psnNo" name="psnNo" size="25%" value="%{registration.planSubmissionNum}" disabled="true"/></td>
			  
			  <!-- <a href="#" onclick="viewRegisterBpa('<s:property value="%{registration.id}"/>')"><s:property value="%{registration.planSubmissionNum}"/></a>  -->
			  </tr>
	         <tr>
	 			<td class="greybox" width="20%">&nbsp;</td>
				<td class="greybox" width="13%"><s:text name="inspectionlbl.applicantname"/></td> 
	   			<td class="greybox"><s:textfield id="applicantName" name="applicantName" value="%{registration.owner.firstName}" disabled="true"/></td>   			
				<td class="greybox" ><s:text name="inspectionlbl.applicantaddress"/></td>
				<td class="greybox" ><s:textarea id="applicantAddress" name="applicantAddress" value="%{registration.bpaOwnerAddress}" rows="3" cols="9" disabled="true"/></td>
	         </tr>
	                 
	      </table>
</div>
<div  id="lpdetails" class="formmainbox">
<h1 class="subhead" ><s:text name="lettertoparty.header"/></h1>
 <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
	   	 	<tr> 
	   	 	<td class="bluebox" width="10%">&nbsp;</td>
	   	 	<td class="bluebox" align="right" width="18%"><s:text name="lpreason"/>: <span class="mandatory" >*</span></td></td>
				<td class="bluebox"width="25%"  ><s:select id="letterToPartyReason" name="letterToParty.letterToPartyReason.id" list="dropdownData.letterToPartyReasonList" listKey="id" listValue="description" value="%{letterToParty.letterToPartyReason.id}" headerKey="-1" headerValue="----Choose------"/></td>
	   	 	     <td class="bluebox" width="20%" align="right"><s:text name="lpdescription"/>:</td>
	 			<td class="bluebox" width="25%"><s:textarea id="letterToPartyRemarks" name="letterToParty.letterToPartyRemarks" value="%{letterToParty.letterToPartyRemarks}" rows="2" cols="32" /></td>   			
             <td class="bluebox" width="14%">&nbsp;</td>
          </tr>
        <!-- <tr> 
	   	 	<td class="greybox" width="10%">&nbsp;</td>
	   	 	<td class="greybox" width="20%" align="right"><s:text name="lp.sentdate"/>: <span class="mandatory" >*</span></td>
            <td class="greybox" width="25%"><sj:datepicker value="%{letterToParty.sentDate}" id="sentDate" name="letterToParty.sentDate" displayFormat="dd/mm/yy" readonly="true" showOn="focus"/></td>
             <td class="greybox" width="14%">&nbsp;</td>   
             <td class="greybox" width="14%">&nbsp;</td>   
             <td class="greybox" width="14%">&nbsp;</td>   
          </tr> 
        -->
             </table>
          </div>
          <s:url id="checklistajax" value="/extd/register/registerBpaExtn!showLetterToPartyCheckList.action"  escapeAmp="false">
	  <s:param name="serviceTypeIdTemp" value="%{registration.serviceType.id}"></s:param>	
	  <s:param name="registrationId" value="%{registrationId}"></s:param>
	  <s:param name="letterToPartyId" value="%{letterToPartyId}"></s:param> 
	  <s:param name="mode" value="%{mode}"></s:param>
	<!--  <s:param name="inspectionId" value="%{inspectionId}"></s:param>	  -->       
	   </s:url>
       <sj:div href="%{checklistajax}" indicator="indicator"  cssClass="formmainbox" id="tab3"  dataType="html" onCompleteTopics="completedchecklistDiv">
        <img id="indicator" src="<egov:url path='/images/loading.gif'/>" alt="Loading..." style="display:none"/>
    </sj:div>
 		 <s:if test="%{mode!='view' && mode!='modify' && mode!='enterSentDate'}"> 
    	<div id="approverInfo">
        <c:set var="approverHeadTDCSS" value="headingwk" scope="request" /> 
        <c:set var="approverHeaderCss" value="headplacerlbl" scope="request"/>
        <c:set var="headerImgCss" value="arrowiconwk" scope="request"/>
        <c:set var="headerImgUrl" value="../common/image/arrow.gif" scope="request"/>
        <c:set var="approverOddCSS" value="whiteboxwk" scope="request" />
        <c:set var="approverOddTextCss" value="whitebox2wk" scope="request" />
        <c:set var="approverEvenCSS" value="greyboxwk" scope="request" />
        <c:set var="approverEvenTextCSS" value="greybox2wk" scope="request" />
		<c:import url="/commons/commonWorkflow.jsp" context="/bpa" />
		</div>
		</s:if>	  
    
   <div class="buttonbottom" align="center">
		<table> 
		<tr>
		 <s:if test="%{mode!='view' && mode!='modify' && mode!='enterSentDate'}">
	  		 <td><s:submit  cssClass="buttonsubmit" id="save" name="save" value="Save"  method="saveLetterToParty" onclick="return validateForm('save')"/></td>  
	  		<s:iterator value="%{getValidActions()}" var="p">
		  		<td><s:submit type="submit" cssClass="buttonsubmit" value="%{p}" id="%{p}" name="%{p}" method="saveLetterToParty" onclick="return validateForm('%{p}')"/></td>
			</s:iterator>
	  		</s:if>	
		 <s:if test="%{mode!='view' && mode=='modify'}">
		  	<td><s:submit  cssClass="buttonsubmit" id="save" name="save" value="Save" method="saveLetterToParty" onclick="return validateForm('save')"/></td>	
	  			</s:if>	         
	  		 <td><input type="button" name="close" id="close" class="button"  value="Close" onclick="window.close();"/>
	  		</td>
	  	</tr>
        </table>
   </div>
	 
	  
</s:push>
</s:form>
 <script type="text/javascript">
  function loadDesignationFromMatrix()
  {
        var currentState=document.getElementById('currentState').value;
       if(document.getElementById('additionalState').value!=""){
         currentState=document.getElementById('additionalState').value;
       }
        var amountRule=document.getElementById('amountRule').value;
        var additionalRule=document.getElementById('additionalRule').value;
        var pendingAction=document.getElementById('pendingActions').value;
        var dept="";
        loadDesignationByDeptAndType('RegistrationExtn',dept,currentState,amountRule,additionalRule,pendingAction); 
  }

  function populateApprover()
	{
		getUsersByDesignationAndDept();
	}

  </script>
</body>
</html>
