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
package org.egov.pgr.web.controller.complaint;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.egov.eis.service.DesignationService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.entity.ReceivingCenter;
import org.egov.pims.commons.Designation;
import org.egov.pims.commons.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/complaint")
public class GenericComplaintAjaxController extends GenericComplaintController {

    @Autowired
    private BoundaryService boundaryService;
     
    @Autowired
    private DesignationService designationService;

    @Autowired
    private PositionMasterService positionMasterService;

    @RequestMapping(value = { "citizen/isLocationRequired", "citizen/anonymous/isLocationRequired",
            "officials/isLocationRequired" }, method = GET)
    public @ResponseBody boolean isLocationRequired(@RequestParam final String complaintTypeName) {
        final ComplaintType complaintType = complaintTypeService.findByName(complaintTypeName);
        return complaintType == null ? Boolean.TRUE : complaintType.isLocationRequired();
    }

    @RequestMapping(value = { "citizen/complaintTypes", "citizen/anonymous/complaintTypes", "officials/complaintTypes",
            "router/complaintTypes", "escalationTime/complaintTypes" }, method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<ComplaintType> getAllComplaintTypesByNameLike(@RequestParam final String complaintTypeName) {
        return complaintTypeService.findAllByNameLike(complaintTypeName);
    }
    
    @RequestMapping(value = "escalationTime/ajax-approvalDesignations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Designation> getAllDesignationsByName( @RequestParam final String designationName) {
      return  designationService.getAllDesignationsByNameLike(designationName);
    }

    @RequestMapping(value = "officials/isCrnRequired", method = GET)
    public @ResponseBody boolean isCrnRequired(@RequestParam final Long receivingCenterId) {
        final ReceivingCenter receivingCenter = receivingCenterService.findByRCenterId(receivingCenterId);
        return receivingCenter == null ? Boolean.TRUE : receivingCenter.isCrnRequired();
    }

    @RequestMapping(value = { "citizen/locations", "citizen/anonymous/locations", "officials/locations" }, method = GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getAllLocationJSON(@RequestParam final String locationName) {
        final StringBuilder locationJSONData = new StringBuilder("[");
        // TODO Improve this code
        boundaryService.getBoundaryByNameLike(locationName).stream().forEach(location -> {
            locationJSONData.append("{\"name\":\"");
            if (location.isRoot())
                locationJSONData.append(location.getName());
            else {
                Boundary currentLocation = location;
                while (!currentLocation.isRoot()) {
                    locationJSONData.append(currentLocation.getName()).append(", ");
                    currentLocation = currentLocation.getParent();
                    if (currentLocation.isRoot()) {
                        locationJSONData.append(currentLocation.getName());
                        break;
                    }
                }
            }
            locationJSONData.append("\",\"id\":").append(location.getId()).append("},");
        });
        if (locationJSONData.lastIndexOf(",") != -1)
            locationJSONData.deleteCharAt(locationJSONData.lastIndexOf(","));
        locationJSONData.append("]");
        return locationJSONData.toString();
    }

    @RequestMapping(value = { "router/position","escalation/position"  }, method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Position> getAllPositionByNameLike(@RequestParam final String positionName,
            final HttpServletResponse response) throws IOException {
        return positionMasterService.getAllPositionsByNameLike(positionName);
    }

    @RequestMapping(value = { "router/boundaries-by-type","escalation/boundaries-by-type" }, method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Boundary> getBoundariesbyType(@RequestParam final String boundaryName,
            @RequestParam final Long boundaryTypeId, final HttpServletResponse response) throws IOException {
        final String likeBoundaryName = "%" + boundaryName + "%";
        return boundaryService.getBondariesByNameAndType(likeBoundaryName, boundaryTypeId);
    }

}