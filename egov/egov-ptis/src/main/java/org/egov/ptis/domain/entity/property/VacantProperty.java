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
/*
 * VacantProperty.java Created on Oct 21, 2005
 *
 * Copyright 2005 eGovernments Foundation. All rights reserved.
 * EGOVERNMENTS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.egov.ptis.domain.entity.property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.commons.Area;
import org.egov.commons.Installment;
import org.egov.exceptions.InvalidPropertyException;

/**
 * The Implementation Class for the VacantProperty
 * 
 * @author Neetu
 * @version 2.00
 * @see org.egov.ptis.domain.entity.property.PropertyDetail
 *      org.egov.ptis.property.model.AbstractProperty,
 * 
 */
public class VacantProperty extends AbstractProperty {

	private static final Logger LOGGER = Logger.getLogger(VacantProperty.class);
	private Area sitalArea;
	private Area totalBuiltupArea;
	private Area commBuiltUpArea;
	private Area plinthArea;
	private Area commVacantLand;
	private Area nonResPlotArea;
	private Boolean irregular;
	private String surveyNumber;
	private Character fieldVerified;
	private java.util.Date fieldVerificationDate;
	private java.util.List<Floor> floorDetails = new ArrayList<Floor>();
	/* private List<FloorImpl> floorDetailsProxy = new ArrayList<FloorImpl>(); */
	private Integer propertyDetailsID;
	private String water_Meter_Num;
	private String elec_Meter_Num;
	private Integer no_of_floors;
	private char fieldIrregular = 'N';
	private Date completion_year;
	private Date effective_date;
	private Date dateOfCompletion;
	private Property property;
	private Date updatedTime;
	private PropertyUsage propertyUsage;
	private PropertyCreationReason creationReason;
	private PropertyTypeMaster propertyTypeMaster;
	private String propertyType;
	private Installment installment;
	private PropertyOccupation propertyOccupation;
	private PropertyMutationMaster propertyMutationMaster;
	private Character comZone = 'N';
	private Character cornerPlot = 'N';
	private boolean lift = false;
	private boolean toilets = false;
	private boolean waterTap = false;
	private boolean structure = false;
	private boolean drainage = false;
	private boolean electricity = false;
	private boolean attachedBathRoom = false;
	private boolean waterHarvesting = false;
	private boolean cable = false;
	private double extentSite;
	private double extentAppartenauntLand;
	private String siteOwner;
	private FloorType floorType;
	private RoofType roofType;
	private WallType wallType;
	private WoodType woodType;
	private Apartment apartment = null;

	public VacantProperty(Area sitalArea, Area totalBuiltupArea, Area commBuiltUpArea,
			Area plinthArea, Area commVacantLand, Area nonResPlotArea, Boolean irregular,
			String surveyNumber, Character fieldVerified, Date fieldVerificationDate,
			List<Floor> floorDetails, Integer propertyDetailsID, String water_Meter_Num,
			String elec_Meter_Num, Integer no_of_floors, char fieldIrregular, Date completion_year,
			Date effective_date, Date dateOfCompletion, Property property, Date updatedTime,
			PropertyUsage propertyUsage, PropertyCreationReason creationReason,
			PropertyTypeMaster propertyTypeMaster, String propertyType, Installment installment,
			PropertyOccupation propertyOccupation, PropertyMutationMaster propertyMutationMaster,
			Character comZone, Character cornerPlot, double extentSite,
			double extentAppartenauntLand, FloorType floorType, RoofType roofType,
			WallType wallType, WoodType woodType, boolean lift, boolean toilets, boolean waterTap,
			boolean structure, boolean drainage, boolean electricity, boolean attachedBathRoom,
			boolean waterHarvesting, boolean cable, String siteOwner) {
		super();
		this.sitalArea = sitalArea;
		this.totalBuiltupArea = totalBuiltupArea;
		this.commBuiltUpArea = commBuiltUpArea;
		this.plinthArea = plinthArea;
		this.commVacantLand = commVacantLand;
		this.nonResPlotArea = nonResPlotArea;
		this.irregular = irregular;
		this.surveyNumber = surveyNumber;
		this.fieldVerified = fieldVerified;
		this.fieldVerificationDate = fieldVerificationDate;
		this.floorDetails = floorDetails;
		this.propertyDetailsID = propertyDetailsID;
		this.water_Meter_Num = water_Meter_Num;
		this.elec_Meter_Num = elec_Meter_Num;
		this.no_of_floors = no_of_floors;
		this.fieldIrregular = fieldIrregular;
		this.completion_year = completion_year;
		this.effective_date = effective_date;
		this.dateOfCompletion = dateOfCompletion;
		this.property = property;
		this.updatedTime = updatedTime;
		this.propertyUsage = propertyUsage;
		this.creationReason = creationReason;
		this.propertyTypeMaster = propertyTypeMaster;
		this.propertyType = propertyType;
		this.installment = installment;
		this.propertyOccupation = propertyOccupation;
		this.propertyMutationMaster = propertyMutationMaster;
		this.comZone = comZone;
		this.cornerPlot = cornerPlot;
		this.extentSite = extentSite;
		this.extentAppartenauntLand = extentAppartenauntLand;
		this.wallType = wallType;
		this.roofType = roofType;
		this.woodType = woodType;
		this.floorType = floorType;
		this.lift = lift;
		this.toilets = toilets;
		this.waterTap = waterTap;
		this.structure = structure;
		this.drainage = drainage;
		this.electricity = electricity;
		this.attachedBathRoom = attachedBathRoom;
		this.waterHarvesting = waterHarvesting;
		this.cable = cable;
		this.siteOwner = siteOwner;
	}

	public Date getDateOfCompletion() {
		return dateOfCompletion;
	}

	public void setDateOfCompletion(Date dateOfCompletion) {
		this.dateOfCompletion = dateOfCompletion;
	}

	public void addFloor(Floor floor) {
		LOGGER.debug("BuildUpFloor.addFloor");
		if (floor != null) {
			getFloorDetails().add(floor);
			no_of_floors = getFloorDetails().size();
		}
	}

	/**
	 * This method removes the Floor Object from the Set view of the Floor
	 * 
	 * @param floor
	 *            The floor to set .
	 */
	public void removeFloor(Floor floor) {
		LOGGER.debug("BuildUpFloor.removeFloor");
		getFloorDetails().remove(floor);
		no_of_floors = getFloorDetails().size();
	}

	/**
	 * @return Returns the commBuiltUpArea.
	 */
	public Area getCommBuiltUpArea() {
		return commBuiltUpArea;
	}

	/**
	 * @param commBuiltUpArea
	 *            The commBuiltUpArea to set.
	 */
	public void setCommBuiltUpArea(Area commBuiltUpArea) {
		this.commBuiltUpArea = commBuiltUpArea;
	}

	/**
	 * @return Returns the commVacantLand.
	 */
	public Area getCommVacantLand() {
		return commVacantLand;
	}

	/**
	 * @param commVacantLand
	 *            The commVacantLand to set.
	 */
	public void setCommVacantLand(Area commVacantLand) {
		this.commVacantLand = commVacantLand;
	}

	/**
	 * @return Returns the completion_year.
	 */
	public Date getCompletion_year() {
		return completion_year;
	}

	/**
	 * @param completion_year
	 *            The completion_year to set.
	 */
	public void setCompletion_year(Date completion_year) {
		this.completion_year = completion_year;
	}

	/**
	 * @return Returns the creationReason.
	 */
	public PropertyCreationReason getCreationReason() {
		return creationReason;
	}

	/**
	 * @param creationReason
	 *            The creationReason to set.
	 */
	public void setCreationReason(PropertyCreationReason creationReason) {
		this.creationReason = creationReason;
	}

	/**
	 * @return Returns the effective_date.
	 */
	public Date getEffective_date() {
		return effective_date;
	}

	/**
	 * @param effective_date
	 *            The effective_date to set.
	 */
	public void setEffective_date(Date effective_date) {
		this.effective_date = effective_date;
	}

	/**
	 * @return Returns the elec_Meter_Num.
	 */
	public String getElec_Meter_Num() {
		return elec_Meter_Num;
	}

	/**
	 * @param elec_Meter_Num
	 *            The elec_Meter_Num to set.
	 */
	public void setElec_Meter_Num(String elec_Meter_Num) {
		this.elec_Meter_Num = elec_Meter_Num;
	}

	/**
	 * @return Returns the fieldIrregular.
	 */
	public char getFieldIrregular() {
		return fieldIrregular;
	}

	/**
	 * @param fieldIrregular
	 *            The fieldIrregular to set.
	 */
	public void setFieldIrregular(char fieldIrregular) {
		this.fieldIrregular = fieldIrregular;
	}

	/**
	 * @return Returns the fieldVerificationDate.
	 */
	public java.util.Date getFieldVerificationDate() {
		return fieldVerificationDate;
	}

	/**
	 * @param fieldVerificationDate
	 *            The fieldVerificationDate to set.
	 */
	public void setFieldVerificationDate(java.util.Date fieldVerificationDate) {
		this.fieldVerificationDate = fieldVerificationDate;
	}

	/**
	 * @return Returns the fieldVerified.
	 */
	public Character getFieldVerified() {
		return fieldVerified;
	}

	/**
	 * @param fieldVerified
	 *            The fieldVerified to set.
	 */
	public void setFieldVerified(Character fieldVerified) {
		this.fieldVerified = fieldVerified;
	}

	/**
	 * @return Returns the floorDetails.
	 */
	public java.util.List<Floor> getFloorDetails() {
		return floorDetails;
	}

	/**
	 * @param floorDetails
	 *            The floorDetails to set.
	 */
	public void setFloorDetails(java.util.List<Floor> floorDetails) {
		this.floorDetails = floorDetails;
	}

	/**
	 * @return Returns the irregular.
	 */
	public Boolean getIrregular() {
		return irregular;
	}

	/**
	 * @param irregular
	 *            The irregular to set.
	 */
	public void setIrregular(Boolean irregular) {
		this.irregular = irregular;
	}

	/**
	 * @return Returns the no_of_floors.
	 */
	public Integer getNo_of_floors() {
		return no_of_floors;
	}

	/**
	 * @param no_of_floors
	 *            The no_of_floors to set.
	 */
	public void setNo_of_floors(Integer no_of_floors) {
		this.no_of_floors = no_of_floors;
	}

	/**
	 * @return Returns the plinthArea.
	 */
	public Area getPlinthArea() {
		return plinthArea;
	}

	/**
	 * @param plinthArea
	 *            The plinthArea to set.
	 */
	public void setPlinthArea(Area plinthArea) {
		this.plinthArea = plinthArea;
	}

	/**
	 * @return Returns the property.
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            The property to set.
	 */
	public void setProperty(Property property) {
		this.property = property;
	}

	/**
	 * @return Returns the propertyDetailsID.
	 */
	public Integer getPropertyDetailsID() {
		return propertyDetailsID;
	}

	/**
	 * @param propertyDetailsID
	 *            The propertyDetailsID to set.
	 */
	public void setPropertyDetailsID(Integer propertyDetailsID) {
		this.propertyDetailsID = propertyDetailsID;
	}

	/**
	 * @return Returns the propertyTypeMaster.
	 */
	public PropertyTypeMaster getPropertyTypeMaster() {
		return propertyTypeMaster;
	}

	/**
	 * @param propertyTypeMaster
	 *            The propertyTypeMaster to set.
	 */
	public void setPropertyTypeMaster(PropertyTypeMaster propertyTypeMaster) {
		this.propertyTypeMaster = propertyTypeMaster;
	}

	/**
	 * @return Returns the propertyUsage.
	 */
	public PropertyUsage getPropertyUsage() {
		return propertyUsage;
	}

	/**
	 * @param propertyUsage
	 *            The propertyUsage to set.
	 */
	public void setPropertyUsage(PropertyUsage propertyUsage) {
		this.propertyUsage = propertyUsage;
	}

	/**
	 * @return Returns the sitalArea.
	 */
	public Area getSitalArea() {
		return sitalArea;
	}

	/**
	 * @param sitalArea
	 *            The sitalArea to set.
	 */
	public void setSitalArea(Area sitalArea) {
		this.sitalArea = sitalArea;
	}

	/**
	 * @return Returns the surveyNumber.
	 */
	public String getSurveyNumber() {
		return surveyNumber;
	}

	/**
	 * @param surveyNumber
	 *            The surveyNumber to set.
	 */
	public void setSurveyNumber(String surveyNumber) {
		this.surveyNumber = surveyNumber;
	}

	/**
	 * @return Returns the totalBuiltupArea.
	 */
	public Area getTotalBuiltupArea() {
		return totalBuiltupArea;
	}

	/**
	 * @param totalBuiltupArea
	 *            The totalBuiltupArea to set.
	 */
	public void setTotalBuiltupArea(Area totalBuiltupArea) {
		this.totalBuiltupArea = totalBuiltupArea;
	}

	/**
	 * @return Returns the updatedTime.
	 */
	public Date getUpdatedTime() {
		return updatedTime;
	}

	/**
	 * @param updatedTime
	 *            The updatedTime to set.
	 */
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	/**
	 * @return Returns the water_Meter_Num.
	 */
	public String getWater_Meter_Num() {
		return water_Meter_Num;
	}

	/**
	 * @param water_Meter_Num
	 *            The water_Meter_Num to set.
	 */
	public void setWater_Meter_Num(String water_Meter_Num) {
		this.water_Meter_Num = water_Meter_Num;
	}

	/**
	 * @return Returns the propertyType.
	 */
	public String getPropertyType() {
		return propertyType;
	}

	/**
	 * @param propertyType
	 *            The propertyType to set.
	 */
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	/**
	 * @return Returns the installment.
	 */
	public Installment getInstallment() {
		return installment;
	}

	/**
	 * @param installment
	 *            The installment to set.
	 */
	public void setInstallment(Installment installment) {
		this.installment = installment;
	}

	/**
	 * @return Returns if the given Object is equal to PropertyImpl
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (this == obj)
			return true;

		if (!(obj instanceof VacantProperty))
			return false;

		final VacantProperty other = (VacantProperty) obj;

		if (getId() != null || other.getId() != null) {
			if (getId().equals(other.getId())) {
				return true;
			}
			return false;
		} else if (getProperty() != null || other.getProperty() != null) {
			if (getProperty().equals(other.getProperty())) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	/**
	 * @return Returns the hashCode
	 */
	public int hashCode() {
		int hashCode = 0;
		if (getId() != null) {
			hashCode = hashCode + this.getId().hashCode();
		} else if ((getProperty() != null)) {
			hashCode = hashCode + this.getProperty().hashCode();
		}
		return hashCode;
	}

	/**
	 * @return Returns the boolean after validating the current object
	 */
	public boolean validateProperty() throws InvalidPropertyException {
		if (getSitalArea() == null) {
			throw new InvalidPropertyException(
					"VacantProperty.validate : SitalArea Data is NULL, Please Check !!");
		}
		/*
		 * if(getTotalBuiltupArea() == null) throw newEGOVRuntimeException(
		 * "VacantProperty.validate : TotalBuiltUpArea is NULL, Please Check !!"
		 * );
		 */

		if (getPropertyAddress() == null) {
			throw new InvalidPropertyException(
					"VacantProperty.validate : PropertyAddress is NULL, Please Check !!");
		}
		if (getProperty() == null) {
			throw new InvalidPropertyException(
					"VacantProperty.validate : Property is NULL, Please Check !!");
		} else if (!getProperty().validateProperty()) {
			throw new InvalidPropertyException(
					"VacantProperty.validate : Property Validate() failed, Please Check !!");
		}
		// can't use validate, not implemented
		/*
		 * if(getBoundary() == null) throw newEGOVRuntimeException(
		 * "VacantProperty.validate : Boundary is NULL, Please Check !!");
		 */
		/*
		 * if(getAddress() == null) throw newEGOVRuntimeException(
		 * "VacantProperty.validate : Address is NULL, Please Check !!"); else
		 * if(getAddress().validate() == false) throw newEGOVRuntimeException(
		 * "VacantProperty.validate : Address Validate() failed, Please Check
		 * !!" );
		 */
		if (getPropertySource() == null) {
			throw new InvalidPropertyException(
					"VacantProperty.validate : PropertySource is NULL, Please Check !!");
		} else if (!getPropertySource().validate()) {
			throw new InvalidPropertyException(
					"VacantProperty.validate : PropertySource Validate() failed, Please Check !!");
		}
		/*
		 * if(getPropertyUsage() == null) throw newEGOVRuntimeException(
		 * "VacantProperty.validate : PropertyUsage is NULL, Please Check !!");
		 * else if(getPropertyUsage().validate() == false) throw new
		 * EGOVRuntimeException( "VacantProperty.validate : PropertyUsage
		 * Validate() failed, Please Check !!" );
		 */
		return true;
	}

	/**
	 * @return Returns the propertyMutationMaster.
	 */
	public PropertyMutationMaster getPropertyMutationMaster() {
		return propertyMutationMaster;
	}

	/**
	 * @param propertyMutationMaster
	 *            The propertyMutationMaster to set.
	 */
	public void setPropertyMutationMaster(PropertyMutationMaster propertyMutationMaster) {
		this.propertyMutationMaster = propertyMutationMaster;
	}

	public Character getComZone() {
		return comZone;
	}

	public void setComZone(Character comZone) {
		this.comZone = comZone;
	}

	public Character getCornerPlot() {
		return cornerPlot;
	}

	public void setCornerPlot(Character cornerPlot) {
		this.cornerPlot = cornerPlot;
	}

	public PropertyOccupation getPropertyOccupation() {
		return propertyOccupation;
	}

	public void setPropertyOccupation(PropertyOccupation propertyOccupation) {
		this.propertyOccupation = propertyOccupation;
	}

	/*
	 * public List<FloorImpl> getFloorDetailsProxy() {
	 * getFloorDetails().addAll(floorDetailsProxy); return floorDetailsProxy; }
	 * 
	 * public void setFloorDetailsProxy(List<FloorImpl> floorDetailsProxy) {
	 * this.floorDetailsProxy = floorDetailsProxy;
	 * getFloorDetails().addAll(floorDetailsProxy); }
	 */

	public VacantProperty() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder objStr = new StringBuilder();

		objStr.append("Id: ").append(getId()).append("|Sital Area: ")
				.append(getSitalArea().getArea()).append("|NoOfFloors: ").append(getNo_of_floors());

		return objStr.toString();
	}

	@Override
	public Area getNonResPlotArea() {
		return nonResPlotArea;
	}

	@Override
	public void setNonResPlotArea(Area nonResPlotArea) {
		this.nonResPlotArea = nonResPlotArea;
	}

	@Override
	public boolean isLift() {
		return lift;
	}

	@Override
	public void setLift(boolean lift) {
		this.lift = lift;
	}

	@Override
	public boolean isToilets() {
		return toilets;
	}

	@Override
	public void setToilets(boolean toilets) {
		this.toilets = toilets;
	}

	@Override
	public boolean isWaterTap() {
		return waterTap;
	}

	@Override
	public void setWaterTap(boolean waterTap) {
		this.waterTap = waterTap;
	}

	@Override
	public boolean isStructure() {
		return structure;
	}

	@Override
	public void setStructure(boolean structure) {
		this.structure = structure;
	}

	@Override
	public boolean isDrainage() {
		return drainage;
	}

	@Override
	public void setDrainage(boolean drainage) {
		this.drainage = drainage;
	}

	@Override
	public boolean isElectricity() {
		return electricity;
	}

	@Override
	public void setElectricity(boolean electricity) {
		this.electricity = electricity;
	}

	@Override
	public boolean isAttachedBathRoom() {
		return attachedBathRoom;
	}

	@Override
	public void setAttachedBathRoom(boolean attachedBathRoom) {
		this.attachedBathRoom = attachedBathRoom;
	}

	@Override
	public boolean isWaterHarvesting() {
		return waterHarvesting;
	}

	@Override
	public void setWaterHarvesting(boolean waterHarvesting) {
		this.waterHarvesting = waterHarvesting;
	}

	@Override
	public boolean isCable() {
		return cable;
	}

	@Override
	public void setCable(boolean cable) {
		this.cable = cable;
	}

	@Override
	public double getExtentSite() {
		return extentSite;
	}

	@Override
	public void setExtentSite(double extentSite) {
		this.extentSite = extentSite;
	}

	@Override
	public double getExtentAppartenauntLand() {
		return extentAppartenauntLand;
	}

	@Override
	public void setExtentAppartenauntLand(double extentAppartenauntLand) {
		this.extentAppartenauntLand = extentAppartenauntLand;
	}

	@Override
	public String getSiteOwner() {
		return siteOwner;
	}

	@Override
	public void setSiteOwner(String siteOwner) {
		this.siteOwner = siteOwner;
	}

	@Override
	public FloorType getFloorType() {
		return floorType;
	}

	@Override
	public void setFloorType(FloorType floorType) {
		this.floorType = floorType;
	}

	@Override
	public RoofType getRoofType() {
		return roofType;
	}

	@Override
	public void setRoofType(RoofType roofType) {
		this.roofType = roofType;
	}

	@Override
	public WallType getWallType() {
		return wallType;
	}

	@Override
	public void setWallType(WallType wallType) {
		this.wallType = wallType;
	}

	@Override
	public WoodType getWoodType() {
		return woodType;
	}

	@Override
	public void setWoodType(WoodType woodType) {
		this.woodType = woodType;
	}
	
	@Override
	public Apartment getApartment() {
		return apartment;
	}
    
	@Override
	public void setApartment(Apartment apartment) {
		this.apartment = apartment;
	}
	

}
