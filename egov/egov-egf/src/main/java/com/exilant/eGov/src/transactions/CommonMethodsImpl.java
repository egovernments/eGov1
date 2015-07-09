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
 * MCDMethods.java  Created on Mar 28, 2006
 *
 *  Copyright 2005 eGovernments Foundation. All rights reserved.
 * EGOVERNMENTS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.exilant.eGov.src.transactions;

/**
 * This class defines common methods used for integration
 * @author Tilak
 *
 */
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.commons.CFiscalPeriod;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.infstr.utils.seqgen.DatabaseSequence;
import org.egov.infstr.utils.seqgen.DatabaseSequenceFirstTimeException;
import org.egov.utils.VoucherHelper;
import org.hibernate.Query;

import com.exilant.exility.common.TaskFailedException;

public class CommonMethodsImpl implements CommonMethodsI {
	Query pst;
	Connection connection;
	List<Object[]> rset;
	private static  final Logger LOGGER = Logger.getLogger(CommonMethodsImpl.class);

	/**
	 * Get the cash in hand code account for the boundary
	 */
	public String getCashInHand(int BoundaryId,Connection connection)  throws Exception {
	  	String cashinHandCode="";
	  	try{
	  		String query = " SELECT a.glcode FROM CHARTOFACCOUNTS a,EG_BOUNDARY b,eg_boundary_type c "+
	  						" WHERE id=(SELECT cashinhand FROM CODEMAPPING WHERE EG_BOUNDARYID= ? )  and b.ID_BNDRY_TYPE=c.ID_BNDRY_TYPE and b.ID_BNDRY= ?";
	  		pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
	  		pst.setInteger(1, BoundaryId);
	  		pst.setInteger(2, BoundaryId);
			rset=pst.list();
			for(Object[] element : rset){
				cashinHandCode=element[0].toString();
			}
			if(rset == null || rset.size() == 0) throw new Exception();
			if(LOGGER.isInfoEnabled())     LOGGER.info(">>>cashinHandCode "+cashinHandCode);
	  	}
	  	catch(Exception e){
	  		LOGGER.error(" Glcode for cashinhand not found "+e.toString(),e);
	  		throw new Exception(e.toString());
	  	}
	  	return cashinHandCode;
	  }
	/**
	 * Get the Cheque in hand code account for the boundary
	 */
	  public String getChequeInHand(int BoundaryId,Connection connection)  throws Exception {
	  	String chequeinHandCode="";
	  	try{
	  		String query = " SELECT a.glcode FROM CHARTOFACCOUNTS a,EG_BOUNDARY b,eg_boundary_type c "+
			" WHERE id=(SELECT chequeinhand FROM CODEMAPPING WHERE EG_BOUNDARYID= ? )  and b.ID_BNDRY_TYPE=c.ID_BNDRY_TYPE and b.ID_BNDRY= ?";
	   		pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
	   		pst.setInteger(1, BoundaryId);
	   		pst.setInteger(2, BoundaryId);
			rset=pst.list();
			for(Object[] element : rset){
				chequeinHandCode=element[0].toString();
			}
			if(rset == null || rset.size() == 0)  throw new Exception("Chequeinhand Code not Found");
			if(LOGGER.isInfoEnabled())     LOGGER.info(">>>chequeinHandCode "+chequeinHandCode);
	  	}
	  	catch(Exception e){
	  		LOGGER.error(" Glcode for chequeinHandCode not found ",e);
	  		throw new Exception(e.toString());
	  	}

	  	return chequeinHandCode;
	  }

	  /**
	   * Get the property tax code for the year
	   */
	  public String getPTCode(String forYear,Connection connection) throws Exception
	  {
	  	String ptCodeAndName="";
	  	try{
	  		if(!forYear.equalsIgnoreCase(""))
			{
	  			if(LOGGER.isInfoEnabled())     LOGGER.info("  forYear  "+forYear);
	  			String query1 = "select financialyear from financialyear  where ? between startingdate and endingdate";
	  			pst=HibernateUtil.getCurrentSession().createSQLQuery(query1);
	  			pst.setString(1, forYear);
				rset=pst.list();
				String fId="",isOld="";
				for(Object[] element : rset){
					fId=element[0].toString();
				}
				if(rset == null || rset.size() == 0)
				{
					String query2 = "select a.glcode,a.name from chartofaccounts a,egf_tax_account_mapping b where b.glcodeid=a.id and upper(b.financialyear)=upper('old')";
					pst= HibernateUtil.getCurrentSession().createSQLQuery(query2);
					rset=pst.list();
					for(Object[] element : rset){
						ptCodeAndName=element[0].toString();
						ptCodeAndName=ptCodeAndName+"#"+element[1].toString();
						if(LOGGER.isInfoEnabled())     LOGGER.info(">>>ptCodeAndName "+ptCodeAndName);
					}
				}
				if(!fId.equalsIgnoreCase(""))
				{
					String query3 = "select a.isold from egf_tax_account_mapping a,egf_tax_code b,financialyear c where a.taxcodeid=b.id and b.code='PT' and a.financialyear=c.financialyear and c.financialyear= ?";
					pst = HibernateUtil.getCurrentSession().createSQLQuery(query3);
					pst.setString(1, fId);
					rset=pst.list();
					for(Object[] element : rset){
						if(LOGGER.isInfoEnabled())     LOGGER.info("   inside 2    ");
						isOld=element[0].toString();
					}
					if(isOld != null && isOld.equals("1"))
					{
								if(LOGGER.isInfoEnabled())     LOGGER.info("   inside 4    ");
								String query4 = "select a.glcode,a.name from chartofaccounts a,egf_tax_account_mapping b where b.glcodeid=a.id and upper(b.financialyear)=upper('old')";
								pst = HibernateUtil.getCurrentSession().createSQLQuery(query4);
								rset=pst.list();
								for(Object[] element : rset){
									ptCodeAndName=element[0].toString();
									ptCodeAndName=ptCodeAndName+"#"+element[1].toString();
									if(LOGGER.isInfoEnabled())     LOGGER.info(">>>ptCodeAndName** "+ptCodeAndName);
								}
					}
					else
					{
								if(LOGGER.isInfoEnabled())     LOGGER.info("   inside 5   ");
								String query5 = "select a.glcode,a.name from chartofaccounts a,egf_tax_account_mapping b,egf_tax_code c,financialyear d where b.taxcodeid=c.id and c.code='PT' and b.glcodeid=a.id and b.financialyear=d.financialyear and d.financialyear= ?";
								pst = HibernateUtil.getCurrentSession().createSQLQuery(query5);
								pst.setString(1, fId);
								rset=pst.list();
								for(Object[] element : rset){
									ptCodeAndName=element[0].toString();
									ptCodeAndName=ptCodeAndName+"#"+element[1].toString();
									if(LOGGER.isInfoEnabled())     LOGGER.info(">>>ptCodeAndName "+ptCodeAndName);
								}
								if(rset == null || rset.size() == 0)
									throw new Exception("Property Tax code not Found for "+forYear);

					}
				}
			}
			else
			{
				//if foryear is not given, then use Sespense code
				String query = "select a.glcode, a.name from chartofaccounts a,egf_accountcode_purpose b where a.purposeid=b.id and upper(b.name)=upper('SuspenseCode')";
				pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
				rset=pst.list();
				for(Object[] element : rset){
					ptCodeAndName=element[0].toString();
					ptCodeAndName=ptCodeAndName+"#"+element[1].toString();
					if(LOGGER.isInfoEnabled())     LOGGER.info(">>>ptCodeAndName1 "+ptCodeAndName);
				}
				if(rset == null || rset.size() == 0) throw new Exception("Property Tax code not Found for "+forYear);
			}
	  	}
	  	catch(Exception e)
	  	{
	  		LOGGER.error(" PT code not found "+e.toString(),e);
	  		throw new Exception(e.toString());
	  	}
	  	return ptCodeAndName;
	  }

	/**
	 * 	Get the account code for the bank account
	 */
	  public String getBankCode(int bankAccountId,Connection connection) throws Exception
	  {
	  	String bankCodeAndName="";
	  	try{
	  		String query = "select glcode,name from chartofaccounts where id=(select glcodeid from bankaccount where id= ?)";
	  		pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
	  		pst.setInteger(1, bankAccountId);
			rset=pst.list();
			for(Object[] element : rset){
				bankCodeAndName=element[0].toString();
				bankCodeAndName=bankCodeAndName+"#"+element[1].toString();
				if(LOGGER.isInfoEnabled())     LOGGER.info(">>>bankCodeAndName "+bankCodeAndName);
			}
			if(rset == null || rset.size() == 0) throw new Exception("BAnk Code Not Found");
	  	}
	  	catch(Exception e){
	  		LOGGER.error(" Bank code not found "+e.toString(),e);
	  		throw new Exception(e.toString());
	  	}
	  	return bankCodeAndName;
	  }

	  /**
	   * Get the fiscal period id for the date passed
	   */
	  public String getFiscalPeriod(String vDate,Connection connection) throws TaskFailedException,Exception
	  {
	  	String fiscalPeriodID="null";
		try
		{
			String query = "select id from fiscalperiod  where ? between startingdate and endingdate";
			pst =HibernateUtil.getCurrentSession().createSQLQuery(query);
			pst.setString(1, vDate);
			rset=pst.list();
			for(Object[] element : rset){
				fiscalPeriodID=element[0].toString();
				if(LOGGER.isInfoEnabled())     LOGGER.info(">>>fiscalPeriodID "+fiscalPeriodID);
			}
			if(rset == null || rset.size() == 0) 
				throw new TaskFailedException("fiscal Period Not Found");
		}
		catch(TaskFailedException e)
		{
			LOGGER.error("fiscal Period Not Found="+e.getMessage(),e);
			throw new TaskFailedException("fiscal Period Not Found");
		}
		catch(Exception e)
		{
			LOGGER.error("failed to get fiscalperiodId "+e.toString(),e);
			throw new Exception(e.toString());
		}
		return fiscalPeriodID;
	  }

	  /**
	   * Get the bank and bank branch of the bank acocunt
	   */
	  public String getBankId(int bankAccountId,Connection connection) throws Exception
	  {
	  	String bankAndBranchId="null";
		try{
			String sql = "select b.id,c.id from bankaccount a,bankbranch b,bank c where a.branchid=b.id and b.bankid=c.id and a.id= ?";
			pst=HibernateUtil.getCurrentSession().createSQLQuery(sql);
			pst.setInteger(1, bankAccountId);
  	  	rset=pst.list();
  	  for(Object[] element : rset){
  	  			bankAndBranchId=element[0].toString();
  	  			bankAndBranchId=bankAndBranchId+"#"+element[1].toString();
  	  			if(LOGGER.isInfoEnabled())     LOGGER.info(">>>bankAndBranchId "+bankAndBranchId);
  	  		}
  	if(rset == null || rset.size() == 0)  throw new Exception("Bank Code Not Found");
		}
		catch(Exception e){
			LOGGER.error(" Bank Id not found "+e.toString(),e);
			throw new Exception(e.toString());
		}
  		return bankAndBranchId;
	}

	  /**
	   * Get the bank balance for the bank account
	   */
	  public double getAccountBalance(int bankAccountId,String vcDate,Connection connection) throws Exception
	  {
	  	double opeAvailable=0,totalAvailable=0;
	  	try{

		  	String str="SELECT decode(sum(openingDebitBalance),null,0,sum(openingDebitBalance))- decode(sum(openingCreditBalance),null,0,sum(openingCreditBalance)) AS \"openingBalance\" "+
				"FROM transactionSummary WHERE financialYearId=( SELECT id FROM financialYear WHERE startingDate <= ?" +
			   	"AND endingDate >= ?)  AND glCodeId =(select glcodeid from bankaccount where id= ?)";
		  	if(LOGGER.isInfoEnabled())     LOGGER.info(str);
		  	pst = HibernateUtil.getCurrentSession().createSQLQuery(str);
		  	pst.setString(1, vcDate);
		  	pst.setString(2, vcDate);
		  	pst.setInteger(3, bankAccountId);
		  	rset = pst.list();
		    for(Object[] element : rset){
		  	 	opeAvailable = Double.parseDouble(element[0].toString());
		    }
			if(LOGGER.isInfoEnabled())     LOGGER.info("opening balance  "+opeAvailable);

	   		String str1="SELECT (decode(sum(gl.debitAmount),null,0,sum(gl.debitAmount)) - decode(sum(gl.creditAmount),null,0,sum(gl.creditAmount))) + " +opeAvailable+""+
				" as \"totalAmount\" FROM   generalLedger gl, voucherHeader vh WHERE vh.id = gl.voucherHeaderId AND gl.glCodeid = (select glcodeid from bankaccount where id= ?) AND  "+
				" vh.voucherDate >=( SELECT TO_CHAR(startingDate, 'dd-Mon-yyyy') FROM financialYear WHERE startingDate <= ? AND endingDate >= ?) AND vh.voucherDate <= ?";
	   		if(LOGGER.isInfoEnabled())     LOGGER.info(str1);
	   		pst = HibernateUtil.getCurrentSession().createSQLQuery(str1);
	   		pst.setInteger(1, bankAccountId);
	   		pst.setString(2, vcDate);
	   		pst.setString(3, vcDate);
	   		pst.setString(4, vcDate);
	   		rset = pst.list();
	   		for(Object[] element : rset){
		   		 totalAvailable =Double.parseDouble( element[0].toString());
		   		if(LOGGER.isInfoEnabled())     LOGGER.info("total balance  "+totalAvailable);
	 		}

	  	}catch(Exception e){
	  		LOGGER.error(" could not get Bankbalance  "+e.toString(),e);
	  		throw new Exception(e.toString());
		}
		return totalAvailable;
	}

	  /**
	   * Get the acccount code and name for the account with the purposeid
	   */
	public String getCodeName(String purposeId) throws Exception
	{
		String codeAndName="null";
		try{
		String query = "select a.glcode, a.name from chartofaccounts a,egf_accountcode_purpose b where a.purposeid=b.id and b.id= ?";
	  	pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
	  	pst.setString(1, purposeId);
  	  	rset=pst.list();
  	  //	for(int i=0;rset.next();i++){
  	  for(Object[] element : rset){
  	  			codeAndName=element[0].toString();
  	  			codeAndName=codeAndName+"#"+element[1].toString();
  	  		}

		}
		catch(Exception e){
			LOGGER.error(" code not found for purpose id "+e.toString(),e);
			throw new Exception(e.toString());
		}
  		return codeAndName;
	}

	/**
	 * Get the name of the	accountcode passed
	 */
	public String getNameFromCode(String glcode,Connection connection) throws Exception
	{

		String codeName="null";
		try{
				String query="select name from chartofaccounts where glcode= ?";
				if(LOGGER.isInfoEnabled())     LOGGER.info("  query   "+query);
			  	pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
			  	pst.setString(1, glcode);
		  	  	rset=pst.list();
		  	  for(Object[] element : rset){
		  	  		codeName=element[0].toString();
		  	  		if(LOGGER.isInfoEnabled())     LOGGER.info("  codeName   "+codeName);
		  	  	}
		  	if(rset == null || rset.size() == 0) 
		  	  		throw new Exception("code not found");
		}
				catch(Exception e){
					LOGGER.error(" code not found "+e.toString(),e);
					throw new Exception(e.toString());
				}
  		return codeName;
	}

	/**
	 * Get the accountcode of the is paased.
	 */
	public String getGlCode(String glCodeId,Connection connection) throws Exception
	{
		String glCode="null";
		try{
				String query="select glcode from chartofaccounts where id= ?";
				if(LOGGER.isInfoEnabled())     LOGGER.info("  query   "+query);
			  	pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
			  	pst.setString(1, glCodeId);
		  	  	rset=pst.list();
		  	  for(Object[] element : rset){
		  	  		glCode=element[0].toString();
		  	  		if(LOGGER.isInfoEnabled())     LOGGER.info("  glCode   "+glCode);
		  	  	}
		  	  if(rset == null || rset.size() == 0) 
		  	  		throw new Exception("id not found");
		}
				catch(Exception e){
					LOGGER.error(" id not found "+e.toString(),e);
					throw new Exception(e.toString());
				}
  		return glCode;
	}

	/**
	 * This is to check if a record already exist in integrationlog
	 */
	public String checkRecordIdInLog(String recordId,int userId,Connection connection) throws Exception
	{
		String cgn=null;
		try
		{
				String query="SELECT VOUCHERNUMBER FROM integrationlog WHERE RECORDID= ? and USERID= ? order by id desc";
				if(LOGGER.isInfoEnabled())     LOGGER.info("  query   "+query);
			  	pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
			  	pst.setString(1, recordId);
			  	pst.setInteger(2, userId);
		  	  	rset=pst.list();
		  	  for(Object[] element : rset){
		  	  		cgn=element[0].toString();
		  	  		if(LOGGER.isInfoEnabled())     LOGGER.info("  cgn in log  "+cgn);
		  	  	}
		}
  	  	catch(Exception e)
  	  	{
			LOGGER.error("Exp="+e.getMessage(),e);
			throw new Exception(e.toString());
		}
		return cgn;
	}

	/**
	 * Get the division code of the id passed
	 */
	public String getDivisionCode(Integer divid,Connection connection) throws Exception
	{
		 String divCode=null;
		 String sql="SELECT distinct BNDRY_NUM FROM EG_BOUNDARY where ID_BNDRY= ?";
		 if(LOGGER.isDebugEnabled())     LOGGER.debug("Divisio code query-->>>>>>>> "+ sql);
		try{

			pst=HibernateUtil.getCurrentSession().createSQLQuery(sql);
			pst.setInteger(1, divid);
			rset=pst.list();
			for(Object[] element : rset){
				divCode=element[0].toString();
				if(LOGGER.isDebugEnabled())     LOGGER.debug("divCode >>>>>>>"+divCode);
			}

		}
		catch(Exception e)
		{
			LOGGER.error("Exp="+e.getMessage(),e);
			throw new Exception(e.toString());
		}
		return divCode;
	}

	/**
	 * Get the division id for the sub field passed
	 */
	public Integer getDivisionId(Integer fieldId,Connection connection) throws Exception
	{
		Integer divId=null;
		 //String sql="SELECT distinct BNDRY_NUM FROM EG_BOUNDARY where ID_BNDRY='"+divid+"'";
		 String sql="Select PARENT from EG_BOUNDARY where ID_BNDRY='"+fieldId+"'";
		 if(LOGGER.isDebugEnabled())     LOGGER.debug("Division id query-->>>>>>>> "+ sql);
		try{

			pst=HibernateUtil.getCurrentSession().createSQLQuery(sql);
			rset=pst.list();
			for(Object[] element : rset){
				divId=Integer.parseInt(element[0].toString());
			}
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Division id is >>>>>>>"+divId);
		}
		catch(Exception e)
		{
			LOGGER.error("Exp="+e.getMessage());
			throw new Exception(e.toString());
		}
		return divId;
	}

	/**
	 * Get the financial year for the data passed
	 */
	public String getFinacialYear(String vDate,Connection connection) throws Exception
	{
	  String finYear="null";
	  String sql="select FINANCIALYEAR from FINANCIALYEAR  where '"+vDate+"' between startingdate and endingdate";
	  try
	  {
	  	pst=HibernateUtil.getCurrentSession().createSQLQuery(sql);
		rset=pst.list();
		for(Object[] element : rset){
	       finYear=element[0].toString();
	       if(LOGGER.isDebugEnabled())     LOGGER.debug("finYear id>>>>>>>"+finYear);
	    }

	  }
	  catch(Exception e)
	  {
	  	LOGGER.error("Exp="+e.getMessage());
		throw new Exception(e.toString());
	  }
	  return finYear;
	}
	/**
	 * This method gets the GlCodeId by passing GLCODE as parameter
	 * added by Sapna
	 */
	public String getGlCodeId(String glCode,Connection connection) throws Exception
	{
		String glCodeId="null";
		try{
				String query="select id from chartofaccounts where glCode like ?";
				if(LOGGER.isInfoEnabled())     LOGGER.info("  query   "+query);
			  	pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
			  	pst.setString(1, glCode);
		  	  	rset=pst.list();
		  	  for(Object[] element : rset){
		  	  		glCodeId=element[0].toString();
		  	  		if(LOGGER.isDebugEnabled())     LOGGER.debug("  glCodeId   "+glCodeId);
		  	  	}
		  	if(rset == null || rset.size() == 0) 
		  	  		throw new Exception("id not found");
		}
				catch(Exception e){
					LOGGER.error(" id not found "+e.toString(),e);
					throw new Exception(e.toString());
				}
  		return glCodeId;
	}

	/**
	 * This API will return the transaction no for any type of txn.
	 * Input :Type,transaction date and connection
	 * Output :Transaction number in the format txnType+number+/+month+/+year
	 */
	public String getTxnNumber(String txnType,String vDate) throws Exception
	{
	  String finYear="";
	  String fiscalPeriod="";
	  String retVal="";

	  String month[] = vDate.split("/");
		if(LOGGER.isInfoEnabled())     LOGGER.info(" The month :"+month[1]);
		SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
	//	Date dt=new Date();
		//dt = sdf.parse( vDate );
		String txndate = formatter.format(sdf.parse( vDate ));

	  String sql="select a.FINANCIALYEAR,b.id from FINANCIALYEAR a,fiscalperiod b  where a.id=b.financialyearid AND ? between b.startingdate and b.endingdate";
	  if(LOGGER.isInfoEnabled())     LOGGER.info(sql);
	  try
	  {
	  	pst=HibernateUtil.getCurrentSession().createSQLQuery(sql);
	  	pst.setString(1, txndate);
		rset=pst.list();
		for(Object[] element : rset){
	       finYear=element[0].toString();
	       fiscalPeriod=element[1].toString();
	       if(LOGGER.isInfoEnabled())     LOGGER.info("finYear id>>>>>>>"+finYear +" fiscalPeriod :"+fiscalPeriod);
	    }
		if(rset == null || rset.size() == 0) 
			throw new Exception("Year is not defined in the system");

		String year = finYear.substring(2,4)+finYear.substring((finYear.length()-2),(finYear.length()));
		if(LOGGER.isInfoEnabled())     LOGGER.info(" The year String :"+year);
		//---
		if(LOGGER.isDebugEnabled())     LOGGER.debug(" In CommonMethodsImpl :getTxnNumber method ");
		PersistenceService persistenceService = new PersistenceService();
		//persistenceService.setSessionFactory(new SessionFactory());
		persistenceService.setType(CFiscalPeriod.class);
		CFiscalPeriod fiscalPeriodObj=(CFiscalPeriod) persistenceService.find("from CFiscalPeriod where id=?",Long.parseLong(fiscalPeriod));
		//Sequence name will be SQ_U_DBP_CGVN_FP7 for txnType U/DBP/CGVN and fiscalPeriodIdStr 7
		String sequenceName = VoucherHelper.sequenceNameFor(txnType, fiscalPeriodObj.getName());
		Long runningNumber = DatabaseSequence.named(sequenceName,persistenceService.getSession()).createIfNecessary().nextVal();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("----- Txn Number : "+runningNumber);
		//---
		

		retVal=txnType+runningNumber.toString()+"/"+month[1]+"/"+year;
		if(LOGGER.isInfoEnabled())     LOGGER.info("Return value is :"+retVal);
	  }
	  catch (DatabaseSequenceFirstTimeException e)
	  {
		  LOGGER.error("Exp="+e.getMessage(),e);
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),e.getMessage())));
	  }
	  catch(Exception e)
	  {
	  	LOGGER.error("Exp="+e.getMessage(),e);
		throw new Exception(e.toString());
	  }
	  return retVal;
	}


	/**
		 * added by Iliyaraja
		 * This API will return the generated Voucher number.
		 * Input :Fund Id,txnType,transaction date and connection
		 * Output :Transaction number in the format fundType+txnType+/+number+/+month+/+year
	*/
		public String getTxnNumber(String fundId,String txnType,String vDate,Connection con) throws Exception
		{
			if(txnType==null || txnType.equals(""))
				throw new Exception("Configuration setting for voucher numbering is not done");

		  String fType="";
		  String finYear="";
		  String fiscalPeriod="";
		  String retVal="";

		  String month[] = vDate.split("/");
			if(LOGGER.isInfoEnabled())     LOGGER.info(" The month :"+month[1]);
			SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			String txndate = formatter.format(sdf.parse( vDate ));

		  String sql="select a.FINANCIALYEAR,b.id from FINANCIALYEAR a,fiscalperiod b  where a.id=b.financialyearid AND '"+txndate+"' between b.startingdate and b.endingdate";
		  if(LOGGER.isInfoEnabled())     LOGGER.info(sql);
		  try
		  {

			// This is for getting fund type based on the fund id.
			String query = "SELECT identifier as \"fund_identi\" from fund where id= ?";
			pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
			pst.setString(1, fundId);
			rset = pst.list();
			for(Object[] element : rset){
			 		fType = element[0].toString();

			 		if(LOGGER.isInfoEnabled())     LOGGER.info("Fund Id--->"+fundId +" Fund Type---->"+fType);
			}
			if(rset == null || rset.size() == 0) 
				throw new Exception("Fund is not defined in the system");

			pst = HibernateUtil.getCurrentSession().createSQLQuery(sql);
			rset=pst.list();
			for(Object[] element : rset){
		       finYear=element[0].toString();
		       fiscalPeriod=element[1].toString();
		       if(LOGGER.isInfoEnabled())     LOGGER.info("finYear id>>>>>>>"+finYear +" fiscalPeriod :"+fiscalPeriod);
		    }
			if(rset == null || rset.size() == 0) 
				throw new Exception("Year is not defined in the system");
			
			String year = finYear.substring(2,4)+finYear.substring((finYear.length()-2),(finYear.length()));
			if(LOGGER.isInfoEnabled())     LOGGER.info(" The year String :"+year);
			txnType=fType.concat(txnType);
				
			//---
			if(LOGGER.isDebugEnabled())     LOGGER.debug(" In CommonMethodsImpl :getTxnNumber method ");
			PersistenceService persistenceService = new PersistenceService();
			//persistenceService.setSessionFactory(new SessionFactory());
			persistenceService.setType(CFiscalPeriod.class);
			CFiscalPeriod fiscalPeriodObj=(CFiscalPeriod) persistenceService.find("from CFiscalPeriod where id=?",Long.parseLong(fiscalPeriod));
			//Sequence name will be SQ_U_DBP_CGVN_FP7 for txnType U/DBP/CGVN and fiscalPeriodIdStr 7
			String sequenceName = VoucherHelper.sequenceNameFor(txnType, fiscalPeriodObj.getName());
			Long runningNumber = DatabaseSequence.named(sequenceName,persistenceService.getSession()).createIfNecessary().nextVal();
			if(LOGGER.isDebugEnabled())     LOGGER.debug("----- Txn Number : "+runningNumber);
			//---
			
			retVal=txnType+"/"+runningNumber.toString()+"/"+month[1]+"/"+year;
			if(LOGGER.isInfoEnabled())     LOGGER.info("Return value is :"+retVal);
		  }
		  catch (DatabaseSequenceFirstTimeException e)
		  {
			  LOGGER.error("Exp="+e.getMessage(),e);
				throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),e.getMessage())));
		  }
		  catch(Exception e)
		  {
		  	LOGGER.error("Exp="+e.getMessage(),e);
			throw new Exception(e.toString());
		  }
		  return retVal;
	}
		/*
		 * This API will return the running number.
		 * Parameter  : Fund Id,txnType,transaction date and connection
		 * return     : String transaction number.
		 */
		public String getTransRunningNumber(String fundId,String txnType,String vDate,Connection con) throws Exception {
			
			  if(txnType==null || txnType.equals(""))
				throw new Exception("Configuration setting for voucher numbering is not done");

			  String fType="";
			  String finYear="";
			  String fiscalPeriod="";
			  String retVal="";

			  SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
			  SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			  String txndate = formatter.format(sdf.parse( vDate ));
	
			  String sql="select a.FINANCIALYEAR,b.id from FINANCIALYEAR a,fiscalperiod b  where a.id=b.financialyearid AND '"+txndate+"' between b.startingdate and b.endingdate";
			  if(LOGGER.isDebugEnabled())     LOGGER.debug("sql in getTransRunningNumber() :"+sql);
			  try
			  {
	
				// This is for getting fund type based on the fund id.
				String query = "SELECT identifier as \"fund_identi\" from fund where id= ?";
				pst=HibernateUtil.getCurrentSession().createSQLQuery(query);
				pst.setString(1, fundId);
				rset = pst.list();
				for(Object[] element : rset){
					fType = element[0].toString();
					if(LOGGER.isInfoEnabled())     LOGGER.info("Fund Id  :--->"+fundId +" Fund Type  :---->"+fType);
				}
				if(rset == null || rset.size() == 0) 					throw new Exception("Fund is not defined in the system");
				pst = HibernateUtil.getCurrentSession().createSQLQuery(sql);
				rset=pst.list();
				for(Object[] element : rset){
			       finYear=element[0].toString();
			       fiscalPeriod=element[1].toString();
			       if(LOGGER.isDebugEnabled())     LOGGER.debug("finYear id>>>>>>>"+finYear +" fiscalPeriod :"+fiscalPeriod);
			    }
				if(rset == null || rset.size() == 0) 
					throw new Exception("Year is not defined in the system");
				
				txnType = fType.concat(txnType);
				//---
				if(LOGGER.isDebugEnabled())     LOGGER.debug(" In CommonMethodsImpl :getTxnNumber method ");
				PersistenceService persistenceService = new PersistenceService();
				//persistenceService.setSessionFactory(new SessionFactory());
				persistenceService.setType(CFiscalPeriod.class);
				CFiscalPeriod fiscalPeriodObj=(CFiscalPeriod) persistenceService.find("from CFiscalPeriod where id=?",Long.parseLong(fiscalPeriod));
				//Sequence name will be SQ_U_DBP_CGVN_FP7 for txnType U/DBP/CGVN and fiscalPeriodIdStr 7
				String sequenceName = VoucherHelper.sequenceNameFor(txnType, fiscalPeriodObj.getName());
				Long runningNumber = DatabaseSequence.named(sequenceName,persistenceService.getSession()).createIfNecessary().nextVal();
				if(LOGGER.isDebugEnabled())     LOGGER.debug("----- Running Number : "+runningNumber);
				//---
				retVal = runningNumber.toString();
				if(LOGGER.isInfoEnabled())     LOGGER.info("Return value is in getTransRunningNumber() :"+retVal);
			  }
			  catch (DatabaseSequenceFirstTimeException e)
			  {
				  LOGGER.error("Exp occured in getTransRunningNumber() :"+e.getMessage(),e);
					throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),e.getMessage())));
			  }
			  catch(Exception e) {
			  	LOGGER.error("Exp occured in getTransRunningNumber() :"+e.getMessage(),e);
				throw new Exception(e.toString());
			  }
			  return retVal;
		}
		
		/**
		 * This API will return the sub feild id when the boundary num is passed.
		 * We are ordering by id_bndry_type desc so that sub field value will come first.
		 */
		public Integer getDivisionIdFromCode(String divisionCode,Connection connection) throws Exception
		{
			Integer divId=null;
	   	    String sql="Select id_bndry from EG_BOUNDARY where BNDRY_NUM= ? and is_bndry_active=1 order by id_bndry_type desc";
			if(LOGGER.isInfoEnabled())     LOGGER.info("Sub Field id query-->>>>>>>> "+ sql);
			pst=HibernateUtil.getCurrentSession().createSQLQuery(sql);
			pst.setString(1, divisionCode);
			rset=pst.list();
			for(Object[] element : rset){
				divId=Integer.parseInt(element[0].toString());
			}
			if(LOGGER.isInfoEnabled())     LOGGER.info("Sub Feild id is >>>>>>>"+divId);
			return divId;
		}

}
