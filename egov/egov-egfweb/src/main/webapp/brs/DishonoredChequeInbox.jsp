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
<%@ page language="java"  %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib tagdir="/WEB-INF/tags" prefix="egovtags"%>
<c:catch var ="catchException">
<%@ page import="java.util.*,java.lang.String,
		org.apache.log4j.Logger,
		com.exilant.eGov.src.transactions.brs.*,
		com.exilant.eGov.src.transactions.brs.DishonoredChequeForm,com.exilant.eGov.src.transactions.VoucherTypeForULB,
		org.egov.general.model.*,
		java.text.SimpleDateFormat,org.egov.utils.FinancialConstants"
%>
<html>
<head>
<title>Process Dishonour Cheque</title>

	
<script>
<%
DishonoredChequeForm DishonoredChequeForm=(DishonoredChequeForm)request.getAttribute("DishonoredChequeForm");
VoucherTypeForULB voucherTypeForULB=new VoucherTypeForULB();
%>	
	
	var vouchHeaderId="";
	var glcodeParam="";
	var bankGlcodearray = new Array();
	var chqGlcodearray = new Array();
	var glcodearrayForPayRev = new Array();
	
	var voucherNumGenTypeReceipt='<%=(String) voucherTypeForULB.readVoucherTypes(FinancialConstants.STANDARD_VOUCHER_TYPE_RECEIPT) %>';
	var voucherNumGenTypePayment='<%= (String)voucherTypeForULB.readVoucherTypes(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT) %>';
	var voucherNumGenTypeJournal='<%= (String)voucherTypeForULB.readVoucherTypes("Journal") %>';
	// added by raja for yui		
	var acccodeArray;
	var codeObj;
	var yuiflag = new Array();
	
function openSearch(obj)
{
	var a = new Array(2);
	var sRtn =showModalDialog("../HTML/Search.html?screenName=mappedCodesAll","","dialogLeft=300;dialogTop=210;dialogWidth=305pt;dialogHeight=300pt;status=no;");
	if ( sRtn != '' )
	{
	a = sRtn.split("`~`");
	var x =getControlInBranch(obj.parentNode,'chartOfAccounts_glCode');
	var y =getControlInBranch(obj.parentNode.parentNode,'glcodeChIdP');
	var z =getControlInBranch(obj.parentNode.parentNode,'accCodedescChP');
	
	x.value = a[0];
	y.value = a[2];
	z.value = a[1];
	}
}
function checkCreditAmt()
{
	textBox= document.getElementById("creditAmt");
	textBoxVal= textBox.value;

}
function setDebitOrCredit(obj)
{
var currRow=getRow(obj);
 if(obj.value!=null&& obj.value!="")
 {
      amt=eval(obj.value).toFixed(2);
      obj.value=amt;
      var debit=getControlInBranch(currRow,"debitAmountR");
	  var debitAmt=parseFloat(debit.value,2);  
	  if(debitAmt>0)
		{
		if(amt>debitAmt)
		{
		alert("Reversal Amount "+amt+" cannot be greater than Debit Amount "+debitAmt);
		return false;
		}
		getControlInBranch(currRow,"debitAmount").value=obj.value;
		getControlInBranch(currRow,"creditAmount").value="0.0";
		}else 
		{
		var credit=getControlInBranch(currRow,"creditAmountR");
		var creditAmt=parseFloat(credit.value,2);
		if(amt>creditAmt)
		{
		alert("Reversal Amount "+amt+" cannot be greater than Credit Amount "+creditAmt);
		return false;
		}
		getControlInBranch(currRow,"debitAmount").value="0.0";
		getControlInBranch(currRow,"creditAmount").value=obj.value;
		}
		
 }else
 {
 	obj.value="0.0";
	getControlInBranch(currRow,"debitAmount").value="0.0";
	getControlInBranch(currRow,"creditAmount").value="0.0";
 
 }
 return true;
}


function autoPopulateDebitOrCredit()
{
var debitTotal=0,creditTotal=0;
var debitAmt1=0,creditAmt1=0;
var obj;
var table= document.getElementById("gridReceiptReversalCheque");
for(var i=1;i<table.rows.length-1;i++)
	{		
		 debitAmt1=getControlInBranch(table.rows[i],"debitAmountR");
		 creditAmt1=getControlInBranch(table.rows[i],"creditAmountR");
		if(creditAmt1.value>debitAmt1.value){
		getControlInBranch(table.rows[i],"creditAmount").value=creditAmt1.value;
		getControlInBranch(table.rows[i],"debitAmount").value="0.0";
		creditTotal=creditTotal+parseFloat((creditAmt1).value,2);
		getControlInBranch(table.rows[i],"userAmount").value=creditAmt1.value;
		}else{
		getControlInBranch(table.rows[i],"creditAmount").value="0.0";
		getControlInBranch(table.rows[i],"debitAmount").value=debitAmt1.value;
		debitTotal=debitTotal+parseFloat((debitAmt1).value,2);
		getControlInBranch(table.rows[i],"userAmount").value=debitAmt1.value;
		}
		

	   netAmt = Math.abs(creditTotal-debitTotal);
	   document.getElementById("userReceiptNet").value=netAmt.toFixed(2);	
}
}

function getAccountNumbers()
{
			document.DishonoredChequeForm.action = "../brs/DishonoredChequeEntries.do?submitType=getAccountNumbers";
			if(document.DishonoredChequeForm.bankId.options[document.DishonoredChequeForm.bankId.selectedIndex].value != 0)
			{
				
				document.DishonoredChequeForm.submit();
			}
}
	function checkBalance()
	{	 	  
	   var bal=document.getElementById("bankBookBal").value;
	   var chqamt=parseFloat(document.getElementById("debitAmount").value);
	   var bankChargeamt=parseFloat(document.getElementById("bankTotalAmt").value);
		bal=isNaN(bal)?0:bal;
		var finalBal=bal-(chqamt+bankChargeamt);
		if(finalBal<0)
		{
		  if(!confirm("WARNING:  Insufficient Balance in Bank-Account"))
		    return false;
		}
		return true;
	}
		
	function getDetails()
	{
		document.DishonoredChequeForm.action = "../brs/DishonoredChequeEntries.do?submitType=getDetails";
		
		/*
			if(document.DishonoredChequeForm.bankId.options[document.DishonoredChequeForm.bankId.selectedIndex].value != 0)
			{			
				if(document.DishonoredChequeForm.accId.options[document.DishonoredChequeForm.accId.selectedIndex].value != 0)
				{

				//alert(document.DishonoredChequeForm.paramTxnDate.value);
					var txdate=document.DishonoredChequeForm.paramTxnDate.value;
					if(txdate != null && txdate != "" && txdate.length>0)
					{	
						var dat=validateDate(document.DishonoredChequeForm.paramTxnDate.value);	
						if (!dat){
						alert('Invalid date format : Enter Date as dd/mm/yyyy');
						document.DishonoredChequeForm.paramTxnDate.focus();
						return;
						}
					}
					else
					{alert("Enter Transaction Date !!!");
					return;
					}

				document.forms("DishonoredChequeForm").submit();

				}
				else
				alert("Select Account No !!!");
			}
			else
			alert("Select Bank And Branch !!!");
		*/
			
			/*
		var txdate=document.DishonoredChequeForm.paramTxnDate.value;
			if(txdate != null && txdate != "" && txdate.length>0)
			{	
				var dat=validateDate(document.DishonoredChequeForm.paramTxnDate.value);	
				if (!dat){
				alert('Invalid date format : Enter Date as dd/mm/yyyy');
				document.DishonoredChequeForm.paramTxnDate.focus();
				return;
				}
			}
			else
			{alert("Enter Transaction Date !!!");
			document.DishonoredChequeForm.paramTxnDate.focus();
			return;
			}
			*/
			
		document.DishonoredChequeForm.submit();
		
	}
		
		
	function onClickCancel()
	{
		window.location="${pageContext.request.contextPath}/brs/DishonoredChequeEntries.do?submitType=toLoad";
				
	}
	
function markForTxn(obj)
{
			var rowobj=getRow(obj);
			var table= document.getElementById("gridDishonoredCheque");
			var chkBox,chkBoxTemp;
										
	for(var i=1;i<table.rows.length;i++)
	{
						
		var voucherNo1=getControlInBranch(table.rows[rowobj.rowIndex],"voucherNo");
		var voucherNo2=getControlInBranch(table.rows[i],"voucherNo");
		
		var isPayCheque1=getControlInBranch(table.rows[rowobj.rowIndex],"isPayCheque");
		var isPayCheque2=getControlInBranch(table.rows[i],"isPayCheque");
		
		chkBox=getControlInBranch(table.rows[rowobj.rowIndex],"postTxn");
		
		chkBoxTemp=getControlInBranch(table.rows[i],"postTxn");
				
		if(chkBox.checked && chkBoxTemp.checked)
		{
		  //chkBox.checked=true;
		  
		  voucherNo1=voucherNo1.innerHTML;
		  voucherNo2=voucherNo2.innerHTML;
		  
		  isPayCheque1=isPayCheque1.innerHTML;
		  isPayCheque2=isPayCheque2.innerHTML;

			if(isPayCheque1==isPayCheque2 && voucherNo1==voucherNo2)
			{
				chkBox.checked=true;
				
			} // check voucherNo equal or not
			else 
			{
			alert("Please Choose only cheques from the same Voucher Type and Voucher No.");
			return false;
			}

		}
		
} //for
return true;
	
}

/*
function selUnSelAll()
{			
			
			var table=document.getElementById('gridDishonoredCheque');
			var chkBox,hchkBox;
			hchkBox=getControlInBranch(table.rows[0],"selectChq");
	for(var i=1;i<table.rows.length;i++)
	{
		var voucherNo1=getControlInBranch(table.rows[i],"voucherNo");
		var voucherNo2=getControlInBranch(table.rows[i+1],"voucherNo");
			chkBox=getControlInBranch(table.rows[i],"postTxn");
			if(hchkBox.checked)
			{
			 // chkBox.checked=true;
			  
			  voucherNo1=voucherNo1.innerHTML;
			  voucherNo2=voucherNo2.innerHTML;
				if(voucherNo1==voucherNo2)
     				{
     					chkBox.checked=true;
     													 
			 	} // check voucherNo equal or not
			 	else
			 	{
			 	alert("Please Choose only cheques from the same Receipt Voucher No.");
			 	chkBox.checked=false;
			 	return;
			 	}
				
			}
			
			else
			{
			 chkBox.checked=false;
			 }
	} //for
	
}
*/

function calcChqAmount()
{
var chqAmount=0;
var vType,chequeType;
//var transdate=document.getElementById('paramTxnDate').value;
//document.getElementById('voucherTxnDate').value=transdate;

var table=document.getElementById('gridDishonoredCheque');

	for(var i=1;i<table.rows.length;i++)
	{
			
		if(getControlInBranch(table.rows[i],'postTxn').checked == true)
		{
		vType=getControlInBranch(table.rows[i],'voucherType').innerHTML;
		chequeType=getControlInBranch(table.rows[i],'isPayCheque').innerHTML;
		
		chqAmount=chqAmount+parseFloat(getControlInBranch(table.rows[i],'chqAmt').innerHTML);
		//document.getElementById('totalChqAmount').innerHTML=chqAmount;
		//document.getElementById('chqTotalAmt').value=chqAmount;
		} 
		
	} // for
	
	//alert(vType);
	//if(vType=='Receipt')
	
	// Here if chequeType is "0" means Receipt and "1" means Payment
	//alert("vType"+vType);
	if(vType=='Receipt' || vType=='Journal Voucher')
	{
		loadChqAccCodes();
		document.getElementById('voucherTypeParam').value='Receipt';
		
		//document.getElementById('todayDateCh').value=transdate;
		//document.getElementById('todayChDt').innerHTML=transdate;
		//document.getElementById('chqTotalAmt').value=chqAmount;
		
		document.getElementById('passedAmount').value=chqAmount;
		document.getElementById('revAmt').value=chqAmount;
		
		document.getElementById('ReversalAmountGrid').style.display ='block'		
		document.getElementById('ReasonReceiptReversal').style.display ='block'		
		document.getElementById('gridReceiptReversalCheque').style.display ='block'
		//document.getElementById('hideAddDeleteRowForR').style.display ='block'
		
		
	//	document.getElementById('glcodeChList').focus();
		if(vType=='Receipt' && voucherNumGenTypeReceipt!='Auto')
		{
		
		document.getElementById('reversalVoucherNumberText').style.display='block';
		document.getElementById('reversalVoucherNumber').style.display='block';
		}
		else if(vType=='Journal Voucher' && voucherNumGenTypeJournal!='Auto')
		{
		document.getElementById('reversalVoucherNumberText').style.display='block';
		document.getElementById('reversalVoucherNumber').style.display='block';
		}
	}
	//else if(vType!='Receipt')
	else
	{
		loadDropDownCodes();
		document.getElementById('voucherTypeParam').value='Payment';
		//document.getElementById('todayDateChP').value=transdate;
		//document.getElementById('todayChDtP').innerHTML=transdate;
		//document.getElementById('creditAmount').value=chqAmount;
		
		document.getElementById('passedAmount').value=chqAmount;
		document.getElementById('revAmt').value=chqAmount;
		
		document.getElementById('ReversalAmountGrid').style.display ='block'		
		document.getElementById('ReasonPaymentReversal').style.display ='block'		
		document.getElementById('gridPaymentReversalCheque').style.display ='block'
		//document.getElementById('hideAddDeleteRowForP').style.display ='block'
		document.getElementById('chqReasonP').focus();
		if(vType=='Payment' && voucherNumGenTypePayment!='Auto')
		{
		
		document.getElementById('reversalVoucherNumberText').style.display='block';
		document.getElementById('reversalVoucherNumber').style.display='block';
		}
		if(vType=='Contra' && voucherNumGenTypePayment!='Auto')
		{ 
		                                      
		document.getElementById('reversalVoucherNumberText').style.display='block';
		    
		document.getElementById('reversalVoucherNumber').style.display='block';
		                   
		}
	}
}

function calcBankAmount()
{
var bankAmount=0;
var transdate=document.getElementById('paramTxnDate').value;
var table=document.getElementById('gridDishonoredCheque');
	for(var i=1;i<table.rows.length;i++)
	{
		if(!getControlInBranch(table.rows[i],'postTxn').checked)
		{
		getControlInBranch(table.rows[i],'bankChargeAmt').value="";
		getControlInBranch(table.rows[i],'refNo').value="";
		}	
		if(getControlInBranch(table.rows[i],'postTxn').checked == true && getControlInBranch(table.rows[i],'bankChargeAmt').value!="")
		{
		//totalBkAmount---for display
		
		//bankAmount=bankAmount+parseFloat(getControlInBranch(table.rows[i],'bankChargeAmt').innerHTML);
		
		bankAmount=bankAmount+parseFloat(getControlInBranch(table.rows[i],'bankChargeAmt').value);
		//alert(bankAmount);
		
		//document.getElementById('totalBkAmount').innerHTML=bankAmount;
		
		document.getElementById('bankTotalAmt').value=bankAmount;
		//document.getElementById('gridBankCharges').style.display ='block'
		//document.getElementById('submitGridForBankCharges').style.display ='block'
	
		} 
		
		
	} // for
	
		
	document.getElementById('todayDateBk').value=transdate;
	document.getElementById('todayBankDt').innerHTML=transdate;
	document.getElementById('bankTotalAmt').value=bankAmount;
	
	//alert("bankAmount value-"+bankAmount);
	if(bankAmount!=0 && bankAmount!="")
	{
		document.getElementById('gridBankCharges').style.display ='block';
		document.getElementById('submitGridForBankCharges').style.display ='block';
		
		if(voucherNumGenTypePayment!='Auto')
		{ 
		document.getElementById('bankChargesVoucherNumberText').style.display ='block';
		document.getElementById('bankChargesVoucherNumber').style.display ='block';
		}
	document.getElementById('bankChReason').style.display ='block';
	document.getElementById('bankChReasonText').style.display ='block';		
		
	}
	
}
function checkValidDate()
{			
	var transdate=document.getElementById('paramTxnDate').value;
	document.getElementById('voucherTxnDate').value=transdate;
	
	var table=document.getElementById('gridDishonoredCheque');
	var chkBox;
	var booleanA=true;
	var j=0;
	var cqdateFirst;
	var cqdateNext;
	for(var i=1;i<table.rows.length;i++)
		{
			chkBox=getControlInBranch(table.rows[i],"postTxn");
			
			if(chkBox.checked)
			{
				if(booleanA)
				{
				booleanA=false;
				cqdateFirst=getControlInBranch(table.rows[i],"passChqDate").value;
				}
				j=j+1;
			}
		}
		
		//alert("j value is "+j);
		//alert("first selected row chq date is "+cqdateFirst);
		
	if(j>1)
	{
			
		for(var i=1;i<table.rows.length;i++)
		{
			chkBox=getControlInBranch(table.rows[i],"postTxn");
			if(chkBox.checked)
			{
				cqdateNext=getControlInBranch(table.rows[i],"passChqDate").value;
			  	//alert("Next chq date is "+cqdateNext);
				 if(compareDate(formatDate6(cqdateFirst),formatDate6(cqdateNext)) == 1)
			 	{
			 	cqdateFirst=cqdateNext;
			 	}
			}
		} //for
	} // main if
	
	
	//alert("Final chq date is "+cqdateFirst);
	
	// for receipt reversal
	var recRevDt=document.getElementById('paramTxnDate').value;
	
	if(compareDate(formatDate6(cqdateFirst),formatDate6(recRevDt)) == -1)
	{
	alert("Transaction date cannot be prior to the selected cancel Chque date");
	return false;
	}
	else
	return true;
}

function ButtonPress(arg)
{

	alert(arg);
	var vType=document.getElementById("voucherTypeParam").value;
	//alert(" cheque type")
		if(arg == "process")
		{
			
			
			if(!checkValidAmt())
			return;
			
			var grid = document.getElementById('gridDishonoredCheque');
			var cnt=grid.rows.length;
			var i=0;
			for(var cnt=1; cnt<grid.rows.length; cnt++)
			{
				chkBox = getControlInBranch(grid.rows[cnt], 'postTxn');
				if(chkBox.checked){

				if(getControlInBranch(grid.rows[cnt],'refNo').value!="")
				{
				getControlInBranch(grid.rows[cnt],'refNo').disabled=false;
				//document.getElementById("refNo").value=getControlInBranch(grid.rows[cnt],'refNo').value;

				document.getElementById("passRefNo").value=getControlInBranch(grid.rows[cnt],'refNo').value;

				//alert(document.getElementById("passRefNo").value);
				}
				else
				{alert("Please Enter Reference No. for selected row !!!");
				return;
				}

				i++;}
			} // for
			
			if(i>0)
			{	var answer=confirm("Are you sure you want to continue?")
				if(!answer)
				return false;
				else
				{
					for(var cnt=1; cnt<grid.rows.length; cnt++)
					{
					chkBox = getControlInBranch(grid.rows[cnt], 'postTxn');

					bankChargeAmt=getControlInBranch(grid.rows[cnt], 'bankChargeAmt');
					chkRefNo=getControlInBranch(grid.rows[cnt], 'refNo');

						if(chkBox.checked){
						chkBox.disabled=true;
						bankChargeAmt.disabled=true;
						chkRefNo.disabled=true;
						i++;}
						else
						{//getControlInBranch(grid.rows[0], 'selectChq').disabled=true;
						chkBox.disabled=true;
						bankChargeAmt.disabled=true;
						chkRefNo.disabled=true;
						}
					} // for
				}
			}// if
			else
			{
			alert("At least one cheque needs to be selected!!!");
			return;
			}
		markForPassVoucher();
		//loadChqAccCodes();
		//Test();

		//fillTodayDate();                     
		calcChqAmount();
		calcBankAmount();
				
		document.getElementById('processGrid').style.display ='none';
		document.getElementById('TxnDateRow').style.display ='block'
		document.getElementById('submitGrid').style.display ='block';
		document.getElementById('submitGridForReversal').style.display ='block';
		} // process
		
		document.getElementById("buttonValue").value=arg;
		//alert(document.getElementById("buttonValue").value);
		
		if(arg != "view" && arg != "process")
		{
			var txdate=document.DishonoredChequeForm.paramTxnDate.value;
			if(txdate != null && txdate != "" && txdate.length>0)
			{	
				var dat=validateDate(document.DishonoredChequeForm.paramTxnDate.value);	
				if (!dat){
				alert('Invalid date format : Enter Date as dd/mm/yyyy');
				document.DishonoredChequeForm.paramTxnDate.focus();
				return;
				}
				}
				else
				{alert("Enter Transaction Date !!!");
				document.DishonoredChequeForm.paramTxnDate.focus();
				return;
			}
			
			if(document.getElementById('reversalVoucherNumber').style.display=='block')
			{
			if(document.getElementById('reversalVoucherNumber').value=='')
			{
			alert("Please provide Reversal Voucher Number");
			return false;
			}
			}
			if(document.getElementById('bankChargesVoucherNumber').style.display=='block')
			{
			if(document.getElementById('bankChargesVoucherNumber').value=='')
			{
			alert("Please provide   Voucher Number for Bank Charges");
			return false;
			}
			}	
			
			if(!checkValidDate())
			return;
			
			//alert(document.getElementById("voucherTypeParam").value);
			var vType=document.getElementById("voucherTypeParam").value;
			if(vType=='Receipt' || vType=='Journal Voucher')
			{
				//alert("Inside Receipt Reversal");
				//if(!checkDuplicatesReceiptRev("gridReceiptReversalCheque","glcodeR","glcodeChList")) return;
				//no duplicate check required
				//alert("1");
			//	if(isRowsEmptyReceiptRev())return;  ENABLE THESE TWO WHEN ADDING ADDNEW ROW 
			//	alert("2");
			//	if(!checkValidAmountReceiptRev("gridReceiptReversalCheque","userAmount","debitAmount","creditAmount")) return;
				//alert("3");
				if(!checkTotalNew("gridReceiptReversalCheque","debitAmount","creditAmount","userAmount")) return;
			}
			if(vType=='Payment')
			{
				//alert("Inside Payment Reversal");
				if(!checkDuplicatesPaymentRev("gridPaymentReversalCheque","glcodeChIdP")) return;
				if(isRowsEmptyPaymentRev())return;
				if(!checkValidAmountPaymentRev("gridPaymentReversalCheque","creditAmount")) return;
				if(!checkTotal("gridPaymentReversalCheque","creditAmount","debitAmount")) return;
						
			}
			//alert("voucherTypeParam value- "+document.getElementById('voucherTypeParam').value);
			//alert("voucherTxnDate value-"+document.getElementById('voucherTxnDate').value);
			document.DishonoredChequeForm.action = "../brs/DishonoredChequeEntries.do?submitType=createDishonourCheque";		
			document.DishonoredChequeForm.submit();
		} // savenew

		//forwardItem
		if(arg == "forwardReceiptItem" &&  vType=='Receipt')
		{
			var txdate=document.DishonoredChequeForm.paramTxnDate.value;
			if(txdate != null && txdate != "" && txdate.length>0)
			{	
				var dat=validateDate(document.DishonoredChequeForm.paramTxnDate.value);	
				if (!dat){
				alert('Invalid date format : Enter Date as dd/mm/yyyy');
				document.DishonoredChequeForm.paramTxnDate.focus();
				return;
				}
				}
				else
				{alert("Enter Transaction Date !!!");
				document.DishonoredChequeForm.paramTxnDate.focus();
				return;
			}
			
			if(document.getElementById('reversalVoucherNumber').style.display=='block')
			{
			if(document.getElementById('reversalVoucherNumber').value=='')
			{
			alert("Please provide Reversal Voucher Number");
			return false;
			}
			}
			if(document.getElementById('bankChargesVoucherNumber').style.display=='block')
			{
			if(document.getElementById('bankChargesVoucherNumber').value=='')
			{
			alert("Please provide   Voucher Number for Bank Charges");
			return false;
			}
			}	
			
			if(!checkValidDate())
			return;
			
			//alert(document.getElementById("voucherTypeParam").value);
			var vType=document.getElementById("voucherTypeParam").value;
			if(vType=='Receipt' || vType=='Journal Voucher')
			{
				//alert("Inside Receipt Reversal");
				//if(!checkDuplicatesReceiptRev("gridReceiptReversalCheque","glcodeR","glcodeChList")) return;
				//no duplicate check required
				//alert("1");
			//	if(isRowsEmptyReceiptRev())return;  ENABLE THESE TWO WHEN ADDING ADDNEW ROW 
			//	alert("2");
			//	if(!checkValidAmountReceiptRev("gridReceiptReversalCheque","userAmount","debitAmount","creditAmount")) return;
				//alert("3");
				if(!checkTotalNew("gridReceiptReversalCheque","debitAmount","creditAmount","userAmount")) return;
			}
			
			//alert("voucherTypeParam value- "+document.getElementById('voucherTypeParam').value);
			//alert("voucherTxnDate value-"+document.getElementById('voucherTxnDate').value);
			document.DishonoredChequeForm.action = "../brs/DishonoredChequeEntries.do?submitType=createDishonourCheque";		
			document.DishonoredChequeForm.submit();
		} // savenew
		                
		
		if(arg == "view")
		{
			//var txdate=document.DishonoredChequeForm.paramTxnDate.value;
			var valFrom=document.DishonoredChequeForm.bankFromDate.value;
			//var valTo=document.DishonoredChequeForm.bankToDate.value;
			if(document.getElementById("instrumentMode").value=="0")
			{
				alert("Please Select Instrument Mode First!");
				document.getElementById("instrumentMode").focus();
				return false;
			}
			
			if(document.getElementById("chequeNo").value=="")
			{
				alert("Please Enter Cheque/DD Number First !!!");
				document.getElementById("chequeNo").focus();
				return false;
			}
			if(valFrom == "" && valFrom.length==0)
			{	
				alert("Please Enter Cheque/DD Date !!!");
				document.DishonoredChequeForm.bankFromDate.focus();
				return false;
				
			}
			if(valFrom != null && valFrom != "" && valFrom.length>0)
			{	
				var dat=validateDate(document.DishonoredChequeForm.bankFromDate.value);
				if (!dat){
				alert('Invalid date format : Enter Date as dd/mm/yyyy');
				document.DishonoredChequeForm.bankFromDate.focus();
				return;
				}
			}
			getDetails();
		}
		
}

function compareDt()
{
	if(compareDate(formatDate6(document.DishonoredChequeForm.bankFromDate.value),formatDate6(document.DishonoredChequeForm.bankToDate.value)) == -1 )
	{
	alert('Cheque Date To cannot be less than Cheque Date From');
	document.DishonoredChequeForm.bankFromDate.focus();
	return false;
	}
	return true;
}
function markForPassVoucher()
{
	var table= document.getElementById("gridDishonoredCheque");
	for(var i=1;i<table.rows.length;i++)
	{
		if(getControlInBranch(table.rows[i],'postTxn').checked == true)
		{
			getControlInBranch(table.rows[i],'passVoucher').value = "yes";
						
			var vHeaderId=getControlInBranch(table.rows[i],'voucherId').innerHTML;
			var pHeaderId=getControlInBranch(table.rows[i],'payinVHId').innerHTML;
			var fundId=getControlInBranch(table.rows[i],'fundId').innerHTML;
			var fundSrcId=getControlInBranch(table.rows[i],'fundSourceId').innerHTML;
			//var fieldId=getControlInBranch(table.rows[i],'fieldId').innerHTML;
			var accIdParam=getControlInBranch(table.rows[i],'accIdParam').innerHTML;
			
			document.getElementById("passVoucherId").value=vHeaderId;
			document.getElementById("passPayinVHId").value=pHeaderId;
			document.getElementById("passFundId").value=fundId;
			document.getElementById("passFundSrcId").value=fundSrcId;
			document.getElementById("passAccId").value=accIdParam;
			//document.getElementById("passFieldId").value=fieldId;
			
			//alert(vHeaderId);
			//alert(pHeaderId);
			//alert(fundId);
			//alert(fundSrcId);
			//alert(fieldId);
		}
		else
		{
			getControlInBranch(table.rows[i],'passVoucher').value = "no";
			//alert("no");
		}
	} // for
}

function Test()
{
//alert(document.getElementById("passVoucherId").value);
//alert(document.getElementById("passPayinVHId").value);
//alert(document.getElementById("passFundId").value);
//alert(document.getElementById("passFundSrcId").value);
//alert(document.getElementById("passFieldId").value);
//alert(document.getElementById("passAccId").value);

}

function clearData()
	{
		//document.getElementById('processGrid').style.display ='none';
		//alert(document.getElementById("gridDishonoredCheque").length);
			
		/*var bankBal="<%=session.getAttribute("accountBalance")%>";
		if(document.DishonoredChequeForm.accId.options[document.DishonoredChequeForm.accId.selectedIndex].value != 0)
		{
				if(bankBal!='null')
				document.getElementById('bankBookBal').value=bankBal;
				document.getElementById('hideBB1').style.display ="block";
				//document.getElementById('hideBB2').style.display ="block";
		}*/
		
		var target="<%=(request.getAttribute("alertMessage"))%>";
		if(target!="null")
		{
			alert("<%=request.getAttribute("alertMessage")%>");
			<%	
			if(request.getAttribute("alertMessage") != null)
			{
			session.removeAttribute("accNumberList2");
			request.setAttribute("alertMessage",null);
			}
			%> 
			document.DishonoredChequeForm.bankId.options.selectedIndex=0;
			document.DishonoredChequeForm.accId.options.selectedIndex=0;
		}
	
	
		<%
		ArrayList al1=(ArrayList)request.getAttribute("dishonCheqDetails");
		if(al1!=null && al1.size()==0)
		{
		%>
			document.getElementById('searchBe').style.display ='block';
			document.getElementById('beDetail').style.display ='block';
			document.getElementById('processGrid').style.display ='none';
						
		<%
		}
		else
		{
		%>
		//document.DishonoredChequeForm.bankId.focus();
		document.DishonoredChequeForm.chequeNo.focus();
		<%
		}
		if(al1!=null && al1.size()>0)
		{
		%>	
		
			document.getElementById('searchBe').style.display ='none';
			document.getElementById('beDetail').style.display ='block';
			
			<%
		}
		%>
	}
	
/*
function fillTodayDate()
{
		//alert("<%=session.getAttribute("todayDate")%>");
	
	var transdate=document.getElementById('paramTxnDate').value;
	document.getElementById('todayDateCh').value=transdate;
	document.getElementById('todayDateBk').value=transdate;
	
	document.getElementById('todayChDt').innerHTML=transdate;
	document.getElementById('todayBankDt').innerHTML=transdate;

}
*/

function clearValues(neibrObj,neibrObj1,neibrObj2,neibrObj3,neibrObj4,neibrObj5,neibrObj6,neibrObj7){
	if(neibrObj != null){
		neibrObj.value ="";
	}
	if(neibrObj2 != null){
		neibrObj2.value="";
	}
	if(neibrObj3 != null){
		neibrObj3.value="";
	}
	if(neibrObj4 != null){
		neibrObj4.value="";
	}
	if(neibrObj5 != null){
		neibrObj5.value="";
	}
	if(neibrObj6 != null){
		neibrObj6.value="";
	}
	if(neibrObj7 != null){
		neibrObj7.value="";
	}
}  

function splitChqGlCode(obj,neibrObjName){
var table;
var vType=document.getElementById("voucherTypeParam").value; 	
	
	if(vType=='Journal Voucher' || vType=='Receipt'){
		 table= document.getElementById("gridReceiptReversalCheque");
	}else{
		 table= document.getElementById("gridPaymentReversalCheque");
	}
	var glcodeVal=obj.value; 
	
	var temp = obj.value;
	 //temp = temp.split('-');
	 temp = temp.split('~');
	 
	 //alert(temp.length);
	 if(temp.length==1)
	 {
		var currRow=getRow(obj);
		neibrObj=getControlInBranch(currRow,neibrObjName);
		//	neibrObj.value=temp[1].replace("`","d")
		
		//alert('table'+table);	
		var neibrObj1=getControlInBranch(table.rows[currRow.rowIndex],"glcodeChIdP");
		var neibrObj2=getControlInBranch(table.rows[currRow.rowIndex],"glcodeChId");
		var neibrObj3=getControlInBranch(table.rows[currRow.rowIndex],"glcodeR");
		var neibrObj4=getControlInBranch(table.rows[currRow.rowIndex],"glAmt");
		var neibrObj5=getControlInBranch(table.rows[currRow.rowIndex],"debitAmount");
		var neibrObj6=getControlInBranch(table.rows[currRow.rowIndex],"creditAmount");
		var neibrObj7=getControlInBranch(table.rows[currRow.rowIndex],"functionChId");
		clearValues(neibrObj,neibrObj1,neibrObj2,neibrObj3,neibrObj4,neibrObj5,neibrObj6,neibrObj7);
	 }
	 else{
	//obj.value=temp[0];
	var currRow=getRow(obj);
	neibrObj=getControlInBranch(currRow,neibrObjName);
	neibrObj.value=temp[1].replace("`","d")
	

		
	var neibrObj1=getControlInBranch(table.rows[currRow.rowIndex],"glcodeChIdP");
	var neibrObj2=getControlInBranch(table.rows[currRow.rowIndex],"glcodeChId");
	var neibrObj3=getControlInBranch(table.rows[currRow.rowIndex],"glcodeR");
	var neibrObj4=getControlInBranch(table.rows[currRow.rowIndex],"glAmt");
	var neibrObj5=getControlInBranch(table.rows[currRow.rowIndex],"debitAmount");
	var neibrObj6=getControlInBranch(table.rows[currRow.rowIndex],"creditAmount");
	var neibrObj7=getControlInBranch(table.rows[currRow.rowIndex],"functionChId");
	}
	if(glcodeVal==0){
		clearValues(neibrObj,neibrObj1,neibrObj2,neibrObj3,neibrObj4,neibrObj5,neibrObj6,neibrObj7);
	}
		
	if(temp[0]==null) return;
	else 
	{
		
		if(neibrObj1 != null){
			neibrObj1.value=glcodeVal;
		}
		if(neibrObj3 != null){
			neibrObj3.value = temp[0];
		}
	}

	if(temp[1]==null) return; else 	neibrObj.value = temp[1];
	if(temp[2]==null) return; 
	else{
	
	
	
	
		if(neibrObj2!=null)
			neibrObj2.value = temp[0];
		if(neibrObj3!=null)
			neibrObj3.value = temp[0];
		if(neibrObj4!=null)
			neibrObj4.value = temp[2];
			
		if(neibrObj5!=null){
			neibrObj5.value = temp[2];
		}
		if(neibrObj6!=null)
			{
			neibrObj6.value = temp[3];	
			}
	
	}
	if(temp[3]==null) return; 
	else
	{
	neibrObj4.value = temp[2].replace("`","");
	neibrObj5.value = temp[2].replace("`","");
	neibrObj6.value = temp[3].replace("`","");
	}
	
}


function splitBankCode()
{
	//alert(bankGlcodearray);

var arr=new Array();
arr=bankGlcodearray[0];
 arr=arr.split("`--`");
 
 document.getElementById('glcodeForBank').value=arr[0]; // glcode
 document.getElementById('accCodedescBk').value=arr[1];  // desc
 document.getElementById('glcodeBkId').value=arr[2]; // glcodeId
 
	//alert(arr[0]);
	//alert(arr[1]);
	//alert(arr[2]);

}
function loadChqAccCodes()
{
vouchHeaderId=document.getElementById("passVoucherId").value;
//loadDropDownChqCodes(vouchHeaderId);
}


function loadDropDownChqCodes(vouchHeaderId)
	 	 {
	    var type='getChqueGlcode';
	 	var url = "../commons/Process.jsp?type=" +type+ "&vouchHeaderId="+vouchHeaderId+" ";
	 	var req2 = initiateRequest();
	 	req2.onreadystatechange = function()
		{
		 	 if (req2.readyState == 4)
		 	 {
			   if (req2.status == 200)
			   {
				var codes2=req2.responseText;
				var a = codes2.split("^");
				var codes = a[0];
				chqGlcodearray=codes.split("+");
								
				//alert(chqGlcodearray);
								
				for(var i=0;i<chqGlcodearray.length;i++)
				{
				//alert("chqGlcodearray[i]"+chqGlcodearray[i]);
				var glcodeValue="";
				glcodeValue=chqGlcodearray[i].replace("`","");
			//	document.getElementById('glcodeChList').options[i+1]=new Option(glcodeValue,glcodeValue);
								
				}
				
			  }
		  	}
	 	};
	 		req2.open("GET", url, true);
	 		req2.send(null);
}
function loadDropDownBankCodes()
	 {

	 	var type='getBankGlcode';
		var url = "../commons/Process.jsp?type=" +type+ " ";
		var req2 = initiateRequest();
		req2.onreadystatechange = function()
		{
			  if (req2.readyState == 4)
			  {
				  if (req2.status == 200)
				  {
					var codes2=req2.responseText;
					var a = codes2.split("^");
					var codes = a[0];
					bankGlcodearray=codes.split("+");
					//alert("bankGlcodearray"+bankGlcodearray);
					splitBankCode();
                   		  }
			  }
		};
		req2.open("GET", url, true);
		req2.send(null);
	 }
	 
function getGlcodeDetail(obj)
{
	//alert(obj.value);
	if(obj.value !='')
	{
	glcodeParam=document.getElementById("chartOfAccounts_glCode").value;
	//alert("glcodeParam-"+glcodeParam);
	loadGlcodeDetail(glcodeParam);
	}
}	 
function loadGlcodeDetail(glcodeParam)
{
	var type='getGlcodeForPayReversal';
	var url = "../commons/Process.jsp?type=" +type+ "&glcodeParam="+glcodeParam+" ";
	var req2 = initiateRequest();
	req2.onreadystatechange = function()
	{
		  if (req2.readyState == 4)
		  {
			  if (req2.status == 200)
			  {
				var codes2=req2.responseText;
				var a = codes2.split("^");
				var codes = a[0];
				glcodearrayForPayRev=codes.split("+");
				//alert("glcodearrayForPayRev"+glcodearrayForPayRev);
				splitGlCodeDetail();
			  }
		  }
	};
	req2.open("GET", url, true);
	req2.send(null);
}
function splitGlCodeDetail()
{
 var arr=new Array();
 arr=glcodearrayForPayRev[0];
  arr=arr.split("`--`");
  
	  //alert(arr[0]);
	 //alert(arr[1]);
	 //alert(arr[2]);
 
	  if(arr[1]!=undefined)
	  { 
	  document.getElementById('chartOfAccounts_glCode').value=arr[0]; // glcode
	  document.getElementById('accCodedescChP').value=arr[1];  // desc
	  document.getElementById('glcodeChIdP').value=arr[2]; // glcodeId
	  }
	  else
	  {
	  alert("Invalid Account Code!!!!");
	  document.getElementById('chartOfAccounts_glCode').value=""; // glcode
	  document.getElementById('accCodedescChP').value="";  // desc
	  document.getElementById('glcodeChIdP').value=""; // glcodeId
	  return false;
	  }
	  
	  return true;
}
	 
function checkValidAmt()
{	
	var table= document.getElementById("gridDishonoredCheque");
	//alert(table.rows.length);
	for(var i=1;i<=table.rows.length;i++)
	{
		if(isNaN(getControlInBranch(table.rows[i],"bankChargeAmt").value))
		{
		alert("Bank Charge Amount should be in Number");
		return false;
		}
		else
		return true;
	}
}
 function imposeMaxLength(Object, MaxLen)
{
	if(Object.value.length > MaxLen)
	alert("Reason should not exceed more than 100 characters !!!");
	return (Object.value.length <= MaxLen);
}
function getDrillDown(obj){
		var row=getRow(obj);
		var table=document.getElementById('gridDishonoredCheque');
		var voucherId = getControlInBranch(table.rows[row.rowIndex],"voucherId");
		var voucherNumber = getControlInBranch(table.rows[row.rowIndex],"voucherNo");
		if(voucherNumber.innerHTML != 'MULTIPLE'){
			url = "${pageContext.request.contextPath}/voucher/preApprovedVoucher!loadvoucherview.action?vhid="+voucherId.innerHTML;
			window.open(url,"","height=650,width=900,scrollbars=yes,left=20,top=20,status=yes");
		}
	}
	
function addRowForReceipt(tablename)
{
		
		var tbl = document.getElementById(tablename);
		var tbody=tbl.tBodies[0];
		var lastRow = tbl.rows.length;
		var rowObj = tbl.rows[lastRow-1].cloneNode(true);
		tbody.appendChild(rowObj);	
		for(var i=0;i<tbl.rows[lastRow-1].cells.length;i++)
		{
		tbl.rows[lastRow-1].cells[i].value="";
		}
		
		//alert(lastRow-1);
		
		document.forms[0].glcodeChId[lastRow-1].value="";
		document.forms[0].glcodeR[lastRow-1].value="";
		document.forms[0].accCodedescCh[lastRow-1].value="";
		document.forms[0].glAmt[lastRow-1].value="";
		document.forms[0].debitAmount[lastRow-1].value="";
		document.forms[0].creditAmount[lastRow-1].value="";
		
		document.forms[0].glcodeChList[lastRow-1].focus();
		//document.getElementById("glcodeChList").focus();
}
function deleteRowForReceipt()
{

	var tbl = document.getElementById('gridReceiptReversalCheque');
	var lastRow = (tbl.rows.length)-1;

	if(lastRow ==1)
	{
	 alert("This row can not be deleted");
	return false;
 	}
	else
	{
	tbl.deleteRow(lastRow);
	return true;
	}
	
	
}
	
function addRowForPayment(tablename)
{
	
	var tbl = document.getElementById(tablename);
	var tbody=tbl.tBodies[0];
	var lastRow = tbl.rows.length;
	var rowObj = tbl.rows[lastRow-1].cloneNode(true);

	tbody.appendChild(rowObj);	
	for(var i=0;i<tbl.rows[lastRow-1].cells.length;i++)
	{
	tbl.rows[lastRow-1].cells[i].value="";
	}
	
	//document.forms[0].glcodeChIdP[lastRow-1].value="";
	//document.forms[0].accCodedescChP[lastRow-1].value="";
	//document.forms[0].creditAmount[lastRow-1].value="";
	
}
function deleteRowForPayment()
{

	var tbl = document.getElementById('gridPaymentReversalCheque');
	var lastRow = (tbl.rows.length)-1;

	if(lastRow ==1)
	{
	 alert("This row can not be deleted");
	return false;
 	}
	else
	{
	tbl.deleteRow(lastRow);
	return true;
	}
	
	
}

function loadDropDownCodes()
{
	var type='getAllCoaCodes';
	var	url = "../commons/Process.jsp?type=" +type+ " ";
	var req2 = initiateRequest();
	req2.onreadystatechange = function()
	{
	  if (req2.readyState == 4)
	  {
		  if (req2.status == 200)
		  {
			var codes2=req2.responseText;
			var a = codes2.split("^");
			var codes = a[0];
			acccodeArray=codes.split("+");
			codeObj = new YAHOO.widget.DS_JSArray(acccodeArray);


		  }
	  }
	};
	req2.open("GET", url, true);
	req2.send(null);
}
function fillNeibrAfterSplit(obj,neibrObjName) 
 {  
			 var temp = obj.value; temp = temp.split("`-`");
			obj.value=temp[0];
 			PageManager.DescService.onblur(neibrObjName);
 			
			var currRow=getRow(obj);
			yuiflag[currRow.rowIndex] = undefined;
			neibrObj=getControlInBranch(currRow,neibrObjName);
			
			//alert(temp[0]);
			//alert(temp[1]);
			//alert(temp[2]);
			
			var table=document.getElementById('gridPaymentReversalCheque');
			//alert(getControlInBranch(table.rows[currRow.rowIndex],"glcodeChIdP"));
			var neibrObj2=getControlInBranch(table.rows[currRow.rowIndex],"glcodeChIdP");
				
			
			if(temp[1]==null) return; else 	neibrObj.value = temp[1];
			if(temp[2]==null) return; else 	neibrObj2.value = temp[2];
			
			//alert("glcode id"+getControlInBranch(table.rows[currRow.rowIndex],"glcodeChIdP").value);
			
			
 }

function autocompletecode(obj,name)
{
			
			// set position of dropdown 
			var src = obj;	
			var target = document.getElementById('codescontainer');	
			var posSrc=findPos(src); 
			target.style.left=posSrc[0];	
			target.style.top=posSrc[1]+22;
			target.style.width=450;	
				
			var currRow=getRow(obj);
			var coaCodeObj = getControlInBranch(currRow,'chartOfAccounts_glCode');
			//40  Down arrow, 38  Up arrow
			if(yuiflag[currRow.rowIndex] == undefined)
   			{
						if(event.keyCode != 40 )
						{
							if(event.keyCode != 38 )
							{
									var oAutoComp = new YAHOO.widget.AutoComplete(coaCodeObj,'codescontainer', codeObj);
									oAutoComp.queryDelay = 0;
									oAutoComp.prehighlightClassName = "yui-ac-prehighlight";
									oAutoComp.useShadow = true;
									oAutoComp.maxResultsDisplayed = 15;
   								oAutoComp.useIFrame = true;
							}
						}
			yuiflag[currRow.rowIndex] = 1;
   			}	
}

function showglEntryForReversal()
{
			
	var vType=document.getElementById("voucherTypeParam").value;
	
	var trObj;
	var tObj=document.getElementById("showEntries");
	var cr=0,dr=0;
	tObj.style.display="block";
	var tableObj=document.getElementById("entries");
			
	// if Payment Reversal start
	if(vType=='Payment' || vType=='Contra')
	{
			var gltable= document.getElementById("gridPaymentReversalCheque");
			if(document.forms[0].glcodeChIdP[0].value == 0){
				alert("Enter Account code Details First");
				tObj.style.display="none";
				return false;
			}
			var gltable= document.getElementById("gridPaymentReversalCheque");
			for(var i=tableObj.rows.length-1;i>=2;i--)
			{
				tableObj.deleteRow(i);
			}
			
			// debit entry should show first
			passedAmount = eval(document.getElementById('passedAmount').value);
			//alert("passedAmount"+passedAmount);
			passedAmount = isNaN(passedAmount) ? 0 : passedAmount;
			//alert("passedAmount"+passedAmount);
			if( passedAmount < 0)
			{
				alert(' amount can not be nagative');
				return false;
			}
			
			// For Getting corresponding Bank glcode and name
			var accountId=document.DishonoredChequeForm.passAccId.value;
			//alert("accountId"+accountId);
			var accountGlcode, accountGlname;
			if(accountId != '')
			{
				//alert(" Inside if ");		
				var url = "../commons/Process.jsp?accountId=" +accountId+ "&type=getBankAccountGlcode";
				var req2 = initiateRequest();  		
				req2.open("GET", url, false);
				req2.send(null);
				if (req2.status == 200) 
				{
				var result=req2.responseText.split("`-`");
				//alert("req2.responseText "+req2.responseText);
				//alert("result[0] "+result[0]);
				//alert("result[1] "+result[1]);     	            	   	           	         	           		              					              
					if(result!= null && result!= "")
					{							
						accountGlcode=result[0];
						accountGlname=result[1];
					}  
				} 
			}
			var temp=0;
			if(passedAmount>0)
			{
				var Obj=getControlInBranch(tableObj.rows[1],"display_Code");
				var newRow=getRow(Obj);
				//newRow=tableObj.tBodies[0].appendChild(newRow);
				objt1=getControlInBranch(newRow,'display_Code');
				objt2=getControlInBranch(newRow,'display_Head');
				objt3=getControlInBranch(newRow,'display_Debit');
				objt4=getControlInBranch(newRow,'display_Credit');
				if(accountGlcode=="") 
					accountGlcode=" -"; 
				if(accountGlname=="") 
					accountGlname=" -";
				objt1.innerHTML=accountGlcode;
				objt2.innerHTML=accountGlname;
				//objt3.innerHTML="0";
				//objt4.innerHTML=passedAmount;
				// for Receipt
				objt3.innerHTML=passedAmount;
				objt4.innerHTML="0";
				temp++;
			}
				
			for(var i=1;i<gltable.rows.length;i++)
			{
				code = null;
				if(getControlInBranch(gltable.rows[i],'glcodeChIdP')){
					var temp = getControlInBranch(gltable.rows[i],'glcodeChIdP').value;
	 				temp = temp.split('~');
					code= temp[0];
				}
				if(code != null)
				{
				name=getControlInBranch(gltable.rows[i],'accCodedescChP').value;
				// For Payment Reversal
				//debit=getControlInBranch(gltable.rows[i],'voucherDetail_debitAmount').value;
				debit=0;
				dr=dr+debit;
				credit=getControlInBranch(gltable.rows[i],'creditAmount').value;
				cr=cr+credit;
				
				
				//narration=getControlInBranch(gltable.rows[i],'voucherDetail_narration').value;
				narration="";
				var Obj=getControlInBranch(tableObj.rows[1],"display_Code");
				trObj=getRow(Obj);
				if(trObj){
				 if(temp==0){
								newRow=trObj;
								objt1=getControlInBranch(newRow,'display_Code');
				 				objt2=getControlInBranch(newRow,'display_Head');
				 				objt3=getControlInBranch(newRow,'display_Debit');
				 				objt4=getControlInBranch(newRow,'display_Credit');
				 				//objt5=getControlInBranch(newRow,'display_Narration');
				 				if(code=="") code=" -"; if(name=="") name=" -"; if(debit=="") debit=" 0"; if(credit=="") credit=" 0"; if(narration=="") narration=" -";
				 				objt1.innerHTML=code;
				 				objt2.innerHTML=name;
				 				objt3.innerHTML=debit;
				 				objt4.innerHTML=credit;
								//objt5.innerHTML=narration
				 }else {
						 var newRow=trObj.cloneNode(true);
						newRow=tableObj.tBodies[0].appendChild(newRow);
						objt1=getControlInBranch(newRow,'display_Code');
						objt2=getControlInBranch(newRow,'display_Head');
						objt3=getControlInBranch(newRow,'display_Debit');
						objt4=getControlInBranch(newRow,'display_Credit');
						//objt5=getControlInBranch(newRow,'display_Narration');
						if(code=="") code=" -"; if(name=="") name=" -"; if(debit=="") debit=" 0"; if(credit=="") credit=" 0"; if(narration=="") narration=" -";
						objt1.innerHTML=code;
						objt2.innerHTML=name;
						objt3.innerHTML=debit;
						objt4.innerHTML=credit;
						//objt5.innerHTML=narration;
				}
				}
			  }
			}
				
			 var dSum=0;
			 var cSum=0;
			 
			 
			for(var i=1;i<tableObj.rows.length;i++)
			{

			  var dObj=getControlInBranch(tableObj.rows[i],"display_Debit");
			  var cObj=getControlInBranch(tableObj.rows[i],"display_Credit");
			
			if(!isNaN(parseFloat(dObj.innerHTML)))
			 {
			   var sum=(parseFloat(dObj.innerHTML));
			   dSum=dSum+sum;
			 }
			 if(!isNaN(parseFloat(cObj.innerHTML)))
				 {
			   var sum=(parseFloat(cObj.innerHTML));
			   cSum=cSum+sum;
			 }
			 
			}
			
			//document.getElementById('totalDebit').value = dSum;
			//document.getElementById('totalCredit').value = cSum;
			if(trObj){
			var newRow=trObj.cloneNode(true);
			
			newRow=tableObj.tBodies[0].appendChild(newRow);
			objt1=getControlInBranch(newRow,'display_Code');
			objt2=getControlInBranch(newRow,'display_Head');
			objt3=getControlInBranch(newRow,'display_Debit');
			objt4=getControlInBranch(newRow,'display_Credit');
			//objt5=getControlInBranch(newRow,'display_Narration');
			objt1.innerHTML=" ";
			objt2.innerHTML=" ";
			
			objt3.innerHTML=Math.round(dSum*100)/100;
		objt4.innerHTML=Math.round(cSum*100)/100;
			
			//objt5.innerHTML=" ";
			
			}
			
	}// if Payment Reversal End
	
	
	// if Receipt Reversal start
	else if(vType =='Receipt' || vType=='Journal Voucher')
	{
		
		var gltable= document.getElementById("gridReceiptReversalCheque");
		code=getControlInBranch(gltable.rows[1],'glcodeR').value;
		
		if(!(code!="" && code!=0))
		{
			alert("Enter Account code Details First");
			tObj.style.display="none";
			return false;
		}



		var gltable= document.getElementById("gridReceiptReversalCheque");
		
		for(var i=tableObj.rows.length-1;i>=2;i--)
		{
			tableObj.deleteRow(i);
		}
		for(var i=1;i<gltable.rows.length-1;i++)
		{

			code=getControlInBranch(gltable.rows[i],'glcodeR').value;
			
			if(code)
			{
			name=getControlInBranch(gltable.rows[i],'accCodedescCh').value;
			
			// For Receipt Reversal
			debit=getControlInBranch(gltable.rows[i],'debitAmount').value;
			dr=dr+debit;
			//credit=getControlInBranch(gltable.rows[i],'chqTotalAmt').value;
			
			credit=getControlInBranch(gltable.rows[i],'creditAmount').value;
			cr=cr+credit;
			usrAmt=getControlInBranch(gltable.rows[i],'userAmount').value;

		//	alert("user amount"+usrAmt);

            
			//narration=getControlInBranch(gltable.rows[i],'voucherDetail_narration').value;
			narration="";
			var Obj=getControlInBranch(tableObj.rows[1],"display_Code");
			trObj=getRow(Obj);

			if(usrAmt>0)
            {
			 if(i==1){
							newRow=trObj;
							objt1=getControlInBranch(newRow,'display_Code');
							objt2=getControlInBranch(newRow,'display_Head');
							objt3=getControlInBranch(newRow,'display_Debit');
							objt4=getControlInBranch(newRow,'display_Credit');
							//objt5=getControlInBranch(newRow,'display_Narration');
							if(code=="") code=" -"; if(name=="") name=" -"; if(debit=="") debit=" 0"; if(credit=="") credit=" 0"; if(narration=="") narration=" -";
							objt1.innerHTML=code;
							objt2.innerHTML=name;
							if(debit>0)
							{
							
							objt3.innerHTML=usrAmt;
							objt4.innerHTML="0.0";
							}
							   else
							   {
							
							objt3.innerHTML="0.0";
							objt4.innerHTML=usrAmt;
							}
							//objt5.innerHTML=narration
			 }else{

					 var newRow=trObj.cloneNode(true);
					newRow=tableObj.tBodies[0].appendChild(newRow);
					objt1=getControlInBranch(newRow,'display_Code');
					objt2=getControlInBranch(newRow,'display_Head');
					objt3=getControlInBranch(newRow,'display_Debit');
					objt4=getControlInBranch(newRow,'display_Credit');
					//objt5=getControlInBranch(newRow,'display_Narration');
					if(code=="") code=" -"; if(name=="") name=" -"; if(debit=="") debit=" 0"; if(credit=="") credit=" 0"; if(narration=="") narration=" -";
					objt1.innerHTML=code;
					objt2.innerHTML=name;
					if(debit>0)
					{
							objt3.innerHTML=usrAmt;
							objt4.innerHTML="0.0";
						//alert(objt3.name+"setting"+usrAmt);
							}
					else
					{       objt3.innerHTML="0.0";    
 							objt4.innerHTML=usrAmt;
							//alert(objt4.name+"setting"+usrAmt);
							}
					//objt5.innerHTML=narration;
			}
			}//i addded
		  }
		}
			passedAmount = eval(document.getElementById('passedAmount').value);
			passedAmount = isNaN(passedAmount) ? 0 : passedAmount;
			if( passedAmount < 0){
						alert(' amount can not be nagative');
						return false;
					}


		// For Getting corresponding Bank glcode and name
		var accountId=document.DishonoredChequeForm.passAccId.value;
		//alert("accountId"+accountId);
		var accountGlcode, accountGlname;
			if(accountId != '')
			{
				//alert("Hi inside if");		
				var url = "../commons/Process.jsp?accountId=" +accountId+ "&type=getBankAccountGlcode";
				var req2 = initiateRequest();  		
				req2.open("GET", url, false);
				req2.send(null);
				if (req2.status == 200) 
				{
					//alert("inside if req2.status == 200");
				var result=req2.responseText.split("`-`");     	            	   	           	         	           		              					              
					if(result!= null && result!= "")
					{							
						accountGlcode=result[0];
						accountGlname=result[1];
					}  
				} 
			}
			if(passedAmount>0)  
			{
				var newRow=trObj.cloneNode(true);
				newRow=tableObj.tBodies[0].appendChild(newRow);
				objt1=getControlInBranch(newRow,'display_Code');
				objt2=getControlInBranch(newRow,'display_Head');
				objt3=getControlInBranch(newRow,'display_Debit');
				objt4=getControlInBranch(newRow,'display_Credit');
				if(accountGlcode=="") 
					accountGlcode=" -"; 
				if(accountGlname=="") 
					accountGlname=" -";
				objt1.innerHTML=accountGlcode;
				objt2.innerHTML=accountGlname;
				
				// for Receipt Reversal
				objt3.innerHTML="0";
				objt4.innerHTML=passedAmount;
				
				// for Payment Reversal
				//objt3.innerHTML=passedAmount;
				//objt4.innerHTML="0";
			}

			//

		 var dSum=0;
		 var cSum=0;


		for(var i=1;i<tableObj.rows.length;i++)
		{

		  var dObj=getControlInBranch(tableObj.rows[i],"display_Debit");
		  var cObj=getControlInBranch(tableObj.rows[i],"display_Credit");

		if(!isNaN(parseFloat(dObj.innerHTML)))
		 {
		   var sum=(parseFloat(dObj.innerHTML));
		   dSum=dSum+sum;
		 }
		 if(!isNaN(parseFloat(cObj.innerHTML)))
			 {
		   var sum=(parseFloat(cObj.innerHTML));
		   cSum=cSum+sum;
		 }

		}

		//document.getElementById('totalDebit').value = dSum;
		//document.getElementById('totalCredit').value = cSum;

		var newRow=trObj.cloneNode(true);
		newRow=tableObj.tBodies[0].appendChild(newRow);
		objt1=getControlInBranch(newRow,'display_Code');
		objt2=getControlInBranch(newRow,'display_Head');
		objt3=getControlInBranch(newRow,'display_Debit');
		objt4=getControlInBranch(newRow,'display_Credit');
		//objt5=getControlInBranch(newRow,'display_Narration');
		objt1.innerHTML=" ";
		objt2.innerHTML=" ";

		objt3.innerHTML=dSum.toFixed(2);
			objt4.innerHTML=cSum.toFixed(2);
		//objt5.innerHTML=" ";
	
	
	}// if Receipt Reversal End
			
			

} //showglEntryForReversal()

function showglEntryForBankCharges()
{
	var trObj;
	var tObj=document.getElementById("showEntriesForBankCharges");
	var cr=0,dr=0;
	tObj.style.display="block";
	var tableObj=document.getElementById("entriesForBankCharges");
			
	
	var gltable= document.getElementById("gridBankCharges");
	
		code=document.getElementById("glcodeForBank").value;
	
		if(!(code!="" && code!=0))
		{
			alert("Bank Charges Account code Details Must");
			tObj.style.display="none";
			return false;
		}



		//var gltable= document.getElementById("gridBankCharges");
		
		for(var i=tableObj.rows.length-1;i>=2;i--)
		{
			tableObj.deleteRow(i);
		}
		
			code=document.getElementById("glcodeForBank").value;
			if(code)
			{
			//name=getControlInBranch(gltable.rows[i],'accCodedescBk').value;
			name=document.getElementById("accCodedescBk").value
			
			//debit=getControlInBranch(gltable.rows[i],'bankTotalAmt').value;
			debit=document.getElementById("bankTotalAmt").value;
			dr=dr+debit;
			//credit=getControlInBranch(gltable.rows[i],'bankTotalAmt').value;
			credit=0;
			cr=cr+credit;


			//narration=getControlInBranch(gltable.rows[i],'voucherDetail_narration').value;
			narration="";
			var Obj=getControlInBranch(tableObj.rows[1],"display_Code");
			trObj=getRow(Obj);


			 if(i==1){
							newRow=trObj;
							objt1=getControlInBranch(newRow,'display_Code');
							objt2=getControlInBranch(newRow,'display_Head');
							objt3=getControlInBranch(newRow,'display_Debit');
							objt4=getControlInBranch(newRow,'display_Credit');
							//objt5=getControlInBranch(newRow,'display_Narration');
							if(code=="") code=" -"; if(name=="") name=" -"; if(debit=="") debit=" 0"; if(credit=="") credit=" 0"; if(narration=="") narration=" -";
							objt1.innerHTML=code;
							objt2.innerHTML=name;
							objt3.innerHTML=debit;
							objt4.innerHTML=credit;
							//objt5.innerHTML=narration
			 }else{

					 var newRow=trObj.cloneNode(true);
					newRow=tableObj.tBodies[0].appendChild(newRow);
					objt1=getControlInBranch(newRow,'display_Code');
					objt2=getControlInBranch(newRow,'display_Head');
					objt3=getControlInBranch(newRow,'display_Debit');
					objt4=getControlInBranch(newRow,'display_Credit');
					//objt5=getControlInBranch(newRow,'display_Narration');
					if(code=="") code=" -"; if(name=="") name=" -"; if(debit=="") debit=" 0"; if(credit=="") credit=" 0"; if(narration=="") narration=" -";
					objt1.innerHTML=code;
					objt2.innerHTML=name;
					objt3.innerHTML=debit;
					objt4.innerHTML=credit;
					//objt5.innerHTML=narration;
			}
		  }
		//}
			bankAmount = eval(document.getElementById('bankTotalAmt').value);
			bankAmount = isNaN(bankAmount) ? 0 : bankAmount;
			if( bankAmount < 0){
						alert(' Bank Charges can not be nagative');
						return false;
					}

		// For Getting corresponding Bank glcode and name
		var accountId=document.DishonoredChequeForm.passAccId.value;
		//alert("accountId"+accountId);
		var accountGlcode, accountGlname;
			if(accountId != '')
			{		
				var url = "../commons/Process.jsp?accountId=" +accountId+ "&type=getBankAccountGlcode";
				var req2 = initiateRequest();  		
				req2.open("GET", url, false);
				req2.send(null);
				if (req2.status == 200) 
				{
				var result=req2.responseText.split("`-`");     	            	   	           	         	           		              					              
					if(result!= null && result!= "")
					{							
						accountGlcode=result[0];
						accountGlname=result[1];
					}  
				} 
			}
			if(bankAmount>0)
			{
				var newRow=trObj.cloneNode(true);
				newRow=tableObj.tBodies[0].appendChild(newRow);
				objt1=getControlInBranch(newRow,'display_Code');
				objt2=getControlInBranch(newRow,'display_Head');
				objt3=getControlInBranch(newRow,'display_Debit');
				objt4=getControlInBranch(newRow,'display_Credit');
				if(accountGlcode=="") 
					accountGlcode=" -"; 
				if(accountGlname=="") 
					accountGlname=" -";
				objt1.innerHTML=accountGlcode;
				objt2.innerHTML=accountGlname;
		
				objt3.innerHTML="0";
				objt4.innerHTML=bankAmount;
				
				
			}

		 var dSum=0;
		 var cSum=0;


		for(var i=1;i<tableObj.rows.length;i++)
		{

		  var dObj=getControlInBranch(tableObj.rows[i],"display_Debit");
		  var cObj=getControlInBranch(tableObj.rows[i],"display_Credit");

		if(!isNaN(parseFloat(dObj.innerHTML)))
		 {
		   var sum=(parseFloat(dObj.innerHTML));
		   dSum=dSum+sum;
		 }
		 if(!isNaN(parseFloat(cObj.innerHTML)))
			 {
		   var sum=(parseFloat(cObj.innerHTML));
		   cSum=cSum+sum;
		 }

		}

		//document.getElementById('totalDebit').value = dSum;
		//document.getElementById('totalCredit').value = cSum;

		var newRow=trObj.cloneNode(true);
		newRow=tableObj.tBodies[0].appendChild(newRow);
		objt1=getControlInBranch(newRow,'display_Code');
		objt2=getControlInBranch(newRow,'display_Head');
		objt3=getControlInBranch(newRow,'display_Debit');
		objt4=getControlInBranch(newRow,'display_Credit');
		//objt5=getControlInBranch(newRow,'display_Narration');
		objt1.innerHTML=" ";
		objt2.innerHTML=" ";

		objt3.innerHTML=Math.round(dSum*100)/100;
		objt4.innerHTML=Math.round(cSum*100)/100;
		//objt5.innerHTML=" ";
} //showglEntryForBankCharges()

function checkDuplicatesReceiptRev(tableId,ctlToSearch,ctlToSearch2)
{
//alert("checkDuplicatesReceiptRev"+ctlToSearch2);
	var table=document.getElementById(tableId);
	var row1Data,rowData;
	for(var i=1;i<table.rows.length-1;i++){
	row1Data=getControlInBranch(table.rows[i],ctlToSearch);
	for(var j=i+1;j<table.rows.length;j++){
		rowData=getControlInBranch(table.rows[j],ctlToSearch);
		rowDataColFirst=getControlInBranch(table.rows[j],ctlToSearch2);
		if((row1Data.value.toLowerCase()==rowData.value.toLowerCase())&&rowData.value.length>0){
			alert("Record "+j+" Account Code  Cannot be Same as Record "+i);
			rowDataColFirst.focus();
			return false;
		}
	}
//alert("checkDuplicatesReceiptRev"+ctlToSearch2);
}
return true;
}
function checkDuplicatesPaymentRev(tableId,ctlToSearch)
{
	var table=document.getElementById(tableId);
	var row1Data,rowData;
	for(var i=1;i<table.rows.length-1;i++){
		row1Data=getControlInBranch(table.rows[i],ctlToSearch);
		for(var j=i+1;j<table.rows.length;j++){
			rowData=getControlInBranch(table.rows[j],ctlToSearch);
			
			if((row1Data.value.toLowerCase()==rowData.value.toLowerCase())&&rowData.value.length>0){
				alert("Record "+j+" Account Code  Cannot be Same as Record "+i);
				rowData.focus();
				return false;
			}
		}

	}
	return true;
}
function isRowsEmptyReceiptRev()
{
	 	var table=document.getElementById("gridReceiptReversalCheque");
	 	var tCodeObj , tAmtObj;
	 	for(var i=1;i<table.rows.length;i++)
	 	{
	 		tCodeObjFirstCol=getControlInBranch(table.rows[i],'glcodeChList');
	 		tCodeObj=getControlInBranch(table.rows[i],'glcodeR');
	 		tAmtObj=getControlInBranch(table.rows[i],'debitAmount');
	 		//tAmtObj=getControlInBranch(table.rows[i],'creditAmount');
	 		if(tCodeObj.value.length>0 && tAmtObj.value.length==0){
	 		  alert("Fill the Amount");
	 		  tAmtObj.focus();
	 		  return true;
	 		}
	 		if(tCodeObj.value.length==0){
	 		  alert("Select The Account Code");
	 		  tCodeObjFirstCol.focus();
	 		  return true;
	 		}
	 	}
	return false;
}
function isRowsEmptyPaymentRev()
{
	 	var table=document.getElementById("gridPaymentReversalCheque");
	 	var tCodeObj , tAmtObj;
	 	for(var i=0;i<table.rows.length-1;i++)
	 	{
	 		//alert(table.rows.length-1);
	 		if(document.forms[0].glcodeChIdP[i].value == 0){
	 		  alert("Fill The AccountCode");
	 		  document.forms[0].glcodeChIdP[i].focus();
	 		  return true;
	 		}
	 		//tAmtObj=getControlInBranch(table.rows[i],'creditAmount');
	 		//if(document.forms[0].glcodeChIdP[i].value!=0 && document.getElementById('creditAmount').innerHTML==''){
	 		  //alert("Fill The Amount");
	 		  //tAmtObj.focus();
	 		  //return true;
	 		//}
	 	}
	return false;
}

function checkValidAmountReceiptRev(tableId,ctlToSearch1,ctlToSearch2,ctlToSearch3)
 {
 	var table=document.getElementById(tableId);
 	var col1,col2,col3;
	//alert(table.rows.length);
		
	 	for(var i=1;i<table.rows.length;i++)
		{
			col1=getControlInBranch(table.rows[i],ctlToSearch1).value;
			col2=getControlInBranch(table.rows[i],ctlToSearch2).value;
			col3=getControlInBranch(table.rows[i],ctlToSearch3).value;
			
			 if(col1!="")
			 {
			 	if(isNaN(col1))
			 	{
				alert("Cheque Amount should be iStringn Number");
				return false;
				}
			 			
				minVal=parseFloat(col1);
				if(minVal>parseFloat(9999999999999.99))
				{
				alert("Cheque Amount cannot be greater than 9999999999999.99");
				return false;
				}
			 
			 }
			 
			 if(col2!="")
			 {
				if(isNaN(col2))
				{
				alert("Amount should be in Number");
				return false;
				}
				maxVal=parseFloat(col2);
				creditVal=parseFloat(col3);
	/**			if(maxVal==0 && creditVal==0)
				{
				alert(" Amount cannot be Zero ");
				getControlInBranch(table.rows[i],ctlToSearch2).focus();
				return false;
				} commenting when dropdown is changed to grid  */
				if(minVal < maxVal)
				{
				alert("In Receipt Reversal Grid"+i+" Row Amount must be Equal Or Less than Cheque Amount");
				getControlInBranch(table.rows[i],ctlToSearch2).focus();				
				return false;
				}
				if(maxVal>parseFloat(9999999999999.99))
				{
				alert("Amount cannot be greater than 9999999999999.99");
				getControlInBranch(table.rows[i],ctlToSearch2).focus();				
				return false;
				}
			}
				 
		} // for
	
	return true;
 }
 
function checkValidAmountPaymentRev(tableId,ctlToSearch1)
 {
 	var table=document.getElementById(tableId);
 	var col1;
		
	 	for(var i=1;i<table.rows.length;i++)String
		{
			col1=document.getElementById(ctlToSearch1).value;
			//col1=getControlInBranch(table.rows[i],ctlToSearch1).value;
			
			 if(col1!="")
			 {
			 	if(isNaN(col1))
			 	{
				alert("Amount should be in Number");
				getControlInBranch(table.rows[i],ctlToSearch1).focus();
				return false;
				}
			 			
				minVal=parseFloat(col1);
				if(minVal>parseFloat(9999999999999.99))
				{
				alert("Amount cannot be greater than 9999999999999.99");
				getControlInBranch(table.rows[i],ctlToSearch1).focus();
				return false;
				}
			 
			 }
			 
			
				 
		} // for
	
	return true;
 } 

function checkTotal(tableId,ctlToSearch1,ctlToSearch2)
 {
 	var passedAmt = document.getElementById('passedAmount').value
 	var chqAmount=0;
 	var creditAmt=0;
 	//alert(passedAmt);
 	//alert("passedAmt"+passedAmt);
 	//alert("ctl1"+ctlToSearch1.value);
 	//alert("ctl2"+ctlToSearch2.value);
  	
 	var table=document.getElementById(tableId);
 	for(var i=1;i<table.rows.length;i++)
	{
	chqAmount=Math.round(chqAmount+parseFloat(getControlInBranch(table.rows[i],ctlToSearch1).value,2))*10/10;
	} // for
	if (ctlToSearch2 != null){
 	for(var i=1;i<table.rows.length;i++)
	{
	creditAmt=Math.round(creditAmt+parseFloat(getControlInBranch(table.rows[i],ctlToSearch2).value,2))*10/10;
	} // for	
	passedAmt = Math.round(parseFloat(passedAmt)+parseFloat(creditAmt))*10/10;
	}
 	//alert(chqAmount);
	//alert(creditAmt);
	//alert(passedAmt);
 	if(chqAmount != passedAmt)
	{
	alert("The Total Amount should be equal to the Reversal Amount");
	return false;
	}
	if(chqAmount<0)
	{  alert("Enter the Valid Amount");
	   return false;
	}
	return true;
 }
 /**
 To check the total for Receipt Reversal
 */
function checkTotalNew(tableId,debitAmount,creditAmount,userAmount)
 {
 	var passedAmt = eval(document.getElementById('passedAmount').value).toFixed(2);
 	var reversalAmt=eval(document.getElementById('userReceiptNet').value).toFixed(2);
 	if(reversalAmt != passedAmt)
	{
	alert("The Total Amount should be equal to the Reversal Amount");
	return false;
	}
	return true;
 }

function calculateNet(tabId)
 {
  	var userDebitTotal=0,userCreditTotal=0;
 	var table=document.getElementById(tabId);
 	for(var i=1;i<table.rows.length-1;i++)
	{
	userDebitTotal=userDebitTotal+parseFloat(getControlInBranch(table.rows[i],"debitAmount").value,2);
	userCreditTotal=userCreditTotal+parseFloat(getControlInBranch(table.rows[i],"creditAmount").value,2);
	} 	
	netAmt = Math.abs(userCreditTotal-userDebitTotal);
	//alert(">>>>Net amount"+netAmt);
	document.getElementById("userReceiptNet").value=netAmt.toFixed(2);
	
 }

 
 function onBodyLoad()
 {	
 	var buttonType="${buttonType}";	
 	
 	//alert(buttonType);
 	if(buttonType == "saveclose")
 	{
 		window.close();
 	}
 	if(buttonType == "saveview")
 	{		
 		
 		//alert("Inside save view");
 		var reversalVhId="<%=request.getAttribute("reversalVhId")%>";
 		var bankChargesVhId="<%=request.getAttribute("bankChargesVhId")%>";
 		
 		var reversalAmount="<%=request.getAttribute("reversalAmount")%>";
 		var bankChargesAmount="<%=request.getAttribute("bankChargesAmount")%>";

 		
 		
 		document.DishonoredChequeForm.action = "../brs/DishonoredChequeEntries.do?submitType=beforePrintDishonoredCheque&reversalVhId="+reversalVhId+"&bankChargesVhId="+bankChargesVhId+"&reversalAmount="+reversalAmount+"&bankChargesAmount="+bankChargesAmount,"";
 		document.DishonoredChequeForm.submit();
 		return;		
	}
}	

</script>
</head>
	<body bgcolor="#ffffff"
		onload="clearData();loadDropDownBankCodes();onBodyLoad();"
		onKeyDown="CloseWindow(window.self);">
		<% 	Logger logger = Logger.getLogger(getClass().getName()); 
		logger.info(">>       INSIDE JSP   >>");
	%>



		<html:form action="/brs/DishonoredChequeEntries.do">

			<input type=hidden name="voucherTypeParam" id="voucherTypeParam" />
			<input type=hidden name="passedAmount" id="passedAmount" />
			<input type=hidden name="passAccId" id="passAccId" />
			<input type=hidden name="voucherTxnDate" id="voucherTxnDate" />
			<input type=hidden name="buttonValue" id="buttonValue" />

			<input type=hidden name="vouHName" id="vouHName" />
			<input type=hidden name="voucherNumber" id="voucherNumber" />
			<input type=hidden name="vouDate" id="vouDate" />
			<input type=hidden name="fund" id="fund" />

			<input type=hidden name="refDate" id="refDate" />
			<input type=hidden name="reason" id="reason" />

			<input type=hidden name="reversalAccCode" id="reversalAccCode" />
			<input type=hidden name="reversalDescn" id="reversalDescn" />
			<input type=hidden name="reversalDebitAmount"
				id="reversalDebitAmount" />
			<input type=hidden name="reversalCreditAmount"
				id="reversalCreditAmount" />

                                      





				<!-- For View Results End-->

			<!-- 	<table align=center id="processGrid" name="processGrid"> 
					<tr class="row1">
						<td>
							<html:button styleClass="button" value="Process" property="b2"
								onclick="ButtonPress('process')" />
						</td>
						<td>
							<html:button styleClass="button" value="Cancel" property="b4"
								onclick="onClickCancel()" />
						</td>
						<td>
							<html:button styleClass="button" value="Close" property="b3"
								onclick="window.close();" />
						</td>
					<tr>
				</table> -->


				</td>
				</tr>

				<!-- TxnDateRow start-->
				<tr>
					<td>
						<table style="display: none" name="TxnDateRow" id="TxnDateRow">
							<tr>
							<td class="labelcell" align="right">
								<span  style="Display:none" id="reversalVoucherNumberText">	RevesalVoucher Number <SPAN class="leadon">*</SPAN> </span>
								</td>
								<td>
									<input type="text" name="reversalVoucherNumber" id="reversalVoucherNumber"  style="Display:none" />
								</td>
							
								<td class="labelcell" align="right">
									Transaction Date
									<SPAN class="leadon">*</SPAN>
								</td>
								<td class="smallfieldcell" align="center">
									<html:text property="paramTxnDate" styleId="paramTxnDate"
										onkeyup="DateFormat(this,this.value,event,false,'3')" />
									<a
										href="javascript:show_calendar('DishonoredChequeForm.paramTxnDate');"
										onmouseover="window.status='Date Picker';return true;"
										onmouseout="window.status='';return true;"><img id="IMG1"
											src="/egi/resources/erp2/images/calendar.gif" width=24 height=22 border=0>
									</a>
								</td>
								
								
								</div>
							</tr>
						</table>
					</td>
				</tr>
				<!-- TxnDateRow end-->


				<!-- ReversalAmountGrid Start -->
				<tr>
					<td>

						<table  name="ReversalAmountGrid"
							id="ReversalAmountGrid">
							<tr>
								<td class="tablesubcaption">
									<div align="center">
										Reversal Amount
									</div>
								</td>
								<td class="fieldcell">
									<input type="text" id="revAmt" name="revAmt" value=""
										style="text-align: right" readonly="true" size="12"
										maxlength="16" />
								</td>
							</tr>
						</table>

					</td>
				</tr>
				<!-- ReversalAmountGrid End -->
				<tr>
					<td>

						<!-- For Receipt Reversal Grid Start-->

						<table  name="ReasonReceiptReversal"
							id="ReasonReceiptReversal">
							<tr>
								<td class="tablesubcaption" colspan=2 align=left>
									Receipt Reversal
								</td>
							</tr>
							<tr>
								<td class="labelcell">
									<div align="center">
										Reason
									</div>
								</td>
								<td>
									<textarea class=narrationfieldinput2 id="chqReason"
										name="chqReason"
										onkeypress="return imposeMaxLength(this, 100);" rows="1"
										cols="20"></textarea>
								</td>
							</tr>
						</table>

					</td>
				</tr>
				<tr>
					<td>
						<table width="78%" align="center" 
							name="gridReceiptReversalCheque" id="gridReceiptReversalCheque"
							cellpadding="0" cellspacing="0">
							<tr>
							
								<td class="thStlyle"><div align="center">Account Code</div></td>
								<td class="thStlyle"><div align="center">Description</div></td>
								<td class="thStlyle"><div align="center">Debit</div></td>
								<td class="thStlyle"><div align="center">Credit</div></td>
								<td class="thStlyle"><div align="center">Reversal Amount</div></td>								
							</tr>
							<c:if test="${vt!='Payment'}">
								<c:forEach var="glCode" items="<%=DishonoredChequeForm.getGlcodeChList()%>">
										<c:set var="gl" value="${fn:split(glCode,'~')}"/>
							<tr>
								<input type="hidden" name="functionChId" id="functionChId" value="${gl[4]}">                  
								<td class="smallfieldcell" align="center" width="25%">
								<input type="text" value="${gl[0]}" name="glcodeChList" id="glcodeChList" readonly="true"/>
									<input type="hidden" name="functionCode" id="functionCode" value="${gl[5]}">
									<input type="hidden" name="glcodeR" id="glcodeR" value="${gl[0]}">                                             
									<input type="hidden" name="glcodeChId" id="glcodeChId" value="${gl[0]}">
								</td>                      
								                                               
								<td class="fieldcelldesc" style="width:325px">
									<html:text property="accCodedescCh" style="text-align:left;width:325px"
										value="${gl[1]}" readonly="true" />
								</td>
								<td class="fieldcell">
								<input type="hidden" name="glAmt" id="glAmt" value="${gl[2]}"
									style="text-align: right" readonly="true" />
								<html:text property="debitAmountR" value="${gl[2]}"
										style="text-align:right;width:100px" size="12" maxlength="16" readonly="true" />
								</td>
								<td class="fieldcell">
									<html:text property="creditAmountR" value="${gl[3]}"
										style="text-align:right;width:100px"  size="12" maxlength="16"  readonly="true"/>
								</td>
								<td >
									<html:text property="userAmount" value="0.0"
										style="text-align:right;width:100px"  size="12" maxlength="16"  onblur="setDebitOrCredit(this);calculateNet('gridReceiptReversalCheque')" />
										<input type="hidden" name="debitAmount" id="debitAmount" value="0.0" />
										<input type="hidden" name="creditAmount" id="creditAmount" value="0.0"/>
								</td>
							</tr>
							
							</c:forEach>
							</c:if>
							<tr>
							<td  colspan="4" style="Text-align:right"> Total </td>
							<td> <input type="text" name="uscerReceiptNet" id="userReceiptNet" value="0.0"
										style="text-align:right;width:100px"  size="12" maxlength="16" readonly="true"/>
							</td>
							</tr>
							</table>

						<!-- Add/Delete button For Receipt Reversal-->
					</td>
				</tr>
				<!--
				<tr>
					<td>
						<table align="center" style="display: none"
							name="hideAddDeleteRowForR" id="hideAddDeleteRowForR">
							<tr>
								<td colspan=4>
								<td colspan="2" align="center">
									<input type="button" class="button" value="Add Row"
										id="addDetail" name="addDetail"
										onclick="addRowForReceipt('gridReceiptReversalCheque');" />
								</td>
								<td colspan="2" align="center">
									<input class="button" type="button" value="Delete"
										name="deleteDetail"
										onclick="javascript:return deleteRowForReceipt();" />
								</td>
								</td>
							</tr>
						</table>
					</td>
				</tr>
//commenting button when changed to grid from dropdown of account codes

				--><!--  	Receipt Reversal Grid End	-->


				<!--  	Payment Reversal Grid Start	-->
				<tr>
					<td>

						<table name="ReasonPaymentReversal"
							id="ReasonPaymentReversal">
							<tr>
								<td class="tablesubcaption" colspan=2 align=left>
									Payment Reversal
								</td>
							</tr>
							<tr>

								<td class="labelcell">
									<div align="center">
										Reason
									</div>
								</td>
								<td>
									<textarea class=narrationfieldinput2 id="chqReasonP"
										name="chqReasonP"
										onkeypress="return imposeMaxLength(this, 100);" rows="1"
										cols="20"></textarea>
								</td>
							</tr>
						</table>

					</td>
				</tr>
			<html:hidden property="subledgerDetails" />
				<tr>
					<td>

						<table width="70%" align="center" 
							name="gridPaymentReversalCheque" id="gridPaymentReversalCheque"
							cellpadding="0" cellspacing="0">

							<tr>
								<td class="thStlyle">
									<div align="center">
										Account Code
									</div>
								</td>
								<td class="thStlyle">
									<div align="center">
										Description
									</div>
								</td>
								<td class="thStlyle">
									<div align="center">
										Debit
									</div>
								</td>
								<td class="thStlyle">
									<div align="center">
										Credit
									</div>
								</td>								
														
							</tr>
							<c:if test="${vt=='Payment'}">
										<c:forEach var="glCode"
											items="<%=DishonoredChequeForm.getGlcodeChList()%>">
													<c:set var="glp" value="${fn:split(glCode,'~')}"/>
							<tr>

								<td class="smallfieldcell" align="center" width="25%">
									<input type="text"   id="glcodeChIdP"
										onchange="splitChqGlCode(this,'accCodedescChP');"
										styleClass="bigcombowidth"  value="${glp[0]}">
								
										
										
										
										
									
									<input type="hidden" name="glcodeChIdP" id="glcodeChIdP" value="${glp[0]}">
									
								</td>
								<td class="fieldcelldesc">
									<html:text property="accCodedescChP" style="text-align:left;width:325px"
										value="${glp[1]}" readonly="true" />
								</td>
								<input type="hidden" name="glAmt" id="glAmt" value="${glp[2]}"
									style="text-align: right;width:100px" readonly="true" />
								

								<td class="fieldcell">
									<html:text property="debitAmount" value="${glp[2]}" styleId="debitAmount"
										style="text-align:right;width:100px" size="12" maxlength="16"  />
								</td>  
								<td class="fieldcell">
									<html:text property="creditAmount" value="${glp[3]}" styleId="creditAmount"
										style="text-align:right;width:100px" size="12" maxlength="16" />
								</td>
								<input type="hidden" name="functionChId" id="functionChId" value="${glp[4]}">    
							</tr>
        
					</c:forEach>
					</c:if>
						</table>

						<!-- Add/Delete button For Payment Reversal-->
						<div id="codescontainer"></div>
					</td>
				</tr>
				<!--<tr>
					<td>
						<table style="display: none" name="hideAddDeleteRowForP"
							id="hideAddDeleteRowForP" align="center">
							<tr>
								<td colspan=4>
								<td colspan="2" align="center">
									<input type="button" class="button" value="Add Row"
										id="addDetail" name="addDetail"
										onclick="addRowForPayment('gridPaymentReversalCheque');" />
								</td>
								<td colspan="2" align="center">
									<input class="button" type="button" value="Delete"
										name="deleteDetail"
										onclick="javascript:return deleteRowForPayment();" />
								</td>
								</td>
							</tr>
						</table>
					</td>
				</tr>



				--><!--  	Payment Reversal Grid End	-->


				<!-- SHOW GL button for Reversal Entries Start -->
				<tr>
					<td>
						<table align=center style="display: none"
							id="submitGridForReversal" name="submitGridForReversal">
							<tr class="row1">
								<td>
									<html:button styleClass="button" value="Show GLEntry"
										property="b3" onclick="showglEntryForReversal()" />
								</td>
							<tr>
						</table>
					</td>
				</tr>
				<!-- SHOW GL button for Reversal Entries End-->


				<!-- FOR SHOW GL START-->
				<tr>
					<td>
						<table width="100%" border=0 cellpadding="3" cellspacing="0"
							id="showEntries" name="showEntries" style="DISPLAY: none">
							<tr>
								<td colspan=4>
									<table width="100%" align=center border="0" cellpadding="0"
										cellspacing="0">
										<tr height="25">
											<TD class=displaydata align=middle>
												<h4>
													GLEntry
												</h4>
											</TD>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table width="80%" border="1" cellpadding="0" cellspacing="0"
										align=center id="entries" name="entries">
										<tr>
											<td style="display: none" class="thStlyle">
												<div align="center" valign="center">
													Code Type
												</div>
											</td>
											<td class="thStlyle">
												<div align="center" valign="center">
													Account Code
												</div>
											</td>
											<td class="thStlyle">
												<div align="center" valign="center">
													Account Head
												</div>
											</td>
											<td class="thStlyle">
												<div align="center" valign="center">
													Debit
												</div>
											</td>
											<td class="thStlyle">
												<div align="center" valign="center">
													Credit
												</div>
											</td>

										</tr>
										<tr>
											<td style="display: none" class="tdStlyle">
												<div name="display_CodeType" id="display_CodeType">
													&nbsp;
												</div>
											</td>
											<td class="tdStlyle">
												<div name="display_Code" id="display_Code">
													&nbsp;
												</div>
											</td>
											<td class="tdStlyle">
												<div name="display_Head" id="display_Head">
													&nbsp;
												</div>
											</td>
											<td class="tdStlyle">
												<div name="display_Debit" id="display_Debit"
													style="text-align: right">
													&nbsp;
												</div>
											</td>
											<td class="tdStlyle">
												<div name="display_Credit" id="display_Credit"
													style="text-align: right">
													&nbsp;
												</div>
											</td>

										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>


				<!-- FOR SHOW GL END -->

				<!-- 	For Bank Charges Grid   -->

				<tr>
					<td>
				<center>
						<table  >
							<tr id="bankChargesrow">
								<td class="labelcell" align="right">
								<span id="bankChargesVoucherNumberText" style="display:none" >	Bank Charges Voucher Number <SPAN class="leadon">*</SPAN>  </span>
								</td>
								<td>
									<input type="text" name="bankChargesVoucherNumber"
										id="bankChargesVoucherNumber"  style="display:none"/>
								</td>
								<td class="labelcell" align="right" >
								<div align="center" id="bankChReasonText" style="display:none">
									Reason
								</div>
							</td>	
								<td>
								<textarea class=narrationfieldinput2 id="bankChReason"
										name="bankChReason" style="Display:none"
										onkeypress="return imposeMaxLength(this, 100);" rows="1"
										cols="20"></textarea>
								</td>
								
							</tr>
						</table>
						


				
						<table width="65%" align="center" style="display: none"
							name="gridBankCharges" id="gridBankCharges" cellpadding="0"
							cellspacing="0" align="center">
							<tr>
								<td class="tablesubcaption" colspan=3 align=left>
									Bank Charges
								</td>
							</tr>
							<td style="display: none" class="thStlyle">
								<div align="center">
									Date
								</div>
							</td>
							<td class="thStlyle">
								<div align="center">
									Account Code
								</div>
							</td>
							<td class="thStlyle">
								<div align="center">
									Description
								</div>
							</td>
							<td class="thStlyle">
								<div align="center">
									Amount
								</div>
							</td>
						
							<tr>
								<td style="display: none" class="tdStlyle">
									<div align="right" id="todayBankDt" name="todayBankDt"></div>
									<html:hidden property="todayDateBk" value="" styleId="todayDateBk" />
								</td>

								<td class="fieldcelldesc">
									<div align="left">
										<html:text property="glcodeForBank" style="text-align:left"
											value="" readonly="true" styleId="glcodeForBank" />
									</div>
									<html:hidden property="glcodeBkId" value="" styleId="glcodeBkId" />
								</td>

								<td class="fieldcelldesc">
									<html:text property="accCodedescBk" styleId="accCodedescBk" style="text-align:left"
										value="" readonly="true" />
								</td>
								<td class="fieldcell">
									<html:text property="bankTotalAmt" value="" styleId="bankTotalAmt"
										style="text-align:right" size="12" maxlength="5"
										readonly="true" />
								</td>

							
							</tr>
						</table>
						</center>

					</td>
				</tr>
				<!-- 	For Bank Charges Grid End -->


				<!-- SHOW GL button for Bank Charges Start -->
				<tr>
					<td>
						<table align=center style="display: none"
							id="submitGridForBankCharges" name="submitGridForBankCharges">
							<tr class="row1">
								<td>
									<html:button styleClass="button" value="Show GLEntry"
										property="b3" onclick="showglEntryForBankCharges()" />
								</td>
							<tr>
						</table>
					</td>
				</tr>
				<!-- SHOW GL button for Bank Charges End-->


				<!-- SHOW GL Entries for Bank Charges START-->
				<tr>
					<td>
						<table width="100%" border=0 cellpadding="3" cellspacing="0"
							id="showEntriesForBankCharges" name="showEntriesForBankCharges"
							style="DISPLAY: none">
							<tr>
								<td colspan=4>
									<table width="100%" align=center border="0" cellpadding="0"
										cellspacing="0">
										<tr height="25">
											<TD class=displaydata align=middle>
												<h4>
													GLEntry
												</h4>
											</TD>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table width="80%" border="1" cellpadding="0" cellspacing="0"
										align=center id="entriesForBankCharges"
										name="entriesForBankCharges">
										<tr>
											<td style="display: none" class="thStlyle">
												<div align="center" valign="center">
													Code Type
												</div>
											</td>
											<td class="thStlyle">
												<div align="center" valign="center">
													Account Code
												</div>
											</td>
											<td class="thStlyle">
												<div align="center" valign="center">
													Account Head
												</div>
											</td>
											<td class="thStlyle">
												<div align="center" valign="center">
													Debit
												</div>
											</td>
											<td class="thStlyle">
												<div align="center" valign="center">
													Credit
												</div>
											</td>

										</tr>
										<tr>
											<td style="display: none" class="tdStlyle">
												<div name="display_CodeType" id="display_CodeType">
													&nbsp;
												</div>
											</td>
											<td class="tdStlyle">
												<div name="display_Code" id="display_Code">
													&nbsp;
												</div>
											</td>
											<td class="tdStlyle">
												<div name="display_Head" id="display_Head">
													&nbsp;
												</div>
											</td>
											<td class="tdStlyle">
												<div name="display_Debit" id="display_Debit"
													style="text-align: right">
													&nbsp;
												</div>
											</td>
											<td class="tdStlyle">
												<div name="display_Credit" id="display_Credit"
													style="text-align: right">
													&nbsp;
												</div>
											</td>

										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>


				<!-- SHOW GL Entries for Bank Charges END -->
<c:if test="${dishonCheqDetails!=null}">
<c:if test="${IntiateReceiptWkfl!=null}">     
 	<div id="buttonDiv" align="center">
		<%@include file="manualWfApproverSelection.jsp"%>                 
	</div>         
</c:if>
</c:if>
<input type="hidden" name="nextAction" value="${nextAction}"/>

				<tr>
					<td>
						<table align=center style="display: none" id="submitGrid"
							name="submitGrid">
							<tr class="row1">
								<td>
								
								<!--<c:forEach var="actionItem" items="${validActions}" > -->
								<html:button styleClass="button" value="forwardItem"
										property="b2" onclick="ButtonPress('forwardReceiptItem')" />  
							<!-- 	</c:forEach> -->
								<html:button styleClass="button" value="Close" property="b7"
										onclick="window.close();" /> 
									<!--<html:button styleClass="button" value="Save & Close"
										property="b2" onclick="ButtonPress('saveclose')" />
									<html:button styleClass="button" value="Copy Amount details"
										property="b3" onclick="autoPopulateDebitOrCredit()" />
									<html:button styleClass="button" value="Save & New"
										property="b4" onclick="ButtonPress('savenew')" />
									<html:button styleClass="button" value="Save & View"
										property="b6" onclick="ButtonPress('saveview')" />

									<html:button styleClass="button" value="Cancel" property="b6"
										onclick="onClickCancel()" />
									<html:button styleClass="button" value="Close" property="b7"
										onclick="window.close();" />-->
								</td>
							<tr>
						</table>
					</td>
				</tr>



<SCRIPT>
function ButtonPress( actionitem )
	{
		        
		document.DishonoredChequeForm.action ="../brs/DishonoredChequeEntries.do?submitType=approveDishonourCheque";	
		document.DishonoredChequeForm.submit();
		
	}
</SCRIPT>
</html:form>
</body>
</html>
</c:catch> 

<c:if test = "${catchException != null}">
   <p>The exception is : ${catchException} <br />
   There is an exception: ${catchException.message}</p>
</c:if>
