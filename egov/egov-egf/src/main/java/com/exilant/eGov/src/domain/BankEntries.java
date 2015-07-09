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
 * BankEntries.java  Created on Aug 25, 2006
 *
 *  Copyright 2005 eGovernments Foundation. All rights reserved.
 * EGOVERNMENTS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.exilant.eGov.src.domain;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.egov.infstr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.eGov.src.transactions.brs.BrsEntries;
import com.exilant.exility.common.TaskFailedException;
import com.exilant.exility.updateservice.PrimaryKeyGenerator;

/**
 * @author Tilak
 * 
 * @Version 1.00
 */
@Transactional(readOnly=true)
public class BankEntries {
	private String id = null;
	private int bankAccountId;
	private String refNo = null;
	private String type = null;
	private String txnDate = "";
	private String txnAmount = null;
	private String glcodeId = null;
	private String voucherheaderId = null;
	private String remarks = null;
	private Long instrumentHeaderId = null;

	private boolean isId = false, isField = false;
	private SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd kk:mm:ss.SSS", Locale.getDefault());
	private SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());

	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy",
			Locale.getDefault());
	private static final Logger LOGGER = Logger.getLogger(BankEntries.class);
	private TaskFailedException taskExc;

	public int getBankAccountId() {
		return bankAccountId;
	}

	/**
	 * @param bankAccountId
	 *            The bankAccountId to set.
	 */
	public void setBankAccountId(int bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	@Transactional
	public void insert() throws TaskFailedException,
			SQLException {
		Query pstmt = null;
		try {
			setId(String.valueOf(PrimaryKeyGenerator.getNextKey("bankEntries")));
			String insertQuery = "INSERT INTO bankEntries (Id, BankAccountId, refNo,type,txndate,txnamount,glcodeid,VoucherHeaderId,remarks,instrumentHeaderId)"
					+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
			if(LOGGER.isDebugEnabled())     LOGGER.debug(insertQuery);
			pstmt = HibernateUtil.getCurrentSession().createSQLQuery(insertQuery);
			pstmt.setString(1, id);
			pstmt.setInteger(2, bankAccountId);
			pstmt.setString(3, refNo);
			pstmt.setString(4, type);
			pstmt.setString(5, txnDate);
			pstmt.setString(6, txnAmount);
			pstmt.setString(7, glcodeId);
			pstmt.setString(8, voucherheaderId);
			pstmt.setString(9, remarks);
			pstmt.setLong(10, instrumentHeaderId);
			pstmt.executeUpdate();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw taskExc;
		} 
	}

	public void update() throws TaskFailedException,
			SQLException {
		try {
			newUpdate();
		} catch (Exception e) {
			LOGGER.error("Error inside update" + e.getMessage(), e);
		}
	}

	public void newUpdate() throws TaskFailedException,
			SQLException {
		Query pstmt = null;
		StringBuilder query = new StringBuilder(500);
		query.append("update bankentries set ");
		if (refNo != null)
			query.append("REFNO=?,");
		if (type != null)
			query.append("TYPE=?,");
		if (txnDate != null)
			query.append("TXNDATE=?,");
		if (txnAmount != null)
			query.append("TXNAMOUNT=?,");
		if (glcodeId != null)
			query.append("GLCODEID=?,");
		if (voucherheaderId != null)
			query.append("VOUCHERHEADERID=?,");
		if (remarks != null)
			query.append("REMARKS=?,");
		if (instrumentHeaderId != null)
			query.append("INSTRUMENTHEADERID=?,");
		int lastIndexOfComma = query.lastIndexOf(",");
		query.deleteCharAt(lastIndexOfComma);
		query.append(" where id=?");
		try {
			int i = 1;
			pstmt = HibernateUtil.getCurrentSession().createSQLQuery(query.toString());
			if (refNo != null)
				pstmt.setString(i++, refNo);
			if (type != null)
				pstmt.setString(i++, type);
			if (txnDate != null)
				pstmt.setString(i++, txnDate);
			if (txnAmount != null)
				pstmt.setString(i++, txnAmount);
			if (glcodeId != null)
				pstmt.setString(i++, glcodeId);
			if (voucherheaderId != null)
				pstmt.setString(i++, voucherheaderId);
			if (remarks != null)
				pstmt.setString(i++, remarks);
			if (instrumentHeaderId != null)
				pstmt.setLong(i++, instrumentHeaderId);
			pstmt.setString(i++, id);

			pstmt.executeUpdate();
		} catch (Exception e) {
			LOGGER.error("Exp in update: " + e.getMessage(),e);
			throw taskExc;
		} 
	}

	public void reverse( String cgNum)
			throws SQLException, TaskFailedException {
		Query pstmt = null;
		try {
			String updateQuery = "update bankentries  set isreversed=1 where voucherheaderid in(select id from voucherheader where cgn=?)";
			if(LOGGER.isDebugEnabled())     LOGGER.debug(updateQuery);
			pstmt = HibernateUtil.getCurrentSession().createSQLQuery(updateQuery);
			pstmt.setString(1, cgNum);
			pstmt.executeUpdate();
		} catch (Exception e) {
			LOGGER.error("Exp in reverse:" + e.getMessage(), e);
			throw taskExc;
		} 
	}

	public ArrayList getRecords(String bankAccId)
			throws TaskFailedException, SQLException {
		String query = "SELECT be.id as \"id\", be.refNo as \"refNo\",  be.type as \"type\", "
				+ " To_Char(be.txnDate,'DD/MM/YYYY') as \"txnDate\", "
				+ " be.txnAmount as \"txnAmount\", "
				+ " be.remarks as \"remarks\",be.glcodeid as \"glcodeid\",be.instrumentHeaderId as\"instrumentHeaderId\" "
				+ " from bankentries be,bankaccount ba where be.bankaccountid=ba.id and ba.id=? and be.voucherheaderid is null ORDER BY txnDate";
		if(LOGGER.isInfoEnabled())     LOGGER.info("  query   " + query);
		Query pstmt = null;
		ArrayList al = new ArrayList();
		List<Object[]> rs = null;
		Date dt;
		BrsEntries brs;
		try {
			pstmt = HibernateUtil.getCurrentSession().createSQLQuery(query);
			pstmt.setString(1, bankAccId);
			rs = pstmt.list();
                 
			for(Object[] element : rs){
				brs = new BrsEntries();
				brs.setId(element[0].toString());
				brs.setRefNo(element[1].toString());
				brs.setType(element[2].toString());
				dt = sdf1.parse(element[3].toString());
				brs.setTxnDate(formatter.format(dt));
				brs.setTxnAmount(element[4].toString());
				brs.setRemarks(element[5].toString());
				brs.setGlCodeId(element[6].toString());
				brs.setInstrumentHeaderId(element[7].toString());
				al.add(brs);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw taskExc;
		} 
		return al;
	}

	// get all ISRECONCILED=0 Cheque details FOR CORE PRODUCT
	public List getChequeDetails(String mode,Long bankAccId, Long bankId, String chequeNo,
			String chqFromDate, String chqToDate,String vhId)
			throws TaskFailedException, SQLException {
		if(LOGGER.isInfoEnabled())     LOGGER.info(" INSIDE getChequeDetails()>>>>>>>> ");
		Query pstmt =null;
		List<Object[]> rs = null;
		List al = new ArrayList();
		try {
			String detailsQuery = getDetailsQuery(mode,bankAccId, bankId, chequeNo,
					chqFromDate, chqToDate, pstmt,vhId);
			int count=0;
			//set pstmt
			
			LOGGER
					.debug("  DishonoredCheque getChequeDetails instrument  Query is  "
							+ detailsQuery);
			pstmt = HibernateUtil.getCurrentSession().createSQLQuery(detailsQuery);
			if (bankAccId != null && bankAccId != 0) {
				count++;
				pstmt.setLong(count, bankAccId);
			}
		

			if (!("").equals(chequeNo)) {
				count++;
				pstmt.setString(count, chequeNo);
			}
			if (!("").equals(chqFromDate)) {
				count++;
				pstmt.setString(count, chqFromDate);
			}
			if (!("").equals(chqToDate)) {
				count++;
				pstmt.setString(count, chqToDate);
			}
			rs = executeChequeDetailsQuery(al, pstmt, detailsQuery);
		} catch (Exception e) {
			LOGGER.error("Exp in getChequeDetails:" + e.getMessage(), e);
			throw taskExc;
		} 
		return al;
	}

	private List<Object[]> executeChequeDetailsQuery(List al,
			Query pstmt, String query) throws SQLException,
			ParseException {
		List<Object[]> rs;
		Date dt;
		//pstmt.
		rs = pstmt.list();
		BrsEntries brs;
		for(Object[] element : rs){
			brs = new BrsEntries();
			brs.setVoucherNumber(element[4].toString());
			brs.setCgnum(element[3].toString()	);
			brs.setVoucherHeaderId(element[0].toString()	);
			brs.setInstrumentHeaderId(element[1].toString()	);
			brs.setPayinSlipVHeaderId(element[2].toString()	);
			brs.setVoucherType(element[5].toString());
			brs.setFundId(element[6].toString()	);
			brs.setFundSourceId(element[7].toString());
			brs.setChequeNumber(element[8].toString());
			brs.setBankName(element[11].toString());
			brs.setAccNumber(element[12].toString());
			brs.setAccIdParam(element[13].toString());
			brs.setPayTo(element[14].toString());
			brs.setPayCheque(element[15].toString());
			
		//	dt = sdf1.parse(rs.getString("CHEQUEDATE"));
			brs.setChequeDate(element[9].toString());
			brs.setAmount(element[10].toString());
			brs.setDepartmentId(element[16].toString());
			brs.setFunctionaryId(element[17].toString());
			brs.setFunctionId(element[18].toString());
			if(LOGGER.isDebugEnabled())     LOGGER.debug("BankEntries | getChequeDetails | departmentId>>>"
					+ brs.getDepartmentId());
			if(LOGGER.isDebugEnabled())     LOGGER.debug("BankEntries | getChequeDetails | functionaryId>>>"
					+ brs.getFunctionaryId());
			al.add(brs);
		}
		return rs;
	}

	private String getDetailsQuery(String mode,Long bankAccId, Long bankId,
			String chequeNo, String chqFromDate, String chqToDate,
			Query pstmt,String vhId) throws SQLException {
		int count = 0;
		StringBuffer basicquery1 = new StringBuffer(
				"SELECT distinct vh.id as \"voucherHeaderId\",ih.id as \"instrumentHeaderId\",vh.id as \"payinVHeaderId\","
						+ "vh.cgn as \"cgnumber\",vh.VOUCHERNUMBER as \"voucherNumber\",vh.TYPE as \"type\",vh.FUNDID as \"fundId\","
						+ "vh.FUNDSOURCEID as \"fundSourceId\",ih.INSTRUMENTNUMBER as \"chequeNumber\",To_Char(ih.INSTRUMENTDATE,'dd/mm/yyyy') as \"chequeDate\","
						+ "ih.INSTRUMENTAMOUNT as \"amount\",bank.NAME as \"bank\",bacc.ACCOUNTNUMBER as \"accNumber\",bacc.ID as \"accIdParam\","
						+ "ih.PAYTO as \"payTo\" ,ih.ISPAYCHEQUE AS \"payCheque\",vmis.DEPARTMENTID AS \"departmentId\","
						+ " vmis.FUNCTIONARYID AS \"functionaryId\" , vmis.FUNCTIONID AS \"functionId\" "
						+ " FROM VOUCHERHEADER vh,egf_instrumentheader ih,BANK bank,BANKACCOUNT bacc,VOUCHERMIS vmis,bankbranch branch,"
						+ "egf_instrumenttype it,EGF_INSTRUMENTVOUCHER iv");

		StringBuffer wherequery1 = new StringBuffer(
				" WHERE vh.status=0 AND vh.id=vmis.voucherheaderid and ih.INSTRUMENTTYPE=it.id and "
						+ "it.TYPE='"+mode+"' and iv.VOUCHERHEADERID=vh.ID and iv.INSTRUMENTHEADERID=ih.id and ((ih.ispaycheque=0 and ih.id_status=(select id from "
						+ "egw_status where moduletype='Instrument' and description='Deposited')) or (ih.ispaycheque=1 and ih.id_status=(select id from "
						+ "egw_status where moduletype='Instrument' and description='New'))) and branch.BANKID=bank.id AND branch.id=bacc.BRANCHID");

		StringBuffer orderbyquery = new StringBuffer(
				" ORDER BY \"voucherNumber\",\"type\",\"chequeDate\" ");
		
		if (bankAccId != null && bankAccId != 0) {
			wherequery1 = wherequery1.append(" AND ih.BANKACCOUNTID=? ");
			wherequery1 = wherequery1.append(" AND ih.BANKACCOUNTID=bacc.ID");
		}
		if ((bankAccId == null || bankAccId == 0) && bankId != null
				&& bankId != 0) {
			wherequery1 = wherequery1.append(" AND bank.id=").append(bankId);
			wherequery1 = wherequery1.append(" AND ih.BANKACCOUNTID=bacc.id");
		}
		if ((bankAccId == null || bankAccId == 0)
				&& (bankId == null || bankId == 0)) {
			wherequery1 = wherequery1.append(" AND ih.BANKACCOUNTID=bacc.id");
		}
		if (!("").equals(chequeNo)) {
			wherequery1 = wherequery1
					.append(" AND ih.INSTRUMENTNUMBER=trim(?) ");
		}
		if (!("").equals(chqFromDate)) {
			wherequery1 = wherequery1.append(" AND ih.INSTRUMENTDATE >? ");
		}
		if (!("").equals(chqToDate)) {
			wherequery1 = wherequery1.append(" AND ih.INSTRUMENTDATE <? ");
		}
		if (null !=vhId && !("").equals(vhId)) {
			wherequery1 = wherequery1.append(" AND vh.id= "+vhId);
		}
	
		String query = new StringBuffer().append(basicquery1).append(
				wherequery1).append(orderbyquery).toString();
		return query;
		
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getGlcodeId() {
		return glcodeId;
	}

	public void setGlcodeId(String glcodeId) {
		this.glcodeId = glcodeId;
	}

	public String getVoucherheaderId() {
		return voucherheaderId;
	}

	public void setVoucherheaderId(String voucherheaderId) {
		this.voucherheaderId = voucherheaderId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getInstrumentHeaderId() {
		return instrumentHeaderId;
	}

	public void setInstrumentHeaderId(Long instrumentHeaderId) {
		this.instrumentHeaderId = instrumentHeaderId;
	}
}
