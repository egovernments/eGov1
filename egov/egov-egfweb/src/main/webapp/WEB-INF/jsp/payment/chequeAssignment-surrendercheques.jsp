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
<html>
<head>
<link rel="stylesheet" type="text/css" href="/EGF/resources/css/ccMenu.css" />
<title><s:text name="surrender.and.reassign.cheques" /></title>
</head>
<body>
	<s:form action="chequeAssignment" theme="simple">
		<s:token />
		<jsp:include page="../budget/budgetHeader.jsp">
			<jsp:param name="heading" value="Surrender/Reassign Cheque" />
		</jsp:include>
		<span class="mandatory"> <s:actionerror /> <s:fielderror /> <s:actionmessage />
		</span>
		<div class="formmainbox">
			<div class="subheadnew"></div>
			<div class="formmainbox">
				<div class="subheadnew">
					<s:property value="bank_account_dept" />
				</div>
				<!--<s:hidden name="department" />
			-->
				<s:hidden name="bankaccount" id="bankaccount" />
				<s:hidden name="bank_branch" />
				<s:hidden name="fromDate" />
				<s:hidden name="toDate" />
				<s:hidden name="voucherNumber" />
				<s:hidden name="instrumentNumber" />
				<table align="center" width="100%" cellpadding="0" cellspacing="0">
					<tr>
						<th class="bluebgheadtdnew"><s:text name="Sl No." /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.instrument.serialno" /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.instrument.no" /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.transacton.no" /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.instrument.amount" /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.instrument.date" /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.partycode" /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.paymentvoucherno" /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.surrender" /></th>
						<th class="bluebgheadtdnew"><s:text
								name="chq.assignment.surrendarreason" /></th>
						<s:if test="%{!isChequeNoGenerationAuto()}">
							<th class="bluebgheadtdnew"><s:text
									name="chq.assignment.instrument.serialno" /></th>
							<th class="bluebgheadtdnew"><s:text
									name="chq.assignment.newchequenumber" /></th>
							<th class="bluebgheadtdnew"><s:text
									name="chq.assignment.newchequedate" /></th>

						</s:if>
					</tr>
					<s:if test="%{instrumentHeaderList.size()>0 }">
						<s:iterator var="p" value="instrumentHeaderList" status="stat">
							<tr>
								<s:hidden name="instrumentHeaderId" value="%{id}" />
								<s:hidden name="paymentVoucherNumber"
									value="%{voucherHeaderId.id}" />
								<td style="text-align: center" class="blueborderfortdnew" />
								<s:property value="#stat.index+1" />
								</td>
								<td style="text-align: center" class="blueborderfortdnew"><s:property
										value="%{serialNo}" /></td>
								<td style="text-align: center" class="blueborderfortdnew"><s:property
										value="%{instrumentNumber}" /></td>
								<td style="text-align: center" class="blueborderfortdnew"><s:property
										value="%{transactionNumber}" /></td>
								<td style="text-align: right" class="blueborderfortdnew"><s:text
										name="format.number">
										<s:param value="%{instrumentAmount}" />
									</s:text></td>
								<td style="text-align: center" class="blueborderfortdnew"><s:date
										name="%{instrumentDate}" format="dd/MM/yyyy" /></td>
								<td style="text-align: center" class="blueborderfortdnew"><s:property
										value="%{payTo}" /></td>

								<td style="text-align: center" class="blueborderfortdnew">
									<s:iterator var="v" value="instrumentVouchers" status="st">
										<A href="#"
											onclick='openDetails(<s:property value="%{voucherHeaderId.id}"/>);'>
											<s:property value="%{voucherHeaderId.voucherNumber}" />
										</A>
									</s:iterator>
								</td>
								<td style="text-align: center" class="blueborderfortdnew"><s:checkbox
										name="surrender"
										value='%{surrender[#stat.index]!=null?true:false}'
										fieldValue="%{id}" /></td>
								<td style="text-align: center" class="blueborderfortdnew">
									<s:select name="surrendarReasons" id="surrendarReasons"
										list="surrendarReasonMap" headerKey="-1"
										headerValue="----Choose----"
										value='%{surrendarReasons[#stat.index]}' /> <s:if
										test="%{!isChequeNoGenerationAuto()}">
										<td style="text-align: right" class="blueborderfortdnew">
											<s:select name="newSerialNo" id="newSerialNo%{#stat.index}"
												list="chequeSlNoMap" value='%{newSerialNo[#stat.index]}' />
										</td>
										<td style="text-align: left" class="blueborderfortdnew"><s:textfield
												name="newInstrumentNumber"
												id="newInstrumentNumber%{#stat.index}"
												value='%{newInstrumentNumber[#stat.index]}'
												onblur="validatechequeno(this)" /></td>
										<td style="text-align: left" class="blueborderfortdnew"><s:textfield
												name="newInstrumentDate"
												value="%{newInstrumentDate[#stat.index]}" /></td>
									</s:if>
							</tr>
						</s:iterator>
						<tr>
							<td class="greybox"><s:text name="chq.issued.department" /><span
								class="mandatory">*</span> <s:select name="department"
									id="department" list="dropdownData.departmentList" listKey="id"
									listValue="name" headerKey="-1"
									headerValue="----Choose----" value="%{department}" /></td>
						</tr>
					</s:if>


				</table>
				<br />

				<s:if test="%{instrumentHeaderList.size()>0}">
					<div class="buttonbottom">
						<s:hidden name="button" id="button" />
						<s:submit type="submit" cssClass="buttonsubmit" name="Surrender"
							value="Surrender" onclick="return surrenderChq();" method="save" />
						<s:submit type="submit" cssClass="buttonsubmit"
							name="SurrenderAndReassign" class="submit"
							value="Surrender & Reassign" onclick="return Reassign();"
							method="save" />
						<input type="button" value="Close"
							onclick="javascript:window.close()" class="button" />
					</div>
				</s:if>
				<s:else>
					<div class="subheadsmallnew" id="noRecordsDiv">No Records
						Found</div>
				</s:else>
			</div>


			<s:token />
	</s:form>
	<script>
		function validatechequeno(obj)
			{
				if(isNaN(obj.value))
				{
					bootbox.alert('Cheque number contains alpha characters.');
					obj.value='';
					return false;
				}
				var index = obj.id.substring(19,obj.id.length);
				if(obj.value=='')
					return true;
				//bootbox.alert(index);		
				if(document.getElementById('department') && document.getElementById('department').options[document.getElementById('department').selectedIndex].value==-1)
				{
					bootbox.alert('Select Cheque Issued From');
					obj.value='';
					return false;
				}
				var name=obj.id;
				name=name.replace("InstrumentNumber","SerialNo");
			   var slObj=	document.getElementById(name);
				var dept = document.getElementById('department').options[dom.get('department').selectedIndex].value;
				var slNo = slObj.options[slObj.selectedIndex].value;
				var url = '${pageContext.request.contextPath}/voucher/common!ajaxValidateChequeNumber.action?bankaccountId='+document.getElementById('bankaccount').value+'&chequeNumber='+obj.value+'&index='+index+'&departmentId='+dept+"&serialNo="+slNo;
				var transaction = YAHOO.util.Connect.asyncRequest('POST', url,callback , null);
			}
			
			
			var callback = {
				success: function(o) {
					var res=o.responseText;
					res = res.split('~');
					if(res[1]=='false')
					{
						bootbox.alert('Enter valid cheque number or This Cheque number has been already used');
						document.getElementById('newInstrumentNumber['+parseInt(res[0])+']').value='';
					}
			    },
			    failure: function(o) {
			    	bootbox.alert('failure');
			    }
			}
			
		
	function openDetails(val)
	{
	var mode="view";
 	window.open("/EGF/voucher/preApprovedVoucher!loadvoucherview.action?vhid="+val+"&showMode="+mode,"","height=650,width=900,scrollbars=yes,left=30,top=30,status=yes");
 	}
 	function surrenderChq(){
		
 		document.getElementById('button').value='surrender';
 		document.getElementById('department').disabled=false;
		return true;
 	}
  	function Reassign()
 	{
	 	document.getElementById('button').value='surrenderAndReassign';
	 	var chqGenMode='<s:property value="isChequeNoGenerationAuto()"/>';
	 	var alertNumber='<s:text name="chq.number.missing.alert"/>';
	 	var alertOnlyNumber='<s:text name="chq.number.should.numeric"/>';
	 	var alertDate='<s:text name="chq.date.missing.alert"/>';
	 	if(chqGenMode=='false')
	 	{
	 		var surrenderObj=document.getElementsByName('surrender');
	 		var newChqNoObj=document.getElementsByName('newInstrumentNumber');
	 		var newChqDateObj=document.getElementsByName('newInstrumentDate');
			var i;
	 		for(i=0;i<surrenderObj.length;i++)
	 		{
	 		 if(surrenderObj[i].checked==true)
	 			{
	 				if(newChqNoObj[i].value==""||newChqNoObj[i].value==undefined)
					{
						bootbox.alert(alertNumber);
						newChqNoObj[i].focus();
						return false;
					}
					else
					{
					if(isNaN(newChqNoObj[i].value))
					{
					bootbox.alert(alertOnlyNumber);
					}
					}
					
					if(newChqDateObj[i].value=="" || newChqDateObj[i].value==undefined)
					{
					bootbox.alert(alertDate);
					newChqDateObj[i].focus();
					return false;
	 				}
	 			}
	  	
	 		}
	 	
 		}
		document.getElementById('department').disabled=false;
		
 	}
 	
 		
		<s:if test="%{isFieldMandatory('department')}">
				document.getElementById('department').disabled=true;
		</s:if>	
	</script>
</body>

</html>
