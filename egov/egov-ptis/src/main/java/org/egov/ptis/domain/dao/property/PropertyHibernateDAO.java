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
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org
 ******************************************************************************/
package org.egov.ptis.domain.dao.property;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.egov.commons.Installment;
import org.egov.demand.model.EgDemand;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infstr.utils.EGovConfig;
import org.egov.portal.entity.Citizen;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyMaterlizeView;
import org.egov.ptis.domain.entity.property.PropertySource;
import org.egov.ptis.exceptions.PropertyNotFoundException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository(value = "propertyDAO")
public class PropertyHibernateDAO implements PropertyDAO {
	private static final Logger LOGGER = Logger.getLogger(PropertyHibernateDAO.class);

	@PersistenceContext
	private EntityManager entityManager;

	private Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	@Override
	public Property getPropertyByID(String propID) {
		Query qry = getCurrentSession().createQuery("from PropertyImpl P where P.id =:ID ");
		qry.setString("ID", propID);
		return (Property) qry.uniqueResult();
	}

	/*
	 * BasicProperty may have multiple Property references, based on different
	 * Src of Info But in this method We return only one of the Property
	 * entities.
	 */
	@Override
	public Property getPropertyByBasicPropertyID(BasicProperty basicProperty) {
		Query qry = getCurrentSession().createQuery(
				"from PropertyImpl P where P.basicProperty =:basicProperty");
		qry.setEntity("basicProperty", basicProperty);
		qry.setMaxResults(1);
		return (Property) qry.uniqueResult();
	}

	/**
	 * This method gives the OnlineWards object for given wardID
	 * 
	 * @param wardId
	 * @return List of onlinewards objects
	 */
	@Override
	public List getOnlineDateByWardID(Integer wardId) {
		Query qry = getCurrentSession().createQuery("from OnlineWards P where P.wardId =:wardId ");
		qry.setInteger("wardId", wardId);
		return qry.list();
	}

	@Override
	public List getAllNonDefaultProperties(BasicProperty basicProperty) {
		Query qry = getCurrentSession()
				.createQuery(
						"from PropertyImpl P where P.basicProperty =:basicProperty and P.isDefaultProperty='N' and P.status='N' ");
		qry.setEntity("basicProperty", basicProperty);
		return qry.list();
	}

	@Override
	public List getAllProperties(BasicProperty basicProperty) {
		Query qry = getCurrentSession().createQuery(
				"from PropertyImpl P where P.basicProperty =:basicProperty  and P.status='N' ");
		qry.setEntity("basicProperty", basicProperty);
		return qry.list();
	}

	/**
	 * This is used to get a unique property based on 3 parameters
	 * BasicProperty,assessmentYearand ProeprtySource, may throw exception in
	 * case of multiple resultset.
	 */
	@Override
	public Property getPropertyForInstallment(BasicProperty basicProperty, Installment insatllment,
			PropertySource src) {
		try {
			Property prop = null;
			Query qry = getCurrentSession()
					.createQuery(
							"from PropertyImpl P where  P.basicProperty =:basicProperty  and P.status='N' and P.installment=:insatllment and P.propertySource =:src ");
			qry.setEntity("insatllment", insatllment);
			qry.setEntity("basicProperty", basicProperty);
			qry.setEntity("src", src);
			if (qry.uniqueResult() != null) {
				prop = (Property) qry.uniqueResult();
				LOGGER.debug("getPropertyForInstallment : prop : " + prop);
			}
			return prop;
		} catch (Exception e) {
			LOGGER.error("Exception in  getPropertyForInstalment in DAO : " + e.getMessage());
			throw new EGOVRuntimeException("Exception in  getPropertyForInstalment" + e);
		}
	}

	@Override
	public List getAllHistories(BasicProperty bp, PropertySource src) {
		Query qry = getCurrentSession()
				.createQuery(
						"from PropertyImpl P where P.basicProperty =:basicProperty and P.status='Y' and P.propertySource =:src ");
		qry.setEntity("basicProperty", bp);
		qry.setEntity("src", src);
		return qry.list();
	}

	@Override
	public List getWardWiseProperties() {
		StringBuffer selectProperties = new StringBuffer(1500);

		selectProperties
				.append("select count(propusage.idUsage),propusage.usageName,BndryImpl.id,BndryImpl.boundaryNum ");
		selectProperties.append("from BasicPropertyImpl BP left join BP.property prop ");
		selectProperties.append("inner join prop.propertyDetail propdetail ");
		selectProperties.append("inner join propdetail.PropertyUsage propusage ");
		selectProperties.append("left join BP.propertyID propid ");
		selectProperties.append("left join propid.wardId BndryImpl ");
		selectProperties
				.append("group by (propusage.usageName,BndryImpl.id,BndryImpl.boundaryNum) ");
		selectProperties.append("order by BndryImpl.boundaryNum");

		Query qry = getCurrentSession().createQuery(selectProperties.toString());
		return qry.list();
	}

	@Override
	public Property getPropertyBySource(String src) {
		Query qry = getCurrentSession().createQuery(
				"from PropertyImpl P where P.propertySource.name like :src ");
		qry.setString("src", src);
		return (Property) qry.uniqueResult();
	}

	@Override
	public List getAllPropertiesForGivenBndryListAndSrc(List bndryList, String src) {
		List propList = new ArrayList();
		if (src != null && !src.isEmpty() && bndryList != null && !bndryList.isEmpty()) {
			LOGGER.debug("getAllPropertiesForGivenBndryListAndSrc : bndryList : " + bndryList
					+ " : src : " + src);
			StringBuffer strBuffer = new StringBuffer(200);
			strBuffer.append("select prop from BasicPropertyImpl BP left join BP.property prop ");
			if (src != null && !src.equals("")) {
				strBuffer.append(" left join prop.propertySource propsrc ");
				strBuffer.append(" where propsrc.name like :src and ");
			} else {
				strBuffer.append(" where ");
			}
			strBuffer.append(" BP.boundary.id in (:bndryList)");

			Query qry = getCurrentSession().createQuery(strBuffer.toString());
			qry.setString("src", src);
			qry.setParameterList("bndryList", bndryList);
			propList = qry.list();
		}
		LOGGER.debug("getAllPropertiesForGivenBndryListAndSrc : propList.size() : "
				+ propList.size());
		return propList;
	}

	@Override
	public List getAllPropertiesForGivenBndryListSrcAndInst(List bndryList, String src,
			Installment inst) {
		List propList = new ArrayList(0);
		if (bndryList != null && !bndryList.isEmpty() && src != null && !src.isEmpty()
				&& inst != null) {
			LOGGER.debug("getAllPropertiesForGivenBndryListSrcAndInst : bndryList : " + bndryList
					+ " : src : " + src + " : inst : " + inst);
			Query qry = getCurrentSession()
					.createQuery(
							"select prop from BasicPropertyImpl BP, PropertyImpl prop left join prop.propertySource propsrc "
									+ "where prop.basicProperty.id = BP.id and propsrc.name like :src and prop.propertyDetail.installment=:inst and BP.boundary.id in (:bndryList)");
			qry.setString("src", src);
			qry.setEntity("inst", inst);
			qry.setParameterList("bndryList", bndryList);
			propList = qry.list();
		}
		LOGGER.debug("getAllPropertiesForGivenBndryListSrcAndInst propList() : " + propList.size());
		return propList;
	}

	// this method will give list of properties which dont have history by
	// passing parameters as basicproperty and propertysrc objects
	@Override
	public List getAllNonHistoryPropertiesForSrc(BasicProperty basicProperty, PropertySource src) {
		List propList = new ArrayList(0);
		if (basicProperty != null && src != null) {
			Query qry = getCurrentSession()
					.createQuery(
							"from PropertyImpl P where P.basicProperty =:basicProperty and P.status='N' and P.propertySource =:src");
			qry.setEntity("basicProperty", basicProperty);
			qry.setEntity("src", src);
			propList = qry.list();
		}
		LOGGER.debug("getAllNonHistoryPropertiesForSrc propList() : " + propList.size());
		return propList;
	}

	/*
	 * getPropertyBySrcAndBp(BasicProperty basicProperty,PropertySource src)
	 * will give property for basicproperty and source
	 */
	@Override
	public Property getPropertyBySrcAndBp(BasicProperty basicProperty, PropertySource src)
			throws EGOVRuntimeException {
		Property prop = null;
		try {
			if (basicProperty != null && src != null) {
				LOGGER.debug("getPropertyBySrcAndBp basicProperty : " + basicProperty);
				Query qry = getCurrentSession()
						.createQuery(
								"from PropertyImpl P where P.basicProperty =:basicProperty and  P.status='N' and P.propertySource =:src");
				qry.setEntity("basicProperty", basicProperty);
				qry.setEntity("src", src);
				prop = (Property) qry.uniqueResult();
			}
			return prop;
		} catch (Exception e) {
			LOGGER.error("Exception in getPropertyBySrcAndBp : " + e.getMessage());
			throw new EGOVRuntimeException("Exception in  getPropertyBySrcAndBp", e);
		}
	}

	/**
	 * This method checks if Count of Properties exceed 500
	 * 
	 * @param bndryList
	 * @return
	 */
	@Override
	public boolean checkIfPropCountExceeds500(List bndryList) throws EGOVRuntimeException {
		boolean chkCntExcds500 = false;
		int count = 0;
		try {
			if (bndryList != null && !bndryList.isEmpty()) {
				String qryStr = "select count(*) from BasicPropertyImpl BP where BP.boundary.id in (:bndryList) ";
				Query qry = getCurrentSession().createQuery(qryStr);
				qry.setParameterList("bndryList", bndryList);
				Integer cnt = (Integer) qry.uniqueResult();
				count = cnt.intValue();
				if (count > 500) {
					chkCntExcds500 = true;
				}
				LOGGER.debug("checkIfPropCountExceeds500 chkCntExcds500 : " + chkCntExcds500);
			}
		} catch (HibernateException e) {
			LOGGER.error("Error occured in PropertyHibernateDao.checkIfPropCountExceeds500"
					+ e.getMessage());
			throw new EGOVRuntimeException("Hibernate Exception in checkIfPropCountExceeds500: "
					+ e.getMessage(), e);
		} catch (Exception e1) {
			LOGGER.error("Error occured in PropertyHibernateDao.checkIfPropCountExceeds500"
					+ e1.getMessage());
			throw new EGOVRuntimeException("Exception in checkIfPropCountExceeds500 : "
					+ e1.getMessage(), e1);
		}
		return chkCntExcds500;
	}

	@Override
	public List getBasicPropertyListByDcNo(String dcNo) throws EGOVRuntimeException {
		List bpList = null;
		try {
			if (dcNo != null) {
				Query qry = getCurrentSession().createQuery(
						"from BasicPropertyImpl BP where BP.dcRegister.id=:dcNo and BP.active='Y'");
				qry.setString("dcNo", dcNo);
				bpList = qry.list();
			}
		} catch (Exception e) {
			LOGGER.error("Error occured in PropertyHibernateDao.getBasicPropertyListByDcNo"
					+ e.getMessage());
			throw new EGOVRuntimeException("Exception in checkIfPropCountExceeds500 : " + e);
		}
		return bpList;
	}

	/**
	 * This is used to get all the proposed arv's for that particular property.
	 */
	@Override
	public List getPtDemandArvProposedList(Property property) {
		Query qry = getCurrentSession()
				.createQuery(
						"from PtDemandARV arv where arv.property =:property  and arv.isHistory='N' and arv.type='Proposed' ");
		qry.setEntity("property", property);
		return qry.list();
	}

	@Override
	public Citizen getOwnerByOwnerId(Long citizenId) {
		Citizen owner = null;
		Query qry = getCurrentSession().createQuery(" FROM Citizen citizen where citizen.id =:id ");
		qry.setLong("id", citizenId);
		owner = (Citizen) qry.uniqueResult();
		return owner;
	}

	@Override
	public List getPropertyDemand(String propertyId) {
		final String rebate = EGovConfig.getProperty("ptis_egov_config.xml", "ACCOUNT_HEAD_REBATE",
				"", "PT");
		Query qry = getCurrentSession()
				.createQuery(
						" select sum(dd.amount) from PropertyTaxDemand pt left join "
								+ "pt.demandDetails dd where pt.history = 'N' and dd.accountHead.accountHeadName !=:accHead and pt.property.basicProperty.upicNo =:PID ");
		qry.setString("accHead", rebate);
		qry.setString("PID", propertyId);
		return qry.list();
	}

	@Override
	public List getPropertyRebate(String propertyId) {
		final String rebate = EGovConfig.getProperty("ptis_egov_config.xml", "ACCOUNT_HEAD_REBATE",
				"", "PT");
		Query qry = getCurrentSession()
				.createQuery(
						" select sum(dd.amount) from PropertyTaxDemand pt left join "
								+ "pt.demandDetails dd where pt.history = 'N' and dd.isApproved = '1' and dd.accountHead.accountHeadName =:accHead and "
								+ "pt.property.basicProperty.upicNo =:PID ");
		qry.setString("accHead", rebate);
		qry.setString("PID", propertyId);
		return qry.list();
	}

	@Override
	public List getPropertyCollection(String propertyId) {
		Query qry = getCurrentSession()
				.createQuery(
						" select sum(TD.inFlowAmount) from PropertyTaxTxAgent pta left join  "
								+ "pta.myTransactions TI left join TI.transactionDetails TD where TI.isCancelled = '0' and pta.basicProperty.upicNo =:PID ");
		qry.setString("PID", propertyId);
		return qry.list();
	}

	@Override
	public List getPTDemandArvByNoticeNumber(String noticeNo) {
		Query qry = getCurrentSession()
				.createQuery(
						" FROM PtDemandARV ar where ar.section72No =:id and ar.isHistory='N' and ar.type ='Proposed' ");
		qry.setString("id", noticeNo);
		return qry.list();
	}

	@Override
	public List getPropsMrkdForDeactByWard(Boundary boundary) throws PropertyNotFoundException {
		List propertyList = new ArrayList();
		try {
			if (boundary != null) {
				Query qry = getCurrentSession()
						.createQuery(
								" select distinct bp from PropertyImpl pi "
										+ " left join pi.basicProperty bp "
										+ " left join bp.propertyStatusValuesSet psv "
										+ " left join psv.propertyStatus ps "
										+ " where bp.boundary = :boundary and pi.status='N' and bp.active='Y' "
										+ " and ps.statusCode='MARK_DEACTIVE' and psv.isActive='Y' ");

				qry.setEntity("boundary", boundary);
				propertyList = qry.list();
			}

			LOGGER.info("List of properties By Query" + propertyList.size());
			return propertyList;
		} catch (HibernateException e) {
			LOGGER.error("Error occured in PropertyHibernateDao.getPropsMrkdForDeactByWard"
					+ e.getMessage());
			PropertyNotFoundException er = new PropertyNotFoundException(
					"Hibernate Exception In getAllPropertiesMarkedForDeactivationByWard: " + e);
			er.initCause(e);
			throw er;
		} catch (Exception e) {
			LOGGER.error("Error occured in PropertyHibernateDao.getPropsMrkdForDeactByWard"
					+ e.getMessage());
			throw new EGOVRuntimeException(
					"Exception in  getAllPropertiesMarkedForDeactivationByWard" + e);
		}
	}

	/**
	 * To get the list of required values as of the Projection,restriction and
	 * order in which the client passes as parameters.All these values are taken
	 * from PropertyMaterlizeView table.
	 * 
	 * @param org
	 *            .hibernate.criterion.Projection projection
	 * @param org
	 *            .hibernate.criterion.Criterion criterion
	 * @param org
	 *            .hibernate.criterion.Order order
	 * 
	 * @return Projection list(i.e mentioned in Projection parameter) from
	 *         PropertyMaterlizeView table.
	 * 
	 */

	@Override
	public List getPropMaterlizeViewList(Projection projection, Criterion criterion, Order order) {
		Criteria criteria = getCurrentSession().createCriteria(PropertyMaterlizeView.class);
		if (projection != null) {
			criteria.setProjection(projection);
		}
		if (criterion != null) {
			criteria.add(criterion);
		}
		if (order != null) {
			criteria.addOrder(order);
		}

		return criteria.list();
	}

	/**
	 * To get the list of required values as of the Projection,restriction and
	 * order in which the client passes as parameters..
	 * 
	 * 
	 * @param Class
	 *            classObj
	 * @param org
	 *            .hibernate.criterion.Projection classObj
	 * @param org
	 *            .hibernate.criterion.Criterion criterion
	 * @param org
	 *            .hibernate.criterion.Order order
	 * 
	 * @return Projection list(i.e mentioned in Projection parameter) .
	 * 
	 */

	@Override
	public List getResultsList(Class classObj, Projection projection, Criterion criterion,
			Order order) {
		Criteria criteria = getCurrentSession().createCriteria(classObj);
		if (projection != null) {
			criteria.setProjection(projection);
		}
		if (criterion != null) {
			criteria.add(criterion);
		}
		if (order != null) {
			criteria.addOrder(order);
		}

		return criteria.list();
	}

	/**
	 * To get the list of required values
	 * 
	 * @param org
	 *            .hibernate.criterion.DetachedCriteria detachedCriteria
	 * 
	 * @return Projection list(i.e mentioned in DetachedCriteria).
	 * 
	 */
	@Override
	public List getResultsList(DetachedCriteria detachedCriteria) {
		Criteria criteria = null;
		if (detachedCriteria != null) {
			criteria = detachedCriteria.getExecutableCriteria(getCurrentSession());
		}
		return criteria.list();
	}

	@Override
	public List getDmdCollAmtInstWise(EgDemand egDemand) {
		List list = new ArrayList();
		StringBuffer strBuf = new StringBuffer(2000);
		strBuf.append(" select dmdRes.id_installment, sum(dmdDet.amount) as amount, sum(dmdDet.amt_collected) as amt_collected, sum(dmdDet.amt_rebate) as amt_rebate, inst.start_date "
				+ "from eg_demand_details dmdDet,eg_demand_reason dmdRes,eg_installment_master inst,eg_demand_reason_master dmdresmas "
				+ "where dmdDet.id_demand_reason=dmdRes.id "
				+ "and dmdDet.id_demand =:dmdId "
				+ "and dmdRes.id_installment = inst.id "
				+ "and dmdresmas.id = dmdres.id_demand_reason_master "
				+ "and dmdresmas.code not in ('"
				+ PropertyTaxConstants.ADVANCE_DMD_RSN_CODE
				+ "','"
				+ PropertyTaxConstants.PENALTY_DMD_RSN_CODE
				+ "','"
				+ PropertyTaxConstants.LPPAY_PENALTY_DMDRSNCODE
				+ "') "
				+ "group by dmdRes.id_installment, inst.start_date " + "order by inst.start_date ");
		Query qry = getCurrentSession().createSQLQuery(strBuf.toString()).setLong("dmdId",
				egDemand.getId());
		return qry.list();
	}

	@Override
	public List getDmdDetIdFromInstallandEgDemand(Installment installment, EgDemand egDemand) {
		List dmdIdList = new ArrayList();
		if (egDemand != null && installment != null) {
			StringBuffer strBuf = new StringBuffer(2000);
			strBuf.append(" select dmdet.id from eg_demand_details dmdet, eg_demand_reason res where dmdet.id_demand_reason= res.id and dmdet.id_demand =:dmdId and res.id_installment =:instlId ");
			Query qry = getCurrentSession().createSQLQuery(strBuf.toString());
			qry.setLong("dmdId", egDemand.getId());
			qry.setInteger("instlId", installment.getId());
			dmdIdList = qry.list();
		}
		return dmdIdList;
	}

	/**
	 * Method called to get the EgptProperty Id from the Bill Id.Property is
	 * linked with EgDemand which internally linked with egBill.
	 * 
	 * @param billId
	 *            - Id of the EgBill Object .
	 * @return java.math.BigDecimal - returns the EgptProperty Id. If the billId
	 *         is null then null is returned.
	 * 
	 */

	@Override
	public BigDecimal getEgptPropertyFromBillId(Long billId) {
		BigDecimal propertyId = null;
		if (billId != null) {
			StringBuffer strBuf = new StringBuffer(2000);
			strBuf.append(" select ptdem.id_property from ")
					.append("egpt_ptdemand ptdem, eg_demand dmd, eg_bill bill ")
					.append("where bill.id_demand = dmd.id and dmd.id = ptdem.id_demand ")
					.append("and dmd.is_history = 'N' and bill.id = :billId ");
			Query qry = getCurrentSession().createSQLQuery(strBuf.toString());
			qry.setLong("billId", billId).setMaxResults(1);
			propertyId = (BigDecimal) qry.uniqueResult();
		}
		return propertyId;
	}

	/**
	 * Method called to get all the Demands(i.,e including the history and non
	 * history) for a BasicProperty.
	 * 
	 * @param basicProperty
	 *            - BasicProperty Object in which Demands needs are to retrieved
	 * @return java.util.List - returns the list of Demands for the
	 *         basicProperty. If the basicPrperty is null then null is returned
	 * 
	 */
	@Override
	public List getAllDemands(BasicProperty basicProperty) {
		List demandIds = null;
		if (basicProperty != null) {
			demandIds = new ArrayList();
			String qryStr = "SELECT ptdem.id_demand " 
			                + "FROM egpt_basic_property bas, "
					+ "  egpt_property prop, " + "  egpt_ptdemand ptdem "
					+ "WHERE bas.ID = prop.ID_BASIC_PROPERTY "
					+ "AND prop.id = ptdem.ID_PROPERTY " 
					+ "AND bas.propertyid = :PropId ";

			Query qry = getCurrentSession().createSQLQuery(qryStr);
			qry.setString("PropId", basicProperty.getUpicNo());
			demandIds = qry.list();
		}
		return demandIds;

	}

	/**
	 * Method called to get EgDemandDetails Ids based on given
	 * EgDemand,Installment and Mastercode.
	 * 
	 * @param installment
	 *            - Installment in which DemandDetail belongs.
	 * @param egDemand
	 *            -EgDemand Object.
	 * @param demandReasonMasterCode
	 *            - EgDemandReasonMaster code
	 * @return egDemand - returns the list of Demands for the basicProperty.
	 * 
	 */

	@Override
	public List getDmdDetIdFromInstallandEgDemand(Installment installment, EgDemand egDemand,
			String demandReasonMasterCode) {
		List dmdIdList = new ArrayList();
		if (egDemand != null && installment != null) {
			StringBuffer strBuf = new StringBuffer(2000);
			strBuf.append(" SELECT dmdet.id FROM eg_demand_details dmdet, eg_demand_reason res , eg_demand_reason_master mast ");
			strBuf.append(" WHERE dmdet.id_demand_reason= res.id AND dmdet.id_demand =:dmdId AND res.id_installment =:instlId ");
			strBuf.append(" AND mast.id = res.id_demand_reason_master AND mast.code =:masterCode ");
			Query qry = getCurrentSession().createSQLQuery(strBuf.toString());
			qry.setLong("dmdId", egDemand.getId());
			qry.setInteger("instlId", installment.getId());
			qry.setString("masterCode", demandReasonMasterCode);
			dmdIdList = qry.list();
		}
		return dmdIdList;
	}

	/**
	 * Returns installment wise demand and collection for all the Demand reasons
	 * 
	 * @param egDemand
	 *            -EgDemand Object.
	 * @return returns list of installment and respective demand and collection
	 *         and rebate.
	 * 
	 */
	@Override
	public List getDmdCollForAllDmdReasons(EgDemand egDemand) {
		List list = new ArrayList();
		StringBuffer strBuf = new StringBuffer(2000);
		strBuf.append(" select dmdRes.ID_INSTALLMENT, sum(dmdDet.amount) as amount, sum(dmdDet.amt_collected) as amt_collected, sum(dmdDet.amt_rebate) as rebate, inst.start_date "
				+ "from eg_demand_details dmdDet ,eg_demand_reason dmdRes, eg_installment_master inst, eg_demand_reason_master dmdresmas "
				+ "where dmdDet.id_demand_reason=dmdRes.id "
				+ "and dmdDet.id_demand =:dmdId "
				+ "and dmdRes.id_installment = inst.id "
				+ "and dmdresmas.id = dmdres.id_demand_reason_master "
				+ "group by dmdRes.id_installment, inst.start_date " + "order by inst.start_date ");
		Query qry = getCurrentSession().createSQLQuery(strBuf.toString()).setLong("dmdId",
				egDemand.getId());
		return qry.list();
	}

	@Override
	public Property findById(Integer id, boolean lock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Property> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property create(Property property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Property property) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Property update(Property property) {
		// TODO Auto-generated method stub
		return null;
	}

}
