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
 * Created on Jul 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.exilant.eGov.src.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.egov.infstr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.exility.common.DataCollection;
import com.exilant.exility.common.TaskFailedException;
import com.exilant.exility.updateservice.PrimaryKeyGenerator;

/**
 * @author Mani
 *
 * This Bean Used to hold the Cheque Format values ,insert and update 
 */
@Transactional(readOnly=true)
public class ChequeFormatMasterBean 
{
	private String id;
	private String bankId;
	private String hight;
	private String field1x,field1y,field1l;
	private String field2x,field2y,field2l;
	private String field3x,field3y,field3l;
	private String field4x,field4y,field4l;
	private String field5x,field5y,field5l;
	private String field6x,field6y,field6l;
	private TaskFailedException taskExc;
	private final static Logger LOGGER=Logger.getLogger(ChequeFormatMasterBean.class);
	private SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy",Locale.getDefault());
	
	/**
	 * @return the bankId
	 */
	public String getBankId() {
		return bankId;
	}
	/**
	 * @param bankId the bankId to set
	 */
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	/**
	 * @return the field1l
	 */
	public String getField1l() {
		return field1l;
	}
	/**
	 * @param field1l the field1l to set
	 */
	public void setField1l(String field1l) {
		this.field1l = field1l;
	}
	/**
	 * @return the field1x
	 */
	public String getField1x() {
		return field1x;
	}
	/**
	 * @param field1x the field1x to set
	 */
	public void setField1x(String field1x) {
		this.field1x = field1x;
	}
	/**
	 * @return the field1y
	 */
	public String getField1y() {
		return field1y;
	}
	/**
	 * @param field1y the field1y to set
	 */
	public void setField1y(String field1y) {
		this.field1y = field1y;
	}
	/**
	 * @return the field2l
	 */
	public String getField2l() {
		return field2l;
	}
	/**
	 * @param field2l the field2l to set
	 */
	public void setField2l(String field2l) {
		this.field2l = field2l;
	}
	/**
	 * @return the field2x
	 */
	public String getField2x() {
		return field2x;
	}
	/**
	 * @param field2x the field2x to set
	 */
	public void setField2x(String field2x) {
		this.field2x = field2x;
	}
	/**
	 * @return the field2y
	 */
	public String getField2y() {
		return field2y;
	}
	/**
	 * @param field2y the field2y to set
	 */
	public void setField2y(String field2y) {
		this.field2y = field2y;
	}
	/**
	 * @return the field3l
	 */
	public String getField3l() {
		return field3l;
	}
	/**
	 * @param field3l the field3l to set
	 */
	public void setField3l(String field3l) {
		this.field3l = field3l;
	}
	/**
	 * @return the field3x
	 */
	public String getField3x() {
		return field3x;
	}
	/**
	 * @param field3x the field3x to set
	 */
	public void setField3x(String field3x) {
		this.field3x = field3x;
	}
	/**
	 * @return the field3y
	 */
	public String getField3y() {
		return field3y;
	}
	/**
	 * @param field3y the field3y to set
	 */
	public void setField3y(String field3y) {
		this.field3y = field3y;
	}
	/**
	 * @return the field4l
	 */
	public String getField4l() {
		return field4l;
	}
	/**
	 * @param field4l the field4l to set
	 */
	public void setField4l(String field4l) {
		this.field4l = field4l;
	}
	/**
	 * @return the field4x
	 */
	public String getField4x() {
		return field4x;
	}
	/**
	 * @param field4x the field4x to set
	 */
	public void setField4x(String field4x) {
		this.field4x = field4x;
	}
	/**
	 * @return the field4y
	 */
	public String getField4y() {
		return field4y;
	}
	/**
	 * @param field4y the field4y to set
	 */
	public void setField4y(String field4y) {
		this.field4y = field4y;
	}
	/**
	 * @return the field5l
	 */
	public String getField5l() {
		return field5l;
	}
	/**
	 * @param field5l the field5l to set
	 */
	public void setField5l(String field5l) {
		this.field5l = field5l;
	}
	/**
	 * @return the field5x
	 */
	public String getField5x() {
		return field5x;
	}
	/**
	 * @param field5x the field5x to set
	 */
	public void setField5x(String field5x) {
		this.field5x = field5x;
	}
	/**
	 * @return the field5y
	 */
	public String getField5y() {
		return field5y;
	}
	/**
	 * @param field5y the field5y to set
	 */
	public void setField5y(String field5y) {
		this.field5y = field5y;
	}
	/**
	 * @return the field6l
	 */
	public String getField6l() {
		return field6l;
	}
	/**
	 * @param field6l the field6l to set
	 */
	public void setField6l(String field6l) {
		this.field6l = field6l;
	}
	/**
	 * @return the field6x
	 */
	public String getField6x() {
		return field6x;
	}
	/**
	 * @param field6x the field6x to set
	 */
	public void setField6x(String field6x) {
		this.field6x = field6x;
	}
	/**
	 * @return the field6y
	 */
	public String getField6y() {
		return field6y;
	}
	/**
	 * @param field6y the field6y to set
	 */
	public void setField6y(String field6y) {
		this.field6y = field6y;
	}
	/**
	 * @return the hight
	 */
	public String getHight() {
		return hight;
	}
	/**
	 * @param hight the hight to set
	 */
	public void setHight(String hight) {
		this.hight = hight;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @param con
	 * @param dc
	 * @throws TaskFailedException
	 * Used to create new format for the Spacific Bank
	 */
	@Transactional
	public void insert(DataCollection dc) throws TaskFailedException
	 {
		boolean formatExists=false;
		Query st=null;
		try{
			long MaxId=0;
			MaxId=getId("eg_cheque_format");
			dc.addValue("id",MaxId);
            Date cDate=new Date();

			String currDate = formatter1.format(cDate);
            dc.addValue("currDate", currDate);
            // If Cheque Format For bank Selected does not exists record the format else throw exception 
            // in that case only format can be modified
		    formatExists=recordExists("eg_cheque_format",dc.getValue("BankList"));
			if(!formatExists)
			{
	            if(LOGGER.isDebugEnabled())     LOGGER.debug("cheFormatExists exists :"+formatExists);
				String mainFormat="insert into eg_cheque_Format (id,bankid,height,field1,field2,field3,field4,field5,field6,"+
									"createdby,createddate )"+
				                    "Values ("+								
				                     dc.getValue("id") +", " +  dc.getValue("BankList")+ " ,"+
				                     dc.getValue("hight") +",'date' ,'pay1','pay2' ,'rupees1','rupees2','rs', "+ 
				                     dc.getValue("current_UserID") +",' " +  dc.getValue("currDate")+"')" ;
				    
				if(LOGGER.isDebugEnabled())     LOGGER.debug("Cheque Format Query :" + mainFormat);
				st = HibernateUtil.getCurrentSession().createSQLQuery(mainFormat);
				st.executeUpdate();
				String subFormat="insert into eg_cheque_format_detail (id,headerid,field,xvalue,yvalue,length,lastmodifiedDate )"+
									"values(?,?,?,?,?,?,?)";
				Query pst=HibernateUtil.getCurrentSession().createSQLQuery(subFormat);
				long subId;
				String fieldArray[]={"date","pay1","pay2","rupees1","rupees2","rs"};
				String [][] detailsgrid=dc.getGrid("detailsgrid");
				for(int i=1;i<=6;i++)
				{
					subId=getId("eg_cheque_format_detail");
					pst.setLong(1,subId);
					pst.setString(2,dc.getValue("id"));
					pst.setString(3,fieldArray[i-1]);
					pst.setString(4,detailsgrid[i-1][0]);
					pst.setString(5,detailsgrid[i-1][1]);
					pst.setString(6,detailsgrid[i-1][2]);
					pst.setString(7,dc.getValue("currDate"));
					pst.executeUpdate();
				}
			}
			else
			{
				throw new TaskFailedException("Chequeformat Already Exists for this bank");
			}	
		}catch(Exception e)
			{
				LOGGER.error(e.getMessage());
				throw taskExc;
			}
		
	 }
	
	/**
	 * 
	 * @param con
	 * @param dc
	 * @throws TaskFailedException
	 * Modify the pre existing Chequeformat for the spcific bank with new Values
	 */
	@Transactional
	 public void modify(DataCollection dc) throws TaskFailedException
	 {
		Query st=null;
		try
		{
			Date cDate=new Date();
			String currDate = formatter1.format(cDate);
			dc.addValue("currDate", currDate);
			String mainFormat="Update  eg_cheque_Format set bankid="+dc.getValue("BankList")+
								",height="+ dc.getValue("hight") + 
								",lastmodifiedBy="+  dc.getValue("current_UserID")+
								",lastmodifiedDate='"+dc.getValue("currDate")+
								"' where bankId="+dc.getValue("BankList") ;
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Cheque Format Update Query :" + mainFormat);
			st  = HibernateUtil.getCurrentSession().createSQLQuery(mainFormat);
			st.executeUpdate();
			String subFormat="update  eg_cheque_Format_detail set xvalue=?,yvalue=?,length=?,lastmodifiedDate=? "+
								"where headerid=? and field=?";
			Query pst=HibernateUtil.getCurrentSession().createSQLQuery(subFormat);
			String fieldArray[]={"date","pay1","pay2","rupees1","rupees2","rs"};
			String [][] detailsgrid=dc.getGrid("detailsgrid"); 
			for(int i=1;i<=6;i++)
			{
				pst.setString(1,detailsgrid[i-1][0]);
				pst.setString(2,detailsgrid[i-1][1]);
				pst.setString(3,detailsgrid[i-1][2]);
				pst.setString(4,dc.getValue("currDate"));
				pst.setString(5,dc.getValue("headerid"));
				pst.setString(6,fieldArray[i-1]);
				pst.executeUpdate();
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Exception in modify:"+e.getMessage());
			dc.addMessage("exilRPError","Error in Update "+e.toString());
			throw taskExc;
		}
	 }
	 
	/**
	 * 
	 * @param id
	 * @param tableName
	 * @param bankid
	 * @param con
	 * @return
	 * @throws TaskFailedException
	 * this method used to cheque whether format for the specified bank Exists 
	 * it iis used to allow to create new format or not
	 * that is if format already exists only it canbe modified 
	 */ 	
	 private boolean recordExists(String tableName,String bankid ) throws TaskFailedException	{
			Query st=null;
			boolean flag=false;
			List<Object[]> rs= null;
			try
			{
				String query="";
				query="select count(*) from "+tableName+" where bankid="+bankid;
				if(LOGGER.isDebugEnabled())     LOGGER.debug("record exists query "+query);
				rs=HibernateUtil.getCurrentSession().createSQLQuery(query).list();
				for(Object[] element : rs){
					if (element[0].toString()==null)
						flag=false;
					else
					{
						if (Integer.parseInt(element[0].toString())>0)
							flag=true;
						else
							flag=false;
					}
				}
				if(rs == null || rs.size() == 0) 
					flag=false;
			}
			catch(Exception e)
			{
				LOGGER.error("Inside recordExists"+e.getMessage());
				throw taskExc;
			}
			return flag;
		}
	private long getId(String tableName)
		{
			return PrimaryKeyGenerator.getNextKey(tableName);
		}
 }
