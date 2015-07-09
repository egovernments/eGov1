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
package org.egov.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infra.admin.master.entity.AppConfig;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infstr.services.PersistenceService;

public class BudgetDetailConfig {
	private static final String DELIMITER = ",";
	private PersistenceService persistenceService;
	List<String> headerFields = new ArrayList<String>();
	List<String> gridFields = new ArrayList<String>();
	List<String> mandatoryFields = new ArrayList<String>();
	
	public BudgetDetailConfig(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
		headerFields = fetchAppConfigValues("budgetDetail.header.component");
		gridFields = fetchAppConfigValues("budgetDetail.grid.component");
		mandatoryFields = fetchAppConfigValues("budgetDetail_mandatory_fields");
	}
	
	public final List<String> getGridFields() {
		return gridFields;
	}

	public final List<String> getMandatoryFields() {
		return mandatoryFields;
	}

	public final List<String> getHeaderFields() {
		return headerFields;
	}

	final List<String> fetchAppConfigValues(String keyName) {
		AppConfig appConfig = (AppConfig) persistenceService.find("from AppConfig where key_name='"+keyName+"'");
		if(appConfig!=null && appConfig.getAppDataValues()!= null){
			if(appConfig.getAppDataValues().iterator().hasNext()){
				AppConfigValues appDataValues = appConfig.getAppDataValues().iterator().next();
				return Arrays.asList(appDataValues.getValue().split(DELIMITER));
			}
		}
		return new ArrayList<String>();
	}
	
	public final boolean shouldShowField(List<String> fieldList,String field){
		return fieldList.isEmpty() || fieldList.contains(field);
	}
	
	public void checkHeaderMandatoryField(Map<String,Object> valuesToBeChecked) {
		for (Entry<String, Object> entry : valuesToBeChecked.entrySet()) {
			if(headerFields.contains(entry.getKey()) && mandatoryFields.contains(entry.getKey()) && entry.getValue() == null)
				throw new ValidationException(Arrays.asList(new ValidationError("budgetDetail."+entry.getKey()+".mandatory","budgetDetail."+entry.getKey()+".mandatory")));
		}
	}

	public void checkGridMandatoryField(Map<String,Object> valuesToBeChecked) {
		for (Entry<String, Object> entry : valuesToBeChecked.entrySet()) {
			if(gridFields.contains(entry.getKey()) && mandatoryFields.contains(entry.getKey()) && entry.getValue() == null)
				throw new ValidationException(Arrays.asList(new ValidationError("budgetDetail."+entry.getKey()+".mandatory","budgetDetail."+entry.getKey()+".mandatory")));
		}
	}
}
