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
package org.egov.collection.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.Challan;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.commons.CFinancialYear;
import org.egov.infra.script.service.ScriptService;
import org.egov.infstr.utils.SequenceNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
@Transactional(readOnly=true)
public class CollectionsNumberGenerator {
	private SequenceNumberGenerator sequenceGenerator;
	@Autowired
	private ScriptService scriptService;
	private CollectionsUtil collectionsUtil;

	/**
	 * This method generates the receipt number for the given receipt header
	 *  
	 * @param receiptHeader
	 *            an instance of <code>ReceiptHeader</code>
	 * 
	 * @return a <code>String</code> representing the receipt number
	 */
	public String generateReceiptNumber(ReceiptHeader receiptHeader){
		CFinancialYear financialYear = collectionsUtil.getFinancialYearforDate(new Date());
		SimpleDateFormat sdf =  new SimpleDateFormat("MM");
		String formattedDate = sdf.format(receiptHeader.getReceiptdate()); 
		String strObj="RECEIPTHEADER_"+financialYear.getFinYearRange(); 
		String result=formattedDate+'/'+financialYear.getFinYearRange()+'/'+sequenceGenerator.getNextNumber(strObj,0).getFormattedNumber();
		return result;
	}
	
	/**
	 * This method returns a list of internal reference numbers, each one corresponding 
	 * to the instrument type for the receipt
	 * 
	 * @param receiptHeader an instance of <code>ReceiptHeader</code> containing the
	 * receipt details
	 *  
	 * @param financialYear an instance of <code>CFinancialYear</code> representing the
	 * financial year for the receipt date
	 * 
	 * @return a <code>List</code> of strings, each representing the internal reference 
	 * number corresponding to a particular instrument type.
	 */
	public List<String> generateInternalReferenceNumber(ReceiptHeader receiptHeader,
			CFinancialYear financialYear,CFinancialYear currentFinancialYear){
		
		return (List<String>) scriptService.executeScript(
				CollectionConstants.SCRIPT_INTERNALREFNO_GENERERATOR, 
				ScriptService.createContext(
				"receiptHeader",receiptHeader,"finYear",financialYear,
				"currFinYr",currentFinancialYear,
				"sequenceGenerator",sequenceGenerator));
	}
	
	/**
	 * This method generates the challan number for the given receipt header
	 * 
	 * @param challan an instance of <code>Challan</code>
	 * 
	 * @return a <code>String</code> representing the challan number
	 */
	public String generateChallanNumber(Challan challan,CFinancialYear financialYear){
		return (String) scriptService.executeScript(
				CollectionConstants.SCRIPT_CHALLANNO_GENERERATOR, ScriptService
						.createContext("challan", challan, "sequenceGenerator",
								sequenceGenerator, "finYear", financialYear,
								"collectionsUtil", collectionsUtil));
	}

	public void setSequenceGenerator(SequenceNumberGenerator sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}
	
	public void setCollectionsUtil(CollectionsUtil collectionsUtil) {
		this.collectionsUtil = collectionsUtil;
	}
}
