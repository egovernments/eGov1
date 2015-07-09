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
package org.egov.infra.admin.master.service;



import java.util.List;

import org.egov.infra.admin.master.entity.AppConfig;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.repository.AppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Author Roopa
 */
@Service
@Transactional(readOnly = true)
public class AppConfigService {
	
	private final AppConfigRepository appConfigValueRepository;
	
   
	 @Autowired
	    public AppConfigService(final AppConfigRepository appConfigValueRepos) {
	        this.appConfigValueRepository = appConfigValueRepos;
	    }

	 public AppConfig findBykeyNameAndModuleName( Long keyName,Long moduleName) {
	        return appConfigValueRepository.findBykeyNameAndModuleName(keyName,moduleName);
	    }
	    public AppConfig findBykeyName(final String keyName) {
	        return appConfigValueRepository.findBykeyName(keyName);
	    }
	    public AppConfig findById(final Long appId) {
	        return appConfigValueRepository.findById(appId);
	    }
	  public List<Module> findByNameContainingIgnoreCase(String likemoduleName) {
		  return appConfigValueRepository.findByNameContainingIgnoreCase(likemoduleName);
	    }
	  public Module findByModuleById(Long moduleId) {
		 return appConfigValueRepository.findByModuleById(moduleId );
	    }
	  public List<AppConfig> findAll() {
	        return appConfigValueRepository.findAll();
	    }
	  public List<AppConfig> findAllByModule(Long module) {
	        return appConfigValueRepository.findAllByModuleId(module);
	    }
	  
	  public Page<AppConfig> getListOfAppConfig(final Integer pageNumber, final Integer pageSize) {
	        final Pageable pageable = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC, "module.name");
	        return appConfigValueRepository.findAll(pageable);
	    }
	  public List<Module> findAllModules() {
	        return appConfigValueRepository.findAllModules();
	    }
	    @Transactional
	    public void createAppConfigValues(final AppConfig appConfig) {
	    	appConfigValueRepository.save(appConfig);
	    }
	    @Transactional
	    public void updateAppConfigValues(final AppConfig appConfig) {
	    	appConfigValueRepository.save(appConfig);
	    }
	    
	    
	
}
