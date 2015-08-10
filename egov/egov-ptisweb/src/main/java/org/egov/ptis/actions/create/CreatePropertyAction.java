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
package org.egov.ptis.actions.create;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.egov.ptis.constants.PropertyTaxConstants.APPLICATION_TYPE_ALTER_ASSESSENT;
import static org.egov.ptis.constants.PropertyTaxConstants.APPLICATION_TYPE_NEW_ASSESSENT;
import static org.egov.ptis.constants.PropertyTaxConstants.CURR_DMD_STR;
import static org.egov.ptis.constants.PropertyTaxConstants.DEVIATION_PERCENTAGE;
import static org.egov.ptis.constants.PropertyTaxConstants.DOCS_CREATE_PROPERTY;
import static org.egov.ptis.constants.PropertyTaxConstants.ELECTIONWARD_BNDRY_TYPE;
import static org.egov.ptis.constants.PropertyTaxConstants.ELECTION_HIERARCHY_TYPE;
import static org.egov.ptis.constants.PropertyTaxConstants.GUARDIAN_RELATION;
import static org.egov.ptis.constants.PropertyTaxConstants.LOCALITY;
import static org.egov.ptis.constants.PropertyTaxConstants.LOCATION_HIERARCHY_TYPE;
import static org.egov.ptis.constants.PropertyTaxConstants.NEW_ASSESSMENT;
import static org.egov.ptis.constants.PropertyTaxConstants.NON_VAC_LAND_PROPERTY_TYPE_CATEGORY;
import static org.egov.ptis.constants.PropertyTaxConstants.OWNERSHIP_TYPE_VAC_LAND;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_STATUS_APPROVED;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_STATUS_WORKFLOW;
import static org.egov.ptis.constants.PropertyTaxConstants.PROP_CREATE_RSN;
import static org.egov.ptis.constants.PropertyTaxConstants.QUERY_PROPERTYIMPL_BYID;
import static org.egov.ptis.constants.PropertyTaxConstants.REVENUE_INSPECTOR_DESGN;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_BILL_NOTCREATED;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_ISACTIVE;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_WORKFLOW;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_YES_XML_MIGRATION;
import static org.egov.ptis.constants.PropertyTaxConstants.VACANT_PROPERTY;
import static org.egov.ptis.constants.PropertyTaxConstants.VAC_LAND_PROPERTY_TYPE_CATEGORY;
import static org.egov.ptis.constants.PropertyTaxConstants.WFLOW_ACTION_STEP_APPROVE;
import static org.egov.ptis.constants.PropertyTaxConstants.WFLOW_ACTION_STEP_CREATE;
import static org.egov.ptis.constants.PropertyTaxConstants.WFLOW_ACTION_STEP_REJECT;
import static org.egov.ptis.constants.PropertyTaxConstants.WF_STATE_COMMISSIONER_APPROVED;
import static org.egov.ptis.constants.PropertyTaxConstants.WF_STATE_REJECTED;
import static org.egov.ptis.constants.PropertyTaxConstants.WF_STATE_REVENUE_CLERK_APPROVED;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.persistence.entity.Address;
import org.egov.infra.persistence.entity.CorrespondenceAddress;
import org.egov.infra.reporting.engine.ReportConstants.FileFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.infra.reporting.viewer.ReportViewerUtil;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.web.utils.WebUtils;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infstr.utils.DateUtils;
import org.egov.portal.entity.Citizen;
import org.egov.ptis.actions.common.CommonServices;
import org.egov.ptis.actions.workflow.WorkflowAction;
import org.egov.ptis.client.util.FinancialUtil;
import org.egov.ptis.client.util.PropertyTaxNumberGenerator;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.demand.PtDemandDao;
import org.egov.ptis.domain.entity.property.Apartment;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.BasicPropertyImpl;
import org.egov.ptis.domain.entity.property.BuiltUpProperty;
import org.egov.ptis.domain.entity.property.Category;
import org.egov.ptis.domain.entity.property.DocumentType;
import org.egov.ptis.domain.entity.property.Floor;
import org.egov.ptis.domain.entity.property.FloorType;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyAddress;
import org.egov.ptis.domain.entity.property.PropertyDetail;
import org.egov.ptis.domain.entity.property.PropertyID;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.entity.property.PropertyMutationMaster;
import org.egov.ptis.domain.entity.property.PropertyOccupation;
import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;
import org.egov.ptis.domain.entity.property.PropertyStatus;
import org.egov.ptis.domain.entity.property.PropertyTypeMaster;
import org.egov.ptis.domain.entity.property.PropertyUsage;
import org.egov.ptis.domain.entity.property.RoofType;
import org.egov.ptis.domain.entity.property.StructureClassification;
import org.egov.ptis.domain.entity.property.TaxExeptionReason;
import org.egov.ptis.domain.entity.property.VacantProperty;
import org.egov.ptis.domain.entity.property.WallType;
import org.egov.ptis.domain.entity.property.WoodType;
import org.egov.ptis.domain.service.property.PropertyPersistenceService;
import org.egov.ptis.domain.service.property.PropertyService;
import org.egov.ptis.domain.service.property.SMSEmailService;
import org.egov.ptis.report.bean.PropertyAckNoticeInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author parvati
 */
@ParentPackage("egov")
@Namespace("/create")
@ResultPath("/WEB-INF/jsp/")
@Results({ @Result(name = "new", location = "create/createProperty-new.jsp"),
        @Result(name = "ack", location = "create/createProperty-ack.jsp"),
        @Result(name = "view", location = "create/createProperty-view.jsp"),
        @Result(name = CreatePropertyAction.PRINTACK, location = "create/createProperty-printAck.jsp") })
public class CreatePropertyAction extends WorkflowAction {
    /**
     *
     */
    private static final long serialVersionUID = -2329719786287615451L;
    private static final String RESULT_ACK = "ack";
    private static final String RESULT_NEW = "new";
    private static final String RESULT_VIEW = "view";
    private static final String MSG_REJECT_SUCCESS = " Property Rejected Successfully ";
    private static final String CREATE = "create";

    private final Logger LOGGER = Logger.getLogger(getClass());
    private PropertyImpl property = new PropertyImpl();
    @Autowired
    private PropertyPersistenceService basicPropertyService;
    private Long zoneId;
    private Long wardId;
    private Long blockId;
    private Long electionWardId;
    private String wardName;
    private String zoneName;
    private String blockName;
    private Long locality;
    private Long floorTypeId;
    private Long roofTypeId;
    private Long wallTypeId;
    private Long woodTypeId;
    private Long ownershipType;
    private String houseNumber;
    private String addressStr;
    private String pinCode;
    private String areaOfPlot;
    private String applicationDate;
    private String dateOfCompletion;
    private String applicationNo;
    private TreeMap<Integer, String> floorNoMap;
    private boolean chkIsTaxExempted;
    private String taxExemptReason;
    private boolean chkIsCorrIsDiff;
    private String corrAddress1;
    private String corrAddress2;
    private String corrPinCode;
    private String genWaterRate;
    private BasicProperty basicProp;

    @Autowired
    private PropertyService propService;
    private Long mutationId;
    private String parentIndex;
    Date propCompletionDate = null;
    private String amenities;
    private String[] floorNoStr = new String[20];
    private String propTypeId;
    private String propUsageId;
    private String propOccId;
    private Map<String, String> propTypeCategoryMap;
    private String propTypeCategoryId;
    FinancialUtil financialUtil = new FinancialUtil();
    private PropertyTypeMaster propTypeMstr;

    private String docNumber;
    private String nonResPlotArea;
    private Category propertyCategory;
    @Autowired
    private PropertyTaxNumberGenerator propertyTaxNumberGenerator;

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private PropertyImpl newProperty = new PropertyImpl();
    private Date currDate;
    private String regdDocNo;
    private Date regdDocDate;
    private String mode = CREATE;
    public static final String PRINTACK = "printAck";
    private ReportService reportService;
    private Integer reportId = -1;
    private boolean approved = false;
    private String northBoundary;
    private String southBoundary;
    private String eastBoundary;
    private String westBoundary;
    private Address ownerAddress;
    private Map<String, String> deviationPercentageMap;
    private Map<String, String> guardianRelationMap;
    private List<DocumentType> documentTypes = new ArrayList<>();

    @Autowired
    private UserService userService;

    @Autowired
    private BoundaryService boundaryService;

    @Autowired
    private SecurityUtils securityUtils;

    private SMSEmailService sMSEmailService;

    @Autowired
    private PtDemandDao ptDemandDAO;

    private static final String CREATE_ACK_TEMPLATE = "createProperty_ack";

    public CreatePropertyAction() {
        super();
        property.setPropertyDetail(new BuiltUpProperty());
        property.setBasicProperty(new BasicPropertyImpl());
        this.addRelatedEntity("property", PropertyImpl.class);
        this.addRelatedEntity("property.propertyDetail.propertyTypeMaster", PropertyTypeMaster.class);
        this.addRelatedEntity("property.propertyDetail.floorDetails.unitType", PropertyTypeMaster.class);
        this.addRelatedEntity("property.propertyDetail.floorDetails.propertyUsage", PropertyUsage.class);
        this.addRelatedEntity("property.propertyDetail.floorDetails.propertyOccupation", PropertyOccupation.class);
        this.addRelatedEntity("property.propertyDetail.floorDetails.structureClassification",
                StructureClassification.class);
        this.addRelatedEntity("property.propertyDetail.floorDetails.taxExemptedReason", TaxExeptionReason.class);
        this.addRelatedEntity("property.basicProperty.propertyOwnerInfo.owner", Citizen.class);
        this.addRelatedEntity("property.propertyDetail.apartment", Apartment.class);
        addRelatedEntity("property.propertyDetail.floorType", FloorType.class);
        addRelatedEntity("property.propertyDetail.roofType", RoofType.class);
        addRelatedEntity("property.propertyDetail.wallType", WallType.class);
        addRelatedEntity("property.propertyDetail.woodType", WoodType.class);

    }

    @Override
    public StateAware getModel() {
        return property;
    }

    @SkipValidation
    @Action(value = "/createProperty-newForm")
    public String newForm() {
        return RESULT_NEW;
    }

    @Action(value = "/createProperty-create")
    public String create() {
        LOGGER.debug("create: Property creation started, Property: " + property + ", zoneId: " + zoneId + ", wardId: "
                + wardId + ", blockId: " + blockId + ", areaOfPlot: " + areaOfPlot + ", dateOfCompletion: "
                + dateOfCompletion + ", chkIsTaxExempted: " + chkIsTaxExempted + ", taxExemptReason: "
                + taxExemptReason + ", propTypeId: " + propTypeId + ", propUsageId: " + propUsageId + ", propOccId: "
                + propOccId);
        final long startTimeMillis = System.currentTimeMillis();
        final BasicProperty basicProperty = createBasicProp(STATUS_ISACTIVE);
        LOGGER.debug("create: BasicProperty after creatation: " + basicProperty);
        basicProperty.setIsTaxXMLMigrated(STATUS_YES_XML_MIGRATION);
        processAndStoreDocumentsWithReason(basicProperty, DOCS_CREATE_PROPERTY);
        // this should be appending to messgae
        transitionWorkFlow(property);
        basicPropertyService.applyAuditing(property.getState());
        basicPropertyService.persist(basicProperty);
        propService.updateIndexes(property, APPLICATION_TYPE_NEW_ASSESSENT);
        buildSMS(property);
        setBasicProp(basicProperty);
        setAckMessage("Property Created Successfully in System and forwarded to " +getApproverName()+" with application number : ");
        final long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        LOGGER.info("create: Property created successfully in system" + "; Time taken(ms) = " + elapsedTimeMillis);
        LOGGER.debug("create: Property creation ended");
        return RESULT_ACK;
    }

    private void populateFormData() {
        final PropertyDetail propertyDetail = property.getPropertyDetail();
        if (propertyDetail != null) {
            if (propertyDetail.getFloorDetails().size() > 0)
                setFloorDetails(property);
            setPropTypeId(propertyDetail.getPropertyTypeMaster().getId().toString());
            if (propTypeId != null && !propTypeId.trim().isEmpty() && !propTypeId.equals("-1")) {
                propTypeMstr = (PropertyTypeMaster) getPersistenceService().find(
                        "from PropertyTypeMaster ptm where ptm.id = ?", Long.valueOf(propTypeId));
                if (propTypeMstr.getCode().equalsIgnoreCase(OWNERSHIP_TYPE_VAC_LAND))
                    setPropTypeCategoryMap(VAC_LAND_PROPERTY_TYPE_CATEGORY);
                else
                    setPropTypeCategoryMap(NON_VAC_LAND_PROPERTY_TYPE_CATEGORY);
            }

            if (!propertyDetail.getPropertyType().equals(VACANT_PROPERTY)) {
                propertyDetail.setCategoryType(propertyDetail.getCategoryType());
                if (property.getPropertyDetail().getFloorType() != null)
                    floorTypeId = property.getPropertyDetail().getFloorType().getId();
                if (property.getPropertyDetail().getRoofType() != null)
                    roofTypeId = property.getPropertyDetail().getRoofType().getId();
                if (property.getPropertyDetail().getWallType() != null)
                    wallTypeId = property.getPropertyDetail().getWallType().getId();
                if (property.getPropertyDetail().getWoodType() != null)
                    woodTypeId = property.getPropertyDetail().getWoodType().getId();
                if (property.getPropertyDetail().getSitalArea() != null)
                    setAreaOfPlot(property.getPropertyDetail().getSitalArea().getArea().toString());
            }
        }

        if (basicProp != null) {
            basicProp.setPropertyOwnerInfoProxy(basicProp.getPropertyOwnerInfo());
            setRegdDocDate(basicProp.getRegdDocDate());
            setRegdDocNo(basicProp.getRegdDocNo());
            setMutationId(basicProp.getPropertyMutationMaster().getId());
            if (null != basicProp.getAddress()) {
                setHouseNumber(basicProp.getAddress().getHouseNoBldgApt());
                setPinCode(basicProp.getAddress().getPinCode());
            }

            for (final PropertyOwnerInfo ownerInfo : basicProp.getPropertyOwnerInfo())
                for (final Address ownerAddress : ownerInfo.getOwner().getAddress())
                    if (null != ownerAddress) {
                        final String corrAddress = ownerAddress.getHouseNoBldgApt() + ","
                                + ownerAddress.getAreaLocalitySector();
                        setCorrAddress1(corrAddress);
                        setCorrAddress2(ownerAddress.getStreetRoadLine());
                        setCorrPinCode(ownerAddress.getPinCode());
                        chkIsCorrIsDiff = true;
                    }
            if (null != basicProp.getPropertyID()) {
                final PropertyID propBoundary = basicProp.getPropertyID();
                setNorthBoundary(propBoundary.getNorthBoundary());
                setSouthBoundary(propBoundary.getSouthBoundary());
                setEastBoundary(propBoundary.getEastBoundary());
                setWestBoundary(propBoundary.getWestBoundary());
                if (null != propBoundary.getLocality().getId())
                    setLocality(boundaryService.getBoundaryById(propBoundary.getLocality().getId()).getId());
                if (null != propBoundary.getElectionBoundary() && null != propBoundary.getElectionBoundary().getId())
                    setElectionWardId(boundaryService.getBoundaryById(propBoundary.getElectionBoundary().getId())
                            .getId());
                if (null != propBoundary.getZone().getId()) {
                    final Boundary zone = propBoundary.getZone();
                    setZoneId(boundaryService.getBoundaryById(zone.getId()).getId());
                    setZoneName(zone.getName());
                }
                if (null != propBoundary.getWard().getId()) {
                    final Boundary ward = propBoundary.getWard();
                    setWardId(boundaryService.getBoundaryById(ward.getId()).getId());
                    setWardName(ward.getName());
                }
                if (null != propBoundary.getArea().getId()) {
                    final Boundary area = propBoundary.getArea();
                    setBlockId(boundaryService.getBoundaryById(area.getId()).getId());
                    setBlockName(area.getName());
                }
            }
        }
    }

    @SkipValidation
    @Action(value = "/createProperty-view")
    public String view() {
        LOGGER.debug("Entered into view, BasicProperty: " + basicProp + ", Property: " + property + ", userDesgn: "
                + userDesgn);
        final String currState = property.getState().getValue();
        populateFormData();
        if (currState.endsWith(WF_STATE_REJECTED) || REVENUE_INSPECTOR_DESGN.equalsIgnoreCase(userDesgn)) {
            // populateFormData();
            mode = EDIT;
            return RESULT_NEW;
        } else {
            mode = VIEW;
            setAddressStr(basicProp.getAddress().toString());
            corrAddress1 = basicProp.getAddress().toString();
            setDocNumber(property.getDocNumber());
            LOGGER.debug("IndexNumber: " + indexNumber + " Amenities: " + amenities + "NoOfFloors: "
                    + (getFloorDetails() != null ? getFloorDetails().size() : "Floor list is NULL")
                    + " Exiting from view");
            return RESULT_VIEW;
        }
    }

    @SkipValidation
    @Action(value = "/createProperty-forward")
    public String forward() {
        if (mode.equalsIgnoreCase(EDIT)) {
            validate();
            if (hasErrors())
                return RESULT_NEW;
            updatePropertyDetails();
        } else {
            validateApproverDetails();
            if (hasErrors())
                return RESULT_VIEW;
        }
        transitionWorkFlow(property);

        if (WFLOW_ACTION_STEP_APPROVE.equalsIgnoreCase(workFlowAction))
            return approve();
        else if (WFLOW_ACTION_STEP_REJECT.equalsIgnoreCase(workFlowAction))
            return reject();

        basicProp.setUnderWorkflow(true);
        basicPropertyService.applyAuditing(property.getState());
        basicProp.addProperty(property);
        basicPropertyService.persist(basicProp);
        propService.updateIndexes(property, APPLICATION_TYPE_NEW_ASSESSENT);
        LOGGER.debug("forward: Property forward started " + property);
        final long startTimeMillis = System.currentTimeMillis();
        setDocNumber(getDocNumber());
        final long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        LOGGER.debug("forward: Time taken(ms) = " + elapsedTimeMillis);
        LOGGER.debug("forward: Property forward ended");
        return RESULT_ACK;
    }

    public void updatePropertyDetails() {
        updatePropertyId(basicProp);
        final Character status = STATUS_WORKFLOW;
        updatePropAddress(basicProp);
        basicPropertyService.createOwners(property, basicProp, ownerAddress);
        final PropertyMutationMaster propertyMutationMaster = (PropertyMutationMaster) getPersistenceService().find(
                "from PropertyMutationMaster pmm where pmm.type=? AND pmm.id=?", PROP_CREATE_RSN, mutationId);
        basicProp.setPropertyMutationMaster(propertyMutationMaster);
        property = propService.createProperty(property, getAreaOfPlot(), propertyMutationMaster.getCode(), propTypeId,
                propUsageId, propOccId, status, getDocNumber(), getNonResPlotArea(), getFloorTypeId(), getRoofTypeId(),
                getWallTypeId(), getWoodTypeId());
        if (!property.getPropertyDetail().getPropertyTypeMaster().getCode().equalsIgnoreCase(OWNERSHIP_TYPE_VAC_LAND))
            propCompletionDate = propService.getLowestDtOfCompFloorWise(property.getPropertyDetail().getFloorDetails());
        else
            propCompletionDate = property.getPropertyDetail().getDateOfCompletion();
        basicProp.setPropOccupationDate(propCompletionDate);

        if (property != null && !property.getDocuments().isEmpty())
            propService.processAndStoreDocument(property.getDocuments());

        if (propTypeMstr != null && propTypeMstr.getCode().equals(OWNERSHIP_TYPE_VAC_LAND))
            property.setPropertyDetail(propService.changePropertyDetail(property, property.getPropertyDetail(), 0)
                    .getPropertyDetail());
        property.setBasicProperty(basicProp);
        propService.createDemand(property, basicProp.getPropOccupationDate());

    }

    private void updatePropertyId(final BasicProperty basicProperty) {
        final PropertyID propertyId = basicProperty.getPropertyID();
        propertyId.setZone(boundaryService.getBoundaryById(getZoneId()));
        propertyId.setWard(boundaryService.getBoundaryById(getWardId()));
        propertyId.setElectionBoundary(boundaryService.getBoundaryById(getElectionWardId()));
        propertyId.setModifiedDate(new Date());
        propertyId.setModifiedDate(new Date());
        propertyId.setArea(boundaryService.getBoundaryById(getBlockId()));
        propertyId.setLocality(boundaryService.getBoundaryById(getLocality()));
        propertyId.setEastBoundary(getEastBoundary());
        propertyId.setWestBoundary(getWestBoundary());
        propertyId.setNorthBoundary(getNorthBoundary());
        propertyId.setSouthBoundary(getSouthBoundary());
    }

    @SkipValidation
    @Action(value = "/createProperty-approve")
    public String approve() {
        LOGGER.debug("approve: Property approval started");
        LOGGER.debug("approve: Property: " + property);
        LOGGER.debug("approve: BasicProperty: " + basicProp);
        property.setStatus(STATUS_ISACTIVE);
        final String assessmentNo = propertyTaxNumberGenerator.generateAssessmentNumber();
        basicProp.setUpicNo(assessmentNo);
        basicProp.setUnderWorkflow(false);
        final PropertyStatus propStatus = (PropertyStatus) getPersistenceService().find(
                "from PropertyStatus where statusCode=?", PROPERTY_STATUS_APPROVED);
        basicProp.setStatus(propStatus);
        approved = true;
        setWardId(basicProp.getPropertyID().getWard().getId());
        processAndStoreDocumentsWithReason(basicProp, DOCS_CREATE_PROPERTY);
        basicPropertyService.applyAuditing(property.getState());
        basicPropertyService.update(basicProp);
        propService.updateIndexes(property, APPLICATION_TYPE_NEW_ASSESSENT);
        buildSMS(property);
        setAckMessage("Property Approved Successfully by : "
                + userService.getUserById(securityUtils.getCurrentUser().getId()).getName()
                + " with assessment number : ");
        LOGGER.debug("approve: BasicProperty: " + getBasicProp() + "AckMessage: " + getAckMessage());
        LOGGER.debug("approve: Property approval ended");
        return RESULT_ACK;
    }

    @SkipValidation
    @Action(value = "/createProperty-reject")
    public String reject() {
        LOGGER.debug("reject: Property rejection started");
        basicPropertyService.applyAuditing(property.getState());
        basicProp.setUnderWorkflow(true);
        basicPropertyService.persist(basicProp);
        propService.updateIndexes(property, APPLICATION_TYPE_NEW_ASSESSENT);
        buildSMS(property);
        setAckMessage(MSG_REJECT_SUCCESS + " and forwarded to initiator " + property.getCreatedBy().getUsername()
                + " with application No :");
        LOGGER.debug("reject: BasicProperty: " + getBasicProp() + "AckMessage: " + getAckMessage());
        LOGGER.debug("reject: Property rejection ended");

        return RESULT_ACK;
    }

    private void setFloorDetails(final Property property) {
        LOGGER.debug("Entered into setFloorDetails, Property: " + property);
        final List<Floor> floorList = property.getPropertyDetail().getFloorDetails();
        property.getPropertyDetail().setFloorDetailsProxy(floorList);
        int i = 0;
        for (final Floor flr : floorList) {
            floorNoStr[i] = propertyTaxUtil.getFloorStr(flr.getFloorNo());
            LOGGER.debug("setFloorDetails: floorNoStr[" + i + "]->" + floorNoStr[i]);
            i++;
        }
        LOGGER.debug("Exiting from setFloorDetails");
    }

    public List<Floor> getFloorDetails() {
        return new ArrayList<Floor>(property.getPropertyDetail().getFloorDetails());
    }

    @Override
    @SuppressWarnings("unchecked")
    @SkipValidation
    public void prepare() {

        LOGGER.debug("Entered into prepare, ModelId: " + getModelId() + ", PropTypeId: " + propTypeId + ", ZoneId: "
                + zoneId + ", WardId: " + wardId);

        currDate = new Date();
        setUserInfo();
        if (isNotBlank(getModelId())) {
            property = (PropertyImpl) getPersistenceService().findByNamedQuery(QUERY_PROPERTYIMPL_BYID,
                    Long.valueOf(getModelId()));
            basicProp = property.getBasicProperty();
            LOGGER.debug("prepare: Property by ModelId: " + property);
            LOGGER.debug("prepare: BasicProperty on property: " + basicProp);
        }
        documentTypes = propService.getPropertyCreateDocumentTypes();
        final List<FloorType> floorTypeList = getPersistenceService().findAllBy("from FloorType order by name");
        final List<RoofType> roofTypeList = getPersistenceService().findAllBy("from RoofType order by name");
        final List<WallType> wallTypeList = getPersistenceService().findAllBy("from WallType order by name");
        final List<WoodType> woodTypeList = getPersistenceService().findAllBy("from WoodType order by name");
        final List<PropertyTypeMaster> propTypeList = getPersistenceService().findAllBy(
                "from PropertyTypeMaster order by orderNo");
        final List<PropertyOccupation> propOccList = getPersistenceService().findAllBy("from PropertyOccupation");
        final List<PropertyMutationMaster> mutationList = getPersistenceService().findAllBy(
                "from PropertyMutationMaster pmm where pmm.type=?", PROP_CREATE_RSN);
        final List<PropertyUsage> usageList = getPersistenceService()
                .findAllBy("from PropertyUsage order by usageName");

        final List<String> ageFacList = getPersistenceService().findAllBy("from DepreciationMaster");
        final List<String> StructureList = getPersistenceService().findAllBy("from StructureClassification");
        final List<String> apartmentsList = getPersistenceService().findAllBy("from Apartment order by name");
        final List<String> taxExemptionReasonList = getPersistenceService().findAllBy(
                "from TaxExeptionReason order by name");

        final List<Boundary> localityList = boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(
                LOCALITY, LOCATION_HIERARCHY_TYPE);
        final List<Boundary> electionWardList = boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(
                ELECTIONWARD_BNDRY_TYPE, ELECTION_HIERARCHY_TYPE);
        final List<Boundary> enumerationBlockList = boundaryService
                .getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(ELECTIONWARD_BNDRY_TYPE,
                        ELECTION_HIERARCHY_TYPE);

        setDeviationPercentageMap(DEVIATION_PERCENTAGE);
        setGuardianRelationMap(GUARDIAN_RELATION);
        addDropdownData("PropTypeMaster", propTypeList);
        addDropdownData("floorType", floorTypeList);
        addDropdownData("roofType", roofTypeList);
        addDropdownData("wallType", wallTypeList);
        addDropdownData("woodType", woodTypeList);
        addDropdownData("apartments", apartmentsList);

        addDropdownData("UsageList", usageList);
        addDropdownData("OccupancyList", propOccList);
        addDropdownData("StructureList", StructureList);
        addDropdownData("AgeFactorList", ageFacList);
        addDropdownData("MutationList", mutationList);
        addDropdownData("LocationFactorList", Collections.EMPTY_LIST);
        setFloorNoMap(CommonServices.floorMap());
        addDropdownData("localityList", localityList);
        addDropdownData("electionWardList", electionWardList);
        addDropdownData("enumerationBlockList", enumerationBlockList);
        addDropdownData("taxExemptionReasonList", taxExemptionReasonList);
        if (propTypeId != null && !propTypeId.trim().isEmpty() && !propTypeId.equals("-1")) {
            propTypeMstr = (PropertyTypeMaster) getPersistenceService().find(
                    "from PropertyTypeMaster ptm where ptm.id = ?", Long.valueOf(propTypeId));
            if (propTypeMstr.getCode().equalsIgnoreCase(OWNERSHIP_TYPE_VAC_LAND))
                setPropTypeCategoryMap(VAC_LAND_PROPERTY_TYPE_CATEGORY);
            else
                setPropTypeCategoryMap(NON_VAC_LAND_PROPERTY_TYPE_CATEGORY);
        } else
            setPropTypeCategoryMap(Collections.EMPTY_MAP);
        // tax exempted properties
        addDropdownData("taxExemptedList", CommonServices.getTaxExemptedList());

        super.prepare();
        LOGGER.debug("prepare: PropTypeList: "
                + (propTypeList != null ? propTypeList : "NULL")
                + ", PropOccuList: "
                + (propOccList != null ? propOccList : "NLL")
                + ", MutationList: "
                + (mutationList != null ? mutationList : "NULL")
                + ", AgeFactList: "
                + (ageFacList != null ? ageFacList : "NULL")
                + "UsageList: "
                + (getDropdownData().get("UsageList") != null ? getDropdownData().get("UsageList") : "List is NULL")
                + ", TaxExemptedReasonList: "
                + (getDropdownData().get("taxExemptedList") != null ? getDropdownData().get("taxExemptedList")
                        : "List is NULL"));

        LOGGER.debug("Exiting from prepare");
    }

    private BasicProperty createBasicProp(final Character status) {
        LOGGER.debug("Entered into createBasicProp, Property: " + property + ", status: " + status + ", wardId: "
                + wardId);

        final BasicProperty basicProperty = new BasicPropertyImpl();
        final PropertyStatus propStatus = (PropertyStatus) getPersistenceService().find(
                "from PropertyStatus where statusCode=?", PROPERTY_STATUS_WORKFLOW);
        basicProperty.setRegdDocDate(property.getBasicProperty().getRegdDocDate());
        basicProperty.setRegdDocNo(property.getBasicProperty().getRegdDocNo());
        basicProperty.setActive(Boolean.TRUE);
        basicProperty.setAddress(createPropAddress());
        basicProperty.setPropertyID(createPropertyID(basicProperty));
        basicProperty.setStatus(propStatus);
        basicProperty.setUnderWorkflow(true);
        final PropertyMutationMaster propertyMutationMaster = (PropertyMutationMaster) getPersistenceService().find(
                "from PropertyMutationMaster pmm where pmm.type=? AND pmm.id=?", PROP_CREATE_RSN, mutationId);
        basicProperty.setPropertyMutationMaster(propertyMutationMaster);
        basicProperty.addPropertyStatusValues(propService.createPropStatVal(basicProperty, "CREATE", null, null, null,
                null, getParentIndex()));
        basicProperty.setBoundary(boundaryService.getBoundaryById(getWardId()));
        basicProperty.setIsBillCreated(STATUS_BILL_NOTCREATED);
        basicPropertyService.createOwners(property, basicProperty, ownerAddress);
        property.setBasicProperty(basicProperty);

        /*
         * isfloorDetailsRequired is used to check if floor details have to be
         * entered for State Govt property or not if isfloorDetailsRequired -
         * true : no floor details created false : floor details created
         */
        property = propService.createProperty(property, getAreaOfPlot(), propertyMutationMaster.getCode(), propTypeId,
                propUsageId, propOccId, status, getDocNumber(), getNonResPlotArea(), getFloorTypeId(), getRoofTypeId(),
                getWallTypeId(), getWoodTypeId());
        property.setStatus(status);

        LOGGER.debug("createBasicProp: Property after call to PropertyService.createProperty: " + property);
        if (!property.getPropertyDetail().getPropertyTypeMaster().getCode().equalsIgnoreCase(OWNERSHIP_TYPE_VAC_LAND))
            propCompletionDate = propService.getLowestDtOfCompFloorWise(property.getPropertyDetail().getFloorDetails());
        else
            propCompletionDate = property.getPropertyDetail().getDateOfCompletion();
        basicProperty.setPropOccupationDate(propCompletionDate);

        if (property != null && !property.getDocuments().isEmpty())
            propService.processAndStoreDocument(property.getDocuments());

        if (propTypeMstr != null && propTypeMstr.getCode().equals(OWNERSHIP_TYPE_VAC_LAND))
            property.setPropertyDetail(changePropertyDetail());
        basicProperty.addProperty(property);
        propService.createDemand(property, propCompletionDate);
        LOGGER.debug("BasicProperty: " + basicProperty + "\nExiting from createBasicProp");
        return basicProperty;
    }

    /**
     * Changes the property details from {@link BuiltUpProperty} to
     * {@link VacantProperty}
     *
     * @return vacant property details
     * @see org.egov.ptis.domain.entity.property.VacantProperty
     */

    private VacantProperty changePropertyDetail() {

        LOGGER.debug("Entered into changePropertyDetail, Property is Vacant land");

        final PropertyDetail propertyDetail = property.getPropertyDetail();
        final VacantProperty vacantProperty = new VacantProperty(propertyDetail.getSitalArea(),
                propertyDetail.getTotalBuiltupArea(), propertyDetail.getCommBuiltUpArea(),
                propertyDetail.getPlinthArea(), propertyDetail.getCommVacantLand(), propertyDetail.getNonResPlotArea(),
                false, propertyDetail.getSurveyNumber(), propertyDetail.getFieldVerified(),
                propertyDetail.getFieldVerificationDate(), propertyDetail.getFloorDetails(),
                propertyDetail.getPropertyDetailsID(), propertyDetail.getWater_Meter_Num(),
                propertyDetail.getElec_Meter_Num(), 0, propertyDetail.getFieldIrregular(),
                propertyDetail.getDateOfCompletion(), propertyDetail.getProperty(), propertyDetail.getUpdatedTime(),
                propertyDetail.getPropertyUsage(), null, propertyDetail.getPropertyTypeMaster(),
                propertyDetail.getPropertyType(), propertyDetail.getInstallment(),
                propertyDetail.getPropertyOccupation(), propertyDetail.getPropertyMutationMaster(),
                propertyDetail.getComZone(), propertyDetail.getCornerPlot(),
                propertyDetail.getExtentSite() != null ? propertyDetail.getExtentSite() : 0.0,
                propertyDetail.getExtentAppartenauntLand() != null ? propertyDetail.getExtentAppartenauntLand() : 0.0,
                propertyDetail.getFloorType(), propertyDetail.getRoofType(), propertyDetail.getWallType(),
                propertyDetail.getWoodType(), propertyDetail.isLift(), propertyDetail.isToilets(),
                propertyDetail.isWaterTap(), propertyDetail.isStructure(), propertyDetail.isElectricity(),
                propertyDetail.isAttachedBathRoom(), propertyDetail.isWaterHarvesting(), propertyDetail.isCable(),
                propertyDetail.getSiteOwner(), propertyDetail.getPattaNumber(),
                propertyDetail.getCurrentCapitalValue(), propertyDetail.getMarketValue(),
                propertyDetail.getCategoryType(), propertyDetail.getOccupancyCertificationNo(),
                propertyDetail.getBuildingPermissionNo(), propertyDetail.getBuildingPermissionDate(),
                propertyDetail.getDeviationPercentage(), propertyDetail.isAppurtenantLandChecked(),
                propertyDetail.isBuildingPlanDetailsChecked());

        vacantProperty.setManualAlv(propertyDetail.getManualAlv());
        vacantProperty.setOccupierName(propertyDetail.getOccupierName());

        LOGGER.debug("Exiting from changePropertyDetail");
        return vacantProperty;
    }

    private void updatePropAddress(final BasicProperty basicProperty) {

        LOGGER.debug("Entered into createPropAddress, \nAreaId: " + getBlockId() + ", House Number: "
                + getHouseNumber() + ", OldHouseNo: " + ", AddressStr: " + getAddressStr() + ", PinCode: "
                + getPinCode());

        final Address propAddr = basicProperty.getAddress();
        propAddr.setHouseNoBldgApt(getHouseNumber());
        propAddr.setAreaLocalitySector(boundaryService.getBoundaryById(getBlockId()).getName());
        propAddr.setStreetRoadLine(boundaryService.getBoundaryById(getLocality()).getName());
        if (getPinCode() != null && !getPinCode().isEmpty())
            propAddr.setPinCode(getPinCode());
        for (final PropertyOwnerInfo owner : basicProperty.getPropertyOwnerInfo())
            for (final Address address : owner.getOwner().getAddress())
                if (null != address)
                    ownerAddress = address;
        if (!isChkIsCorrIsDiff()) {
            ownerAddress.setAreaLocalitySector(propAddr.getAreaLocalitySector());
            ownerAddress.setHouseNoBldgApt(propAddr.getHouseNoBldgApt());
            ownerAddress.setStreetRoadLine(propAddr.getStreetRoadLine());
            ownerAddress.setPinCode(propAddr.getPinCode());

        } else {
            final String[] corrAddr = getCorrAddress1().split(",");
            if (corrAddr.length == 1)
                ownerAddress.setAreaLocalitySector(getCorrAddress1());
            else
                ownerAddress.setAreaLocalitySector(corrAddr[1]);
            ownerAddress.setHouseNoBldgApt(getHouseNumber());
            ownerAddress.setStreetRoadLine(getCorrAddress2());
            ownerAddress.setPinCode(getCorrPinCode());
        }
    }

    private PropertyAddress createPropAddress() {

        LOGGER.debug("Entered into createPropAddress, \nAreaId: " + getBlockId() + ", House Number: "
                + getHouseNumber() + ", OldHouseNo: " + ", AddressStr: " + getAddressStr() + ", PinCode: "
                + getPinCode());

        final Address propAddr = new PropertyAddress();
        propAddr.setHouseNoBldgApt(getHouseNumber());
        propAddr.setAreaLocalitySector(boundaryService.getBoundaryById(getBlockId()).getName());
        propAddr.setStreetRoadLine(boundaryService.getBoundaryById(getLocality()).getName());
        if (getPinCode() != null && !getPinCode().isEmpty())
            propAddr.setPinCode(getPinCode());
        if (!isChkIsCorrIsDiff()) {
            ownerAddress = new CorrespondenceAddress();
            ownerAddress.setAreaLocalitySector(propAddr.getAreaLocalitySector());
            ownerAddress.setHouseNoBldgApt(propAddr.getHouseNoBldgApt());
            ownerAddress.setStreetRoadLine(propAddr.getStreetRoadLine());
            ownerAddress.setPinCode(propAddr.getPinCode());
        } else {
            ownerAddress = new CorrespondenceAddress();
            ownerAddress.setHouseNoBldgApt(getHouseNumber());
            ownerAddress.setAreaLocalitySector(getCorrAddress1());
            ownerAddress.setStreetRoadLine(getCorrAddress2());
            ownerAddress.setPinCode(getCorrPinCode());
        }
        LOGGER.debug("PropertyAddress: " + propAddr + "\nExiting from createPropAddress");
        return (PropertyAddress) propAddr;
    }

    private PropertyID createPropertyID(final BasicProperty basicProperty) {
        final PropertyID propertyId = new PropertyID();
        propertyId.setZone(boundaryService.getBoundaryById(getZoneId()));
        propertyId.setWard(boundaryService.getBoundaryById(getWardId()));
        propertyId.setElectionBoundary(boundaryService.getBoundaryById(getElectionWardId()));
        propertyId.setCreatedDate(new Date());
        propertyId.setModifiedDate(new Date());
        propertyId.setModifiedDate(new Date());
        propertyId.setArea(boundaryService.getBoundaryById(getBlockId()));
        propertyId.setLocality(boundaryService.getBoundaryById(getLocality()));
        propertyId.setEastBoundary(getEastBoundary());
        propertyId.setWestBoundary(getWestBoundary());
        propertyId.setNorthBoundary(getNorthBoundary());
        propertyId.setSouthBoundary(getSouthBoundary());
        propertyId.setBasicProperty(basicProperty);
        LOGGER.debug("PropertyID: " + propertyId + "\nExiting from createPropertyID");
        return propertyId;
    }

    @Override
    public void validate() {
        LOGGER.debug("Entered into validate\nZoneId: " + zoneId + ", WardId: " + wardId + ", AreadId: " + blockId
                + ", HouseNumber: " + houseNumber + ", PinCode: " + pinCode + ", MutationId: " + mutationId);

        if (locality == null || locality == -1)
            addActionError(getText("mandatory.localityId"));

        if (wardId == null || wardId == -1)
            addActionError(getText("mandatory.ward"));
        else if (!StringUtils.isBlank(houseNumber))
            validateHouseNumber(wardId, houseNumber, basicProp);
        else
            addActionError(getText("mandatory.doorNo"));

        for (final PropertyOwnerInfo owner : property.getBasicProperty().getPropertyOwnerInfoProxy())
            if (owner != null) {
                if (StringUtils.isBlank(owner.getOwner().getAadhaarNumber()))
                    addActionError(getText("mandatory.adharNo"));
                if (StringUtils.isBlank(owner.getOwner().getSalutation()))
                    addActionError(getText("mandatory.salutation"));
                if (StringUtils.isBlank(owner.getOwner().getName()))
                    addActionError(getText("mandatory.ownerName"));
                if (null == owner.getOwner().getGender())
                    addActionError(getText("mandatory.gender"));
                if (StringUtils.isBlank(owner.getOwner().getMobileNumber()))
                    addActionError(getText("mandatory.mobilenumber"));
                if (StringUtils.isBlank(owner.getOwner().getEmailId()))
                    addActionError(getText("mandatory.emailId"));
            }

        validateProperty(property, areaOfPlot, dateOfCompletion, chkIsTaxExempted, taxExemptReason, propTypeId,
                propUsageId, propOccId, floorTypeId, roofTypeId, wallTypeId, woodTypeId);

        if (isBlank(pinCode))
            addActionError(getText("mandatory.pincode"));
        if (chkIsCorrIsDiff) {
            if (isBlank(corrAddress1))
                addActionError(getText("mandatory.corr.addr1"));
            if (isBlank(corrAddress2))
                addActionError(getText("mandatory.corr.addr2"));
            if (isBlank(corrPinCode) && corrPinCode.length() < 6)
                addActionError(getText("mandatory.corr.pincode.size"));
        }
        validateApproverDetails();
        super.validate();
    }

    @SkipValidation
    @Action(value = "/createProperty-printAck")
    public String printAck() {
        final HttpServletRequest request = ServletActionContext.getRequest();
        final String url = WebUtils.extractRequestDomainURL(request, false);
        final String imagePath = url.concat(PropertyTaxConstants.IMAGES_BASE_PATH).concat(ReportUtil.fetchLogo());
        final PropertyAckNoticeInfo ackBean = new PropertyAckNoticeInfo();
        final Map<String, Object> reportParams = new HashMap<String, Object>();
        ackBean.setOwnerName(basicProp.getFullOwnerName());
        ackBean.setOwnerAddress(basicProp.getAddress().toString());
        ackBean.setApplicationDate(new SimpleDateFormat("dd/MM/yyyy").format(basicProp.getCreatedDate()));
        ackBean.setApplicationNo(property.getApplicationNo());
        ackBean.setApprovedDate(new SimpleDateFormat("dd/MM/yyyy").format(property.getState().getCreatedDate()));
        final Date tempNoticeDate = DateUtils.add(property.getState().getCreatedDate(), Calendar.DAY_OF_MONTH,
                15);
        ackBean.setNoticeDueDate(tempNoticeDate);
        reportParams.put("logoPath", imagePath);
        reportParams.put("loggedInUsername", propertyTaxUtil.getLoggedInUser(getSession()).getName());
        final ReportRequest reportInput = new ReportRequest(CREATE_ACK_TEMPLATE, ackBean, reportParams);
        reportInput.setReportFormat(FileFormat.PDF);
        final ReportOutput reportOutput = reportService.createReport(reportInput);
        reportId = ReportViewerUtil.addReportToSession(reportOutput, getSession());
        return PRINTACK;
    }

    private void buildSMS(final PropertyImpl property) {
        final User user = property.getBasicProperty().getPrimaryOwner();
        user.getEmailId();
        final String mobileNumber = user.getMobileNumber();
        final String applicantName = user.getName();
        final List<String> args = new ArrayList<String>();
        args.add(applicantName);
        String smsMsg = "";
        final Map<String, BigDecimal> demandCollMap = ptDemandDAO.getDemandCollMap(property);
        if (null != property && null != property.getState()) {
            final State propertyState = property.getState();
            if (propertyState.getValue().startsWith(WFLOW_ACTION_STEP_CREATE))
                if (propertyState.getValue().endsWith(WF_STATE_REVENUE_CLERK_APPROVED)) {
                    args.add(property.getApplicationNo());
                    args.add(sMSEmailService.getCityName());
                    smsMsg = getText("msg.newpropertycreate.sms", args);
                } else if (propertyState.getValue().endsWith(WF_STATE_REJECTED)) {
                    args.add(property.getApplicationNo());
                    args.add(sMSEmailService.getCityName());
                    smsMsg = getText("msg.newpropertyreject.sms", args);
                } else if (propertyState.getValue().endsWith(WF_STATE_COMMISSIONER_APPROVED)) {
                    args.add(property.getBasicProperty().getUpicNo());
                    args.add(demandCollMap.get(CURR_DMD_STR).toString());
                    args.add(DateUtils.getFormattedDate(property.getBasicProperty().getPropOccupationDate(),
                            "dd/MM/yyyy"));
                    args.add(sMSEmailService.getCityName());
                    smsMsg = getText("msg.newpropertyapprove.sms", args);
                }
        }
        sMSEmailService.sendSMSOnNewAssessment(mobileNumber, smsMsg);
    }

    @Override
    public PropertyImpl getProperty() {
        return property;
    }

    @Override
    public void setProperty(final PropertyImpl property) {
        this.property = property;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(final Long zoneId) {
        this.zoneId = zoneId;
    }

    public Long getWardId() {
        return wardId;
    }

    public void setWardId(final Long wardId) {
        this.wardId = wardId;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(final Long blockId) {
        this.blockId = blockId;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(final String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAddressStr() {
        return addressStr;
    }

    public void setAddressStr(final String addressStr) {
        this.addressStr = addressStr;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(final String pinCode) {
        this.pinCode = pinCode;
    }

    public String getAreaOfPlot() {
        return areaOfPlot;
    }

    public void setAreaOfPlot(final String areaOfPlot) {
        this.areaOfPlot = areaOfPlot;
    }

    public String getDateOfCompletion() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(final String dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    public TreeMap<Integer, String> getFloorNoMap() {
        return floorNoMap;
    }

    public void setFloorNoMap(final TreeMap<Integer, String> floorNoMap) {
        this.floorNoMap = floorNoMap;
    }

    public boolean isChkIsTaxExempted() {
        return chkIsTaxExempted;
    }

    public void setChkIsTaxExempted(final boolean chkIsTaxExempted) {
        this.chkIsTaxExempted = chkIsTaxExempted;
    }

    public String getTaxExemptReason() {
        return taxExemptReason;
    }

    public void setTaxExemptReason(final String taxExemptReason) {
        this.taxExemptReason = taxExemptReason;
    }

    public boolean isChkIsCorrIsDiff() {
        return chkIsCorrIsDiff;
    }

    public void setChkIsCorrIsDiff(final boolean chkIsCorrIsDiff) {
        this.chkIsCorrIsDiff = chkIsCorrIsDiff;
    }

    public String getCorrAddress1() {
        return corrAddress1;
    }

    public void setCorrAddress1(final String corrAddress1) {
        this.corrAddress1 = corrAddress1;
    }

    public String getCorrAddress2() {
        return corrAddress2;
    }

    public void setCorrAddress2(final String corrAddress2) {
        this.corrAddress2 = corrAddress2;
    }

    public String getCorrPinCode() {
        return corrPinCode;
    }

    public void setCorrPinCode(final String corrPinCode) {
        this.corrPinCode = corrPinCode;
    }

    @Override
    public String getGenWaterRate() {
        return genWaterRate;
    }

    @Override
    public void setGenWaterRate(final String genWaterRate) {
        this.genWaterRate = genWaterRate;
    }

    public BasicProperty getBasicProp() {
        return basicProp;
    }

    public void setBasicProp(final BasicProperty basicProp) {
        this.basicProp = basicProp;
    }

    /**
     * This implementation transitions the <code>PropertyImpl</code> to the next
     * workflow state.
     */
    /*
     * @Override protected PropertyImpl property() { return property; }
     */

    public PropertyService getPropService() {
        return propService;
    }

    public void setPropService(final PropertyService propService) {
        this.propService = propService;
    }

    public Long getMutationId() {
        return mutationId;
    }

    public void setMutationId(final Long mutationId) {
        this.mutationId = mutationId;
    }

    public String getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(final String parentIndex) {
        this.parentIndex = parentIndex;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(final String amenities) {
        this.amenities = amenities;
    }

    public String[] getFloorNoStr() {
        return floorNoStr;
    }

    public void setFloorNoStr(final String[] floorNoStr) {
        this.floorNoStr = floorNoStr;
    }

    public String getPropTypeId() {
        return propTypeId;
    }

    public void setPropTypeId(final String propTypeId) {
        this.propTypeId = propTypeId;
    }

    public String getPropUsageId() {
        return propUsageId;
    }

    public void setPropUsageId(final String propUsageId) {
        this.propUsageId = propUsageId;
    }

    public String getPropOccId() {
        return propOccId;
    }

    public void setPropOccId(final String propOccId) {
        this.propOccId = propOccId;
    }

    public String getAckMessage() {
        return ackMessage;
    }

    public void setAckMessage(final String ackMessage) {
        this.ackMessage = ackMessage;
    }

    public Map<String, String> getPropTypeCategoryMap() {
        return propTypeCategoryMap;
    }

    public void setPropTypeCategoryMap(final Map<String, String> propTypeCategoryMap) {
        this.propTypeCategoryMap = propTypeCategoryMap;
    }

    public String getPropTypeCategoryId() {
        return propTypeCategoryId;
    }

    public void setPropTypeCategoryId(final String propTypeCategoryId) {
        this.propTypeCategoryId = propTypeCategoryId;
    }

    public PropertyTypeMaster getPropTypeMstr() {
        return propTypeMstr;
    }

    public void setPropTypeMstr(final PropertyTypeMaster propTypeMstr) {
        this.propTypeMstr = propTypeMstr;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(final String docNumber) {
        this.docNumber = docNumber;
    }

    public Category getPropertyCategory() {
        return propertyCategory;
    }

    public void setPropertyCategory(final Category propertyCategory) {
        this.propertyCategory = propertyCategory;
    }

    public String getNonResPlotArea() {
        return nonResPlotArea;
    }

    public void setNonResPlotArea(final String nonResPlotArea) {
        this.nonResPlotArea = nonResPlotArea;
    }

    public void setPropertyTaxNumberGenerator(final PropertyTaxNumberGenerator propertyTaxNumberGenerator) {
        this.propertyTaxNumberGenerator = propertyTaxNumberGenerator;
    }

    public PropertyImpl getNewProperty() {
        return newProperty;
    }

    public void setNewProperty(final PropertyImpl newProperty) {
        this.newProperty = newProperty;
    }

    public Date getCurrDate() {
        return currDate;
    }

    public void setCurrDate(final Date currDate) {
        this.currDate = currDate;
    }

    public Long getLocality() {
        return locality;
    }

    public void setLocality(final Long locality) {
        this.locality = locality;
    }

    public Long getFloorTypeId() {
        return floorTypeId;
    }

    public void setFloorTypeId(final Long floorTypeId) {
        this.floorTypeId = floorTypeId;
    }

    public Long getRoofTypeId() {
        return roofTypeId;
    }

    public void setRoofTypeId(final Long roofTypeId) {
        this.roofTypeId = roofTypeId;
    }

    public Long getWallTypeId() {
        return wallTypeId;
    }

    public void setWallTypeId(final Long wallTypeId) {
        this.wallTypeId = wallTypeId;
    }

    public Long getWoodTypeId() {
        return woodTypeId;
    }

    public void setWoodTypeId(final Long woodTypeId) {
        this.woodTypeId = woodTypeId;
    }

    public Long getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(final Long ownershipType) {
        this.ownershipType = ownershipType;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(final String wardName) {
        this.wardName = wardName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(final String zoneName) {
        this.zoneName = zoneName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(final String blockName) {
        this.blockName = blockName;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(final String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getRegdDocNo() {
        return regdDocNo;
    }

    public void setRegdDocNo(final String regdDocNo) {
        this.regdDocNo = regdDocNo;
    }

    public Date getRegdDocDate() {
        return regdDocDate;
    }

    public void setRegdDocDate(final Date regdDocDate) {
        this.regdDocDate = regdDocDate;
    }

    @Override
    public AssignmentService getAssignmentService() {
        return assignmentService;
    }

    @Override
    public void setAssignmentService(final AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(final String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public Long getElectionWardId() {
        return electionWardId;
    }

    public void setElectionWardId(final Long electionWardId) {
        this.electionWardId = electionWardId;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public void setEisCommonService(final EisCommonService eisCommonService) {
        this.eisCommonService = eisCommonService;
    }

    public void setBoundaryService(final BoundaryService boundaryService) {
        this.boundaryService = boundaryService;
    }

    public void setSecurityUtils(final SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public void setbasicPropertyService(final PropertyPersistenceService basicPropertyService) {
        this.basicPropertyService = basicPropertyService;
    }

    public void setPropCompletionDate(final Date propCompletionDate) {
        this.propCompletionDate = propCompletionDate;
    }

    public void setFinancialUtil(final FinancialUtil financialUtil) {
        this.financialUtil = financialUtil;
    }

    public void setPropWF(final PropertyImpl propWF) {
    }

    public void setReportService(final ReportService reportService) {
        this.reportService = reportService;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(final Integer reportId) {
        this.reportId = reportId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    public String getNorthBoundary() {
        return northBoundary;
    }

    public void setNorthBoundary(final String northBoundary) {
        this.northBoundary = northBoundary;
    }

    public String getSouthBoundary() {
        return southBoundary;
    }

    public void setSouthBoundary(final String southBoundary) {
        this.southBoundary = southBoundary;
    }

    public String getEastBoundary() {
        return eastBoundary;
    }

    public void setEastBoundary(final String eastBoundary) {
        this.eastBoundary = eastBoundary;
    }

    public String getWestBoundary() {
        return westBoundary;
    }

    public void setWestBoundary(final String westBoundary) {
        this.westBoundary = westBoundary;
    }

    public Map<String, String> getDeviationPercentageMap() {
        return deviationPercentageMap;
    }

    public void setDeviationPercentageMap(final Map<String, String> deviationPercentageMap) {
        this.deviationPercentageMap = deviationPercentageMap;
    }

    public Map<String, String> getGuardianRelationMap() {
        return guardianRelationMap;
    }

    public void setGuardianRelationMap(final Map<String, String> guardianRelationMap) {
        this.guardianRelationMap = guardianRelationMap;
    }

    public List<DocumentType> getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(final List<DocumentType> documentTypes) {
        this.documentTypes = documentTypes;
    }

    public SMSEmailService getsMSEmailService() {
        return sMSEmailService;
    }

    public void setsMSEmailService(final SMSEmailService sMSEmailService) {
        this.sMSEmailService = sMSEmailService;
    }

    public Address getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(final Address ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    @Override
    public String getAdditionalRule() {
        return NEW_ASSESSMENT;
    }

}