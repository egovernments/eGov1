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
<html>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<%@ taglib prefix="sx" uri="/WEB-INF/struts-dojo-tags.tld" %>

<%@ taglib prefix="EGF" tagdir="/WEB-INF/tags"%>
<%@ page language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<head>
<title>Direct Bank Payment</title>
<sx:head/>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/voucherHelper.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/directBankPaymentHelper.js"></script>
<script type="text/javascript" src="/EGF/resources/javascript/calender.js"></script>
<script type="text/javascript" src="/EGF/resources/javascript/calendar.js" ></script>
<script type="text/javascript" src="/EGF/resources/javascript/dateValidation.js"></script>
<script type="text/javascript" src="/EGF/commonjs/ajaxCommonFunctions.js"></script>

<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
	<style type="text/css">
	#codescontainer {position:absolute;left:11em;width:9%;text-align: left;}
	#codescontainer .yui-ac-content {position:absolute;width:600px;border:1px solid #404040;background:#fff;overflow:hidden;z-index:9050;}
	#codescontainer .yui-ac-shadow {position:absolute;margin:.3em;width:300px;background:#a0a0a0;z-index:9049;}
	#codescontainer ul {padding:5px 0;width:100%;}
	#codescontainer li {padding:0 5px;cursor:default;white-space:nowrap;}
	#codescontainer li.yui-ac-highlight {background:#ff0;}
	#codescontainer li.yui-ac-prehighlight {background:#FFFFCC;}
	

</style>
<script>
	path="${pageContext.request.contextPath}";
	var showMode='<s:property value="showMode"/>';	
		var totaldbamt=0,totalcramt=0;
		var OneFunctionCenter= <s:property value="isRestrictedtoOneFunctionCenter"/>; 
		//alert(">>.."+OneFunctionCenter);                 
		var makeVoucherDetailTable = function() {
			<s:if test='%{isRestrictedtoOneFunctionCenter == true}'>                                   
			var voucherDetailColumns = [                   
				{key:"functionid",hidden:true,width:90,  formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".functionIdDetail","hidden")},
				{key:"function",hidden:true,label:'Function Name',width:90, formatter:createTextFieldFormatterForFunctionJV(VOUCHERDETAILLIST,".functionDetail","hidden")},    
				{key:"glcodeid",hidden:true,width:90, formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".glcodeIdDetail","hidden")},
				{key:"glcode",label:'Account Code <span class="mandatory">*</span>',width:100,   formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".glcodeDetail","text")},
				{key:"accounthead", label:'Account Head',width:250,formatter:createLongTextFieldFormatterJV(VOUCHERDETAILLIST,".accounthead")},				
				{key:"debitamount",label:'Debit Amount',width:90, className:'bluebgheadtd' ,formatter:createAmountFieldFormatterJV(VOUCHERDETAILLIST,".debitAmountDetail","updateDebitAmountJV()")}, 
				{key:"creditamount",label:'Credit Amount',width:90,formatter:createAmountFieldFormatterJV(VOUCHERDETAILLIST,".creditAmountDetail","updateCreditAmountJV()")},
				{key:'Add',label:'Add',formatter:createAddImageFormatter("${pageContext.request.contextPath}")},
				{key:'Delete',label:'Delete',formatter:createDeleteImageFormatter("${pageContext.request.contextPath}")}
			];
			</s:if>
			<s:else>
			var voucherDetailColumns = [ 
       			{key:"functionid",hidden:true,width:90,  formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".functionIdDetail","hidden")},
       			{key:"function",label:'Function Name',width:90, formatter:createTextFieldFormatterForFunctionJV(VOUCHERDETAILLIST,".functionDetail","text")},         
       			{key:"glcodeid",hidden:true,width:90, formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".glcodeIdDetail","hidden")},
       			{key:"glcode",label:'Account Code <span class="mandatory">*</span>',width:100,   formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".glcodeDetail","text")},
       			{key:"accounthead", label:'Account Head',width:250,formatter:createLongTextFieldFormatterJV(VOUCHERDETAILLIST,".accounthead")},				
       			{key:"debitamount",label:'Debit Amount',width:90, className:'bluebgheadtd' ,formatter:createAmountFieldFormatterJV(VOUCHERDETAILLIST,".debitAmountDetail","updateDebitAmountJV()")}, 
       			{key:"creditamount",label:'Credit Amount',width:90,formatter:createAmountFieldFormatterJV(VOUCHERDETAILLIST,".creditAmountDetail","updateCreditAmountJV()")},
       			{key:'Add',label:'Add',formatter:createAddImageFormatter("${pageContext.request.contextPath}")},
       			{key:'Delete',label:'Delete',formatter:createDeleteImageFormatter("${pageContext.request.contextPath}")}
       		];
		</s:else>         
	    var voucherDetailDS = new YAHOO.util.DataSource(); 
		billDetailsTable = new YAHOO.widget.DataTable("billDetailTable",voucherDetailColumns, voucherDetailDS);
		billDetailsTable.on('cellClickEvent',function (oArgs) {
			var target = oArgs.target;
			var record = this.getRecord(target);
			var column = this.getColumn(target);
			if (column.key == 'Add') { 
			 	if(showMode=='nonbillPayment')
			 	return;
					billDetailsTable.addRow({SlNo:billDetailsTable.getRecordSet().getLength()+1});
				updateAccountTableIndex();
			}
			if (column.key == 'Delete') { 	
				if(showMode=='nonbillPayment')
			 		return;
				if(this.getRecordSet().getLength()>1){			
					this.deleteRow(record);
					allRecords=this.getRecordSet();
					for(var i=0;i<allRecords.getLength();i++){
						this.updateCell(this.getRecord(i),this.getColumn('SlNo'),""+(i+1));
					}
					updateDebitAmountJV();updateCreditAmountJV();
					check();
				}
				else{
					alert("This row can not be deleted");
				}
			}
			
			        
		}
		);
		<s:iterator value="billDetailslist" status="stat">
				billDetailsTable.addRow({SlNo:billDetailsTable.getRecordSet().getLength()+1,
					"functionid":'<s:property value="functionIdDetail"/>',
					"function":'<s:property value="functionDetail"/>',
					"glcodeid":'<s:property value="glcodeIdDetail"/>',
					"glcode":'<s:property value="glcodeDetail"/>',
					"accounthead":'<s:property value="accounthead"/>',
					"debitamount":'<s:text name="format.number" ><s:param value="%{debitAmountDetail}"/></s:text>',
					"creditamount":'<s:text name="format.number" ><s:param value="%{creditAmountDetail}"/></s:text>'
				});
				var index = '<s:property value="#stat.index"/>';
				updateGridPJV('functionIdDetail',index,'<s:property value="functionIdDetail"/>');
				updateGridPJV('functionDetail',index,'<s:property value="functionDetail"/>');
				updateGridPJV('glcodeIdDetail',index,'<s:property value="glcodeIdDetail"/>');
				updateGridPJV('glcodeDetail',index,'<s:property value="glcodeDetail"/>');
				updateGridPJV('accounthead',index,'<s:property value="accounthead"/>');
				updateGridPJV('debitAmountDetail',index,'<s:text name="format.number" ><s:param value="%{debitAmountDetail}"/></s:text>');
				updateGridPJV('creditAmountDetail',index,'<s:text name="format.number" ><s:param value="%{creditAmountDetail}"/></s:text>');
				totaldbamt = totaldbamt+parseFloat('<s:property value="debitAmountDetail"/>');
				totalcramt = totalcramt+parseFloat('<s:property value="creditAmountDetail"/>');
				updateAccountTableIndex();	
			</s:iterator>
				

		var tfoot = billDetailsTable.getTbodyEl().parentNode.createTFoot();
		var tr = tfoot.insertRow(-1);
		var th = tr.appendChild(document.createElement('th'));
		th.colSpan = 5;
		th.innerHTML = 'Total&nbsp;&nbsp;&nbsp;';
		th.align='right';
		var td = tr.insertCell(-1);
		td.width="90"
		td.innerHTML="<input type='text' style='text-align:right;width:100px;'  id='totaldbamount' name='totaldbamount' readonly='true' tabindex='-1'/>";
		var td = tr.insertCell(-1);
		td.width="90"
		td.align="right"
		td.innerHTML="<input type='text' style='text-align:right;width:100px;'  id='totalcramount' name='totalcramount' readonly='true' tabindex='-1'/>";
		document.getElementById('totaldbamount').value=totaldbamt.toFixed(2);
		document.getElementById('totalcramount').value=totalcramt.toFixed(2); 
		}
		var glcodeOptions=[{label:"--- Select ---", value:"0"}];
		<s:iterator value="dropdownData.glcodeList">
	    glcodeOptions.push({label:'<s:property value="glcode"/>', value:'<s:property value="id"/>'})
	</s:iterator>
	var detailtypeOptions=[{label:"--- Select ---", value:"0"}];
	<s:iterator value="dropdownData.detailTypeList">
	    detailtypeOptions.push({label:'<s:property value="name"/>', value:'<s:property value="id"/>'})
	</s:iterator>
	
	
	
		
	var makeSubLedgerTable = function() {
		var subledgerColumns = [ 
			{key:"subledgerCode",hidden:true,width:90, formatter:createSLTextFieldFormatterJV(SUBLEDGERLIST,".subledgerCode","hidden")},
			{key:"glcode.id",label:'Account Code <span class="mandatory">*</span>',width:90, formatter:createDropdownFormatterJV(SUBLEDGERLIST,"loaddropdown(this)"),  dropdownOptions:glcodeOptions},
			{key:"detailTypeName",hidden:true,width:90, formatter:createSLTextFieldFormatterJV(SUBLEDGERLIST,".detailTypeName","hidden")},
			{key:"detailType.id",label:'Type <span class="mandatory">*</span>',width:90, formatter:createDropdownFormatterJV1(SUBLEDGERLIST),dropdownOptions:detailtypeOptions},
			{key:"detailCode",label:'Code <span class="mandatory">*</span>',width:120, formatter:createSLDetailCodeTextFieldFormatterJV(SUBLEDGERLIST,".detailCode","splitEntitiesDetailCode(this)", ".search", "openSearchWindowFromJV(this)")},
			{key:"detailKeyId",hidden:true,width:100, formatter:createSLHiddenFieldFormatterJV(SUBLEDGERLIST,".detailKeyId")},
			{key:"detailKey",label:'Name',width:180, formatter:createSLLongTextFieldFormatterJV(SUBLEDGERLIST,".detailKey","")},
			{key:"amount",label:'Amount',width:90, formatter:createSLAmountFieldFormatterJV(SUBLEDGERLIST,".amount")},
			{key:'Add',label:'Add',formatter:createAddImageFormatter("${pageContext.request.contextPath}")},
			{key:'Delete',label:'Delete',formatter:createDeleteImageFormatter("${pageContext.request.contextPath}")}
		];
	    var subledgerDS = new YAHOO.util.DataSource(); 
		subLedgersTable = new YAHOO.widget.DataTable("subLedgerTable",subledgerColumns, subledgerDS);
		subLedgersTable.on('cellClickEvent',function (oArgs) {
			var target = oArgs.target;
			var record = this.getRecord(target);
			var column = this.getColumn(target);
			if (column.key == 'Add') { 
			if(showMode=='nonbillPayment')
			 		return;
				subLedgersTable.addRow({SlNo:subLedgersTable.getRecordSet().getLength()+1});
				updateSLTableIndex();
				check();
			}
			if (column.key == 'Delete') { 	
			if(showMode=='nonbillPayment')
			 		return;		
				if(this.getRecordSet().getLength()>1){			
					this.deleteRow(record);
					allRecords=this.getRecordSet();
					for(var i=0;i<allRecords.getLength();i++){
						this.updateCell(this.getRecord(i),this.getColumn('SlNo'),""+(i+1));
					}
				}
				else{
					alert("This row can not be deleted");
				}
			}        
		});
	
		<s:iterator value="subLedgerlist" status="stat">
				subLedgersTable.addRow({SlNo:subLedgersTable.getRecordSet().getLength()+1,
					"subledgerCode":'<s:property value="subledgerCode"/>',
					"glcode.id":'<s:property value="glcode.id"/>',
					"detailType.id":'<s:property value="detailType.id"/>',
					"detailTypeName":'<s:property value="detailTypeName"/>',
					"detailCode":'<s:property value="detailCode"/>',
					"detailKeyId":'<s:property value="detailKeyId"/>',
					"detailKey":'<s:property value="detailKey"/>',
					"debitAmount":'<s:text name="format.number" ><s:param value="%{debitAmount}"/></s:text>',
					"creditAmount":'<s:text name="format.number" ><s:param value="%{creditAmount}"/></s:text>'
				});'<s:property value="glcode.id"/>'
				var index = '<s:property value="#stat.index"/>';
				updateGridSLDropdownJV('glcode.id',index,'<s:property value="glcode.id"/>','<s:property value="subledgerCode"/>');
				updateGridSLDropdownJV('detailType.id',index,'<s:property value="detailType.id"/>','<s:property value="detailTypeName"/>');
				updateSLGridPJV('detailCode',index,'<s:property value="detailCode"/>');
				updateSLGridPJV('subledgerCode',index,'<s:property value="subledgerCode"/>');
				updateSLGridPJV('detailKeyId',index,'<s:property value="detailKeyId"/>');
				updateSLGridPJV('detailKey',index,'<s:property value="detailKey"/>');
				updateSLGridPJV('amount',index,'<s:text name="format.number" ><s:param value="%{amount}"/></s:text>');
				updateSLTableIndex();
			</s:iterator>
	
	}
	var amountshouldbenumeric='<s:text  name="amount.should.be.numeric"/>';
	var succesMessage='<s:text name="directbank.transaction.succcess"/>';
	var totalsnotmatchingamount='<s:text name="totals.not.matching.amount"/>';
	var 	button='<s:property value="button"/>';
	</script>
	
</head>
<body onload="onLoadTask_new();loadDropDownCodesExcludingCashAndBank();loadDropDownCodesFunction();">
<s:form  action="directBankPayment" theme="css_xhtml" name="dbpform" validate="true">
<s:push value="model">
<jsp:include page="../budget/budgetHeader.jsp">
<jsp:param value="Direct Bank Payment" name="heading"/>
</jsp:include>
<div class="formmainbox"><div class="formheading"/><div class="subheadnew">Create Direct  Bank Payment</div>
</div>
		<div align="center">
<font  style='color: red ;'> 
<p class="error-block" id="lblError" ></p>
</font>
</div>
<span class="mandatory" >
				<div id="Errors" ><s:actionerror /><s:fielderror /></div>
				<s:actionmessage />
			</span>
	<table border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr>
		<td width="10%" class="bluebox"></td>
		<s:if test="%{shouldshowVoucherNumber()}">
			<td class="bluebox" width="22%"><s:text name="voucher.number"/><span class="mandatory">*</span></td>
			<td class="bluebox" width="22%"><s:textfield name="voucherNumber" id="voucherNumber" /></td></s:if>
			<s:hidden name="id"/>
			
			<td class="bluebox" width="18%"><s:text name="voucher.date"/><span class="mandatory">*</span></td>
			<s:date name='voucherDate' id="voucherDateId" format='dd/MM/yyyy'/>
			<td class="bluebox" width="34%">
			<div name="daterow" >
			<s:textfield  name="voucherDate" id="voucherDate" maxlength="10" onkeyup="DateFormat(this,this.value,event,false,'3')" size="15" value="%{voucherDateId}"/><A href="javascript:show_calendar('forms[0].voucherDate',null,null,'DD/MM/YYYY');" style="text-decoration:none" align="left"><img img width="18" height="18" border="0" align="absmiddle" alt="Date" src="/egi/resources/erp2/images/calendaricon.gif" /></A> </div></td>
		</tr>
	<%@include file="directBankPayment-form.jsp"%>  
	
	
	<div class="subheadsmallnew"></div>
	<div align="left" class="mandatory">* Mandatory Fields</div>                          
	</div>
	<s:hidden name="typeOfAccount" id="typeOfAccount" value="%{typeOfAccount}"/>
	
	<tr>
  		<td  colspan="6"> 
			<s:if test='%{! wfitemstate.equalsIgnoreCase("END")}'>
				<%@include file="../voucher/workflowApproval.jsp"%>
			</s:if>
		</td>
  	</tr>
	<tr>
		<td class="bluebox">&nbsp;</td>
		<td class="bluebox" ><strong>Comments</strong></td>
		<td class="bluebox" colspan="4"><s:textarea name="comments" id="comments" cols="100" rows="3" onblur="checkLength(this)" value="%{getComments()}"/></td>
	</tr>
	</table>
	<table align="center">
	<tr class="buttonbottom" id="buttondiv" style="align:middle" >
		<s:hidden  name="actionname" id="actionName" value="%{action}"/>   
		<s:iterator value="%{getValidActions()}" var="p"  status="s">
	 	<td> <s:submit type="submit" cssClass="buttonsubmit" value="%{description}" id="wfBtn%{#s.index}" name="%{name}" method="create" onclick="return validate('%{name}','%{description}')"/> </td> 
		</s:iterator>
		<td><input type="button" value="Close" onclick="javascript:window.close()" class="button" /></td>
	</tr>
	<tr class="buttonbottom" id="buttondivdefault" style="align:middle;display:none" >
	<td><input type="button" value="Close" onclick="javascript:window.close()" class="button" /></td>
	</tr>
	</table>
</s:push>
<s:hidden name="showMode"/>
<s:token/>
</s:form>
<s:if test="%{!validateUser('createpayment')}">
		<script>
			//document.getElementById('searchBtn').disabled=true;
			document.getElementById('Errors').innerHTML='<s:text name="payment.invalid.user"/>';
			if(document.getElementById('vouchermis.departmentid'))
			{
				var d = document.getElementById('vouchermis.departmentid');
				d.options[d.selectedIndex].text='----Choose----';
				d.options[d.selectedIndex].text.value=-1;
			}
			disableControls(0,true);
			document.getElementById("closeButton").disabled=false;
		</script>
		</s:if>
<s:if test="%{validateUser('deptcheck')}">
				<script>
					if(document.getElementById('vouchermis.departmentid'))
						document.getElementById('vouchermis.departmentid').disabled=true;
				</script>
			</s:if>
			
			
			
			
<SCRIPT type="text/javascript">

function validateAppoveUser(name,value){
			//document.getElementById('lblError').innerHTML ="";
			document.getElementById("actionName").value= name;

			<s:if test="%{wfitemstate =='END'}">
				if(value == 'Approve' || value == 'Reject') {
					document.getElementById("approverUserId").value=-1;
					return true;
				}
			</s:if>
			<s:else>
				if( (value == 'Approve' || value == 'Forward' || value=='Save And Forward' ) && null != document.getElementById("approverUserId") && document.getElementById("approverUserId").value == -1){
					alert("please select User");
					//document.getElementById('lblError').innerHTML ="Please Select the user";
					return false;
				}
			</s:else>
			
			return true;
		}
		

function	onLoadTask_new()
{
	//alert(showMode);                                                      
	if(button!=null && button!="")
	{
		if(document.getElementById("Errors").innerHTML=='')  
		{
			alert(succesMessage);
			if(button=="Save_Close")
				{
				window.close();
				}
			else if(button=="Save_View")
				{
						var vhId='<s:property value="voucherHeader.id"/>';
						document.forms[0].action = "${pageContext.request.contextPath}/voucher/preApprovedVoucher!loadvoucherview.action?vhid="+vhId;
						document.forms[0].submit();
				}
			else if(button=="Save_New")
				{      	
					document.forms[0].button.value='';
				    document.forms[0].action = "directBankPayment!newform.action";
				 	document.forms[0].submit();
				}
		}
		
		
 	}else
 	{
 		
 		<s:if test="%{showMode=='nonbillPayment'}">
			//alert('<s:property value="showMode"/>');
			if(document.getElementById("Errors").innerHTML!='')
			{
			document.getElementById('buttondiv').style.display="none";
			document.getElementById('buttondivdefault').style.display="block";
			}
		</s:if>
 	}
 	
		 <s:if test="%{validateUser('balancecheck')}">
					if(document.getElementById('balanceText'))
					{
						document.getElementById('balanceText').style.display='block';
						document.getElementById('balanceAvl').style.display='block';
					}
		</s:if>
		
		if(showMode=='nonbillPayment')
		{
		disableForNonBillPayment();	
		disableYUIAddDeleteButtons(true);
		}
		
}

function populateAccNum(branch){
	var fundObj = document.getElementById('fundId');
	var bankbranchId = branch.options[branch.selectedIndex].value;
	var index=bankbranchId.indexOf("-");
	var bankId = bankbranchId.substring(0,index);
	var brId=bankbranchId.substring(index+1,bankbranchId.length);
	
	var vTypeOfAccount = '<s:property value="%{typeOfAccount}"/>';
	
	populateaccountNumber({fundId: fundObj.options[fundObj.selectedIndex].value,bankId:bankId,branchId:brId,typeOfAccount:vTypeOfAccount})
}

</SCRIPT>
</body>
</html>
