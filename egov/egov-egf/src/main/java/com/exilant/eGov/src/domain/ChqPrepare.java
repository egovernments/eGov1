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
 * PrepareChq.java Created on July 17, 2007
 *
 * Copyright 2005 eGovernments Foundation. All rights reserved.
 * EGOVERNMENTS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.exilant.eGov.src.domain;

import java.sql.Connection;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.infstr.utils.NumberToWord;
import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.eGov.src.common.EGovernCommon;
import com.exilant.exility.common.TaskFailedException;

/**
 * 
 * @author Mani 
 * 			This Class reads the data to be printed on the cheque and
 *         	positions where these datas to be placed in the cheque for printing
 */
@Transactional(readOnly=true)
public class ChqPrepare {
	public ChqPositions position;
	private final static Logger LOGGER=Logger.getLogger(ChqPrepare.class);
	EGovernCommon cm = new EGovernCommon();

	NumberToWord ntow = new NumberToWord();// to Convert Amount to words
	Connection con;
	List<Object[]> rs;
	Query st;
	String bankId;
	public ChqContent textBean;

	public ChqPrepare(String bankId, String name, String amount,
			String chequeDate) throws TaskFailedException { // this.con=con;
		textBean = new ChqContent();
		this.bankId = bankId;
		textBean.setRs(amount);
		textBean.setDate(chequeDate);
		textBean.setName1(name.toUpperCase());
		textBean = getChqContent();
	}

	/**
	 * 
	 * @return ChqContent object
	 * @throws TaskFailedException
	 *             This Method reads the data to be printed on the cheque. based
	 *             on the length of the field data may be printed in single line
	 *             devided to 2 lines . example 'name' will be of two lines or
	 *             'amount in words' may be of two lines the return will be
	 *             ready to print contents of cheque with positions and modified
	 *             data
	 */
	public ChqContent getChqContent() throws TaskFailedException {
		position = getChqPositions(bankId);
		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		String rupees = textBean.getRs();
		textBean.amount1 = ntow.convertToWord(rupees);

		sb1.append(" ");
		int nocharpercm = 5;
		if ((int) position.amount1.l < textBean.amount1.length()) {
			int length = 0;

			StringTokenizer st = new StringTokenizer(textBean.amount1
					.toUpperCase());
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				length = length + s.length() + 1;
				if (length <= (int) position.amount1.l * nocharpercm) {
					sb.append(s);
					sb.append(" ");
				} else { 
					sb1.append(s);
					sb1.append(" ");
					textBean.setAmount2(new String(sb1).toUpperCase());
				}
			}
			textBean.setAmount1(new String(sb).toUpperCase());
			// textBean.setAmount2(new String(sb1));
		}
		StringBuffer nb = new StringBuffer();
		StringBuffer nb1 = new StringBuffer();
		nb1.append(" ");
		nocharpercm = 5;// Number of charcters printed per centimeter of cheque
		// this decides the data can be printed in single line or two lines
		if ((int) position.name1.l < textBean.name1.length()) {
			int length = 0;

			StringTokenizer st = new StringTokenizer(textBean.name1
					.toUpperCase());
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				length = length + s.length() + 1;
				if (length <= (int) position.name1.l * nocharpercm) {
					nb.append(s);
					nb.append(" ");
				} else {
					nb1.append(s);
					nb1.append(" ");
					textBean.setName2(new String(nb1).toUpperCase());

				}

			}
			textBean.setName1(new String(nb).toUpperCase());

		}

		return textBean;
	}

	/**
	 * 
	 * @param bankId
	 * @return ChqPositions object
	 * @throws TaskFailedException
	 *             This method reads the data from master table to position the
	 *             fields on the cheque for one specific Bank
	 */
	public ChqPositions getChqPositions(String bankId)
			throws TaskFailedException {

		position = new ChqPositions();
		try {

			String getXYs = " Select cf.height, cfd.FIELD,cfd.XVALUE,cfd.YVALUE,cfd.LENGTH from eg_cheque_Format_detail cfd ,eg_cheque_Format cf  where cfd.headerid=cf.id and cf.bankid="
					+ bankId + " order by cfd.id";
			rs = HibernateUtil.getCurrentSession().createSQLQuery(getXYs).list();
			for(Object[] element : rs){
				ChqPosition pos = new ChqPosition();
				pos.setX(Float.parseFloat(element[2].toString()));
				pos.setY(Float.parseFloat(element[3].toString()));
				pos.setL(Float.parseFloat(element[4].toString()));
				if (element[1].toString().equalsIgnoreCase("date"))
					position.setDate(pos);
				else if (element[1].toString().equalsIgnoreCase("pay1"))
					position.setName1(pos);
				else if (element[1].toString().equalsIgnoreCase("pay2"))
					position.setName2(pos);
				else if (element[1].toString().equalsIgnoreCase("rupees1"))
					position.setAmount1(pos);
				else if (element[1].toString().equalsIgnoreCase("rupees2"))
					position.setAmount2(pos);
				else if (element[1].toString().equalsIgnoreCase("rs"))
					position.setRs(pos);
			}

		} catch (Exception e) {
			LOGGER.error("Exp in getChqPositions"+e.getMessage());
			throw new TaskFailedException(e.getMessage());
		} 		return position;
	}
}
