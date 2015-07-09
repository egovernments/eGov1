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
package org.egov.infra.web.controller.admin.masters.appConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.egov.infra.admin.master.entity.AppConfig;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigService;
import org.egov.infra.admin.master.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Roopa
 */

@Controller
/*
 * @RequestMapping("/appConfig/update/keyName/{keyName}/moduleName/{moduleName}")
 */
@RequestMapping("/appConfig/update/{keyNameArray}")
public class ModifyAppConfigController {

	private AppConfigService appConfigValueService;

	@Autowired
	public ModifyAppConfigController(AppConfigService appConfigValueService,
			ModuleService moduleService) {
		this.appConfigValueService = appConfigValueService;
	}

	
	 @ModelAttribute public AppConfig appConfigModel(@PathVariable final String[]  keyNameArray,Model model) { 
		 Long keyName=null;
		 Long moduleName=null;
		 if(keyNameArray.length>1)
		 {
			keyName=Long.parseLong(keyNameArray[0]);
			moduleName=Long.parseLong(keyNameArray[1]);
		 }
		return appConfigValueService.findBykeyNameAndModuleName(keyName,moduleName); 
		}
	 

	/*@ModelAttribute
	public AppConfig appConfigModel(@PathVariable String keyName) {

		return appConfigValueService.findBykeyName(keyName);
	}*/

	@RequestMapping(method = RequestMethod.GET)
	public String complaintTypeFormForUpdate(
			@ModelAttribute AppConfig appConfig, Model model) {
		if (appConfig != null) {
			if (appConfig.getAppDataValues().isEmpty()) {
				appConfig.addAppDataValues(new AppConfigValues());
			} else {
				appConfig.setAppDataValues(appConfig.getAppDataValues());
			}
			model.addAttribute("mode", "update");
			return "appConfig-editform";
		} else {
			model.addAttribute("message", "No record found");
			return "appConfig-norecord";
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String updateComplaintType(
			@Valid @ModelAttribute AppConfig appConfig, BindingResult errors,
			RedirectAttributes redirectAttrs, Model model) {
		if (errors.hasErrors()) {
			return "appConfig-editform";
		}
		appConfig=buildFeeDetails(appConfig,appConfig.getAppDataValues());
		appConfigValueService.updateAppConfigValues(appConfig);
		String message = "AppConfig Value updated Successfully";
		redirectAttrs.addFlashAttribute("appConfig", appConfig);
		model.addAttribute("message", message);

		return "appConfig-success";
	}

	private AppConfig buildFeeDetails(AppConfig appConfig,List<AppConfigValues> unitDetail)
	{
		Set<AppConfigValues> unitSet=new HashSet<AppConfigValues>();
    	
			for(AppConfigValues unitdetail:unitDetail)
	    	{
				if(unitdetail.getEffectiveFrom() != null && !"".equals(unitdetail.getValue())){
	    		unitdetail.setKey(appConfig);
	    		unitSet.add(unitdetail);
				}
	    	}	
			appConfig.getAppDataValues().clear();
			
			appConfig.getAppDataValues().addAll(unitSet);
   	
    	return appConfig;
    	
	}
}
