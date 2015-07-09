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
package org.egov.wtms.application.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.validation.regex.Constants;
import org.egov.wtms.masters.entity.MeterCost;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "egwtr_connection")
@SequenceGenerator(name = WaterConnection.SEQ_CONNECTION, sequenceName = WaterConnection.SEQ_CONNECTION, allocationSize = 1)
public class WaterConnection extends AbstractAuditable {

    private static final long serialVersionUID = -361205348191992865L;
    public static final String SEQ_CONNECTION = "SEQ_EGWTR_CONNECTION";

    @Id
    @GeneratedValue(generator = SEQ_CONNECTION, strategy = GenerationType.SEQUENCE)
    private Long id;

    @SafeHtml
    // @Column(name = "consumerCode", unique = true)
    @Length(min = 3, max = 50)
    private String consumerCode;

    @NotNull
    @SafeHtml
    @Length(min = 3, max = 50)
    private String propertyIdentifier;

    @SafeHtml
    @Length(min = 3, max = 50)
    private String bpaIdentifier;

    @Pattern(regexp = Constants.NUMERIC)
    @SafeHtml
    @Length(min = 10, max = 12)
    private String mobileNumber;

    @Length(max = 100)
    @SafeHtml
    @Email(regexp = Constants.EMAIL)
    private String email;

    @SafeHtml
    @Length(min = 3, max = 50)
    private String meterNumber;

    @ManyToOne
    @JoinColumn(name = "parentConnection")
    private WaterConnection parentConnection;

    @ManyToOne
    @JoinColumn(name = "meter")
    private MeterCost meter;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getConsumerCode() {
        return consumerCode;
    }

    public void setConsumerCode(final String consumerCode) {
        this.consumerCode = consumerCode;
    }

    public String getPropertyIdentifier() {
        return propertyIdentifier;
    }

    public void setPropertyIdentifier(final String propertyIdentifier) {
        this.propertyIdentifier = propertyIdentifier;
    }

    public String getBpaIdentifier() {
        return bpaIdentifier;
    }

    public void setBpaIdentifier(final String bpaIdentifier) {
        this.bpaIdentifier = bpaIdentifier;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(final String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public WaterConnection getParentConnection() {
        return parentConnection;
    }

    public void setParentConnection(final WaterConnection parentConnection) {
        this.parentConnection = parentConnection;
    }

    public MeterCost getMeter() {
        return meter;
    }

    public void setMeter(final MeterCost meter) {
        this.meter = meter;
    }

}