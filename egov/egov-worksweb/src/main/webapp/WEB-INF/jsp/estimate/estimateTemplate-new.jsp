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
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib prefix="s" uri="/WEB-INF/struts-tags.tld" %>  
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/egov-authz.tld" prefix="egov-authz" %>
<html>
<title><s:text name='page.title.estimate.template'/></title>
<body class="yui-skin-sam">

<script src="<egov:url path='js/works.js'/>"></script>
<script>
function enableFieldsForModify(){
    id=dom.get('id').value;
    document.estimateTemplateForm.action='${pageContext.request.contextPath}/estimate/estimateTemplate!edit.action?mode=edit&id='+id;
    document.estimateTemplateForm.submit();
}

function validateCancel() {
	var msg='<s:text name="estimate.template.modify.confirm"/>';
	if(!confirmCancel(msg,'')) {
		return false;
	}
	else {
	    return true;
	}
}

function validateEstimateTemplateFormAndSubmit() {
    clearMessage('estimatetemplateerror')
	links=document.estimateTemplateForm.getElementsByTagName("span");
	errors=false;
	for(i=0;i<links.length;i++) {
        if(links[i].innerHTML=='&nbsp;x' && links[i].style.display!='none'){
            errors=true;
            break;
        }
    }
    if(errors) {
        dom.get("estimatetemplateerror").style.display='';
    	document.getElementById("estimatetemplateerror").innerHTML='<s:text name="contractor.validate_x.message" />';
    	return false;
    }
    if(!validateHeaderBeforeSubmit(document.estimateTemplateForm)){
    	return false;
    }
    else {
    	mode=dom.get('mode').value;
    	if(mode=='edit'){
    	 if(validateCancel()){
    	  document.estimateTemplateForm.action='${pageContext.request.contextPath}/estimate/estimateTemplate!save.action';
    	  document.estimateTemplateForm.submit();
    	 }
    	}
    	else{
    	document.estimateTemplateForm.action='${pageContext.request.contextPath}/estimate/estimateTemplate!save.action';
    	document.estimateTemplateForm.submit();
    	}
   	}
   	return true;
}

</script>
<div id="estimatetemplateerror" class="errorstyle" style="display:none;"></div>
<div id="templatecodeerror" class="errorstyle" style="display:none;">
<s:text name="estimateTemplate.code.isunique"/>
</div>
    <s:if test="%{hasErrors()}">
        <div id="errorstyle" class="errorstyle" >
          <s:actionerror/>
          <s:fielderror/>
        </div>
    </s:if>
    <s:if test="%{hasActionMessages()}">
        <div class="messagestyle">
        	<s:property value="%{code}"/> &nbsp; <s:actionmessage theme="simple"/>
        	
        </div>
    </s:if>
    <s:form theme="simple" name="estimateTemplateForm" >
    <s:token/>
<s:push value="model">

	
<s:if test="%{model.id!=null}">
	<s:hidden name="id" value="%{id}" id="id"/>
    <s:hidden name="mode" value="%{mode}" id="mode"/>
</s:if> 
<s:else>
    <s:hidden name="id" value="%{null}" id="mode" />
</s:else>
<div class="formmainbox"><div class="insidecontent">
  <div class="rbroundbox2">
	<div class="rbtop2"><div></div></div>
	  <div class="rbcontent2"><div class="datewk">
	 <%@ include file='estimateTemplate-header.jsp'%>
	<%@ include file='estimateTemplate-sor.jsp'%>
	<%@ include file='estimateTemplate-nonSor.jsp'%>
	  <div class="rbbot2"><div></div></div>
      </div>     
	
</div>
  </div>
</div>
<div class="buttonholderwk">
		
	  <p>
	    <s:if test="%{mode!='view'}">
			<input type="submit" class="buttonfinal" value="SAVE" id="saveButton" name="button" onclick="return validateEstimateTemplateFormAndSubmit()"/>&nbsp;
		</s:if>
		<egov-authz:authorize actionName="editEstimateTemplate">
		<s:if test="%{mode=='view'}">
			<input type="button" class="buttonfinal" value="MODIFY" id="modifyButton" name="button" onclick="enableFieldsForModify()"/>&nbsp;
		</s:if>
		</egov-authz:authorize>
		<s:if test="%{model.id==null}" >
			<input type="button" class="buttonfinal" value="CLEAR" id="button" name="clear" onclick="this.form.reset();">&nbsp;
		</s:if>
		<input type="button" class="buttonfinal" value="CLOSE" id="closeButton" name="closeButton" onclick="window.close();" />
	  </p>
		
</div>
</s:push>
</s:form>
</body>
</html>
