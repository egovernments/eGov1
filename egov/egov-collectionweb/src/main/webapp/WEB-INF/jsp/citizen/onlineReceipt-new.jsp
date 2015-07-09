<!-- eGov suite of products aim to improve the internal efficiency,transparency, 
    accountability and the service delivery of the government  organizations.
 
     Copyright (C) <2015>  eGovernments Foundation
 
     The updated version of eGov suite of products as by eGovernments Foundation 
     is available at http://www.egovernments.org
 
     This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     any later version.
 
     This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.
 
     You should have received a copy of the GNU General Public License
     along with this program. If not, see http://www.gnu.org/licenses/ or 
     http://www.gnu.org/licenses/gpl.html .
 
     In addition to the terms of the GPL license to be adhered to in using this
     program, the following additional terms are to be complied with:
 
 	1) All versions of this program, verbatim or modified must carry this 
 	   Legal Notice.
 
 	2) Any misrepresentation of the origin of the material is prohibited. It 
 	   is required that all modified versions of this material be marked in 
 	   reasonable ways as different from the original version.
 
 	3) This license does not grant any rights to any user of the program 
 	   with regards to rights under trademark law for use of the trade names 
 	   or trademarks of eGovernments Foundation.
 
   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
-->


<%@ include file="/includes/taglibs.jsp" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Online Payment</title>
<script src="common/js/watermark.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/collectionspublic.css" />
<script type="text/javascript">

jQuery.noConflict();
jQuery(document).ready(function() {
  	 
     jQuery(" form ").submit(function( event ) {
    	 doLoadingMask();
    });
     doLoadingMask();
 });

jQuery(window).load(function () {
	undoLoadingMask();
});

 
var billscount=0;
var accountscount=0;
var initialSetting="true";
var multiplePayee="false";
function populateapportioningamountnew(){
	//document.getElementById("button2").disabled = true;
	document.getElementById("receipt_error_area").innerHTML='';
	dom.get("receipt_error_area").style.display="none";
	// total of actual amt to be credited - can be removed
	var billingtotal=dom.get("totalAmountToBeCollected").value;
	var checkpartpaymentvalue=dom.get("partPaymentAllowed").value;
	var checkoverridevalue=dom.get("overrideAccountHeads").value;
	var collectiontotal=0,cashamount=0,chequeamount=0,cardamount=0;

	var noofaccounts=dom.get("totalNoOfAccounts").value;
	var credittotal=0;
	collectiontotal=dom.get("paymentAmount").value;
	var zeroAccHeads=false;
	if(isNaN(collectiontotal)){
		document.getElementById("receipt_error_area").innerHTML+='<s:text name="onlineReceipts.invalidamount" />' + '<br>';
		dom.get("receipt_error_area").style.display="block";
		return;
	}
	collectiontotal=eval(collectiontotal);
    	billingtotal=eval(billingtotal);

	for(var j=0;j<noofaccounts; j++)
	{
		var advanceRebatePresent=document.getElementById('receiptDetailList['+j+'].isActualDemand').value;
		if(advanceRebatePresent==0){
			zeroAccHeads=true;
		}
	}
	if(dom.get("callbackForApportioning").value=="true")
	{
		if(collectiontotal > billingtotal && zeroAccHeads==false)
		{
			document.getElementById("receipt_error_area").innerHTML+='<s:text name="onlineReceipts.greatercollectioamounterror.errormessage" />' + '<br>';
			dom.get("receipt_error_area").style.display="block";
			return false;
		}
		else
		{																														    			//makeJSONCall(["OrderNumber","CreditAmount","DebitAmount","CrAmountToBePaid"],'${pageContext.request.contextPath}/citizen/onlineReceipt!apportionBillAmount.action',{onlineInstrumenttotal:collectiontotal},apportionLoadHandler,apportionLoadFailureHandler);
		}
	}
	else if(dom.get("callbackForApportioning").value=="false")
	{
		if(initialSetting=="true"){
			initialiseCreditAmount();
		}
	
		credittotal=calculateCreditTotal();
		if(checkpartpaymentvalue=="true" && multiplePayee=="false")
		{
			// if overridign accounts is permitted and collected total is same as the 
			// credit total, do not apportion
		    	if(checkoverridevalue=="true" && collectiontotal==credittotal){
		   		return;
		    	}
		    	if(collectiontotal==""){
		   		waterMarkInitialize('paymentAmount','0.0');
		    		return;
		    	}
			if(collectiontotal <= billingtotal)
			{
				var remainingamount=0;
				var check=0;
				for(var j=0;j<noofaccounts; j++)
				{
					var amounttobecollected=document.getElementById('receiptDetailList['+j+'].cramountToBePaid').value;
					amounttobecollected=Math.round(amounttobecollected*100)/100;

					if(check==0)
					{
						if(collectiontotal>amounttobecollected)
						{
							document.getElementById('receiptDetailList['+j+'].cramount').value=parseFloat(amounttobecollected).toFixed(1);
							document.getElementById('receiptDetailList['+j+'].cramountDisplay').innerHTML=parseFloat(amounttobecollected).toFixed(1);
								
							remainingamount=collectiontotal-amounttobecollected;
							remainingamount=Math.round(remainingamount*100)/100;
							document.getElementById('receiptDetailList['+j+'].balanceAmount').value="0.0";
							document.getElementById('receiptDetailList['+j+'].balanceAmtDisplay').innerHTML="0.0";
						}
						else if(collectiontotal<=amounttobecollected)
						{
							document.getElementById('receiptDetailList['+j+'].cramount').value=parseFloat(collectiontotal).toFixed(1);
							document.getElementById('receiptDetailList['+j+'].cramountDisplay').innerHTML=parseFloat(collectiontotal).toFixed(1);
								
							var amt1=amounttobecollected-collectiontotal;
							amt1=Math.round(amt1*100)/100;
							document.getElementById('receiptDetailList['+j+'].balanceAmount').value=amt1.toFixed(1);
							document.getElementById('receiptDetailList['+j+'].balanceAmtDisplay').innerHTML=amt1.toFixed(1);
						}
					}
					else if(check!=0 && remainingamount>0)
					{
						if(remainingamount>amounttobecollected)
						{
							document.getElementById('receiptDetailList['+j+'].cramount').value=parseFloat(amounttobecollected).toFixed(1);
							document.getElementById('receiptDetailList['+j+'].cramountDisplay').innerHTML=parseFloat(amounttobecollected).toFixed(1);
						
							remainingamount=remainingamount-amounttobecollected;
							remainingamount=Math.round(remainingamount*100)/100;
							document.getElementById('receiptDetailList['+j+'].balanceAmount').value="0.0";
							document.getElementById('receiptDetailList['+j+'].balanceAmtDisplay').innerHTML="0.0"
						}
						else if(remainingamount<=amounttobecollected)
						{
							document.getElementById('receiptDetailList['+j+'].cramount').value=remainingamount.toFixed(1);
								document.getElementById('receiptDetailList['+j+'].cramountDisplay').innerHTML=remainingamount.toFixed(1);
							
						
							var amt2=amounttobecollected-remainingamount;
							amt2=Math.round(amt2*100)/100;
							remainingamount=remainingamount-document.getElementById('receiptDetailList['+j+'].cramount').value;
							document.getElementById('receiptDetailList['+j+'].balanceAmount').value=amt2.toFixed(1);
							document.getElementById('receiptDetailList['+j+'].balanceAmtDisplay').innerHTML=amt2.toFixed(1);
						}
					}
					else if(check!=0 && remainingamount==0)
					{
						document.getElementById('receiptDetailList['+j+'].cramount').value="0.0";
						document.getElementById('receiptDetailList['+j+'].cramountDisplay').innerHTML="0.0";
						
						document.getElementById('receiptDetailList['+j+'].balanceAmount').value=parseFloat(amounttobecollected).toFixed(1);
						document.getElementById('receiptDetailList['+j+'].balanceAmtDisplay').innerHTML=parseFloat(amounttobecollected).toFixed(1);
					}
					check++;
				}//end of for
				//dom.get("totalamountdisplay").value=collectiontotal;
			}
			else
			{
				document.getElementById("receipt_error_area").innerHTML+='<s:text name="onlineReceipts.greatercollectioamounterror.errormessage" />' + '<br>';
			dom.get("receipt_error_area").style.display="block";
			}
		}
			
	}
	//document.getElementById("button2").disabled = false;
}

function initialiseCreditAmount(){
	var noofaccounts=dom.get("totalNoOfAccounts").value;
	for(var j=0;j<noofaccounts; j++)
	{
		document.getElementById('receiptDetailList['+j+'].cramount').value=document.getElementById('receiptDetailList['+j+'].cramountToBePaid').value;
	}
	initialSetting="false";
}

function calculateCreditTotal(){
	var noofaccounts=dom.get("totalNoOfAccounts").value;
	var creditamount=0,credittotal=0,debitamount=0;
	//this step is done to populate credit amount
    for(var j=0;j<noofaccounts; j++)
	{
		var receivedAmount=eval(document.getElementById('receiptDetailList['+j+'].cramount').value);
		var rebateamount=eval(document.getElementById('receiptDetailList['+j+'].dramount').value);
		creditamount = isNaN(receivedAmount)?0:receivedAmount;
		debitamount = isNaN(rebateamount)?0:rebateamount;
		credittotal=credittotal+creditamount;
		credittotal=credittotal-debitamount;
	}
	return credittotal;
}

function validateOnlineReceipt(){
	populateapportioningamountnew();
	var amount=dom.get("paymentAmount").value;
	var billingtotal=dom.get("totalAmountToBeCollected").value;
	document.getElementById("receipt_error_area").innerHTML='';
	dom.get("receipt_error_area").style.display="none";
	var checkpartpaymentvalue=dom.get("partPaymentAllowed").value;
	var validation=true;
	var zeroAccHeads=false;
	var noofaccounts=dom.get("totalNoOfAccounts").value;
	for(var j=0;j<noofaccounts; j++)
	{
		var advanceRebatePresent=document.getElementById('receiptDetailList['+j+'].isActualDemand').value;
		if(advanceRebatePresent==0){
			zeroAccHeads=true;
		}
	}
	if(amount==null || amount=="" || isNaN(amount) || amount=="0.0" || amount=="0" || amount<0 || amount.startsWith('+')){
		document.getElementById("receipt_error_area").innerHTML+='<s:text name="onlineReceipts.invalidamount" />' + '<br>';
		dom.get("receipt_error_area").style.display="block";
		validation=false;
		return false;
	}
	amount=eval(amount);
	billingtotal=eval(billingtotal);

	if(checkpartpaymentvalue=="false" && amount < billingtotal){
		document.getElementById("receipt_error_area").innerHTML+='<s:text name="onlineReceipts.partpaymenterror" />' + '<br>';
		dom.get("receipt_error_area").style.display="block";
		validation=false;
		return false;
	}

	if(amount > billingtotal && zeroAccHeads==false)
	{
		document.getElementById("receipt_error_area").innerHTML+='<s:text name="onlineReceipts.greatercollectioamounterror.errormessage" />' + '<br>';
		dom.get("receipt_error_area").style.display="block";
		validation=false;
	}
	if(dom.get("paymentServiceId").value=="-1"){
		document.getElementById("receipt_error_area").innerHTML+='<s:text name="onlineReceipts.selectpaymentgateway.errormessage" />' + '<br>';
		dom.get("receipt_error_area").style.display="block";
		validation=false;
		return false;
	}
	
	if(!dom.get("checkbox").checked){
		document.getElementById("receipt_error_area").innerHTML+='<s:text name="onlineReceipts.acceptterms" />' + '<br>';
		dom.get("receipt_error_area").style.display="block";
		validation=false;
	}
	if(validation==false){
		return false;
	}
	else {
			doLoadingMask('#loadingMask');
			document.collDetails.action="onlineReceipt!saveNew.action";
			document.collDetails.submit();
  		}
}

function openTerms(serviceCode){
	newwindow=window.open ("../termsandconditons/"+serviceCode+"_terms_conditions.html","Terms and Conditions",'height=300,width=1000'); 
}

function initiateRequest() 
{
	if (window.XMLHttpRequest) {
		return new XMLHttpRequest();
	}
	else if (window.ActiveXObject) {
		isIE = true;
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
}

function trimResponse(responseTxt) 
{
	var responseTxt =responseTxt.replace(/^\s+|\s+$/g,"");	
	return responseTxt;
}

var serviceMsg;

function displayTransactionDetails(){
	document.getElementById('transactiondiv').style.display = 'none';
	var paymentServiceId=dom.get("paymentServiceId").value;
	if(paymentServiceId>0)
	{
		callAjax(paymentServiceId);
	}
}

function callAjax(paymentServiceId){

	var url = "../termsandconditons/getServiceByID.jsp?serviceid=" + paymentServiceId ;
	var req = initiateRequest();
	
	req.onreadystatechange = function()
	{
		if (req.readyState == 4)
	    	{
	        	strResponse = trimResponse(req.responseText);
		 	if (req.status == 200)
			{
				serviceMsg=trimResponse(req.responseText);
			 	dom.get("transactiondiv").innerHTML=serviceMsg;
			 	document.getElementById('transactiondiv').style.display = 'block';
		     	}//close If  
			else
		     	{
		        	alert("<s:text name='onlineReceipts.transactionmessage.errormessage'/>");
		        	document.getElementById('transactiondiv').style.display = 'none';
		     	}
	     	}//close If
	}//close req.onreadystatechange
	req.open("GET", url, true);
	req.send(null);
	
}

function onLoad(){
	 //document.getElementById("button2").disabled = true;
}
 </script>
<style type="text/css">
<!--
#apDiv1 {
	position:absolute;
	width:36px;
	height:27px;
	z-index:1;
	left: 424px;
	top: 760px;
}
-->
</style>
</head>

<body onload="onLoad();">
<div class="maincontainer">
<s:form theme="simple" name="collDetails" action="onlineReceipt">
 <div class="errorstyle" id="receipt_error_area" style="display:none;"></div>
   <div class="formmainbox">

   <div class="subheadnew">
					<span class="complaintmsg"><s:text
							name="onlineReceipts.payyourtax" /></span>
					<div class="dottedgridlarge2"></div>
	</div>

    <div class="margin20 container-medium">
	
	<div class="text-left margin-5"><s:text name="onlineReceipts.lastthreetransaction"/></div>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	
	  <tr>
	    <th class="bluebgheadtd"><s:text name="onlineReceipts.lastthreetbl.sno"/></th>
	    <th class="bluebgheadtd"><s:text name="onlineReceipts.lastthreetbl.transid"/></th>
	    <th class="bluebgheadtd"><s:text name="onlineReceipts.lastthreetbl.transdatetime"/></th>
	    <th class="bluebgheadtd"><s:text name="onlineReceipts.lastthreetbl.amtrs"/></th>
	    <th class="bluebgheadtd"><s:text name="onlineReceipts.lastthreetbl.status"/></th>
	  </tr>
	  
	  <tr>
	    <td class="blueborderfortd text-center">-</td>
	    <td class="blueborderfortd text-center">-</td>
	    <td class="blueborderfortd text-center">-</td>
	    <td class="blueborderfortd text-center">-</td>
	    <td class="blueborderfortd text-center">-</td>
	  </tr>
	
	</table>
	
	
	<div class="rbroundbox3">
	
		<div class="rbcontent4"><div class="containerformsg1">
		  
	  <div class="text-left margin-5"><s:text name="onlineReceipts.paytaxtext"/></div>  
      <table width="100%" border="0" cellspacing="0" cellpadding="0" id="billsheaderinfotable">
     <s:hidden name="collectXML" id="collectXML" value="%{collectXML}" />
<s:hidden label="totalNoOfBills" id="totalNoOfBills" name="totalNoOfBills" value="%{totalNoOfBills}"/>
<s:hidden label="totalNoOfAccounts" id="totalNoOfAccounts" value="%{totalNoOfAccounts}" name="totalNoOfAccounts"/>
<s:hidden label="multiplePayee" id="multiplePayee" name="multiplePayee" value="%{multiplePayee}"/>
<s:hidden label="reasonForCancellation" id="reasonForCancellation" name="reasonForCancellation" />
<s:hidden label="oldReceiptId" id="oldReceiptId" name="oldReceiptId"/>
<s:hidden label="overrideAccountHeads" id="overrideAccountHeads" value="%{overrideAccountHeads}" name="overrideAccountHeads"/>
<s:hidden label="partPaymentAllowed" id="partPaymentAllowed" value="%{partPaymentAllowed}" name="partPaymentAllowed"/>
<s:hidden label="cashAllowed" id="cashAllowed" value="%{cashAllowed}" name="cashAllowed"/>
<s:hidden label="cardAllowed" id="cardAllowed" value="%{cardAllowed}" name="cardAllowed"/>
<s:hidden label="chequeDDAllowed" id="chequeDDAllowed" value="%{chequeDDAllowed}" name="chequeDDAllowed"/>
<s:hidden label="totalAmountToBeCollected" id="totalAmountToBeCollected" value="%{totalAmountToBeCollected}" name="totalAmountToBeCollected"/>
<s:hidden label="callbackForApportioning" id="callbackForApportioning" value="%{callbackForApportioning}" name="callbackForApportioning"/>
<%int i=1;%>
<%int rcptDtlCnt=0; %>

	<tr>
	  <td>
	    <div class="switchgroup1" id="bobcontent<s:property value="#receiptheaderrowstatus.index + 1" />">
	    
	           
	    
		       <table width="100%" border="0" cellpadding="0" cellspacing="0"  id="accountdetailtable<%=i%>" name="accountdetailtable<%=i%>" class="tbl-medium">
		              <tr>
		               <th class="bluebgheadtd text-left" width="90%" ><s:text name="onlineReceipts.accountdetails.description"/></td>
		               <th class="bluebgheadtd" width="10%" ><div align="right"><s:text name="onlineReceipts.demand"/></div></td>
		                 <!--td class="head" width="19%" ><s:text name="onlineReceipts.collectedamount"/></td-->
		        	 <!--td class="head" width="19%" ><s:text name="onlineReceipts.balance"/></td-->
		                </tr>
		                <s:iterator value="%{receiptDetailList}" status="rowreceiptdetailstatus">
			              	<tr>

				               
				                <td class="blueborderfortd"><s:property  value="%{description}" /></td>

				                <td class="blueborderfortd">
				                	<div align="right">
				                	<s:property value="%{cramountToBePaid}"/>
				                	  	<input id="receiptDetailList[<%=rcptDtlCnt%>].cramountToBePaid" name="receiptDetailList[<%=rcptDtlCnt%>].cramountToBePaid" value='<s:property value="%{cramountToBePaid}"/>' type="hidden" readonly="true" disabled="disabled" size="12"/></div>
							
									<input type="hidden" align="right" id="receiptDetailList[<%=rcptDtlCnt%>].cramountDisplay" />
									<input name="receiptDetailList[<%=rcptDtlCnt%>].cramount" value="0.0" type="hidden" id="receiptDetailList[<%=rcptDtlCnt%>].cramount"  size="12" onblur='checkandcalculatecredittotal(<%=rcptDtlCnt%>,this);'/>
									
									<input type="hidden" name="receiptDetailList[<%=rcptDtlCnt%>].ordernumber" id="receiptDetailList[<%=rcptDtlCnt%>].ordernumber" value='<s:property value="ordernumber"/>' />
									<input type="hidden" name="receiptDetailList[<%=rcptDtlCnt%>].receiptHeader.referencenumber" id="receiptDetailList[<%=rcptDtlCnt%>].receiptHeader.referencenumber" value='<s:property value="referencenumber"/>'/>
									<input type="hidden" name="receiptDetailList[<%=rcptDtlCnt%>].dramount" id="receiptDetailList[<%=rcptDtlCnt%>].dramount" />
									<input type="hidden" name="receiptDetailList[<%=rcptDtlCnt%>].isActualDemand" id="receiptDetailList[<%=rcptDtlCnt%>].isActualDemand" value='<s:property value="isActualDemand"/>' />
								
								<input type="hidden" id="receiptDetailList[<%=rcptDtlCnt%>].balanceAmtDisplay" />
								<input type="hidden" name="receiptDetailList[<%=rcptDtlCnt%>].balanceAmount" id="receiptDetailList[<%=rcptDtlCnt%>].balanceAmount" value="0.0"/>
										
								
								</td>
								
			              	</tr>
			              	<%rcptDtlCnt=rcptDtlCnt+1;%>
		                </s:iterator> <!--  Finished iterating through the account heads (receipt detail) -->
		                  <tr>
		          <!--td class="tablestyle">&nbsp;</td-->
		          <!--td class="tablestyle">&nbsp;</td-->
		          <td class="blueborderfortd text-right bg-gray"><span class="justbold"><s:text name="onlineReceipts.totalbalance"/> :</span></td>
		          <td class="blueborderfortd text-right bg-gray"><span class="justbold"><s:property  value="%{totalAmountToBeCollected}" /></span></td>
	            </tr>
	             <tr>
		          <!--td class="tablestyle3">&nbsp;</td-->
		          <td colspan="1" class="blueborderfortd text-right bg-gray"><span class="justbold"><s:text name="onlineReceipts.balanceamounttopay"/> :<span class="mandatory1">*</span></span></td>
		          <td class="blueborderfortd text-right bg-gray"><s:textfield label="paymentAmount" id="paymentAmount" maxlength="12" name="paymentAmount" size="12" value="0.0" cssStyle="color:DarkGray; text-align:right" onfocus = "waterMarkTextIn('paymentAmount','0.0');" onkeyup="populateapportioningamountnew()" onload="waterMarkInitialize('paymentAmount','0.0');"/></td>
	            </tr>
	      
		       <td colspan="2">
		         <span class="mandatory1 padding-5"><s:text name="onlineReceipts.mandatoryfields"/></span>
		       </td>
		       
		       </tr>
		       
		      
	            
		            </table> <!-- End of accountdetailtable i -->
	            
	          
	          <%i=i+1;%>
	        <!-- End of table enclosing all account detail tables -->
	      </div></td>
	      
	      
	  </tr>
      
      </table>
      
      <div class="text-left margin-5"><s:text name="onlineReceipts.paythrough"/></div>
      
      <table class="table-payment">
      
        <tr>
        
          <td class="blueborderfortd" width="50%">
           <div>
             <s:radio id="radiobutton" name="paythrough" title="Axis Bank" list="#{'1' : ' &nbsp;Axis Bank'}"/>
           </div> 
           <div>Transaction fees is NIL.</div>
          </td>
          <td class="blueborderfortd" width="50%"> 
            <div>
             <s:radio id="radiobutton" name="paythrough" title="Axis Bank" list="#{'2' : ' &nbsp;Bill Desk Payment Gateway'}"/>
            </div> 
            <div>
              Transaction fees for various modes of payment are as follows :-
              <ul>
                <li>Net Banking : Rs.6/- Per Transaction + 12.36% on Rs.6/- (Service tax + Education cess)</li>
                <li>Credit Card (Master Card/Visa) : 1.75% on Bill Amount + 12.36% on 1.75% of bill amount (Service tax + Education cess)</li>
              </ul>
            </div>
          </td>
          
        </tr>
        <tr>
         <td class="blueborderfortd" colspan="2">
          <div>
           <s:checkbox name="terms" id="terms" fieldValue="false"/> <label for="terms"><s:text name="onlineReceipts.termsandconditions"/> </label>  <br/>
           <s:text name="onlineReceipts.termshelptext" />
          </div>
         </td>
        </tr>
        <tr>
         <td class="blueborderfortd" colspan="2">
          <div>
           <label>Terms And Conditions: </label>
          </div>
          <ul>
           <li>By accepting to make Payment online it is implied that the customer agrees to the terms and conditions of Netbanking System/credit card company.</li>
           <li>Once the payment transaction is submitted request for refund will not be entertained.</li>
           <li>After completing payment entries, customer will get a unique Payment Identification Number (BID Number) which may be quoted for all future communications with reference to this transaction.</li>
           <li>The actual updation of payment by Corporation will take place after getting confirmation from the Banks.</li>
           <li>In the exigency of connection getting timedout or user clicking to close the browser before getting payment confirmation message he has to wait for sometime before proceeding to make subsequent payment for the same bill(s).</li>
           <li>If you are not getting the receipt or any break in connectivity in the middle of the operations check the receipt status on the next day. The System has a provision for Auto Reconcilation and will generate the Receipt if the Payment was actually effected from your side.</li>
          </ul>
         </td>
        </tr>
        <tr>
          <td class="blueborderfortd" colspan="2">Any Discrepancy found in the arrears mentioned above you may contact respective Zonal Revenue Officer with payment receipt for rectification.</td>
        </tr>
        
      
      </table>
      
      <br/>
      
      
      </div><div class="contentright"></div><div class="dottedgridlarge"></div>
      <div>
			  	
		  </div>
       <div id="transactiondiv" style="display:none">
       
		  </div>
	<div id="loadingMask" style="display:none;overflow:hidden;text-align: center"><img src="${pageContext.request.contextPath}/images/bar_loader.gif"/> <span style="color: red">Please wait....</span></div>
   	  <div class="bottombuttonholder" >
   	    <input align="center" type="button"   class="buttonsubmit" id="button2" value="Pay Online" onclick="return validateOnlineReceipt();"/>
   	 </div></div>
   	 <br/>
	</div></div>
	</div>
</s:form>

</div>
</body>



