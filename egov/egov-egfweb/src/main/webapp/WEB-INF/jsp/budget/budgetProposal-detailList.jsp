<!--  #-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#      accountability and the service delivery of the government  organizations.
#   
#       Copyright (C) <2015>  eGovernments Foundation
#   
#       The updated version of eGov suite of products as by eGovernments Foundation 
#       is available at http://www.egovernments.org
#   
#       This program is free software: you can redistribute it and/or modify
#       it under the terms of the GNU General Public License as published by
#       the Free Software Foundation, either version 3 of the License, or
#       any later version.
#   
#       This program is distributed in the hope that it will be useful,
#       but WITHOUT ANY WARRANTY; without even the implied warranty of
#       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#       GNU General Public License for more details.
#   
#       You should have received a copy of the GNU General Public License
#       along with this program. If not, see http://www.gnu.org/licenses/ or 
#       http://www.gnu.org/licenses/gpl.html .
#   
#       In addition to the terms of the GPL license to be adhered to in using this
#       program, the following additional terms are to be complied with:
#   
#   	1) All versions of this program, verbatim or modified must carry this 
#   	   Legal Notice.
#   
#   	2) Any misrepresentation of the origin of the material is prohibited. It 
#   	   is required that all modified versions of this material be marked in 
#   	   reasonable ways as different from the original version.
#   
#   	3) This license does not grant any rights to any user of the program 
#   	   with regards to rights under trademark law for use of the trade names 
#   	   or trademarks of eGovernments Foundation.
#   
#     In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#-------------------------------------------------------------------------------  -->
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<%@ page language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<html>  
<head>  
    <title><s:text name="budgetdetail"/></title>
    <link rel="stylesheet" href="/EGF/resources/css/tabber.css" TYPE="text/css">
	<script type="text/javascript" src="/EGF/resources/javascript/tabber.js"></script>
	<script type="text/javascript" src="/EGF/resources/javascript/tabber2.js"></script>
	<script type="text/javascript" src="/EGF/resources/javascript/helper.js"></script>
	<script type="text/javascript" src="/EGF/resources/javascript/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="/EGF/resources/javascript/jquery/jquery.fixheadertable.js"></script>
	<link rel="stylesheet" type="text/css" href="/EGF/resources/css/jquery/base.css" />
	<link rel="stylesheet" type="text/css" href="/EGF/resources/css/jquery-ui/css/redmond/jquery-ui-1.8.4.custom.css" />

    <jsp:include page="budgetHeader.jsp"/>
    <SCRIPT type="text/javascript">
  
    
    var dept_callback = {
		success: function(o){
			if(trimStr(o.responseText) != '' && trimStr(o.responseText) != '0'){
				document.getElementById('departmentid').value = trimStr(o.responseText);
				if(document.getElementById('departmentid').value!=-1){
					document.getElementById('departmentid').disabled=true;
					populateDesg();
				}
			}else{
					document.getElementById('departmentid').disabled=false;
				}},
			failure: function(o) {
				document.getElementById('departmentid').disabled=false;
		    }
		}
		
		function defaultDept(){
			var url = '/EGF/voucher/common!ajaxLoadDefaultDepartment.action';
			YAHOO.util.Connect.asyncRequest('POST', url, dept_callback, null);
		}
    </SCRIPT>
</head>  
	<body>  
	<s:form action="budgetProposal" theme="simple" >
	<s:token/>
	<div style="color: red">
		<s:actionmessage theme="simple"/>
		<s:actionerror/>  
		<s:fielderror />
	</div>
		<div align="left"><br/>
    	<table border="0" cellspacing="0" cellpadding="0" width="1250px">
        <tr>
        <td> 
         	<div class="tabber">
           		<div class="tabbertab" style="height:500px;width:1200px;">    
					<h2>Budget Details</h2>
				
						<script>
						var callback = {
							     success: function(o) {

								if(o.responseText=='successful')
								{
								     alert("Deletion success");
								}else
								{
								     alert("Deletion failed");
								}


								 },
							     failure: function(o) {
								     alert("Deletion failed");
							     }
						} 
						function deleteBudgetDetail(re, be,obj,bename,rename){
							var rownum=getRow(obj);
							var table=document.getElementById("detailsTable");
							var beamount=document.getElementById(bename).value;
							var reamount=document.getElementById(rename).value;
							if(beamount==0 && reamount==0){
							if(table)
								{
								table.deleteRow(rownum.rowIndex);
								} 
							alert("Sending Request to server Please wait for Confirmation");
							var transaction = YAHOO.util.Connect.asyncRequest('POST', 'budgetProposal!ajaxDeleteBudgetDetail.action?bpBean.id='+re+'&bpBean.nextYrId='+be, callback, null);
						}else{
							alert("This Budget detail cannot be deleted ");
						}  
						}
						</script>
						<s:set var="validButtons" value="%{validActions}" />
						<jsp:include page="budgetHeader.jsp"/>
						<s:if test="%{isConsolidatedScreen()}">
							<div align="right" class="extracontent">
								Amount in Thousands
							</div>
						</s:if>
						<s:else>
							<div align="right" class="extracontent">
								Amount in Rupees
							</div>
						</s:else>
						
							<s:hidden name="topBudget.id" value="%{getTopBudget().getId()}"/>
							<s:hidden name="consolidatedScreen" value="%{consolidatedScreen}"/>
							<s:hidden name="budgetDetail.id" id="budgetDetail.id"/>
							<s:hidden name="budgetDetail.budget.id" id="budgetDetail.budget.id"/>
								
							<s:if test="%{!bpBeanList.isEmpty()}">
				               	<div id="detail" style="height:430px;width:1150px;" >
				               	<%@ include file="budgetProposal-modifyList.jsp" %>
								
								</div>
							</s:if>
							<br/><br/>
							<script>
								function validateAmounts(){
									var len = <s:property value="savedbudgetDetailList.size"/>;
									for(i=0;i<len;i++){
										if(document.getElementById('savedbudgetDetailList['+i+'].approvedAmount') && document.getElementById('savedbudgetDetailList['+i+'].approvedAmount').value == ''){
											alert("Enter approved amount");
											return false;
										}
									}
									return true;
								}
								function validateAppoveUser(name,value){
									<s:if test="%{wfitemstate =='END'}">
										if(value == 'Approve' || value == 'Reject') {
											document.getElementById("approverUserId").value=-1;
											return true;
										}
									</s:if>
									<s:else>
										if( (value == 'forward' || value == 'Forward') && null != document.getElementById("approverUserId") && document.getElementById("approverUserId").value == -1){
											alert("Please select User");
											return false;
										}
									</s:else>
										      
									return true;
								}
							</script>
							<s:hidden  id="scriptName" value="BudgetDetail.nextDesg"/>
					
				</div> <!-- Individual tab -->
				<div class="tabbertab" id="approvalDetails" style="height:500px;width:1200px;">
				<h2>Approval Details</h2>
					
					<table align="center" border="0" cellpadding="0" cellspacing="0"
									width="100%" class="tablebottom"
									style="border-right: 0px solid #C5C5C5;">
									<tr>
										<td width="5%"></td>
										<td class="blueborderfortd" width="5%"><b>Budget:</b></td>
										<td class="blueborderfortd">
											<s:property value="%{getTopBudget().getName()}" />
										</td>
										<td class="blueborderfortd" width="5%"><b>Remarks:</b></td>
										<td class="blueborderfortd">
											<textarea cols="50" rows="3" name='comments' ><s:property value="comments"/></textarea>
										</td>
										<s:if test="%{isConsolidatedScreen()}">
										<td class="blueborderfortd" width="5%"><b><s:text name="As On Date"/>:</b></td>
										<td class="blueborderfortd" width="5%">
											<s:textfield name="asOndate" id="asOndate" cssStyle="width:100px"/><a href="javascript:show_calendar('budgetProposal.asOndate');" style="text-decoration:none">&nbsp;<img src="/egi/resources/erp2/images/calendaricon.gif" border="0"/></a>(dd/mm/yyyy)
										</td>
										<td width="5%"></td>
										<td><input type="button" class="buttonsubmit" value="Refresh" id="refresh" name="refresh" onclick="updateNew()" /></td>
										<td width="5%"></td>
										<td width="5%"></td>
										<td width="5%"></td>
										<td width="5%"></td>
										<td width="5%"></td>
										<td width="5%"></td>  
										<td width="5%"></td>
										<td width="5%"></td>
										</s:if>
									</tr>
								</table>
					
						<s:if test='%{!"END".equalsIgnoreCase(wfitemstate)}'>
							<%@include file="../voucher/workflowApproval.jsp"%>
							<script>
								document.getElementById('departmentid').value='<s:property value="savedbudgetDetailList[0].executingDepartment.id"/>';
								populateDesg();
								defaultDept();
							</script>
						</s:if>       
					
					<div class="buttonholderwk" id="buttonsDiv"  >
					<s:hidden  name="actionName" />
					<s:hidden name="mode"/>  
					<centre>
					<div class="buttonbottom" id="sbuttons" style="text-align:center">
					
					<s:iterator value="%{getValidActions()}">  
					
					  	<s:submit  cssClass="buttonsubmit" value="%{capitalize(description)}" id="%{name}" name="%{name}" method="update" onclick=" document.budgetProposal.actionName.value='%{name}';return validateAppoveUser('%{name}','%{description}')"/>
					</s:iterator>
				<input type="button" value="Close" onclick="javascript:window.close()" class="button"/>
			</div>
			
			<div id="exportButton" class="buttonbottom" style="text-align:center">
				<!--<s:submit method="generatePdf" value="Save As Pdf" cssClass="buttonsubmit" id="generatePdf" />
				<s:submit method="generateXls" value="Save As Xls" cssClass="buttonsubmit" id="generateXls" />-->
				<input type="button" class="buttonsubmit" value="EXPORT PDF" id="exportpdf" name="exportpdf" onclick="return exportPDF();"/>
				<input type="button" class="buttonsubmit" value="EXPORT EXCEL" id="exportpdf" name="exportpdf" onclick="return exportExcel();"/>
			</div>
			</centre>
			</div>          
					  
				</div><!-- Individual tab -->
			</div>
		</td>
		</tr>
		</table>
		</div>
		
		
	    
	<script>
	if(document.getElementById("approve")){
		//alert("-----"+document.getElementById("approve").value);
		document.getElementById("approvalDetails").style.display = 'none';
	}
	<s:if test='%{isHod()}'>
		<s:if test="%{!isAllfunctionsArrived()}">
		   alert("Not All function Centers Received , Forward Not allowed ");
		   alert('List of functions not yet Received are : <s:property value="functionsNotYetReceiced"/>');
		   if(document.getElementById("forward"))
			document.getElementById("forward").style.display = 'none';
		</s:if>
	</s:if>   
	


		 function exportPDF() {
		 	 var budgetId=document.getElementById("budgetDetail.budget.id").value;
			 var url="${pageContext.request.contextPath}/budget/budgetProposal!generatePdf.action?budgetDetail.budget.id="+budgetId;
			 window.open(url,'','height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
			 }

		 function exportExcel() {
		 	 var budgetId=document.getElementById("budgetDetail.budget.id").value;
			 var url="${pageContext.request.contextPath}/budget/budgetProposal!generateXls.action?budgetDetail.budget.id="+budgetId;
			 window.open(url,'','height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
			 }

	function updateNew(){
		var asOndate = document.getElementById("asOndate").value;
		var budid = document.getElementById("budgetDetail.budget.id").value;
		window.location = "/EGF/budget/budgetProposal!modifyBudgetDetailList.action?budgetDetail.budget.id="+budid+"&asOndate="+asOndate;
		return true;
	}

	var elementId = null;
    function showDocumentManager(obj){
        
    	if(obj.id == 'budgetDocUploadButton'){
            elementId = 'budgetDocNumber';
        }else{
            elementId = "bpBeanList["+obj+"].documentNumber";
        }
        docManager(document.getElementById(elementId).value);
    }
 var docNumberUpdater = function (docNumber){
            document.getElementById(elementId).value = docNumber;   
        }	
 var checkDatatableLoaded = window.setInterval(checkDatatable,1);
 function checkDatatable() {
	  if (jQuery('.t_fixed_header_main_wrapper_child')) {
		  jQuery('.t_fixed_header_main_wrapper_child').css('width','918px');
		  window.clearInterval(checkDatatableLoaded);
	  }
 } 			 			
	</script>
	</s:form>
	</body>  
</html>
