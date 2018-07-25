package org.egov.ptis.bean.aadharseeding;

import java.util.Date;
import java.util.List;

import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;

public class AadharSeedingRequest {

    private Long wardId;
    private Long electionWardId;
    private String doorNo;
    private String assessmentNo;
    private String address;
    private String ownershipCategory;
    private String zoneName;
    private String revenueWardName;
    private String blockName;
    private String electionWardName;
    private String localty;
    private List<PropertyOwnerInfo> propertyOwnerInfo;
    private List<PropertyOwnerInfo> propertyOwnerInfoProxy;
    private Double latitude;
    private Double longitude;
    private Double extentOfSite;
    private Float plinthArea;
    private String propertyType;
    private String docNo;
    private Date docDate;
    private String surveyNumber;
    private String documentType;
    private String successMessage;
    private String status;
    
    
    public Long getWardId() {
        return wardId;
    }

    public void setWardId(Long wardId) {
        this.wardId = wardId;
    }

    public Long getElectionWardId() {
        return electionWardId;
    }

    public void setElectionWardId(Long electionWardId) {
        this.electionWardId = electionWardId;
    }

    public String getDoorNo() {
        return doorNo;
    }

    public void setDoorNo(String doorNo) {
        this.doorNo = doorNo;
    }

    public String getAssessmentNo() {
        return assessmentNo;
    }

    public void setAssessmentNo(String assessmentNo) {
        this.assessmentNo = assessmentNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwnershipCategory() {
        return ownershipCategory;
    }

    public void setOwnershipCategory(String ownershipCategory) {
        this.ownershipCategory = ownershipCategory;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getRevenueWardName() {
        return revenueWardName;
    }

    public void setRevenueWardName(String revenueWardName) {
        this.revenueWardName = revenueWardName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getElectionWardName() {
        return electionWardName;
    }

    public void setElectionWardName(String electionWardName) {
        this.electionWardName = electionWardName;
    }

    public String getLocalty() {
        return localty;
    }

    public void setLocalty(String localty) {
        this.localty = localty;
    }

    public List<PropertyOwnerInfo> getPropertyOwnerInfo() {
        return propertyOwnerInfo;
    }

    public void setPropertyOwnerInfo(List<PropertyOwnerInfo> propertyOwnerInfo) {
        this.propertyOwnerInfo = propertyOwnerInfo;
    }

    public List<PropertyOwnerInfo> getPropertyOwnerInfoProxy() {
        return propertyOwnerInfoProxy;
    }

    public void setPropertyOwnerInfoProxy(List<PropertyOwnerInfo> propertyOwnerInfoProxy) {
        this.propertyOwnerInfoProxy = propertyOwnerInfoProxy;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getExtentOfSite() {
        return extentOfSite;
    }

    public void setExtentOfSite(Double extentOfSite) {
        this.extentOfSite = extentOfSite;
    }

    public Float getPlinthArea() {
        return plinthArea;
    }

    public void setPlinthArea(Float plinthArea) {
        this.plinthArea = plinthArea;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public String getSurveyNumber() {
        return surveyNumber;
    }

    public void setSurveyNumber(String surveyNumber) {
        this.surveyNumber = surveyNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}