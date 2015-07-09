/*******************************************************************************
 * eGov suite of products aim to improve the internal efficiency,transparency, 
 *    accountability and the service delivery of the government  organizations.
 * 
 *     Copyright (C) <2015>  eGovernments Foundation
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
 * 	1) All versions of this program, verbatim or modified must carry this 
 * 	   Legal Notice.
 * 
 * 	2) Any misrepresentation of the origin of the material is prohibited. It 
 * 	   is required that all modified versions of this material be marked in 
 * 	   reasonable ways as different from the original version.
 * 
 * 	3) This license does not grant any rights to any user of the program 
 * 	   with regards to rights under trademark law for use of the trade names 
 * 	   or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org
 ******************************************************************************/
package org.egov.ptis.domain.entity.objection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.egov.commons.EgwStatus;
import org.egov.infra.persistence.entity.Auditable;
import org.egov.infra.persistence.validator.annotation.Required;
import org.egov.infra.persistence.validator.annotation.ValidateDate;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.hibernate.validator.constraints.Length;
/**
 * @author manoranjan
 * 
 */
//@CompareDates(fromDate = "dateOfOutcome", toDate = "recievedOn", dateFormat = "dd/MM/yyyy", message = "dateOfOutcome.greaterThan.recievedOn")
public class Objection extends StateAware implements Auditable{

	/**
	 * Default serial version Id
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private EgwStatus egwStatus;

	private BasicProperty basicProperty;

	@Length(max = 50, message = "objection.objectionNumber.length")
	private String objectionNumber;

	@ValidateDate(allowPast = true, dateFormat = "dd/MM/yyyy", message = "objection.receivedOn.futuredate")
	@org.egov.infra.persistence.validator.annotation.DateFormat(message = "invalid.fieldvalue.receivedOn")
	private Date recievedOn;

	@Length(max = 256, message = "objection.objectionNumber.length")
	private String recievedBy;

	private String details;

	private String docNumberObjection;

	private String docNumberOutcome;
	private PropertyImpl referenceProperty;

	@Valid
	private List<Hearing> hearings = new LinkedList<Hearing>();

	@Valid
	private List<Inspection> inspections = new LinkedList<Inspection>();

	@ValidateDate(allowPast = true, dateFormat = "dd/MM/yyyy", message = "objection.outcomedate.futuredate")
	@org.egov.infra.persistence.validator.annotation.DateFormat(message = "invalid.fieldvalue.outcomedate")
	private Date dateOfOutcome;

	private String remarks;// for dateOfOutcome

	private Boolean objectionRejected;
	public static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

	@Override
	public String getStateDetails() {
		return getBasicProperty().getUpicNo();
	}

	public EgwStatus getEgwStatus() {
		return egwStatus;
	}

	public String getObjectionNumber() {
		return objectionNumber;
	}

	@Required(message = "objection.receiviedOn.null")
	public Date getRecievedOn() {
		return recievedOn;
	}

	@Required(message = "objection.receiviedBy.null")
	@Length(max = 256, message = "objection.receivedBy.length")
	public String getRecievedBy() {
		return recievedBy;
	}

	@Required(message = "objection.details.null")
	@Length(max = 1024, message = "objection.details.length")
	public String getDetails() {
		return details;
	}

	public void setEgwStatus(EgwStatus egwStatus) {
		this.egwStatus = egwStatus;
	}

	public void setObjectionNumber(String objectionNumber) {
		this.objectionNumber = objectionNumber;
	}

	public void setRecievedOn(Date recievedOn) {
		this.recievedOn = recievedOn;
	}

	public void setRecievedBy(String recievedBy) {
		this.recievedBy = recievedBy;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public List<Hearing> getHearings() {
		return hearings;
	}

	public void setHearings(List<Hearing> hearings) {
		this.hearings = hearings;
	}

	public List<Inspection> getInspections() {
		return inspections;
	}

	public Date getDateOfOutcome() {
		return dateOfOutcome;
	}

	public String getRemarks() {
		return remarks;
	}

	public Boolean getObjectionRejected() {
		return objectionRejected;
	}

	public void setInspections(List<Inspection> inspections) {
		this.inspections = inspections;
	}

	public void setDateOfOutcome(Date dateOfOutcome) {
		this.dateOfOutcome = dateOfOutcome;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setObjectionRejected(Boolean objectionRejected) {
		this.objectionRejected = objectionRejected;
	}

	public BasicProperty getBasicProperty() {
		return basicProperty;
	}

	public void setBasicProperty(BasicProperty basicProperty) {
		this.basicProperty = basicProperty;
	}

	public String getDocNumberObjection() {
		return docNumberObjection;
	}

	public String getDocNumberOutcome() {
		return docNumberOutcome;
	}

	public void setDocNumberObjection(String docNumberObjection) {
		this.docNumberObjection = docNumberObjection;
	}

	public void setDocNumberOutcome(String docNumberOutcome) {
		this.docNumberOutcome = docNumberOutcome;
	}

	public String getFmtdReceivedOn() {
		if (recievedOn != null)
			return dateFormat.format(recievedOn);
		else
			return "";
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		
		sb.append("UcipNo :").append(null!=basicProperty?basicProperty.getUpicNo():" ");
		sb.append("status :").append(null!= egwStatus?egwStatus.getDescription():" ");
		sb.append("objectionNumber :").append(null!= objectionNumber?objectionNumber:" ");
		
		return sb.toString();
	}

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
       this.id= id;
    }

    public PropertyImpl getReferenceProperty() {
        return referenceProperty;
    }

    public void setReferenceProperty(PropertyImpl referenceProperty) {
        this.referenceProperty = referenceProperty;
    }

}
