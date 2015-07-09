/*******************************************************************************
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
 * 	1) All versions of this program, verbatim or modified must carry this 
 * 	   Legal Notice.
 * 
 * 	2) Any misrepresentation of the origin of the material is prohibited. It 
 * 	   is required that all modified versions of this material be marked in 
 * 	   reasonable ways as different from the original version.
 * 
 * 	3) This license does not grant any rights to any user of the program 
 * 	   with regards to rights under trademark law for use of the trade names 
 * 	   or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
package org.egov.dao.bills;



import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.commons.CVoucherHeader;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.dao.GenericHibernateDAO;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.model.bills.EgBillregister;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unchecked")
@Transactional(readOnly=true)
public class EgBillRegisterHibernateDAO extends GenericHibernateDAO{
	private final Logger LOGGER = Logger.getLogger(getClass());
	private  PersistenceService<EgBillregister, Long> egBillRegisterService;
	private Session session;
	public EgBillRegisterHibernateDAO(Class persistentClass, Session session) {
		super(persistentClass, session);
		 egBillRegisterService = new PersistenceService<EgBillregister, Long>();
		// egBillRegisterService.setSessionFactory(new SessionFactory());
		 egBillRegisterService.setType(EgBillregister.class);
	}
	
	
	public List<String> getDistinctEXpType(){
		session =HibernateUtil.getCurrentSession();
      
		List<String> list = (List<String>)session.createQuery("select DISTINCT (expendituretype) from EgBillregister egbills" ).list();
        return list;
       
		
	}

	//shoud get called only for other t Fixed asset
	public String getBillTypeforVoucher(CVoucherHeader voucherHeader) throws ValidationException{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("EgBillRegisterHibernateDAO | getBillTypeforVoucher");
		if(null == voucherHeader){
			 throw new ValidationException(Arrays.asList(new ValidationError("voucher header null","VoucherHeader supplied is null")));
		}
		session =HibernateUtil.getCurrentSession();
		Query qry =  session.createQuery("from  EgBillregister br where br.egBillregistermis.voucherHeader.id=:voucherId");
		qry.setLong("voucherId",voucherHeader.getId());
		EgBillregister billRegister=(EgBillregister)qry.uniqueResult(); 
		return (billRegister==null?null:billRegister.getExpendituretype());
	}
	
	//shoud get called only for Fixed asset
	public String getBillSubTypeforVoucher(CVoucherHeader voucherHeader) throws ValidationException{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("EgBillRegisterHibernateDAO | getBillTypeforVoucher");
		if(null == voucherHeader){
			 throw new ValidationException(Arrays.asList(new ValidationError("voucher header null","VoucherHeader supplied is null")));
		}
		session =HibernateUtil.getCurrentSession();
		Query qry =  session.createQuery("from  EgBillregister br where br.egBillregistermis.voucherHeader.id=:voucherId");
		qry.setLong("voucherId",voucherHeader.getId());
		EgBillregister billRegister=(EgBillregister)qry.uniqueResult(); 
		return (billRegister==null?"General":billRegister.getEgBillregistermis().getEgBillSubType()==null?billRegister.getExpendituretype():billRegister.getEgBillregistermis().getEgBillSubType().getName());
	}
}
