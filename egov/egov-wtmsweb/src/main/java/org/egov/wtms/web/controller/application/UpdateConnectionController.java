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
package org.egov.wtms.web.controller.application;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.service.ConnectionDemandService;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.utils.WaterTaxUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/application")
public class UpdateConnectionController extends GenericConnectionController {

    private final WaterConnectionDetailsService waterConnectionDetailsService;

    private final DepartmentService departmentService;

    private WaterConnectionDetails waterConnectionDetails;

    @Autowired
    private ConnectionDemandService connectionDemandService;
    @Autowired
    private WaterTaxUtils waterTaxUtils;

    @Autowired
    private PositionMasterService positionMasterService;

    @Autowired
    public UpdateConnectionController(final WaterConnectionDetailsService waterConnectionDetailsService,
            final DepartmentService departmentService, final ConnectionDemandService connectionDemandService,
            final SmartValidator validator) {
        this.waterConnectionDetailsService = waterConnectionDetailsService;
        this.departmentService = departmentService;
    }

    @ModelAttribute
    public WaterConnectionDetails getWaterConnectionDetails(@PathVariable final String applicationNumber) {
        waterConnectionDetails = waterConnectionDetailsService.findByApplicationNumber(applicationNumber);
        return waterConnectionDetails;
    }

    @Override
    @ModelAttribute
    public StateAware getModel() {
        return waterConnectionDetails;
    }

    @RequestMapping(value = "/update/{applicationNumber}", method = RequestMethod.GET)
    public String view(final Model model, @PathVariable final String applicationNumber, final HttpServletRequest request) {
        waterConnectionDetails = waterConnectionDetailsService.findByApplicationNumber(applicationNumber);
        model.addAttribute("stateType", waterConnectionDetails.getClass().getSimpleName());
        model.addAttribute("additionalRule", getAdditionalRule());
        model.addAttribute("currentState", waterConnectionDetails.getCurrentState().getValue());
        model.addAttribute("statuscode", waterConnectionDetails.getEgwStatus().getCode());
        model.addAttribute("wfstate", waterConnectionDetails.getState().getId());
        model.addAttribute("approvalPositionExist",  waterConnectionDetailsService.getApprovalPositionByMatrixDesignation(waterConnectionDetails,
                0l, getAdditionalRule()));
        return loadViewData(model, request, waterConnectionDetails);
    }

    private String loadViewData(final Model model, final HttpServletRequest request,
            final WaterConnectionDetails waterConnectionDetails) {
        model.addAttribute("waterConnectionDetails", waterConnectionDetails);
        model.addAttribute("feeDetails", connectionDemandService.getSplitFee(waterConnectionDetails));
        model.addAttribute(
                "connectionType",
                waterConnectionDetailsService.getConnectionTypesMap().get(
                        waterConnectionDetails.getConnectionType().name()));

        if (null == request.getAttribute("mode")) {
            model.addAttribute("applicationHistory", waterConnectionDetailsService.getHistory(waterConnectionDetails));
            model.addAttribute("approvalDepartmentList", departmentService.getAllDepartments());
            model.addAttribute("mode", "inbox");
        }
        return "newconnection-edit";
    }

    @RequestMapping(value = "/update/{applicationNumber}", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final WaterConnectionDetails waterConnectionDetails,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Model model) {

        Long approvalPosition = 0l;
        String approvalComent = "";
        String workFlowAction = "";

        if (request.getParameter("approvalComent") != null)
            approvalComent = request.getParameter("approvalComent");
        if (request.getParameter("workFlowAction") != null)
            workFlowAction = request.getParameter("workFlowAction");
        // TODO: IN Commissioner inbox
        if (workFlowAction!=null && workFlowAction.equals(WaterTaxConstants.APPROVEWORKFLOWACTION) && waterConnectionDetails.getEgwStatus() != null && waterConnectionDetails.getEgwStatus().getCode() != null
                && waterConnectionDetails.getEgwStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_FEEPAID)){
            validateSanctionDetails(waterConnectionDetails, resultBinder);
        }
        if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

        if(approvalPosition==null || approvalPosition.equals(Long.valueOf(0))){
        approvalPosition = waterConnectionDetailsService.getApprovalPositionByMatrixDesignation(waterConnectionDetails,
                approvalPosition, getAdditionalRule());
        }
        if (!resultBinder.hasErrors()) {
            waterConnectionDetailsService.updateNewWaterConnection(waterConnectionDetails, approvalPosition,
                    approvalComent, getAdditionalRule(), workFlowAction);

            final String pathVars = waterConnectionDetails.getApplicationNumber() + ","
                    + waterTaxUtils.getApproverUserName(approvalPosition);
            return "redirect:/application/application-success?pathVars=" + pathVars;
        } else
            return loadViewData(model, request, waterConnectionDetails);
    }

    private void validateSanctionDetails(final WaterConnectionDetails waterConnectionDetails, final BindingResult errors) {

        if (waterConnectionDetails.getApprovalNumber() == null)
            errors.rejectValue("approvalNumber", "approvalNumber.required");

        if (waterConnectionDetails.getApprovalDate() == null)
            errors.rejectValue("approvalDate", "approvalDate.required");
    }

    public String getAdditionalRule() {
        return "NEW CONNECTION";
    }
}