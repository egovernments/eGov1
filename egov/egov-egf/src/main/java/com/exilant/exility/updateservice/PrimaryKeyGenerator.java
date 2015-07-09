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
package com.exilant.exility.updateservice;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.infstr.utils.HibernateUtil;
import org.hibernate.Query;



public class PrimaryKeyGenerator
{
	private static final Logger LOGGER = Logger.getLogger(PrimaryKeyGenerator.class);
	private static PrimaryKeyGenerator singletonInstance;

	//private static HashMap nextKeys = new HashMap();
	static
	{
		singletonInstance = new PrimaryKeyGenerator();
	}

	public static PrimaryKeyGenerator getKeyGenerator(){
		//if(singletonInstance == null)singletonInstance = new PrimaryKeyGenerator();
		return singletonInstance;
	}

	private PrimaryKeyGenerator()
	{
		super();
	}

	public static long getNextKey(String tableName)
	{
		long key = 0;
		String sql = "select nextval('seq_" + tableName + "')";
		try
		{
			Query pst = HibernateUtil.getCurrentSession().createSQLQuery(sql);
			List<BigInteger> rs = pst.list();
			key =rs!=null?rs.get(0).longValue():0l;
			if(rs == null || rs.size() == 0) 
			throw new Exception();
		}
		catch(Exception e)
		{
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Exp="+e.getMessage());
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Error getting value from sequence "+e.toString());
		}
		
		if(LOGGER.isDebugEnabled())     LOGGER.debug("PK for "+tableName+" is "+key);
		return key;
	}



}
