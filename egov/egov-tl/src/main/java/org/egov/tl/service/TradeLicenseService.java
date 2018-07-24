/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
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
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.tl.service;

import org.apache.commons.lang3.StringUtils;
import org.egov.commons.Installment;
import org.egov.demand.model.BillReceipt;
import org.egov.demand.model.EgDemandDetails;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.reporting.engine.ReportFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.pims.commons.Position;
import org.egov.tl.entity.License;
import org.egov.tl.entity.LicenseAppType;
import org.egov.tl.entity.LicenseDemand;
import org.egov.tl.entity.LicenseDocument;
import org.egov.tl.entity.LicenseDocumentType;
import org.egov.tl.entity.NatureOfBusiness;
import org.egov.tl.entity.TradeLicense;
import org.egov.tl.entity.WorkflowBean;
import org.egov.tl.entity.contracts.DemandNoticeForm;
import org.egov.tl.entity.contracts.OnlineSearchForm;
import org.egov.tl.entity.contracts.SearchForm;
import org.egov.tl.repository.SearchTradeRepository;
import org.egov.tl.repository.specs.SearchTradeSpec;
import org.egov.tl.utils.LicenseUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static org.apache.commons.lang.StringEscapeUtils.escapeXml;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.egov.infra.config.core.ApplicationThreadLocals.getMunicipalityName;
import static org.egov.infra.reporting.engine.ReportFormat.PDF;
import static org.egov.infra.reporting.util.ReportUtil.CONTENT_TYPES;
import static org.egov.infra.utils.ApplicationConstant.NA;
import static org.egov.infra.utils.DateUtils.currentDateToDefaultDateFormat;
import static org.egov.infra.utils.DateUtils.getDefaultFormattedDate;
import static org.egov.infra.utils.DateUtils.toYearFormat;
import static org.egov.infra.utils.FileUtils.addFilesToZip;
import static org.egov.infra.utils.FileUtils.byteArrayToFile;
import static org.egov.infra.utils.FileUtils.toByteArray;
import static org.egov.infra.utils.StringUtils.append;
import static org.egov.tl.utils.Constants.*;
import static org.hibernate.criterion.MatchMode.ANYWHERE;

@Transactional(readOnly = true)
public class TradeLicenseService extends AbstractLicenseService<TradeLicense> {

    @Autowired
    private TradeLicenseSmsAndEmailService tradeLicenseSmsAndEmailService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private LicenseUtils licenseUtils;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private SearchTradeRepository searchTradeRepository;

    @Autowired
    private CityService cityService;

    @Autowired
    private EisCommonService eisCommonService;

    protected NatureOfBusiness getNatureOfBusiness() {
        return natureOfBusinessService.getNatureOfBusinessByName("Permanent");
    }

    @Override
    protected Module getModuleName() {
        return moduleService.getModuleByName(TRADE_LICENSE);
    }

    @Override
    protected void sendEmailAndSMS(final TradeLicense license, final String currentAction) {
        tradeLicenseSmsAndEmailService.sendSmsAndEmail(license, currentAction);
    }

    @Override
    protected LicenseAppType getLicenseApplicationTypeForRenew() {
        return licenseAppTypeService.getLicenseAppTypeByCode(RENEW_APPTYPE_CODE);
    }

    @Override
    protected LicenseAppType getLicenseApplicationType() {
        return licenseAppTypeService.getLicenseAppTypeByCode(NEW_APPTYPE_CODE);
    }

    @Override
    protected LicenseAppType getClosureLicenseApplicationType() {
        return licenseAppTypeService.getLicenseAppTypeByCode(CLOSURE_APPTYPE_CODE);
    }

    @Transactional
    public void save(final License license) {
        final BigDecimal currentDemandAmount = recalculateLicenseFee(license.getCurrentDemand());
        final BigDecimal feematrixDmdAmt = calculateFeeAmount(license);
        if (feematrixDmdAmt.compareTo(currentDemandAmount) >= 0)
            updateDemandForChangeTradeArea((TradeLicense) license);
        processAndStoreDocument(license);
        licenseRepository.save(license);
    }

    @Transactional
    public void updateTradeLicense(final TradeLicense license, final WorkflowBean workflowBean) {
        processAndStoreDocument(license);
        licenseRepository.save(license);
        tradeLicenseSmsAndEmailService.sendSmsAndEmail(license, workflowBean.getWorkFlowAction());
        licenseApplicationIndexService.createOrUpdateLicenseApplicationIndex(license);
    }


    public void updateStatusInWorkFlowProgress(TradeLicense license, final String workFlowAction) {

        List<Position> userPositions = positionMasterService.getPositionsForEmployee(securityUtils.getCurrentUser().getId());
        if (BUTTONAPPROVE.equals(workFlowAction)) {
            if (isEmpty(license.getLicenseNumber()) && license.isNewApplication())
                license.setLicenseNumber(licenseNumberUtils.generateLicenseNumber());

            if (license.getCurrentDemand().getBaseDemand().compareTo(license.getCurrentDemand().getAmtCollected()) <= 0)
                license.setEgwStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(TRADELICENSEMODULE, APPLICATION_STATUS_APPROVED_CODE));
            else
                license.setEgwStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(TRADELICENSEMODULE, APPLICATION_STATUS_SECONDCOLLECTION_CODE));
            generateAndStoreCertificate(license);

        }
        if (BUTTONAPPROVE.equals(workFlowAction) || BUTTONFORWARD.equals(workFlowAction)) {
            license.setStatus(licenseStatusService.getLicenseStatusByCode(STATUS_UNDERWORKFLOW));
            if (license.getState().getValue().equals(WF_REVENUECLERK_APPROVED))
                license.setEgwStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(TRADELICENSEMODULE, APPLICATION_STATUS_INSPE_CODE));
            else if (license.getState().getValue().equals(WORKFLOW_STATE_REJECTED))
                license.setEgwStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(TRADELICENSEMODULE, APPLICATION_STATUS_CREATED_CODE));
        }

        if (GENERATECERTIFICATE.equals(workFlowAction)) {
            license.setActive(true);
            license.setStatus(licenseStatusService.getLicenseStatusByCode(STATUS_ACTIVE));
            // setting license to non-legacy, old license number will be the only tracking
            // to check a license created as legacy or new hereafter.
            license.setLegacy(false);
            validityService.applyLicenseValidity(license);
            license.setEgwStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(TRADELICENSEMODULE, APPLICATION_STATUS_GENECERT_CODE));
        }
        if (BUTTONREJECT.equals(workFlowAction))
            if (license.getLicenseAppType() != null && userPositions.contains(license.getCurrentState().getInitiatorPosition())
                    && ("Rejected".equals(license.getState().getValue()))
                    || "License Created".equals(license.getState().getValue())) {
                license.setStatus(licenseStatusService.getLicenseStatusByCode(STATUS_CANCELLED));
                license.setEgwStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(TRADELICENSEMODULE, APPLICATION_STATUS_CANCELLED));
                if (license.isNewApplication())
                    license.setActive(false);
            } else {
                license.setStatus(licenseStatusService.getLicenseStatusByCode(STATUS_REJECTED));
                license.setEgwStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(TRADELICENSEMODULE, APPLICATION_STATUS_REJECTED));
            }
        if (license.hasState() && license.getState().getValue().contains(WF_REVENUECLERK_APPROVED)) {
            final BigDecimal currentDemandAmount = recalculateLicenseFee(license.getCurrentDemand());
            final BigDecimal recalDemandAmount = calculateFeeAmount(license);
            if (recalDemandAmount.compareTo(currentDemandAmount) >= 0)
                updateDemandForChangeTradeArea(license);
        }
    }

    @ReadOnly
    public ReportOutput generateLicenseCertificate(License license, boolean isProvisional) {
        String reportTemplate;
        if (CITY_GRADE_CORPORATION.equals(cityService.getCityGrade()))
            reportTemplate = "tl_licenseCertificateForCorp";
        else
            reportTemplate = "tl_licenseCertificate";
        ReportOutput reportOutput = reportService.createReport(new ReportRequest(reportTemplate, license,
                getReportParamsForCertificate(license, isProvisional)));
        reportOutput.setReportName(license.generateCertificateFileName());
        return reportOutput;
    }

    private Map<String, Object> getReportParamsForCertificate(License license, boolean isProvisional) {

        final Map<String, Object> reportParams = new HashMap<>();
        reportParams.put("applicationnumber", license.getApplicationNumber());
        reportParams.put("applicantName", license.getLicensee().getApplicantName());
        reportParams.put("licencenumber", license.getLicenseNumber());
        reportParams.put("wardName", license.getBoundary().getName());
        reportParams.put("cscNumber", "");
        reportParams.put("nameOfEstablishment", escapeXml(license.getNameOfEstablishment()));
        reportParams.put("licenceAddress", escapeXml(license.getAddress()));
        reportParams.put("municipality", cityService.getMunicipalityName());
        reportParams.put("district", cityService.getDistrictName());
        reportParams.put("category", escapeXml(license.getCategory().getName()));
        reportParams.put("subCategory", escapeXml(license.getTradeName().getName()));
        reportParams.put("appType", license.isNewApplication() ? "New Trade" : "Renewal");
        reportParams.put("currentDate", currentDateToDefaultDateFormat());
        reportParams.put("carporationulbType", getMunicipalityName().contains("Corporation"));
        Optional<EgDemandDetails> demandDetails = license.getCurrentDemand().getEgDemandDetails().stream()
                .sorted(Comparator.comparing(EgDemandDetails::getInstallmentEndDate).reversed())
                .filter(demandDetail -> demandDetail.getEgDemandReason().getEgDemandReasonMaster().getReasonMaster().equals(LICENSE_FEE_TYPE))
                .filter(demandDetail -> demandDetail.getAmtCollected().doubleValue() > 0)
                .findFirst();
        BigDecimal amtPaid;
        String installmentYear;
        if (demandDetails.isPresent()) {
            amtPaid = demandDetails.get().getAmtCollected();
            installmentYear = toYearFormat(demandDetails.get().getInstallmentStartDate()) + "-" +
                    toYearFormat(demandDetails.get().getInstallmentEndDate());
        } else {
            throw new ValidationException("License Fee is not paid", "License Fee is not paid");
        }

        reportParams.put("installMentYear", installmentYear);
        reportParams.put("applicationdate", getDefaultFormattedDate(license.getApplicationDate()));
        reportParams.put("demandUpdateDate", getDefaultFormattedDate(license.getCurrentDemand().getModifiedDate()));
        reportParams.put("demandTotalamt", amtPaid);

        User approver;
        if (isProvisional || license.getApprovedBy() == null) {
            approver = licenseUtils.getCommissionerAssignment().getEmployee();
        } else {
            approver = license.getApprovedBy();
        }
        ByteArrayInputStream commissionerSign = new ByteArrayInputStream(
                approver == null || approver.getSignature() == null ? new byte[0] : approver.getSignature());
        reportParams.put("commissionerSign", commissionerSign);

        if (isProvisional)
            reportParams.put("certificateType", "provisional");
        else {
            reportParams.put("qrCode", license.qrCode(installmentYear, amtPaid));
        }

        return reportParams;
    }

    @ReadOnly
    public List<String> getTradeLicenseForGivenParam(final String paramValue, final String paramType) {
        List<String> licenseList = new ArrayList<>();
        if (isNotBlank(paramValue) && isNotBlank(paramType)) {
            if (SEARCH_BY_APPNO.equals(paramType))
                licenseList = licenseRepository.findAllApplicationNumberLike(paramValue);

            else if (SEARCH_BY_LICENSENO.equals(paramType))
                licenseList = licenseRepository.findAllLicenseNumberLike(paramValue);

            else if (SEARCH_BY_OLDLICENSENO.equals(paramType))
                licenseList = licenseRepository.findAllOldLicenseNumberLike(paramValue);

            else if (SEARCH_BY_TRADETITLE.equals(paramType))
                licenseList = licenseRepository.findAllNameOfEstablishmentLike(paramValue);

            else if (SEARCH_BY_TRADEOWNERNAME.equals(paramType))
                licenseList = licenseRepository.findAllApplicantNameLike(paramValue);

            else if (SEARCH_BY_PROPERTYASSESSMENTNO.equals(paramType))
                licenseList = licenseRepository.findAllAssessmentNoLike(paramValue);

            else if (SEARCH_BY_MOBILENO.equals(paramType))
                licenseList = licenseRepository.findAllMobilePhoneNumberLike(paramValue);
        }

        return licenseList;
    }

    @ReadOnly
    public Page<SearchForm> searchTradeLicense(final SearchForm searchForm) {
        Pageable pageable = new PageRequest(searchForm.pageNumber(),
                searchForm.pageSize(), searchForm.orderDir(), searchForm.orderBy());
        User currentUser = securityUtils.getCurrentUser();
        Page<License> licenses = searchTradeRepository.findAll(SearchTradeSpec.searchTrade(searchForm), pageable);
        List<SearchForm> searchResults = new ArrayList<>();
        licenses.forEach(license ->
                searchResults.add(new SearchForm(license, currentUser, getOwnerName(license), licenseConfigurationService.getFeeCollectorRoles()))
        );
        return new PageImpl<>(searchResults, pageable, licenses.getTotalElements());
    }

    @ReadOnly
    public List<OnlineSearchForm> onlineSearchTradeLicense(final OnlineSearchForm searchForm) {
        final Criteria searchCriteria = entityQueryService.getSession().createCriteria(TradeLicense.class);
        searchCriteria.createAlias("licensee", "licc").createAlias("category", "cat")
                .createAlias("tradeName", "subcat").createAlias("status", "licstatus");
        if (isNotBlank(searchForm.getApplicationNumber()))
            searchCriteria.add(Restrictions.eq("applicationNumber", searchForm.getApplicationNumber()).ignoreCase());
        if (isNotBlank(searchForm.getLicenseNumber()))
            searchCriteria.add(Restrictions.eq("licenseNumber", searchForm.getLicenseNumber()).ignoreCase());
        if (isNotBlank(searchForm.getMobileNo()))
            searchCriteria.add(Restrictions.eq("licc.mobilePhoneNumber", searchForm.getMobileNo()));
        if (isNotBlank(searchForm.getTradeOwnerName()))
            searchCriteria.add(Restrictions.like("licc.applicantName", searchForm.getTradeOwnerName(), ANYWHERE));


        searchCriteria.add(Restrictions.isNotNull("applicationNumber"));
        searchCriteria.addOrder(Order.asc("id"));
        List<OnlineSearchForm> searchResult = new ArrayList<>();
        for (License license : (List<License>) searchCriteria.list()) {
            if (license != null)
                searchResult.add(new OnlineSearchForm(license, getDemandColl(license)));
        }
        return searchResult;
    }

    public BigDecimal[] getDemandColl(License license) {
        BigDecimal[] dmdColl = new BigDecimal[3];
        Arrays.fill(dmdColl, BigDecimal.ZERO);
        final Installment latestInstallment = this.installmentDao.getInsatllmentByModuleForGivenDate(getModuleName(),
                new DateTime().withMonthOfYear(4).withDayOfMonth(1).toDate());
        license.getCurrentDemand().getEgDemandDetails().stream().forEach(egDemandDetails -> {
                    if (latestInstallment.equals(egDemandDetails.getEgDemandReason().getEgInstallmentMaster())) {
                        dmdColl[1] = dmdColl[1].add(egDemandDetails.getAmount());
                        dmdColl[2] = dmdColl[2].add(egDemandDetails.getAmtCollected());
                    } else {
                        dmdColl[0] = dmdColl[0].add(egDemandDetails.getAmount());
                        dmdColl[2] = dmdColl[2].add(egDemandDetails.getAmtCollected());
                    }
                }
        );
        return dmdColl;
    }

    @ReadOnly
    public List<DemandNoticeForm> getLicenseDemandNotices(final DemandNoticeForm demandNoticeForm) {
        final Criteria searchCriteria = entityQueryService.getSession().createCriteria(TradeLicense.class);
        searchCriteria.createAlias("licensee", "licc").createAlias("category", "cat").createAlias("tradeName", "subcat")
                .createAlias("status", "licstatus").createAlias("natureOfBusiness", "nob")
                .createAlias("licenseDemand", "licDemand").createAlias("licenseAppType", "appType")
                .add(Restrictions.ne("appType.code", CLOSURE_APPTYPE_CODE));
        if (isNotBlank(demandNoticeForm.getLicenseNumber()))
            searchCriteria.add(Restrictions.eq("licenseNumber", demandNoticeForm.getLicenseNumber()).ignoreCase());
        if (isNotBlank(demandNoticeForm.getOldLicenseNumber()))
            searchCriteria
                    .add(Restrictions.eq("oldLicenseNumber", demandNoticeForm.getOldLicenseNumber()).ignoreCase());
        if (demandNoticeForm.getCategoryId() != null)
            searchCriteria.add(Restrictions.eq("cat.id", demandNoticeForm.getCategoryId()));
        if (demandNoticeForm.getSubCategoryId() != null)
            searchCriteria.add(Restrictions.eq("subcat.id", demandNoticeForm.getSubCategoryId()));
        if (demandNoticeForm.getWardId() != null)
            searchCriteria.createAlias("parentBoundary", "wards")
                    .add(Restrictions.eq("wards.id", demandNoticeForm.getWardId()));
        if (demandNoticeForm.getElectionWard() != null)
            searchCriteria.createAlias("adminWard", "electionWard")
                    .add(Restrictions.eq("electionWard.id", demandNoticeForm.getElectionWard()));
        if (demandNoticeForm.getLocalityId() != null)
            searchCriteria.createAlias("boundary", "locality")
                    .add(Restrictions.eq("locality.id", demandNoticeForm.getLocalityId()));
        if (demandNoticeForm.getStatusId() == null)
            searchCriteria.add(Restrictions.ne("licstatus.statusCode", StringUtils.upperCase("CAN")));
        else
            searchCriteria.add(Restrictions.eq("status.id", demandNoticeForm.getStatusId()));
        searchCriteria
                .add(Restrictions.eq("isActive", true))
                .add(Restrictions.eq("nob.name", PERMANENT_NATUREOFBUSINESS))
                .add(Restrictions.gtProperty("licDemand.baseDemand", "licDemand.amtCollected"))
                .addOrder(Order.asc("id"));
        final List<DemandNoticeForm> finalList = new LinkedList<>();

        for (final TradeLicense license : (List<TradeLicense>) searchCriteria.list()) {
            LicenseDemand licenseDemand = license.getCurrentDemand();
            if (licenseDemand != null) {
                Installment currentInstallment = licenseDemand.getEgInstallmentMaster();
                List<Installment> previousInstallment = installmentDao
                        .fetchPreviousInstallmentsInDescendingOrderByModuleAndDate(
                                licenseUtils.getModule(TRADE_LICENSE), currentInstallment.getToDate(), 1);
                Map<String, Map<String, BigDecimal>> outstandingFees = getOutstandingFeeForDemandNotice(license,
                        currentInstallment, previousInstallment.get(0));
                Map<String, BigDecimal> licenseFees = outstandingFees.get(LICENSE_FEE_TYPE);
                finalList.add(new DemandNoticeForm(license, licenseFees, getOwnerName(license)));
            }
        }
        return finalList;
    }

    public String getOwnerName(License license) {
        String ownerName = NA;
        if (license.getState() != null && license.currentAssignee() != null) {
            List<Assignment> assignmentList = assignmentService
                    .getAssignmentsForPosition(license.currentAssignee().getId(), new Date());
            if (!assignmentList.isEmpty())
                ownerName = assignmentList.get(0).getEmployee().getName();
            ownerName = format(PROCESS_OWNER_FORMAT, ownerName, license.currentAssignee().getName());
        }
        return ownerName;

    }

    public List<HashMap<String, Object>> populateHistory(final TradeLicense tradeLicense) {
        final List<HashMap<String, Object>> processHistoryDetails = new ArrayList<>();
        if (tradeLicense.hasState()) {
            State<Position> state = tradeLicense.getCurrentState();
            final HashMap<String, Object> currentStateDetail = new HashMap<>();
            currentStateDetail.put("date", state.getLastModifiedDate());
            currentStateDetail.put("updatedBy", state.getSenderName().contains(DELIMITER_COLON)
                    ? state.getSenderName().split(DELIMITER_COLON)[1] : state.getSenderName());
            currentStateDetail.put("status", state.isEnded() ? "Completed" : state.getValue());
            currentStateDetail.put("comments", defaultString(state.getComments()));
            User ownerUser = state.getOwnerUser();
            Position ownerPosition = state.getOwnerPosition();
            if (ownerPosition != null) {
                User usr = eisCommonService.getUserForPosition(ownerPosition.getId(), state.getLastModifiedDate());
                currentStateDetail.put("user", usr == null ? NA : usr.getName());
            } else
                currentStateDetail.put("user", ownerUser == null ? NA : ownerUser.getName());

            processHistoryDetails.add(currentStateDetail);
            state.getHistory().stream().sorted(Comparator.comparing(StateHistory<Position>::getLastModifiedDate).reversed()).
                    forEach(sh -> processHistoryDetails.add(constructHistory(sh)));
        }
        return processHistoryDetails;
    }

    private HashMap<String, Object> constructHistory(StateHistory<Position> stateHistory) {
        final HashMap<String, Object> processHistory = new HashMap<>();
        processHistory.put("date", stateHistory.getLastModifiedDate());
        processHistory.put("updatedBy", stateHistory.getSenderName().contains(DELIMITER_COLON)
                ? stateHistory.getSenderName().split(DELIMITER_COLON)[1] : stateHistory.getSenderName());
        processHistory.put("status", stateHistory.getValue());
        processHistory.put("comments", defaultString(stateHistory.getComments()));
        Position ownerPosition = stateHistory.getOwnerPosition();
        User ownerUser = stateHistory.getOwnerUser();
        if (ownerPosition != null) {
            User userPos = eisCommonService.getUserForPosition(ownerPosition.getId(), stateHistory.getLastModifiedDate());
            processHistory.put("user", userPos == null ? NA : userPos.getName());
        } else
            processHistory.put("user",
                    ownerUser == null ? NA : ownerUser.getName());
        return processHistory;
    }

    @ReadOnly
    public List<License> getLicenses(Example license) {
        return licenseRepository.findAll(license);
    }

    public List<BillReceipt> getReceipts(License license) {
        return demandGenericDao.getBillReceipts(license.getCurrentDemand());
    }

    public LicenseDocumentType getLicenseDocumentType(Long id) {
        return licenseDocumentTypeRepository.findOne(id);
    }

    public Map<String, Map<String, List<LicenseDocument>>> getAttachedDocument(Long licenseId) {

        List<LicenseDocument> licenseDocuments = getLicenseById(licenseId).getDocuments();
        Map<String, Map<String, List<LicenseDocument>>> licenseDocumentDetails = new HashMap<>();
        licenseDocumentDetails.put(NEW_APPTYPE_CODE, new HashMap<>());
        licenseDocumentDetails.put(RENEW_APPTYPE_CODE, new HashMap<>());
        licenseDocumentDetails.put(CLOSURE_APPTYPE_CODE, new HashMap<>());

        for (LicenseDocument document : licenseDocuments) {
            String docType = document.getType().getName();
            String appType = document.getType().getApplicationType().toString();

            if (licenseDocumentDetails.get(appType).containsKey(docType)) {
                licenseDocumentDetails.get(appType).get(docType).add(document);
            } else {
                List<LicenseDocument> documents = new ArrayList<>();
                documents.add(document);
                licenseDocumentDetails.get(appType).put(docType, documents);
            }
        }
        return licenseDocumentDetails;
    }

    public ReportOutput generateAcknowledgment(String uid) {
        License license = getLicenseByUID(uid);
        Map<String, Object> reportParams = new HashMap<>();
        reportParams.put("amount", license.getTotalBalance());
        ReportRequest reportRequest = new ReportRequest("tl_license_acknowledgment", license, reportParams);
        reportRequest.setReportFormat(ReportFormat.PDF);
        ReportOutput reportOutput = reportService.createReport(reportRequest);
        reportOutput.setReportName(append("license_ack_", license.getApplicationNumber()));
        return reportOutput;
    }

    @ReadOnly
    public ReportOutput generateClosureNotice(String reportFormat) {
        ReportOutput reportOutput = new ReportOutput();
        Map<String, Object> reportParams = new HashMap<>();
        List<License> licenses = searchTradeRepository.findLicenseClosureByCurrentInstallmentYear(new Date());
        if (licenses.isEmpty()) {
            reportOutput.setReportName("tl_closure_notice");
            reportOutput.setReportFormat(ReportFormat.PDF);
            reportOutput.setReportOutputData("No Data".getBytes());
        } else {
            reportParams.put("License", licenses);
            reportParams.put("corp", cityService.getCityGrade());
            reportParams.put("currentDate", currentDateToDefaultDateFormat());
            reportParams.put("municipality", cityService.getMunicipalityName());
            reportOutput = reportService.createReport(
                    new ReportRequest("tl_closure_notice", licenses, reportParams));
        }
        if (reportFormat.equalsIgnoreCase("zip"))
            reportOutput.setReportOutputData(toByteArray(addFilesToZip(byteArrayToFile(reportOutput.getReportOutputData(),
                    "tl_closure_notice_", ".pdf").toFile())));
        return reportOutput;
    }

    public void generateAndStoreCertificate(License license) {
        FileStoreMapper fileStore = fileStoreService.store(generateLicenseCertificate(license, false).getReportOutputData(),
                license.generateCertificateFileName() + ".pdf", CONTENT_TYPES.get(PDF), TL_FILE_STORE_DIR);
        license.setCertificateFileId(fileStore.getFileStoreId());
    }

    public TradeLicense getLicenseByUID(String uid) {
        return (TradeLicense) licenseRepository.findByUid(uid);
    }
}
