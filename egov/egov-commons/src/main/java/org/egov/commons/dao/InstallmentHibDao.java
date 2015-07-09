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
package org.egov.commons.dao;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.Installment;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infstr.dao.GenericHibernateDAO;
import org.egov.infstr.utils.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository(value = "installmentDAO")
public class InstallmentHibDao<T, id extends Serializable> extends GenericHibernateDAO implements InstallmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    protected Session getCurrentSession() {
            return entityManager.unwrap(Session.class);
    }
    
	public InstallmentHibDao() {
		super(Installment.class, null);
	}

	/**
	 * @param persistentClass
	 * @param session
	 */
	public InstallmentHibDao(final Class<T> persistentClass, final Session session) {
		super(persistentClass, session);
	}

	@Override
	public List getInsatllmentByModule(final Module module) {
		final Query qry = getCurrentSession().createQuery("from Installment I where I.module=:module");
		qry.setEntity("module", module);

		return qry.list();
	}

	@Override
	public List getInsatllmentByModule(final Module module, final Date year) {
		final Query qry = getCurrentSession().createQuery("from Installment I where I.module=:module and I.installmentYear=:year");
		qry.setEntity("module", module);
		qry.setDate("year", year);

		return qry.list();
	}

	@Override
	public Installment getInsatllmentByModule(final Module module, final Date year, final Integer installmentNumber) {
		final Query qry = getCurrentSession().createQuery("from Installment I where I.module=:module and I.installmentYear=:year and I.installmentNumber =:instNum");
		qry.setEntity("module", module);
		qry.setDate("year", year);
		qry.setInteger("instNum", installmentNumber);

		return (Installment) qry.uniqueResult();
	}

	@Override
	public Installment getInsatllmentByModuleForGivenDate(final Module module, final Date installmentDate) {
		final Query qry = getCurrentSession().createQuery("from Installment I where I.module=:module and (I.fromDate <= :fromYear and I.toDate >=:toYear)");
		qry.setEntity("module", module);
		qry.setDate("fromYear", installmentDate);
		qry.setDate("toYear", installmentDate);

		return (Installment) qry.uniqueResult();

	}

	@Override
	public List<Installment> getEffectiveInstallmentsforModuleandDate(final Date dateToCompare, final int noOfMonths, final Module mod) {
		final Query qry = getCurrentSession().createQuery("from org.egov.commons.Installment inst where  inst.toDate >= :dateToCompare and inst.toDate < :dateToComparemax   and inst.module=:module");
		qry.setDate("dateToCompare", dateToCompare);
		qry.setEntity("module", mod);
		final Date dateToComparemax = DateUtils.add(dateToCompare, Calendar.MONTH, noOfMonths);
		qry.setDate("dateToComparemax", dateToComparemax);

		return qry.list();
	}
}
