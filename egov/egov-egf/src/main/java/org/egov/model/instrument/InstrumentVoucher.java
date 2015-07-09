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
package org.egov.model.instrument;

import org.egov.commons.CVoucherHeader;
import org.egov.infstr.models.BaseModel;

/**
 * 
 * @author Mani
 * 
 */

public class InstrumentVoucher extends BaseModel {

	InstrumentHeader instrumentHeaderId;
	CVoucherHeader voucherHeaderId;

	public InstrumentHeader getInstrumentHeaderId() {
		return instrumentHeaderId;
	}

	public void setInstrumentHeaderId(InstrumentHeader instrumentHeaderId) {
		this.instrumentHeaderId = instrumentHeaderId;
	}

	public CVoucherHeader getVoucherHeaderId() {
		return voucherHeaderId;
	}

	public void setVoucherHeaderId(CVoucherHeader voucherHeaderId) {
		this.voucherHeaderId = voucherHeaderId;
	}

	public String toString() {
		StringBuffer ivBuffer = new StringBuffer();
		ivBuffer.append("[id=" + id).append(
				"instrumentHeader=" + instrumentHeaderId).append(
				"voucherHeader=" + voucherHeaderId).append("]");
		return ivBuffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((instrumentHeaderId == null) ? 0 : instrumentHeaderId
						.hashCode());
		result = prime * result
				+ ((voucherHeaderId == null) ? 0 : voucherHeaderId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstrumentVoucher other = (InstrumentVoucher) obj;
		if (instrumentHeaderId == null) {
			if (other.instrumentHeaderId != null)
				return false;
		} else if (!instrumentHeaderId.equals(other.instrumentHeaderId))
			return false;
		if (voucherHeaderId == null) {
			if (other.voucherHeaderId != null)
				return false;
		} else if (!voucherHeaderId.equals(other.voucherHeaderId))
			return false;
		return true;
	}

}
