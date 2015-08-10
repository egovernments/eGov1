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
package org.egov.wtms.application.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.Installment;
import org.egov.commons.dao.InstallmentDao;
import org.egov.demand.dao.EgBillDao;
import org.egov.demand.model.EgBillType;
import org.egov.demand.model.EgDemand;
import org.egov.demand.model.EgDemandDetails;
import org.egov.demand.model.EgDemandReason;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infstr.beanfactory.ApplicationContextBeanProvider;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.wtms.application.entity.FieldInspectionDetails;
import org.egov.wtms.application.entity.WaterConnection;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.repository.WaterConnectionDetailsRepository;
import org.egov.wtms.application.rest.WaterTaxDue;
import org.egov.wtms.application.service.collection.ConnectionBillService;
import org.egov.wtms.application.service.collection.WaterConnectionBillable;
import org.egov.wtms.masters.entity.DonationDetails;
import org.egov.wtms.masters.service.DonationDetailsService;
import org.egov.wtms.masters.service.DonationHeaderService;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConnectionDemandService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DonationDetailsService donationDetailsService;

    @Autowired
    private DonationHeaderService donationHeaderService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private InstallmentDao installmentDao;

    @Autowired
    WaterConnectionService waterConnectionService;

    @Autowired
    WaterConnectionDetailsService waterConnectionDetailsService;

    @Autowired
    private ApplicationContextBeanProvider beanProvider;

    @Autowired
    private EgBillDao egBillDAO;

    @Autowired
    private ConnectionBillService connectionBillService;

    @Autowired
    private PropertyExternalService propertyExternalService;

    @Autowired
    private WaterConnectionDetailsRepository waterConnectionDetailsRepository;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public EgDemand createDemand(final WaterConnectionDetails waterConnectionDetails) {
        final Map<String, Object> feeDetails = new HashMap<String, Object>();
        DonationDetails donationDetails = null;
        final FieldInspectionDetails fieldInspectionDetails = waterConnectionDetails.getFieldInspectionDetails();

        if (null != fieldInspectionDetails)
            feeDetails.put(WaterTaxConstants.WATERTAX_FIELDINSPECTION_CHARGE, fieldInspectionDetails.getEstimationCharges());

        if (!WaterTaxConstants.BPL_CATEGORY.equalsIgnoreCase(waterConnectionDetails.getCategory().getCode()))
            donationDetails = donationDetailsService.findByDonationHeader(donationHeaderService
                    .findByCategoryandUsage(waterConnectionDetails.getCategory(), waterConnectionDetails.getUsageType()));

        if (donationDetails != null)
            feeDetails.put(WaterTaxConstants.WATERTAX_DONATION_CHARGE, donationDetails.getAmount());

        final Installment installment = installmentDao.getInsatllmentByModuleForGivenDate(
                moduleService.getModuleByName(WaterTaxConstants.EGMODULE_NAME), new Date());
        double totalFee = 0.0;

        final Set<EgDemandDetails> dmdDetailSet = new HashSet<EgDemandDetails>();
        for (final String demandReason : feeDetails.keySet()) {
            dmdDetailSet.add(createDemandDetails((Double) feeDetails.get(demandReason), demandReason, installment));
            totalFee += (Double) feeDetails.get(demandReason);
        }

        final EgDemand egDemand = new EgDemand();
        egDemand.setBaseDemand(BigDecimal.valueOf(totalFee));
        egDemand.setEgInstallmentMaster(installment);
        egDemand.getEgDemandDetails().addAll(dmdDetailSet);
        egDemand.setIsHistory("N");
        egDemand.setMinAmtPayable(BigDecimal.valueOf(totalFee));
        egDemand.setCreateDate(new Date());
        egDemand.setModifiedDate(new Date());
        return egDemand;
    }

    private EgDemandDetails createDemandDetails(final Double amount, final String demandReason,
            final Installment installment) {

        final Query demandQuery = getCurrentSession().getNamedQuery("DEMANDREASONBY_CODE_AND_INSTALLMENTID");
        demandQuery.setParameter(0, demandReason);
        demandQuery.setParameter(1, installment.getId());
        final EgDemandReason demandReasonObj = (EgDemandReason) demandQuery.uniqueResult();
        final EgDemandDetails demandDetail = new EgDemandDetails();
        demandDetail.setAmount(BigDecimal.valueOf(amount));
        demandDetail.setAmtCollected(BigDecimal.ZERO);
        demandDetail.setAmtRebate(BigDecimal.ZERO);
        demandDetail.setEgDemandReason(demandReasonObj);
        demandDetail.setCreateDate(new Date());
        demandDetail.setModifiedDate(new Date());
        return demandDetail;
    }

    public HashMap<String, Double> getSplitFee(final WaterConnectionDetails waterConnectionDetails) {
        final EgDemand demand = waterConnectionDetails.getDemand();
        final HashMap<String, Double> splitAmount = new HashMap<>();
        if (demand != null && demand.getEgDemandDetails() != null && demand.getEgDemandDetails().size() > 0)
            for (final EgDemandDetails detail : demand.getEgDemandDetails())
                if (WaterTaxConstants.WATERTAX_CONNECTION_CHARGE
                        .equals(detail.getEgDemandReason().getEgDemandReasonMaster().getCode()))
                    splitAmount.put(WaterTaxConstants.WATERTAX_CONNECTION_CHARGE, detail.getAmount().doubleValue());
                else if (WaterTaxConstants.WATERTAX_SECURITY_CHARGE
                        .equals(detail.getEgDemandReason().getEgDemandReasonMaster().getCode()))
                    splitAmount.put(WaterTaxConstants.WATERTAX_SECURITY_CHARGE, detail.getAmount().doubleValue());
                else if (WaterTaxConstants.WATERTAX_DONATION_CHARGE
                        .equals(detail.getEgDemandReason().getEgDemandReasonMaster().getCode()))
                    splitAmount.put(WaterTaxConstants.WATERTAX_DONATION_CHARGE, detail.getAmount().doubleValue());
        return splitAmount;
    }

    public WaterTaxDue getDueDetailsByConsumerCode(final String consumerCode) {
        final WaterTaxDue waterTaxDue = new WaterTaxDue();
        final List<String> consumerCodes = new ArrayList<>();
        final WaterConnectionDetails waterConnectionDetails = waterConnectionDetailsService
                .findByApplicationNumberOrConsumerCode(consumerCode);
        if (null != waterConnectionDetails) {
            getDueInfo(waterConnectionDetails);
            consumerCodes.add(waterConnectionDetails.getConnection().getConsumerCode());
            waterTaxDue.setConsumerCode(consumerCodes);
            waterTaxDue.setPropertyID(waterConnectionDetails.getConnection().getPropertyIdentifier());
            waterTaxDue.setConnectionCount(consumerCodes.size());
            waterTaxDue.setIsSuccess(true);
        } else {
            waterTaxDue.setIsSuccess(false);
            waterTaxDue.setConsumerCode(Collections.EMPTY_LIST);
            waterTaxDue.setConnectionCount(0);
            waterTaxDue.setErrorCode(WaterTaxConstants.CONSUMERCODE_NOT_EXIST_ERR_CODE);
            waterTaxDue.setErrorMessage(WaterTaxConstants.WTAXDETAILS_CONSUMER_CODE_NOT_EXIST_ERR_MSG_PREFIX
                    + consumerCode + WaterTaxConstants.WTAXDETAILS_NOT_EXIST_ERR_MSG_SUFFIX);
        }
        return waterTaxDue;
    }

    public WaterTaxDue getDueDetailsByPropertyId(final String propertyIdentifier) {
        BigDecimal arrDmd = new BigDecimal(0);
        BigDecimal arrColl = new BigDecimal(0);
        BigDecimal currDmd = new BigDecimal(0);
        BigDecimal currColl = new BigDecimal(0);
        BigDecimal totalDue = new BigDecimal(0);
        WaterTaxDue waterTaxDue = null;
        final List<WaterConnection> waterConnections = waterConnectionService
                .findByPropertyIdentifier(propertyIdentifier);
        if (waterConnections.isEmpty()) {
            waterTaxDue = new WaterTaxDue();
            waterTaxDue.setConsumerCode(Collections.EMPTY_LIST);
            waterTaxDue.setConnectionCount(0);
            waterTaxDue.setIsSuccess(false);
            waterTaxDue.setErrorCode(WaterTaxConstants.PROPERTYID_NOT_EXIST_ERR_CODE);
            waterTaxDue.setErrorMessage(WaterTaxConstants.WTAXDETAILS_PROPERTYID_NOT_EXIST_ERR_MSG_PREFIX
                    + propertyIdentifier + WaterTaxConstants.WTAXDETAILS_NOT_EXIST_ERR_MSG_SUFFIX);
        } else {
            waterTaxDue = new WaterTaxDue();
            final List<String> consumerCodes = new ArrayList<>();
            for (final WaterConnection connection : waterConnections)
                if (connection.getConsumerCode() != null) {

                    final WaterConnectionDetails waterConnectionDetails = waterConnectionDetailsService
                            .findByConnection(connection);

                    waterTaxDue = getDueInfo(waterConnectionDetails);
                    waterTaxDue.setPropertyID(propertyIdentifier);
                    consumerCodes.add(connection.getConsumerCode());
                    arrDmd = arrDmd.add(waterTaxDue.getArrearDemand());
                    arrColl = arrColl.add(waterTaxDue.getArrearCollection());
                    currDmd = currDmd.add(waterTaxDue.getCurrentDemand());
                    currColl = currColl.add(waterTaxDue.getCurrentCollection());
                    totalDue = totalDue.add(waterTaxDue.getTotalTaxDue());
                }
            waterTaxDue.setArrearDemand(arrDmd);
            waterTaxDue.setArrearCollection(arrColl);
            waterTaxDue.setCurrentDemand(currDmd);
            waterTaxDue.setCurrentCollection(currColl);
            waterTaxDue.setTotalTaxDue(totalDue);
            waterTaxDue.setConsumerCode(consumerCodes);
            waterTaxDue.setConnectionCount(waterConnections.size());
            waterTaxDue.setIsSuccess(true);
        }
        return waterTaxDue;
    }

    private WaterTaxDue getDueInfo(final WaterConnectionDetails waterConnectionDetails) {
        final Map<String, BigDecimal> resultmap = getDemandCollMap(waterConnectionDetails);
        final WaterTaxDue waterTaxDue = new WaterTaxDue();
        if (null != resultmap && !resultmap.isEmpty()) {
            final BigDecimal currDmd = resultmap.get(WaterTaxConstants.CURR_DMD_STR);
            waterTaxDue.setCurrentDemand(currDmd);
            final BigDecimal arrDmd = resultmap.get(WaterTaxConstants.ARR_DMD_STR);
            waterTaxDue.setArrearDemand(arrDmd);
            final BigDecimal currCollection = resultmap.get(WaterTaxConstants.CURR_COLL_STR);
            waterTaxDue.setCurrentCollection(currCollection);
            final BigDecimal arrCollection = resultmap.get(WaterTaxConstants.ARR_COLL_STR);
            waterTaxDue.setArrearCollection(arrCollection);
            // Calculating tax dues
            final BigDecimal taxDue = currDmd.add(arrDmd).subtract(currCollection).subtract(arrCollection);
            waterTaxDue.setTotalTaxDue(taxDue);
        }
        return waterTaxDue;
    }

    public Map<String, BigDecimal> getDemandCollMap(final WaterConnectionDetails waterConnectionDetails) {
        final EgDemand currDemand = waterConnectionDetails.getDemand();
        Installment installment = null;
        List<Object> dmdCollList = new ArrayList<Object>();
        Installment currInst = null;
        Integer instId = null;
        BigDecimal currDmd = BigDecimal.ZERO;
        BigDecimal arrDmd = BigDecimal.ZERO;
        BigDecimal currCollection = BigDecimal.ZERO;
        BigDecimal arrColelection = BigDecimal.ZERO;
        final Map<String, BigDecimal> retMap = new HashMap<String, BigDecimal>();

        if (currDemand != null)
            dmdCollList = getDmdCollAmtInstallmentWise(currDemand);
        currInst = installmentDao.getInsatllmentByModuleForGivenDate(
                moduleService.getModuleByName(WaterTaxConstants.EGMODULE_NAME), new Date());

        for (final Object object : dmdCollList) {
            final Object[] listObj = (Object[]) object;
            instId = Integer.valueOf(listObj[1].toString());
            installment = (Installment) installmentDao.findById(instId, false);
            if (currInst.equals(installment)) {
                if (listObj[3] != null && !listObj[3].equals(BigDecimal.ZERO))
                    currCollection = currCollection.add(new BigDecimal((Double) listObj[3]));
                currDmd = currDmd.add((BigDecimal) listObj[2]);
            } else {
                arrDmd = arrDmd.add((BigDecimal) listObj[2]);
                if (listObj[3] != null && !listObj[3].equals(BigDecimal.ZERO))
                    arrColelection = arrColelection.add((BigDecimal) listObj[2]);
            }
        }
        retMap.put(WaterTaxConstants.CURR_DMD_STR, currDmd);
        retMap.put(WaterTaxConstants.ARR_DMD_STR, arrDmd);
        retMap.put(WaterTaxConstants.CURR_COLL_STR, currCollection);
        retMap.put(WaterTaxConstants.ARR_COLL_STR, arrColelection);
        return retMap;
    }

    public List<Object> getDmdCollAmtInstallmentWise(final EgDemand egDemand) {
        final StringBuffer strBuf = new StringBuffer(2000);
        strBuf.append(
                "select dmdRes.id,dmdRes.id_installment, sum(dmdDet.amount) as amount, sum(dmdDet.amt_collected) as amt_collected, "
                        + "sum(dmdDet.amt_rebate) as amt_rebate, inst.start_date from eg_demand_details dmdDet,eg_demand_reason dmdRes, "
                        + "eg_installment_master inst,eg_demand_reason_master dmdresmas where dmdDet.id_demand_reason=dmdRes.id "
                        + "and dmdDet.id_demand =:dmdId and dmdRes.id_installment = inst.id and dmdresmas.id = dmdres.id_demand_reason_master "
                        + "group by dmdRes.id,dmdRes.id_installment, inst.start_date order by inst.start_date ");
        return getCurrentSession().createSQLQuery(strBuf.toString()).setLong("dmdId", egDemand.getId()).list();
    }

    public String generateBill(final String consumerCode) {
        String collectXML = "";
        final WaterConnectionBillable waterConnectionBillable = (WaterConnectionBillable) beanProvider
                .getBean("waterConnectionBillable");
        final WaterConnectionDetails waterConnectionDetails = waterConnectionDetailsService
                .findByApplicationNumberOrConsumerCode(consumerCode);
        final AssessmentDetails assessmentDetails = propertyExternalService
                .loadAssessmentDetails(waterConnectionDetails.getConnection().getPropertyIdentifier(),
                        PropertyExternalService.FLAG_FULL_DETAILS);
        waterConnectionBillable.setWaterConnectionDetails(waterConnectionDetails);
        waterConnectionBillable.setAssessmentDetails(assessmentDetails);
        waterConnectionBillable.setUserId(EgovThreadLocals.getUserId());
        waterConnectionBillable.setReferenceNumber(
                generateBillNumber(assessmentDetails.getBoundaryDetails().getWardNumber().toString()));
        waterConnectionBillable.setBillType(getBillTypeByCode(WaterTaxConstants.BILLTYPE_MANUAL));

        final String billXml = connectionBillService.getBillXML(waterConnectionBillable);
        collectXML = URLEncoder.encode(billXml);
        return collectXML;
    }

    public EgBillType getBillTypeByCode(final String typeCode) {
        final EgBillType billType = egBillDAO.getBillTypeByCode(typeCode);
        return billType;
    }

    public String generateBillNumber(final String wardNo) {
        final StringBuffer billNo = new StringBuffer();
        final Module module = moduleService.getModuleByName(WaterTaxConstants.EGMODULE_NAME);
        installmentDao.getInsatllmentByModuleForGivenDate(module, new Date());
        // FIX ME
        /*
         * String index = sequenceNumberGenerator.getNextNumberWithFormat( BILLGEN_SEQNAME_PREFIX + wardNo, 7, '0',
         * Long.valueOf(1)) .getFormattedNumber(); billNo.append(wardNo); billNo.append("/"); billNo.append(index);
         * billNo.append("/"); billNo.append(finYear.getDescription());
         */
        return billNo.toString();
    }

    public EgDemand getDemandByInstAndConsumerCode(final Installment installment, final String consumerCode) {
        return waterConnectionDetailsRepository.findByConsumerCodeAndInstallment(installment, consumerCode).getDemand();
    }
}
