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
package org.egov.eis.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.egov.commons.Bank;
import org.egov.commons.Bankbranch;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.pims.commons.Position;
import org.hibernate.search.annotations.DocumentId;

@Entity
@Table(name = "eg_drawingofficer")
@SequenceGenerator(name = DrawingOfficer.SEQ_DRAWINGOFFICER, sequenceName = DrawingOfficer.SEQ_DRAWINGOFFICER, allocationSize = 1)
public class DrawingOfficer extends AbstractAuditable {
    private static final long serialVersionUID = 1678672850806848215L;
    public static final String SEQ_DRAWINGOFFICER = "SEQ_EG_DRAWINGOFFICER";

    @DocumentId
    @Id
    @GeneratedValue(generator = SEQ_DRAWINGOFFICER, strategy = GenerationType.SEQUENCE)
    private Long id;

    private String code;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank")
    private Bank bank;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bankbranch")
    private Bankbranch bankBranch;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position")
    private Position position;
    String accountNumber;
    String tan;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(final Bank bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTan() {
        return tan;
    }

    public void setTan(final String tan) {
        this.tan = tan;
    }

    public Bankbranch getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(final Bankbranch bankBranch) {
        this.bankBranch = bankBranch;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(final Position position) {
        this.position = position;
    }

}
