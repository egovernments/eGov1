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

package org.egov.collection.web.actions.reports;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.reporting.engine.ReportConstants.FileFormat;
import org.egov.infra.reporting.engine.ReportRequest.ReportDataSourceType;
import org.egov.infra.web.struts.actions.ReportFormAction;
import org.springframework.transaction.annotation.Transactional;
@Transactional(readOnly=true)
public class BankRemittanceReportAction extends ReportFormAction {

	private static final long serialVersionUID = 1L;
	
	private static final String EGOV_DEPT_ID = "EGOV_DEPT_ID";
	private static final String BANK_REMITTANCE_REPORT_TEMPLATE = "bank_remittance";
	
	private CollectionsUtil collectionsUtil;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.egov.infra.web.struts.actions.BaseFormAction#prepare()
	 */
	public void prepare() {
		setReportFormat(FileFormat.PDF);
		setDataSourceType(ReportDataSourceType.HQL);
	}
	
	/**
	 * @param collectionsUtil the collections utility object to set
	 */
	public void setCollectionsUtil(CollectionsUtil collectionsUtil) {
		this.collectionsUtil = collectionsUtil;
	}
	
	/**
	 * @return the department id
	 */
	public Integer getDeptId() {
		return (Integer) getReportParam(EGOV_DEPT_ID);
	}

	/**
	 * @param deptId
	 *            the department id to set
	 */
	public void setDeptId(Integer deptId) {
		setReportParam(EGOV_DEPT_ID, deptId);
	}
	
	
	@Override
	@Action(value="/reports/bankRemittanceReport-criteria",results = { @Result(name = INDEX,type="redirect")})
	public String criteria() {
		// Setup drop down data for department list
		addRelatedEntity("department", Department.class, "deptName");
		setupDropdownDataExcluding();
		
		// Set default values of criteria fields
		Department dept = collectionsUtil.getDepartmentOfLoggedInUser();
		if(dept != null) {
			setReportParam(EGOV_DEPT_ID, dept.getId());
		}
		
		return INDEX;
	}

	@Override
	protected String getReportTemplateName() {
		return BANK_REMITTANCE_REPORT_TEMPLATE;
	}
}
