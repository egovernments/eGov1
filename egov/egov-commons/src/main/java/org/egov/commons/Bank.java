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
package org.egov.commons;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Bank implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String code;

	private String name;

	private String narration;

	private int isactive;

	private Date lastmodified;

	private Date created;

	private BigDecimal modifiedby;

	private String type;

	private Set<Bankbranch> bankbranchs = new HashSet<Bankbranch>(0);

	public Bank() {
		//For hibernate to work
	}

	public Bank(String code, String name, int isactive, Date lastmodified, Date created, BigDecimal modifiedby) {
		this.code = code;
		this.name = name;
		this.isactive = isactive;
		this.lastmodified = lastmodified;
		this.created = created;
		this.modifiedby = modifiedby;
	}

	public Bank(String code, String name, String narration, int isactive, Date lastmodified, Date created, BigDecimal modifiedby, String type, Set<Bankbranch> bankbranchs) {
		this.code = code;
		this.name = name;
		this.narration = narration;
		this.isactive = isactive;
		this.lastmodified = lastmodified;
		this.created = created;
		this.modifiedby = modifiedby;
		this.type = type;
		this.bankbranchs = bankbranchs;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNarration() {
		return this.narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public Date getLastmodified() {
		return this.lastmodified;
	}

	public void setLastmodified(Date lastmodified) {
		this.lastmodified = lastmodified;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public BigDecimal getModifiedby() {
		return this.modifiedby;
	}

	public void setModifiedby(BigDecimal modifiedby) {
		this.modifiedby = modifiedby;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Set<Bankbranch> getBankbranchs() {
		return this.bankbranchs;
	}

	public void setBankbranchs(Set<Bankbranch> bankbranchs) {
		this.bankbranchs = bankbranchs;
	}

    public int getIsactive() {
        return isactive;
    }

    public void setIsactive(int isactive) {
        this.isactive = isactive;
    }

}
