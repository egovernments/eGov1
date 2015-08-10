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
package org.egov.wtms.utils;

import java.util.Date;
import java.util.List;

import org.egov.commons.EgwStatus;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.messaging.MessagingService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.commons.Position;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

@Service
public class WaterTaxUtils {

    @Autowired
    private AppConfigValueService appConfigValuesService;

    @Autowired
    private CityService cityService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private PositionMasterService positionMasterService;

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    public Boolean isSmsEnabled() {
        final AppConfigValues appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                WaterTaxConstants.MODULE_NAME, WaterTaxConstants.SENDSMSFORWATERTAX).get(0);
        return "YES".equalsIgnoreCase(appConfigValue.getValue());
    }

    public String getDepartmentForWorkFlow() {
        String department = "";
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                WaterTaxConstants.MODULE_NAME, WaterTaxConstants.WATERTAXWORKFLOWDEPARTEMENT);
        if (null != appConfigValue && !appConfigValue.isEmpty())
            department = appConfigValue.get(0).getValue();
        return department;
    }

    public String getDesignationForThirdPartyUser() {
        String designation = "";
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                WaterTaxConstants.MODULE_NAME, WaterTaxConstants.CLERKDESIGNATIONFORCSCOPERATOR);
        if (null != appConfigValue && !appConfigValue.isEmpty())
            designation = appConfigValue.get(0).getValue();
        return designation;
    }

    public Boolean isEmailEnabled() {
        final AppConfigValues appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                WaterTaxConstants.MODULE_NAME, WaterTaxConstants.SENDEMAILFORWATERTAX).get(0);
        return "YES".equalsIgnoreCase(appConfigValue.getValue());
    }

    public Boolean isNewConnectionAllowedIfPTDuePresent() {
        final AppConfigValues appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                WaterTaxConstants.MODULE_NAME, WaterTaxConstants.NEWCONNECTIONALLOWEDIFPTDUE).get(0);
        return "YES".equalsIgnoreCase(appConfigValue.getValue());
    }

    public Boolean isMultipleNewConnectionAllowedForPID() {
        final AppConfigValues appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                WaterTaxConstants.MODULE_NAME,WaterTaxConstants.MULTIPLENEWCONNECTIONFORPID).get(0);
        return "YES".equalsIgnoreCase(appConfigValue.getValue());
    }

    public Boolean isConnectionAllowedIfWTDuePresent(final String connectionType) {
        final Boolean isAllowed = false;
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                WaterTaxConstants.MODULE_NAME, connectionType);
        if (null != appConfigValue && !appConfigValue.isEmpty())
            return "YES".equalsIgnoreCase(appConfigValue.get(0).getValue());

        return isAllowed;
    }

    public String documentRequiredForBPLCategory() {
        String documentName = null;
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                WaterTaxConstants.MODULE_NAME, WaterTaxConstants.DOCUMENTREQUIREDFORBPL);
        if (appConfigValue != null && !appConfigValue.isEmpty())
            documentName = appConfigValue.get(0).getValue();
        return documentName;
    }

    public String getCityName() {
        return cityService.getCityByURL(EgovThreadLocals.getDomainName()).getName();
    }

    public String getCityCode() {
        return cityService.getCityByURL(EgovThreadLocals.getDomainName()).getCode();
    }

    public String smsAndEmailBodyByCodeAndArgs(final String code, final WaterConnectionDetails waterConnectionDetails,
            final String applicantName) {
        final String smsMsg = messageSource.getMessage(code,
                new String[] { applicantName, waterConnectionDetails.getApplicationNumber(), getCityName() }, null);
        return smsMsg;
    }

    public String emailBodyforApprovalEmailByCodeAndArgs(final String code,
            final WaterConnectionDetails waterConnectionDetails, final String applicantName) {
        final String smsMsg = messageSource.getMessage(code,
                new String[] { applicantName, waterConnectionDetails.getApplicationNumber(),
                waterConnectionDetails.getConnection().getConsumerCode(), getCityName() }, null);
        return smsMsg;
    }

    public String emailSubjectforEmailByCodeAndArgs(final String code, final String applicationNumber) {
        final String emailSubject = messageSource.getMessage(code, new String[] { applicationNumber }, null);
        return emailSubject;
    }

    public void sendSMSOnWaterConnection(final String mobileNumber, final String smsBody) {
        messagingService.sendSMS(smsBody, mobileNumber);
    }

    public void sendEmailOnWaterConnection(final String email, final String emailBody, final String emailSubject) {
        messagingService.sendEmail(email, emailBody, emailSubject);
    }

    public Position getCityLevelCommissionerPosition(final String commissionerDesgn) {
       return assignmentService.findPrimaryAssignmentForDesignationName(commissionerDesgn).get(0).getPosition();
    }

    public String getApproverUserName(final Long approvalPosition) {
        Assignment assignment = null;
        if (approvalPosition != null)
            assignment = assignmentService.getPrimaryAssignmentForPositionAndDate(approvalPosition, new Date());
        return assignment != null ? assignment.getEmployee().getUsername() : "";
    }

    public EgwStatus getStatusByCodeAndModuleType(final String code, final String moduleName) {
        return (EgwStatus) persistenceService.find("from EgwStatus where moduleType=? and code=?", moduleName, code);
    }

    public Long getApproverPosition(final String designationName, final WaterConnectionDetails waterConnectionDetails) {

        final List<StateHistory> stateHistoryList = waterConnectionDetails.getState().getHistory();
        Long approverPosition = 0l;
        if (stateHistoryList != null && !stateHistoryList.isEmpty()) {
            for (final StateHistory stateHistory : stateHistoryList) {
                final List<Assignment> assignmentList = assignmentService.getAssignmentsForPosition(stateHistory
                        .getOwnerPosition().getId(), new Date());
                for (final Assignment assgn : assignmentList)
                    if (assgn.getDesignation().getName().equals(designationName)) {
                        approverPosition = stateHistory.getOwnerPosition().getId();
                        break;
                    }
            }
            // TODO: just incase entry is not present in workflow histrory table
            // then checkinh in workflow state table;
            if (approverPosition == 0) {
                final State stateObj = waterConnectionDetails.getState();
                final List<Assignment> assignmentList = assignmentService.getAssignmentsForPosition(stateObj
                        .getOwnerPosition().getId(), new Date());
                for (final Assignment assgn : assignmentList)
                    if (assgn.getDesignation().getName().equals(designationName)) {
                        approverPosition = stateObj.getOwnerPosition().getId();
                        break;
                    }
            }
        } else {
            // First after AE->CLERK need previous clerk User so by passing
            // workflow Initiator name
            final Position posObjToClerk = positionMasterService.getCurrentPositionForUser(waterConnectionDetails
                    .getCreatedBy().getId());
            approverPosition = posObjToClerk.getId();
        }

        return approverPosition;

    }
}
