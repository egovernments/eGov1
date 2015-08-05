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
package org.egov.wtms.utils.constants;

public class WaterTaxConstants {

    public static final String MODULE_NAME = "Water Tax Management";
    public static final String DASH_DELIM = "-";
    public static final String APPROVED = "APPROVED";
    public static final String CREATEWORKFLOWSTATE = "CREATED";
    public static final String APPLICATION_NUMBER = "applicationNumber";
    public static final String NEWCONNECTION = "NEWCONNECTION";
    public static final String METERED = "Metered";
    public static final String NON_METERED = "Non-metered";
    public static final String EGMODULES_NAME = "Water Tax";
    public static final String EGMODULE_NAME = "Water Tax Management";
    public static final String WATERTAX_CONNECTION_CHARGE = "WTAXCONCHARGE";
    public static final String WATERTAX_SECURITY_CHARGE = "WTAXSECURITY";
    public static final String WATERTAX_DONATION_CHARGE = "WTAXDONATION";
    public static final String CONNECTION_FEE = "Connection fee";
    public static final String ADDNLCONNECTION = "ADDNLCONNECTION";
    public static final String CHANGEOFUSE = "CHANGEOFUSE";
    public static final String SMSEMAILTYPEADDITONALCONNCREATE = "additionalconncreate";
    public static final String SMSEMAILTYPEADDITONALCONNAPPROVE = "additionalconnapprove";
    public static final String SMSEMAILTYPENEWCONNAPPROVE = "newconnapprove";
    public static final String SMSEMAILTYPENEWCONNCREATE = "newconncreate";
    public static final String CATEGORY_BPL = "BPL";

    // User Roles
    public static final String CSCOPERTAORROLE = "CSC Operator";
    public static final String CLERKULB = "ULB Operator";
    public static final String APPROVERROLE = "Water Tax Approver";
    public static final String SUPERUSER = "Super User";

    // Rest API constants
    public static final String CURR_DMD_STR = "CURR_DMD";
    public static final String ARR_DMD_STR = "ARR_DMD";
    public static final String CURR_COLL_STR = "CURR_COLL";
    public static final String ARR_COLL_STR = "ARR_COLL";

    public static final String CONSUMERCODE_NOT_EXIST_ERR_CODE = "WTAX100";
    public static final String WTAXDETAILS_PROPERTYID_NOT_EXIST_ERR_MSG_PREFIX = "Water Connection details with Assessment Number ";
    public static final String PROPERTYID_NOT_EXIST_ERR_CODE = "WTAX101";
    public static final String WTAXDETAILS_CONSUMER_CODE_NOT_EXIST_ERR_MSG_PREFIX = "Water Connection details with Consumer code ";
    public static final String WTAXDETAILS_NOT_EXIST_ERR_MSG_SUFFIX = " does not exist";

    public static final String BILLTYPE_AUTO = "AUTO";
    public static final String BILLTYPE_MANUAL = "MANUAL";
    public static final String CITIZENUSER = "9999999999";
    public static final String BILLTYPE_ONLINE = "ONLINE";
    
    public static final String WF_STATE_REJECTED = "Rejected";
    public static final String WFLOW_ACTION_STEP_REJECT = "Reject";
    public static final String WF_STATE_REVENUE_CLERK_APPROVAL_PENDING = "Revenenu Clerk Approval Pending";
}
