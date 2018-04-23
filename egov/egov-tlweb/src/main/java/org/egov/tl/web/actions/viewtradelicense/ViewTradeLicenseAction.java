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

package org.egov.tl.web.actions.viewtradelicense;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.eis.entity.Assignment;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infra.web.struts.annotation.ValidationErrorPageExt;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.tl.entity.TradeLicense;
import org.egov.tl.entity.WorkflowBean;
import org.egov.tl.service.AbstractLicenseService;
import org.egov.tl.service.LicenseClosureService;
import org.egov.tl.utils.Constants;
import org.egov.tl.web.actions.BaseLicenseAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.egov.infra.reporting.engine.ReportFormat.PDF;
import static org.egov.infra.reporting.util.ReportUtil.CONTENT_TYPES;
import static org.egov.infra.utils.ApplicationConstant.CITIZEN_ROLE_NAME;
import static org.egov.infra.utils.ApplicationConstant.PUBLIC_ROLE_NAME;
import static org.egov.tl.utils.Constants.BUTTONAPPROVE;
import static org.egov.tl.utils.Constants.BUTTONFORWARD;
import static org.egov.tl.utils.Constants.BUTTONREJECT;
import static org.egov.tl.utils.Constants.CSCOPERATOR;
import static org.egov.tl.utils.Constants.MEESEVAOPERATOR;
import static org.egov.tl.utils.Constants.MEESEVA_RESULT_ACK;
import static org.egov.tl.utils.Constants.MESSAGE;
import static org.egov.tl.utils.Constants.REPORT_PAGE;
import static org.egov.tl.utils.Constants.TL_FILE_STORE_DIR;

@ParentPackage("egov")
@Results({@Result(name = REPORT_PAGE, location = "viewTradeLicense-report.jsp"),
        @Result(name = MESSAGE, location = "viewTradeLicense-message.jsp"),
        @Result(name = "closure", location = "viewTradeLicense-closure.jsp"),
        @Result(name = "digisigncertificate", type = "redirect", location = "/tradelicense/download/digisign-certificate",
                params = {"file", "${digiSignedFile}", "applnum", "${applNum}"}),
        @Result(name = "closureEndorsementNotice", type = "redirect", location = "/license/closure/digisign-transition",
                params = {"fileStoreIds", "${fileStoreIds}", "applicationNumbers", "${applicationNo}"}),
        @Result(name = "closureEndorsementDigiSign", location = "closure-endorsementnotice-digitalsigned.jsp")
})
public class
ViewTradeLicenseAction extends BaseLicenseAction<TradeLicense> {
    private static final long serialVersionUID = 1L;
    private static final String MODEL_ID = "model.id";

    private TradeLicense tradeLicense = new TradeLicense();
    private Long licenseid;
    private String url;
    private Boolean enableState;
    private String digiSignedFile;
    private String applNum;

    @Autowired
    private transient LicenseClosureService licenseClosureService;

    @Autowired
    @Qualifier("fileStoreService")
    private transient FileStoreService fileStoreService;

    @Override
    public TradeLicense getModel() {
        return tradeLicense;

    }

    @Override
    protected TradeLicense license() {
        return tradeLicense;
    }

    @Override
    protected AbstractLicenseService<TradeLicense> licenseService() {
        return tradeLicenseService;
    }

    @Override
    public String getReportId() {
        return reportId;
    }

    @Override
    @Action(value = "/viewtradelicense/viewTradeLicense-showForApproval")
    public String showForApproval() throws IOException {
        tradeLicense = tradeLicenseService.getLicenseById(license().getId());
        return super.showForApproval();
    }

    @Action(value = "/viewtradelicense/viewTradeLicense-view")
    public String view() {
        if (license() != null && license().getId() != null)
            tradeLicense = tradeLicenseService.getLicenseById(license().getId());
        else if (applicationNo != null && !applicationNo.isEmpty())
            tradeLicense = tradeLicenseService.getLicenseByApplicationNumber(applicationNo);
        licenseHistory = tradeLicenseService.populateHistory(tradeLicense);
        return Constants.VIEW;
    }

    @ValidationErrorPage(REPORT_PAGE)
    @Action(value = "/viewtradelicense/viewTradeLicense-generateCertificate")
    public String generateCertificate() {
        setLicenseIdIfServletRedirect();
        tradeLicense = tradeLicenseService.getLicenseById(license().getId());
        if (isNotBlank(tradeLicense.getCertificateFileId())) {
            setDigiSignedFile(license().getCertificateFileId());
            setApplNum(license().getApplicationNumber());
            return "digisigncertificate";
        } else {
            reportId = reportViewerUtil
                    .addReportToTempCache(tradeLicenseService.generateLicenseCertificate(license(), false));
            return REPORT_PAGE;
        }
    }

    @ValidationErrorPage(REPORT_PAGE)
    @Action(value = "/viewtradelicense/generate-provisional-certificate")
    public String generateProvisionalCertificate() {
        setLicenseIdIfServletRedirect();
        tradeLicense = tradeLicenseService.getLicenseById(license().getId());
        reportId = reportViewerUtil.addReportToTempCache(tradeLicenseService.generateLicenseCertificate(license(), true));
        return REPORT_PAGE;
    }

    private void setLicenseIdIfServletRedirect() {
        if (tradeLicense.getId() == null && getSession().get(MODEL_ID) != null) {
            tradeLicense.setId((Long) getSession().get(MODEL_ID));
            getSession().remove(MODEL_ID);
        }
    }

    @Action(value = "/viewtradelicense/showclosureform")
    public String showClosureForm() throws IOException {
        prepareClosureForm();
        setUrl("viewtradelicense/saveclosure.action?model.id=");
        if (tradeLicense.hasState() && !tradeLicense.transitionCompleted()) {
            ServletActionContext.getResponse().setContentType("text/html");
            ServletActionContext.getResponse().getWriter().write("<center style='color:red;font-weight:bolder'>" + tradeLicense.getLicenseAppType().getName() + " License workflow is in progress !</center>");
            return null;
        }
        return "closure";
    }

    public void prepareClosureForm() {
        if (license() != null && license().getId() != null)
            tradeLicense = tradeLicenseService.getLicenseById(license().getId());
        if (tradeLicense.hasState() && tradeLicense.getCurrentState().getValue().equalsIgnoreCase("SI/SS Approved"))
            setEnableState(true);
    }

    @Action(value = "/viewtradelicense/viewTradeLicense-closure")
    public String viewClosure() {
        prepareClosureForm();
        setUrl("viewtradelicense/viewTradeLicense-cancelLicense.action?model.id=");
        licenseHistory = tradeLicenseService.populateHistory(tradeLicense);
        return "closure";
    }

    @ValidationErrorPageExt(action = "closure", makeCall = true, toMethod = "prepareClosureForm")
    @Action(value = "/viewtradelicense/saveclosure")
    public String saveClosure() {
        populateWorkflowBean();
        if (getLicenseid() != null) {
            tradeLicense = tradeLicenseService.getLicenseById(getLicenseid());
            if (tradeLicenseService.currentUserIsMeeseva()) {
                tradeLicense.setApplicationNumber(getApplicationNo());
                tradeLicenseService.closureWithMeeseva(tradeLicense, workflowBean);
            } else
                tradeLicenseService.saveClosure(tradeLicense, workflowBean);
        }
        if (hasCSCPublicRole())
            addActionMessage(this.getText("license.closure.initiated"));
        else if (BUTTONFORWARD.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
            List<Assignment> assignments = assignmentService.getAssignmentsForPosition(workflowBean.getApproverPositionId());
            String nextDesgn = !assignments.isEmpty() ? assignments.get(0).getDesignation().getName() : "";
            final String userName = !assignments.isEmpty() ? assignments.get(0).getEmployee().getName() : "";
            addActionMessage(this.getText("license.closure.sent") + " " + nextDesgn + " - " + userName);
        }
        return tradeLicenseService.currentUserIsMeeseva() ? MEESEVA_RESULT_ACK : "message";
    }

    @Action(value = "/viewtradelicense/viewTradeLicense-cancelLicense")
    public String updateLicenseClosure() {
        populateWorkflowBean();
        if (getLicenseid() != null) {
            tradeLicense = tradeLicenseService.getLicenseById(getLicenseid());
            WorkFlowMatrix wfmatrix = tradeLicenseService.getWorkFlowMatrixApi(license(), workflowBean);
            if (!license().getCurrentState().getValue().equals(wfmatrix.getCurrentState())) {
                addActionMessage(this.getText("wf.item.processed"));
                return MESSAGE;
            }
            if (BUTTONAPPROVE.equals(workflowBean.getWorkFlowAction()))
                return approveClosureWithDigiSign(tradeLicense);
            tradeLicenseService.cancelLicenseWorkflow(tradeLicense, workflowBean);
            if (BUTTONFORWARD.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
                List<Assignment> assignments = assignmentService.getAssignmentsForPosition(workflowBean.getApproverPositionId());
                String nextDesgn = !assignments.isEmpty() ? assignments.get(0).getDesignation().getName() : "";
                final String userName = !assignments.isEmpty() ? assignments.get(0).getEmployee().getName() : "";
                addActionMessage(this.getText("license.closure.sent") + " " + nextDesgn + " - " + userName);
            } else if (BUTTONREJECT.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
                if (license().getState().getValue().contains(Constants.WORKFLOW_STATE_REJECTED)) {
                    List<Assignment> assignments = assignmentService.getAssignmentsForPosition(license().getState().getInitiatorPosition().getId());
                    final String userName = !assignments.isEmpty() ? assignments.get(0).getEmployee().getName() : "";
                    addActionMessage(this.getText("license.closure.rejectedfirst") + (" " + license().getState().getInitiatorPosition().getDeptDesig().getDesignation().getName() + " - ") + " " + userName);
                } else addActionMessage(this.getText("license.closure.rejected") + " " + license().getLicenseNumber());
            } else
                addActionMessage(this.getText("license.closure.msg") + license().getLicenseNumber());
        }
        return MESSAGE;
    }

    private String approveClosureWithDigiSign(TradeLicense license) {
        FileStoreMapper fileStore = fileStoreService
                .store(licenseClosureService.generateClosureEndorsementNotice(license).asInputStream(),
                        license.generateCertificateFileName() + ".pdf", CONTENT_TYPES.get(PDF), TL_FILE_STORE_DIR);
        license.setDigiSignedCertFileStoreId(fileStore.getFileStoreId());
        licenseClosureService.update(license);
        fileStoreIds = license.getDigiSignedCertFileStoreId();
        ulbCode = ApplicationThreadLocals.getCityCode();
        applicationNo = license.getApplicationNumber();
        return licenseUtils.isDigitalSignEnabled() ? "closureEndorsementDigiSign" : "closureEndorsementNotice";
    }

    @Override
    public String getAdditionalRule() {
        return Constants.CLOSURE_ADDITIONAL_RULE;
    }

    public WorkflowBean getWorkflowBean() {
        return workflowBean;
    }

    public void setWorkflowBean(final WorkflowBean workflowBean) {
        this.workflowBean = workflowBean;
    }

    public Long getLicenseid() {
        return licenseid;
    }

    public void setLicenseid(Long licenseid) {
        this.licenseid = licenseid;
    }

    public Boolean hasCSCPublicRole() {
        return securityUtils.getCurrentUser().hasAnyRole(CSCOPERATOR, MEESEVAOPERATOR, PUBLIC_ROLE_NAME, CITIZEN_ROLE_NAME);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getEnableState() {
        return enableState;
    }

    public void setEnableState(Boolean enableState) {
        this.enableState = enableState;
    }

    public String getDigiSignedFile() {
        return digiSignedFile;
    }

    public void setDigiSignedFile(String digiSignedFile) {
        this.digiSignedFile = digiSignedFile;
    }

    public String getApplNum() {
        return applNum;
    }

    public void setApplNum(String applNum) {
        this.applNum = applNum;
    }

    @Override
    public List<String> getValidActions() {
        List<String> validActions = new ArrayList<>();
        if (null == getModel() || null == getModel().getId() || getModel().getCurrentState() == null
                || (getModel() != null && getModel().getCurrentState() != null ? getModel().getCurrentState().isEnded() : false)) {
            validActions = Arrays.asList(FORWARD);
        } else if (getModel().getCurrentState() != null) {
            validActions.addAll(this.customizedWorkFlowService.getNextValidActions(getModel()
                            .getStateType(), getWorkFlowDepartment(), getAmountRule(),
                    getAdditionalRule(), getModel().getCurrentState().getValue(),
                    getPendingActions(), getModel().getCreatedDate()));
            validActions.removeIf(validAction -> "Reassign".equals(validAction) && getModel().getState().getCreatedBy().getId().equals(ApplicationThreadLocals.getUserId()));
        }

        return validActions;
    }
}