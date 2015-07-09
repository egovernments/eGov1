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
package org.egov.ptis.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.egov.demand.dao.DepreciationMasterDao;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.persistence.entity.Address;
import org.egov.infstr.utils.EGovConfig;
import org.egov.ptis.domain.dao.property.CategoryDao;
import org.egov.ptis.domain.dao.property.PropertyMutationMasterDAO;
import org.egov.ptis.domain.dao.property.PropertyOccupationDAO;
import org.egov.ptis.domain.dao.property.PropertySourceDAO;
import org.egov.ptis.domain.dao.property.PropertyStatusDAO;
import org.egov.ptis.domain.dao.property.PropertyTypeMasterDAO;
import org.egov.ptis.domain.dao.property.PropertyUsageDAO;
import org.egov.ptis.domain.dao.property.StructureClassificationDAO;
import org.egov.ptis.domain.dao.property.TaxPercDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.ConstructionTypeImpl;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyCreationReason;
import org.egov.ptis.domain.entity.property.PropertyOccupation;
import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;
import org.egov.ptis.domain.entity.property.PropertySource;
import org.egov.ptis.domain.entity.property.PropertyStatus;
import org.egov.ptis.domain.entity.property.PropertyUsage;
import org.egov.ptis.domain.entity.property.StructureClassification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author deepak YN
 * 
 */

public class PTISCacheManager implements PTISCacheManagerInteface {

	private static final Logger LOGGER = Logger.getLogger(PTISCacheManager.class);
	private static boolean reset = true;
	private static ArrayList propertyCreationReasonsList = new ArrayList();
	private static ArrayList structuralFactorslist = new ArrayList();
	private static ArrayList constructionTypeslist = new ArrayList();
	private static ArrayList allPropUsagelist = new ArrayList();
	private static ArrayList allPropOccTypeslist = new ArrayList();
	private static ArrayList allPropertyStatuslist = new ArrayList();
	private static ArrayList allPropertySourcelist = new ArrayList();
	private static ArrayList allAllTaxRatelist = new ArrayList();
	private static ArrayList allCategorieslist = new ArrayList();
	private static ArrayList allDepreciationRates = new ArrayList();
	private static HashMap allPropertySourceMap = new HashMap();
	private static HashMap allPropertyStatusMap = new HashMap();
	private static HashMap allPropUsageMap = new HashMap();
	private static HashMap allPropOccTypesMap = new HashMap();
	private static HashMap structuralFactorsMap = new HashMap();
	private static HashMap constructionTypesMap = new HashMap();
	private static HashMap propertyCreationReasonsMap = new HashMap();
	private static HashMap boundaryMap = new HashMap();
	public String cityName = EGovConfig.getProperty("CITY", "", "PT");
	public String block = EGovConfig.getProperty("ptis_egov_config.xml", "BNDRYTYPNAME3", "", "PT");
	public String street = EGovConfig
			.getProperty("ptis_egov_config.xml", "BNDRYTYPNAME4", "", "PT");
	@Autowired
	private static BoundaryService boundaryService;
	@Autowired
	private static PropertyTypeMasterDAO propertyTypeMasterDAO;
	@Autowired
	private static PropertyUsageDAO propertyUsageDAO;
	@Autowired
	private static StructureClassificationDAO structureClassificationDAO;
	@Autowired
	private static PropertyMutationMasterDAO propertyMutationMasterDAO;
	@Autowired
	private static PropertySourceDAO propertySourceDAO;
	@Autowired
	private static PropertyStatusDAO propertyStatusDAO;
	@Autowired
	private static PropertyOccupationDAO propertyOccupationDAO;
	@Autowired
	private static TaxPercDAO taxPercDAO;
	@Autowired
	private static CategoryDao categoryDAO;
	@Autowired
	private static DepreciationMasterDao depreciationMasterDAO;

	@Override
	public List getAllDepreciationRates() {
		if (reset) {
			update();
		}
		return allDepreciationRates;
	}

	@Override
	public List getPropertyCreationReasons() {
		if (reset) {
			update();
		}
		return propertyCreationReasonsList;
	}

	@Override
	public List getAllTaxRates() {
		if (reset) {
			update();
		}
		return allAllTaxRatelist;
	}

	@Override
	public List getAllStructuralFactors() {
		if (reset) {
			update();
		}
		return structuralFactorslist;
	}

	@Override
	public List getAllCategories() {
		if (reset) {
			update();
		}
		return allCategorieslist;
	}

	@Override
	public List getAllConstructionTypes() {
		if (reset) {
			update();
		}
		return constructionTypeslist;
	}

	@Override
	public List getAllPropUsage() {
		if (reset) {
			update();
		}
		return allPropUsagelist;
	}

	@Override
	public List getAllPropOccTypes() {
		if (reset) {
			update();
		}
		return allPropOccTypeslist;
	}

	@Override
	public List getAllPropertyStatus() {
		if (reset) {
			update();
		}
		return allPropertyStatuslist;
	}

	@Override
	public List getAllPropertySource() {
		if (reset) {
			update();
		}
		return allPropertySourcelist;
	}

	@Override
	public PropertySource getPropertySourceById(Integer propSrcId) {
		if (reset) {
			update();
		}
		return (PropertySource) allPropertySourceMap.get(propSrcId);
	}

	@Override
	public PropertyStatus getPropertyStatusById(Integer propStatusId) {
		if (reset) {
			update();
		}
		return (PropertyStatus) allPropertyStatusMap.get(propStatusId);
	}

	@Override
	public PropertyUsage getPropertyUsageById(Integer propUsageId) {
		if (reset) {
			update();
		}
		return (PropertyUsage) allPropUsageMap.get(propUsageId);
	}

	@Override
	public PropertyOccupation getPropertyOccupationById(Integer propOccId) {
		if (reset) {
			update();
		}
		return (PropertyOccupation) allPropOccTypesMap.get(propOccId);
	}

	@Override
	public StructureClassification getStructureClassificationById(Integer strucClssfnId) {
		if (reset) {
			update();
		}
		return (StructureClassification) structuralFactorsMap.get(strucClssfnId);
	}

	@Override
	public ConstructionTypeImpl getConstructionTypeById(Integer id) {
		if (reset) {
			update();
		}
		return (ConstructionTypeImpl) constructionTypesMap.get(id);
	}

	@Override
	public PropertyCreationReason getReasonById(Integer id) {
		if (reset) {
			update();
		}
		return (PropertyCreationReason) propertyCreationReasonsMap.get(id);
	}

	@Override
	public Boundary getBoundary(Integer id) {
		if (reset) {
			update();
		}
		return (Boundary) boundaryMap.get(id);
	}

	private static synchronized void update() {
		LOGGER.debug("Starting update in PTISCacheManager util.........." + reset);
		String cessLibId = EGovConfig.getProperty("ptis_egov_config.xml", "LIB_CESSID", "", "PT");
		LOGGER.info("cessLibId" + cessLibId);
		String cessBegId = EGovConfig.getProperty("ptis_egov_config.xml", "BEG_CESSID", "", "PT");
		LOGGER.info("cessBegId" + cessBegId);
		String cessHelthId = EGovConfig
				.getProperty("ptis_egov_config.xml", "HLTH_CESSID", "", "PT");
		LOGGER.info("cessHelthId" + cessHelthId);
		String totalCessId = EGovConfig.getProperty("ptis_egov_config.xml", "TOTAL_CESSID", "",
				"PT");
		LOGGER.info("totalCessId" + totalCessId);
		propertyCreationReasonsList = new ArrayList();
		structuralFactorslist = new ArrayList();
		constructionTypeslist = new ArrayList();
		allPropUsagelist = new ArrayList();
		allPropOccTypeslist = new ArrayList();
		allPropertyStatuslist = new ArrayList();
		allPropertySourcelist = new ArrayList();
		allPropertySourceMap = new HashMap();
		allPropertyStatusMap = new HashMap();
		allPropUsageMap = new HashMap();
		allPropOccTypesMap = new HashMap();
		structuralFactorsMap = new HashMap();
		constructionTypesMap = new HashMap();
		propertyCreationReasonsMap = new HashMap();
		boundaryMap = new HashMap();
		allAllTaxRatelist = new ArrayList();
		allCategorieslist = new ArrayList();
		allDepreciationRates = new ArrayList();
		try {
			/*
			 * PropertyTypeMasterDAO propTypeMstrDao = PropertyDAOFactory
			 * .getDAOFactory().getPropertyTypeMasterDAO(); PropertyUsageDAO
			 * propertyUsageDAO = PropertyDAOFactory
			 * .getDAOFactory().getPropertyUsageDAO();
			 * StructureClassificationDAO strucClsfnDao = PropertyDAOFactory
			 * .getDAOFactory().getStructureClassificationDAO();
			 * PropertyMutationMasterDAO propMutMstrDao = PropertyDAOFactory
			 * .getDAOFactory().getPropertyMutationMstrDAO(); PropertySourceDAO
			 * propSrcDao = PropertyDAOFactory.getDAOFactory()
			 * .getPropertySourceDAO(); PropertyStatusDAO propStatusDao =
			 * PropertyDAOFactory .getDAOFactory().getPropertyStatusDAO();
			 * PropertyUsageDAO propUsageDAO =
			 * PropertyDAOFactory.getDAOFactory() .getPropertyUsageDAO();
			 * PropertyOccupationDAO propOccDAO = PropertyDAOFactory
			 * .getDAOFactory().getPropertyOccupationDAO(); TaxPercDAO
			 * taxPercDAO = PropertyDAOFactory.getDAOFactory() .getTaxPercDao();
			 * CategoryDao categoryDao = PropertyDAOFactory.getDAOFactory()
			 * .getCategoryDao();
			 */

			allPropertySourcelist = (ArrayList) propertySourceDAO.findAll();
			Iterator allPropertySourcelistIter = allPropertySourcelist.iterator();
			while (allPropertySourcelistIter.hasNext()) {
				PropertySource obj = (PropertySource) allPropertySourcelistIter.next();
				PropertySource propertySource = (PropertySource) propertySourceDAO.findById(
						obj.getID(), false);
				allPropertySourceMap.put(obj.getID(), propertySource);
			}
			allPropertyStatuslist = (ArrayList) propertyStatusDAO.findAll();
			Iterator allPropertyStatuslistIter = allPropertyStatuslist.iterator();
			while (allPropertyStatuslistIter.hasNext()) {
				PropertyStatus obj = (PropertyStatus) allPropertyStatuslistIter.next();
				PropertyStatus propertyStatus = (PropertyStatus) propertyStatusDAO.findById(
						obj.getID(), false);
				allPropertyStatusMap.put(obj.getID(), propertyStatus);
			}
			allPropUsagelist = (ArrayList) propertyUsageDAO.findAll();
			Iterator allPropUsagelistIter = allPropUsagelist.iterator();
			while (allPropUsagelistIter.hasNext()) {
				PropertyUsage obj = (PropertyUsage) allPropUsagelistIter.next();
				PropertyUsage propertyUsage = (PropertyUsage) propertyUsageDAO.findById(
						obj.getId(), false);
				allPropUsageMap.put(obj.getId(), propertyUsage);
			}
			allPropOccTypeslist = (ArrayList) propertyOccupationDAO.findAll();
			Iterator allPropOccTypeslistIter = allPropOccTypeslist.iterator();
			while (allPropOccTypeslistIter.hasNext()) {
				PropertyOccupation obj = (PropertyOccupation) allPropOccTypeslistIter.next();
				PropertyOccupation propertyOccupation = (PropertyOccupation) propertyOccupationDAO
						.findById(obj.getId(), false);
				allPropOccTypesMap.put(obj.getId(), propertyOccupation);
			}
			structuralFactorslist = (ArrayList) structureClassificationDAO.findAll();
			Iterator structuralFactorslistIter = structuralFactorslist.iterator();
			while (structuralFactorslistIter.hasNext()) {
				StructureClassification obj = (StructureClassification) structuralFactorslistIter
						.next();
				StructureClassification structureClassification = (StructureClassification) structureClassificationDAO
						.findById(obj.getId(), false);
				structuralFactorsMap.put(obj.getId(), structureClassification);
			}
			/*
			 * constructionTypeslist =
			 * (ArrayList)getPropertyManager().getAllConstructionTypes();
			 * Iterator constructionTypesIter =constructionTypeslist.iterator();
			 * while(constructionTypesIter.hasNext()) { ConstructionTypeImpl obj
			 * = (ConstructionTypeImpl)constructionTypesIter.next();
			 * ConstructionTypeImpl constructionTypeImpl =
			 * getPropertyManager().getConstructionTypeById(obj.getID());
			 * constructionTypesMap.put(obj.getID(),constructionTypeImpl); }
			 */
			List<Boundary> boundaries = boundaryService.getAllBoundaries();
			for (Boundary boundary : boundaries) {
				boundaryMap.put(boundary.getId(), boundary);
			}

			allAllTaxRatelist = (ArrayList) taxPercDAO.findAll();
			allCategorieslist = (ArrayList) categoryDAO.findAll();
			allDepreciationRates = (ArrayList) depreciationMasterDAO.findAll();

		} catch (Exception sqe) {
			LOGGER.info("Exception in update()-----PTISCacheManager----" + sqe.getMessage());
			throw new EGOVRuntimeException(sqe.getMessage());
		}
		reset = false;
	}

	/*
	 * buiding property address from basic property's old municipal no and
	 * basicProperty's address object(i.e streetaddress1,citytownvillage,pincode
	 * from db columns
	 */
	@Override
	public String buildAddress(BasicProperty basicProperty) {
		if (basicProperty == null) {
			throw new EGOVRuntimeException("Internal Server Error  BasicProperty is Null!!");
		}
		Address address = basicProperty.getAddress();
		String addressStr = "";
		if (address == null) {
			throw new EGOVRuntimeException(
					"Internal Server Error in Searching Property Address is Null!!");
		}
		if (basicProperty.getOldMuncipalNum() != null) {
			addressStr = "(Old No. " + basicProperty.getOldMuncipalNum() + " )";
		}

		return buildAddressFromAddress(address);

	}

	/*
	 * this method used to build owner name by taking ownerset as parameter.if
	 * owner firstname exists then bulid ownername else take firnamelocal and
	 * build ownername
	 */
	@Override
	public String buildOwnerFullName(List<PropertyOwnerInfo> ownerSet) {
		if (ownerSet == null) {
			throw new EGOVRuntimeException("Property Owner set is null...");
		}
		String ownerFullName = "";
		Set<String> ownerNameSet = new HashSet<String>();
		for (PropertyOwnerInfo propOwnerInfo : ownerSet) {
		        User propOwner = propOwnerInfo.getOwner();
			LOGGER.debug("buildOwnerFullName : Owner id " + propOwner.getId());
			if (propOwner.getName() != null && !propOwner.getName().trim().equals("")) {
				if (!ownerNameSet.contains(propOwner.getName().trim())) {
					if (!ownerFullName.trim().equals("")) {
						if (!ownerFullName.equals("")) {
							ownerFullName += ", ";
						}
					}
					ownerNameSet.add(propOwner.getName().trim());
					ownerFullName = (propOwner.getName() == null ? "" : propOwner.getName());
					LOGGER.debug("buildOwnerFullName : ownerFullNameEnglish : " + ownerFullName);
				}
			}

		}
		LOGGER.debug("buildOwnerFullName : ownerFullName" + ownerFullName);
		return ownerFullName;
	}

	/*
	 * getTxPercWithUsg will take lstTaxRates as parameter and returns Map
	 * (key-Integer,business meaning id of TaxPerc, value-String ,business
	 * meaning taxperc-usg)
	 */
	@Override
	public Map getTxPercWithUsg(List lstTaxRates) {

		Iterator ItrlstTaxRates = lstTaxRates.iterator();
		Map taxRatesMap = new HashMap();
		while (ItrlstTaxRates.hasNext()) {
			org.egov.ptis.domain.entity.property.TaxPerc txPerc = (org.egov.ptis.domain.entity.property.TaxPerc) ItrlstTaxRates
					.next();
			if (txPerc != null) {
				txPerc.getTax_perc();
				txPerc.getPropertyUsage().getUsageName();
				String usgwithTx = txPerc.getTax_perc().toString() + "-"
						+ txPerc.getPropertyUsage().getUsageName();
				LOGGER.info("usgwithTx " + usgwithTx);
				taxRatesMap.put(txPerc.getId(), usgwithTx);

			}
		}
		return taxRatesMap;
	}

	/*
	 * buildOwnerFullName this API returns String of ownerName by passing
	 * BasicProperty as parameter and get all properties owners and add all
	 * those to a set and calls overloaded method buildOwnerFullName(ownerSet);
	 */
	@Override
	public String buildOwnerFullName(BasicProperty bp) {
		return buildOwnerFullName(bp.getPropertyOwnerInfo());
	}

	@Override
	public String buildAddressFromAddress(Address address) {
		String addressStr = "";
		if (address != null) {
			addressStr = (address.getHouseNoBldgApt() == null ? " " : address.getHouseNoBldgApt());
			if (!addressStr.trim().equals("")) {
				addressStr = addressStr
						+ (address.getLandmark() == null ? " " : ", " + address.getLandmark());
			} else {
				addressStr = (address.getLandmark() == null ? " " : address.getLandmark());
			}

			if (!addressStr.trim().equals("")) {
				addressStr = addressStr
						+ (address.getAreaLocalitySector() == null ? "" : ", "
								+ address.getAreaLocalitySector());
				addressStr = addressStr
						+ (address.getCityTownVillage() == null ? "" : ", "
								+ address.getCityTownVillage());
				addressStr = addressStr
						+ (address.getPinCode() == null ? "" : ", "
								+ address.getPinCode().toString());
			} else {
				addressStr = addressStr
						+ (address.getAreaLocalitySector() == null ? "" : ", "
								+ address.getAreaLocalitySector());
				addressStr = (address.getCityTownVillage() == null ? "" : address
						.getCityTownVillage());

				if (!addressStr.trim().equals("")) {
					addressStr = addressStr
							+ (address.getPinCode() == null ? "" : ", "
									+ address.getPinCode().toString());
				} else {
					addressStr = addressStr
							+ (address.getPinCode() == null ? "" : address.getPinCode().toString());
				}
			}
		}
		return addressStr;

	}

	@Override
	public String buildAddressByImplemetation(Address address) {
		StringBuilder addressStr = new StringBuilder("");
		addressStr.append(address.getHouseNoBldgApt() != null ? address.getHouseNoBldgApt() : " ");
		addressStr.append(
				address.getAreaLocalitySector() != null ? address.getAreaLocalitySector() : " ");
		addressStr
				.append(address.getCityTownVillage() != null ? address.getCityTownVillage() : " ");
		addressStr.append(address.getLandmark() != null ? address.getLandmark() : " ");
		addressStr.append(
				address.getPinCode() != null ? " -  " + address.getPinCode().toString() : " ");
		return addressStr.toString();
	}

}
