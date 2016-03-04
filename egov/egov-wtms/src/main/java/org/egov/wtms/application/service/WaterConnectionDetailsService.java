/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 *
 * Copyright (C) <2015> eGovernments Foundation
 *
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 *
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 *
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 *
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 *
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 *
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.wtms.application.service;

import static org.egov.wtms.utils.constants.WaterTaxConstants.WFLOW_ACTION_STEP_REJECT;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;

import org.egov.commons.EgModules;
import org.egov.commons.Installment;
import org.egov.demand.model.EgDemand;
import org.egov.eis.entity.Assignment;
import org.egov.eis.entity.AssignmentAdaptor;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.search.elastic.entity.ApplicationIndex;
import org.egov.infra.search.elastic.entity.ApplicationIndexBuilder;
import org.egov.infra.search.elastic.entity.enums.ApprovalStatus;
import org.egov.infra.search.elastic.entity.enums.ClosureStatus;
import org.egov.infra.search.elastic.service.ApplicationIndexService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.ApplicationNumberGenerator;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.workflow.WorkFlowMatrix;
import org.egov.pims.commons.Position;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.OwnerName;
import org.egov.ptis.domain.model.enums.BasicPropertyStatus;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.wtms.application.entity.ApplicationDocuments;
import org.egov.wtms.application.entity.WaterConnection;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.repository.WaterConnectionDetailsRepository;
import org.egov.wtms.application.workflow.ApplicationWorkflowCustomDefaultImpl;
import org.egov.wtms.elasticSearch.service.ConsumerIndexService;
import org.egov.wtms.masters.entity.ApplicationType;
import org.egov.wtms.masters.entity.DocumentNames;
import org.egov.wtms.masters.entity.DonationDetails;
import org.egov.wtms.masters.entity.WaterRatesDetails;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.masters.entity.enums.ConnectionType;
import org.egov.wtms.masters.service.ApplicationProcessTimeService;
import org.egov.wtms.masters.service.ApplicationTypeService;
import org.egov.wtms.masters.service.DocumentNamesService;
import org.egov.wtms.utils.PropertyExtnUtils;
import org.egov.wtms.utils.WaterTaxNumberGenerator;
import org.egov.wtms.utils.WaterTaxUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Service
@Transactional(readOnly = true)
public class WaterConnectionDetailsService {

    protected WaterConnectionDetailsRepository waterConnectionDetailsRepository;
    private static final Logger LOG = LoggerFactory.getLogger(WaterConnectionDetailsService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationNumberGenerator applicationNumberGenerator;

    @Autowired
    private ApplicationTypeService applicationTypeService;

    @Autowired
    private SimpleWorkflowService<WaterConnectionDetails> waterConnectionWorkflowService;

    @Autowired
    private ApplicationProcessTimeService applicationProcessTimeService;

    @Autowired
    private ApplicationIndexService applicationIndexService;

    @Autowired
    private DocumentNamesService documentNamesService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private PropertyExtnUtils propertyExtnUtils;

    @Autowired
    private EisCommonService eisCommonService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private WaterTaxNumberGenerator waterTaxNumberGenerator;

    @Autowired
    private ConsumerIndexService consumerIndexService;

    @Autowired
    private WaterTaxUtils waterTaxUtils;

    @Autowired
    private ConnectionDemandService connectionDemandService;

    @Autowired
    private WaterConnectionSmsAndEmailService waterConnectionSmsAndEmailService;

    @Autowired
    @Qualifier("fileStoreService")
    protected FileStoreService fileStoreService;

    @Autowired
    public WaterConnectionDetailsService(final WaterConnectionDetailsRepository waterConnectionDetailsRepository) {
        this.waterConnectionDetailsRepository = waterConnectionDetailsRepository;
    }

    public WaterConnectionDetails findBy(final Long waterConnectionId) {
        return waterConnectionDetailsRepository.findOne(waterConnectionId);
    }

    public List<WaterConnectionDetails> findAll() {
        return waterConnectionDetailsRepository.findAll(new Sort(Sort.Direction.ASC,
                WaterTaxConstants.APPLICATION_NUMBER));
    }

    public WaterConnectionDetails findByApplicationNumber(final String applicationNumber) {
        return waterConnectionDetailsRepository.findByApplicationNumber(applicationNumber);
    }

    public WaterConnectionDetails load(final Long id) {
        return waterConnectionDetailsRepository.getOne(id);
    }

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public Page<WaterConnectionDetails> getListWaterConnectionDetails(final Integer pageNumber, final Integer pageSize) {
        final Pageable pageable = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC,
                WaterTaxConstants.APPLICATION_NUMBER);
        return waterConnectionDetailsRepository.findAll(pageable);
    }

    @Transactional
    public WaterConnectionDetails createNewWaterConnection(final WaterConnectionDetails waterConnectionDetails,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction, final String sourceChannel) {
        if (waterConnectionDetails.getApplicationNumber() == null)
            waterConnectionDetails.setApplicationNumber(applicationNumberGenerator.generate());
        waterConnectionDetails.setApplicationDate(new Date());
        final Integer appProcessTime = applicationProcessTimeService.getApplicationProcessTime(
                waterConnectionDetails.getApplicationType(), waterConnectionDetails.getCategory());
        if (appProcessTime != null)
            waterConnectionDetails.setDisposalDate(getDisposalDate(waterConnectionDetails, appProcessTime));
        final WaterConnectionDetails savedWaterConnectionDetails = waterConnectionDetailsRepository
                .save(waterConnectionDetails);
        // TODO: as off now using anonymous created for MeeSeva after we need to
        // craete role for this and add in appconfig for
        // recordCreatedBYNonEmployee API
        // so it will work as CSC Operator record
        if (userService.getUserById(waterConnectionDetails.getCreatedBy().getId()).getUsername().equals("anonymous")) {
            EgovThreadLocals.setUserId(Long.valueOf("40"));
            savedWaterConnectionDetails.setCreatedBy(userService.getUserById(EgovThreadLocals.getUserId()));
        }
        if (LOG.isDebugEnabled())
            LOG.debug(" persisting WaterConnectionDetail object is completed and WorkFlow API Stared ");
        final ApplicationWorkflowCustomDefaultImpl applicationWorkflowCustomDefaultImpl = getInitialisedWorkFlowBean();
        if (LOG.isDebugEnabled())
            LOG.debug("applicationWorkflowCustomDefaultImpl initialization is done");
        applicationWorkflowCustomDefaultImpl.createCommonWorkflowTransition(savedWaterConnectionDetails,
                approvalPosition, approvalComent, additionalRule, workFlowAction);

        updateIndexes(savedWaterConnectionDetails, sourceChannel);
        waterConnectionSmsAndEmailService.sendSmsAndEmail(waterConnectionDetails, workFlowAction);
        if (LOG.isDebugEnabled())
            LOG.debug("updating water Connection Deatail is complted");

        return savedWaterConnectionDetails;
    }

    @Transactional
    public WaterConnectionDetails createExisting(final WaterConnectionDetails waterConnectionDetails) {
        waterConnectionDetails.getExistingConnection().setWaterConnectionDetails(waterConnectionDetails);
        waterConnectionDetails.setApplicationNumber(waterConnectionDetails.getConnection().getConsumerCode());
        waterConnectionDetails.setApplicationDate(waterConnectionDetails.getExecutionDate());
        waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                WaterTaxConstants.APPLICATION_STATUS_SANCTIONED, WaterTaxConstants.MODULETYPE));
        if (waterConnectionDetails.getApplicationType().getCode().equalsIgnoreCase(WaterTaxConstants.ADDNLCONNECTION)) {
            final WaterConnectionDetails primaryConnectionDetails = getPrimaryConnectionDetailsByPropertyIdentifier(waterConnectionDetails
                    .getConnection().getPropertyIdentifier());
            waterConnectionDetails.getConnection().setParentConnection(primaryConnectionDetails.getConnection());
        }
        final WaterConnectionDetails savedWaterConnectionDetails = waterConnectionDetailsRepository
                .save(waterConnectionDetails);
        updateConsumerIndex(savedWaterConnectionDetails);
        // TODO Updation of Demand should be done here also fixupdate indexes
        return savedWaterConnectionDetails;
    }

    public List<ConnectionType> getAllConnectionTypes() {
        return Arrays.asList(ConnectionType.values());
    }

    public Map<String, String> getConnectionTypesMap() {
        final Map<String, String> connectionTypeMap = new LinkedHashMap<String, String>(0);
        connectionTypeMap.put(ConnectionType.METERED.toString(), WaterTaxConstants.METERED);
        connectionTypeMap.put(ConnectionType.NON_METERED.toString(), WaterTaxConstants.NON_METERED);
        return connectionTypeMap;
    }

    public List<DocumentNames> getAllActiveDocumentNames(final ApplicationType applicationType) {
        return documentNamesService.getAllActiveDocumentNamesByApplicationType(applicationType);
    }

    public WaterConnectionDetails findByApplicationNumberOrConsumerCodeAndStatus(final String number,
            final ConnectionStatus connectionStatus) {
        return waterConnectionDetailsRepository.findByApplicationNumberOrConnection_ConsumerCodeAndConnectionStatus(
                number, number, connectionStatus);
    }

    public WaterConnectionDetails findByApplicationNumberOrConsumerCode(final String number) {
        return waterConnectionDetailsRepository.findByApplicationNumberOrConnection_ConsumerCode(number, number);
    }

    public WaterConnectionDetails findByConnection(final WaterConnection waterConnection) {
        return waterConnectionDetailsRepository.findByConnection(waterConnection);
    }

    public WaterConnectionDetails findByConsumerCodeAndConnectionStatus(final String comsumerCode,
            final ConnectionStatus connectionStatus) {
        return waterConnectionDetailsRepository.findByConnection_ConsumerCodeAndConnectionStatus(comsumerCode,
                connectionStatus);
    }

    public WaterConnectionDetails getActiveConnectionDetailsByConnection(final WaterConnection waterConnection) {
        return waterConnectionDetailsRepository.findByConnectionAndConnectionStatus(waterConnection,
                ConnectionStatus.ACTIVE);
    }

    public WaterConnectionDetails getPrimaryConnectionDetailsByPropertyIdentifier(final String propertyIdentifier) {
        return waterConnectionDetailsRepository.getPrimaryConnectionDetailsByPropertyID(propertyIdentifier);
    }

    public List<WaterConnectionDetails> getAllConnectionDetailsByParentConnection(final Long parentId) {
        return waterConnectionDetailsRepository.getAllConnectionDetailsByParentConnection(parentId);
    }

    public List<Hashtable<String, Object>> getHistory(final WaterConnectionDetails waterConnectionDetails) {
        User user = null;
        final List<Hashtable<String, Object>> historyTable = new ArrayList<Hashtable<String, Object>>(0);
        final State state = waterConnectionDetails.getState();
        final Hashtable<String, Object> map = new Hashtable<String, Object>(0);
        if (null != state) {
            map.put("date", state.getDateInfo());
            map.put("comments", state.getComments() != null ? state.getComments() : "");
            map.put("updatedBy", state.getLastModifiedBy().getUsername() + "::" + state.getLastModifiedBy().getName());
            map.put("status", state.getValue());
            final Position ownerPosition = state.getOwnerPosition();
            user = state.getOwnerUser();
            if (null != user) {
                map.put("user", user.getUsername() + "::" + user.getName());
                map.put("department", null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
                        .getDepartmentForUser(user.getId()).getName() : "");
            } else if (null != ownerPosition && null != ownerPosition.getDeptDesig()) {
                user = eisCommonService.getUserForPosition(ownerPosition.getId(), new Date());
                map.put("user", null != user.getUsername() ? user.getUsername() + "::" + user.getName() : "");
                map.put("department", null != ownerPosition.getDeptDesig().getDepartment() ? ownerPosition
                        .getDeptDesig().getDepartment().getName() : "");
            }
            historyTable.add(map);
            if (!waterConnectionDetails.getStateHistory().isEmpty() && waterConnectionDetails.getStateHistory() != null)
                Collections.reverse(waterConnectionDetails.getStateHistory());
            for (final StateHistory stateHistory : waterConnectionDetails.getStateHistory()) {
                final Hashtable<String, Object> HistoryMap = new Hashtable<String, Object>(0);
                HistoryMap.put("date", stateHistory.getDateInfo());
                HistoryMap.put("comments", stateHistory.getComments());
                HistoryMap.put("updatedBy", stateHistory.getLastModifiedBy().getUsername() + "::"
                        + stateHistory.getLastModifiedBy().getName());
                HistoryMap.put("status", stateHistory.getValue());
                final Position owner = stateHistory.getOwnerPosition();
                user = stateHistory.getOwnerUser();
                if (null != user) {
                    HistoryMap.put("user", user.getUsername() + "::" + user.getName());
                    HistoryMap.put("department",
                            null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
                                    .getDepartmentForUser(user.getId()).getName() : "");
                } else if (null != owner && null != owner.getDeptDesig()) {
                    user = eisCommonService.getUserForPosition(owner.getId(), new Date());
                    HistoryMap
                    .put("user", null != user.getUsername() ? user.getUsername() + "::" + user.getName() : "");
                    HistoryMap.put("department", null != owner.getDeptDesig().getDepartment() ? owner.getDeptDesig()
                            .getDepartment().getName() : "");
                }
                historyTable.add(HistoryMap);
            }
        }
        return historyTable;
    }

    @Transactional
    public WaterConnectionDetails updateWaterConnection(final WaterConnectionDetails waterConnectionDetails,
            final Long approvalPosition, final String approvalComent, String additionalRule,
            final String workFlowAction, final String mode, final ReportOutput reportOutput, final String sourceChannel)
                    throws ValidationException {
        applicationStatusChange(waterConnectionDetails, workFlowAction, mode, sourceChannel);
        if (WaterTaxConstants.APPLICATION_STATUS_CLOSERDIGSIGNPENDING.equals(waterConnectionDetails.getStatus().getCode())
                && waterConnectionDetails.getCloseConnectionType() != null
                && workFlowAction.equals(WaterTaxConstants.APPROVEWORKFLOWACTION)) {
            waterConnectionDetails.setApplicationType(applicationTypeService
                    .findByCode(WaterTaxConstants.CLOSINGCONNECTION));
            waterConnectionDetails.setCloseApprovalDate(new Date());
        }
        if (WaterTaxConstants.APPLICATION_STATUS_RECONNDIGSIGNPENDING.equals(waterConnectionDetails.getStatus()
                .getCode())
                && waterConnectionDetails.getCloseConnectionType().equals(WaterTaxConstants.TEMPERARYCLOSECODE)
                && waterConnectionDetails.getReConnectionReason() != null
                && workFlowAction.equals(WaterTaxConstants.APPROVEWORKFLOWACTION)) {
            waterConnectionDetails.setApplicationType(applicationTypeService
                    .findByCode(WaterTaxConstants.RECONNECTIONCONNECTION));
            waterConnectionDetails.setConnectionStatus(ConnectionStatus.ACTIVE);
            waterConnectionDetails.setReconnectionApprovalDate(new Date());
            if (ConnectionType.NON_METERED.equals(waterConnectionDetails.getConnectionType())) {
                Installment nonMeterReconnInstallment = null;
                Boolean reconnInSameInstallment = null;
                if (checkTwoDatesAreInSameInstallment(waterConnectionDetails)) {
                    final Installment nonMeterCurrentInstallment = connectionDemandService.getCurrentInstallment(
                            WaterTaxConstants.WATER_RATES_NONMETERED_PTMODULE, null,
                            waterConnectionDetails.getReconnectionApprovalDate());
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime(nonMeterCurrentInstallment.getToDate());
                    cal.add(Calendar.DATE, 1);
                    final Date newDateForNextInstall = cal.getTime();
                    nonMeterReconnInstallment = connectionDemandService.getCurrentInstallment(
                            WaterTaxConstants.WATER_RATES_NONMETERED_PTMODULE, null, newDateForNextInstall);
                    reconnInSameInstallment = Boolean.TRUE;
                } else {
                    nonMeterReconnInstallment = connectionDemandService.getCurrentInstallment(
                            WaterTaxConstants.WATER_RATES_NONMETERED_PTMODULE, null,
                            waterConnectionDetails.getReconnectionApprovalDate());
                    reconnInSameInstallment = Boolean.FALSE;
                }
                connectionDemandService.updateDemandForNonmeteredConnection(waterConnectionDetails,
                        nonMeterReconnInstallment, reconnInSameInstallment);
            }
            updateIndexes(waterConnectionDetails, sourceChannel);
        }

        if (ConnectionType.NON_METERED.equals(waterConnectionDetails.getConnectionType())
                && WaterTaxConstants.APPLICATION_STATUS_SANCTIONED.equalsIgnoreCase(waterConnectionDetails.getStatus()
                        .getCode())) {
            connectionDemandService.updateDemandForNonmeteredConnection(waterConnectionDetails, null, null);
            updateIndexes(waterConnectionDetails, sourceChannel);
        }

        // Setting FileStoreMap object object while Commissioner Sign's the document
        if (workFlowAction != null && workFlowAction.equalsIgnoreCase(WaterTaxConstants.SIGNWORKFLOWACTION)
                && reportOutput != null) {
            final String fileName = WaterTaxConstants.SIGNED_DOCUMENT_PREFIX + waterConnectionDetails.getWorkOrderNumber()
                    + ".pdf";
            final InputStream fileStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
            final FileStoreMapper fileStore = fileStoreService.store(fileStream, fileName, "application/pdf",
                    WaterTaxConstants.FILESTORE_MODULECODE);
            waterConnectionDetails.setFileStore(fileStore);
        }

        WaterConnectionDetails updatedWaterConnectionDetails = waterConnectionDetailsRepository
                .save(waterConnectionDetails);
        final ApplicationWorkflowCustomDefaultImpl applicationWorkflowCustomDefaultImpl = getInitialisedWorkFlowBean();
        if (waterConnectionDetails.getCloseConnectionType() != null)
            additionalRule = WaterTaxConstants.WORKFLOW_CLOSUREADDITIONALRULE;

        if (waterConnectionDetails.getReConnectionReason() != null)
            additionalRule = WaterTaxConstants.RECONNECTIONCONNECTION;
        applicationWorkflowCustomDefaultImpl.createCommonWorkflowTransition(updatedWaterConnectionDetails,
                approvalPosition, approvalComent, additionalRule, workFlowAction);

        // To backUpdate waterConnectiondetails after ClosureConnection is
        // cancelled
        if (waterConnectionDetails.getCloseConnectionType() != null
                && waterConnectionDetails.getReConnectionReason() == null
                && waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CANCELLED)
                && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.INACTIVE)) {
            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_SANCTIONED, WaterTaxConstants.MODULETYPE));
            waterConnectionDetails.setConnectionStatus(ConnectionStatus.ACTIVE);
            waterConnectionDetails.setCloseConnectionType(null);
            waterConnectionDetails.setCloseconnectionreason(null);
            waterConnectionDetails.setApplicationType(applicationTypeService.findByCode(waterConnectionDetails
                    .getPreviousApplicationType()));
            updateIndexes(waterConnectionDetails, sourceChannel);
            updatedWaterConnectionDetails = waterConnectionDetailsRepository.save(waterConnectionDetails);
        }
        // back to CLoserSanctioned Status if Reconnection is Rejected 2 times
        if (waterConnectionDetails.getReConnectionReason() != null
                && waterConnectionDetails.getCloseConnectionType() == WaterTaxConstants.TEMPERARYCLOSECODE
                && waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CANCELLED)
                && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.INACTIVE)) {
            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED, WaterTaxConstants.MODULETYPE));
            waterConnectionDetails.setConnectionStatus(ConnectionStatus.CLOSED);
            waterConnectionDetails.setReConnectionReason(null);
            waterConnectionDetails.setApplicationType(applicationTypeService
                    .findByCode(WaterTaxConstants.CLOSINGCONNECTION));
            updateIndexes(waterConnectionDetails, sourceChannel);
            updatedWaterConnectionDetails = waterConnectionDetailsRepository.save(waterConnectionDetails);
        }
        if (!workFlowAction.equalsIgnoreCase(WFLOW_ACTION_STEP_REJECT))
            waterConnectionSmsAndEmailService.sendSmsAndEmail(waterConnectionDetails, workFlowAction);

        updateIndexes(waterConnectionDetails, sourceChannel);

        return updatedWaterConnectionDetails;
    }

    /**
     * @return Initialise Bean ApplicationWorkflowCustomDefaultImpl
     */
    public ApplicationWorkflowCustomDefaultImpl getInitialisedWorkFlowBean() {
        ApplicationWorkflowCustomDefaultImpl applicationWorkflowCustomDefaultImpl = null;
        if (null != context)
            applicationWorkflowCustomDefaultImpl = (ApplicationWorkflowCustomDefaultImpl) context
            .getBean("applicationWorkflowCustomDefaultImpl");
        return applicationWorkflowCustomDefaultImpl;
    }

    public Boolean checkTwoDatesAreInSameInstallment(final WaterConnectionDetails waterConnectionDetails) {
        Boolean DateInSameInstallment = Boolean.FALSE;

        final Installment nonMeterClosedInstallment = connectionDemandService.getCurrentInstallment(
                WaterTaxConstants.WATER_RATES_NONMETERED_PTMODULE, null, waterConnectionDetails.getCloseApprovalDate());
        final Installment nonMeterReconnInstallment = connectionDemandService.getCurrentInstallment(
                WaterTaxConstants.WATER_RATES_NONMETERED_PTMODULE, null,
                waterConnectionDetails.getReconnectionApprovalDate());
        if (nonMeterClosedInstallment.getInstallmentNumber().equals(nonMeterReconnInstallment.getInstallmentNumber()))
            DateInSameInstallment = Boolean.TRUE;

        return DateInSameInstallment;
    }

    public void applicationStatusChange(final WaterConnectionDetails waterConnectionDetails,
            final String workFlowAction, final String mode, final String sourceChannel) {
        if (null != waterConnectionDetails && null != waterConnectionDetails.getStatus()
                && null != waterConnectionDetails.getStatus().getCode())
            if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CREATED)
                    && waterConnectionDetails.getState() != null && workFlowAction.equals("Submit"))
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_VERIFIED, WaterTaxConstants.MODULETYPE));
            else if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_VERIFIED))
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN, WaterTaxConstants.MODULETYPE));
            else if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN))
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_FEEPAID, WaterTaxConstants.MODULETYPE));
            else if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)
                    && workFlowAction.equalsIgnoreCase(WaterTaxConstants.APPROVEWORKFLOWACTION)) {

                if (waterConnectionDetails.getConnection().getConsumerCode() == null)
                    waterConnectionDetails.getConnection().setConsumerCode(
                            waterTaxNumberGenerator.generateConsumerNumber());

                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_DIGITALSIGNPENDING, WaterTaxConstants.MODULETYPE));

            } else if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_DIGITALSIGNPENDING))
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_APPROVED, WaterTaxConstants.MODULETYPE));
            else if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_APPROVED))
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_WOGENERATED, WaterTaxConstants.MODULETYPE));
            else if (WaterTaxConstants.APPLICATION_STATUS_WOGENERATED.equalsIgnoreCase(waterConnectionDetails
                    .getStatus().getCode()))
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_SANCTIONED, WaterTaxConstants.MODULETYPE));
            else if (WaterTaxConstants.APPLICATION_STATUS_SANCTIONED.equalsIgnoreCase(waterConnectionDetails
                    .getStatus().getCode()) && waterConnectionDetails.getCloseConnectionType() != null) {
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_CLOSERINITIATED, WaterTaxConstants.MODULETYPE));
                updateIndexes(waterConnectionDetails, sourceChannel);
            } else if (!workFlowAction.equals("Reject"))
                if (!mode.equals("closeredit")
                        && WaterTaxConstants.APPLICATION_STATUS_CLOSERINITIATED.equalsIgnoreCase(waterConnectionDetails
                                .getStatus().getCode()) && waterConnectionDetails.getCloseConnectionType() != null)
                    waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                            WaterTaxConstants.APPLICATION_STATUS_CLOSERINPROGRESS, WaterTaxConstants.MODULETYPE));
                else if (workFlowAction.equals("Approve")
                        && WaterTaxConstants.APPLICATION_STATUS_CLOSERINPROGRESS
                        .equalsIgnoreCase(waterConnectionDetails.getStatus().getCode())
                        && waterConnectionDetails.getCloseConnectionType() != null)
                    waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                            WaterTaxConstants.APPLICATION_STATUS_CLOSERDIGSIGNPENDING, WaterTaxConstants.MODULETYPE));
                else if (WaterTaxConstants.APPLICATION_STATUS_CLOSERDIGSIGNPENDING.equalsIgnoreCase(waterConnectionDetails
                        .getStatus().getCode()) && waterConnectionDetails.getCloseConnectionType() != null) {
                    waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                            WaterTaxConstants.APPLICATION_STATUS_CLOSERAPRROVED, WaterTaxConstants.MODULETYPE));

                    updateIndexes(waterConnectionDetails, sourceChannel);
                } else if (WaterTaxConstants.APPLICATION_STATUS_CLOSERAPRROVED.equalsIgnoreCase(waterConnectionDetails
                        .getStatus().getCode()) && waterConnectionDetails.getCloseConnectionType() != null) {
                    waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                            WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED, WaterTaxConstants.MODULETYPE));
                    updateIndexes(waterConnectionDetails, sourceChannel);
                } else if (WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED
                        .equalsIgnoreCase(waterConnectionDetails.getStatus().getCode())
                        && waterConnectionDetails.getCloseConnectionType().equals(WaterTaxConstants.TEMPERARYCLOSECODE)) {
                    waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                            WaterTaxConstants.WORKFLOW_RECONNCTIONINITIATED, WaterTaxConstants.MODULETYPE));
                    updateIndexes(waterConnectionDetails, sourceChannel);
                } else if (!workFlowAction.equals("Reject"))
                    if (!mode.equals("reconnectioneredit"))
                        if (WaterTaxConstants.WORKFLOW_RECONNCTIONINITIATED.equalsIgnoreCase(waterConnectionDetails
                                .getStatus().getCode())
                                && waterConnectionDetails.getCloseConnectionType().equals(
                                        WaterTaxConstants.TEMPERARYCLOSECODE))
                            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                                    WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONINPROGRESS,
                                    WaterTaxConstants.MODULETYPE));
                        else if (workFlowAction.equals("Approve")
                                && WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONINPROGRESS
                                .equalsIgnoreCase(waterConnectionDetails.getStatus().getCode())
                                && waterConnectionDetails.getCloseConnectionType().equals(
                                        WaterTaxConstants.TEMPERARYCLOSECODE))
                            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                                    WaterTaxConstants.APPLICATION_STATUS_RECONNDIGSIGNPENDING,
                                    WaterTaxConstants.MODULETYPE));
                        else if (WaterTaxConstants.APPLICATION_STATUS_RECONNDIGSIGNPENDING
                                .equalsIgnoreCase(waterConnectionDetails.getStatus().getCode())
                                && waterConnectionDetails.getCloseConnectionType().equals(
                                        WaterTaxConstants.TEMPERARYCLOSECODE))
                            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                                    WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONAPPROVED,
                                    WaterTaxConstants.MODULETYPE));
                        else if (WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONAPPROVED
                                .equalsIgnoreCase(waterConnectionDetails.getStatus().getCode())
                                && waterConnectionDetails.getCloseConnectionType().equals(
                                        WaterTaxConstants.TEMPERARYCLOSECODE))
                            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                                    WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONSANCTIONED,
                                    WaterTaxConstants.MODULETYPE));

    }

    public Long getApprovalPositionByMatrixDesignation(final WaterConnectionDetails waterConnectionDetails,
            Long approvalPosition, final String additionalRule, final String mode, final String workFlowAction) {
        final WorkFlowMatrix wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails
                .getStateType(), null, null, additionalRule, waterConnectionDetails.getCurrentState().getValue(), null);
        if (waterConnectionDetails.getStatus() != null && waterConnectionDetails.getStatus().getCode() != null)
            if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CREATED)
                    && waterConnectionDetails.getState() != null)
                if (mode.equals("edit"))
                    approvalPosition = waterConnectionDetails.getState().getOwnerPosition().getId();
                else
                    approvalPosition = waterTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                            waterConnectionDetails);
            else if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_APPROVED)
                    || WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN.equalsIgnoreCase(waterConnectionDetails
                            .getStatus().getCode())
                            || !"".equals(workFlowAction)
                            && workFlowAction.equals(WFLOW_ACTION_STEP_REJECT)
                            && waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINITIATED)
                            && waterConnectionDetails.getState().getValue().equals(WaterTaxConstants.WF_STATE_REJECTED))
                approvalPosition = waterTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                        waterConnectionDetails);
            else if (wfmatrix.getNextDesignation() != null
                    && (waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)
                            || waterConnectionDetails.getLegacy().equals(false) && waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINPROGRESS)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERAPRROVED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERDIGSIGNPENDING)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_RECONNDIGSIGNPENDING)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_DIGITALSIGNPENDING)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONAPPROVED)
                            || workFlowAction.equals(WFLOW_ACTION_STEP_REJECT)
                            && waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.WORKFLOW_RECONNCTIONINITIATED) || workFlowAction
                            .equals(WFLOW_ACTION_STEP_REJECT)
                            && waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.WORKFLOW_RECONNCTIONINITIATED) || workFlowAction
                            .equals(WFLOW_ACTION_STEP_REJECT)
                            && waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINITIATED)))
                approvalPosition = waterTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                        waterConnectionDetails);
            else if (wfmatrix.getNextDesignation() != null
                    && (waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_VERIFIED)
                            || !workFlowAction.equals(WFLOW_ACTION_STEP_REJECT)
                            && waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINITIATED) || !waterConnectionDetails
                            .getState().getValue().equals(WaterTaxConstants.WF_STATE_REJECTED)
                            && !workFlowAction.equals(WFLOW_ACTION_STEP_REJECT)
                            && (waterConnectionDetails.getStatus().getCode()
                                    .equals(WaterTaxConstants.WORKFLOW_RECONNCTIONINITIATED) || waterConnectionDetails
                                    .getStatus().getCode()
                                    .equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONINPROGRESS)))
                    || waterConnectionDetails.getLegacy().equals(true) && waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINPROGRESS)) {
                final Position posobj = waterTaxUtils.getCityLevelCommissionerPosition(wfmatrix.getNextDesignation());
                if (posobj != null)
                    approvalPosition = posobj.getId();
            }

        return approvalPosition;
    }

    public void updateConsumerIndex(final WaterConnectionDetails waterConnectionDetails) {
        final AssessmentDetails assessmentDetails = propertyExtnUtils.getAssessmentDetailsForFlag(
                waterConnectionDetails.getConnection().getPropertyIdentifier(),
                PropertyExternalService.FLAG_FULL_DETAILS, BasicPropertyStatus.ALL);
        final BigDecimal amountTodisplayInIndex = getTotalAmount(waterConnectionDetails);
        if (waterConnectionDetails.getLegacy())
            consumerIndexService.createConsumerIndex(waterConnectionDetails, assessmentDetails, amountTodisplayInIndex);
    }

    public void updateIndexes(final WaterConnectionDetails waterConnectionDetails, final String sourceChannel) {
        final AssessmentDetails assessmentDetails = propertyExtnUtils.getAssessmentDetailsForFlag(
                waterConnectionDetails.getConnection().getPropertyIdentifier(),
                PropertyExternalService.FLAG_FULL_DETAILS, BasicPropertyStatus.ALL);
        if (LOG.isDebugEnabled())
            LOG.debug(" updating Indexes Started... ");
        BigDecimal amountTodisplayInIndex = BigDecimal.ZERO;
        if (waterConnectionDetails.getConnection().getConsumerCode() != null)
            amountTodisplayInIndex = getTotalAmount(waterConnectionDetails);
        if (waterConnectionDetails.getLegacy()
                && (null == waterConnectionDetails.getId() || null != waterConnectionDetails.getId()
                        && waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_SANCTIONED))) {
            consumerIndexService.createConsumerIndex(waterConnectionDetails, assessmentDetails, amountTodisplayInIndex);
            return;
        }
        Iterator<OwnerName> ownerNameItr = null;
        if (null != assessmentDetails.getOwnerNames())
            ownerNameItr = assessmentDetails.getOwnerNames().iterator();
        final StringBuilder consumerName = new StringBuilder();
        final StringBuilder mobileNumber = new StringBuilder();
        Assignment assignment = null;
        User user = null;
        final StringBuilder aadharNumber = new StringBuilder();
        if (null != ownerNameItr && ownerNameItr.hasNext()) {
            final OwnerName primaryOwner = ownerNameItr.next();
            consumerName.append(primaryOwner.getOwnerName() != null ? primaryOwner.getOwnerName() : "");
            mobileNumber.append(primaryOwner.getMobileNumber() != null ? primaryOwner.getMobileNumber() : "");
            aadharNumber.append(primaryOwner.getAadhaarNumber() != null ? primaryOwner.getAadhaarNumber() : "");
            while (ownerNameItr.hasNext()) {
                final OwnerName secondaryOwner = ownerNameItr.next();
                consumerName.append(",").append(secondaryOwner.getOwnerName() != null ? secondaryOwner.getOwnerName() : "");
                mobileNumber.append(",").append(secondaryOwner.getMobileNumber() != null ? secondaryOwner.getMobileNumber() : "");
                aadharNumber.append(",").append(
                        secondaryOwner.getAadhaarNumber() != null ? secondaryOwner.getAadhaarNumber() : "");
            }

        }
        List<Assignment> asignList = null;
        if (waterConnectionDetails.getState() != null && waterConnectionDetails.getState().getOwnerPosition() != null) {
            assignment = assignmentService.getPrimaryAssignmentForPositionAndDate(waterConnectionDetails.getState()
                    .getOwnerPosition()
                    .getId(), new Date());
            if (assignment != null)
            {
                asignList = new ArrayList<Assignment>();
                asignList.add(assignment);
            }
            else if (assignment == null)
                asignList = assignmentService.getAssignmentsForPosition(waterConnectionDetails.getState().getOwnerPosition()
                        .getId(), new Date());
            if (!asignList.isEmpty())
                user = userService.getUserById(asignList.get(0).getEmployee().getId());
        } else
            user = securityUtils.getCurrentUser();
        ApplicationIndex applicationIndex = applicationIndexService
                .findByApplicationNumber(waterConnectionDetails.getApplicationNumber());
        if (applicationIndex != null && null != waterConnectionDetails.getId() && waterConnectionDetails.getStatus() != null
                && !waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CREATED)) {
            if (waterConnectionDetails.getStatus() != null
                    && (waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_APPROVED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_VERIFIED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_DIGITALSIGNPENDING)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CANCELLED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINITIATED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINPROGRESS)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERAPRROVED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERDIGSIGNPENDING)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.WORKFLOW_RECONNCTIONINITIATED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONINPROGRESS)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONSANCTIONED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_RECONNDIGSIGNPENDING)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_WOGENERATED)
                            || waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_SANCTIONED) || waterConnectionDetails
                            .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED))) {
                applicationIndex.setApplicantAddress(assessmentDetails.getPropertyAddress());
                applicationIndex.setOwnername(user.getUsername() + "::" + user.getName());
                applicationIndex.setApproved(ApprovalStatus.UNKNOWN);
                applicationIndex.setClosed(ClosureStatus.NO);
                applicationIndex.setStatus(waterConnectionDetails.getStatus().getDescription());
                if (sourceChannel == null)
                    applicationIndex.setChannel(WaterTaxConstants.SYSTEM);
                else
                    applicationIndex.setChannel(sourceChannel);
                int elapsedDays = 0;
                if (waterConnectionDetails
                        .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED)
                        || waterConnectionDetails
                        .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONSANCTIONED)) {
                    final List<StateHistory> stateHistoryList = waterConnectionDetails.getState().getHistory();
                    Date applicationDate = null;
                    for (final StateHistory stateHistory : stateHistoryList)
                        if (stateHistory.getValue().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSER)
                                || stateHistory.getValue().equals(WaterTaxConstants.APPLICATION_STATUS_RE_CONN))
                            applicationDate = stateHistory.getLastModifiedDate();
                    if (applicationDate != null)
                        elapsedDays = DateUtils.noOfDays(applicationDate, waterConnectionDetails.getLastModifiedDate());
                    applicationIndex.setElapsedDays(elapsedDays);
                }
                if (waterConnectionDetails
                        .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_SANCTIONED))
                    applicationIndex.setElapsedDays(DateUtils.noOfDays(waterConnectionDetails.getApplicationDate(),
                            waterConnectionDetails.getExecutionDate()));
                if (waterConnectionDetails
                        .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONSANCTIONED)
                        || waterConnectionDetails
                        .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_SANCTIONED)
                        || waterConnectionDetails
                        .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED)) {
                    applicationIndex.setApproved(ApprovalStatus.APPROVED);
                    applicationIndex.setClosed(ClosureStatus.YES);
                }
                if (waterConnectionDetails
                        .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CANCELLED))
                {
                    applicationIndex.setApproved(ApprovalStatus.REJECTED);
                    applicationIndex.setClosed(ClosureStatus.YES);
                }
                if (waterConnectionDetails.getConnection().getConsumerCode() != null)
                    applicationIndex.setConsumerCode(waterConnectionDetails.getConnection().getConsumerCode());
                applicationIndexService.updateApplicationIndex(applicationIndex);
            }
            // Creating Consumer Index only on Sanction
            if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_SANCTIONED))
                if (waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.INPROGRESS)
                        && !waterConnectionDetails.getApplicationType().getCode()
                        .equalsIgnoreCase(WaterTaxConstants.CHANGEOFUSE)) {
                    waterConnectionDetails.setConnectionStatus(ConnectionStatus.ACTIVE);
                    if (LOG.isDebugEnabled())
                        LOG.debug(" updating Consumer Index Started... ");
                    if (!waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.INACTIVE)
                            || !waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.INPROGRESS))
                        consumerIndexService.createConsumerIndex(waterConnectionDetails, assessmentDetails,
                                amountTodisplayInIndex);
                    if (LOG.isDebugEnabled())
                        LOG.debug(" updating Consumer Index completed... ");
                }
            // To Update After ClosureConnection is rejected
            if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_SANCTIONED)
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE))
                consumerIndexService.createConsumerIndex(waterConnectionDetails, assessmentDetails,
                        amountTodisplayInIndex);
            if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED)
                    || waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERAPRROVED)
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.CLOSED))
                consumerIndexService.createConsumerIndex(waterConnectionDetails, assessmentDetails,
                        amountTodisplayInIndex);

            if (waterConnectionDetails.getCloseConnectionType() != null
                    && waterConnectionDetails.getCloseConnectionType().equals(WaterTaxConstants.TEMPERARYCLOSECODE)
                    && (waterConnectionDetails.getStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONAPPROVED) || waterConnectionDetails
                            .getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONSANCTIONED))) {
                waterConnectionDetails.setConnectionStatus(ConnectionStatus.ACTIVE);
                consumerIndexService.createConsumerIndex(waterConnectionDetails, assessmentDetails,
                        amountTodisplayInIndex);
            }
        } else {
            final String strQuery = "select md from EgModules md where md.name=:name";
            final Query hql = getCurrentSession().createQuery(strQuery);
            hql.setParameter("name", WaterTaxConstants.EGMODULES_NAME);
            if (waterConnectionDetails.getApplicationDate() == null)
                waterConnectionDetails.setApplicationDate(new Date());
            if (waterConnectionDetails.getApplicationNumber() == null)
                waterConnectionDetails.setApplicationNumber(waterConnectionDetails.getConnection().getConsumerCode());
            if (applicationIndex == null) {
                if (LOG.isDebugEnabled())
                    LOG.debug(" updating Application Index creation Started... ");
                final ApplicationIndexBuilder applicationIndexBuilder = new ApplicationIndexBuilder(
                        ((EgModules) hql.uniqueResult()).getName(), waterConnectionDetails.getApplicationNumber(),
                        waterConnectionDetails.getApplicationDate(), waterConnectionDetails.getApplicationType().getName(),
                        consumerName.toString(), waterConnectionDetails.getStatus().getDescription().toString(),
                        "/wtms/application/view/" + waterConnectionDetails.getApplicationNumber(),
                        assessmentDetails.getPropertyAddress(), user.getUsername() + "::" + user.getName());

                if (waterConnectionDetails.getDisposalDate() != null)
                    applicationIndexBuilder.disposalDate(waterConnectionDetails.getDisposalDate());
                applicationIndexBuilder.mobileNumber(mobileNumber.toString());
                applicationIndexBuilder.aadharNumber(aadharNumber.toString());
                applicationIndexBuilder.closed(ClosureStatus.NO);
                applicationIndexBuilder.approved(ApprovalStatus.UNKNOWN);
                if (sourceChannel == null)
                    applicationIndexBuilder.channel(WaterTaxConstants.SYSTEM);
                else
                    applicationIndexBuilder.channel(sourceChannel);
                applicationIndex = applicationIndexBuilder.build();
                if (!waterConnectionDetails.getLegacy() && !waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_SANCTIONED))
                    applicationIndexService.createApplicationIndex(applicationIndex);
            }
            if (LOG.isDebugEnabled())
                LOG.debug(" updating Application Index creation complted... ");
        }
    }

    public Date getDisposalDate(final WaterConnectionDetails waterConnectionDetails, final Integer appProcessTime) {
        final Calendar c = Calendar.getInstance();
        c.setTime(waterConnectionDetails.getApplicationDate());
        c.add(Calendar.DATE, appProcessTime);
        return c.getTime();
    }

    public WaterConnectionDetails getParentConnectionDetails(final String propertyIdentifier,
            final ConnectionStatus connectionStatus) {
        return waterConnectionDetailsRepository
                .findByConnection_PropertyIdentifierAndConnectionStatusAndConnection_ParentConnectionIsNull(
                        propertyIdentifier, connectionStatus);
    }

    public WaterConnectionDetails getParentConnectionDetailsForParentConnectionNotNull(final String consumercode,
            final ConnectionStatus connectionStatus) {
        return waterConnectionDetailsRepository
                .findByConnection_ConsumerCodeAndConnectionStatusAndAndConnection_ParentConnectionIsNotNull(
                        consumercode, connectionStatus);
    }

    public WaterConnectionDetails getWaterConnectionDetailsByDemand(final EgDemand demand) {
        return waterConnectionDetailsRepository.findByDemand(demand);
    }

    @Transactional
    public void save(final WaterConnectionDetails detail) {
        waterConnectionDetailsRepository.save(detail);
    }

    public WaterConnectionDetails getActiveNonHistoryConnectionDetailsByConnection(final WaterConnection waterConnection) {
        return waterConnectionDetailsRepository.findByConnectionAndConnectionStatusAndIsHistory(waterConnection,
                ConnectionStatus.ACTIVE, Boolean.FALSE);
    }

    public BigDecimal getTotalAmount(final WaterConnectionDetails waterConnectionDetails) {
        final EgDemand currentDemand = waterConnectionDetails.getDemand();
        BigDecimal balance = BigDecimal.ZERO;
        if (currentDemand != null) {
            final List<Object> instVsAmt = connectionDemandService.getDmdCollAmtInstallmentWise(currentDemand);
            for (final Object object : instVsAmt) {
                final Object[] ddObject = (Object[]) object;
                final BigDecimal dmdAmt = new BigDecimal((Double) ddObject[2]);
                BigDecimal collAmt = BigDecimal.ZERO;
                if (ddObject[2] != null)
                    collAmt = new BigDecimal((Double) ddObject[3]);
                balance = balance.add(dmdAmt.subtract(collAmt));
            }
        }
        return balance;
    }

    public List<ApplicationDocuments> getApplicationDocForExceptClosureAndReConnection(
            final WaterConnectionDetails waterConnectionDetails) {
        final List<ApplicationDocuments> tempDocList = new ArrayList<ApplicationDocuments>(0);
        if (waterConnectionDetails != null)
            for (final ApplicationDocuments appDoc : waterConnectionDetails.getApplicationDocs())
                if (appDoc.getDocumentNames() != null
                && (appDoc.getDocumentNames().getApplicationType().getCode()
                        .equals(WaterTaxConstants.NEWCONNECTION)
                        || appDoc.getDocumentNames().getApplicationType().getCode()
                        .equals(WaterTaxConstants.ADDNLCONNECTION) || appDoc.getDocumentNames()
                        .getApplicationType().getCode().equals(WaterTaxConstants.CHANGEOFUSE)))
                    tempDocList.add(appDoc);
        return tempDocList;
    }

    public WaterConnectionDetails createNewWaterConnection(final WaterConnectionDetails waterConnectionDetails,
            final Long approvalPosition, final String approvalComent, final String code, final String workFlowAction,
            final HashMap<String, String> meesevaParams, final String sourceChannel) {
        return createNewWaterConnection(waterConnectionDetails, approvalPosition, approvalComent, code, workFlowAction,
                sourceChannel);

    }

    public void validateWaterRateAndDonationHeader(final WaterConnectionDetails waterConnectionDetails, final BindingResult errors)
    {
        final DonationDetails donationDetails = connectionDemandService.getDonationDetails(waterConnectionDetails);
        if (donationDetails == null)
            throw new ValidationException("donation.combination.required");
        if (waterConnectionDetails.getConnectionType().equals(ConnectionType.NON_METERED)) {
            final WaterRatesDetails waterRatesDetails = connectionDemandService
                    .getWaterRatesDetailsForDemandUpdate(waterConnectionDetails);
            if (waterRatesDetails == null)
                throw new ValidationException("err.water.rate.not.found");
        }
    }

    /*
     * public void validateWaterRateAndDonationHeader(final WaterConnectionDetails waterConnectionDetails, final BindingResult
     * errors) { DonationDetails donationDetails = connectionDemandService.getDonationDetails(waterConnectionDetails);
     * if(donationDetails ==null) { errors.rejectValue("usageType", "donation.combination.required"); }
     * if(waterConnectionDetails.getConnectionType().name().equals(ConnectionType.NON_METERED)){ WaterRatesDetails
     * waterRatesDetails =connectionDemandService.getWaterRatesDetailsForDemandUpdate(waterConnectionDetails);
     * if(waterRatesDetails==null){ errors.rejectValue("usageType", "err.water.rate.not.found"); } } }
     */
    public String getApprovalPositionOnValidate(final Long approvalPositionId) {
        Assignment assignmentObj = null;
        final List<Assignment> assignmentList = new ArrayList<Assignment>();
        if (approvalPositionId != null && approvalPositionId != 0 && approvalPositionId != -1) {
            assignmentObj = assignmentService.getPrimaryAssignmentForPositionAndDate(approvalPositionId, new Date());
            assignmentList.add(assignmentObj);

            final Gson jsonCreator = new GsonBuilder().registerTypeAdapter(Assignment.class, new AssignmentAdaptor())
                    .create();
            return jsonCreator.toJson(assignmentList, new TypeToken<Collection<Assignment>>() {
            }.getType());
        }
        return "[]";
    }

    @Transactional
    public WaterConnectionDetails updateWaterConnectionDetailsWithFileStore(final WaterConnectionDetails waterConnectionDetails) {
        final WaterConnectionDetails upadtedWaterConnectionDetails = entityManager.merge(waterConnectionDetails);
        return upadtedWaterConnectionDetails;
    }
}
