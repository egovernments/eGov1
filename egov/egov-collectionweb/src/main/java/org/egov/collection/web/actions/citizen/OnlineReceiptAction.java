/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
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
 */
package org.egov.collection.web.actions.citizen;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.StreamResult;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.OnlinePayment;
import org.egov.collection.entity.ReceiptDetail;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.handler.BillCollectXmlHandler;
import org.egov.collection.integration.models.BillInfoImpl;
import org.egov.collection.integration.models.BillReceiptInfo;
import org.egov.collection.integration.models.BillReceiptInfoImpl;
import org.egov.collection.integration.pgi.PaymentRequest;
import org.egov.collection.integration.pgi.PaymentResponse;
import org.egov.collection.service.ReceiptHeaderService;
import org.egov.collection.utils.CollectionCommon;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.collection.utils.FinancialsUtil;
import org.egov.commons.EgwStatus;
import org.egov.commons.Fund;
import org.egov.commons.service.CommonsServiceImpl;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.models.ServiceDetails;
import org.egov.model.instrument.InstrumentHeader;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@ParentPackage("egov")
@Results({ 
    @Result(name = BaseFormAction.NEW, location = "onlineReceipt-new.jsp"),
    @Result(name = OnlineReceiptAction.REDIRECT, location = "onlineReceipt-redirect.jsp")
})
public class OnlineReceiptAction extends BaseFormAction implements ServletRequestAware{

    private static final Logger LOGGER = Logger.getLogger(OnlineReceiptAction.class);
    public static final String REDIRECT = "redirect";
    private static final long serialVersionUID = 1L;

    //private List<ReceiptPayeeDetails> modelPayeeList = new ArrayList<ReceiptPayeeDetails>();
    private CollectionsUtil collectionsUtil;
    private ReceiptHeaderService receiptHeaderService;
    private CollectionCommon collectionCommon;
    private CommonsServiceImpl commonsService;

    private List<ValidationError> errors = new ArrayList<ValidationError>();
    
    private BigDecimal onlineInstrumenttotal = BigDecimal.ZERO;
    private List<ReceiptDetail> receiptDetailList = new ArrayList<ReceiptDetail>();
    private BillInfoImpl collDetails = new BillInfoImpl();
    private BillCollectXmlHandler xmlHandler;
    private String collectXML;
    private String serviceName;
    private List<String> collectionModesNotAllowed = new ArrayList<String>();
    private Boolean overrideAccountHeads;
    private Boolean partPaymentAllowed;
    private BigDecimal totalAmountToBeCollected;
    private BigDecimal paymentAmount;
    private FinancialsUtil financialsUtil;
    private ReceiptHeader onlinePaymentReceiptHeader;
    private Long receiptId;
    private ReceiptHeader[] receipts;
    private Integer paymentServiceId = -1;
    private Integer reportId = -1;
    private String serviceCode;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private String responseMsg = "";
    private HttpSession session = null;
    private HttpServletRequest request;
    
    private static final String RESULT = "result";
    private static final String RECONRESULT = "reconresult";
    private Boolean callbackForApportioning;
    private String receiptNumber;
	private String consumerCode;
	private String receiptResponse = "";
	
	private ReceiptHeader receiptHeader;
	
	public Object getModel() {
	        return null;
	}
	
    @Action(value = "/citizen/onlineReceipt-newform")
    public String newform() {
        return NEW;
    }

    public String testOnlinePaytMsg() {
        return "PaytGatewayTest";
    }

    @Action(value = "/citizen/onlineReceipt-saveNew")
    public String saveNew() {
    	/**
		 * initialise receipt info,persist receipt, create bill desk payment
		 * object and redirect to payment screen
		 */
		if (callbackForApportioning && !overrideAccountHeads){
			this.apportionBillAmount();
			/*for (ReceiptPayeeDetails payee : modelPayeeList) {
				for (ReceiptHeader receiptHeader : payee.getReceiptHeaders()) {
					if(receiptDetailList == null || receiptDetailList.isEmpty() || receiptDetailList.size() == 0){
						throw new EGOVRuntimeException("Receipt could not be created as the apportioned receipt detail list is empty");
					}else{
						receiptHeader.setReceiptDetails(new HashSet(receiptDetailList));
					}
				}
			}*/	
		}
		populateAndPersistReceipts();
		return REDIRECT;
    }
    
	public StreamResult processResponseMessage() {
		
		try {
			long startTimeInMilis = System.currentTimeMillis();
			
			LOGGER.info("Response Msg :  " +responseMsg);

			/**
			 * TO DO : The below 'if loop' Is To be removed once the test URL is
			 * UP
			 */
			if (getTestReceiptId() != null) {
				responseMsg = "MerchantID|CustomerID|TxnReferenceNo|BankReferenceNo|1000.0|BankID|"
						+ "BankMerchantID|TxnType|CurrencyName|ItemCode|SecurityType|SecurityID|SecurityPassword|"
						+ "10-05-2010 15:39:09|" + getTestAuthStatusCode() + "|SettlementType|" + getTestReceiptId()
						+ "|AdditionalInfo2|AdditionalInfo3|AdditionalInfo4|"
						+ "AdditionalInfo5|AdditionalInfo6|AdditionalInfo7|ErrorStatus|ErrorDescription";
				/*String checksum = PGIUtil.doDigest(responseMsg, CollectionConstants.UNIQUE_CHECKSUM_KEY);
				responseMsg += "|" + checksum;*/
				serviceCode = "BDPGI";
			}
			ServiceDetails paymentService = (ServiceDetails) getPersistenceService().findByNamedQuery(
					CollectionConstants.QUERY_SERVICE_BY_CODE, CollectionConstants.SERVICECODE_PGI_BILLDESK);

			setPaymentResponse(collectionCommon.createPaymentResponse(paymentService, getMsg()));
			

			onlinePaymentReceiptHeader = receiptHeaderService.findByNamedQuery(CollectionConstants.QUERY_RECEIPT_BY_RECEIPTID_AND_REFERENCENUMBER, Long.valueOf(paymentResponse.getReceiptId()),paymentResponse.getCustomerId());
			if (onlinePaymentReceiptHeader != null) {
					// if status code is 0002, ie Bill desk waiting for response from
					// payment gateway then make transaction in pending state.
					if (CollectionConstants.PGI_AUTHORISATION_CODE_WAITINGFOR_PAY_GATEWAY_RESPONSE.equals(paymentResponse
							.getAuthStatus())) {
						EgwStatus paymentStatus = commonsService.getStatusByModuleAndCode(
								CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
								CollectionConstants.ONLINEPAYMENT_STATUS_CODE_PENDING);
						onlinePaymentReceiptHeader.getOnlinePayment().setStatus(paymentStatus);
						onlinePaymentReceiptHeader.getOnlinePayment().setAuthorisationStatusCode(
								paymentResponse.getAuthStatus());
					} else if (CollectionConstants.PGI_AUTHORISATION_CODE_SUCCESS.equals(paymentResponse.getAuthStatus())) {
						processSuccessMsg();
					} else {
						processFailureMsg();
					}
		
					long elapsedTimeInMillis = System.currentTimeMillis() - startTimeInMilis;
					LOGGER.info("Online Receipt is persisted with receipt number :"
							+ onlinePaymentReceiptHeader.getReceiptnumber() + "; Time taken (millis)" + elapsedTimeInMillis);
			} else {
				LOGGER.info("Error in processResponseMessage :::::: onlinePaymentReceiptHeader object is null");
				receiptResponse = "FAILURE|NA";
			}		
					
		} catch (Exception exp) {
			LOGGER.error("Error in processResponseMessage",exp);
			receiptResponse = "FAILURE|NA";
		}
		return new StreamResult(new ByteArrayInputStream(receiptResponse.getBytes()));
	}

    /**
     * 
     * @return
     */
    @ValidationErrorPage(value = "result")
    public String acceptMessageFromPaymentGateway() {

        /**
         * TO DO : The below 'if loop' Is To be removed once the test URL is UP
         */
    	
    	long startTimeInMilis = System.currentTimeMillis();
    	
        if (getTestReceiptId() != null) {
            responseMsg = "MerchantID|CustomerID|TxnReferenceNo|BankReferenceNo|1000.0|BankID|"
                    + "BankMerchantID|TxnType|CurrencyName|ItemCode|SecurityType|SecurityID|SecurityPassword|"
                    + "10-05-2010 15:39:09|" + getTestAuthStatusCode() + "|SettlementType|" + getTestReceiptId()
                    + "|AdditionalInfo2|AdditionalInfo3|AdditionalInfo4|"
                    + "AdditionalInfo5|AdditionalInfo6|AdditionalInfo7|ErrorStatus|ErrorDescription";
            /*String checksum = PGIUtil.doDigest(responseMsg, CollectionConstants.UNIQUE_CHECKSUM_KEY);
            responseMsg += "|" + checksum;*/
            serviceCode = "BDPGI";
        }

        LOGGER.info("responseMsg:	" + responseMsg);

        ServiceDetails paymentService = (ServiceDetails) getPersistenceService().findByNamedQuery(
                CollectionConstants.QUERY_SERVICE_BY_CODE, getServiceCode());

        try {
            setPaymentResponse(collectionCommon.createPaymentResponse(paymentService, getMsg()));
        } catch (EGOVRuntimeException egovEx) {
            throw new ValidationException(Arrays.asList(new ValidationError(egovEx.getMessage(), egovEx.getMessage())));
        }

        onlinePaymentReceiptHeader = receiptHeaderService.findByNamedQuery(CollectionConstants.QUERY_RECEIPT_BY_RECEIPTID_AND_REFERENCENUMBER, Long.valueOf(paymentResponse.getReceiptId()),paymentResponse.getCustomerId());
        
     // if status code is 0002, ie Bill desk waiting for response from payment gateway then make transaction in pending state.
     		if(CollectionConstants.PGI_AUTHORISATION_CODE_WAITINGFOR_PAY_GATEWAY_RESPONSE.equals(paymentResponse.getAuthStatus())){
     			EgwStatus paymentStatus = commonsService.getStatusByModuleAndCode(CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
     					CollectionConstants.ONLINEPAYMENT_STATUS_CODE_PENDING);
     			onlinePaymentReceiptHeader.getOnlinePayment().setStatus(paymentStatus);
     			onlinePaymentReceiptHeader.getOnlinePayment().setAuthorisationStatusCode(paymentResponse.getAuthStatus());
     	}
        
        else if (CollectionConstants.PGI_AUTHORISATION_CODE_SUCCESS.equals(paymentResponse.getAuthStatus())) {
            processSuccessMsg();
        } else {
            processFailureMsg();
        }

     	long elapsedTimeInMillis = System.currentTimeMillis() - startTimeInMilis;
     	LOGGER.info("$$$$$$ Receipt Persisted with Receipt Number: " + onlinePaymentReceiptHeader.getReceiptnumber()
				+ (onlinePaymentReceiptHeader.getConsumerCode() != null ? " and consumer code: " + onlinePaymentReceiptHeader.getConsumerCode() : "")
				+ "; Time taken(ms) = " + elapsedTimeInMillis);
    	
    	return RESULT;
    }

    // TO BE REMOVED ONCE THE TEST URL IS UP
    private Long testReceiptId;
    // TO BE REMOVED ONCE THE TEST URL IS UP
    private String testAuthStatusCode;

    // TO BE REMOVED ONCE THE TEST URL IS UP
    public Long getTestReceiptId() {
        return testReceiptId;
    }

    // TO BE REMOVED ONCE THE TEST URL IS UP
    public void setTestReceiptId(Long testReceiptId) {
        this.testReceiptId = testReceiptId;
    }

    // TO BE REMOVED ONCE THE TEST URL IS UP
    public String getTestAuthStatusCode() {
        return testAuthStatusCode;
    }

    // TO BE REMOVED ONCE THE TEST URL IS UP
    public void setTestAuthStatusCode(String testAuthStatusCode) {
        this.testAuthStatusCode = testAuthStatusCode;
    }

    /**
     * This method processes the failure message arriving from the payment
     * gateway.
     * 
     * The receipt and the online transaction are both cancelled.
     * 
     * The authorisation status for reason of failure is also persisted.
     * 
     * The reason for payment failure is displayed back to the user
     * 
     */
    @Transactional
    private void processFailureMsg() {

        EgwStatus receiptStatus = collectionsUtil
                .getReceiptStatusForCode(CollectionConstants.RECEIPT_STATUS_CODE_CANCELLED);
        onlinePaymentReceiptHeader.setStatus(receiptStatus);

        EgwStatus paymentStatus = commonsService.getStatusByModuleAndCode(
                CollectionConstants.MODULE_NAME_ONLINEPAYMENT, CollectionConstants.ONLINEPAYMENT_STATUS_CODE_FAILURE);
        onlinePaymentReceiptHeader.getOnlinePayment().setStatus(paymentStatus);

        onlinePaymentReceiptHeader.getOnlinePayment().setAuthorisationStatusCode(paymentResponse.getAuthStatus());

        receiptHeaderService.persist(onlinePaymentReceiptHeader);

        LOGGER.debug("Cancelled receipt after receiving failure message from the payment gateway");

        addActionError(getText(onlinePaymentReceiptHeader.getOnlinePayment().getService().getCode().toLowerCase()
                + ".pgi." + onlinePaymentReceiptHeader.getService().getCode().toLowerCase() + "."
                + paymentResponse.getAuthStatus()));
        receiptResponse = "FAILURE|NA";
    }

    /**
     * This method processes the success message arriving from the payment
     * gateway.
     * 
     * The receipt status is changed from PENDING to APPROVED and the online
     * transaction status is changed from PENDING to SUCCCESS.
     * 
     * The authorisation status for success(0300) for the online transaction is
     * also persisted.
     * 
     * An instrument of type 'ONLINE' is created with the transaction details
     * and are persisted along with the receipt details.
     * 
     * Voucher for the receipt is created and the Financial System is updated.
     * 
     * The billing system is updated about the receipt creation.
     * 
     * In case update to financial systems/billing system fails, the receipt
     * creation is rolled back and the receipt/payment status continues to be in
     * PENDING state ( and will be reconciled manually).
     * 
     */
    @Transactional
    private void processSuccessMsg() {
        errors.clear();
        
        // If receipt is already present in system, returns the existing receiptNumber.
        
        if(onlinePaymentReceiptHeader.getReceiptnumber() != null && onlinePaymentReceiptHeader.getReceiptnumber().length()>0){
        	receiptResponse = "SUCCESS|" + onlinePaymentReceiptHeader.getReceiptnumber();
        } else {

        createSuccessPayment(onlinePaymentReceiptHeader, paymentResponse.getTxnDate(), paymentResponse
                .getTxnReferenceNo(), paymentResponse.getTxnAmount(), paymentResponse.getAuthStatus(), null);

        LOGGER.debug("Persisted receipt after receiving success message from the payment gateway");

        boolean updateToSystems = true;

        try {
            receiptHeaderService.createVoucherForReceipt(onlinePaymentReceiptHeader, Boolean.FALSE);
            LOGGER.debug("Updated financial systems and created voucher.");
        } catch (EGOVRuntimeException ex) {
            updateToSystems = false;
            errors
                    .add(new ValidationError(
                            "Receipt creation transaction rolled back as update to financial system failed. Payment is in PENDING state.",
                            "Receipt creation transaction rolled back as update to financial system failed. Payment is in PENDING state."));
            LOGGER.error("Update to financial systems failed");
        }

        try {
            HashSet<BillReceiptInfo> billReceipt = new HashSet<BillReceiptInfo>();
            billReceipt.add(new BillReceiptInfoImpl(onlinePaymentReceiptHeader));
           /* if (!receiptPayeeDetailsService.updateBillingSystem(onlinePaymentReceiptHeader.getService().getCode(),
                    billReceipt)) {
                updateToSystems = false;
            }*/
        } catch (EGOVRuntimeException ex) {
            // Receipt creation is rolled back, and payment continues to be in
            // PENDING state.
            errors.add(new ValidationError(getText(onlinePaymentReceiptHeader.getOnlinePayment().getService().getCode()
                    .toLowerCase()
                    + ".pgi."
                    + onlinePaymentReceiptHeader.getService().getCode().toLowerCase()
                    + ".billingsystemupdate.error"),
                    "Receipt creation transaction rolled back as update to billing system failed. "
                            + "Payment is in PENDING state. "
                            + "Please do not attempt another Online Credit Card transaction. " + "Request to contact "
                            + getText("reports.title.corporation_name") + " for collection of receipt."));
            LOGGER
                    .debug("Receipt creation rolled back as update to billing system failed. Payment is in PENDING state.");

            throw new ValidationException(errors);
        }

        if (updateToSystems) {
        	onlinePaymentReceiptHeader.setIsReconciled(true);
			receiptHeaderService.persist(onlinePaymentReceiptHeader);
            LOGGER.debug("Updated billing system : " + onlinePaymentReceiptHeader.getService().getName());
        } else {
            LOGGER.debug("Rolling back receipt creation transaction as update to billing system/financials failed.");
        }
        receiptResponse = "SUCCESS|" + onlinePaymentReceiptHeader.getReceiptnumber();
        }
    }

    private List<InstrumentHeader> createOnlineInstrument(Date transactionDate, String transactionId,
            BigDecimal transactionAmt) {
        InstrumentHeader onlineInstrumentHeader = new InstrumentHeader();
        List<InstrumentHeader> instrumentHeaderList = new ArrayList<InstrumentHeader>();
        onlineInstrumentHeader.setInstrumentType(financialsUtil.getInstrumentTypeByType(CollectionConstants.INSTRUMENTTYPE_ONLINE));

        onlineInstrumentHeader.setTransactionDate(transactionDate);
        onlineInstrumentHeader.setIsPayCheque(CollectionConstants.ZERO_INT);
        onlineInstrumentHeader.setTransactionNumber(transactionId);
        onlineInstrumentHeader.setInstrumentAmount(transactionAmt);

        instrumentHeaderList.add(onlineInstrumentHeader);

        instrumentHeaderList = receiptHeaderService.createInstrument(instrumentHeaderList);

        return instrumentHeaderList;
    }

    /**
     * 
     * 
     * @param receipts
     *            - list of receipts which have to be processed as successful
     *            payments. For payments created as a response from bill desk,
     *            size of the array will be 1.
     * 
     */
    @Transactional
    private void createSuccessPayment(ReceiptHeader receipt, Date transactionDate, String transactionId,
            BigDecimal transactionAmt, String authStatusCode, String remarks) {
        EgwStatus receiptStatus = collectionsUtil
                .getReceiptStatusForCode(CollectionConstants.RECEIPT_STATUS_CODE_APPROVED);
        receipt.setStatus(receiptStatus);

        receipt
                .setReceiptInstrument(new HashSet(
                        createOnlineInstrument(transactionDate, transactionId, transactionAmt)));

        //receiptPayeeDetailsService.setReceiptNumber(receipt);

        receipt.setIsReconciled(Boolean.FALSE);

        receipt.getOnlinePayment().setAuthorisationStatusCode(authStatusCode);
        receipt.getOnlinePayment().setTransactionNumber(transactionId);
        receipt.getOnlinePayment().setTransactionAmount(transactionAmt);
        receipt.getOnlinePayment().setTransactionDate(transactionDate);
        receipt.getOnlinePayment().setRemarks(remarks);

        // set online payment status as SUCCESS
        receipt.getOnlinePayment().setStatus(
                collectionsUtil.getEgwStatusForModuleAndCode(CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
                        CollectionConstants.ONLINEPAYMENT_STATUS_CODE_SUCCESS));

        receiptHeaderService.persist(receipt);
    }

    /**
     * This method is invoked for manually reconciling online payments.
     * 
     * If a payment is reconciled as a Success Payment, the receipt is created,
     * the receipt is marked as APPROVED , the payment is marked as SUCCESS, and
     * the voucher is created.
     * 
     * If a payment is reconciled as To Be Refunded or Refunded, the transaction
     * details are persisted, receipt is marked as FAILED and the payment is
     * marked as TO BE REFUNDED/REFUNDED respectively.
     * 
     * The billing system is updated about all the payments that have been
     * successful.
     * 
     * @return
     */
    @ValidationErrorPage(value = "reconresult")
    @Transactional
    public String reconcileOnlinePayment() {

        HashSet<BillReceiptInfo> billReceipts = new HashSet<BillReceiptInfo>();

        ReceiptHeader[] receipts = new ReceiptHeader[selectedReceipts.length];

        Date transDate = null;

        errors.clear();

        for (int i = 0; i < getSelectedReceipts().length; i++) {
            receipts[i] = receiptHeaderService.findById(selectedReceipts[i], false);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (getTransactionDate()[i] != null) {
                String vdt = getTransactionDate()[i];
                try {
                    transDate = sdf.parse(vdt);
                } catch (ParseException e) {
                    LOGGER.debug("Error occured while parsing date " + e.getMessage());
                }
            }

            if (getStatusCode()[i].equals(CollectionConstants.ONLINEPAYMENT_STATUS_CODE_SUCCESS)) {
                createSuccessPayment(receipts[i], transDate, getTransactionId()[i], receipts[i].getTotalAmount(), null,
                        getRemarks()[i]);

                LOGGER.debug("Manually reconciled a success online payment");

                try {
                    receiptHeaderService.createVoucherForReceipt(receipts[i], Boolean.FALSE);
                    LOGGER.debug("Updated financial systems and created voucher.");
                } catch (EGOVRuntimeException ex) {
                    errors.add(new ValidationError(
                            "Manual Reconciliation Rolled back as Voucher Creation Failed For Payment Reference ID : "
                                    + receipts[i].getId(),
                            "Manual Reconciliation Rolled back as Voucher Creation Failed For Payment Reference ID : "
                                    + receipts[i].getId()));
                    LOGGER.error("Update to financial systems failed");
                    throw new ValidationException(errors);
                }

                billReceipts.add(new BillReceiptInfoImpl(receipts[i]));
            }

            if (CollectionConstants.ONLINEPAYMENT_STATUS_CODE_TO_BE_REFUNDED.equals(getStatusCode()[i])
                    || CollectionConstants.ONLINEPAYMENT_STATUS_CODE_REFUNDED.equals(getStatusCode()[i])) {
                EgwStatus receiptStatus = collectionsUtil
                        .getReceiptStatusForCode(CollectionConstants.RECEIPT_STATUS_CODE_FAILED);
                receipts[i].setStatus(receiptStatus);

                receipts[i].getOnlinePayment().setTransactionNumber(getTransactionId()[i]);
                receipts[i].getOnlinePayment().setTransactionAmount(receipts[i].getTotalAmount());
                receipts[i].getOnlinePayment().setTransactionDate(transDate);
                receipts[i].getOnlinePayment().setRemarks(getRemarks()[i]);

                // set online payment status as TO BE REFUNDED/REFUNDED
                if (getStatusCode()[i].equals(CollectionConstants.ONLINEPAYMENT_STATUS_CODE_TO_BE_REFUNDED)) {
                    receipts[i].getOnlinePayment().setStatus(
                            collectionsUtil.getEgwStatusForModuleAndCode(CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
                                    CollectionConstants.ONLINEPAYMENT_STATUS_CODE_TO_BE_REFUNDED));
                } else {
                    receipts[i].getOnlinePayment().setStatus(
                            collectionsUtil.getEgwStatusForModuleAndCode(CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
                                    CollectionConstants.ONLINEPAYMENT_STATUS_CODE_REFUNDED));
                }

                receiptHeaderService.persist(receipts[i]);

                LOGGER.debug("Manually reconciled an online payment to " + getStatusCode()[i] + " state.");
            }
        }

        // update billing system about successfully created online receipt
        // payments.
        try {
            if (!billReceipts.isEmpty()) {
                //receiptPayeeDetailsService.updateBillingSystem(receipts[0].getService().getCode(), billReceipts);
                
              //Update IS_RECONCILED to true in EGCL_COLLECTIONHEADER
				for(ReceiptHeader receiptHeader:receipts){
					receiptHeader.setIsReconciled(true);
					receiptHeaderService.persist(receiptHeader);
					
				}
            }
        } catch (EGOVRuntimeException ex) {
            errors.add(new ValidationError("Manual Reconciliation of Online Payments Rolled back as "
                    + "update to billing system failed.", "Manual Reconciliation of Online Payments Rolled back as "
                    + "update to billing system failed."));
            LOGGER.error("Update to billing systems failed");

            throw new ValidationException(errors);
        }

        /*
         * if(!errors.isEmpty()){ throw new ValidationException(errors); }
         */
        return RECONRESULT;
    }

    public String view() {
        setReceipts(new ReceiptHeader[1]);
        receipts[0] = receiptHeaderService.findById(getReceiptId(), false);

        try {
            /*setReportId(collectionCommon.generateReport(receipts, getSession(), true));*/
        } catch (Exception e) {
            LOGGER.error(CollectionConstants.REPORT_GENERATION_ERROR, e);
            throw new EGOVRuntimeException(CollectionConstants.REPORT_GENERATION_ERROR, e);
        }
        return CollectionConstants.REPORT;
    }
    
    public String viewReceipt() {
		LOGGER.debug("::VIEWRECEIPT API:::  ServiceCode: " + getServiceCode() + ", Receipt Number=" + getReceiptNumber() + ", Consumer Code="
				+ getConsumerCode());
		ReceiptHeader receiptHeader = (ReceiptHeader) getPersistenceService().findByNamedQuery(
				CollectionConstants.QUERY_RECEIPT_BY_SERVICE_RECEIPTNUMBER_CONSUMERCODE, getServiceCode(), getReceiptNumber(), getReceiptNumber(),
				getConsumerCode());

		if (receiptHeader != null) {
			setReceiptId(receiptHeader.getId());
			return view();
		} else {
			throw new ValidationException(Arrays.asList(new ValidationError("No Receipt Data Found", "No Receipt Data Found")));
		}

	}

    @Override
    public void prepare() {
        super.prepare();
     
        // set user id of citizen to thread locals for base model
        session = request.getSession();
		
		User user = collectionsUtil.getUserByUserName(CollectionConstants.CITIZEN_USER_NAME);
		EgovThreadLocals.setUserId(user.getId());
		session.setAttribute(CollectionConstants.SESSION_VAR_LOGIN_USER_NAME, user.getUsername());
		
		// populates model when request is from the billing system
        if (StringUtils.isNotBlank(getCollectXML())) {
        	String decodedCollectXml = java.net.URLDecoder.decode(getCollectXML());
            try {
                collDetails = (BillInfoImpl) xmlHandler.toObject(decodedCollectXml);
               // modelPayeeList.clear();

                Fund fund = commonsService.fundByCode(collDetails.getFundCode());
                if (fund == null) {
                    addActionError(getText("billreceipt.improperbilldata.missingfund"));
                }

                Department dept = (Department) getPersistenceService().findByNamedQuery(
                        CollectionConstants.QUERY_DEPARTMENT_BY_CODE, collDetails.getDepartmentCode());
                if (dept == null) {
                    addActionError(getText("billreceipt.improperbilldata.missingdepartment"));
                }

                ServiceDetails service = (ServiceDetails) getPersistenceService().findByNamedQuery(
                        CollectionConstants.QUERY_SERVICE_BY_CODE, collDetails.getServiceCode());

                setServiceName(service.getName());
                setCollectionModesNotAllowed(collDetails.getCollectionModesNotAllowed());
                setOverrideAccountHeads(collDetails.getOverrideAccountHeadsAllowed());
                setPartPaymentAllowed(collDetails.getPartPaymentAllowed());
                setCallbackForApportioning(collDetails.getCallbackForApportioning());
                totalAmountToBeCollected = BigDecimal.valueOf(0);
                
                receiptHeader = collectionCommon.initialiseReceiptModelWithBillInfo(collDetails, fund, dept);
                
                totalAmountToBeCollected = totalAmountToBeCollected.add(receiptHeader
                        .getTotalAmountToBeCollected());
                for (ReceiptDetail rDetails : receiptHeader.getReceiptDetails()) {
                        rDetails.getCramountToBePaid().setScale(CollectionConstants.AMOUNT_PRECISION_DEFAULT,
                                        BigDecimal.ROUND_UP);
                }
                this.setReceiptDetailList(new ArrayList<ReceiptDetail>(receiptHeader.getReceiptDetails()));
            
                if(totalAmountToBeCollected.compareTo(BigDecimal.ZERO) == -1){
                    addActionError(getText("billreceipt.totalamountlessthanzero.error"));
                    LOGGER.info(getText("billreceipt.totalamountlessthanzero.error"));
                } else {
                        this.setTotalAmountToBeCollected(totalAmountToBeCollected.setScale(
                                        CollectionConstants.AMOUNT_PRECISION_DEFAULT, BigDecimal.ROUND_UP));
                }
                    
                /*modelPayeeList = collectionCommon.initialiseReceiptModelWithBillInfo(collDetails, fund, dept);

                for (ReceiptPayeeDetails payeeDetails : modelPayeeList) {
                    for (ReceiptHeader receiptHeader : payeeDetails.getReceiptHeaders()) {
                    	setTotalAmountToBeCollected(receiptHeader.getTotalAmountToBeCollected());
						this.setReceiptDetailList(new ArrayList<ReceiptDetail>(receiptHeader.getReceiptDetails()));
                    }
                }*/
            } catch (Exception e) {
                LOGGER.error(getText("billreceipt.error.improperbilldata") + e.getMessage());
                addActionError(getText("billreceipt.error.improperbilldata"));
            }
        }
        addDropdownData("paymentServiceList", getPersistenceService().findAllByNamedQuery(
                CollectionConstants.QUERY_SERVICES_BY_TYPE, CollectionConstants.SERVICE_TYPE_PAYMENT));
    }

    private void populateAndPersistReceipts() {
        ServiceDetails paymentService = (ServiceDetails) getPersistenceService().findByNamedQuery(
                CollectionConstants.QUERY_SERVICE_BY_ID, paymentServiceId.longValue());

       // for (ReceiptPayeeDetails payee : modelPayeeList) {
         //   for (ReceiptHeader receiptHeader : payee.getReceiptHeaders()) {

                // only newly created receipts need to be initialised with the
                // data.
                // The cancelled receipt can be excluded from this processing.
                if (receiptHeader.getStatus() == null) {
                    receiptHeader.setReceiptdate(new DateTime());;

                    receiptHeader.setReceipttype(CollectionConstants.RECEIPT_TYPE_BILL);
                    receiptHeader.setIsModifiable(Boolean.FALSE);
                    // recon flag should be set as false when the receipt is
                    // actually
                    // created on successful online transaction
                    receiptHeader.setIsReconciled(Boolean.TRUE);
                    receiptHeader.setCollectiontype(CollectionConstants.COLLECTION_TYPE_ONLINECOLLECTION);
                    receiptHeader.setStatus(commonsService.getStatusByModuleAndCode(
                            CollectionConstants.MODULE_NAME_RECEIPTHEADER,
                            CollectionConstants.RECEIPT_STATUS_CODE_PENDING));

                    setOnlineInstrumenttotal(getOnlineInstrumenttotal().add(this.getPaymentAmount()));

                    BigDecimal debitAmount = BigDecimal.ZERO;

                    for (ReceiptDetail creditChangeReceiptDetail : getReceiptDetailList()) {
                        // calculate sum of creditamounts as a debit value to
                        // create a
                        // debit account head and add to receipt details
                        debitAmount = debitAmount.add(creditChangeReceiptDetail.getCramount());
                        debitAmount = debitAmount.subtract(creditChangeReceiptDetail.getDramount());
                        
                        for (ReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
                            if (creditChangeReceiptDetail.getReceiptHeader().getReferencenumber().equals(
                                    receiptDetail.getReceiptHeader().getReferencenumber())
                                    && receiptDetail.getOrdernumber()
                                            .equals(creditChangeReceiptDetail.getOrdernumber())) {
                                receiptDetail.setCramount(creditChangeReceiptDetail.getCramount());
                            }
                        }
                    }
                    // end of outer for loop
                    receiptHeader.setTotalAmount(onlineInstrumenttotal);

                    receiptHeader.addReceiptDetail(collectionCommon.addDebitAccountHeadDetails(debitAmount,
                            receiptHeader, BigDecimal.ZERO, onlineInstrumenttotal,
                            CollectionConstants.INSTRUMENTTYPE_ONLINE));

                    // Add Online Payment Details
                    OnlinePayment onlinePayment = new OnlinePayment();

                    onlinePayment.setStatus(commonsService.getStatusByModuleAndCode(
                            CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
                            CollectionConstants.ONLINEPAYMENT_STATUS_CODE_PENDING));
                    onlinePayment.setReceiptHeader(receiptHeader);
                    onlinePayment.setService(paymentService);

                    receiptHeader.setOnlinePayment(onlinePayment);
                }
            //}
       // }// end of looping through receipt headers

        /**
         * Persist the receipt payee details which will internally persist all
         * the receipt headers
         */

       // receiptPayeeDetailsService.persistPendingReceipts(new HashSet<ReceiptPayeeDetails>(modelPayeeList));
    
    receiptHeaderService.persist(receiptHeader);
    receiptHeaderService.getSession().flush();

        /**
         * Construct Request Object For Bill Desk Payment Gateway
         * 
         */

       /* for (ReceiptPayeeDetails payee : modelPayeeList) {
            for (ReceiptHeader receiptHeader : payee.getReceiptHeaders()) {
                setPaymentRequest(collectionCommon.createPaymentRequest(paymentService, receiptHeader));
            }

        }*/

    }// end of method
    
    public void setCollectionsUtil(CollectionsUtil collectionsUtil) {
        this.collectionsUtil = collectionsUtil;
    }

    public void setReceiptHeaderService(ReceiptHeaderService receiptHeaderService) {
        this.receiptHeaderService = receiptHeaderService;
    }

    public void setCollectionCommon(CollectionCommon collectionCommon) {
        this.collectionCommon = collectionCommon;
    }

    public void setCommonsService(CommonsServiceImpl commonsService) {
        this.commonsService = commonsService;
    }

    private String[] transactionId;
    private Long[] selectedReceipts;
    private String[] transactionDate;
    private String[] statusCode;
    private String[] remarks;

    public String[] getRemarks() {
        return remarks;
    }

    public void setRemarks(String[] remarks) {
        this.remarks = remarks;
    }

    public void setTransactionId(String[] transactionId) {
        this.transactionId = transactionId;
    }

    public String[] getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String[] transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String[] getTransactionId() {
        return transactionId;
    }

    public Long[] getSelectedReceipts() {
        return selectedReceipts;
    }

    public void setSelectedReceipts(Long[] selectedReceipts) {
        this.selectedReceipts = selectedReceipts;
    }

    public String[] getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String[] statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public ReceiptHeader[] getReceipts() {
        return receipts;
    }

    public void setReceipts(ReceiptHeader[] receipts) {
        this.receipts = receipts;
    }

    /**
     * This getter will be invoked by framework from UI. It returns the total
     * number of bill accounts that are present in the XML arriving from the
     * billing system
     * 
     * @return
     */
    public Integer getTotalNoOfAccounts() {
        Integer totalNoOfAccounts = 0;
        /*for (ReceiptPayeeDetails payee : modelPayeeList) {
            for (ReceiptHeader header : payee.getReceiptHeaders()) {
                totalNoOfAccounts += header.getReceiptDetails().size();
            }
        }*/
        return totalNoOfAccounts;
    }

    /**
     * This getter will be invoked by framework from UI. It returns the total
     * amount of bill accounts that are present in the XML arriving from the
     * billing system
     * 
     * @return
     */
    public BigDecimal getTotalAmountToBeCollected() {
        return totalAmountToBeCollected;
    }

    public void setTotalAmountToBeCollected(BigDecimal totalAmountToBeCollected) {
        this.totalAmountToBeCollected = totalAmountToBeCollected;
    }

    /**
     * This getter will be invoked by the framework from UI. It returns the
     * amount payed by the citizen.
     * 
     * @return the paymentAmount
     */
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * @param paymentAmount
     *            the paymentAmount to set
     */
    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * @return the paymentRequest
     */
    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    /**
     * @param paymentRequest
     *            the paymentRequest to set
     */
    public void setPaymentRequest(PaymentRequest paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(PaymentResponse paymentResponse) {
        this.paymentResponse = paymentResponse;
    }

    /**
     * @return the paymentServiceId
     */
    public Integer getPaymentServiceId() {
        return paymentServiceId;
    }

    /**
     * @param paymentServiceId
     *            the paymentServiceId to set
     */
    public void setPaymentServiceId(Integer paymentServiceId) {
        this.paymentServiceId = paymentServiceId;
    }

    public String getMsg() {
        return responseMsg;
    }

    public void setMsg(String successMsg) {
        this.responseMsg = successMsg;
    }

    public void setFinancialsUtil(FinancialsUtil financialsUtil) {
        this.financialsUtil = financialsUtil;
    }

    public ReceiptHeader getOnlinePaymentReceiptHeader() {
        return onlinePaymentReceiptHeader;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the collectionModesNotAllowed
     */
    public List<String> getCollectionModesNotAllowed() {
        return collectionModesNotAllowed;
    }

    /**
     * @param collectionModesNotAllowed
     *            the collectionModesNotAllowed to set
     */
    public void setCollectionModesNotAllowed(List<String> collectionModesNotAllowed) {
        this.collectionModesNotAllowed = collectionModesNotAllowed;
    }

    /**
     * @return the overrideAccountHeads
     */
    public Boolean getOverrideAccountHeads() {
        return overrideAccountHeads;
    }

    /**
     * @param overrideAccountHeads
     *            the overrideAccountHeads to set
     */
    public void setOverrideAccountHeads(Boolean overrideAccountHeads) {
        this.overrideAccountHeads = overrideAccountHeads;
    }

    /**
     * @return the partPaymentAllowed
     */
    public Boolean getPartPaymentAllowed() {
        return partPaymentAllowed;
    }

    /**
     * @param partPaymentAllowed
     *            the partPaymentAllowed to set
     */
    public void setPartPaymentAllowed(Boolean partPaymentAllowed) {
        this.partPaymentAllowed = partPaymentAllowed;
    }

    /**
     * @param xmlHandler
     *            the xmlHandler to set
     */
    public void setXmlHandler(BillCollectXmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    /**
     * @return the collectXML
     */
    public String getCollectXML() {
        return collectXML;
    }

    /**
     * @param collectXML
     *            the collectXML to set
     */
    public void setCollectXML(String collectXML) {
        this.collectXML = collectXML;
    }

    /**
     * @return the receiptDetailList
     */
    public List<ReceiptDetail> getReceiptDetailList() {
        return receiptDetailList;
    }

    /**
     * @param receiptDetailList
     *            the receiptDetailList to set
     */
    public void setReceiptDetailList(List<ReceiptDetail> receiptDetailList) {
        this.receiptDetailList = receiptDetailList;
    }

   
    /**
     * @return the onlineInstrumenttotal
     */
    public BigDecimal getOnlineInstrumenttotal() {
        return onlineInstrumenttotal;
    }

    /**
     * @param onlineInstrumenttotal
     *            the onlineInstrumenttotal to set
     */
    public void setOnlineInstrumenttotal(BigDecimal onlineInstrumenttotal) {
        this.onlineInstrumenttotal = onlineInstrumenttotal;
    }

    private void apportionBillAmount() {
		receiptDetailList = collectionCommon.apportionBillAmount(paymentAmount, (ArrayList<ReceiptDetail>)this.getReceiptDetailList());
	}

    /**
     * @return the callbackForApportioning
     */
    public Boolean getCallbackForApportioning() {
        return callbackForApportioning;
    }

    /**
     * @param callbackForApportioning
     *            the callbackForApportioning to set
     */
    public void setCallbackForApportioning(Boolean callbackForApportioning) {
        this.callbackForApportioning = callbackForApportioning;
    }
    
    @Override
    public void setServletRequest(HttpServletRequest arg0) {
        this.request = arg0;
    }

	/**
	 * @return the receiptNumber
	 */
	public String getReceiptNumber() {
		return receiptNumber;
	}

	/**
	 * @param receiptNumber the receiptNumber to set
	 */
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	/**
	 * @return the consumerCode
	 */
	public String getConsumerCode() {
		return consumerCode;
	}

	/**
	 * @param consumerCode the consumerCode to set
	 */
	public void setConsumerCode(String consumerCode) {
		this.consumerCode = consumerCode;
	}

	/**
	 * @return the receiptResponse
	 */
	public String getReceiptResponse() {
		return receiptResponse;
	}

	/**
	 * @param receiptResponse the receiptResponse to set
	 */
	public void setReceiptResponse(String receiptResponse) {
		this.receiptResponse = receiptResponse;
	}
	
	
}