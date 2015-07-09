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
package org.egov.pgr.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.egov.eis.service.DesignationService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.web.support.json.adapter.UserAdaptor;
import org.egov.infstr.services.EISServeable;
import org.egov.pims.commons.Designation;
import org.egov.pims.commons.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Controller
public class AjaxController {

    @Autowired
    private BoundaryService boundaryService;

    @Autowired
    private EISServeable eisService;
    
    @Autowired
    private DesignationService designationService;
   
    @Autowired
    private PositionMasterService positionMasterService;
    
    @RequestMapping(value = "/ajax-getWards", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Boundary> getWardsForZone(@RequestParam final Long id) {
        return boundaryService.getActiveChildBoundariesByBoundaryId(id);
    }

    @RequestMapping(value = "/ajax-approvalDesignations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Designation> getDesignations(
            @ModelAttribute("designations") @RequestParam final Integer approvalDepartment) {
        List<Designation> designations =  eisService.getAllDesignationByDept(approvalDepartment, new Date());
        //FIXME this is hack for lazy loaded collection
        designations.forEach(designation -> designation.toString());
        return designations;
    }
    
    @RequestMapping(value = "/ajax-designationsByDepartment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Designation> getDesignationsByDepartmentId(
            @ModelAttribute("designations") @RequestParam final Long approvalDepartment) {
        List<Designation> designations =  designationService.getAllDesignationByDepartment(approvalDepartment, new Date());
         designations.forEach(designation -> designation.toString());
        return designations;
    }
    
    @RequestMapping(value = "/ajax-positionsByDepartmentAndDesignation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Position>  getPositionByDepartmentAndDesignation(@RequestParam final Long approvalDepartment,
			@RequestParam final Long approvalDesignation,
			final HttpServletResponse response) {
		List<Position> positions = new ArrayList<Position>();
		positions = positionMasterService.getPositionsByDepartmentAndDesignationForGivenRange(
				approvalDepartment, approvalDesignation, new Date());

		positions.forEach(position -> position.toString());
		// }
		return positions;
	}

    /**
     * This api uses UserAdaptor to Construct Json
     *
     * @param approvalDepartment
     * @param approvalDesignation
     * @return
     * @throws IOException
     */

    @RequestMapping(value = "/ajax-approvalPositions", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getPositions(@RequestParam final Integer approvalDepartment,
            @RequestParam final Integer approvalDesignation, final HttpServletResponse response) throws IOException {
        if (approvalDepartment != null && approvalDepartment != 0 && approvalDesignation != null && approvalDesignation != 0) {
            final List<User> users = eisService.getUsersByDeptAndDesig(approvalDepartment, approvalDesignation, new Date());
            // below line should be removed once the commonService.getPosistions
            // apis query joins and returns user
            final Gson jsonCreator = new GsonBuilder().registerTypeAdapter(User.class, new UserAdaptor()).create();
            return jsonCreator.toJson(users, new TypeToken<Collection<User>>() {}.getType());
        }
        return "[]";
    }
}
