/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
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
package org.egov.wtms.application.workflow;

import static org.egov.wtms.utils.constants.WaterTaxConstants.APPLICATION_STATUS_FEEPAID;
import static org.egov.wtms.utils.constants.WaterTaxConstants.CLOSINGCONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.JUNIOR_ASSISTANT_DESIGN;
import static org.egov.wtms.utils.constants.WaterTaxConstants.RECONNECTIONCONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.REGULARIZE_CONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.ROLE_APPROVERROLE;
import static org.egov.wtms.utils.constants.WaterTaxConstants.SENIOR_ASSISTANT_DESIGN;
import static org.egov.wtms.utils.constants.WaterTaxConstants.SIGNWORKFLOWACTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WFLOW_ACTION_STEP_REJECT;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WFLOW_ACTION_STEP_THIRDPARTY_CREATED;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WF_STATE_AE_APPROVAL_PENDING;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WF_STATE_CANCELLED;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WF_STATE_REJECTED;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.egov.demand.model.EgDemand;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.Role;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.pims.commons.Position;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.entity.WaterDemandConnection;
import org.egov.wtms.application.repository.WaterConnectionDetailsRepository;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.application.service.WaterConnectionSmsAndEmailService;
import org.egov.wtms.application.service.WaterDemandConnectionService;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.utils.WaterTaxUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class ApplicationCommonWorkflow.
 */
public abstract class ApplicationWorkflowCustomImpl implements ApplicationWorkflowCustom {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationWorkflowCustomImpl.class);

    @Autowired
    private WaterConnectionDetailsRepository waterConnectionDetailsRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private WaterDemandConnectionService waterDemandConnectionService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private PositionMasterService positionMasterService;

    @Autowired
    private WaterTaxUtils waterTaxUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private WaterConnectionSmsAndEmailService waterConnectionSmsAndEmailService;

    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<WaterConnectionDetails> waterConnectionWorkflowService;

    @Autowired
    private WaterConnectionDetailsService waterConnectionDetailsService;

    @Override
    public void createCommonWorkflowTransition(final WaterConnectionDetails waterConnectionDetails,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction) {
        if (LOG.isDebugEnabled())
            LOG.debug(" Create WorkFlow Transition Started  ...");
        final User user = securityUtils.getCurrentUser();
        final DateTime currentDate = new DateTime();
        User currentUser;
        final Assignment userAssignment = assignmentService.getPrimaryAssignmentForUser(user.getId());

        Position pos = null;
        Assignment wfInitiator = null;
        final Boolean recordCreatedBYNonEmployee;
        final Boolean recordCreatedBYCitizenPortal;
        final Boolean recordCreatedByAnonymousUser;
        final Boolean recordCreatedBySuperUser;
        final Boolean recordCreatedByRoleAdmin;

        if (user != null && user.getId() != waterConnectionDetails.getCreatedBy().getId()
                && (CLOSINGCONNECTION.equalsIgnoreCase(waterConnectionDetails.getApplicationType().getCode()) ||
                        RECONNECTIONCONNECTION.equalsIgnoreCase(waterConnectionDetails.getApplicationType().getCode()))) {
            recordCreatedBYNonEmployee = waterTaxUtils.getCurrentUserRole(user);
            recordCreatedBYCitizenPortal = waterTaxUtils.isCitizenPortalUser(user);
            recordCreatedByAnonymousUser = waterTaxUtils.isAnonymousUser(user);
            recordCreatedBySuperUser = waterTaxUtils.isSuperUser(user);
            recordCreatedByRoleAdmin = waterTaxUtils.isRoleAdmin(user);
        } else {
            recordCreatedBYNonEmployee = waterTaxUtils
                    .getCurrentUserRole(waterConnectionDetails.getCreatedBy());
            recordCreatedBYCitizenPortal = waterTaxUtils
                    .isCitizenPortalUser(userService.getUserById(waterConnectionDetails.getCreatedBy().getId()));
            recordCreatedByAnonymousUser = waterTaxUtils
                    .isAnonymousUser(userService.getUserById(waterConnectionDetails.getCreatedBy().getId()));
            recordCreatedBySuperUser = waterTaxUtils
                    .isSuperUser(userService.getUserById(waterConnectionDetails.getCreatedBy().getId()));
            recordCreatedByRoleAdmin = waterTaxUtils
                    .isRoleAdmin(userService.getUserById(waterConnectionDetails.getCreatedBy().getId()));
        }
        String currState = "";
        String loggedInUserDesignation = waterTaxUtils.loggedInUserDesignation(waterConnectionDetails);

        final String natureOfwork = getNatureOfTask(waterConnectionDetails);
        if (recordCreatedBYNonEmployee || recordCreatedBYCitizenPortal || recordCreatedByAnonymousUser || recordCreatedBySuperUser
                || recordCreatedByRoleAdmin) {
            currState = WFLOW_ACTION_STEP_THIRDPARTY_CREATED;
            if (!waterConnectionDetails.getStateHistory().isEmpty()) {
                wfInitiator = assignmentService.getPrimaryAssignmentForPositon(
                        waterConnectionDetails.getStateHistory().get(0).getOwnerPosition().getId());
                if (wfInitiator == null) {
                    final List<Assignment> assignmentList = assignmentService
                            .getAssignmentsForPosition(
                                    waterConnectionDetails.getStateHistory().get(0).getOwnerPosition().getId());
                    if (!assignmentList.isEmpty())
                        wfInitiator = assignmentList.get(0);
                }
            }

        } else if (null != waterConnectionDetails.getId()) {
            currentUser = userService.getUserById(waterConnectionDetails.getCreatedBy().getId());
            if (currentUser != null && waterConnectionDetails.getLegacy().equals(true)) {
                for (final Role userrole : currentUser.getRoles())
                    if (userrole.getName().equals(WaterTaxConstants.ROLE_SUPERUSER) ||
                            ROLE_APPROVERROLE.equals(userrole.getName())) {
                        final Position positionuser = waterTaxUtils.getZonalLevelClerkForLoggedInUser(
                                waterConnectionDetails.getConnection().getPropertyIdentifier());
                        if (positionuser != null) {
                            wfInitiator = assignmentService.getPrimaryAssignmentForPositionAndDate(positionuser.getId(),
                                    new Date());

                            if (wfInitiator == null) {
                                final List<Assignment> assignmentList = assignmentService
                                        .getAssignmentsForPosition(positionuser.getId());
                                if (!assignmentList.isEmpty())
                                    wfInitiator = assignmentList.get(0);
                            }
                            break;
                        }
                    }
            } else if (WFLOW_ACTION_STEP_REJECT.equalsIgnoreCase(workFlowAction)) {
                final Position position = waterTaxUtils.getZonalLevelClerkForLoggedInUser(
                        waterConnectionDetails.getConnection().getPropertyIdentifier());
                if (position != null) {
                    wfInitiator = assignmentService.getPrimaryAssignmentForPositionAndDate(position.getId(),
                            new Date());

                    if (wfInitiator == null) {
                        final List<Assignment> assignmentList = assignmentService
                                .getAssignmentsForPosition(position.getId());
                        if (!assignmentList.isEmpty())
                            wfInitiator = assignmentList.get(0);
                    }
                }
            } else {
                wfInitiator = assignmentService
                        .getPrimaryAssignmentForUser(waterConnectionDetails.getCreatedBy().getId());

                if (wfInitiator == null) {
                    final List<Assignment> assignmtList = assignmentService
                            .getAllActiveEmployeeAssignmentsByEmpId(waterConnectionDetails.getCreatedBy().getId());
                    if (!assignmtList.isEmpty())
                        wfInitiator = assignmtList.get(0);

                }

            }
        }
        if (workFlowAction != null && WaterTaxConstants.WFLOW_ACTION_STEP_CANCEL.equalsIgnoreCase(workFlowAction)) {
            waterConnectionDetails.setConnectionStatus(ConnectionStatus.INACTIVE);
            final EgDemand demand = waterTaxUtils.getCurrentDemand(waterConnectionDetails).getDemand();
            if (demand != null) {
                final WaterDemandConnection waterDemandConnection = waterDemandConnectionService
                        .findByWaterConnectionDetailsAndDemand(waterConnectionDetails, demand);
                demand.setIsHistory("Y");
                demand.setModifiedDate(new Date());
                waterDemandConnection.setDemand(demand);
                waterDemandConnectionService.updateWaterDemandConnection(waterDemandConnection);
            }

            waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                    WaterTaxConstants.APPLICATION_STATUS_CANCELLED, WaterTaxConstants.MODULETYPE));
            waterConnectionDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComent).withStateValue(WF_STATE_CANCELLED).withOwner((Position) null)
                    .withDateInfo(currentDate.toDate())
                    .withNatureOfTask(natureOfwork)
                    .withNextAction("END");
            waterConnectionDetailsRepository.save(waterConnectionDetails);
            waterConnectionSmsAndEmailService.sendSmsAndEmailOnRejection(waterConnectionDetails, approvalComent);
            waterConnectionDetailsService.updateIndexes(waterConnectionDetails);

        } else if (WFLOW_ACTION_STEP_REJECT.equalsIgnoreCase(workFlowAction)) {
            if (wfInitiator != null && wfInitiator.equals(userAssignment)) {
                waterConnectionDetails.setConnectionStatus(ConnectionStatus.INACTIVE);
                if (waterConnectionDetails.getStatus() != null && waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN)) {
                    final EgDemand demand = waterTaxUtils.getCurrentDemand(waterConnectionDetails).getDemand();
                    if (demand != null) {
                        final WaterDemandConnection waterDemandConnection = waterDemandConnectionService
                                .findByWaterConnectionDetailsAndDemand(waterConnectionDetails, demand);
                        demand.setIsHistory("Y");
                        demand.setModifiedDate(new Date());
                        waterDemandConnection.setDemand(demand);
                        waterDemandConnectionService.updateWaterDemandConnection(waterDemandConnection);
                    }
                }
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_CANCELLED, WaterTaxConstants.MODULETYPE));
                waterConnectionDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withDateInfo(currentDate.toDate()).withNatureOfTask(natureOfwork)
                        .withNextAction("END");
                waterConnectionSmsAndEmailService.sendSmsAndEmailOnRejection(waterConnectionDetails, approvalComent);
                waterConnectionDetailsService.updateIndexes(waterConnectionDetails);
            } else {
                final String stateValue = WF_STATE_REJECTED;
                waterConnectionDetails.transition().progressWithStateCopy()
                        .withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withStateValue(stateValue).withDateInfo(currentDate.toDate())
                        .withOwner(wfInitiator != null && wfInitiator.getPosition() != null ? wfInitiator.getPosition() : null)
                        .withNextAction("Application Rejected")
                        .withNatureOfTask(natureOfwork);
            }
        } else {
            if (null != approvalPosition && approvalPosition != -1 && !approvalPosition.equals(Long.valueOf(0)))
                pos = positionMasterService.getPositionById(approvalPosition);
            WorkFlowMatrix wfmatrix = null;
            if (waterConnectionDetails.getState() == null) {
                if (REGULARIZE_CONNECTION.equalsIgnoreCase(waterConnectionDetails.getApplicationType().getCode()) &&
                        isDesignationOfClerk())
                    currState = "NEW";
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                        additionalRule, currState, null);
                waterConnectionDetails.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                        .withOwner(pos).withNextAction(wfmatrix.getNextAction()).withNatureOfTask(natureOfwork);
            } else if (SIGNWORKFLOWACTION.equalsIgnoreCase(workFlowAction))
                waterConnectionDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withDateInfo(currentDate.toDate()).withNatureOfTask(natureOfwork)
                        .withNextAction("END");
            else if (null != approvalComent && "Receipt Cancelled".equalsIgnoreCase(approvalComent)) {
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                        additionalRule, "Asst engg approved", null);
                waterConnectionDetails.transition().progressWithStateCopy()
                        .withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                        .withDateInfo(currentDate.toDate()).withOwner(pos).withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(natureOfwork);
            } else if ((additionalRule.equals(WaterTaxConstants.WORKFLOW_CLOSUREADDITIONALRULE)
                    || additionalRule.equals(WaterTaxConstants.RECONNECTIONCONNECTION))
                    && (waterConnectionDetails.getCurrentState().getValue().equals("Closed")
                            || waterConnectionDetails.getCurrentState().getValue().equals("END"))) {
                if (currState != null && (waterTaxUtils.getCurrentUserRole() || waterTaxUtils.isCurrentUserCitizenRole()
                        || waterTaxUtils.isMeesevaUser(securityUtils.getCurrentUser())
                        || waterTaxUtils.isAnonymousUser(securityUtils.getCurrentUser())))
                    wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                            additionalRule, currState, null);
                else
                    wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                            additionalRule, null, null);
                if (wfmatrix != null && !wfmatrix.getNextAction().equalsIgnoreCase("END"))
                    waterConnectionDetails.transition().reopen()
                            .withSenderName(user.getUsername() + "::" + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                            .withDateInfo(currentDate.toDate()).withOwner(pos).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(natureOfwork);
            } else {
                if (REGULARIZE_CONNECTION.equalsIgnoreCase(waterConnectionDetails.getApplicationType().getCode())) {
                    if (SENIOR_ASSISTANT_DESIGN.equalsIgnoreCase(loggedInUserDesignation) ||
                            JUNIOR_ASSISTANT_DESIGN.equalsIgnoreCase(loggedInUserDesignation))
                        wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                                null, additionalRule, waterConnectionDetails.getCurrentState().getValue(),
                                waterConnectionDetailsService.getReglnConnectionPendingAction(waterConnectionDetails,
                                        null, workFlowAction),
                                null);
                    else
                        wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                                null, additionalRule, waterConnectionDetails.getCurrentState().getValue(),
                                waterConnectionDetailsService.getReglnConnectionPendingAction(waterConnectionDetails,
                                        loggedInUserDesignation, workFlowAction),
                                null);
                } else if (getloggedinUserDesignationForEstimationAndWorkOrderGeneratedStatus(loggedInUserDesignation))
                    wfmatrix = getMatrixbyStatusAndLoggedInUser(waterConnectionDetails, additionalRule, workFlowAction,
                            loggedInUserDesignation);
                else if (APPLICATION_STATUS_FEEPAID.equalsIgnoreCase(waterConnectionDetails.getStatus().getCode()) &&
                        WF_STATE_REJECTED.equalsIgnoreCase(waterConnectionDetails.getState().getValue()))
                    wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                            null, additionalRule, waterConnectionDetails.getCurrentState().getValue(),
                            WF_STATE_AE_APPROVAL_PENDING, null);
                else
                    wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                            null, additionalRule, waterConnectionDetails.getCurrentState().getValue(), null, null);
                if ((additionalRule.equals(WaterTaxConstants.WORKFLOW_CLOSUREADDITIONALRULE)
                        || additionalRule.equals(WaterTaxConstants.RECONNECTIONCONNECTION)) && wfmatrix != null
                        && wfmatrix.getNextAction().equalsIgnoreCase("END"))
                    waterConnectionDetails.transition().end()
                            .withSenderName(user.getUsername() + "::" + user.getName()).withComments(approvalComent)
                            .withDateInfo(currentDate.toDate()).withNatureOfTask(natureOfwork).withNextAction("END");
                else
                    waterConnectionDetails.transition().progressWithStateCopy()
                            .withSenderName(user.getUsername() + "::" + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                            .withDateInfo(currentDate.toDate()).withOwner(pos).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(natureOfwork);
            }

        }
        if (LOG.isDebugEnabled())
            LOG.debug(" WorkFlow Transition Completed  ...");
    }

    /*
     * NOTE: AS per new wOrkflow using API to get currect matrix for loggedinUSer is COMM, SE ,ME,EE
     */
    protected WorkFlowMatrix getMatrixbyStatusAndLoggedInUser(final WaterConnectionDetails waterConnectionDetails,
            final String additionalRule, final String workFlowAction, final String loggedInUserDesignation) {
        WorkFlowMatrix wfmatrix = null;
        wfmatrix = getWorkFlowMatrix(workFlowAction, loggedInUserDesignation, waterConnectionDetails, additionalRule);
        if (wfmatrix == null
                && (WaterTaxConstants.APPROVEWORKFLOWACTION.equalsIgnoreCase(workFlowAction)
                        || WaterTaxConstants.FORWARDWORKFLOWACTION.equalsIgnoreCase(workFlowAction))
                && (loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.MUNICIPAL_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.DEPUTY_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.SUPERIENTEND_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.EXECUTIVE_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.COMMISSIONER_DESGN))) {
            if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERDIGSIGNPENDING))
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                        additionalRule, WaterTaxConstants.WF_STATE_COLSURE_APPROVED, null, null,
                        loggedInUserDesignation);
            if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_RECONNDIGSIGNPENDING))
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                        additionalRule, WaterTaxConstants.WF_STATE_RECONN_APPROVED, null, null,
                        loggedInUserDesignation);
        }

        if (wfmatrix == null
                && waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_VERIFIED)
                && (loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.ASSISTANT_EXECUTIVE_ENGINEER_DESIGN) ||
                        loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.TAP_INSPPECTOR_DESIGN)))
            wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                    additionalRule, waterConnectionDetails.getCurrentState().getValue(), null, null,
                    null);
        if (wfmatrix == null)
            wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                    additionalRule, waterConnectionDetails.getCurrentState().getValue(), null, null,
                    loggedInUserDesignation);
        return wfmatrix;
    }

    public WorkFlowMatrix getWorkFlowMatrix(final String workFlowAction, final String loggedInUserDesignation,
            final WaterConnectionDetails waterConnectionDetails, final String additionalRule) {
        WorkFlowMatrix workFlowMatrix = null;
        if (workFlowAction.equals(WaterTaxConstants.FORWARDWORKFLOWACTION)
                && waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_DIGITALSIGNPENDING)
                &&
                loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.DEPUTY_ENGINEER_DESIGN))
            workFlowMatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                    additionalRule, "Deputy Engineer Approved", null, null, loggedInUserDesignation);
        if (workFlowMatrix == null && workFlowAction.equals(WaterTaxConstants.FORWARDWORKFLOWACTION)
                && waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_DIGITALSIGNPENDING)
                && (loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.MUNICIPAL_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.SUPERIENTEND_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.EXECUTIVE_ENGINEER_DESIGN)))
            workFlowMatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                    additionalRule, WaterTaxConstants.EXECUTIVEENGINEERFORWARDED, null, null, loggedInUserDesignation);
        else if (workFlowAction.equals(WaterTaxConstants.FORWARDWORKFLOWACTION)
                && waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)
                && (loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.MUNICIPAL_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.DEPUTY_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.ASSISTANT_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.ASSISTANT_EXECUTIVE_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.SUPERIENTEND_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.EXECUTIVE_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.TAP_INSPPECTOR_DESIGN)))
            workFlowMatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                    additionalRule, WaterTaxConstants.WF_STATE_STATE_FORWARD, null, null, loggedInUserDesignation);
        else if (workFlowAction.equals(WaterTaxConstants.APPROVEWORKFLOWACTION)
                && waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_DIGITALSIGNPENDING)
                && (loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.MUNICIPAL_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.SUPERIENTEND_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.EXECUTIVE_ENGINEER_DESIGN)))
            workFlowMatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                    additionalRule, WaterTaxConstants.EXECUTIVEENGINEERFORWARDED, null, null, loggedInUserDesignation);
        else if (workFlowMatrix == null
                && (waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.NEWCONNECTION)
                        || waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.CHANGEOFUSE)
                        || waterConnectionDetails.getApplicationType().getCode()
                                .equals(WaterTaxConstants.ADDNLCONNECTION))
                && workFlowAction.equals(WaterTaxConstants.SIGNWORKFLOWACTION))
            workFlowMatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                    additionalRule, WaterTaxConstants.WF_STATE_COMMISSIONER_APPROVED, null, null,
                    loggedInUserDesignation);
        if (workFlowMatrix == null && WaterTaxConstants.FORWARDWORKFLOWACTION.equalsIgnoreCase(workFlowAction)) {
            if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINPROGRESS))
                workFlowMatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                        additionalRule, WaterTaxConstants.WF_STATE_CLOSURE_FORWARED_APPROVER, null, null,
                        loggedInUserDesignation);
            if (waterConnectionDetails.getStatus().getCode()
                    .equals(WaterTaxConstants.APPLICATION_STATUS_RECONNCTIONINPROGRESS))
                workFlowMatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null, null,
                        additionalRule, WaterTaxConstants.WF_STATE_RECONN_FORWARED_APPROVER, null, null,
                        loggedInUserDesignation);
        }
        return workFlowMatrix;
    }

    protected boolean getloggedinUserDesignationForEstimationAndWorkOrderGeneratedStatus(final String loggedInUserDesignation) {
        return loggedInUserDesignation != null && !"".equals(loggedInUserDesignation)
                && (loggedInUserDesignation.equals(WaterTaxConstants.COMMISSIONER_DESGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.EXECUTIVE_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.MUNICIPAL_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.SUPERIENTEND_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.TAP_INSPPECTOR_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.ASSISTANT_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.DEPUTY_ENGINEER_DESIGN)
                        || loggedInUserDesignation.equalsIgnoreCase(WaterTaxConstants.ASSISTANT_EXECUTIVE_ENGINEER_DESIGN));
    }

    public String getNatureOfTask(final WaterConnectionDetails waterConnectionDetails) {
        final String wfTypeDisplayName = "Water Tap Connection";
        if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINPROGRESS)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINITIATED)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERDIGSIGNPENDING)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERAPRROVED)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED))
            return "Closure " + wfTypeDisplayName;
        else if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.WORKFLOW_RECONNCTIONINITIATED)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_RECONNCTIONINPROGRESS)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_RECONNCTIONAPPROVED)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_RECONNDIGSIGNPENDING)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS_RECONNCTIONSANCTIONED))
            return "Reconnection " + wfTypeDisplayName;
        else if (waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.NEWCONNECTION))
            return "New " + wfTypeDisplayName;
        else if (waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.ADDNLCONNECTION))
            return "Additional " + wfTypeDisplayName;
        else if (waterConnectionDetails.getApplicationType().getCode().equals(WaterTaxConstants.CHANGEOFUSE))
            return "Change Of Usage " + wfTypeDisplayName;
        else
            return waterConnectionDetails.getApplicationType().getName() + " " + wfTypeDisplayName;
    }

    public boolean isDesignationOfClerk() {
        List<Assignment> assignments;
        assignments = assignmentService.getAllAssignmentsByEmpId(securityUtils.getCurrentUser().getId());
        for (Assignment assignment : assignments)
            if (Arrays.asList(SENIOR_ASSISTANT_DESIGN, JUNIOR_ASSISTANT_DESIGN)
                    .contains(assignment.getPosition().getDeptDesig().getDesignation().getName()))
                return true;
        return false;
    }

}
