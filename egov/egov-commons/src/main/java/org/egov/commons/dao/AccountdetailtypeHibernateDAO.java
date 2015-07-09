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

import java.lang.reflect.Field;

import org.egov.exceptions.EGOVException;
import org.egov.infstr.dao.GenericHibernateDAO;
import org.hibernate.Query;
import org.hibernate.Session;

public class AccountdetailtypeHibernateDAO extends GenericHibernateDAO {

	public AccountdetailtypeHibernateDAO(final Class persistentClass, final Session session) {
		super(persistentClass, session);
	}

	/**
	 * This API will return the detailtypeid for the object that is passed. If this master is mapped in the accountdetailtype , 
	 * it will return the id else null. Assumption: Any object that is passed to this API needs to have an instance variable by 
	 * name "tablename". tablename is the table that object refers to. If this instance variable is not present in the object, 
	 * its assumed that this object is not set as a subledgertype. If there is a value for instance variable "tablename" , 
	 * this will be checked against accountdetailtype.tablename.
	 * @param master
	 * @return
	 * @throws EGOVException
	 */
	public Integer getDetailtypeforObject(final Object master) throws EGOVException {
		if (null == master) {
			throw new EGOVException("The object supplied is null");
		}
		try {
			final Field tableNameField = Class.forName(master.getClass().getName()).getDeclaredField("tablename");
			tableNameField.setAccessible(true);
			final Query query = getCurrentSession().createQuery("select adt.id from Accountdetailtype adt where UPPER(tablename)=:tableName");
			query.setString("tableName", ((String) tableNameField.get(master)).toUpperCase());
			Integer detailtypeid = null;
			if (query.uniqueResult() != null) {
				detailtypeid = (Integer) query.uniqueResult();
			}
			return detailtypeid;
		} catch (final NoSuchFieldException e) {
			return null;// return the null if the object passed doesnot have the instance variable tablename.
		} catch (final Exception e) {
			throw new EGOVException("Exception occured while getting detailtypeid ", e);
		}

	}
}