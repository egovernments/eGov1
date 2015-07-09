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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.egov.commons.CFinancialYear;
import org.egov.commons.service.CommonsService;
import org.egov.dao.budget.BudgetDetailsDAO;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.ValidationException;
import org.egov.pims.service.EmployeeServiceOld;
import org.egov.works.models.estimate.AbstractEstimate;
import org.egov.works.services.AbstractEstimateService;
import org.egov.works.services.WorksService;
import org.springframework.beans.factory.annotation.Autowired;

@Result(name=BaseFormAction.SUCCESS,type="StreamResult.class",location="estimatePDF", params={"inputName","estimatePDF","contentType","application/pdf","contentDisposition","no-cache;filename=AbstractEstimatePDF.pdf"})
@ParentPackage("egov")
public class AbstractEstimatePDFAction extends BaseFormAction{
	private static final Logger logger = Logger.getLogger(AbstractEstimatePDFAction.class);
	private Long estimateID;
	private InputStream estimatePDF;
	private AbstractEstimateService abstractEstimateService;
	@Autowired
        private EmployeeServiceOld employeeService;
	@Autowired
        private CommonsService commonsService;
	private WorksService worksService;
	private BudgetDetailsDAO budgetDetailsDAO;
	
	public Object getModel() {
		return null;
	}	
	
	public String execute(){
		if(estimateID!=null){
			AbstractEstimate estimate = getAbstractEstimate();
				Boundary b = getTopLevelBoundary(estimate.getWard());
				CFinancialYear financialYear = getCurrentFinancialYear();
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024*100);
				EstimatePDFGenerator pdfGenerator =new EstimatePDFGenerator(estimate,b==null?"":b.getName(),financialYear,out);
				pdfGenerator.setPersistenceService(getPersistenceService());
				pdfGenerator.setEmployeeService(employeeService);
				pdfGenerator.setBudgetDetailsDAO(abstractEstimateService.getBudgetDetailsDAO());
				pdfGenerator.setAbstractEstimateService(abstractEstimateService);
				pdfGenerator.setWorksService(worksService);
				try {
					pdfGenerator.generatePDF();
				} catch (ValidationException e) {
					// TODO Auto-generated catch block
					logger.debug("exception "+e);
				}
				
				estimatePDF=new ByteArrayInputStream(out.toByteArray());
		}
		return SUCCESS;
	}
		
	private AbstractEstimate getAbstractEstimate() {
		return (AbstractEstimate) getPersistenceService().find("from AbstractEstimate e where e.id=?", estimateID);
	}

	protected Boundary getTopLevelBoundary(Boundary boundary) {
		Boundary b = boundary;
		while(b!=null && b.getParent()!=null){
			b=b.getParent();
		}
		return b;
	}

	protected CFinancialYear getCurrentFinancialYear() {
		/**
		 * for the year end process getCurrentFinancialYear API should return the next CFinancialYear object
		 */
		return commonsService.getFinancialYearByFinYearRange(worksService.getWorksConfigValue("FINANCIAL_YEAR_RANGE"));
		//return (CFinancialYear) getPersistenceService().find("from CFinancialYear cfinancialyear where ? between cfinancialyear.startingDate and cfinancialyear.endingDate",new java.util.Date());
	}
	
	public void setEstimateID(Long estimateID) {
		this.estimateID = estimateID;
	}
	
	public InputStream getEstimatePDF() {
		return estimatePDF;
	}
	

	public AbstractEstimateService getAbstractEstimateService() {
		return abstractEstimateService;
	}

	public void setAbstractEstimateService(
			AbstractEstimateService abstractEstimateService) {
		this.abstractEstimateService = abstractEstimateService;
	}

	public EmployeeServiceOld getemployeeService() {
		return employeeService;
	}

	public void setEmployeeService(EmployeeServiceOld employeeService) {
		this.employeeService = employeeService;
	}

	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}

	public void setWorksService(WorksService worksService) {
		this.worksService = worksService;
	}

	public void setBudgetDetailsDAO(BudgetDetailsDAO budgetDetailsDAO) {
		this.budgetDetailsDAO = budgetDetailsDAO;
	}

}
