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
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
package org.egov.egf.masters.model;

import org.apache.log4j.Logger;
import org.egov.commons.EgwStatus;
import org.egov.commons.utils.EntityType;
import org.egov.infstr.models.BaseModel;

public class FundingAgency extends BaseModel implements EntityType {
	private static final long	serialVersionUID	= -5310440748432491445L;
	
	final static Logger LOGGER=Logger.getLogger(LoanGrantReceiptDetail.class);
	private String name;
	private String code;
	private String address;
	private String remarks;
	private boolean isActive;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public String getBankaccount() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.egov.commons.utils.EntityType#getBankname()
	 */
	@Override
	public String getBankname() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.egov.commons.utils.EntityType#getEntityDescription()
	 */
	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return code;
	}
	/* (non-Javadoc)
	 * @see org.egov.commons.utils.EntityType#getEntityId()
	 */
	@Override
	public Integer getEntityId() {
		// TODO Auto-generated method stub
		return Integer.valueOf(this.id.intValue());
	}
	/* (non-Javadoc)
	 * @see org.egov.commons.utils.EntityType#getIfsccode()
	 */
	@Override
	public String getIfsccode() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.egov.commons.utils.EntityType#getModeofpay()
	 */
	@Override
	public String getModeofpay() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.egov.commons.utils.EntityType#getPanno()
	 */
	@Override
	public String getPanno() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.egov.commons.utils.EntityType#getTinno()
	 */
	@Override
	public String getTinno() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	@Override
	public EgwStatus getEgwStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public EgwStatus getEgwStatus() {
		// TODO Auto-generated method stub
		return null;
	}
*/
}
