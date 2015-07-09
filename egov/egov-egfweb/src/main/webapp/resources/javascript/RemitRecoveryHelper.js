/*#-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
# accountability and the service delivery of the government  organizations.
#   
#  Copyright (C) <2015>  eGovernments Foundation
#   
#  The updated version of eGov suite of products as by eGovernments Foundation 
#  is available at http://www.egovernments.org
#   
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  any later version.
#   
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#   
#  You should have received a copy of the GNU General Public License
#  along with this program. If not, see http://www.gnu.org/licenses/ or 
#  http://www.gnu.org/licenses/gpl.html .
#   
#  In addition to the terms of the GPL license to be adhered to in using this
#  program, the following additional terms are to be complied with:
#   
# 1) All versions of this program, verbatim or modified must carry this 
#    Legal Notice.
#   
# 2) Any misrepresentation of the origin of the material is prohibited. It 
#    is required that all modified versions of this material be marked in 
#    reasonable ways as different from the original version.
#   
# 3) This license does not grant any rights to any user of the program 
#    with regards to rights under trademark law for use of the trade names 
#    or trademarks of eGovernments Foundation.
#   
# In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#-------------------------------------------------------------------------------*/
function populateNarration(accnumObj){
    
	var accnum =  accnumObj.options[accnumObj.selectedIndex].value;
	var bankbranchObj=document.getElementById('bankId');
	var bankbranchId = bankbranchObj.options[bankbranchObj.selectedIndex].value;
	var index=bankbranchId.indexOf("-");
	var branchId=bankbranchId.substring(index+1,bankbranchId.length);
	var url = '../voucher/common!loadAccNumNarration.action?accnum='+accnum+'&branchId='+branchId;
	YAHOO.util.Connect.asyncRequest('POST', url, postTypeFrom, null);
}

/*function loadBalance(obj)
{
	if(dom.get('voucherdate').value=='')
	{
		alert("Please Select the Voucher Date!!");
		obj.options.value=-1;
		return;
	}
	if(obj.options[obj.selectedIndex].value==-1)
		dom.get('balance').value='';
	else
		populatebalance({bankaccount:obj.options[obj.selectedIndex].value,voucherDate:dom.get('voucherdate').value+'&date='+new Date()});
}
*/

function populateAvailableBalance(accnumObj){
	if(document.getElementById('voucherDate').value=='')
	{
		alert("Please Select the Voucher Date!!");
		accnumObj.options.value=-1;
		return;
	}
	if(accnumObj.options[accnumObj.selectedIndex].value==-1)
		document.getElementById('availableBalance').value='';
	else
		populateavailableBalance({bankaccount:accnumObj.options[accnumObj.selectedIndex].value,voucherDate:document.getElementById('voucherDate').value+'&date='+new Date()});

}

var callback = {
		success: function(o){
	        alert(o.responseText.value);
			document.getElementById('availableBalance').value=o.responseText;
			},
			failure: function(o) {
		    }
		}                  
		
var postTypeFrom = {
success: function(o) {
		document.getElementById('accnumnar').value= o.responseText;
		},
    failure: function(o) {
    	alert('failure');
    }
}

function loadBank(fund)
{
	var vTypeOfAccount = document.getElementById('typeOfAccount').value;
populatebankId({fundId:fund.options[fund.selectedIndex].value, typeOfAccount:vTypeOfAccount})	
}




function enableAll()
{
	for(var i=0;i<document.forms[0].length;i++)
		document.forms[0].elements[i].disabled =false;
}

function disableControls(frmIndex, isDisable)
{
	for(var i=0;i<document.forms[frmIndex].length;i++)
		document.forms[frmIndex].elements[i].disabled =isDisable;
}

function balanceCheck(obj, name, value)   
		{
			
			if(!validateAppoveUser(name,value))
				return false;
	
			if(obj.id=='wfBtn1') // in case of Reject
				return true;
			if(document.getElementById('balanceAvl') && document.getElementById('balanceAvl').style.display=="block" )
			{
				if(parseFloat(document.getElementById('amount').value)>parseFloat(document.getElementById('availableBalance').value))
				{
					alert(insuffiecientBankBalance);
					return false;
				}
			}
			return true;
		}
