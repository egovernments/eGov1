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
package org.egov.works.web.actions.tender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.models.Money;
import org.egov.works.models.estimate.AbstractEstimate;
import org.egov.works.models.tender.TenderResponse;
import org.egov.works.models.tender.WorksPackage;
import org.egov.works.services.AbstractEstimateService;
import org.egov.works.services.WorksPackageService;

public class AjaxWorksPackageAction extends BaseFormAction {
    
    private static final long serialVersionUID = -5753205367102548473L;
    private static final String ESTIMATE_LIST = "estList";
    private List<AbstractEstimate> abstractEstimateList = new ArrayList<AbstractEstimate>();
    private AbstractEstimateService abstractEstimateService;
    private Money worktotalValue;
    private String estId;
    private String wpId;

    private WorksPackage worksPackage = new WorksPackage();

    private static final String TENDERFILENUMBERUNIQUECHECK = "tenderFileNumberUniqueCheck";
    private Long id;
    private String tenderFileNumber;
    private WorksPackageService workspackageService;
    private static final String TENDER_RESPONSE_CHECK = "tenderResponseCheck";
    private boolean tenderResponseCheck;
    private String tenderNegotiationNo;
    private String query = "";
    private List<WorksPackage> wpList = new LinkedList<WorksPackage>();
    private static final String WP_NUMBER_SEARCH_RESULTS = "wpNoSearchResults";
    private static final String TENDER_FILE_NUMBER_SEARCH_RESULTS = "tenderFileNoSearchResults";
    private String mode;
    private List<String> estimateNumberSearchList = new LinkedList<String>();
    private static final String ESTIMATE_NUMBER_SEARCH_RESULTS = "estimateNoSearchResults";

    public String estimateList() {
        if (StringUtils.isNotBlank(estId)) {
            abstractEstimateList = abstractEstimateService.getAbEstimateListById(estId);
            setWorktotalValue(abstractEstimateService.getWorkValueIncludingTaxesForEstList(abstractEstimateList));
        }
        return ESTIMATE_LIST;
    }

    public String tenderFileNumberUniqueCheck() {
        return TENDERFILENUMBERUNIQUECHECK;
    }

    public boolean getTenderFileNumberCheck() {
        boolean tenderFileNoexistsOrNot = false;
        Long wpId = null;
        if (id == null || id == 0) {
            if (getPersistenceService().findByNamedQuery("TenderFileNumberUniqueCheck", tenderFileNumber) != null)
                wpId = (Long) getPersistenceService().findByNamedQuery("TenderFileNumberUniqueCheck", tenderFileNumber);
        } else if (getPersistenceService().findByNamedQuery("TenderFileNumberUniqueCheckForEdit", tenderFileNumber, id) != null)
            wpId = (Long) getPersistenceService().findByNamedQuery("TenderFileNumberUniqueCheckForEdit",
                    tenderFileNumber, id);

        if (wpId != null)
            worksPackage = workspackageService.findById(wpId, false);

        if (worksPackage != null && worksPackage.getId() != null)
            tenderFileNoexistsOrNot = true;

        return tenderFileNoexistsOrNot;
    }

    public String isTRPresentForWPCheck() {
        tenderResponseCheck = false;
        tenderNegotiationNo = "";
        if (!StringUtils.isEmpty(wpId)) {
            final String query = "from TenderResponse tr where tr.tenderEstimate.worksPackage.id=? and tr.egwStatus.code!='CANCELLED'";
            final List<Object> paramList = new ArrayList<Object>();
            paramList.add(Long.valueOf(wpId));
            final List<TenderResponse> trList = getPersistenceService().findAllBy(query, paramList.toArray());
            if (trList != null && trList.size() > 0) {
                tenderResponseCheck = true;
                tenderNegotiationNo = trList.get(0).getNegotiationNumber();
            }
        }
        return TENDER_RESPONSE_CHECK;
    }

    public String searchWorksPackageNumber() {
        String strquery = "";
        final ArrayList<Object> params = new ArrayList<Object>();
        if (!StringUtils.isEmpty(query)) {
            if (mode != null && mode.equalsIgnoreCase("cancelWP")) {
                strquery = "from WorksPackage as wp where wp.wpNumber like '%'||?||'%' and wp.egwStatus.code=? ";
                params.add(query.toUpperCase());
                params.add("APPROVED");
            } else {
                strquery = "from WorksPackage as wp where wp.wpNumber like '%'||?||'%' and wp.egwStatus.code<>? ";
                params.add(query.toUpperCase());
                params.add("NEW");
            }

            wpList = getPersistenceService().findAllBy(strquery, params.toArray());
        }
        return WP_NUMBER_SEARCH_RESULTS;
    }

    public String searchTenderFileNumber() {
        String strquery = "";
        final ArrayList<Object> params = new ArrayList<Object>();
        if (!StringUtils.isEmpty(query)) {
            strquery = "from WorksPackage as wp where wp.tenderFileNumber like '%'||?||'%' and wp.egwStatus.code<>?";
            params.add(query.toUpperCase());
            params.add("NEW");
            wpList = getPersistenceService().findAllBy(strquery, params.toArray());
        }
        return TENDER_FILE_NUMBER_SEARCH_RESULTS;
    }

    public String searchEstimateNumber() {
        String strquery = "";
        final ArrayList<Object> params = new ArrayList<Object>();
        if (!StringUtils.isEmpty(query)) {
            strquery = "select distinct(wpd.estimate.estimateNumber) from WorksPackageDetails wpd where wpd.worksPackage.egwStatus.code<>? and wpd.estimate.estimateNumber like '%'||?||'%' ";
            params.add("NEW");
            params.add(query.toUpperCase());
            estimateNumberSearchList = getPersistenceService().findAllBy(strquery, params.toArray());
        }
        return ESTIMATE_NUMBER_SEARCH_RESULTS;
    }

    public Money getWorktotalValue() {
        return worktotalValue;
    }

    public void setWorktotalValue(final Money worktotalValue) {
        this.worktotalValue = worktotalValue;
    }

    public String getEstId() {
        return estId;
    }

    public void setEstId(final String estId) {
        this.estId = estId;
    }

    @Override
    public Object getModel() {
        return null;
    }

    public void setAbstractEstimateService(final AbstractEstimateService abstractEstimateService) {
        this.abstractEstimateService = abstractEstimateService;
    }

    public List<AbstractEstimate> getAbstractEstimateList() {
        return abstractEstimateList;
    }

    public void setAbstractEstimateList(final List<AbstractEstimate> abstractEstimateList) {
        this.abstractEstimateList = abstractEstimateList;
    }

    public WorksPackage getWorksPackage() {
        return worksPackage;
    }

    public void setWorksPackage(final WorksPackage worksPackage) {
        this.worksPackage = worksPackage;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTenderFileNumber() {
        return tenderFileNumber;
    }

    public void setTenderFileNumber(final String tenderFileNumber) {
        this.tenderFileNumber = tenderFileNumber;
    }

    public void setWorkspackageService(final WorksPackageService workspackageService) {
        this.workspackageService = workspackageService;
    }

    public void setWpId(final String wpId) {
        this.wpId = wpId;
    }

    public boolean getTenderResponseCheck() {
        return tenderResponseCheck;
    }

    public String getTenderNegotiationNo() {
        return tenderNegotiationNo;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public List<WorksPackage> getWpList() {
        return wpList;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public List<String> getEstimateNumberSearchList() {
        return estimateNumberSearchList;
    }

}
