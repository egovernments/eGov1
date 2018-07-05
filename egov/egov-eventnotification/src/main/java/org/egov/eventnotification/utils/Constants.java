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
package org.egov.eventnotification.utils;

public final class Constants {

    public static final String MODULE_NAME = "Eventnotification";

    public static final String EVENT_ID = "id";
    public static final String NAME = "name";
    public static final String EVENT_HOST = "eventhost";

    public static final String MODULE_ID = "moduleId";
    public static final String CATEGORY_ID = "id";
    public static final String PARAMETER_ID = "id";
    public static final String DRAFT_NOTIFICATION_TYPE = "draftType";
    public static final String DRAFT_ID = "id";

    public static final String DDMMYYYY = "dd/MM/yyyy";

    public static final String EVENT_EVENTNAME = "eventName";
    public static final String EVENT_EVENTHOST = "eventHost";

    public static final String EVENT_LIST = "eventList";
    public static final String EVENT_TYPE_LIST = "eventTypeList";
    public static final String MODE = "mode";
    public static final String NOTIFICATION_MESSAGE = "message";

    public static final String MODE_VIEW = "view";
    public static final String MODE_UPDATE = "update";
    public static final String MODE_CREATE = "create";
    public static final String MODE_DELETE = "delete";

    public static final String EVENT = "event";
    public static final String NOTIFICATION_DRAFT = "notificationDraft";
    public static final String FILE = "file";
    public static final String DRAFT = "draft";
    public static final String TEMPLATE_MODULE = "TemplateModule";
    public static final String MODULE_CATEGORY = "ModuleCategory";
    public static final String CATEGORY_PARAMETERS = "CategoryParameters";

    public static final String HOUR_LIST = "hourList";
    public static final String MINUTE_LIST = "minuteList";

    public static final String API_UPDATE_ID = "update/{id}";

    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String TOTAL_PAGES = "totalPages";

    public static final String INACTIVE = "Inactive";
    public static final String EVENT_STATUS_LIST = "eventStatusList";
    public static final String STATUS = "Status";
    public static final String USERID = "userid";
    public static final String EVENTID = "eventid";
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String STATUS_COLUMN = "status";
    public static final String EVENT_DATE_TYPE = "eventDateType";
    public static final String UPCOMING = "upcoming";
    public static final String ONGOING = "ongoing";
    public static final String NOTIFICATION_TYPE_EVENT = "Event";
    public static final String DRAFT_LIST = "draftList";
    public static final String SCHEDULED_STATUS = "scheduled";
    public static final String NOTIFICATION_JOB = "notificationJob";
    public static final String SCHEDULE = "Schedule";
    public static final String NOTIFICATION_SCHEDULE = "notificationSchedule";
    public static final String SCHEDULER_REPEAT_LIST = "repeatList";
    public static final String USER = "user";
    public static final String SCHEDULEID = "scheduleId";
    public static final String YES = "Yes";
    public static final String SCHEDULE_RUNNING = "Running";
    public static final String SCHEDULE_COMPLETE = "Complete";

    public static final String MSG_SCHEDULED_DELETE_ERROR = "msg.notification.schedule.delete.error";
    public static final String NOTIFICATION_TYPE = "notice";
    public static final String BUSINESS_NOTIFICATION_TYPE = "BUSINESS";
    public static final String DEFAULTERS_LIST = "defaultersList";
    public static final String JOB_TYPE = "jobType";
    public static final String DAILY_JOB_TYPE = "Daily Job";
    public static final String MONTHLY_JOB_TYPE = "Monthly Job";
    public static final String YEARLY_JOB_TYPE = "Yearly Job";
    public static final String ONETIME_JOB_TYPE = "One Tome Job";
    public static final String SCHEDULE_SERVICE = "scheduleService";
    public static final String PUSH_NOTIFICATION_SERVICE = "pushNotificationService";
    public static final String USER_SERVICE = "userService";
    public static final String MESSAGE_USERNAME = "{{userName}}";
    public static final String MESSAGE_PROPTNO = "{{propertyNumber}}";
    public static final String MESSAGE_DUEDATE = "{{dueDate}}";
    public static final String MESSAGE_ASMNTNO = "{{assessmentNumber}}";
    public static final String MESSAGE_DUEAMT = "{{dueAmount}}";
    public static final String MESSAGE_CONSNO = "{{consumerNumber}}";
    public static final String MESSAGE_BILLNO = "{{billNumber}}";
    public static final String MESSAGE_BILLAMT = "{{billAmount}}";
    public static final String MESSAGE_DISRPTDATE = "{{disruptionDate}}";
    public static final String ERROR_PROCESS_REQUEST = "error.process.request";
    public static final String ERROR = "Error";
    public static final String MODULE = "module";
    public static final String CATEGORY = "category";
    public static final String SUCCESS1 = "Success";
    public static final int MIN_NUMBER_OF_REQUESTS = 1;
    public static final int ZERO = 0;
    public static final String MINUTES_CRON = "{minutes}";
    public static final String HOURS_CRON = "{hours}";
    public static final String DAY_CRON = "{day}";
    public static final String MONTH_CRON = "{month}";

    public static final String PROPERTY_MODULE = "Property";
    public static final String WATER_CHARGES_MODULE = "Water Charges";

    public static final String BMA_INTERFACE_SUFFIX = "BuildMessageAdapter";
    public static final String ACTIVE = "Active";
    public static final String INTERESTED_COUNT = "interestedCount";

    public static final double DOUBLE_DEFAULT = 0.0;
    public static final String URL = "url";
    public static final String NO = "No";
    public static final String EVENT_FILENAME = "fileName";
    public static final String EVENT_FILESTOREID = "fileStoreId";
    public static final String EVENT_COST = "cost";
    public static final int MAX_TEN = 10;
    public static final String MESSAGE = "message";
    public static final String EVENT_NOTIFICATION_GROUP = "EVENT_NOTIFICATION_GROUP";
    public static final String TRIGGER = "eventNotificationTrigger";
    public static final String JOB = "eventNotificationJob";
    public static final String BEANNOTIFSCH = "eventnotificationScheduler";

    public static final String CONTEXTURL = "contextURL";

    public static final String USER_ID = "userId";
    public static final String USER_TOKEN_ID = "userToken";
    public static final String USER_DEVICE_ID = "deviceId";

    public static final String DESCRIPTION = "description";
    public static final String START_DATE = "startDate";
    public static final String START_TIME = "startTime";
    public static final String END_DATE = "endDate";
    public static final String END_TIME = "endTime";
    public static final String EVENT_LOC = "eventlocation";
    public static final String ADDRESS = "address";
    public static final String CONTACT_NO = "contactnumber";
    public static final String ISPAID = "ispaid";
    public static final String EVENTTYPE = "eventType";
    public static final String USER_INTERESTED = "userInterested";
    public static final String VIEWNAME = "error/500";
    public static final String ALTERROR = "altError";

    private Constants() {

    }
}
