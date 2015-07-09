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
package org.egov.works.models.masters;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.commons.ContractorGrade;
import org.egov.commons.EgwStatus;
import org.egov.commons.Period;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.persistence.validator.annotation.OptionalPattern;
import org.egov.infstr.ValidationError;
import org.egov.infstr.models.BaseModel;
import org.egov.works.utils.WorksConstants;
import org.hibernate.validator.constraints.Length;


public class ContractorDetail extends BaseModel{
		
	private Contractor contractor;
	
	private Department department;
	
	@Length(max=50,message="contractorDetail.registrationNumber.length")
	@OptionalPattern(regex=WorksConstants.alphaNumericwithspecialchar,message="contractorDetail.registrationNumber.alphaNumeric")
	private String registrationNumber;
	
	private EgwStatus status;
	
	private ContractorGrade grade;
	
	@Valid
	private Period validity;

	public Contractor getContractor() {
		return contractor;
	}

	public void setContractor(Contractor contractor) {
		this.contractor = contractor;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
	}

	public ContractorGrade getGrade() {
		return grade;
	}

	public void setGrade(ContractorGrade grade) {
		this.grade = grade;
	}

	public Period getValidity() {
		return validity;
	}

	public void setValidity(Period validity) {
		this.validity = validity;
	}
	
	public List<ValidationError> validate() {
		List<ValidationError> validationErrors = new ArrayList<ValidationError>(); 
		 if(department==null || department.getId() == null){
			 validationErrors.add(new ValidationError("department","contractorDetails.department.required"));
		 }
		 if(status==null || status.getId() == null){
			 validationErrors.add(new ValidationError("status","contractorDetails.status.required"));
		 }
		if(validity == null || (validity !=null && validity.getStartDate()==null)){
			validationErrors.add(new ValidationError("validity","contractorDetails.startDate_empty"));						
		}
		else if(validity == null || (validity !=null && !compareDates(validity.getStartDate(),validity.getEndDate()))){
			validationErrors.add(new ValidationError("validity","contractorDetails.invalid_date_range"));
		}
		if(validationErrors.isEmpty()){
			return null;
		}
		else {
			return validationErrors;
		}
	}
	public static boolean compareDates(java.util.Date startDate,java.util.Date endDate) {
		if(startDate==null) {
			return false;
		}
		
		if(endDate==null) {
			return true;
		}
		
		if(endDate.before(startDate)) {
			return false;
		}    	
		return true;
	}	
}
