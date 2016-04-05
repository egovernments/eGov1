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
package org.egov.works.lineestimate.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.Accountdetailkey;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CFinancialYear;
import org.egov.commons.dao.AccountdetailkeyHibernateDAO;
import org.egov.commons.dao.AccountdetailtypeHibernateDAO;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.dao.budget.BudgetDetailsDAO;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.workflow.WorkFlowMatrix;
import org.egov.model.budget.BudgetUsage;
import org.egov.pims.commons.Position;
import org.egov.works.lineestimate.entity.DocumentDetails;
import org.egov.works.lineestimate.entity.LineEstimate;
import org.egov.works.lineestimate.entity.LineEstimateAppropriation;
import org.egov.works.lineestimate.entity.LineEstimateDetails;
import org.egov.works.lineestimate.entity.LineEstimateForLoaSearchRequest;
import org.egov.works.lineestimate.entity.LineEstimateForLoaSearchResult;
import org.egov.works.lineestimate.entity.LineEstimateSearchRequest;
import org.egov.works.lineestimate.entity.enums.LineEstimateStatus;
import org.egov.works.lineestimate.entity.enums.WorkCategory;
import org.egov.works.lineestimate.repository.LineEstimateAppropriationRepository;
import org.egov.works.lineestimate.repository.LineEstimateDetailsRepository;
import org.egov.works.lineestimate.repository.LineEstimateRepository;
import org.egov.works.models.estimate.EstimateNumberGenerator;
import org.egov.works.models.estimate.ProjectCode;
import org.egov.works.utils.WorksConstants;
import org.egov.works.utils.WorksUtils;
import org.elasticsearch.common.joda.time.DateTime;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class LineEstimateService {

    private static final Logger LOG = LoggerFactory.getLogger(LineEstimateService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final LineEstimateRepository lineEstimateRepository;

    private final LineEstimateDetailsRepository lineEstimateDetailsRepository;

    private final LineEstimateAppropriationRepository lineEstimateAppropriationRepository;

    @Autowired
    private LineEstimateNumberGenerator lineEstimateNumberGenerator;

    @Autowired
    private WorkOrderIdentificationNumberGenerator workOrderIdentificationNumberGenerator;

    @Autowired
    private BudgetAppropriationNumberGenerator budgetAppropriationNumberGenerator;

    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;

    @Autowired
    private EstimateNumberGenerator estimateNumberGenerator;

    @Autowired
    private WorksUtils worksUtils;

    @Autowired
    private EisCommonService eisCommonService;

    @Autowired
    private SimpleWorkflowService<LineEstimate> lineEstimateWorkflowService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private PositionMasterService positionMasterService;

    @Autowired
    private AccountdetailtypeHibernateDAO accountdetailtypeHibernateDAO;

    @Autowired
    private AccountdetailkeyHibernateDAO accountdetailkeyHibernateDAO;

    @Autowired
    private FinancialYearHibernateDAO financialYearHibernateDAO;

    @Autowired
    private PersistenceService<ProjectCode, Long> projectCodeService;

    @Autowired
    private BudgetDetailsDAO budgetDetailsDAO;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    @Autowired
    public LineEstimateService(final LineEstimateRepository lineEstimateRepository,
            final LineEstimateDetailsRepository lineEstimateDetailsRepository,
            final LineEstimateAppropriationRepository lineEstimateAppropriationRepository) {
        this.lineEstimateRepository = lineEstimateRepository;
        this.lineEstimateDetailsRepository = lineEstimateDetailsRepository;
        this.lineEstimateAppropriationRepository = lineEstimateAppropriationRepository;
    }

    public LineEstimate getLineEstimateById(final Long id) {
        return lineEstimateRepository.findById(id);
    }

    @Transactional
    public LineEstimate create(final LineEstimate lineEstimate, final MultipartFile[] files,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction) throws IOException {
        lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE,
                LineEstimateStatus.CREATED.toString()));
        final CFinancialYear financialYear = getCurrentFinancialYear(lineEstimate.getLineEstimateDate());
        for (final LineEstimateDetails lineEstimateDetail : lineEstimate.getLineEstimateDetails()) {
            final String estimateNumber = estimateNumberGenerator.generateEstimateNumber(lineEstimate, financialYear);
            lineEstimateDetail.setEstimateNumber(estimateNumber);
            lineEstimateDetail.setLineEstimate(lineEstimate);
        }
        if (lineEstimate.getLineEstimateNumber() == null || lineEstimate.getLineEstimateNumber().isEmpty()) {
            final String lineEstimateNumber = lineEstimateNumberGenerator.generateLineEstimateNumber(lineEstimate);
            lineEstimate.setLineEstimateNumber(lineEstimateNumber);
        }

        final LineEstimate newLineEstimate = lineEstimateRepository.save(lineEstimate);

        createLineEstimateWorkflowTransition(newLineEstimate,
                approvalPosition, approvalComent, additionalRule, workFlowAction);

        final List<DocumentDetails> documentDetails = worksUtils.getDocumentDetails(files, newLineEstimate,
                WorksConstants.MODULE_NAME_LINEESTIMATE);
        if (!documentDetails.isEmpty()) {
            newLineEstimate.setDocumentDetails(documentDetails);
            worksUtils.persistDocuments(documentDetails);
        }
        return newLineEstimate;
    }

    private LineEstimate update(final LineEstimate lineEstimate, final String removedLineEstimateDetailsIds,
            final MultipartFile[] files, final CFinancialYear financialYear) throws IOException {
        for (final LineEstimateDetails lineEstimateDetails : lineEstimate.getLineEstimateDetails())
            if (lineEstimateDetails != null && lineEstimateDetails.getId() == null) {
                final String estimateNumber = estimateNumberGenerator.generateEstimateNumber(lineEstimate, financialYear);
                lineEstimateDetails.setEstimateNumber(estimateNumber);
                lineEstimateDetails.setLineEstimate(lineEstimate);
            }
        List<LineEstimateDetails> list = new ArrayList<LineEstimateDetails>(lineEstimate.getLineEstimateDetails());
        list = removeDeletedLineEstimateDetails(list, removedLineEstimateDetailsIds);

        lineEstimate.setLineEstimateDetails(list);
        final LineEstimate persistedLineEstimate = lineEstimateRepository.save(lineEstimate);

        final List<DocumentDetails> documentDetails = worksUtils.getDocumentDetails(files, persistedLineEstimate,
                WorksConstants.MODULE_NAME_LINEESTIMATE);
        if (!documentDetails.isEmpty()) {
            persistedLineEstimate.setDocumentDetails(documentDetails);
            worksUtils.persistDocuments(documentDetails);
        }
        return lineEstimateRepository.save(persistedLineEstimate);
    }

    public LineEstimate getLineEstimateByLineEstimateNumber(final String lineEstimateNumber) {
        return lineEstimateRepository.findByLineEstimateNumber(lineEstimateNumber);
    }

    public List<LineEstimateDetails> removeDeletedLineEstimateDetails(final List<LineEstimateDetails> list,
            final String removedLineEstimateDetailsIds) {
        final List<LineEstimateDetails> details = new ArrayList<LineEstimateDetails>();
        if (null != removedLineEstimateDetailsIds) {
            final String[] ids = removedLineEstimateDetailsIds.split(",");
            final List<String> strList = new ArrayList<String>();
            for (final String str : ids)
                strList.add(str);
            for (final LineEstimateDetails line : list)
                if (line.getId() != null) {
                    if (!strList.contains(line.getId().toString()))
                        details.add(line);
                } else
                    details.add(line);
        } else
            return list;
        return details;
    }

    public List<LineEstimate> searchLineEstimates(final LineEstimateSearchRequest lineEstimateSearchRequest) {
        final Criteria criteria = entityManager.unwrap(Session.class).createCriteria(LineEstimate.class)
                .createAlias("lineEstimateDetails", "lineEstimateDetail");
        if (lineEstimateSearchRequest != null) {
            if (lineEstimateSearchRequest.getAdminSanctionNumber() != null)
                criteria.add(Restrictions.eq("adminSanctionNumber", lineEstimateSearchRequest.getAdminSanctionNumber()));
            if (lineEstimateSearchRequest.getBudgetHead() != null)
                criteria.add(Restrictions.eq("budgetHead.id", lineEstimateSearchRequest.getBudgetHead()));
            if (lineEstimateSearchRequest.getExecutingDepartment() != null)
                criteria.add(Restrictions.eq("executingDepartment.id", lineEstimateSearchRequest.getExecutingDepartment()));
            if (lineEstimateSearchRequest.getFunction() != null)
                criteria.add(Restrictions.eq("function.id", lineEstimateSearchRequest.getFunction()));
            if (lineEstimateSearchRequest.getFund() != null)
                criteria.add(Restrictions.eq("fund.id", lineEstimateSearchRequest.getFund().intValue()));
            if (lineEstimateSearchRequest.getEstimateNumber() != null)
                criteria.add(Restrictions.eq("lineEstimateNumber", lineEstimateSearchRequest.getEstimateNumber()));
            if (lineEstimateSearchRequest.getAdminSanctionFromDate() != null)
                criteria.add(Restrictions.ge("adminSanctionDate", lineEstimateSearchRequest.getAdminSanctionFromDate()));
            if (lineEstimateSearchRequest.getAdminSanctionToDate() != null)
                criteria.add(Restrictions.le("adminSanctionDate", lineEstimateSearchRequest.getAdminSanctionToDate()));
        }
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    public List<LineEstimateDetails> searchLineEstimatesForLoa(final LineEstimateForLoaSearchRequest lineEstimateForLoaSearchRequest) {

        final List<String> lineEstimateNumbers = lineEstimateDetailsRepository
                .findEstimateNumbersToSearchLineEstimatesForLoa(WorksConstants.CANCELLED_STATUS);

        final Criteria criteria = entityManager.unwrap(Session.class).createCriteria(LineEstimateDetails.class)
                .createAlias("lineEstimate", "lineEstimate")
                .createAlias("lineEstimate.status", "status");
        if (lineEstimateForLoaSearchRequest != null) {
            if (lineEstimateForLoaSearchRequest.getAdminSanctionNumber() != null)
                criteria.add(Restrictions.ilike("lineEstimate.adminSanctionNumber", lineEstimateForLoaSearchRequest.getAdminSanctionNumber()));
            if (lineEstimateForLoaSearchRequest.getExecutingDepartment() != null)
                criteria.add(Restrictions.eq("lineEstimate.executingDepartment.id", lineEstimateForLoaSearchRequest.getExecutingDepartment()));
            if (lineEstimateForLoaSearchRequest.getEstimateNumber() != null)
                criteria.add(Restrictions.eq("estimateNumber",
                        lineEstimateForLoaSearchRequest.getEstimateNumber()));
            if (lineEstimateForLoaSearchRequest.getAdminSanctionFromDate() != null)
                criteria.add(Restrictions.ge("lineEstimate.adminSanctionDate", lineEstimateForLoaSearchRequest.getAdminSanctionFromDate()));
            if (lineEstimateForLoaSearchRequest.getAdminSanctionToDate() != null)
                criteria.add(Restrictions.le("lineEstimate.adminSanctionDate", lineEstimateForLoaSearchRequest.getAdminSanctionToDate()));
            if (lineEstimateForLoaSearchRequest.getLineEstimateCreatedBy() != null)
                criteria.add(Restrictions.eq("lineEstimate.createdBy.id", lineEstimateForLoaSearchRequest.getLineEstimateCreatedBy()));
            criteria.add(Restrictions.in("estimateNumber", lineEstimateNumbers));
            criteria.add(Restrictions.eq("status.code", LineEstimateStatus.TECHNICAL_SANCTIONED.toString()));
        }
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    public List<String> findLineEstimateNumbers(final String name) {
        final List<LineEstimate> lineEstimates = lineEstimateRepository
                .findByLineEstimateNumberContainingIgnoreCase(name);
        final List<String> results = new ArrayList<String>();
        for (final LineEstimate details : lineEstimates)
            results.add(details.getLineEstimateNumber());
        return results;
    }

    public List<String> findEstimateNumbersForLoa(final String name) {
        final List<String> lineEstimateNumbers = lineEstimateDetailsRepository.findEstimateNumbersForLoa("%" + name + "%",
                LineEstimateStatus.TECHNICAL_SANCTIONED.toString(), WorksConstants.CANCELLED_STATUS);

        return lineEstimateNumbers;
    }

    public List<String> findAdminSanctionNumbers(final String name) {
        final List<LineEstimate> lineEstimates = lineEstimateRepository.findByAdminSanctionNumberContainingIgnoreCase(name);
        final List<String> results = new ArrayList<String>();
        for (final LineEstimate estimate : lineEstimates)
            results.add(estimate.getAdminSanctionNumber());
        return results;
    }

    public List<String> findAdminSanctionNumbersForLoa(final String name) {
        final List<String> adminSanctionNumbers = lineEstimateDetailsRepository.findAdminSanctionNumbersForLoa("%" + name + "%",
                LineEstimateStatus.TECHNICAL_SANCTIONED.toString(), WorksConstants.CANCELLED_STATUS);

        return adminSanctionNumbers;
    }

    public List<LineEstimateForLoaSearchResult> searchLineEstimatesForLOA(
            final LineEstimateForLoaSearchRequest lineEstimateForLoaSearchRequest) {
        final List<LineEstimateDetails> lineEstimateDetails = searchLineEstimatesForLoa(lineEstimateForLoaSearchRequest);
        final List<LineEstimateForLoaSearchResult> lineEstimateForLoaSearchResults = new ArrayList<LineEstimateForLoaSearchResult>();
        for (final LineEstimateDetails led : lineEstimateDetails) {
            final LineEstimateForLoaSearchResult result = new LineEstimateForLoaSearchResult();
            result.setId(led.getLineEstimate().getId());
            result.setAdminSanctionNumber(led.getLineEstimate().getAdminSanctionNumber());
            result.setCreatedBy(led.getLineEstimate().getCreatedBy().getName());
            result.setEstimateAmount(led.getEstimateAmount());
            result.setEstimateNumber(led.getEstimateNumber());
            result.setNameOfWork(led.getNameOfWork());
            if (led.getLineEstimate().getAdminSanctionBy() != null)
                result.setAdminSanctionBy(led.getLineEstimate().getAdminSanctionBy().getName());
            result.setActualEstimateAmount(led.getActualEstimateAmount());
            lineEstimateForLoaSearchResults.add(result);
        }
        return lineEstimateForLoaSearchResults;
    }

    public List<Hashtable<String, Object>> getHistory(final LineEstimate lineEstimate) {
        User user = null;
        final List<Hashtable<String, Object>> historyTable = new ArrayList<Hashtable<String, Object>>();
        final State state = lineEstimate.getState();
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
            if (!lineEstimate.getStateHistory().isEmpty() && lineEstimate.getStateHistory() != null)
                Collections.reverse(lineEstimate.getStateHistory());
            for (final StateHistory stateHistory : lineEstimate.getStateHistory()) {
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

    public Long getApprovalPositionByMatrixDesignation(final LineEstimate lineEstimate,
            Long approvalPosition, final String additionalRule, final String mode, final String workFlowAction) {
        final WorkFlowMatrix wfmatrix = lineEstimateWorkflowService.getWfMatrix(lineEstimate
                .getStateType(), null, null, additionalRule, lineEstimate.getCurrentState().getValue(), null);
        if (lineEstimate.getStatus() != null && lineEstimate.getStatus().getCode() != null)
            if (lineEstimate.getStatus().getCode().equals(LineEstimateStatus.CREATED.toString())
                    && lineEstimate.getState() != null)
                if (mode.equals("edit"))
                    approvalPosition = lineEstimate.getState().getOwnerPosition().getId();
                else
                    approvalPosition = worksUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                            lineEstimate);
        if (lineEstimate.getStatus().getCode()
                .equals(LineEstimateStatus.CHECKED.toString()))
            approvalPosition = worksUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                    lineEstimate);
        if (workFlowAction.equals(WorksConstants.CANCEL_ACTION)
                && wfmatrix.getNextState().equals(WorksConstants.WF_STATE_CREATED))
            approvalPosition = null;

        return approvalPosition;
    }

    @Transactional
    public LineEstimate updateLineEstimateDetails(
            final LineEstimate lineEstimate,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction, final String mode, final ReportOutput reportOutput,
            final String removedLineEstimateDetailsIds,
            final MultipartFile[] files, final CFinancialYear financialYear) throws ValidationException, IOException {
        LineEstimate updatedLineEstimate = null;

        if (lineEstimate.getStatus().getCode().equals(LineEstimateStatus.REJECTED.toString())) {
            updatedLineEstimate = update(lineEstimate, removedLineEstimateDetailsIds, files, financialYear);
            try {
                if (workFlowAction.equals(WorksConstants.FORWARD_ACTION) &&
                        lineEstimate.getWorkCategory().toString().equals(WorkCategory.NON_SLUM_WORK.toString()))
                    resetWorkCategoryDetailsOnModify(lineEstimate);
                lineEstimateStatusChange(updatedLineEstimate, workFlowAction, mode);
            } catch (final ValidationException e) {
                throw new ValidationException(e.getErrors());
            }
        } else
            try {
                if (lineEstimate.getStatus().getCode().equals(LineEstimateStatus.BUDGET_SANCTIONED.toString())
                        && workFlowAction.equals(WorksConstants.REJECT_ACTION))
                    resetAdminSanctionDetails(lineEstimate);
                else if (lineEstimate.getStatus().getCode().equals(LineEstimateStatus.BUDGET_SANCTIONED.toString()) &&
                        !workFlowAction.equals(WorksConstants.REJECT_ACTION))
                    setAdminSanctionByAndDate(lineEstimate);
                doBudgetoryAppropriation(lineEstimate, workFlowAction);
                lineEstimateStatusChange(lineEstimate, workFlowAction, mode);
            } catch (final ValidationException e) {
                throw new ValidationException(e.getErrors());
            }
        updatedLineEstimate = lineEstimateRepository.save(lineEstimate);

        createLineEstimateWorkflowTransition(updatedLineEstimate,
                approvalPosition, approvalComent, additionalRule, workFlowAction);

        return updatedLineEstimate;
    }

    private void resetWorkCategoryDetailsOnModify(final LineEstimate lineEstimate) {
        lineEstimate.setTypeOfSlum(null);
        lineEstimate.setBeneficiary(null);
    }

    private void resetAdminSanctionDetails(final LineEstimate lineEstimate) {
        lineEstimate.setAdminSanctionNumber(null);
        lineEstimate.setCouncilResolutionNumber(null);
        lineEstimate.setCouncilResolutionDate(null);
    }

    private void setAdminSanctionByAndDate(final LineEstimate lineEstimate) {
        lineEstimate.setAdminSanctionDate(new Date());
        lineEstimate.setAdminSanctionBy(securityUtils.getCurrentUser());
    }

    private void doBudgetoryAppropriation(final LineEstimate lineEstimate, final String workFlowAction) {
        if (lineEstimate.getStatus().getCode()
                .equals(LineEstimateStatus.CHECKED.toString()) && !workFlowAction.equals(WorksConstants.REJECT_ACTION)) {
            final List<Long> budgetheadid = new ArrayList<Long>();
            budgetheadid.add(lineEstimate.getBudgetHead().getId());

            for (final LineEstimateDetails led : lineEstimate.getLineEstimateDetails()) {
                final boolean flag = checkConsumeEncumbranceBudget(led, getCurrentFinancialYear(new Date()).getId(), led
                        .getEstimateAmount().doubleValue(), budgetheadid);

                if (!flag)
                    throw new ValidationException("", "error.budgetappropriation.amount");
            }
        } else if (workFlowAction.equals(WorksConstants.REJECT_ACTION))
            if (lineEstimate.getStatus().getCode()
                    .equals(LineEstimateStatus.BUDGET_SANCTIONED.toString()))
                for (final LineEstimateDetails led : lineEstimate.getLineEstimateDetails())
                    releaseBudgetOnReject(led);

    }

    public void lineEstimateStatusChange(final LineEstimate lineEstimate, final String workFlowAction,
            final String mode) throws ValidationException {
        if (null != lineEstimate && null != lineEstimate.getStatus()
                && null != lineEstimate.getStatus().getCode())
            if (lineEstimate.getStatus().getCode().equals(LineEstimateStatus.CREATED.toString())
                    && lineEstimate.getState() != null && workFlowAction.equals(WorksConstants.SUBMIT_ACTION))
                lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE,
                        LineEstimateStatus.CHECKED.toString()));
            else if (lineEstimate.getStatus().getCode()
                    .equals(LineEstimateStatus.CHECKED.toString()) && !workFlowAction.equals(WorksConstants.REJECT_ACTION))
                lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE,
                        LineEstimateStatus.BUDGET_SANCTIONED.toString()));
            else if (lineEstimate.getStatus().getCode()
                    .equals(LineEstimateStatus.BUDGET_SANCTIONED.toString())
                    && !workFlowAction.equals(WorksConstants.REJECT_ACTION)) {
                lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE,
                        LineEstimateStatus.ADMINISTRATIVE_SANCTIONED.toString()));

                for (final LineEstimateDetails led : lineEstimate.getLineEstimateDetails())
                    setProjectCode(led);
            } else if (lineEstimate.getStatus().getCode()
                    .equals(LineEstimateStatus.ADMINISTRATIVE_SANCTIONED.toString())
                    && !workFlowAction.equals(WorksConstants.REJECT_ACTION))
                lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE,
                        LineEstimateStatus.TECHNICAL_SANCTIONED.toString()));
            else if (workFlowAction.equals(WorksConstants.REJECT_ACTION))
                lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE,
                        LineEstimateStatus.REJECTED.toString()));
            else if (lineEstimate.getStatus().getCode()
                    .equals(LineEstimateStatus.REJECTED.toString()) && workFlowAction.equals(WorksConstants.CANCEL_ACTION))
                lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE,
                        LineEstimateStatus.CANCELLED.toString()));
            else if (lineEstimate.getStatus().getCode()
                    .equals(LineEstimateStatus.REJECTED.toString()) && workFlowAction.equals(WorksConstants.FORWARD_ACTION))
                lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE,
                        LineEstimateStatus.CREATED.toString()));

    }

    public List<User> getLineEstimateCreatedByUsers() {
        return lineEstimateRepository.getLineEstimateCreatedByUsers(LineEstimateStatus.TECHNICAL_SANCTIONED.toString());
    }

    public List<Department> getUserDepartments(final User currentUser) {
        final List<Assignment> assignments = assignmentService.findByEmployeeAndGivenDate(currentUser.getId(), new Date());
        final List<Department> uniqueDepartmentList = new ArrayList<Department>();
        Department prevDepartment = new Department();
        final Iterator iterator = assignments.iterator();
        while (iterator.hasNext()) {
            final Assignment assignment = (Assignment) iterator.next();
            if (!assignment.getDepartment().getName().equals(prevDepartment.getName()))
                uniqueDepartmentList.add(assignment.getDepartment());
            prevDepartment = assignment.getDepartment();
        }
        return uniqueDepartmentList;
    }

    public LineEstimateDetails findByEstimateNumber(final String estimateNumber) {
        return lineEstimateDetailsRepository.findByEstimateNumberAndLineEstimate_Status_CodeEquals(estimateNumber, LineEstimateStatus.TECHNICAL_SANCTIONED.toString());
    }

    public void createLineEstimateWorkflowTransition(final LineEstimate lineEstimate,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction) {
        if (LOG.isDebugEnabled())
            LOG.debug(" Create WorkFlow Transition Started  ...");
        final User user = securityUtils.getCurrentUser();
        final DateTime currentDate = new DateTime();
        final Assignment userAssignment = assignmentService.getPrimaryAssignmentForUser(user.getId());
        Position pos = null;
        Assignment wfInitiator = null;
        final String currState = "";
        final String natureOfwork = WorksConstants.WORKFLOWTYPE_DISPLAYNAME;

        if (null != lineEstimate.getId())
            wfInitiator = assignmentService.getPrimaryAssignmentForUser(lineEstimate.getCreatedBy().getId());
        if (WorksConstants.REJECT_ACTION.toString().equalsIgnoreCase(workFlowAction)) {
            if (wfInitiator.equals(userAssignment))
                lineEstimate.transition(true).end().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withDateInfo(currentDate.toDate()).withNatureOfTask(natureOfwork);
            else {
                final String stateValue = WorksConstants.WF_STATE_REJECTED;
                lineEstimate.transition(true).withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent)
                        .withStateValue(stateValue).withDateInfo(currentDate.toDate())
                        .withOwner(wfInitiator.getPosition())
                        .withNextAction("")
                        .withNatureOfTask(natureOfwork);
            }
        } else {
            if (null != approvalPosition && approvalPosition != -1 && !approvalPosition.equals(Long.valueOf(0)))
                pos = positionMasterService.getPositionById(approvalPosition);
            WorkFlowMatrix wfmatrix = null;
            if (null == lineEstimate.getState()) {
                wfmatrix = lineEstimateWorkflowService.getWfMatrix(lineEstimate.getStateType(), null,
                        null, additionalRule, currState, null);
                lineEstimate.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent)
                        .withStateValue(wfmatrix.getNextState()).withDateInfo(new Date()).withOwner(pos)
                        .withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(natureOfwork);
            } else if (WorksConstants.CANCEL_ACTION.toString().equalsIgnoreCase(workFlowAction)) {
                final String stateValue = WorksConstants.WF_STATE_CANCELLED;
                wfmatrix = lineEstimateWorkflowService.getWfMatrix(lineEstimate.getStateType(), null,
                        null, additionalRule, lineEstimate.getCurrentState().getValue(), null);
                lineEstimate.transition(true).withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent)
                        .withStateValue(stateValue).withDateInfo(currentDate.toDate()).withOwner(pos)
                        .withNextAction("")
                        .withNatureOfTask(natureOfwork);
            } else {
                wfmatrix = lineEstimateWorkflowService.getWfMatrix(lineEstimate.getStateType(), null,
                        null, additionalRule, lineEstimate.getCurrentState().getValue(), null);
                lineEstimate.transition(true).withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent)
                        .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate()).withOwner(pos)
                        .withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(natureOfwork);
            }
        }
        if (LOG.isDebugEnabled())
            LOG.debug(" WorkFlow Transition Completed  ...");
    }

    public LineEstimate getLineEstimateByTechnicalSanctionNumber(final String technicalSanctionNumber) {
        return lineEstimateRepository.findByTechnicalSanctionNumberIgnoreCaseAndStatus_CodeNot(technicalSanctionNumber,
                LineEstimateStatus.CANCELLED.toString());
    }

    public void setProjectCode(final LineEstimateDetails lineEstimateDetails) {
        final ProjectCode projectCode = new ProjectCode();
        final String code = workOrderIdentificationNumberGenerator.generateWorkOrderIdentificationNumber(lineEstimateDetails);
        projectCode.setCode(code);

        projectCode.setCodeName(lineEstimateDetails.getNameOfWork());
        projectCode.setDescription(lineEstimateDetails.getNameOfWork());
        projectCode.setActive(true);
        projectCode.setEgwStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(ProjectCode.class.getSimpleName(),
                WorksConstants.DEFAULT_PROJECTCODE_STATUS));
        lineEstimateDetails.setProjectCode(projectCode);
        projectCodeService.persist(projectCode);
        createAccountDetailKey(projectCode);
    }

    protected void createAccountDetailKey(final ProjectCode proj) {
        final Accountdetailtype accountdetailtype = accountdetailtypeHibernateDAO.getAccountdetailtypeByName("PROJECTCODE");
        final Accountdetailkey adk = new Accountdetailkey();
        adk.setGroupid(1);
        adk.setDetailkey(proj.getId().intValue());
        adk.setDetailname(accountdetailtype.getAttributename());
        adk.setAccountdetailtype(accountdetailtype);
        accountdetailkeyHibernateDAO.create(adk);

    }

    public CFinancialYear getCurrentFinancialYear(final Date estimateDate) {
        return financialYearHibernateDAO.getFinYearByDate(estimateDate);
    }

    public LineEstimate getLineEstimateByAdminSanctionNumber(final String adminSanctionNumber) {
        return lineEstimateRepository.findByAdminSanctionNumberIgnoreCaseAndStatus_codeNotLike(adminSanctionNumber,
                WorksConstants.CANCELLED_STATUS);
    }

    public boolean checkConsumeEncumbranceBudget(final LineEstimateDetails lineEstimateDetails, final Long finyrId,
            final double budgApprAmnt, final List<Long> budgetheadid) {
        final boolean flag = true;
        final BudgetUsage budgetUsage = budgetDetailsDAO.consumeEncumbranceBudget(
                budgetAppropriationNumberGenerator.generateBudgetAppropriationNumber(lineEstimateDetails),
                finyrId,
                Integer.valueOf(11),
                lineEstimateDetails.getEstimateNumber(),
                Integer.parseInt(lineEstimateDetails.getLineEstimate().getExecutingDepartment().getId().toString()),
                lineEstimateDetails.getLineEstimate().getFunction() == null ? null : lineEstimateDetails.getLineEstimate()
                        .getFunction().getId(),
                null,
                lineEstimateDetails.getLineEstimate().getScheme() == null ? null : lineEstimateDetails.getLineEstimate()
                        .getScheme().getId(),
                lineEstimateDetails.getLineEstimate().getSubScheme() == null ? null : lineEstimateDetails.getLineEstimate()
                        .getSubScheme().getId(),
                lineEstimateDetails.getLineEstimate().getWard() == null ? null : Integer.parseInt(lineEstimateDetails
                        .getLineEstimate().getWard().getId().toString()),
                budgetheadid,
                lineEstimateDetails.getLineEstimate().getFund() == null ? null : lineEstimateDetails.getLineEstimate().getFund()
                        .getId(),
                budgApprAmnt);

        if (budgetUsage != null)
            persistBudgetAppropriationDetails(lineEstimateDetails, budgetUsage);
        else
            return false;

        return flag;
    }

    public boolean releaseBudgetOnReject(final LineEstimateDetails lineEstimateDetails) throws ValidationException {

        final LineEstimateAppropriation lineEstimateAppropriation = lineEstimateAppropriationRepository
                .findByLineEstimateDetails_EstimateNumber(lineEstimateDetails.getEstimateNumber());
        final List<Long> budgetheadid = new ArrayList<Long>();
        budgetheadid.add(lineEstimateDetails.getLineEstimate().getBudgetHead().getId());
        BudgetUsage budgetUsage = null;
        final boolean flag = false;

        budgetUsage = budgetDetailsDAO.releaseEncumbranceBudget(
                lineEstimateAppropriation.getBudgetUsage() == null ? null
                        : budgetAppropriationNumberGenerator.generateCancelledBudgetAppropriationNumber(
                                lineEstimateAppropriation.getBudgetUsage().getAppropriationnumber()),
                lineEstimateAppropriation.getBudgetUsage().getFinancialYearId().longValue(),
                Integer.valueOf(11),
                lineEstimateAppropriation.getLineEstimateDetails().getEstimateNumber(),
                Integer.parseInt(lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getExecutingDepartment()
                        .getId().toString()),
                lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getFunction() == null ? null
                        : lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getFunction().getId(),
                null,
                lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getScheme() == null ? null
                        : lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getScheme().getId(),
                lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getSubScheme() == null ? null
                        : lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getSubScheme().getId(),
                lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getWard() == null ? null : Integer
                        .parseInt(lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getWard().getId()
                                .toString()),
                lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getBudgetHead() == null ? null
                        : budgetheadid,
                lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getFund() == null ? null
                        : lineEstimateAppropriation.getLineEstimateDetails().getLineEstimate().getFund().getId(),
                lineEstimateAppropriation.getBudgetUsage().getConsumedAmount());

        if (lineEstimateAppropriation.getLineEstimateDetails() != null)
            persistBudgetReleaseDetails(lineEstimateDetails, budgetUsage);
        return flag;
    }

    private void persistBudgetAppropriationDetails(final LineEstimateDetails lineEstimateDetails,
            final BudgetUsage budgetUsage) {
        LineEstimateAppropriation lineEstimateAppropriation = null;
        lineEstimateAppropriation = lineEstimateAppropriationRepository
                .findByLineEstimateDetails_EstimateNumber(lineEstimateDetails.getEstimateNumber());

        if (lineEstimateAppropriation != null)
            lineEstimateAppropriation.setBudgetUsage(budgetUsage);
        else {
            lineEstimateAppropriation = new LineEstimateAppropriation();
            lineEstimateAppropriation.setLineEstimateDetails(lineEstimateDetails);
            ;
            lineEstimateAppropriation.setBudgetUsage(budgetUsage);
        }
        lineEstimateAppropriationRepository.save(lineEstimateAppropriation);
    }

    private void persistBudgetReleaseDetails(final LineEstimateDetails lineEstimateDetails, final BudgetUsage budgetUsage) {
        LineEstimateAppropriation lineEstimateAppropriation = null;
        lineEstimateAppropriation = lineEstimateAppropriationRepository
                .findByLineEstimateDetails_EstimateNumber(lineEstimateDetails.getEstimateNumber());
        lineEstimateAppropriation.setBudgetUsage(budgetUsage);
        lineEstimateAppropriationRepository.save(lineEstimateAppropriation);
    }
}
