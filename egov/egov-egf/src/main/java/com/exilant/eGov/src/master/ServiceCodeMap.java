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
package com.exilant.eGov.src.master;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.infstr.utils.HibernateUtil;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.eGov.src.domain.CodeServiceMap;
import com.exilant.exility.common.AbstractTask;
import com.exilant.exility.common.DataCollection;
import com.exilant.exility.common.TaskFailedException;
@Transactional(readOnly=true)
public class ServiceCodeMap extends AbstractTask {
	org.hibernate.Query pstmt=null; 
	List<Object[]> rset=null;
	private static final Logger LOGGER = Logger.getLogger(ServiceCodeMap.class);
	public void execute(String taskName, String gridName, DataCollection dc,
			Connection conn, boolean errorOnNoData,
			boolean gridHasColumnHeading, String prefix)
			throws TaskFailedException {
		try {
			postInCodeServiceMap(dc, conn);
			dc.addMessage("eGovSuccess", "Mapping");
		} catch (Exception sqlex) {
			LOGGER.error("eGovFailure"+sqlex.getMessage(),sqlex);
			dc.addMessage("eGovFailure", "Mapping failed");
			throw new TaskFailedException();
		}
	}
	@Transactional
	public void postInCodeServiceMap(DataCollection dc, Connection conn)
			throws TaskFailedException {

		final String srvId = dc.getValue("ftService_id");
		String codeList[] = dc.getValueList("ftService_toCode");
		if (codeList == null) {
			codeList = new String[1];
			codeList[0] = dc.getValue("ftService_toCode");
		}
		try{
		String delQuery="delete FROM codeservicemap WHERE serviceid=?";
		pstmt=HibernateUtil.getCurrentSession().createSQLQuery(delQuery);
		pstmt.setString(1,srvId);
		pstmt.executeUpdate();
		CodeServiceMap csm = new CodeServiceMap();
		csm.setServiceId(srvId);
		for (int i = 0; i < codeList.length; i++) {
			csm.setGlCodeId(codeList[i]);
			csm.insert();
		}
		}
		catch(SQLException ex){ 
			LOGGER.error("ERROR"+ex.getMessage(),ex);
		}
		
	}

}
