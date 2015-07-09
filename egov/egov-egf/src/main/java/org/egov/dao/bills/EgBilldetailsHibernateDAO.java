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
/*
 * Created on Oct 21, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.egov.dao.bills;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.exceptions.EGOVException;
import org.egov.infstr.dao.GenericHibernateDAO;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.model.bills.EgBilldetails;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
@Transactional(readOnly=true)
public class EgBilldetailsHibernateDAO extends GenericHibernateDAO implements
EgBilldetailsDAO
{
	private Session session;
	private final Logger logger = Logger.getLogger(getClass().getName());
	public EgBilldetailsHibernateDAO(Class persistentClass, Session session)
	{
		super(persistentClass, session);

	}

	public BigDecimal getOtherBillsAmount(Long minGlCodeId, Long maxGlCodeId,
			Long majGlCodeId, String finYearID, String functionId,
			String schemeId, String subSchemeId, String asOnDate, String billType)
			throws Exception {
		if(logger.isDebugEnabled())     logger.debug("------- Inside getOtherBillsAmount() -----------");
		Query qry=null;
	    StringBuffer qryStr = new StringBuffer();
	    BigDecimal result=new BigDecimal("0.00");
		try{
			String dateCond="";
			String funcStr="";
			String schStr="";
			String glcodeStr="";


			qryStr.append("select sum(bd.debitamount) from EgBilldetails bd, EgBillregister br, EgBillregistermis brm where br.id=bd.egBillregister.id and br.id=brm.egBillregister.id and bd.egBillregister.id=brm.egBillregister.id and brm.financialyear.id =:finYearID and br.expendituretype not in ( :billType)  and br.status.id not in (SELECT es.id FROM EgwStatus es  WHERE  UPPER(es.description) LIKE '%CANCELLED%') ");

			if(!(asOnDate==null || "".equals(asOnDate)))
				dateCond=" and br.billdate <=:asOnDate";

			if(!(functionId==null || "".equals(functionId)   ))
			    funcStr=" and bd.functionid =:functionId";

			if((!(schemeId==null || "".equals(schemeId)  )) && (subSchemeId==null || "".equals(subSchemeId) ))
				schStr="  and brm.scheme =:schemeId";

			if((!(schemeId==null || "".equals(schemeId))) && (!(subSchemeId==null || "".equals(subSchemeId))))
				schStr="  and brm.scheme =:schemeId and brm.subScheme =:subSchemeId";

			if(minGlCodeId!=0 && maxGlCodeId!=0)
				glcodeStr=" and bd.glcodeid between :minGlCodeId and :maxGlCodeId";
			else if(maxGlCodeId!=0)
				glcodeStr=" and bd.glcodeid =:maxGlCodeId";
			else if(majGlCodeId!=0)
				glcodeStr=" and bd.glcodeid =:majGlCodeId";

			qryStr.append(dateCond);
			qryStr.append(funcStr);
			qryStr.append(schStr);
			qryStr.append(glcodeStr);
			qry =HibernateUtil.getCurrentSession().createQuery(qryStr.toString());
			if(!(functionId==""  || functionId==null))
				 qry.setString("functionId", functionId);
			if((!(schemeId=="" || schemeId==null)) && (subSchemeId=="" || subSchemeId==null))
				 qry.setString("schemeId", schemeId);
			if((!(schemeId=="" || schemeId==null)) && (!(subSchemeId=="" || subSchemeId==null)))
			 {
				 qry.setString("schemeId", schemeId);
				 qry.setString("subSchemeId", subSchemeId);
			 }
			if(!(asOnDate=="" || asOnDate==null))
				 qry.setString("asOnDate",asOnDate);
			if(minGlCodeId!=0 && maxGlCodeId!=0)
			 {
				 qry.setLong("minGlCodeId",minGlCodeId);
				 qry.setLong("maxGlCodeId",maxGlCodeId);
			 }
			else if(maxGlCodeId!=0)
				 qry.setLong("maxGlCodeId",maxGlCodeId);
			else if(majGlCodeId!=0)
				qry.setLong("majGlCodeId",majGlCodeId);
			qry.setString("finYearID", finYearID);
			qry.setString("billType", billType);

			if(logger.isInfoEnabled())     logger.info("qry---------> "+qry);

			if(qry.uniqueResult()!=null)
			{
				return  new BigDecimal(qry.uniqueResult().toString());
			}
			else
			{
				return result;
			}
		}catch(Exception e)
	    {
	       	logger.error(e.getCause()+" Error in getOtherBillsAmount");
	       	throw new EGOVException(e.getMessage());
        }
	}

	public EgBilldetails getBillDetails(Long billId, List glcodeIdList)	throws Exception {
		Query qry=null;
	    StringBuffer qryStr = new StringBuffer();
	    EgBilldetails billdetails=null;
 	    try
	    {
	    	qryStr.append("from EgBilldetails bd where bd.creditamount>0 AND bd.glcodeid IN (:glcodeIds) AND billid=:billId ");
	    	qry =HibernateUtil.getCurrentSession().createQuery(qryStr.toString());
	    	qry.setParameterList("glcodeIds", glcodeIdList);
			qry.setLong("billId", billId);
	    	if(logger.isInfoEnabled())     logger.info("qry---------> "+qry);
	    	billdetails = (EgBilldetails) qry.uniqueResult();
	    }catch(Exception e)
	    {
	       	logger.error(e.getCause()+" Error in getBillDetails");
	       	throw new EGOVException(e.getMessage());
        }
		return billdetails;
	}
}
