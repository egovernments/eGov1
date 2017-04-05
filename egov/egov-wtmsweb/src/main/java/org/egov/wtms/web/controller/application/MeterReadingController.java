/*
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
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
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
 */
package org.egov.wtms.web.controller.application;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.egov.infra.utils.DateUtils;
import org.egov.wtms.application.entity.MeterReadingConnectionDetails;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.repository.WaterConnectionDetailsRepository;
import org.egov.wtms.application.service.ConnectionDemandService;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.masters.entity.WaterRatesDetails;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.masters.repository.WaterRatesDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/application")
public class MeterReadingController {

    @Autowired
    private WaterConnectionDetailsService waterConnectionDetailsService;
    private final WaterConnectionDetailsRepository waterConnectionDetailsRepository;
    private final WaterRatesDetailsRepository waterRatesDetailsRepository;
    private final ConnectionDemandService connectionDemandService;

    @Autowired
    public MeterReadingController(final WaterConnectionDetailsRepository waterConnectionDetailsRepository,
            final WaterRatesDetailsRepository waterRatesDetailsRepository,
            final ConnectionDemandService connectionDemandService) {
        this.waterConnectionDetailsRepository = waterConnectionDetailsRepository;
        this.waterRatesDetailsRepository = waterRatesDetailsRepository;
        this.connectionDemandService = connectionDemandService;
    }

    @ModelAttribute
    public WaterConnectionDetails getWaterConnectionDetails(@PathVariable final String consumerCode) {
        return waterConnectionDetailsService.findByConsumerCodeAndConnectionStatus(consumerCode, ConnectionStatus.ACTIVE);

    }

    private String loadViewData(final Model model,
            final WaterConnectionDetails waterConnectionDetails) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        model.addAttribute("waterConnectionDetails", waterConnectionDetails);
        model.addAttribute("executionDate", formatter.format(waterConnectionDetails.getExecutionDate()));
        model.addAttribute("feeDetails", connectionDemandService.getSplitFee(waterConnectionDetails));
        model.addAttribute(
                "connectionType",
                waterConnectionDetailsService.getConnectionTypesMap().get(
                        waterConnectionDetails.getConnectionType().name()));
        model.addAttribute("mode", "meterEntry");
        model.addAttribute("meterReadingCurrentObj", new MeterReadingConnectionDetails());
        final BigDecimal waterTaxDueforParent = waterConnectionDetailsService.getTotalAmount(waterConnectionDetails);
        model.addAttribute("waterTaxDueforParent", waterTaxDueforParent);
        return "newconnection-meterEntry";
    }

    @RequestMapping(value = "/meterentry/{consumerCode}", method = RequestMethod.GET)
    public String view(final Model model, @PathVariable final String consumerCode, final HttpServletRequest request) {
        final WaterConnectionDetails waterConnectionDetails = waterConnectionDetailsService
                .findByConsumerCodeAndConnectionStatus(consumerCode, ConnectionStatus.ACTIVE);
        MeterReadingConnectionDetails meterReadingpriviousObj = null;
        final List<MeterReadingConnectionDetails> meterReadingpriviousObjlist = waterConnectionDetailsRepository
                .findPreviousMeterReadingReading(waterConnectionDetails.getId());
        if (!meterReadingpriviousObjlist.isEmpty())
            meterReadingpriviousObj = meterReadingpriviousObjlist.get(0);
        else {
            meterReadingpriviousObj = new MeterReadingConnectionDetails();
            if (waterConnectionDetails.getConnection().getInitialReading() != null)
                meterReadingpriviousObj.setCurrentReading(waterConnectionDetails.getConnection().getInitialReading());
            else
                meterReadingpriviousObj.setCurrentReading(0l);
        }
        model.addAttribute("meterReadingpriviousObj", meterReadingpriviousObj);
        if (connectionDemandService.meterEntryAllReadyExistForCurrentMonth(waterConnectionDetails, new Date()))
            return "redirect:/application/meterdemandnotice?pathVar="
                    + waterConnectionDetails.getConnection().getConsumerCode();
        else
            return loadViewData(model, waterConnectionDetails);

    }

    @RequestMapping(value = "/meterentry/{consumerCode}", method = RequestMethod.POST)
    public String updateMeterEntry(@ModelAttribute final WaterConnectionDetails waterConnectionDetails,
            final BindingResult errors, final RedirectAttributes redirectAttrs, final Model model,
            final HttpServletRequest request) {
        final String sourceChannel = request.getParameter("Source");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date givenDate = null;
        try {
            givenDate = dateFormat.parse(request.getParameter("metercurrentReadingDate"));
        } catch (final ParseException e) {

        }
        if (connectionDemandService.meterEntryAllReadyExistForCurrentMonth(waterConnectionDetails, givenDate))
            return "redirect:/application/meterdemandnotice?pathVar="
                    + waterConnectionDetails.getConnection().getConsumerCode();
        final MeterReadingConnectionDetails meterReadingConnectionDeatilObj = new MeterReadingConnectionDetails();
        Long previousReading = 0l;
        if (errors.hasErrors())
            return "newconnection-meterEntry";
        if (null != request.getParameter("previousreading") && !"".equals(request.getParameter("previousreading")))
            previousReading = Long.valueOf(request.getParameter("previousreading"));

        if (Long.valueOf(request.getParameter("metercurrentReading")) < previousReading) {
            final String message = "Current rate should not be less than Previous reading";
            model.addAttribute("message", message);
            return "newconnection-meterEntry";
        }
        final WaterConnectionDetails waterconnectionDetails = billCalculationAndDemandUpdate(waterConnectionDetails, request,
                meterReadingConnectionDeatilObj, previousReading, dateFormat);
        final WaterConnectionDetails savedWaterConnectionDetails = waterConnectionDetailsRepository
                .save(waterconnectionDetails);
        waterConnectionDetailsService.updateIndexes(savedWaterConnectionDetails, sourceChannel);
        return "redirect:/application/meterdemandnotice?pathVar="
                + savedWaterConnectionDetails.getConnection().getConsumerCode();
    }

    private WaterConnectionDetails billCalculationAndDemandUpdate(final WaterConnectionDetails waterConnectionDetails,
            final HttpServletRequest request,
            final MeterReadingConnectionDetails meterReadingConnectionDeatilObj, final Long previousReading,
            final SimpleDateFormat dateFormat) {
        Date currentDate = null;
        Date previousDate = null;
        int noofmonths;

        final String readingDate = request.getParameter("metercurrentReadingDate");

        try {
            currentDate = dateFormat.parse(readingDate);
            if (request.getParameter("previousreadingDate") != null)
                previousDate = dateFormat.parse(request.getParameter("previousreadingDate"));
        } catch (final ParseException e) {

        }
        meterReadingConnectionDeatilObj.setCurrentReading(Long.valueOf(request.getParameter("metercurrentReading")));
        meterReadingConnectionDeatilObj.setCurrentReadingDate(currentDate);

        populateMeterReadingDetails(meterReadingConnectionDeatilObj, waterConnectionDetails);
        if (previousDate != null)
            noofmonths = DateUtils.noOfMonthsBetween(previousDate, currentDate);
        else
            noofmonths = DateUtils.noOfMonthsBetween(new Date(), currentDate);
        final Long currentToPreviousDiffOfUnits = Long.valueOf(request.getParameter("metercurrentReading"))
                - previousReading;
        Long noOfUnitsForPerMonth;
        if (noofmonths > 0)
            noOfUnitsForPerMonth = currentToPreviousDiffOfUnits / noofmonths;
        else
            noOfUnitsForPerMonth = currentToPreviousDiffOfUnits;

        final double finalAmountToBePaid = calculateAmountTobePaid(waterConnectionDetails, noofmonths,
                noOfUnitsForPerMonth);
        WaterConnectionDetails waterconnectionDetails = null;
        if (BigDecimal.valueOf(finalAmountToBePaid).compareTo(BigDecimal.ZERO) > 0)
            waterconnectionDetails = connectionDemandService.updateDemandForMeteredConnection(waterConnectionDetails,
                    BigDecimal.valueOf(finalAmountToBePaid), currentDate);
        return waterconnectionDetails;
    }

    private double calculateAmountTobePaid(final WaterConnectionDetails waterConnectionDetails, final int noofmonths,
            final Long noOfUnitsForPerMonth) {
        WaterRatesDetails waterRateDetail = null;
        final List<WaterRatesDetails> waterDetList = waterRatesDetailsRepository
                .findByWaterRate(waterConnectionDetails.getConnectionType(), waterConnectionDetails.getUsageType(),
                        noOfUnitsForPerMonth);
        if (!waterDetList.isEmpty())
            waterRateDetail = waterDetList.get(0);
        final double amountToBeCollectedWithUnitRatePerMonth = noOfUnitsForPerMonth
                * (waterRateDetail != null ? waterRateDetail.getUnitRate() : 0d);
        double finalAmountToBePaid;
        if (noofmonths > 0)
            finalAmountToBePaid = amountToBeCollectedWithUnitRatePerMonth * noofmonths;
        else
            finalAmountToBePaid = amountToBeCollectedWithUnitRatePerMonth;// 1000
        return finalAmountToBePaid;
    }

    private void populateMeterReadingDetails(final MeterReadingConnectionDetails meterReadingConnectionDeatilObj,
            final WaterConnectionDetails waterConnectionDetails) {
        final List<MeterReadingConnectionDetails> meterentryDetailsList = new ArrayList<>(
                0);
        if (meterReadingConnectionDeatilObj != null && validMeterEntryDetail(meterReadingConnectionDeatilObj)) {
            meterReadingConnectionDeatilObj.setWaterConnectionDetails(waterConnectionDetails);
            meterentryDetailsList.add(meterReadingConnectionDeatilObj);
        }
        waterConnectionDetails.getMeterConnection().clear();
        waterConnectionDetails.setMeterConnection(meterentryDetailsList);
    }

    private boolean validMeterEntryDetail(final MeterReadingConnectionDetails meterReadingConnectionDetails) {
        if (meterReadingConnectionDetails.getCurrentReading() == null
                && meterReadingConnectionDetails.getCurrentReadingDate() == null)
            return false;
        return true;
    }

}