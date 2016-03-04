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
package org.egov.wtms.application.service.collection;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.egov.collection.entity.ReceiptDetail;
import org.egov.collection.integration.models.BillReceiptInfo;
import org.egov.collection.integration.models.BillReceiptInfoImpl;
import org.egov.collection.integration.models.ReceiptAccountInfo;
import org.egov.collection.integration.models.ReceiptAmountInfo;
import org.egov.demand.dao.EgBillDao;
import org.egov.demand.integration.TaxCollection;
import org.egov.demand.model.EgBill;
import org.egov.demand.model.EgBillDetails;
import org.egov.demand.model.EgDemand;
import org.egov.demand.model.EgDemandDetails;
import org.egov.demand.model.EgDemandReason;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.workflow.WorkFlowMatrix;
import org.egov.pims.commons.Position;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.repository.WaterConnectionDetailsRepository;
import org.egov.wtms.application.rest.CollectionApportioner;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.application.service.WaterConnectionSmsAndEmailService;
import org.egov.wtms.application.workflow.ApplicationWorkflowCustomDefaultImpl;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.utils.WaterTaxUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WaterTaxCollection extends TaxCollection {
    private static final Logger LOGGER = Logger.getLogger(WaterTaxCollection.class);
    @Autowired
    private EgBillDao egBillDAO;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private WaterConnectionDetailsService waterConnectionDetailsService;

    @Autowired
    private WaterConnectionDetailsRepository waterConnectionDetailsRepository;

    @Autowired
    private WaterTaxUtils waterTaxUtils;

    @Autowired
    private SimpleWorkflowService<WaterConnectionDetails> waterConnectionWorkflowService;

    @Autowired
    private WaterConnectionSmsAndEmailService waterConnectionSmsAndEmailService;

    @Autowired
    private ConnectionBillService connectionBillService;

    @PersistenceContext
    private EntityManager entityManager;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    @Transactional
    public void updateDemandDetails(final BillReceiptInfo billRcptInfo) throws ApplicationRuntimeException {
        final BigDecimal totalAmount = billRcptInfo.getTotalAmount();
        final EgDemand demand = getCurrentDemand(Long.valueOf(billRcptInfo.getBillReferenceNum()));
        final String indexNo = ((BillReceiptInfoImpl) billRcptInfo).getReceiptMisc().getReceiptHeader()
                .getConsumerCode();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("updateDemandDetails : Demand before proceeding : " + demand);
                LOGGER.debug("updateDemandDetails : collection back update started for property : " + indexNo
                        + " and receipt event is " + billRcptInfo.getEvent() + ". Total Receipt amount is." + totalAmount
                        + " with receipt no." + billRcptInfo.getReceiptNum());
            }

            if (billRcptInfo.getEvent().equals(EVENT_RECEIPT_CREATED)) {
                updateCollForRcptCreate(demand, billRcptInfo, totalAmount);
                updateWaterConnectionDetails(demand);
                updateWaterTaxIndexes(demand);
            } else if (billRcptInfo.getEvent().equals(EVENT_RECEIPT_CANCELLED)) {
                updateCollectionForRcptCancel(demand, billRcptInfo);
                updateWaterConnDetailsStatus(demand, billRcptInfo);
                updateWaterTaxIndexes(demand);
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("updateDemandDetails : Demand after processed : " + demand);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApplicationRuntimeException("Error occured during back update of DCB : " + e.getMessage(), e);
        }
    }

    /**
     * @param demand Updates WaterConnectionDetails Object once Collection Is done. send Record move to Commissioner and Send SMS
     * and Email after Collection
     */
    @Transactional
    public void updateWaterConnectionDetails(final EgDemand demand) {
        final WaterConnectionDetails waterConnectionDetails = waterConnectionDetailsService
                .getWaterConnectionDetailsByDemand(demand);
        if (!waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE)) {
            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_FEEPAID, WaterTaxConstants.MODULETYPE));
            Long approvalPosition = Long.valueOf(0);
            final WorkFlowMatrix wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails
                    .getStateType(), null, null, WaterTaxConstants.NEW_CONNECTION_MATRIX_ADDL_RULE,
                    waterConnectionDetails.getCurrentState().getValue(), null);
            final Position posobj = waterTaxUtils.getCityLevelCommissionerPosition(wfmatrix.getNextDesignation());
            if (posobj != null)
                approvalPosition = posobj.getId();
            final ApplicationWorkflowCustomDefaultImpl applicationWorkflowCustomDefaultImpl = waterConnectionDetailsService
                    .getInitialisedWorkFlowBean();
            applicationWorkflowCustomDefaultImpl.createCommonWorkflowTransition(waterConnectionDetails,
                    approvalPosition, WaterTaxConstants.FEE_COLLECTION_COMMENT,
                    WaterTaxConstants.NEW_CONNECTION_MATRIX_ADDL_RULE, null);
            waterConnectionSmsAndEmailService.sendSmsAndEmail(waterConnectionDetails, null);
            waterConnectionDetailsRepository.saveAndFlush(waterConnectionDetails);
        }
    }

    @Transactional
    private void updateCollForRcptCreate(final EgDemand demand, final BillReceiptInfo billRcptInfo,
            final BigDecimal totalAmount) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("updateCollForRcptCreate : Updating Collection Started For Demand : " + demand
                    + " with BillReceiptInfo - " + billRcptInfo);
        try {
            updateDemandDetailForReceiptCreate(billRcptInfo.getAccountDetails(), demand, billRcptInfo, totalAmount);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApplicationRuntimeException(
                    "Error occured during back update of DCB : updateCollForRcptCreate() " + e.getMessage(), e);
        }
    }

    @Transactional
    private void updateDemandDetailForReceiptCreate(final Set<ReceiptAccountInfo> accountDetails,
            final EgDemand demand, final BillReceiptInfo billRcptInfo, final BigDecimal totalAmount) {

        final StringBuilder query = new StringBuilder(
                "select dmdet FROM EgDemandDetails dmdet left join fetch dmdet.egDemandReason dmdRsn ")
        .append("left join fetch dmdRsn.egDemandReasonMaster dmdRsnMstr left join fetch dmdRsn.egInstallmentMaster installment ")
        .append("WHERE dmdet.egDemand.id = :demand");
        final List<EgDemandDetails> demandDetailList = getCurrentSession().createQuery(query.toString())
                .setLong("demand", demand.getId()).list();

        final Map<String, Map<String, EgDemandDetails>> installmentWiseDemandDetailsByReason = new HashMap<String, Map<String, EgDemandDetails>>();
        Map<String, EgDemandDetails> demandDetailByReason = null;

        EgDemandReason dmdRsn = null;
        String installmentDesc = null;

        for (final EgDemandDetails dmdDtls : demandDetailList)
            if (dmdDtls.getAmount().compareTo(BigDecimal.ZERO) > 0) {

                dmdRsn = dmdDtls.getEgDemandReason();
                installmentDesc = dmdRsn.getEgInstallmentMaster().getDescription();
                demandDetailByReason = new HashMap<String, EgDemandDetails>(0);
                if (installmentWiseDemandDetailsByReason.get(installmentDesc) == null) {
                    demandDetailByReason.put(dmdRsn.getEgDemandReasonMaster().getReasonMaster(), dmdDtls);
                    installmentWiseDemandDetailsByReason.put(installmentDesc, demandDetailByReason);
                } else
                    installmentWiseDemandDetailsByReason.get(installmentDesc).put(
                            dmdRsn.getEgDemandReasonMaster().getReasonMaster(), dmdDtls);
            } else if (LOGGER.isDebugEnabled())
                LOGGER.debug("saveCollectionDetails - demand detail amount is zero " + dmdDtls);

        EgDemandDetails demandDetail = null;

        for (final ReceiptAccountInfo rcptAccInfo : accountDetails)
            if (rcptAccInfo.getDescription() != null && !rcptAccInfo.getDescription().isEmpty())
                if (rcptAccInfo.getCrAmount() != null && rcptAccInfo.getCrAmount().compareTo(BigDecimal.ZERO) == 1) {
                    final String[] desc = rcptAccInfo.getDescription().split("-", 2);
                    final String[] installsplit = desc[1].split("#");
                    final String reason = desc[0].trim();
                    final String instDesc = installsplit[0].trim();
                    demandDetail = installmentWiseDemandDetailsByReason.get(instDesc).get(reason);
                    demandDetail.addCollectedWithOnePaisaTolerance(rcptAccInfo.getCrAmount());
                    if (demandDetail.getEgDemandReason().getEgDemandReasonMaster().getIsDemand())
                        demand.addCollected(rcptAccInfo.getCrAmount());
                    persistCollectedReceipts(demandDetail, billRcptInfo.getReceiptNum(), totalAmount,
                            billRcptInfo.getReceiptDate(), demandDetail.getAmtCollected());
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Persisted demand and receipt details for tax : " + reason + " installment : "
                                + instDesc + " with receipt No : " + billRcptInfo.getReceiptNum() + " for Rs. "
                                + rcptAccInfo.getCrAmount());
                }

    }

    @Override
    protected Module module() {
        return moduleService.getModuleByName(WaterTaxConstants.EGMODULE_NAME);
    }

    public EgDemand getCurrentDemand(final Long billId) {
        final EgBill egBill = egBillDAO.findById(billId, false);
        return egBill.getEgDemand();
    }

    // Receipt cancellation ,updating bill,demanddetails,demand
    @Transactional
    private void updateCollectionForRcptCancel(final EgDemand demand, final BillReceiptInfo billRcptInfo) {
        LOGGER.debug("reconcileCollForRcptCancel : Updating Collection Started For Demand : " + demand
                + " with BillReceiptInfo - " + billRcptInfo);
        try {
            cancelBill(Long.valueOf(billRcptInfo.getBillReferenceNum()));
            updateDmdDetForRcptCancel(demand, billRcptInfo);
            LOGGER.debug("reconcileCollForRcptCancel : Updating Collection finished For Demand : " + demand);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApplicationRuntimeException("Error occured during back update of DCB : " + e.getMessage(), e);
        }
    }

    @Transactional
    private void cancelBill(final Long billId) {
        if (billId != null) {
            final EgBill egBill = egBillDAO.findById(billId, false);
            egBill.setIs_Cancelled("Y");
        }
    }

    @Transactional
    private void updateDmdDetForRcptCancel(final EgDemand demand, final BillReceiptInfo billRcptInfo) {
        LOGGER.debug("Entering method updateDmdDetForRcptCancel");
        String installment = "";
        for (final ReceiptAccountInfo rcptAccInfo : billRcptInfo.getAccountDetails())
            if (rcptAccInfo.getCrAmount() != null && rcptAccInfo.getCrAmount().compareTo(BigDecimal.ZERO) == 1
            && !rcptAccInfo.getIsRevenueAccount()) {
                final String[] desc = rcptAccInfo.getDescription().split("-", 2);
                final String reason = desc[0].trim();
                final String[] installsplit = desc[1].split("#");
                installment = installsplit[0].trim();

                for (final EgDemandDetails demandDetail : demand.getEgDemandDetails())
                    if (reason.equalsIgnoreCase(demandDetail.getEgDemandReason().getEgDemandReasonMaster()
                            .getReasonMaster())
                            && installment.equalsIgnoreCase(demandDetail.getEgDemandReason().getEgInstallmentMaster()
                                    .getDescription())) {
                        if (demandDetail.getAmtCollected().compareTo(rcptAccInfo.getCrAmount()) < 0)
                            throw new ApplicationRuntimeException(
                                    "updateDmdDetForRcptCancel : Exception while updating cancel receipt, "
                                            + "to be deducted amount " + rcptAccInfo.getCrAmount()
                                            + " is greater than the collected amount " + demandDetail.getAmtCollected()
                                            + " for demandDetail " + demandDetail);

                        demandDetail
                        .setAmtCollected(demandDetail.getAmtCollected().subtract(rcptAccInfo.getCrAmount()));
                        if (demand.getAmtCollected() != null && demand.getAmtCollected().compareTo(BigDecimal.ZERO) > 0
                                && demandDetail.getEgDemandReason().getEgDemandReasonMaster().getIsDemand())
                            demand.setAmtCollected(demand.getAmtCollected().subtract(rcptAccInfo.getCrAmount()));

                        LOGGER.info("Deducted Collected amount Rs." + rcptAccInfo.getCrAmount() + " for tax : "
                                + reason + " and installment : " + installment);
                    }
            }
        updateReceiptStatusWhenCancelled(billRcptInfo.getReceiptNum());
        LOGGER.debug("Exiting method updateDmdDetForRcptCancel");
    }

    @Transactional
    private void updateWaterConnDetailsStatus(final EgDemand demand, final BillReceiptInfo billRcptInfo) {
        final WaterConnectionDetails waterConnectionDetails = waterConnectionDetailsService
                .getWaterConnectionDetailsByDemand(demand);
        StateHistory stateHistory = null;
        if (waterConnectionDetails.getStatus().getCode().equalsIgnoreCase(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)) {
            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN, WaterTaxConstants.MODULETYPE));
            Long approvalPosition = Long.valueOf(0);
            if (!waterConnectionDetails.getStateHistory().isEmpty() && waterConnectionDetails.getStateHistory() != null)
                Collections.reverse(waterConnectionDetails.getStateHistory());
            stateHistory = waterConnectionDetails.getStateHistory().get(0);
            final Position owner = stateHistory.getOwnerPosition();
            if (owner != null)
                approvalPosition = owner.getId();
            final ApplicationWorkflowCustomDefaultImpl applicationWorkflowCustomDefaultImpl = waterConnectionDetailsService
                    .getInitialisedWorkFlowBean();
            applicationWorkflowCustomDefaultImpl.createCommonWorkflowTransition(waterConnectionDetails,
                    approvalPosition, "Receipt Cancelled", WaterTaxConstants.NEW_CONNECTION_MATRIX_ADDL_RULE, null);
        }

    }

    private void updateWaterTaxIndexes(final EgDemand demand) {
        final WaterConnectionDetails waterConnectionDetails = waterConnectionDetailsService
                .getWaterConnectionDetailsByDemand(demand);
        final String sourceChannel = null;
        waterConnectionDetailsService.updateIndexes(waterConnectionDetails, sourceChannel);
    }

    @Override
    @Transactional
    public void apportionCollection(final String billRefNo, final BigDecimal amtPaid,
            final List<ReceiptDetail> receiptDetails) {
        final CollectionApportioner apportioner = new CollectionApportioner();
        final Map<String, BigDecimal> instDemand = getInstDemand(receiptDetails);
        apportioner.apportion(amtPaid, receiptDetails, instDemand);
    }

    public Map<String, BigDecimal> getInstDemand(final List<ReceiptDetail> receiptDetails) {
        final Map<String, BigDecimal> retMap = new HashMap<String, BigDecimal>();
        String installment = "";
        String[] desc;

        for (final ReceiptDetail rd : receiptDetails) {
            final String glCode = rd.getAccounthead().getGlcode();
            installment = "";
            desc = rd.getDescription().split("-", 2);
            final String[] installsplit = desc[1].split("#");
            installment = installsplit[0].trim();

            if (WaterTaxConstants.GLCODEMAP_FOR_CURRENTTAX.containsValue(glCode))
                if (retMap.get(installment) == null)
                    retMap.put(installment, rd.getCramountToBePaid());
                else
                    retMap.put(installment, retMap.get(installment).add(rd.getCramountToBePaid()));
            if (WaterTaxConstants.GLCODES_FOR_CURRENTTAX.contains(glCode))
                prepareTaxMap(retMap, installment, rd, "FULLTAX");
        }
        return retMap;
    }

    /**
     * @param retMap
     * @param installment
     * @param rd
     */
    private void prepareTaxMap(final Map<String, BigDecimal> retMap, final String installment, final ReceiptDetail rd,
            final String type) {
        if (retMap.get(installment + type) == null)
            retMap.put(installment + type, rd.getCramountToBePaid());
        else
            retMap.put(installment + type, retMap.get(installment + type).add(rd.getCramountToBePaid()));
    }

    @Override
    public List<ReceiptDetail> reconstructReceiptDetail(final String billReferenceNumber, final BigDecimal actualAmountPaid,
            final List<ReceiptDetail> receiptDetailList) {
        final Long billID = Long.valueOf(billReferenceNumber);
        final List<EgBillDetails> billDetails = new ArrayList<EgBillDetails>(0);
        final EgBill bill = connectionBillService.updateBillWithLatest(billID);
        LOGGER.debug("Reconstruct consumer code :" + bill.getConsumerId() + ", with bill reference number: "
                + billReferenceNumber + ", for Amount Paid :"
                + actualAmountPaid);
        final CollectionApportioner apportioner = new CollectionApportioner();
        billDetails.addAll(bill.getEgBillDetails());
        return apportioner.reConstruct(actualAmountPaid, billDetails);
    }

    @Override
    public String constructAdditionalInfoForReceipt(final BillReceiptInfo billReceiptInfo) {
        StringBuilder additionalInfo = new StringBuilder("Paid From");
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        final EgBill egBill = egBillDAO.findById(Long.valueOf(billReceiptInfo.getBillReferenceNum()), false);
        final BigDecimal amounttobeCalc = egBill.getTotalAmount().subtract(egBill.getTotalCollectedAmount());
        final List<EgBillDetails> billdEtList = new ArrayList<EgBillDetails>(egBill.getEgBillDetails());
        for (final EgBillDetails billDet : egBill.getEgBillDetails()) {
            if (billDet.getOrderNo() == 1) {
                additionalInfo.append(" ").append(
                        formatter.format(billDet.getEgDemandReason().getEgInstallmentMaster().getFromDate())).append(" To ");
                if (billdEtList.size() == 1) {
                    additionalInfo.append(formatter.format(billDet.getEgDemandReason().getEgInstallmentMaster().getToDate()));
                    break;
                }

            }
            if (billdEtList.size() > 1
                    && billdEtList.get(billdEtList.size() - 1).getOrderNo().equals(billDet.getOrderNo())) {
                additionalInfo.append(formatter.format(billDet.getEgDemandReason().getEgInstallmentMaster().getToDate()));
                break;
            }
        }
        if (amounttobeCalc.compareTo(BigDecimal.ZERO) == 1)
            additionalInfo = additionalInfo.append(" (Partialy)");

        return additionalInfo.toString();
    }

    @Override
    public ReceiptAmountInfo receiptAmountBifurcation(final BillReceiptInfo billReceiptInfo) {
        final ReceiptAmountInfo receiptAmountInfo = new ReceiptAmountInfo();
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        final EgBill egBill = egBillDAO.findById(Long.valueOf(billReceiptInfo.getBillReferenceNum()), false);
        final List<EgBillDetails> billDetails = new ArrayList<EgBillDetails>(egBill.getEgBillDetails());

        BigDecimal currentInstallmentAmount = BigDecimal.ZERO;
        BigDecimal arrearAmount = BigDecimal.ZERO;

        for (final EgBillDetails billDet : egBill.getEgBillDetails())
            if (billDet.getCrAmount() != null && billDet.getCrAmount().compareTo(BigDecimal.ZERO) == 1) {
                final String[] desc = billDet.getDescription().split("-", 2);
                final String[] installsplit = desc[1].split("#");
                if (installsplit[0].trim().equals(installsplit[1].trim()))
                    currentInstallmentAmount = currentInstallmentAmount.add(billDet.getCrAmount());
                else
                    arrearAmount = arrearAmount.add(billDet.getCrAmount());

            }

        for (final EgBillDetails billDet : egBill.getEgBillDetails()) {
            if (billDet.getOrderNo() == 1) {
                receiptAmountInfo.setInstallmentFrom(formatter.format(billDet.getEgDemandReason().getEgInstallmentMaster()
                        .getFromDate()));
                if (billDetails.size() == 1) {
                    receiptAmountInfo.setInstallmentTo(formatter.format(billDet.getEgDemandReason().getEgInstallmentMaster()
                            .getToDate()));
                    break;
                }

            }
            if (billDetails.size() > 1
                    && billDetails.get(billDetails.size() - 1).getOrderNo().equals(billDet.getOrderNo())) {
                receiptAmountInfo.setInstallmentTo(formatter.format(billDet.getEgDemandReason().getEgInstallmentMaster()
                        .getToDate()));
                break;
            }
        }
        receiptAmountInfo.setArrearsAmount(arrearAmount);
        receiptAmountInfo.setCurrentInstallmentAmount(currentInstallmentAmount);
        return receiptAmountInfo;
    }

}