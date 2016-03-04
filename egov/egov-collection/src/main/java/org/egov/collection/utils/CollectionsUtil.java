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

package org.egov.collection.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.Challan;
import org.egov.collection.entity.OnlinePayment;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.integration.models.BillReceiptInfoImpl;
import org.egov.collection.integration.models.ReceiptAmountInfo;
import org.egov.collection.integration.services.BillingIntegrationService;
import org.egov.commons.CFinancialYear;
import org.egov.commons.EgwStatus;
import org.egov.commons.Fund;
import org.egov.commons.dao.ChartOfAccountsHibernateDAO;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.service.CommonsService;
import org.egov.eis.entity.Assignment;
import org.egov.eis.entity.EmployeeView;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.DesignationService;
import org.egov.eis.service.EisCommonService;
import org.egov.eis.service.EmployeeService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.Location;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.entity.Role;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.exception.NoSuchObjectException;
import org.egov.infra.script.entity.Script;
import org.egov.infra.search.elastic.entity.CollectionIndex;
import org.egov.infra.search.elastic.entity.CollectionIndexBuilder;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infstr.models.ServiceDetails;
import org.egov.infstr.services.EISServeable;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.contra.ContraJournalVoucher;
import org.egov.pims.commons.Designation;
import org.egov.pims.commons.Position;
import org.egov.pims.model.PersonalInformation;
import org.egov.pims.service.SearchPositionService;
import org.egov.pims.utils.EisManagersUtill;
import org.hibernate.Query;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class CollectionsUtil {
    private final Map<String, EgwStatus> statusMap = new HashMap<String, EgwStatus>(0);
    public static final SimpleDateFormat CHEQUE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private PersistenceService persistenceService;
    @Autowired
    private UserService userService;
    private CommonsService commonsService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private AppConfigValueService appConfigValuesService;
    @Autowired
    private EisCommonService eisCommonService;
    private EISServeable eisService;
    @Autowired
    private SearchPositionService searchPositionService;
    @Autowired
    private ApplicationContext context;
    private static final Logger LOGGER = Logger.getLogger(CollectionsUtil.class);
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private PositionMasterService posService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    protected AssignmentService assignmentService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DesignationService designationService;
    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
    @Autowired
    private ChartOfAccountsHibernateDAO chartOfAccountsHibernateDAO;

    /**
     * Returns the Status object for given status code for a receipt
     *
     * @param statusCode Status code for which status object is to be returned
     * @return the Status object for given status code for a receipt
     */
    public EgwStatus getReceiptStatusForCode(final String statusCode) {
        EgwStatus status = statusMap.get(statusCode);

        synchronized (this) {
            if (status == null) {
                // Status not yet cached. Get it from DB and cache it
                status = getStatusForModuleAndCode(CollectionConstants.MODULE_NAME_RECEIPTHEADER, statusCode);

                if (status != null)
                    statusMap.put(statusCode, status);
            }
        }

        return status;
    }
   

    /**
     * This method returns the <code>EgwStatus</code> for the given module type and status code
     *
     * @param moduleName Module name of the required status
     * @param statusCode Status code of the required status
     * @return the <code>EgwStatus</code> instance
     */
    public EgwStatus getStatusForModuleAndCode(final String moduleName, final String statusCode) {

        final EgwStatus status = egwStatusDAO.getStatusByModuleAndCode(moduleName, statusCode);
        return status;
    }

    /**
     * @param sessionMap Map of session variables
     * @return user name of currently logged in user
     */
    public String getLoggedInUserName() {
        return securityUtils.getCurrentUser().getName();
    }

    /**
     * This method returns the User instance associated with the logged in user
     *
     * @param sessionMap Map of session variables
     * @return the logged in user
     */
    public User getLoggedInUser() {
        return securityUtils.getCurrentUser();
    }

    /**
     * @param user the user whose department is to be returned
     * @return department of the given user
     */
    public Department getDepartmentOfUser(final User user) {
        return eisCommonService.getDepartmentForUser(user.getId());
    }

    /**
     * @param sessionMap map of session variables
     * @return department of currently logged in user
     */
    public Department getDepartmentOfLoggedInUser() {
        final User user = securityUtils.getCurrentUser();
        return getDepartmentOfUser(user);
    }

    /**
     * This method returns the User instance for the userName passed as parameter
     *
     * @param userName
     * @return User
     */
    public User getUserByUserName(final String userName) {
        return userService.getUserByUsername(userName);
    }

    /**
     * @param sessionMap Map of session variables
     * @return Location object for given user
     */
    public Location getLocationOfUser(final Map<String, Object> sessionMap) {
        Location location = null;
        try {
            location = getLocationById(Long.valueOf((String) sessionMap
                    .get(CollectionConstants.SESSION_VAR_LOGIN_USER_LOCATIONID)));
            if (location == null)
                throw new ApplicationRuntimeException("Unable to fetch the location of the logged in user ["
                        + (String) sessionMap.get(CollectionConstants.SESSION_VAR_LOGIN_USER_NAME) + "]");
        } catch (final Exception exp) {
            final String errorMsg = "Unable to fetch the location of the logged in user ["
                    + (String) sessionMap.get(CollectionConstants.SESSION_VAR_LOGIN_USER_NAME) + "]";
            LOGGER.error(errorMsg, exp);
            throw new ApplicationRuntimeException(errorMsg, exp);
        }
        return location;
    }

    public Location getLocationById(final Long locationId) {
        return (Location) persistenceService.findByNamedQuery(CollectionConstants.QUERY_GET_LOCATIONBYID, locationId);
    }

    /**
     * @return list of all active counters
     */
    public List getAllCounters() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_ALLCOUNTERS);
    }

    /**
     * @return list of all active counters
     */
    public List getActiveCounters() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_ACTIVE_COUNTERS);
    }

    /**
     * @return List of all collection services (service type = B (Billing) or C (Challan)
     */
    public List getCollectionServiceList() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_COLLECTION_SERVICS);
    }

    /**
     * @return List of all collection services (service type = C)
     */
    public List getChallanServiceList() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_SERVICES_BY_TYPE,
                CollectionConstants.SERVICE_TYPE_COLLECTION);
    }

    /**
     * @return List of all billing services
     */
    public List getBillingServiceList() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_SERVICES_BY_TYPE,
                CollectionConstants.SERVICE_TYPE_BILLING);
    }

    /**
     * @return list of all users who have created at least one receipt
     */
    public List getReceiptCreators() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_CREATEDBYUSERS_OF_RECEIPTS);
    }

    /**
     * @return list of all zones that have receipts created
     */
    public List getReceiptZoneList() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_ZONE_OF_RECEIPTS);
    }

    /**
     * This method returns the collection modes that are not allowed based on rules configured in the script
     *
     * @param loggedInUser a <code>User</code> entity representing the logged in user.
     * @return a <code>List</code> of <code>String</code> values representing the mode of payments supported.
     */
    public List<String> getCollectionModesNotAllowed(final User loggedInUser) {
        final List<String> collectionsModeNotAllowed = new ArrayList<String>(0);
        final List<AppConfigValues> deptCodesApp = appConfigValuesService
                .getConfigValuesByModuleAndKey(CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                        CollectionConstants.COLLECTION_DEPARTMENT_COLLMODES);
        final List<String> deptCodes = new ArrayList<String>();
        for (final AppConfigValues deptCode : deptCodesApp)
            deptCodes.add(deptCode.getValue());
        Department dept = null;
        final Boolean isEmp = isEmployee(loggedInUser);
        if (isEmp)
            dept = getDepartmentOfUser(loggedInUser);
        if (isEmp && dept == null) {
            final List<ValidationError> validationErrors = new ArrayList<ValidationError>(0);
            validationErrors.add(new ValidationError("Department", "billreceipt.counter.deptcode.null"));
        } else if (!isEmp || dept != null && !deptCodes.isEmpty() && deptCodes.contains(dept.getCode())) {
            collectionsModeNotAllowed.add(CollectionConstants.INSTRUMENTTYPE_CARD);
            collectionsModeNotAllowed.add(CollectionConstants.INSTRUMENTTYPE_BANK);
        } else {
            collectionsModeNotAllowed.add(CollectionConstants.INSTRUMENTTYPE_CASH);
            collectionsModeNotAllowed.add(CollectionConstants.INSTRUMENTTYPE_CARD);
            collectionsModeNotAllowed.add(CollectionConstants.INSTRUMENTTYPE_BANK);
        }
        return collectionsModeNotAllowed;
    }

    public Boolean isEmployee(final User user) {
        for (final Role role : user.getRoles())
            for (final AppConfigValues appconfig : getThirdPartyUserRoles())
                if (role != null && role.getName().equals(appconfig.getValue()))
                    return false;
        return true;
    }

    public List<AppConfigValues> getThirdPartyUserRoles() {

        final List<AppConfigValues> appConfigValueList = appConfigValuesService.getConfigValuesByModuleAndKey(
                CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG, CollectionConstants.COLLECTION_ROLEFORNONEMPLOYEE);
        return !appConfigValueList.isEmpty() ? appConfigValueList : null;

    }

    public String getDepartmentForWorkFlow() {
        String department = "";
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG, CollectionConstants.COLLECTION_WORKFLOWDEPARTMENT);
        if (null != appConfigValue && !appConfigValue.isEmpty())
            department = appConfigValue.get(0).getValue();
        return department;
    }

    public Position getPositionByDeptDesgAndBoundary(final Boundary boundary) {
        final String designationStr = getDesignationForThirdPartyUser();
        final String departmentStr = getDepartmentForWorkFlow();
        final String[] department = departmentStr.split(",");
        final String[] designation = designationStr.split(",");
        List<Assignment> assignment = new ArrayList<Assignment>();
        for (final String dept : department) {
            for (final String desg : designation) {
                assignment = assignmentService.findByDepartmentDesignationAndBoundary(departmentService
                        .getDepartmentByName(dept).getId(), designationService.getDesignationByName(desg).getId(),
                        boundary.getId());
                if (!assignment.isEmpty())
                    break;
            }
            if (!assignment.isEmpty())
                break;
        }
        return !assignment.isEmpty() ? assignment.get(0).getPosition() : null;
    }

    public String getDesignationForThirdPartyUser() {
        String designation = "";
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                CollectionConstants.COLLECTION_DESIGNATIONFORCSCOPERATOR);
        if (null != appConfigValue && !appConfigValue.isEmpty())
            designation = appConfigValue.get(0).getValue();
        return designation;
    }

    /**
     * @param sessionMap Map of session variables
     * @return Position of logged in user
     */
    public Position getPositionOfUser(final User user) {
        return posService.getCurrentPositionForUser(user.getId());
    }

    /**
     * Gets position by given position name
     *
     * @param positionName Position name
     * @return Position object for given position name
     */
    public Position getPositionByName(final String positionName) {
        return posService.getPositionByName(positionName);
    }

    /**
     * This method retrieves the <code>CFinancialYear</code> for the given date.
     *
     * @param date an instance of <code>Date</code> for which the financial year is to be retrieved.
     * @return an instance of <code></code> representing the financial year for the given date
     */
    public CFinancialYear getFinancialYearforDate(final Date date) {
        return (CFinancialYear) persistenceService
                .getSession()
                .createQuery(
                        "from CFinancialYear cfinancialyear where ? between "
                                + "cfinancialyear.startingDate and cfinancialyear.endingDate").setDate(0, date).list()
                                .get(0);
    }

    /**
     * This method checks if the given challan is valid.
     *
     * @param challan the <code>Challan</code> instance whose validity has to be checked
     * @return a boolean value - true indicating that the challan is valid and false indicating that teh challan is not valid
     */
    public boolean checkChallanValidity(final Challan challan) {
        final Calendar current = Calendar.getInstance();
        current.clear(Calendar.HOUR_OF_DAY);
        current.clear(Calendar.MINUTE);
        current.clear(Calendar.SECOND);
        current.clear(Calendar.MILLISECOND);

        final Calendar validityStart = Calendar.getInstance();
        validityStart.setTime(challan.getChallanDate());
        validityStart.clear(Calendar.HOUR_OF_DAY);
        validityStart.clear(Calendar.MINUTE);
        validityStart.clear(Calendar.SECOND);
        validityStart.clear(Calendar.MILLISECOND);

        final Calendar validityEnd = Calendar.getInstance();
        validityEnd.setTime(challan.getValidUpto());
        validityEnd.clear(Calendar.HOUR_OF_DAY);
        validityEnd.clear(Calendar.MINUTE);
        validityEnd.clear(Calendar.SECOND);
        validityEnd.clear(Calendar.MILLISECOND);

        if (validityStart.compareTo(current) <= 0 && validityEnd.compareTo(current) >= 0)
            return true;
        return false;
    }

    public void setScriptService(final PersistenceService<Script, Long> scriptService) {
    }

    /**
     * Fetches given bean from application context
     *
     * @param beanName name of bean to be fetched
     * @return given bean from application context
     */
    public Object getBean(final String beanName) {

        Object bean = null;
        try {
            bean = context.getBean(beanName);
            LOGGER.debug(" Got bean : " + beanName);
        } catch (final BeansException e) {
            final String errorMsg = "Could not locate bean [" + beanName + "]";
            LOGGER.error(errorMsg, e);
            throw new ApplicationRuntimeException(errorMsg, e);
        }
        return bean;
    }

    /**
     * This method returns the currently active config value for the given module name and key
     *
     * @param moduleName a <code>String<code> representing the module name
     * @param key a <code>String</code> representing the key
     * @param defaultValue Default value to be returned in case the key is not defined
     * @return <code>String</code> representing the configuration value
     */
    public String getAppConfigValue(final String moduleName, final String key, final String defaultValue) {
        final AppConfigValues configVal = appConfigValuesService.getAppConfigValueByDate(moduleName, key, new Date());
        return configVal == null ? defaultValue : configVal.getValue();
    }

    /**
     * This method returns the config value for the given module name and key
     *
     * @param moduleName a <code>String<code> representing the module name
     * @param key a <code>String</code> representing the key
     * @return <code>String</code> representing the configuration value
     */
    public String getAppConfigValue(final String moduleName, final String key) {
        final List<AppConfigValues> appConfValues = appConfigValuesService.getConfigValuesByModuleAndKey(moduleName,
                key);
        if (appConfValues != null && appConfValues.size() > 0)
            return appConfValues.get(0).getValue();
        else
            return "";
    }

    /**
     * This method returns the list of config values for the given module name and key
     *
     * @param moduleName a <code>String<code> representing the module name
     * @param key a <code>String</code> representing the key
     * @return <code>List<AppConfigValues></code> representing the list of configuration values
     */
    public List<AppConfigValues> getAppConfigValues(final String moduleName, final String key) {
        return appConfigValuesService.getConfigValuesByModuleAndKey(moduleName, key);
    }

    /**
     * Gets position by given position id
     *
     * @param positionId Position Id
     * @return Position object for given position id
     */
    public Position getPositionById(final Long positionId) {
        return posService.getPositionById(positionId);
    }

    /**
     * This method is invoked from the ReceiptHeader.workFlow script and returns the position for the employee id passed as
     * parameter
     *
     * @param employeeId PersonalInformation Id
     * @return Position object for Employee Id passed as parameter
     */

    public Position getPositionforEmp(final Integer employeeId) {
        return EisManagersUtill.getEmployeeService().getPositionforEmp(employeeId);
    }

    /**
     * This method is invoked from the ReceiptHeader.workFlow script and returns Employee object for the given Department Id,
     * Designation Id ,Boundary Id and FunctionaryId
     *
     * @param deptId Department Id
     * @param designationId Designation Id
     * @param boundaryId Boundary Id
     * @param functionaryId Functionary Id
     * @return PersonalInformation
     */

    public PersonalInformation getEmployeeByDepartmentDesignationBoundaryandFunctionary(final Long deptId,
            final Long designationId, final Integer boundaryId, final Integer functionaryId) {
        PersonalInformation personalInformation = null;
        try {
            personalInformation = EisManagersUtill.getEmployeeService().getEmployeeByFunctionary(deptId, designationId,
                    Long.valueOf(boundaryId), functionaryId);
        } catch (final Exception e) {
            final String errorMsg = "Could not get PersonalInformation";
            LOGGER.error(errorMsg, e);
            throw new ApplicationRuntimeException(errorMsg, e);
        }
        return personalInformation;
    }

    /**
     * @param sessionMap
     * @return
     */

    public List<Department> getAllNonPrimaryAssignmentsOfLoggedInUser() {
        return getAllNonPrimaryAssignmentsOfUser(getLoggedInUser());
    }

    /**
     * @param user the user whose non-primary department list is to be returned
     * @return list of non-primary department of the given user
     */
    public List<Department> getAllNonPrimaryAssignmentsOfUser(final User user) {
        final List<Department> departmentlist = new ArrayList<Department>();
        try {
            final HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("code", EisManagersUtill.getEmployeeService().getEmpForUserId(user.getId()).getCode());
            final List<EmployeeView> employeeViewList = (List<EmployeeView>) eisService.getEmployeeInfoList(paramMap);
            if (!employeeViewList.isEmpty())
                for (final EmployeeView employeeView : employeeViewList)
                    if (!employeeView.getAssignment().getPrimary())
                        departmentlist.add(employeeView.getAssignment().getDepartment());
        } catch (final Exception e) {
            final String errorMsg = "Could not get list of assignments";
            LOGGER.error(errorMsg, e);
            throw new ApplicationRuntimeException(errorMsg, e);
        }

        return departmentlist;
    }

    /**
     * @param user the user whose non-primary department is to be returned
     * @return non-primary department of the given user. In case user has multiple non-primary departments, the first one will be
     * returned.
     */
    public Department getNonPrimaryDeptOfUser(final User user) {
        final List<Department> nonPrimaryAssignments = getAllNonPrimaryAssignmentsOfUser(user);
        return nonPrimaryAssignments.isEmpty() ? null : nonPrimaryAssignments.get(0);
    }

    public List<Designation> getDesignationsAllowedForChallanApproval(final Integer departmentId,
            final ReceiptHeader receiptHeaderObj) {
        List<Designation> designations = new ArrayList<Designation>(0);
        designations = designationService.getAllDesignationByDepartment(Long.valueOf(departmentId), new Date());
        final List<Designation> designation = new ArrayList<Designation>(0);

        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                CollectionConstants.COLLECTION_DESIG_CHALLAN_WORKFLOW);
        for (final Designation desig : designations)
            for (final AppConfigValues app : appConfigValue)
                if (desig.getName().equals(app.getValue()))
                    designation.add(desig);
        return designation;
    }

    public List<Department> getDepartmentsAllowedForChallanApproval(final User loggedInUser,
            final ReceiptHeader receiptHeaderObj) {
        final List<Department> departments = new ArrayList<Department>(0);
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                CollectionConstants.MODULE_NAME_COLLECTIONS_CONFIG,
                CollectionConstants.COLLECTION_DESIG_CHALLAN_WORKFLOW);
        if (null != appConfigValue && !appConfigValue.isEmpty())
            for (final AppConfigValues app : appConfigValue) {
                final List<Assignment> assignments = assignmentService.findPrimaryAssignmentForDesignationName(app
                        .getValue());
                for (final Assignment assign : assignments)
                    if (!departments.contains(assign.getDepartment()))
                        departments.add(assign.getDepartment());
            }
        return departments;
    }

    public List<Department> getDepartmentsAllowedForBankRemittanceApproval(final User loggedInUser) {
        List<Department> departments;
        Department department;
        final ContraJournalVoucher contraJournalVoucherObj = new ContraJournalVoucher();
        if (contraJournalVoucherObj.getVoucherHeaderId() == null)
            department = getDepartmentOfUser(loggedInUser);
        else
            department = contraJournalVoucherObj.getVoucherHeaderId().getVouchermis().getDepartmentid();
        if (department.getCode().equals('R')) {
            if (contraJournalVoucherObj.getVoucherHeaderId() == null)
                departments = persistenceService.findAllBy("select dept from Department dept where dept.code=?", 'R');
            else
                departments = persistenceService.findAllBy("select dept from Department dept where dept.code=?", "CAF");
        } else
            departments = persistenceService.findAllBy("select dept from Department dept order by dept.name ");

        return departments;
    }

    public List<Designation> getDesignationsAllowedForBankRemittanceApproval(final Long departmentId) {
        Department department;
        List<Designation> designations;
        final ContraJournalVoucher contraJournalVoucherObj = new ContraJournalVoucher();
        department = (Department) persistenceService.find("select dept from Department dept where dept.id=?",
                departmentId);
        if (contraJournalVoucherObj.getVoucherHeaderId() == null) {
            if (department.getCode().equals('R'))
                designations = persistenceService
                .findAllBy(
                        "select distinct(dm) from Designation dm,Assignment a where a.designation.id=dm.id and (a.toDate >= current_timestamp or a.toDate is null) and a.department.id=? and upper(dm.name)=?",
                        departmentId, "REVENUE INSPECTOR");
            else
                designations = persistenceService
                .findAllBy(
                        "select distinct(dm) from Designation dm,Assignment a where a.designation.id=dm.id and (a.toDate >= current_timestamp or a.toDate is null) and a.department.id=?",
                        departmentId);
        } else if (department.getCode().equals("CAF"))
            designations = persistenceService
            .findAllBy(
                    "select distinct(dm) from Designation dm,Assignment a where a.designation,id=dm.id and (a.toDate >= current_timestamp or a.toDate is null) and a.department.code=? and upper(dm.name)=?",
                    "CAF", "SENIOR GRADE CLERK");
        else
            designations = persistenceService
            .findAllBy(
                    "select distinct(dm) from Designation dm,Assignment a where a.designation.id=dm.id and (a.toDate >= current_timestamp or a.toDate is null) and a.department.id=?",
                    departmentId);
        return designations;
    }

    /**
     * This method checks if the given glcode belongs to an account head representing an arrear account head (for Property Tax).
     * The glcodes for such accounts are retrieved from App Config.
     *
     * @param glcode The Chart of Accounts Code
     * @param description Description of the glcode
     * @returna a <code>Boolean</code> indicating if the glcode is arrear account head
     */
    public boolean isPropertyTaxArrearAccountHead(final String glcode, final String description) {
        final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(
                CollectionConstants.MODULE_NAME_PROPERTYTAX, "ISARREARACCOUNT");
        final AppConfigValues penaltyGlCode = appConfigValuesService.getAppConfigValueByDate(
                CollectionConstants.MODULE_NAME_PROPERTYTAX, "PTPENALTYGLCODE", new Date());
        boolean retValue = false;
        LOGGER.debug("isPropertyTaxArrearAccountHead glcode " + glcode + " description " + description);
        if (penaltyGlCode != null && penaltyGlCode.getValue().equals(glcode)) {
            final Module module = moduleService.getModuleByName(CollectionConstants.MODULE_NAME_PROPERTYTAX);
            final String currInst = commonsService.getInsatllmentByModuleForGivenDate(module, new Date())
                    .getDescription();
            if (currInst.equals(description.substring(16, description.length())))
                retValue = false;
            else
                retValue = true;
        } else {
            final ArrayList<String> accValues = new ArrayList<String>(0);
            for (final AppConfigValues value : list)
                accValues.add(value.getValue());
            if (accValues.contains(glcode))
                retValue = true;
            else
                retValue = false;
        }

        return retValue;
    }

    public List<EmployeeView> getPositionBySearchParameters(final String beginsWith, final Integer desId,
            final Integer deptId, final Integer jurdId, final Integer roleId, final Date userDate,
            final Integer maxResults) throws NoSuchObjectException {

        return searchPositionService.getPositionBySearchParameters(beginsWith, desId, deptId,
                jurdId != null ? Long.valueOf(jurdId) : null, roleId, userDate, maxResults);

    }

    /**
     * @param consumerCode
     * @return last three online transaction for the consumerCode
     */
    public List<OnlinePayment> getOnlineTransactionHistory(final String consumerCode) {
        final String hql = "select online from ReceiptHeader rh, org.egov.collection.entity.OnlinePayment online where rh.id = online.receiptHeader.id and rh.consumerCode =:consumercode  order by online.id desc";
        final Query query = persistenceService.getSession().createQuery(hql);
        query.setString("consumercode", consumerCode);
        query.setMaxResults(3);
        return query.list();
    }

    /**
     * @return list of all active locations
     */
    public List getAllLocations() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_ALL_LOCATIONS);
    }

    /**
     * @return list of all fund
     */
    public List<Fund> getAllFunds() {
        return persistenceService.findAllByNamedQuery(CollectionConstants.QUERY_ALL_FUND);
    }

    public User getUserById(final Long userId) {
        return userService.getUserById(userId);
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public void setCommonsService(final CommonsService commonsService) {
        this.commonsService = commonsService;
    }

    public void setPersistenceService(final PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public CollectionIndex constructCollectionIndex(final ReceiptHeader receiptHeader) {
        ReceiptAmountInfo receiptAmountInfo = new ReceiptAmountInfo();
        final ServiceDetails billingService = receiptHeader.getService();

        String instrumentType="";
        if(!receiptHeader.getReceiptInstrument().isEmpty())
            instrumentType = receiptHeader.getReceiptInstrument().iterator().next().getInstrumentType().getType();
        final CollectionIndexBuilder collectionIndexBuilder = new CollectionIndexBuilder(receiptHeader.getReceiptdate(),
                receiptHeader.getReceiptnumber(), billingService.getName(), instrumentType , receiptHeader.getTotalAmount(),
                receiptHeader.getSource(),
                receiptHeader.getStatus().getDescription()
                );

        collectionIndexBuilder.consumerCode(receiptHeader.getConsumerCode() != null ? receiptHeader.getConsumerCode() : "");
        collectionIndexBuilder.billNumber(receiptHeader.getReferencenumber() != null ? receiptHeader.getReferencenumber() : "");
        collectionIndexBuilder.paymentGateway(receiptHeader.getOnlinePayment() != null ? receiptHeader.getOnlinePayment()
                .getService().getName() : "");
        collectionIndexBuilder.payeeName(receiptHeader.getPayeeName() != null ? receiptHeader.getPayeeName() : "");

        if (receiptHeader.getReceipttype() == CollectionConstants.RECEIPT_TYPE_BILL) {
            final BillingIntegrationService billingServiceBean = (BillingIntegrationService) getBean(billingService.getCode()
                    + CollectionConstants.COLLECTIONS_INTERFACE_SUFFIX);
            try {
                receiptAmountInfo = billingServiceBean.receiptAmountBifurcation(new BillReceiptInfoImpl(receiptHeader, chartOfAccountsHibernateDAO));
            } catch (final Exception e) {
                final String errMsg = "Exception while constructing collection index for receipt number ["
                        + receiptHeader.getReceiptnumber() + "]!";
                LOGGER.error(errMsg, e);
                throw new ApplicationRuntimeException(errMsg, e);
            }
        }
        collectionIndexBuilder.arrearAmount(receiptAmountInfo.getArrearsAmount());
        collectionIndexBuilder.advanceAmount(receiptAmountInfo.getAdvanceAmount());
        collectionIndexBuilder.currentAmount(receiptAmountInfo.getCurrentInstallmentAmount());
        collectionIndexBuilder.penaltyAmount(receiptAmountInfo.getPenaltyAmount());
        collectionIndexBuilder.arrearCess(receiptAmountInfo.getArrearCess());
        collectionIndexBuilder.latePaymentChargesAmount(receiptAmountInfo.getLatePaymentCharges());
        collectionIndexBuilder.currentCess(receiptAmountInfo.getCurrentCess());
        if (receiptAmountInfo.getInstallmentFrom() != null)
            collectionIndexBuilder.installmentFrom(receiptAmountInfo.getInstallmentFrom());
        if (receiptAmountInfo.getInstallmentTo() != null)
            collectionIndexBuilder.installmentTo(receiptAmountInfo.getInstallmentTo());

        return collectionIndexBuilder.build();
    }

    public Boolean checkVoucherCreation(final ReceiptHeader receiptHeader) {
        Boolean createVoucherForBillingService = Boolean.FALSE;
        if (receiptHeader.getService().getVoucherCutOffDate() != null
                && receiptHeader.getReceiptDate().compareTo(receiptHeader.getService().getVoucherCutOffDate()) > 0) {
            if (receiptHeader.getService().getVoucherCreation()!= null)
                createVoucherForBillingService = receiptHeader.getService().getVoucherCreation();
        } else if (receiptHeader.getService().getVoucherCutOffDate() == null)
            if (receiptHeader.getService().getVoucherCreation()!= null)
                createVoucherForBillingService = receiptHeader.getService().getVoucherCreation();
        return createVoucherForBillingService;
    }
    
    public String getApproverName(Position position) {
        String approver;
        final Assignment assignment = assignmentService.getPrimaryAssignmentForPositon(position.getId());
        approver = assignment.getEmployee().getName().concat("~").concat(assignment.getEmployee().getCode())
                .concat("~").concat(assignment.getPosition().getName());
        return approver;
    }

}
