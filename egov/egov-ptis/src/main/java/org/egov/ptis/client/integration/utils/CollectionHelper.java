/*******************************************************************************
 * eGov suite of products aim to improve the internal efficiency,transparency, 
 *    accountability and the service delivery of the government  organizations.
 * 
 *     Copyright (C) <2015>  eGovernments Foundation
 * 
 *     The updated version of eGov suite of products as by eGovernments Foundation 
 *     is available at http://www.egovernments.org
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or 
 *     http://www.gnu.org/licenses/gpl.html .
 * 
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 * 
 * 	1) All versions of this program, verbatim or modified must carry this 
 * 	   Legal Notice.
 * 
 * 	2) Any misrepresentation of the origin of the material is prohibited. It 
 * 	   is required that all modified versions of this material be marked in 
 * 	   reasonable ways as different from the original version.
 * 
 * 	3) This license does not grant any rights to any user of the program 
 * 	   with regards to rights under trademark law for use of the trade names 
 * 	   or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org
 ******************************************************************************/
package org.egov.ptis.client.integration.utils;

import static org.egov.ptis.constants.PropertyTaxConstants.PTIS_COLLECTION_SERVICE_CODE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.collection.entity.ReceiptDetail;
import org.egov.collection.integration.models.BillAccountDetails;
import org.egov.collection.integration.models.BillDetails;
import org.egov.collection.integration.models.BillInfo.COLLECTIONTYPE;
import org.egov.collection.integration.models.BillInfoImpl;
import org.egov.collection.integration.models.BillPayeeDetails;
import org.egov.collection.integration.models.BillReceiptInfo;
import org.egov.collection.integration.models.PaymentInfo;
import org.egov.collection.integration.models.PaymentInfo.TYPE;
import org.egov.collection.integration.models.PaymentInfoCard;
import org.egov.collection.integration.models.PaymentInfoCash;
import org.egov.collection.integration.models.PaymentInfoChequeDD;
import org.egov.collection.integration.services.CollectionIntegrationService;
import org.egov.commons.CChartOfAccounts;
import org.egov.dcb.bean.CashPayment;
import org.egov.dcb.bean.ChequePayment;
import org.egov.dcb.bean.CreditCardPayment;
import org.egov.dcb.bean.DDPayment;
import org.egov.dcb.bean.Payment;
import org.egov.dcb.service.EgovSpringBeanDefinition;
import org.egov.demand.model.EgBill;
import org.egov.demand.model.EgBillDetails;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.service.collection.PropertyTaxCollection;
import org.hibernate.FlushMode;

/**
 * Performs collections operations: (1) Fetch the details of a given receipt;
 * (2) Execute a collection for a particular bill and amount.; (3) Search for
 * existing payment ref no.
 */
public class CollectionHelper {
	private static final Logger LOG = Logger.getLogger(CollectionHelper.class);
	private EgBill bill;
	private CollectionIntegrationService collectionService = EgovSpringBeanDefinition.getCollectionIntegrationService();

	/**
	 * Use this constructor when you're only interested in getting the details
	 * of a receipt.
	 */
	public CollectionHelper() {
	}

	/**
	 * Use this constructor when you're doing a collection.
	 * 
	 * @param bill
	 */
	public CollectionHelper(EgBill bill) {
		this.bill = bill;
	}

	/**
	 * Executes a collection.
	 * 
	 * @param payment
	 * @return
	 */
	public BillReceiptInfo executeCollection(Payment payment) {
		if (!isCollectionPermitted()) {
			throw new EGOVRuntimeException(
					"Collection is not allowed - current balance is zero and advance coll exists.");
		}

		List<PaymentInfo> paymentInfoList = preparePaymentInfo(payment);
		BillInfoImpl billInfo = null;

		LOG.debug("CollectionHelper.executeCollection(): collection is from the field...");
		billInfo = prepareBillInfo(payment.getAmount(), COLLECTIONTYPE.F);

		return collectionService.createReceipt(billInfo, paymentInfoList);
	}

	public BillReceiptInfo generateMiscReceipt(Payment payment) {
		if (!isCollectionPermitted()) {
			throw new EGOVRuntimeException(
					"Collection is not allowed - current balance is zero and advance coll exists.");
		}
		List<PaymentInfo> paymentInfoList = preparePaymentInfo(payment);
		BillInfoImpl billInfo = prepareBillInfo(payment.getAmount(), COLLECTIONTYPE.C);
		return collectionService.createMiscellaneousReceipt(billInfo, paymentInfoList);
	}
	
	/**
	 * Fetches the details of a given receipt number.
	 * 
	 * @param receiptNumber
	 * @return
	 */
	public BillReceiptInfo getReceiptInfo(String receiptNumber) {
		preventSessionSaveOrUpdate();
		return collectionService.getReceiptInfo(PTIS_COLLECTION_SERVICE_CODE, receiptNumber);
	}

	private List<PaymentInfo> preparePaymentInfo(Payment payment) {
		List<PaymentInfo> paymentInfoList = new ArrayList<PaymentInfo>();
		PaymentInfo paymentInfo = null;
		if (payment != null) {

			if (payment instanceof ChequePayment) {
				ChequePayment chequePayment = (ChequePayment) payment;
				paymentInfo = new PaymentInfoChequeDD(chequePayment.getBankId(), chequePayment.getBranchName(),
						chequePayment.getInstrumentDate(), chequePayment.getInstrumentNumber(), TYPE.cheque,
						payment.getAmount());

			} else if (payment instanceof DDPayment) {
				DDPayment chequePayment = (DDPayment) payment;
				paymentInfo = new PaymentInfoChequeDD(chequePayment.getBankId(), chequePayment.getBranchName(),
						chequePayment.getInstrumentDate(), chequePayment.getInstrumentNumber(), TYPE.dd,
						payment.getAmount());

			} else if (payment instanceof CreditCardPayment) {
				paymentInfo = prepareCardPaymentInfo((CreditCardPayment) payment, new PaymentInfoCard());

			} else if (payment instanceof CashPayment) {
				paymentInfo = new PaymentInfoCash(payment.getAmount());
			}
		}
		paymentInfoList.add(paymentInfo);
		return paymentInfoList;
	}

	/**
	 * Apportions the paid amount amongst the appropriate GL codes and returns
	 * the collections object that can be sent to the collections API for
	 * processing.
	 * 
	 * @param bill
	 * @param amountPaid
	 * @return
	 */
	private BillInfoImpl prepareBillInfo(BigDecimal amountPaid, COLLECTIONTYPE collType) {
		BillInfoImpl billInfoImpl = initialiseFromBill(amountPaid, collType);

		ArrayList<ReceiptDetail> receiptDetails = new ArrayList<ReceiptDetail>();
		List<EgBillDetails> billDetails = new ArrayList<EgBillDetails>(bill.getEgBillDetails());
		Collections.sort(billDetails);

		for (EgBillDetails billDet : billDetails) {
			receiptDetails.add(initReceiptDetail(billDet.getGlcode(), BigDecimal.ZERO, // billDet.getCrAmount(),
					billDet.getCrAmount(), billDet.getDrAmount(), billDet.getDescription()));
		}

		new PropertyTaxCollection().apportionPaidAmount(String.valueOf(bill.getId()), amountPaid, receiptDetails);

		for (EgBillDetails billDet : bill.getEgBillDetails()) {
			for (ReceiptDetail rd : receiptDetails) {
				//FIX ME
				if ((billDet.getGlcode().equals(rd.getAccounthead().getGlcode()))
						&& (billDet.getDescription().equals(rd.getDescription()))) {
					BillAccountDetails billAccDetails = new BillAccountDetails(billDet.getGlcode(),
							billDet.getOrderNo(), rd.getCramount(), rd.getDramount(), billDet.getFunctionCode(),
							billDet.getDescription(),null /*billDet.getAdditionalFlag()*/);
					billInfoImpl.getPayees().get(0).getBillDetails().get(0).addBillAccountDetails(billAccDetails);
					break;
				}
			}
		}
		return billInfoImpl;
	}

	/**
	 * Populates a BillInfo object from the bill -- the GL codes, descripion and
	 * dr/cr amounts.
	 * 
	 * @param bill
	 * @return
	 */
	private BillInfoImpl initialiseFromBill(BigDecimal amountPaid, COLLECTIONTYPE collType) {
		BillInfoImpl billInfoImpl = null;
		BillPayeeDetails billPayeeDet = null;
		List<BillPayeeDetails> billPayeeDetList = new ArrayList<BillPayeeDetails>();
		List<String> collModesList = new ArrayList<String>();
		String[] collModes = bill.getCollModesNotAllowed().split(",");
		for (String coll : collModes) {
			collModesList.add(coll);
		}
		billInfoImpl = new BillInfoImpl(bill.getServiceCode(), bill.getFundCode(), bill.getFunctionaryCode(),
				bill.getFundSourceCode(), bill.getDepartmentCode(), "Property Tax collection", bill.getCitizenName(),
				bill.getPartPaymentAllowed(), bill.getOverrideAccountHeadsAllowed(), collModesList, collType);
		billPayeeDet = new BillPayeeDetails(bill.getCitizenName(), bill.getCitizenAddress());

		BillDetails billDetails = new BillDetails(bill.getId().toString(), bill.getCreateDate(),
				bill.getConsumerId(), bill.getBoundaryNum().toString(), bill.getBoundaryType(), bill.getDescription(),
				amountPaid, // the actual amount paid, which might include
							// advances
				bill.getMinAmtPayable());
		billPayeeDet.addBillDetails(billDetails);
		billPayeeDetList.add(billPayeeDet);
		billInfoImpl.setPayees(billPayeeDetList);
		return billInfoImpl;
	}

	private ReceiptDetail initReceiptDetail(String glCode, BigDecimal crAmount, BigDecimal crAmountToBePaid,
			BigDecimal drAmount, String description) {
		ReceiptDetail receiptDetail = new ReceiptDetail();
		CChartOfAccounts accountHead = new CChartOfAccounts();
		accountHead.setGlcode(glCode);
		receiptDetail.setAccounthead(accountHead);
		receiptDetail.setDescription(description);
		receiptDetail.setCramount(crAmount);
		receiptDetail.setCramountToBePaid(crAmountToBePaid);
		receiptDetail.setDramount(drAmount);
		return receiptDetail;
	}

	private PaymentInfoCard prepareCardPaymentInfo(CreditCardPayment cardPayment, PaymentInfoCard paymentInfoCard) {
		paymentInfoCard.setInstrumentNumber(cardPayment.getCreditCardNo());
		paymentInfoCard.setInstrumentAmount(cardPayment.getAmount());
		paymentInfoCard.setExpMonth(cardPayment.getExpMonth());
		paymentInfoCard.setExpYear(cardPayment.getExpYear());
		paymentInfoCard.setCvvNumber(cardPayment.getCvv());
		paymentInfoCard.setCardTypeValue(cardPayment.getCardType());
		paymentInfoCard.setTransactionNumber(cardPayment.getTransactionNumber());
		return paymentInfoCard;
	}

	private boolean isCollectionPermitted() {
		boolean allowed = thereIsCurrentBalanceToBePaid();
		LOG.debug("isCollectionPermitted() returned: " + allowed);
		return allowed;
	}

	private boolean thereIsCurrentBalanceToBePaid() {
		boolean result = false;
		BigDecimal currentBal = BigDecimal.ZERO;
		for (Map.Entry<String, String> entry : PropertyTaxConstants.GLCODEMAP_FOR_CURRENTTAX.entrySet()) {
			currentBal = currentBal.add(bill.balanceForGLCode(entry.getValue()));
		}
		if (currentBal != null && currentBal.compareTo(BigDecimal.ZERO) > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * Hibernate was firing a saveOrUpdate() for some reason, which was then
	 * bombing because the userID was not there in EgovThreadlocals. Making the
	 * flush mode MANUAL prevents hibernate from doing this.
	 */
	private void preventSessionSaveOrUpdate() {
		HibernateUtil.getCurrentSession().setFlushMode(FlushMode.MANUAL);
	}

	EgBill getBill() {
		return bill;
	}

	void setBill(EgBill bill) {
		this.bill = bill;
	}

}
