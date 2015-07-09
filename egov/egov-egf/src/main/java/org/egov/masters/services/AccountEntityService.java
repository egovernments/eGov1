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
/**
 * 
 */
package org.egov.masters.services;

import java.util.ArrayList;
import java.util.List;

import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.infstr.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.masters.model.AccountEntity;
import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mani
 *
 */
@Transactional(readOnly=true)
public class AccountEntityService extends PersistenceService<AccountEntity, Integer> implements EntityTypeService{
		public List<EntityType> getAllActiveEntities(Integer accountDetailTypeId) {
	      List<EntityType> entities=new ArrayList<EntityType>();
			entities.addAll(findAllBy("from AccountEntity a where a.isactive=? and accountdetailtype.id=?",true,accountDetailTypeId));
	       return entities;
		}

		@Override
		public List<EntityType> filterActiveEntities(String filterKey, int maxRecords, Integer accountDetailTypeId) {
			Integer pageSize = (maxRecords > 0 ? maxRecords : null);
			  List<EntityType> entities=new ArrayList<EntityType>();
			  filterKey="%"+filterKey+"%";
				String qry="from AccountEntity  where accountdetailtype.id=? and ((upper(code) like upper(?) or upper(name) like upper(?))  and isactive=?)   order by code,name";
				entities.addAll((List<EntityType>)findPageBy(qry,0, pageSize,accountDetailTypeId,filterKey,filterKey,true).getList());
		       return entities;
			
		}

		@Override
		public List getAssetCodesForProjectCode(Integer accountdetailkey)
			throws ValidationException {
		
		return null;
		}
		 public List<EntityType> validateEntityForRTGS(List<Long> idsList) throws ValidationException {
			 
			 return null;
			 
		 }
		 public List<EntityType> getEntitiesById(List<Long> idsList) throws ValidationException {
			
			 List<Integer> ids=new ArrayList<Integer>();
				if(idsList!=null)
				{
					for(Long id:idsList)
					{
						ids.add(id.intValue());
					}
				}
			
			 List<EntityType> entities=new ArrayList<EntityType>();
			 Query entitysQuery = HibernateUtil.getCurrentSession().createQuery(" from AccountEntity where id in ( :IDS )");
			 entitysQuery.setParameterList("IDS", ids);
			 entities = entitysQuery.list();
			return entities;
			 
		 }

}
