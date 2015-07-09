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
<jsp:include page="../voucher/vouchertrans-filter-new-contingent.jsp"/>
<tr>
	<td class="greybox"></td>
	<td class="greybox"><s:text name="function"/></td>
	<td class="greybox" ><s:textfield name="commonBean.functionName" id="commonBean.functionName" onkeyup="autocompletecodeFunctionHeader(this,event)" onblur="fillNeibrAfterSplitFunctionHeader(this)"  size="30"/>
	 <s:hidden name="commonBean.functionId" id="commonBean.functionId" /></td>
	<td class="greybox"></td>
	<td class="greybox"></td>
	<!--<td class="greybox"><s:text name="inward.serial.number"/></td>
	<td class="greybox"><s:textfield name="commonBean.inwardSerialNumber" id="commonBean.inwardSerialNumber"/></td>
--></tr>

<tr>
	<td class="bluebox"></td>
	<td class="bluebox"><s:text name="voucher.narration"/></td>
	<td class="bluebox" colspan="3"><s:textarea name="description" id="description"  cols="100"/><br/><span class="highlight2">Max. 1024 characters</span></td> 
</tr>
<tr id="budgetReappRow">
<td class="greybox"></td>
<td class="greybox"><s:text name="budget.reappropriation.number"/></td>
<td class="greybox"><s:textfield name="commonBean.budgetReappNo" id="commonBean.budgetReappNo"/></td>
<td class="greybox"></td>
<td class="greybox"></td>
</tr>


<tr>
<td class="greybox" colspan="6" style="text-align:center"/>
<div class="generatecheque">
<a onclick="hideShow()" href="#">
Show/Hide Details</a></div>
</tr>
<table>
<tr>
<td colspan="6">
<hr class="blankline"/>
</td>
</tr>
</table>
<center>
<div>
<table width="90%" cellspacing="0" cellpadding="0" border="0" class="tablebottom" align="center">
	<tr>
	<td class="bluebox3" colspan="5">
		<div class="billheadnew">
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
				<tr>
				<td class="bluebox"><s:text name="subledger.type"/></td>
				<td  class="bluebox" align="left" ><s:select name="commonBean.subledgerType" id="commonBean.subledgerType"  list="dropdownData.accountDetailTypeList" listKey="id" listValue="description" headerKey="" onchange="load_COA_Entities(this)" headerValue="---Choose---"/> 
				<td class="bluebox"><s:text name="bill.subtype"/><span class="mandatory">*</span></td>
				<td  class="bluebox" ><s:select name="commonBean.billSubType" list="dropdownData.billSubTypeList"     listKey="id" listValue="name" headerKey="" headerValue="----Choose----"   onchange="loadCheckList(this)"/></td>
				</tr>
				<tr>
			
				<td valign="top" class="greybox">
				<div  id="entitycode">Entity Code:</div> </td>
							<td class="greybox" ><span class="greybox"> <input type="text" name="detailCode"  id="detailCode"  autocomplete='off' onblur="splitEntities(this)">
							</span>
							<span id="genricimage"><img src="/egi/resources/erp2/images/plus1.gif"   onclick="openSearchWindow(this, 'subledger');" ></span>&nbsp;&nbsp;
							</td>
							<td colspan="2" class="greybox"><input type="text"  id="detailName" name="detailName" size="45"  />
							<input type="hidden" id="detailKey" name="detailKey"/>
						</td>
												
					</tr>
					<tr>
			<td class="bluebox"><s:text name="party.bill.number"/></td>
			<td class="bluebox"><s:textfield name="commonBean.partyBillNumber" id="commonBean.partyBillNumber"/></td>
			<td class="bluebox"><s:text name="party.bill.date"/></td>
			<s:date name='commonBean.partyBillDate' id="commonBean.partyBillDateId" format='dd/MM/yyyy'/>
			<td class="bluebox"><s:textfield name="commonBean.partyBillDate"  id="partyBillDate" onkeyup="DateFormat(this,this.value,event,false,'3')" value="%{commonBean.partyBillDateId}"/>
				<a href="javascript:show_calendar('cbill.partyBillDate');"	style="text-decoration: none">&nbsp;<img tabIndex="-1"
													src="/egi/resources/erp2/images/calendaricon.gif"		border="0" /></A></td>
			
			</tr>
			<tr>
		<td class="blueborderfortd1" style="text-align:left"><s:text name="payto"/></td>
		<td class="blueborderfortd1" style="text-align:left;width:240" colspan="4" >
		<s:textfield name="commonBean.payto" id="commonBean.payto" size="55"/>
		</td>
		</tr>
				</tbody>
			</table>
		</div>
		</td>
		</tr>
		</table>
	</div>	
					<div class="yui-skin-sam" align="center">
						<div id="billDetailTable"></div>
					</div>
					
					<script>
				    makeVoucherDetailTable();
				    document.getElementById('billDetailTable').getElementsByTagName('table')[0].width="90%";
					</script>
				<div class="yui-skin-sam" align="center">
					<div id="billDetailTableCredit"></div>
					</div>
				
					<script>
			   	makeVoucherDetailTableCredit();
			   	document.getElementById('billDetailTableCredit').getElementsByTagName('table')[0].width="90%";
				</script>
		
		<div class="yui-skin-sam" align="center">
		<div id="billDetailTableNet"></div>
		</div>
	
		<script>
		makeVoucherDetailTableNet();
		document.getElementById('billDetailTableNet').getElementsByTagName('table')[0].width="90%";
		</script>
<div id="codescontainer"></div>

<div>
<table width="90%" cellspacing="0" cellpadding="0" border="0" class="tablebottom" align="center">
		<tr align="center">
	
		<td  align="center" class="blueborderfortd1" style="text-align:center"><input type="button" name="Done" onclick="updateTabels()" class="buttongeneral" value="Done" align="middle"/>
		</td>
	
	</tr>
		</table>
</div>
<div>

<table width="90%" cellspacing="0" cellpadding="0" border="0"  align="center">
<tr>
	<td colspan="5">
<hr/>
	</td>
</tr>
</table>
</div>
<div id="summary">
				<div class="yui-skin-sam" align="center">
						<div id="billDetailTableFinal"></div>
					</div>
					
					<script>
				    makeVoucherDetailTableFinal();
				    document.getElementById('billDetailTableFinal').getElementsByTagName('table')[0].width="90%";
					</script>
					<div class="yui-skin-sam" align="center">
					<div id="billDetailTableCreditFinal"></div>
					</div>
				
					<script>
			   	makeVoucherDetailTableCreditFinal();
			   	document.getElementById('billDetailTableCreditFinal').getElementsByTagName('table')[0].width="90%";
				</script>
		
			

		<div class="yui-skin-sam" align="center">
		<div id="billDetailTableNetFinal"></div>
		</div>
	
		<script>
		makeVoucherDetailTableNetFinal();
		document.getElementById('billDetailTableNetFinal').getElementsByTagName('table')[0].width="90%";
		</script>

<div>
<table width="90%" cellspacing="0" cellpadding="0" border="0" align="center">
<tr>
	<td colspan="5">
	<hr>
	</td>
</tr>
</table>
</div>
	
<div>	
<table class="tablebottom" width="90%" cellspacing="0" cellpadding="0" border="0">
<tbody>
<tr>
<th colspan="5">
<div class="subheadsmallnew"><s:text name="subledger.details"/></div>
</th>
</tr>
</tbody></table>  
</div>
	<div class="yui-skin-sam" align="center">
		<div id="billDetailTableSubledger"></div>
		</div>
		<script>
		makeVoucherDetailTableSubledger();
		document.getElementById('billDetailTableSubledger').getElementsByTagName('table')[0].width="90%";
		</script>

</div>	
 </center>
 
  <s:hidden  id="scriptName" value="cbill.nextUser"/>
	 
	 
