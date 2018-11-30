/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
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

package org.egov.infra.web.controller.admin.masters.hierarchytype;

import org.egov.infra.admin.master.entity.HierarchyType;
import org.egov.infra.admin.master.service.HierarchyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/hierarchytype")
public class SearchHierarchyTypeController {

    private static final String REQUEST_MAP_VIEW = "/view";
    private static final String REQUEST_MAP_UPDATE = "/update";

    @Autowired
    private HierarchyTypeService hierarchyTypeService;

    @ModelAttribute
    public HierarchyType hierarchyTypeModel() {
        return new HierarchyType();
    }

    @ModelAttribute(value = "hierarchyTypes")
    public List<HierarchyType> listHierarchyTypes() {
        return hierarchyTypeService.getAllHierarchyTypes();
    }

    @GetMapping({REQUEST_MAP_VIEW, REQUEST_MAP_UPDATE})
    public String showHierarchyTypes() {
        return "hierarchyType-list";
    }

    @PostMapping({REQUEST_MAP_VIEW, REQUEST_MAP_UPDATE})
    public String search(@ModelAttribute HierarchyType hierarchyType, BindingResult bindResult, HttpServletRequest request) {
        if (bindResult.hasErrors())
            return "boundaryType-form";
        String requestURI = request.getRequestURI();
        String redirectURI = "";
        if (requestURI.contains("view"))
            redirectURI = "redirect:/hierarchytype/view/" + hierarchyType.getName();
        else if (requestURI.contains("update"))
            redirectURI = "redirect:/hierarchytype/update/" + hierarchyType.getName();
        return redirectURI;
    }
}