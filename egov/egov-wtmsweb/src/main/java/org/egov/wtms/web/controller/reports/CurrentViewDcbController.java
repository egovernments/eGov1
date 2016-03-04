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

package org.egov.wtms.web.controller.reports;

import static org.egov.demand.model.EgdmCollectedReceipt.RCPT_CANCEL_STATUS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.egov.commons.Installment;
import org.egov.dcb.bean.DCBDisplayInfo;
import org.egov.dcb.bean.DCBReport;
import org.egov.dcb.bean.Receipt;
import org.egov.dcb.service.DCBServiceImpl;
import org.egov.demand.model.EgdmCollectedReceipt;
import org.egov.infra.admin.master.entity.Role;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.enums.BasicPropertyStatus;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.service.CurrentDcbService;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.application.service.collection.WaterConnectionBillable;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.utils.PropertyExtnUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/viewDcb")
public class CurrentViewDcbController {

    @Autowired
    private CurrentDcbService currentDcbService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired(required = true)
    protected WaterConnectionDetailsService waterConnectionDetailsService;
    @Autowired
    private PropertyExtnUtils propertyExtnUtils;

    @ModelAttribute("citizenRole")
    public Boolean getCitizenUserRole() {
        Boolean citizenrole = Boolean.FALSE;
        if (EgovThreadLocals.getUserId() != null) {
            final User currentUser = userService.getUserById(EgovThreadLocals.getUserId());
            for (final Role userrole : currentUser.getRoles())
                if (userrole.getName().equals(WaterTaxConstants.ROLE_CITIZEN)) {
                    citizenrole = Boolean.TRUE;
                    break;
                }
        } else
            citizenrole = Boolean.TRUE;
        return citizenrole;
    }

    @ModelAttribute
    public WaterConnectionDetails getWaterConnectionDetails(@PathVariable final String applicationCode) {
        final WaterConnectionDetails waterConnectionDetails = waterConnectionDetailsService
                .findByConsumerCodeAndConnectionStatus(applicationCode, ConnectionStatus.ACTIVE);
        return waterConnectionDetails;
    }

    @Autowired
    public @ModelAttribute("connectionTypes") Map<String, String> connectionTypes() {
        return waterConnectionDetailsService.getConnectionTypesMap();
    }

    @RequestMapping(value = "/consumerCodeWis/{applicationCode}", method = RequestMethod.GET)
    public String search(final Model model, @PathVariable final String applicationCode, final HttpServletRequest request) {
        final WaterConnectionDetails waterConnectionDetails = getWaterConnectionDetails(applicationCode);

        List<Receipt> cancelRcpt = new ArrayList<Receipt>();
        List<Receipt> activeRcpts = new ArrayList<Receipt>();
        DCBReport dCBReport = new DCBReport();

        model.addAttribute("consumerCode", waterConnectionDetails.getApplicationNumber());

        model.addAttribute(
                "connectionType",
                waterConnectionDetailsService.getConnectionTypesMap().get(
                        waterConnectionDetails.getConnectionType().name()));
        if (waterConnectionDetails.getDemand() != null) {
            final DCBServiceImpl dcbdemandService = (DCBServiceImpl) context.getBean("dcbdemandService");
            final DCBDisplayInfo dcbDispInfo = currentDcbService.getDcbDispInfo();

            final WaterConnectionBillable waterConnectionBillable = (WaterConnectionBillable) context
                    .getBean("waterConnectionBillable");
            final AssessmentDetails assessmentDetails = propertyExtnUtils.getAssessmentDetailsForFlag(
                    waterConnectionDetails.getConnection().getPropertyIdentifier(),
                    PropertyExternalService.FLAG_FULL_DETAILS, BasicPropertyStatus.ALL);
            waterConnectionBillable.setWaterConnectionDetails(waterConnectionDetails);
            waterConnectionBillable.setAssessmentDetails(assessmentDetails);
            dcbdemandService.setBillable(waterConnectionBillable);
            dCBReport = dcbdemandService.getCurrentDCBAndReceipts(dcbDispInfo);
            activeRcpts = populateActiveReceiptsOnly(dCBReport.getReceipts());
            cancelRcpt = populateCancelledReceiptsOnly(dCBReport.getReceipts());
            model.addAttribute("activeRcpts", activeRcpts);
            model.addAttribute("cancelRcpt", cancelRcpt);
            model.addAttribute("totalRcptAmt", calculateReceiptTotal(activeRcpts));
            model.addAttribute("CanceltotalRcptAmt", calculateCancelledReceiptTotal(cancelRcpt));
            model.addAttribute("applicationTypeCode", waterConnectionDetails.getApplicationType().getCode());
            model.addAttribute("dcbReport", dCBReport);
            final BigDecimal waterTaxDueforParent = waterConnectionDetailsService.getTotalAmount(waterConnectionDetails);
            model.addAttribute("waterTaxDueforParent", waterTaxDueforParent);
            model.addAttribute("mode", "viewdcb");

        }
        return "currentDcb-new";
    }

    public BigDecimal calculateReceiptTotal(final List<Receipt> activeRcpts) {
        final List<Receipt> rcpts = new ArrayList<Receipt>();
        BigDecimal totalRcptAmt = BigDecimal.ZERO;
        for (final Receipt r : activeRcpts)
            if (!rcpts.contains(r) && r.getReceiptStatus().equals('A')) {
                rcpts.add(r);
                totalRcptAmt = totalRcptAmt.add(r.getReceiptAmt());
            }
        return totalRcptAmt;
    }

    public BigDecimal calculateCancelledReceiptTotal(final List<Receipt> cancelRcpt) {
        final List<Receipt> rcpts = new ArrayList<Receipt>();
        BigDecimal totalRcptAmt = BigDecimal.ZERO;
        for (final Receipt r : cancelRcpt)
            if (!rcpts.contains(r) && r.getReceiptStatus().equals(EgdmCollectedReceipt.RCPT_CANCEL_STATUS)) {
                rcpts.add(r);
                totalRcptAmt = totalRcptAmt.add(r.getReceiptAmt());
            }
        return totalRcptAmt;
    }

    /**
     * This method populates Active receipts only.
     *
     * @param Map <Installment, List<Receipt>> receipts
     * @return List<Receipt>
     */

    private List<Receipt> populateActiveReceiptsOnly(final Map<Installment, List<Receipt>> receipts) {

        final List<Receipt> rcpt = new ArrayList<Receipt>();
        for (final Map.Entry<Installment, List<Receipt>> entry : receipts.entrySet())
            for (final Receipt r : entry.getValue())
                if (!rcpt.contains(r) && !r.getReceiptStatus().equals(RCPT_CANCEL_STATUS))
                    rcpt.add(r);
        return receiptsInDescendingOrderOfReceiptDate(rcpt);
    }

    private List<Receipt> receiptsInDescendingOrderOfReceiptDate(final List<Receipt> receipts) {

        Collections.sort(receipts, (r1, r2) -> r2.getReceiptDate().compareTo(r1.getReceiptDate()));

        return receipts;
    }

    /**
     * This method populates cancelled receipts only.
     *
     * @param Map <Installment, List<Receipt>> receipts
     * @return List<Receipt>
     */

    private List<Receipt> populateCancelledReceiptsOnly(final Map<Installment, List<Receipt>> receipts) {

        final List<Receipt> rcpt = new ArrayList<Receipt>();
        for (final Map.Entry<Installment, List<Receipt>> entry : receipts.entrySet())
            for (final Receipt r : entry.getValue())
                if (!rcpt.contains(r) && r.getReceiptStatus().equals(RCPT_CANCEL_STATUS))
                    rcpt.add(r);
        return receiptsInDescendingOrderOfReceiptDate(rcpt);
    }

}
