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
package org.egov.eis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.eis.entity.Assignment;
import org.egov.eis.entity.Employee;
import org.egov.eis.entity.EmployeeSearchDTO;
import org.egov.eis.entity.HeadOfDepartments;
import org.egov.eis.entity.enums.EmployeeStatus;
import org.egov.eis.repository.AssignmentRepository;
import org.egov.eis.repository.EmployeeRepository;
import org.egov.eis.utils.constants.EisConstants;
import org.egov.infra.admin.master.service.RoleService;
import org.egov.infra.config.properties.ApplicationProperties;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    @PersistenceContext
    private EntityManager entityManager;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    private final EmployeeRepository employeeRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    public EmployeeService(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @SuppressWarnings("unchecked")
    public List<CFunction> getAllFunctions() {
        return getCurrentSession()
                .createQuery("from CFunction where isactive = 1 AND isnotleaf=0 order by upper(name)").list();
    }

    @SuppressWarnings("unchecked")
    public List<Functionary> getAllFunctionaries() {
        return getCurrentSession().createQuery("from Functionary where isactive=1 order by upper(name)").list();
    }

    @SuppressWarnings("unchecked")
    public List<Fund> getAllFunds() {
        return getCurrentSession().createQuery("from Fund where isactive = 1 and isNotLeaf!=1 order by upper(name)")
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Fund> getAllGrades() {
        return getCurrentSession().createQuery("from GradeMaster order by name").list();
    }

    @Transactional
    public void create(final Employee employee) {
        employee.setPwdExpiryDate(new DateTime().plusDays(applicationProperties.userPasswordExpiryInDays()).toDate());

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        // Following is added to prevent null values and empty assignment
        // objects getting persisted
        employee.setAssignments(employee.getAssignments().parallelStream()
                .filter(assignment -> assignment.getPosition() != null).collect(Collectors.toList()));
        for (final Assignment assign : employee.getAssignments()) {
            assign.setEmployee(employee);
            assign.setDepartment(assign.getDepartment());
            for (final HeadOfDepartments hod : assign.getDeptSet())
                hod.setAssignment(assign);
        }
        employee.getRoles().add(roleService.getRoleByName(EisConstants.ROLE_EMPLOYEE));
        employeeRepository.save(employee);
    }

    @Transactional
    public void update(final Employee employee) {
        // Following is added to prevent null values and empty assignment
        // objects getting persisted
        employee.setAssignments(employee.getAssignments().parallelStream()
                .filter(assignment -> assignment.getPosition() != null).collect(Collectors.toList()));
        for (final Assignment assign : employee.getAssignments()) {
            assign.setEmployee(employee);
            assign.setDepartment(assign.getDepartment());
            for (final HeadOfDepartments hod : assign.getDeptSet())
                hod.setAssignment(assign);
        }
        employeeRepository.saveAndFlush(employee);
    }

    public List<Employee> searchEmployees(EmployeeSearchDTO searchParams) {
        
        Criteria criteria  = getCurrentSession().createCriteria(Assignment.class,"assignment").
                createAlias("assignment.employee", "employee");
        if(null!=searchParams.getCode() && !searchParams.getCode().isEmpty())
            criteria.add(Restrictions.eq("employee.code",searchParams.getCode()));
        if(null!=searchParams.getName() && !searchParams.getName().isEmpty())
            criteria.add(Restrictions.eq("employee.name",searchParams.getName()));
        if(null!=searchParams.getAadhaar() && !searchParams.getAadhaar().isEmpty())
            criteria.add(Restrictions.eq("employee.aadhaar",searchParams.getAadhaar()));
        if(null!=searchParams.getMobileNumber() && !searchParams.getMobileNumber().isEmpty())
            criteria.add(Restrictions.eq("employee.mobileNumber",searchParams.getMobileNumber()));
        if(null!=searchParams.getPan() && !searchParams.getPan().isEmpty())
            criteria.add(Restrictions.eq("employee.pan",searchParams.getPan()));
        if(null!=searchParams.getEmail() && !searchParams.getEmail().isEmpty())
            criteria.add(Restrictions.eq("employee.emailId",searchParams.getEmail()));
        if(null!=searchParams.getStatus() && !searchParams.getStatus().isEmpty())
            criteria.add(Restrictions.eq("employee.employeeStatus",EmployeeStatus.valueOf(searchParams.getStatus())));
        if(null!=searchParams.getEmployeeType() && !searchParams.getEmployeeType().isEmpty()){
            criteria.createAlias("employee.employeeType", "employeeType");
            criteria.add(Restrictions.eq("employeeType.name",searchParams.getEmployeeType()));
        }
        if(null!=searchParams.getDepartment() && !searchParams.getDepartment().isEmpty()){
            criteria.createAlias("assignment.department", "department");
            criteria.add(Restrictions.eq("department.name",searchParams.getDepartment()));
        }
        if(null!=searchParams.getDesignation() && !searchParams.getDesignation().isEmpty()) {
            criteria.createAlias("assignment.designation", "designation");
            criteria.add(Restrictions.eq("designation.name",searchParams.getDesignation()));
        }
        if(null!=searchParams.getFunctionary() && !searchParams.getFunctionary().isEmpty()) {
            criteria.createAlias("assignment.functionary", "functionary");
            criteria.add(Restrictions.eq("functionary.name",searchParams.getFunctionary()));
        }
        if(null!=searchParams.getFunction() && !searchParams.getFunction().isEmpty()) {
            criteria.createAlias("assignment.function", "function");
            criteria.add(Restrictions.eq("function.name",searchParams.getFunction()));
        }
        
        final ProjectionList projections = Projections.projectionList().add(Projections.property("assignment.employee"));
        criteria.setProjection(projections);
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return (List<Employee>)criteria.list();
        
    }

    @Transactional
    public void delete(final Employee employee) {
        employeeRepository.delete(employee);
    }

    /**
     * Get employee object by id
     *
     * @param id
     * @return Employee Object
     */
    public Employee getEmployeeById(final Long id) {
        return employeeRepository.findOne(id);
    }

    /**
     * Get employee object by code
     *
     * @param code
     * @return Employee Object
     */
    public Employee getEmployeeByCode(final String code) {
        return employeeRepository.findByCode(code);
    }

    /**
     * Get list of employee objects by employee status
     *
     * @param status
     * @return List of employee objects
     */
    public List<Employee> getEmployeesByStatus(final EmployeeStatus status) {
        return employeeRepository.findByEmployeeStatus(status);
    }

    /**
     * Get list of employee objects by employee type
     *
     * @param id
     * @return List of employee objects
     */
    public List<Employee> getEmployeesByType(final Long id) {
        return employeeRepository.findByEmployeeType_Id(id);
    }

    /**
     * Get employee by user name
     *
     * @param userName
     * @return Employee Object
     */
    public Employee getEmployeeByUserName(final String userName) {
        return employeeRepository.findByUsername(userName);
    }

    /**
     * Get List of employee objects by department ,designation and boundary ids
     *
     * @param deptId
     * @param desigId
     * @param boundaryId
     * @return List of employee objects
     */
    public List<Employee> findByDepartmentDesignationAndBoundary(final Long deptId, final Long desigId,
            final Long boundaryId) {
        return employeeRepository.findByDepartmentDesignationAndBoundary(deptId, desigId, boundaryId);
    }

    /**
     * Returns list of employee for a given position
     *
     * @param posId
     * @return List of PersonalInformation
     */
    public List<Employee> getEmployeesForPosition(final Long posId) {
        final Set<Employee> employees = new HashSet<Employee>();
        final List<Assignment> assignList = assignmentRepository.getAssignmentsForPosition(posId);
        for (final Assignment assign : assignList)
            employees.add(assign.getEmployee());
        return new ArrayList<Employee>(employees);
    }

    /**
     * Returns primary assignment's employee for position
     *
     * @param posId
     * @return Employee object
     */
    public Employee getPrimaryAssignmentEmployeeForPos(final Long posId) {
        return assignmentRepository.getPrimaryAssignmentForPosition(posId).getEmployee();
    }

    /**
     * Returns employee object for position id and given date
     *
     * @param posId
     * @param givenDate
     * @return Employee object
     */
    public Employee getEmployeeForPositionAndDate(final Long posId, final Date givenDate) {
        return assignmentRepository.getPrimaryAssignmentForPositionAndDate(posId, givenDate).getEmployee();
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

}
