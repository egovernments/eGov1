/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
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
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.eventnotification.web.controller.notificationdraft;

import static org.egov.eventnotification.constants.Constants.CATEGORY_PARAMETERS;
import static org.egov.eventnotification.constants.Constants.DRAFT_LIST;
import static org.egov.eventnotification.constants.Constants.MESSAGE;
import static org.egov.eventnotification.constants.Constants.MODE;
import static org.egov.eventnotification.constants.Constants.MODE_CREATE;
import static org.egov.eventnotification.constants.Constants.MODE_VIEW;
import static org.egov.eventnotification.constants.Constants.MODULE_CATEGORY;
import static org.egov.eventnotification.constants.Constants.NOTIFICATION_DRAFT;
import static org.egov.eventnotification.constants.Constants.TEMPLATE_MODULE;
import static org.egov.eventnotification.constants.Constants.VIEW_DRAFTUPDATE;
import static org.egov.eventnotification.constants.Constants.VIEW_DRAFTVIEWRESULT;
import static org.egov.eventnotification.constants.Constants.VIEW_EVENTCREATE;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.eventnotification.entity.CategoryParameters;
import org.egov.eventnotification.entity.Drafts;
import org.egov.eventnotification.entity.ModuleCategory;
import org.egov.eventnotification.service.CategoryParametersService;
import org.egov.eventnotification.service.DraftService;
import org.egov.eventnotification.service.DraftTypeService;
import org.egov.eventnotification.service.ModuleCategoryService;
import org.egov.eventnotification.service.TemplateModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ModifyNotificationDraftController {
    @Autowired
    private DraftService draftService;

    @Autowired
    private CategoryParametersService categoryParametersService;

    @Autowired
    private ModuleCategoryService moduleCategoryService;

    @Autowired
    private TemplateModuleService templateModuleService;

    @Autowired
    private DraftTypeService draftTypeService;

    @ModelAttribute("drafts")
    public Drafts getNotificationDrafts(@PathVariable("id") Long id) {
        return draftService.getDraftById(id);
    }

    @GetMapping("/drafts/update/{id}")
    public String update(@ModelAttribute Drafts drafts, Model model) {
        List<ModuleCategory> moduleCategoryList = new ArrayList<>();
        List<CategoryParameters> categoryParametersList = new ArrayList<>();
        if (drafts.getModule() != null)
            moduleCategoryList = moduleCategoryService.getCategoriesForModule(drafts.getModule().getId());
        if (drafts.getCategory() != null)
            categoryParametersList = categoryParametersService.getParametersForCategory(drafts.getCategory().getId());
        model.addAttribute(DRAFT_LIST, draftTypeService.getAllDraftType());
        model.addAttribute(TEMPLATE_MODULE, templateModuleService.getAllModules());
        model.addAttribute(MODULE_CATEGORY, moduleCategoryList);
        model.addAttribute(CATEGORY_PARAMETERS, categoryParametersList);

        model.addAttribute(MODE, MODE_VIEW);

        return VIEW_DRAFTUPDATE;
    }

    @PostMapping("/drafts/update/{id}")
    public String update(@PathVariable("id") Long id, @Valid @ModelAttribute Drafts drafts,
            BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(MESSAGE, "msg.draft.update.error");
            model.addAttribute(MODE, MODE_CREATE);
            return VIEW_EVENTCREATE;
        }
        drafts.setId(id);
        drafts = draftService.updateDraft(drafts);
        model.addAttribute(NOTIFICATION_DRAFT, drafts);
        model.addAttribute(MESSAGE, "msg.draft.update.success");
        model.addAttribute(MODE, MODE_VIEW);
        return VIEW_DRAFTVIEWRESULT;
    }
}
