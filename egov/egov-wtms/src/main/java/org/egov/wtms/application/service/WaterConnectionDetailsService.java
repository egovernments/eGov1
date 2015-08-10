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
import static org.egov.wtms.utils.constants.WaterTaxConstants.WF_STATE_REJECTED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.EgModules;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.eis.service.PositionMasterService;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.search.elastic.entity.ApplicationIndex;
import org.egov.infra.search.elastic.entity.ApplicationIndexBuilder;
import org.egov.infra.search.elastic.service.ApplicationIndexService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.ApplicationNumberGenerator;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.workflow.WorkFlowMatrix;
import org.egov.pims.commons.Position;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.OwnerName;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.wtms.application.entity.WaterConnection;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.repository.WaterConnectionDetailsRepository;
import org.egov.wtms.elasticSearch.service.ConsumerIndexService;
import org.egov.wtms.masters.entity.ApplicationType;
import org.egov.wtms.masters.entity.DocumentNames;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.masters.entity.enums.ConnectionType;
import org.egov.wtms.masters.service.ApplicationProcessTimeService;
import org.egov.wtms.masters.service.DocumentNamesService;
import org.egov.wtms.utils.ConsumerNumberGenerator;
import org.egov.wtms.utils.WaterTaxUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.elasticsearch.common.joda.time.DateTime;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WaterConnectionDetailsService {

    protected WaterConnectionDetailsRepository waterConnectionDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ApplicationNumberGenerator applicationNumberGenerator;

    @Autowired
    private SimpleWorkflowService<WaterConnectionDetails> waterConnectionWorkflowService;

    @Autowired
    private ApplicationProcessTimeService applicationProcessTimeService;

    @Autowired
    private ApplicationIndexService applicationIndexService;

    @Autowired
    private DocumentNamesService documentNamesService;

    @Autowired
    private ConnectionDemandService connectionDemandService;

    @Autowired
    private PropertyExternalService propertyExternalService;

    @Autowired
    private EisCommonService eisCommonService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private PositionMasterService positionMasterService;

    @Autowired
    private ConsumerNumberGenerator consumerNumberGenerator;

    @Autowired
    private ConsumerIndexService consumerIndexService;

    @Autowired
    private WaterTaxUtils waterTaxUtils;

    private String applicantName;

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
            final String workFlowAction) {
        if (waterConnectionDetails.getApplicationNumber() == null)
            waterConnectionDetails.setApplicationNumber(applicationNumberGenerator.generate());
        waterConnectionDetails.setApplicationDate(new Date());
        final Integer appProcessTime = applicationProcessTimeService.getApplicationProcessTime(
                waterConnectionDetails.getApplicationType(), waterConnectionDetails.getCategory());
        waterConnectionDetails.setDemand(connectionDemandService.createDemand(waterConnectionDetails));
        if (appProcessTime != null)
            waterConnectionDetails.setDisposalDate(getDisposalDate(waterConnectionDetails, appProcessTime));
        final WaterConnectionDetails savedWaterConnectionDetails = waterConnectionDetailsRepository
                .save(waterConnectionDetails);

        createMatrixWorkflowTransition(savedWaterConnectionDetails, approvalPosition, approvalComent, additionalRule,
                workFlowAction);

        updateIndexes(savedWaterConnectionDetails);
        sendSmsAndEmail(waterConnectionDetails);

        return savedWaterConnectionDetails;
    }

    private void sendSmsAndEmail(final WaterConnectionDetails waterConnectionDetails) {
        final AssessmentDetails assessmentDetails = propertyExternalService.loadAssessmentDetails(
                waterConnectionDetails.getConnection().getPropertyIdentifier(),
                PropertyExternalService.FLAG_MOBILE_EMAIL);
        final String email = assessmentDetails.getPrimaryEmail();
        final String mobileNumber = assessmentDetails.getPrimaryMobileNo();
        if (waterConnectionDetails != null && waterConnectionDetails.getApplicationType() != null
                && waterConnectionDetails.getApplicationType().getCode() != null
                && waterConnectionDetails.getState() != null && waterConnectionDetails.getState().getValue() != null)
            if (waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.NEWCONNECTION)
                    && waterConnectionDetails.getState().getValue().equals(WaterTaxConstants.CREATEWORKFLOWSTATE)) {
                buildSMS(waterConnectionDetails, WaterTaxConstants.SMSEMAILTYPENEWCONNCREATE, mobileNumber);
                buildEmail(waterConnectionDetails, WaterTaxConstants.SMSEMAILTYPENEWCONNCREATE, email);
            } else if (waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.NEWCONNECTION)
                    && waterConnectionDetails.getState().getValue().equals(WaterTaxConstants.APPROVED)) {
                buildSMS(waterConnectionDetails, WaterTaxConstants.SMSEMAILTYPENEWCONNAPPROVE, mobileNumber);
                buildEmail(waterConnectionDetails, WaterTaxConstants.SMSEMAILTYPENEWCONNAPPROVE, email);
            } else if (waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.ADDNLCONNECTION)
                    && waterConnectionDetails.getState().getValue().equals(WaterTaxConstants.CREATEWORKFLOWSTATE)) {
                buildSMS(waterConnectionDetails, WaterTaxConstants.SMSEMAILTYPEADDITONALCONNCREATE, mobileNumber);
                buildEmail(waterConnectionDetails, WaterTaxConstants.SMSEMAILTYPEADDITONALCONNCREATE, email);
            } else if (waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.ADDNLCONNECTION)
                    && waterConnectionDetails.getState().getValue().equals(WaterTaxConstants.APPROVED)) {
                buildSMS(waterConnectionDetails, WaterTaxConstants.SMSEMAILTYPEADDITONALCONNAPPROVE, mobileNumber);
                buildEmail(waterConnectionDetails, WaterTaxConstants.SMSEMAILTYPEADDITONALCONNAPPROVE, email);
            }
    }

    public List<ConnectionType> getAllConnectionTypes() {
        return Arrays.asList(ConnectionType.values());
    }

    public Map<String, String> getConnectionTypesMap() {
        final Map<String, String> connectionTypeMap = new LinkedHashMap<String, String>();
        connectionTypeMap.put(ConnectionType.METERED.toString(), WaterTaxConstants.METERED);
        connectionTypeMap.put(ConnectionType.NON_METERED.toString(), WaterTaxConstants.NON_METERED);
        return connectionTypeMap;
    }

    public List<DocumentNames> getAllActiveDocumentNames(final ApplicationType applicationType) {
        return documentNamesService.getAllActiveDocumentNamesByApplicationType(applicationType);
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

    public List<Hashtable<String, Object>> getHistory(final WaterConnectionDetails waterConnectionDetails) {
        User user = null;
        final List<Hashtable<String, Object>> historyTable = new ArrayList<Hashtable<String, Object>>();
        final State state = waterConnectionDetails.getState();
        final Hashtable<String, Object> map = new Hashtable<String, Object>(0);
        if (null != state) {
            map.put("date", state.getDateInfo());
            map.put("comments", state.getComments());
            map.put("updatedBy", state.getLastModifiedBy().getName());
            map.put("status", state.getValue());
            final Position ownerPosition = state.getOwnerPosition();
            user = state.getOwnerUser();
            if (null != user) {
                map.put("user", user.getUsername());
                map.put("department", null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
                        .getDepartmentForUser(user.getId()).getName() : "");
            } else if (null != ownerPosition && null != ownerPosition.getDeptDesig()) {
                user = eisCommonService.getUserForPosition(ownerPosition.getId(), new Date());
                map.put("user", null != user.getUsername() ? user.getUsername() : "");
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
                HistoryMap.put("updatedBy", stateHistory.getLastModifiedBy().getName());
                HistoryMap.put("status", stateHistory.getValue());
                final Position owner = stateHistory.getOwnerPosition();
                user = stateHistory.getOwnerUser();
                if (null != user) {
                    HistoryMap.put("user", user.getUsername());
                    HistoryMap.put("department",
                            null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
                                    .getDepartmentForUser(user.getId()).getName() : "");
                } else if (null != owner && null != owner.getDeptDesig()) {
                    user = eisCommonService.getUserForPosition(owner.getId(), new Date());
                    HistoryMap.put("user", null != user.getUsername() ? user.getUsername() : "");
                    HistoryMap.put("department", null != owner.getDeptDesig().getDepartment() ? owner.getDeptDesig()
                            .getDepartment().getName() : "");
                }
                historyTable.add(HistoryMap);
            }
        }
        return historyTable;
    }

    private void createMatrixWorkflowTransition(final WaterConnectionDetails waterConnectionDetails,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction) {
        final User user = securityUtils.getCurrentUser();
        final DateTime currentDate = new DateTime();
        final Assignment userAssignment = assignmentService.getPrimaryAssignmentForUser(user.getId());
        Position pos = null;

        if (WFLOW_ACTION_STEP_REJECT.equalsIgnoreCase(workFlowAction)) {
            final Assignment wfInitiator = assignmentService.getPrimaryAssignmentForUser(waterConnectionDetails
                    .getCreatedBy().getId());
            if (wfInitiator.equals(userAssignment)) {
                waterConnectionDetails.setConnectionStatus(ConnectionStatus.INACTIVE);
                waterConnectionDetails.setEgwStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_CANCELLED, WaterTaxConstants.MODULETYPE));
                waterConnectionDetails.transition(true).end().withSenderName(user.getName())
                .withComments(approvalComent).withDateInfo(currentDate.toDate());
            } else {
                final String stateValue = WF_STATE_REJECTED;
                waterConnectionDetails.transition(true).withSenderName(user.getName()).withComments(approvalComent)
                .withStateValue(stateValue).withDateInfo(currentDate.toDate())
                .withOwner(wfInitiator.getPosition()).withNextAction("Application Rejected");
            }

        } else {
            WorkFlowMatrix wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(),
                    null, null, additionalRule, null, null);
            if (null != approvalPosition && approvalPosition != -1 && !approvalPosition.equals(Long.valueOf(0)))
                pos = positionMasterService.getPositionById(approvalPosition);
            if (null == waterConnectionDetails.getState())
                waterConnectionDetails.transition().start().withSenderName(user.getName()).withComments(approvalComent)
                .withStateValue(wfmatrix.getNextState()).withDateInfo(new Date()).withOwner(pos)
                .withNextAction(wfmatrix.getNextAction());
            else if (workFlowAction.equals(WaterTaxConstants.WF_STATE_TAP_EXECUTION_DATE_BUTTON)
                    || workFlowAction.equals("Enter Meter Details")) {
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                        null, additionalRule, waterConnectionDetails.getCurrentState().getValue(), null);
                waterConnectionDetails.setEgwStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_SANCTIONED, WaterTaxConstants.MODULETYPE));
                waterConnectionDetails.setConnectionStatus(ConnectionStatus.ACTIVE);
                if (wfmatrix.getNextAction().equalsIgnoreCase("END"))
                    waterConnectionDetails.transition(true).end().withSenderName(user.getName())
                            .withComments(approvalComent).withDateInfo(currentDate.toDate());
            } else {
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                        null, additionalRule, waterConnectionDetails.getCurrentState().getValue(), null);

                waterConnectionDetails.transition(true).withSenderName(user.getName()).withComments(approvalComent)
                .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate()).withOwner(pos)
                        .withNextAction(wfmatrix.getNextAction());
            }
        }
    }

    @Transactional
    public WaterConnectionDetails updateNewWaterConnection(final WaterConnectionDetails waterConnectionDetails,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction) {
        applicationStatusChange(waterConnectionDetails, workFlowAction);

        final WaterConnectionDetails updatedWaterConnectionDetails = waterConnectionDetailsRepository
                .save(waterConnectionDetails);

        createMatrixWorkflowTransition(updatedWaterConnectionDetails, approvalPosition, approvalComent, additionalRule,
                workFlowAction);
        sendSmsAndEmail(waterConnectionDetails);
        return updatedWaterConnectionDetails;
    }

    private void applicationStatusChange(final WaterConnectionDetails waterConnectionDetails,
            final String workFlowAction) {
        // TODO: this application status changes needs to do once Assistant
        // Enginneer approves record
        if (waterConnectionDetails.getEgwStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CREATED)
                && waterConnectionDetails.getState() != null)
            waterConnectionDetails.setEgwStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_VERIFIED, WaterTaxConstants.MODULETYPE));
        // TODO: status change from Verified to Estimate generate in clerk inbox
        // ..as off now am skiffing this
        else if (waterConnectionDetails.getEgwStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_VERIFIED))
            /*waterConnectionDetails.setEgwStatus(waterTaxUtils.getstatusbyCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN, WaterTaxConstants.MODULETYPE));
        // TODO: status change from Estimate generated to Estimate Amount paid
        // after collection in clerk ..
        else if (waterConnectionDetails.getEgwStatus().getCode()
                .equals(WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN))*/
            
            //TODO Skipping this status cos no collection as pf now.
            waterConnectionDetails.setEgwStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_FEEPAID, WaterTaxConstants.MODULETYPE));
        // TODO:this application status needs to do after commssioner click on
        // Approve button
        else if (waterConnectionDetails.getEgwStatus() != null
                && waterConnectionDetails.getEgwStatus().getCode() != null
                && waterConnectionDetails.getEgwStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)
                && workFlowAction.equalsIgnoreCase(WaterTaxConstants.APPROVEWORKFLOWACTION)) {

            if (waterConnectionDetails.getConnection().getConsumerCode() == null)
                waterConnectionDetails.getConnection().setConsumerCode(consumerNumberGenerator.generate());
            updateIndexes(waterConnectionDetails);
            waterConnectionDetails.setEgwStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_APPROVED, WaterTaxConstants.MODULETYPE));
        }
        // TODO: this application status needs to do after workflow generated
        else if (waterConnectionDetails.getEgwStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_APPROVED))
            waterConnectionDetails.setEgwStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_WOGENERATED, WaterTaxConstants.MODULETYPE));
       
        updateIndexes(waterConnectionDetails);
    }

    public Long getApprovalPositionByMatrixDesignation(final WaterConnectionDetails waterConnectionDetails,
            Long approvalPosition, final String additionalRule) {
        final WorkFlowMatrix wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails
                .getStateType(), null, null, additionalRule, waterConnectionDetails.getCurrentState().getValue(), null);

        if (waterConnectionDetails.getEgwStatus() != null && waterConnectionDetails.getEgwStatus().getCode() != null)
            if (waterConnectionDetails.getEgwStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CREATED)
                    && waterConnectionDetails.getState() != null)
                approvalPosition = waterTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                        waterConnectionDetails);// AE->Clerk
            else if (waterConnectionDetails.getEgwStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_APPROVED))
                // to get Assistant Engineer from Clerk after Approved
                approvalPosition = waterTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                        waterConnectionDetails);
            else if (waterConnectionDetails.getEgwStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID))
                // to get previous Clerk from Commissioner inbox
                approvalPosition = waterTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                        waterConnectionDetails);
            // this API Needs to call once collection done//&&
            // workFlowAction.equals(WaterTaxConstants.WF_STATE_BUTTON_GENERATEESTIMATE)
            else if (waterConnectionDetails.getEgwStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN)
                    || waterConnectionDetails.getEgwStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_VERIFIED)
                    || waterConnectionDetails.getEgwStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)){
                Position posobj=waterTaxUtils.getCityLevelCommissionerPosition(wfmatrix.getNextDesignation());
                if(posobj!=null)
                approvalPosition = posobj.getId();
            }
        return approvalPosition;
    }

    private void updateIndexes(final WaterConnectionDetails waterConnectionDetails) {

        final AssessmentDetails assessmentDetails = propertyExternalService.loadAssessmentDetails(
                waterConnectionDetails.getConnection().getPropertyIdentifier(),
                PropertyExternalService.FLAG_FULL_DETAILS);

        Iterator<OwnerName> ownerNameItr = assessmentDetails.getOwnerNames().iterator();
        final StringBuilder consumerName = new StringBuilder();
        if (ownerNameItr.hasNext()) {
            consumerName.append(ownerNameItr.next().getOwnerName());
            while (ownerNameItr.hasNext())
                consumerName.append(", ".concat(ownerNameItr.next().getOwnerName()));
        }
        setApplicantName(consumerName.toString());
        if (waterConnectionDetails.getEgwStatus() != null
                && !waterConnectionDetails.getEgwStatus().getCode()
                .equals(WaterTaxConstants.APPLICATION_STATUS_CREATED)) {
            if (waterConnectionDetails.getEgwStatus() != null
                    && (waterConnectionDetails.getEgwStatus().getCode()
                            .equals(WaterTaxConstants.APPLICATION_STATUS_APPROVED)
                            || waterConnectionDetails.getEgwStatus().getCode()
                                    .equals(WaterTaxConstants.APPLICATION_STATUS_VERIFIED)
                            || waterConnectionDetails.getEgwStatus().getCode()
                                    .equals(WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN)
                            || waterConnectionDetails.getEgwStatus().getCode()
                                    .equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)
                            || waterConnectionDetails.getEgwStatus().getCode()
                                    .equals(WaterTaxConstants.APPLICATION_STATUS_WOGENERATED) 
                            || waterConnectionDetails
                            .getEgwStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_SANCTIONED))) {
                final ApplicationIndex applicationIndex = applicationIndexService
                        .findByApplicationNumber(waterConnectionDetails.getApplicationNumber());
                applicationIndex.setStatus(waterConnectionDetails.getConnectionStatus().toString());
                applicationIndexService.updateApplicationIndex(applicationIndex);
            }
            if (waterConnectionDetails.getEgwStatus() != null
                    && waterConnectionDetails.getEgwStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_APPROVED))
                consumerIndexService.createConsumerIndex(waterConnectionDetails, assessmentDetails);
        } else {
            final String strQuery = "select md from EgModules md where md.name=:name";
            final Query hql = getCurrentSession().createQuery(strQuery);
            hql.setParameter("name", WaterTaxConstants.EGMODULES_NAME);

            final ApplicationIndexBuilder applicationIndexBuilder = new ApplicationIndexBuilder(
                    ((EgModules) hql.uniqueResult()).getName(), waterConnectionDetails.getApplicationNumber(),
                    waterConnectionDetails.getApplicationDate(), waterConnectionDetails.getApplicationType().getName(),
                    consumerName.toString(), waterConnectionDetails.getConnectionStatus().toString(),
                    "/wtms/application/view/" + waterConnectionDetails.getApplicationNumber());

            if (waterConnectionDetails.getDisposalDate() != null)
                applicationIndexBuilder.disposalDate(waterConnectionDetails.getDisposalDate());
            ownerNameItr = assessmentDetails.getOwnerNames().iterator();
            if (ownerNameItr != null && ownerNameItr.hasNext())
                applicationIndexBuilder.mobileNumber(ownerNameItr.next().getMobileNumber());
            final ApplicationIndex applicationIndex = applicationIndexBuilder.build();
            applicationIndexService.createApplicationIndex(applicationIndex);
        }
    }

    public void buildSMS(final WaterConnectionDetails waterConnectionDetails, final String type,
            final String mobileNumber) {
        if (mobileNumber != null) {
            String smsMsg = null;
            Boolean flag = Boolean.FALSE;
            if (waterTaxUtils.isSmsEnabled())
                if (type.equalsIgnoreCase(WaterTaxConstants.SMSEMAILTYPENEWCONNCREATE)) {
                    flag = Boolean.TRUE;
                    smsMsg = waterTaxUtils.smsAndEmailBodyByCodeAndArgs("msg.newconncetioncreate.sms",
                            waterConnectionDetails, applicantName);
                } else if (type.equalsIgnoreCase(WaterTaxConstants.SMSEMAILTYPEADDITONALCONNCREATE)) {
                    flag = Boolean.TRUE;
                    smsMsg = waterTaxUtils.smsAndEmailBodyByCodeAndArgs("msg.additionalconncetioncreate.sms",
                            waterConnectionDetails, applicantName);

                } else if (type.equals(WaterTaxConstants.SMSEMAILTYPENEWCONNAPPROVE)) {
                    flag = Boolean.TRUE;
                    smsMsg = waterTaxUtils.smsAndEmailBodyByCodeAndArgs("msg.newconncetionapproval.sms",
                            waterConnectionDetails, applicantName);
                } else if (type.equals(WaterTaxConstants.SMSEMAILTYPEADDITONALCONNAPPROVE)) {
                    flag = Boolean.TRUE;
                    smsMsg = waterTaxUtils.smsAndEmailBodyByCodeAndArgs("msg.additionalconncetionapproval.sms",
                            waterConnectionDetails, applicantName);
                }
            if (flag)
                waterTaxUtils.sendSMSOnWaterConnection(mobileNumber, smsMsg);
        }
    }

    public void buildEmail(final WaterConnectionDetails waterConnectionDetails, final String type, final String email) {
        if (email != null)
            if (waterTaxUtils.isEmailEnabled())
                try {
                    String body = "";
                    String subject = "";
                    Boolean flag = Boolean.FALSE;
                    if (type.equalsIgnoreCase(WaterTaxConstants.SMSEMAILTYPENEWCONNCREATE)) {
                        flag = Boolean.TRUE;
                        body = waterTaxUtils.smsAndEmailBodyByCodeAndArgs("msg.newconncetioncreate.email.body",
                                waterConnectionDetails, applicantName);
                        subject = waterTaxUtils.emailSubjectforEmailByCodeAndArgs(
                                "msg.newconncetioncreate.email.subject", waterConnectionDetails.getApplicationNumber());
                    } else if (type.equalsIgnoreCase(WaterTaxConstants.SMSEMAILTYPEADDITONALCONNCREATE)) {
                        flag = Boolean.TRUE;
                        body = waterTaxUtils.smsAndEmailBodyByCodeAndArgs("msg.additionalconnectioncreate.email.body",
                                waterConnectionDetails, applicantName);
                        subject = waterTaxUtils.emailSubjectforEmailByCodeAndArgs(
                                "msg.additionalconnectioncreate.email.subject",
                                waterConnectionDetails.getApplicationNumber());
                    } else if (type.equalsIgnoreCase(WaterTaxConstants.SMSEMAILTYPENEWCONNAPPROVE)) {
                        flag = Boolean.TRUE;
                        body = waterTaxUtils.emailBodyforApprovalEmailByCodeAndArgs(
                                "msg.newconncetionapproval.email.body", waterConnectionDetails, applicantName);
                        subject = waterTaxUtils
                                .emailSubjectforEmailByCodeAndArgs("msg.newconncetionapprove.email.subject",
                                        waterConnectionDetails.getApplicationNumber());
                    } else if (type.equalsIgnoreCase(WaterTaxConstants.SMSEMAILTYPEADDITONALCONNAPPROVE)) {
                        flag = Boolean.TRUE;
                        body = waterTaxUtils.emailBodyforApprovalEmailByCodeAndArgs(
                                "msg.additionalconncetionapproval.email.body", waterConnectionDetails, applicantName);
                        subject = waterTaxUtils.emailSubjectforEmailByCodeAndArgs(
                                "msg.additionalconncetionapproval.email.subject",
                                waterConnectionDetails.getApplicationNumber());
                    }
                    if (flag)
                        waterTaxUtils.sendEmailOnWaterConnection(email, body, subject);

                } catch (final EGOVRuntimeException egovExp) {
                    // emailMsg =
                    // messageSource.getMessage("email.failure.msg1");
                }
            else {
                // emailMsg= messageSource.getMessage("email.failure.msg1");
            }

    }

    @Transactional
    public WaterConnectionDetails createChangeOfUseApplication(final WaterConnectionDetails changeOfUse,
            final Long approvalPosition, final String approvalComent) {
        if (changeOfUse.getApplicationNumber() == null)
            changeOfUse.setApplicationNumber(applicationNumberGenerator.generate());

        final Integer appProcessTime = applicationProcessTimeService.getApplicationProcessTime(
                changeOfUse.getApplicationType(), changeOfUse.getCategory());
        if (appProcessTime != null)
            changeOfUse.setDisposalDate(getDisposalDate(changeOfUse, appProcessTime));
        final WaterConnectionDetails savedChangeOfUse = waterConnectionDetailsRepository.save(changeOfUse);
        return savedChangeOfUse;
    }

    private Date getDisposalDate(final WaterConnectionDetails changeOfUse, final Integer appProcessTime) {
        final Calendar c = Calendar.getInstance();
        c.setTime(changeOfUse.getApplicationDate());
        c.add(Calendar.DATE, appProcessTime);
        return c.getTime();
    }

    public void setApplicantName(final String applicantName) {
        this.applicantName = applicantName;
    }

    public WaterConnectionDetails getParentConnectionDetails(final String consumerCode,
            final ConnectionStatus connectionStatus) {
        return waterConnectionDetailsRepository
                .findByConnection_ConsumerCodeAndConnectionStatusAndConnection_ParentConnectionIsNull(consumerCode,
                        connectionStatus);
    }

}
