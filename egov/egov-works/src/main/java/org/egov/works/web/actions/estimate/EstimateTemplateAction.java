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
package org.egov.works.web.actions.estimate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.entity.UOM;
import org.egov.commons.EgwTypeOfWork;
import org.egov.infra.web.struts.actions.SearchFormAction;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.search.SearchQueryHQL;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.service.EmployeeServiceOld;
import org.egov.pims.service.PersonalInformationService;
import org.egov.works.models.estimate.EstimateTemplate;
import org.egov.works.models.estimate.EstimateTemplateActivity;
import org.egov.works.models.masters.ScheduleOfRate;
import org.egov.works.services.AbstractEstimateService;
import org.egov.works.utils.WorksConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class EstimateTemplateAction extends SearchFormAction {
    
    private static final long serialVersionUID = 3610026596221473556L;
    private static final String VIEW = "view";
    private EstimateTemplate estimateTemplate = new EstimateTemplate();
    private List<EstimateTemplateActivity> sorActivities = new LinkedList<EstimateTemplateActivity>();
    private List<EstimateTemplateActivity> nonSorActivities = new LinkedList<EstimateTemplateActivity>();
    @Autowired
    private EmployeeServiceOld employeeService;
    private PersonalInformationService personalInformationService;
    private PersistenceService<EstimateTemplate, Long> estimateTemplateService;
    private String mode = null;
    private Long id;
    private String sourcePage = null;
    private Long typeOfWork;
    private String estimateTemplateCode;
    private Long subTypeOfWork;

    private String checkDWRelatedSORs;

    public String getCheckDWRelatedSORs() {
        return checkDWRelatedSORs;
    }

    public void setCheckDWRelatedSORs(final String checkDWRelatedSORs) {
        this.checkDWRelatedSORs = checkDWRelatedSORs;
    }

    private AbstractEstimateService abstractEstimateService;

    public EstimateTemplateAction() {
        addRelatedEntity("workType", EgwTypeOfWork.class);
        addRelatedEntity("subType", EgwTypeOfWork.class);
    }

    @Override
    public Object getModel() {
        // TODO Auto-generated method stub
        return estimateTemplate;
    }

    protected void setModel(final EstimateTemplate estimateTemplate) {
        this.estimateTemplate = estimateTemplate;
    }

    public String edit() {
        return EDIT;
    }

    @Override
    public void prepare() {
        if (id != null)
            estimateTemplate = estimateTemplateService.findById(id, false);
        final AjaxEstimateAction ajaxEstimateAction = new AjaxEstimateAction();
        ajaxEstimateAction.setPersistenceService(getPersistenceService());
        ajaxEstimateAction.setEmployeeService(employeeService);
        ajaxEstimateAction.setPersonalInformationService(personalInformationService);
        super.prepare();
        setupDropdownDataExcluding("workType", "subType");
        addDropdownData("parentCategoryList",
                getPersistenceService().findAllBy("from EgwTypeOfWork etw1 where etw1.parentid is null"));
        List<UOM> uomList = getPersistenceService().findAllBy("from UOM  order by upper(uom)");
        if (!VIEW.equals(mode))
            uomList = abstractEstimateService.prepareUomListByExcludingSpecialUoms(uomList);
        addDropdownData("uomList", uomList);
        addDropdownData("scheduleCategoryList",
                getPersistenceService().findAllBy("from ScheduleCategory order by upper(code)"));
        populateCategoryList(ajaxEstimateAction, estimateTemplate.getWorkType() != null);

    }

    public String newform() {
        return NEW;
    }

    @Transactional
    public String save() {
        estimateTemplate.getEstimateTemplateActivities().clear();
        populateSorActivities();
        populateNonSorActivities();
        populateActivities();
        if (estimateTemplate.getId() == null)
            estimateTemplate.setStatus(1);
        else
            setMode("edit");
        estimateTemplate = estimateTemplateService.persist(estimateTemplate);
        return "success";
    }

    protected void populateSorActivities() {
        for (final EstimateTemplateActivity activity : sorActivities)
            if (validSorActivity(activity)) {
                activity.setSchedule((ScheduleOfRate) getPersistenceService().find("from ScheduleOfRate where id = ?",
                        activity.getSchedule().getId()));
                activity.setUom(activity.getSchedule().getUom());
                estimateTemplate.addActivity(activity);
            }
    }

    protected boolean validSorActivity(final EstimateTemplateActivity activity) {
        if (activity != null && activity.getSchedule() != null && activity.getSchedule().getId() != null)
            return true;

        return false;
    }

    protected void populateNonSorActivities() {
        for (final EstimateTemplateActivity activity : nonSorActivities)
            if (activity != null) {
                activity.setUom(activity.getNonSor().getUom());
                estimateTemplate.addActivity(activity);
            }
    }

    private void populateActivities() {
        for (final EstimateTemplateActivity activity : estimateTemplate.getEstimateTemplateActivities())
            activity.setEstimateTemplate(estimateTemplate);
    }

    protected void populateCategoryList(final AjaxEstimateAction ajaxEstimateAction, final boolean categoryPopulated) {
        if (categoryPopulated) {
            ajaxEstimateAction.setCategory(estimateTemplate.getWorkType().getId());
            ajaxEstimateAction.subcategories();
            addDropdownData("categoryList", ajaxEstimateAction.getSubCategories());
        } else
            addDropdownData("categoryList", Collections.emptyList());
    }

    public boolean validCode() {
        boolean status = false;
        if (estimateTemplate != null && estimateTemplate.getCode() != null) {
            final AjaxEstimateTemplateAction ajaxEstimateTemplateAction = new AjaxEstimateTemplateAction();
            ajaxEstimateTemplateAction.setCode(estimateTemplate.getCode());
            ajaxEstimateTemplateAction.setPersistenceService(persistenceService);
            if (ajaxEstimateTemplateAction.getCodeCheck())
                status = true;
        }
        return status;
    }

    @Override
    public String search() {
        estimateTemplate.setStatus(1);
        return "search";
    }

    public String searchDetails() {
        if (estimateTemplate.getWorkType() == null || estimateTemplate.getWorkType().getId() == -1) {
            final String messageKey = "estimate.template.search.workType.error";
            addActionError(getText(messageKey));
            return "search";
        }
        setPageSize(WorksConstants.PAGE_SIZE);
        super.search();
        return "search";
    }

    public EmployeeServiceOld getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(final EmployeeServiceOld employeeService) {
        this.employeeService = employeeService;
    }

    public void setPersonalInformationService(final PersonalInformationService personalInformationService) {
        this.personalInformationService = personalInformationService;
    }

    public List<EstimateTemplateActivity> getSorActivities() {
        return sorActivities;
    }

    public void setSorActivities(final List<EstimateTemplateActivity> sorActivities) {
        this.sorActivities = sorActivities;
    }

    public List<EstimateTemplateActivity> getNonSorActivities() {
        return nonSorActivities;
    }

    public void setNonSorActivities(final List<EstimateTemplateActivity> nonSorActivities) {
        this.nonSorActivities = nonSorActivities;
    }

    public PersistenceService<EstimateTemplate, Long> getEstimateTemplateService() {
        return estimateTemplateService;
    }

    public void setEstimateTemplateService(final PersistenceService<EstimateTemplate, Long> estimateTemplateService) {
        this.estimateTemplateService = estimateTemplateService;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public SearchQuery prepareQuery(final String sortField, final String sortOrder) {
        String dynQuery = " from EstimateTemplate et where et.id is not null ";
        final List<Object> paramList = new ArrayList<Object>();
        dynQuery = dynQuery + " and et.status = ?";
        paramList.add(estimateTemplate.getStatus());
        if (estimateTemplate.getWorkType() != null && estimateTemplate.getWorkType().getId() != -1) {
            dynQuery = dynQuery + " and et.workType.id = ? ";
            paramList.add(estimateTemplate.getWorkType().getId());
        }
        if (estimateTemplate.getSubType() != null && estimateTemplate.getSubType().getId() != -1) {
            dynQuery = dynQuery + " and et.subType.id = ? ";
            paramList.add(estimateTemplate.getSubType().getId());
        }
        if (StringUtils.isNotBlank(estimateTemplate.getCode().trim()))
            dynQuery = dynQuery + " and UPPER(et.code) like '%" + estimateTemplate.getCode().trim().toUpperCase()
            + "%'";
        if (StringUtils.isNotBlank(estimateTemplate.getName().trim()))
            dynQuery = dynQuery + " and UPPER(et.name) like '%" + estimateTemplate.getName().trim().toUpperCase()
            + "%'";
        if (StringUtils.isNotBlank(estimateTemplate.getDescription().trim()))
            dynQuery = dynQuery + " and UPPER(et.description) like '%"
                    + estimateTemplate.getDescription().trim().toUpperCase() + "%'";
        final String countQuery = "select distinct count(et) " + dynQuery;
        return new SearchQueryHQL(dynQuery, countQuery, paramList);
    }

    public String getSourcePage() {
        return sourcePage;
    }

    public void setSourcePage(final String sourcePage) {
        this.sourcePage = sourcePage;
    }

    public Long getTypeOfWork() {
        return typeOfWork;
    }

    public void setTypeOfWork(final Long typeOfWork) {
        this.typeOfWork = typeOfWork;
    }

    public String getEstimateTemplateCode() {
        return estimateTemplateCode;
    }

    public void setEstimateTemplateCode(final String estimateTemplateCode) {
        this.estimateTemplateCode = estimateTemplateCode;
    }

    public Long getSubTypeOfWork() {
        return subTypeOfWork;
    }

    public void setSubTypeOfWork(final Long subTypeOfWork) {
        this.subTypeOfWork = subTypeOfWork;
    }

    public AbstractEstimateService getAbstractEstimateService() {
        return abstractEstimateService;
    }

    public void setAbstractEstimateService(final AbstractEstimateService abstractEstimateService) {
        this.abstractEstimateService = abstractEstimateService;
    }

}