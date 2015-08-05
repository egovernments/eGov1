/*
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
package org.egov.pgr.service.dashboard;

import static org.egov.infra.utils.DateUtils.endOfGivenDate;
import static org.egov.infra.utils.DateUtils.startOfGivenDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.pgr.repository.dashboard.DashboardRepository;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class DashboardService {

    private static final DateTimeFormatter DFLT_DATE_FRMTR = DateTimeFormat.forPattern("dd/MM/yyyy");

    public static final Map<String, String[]> COLOR_GRADIENTS = new HashMap<String, String[]>();

    static {
        COLOR_GRADIENTS.put("#5B94CB", new String[] { "#00285F", "#1D568D", "#4B84BB", "#79B2E9", "#A7E0FF" });
        COLOR_GRADIENTS.put("#938250", new String[] { "#584e30", "#665b38", "#756840", "#847548", "#9d8e61" });
        COLOR_GRADIENTS.put("#f9f107", new String[] { "#BBB300", "#E9E100", "#FFFF25", "#FFFF53", "#FFFF79" });
        COLOR_GRADIENTS.put("#6AC657", new String[] { "#005A00", "#0E6A00", "#2C8819", "#5AB647", "#88E475" });
        COLOR_GRADIENTS.put("#4F54B8", new String[] { "#00004C", "#11167A", "#3F44A8", "#6D72D6", "#9BA0FF" });
        COLOR_GRADIENTS.put("#B15D16", new String[] { "#450000", "#731F00", "#A14D06", "#CF7B34", "#FDA962" });
        COLOR_GRADIENTS.put("#C00000", new String[] { "#540000", "#B00000", "#DE1E1E", "#FF4C4C", "#FF5454" });
    }

    @Autowired
    private DashboardRepository dashboardRepository;

    public Collection<Integer> getComplaintRegistrationTrend() {
        final DateTime currentDate = new DateTime();
        final Map<String, Integer> currentYearTillDays = constructDatePlaceHolder(currentDate.minusDays(6), currentDate, "MM-dd");
        for (final Object[] compDtl : dashboardRepository
                .fetchComplaintRegistrationTrendBetween(startOfGivenDate(currentDate.minusDays(6)).toDate(),
                        endOfGivenDate(currentDate).toDate()))
            currentYearTillDays.put(String.valueOf(compDtl[0]), Integer.valueOf(String.valueOf(compDtl[1])));
        return currentYearTillDays.values();
    }

    public Collection<Integer> getComplaintResolutionTrend() {
        final DateTime currentDate = new DateTime();
        final Map<String, Integer> currentYearTillDays = constructDatePlaceHolder(currentDate.minusDays(6), currentDate, "MM-dd");
        for (final Object[] compDtl : dashboardRepository.fetchComplaintResolutionTrendBetween(
                startOfGivenDate(currentDate.minusDays(6)).toDate(),
                endOfGivenDate(currentDate).toDate()))
            currentYearTillDays.put(String.valueOf(compDtl[1]), Integer.valueOf(String.valueOf(compDtl[0])));
        return currentYearTillDays.values();
    }

    public List<Map<String, Object>> getMonthlyAggregate() {
        final DateTime currentDate = new DateTime();
        final List<Map<String, Object>> dataHolder = constructMonthPlaceHolder(currentDate.minusMonths(6), currentDate,
                "MMM-yyyy");
        for (final Object[] compCnt : dashboardRepository.fetchMonthlyAggregateBetween(
                startOfGivenDate(currentDate.minusMonths(6).withDayOfMonth(1)).toDate(), endOfGivenDate(currentDate).toDate()))
            for (final Map<String, Object> mapdata : dataHolder)
                if (mapdata.containsValue(StringUtils.capitalize(String.valueOf(compCnt[0]).toLowerCase())))
                    mapdata.put("y", Integer.valueOf(String.valueOf(compCnt[1])));
        return dataHolder;
    }

    public List<Map<String, Object>> getCompTypewiseAggregate() {
        final DateTime currentDate = new DateTime();
        final List<Map<String, Object>> compTypeWiseData = new LinkedList<Map<String, Object>>();
        long totalOthersCount = 0;
        int topCount = 1;
        for (final Object[] complaint : dashboardRepository.fetchComplaintTypeWiseBetween(
                startOfGivenDate(currentDate.minusMonths(6).withDayOfMonth(1)).toDate(), endOfGivenDate(currentDate).toDate())) {
            final Map<String, Object> compTypewiseCnt = new HashMap<String, Object>();
            final Integer complaintCount = Integer.valueOf(String.valueOf(complaint[2]));
            if (topCount < 9) {
                compTypewiseCnt.put("name", String.valueOf(complaint[0]));
                compTypewiseCnt.put("ctId", complaint[1]);
                compTypewiseCnt.put("y", complaintCount);
                compTypeWiseData.add(compTypewiseCnt);
                topCount++;
            } else
                totalOthersCount += complaintCount;
        }

        if (totalOthersCount > 0) {
            final Map<String, Object> othersData = new HashMap<String, Object>();
            othersData.put("name", "Others");
            othersData.put("ctId", "");
            othersData.put("y", totalOthersCount);
            compTypeWiseData.add(othersData);
        }
        return compTypeWiseData;
    }

    public List<List<Map<String, Object>>> getWardwisePerformance() {
        final DateTime currentDate = new DateTime();
        final List<Object[]> wardwisePerformanceData = dashboardRepository.fetchWardwisePerformanceTill(currentDate);
        final List<List<Map<String, Object>>> datas = new LinkedList<>();
        datas.add(performanceAnalysis(wardwisePerformanceData, currentDate));
        datas.add(performanceProjection(wardwisePerformanceData, currentDate));
        return datas;
    }

    public List<List<Object>> getAgeingByWard(final String wardName) {
        return getAgeingData("pgr.wardwise.ageing", wardName);
    }

    private List<Map<String, Object>> performanceProjection(final List<Object[]> wardwisePerformanceData,
            final DateTime currentDate) {
        final DecimalFormat df = new DecimalFormat("####0.00");
        final List<Map<String, Object>> compAggrData = new ArrayList<Map<String, Object>>();
        for (final Object[] compData : wardwisePerformanceData) {
            final Map<String, Object> complaintData = new HashMap<String, Object>();
            complaintData.put("name", compData[0]);
            final BigInteger compData1 = (BigInteger) compData[1];
            final BigInteger compData3 = (BigInteger) compData[3];
            final BigInteger compData4 = (BigInteger) compData[4];
            final double noOfCompAsOnDate = compData1.doubleValue();
            final double noOfCompReceivedBtw = compData3.doubleValue();
            final double noOfCompPenAsonDate = compData4.doubleValue();
            complaintData.put("y", new BigDecimal(df.format(100 * (noOfCompAsOnDate + noOfCompReceivedBtw - noOfCompPenAsonDate)
                    / (noOfCompAsOnDate + noOfCompReceivedBtw))));
            compAggrData.add(complaintData);
        }

        // SORT ZONEWISE PERFORMANCE BY REDRESSAL %
        sortData(compAggrData, "y");
        return compAggrData;
    }

    public Map<String, Object> topComplaints() {
        final DateTime currentDate = new DateTime();

        final List<Object> dataHolder5 = constructListOfMonthPlaceHolder(currentDate.minusMonths(6), currentDate, "MMM");
        List<Object[]> topFiveCompTypeData = dashboardRepository.fetchTopComplaintsBetween(
                startOfGivenDate(currentDate.minusMonths(6).withDayOfMonth(1)).toDate(), endOfGivenDate(currentDate).toDate());
        List<Object> dataHolder = new LinkedList<Object>();
        int index = 0;
        Map<String, Object> data = new HashMap<String, Object>();
        List<Integer> compCount = new ArrayList<Integer>();
        for (final Object[] top5CompType : topFiveCompTypeData) {
                if (index < 5) {
                        compCount.add(((BigDecimal) top5CompType[1]).intValue());
                        index++;
                } else {
                        compCount.add(((BigDecimal) top5CompType[1]).intValue());
                        data.put("name", String.valueOf(top5CompType[2]));
                        data.put("data", new LinkedList<Integer>(compCount));
                        dataHolder.add(new LinkedHashMap<String, Object>(data));
                        index = 0;
                        compCount.clear();
                        data.clear();
                }
        }
        Map<String, Object> topFiveCompDataHolder = new LinkedHashMap<String, Object>();
        topFiveCompDataHolder.put("year", dataHolder5);
        topFiveCompDataHolder.put("series", dataHolder);

        return topFiveCompDataHolder;
}
    
    private List<List<Object>> getAgeingData(final String querykey, final String wardName) {
        final Object[] compData = dashboardRepository.fetchComplaintAgeing(querykey, wardName);
        final List<Object> cntabv90 = new LinkedList<Object>();
        cntabv90.add("&gt; 90 Days");
        cntabv90.add(((BigInteger) compData[0]).intValue());
        final List<Object> cntbtw45to90 = new LinkedList<Object>();
        cntbtw45to90.add("90-45 Days");
        cntbtw45to90.add(((BigInteger) compData[1]).intValue());
        final List<Object> cntbtw15to45 = new LinkedList<Object>();
        cntbtw15to45.add("44-15 Days");
        cntbtw15to45.add(((BigInteger) compData[2]).intValue());
        final List<Object> cntlsthn15 = new LinkedList<Object>();
        cntlsthn15.add("&lt; 15 Days");
        cntlsthn15.add(((BigInteger) compData[3]).intValue());
        final List<List<Object>> dataHolder = new LinkedList<List<Object>>();
        dataHolder.add(cntabv90);
        dataHolder.add(cntbtw45to90);
        dataHolder.add(cntbtw15to45);
        dataHolder.add(cntlsthn15);

        return dataHolder;
    }

    private List<Map<String, Object>> performanceAnalysis(final List<Object[]> wardwisePerformanceData,
            final DateTime currentDate) {
        final List<Map<String, Object>> compAggrData = new ArrayList<Map<String, Object>>();
        final String formattedFrm = endOfGivenDate(currentDate.minusDays(14)).toString(DFLT_DATE_FRMTR);
        final String formattedDayAfterFrm = startOfGivenDate(currentDate.minusDays(13)).toString(DFLT_DATE_FRMTR);
        final String formattedTo = currentDate.toString(DFLT_DATE_FRMTR);
        final DecimalFormat df = new DecimalFormat("####0.00");
        for (final Object[] compData : wardwisePerformanceData) {
            final Map<String, Object> complaintData = new HashMap<String, Object>();
            complaintData.put("zone", compData[0]);
            final BigInteger compData1 = (BigInteger) compData[1];
            final BigInteger compData3 = (BigInteger) compData[3];
            final BigInteger compData4 = (BigInteger) compData[4];
            final double noOfCompAsOnDate = compData1.doubleValue();
            final double noOfCompReceivedBtw = compData3.doubleValue();
            final double noOfCompPenAsonDate = compData4.doubleValue();
            complaintData.put("dateAsOn2WeekBack", formattedFrm);
            complaintData.put("noOfCompAsOnDate", noOfCompAsOnDate);
            complaintData.put("dateAsOnDayAfter", formattedDayAfterFrm);
            complaintData.put("noOfCompReceivedBtw", noOfCompReceivedBtw);
            complaintData.put("dateAsOn", formattedTo);
            complaintData.put("noOfCompPenAsonDate", noOfCompPenAsonDate);
            complaintData.put("disposalPerc", df.format(100 * (noOfCompAsOnDate + noOfCompReceivedBtw - noOfCompPenAsonDate)
                    / (noOfCompAsOnDate + noOfCompReceivedBtw)));
            complaintData.put("lat", compData[6]);
            complaintData.put("lng", compData[7]);
            complaintData.put("zoneId", compData[8]);
            compAggrData.add(complaintData);
        }

        // SORT ZONEWISE PERFORMANCE BY REDRESSAL %
        sortData(compAggrData, "disposalPerc");

        // ASSIGN A RANK BASED ON ORDER
        assignRank(compAggrData, "rank");
        return compAggrData;
    }

    public List<List<Object>> getComplaintSLA() {
        return getAgeingData("pgr.comp.count.sla.breakup", null);
    }

    public List<Map<String, Object>> getOpenComplaintSLA() {
        final DateTime startOfTheYear = new LocalDate().minusYears(1).toDateTimeAtStartOfDay();
        final DateTime tillDate = LocalTime.MIDNIGHT.toDateTimeToday();
        final List<Object[]> openComplaints = dashboardRepository.fetchOpenComplaintAggregateBetween(startOfTheYear, tillDate);

        final List<Map<String, Object>> compAggrData = new ArrayList<Map<String, Object>>();
        final Map<String, Object> complaintData = new HashMap<String, Object>();
        String lastZone = null;
        double regComplaint = 0;
        double openFrm90Days = 0;
        double totalOpen = 0;
        double pecentage = 0;
        final Date dateBefore90Days = new LocalDate().minusDays(90).toDateTimeAtStartOfDay().toDate();
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        final String formattedFrm = startOfTheYear.toString(dtf);
        final String formattedTo = tillDate.toString(dtf);
        for (final Object[] compData : openComplaints) {
            final String statusName = String.valueOf(compData[4]);
            final long count = Long.valueOf(String.valueOf(compData[5]));
            if ("REGISTERED FORWARDED PROCESSING REOPENED".contains(statusName)) {
                if (((Date) compData[6]).before(dateBefore90Days))
                    openFrm90Days += count;
                totalOpen += count;
            }
            regComplaint += count;
            complaintData.put("lat", compData[3]);
            complaintData.put("lng", compData[2]);
            complaintData.put("startDt", formattedFrm);
            complaintData.put("endDt", formattedTo);
            complaintData.put("zoneID", compData[1]);
            final String currentZone = String.valueOf(compData[0]);
            if (openComplaints.size() == 1 || lastZone != null && !currentZone.equals(lastZone)) {
                pecentage = Math.round(100 * (totalOpen / regComplaint));
                complaintData.put("pecentage", pecentage);
                complaintData.put("regComp", regComplaint);
                complaintData.put("open90Comp", openFrm90Days);
                complaintData.put("openComp", totalOpen);
                complaintData.put("zone", lastZone);
                compAggrData.add(new HashMap<String, Object>(complaintData));
                pecentage = 0;
                regComplaint = 0;
                totalOpen = 0;
                openFrm90Days = 0;
            }
            lastZone = currentZone;
        }

        // SORT BASED ON TOTAL NO. OF OPEN COMP > 90
        sortData(compAggrData, "open90Comp");

        return compAggrData;
    }

    public List<Map<String, Object>> getWardwiseComplaintByComplaintType(final Long complaintTypeId,
            final String currentChartColor) {
        final DateTime currentDate = new DateTime();
        final List<Map<String, Object>> wardWiseData = new LinkedList<Map<String, Object>>();
        double topCount = -1;
        for (final Object[] complaint : dashboardRepository.fetchComplaintsByComplaintTypeGroupByWard(complaintTypeId,
                startOfGivenDate(currentDate.minusMonths(6).withDayOfMonth(1).withTimeAtStartOfDay()),
                endOfGivenDate(currentDate))) {
            final Map<String, Object> wardwiseCnt = new HashMap<String, Object>();
            wardwiseCnt.put("wardName", String.valueOf(complaint[0]));
            wardwiseCnt.put("wardId", ((BigInteger) complaint[1]).intValue());
            final double count = ((BigInteger) complaint[2]).doubleValue();
            wardwiseCnt.put("count", count);
            if (topCount == -1)
                topCount = count;
            final double perc = count * 100 / topCount;
            final String[] colors = COLOR_GRADIENTS.get(currentChartColor);
            if (perc <= 20)
                wardwiseCnt.put("color", colors[4]);
            else if (perc > 20.0 && perc <= 40.0)
                wardwiseCnt.put("color", colors[3]);
            else if (perc > 40.0 && perc <= 60.0)
                wardwiseCnt.put("color", colors[2]);
            else if (perc > 60.0 && perc <= 80.0)
                wardwiseCnt.put("color", colors[1]);
            else
                wardwiseCnt.put("color", colors[0]);

            wardWiseData.add(wardwiseCnt);
        }
        return wardWiseData;
    }

    private static Map<String, Integer> constructDatePlaceHolder(final DateTime startDate, final DateTime endDate,
            final String pattern) {
        final Map<String, Integer> currentYearTillDays = new LinkedHashMap<String, Integer>();
        for (DateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1))
            currentYearTillDays.put(date.toString(pattern), Integer.valueOf(0));
        currentYearTillDays.put(endDate.toString(pattern), Integer.valueOf(0));
        return currentYearTillDays;
    }

    private static List<Map<String, Object>> constructMonthPlaceHolder(final DateTime startDate, final DateTime endDate,
            final String pattern) {
        final List<Map<String, Object>> dataHolder = new LinkedList<Map<String, Object>>();
        for (DateTime date = startDate; date.isBefore(endDate) || date.isEqual(endDate); date = date.plusMonths(1)) {
            final Map<String, Object> currentYearTillDays = new LinkedHashMap<String, Object>();
            currentYearTillDays.put("name", date.toString(pattern));
            currentYearTillDays.put("y", Double.valueOf(0));
            dataHolder.add(currentYearTillDays);
        }
        return dataHolder;
    }
    
    public static List<Object> constructListOfMonthPlaceHolder(final DateTime startDate, final DateTime endDate, final String pattern) {
        final List<Object> dataHolder = new LinkedList<Object>();
                for (DateTime date = startDate; date.isBefore(endDate); date = date.plusMonths(1)) {
                        dataHolder.add(date.toString(pattern));
        }
        return dataHolder;
    }

    private static void sortData(final List<Map<String, Object>> dataList, final String key) {
        Collections.sort(dataList, (map1, map2) -> {
            return Double.valueOf(map1.get(key).toString()) <= Double.valueOf(map2.get(key).toString()) ? 1 : -1;
        });
    }

    private static void assignRank(final List<Map<String, Object>> dataList, final String key) {
        int counter = 1;
        for (final Map<String, Object> map : dataList)
            map.put(key, counter++);
    }
}
