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
package org.egov.ptis.actions.modify;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;
import static org.egov.ptis.constants.PropertyTaxConstants.ASSISTANT_DESGN;
import static org.egov.ptis.constants.PropertyTaxConstants.ASSISTANT_ROLE;
import static org.egov.ptis.constants.PropertyTaxConstants.BUILT_UP_PROPERTY;
import static org.egov.ptis.constants.PropertyTaxConstants.COMMISSIONER_DESGN;
import static org.egov.ptis.constants.PropertyTaxConstants.DEMAND_RSNS_LIST;
import static org.egov.ptis.constants.PropertyTaxConstants.DOCS_AMALGAMATE_PROPERTY;
import static org.egov.ptis.constants.PropertyTaxConstants.DOCS_BIFURCATE_PROPERTY;
import static org.egov.ptis.constants.PropertyTaxConstants.DOCS_MODIFY_PROPERTY;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_MODIFY_REASON_ADD_OR_ALTER;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_MODIFY_REASON_AMALG;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_MODIFY_REASON_BIFURCATE;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_MODIFY_REASON_COURT_RULE;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_MODIFY_REASON_DATA_ENTRY;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_MODIFY_REASON_DATA_UPDATE;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_MODIFY_REASON_MODIFY;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_MODIFY_REASON_OBJ;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_STATUS_INACTIVE;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPTYPE_CENTRAL_GOVT;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPTYPE_NON_RESD;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPTYPE_OPEN_PLOT;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPTYPE_RESD;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPTYPE_STATE_GOVT;
import static org.egov.ptis.constants.PropertyTaxConstants.PROP_CREATE_RSN;
import static org.egov.ptis.constants.PropertyTaxConstants.QUERY_BASICPROPERTY_BY_UPICNO;
import static org.egov.ptis.constants.PropertyTaxConstants.QUERY_PROPERTYIMPL_BYID;
import static org.egov.ptis.constants.PropertyTaxConstants.QUERY_WORKFLOW_PROPERTYIMPL_BYID;
import static org.egov.ptis.constants.PropertyTaxConstants.REVENUE_OFFICER_DESGN;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_ISACTIVE;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_ISHISTORY;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_WORKFLOW;
import static org.egov.ptis.constants.PropertyTaxConstants.VACANT_PROPERTY;
import static org.egov.ptis.constants.PropertyTaxConstants.VOUCH_CREATE_RSN_DEACTIVATE;
import static org.egov.ptis.constants.PropertyTaxConstants.WFLOW_ACTION_STEP_COMMISSIONER_APPROVED;
import static org.egov.ptis.constants.PropertyTaxConstants.WFOWNER;
import static org.egov.ptis.constants.PropertyTaxConstants.WFSTATUS;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.Area;
import org.egov.commons.Installment;
import org.egov.demand.model.EgDemandDetails;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.persistence.entity.Address;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.reporting.engine.ReportConstants.FileFormat;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.infra.reporting.viewer.ReportViewerUtil;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infra.web.utils.WebUtils;
import org.egov.infstr.ValidationError;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.DateUtils;
import org.egov.infstr.utils.StringUtils;
import org.egov.ptis.actions.common.CommonServices;
import org.egov.ptis.actions.workflow.WorkflowAction;
import org.egov.ptis.client.util.FinancialUtil;
import org.egov.ptis.client.util.PropertyTaxNumberGenerator;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.egov.ptis.client.workflow.WorkflowDetails;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.demand.PtDemandDao;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.dao.property.PropertyStatusValuesDAO;
import org.egov.ptis.domain.dao.property.PropertyTypeMasterDAO;
import org.egov.ptis.domain.entity.demand.Ptdemand;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.BuiltUpProperty;
import org.egov.ptis.domain.entity.property.Category;
import org.egov.ptis.domain.entity.property.DocumentType;
import org.egov.ptis.domain.entity.property.Floor;
import org.egov.ptis.domain.entity.property.FloorType;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyAddress;
import org.egov.ptis.domain.entity.property.PropertyDetail;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.entity.property.PropertyMutationMaster;
import org.egov.ptis.domain.entity.property.PropertyOccupation;
import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;
import org.egov.ptis.domain.entity.property.PropertyStatus;
import org.egov.ptis.domain.entity.property.PropertyStatusValues;
import org.egov.ptis.domain.entity.property.PropertyTypeMaster;
import org.egov.ptis.domain.entity.property.PropertyUsage;
import org.egov.ptis.domain.entity.property.RoofType;
import org.egov.ptis.domain.entity.property.StructureClassification;
import org.egov.ptis.domain.entity.property.VacantProperty;
import org.egov.ptis.domain.entity.property.WallType;
import org.egov.ptis.domain.entity.property.WoodType;
import org.egov.ptis.domain.service.property.PropertyPersistenceService;
import org.egov.ptis.domain.service.property.PropertyService;
import org.egov.ptis.report.bean.PropertyAckNoticeInfo;
import org.egov.ptis.utils.PTISCacheManager;
import org.egov.ptis.utils.PTISCacheManagerInteface;
import org.springframework.beans.factory.annotation.Autowired;

@ParentPackage("egov")
@ResultPath(value = "/WEB-INF/jsp")
@Results({ @Result(name = "ack", location = "modify/modifyProperty-ack.jsp"),
	@Result(name = "edit", location = "modify/modifyProperty-new.jsp"),
	@Result(name = "new", location = "modify/modifyProperty-new.jsp"),
	@Result(name = "view", location = "modify/modifyProperty-view.jsp"),
	@Result(name = "workFlowError", location = "workflow/workflow-error.jsp"),
	@Result(name = ModifyPropertyAction.PRINTACK, location = "modify/modifyProperty-printAck.jsp")})
@Namespace("/modify")
public class ModifyPropertyAction extends WorkflowAction {
	private static final long serialVersionUID = 1L;
	private static final String RESULT_ACK = "ack";
	private static final String RESULT_ERROR = "error";
	private static final String VIEW = "view";
	private Logger LOGGER = Logger.getLogger(getClass());
	@Autowired
	private PropertyPersistenceService basicPropertyService;
	private PersistenceService<Property, Long> propertyImplService;
	private PersistenceService<Floor, Long> floorService;
	private BasicProperty basicProp;
	private PropertyImpl oldProperty = new PropertyImpl();
	private PropertyImpl propertyModel = new PropertyImpl();
	private boolean chkIsTaxExempted;
	private String taxExemptReason;
	private String areaOfPlot;
	private Map<String, String> waterMeterMap;
	private boolean generalTax;
	private boolean sewerageTax;
	private boolean lightingTax;
	private boolean fireServTax;
	private boolean bigResdBldgTax;
	private boolean educationCess;
	private boolean empGuaCess;
	private TreeMap<Integer, String> floorNoMap;
	private String reasonForModify;
	private String dateOfCompletion;
	private String modifyRsn;
	private Map<String, String> modifyReasonMap;
	private String ownerName;
	private String propAddress;
	private String corrsAddress;
	private String[] amalgPropIds;
	private PropertyService propService;
	private String courtOrdNum;
	private String orderDate;
	private String judgmtDetails;
	private String isAuthProp;
	private String amalgStatus;
	private BasicProperty amalgPropBasicProp;
	private String oldpropId;
	private String oldOwnerName;
	private String oldPropAddress;
	private String ackMessage;
	private Map<String, String> amenitiesMap;
	private String propTypeId;
	private String propTypeCategoryId;
	private String propUsageId;
	private String propOccId;
	private String amenities;
	private String[] floorNoStr = new String[100];
	@Autowired
	private BasicPropertyDAO basicPropertyDAO;
	List<ValidationError> errors = new ArrayList<ValidationError>();
	private PTISCacheManagerInteface ptisCacheMgr = new PTISCacheManager();
	final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	int i = 0;
	private PropertyImpl propWF;// would be current property workflow obj
	private Map<String, String> propTypeCategoryMap;
	FinancialUtil financialUtil = new FinancialUtil();
	private String docNumber;
	private Category propertyCategory;
	private boolean isfloorDetailsRequired;
	private boolean updateData;
	private PropertyAddress propertyAddr;
	private String parcelId;
	private String northBound;
	private String southBound;
	private String eastBound;
	private String westBound;
	private PropertyTaxNumberGenerator propertyTaxNumberGenerator;
	private String errorMessage;
	private String partNo;
	private List<PropertyOwnerInfo> propertyOwners = new ArrayList<PropertyOwnerInfo>();
	private String modificationType;
	private boolean isTenantFloorPresent;
	private String mode;
	@Autowired
	private EisCommonService eisCommonService;
	@Autowired
	private UserService userService;
	@Autowired
	private PropertyTypeMasterDAO propertyTypeMasterDAO;
	@Autowired
	private PropertyStatusValuesDAO propertyStatusValuesDAO;
	@Autowired
	private PtDemandDao ptDemandDAO;
	private Integer buildingPermissionNo;
	private Date buildingPermissionDate;
	private Long floorTypeId;
	private Long roofTypeId;
	private Long wallTypeId;
	private Long woodTypeId;
	private Long apartmentId;
	@Autowired
	private SecurityUtils securityUtils;
	@Autowired
	private AssignmentService assignmentService;
	private List<DocumentType> documentTypes = new ArrayList<>();
	
	public static final String PRINTACK = "printAck";
	private ReportService reportService;
	private Integer reportId = -1;
	private static final String MODIFY_ACK_TEMPLATE = "modifyProperty_ack";

	public ModifyPropertyAction() {
		super();
		propertyModel.setPropertyDetail(new BuiltUpProperty());
		this.addRelatedEntity("propertyDetail.propertyTypeMaster", PropertyTypeMaster.class);
		this.addRelatedEntity("propertyDetail.floorDetails.unitType",
				PropertyTypeMaster.class);
		this.addRelatedEntity("propertyDetail.floorDetails.propertyUsage",
				PropertyUsage.class);
		this.addRelatedEntity("propertyDetail.floorDetails.propertyOccupation",
				PropertyOccupation.class);
		this.addRelatedEntity("propertyDetail.floorDetails.structureClassification",
				StructureClassification.class);
	}

	@SkipValidation
	@Override
	public Object getModel() {
		return propertyModel;
	}

	@SkipValidation
	@Action(value = "/modifyProperty-modifyForm")
	public String modifyForm() {
		LOGGER.debug("Entered into modifyForm, \nIndexNumber: " + indexNumber + ", BasicProperty: "
				+ basicProp + ", OldProperty: " + oldProperty + ", PropertyModel: "
				+ propertyModel);
		String target = "";
		target = populateFormData(Boolean.FALSE);
		LOGGER.debug("modifyForm: IsAuthProp: " + getIsAuthProp() + ", AreaOfPlot: "
				+ getAreaOfPlot() + ", PropTypeId: " + getPropTypeId() + ", PropTypeCategoryId: "
				+ getPropTypeCategoryId() + ", PropUsageId: " + getPropUsageId() + ", PropOccId: "
				+ getPropOccId());
		LOGGER.debug("Exiting from modifyForm");
		return target;
	}

	private String populateFormData(Boolean fromInbox) {
		LOGGER.debug("Entered into populateFormData");
		String target = "";
		Map<String, String> wfMap = basicProp.getPropertyWfStatus();
		PropertyImpl propertyImpl = null;
		String wfStatus = wfMap.get(WFSTATUS);
		if (wfStatus.equalsIgnoreCase("TRUE") && !fromInbox) {
			getSession().put(WFOWNER, wfMap.get(WFOWNER));
			setWfErrorMsg("This Property Under Work flow in " + getSession().get(WFOWNER)
					+ "'s inbox. Please finish pending work flow before do any transactions on it.");
			target = "workFlowError";
		} else {
			setOldProperty((PropertyImpl) getBasicProp().getProperty());
			if (propWF == null && (propertyModel == null || propertyModel.getId() == null)) {
				propertyImpl = (PropertyImpl) oldProperty.createPropertyclone();
			} else {
				propertyImpl = propWF != null ? propWF : propertyModel;
				// setReasonForModify is only for work flow revert changes
				setReasonForModify(propertyImpl.getPropertyDetail().getPropertyMutationMaster()
						.getCode());
			}
			setProperty(propertyImpl);
			setOwnerName(ptisCacheMgr.buildOwnerFullName(basicProp.getPropertyOwnerInfo()));
			setPropAddress(ptisCacheMgr.buildAddressByImplemetation(getBasicProp().getAddress()));
			propertyAddr = basicProp.getAddress();
			corrsAddress = PropertyTaxUtil.getOwnerAddress(basicProp.getPropertyOwnerInfo());
			if (propertyModel.getPropertyDetail().getFloorType() != null) {
				floorTypeId = propertyModel.getPropertyDetail().getFloorType().getId();
			}
			if (propertyModel.getPropertyDetail().getRoofType() != null) {
				roofTypeId = propertyModel.getPropertyDetail().getRoofType().getId();
			}
			if (propertyModel.getPropertyDetail().getWallType() != null) {
				wallTypeId = propertyModel.getPropertyDetail().getWallType().getId();
			}
			if (propertyModel.getPropertyDetail().getWoodType() != null) {
				woodTypeId = propertyModel.getPropertyDetail().getWoodType().getId();
			}
			if (propertyModel.getPropertyDetail().getSitalArea() != null) {
				setAreaOfPlot(propertyModel.getPropertyDetail().getSitalArea().getArea().toString());
			}
			PropertyTypeMaster propertyType = propertyModel.getPropertyDetail()
					.getPropertyTypeMaster();
			propTypeId = propertyType.getId().toString();
			if (propertyModel.getPropertyDetail().getPropertyUsage() != null) {
				propUsageId = propertyModel.getPropertyDetail().getPropertyUsage().getId()
						.toString();
			}
			if (propertyModel.getPropertyDetail().getPropertyOccupation() != null) {
				propOccId = propertyModel.getPropertyDetail().getPropertyOccupation().getId()
						.toString();
			}
			setDocNumber(propertyModel.getDocNumber());
			target = NEW;
		}

		LOGGER.debug("populateFormData - target : " + target + "\n Exiting from populateFormData");
		return target;
	}

	@SkipValidation
	@Action(value = "/modifyProperty-view")
	public String view() {
		LOGGER.debug(
				"Entered into view, BasicProperty: " + basicProp + ", ModelId: " + getModelId());
		if (getModelId() != null) {
			propertyModel = (PropertyImpl) getPersistenceService()
					.findByNamedQuery(QUERY_PROPERTYIMPL_BYID, Long.valueOf(getModelId()));
			setModifyRsn(propertyModel.getPropertyDetail().getPropertyMutationMaster().getCode());
			LOGGER.debug("view: PropertyModel by model id: " + propertyModel);
		}
		String currWfState = propertyModel.getState().getNextAction();
		populateFormData(Boolean.TRUE);
		corrsAddress = PropertyTaxUtil.getOwnerAddress(propertyModel.getBasicProperty().getPropertyOwnerInfo());
		amalgPropIds = new String[10];
		if (propertyModel.getPropertyDetail().getFloorDetails().size() > 0) {
			setFloorDetails(propertyModel);
		}
		if (!currWfState.endsWith(WFLOW_ACTION_STEP_COMMISSIONER_APPROVED)) {
			int i = 0;
			for (PropertyStatusValues propstatval : basicProp.getPropertyStatusValuesSet()) {
				if (propstatval.getIsActive().equals("W")) {
					setPropStatValForView(propstatval);
					LOGGER.debug(
							"view: PropertyStatusValues for new modify screen: " + propstatval);
				}
				// setting the amalgamated properties
				LOGGER.debug("view: Amalgamated property ids:");
				if (PROP_CREATE_RSN.equals(propstatval.getPropertyStatus().getStatusCode())
						&& propstatval.getIsActive().equals("Y")) {
					if (propstatval.getReferenceBasicProperty() != null) {
						amalgPropIds[i] = propstatval.getReferenceBasicProperty().getUpicNo();
						LOGGER.debug(amalgPropIds[i] + ", ");
						i++;
					}
				}
			}
		}

		if (currWfState.endsWith(WFLOW_ACTION_STEP_COMMISSIONER_APPROVED)) {
			setIsApprPageReq(Boolean.FALSE);
			if (basicProp.getUpicNo() != null && !basicProp.getUpicNo().isEmpty()) {
				setIndexNumber(basicProp.getUpicNo());
			}

			int i = 0;
			for (PropertyStatusValues propstatval : basicProp.getPropertyStatusValuesSet()) {
				if (propstatval.getIsActive().equals("Y")) {
					setPropStatValForView(propstatval);
					LOGGER.debug("PropertyStatusValues for view modify screen: " + propstatval);
				}
				// setting the amalgamated properties
				LOGGER.debug("view: Amalgamated property ids:");
				if (PROP_CREATE_RSN.equals(propstatval.getPropertyStatus().getStatusCode())
						&& propstatval.getIsActive().equals("Y")) {
					if (propstatval.getReferenceBasicProperty() != null) {
						amalgPropIds[i] = propstatval.getReferenceBasicProperty().getUpicNo();
						LOGGER.debug(amalgPropIds[i] + ", ");
						i++;
					}
				}
			}
		}

		propertyAddr = basicProp.getAddress();
		setModifyRsn(propertyModel.getPropertyDetail().getPropertyMutationMaster().getCode());
		setDocNumber(propertyModel.getDocNumber());
		LOGGER.debug("view: ModifyReason: " + getModifyRsn());
		LOGGER.debug("Exiting from view");
		return VIEW;
	}

	@SkipValidation
	@Action(value = "/modifyProperty-forward")
	public String forwardModify() {
		LOGGER.debug("forwardModify: Modify property started " + propertyModel);
		this.validate();
		if (hasErrors()) {
			if (ASSISTANT_DESGN.equalsIgnoreCase(userDesgn)) {
				return NEW;
			} else if (REVENUE_OFFICER_DESGN.equalsIgnoreCase(userDesgn)
					|| COMMISSIONER_DESGN.equalsIgnoreCase(userDesgn)) {
				return VIEW;
			}
		}
		long startTimeMillis = System.currentTimeMillis();
		if (getModelId() != null && !getModelId().trim().isEmpty()) {
			propWF = (PropertyImpl) getPersistenceService()
					.findByNamedQuery(QUERY_WORKFLOW_PROPERTYIMPL_BYID, Long.valueOf(getModelId()));
			
			LOGGER.debug("forwardModify: Workflow property: " + propWF);
			basicProp = propWF.getBasicProperty();
			setBasicProp(basicProp);
		} else {
			populateBasicProp();
		}
		oldProperty = (PropertyImpl) basicProp.getProperty();
		modifyBasicProp(getDocNumber());
		transitionWorkFlow(propertyModel);
		basicPropertyService.applyAuditing(propertyModel.getState());
		basicPropertyService.update(basicProp);
		setModifyRsn(propertyModel.getPropertyDetail().getPropertyMutationMaster().getCode());
		prepareAckMsg();
		addActionMessage(getText("property.forward.success",
				new String[] { propertyModel.getBasicProperty().getUpicNo() }));
		long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
		LOGGER.info("forwardModify: Modify property forwarded successfully; Time taken(ms) = "
				+ elapsedTimeMillis);
		LOGGER.debug("forwardModify: Modify property forward ended");
		return RESULT_ACK;
	}

	@ValidationErrorPage(value = "view")
	@Action(value = "/modifyProperty-forwardView")
	public String forwardView() {
		LOGGER.debug("Entered into forwardView");
		propertyModel = (PropertyImpl) getPersistenceService()
				.findByNamedQuery(QUERY_PROPERTYIMPL_BYID, Long.valueOf(getModelId()));
		LOGGER.debug("forwardView: Workflow property: " + propertyModel);
		transitionWorkFlow(propertyModel);
		setModifyRsn(propertyModel.getPropertyDetail().getPropertyMutationMaster().getCode());
		prepareAckMsg();
		addActionMessage(getText("property.forward.success",
				new String[] { propertyModel.getBasicProperty().getUpicNo() }));
		LOGGER.debug("Exiting from forwardView");
		return RESULT_ACK;
	}

	@SkipValidation
	@Action(value = "modifyProperty-approve")
	public String approve() {
		LOGGER.debug("Enter method approve");
		amalgPropIds = new String[10];
		propertyModel = (PropertyImpl) getPersistenceService()
				.findByNamedQuery(QUERY_PROPERTYIMPL_BYID, Long.valueOf(getModelId()));
		LOGGER.debug("approve: Workflow property: " + propertyModel);
		basicProp = propertyModel.getBasicProperty();
		oldProperty = (PropertyImpl) basicProp.getProperty();

		transitionWorkFlow(propertyModel);
		//FIX ME -- Uncomment this while implementing amalgamation of assessment
		/*int i = 0;
		for (PropertyStatusValues propstatval : basicProp.getPropertyStatusValuesSet()) {
			if (propstatval.getIsActive().equals("Y")) {
				if (PROP_CREATE_RSN.equals(propstatval.getPropertyStatus().getStatusCode())) {
					if (propstatval.getReferenceBasicProperty() != null) {
						amalgPropIds[i] = propstatval.getReferenceBasicProperty().getUpicNo();
						i++;
					}
				}
			}
		}
		setAmalgPropInactive();*/
		if (!PROPERTY_MODIFY_REASON_OBJ.equals(modifyRsn)) {
			propService.setWFPropStatValActive(basicProp);
		}

		if ((PROPERTY_MODIFY_REASON_MODIFY.equals(modifyRsn) || PROPERTY_MODIFY_REASON_AMALG.equals(modifyRsn)
						|| PROPERTY_MODIFY_REASON_BIFURCATE.equals(modifyRsn))) {
			//createVoucher(); // Creates voucher
		}

		setModifyRsn(propertyModel.getPropertyDetail().getPropertyMutationMaster().getCode());

		/**
		 * The old property will be made history and the workflow property will
		 * be made active only when all the changes are completed in case of
		 * modify reason is 'MODIFY'
		 */
		if (((PROPERTY_MODIFY_REASON_MODIFY.equals(getModifyRsn())))) {

			propertyModel.setStatus(STATUS_ISACTIVE);
			oldProperty.setStatus(STATUS_ISHISTORY);
			propertyTaxUtil.makeTheEgBillAsHistory(basicProp);
		}
		// upload docs
		processAndStoreDocumentsWithReason(basicProp, getReason(modifyRsn));
		if (PROPERTY_MODIFY_REASON_MODIFY.equals(getModifyRsn())) {
			updateAddress();
		}

		basicPropertyService.update(basicProp);
		setBasicProp(basicProp);
		setAckMessage(getText("property.approve.success",
				new String[] { propertyModel.getBasicProperty().getUpicNo() }));
		LOGGER.debug("Exiting approve");
		return RESULT_ACK;
	}

	@SkipValidation
	@Action(value = "/modifyProperty-reject")
	public String reject() {
		LOGGER.debug("reject: Property rejection started");
		propertyModel = (PropertyImpl) getPersistenceService()
				.findByNamedQuery(QUERY_PROPERTYIMPL_BYID, Long.valueOf(getModelId()));
		LOGGER.debug("reject: Property: " + propertyModel);
		BasicProperty basicProperty = propertyModel.getBasicProperty();
		setBasicProp(basicProperty);
		if (ASSISTANT_DESGN.equalsIgnoreCase(userDesgn)) {
			propertyModel.setStatus(STATUS_ISHISTORY);
		}
		LOGGER.debug("reject: BasicProperty: " + basicProperty);
		transitionWorkFlow(propertyModel);
		propertyImplService.update(propertyModel);
		setModifyRsn(propertyModel.getPropertyDetail().getPropertyMutationMaster().getCode());
		setAckMessage("Property Rejected Successfully and forwarded to initiator : "
				+ propertyModel.getCreatedBy().getUsername() + " with Index Number : ");
		LOGGER.debug("reject: BasicProperty: " + getBasicProp() + "AckMessage: " + getAckMessage());
		LOGGER.debug("reject: Property rejection ended");

		return RESULT_ACK;
	}

	/*@SkipValidation
	public String editOwnerForm() {
		LOGGER.debug(
				"Entered into editOwnerForm, edit facility for Owner Name and PartNO, indexNumber: "
						+ indexNumber);
		setOwnerName(
				ptisCacheMgr.buildOwnerFullName(basicProp.getPropertyOwnerInfo()));
		setPropertyOwners(basicProp.getPropertyOwnerInfo());
		return "ownerForm";
	}*/

	/*@SkipValidation
	public String updateOwner() {
		LOGGER.debug("Entered into updateOwner");
		List<PropertyOwnerInfo> existingOwners = basicProp.getPropertyOwnerInfo();
		List<PropertyOwnerInfo> newOwners = getPropertyOwners();
		StringBuilder auditDetail1 = new StringBuilder();
		PropertyOwnerInfo propertyOwner = null;
		int index = 0;

		if (!newOwners.isEmpty()) {
			for (PropertyOwnerInfo propOwner : existingOwners) {
				propertyOwner = newOwners.get(index);
				propOwner.setOrderNo(index + 1);
				LOGGER.debug("updateOwner new owner " + propertyOwner.getName());
				auditDetail1.append("Owner ").append(index + 1).append(":")
						.append(propOwner.getName()).append(PIPE_CHAR)
						.append(propertyOwner.getName());
				propOwner.setName(propertyOwner.getName());
				index++;
			}
		}
		auditDetail1.append("Part No : ").append(basicProp.getPartNo()).append(PIPE_CHAR)
				.append(partNo);
		LOGGER.debug(
				"updateOwner, old part no=" + basicProp.getPartNo() + ", new part no=" + partNo);
		basicProp.setPartNo(partNo);
		setAckMessage(getText("property.editowner.success"));
		return RESULT_ACK;
	}*/

	@Override
	@SuppressWarnings("unchecked")
	public void prepare() {
		LOGGER.debug("Entered into preapre, ModelId: " + getModelId());
		super.prepare();
		setUserInfo();
		if (getModelId() != null && !getModelId().isEmpty()) {
			setBasicProp((BasicProperty) getPersistenceService().find(
					"select prop.basicProperty from PropertyImpl prop where prop.id=?",
					Long.valueOf(getModelId())));
			LOGGER.debug("prepare: BasicProperty: " + basicProp);
			propWF = (PropertyImpl) getPersistenceService().findByNamedQuery(
					QUERY_WORKFLOW_PROPERTYIMPL_BYID, Long.valueOf(getModelId()));
			if (propWF != null) {
				setProperty(propWF);
			}
		} else if (indexNumber != null && !indexNumber.trim().isEmpty()) {
			setBasicProp((BasicProperty) getPersistenceService().findByNamedQuery(
					QUERY_BASICPROPERTY_BY_UPICNO, indexNumber));
		}
		this.documentTypes = propService.getPropertyModificationDocumentTypes();
		List<FloorType> floorTypes = getPersistenceService()
				.findAllBy("from FloorType order by name");
		List<RoofType> roofTypes = getPersistenceService().findAllBy("from RoofType order by name");
		List<WallType> wallTypes = getPersistenceService().findAllBy("from WallType order by name");
		List<WoodType> woodTypes = getPersistenceService().findAllBy("from WoodType order by name");
		List<PropertyTypeMaster> propTypeList = getPersistenceService()
				.findAllBy("from PropertyTypeMaster order by orderNo");
		List<PropertyMutationMaster> propMutList = getPersistenceService().findAllBy(
				"from PropertyMutationMaster where type = 'MODIFY' and code not in('AMALG','BIFURCATE', 'DATA_ENTRY')");
		List<String> StructureList = getPersistenceService()
				.findAllBy("from StructureClassification");
		List<PropertyUsage> usageList = getPersistenceService()
				.findAllBy("from PropertyUsage order by usageName");
		List<PropertyOccupation> propOccList = getPersistenceService()
				.findAllBy("from PropertyOccupation");
		List<String> ageFacList = getPersistenceService().findAllBy("from DepreciationMaster");
		setFloorNoMap(CommonServices.floorMap());
		addDropdownData("floorType", floorTypes);
		addDropdownData("roofType", roofTypes);
		addDropdownData("wallType", wallTypes);
		addDropdownData("woodType", woodTypes);
		addDropdownData("PropTypeMaster", propTypeList);
		addDropdownData("OccupancyList", propOccList);
		addDropdownData("UsageList", usageList);
		addDropdownData("MutationList", propMutList);
		addDropdownData("StructureList", StructureList);
		addDropdownData("AgeFactorList", ageFacList);
		addDropdownData("Appartments", Collections.EMPTY_LIST);
		if (getBasicProp() != null) {
			setPropAddress(ptisCacheMgr.buildAddressByImplemetation(getBasicProp().getAddress()));
		}
		if (propWF != null) {
			setOwnerName(ptisCacheMgr.buildOwnerFullName(propWF.getBasicProperty().getPropertyOwnerInfo()));
			List<PropertyOwnerInfo> ownerSet = propWF.getBasicProperty().getPropertyOwnerInfo();
			if (ownerSet != null && !ownerSet.isEmpty()) {
				for (PropertyOwnerInfo owner : ownerSet) {
					for (Address address : owner.getOwner().getAddress()) {
						corrsAddress = ptisCacheMgr.buildAddressByImplemetation(address);
						break;
					}
				}
			}
			for (PropertyStatusValues propstatval : basicProp.getPropertyStatusValuesSet()) {
				if (propstatval.getIsActive().equals("W")) {
					setPropStatValForView(propstatval);
				}
			}
		}
		LOGGER.debug("Exiting from preapre, ModelId: " + getModelId());
	}

	private void modifyBasicProp(String docNumber) {
		LOGGER.debug("Entered into modifyBasicProp, BasicProperty: " + basicProp);
		LOGGER.debug("modifyBasicProp: PropTypeId: " + propTypeId + ", PropUsageId: " + propUsageId
				+ ", PropOccId: " + propOccId + ", statusModifyRsn: " + modifyRsn
				+ ", ReasonForModify: " + reasonForModify + ", NoOfAmalgmatedProps: "
				+ ((amalgPropIds != null) ? amalgPropIds.length : "NULL"));

		Date propCompletionDate = null;
		String mutationCode = null;
		Character status = STATUS_WORKFLOW;
		PropertyTypeMaster proptypeMstr = propertyTypeMasterDAO
				.getPropertyTypeMasterById(Integer.valueOf(propTypeId));
		if (!proptypeMstr.getCode().equalsIgnoreCase(PROPTYPE_OPEN_PLOT)) {
			if ((proptypeMstr.getCode().equalsIgnoreCase(PROPTYPE_STATE_GOVT)
					|| proptypeMstr.getCode().equalsIgnoreCase(PROPTYPE_CENTRAL_GOVT))
					&& isfloorDetailsRequired) {
				propCompletionDate = propService.getPropOccupatedDate(getDateOfCompletion());
			} else {
				propCompletionDate = propService.getLowestDtOfCompFloorWise(
						propertyModel.getPropertyDetail().getFloorDetails());
			}

		} else {
			propCompletionDate = propService.getPropOccupatedDate(getDateOfCompletion());
		}
		// AMALG & BIFUR
		if (PROPERTY_MODIFY_REASON_AMALG.equals(modifyRsn)
				|| PROPERTY_MODIFY_REASON_BIFURCATE.equals(modifyRsn)) {
			basicProp.addPropertyStatusValues(propService.createPropStatVal(basicProp,
					getModifyRsn(), propCompletionDate, null, null, null, null, buildingPermissionDate, buildingPermissionNo));
			if (PROPERTY_MODIFY_REASON_AMALG.equals(modifyRsn)) {
				propService.createAmalgPropStatVal(amalgPropIds, basicProp);
			}
			mutationCode = getModifyRsn();
		} else if (PROPERTY_MODIFY_REASON_MODIFY.equals(modifyRsn)) { // MODIFY
			basicProp.addPropertyStatusValues(
					propService.createPropStatVal(basicProp, PROPERTY_MODIFY_REASON_MODIFY,
							propCompletionDate, null, null, null, null, buildingPermissionDate, buildingPermissionNo));
			mutationCode = getReasonForModify();
		} else if (PROPERTY_MODIFY_REASON_COURT_RULE.equals(getReasonForModify())) { // COURT_RULE
			basicProp.addPropertyStatusValues(propService.createPropStatVal(basicProp,
					PROPERTY_MODIFY_REASON_MODIFY, propCompletionDate, getCourtOrdNum(),
					propService.getPropOccupatedDate(getOrderDate()), getJudgmtDetails(), null,
					buildingPermissionDate, buildingPermissionNo));
			mutationCode = getReasonForModify();
		}
		basicProp.setPropOccupationDate(propCompletionDate);

		setProperty(propService.createProperty(propertyModel, getAreaOfPlot(), mutationCode,
				propTypeId, propUsageId, propOccId, status, propertyModel.getDocNumber(), null,
				isfloorDetailsRequired, floorTypeId, roofTypeId, wallTypeId, woodTypeId));

		propertyModel.setBasicProperty(basicProp);
		propertyModel.setEffectiveDate(propCompletionDate);
		propertyModel.getPropertyDetail().setEffective_date(propCompletionDate);

		PropertyImpl newProperty = (PropertyImpl) propService.createDemand(propertyModel,
				oldProperty, propCompletionDate, isfloorDetailsRequired);

		PropertyTypeMaster propTypeMstr = (PropertyTypeMaster) getPersistenceService()
				.find("from PropertyTypeMaster ptm where ptm.code = ?", PROPTYPE_OPEN_PLOT);

		Long oldPropTypeId = oldProperty.getPropertyDetail().getPropertyTypeMaster().getId();

		/*
		 * if modifying from OPEN_PLOT to OTHERS or from OTHERS to OPEN_PLOT
		 * property type
		 */
		if ((oldPropTypeId == propTypeMstr.getId()
				&& Long.parseLong(propTypeId) != propTypeMstr.getId())
				|| (oldPropTypeId != propTypeMstr.getId()
						&& Long.parseLong(propTypeId) == propTypeMstr.getId())) {

			if ((propTypeMstr != null)
					&& (StringUtils.equals(propTypeMstr.getId().toString(), propTypeId))) {
				changePropertyDetail(newProperty, new VacantProperty(), 0);
			} else {
				changePropertyDetail(newProperty, new BuiltUpProperty(),
						newProperty.getPropertyDetail().getNo_of_floors());
			}
		}

		Property modProperty = null;
		Property previousProperty = oldProperty;
		if (previousProperty == null) {
			LOGGER.info("modifyBasicProp, Could not get the previous property. DCB for arrears will be incorrect");
		} else {
			modProperty = propService.createDemandForModify(previousProperty, newProperty,
					propCompletionDate);
			modProperty = propService.createArrearsDemand(previousProperty, propCompletionDate,
					newProperty);
		}

		Map<Installment, Set<EgDemandDetails>> demandDetailsSetByInstallment = null;
		List<Installment> installments = null;

		Set<EgDemandDetails> oldEgDemandDetailsSet = getOldDemandDetails(oldProperty, newProperty);
		demandDetailsSetByInstallment = getEgDemandDetailsSetByInstallment(oldEgDemandDetailsSet);
		installments = new ArrayList<Installment>(demandDetailsSetByInstallment.keySet());
		Collections.sort(installments);
		for (Installment inst : installments) {
			Map<String, BigDecimal> dmdRsnAmt = new LinkedHashMap<String, BigDecimal>();
			for (String rsn : DEMAND_RSNS_LIST) {
				EgDemandDetails newDmndDtls = propService
						.getEgDemandDetailsForReason(demandDetailsSetByInstallment.get(inst), rsn);
				if (newDmndDtls != null && newDmndDtls.getAmtCollected() != null) {
					// If there is collection then add to map
					if (newDmndDtls.getAmtCollected().compareTo(BigDecimal.ZERO) > 0) {
						dmdRsnAmt.put(
								newDmndDtls.getEgDemandReason().getEgDemandReasonMaster().getCode(),
								newDmndDtls.getAmtCollected());
					}
				}
			}
			propService.getExcessCollAmtMap().put(inst, dmdRsnAmt);
		}
		Ptdemand currentDemand = getCurrrentDemand(modProperty);
		demandDetailsSetByInstallment = getEgDemandDetailsSetByInstallment(
				currentDemand.getEgDemandDetails());
		installments = new ArrayList<Installment>(demandDetailsSetByInstallment.keySet());
		Collections.sort(installments);
		for (Installment inst : installments) {
			Map<String, BigDecimal> dmdRsnAmt = new LinkedHashMap<String, BigDecimal>();
			for (String rsn : DEMAND_RSNS_LIST) {
				EgDemandDetails newDmndDtls = propService
						.getEgDemandDetailsForReason(demandDetailsSetByInstallment.get(inst), rsn);
				if (newDmndDtls != null && newDmndDtls.getAmtCollected() != null) {
					BigDecimal extraCollAmt = newDmndDtls.getAmtCollected()
							.subtract(newDmndDtls.getAmount());
					// If there is extraColl then add to map
					if (extraCollAmt.compareTo(BigDecimal.ZERO) > 0) {
						dmdRsnAmt.put(
								newDmndDtls.getEgDemandReason().getEgDemandReasonMaster().getCode(),
								extraCollAmt);
						newDmndDtls.setAmtCollected(
								newDmndDtls.getAmtCollected().subtract(extraCollAmt));
						newDmndDtls.setModifiedDate(new Date());
					}
				}
			}
			propService.getExcessCollAmtMap().put(inst, dmdRsnAmt);
		}

		LOGGER.info("Excess Collection - " + propService.getExcessCollAmtMap());

		propService.adjustExcessCollectionAmount(installments, demandDetailsSetByInstallment,
				currentDemand);

		if (modProperty != null && !modProperty.getDocuments().isEmpty()) {
			propService.processAndStoreDocument(modProperty.getDocuments());
		}

		if (modProperty == null) {
			basicProp.addProperty(newProperty);
		} else {
			basicProp.addProperty(modProperty);
		}

		if (!newProperty.getPropertyDetail().getPropertyTypeMaster().getCode()
				.equalsIgnoreCase(PROPTYPE_OPEN_PLOT)) {
			propService.createAttributeValues(newProperty, PropertyTaxUtil.getCurrentInstallment());
		}

		LOGGER.debug("Exiting modifyBasicProp");
	}

	private Set<EgDemandDetails> getOldDemandDetails(Property oldProperty, Property newProperty) {

		Set<EgDemandDetails> oldDemandDetails = new HashSet<EgDemandDetails>();

		for (EgDemandDetails dd : getCurrrentDemand(oldProperty).getEgDemandDetails()) {
			if (dd.getEgDemandReason().getEgInstallmentMaster().getFromDate()
					.before(newProperty.getEffectiveDate())) {
				oldDemandDetails.add(dd);
			}
		}

		return oldDemandDetails;
	}

	private Map<Installment, Set<EgDemandDetails>> getEgDemandDetailsSetByInstallment(
			Set<EgDemandDetails> demandDetailsSet) {
		Map<Installment, Set<EgDemandDetails>> newEgDemandDetailsSetByInstallment = new HashMap<Installment, Set<EgDemandDetails>>();

		for (EgDemandDetails dd : demandDetailsSet) {

			if (dd.getAmtCollected() == null) {
				dd.setAmtCollected(ZERO);
			}

			if (newEgDemandDetailsSetByInstallment
					.get(dd.getEgDemandReason().getEgInstallmentMaster()) == null) {
				Set<EgDemandDetails> ddSet = new HashSet<EgDemandDetails>();
				ddSet.add(dd);
				newEgDemandDetailsSetByInstallment
						.put(dd.getEgDemandReason().getEgInstallmentMaster(), ddSet);
			} else {
				newEgDemandDetailsSetByInstallment
						.get(dd.getEgDemandReason().getEgInstallmentMaster()).add(dd);
			}
		}

		return newEgDemandDetailsSetByInstallment;
	}

	/**
	 * @param property
	 * @return
	 */
	private Ptdemand getCurrrentDemand(Property property) {
		Ptdemand currentDemand = null;

		for (Ptdemand ptdemand : property.getPtDemandSet()) {
			if (ptdemand.getEgInstallmentMaster().equals(PropertyTaxUtil.getCurrentInstallment())) {
				currentDemand = ptdemand;
				break;
			}
		}
		return currentDemand;
	}

	/**
	 * Changes the property details to {@link BuiltUpProperty} or
	 * {@link VacantProperty}
	 * 
	 * @param modProperty
	 *            the property which is getting modified
	 * 
	 * @param propDetail
	 *            the {@link PropertyDetail} type, either
	 *            {@link BuiltUpProperty} or {@link VacantProperty}
	 * 
	 * @param numOfFloors
	 *            the no. of floors which is dependent on {@link PropertyDetail}
	 * 
	 * @see {@link PropertyDetail}, {@link BuiltUpProperty},
	 *      {@link VacantProperty}
	 */

	private void changePropertyDetail(Property modProperty, PropertyDetail propDetail,
			Integer numOfFloors) {

		LOGGER.debug("Entered into changePropertyDetail, Property is Vacant Land");

		PropertyDetail propertyDetail = modProperty.getPropertyDetail();

		propDetail.setSitalArea(propertyDetail.getSitalArea());
		propDetail.setTotalBuiltupArea(propertyDetail.getTotalBuiltupArea());
		propDetail.setCommBuiltUpArea(propertyDetail.getCommBuiltUpArea());
		propDetail.setPlinthArea(propertyDetail.getPlinthArea());
		propDetail.setCommVacantLand(propertyDetail.getCommVacantLand());
		propDetail.setSurveyNumber(propertyDetail.getSurveyNumber());
		propDetail.setFieldVerified(propertyDetail.getFieldVerified());
		propDetail.setFieldVerificationDate(propertyDetail.getFieldVerificationDate());
		propDetail.setFloorDetails(propertyDetail.getFloorDetails());
		propDetail.setPropertyDetailsID(propertyDetail.getPropertyDetailsID());
		propDetail.setWater_Meter_Num(propertyDetail.getWater_Meter_Num());
		propDetail.setElec_Meter_Num(propertyDetail.getElec_Meter_Num());
		propDetail.setNo_of_floors(numOfFloors);
		propDetail.setFieldIrregular(propertyDetail.getFieldIrregular());
		propDetail.setCompletion_year(propertyDetail.getCompletion_year());
		propDetail.setEffective_date(propertyDetail.getEffective_date());
		propDetail.setDateOfCompletion(propertyDetail.getDateOfCompletion());
		propDetail.setProperty(propertyDetail.getProperty());
		propDetail.setUpdatedTime(propertyDetail.getUpdatedTime());
		propDetail.setPropertyTypeMaster(propertyDetail.getPropertyTypeMaster());
		propDetail.setPropertyType(propertyDetail.getPropertyType());
		propDetail.setInstallment(propertyDetail.getInstallment());
		propDetail.setPropertyOccupation(propertyDetail.getPropertyOccupation());
		propDetail.setPropertyMutationMaster(propertyDetail.getPropertyMutationMaster());
		propDetail.setComZone(propertyDetail.getComZone());
		propDetail.setCornerPlot(propertyDetail.getCornerPlot());

		if (numOfFloors == 0) {
			propDetail.setPropertyUsage(propertyDetail.getPropertyUsage());
		} else {
			propDetail.setPropertyUsage(null);
		}

		propDetail.setExtra_field1(propertyDetail.getExtra_field1());
		propDetail.setExtra_field2(propertyDetail.getExtra_field2());
		propDetail.setExtra_field3(propertyDetail.getExtra_field3());
		propDetail.setExtra_field4(propertyDetail.getExtra_field4());
		propDetail.setExtra_field5(propertyDetail.getExtra_field5());
		propDetail.setExtra_field6(propertyDetail.getExtra_field6());
		propDetail.setManualAlv(propertyDetail.getManualAlv());
		propDetail.setOccupierName(propertyDetail.getOccupierName());

		modProperty.setPropertyDetail(propDetail);

		LOGGER.debug("Exiting from changePropertyDetail");
	}

	private void populateBasicProp() {
		LOGGER.debug("Entered into populateBasicProp");
		if (basicProp == null) {
			if (indexNumber != null && !indexNumber.trim().isEmpty()) {
				setBasicProp((BasicProperty) getPersistenceService()
						.findByNamedQuery(QUERY_BASICPROPERTY_BY_UPICNO, indexNumber));
			} else if (getModelId() != null && !getModelId().equals("")) {
				setBasicProp(((PropertyImpl) getPersistenceService()
						.findByNamedQuery(QUERY_PROPERTYIMPL_BYID, Long.valueOf(getModelId())))
								.getBasicProperty());
			}
		}
		LOGGER.debug("Exiting from populateBasicProp");
	}

	//FIX ME -- Uncomment while implementing amalgamation of assessment

	/*@SkipValidation
	public String getStatus() {
		LOGGER.debug("Entered into getStatus");
		checkAmalgStatus();
		LOGGER.debug("Exiting from getStatus");
		return "showStatus";
	}*/

	/*private void checkAmalgStatus() {
		LOGGER.debug("Entered into checkAmalgStatus, OldPropId: " + oldpropId);

		List<String> code = new ArrayList<String>();
		BigDecimal currDemand = BigDecimal.ZERO;
		BigDecimal currDemandDue = BigDecimal.ZERO;
		BigDecimal currCollection = BigDecimal.ZERO;
		BigDecimal arrDemand = BigDecimal.ZERO;
		BigDecimal arrCollection = BigDecimal.ZERO;
		BigDecimal arrearsDue = BigDecimal.ZERO;
		PropertyStatusValues propStatVal = null;
		code.add(PROPERTY_STATUS_MARK_DEACTIVE);
		amalgPropBasicProp = basicPropertyDAO.getBasicPropertyByPropertyID(oldpropId);
		LOGGER.debug("Amalgmated BasicProperty: " + amalgPropBasicProp);
		Map<String, String> wfMap = null;
		String wfStatus = null;
		if (amalgPropBasicProp != null) {
			propStatVal = propertyStatusValuesDAO
					.getLatestPropertyStatusValuesByPropertyIdAndCode(oldpropId, code);
			wfMap = amalgPropBasicProp.getPropertyWfStatus();
			wfStatus = wfMap.get(WFSTATUS);
			if (!wfStatus.equalsIgnoreCase("TRUE")) {
				PropertyImpl oldProp = (PropertyImpl) amalgPropBasicProp.getProperty();
				setOldOwnerName(ptisCacheMgr.buildOwnerFullName(oldProp.getPropertyOwnerInfo()));
				setOldPropAddress(
						ptisCacheMgr.buildAddressByImplemetation(amalgPropBasicProp.getAddress()));

				Map<String, BigDecimal> DmdCollMap = ptDemandDAO.getDemandCollMap(oldProp);
				currDemand = DmdCollMap.get("CURR_DMD");
				arrDemand = DmdCollMap.get("ARR_DMD");
				currCollection = DmdCollMap.get("CURR_COLL");
				arrCollection = DmdCollMap.get("ARR_COLL");
				currDemandDue = currDemand.subtract(currCollection);
				arrearsDue = arrDemand.subtract(arrCollection);
			}
		}

		if (amalgPropBasicProp == null) {
			setAmalgStatus("Property does not Exist");
		} else if (propStatVal != null) {
			setAmalgStatus("Property is Marked for Deactivation");
		} else if (!amalgPropBasicProp.isActive()) {
			setAmalgStatus("Property is Deactivated");
		} else if (wfStatus.equalsIgnoreCase("TRUE")) {
			setAmalgStatus("This Property Under Work flow in " + wfMap.get(WFOWNER)
					+ "'s inbox. Please finish pending work flow before doing any transactions on it.");
		} else if (currDemandDue.compareTo(BigDecimal.ZERO) == 1
				|| arrearsDue.compareTo(BigDecimal.ZERO) == 1) {
			setAmalgStatus("Property has Pending Balance");
		} else {
			setAmalgStatus("Property is Ready for Amalgamation");
		}

		LOGGER.debug("AmalgStatus: " + getAmalgStatus() + "\nExiting from checkAmalgStatus");
	}*/

	private void setFloorDetails(Property property) {
		LOGGER.debug("Entered into setFloorDetails, Property: " + property);

		List<Floor> flrDtSet = property.getPropertyDetail().getFloorDetails();
		int i = 0;
		for (Floor flr : flrDtSet) {
			floorNoStr[i] = (propertyTaxUtil.getFloorStr(flr.getFloorNo()));
			i++;
		}

		LOGGER.debug("Exiting from setFloorDetails: ");
	}

	public List<Floor> getFloorDetails() {
		return new ArrayList<Floor>(propertyModel.getPropertyDetail().getFloorDetails());
	}

	@Override
	public void validate() {
		LOGGER.debug("Entered into validate, ReasonForModify: " + reasonForModify + ", ModifyRsn: "
				+ modifyRsn);
		if (reasonForModify == null || reasonForModify.equals("-1")) {
			addActionError(getText("mandatory.rsnForMdfy"));
		}
		validateProperty(propertyModel, areaOfPlot, dateOfCompletion, chkIsTaxExempted,
				taxExemptReason, isAuthProp, propTypeId, propUsageId, propOccId, 
				isfloorDetailsRequired, isUpdateData(), floorTypeId, roofTypeId, wallTypeId, woodTypeId);

		super.validate();

		LOGGER.debug("Exiting from validate, BasicProperty: " + getBasicProp());
	}

	private void setAmalgPropInactive() {
		LOGGER.debug("Entered into setAmalgPropInactive: " + propertyModel);

		if (PROPERTY_MODIFY_REASON_AMALG
				.equals(propertyModel.getPropertyDetail().getPropertyMutationMaster().getCode())) {
			for (String amalgId : amalgPropIds) {
				if (amalgId != null && !amalgId.equals("")) {
					BasicProperty amalgBasicProp = (BasicProperty) getPersistenceService()
							.findByNamedQuery(PropertyTaxConstants.QUERY_BASICPROPERTY_BY_UPICNO,
									amalgId);
					PropertyStatusValues amalgPropStatVal = new PropertyStatusValues();
					PropertyStatus propertyStatus = (PropertyStatus) getPersistenceService().find(
							"from PropertyStatus where statusCode=?", PROPERTY_STATUS_INACTIVE);
					amalgPropStatVal.setIsActive("Y");
					amalgPropStatVal.setPropertyStatus(propertyStatus);
					amalgPropStatVal.setReferenceDate(new Date());
					amalgPropStatVal.setReferenceNo("0001");
					amalgPropStatVal.setRemarks("Property Amalgamated");
					amalgBasicProp.addPropertyStatusValues(amalgPropStatVal);
					Map<Installment, Map<String, BigDecimal>> amounts = propService
							.prepareRsnWiseDemandForPropToBeDeactivated(
									amalgBasicProp.getProperty());
					financialUtil.createVoucher(amalgBasicProp.getUpicNo(), amounts,
							VOUCH_CREATE_RSN_DEACTIVATE);
					amalgBasicProp.setActive(Boolean.FALSE);
					amalgBasicProp.setStatus(propertyStatus);
					// At final approval a new PropetyStatusValues has to
					// created with status INACTIVE and
					// set the amalgBasicProp status as INACTIVE and ISACTIVE as
					// 'N'
					amalgPropStatVal.setBasicProperty(amalgBasicProp);
				}
			}
		}
		LOGGER.debug("Exiting from setAmalgPropInactive");
	}

	private void setPropStatValForView(PropertyStatusValues propstatval) {
		LOGGER.debug("Entered into setPropStatValForView " + propstatval);
		PropertyImpl propertyImpl = propWF != null ? propWF : propertyModel;
		if (PROPERTY_MODIFY_REASON_MODIFY.equals(propstatval.getPropertyStatus().getStatusCode())) {
			// setting the court rule details
			if (PROPERTY_MODIFY_REASON_COURT_RULE.equals(propertyImpl.getPropertyDetail()
					.getPropertyMutationMaster().getCode())) {
				setCourtOrdNum(propstatval.getReferenceNo());
				setOrderDate(sdf.format(propstatval.getReferenceDate()));
				setJudgmtDetails(propstatval.getRemarks());
			}
		}
		if (propertyImpl.getPropertyDetail().getPropertyTypeMaster().getCode()
				.equalsIgnoreCase(PROPTYPE_OPEN_PLOT)) {
			setDateOfCompletion(propstatval.getExtraField1());
		}

		setBuildingPermissionNo(propstatval.getBuildingPermissionNo());
		setBuildingPermissionDate(propstatval.getBuildingPermissionDate());

		LOGGER.debug("Entered into setPropStatValForView");
	}

	private void prepareAckMsg() {
		LOGGER.debug("Entered into prepareAckMsg, ModifyRsn: " + modifyRsn);
		User approverUser = userService.getUserById(getWorkflowBean().getApproverUserId());

		if (PROPERTY_MODIFY_REASON_MODIFY.equals(modifyRsn)
				|| PROPERTY_MODIFY_REASON_ADD_OR_ALTER.equals(modifyRsn)) {
			setAckMessage(getText("property.modify.forward.success",
					new String[] { approverUser.getUsername() }));
		} else if (PROPERTY_MODIFY_REASON_BIFURCATE.equals(modifyRsn)) {
			setAckMessage(getText("property.bifur.forward.success",
					new String[] { approverUser.getUsername() }));
		} else if (PROPERTY_MODIFY_REASON_AMALG.equals(modifyRsn)) {
			setAckMessage(getText("property.amalg.forward.success",
					new String[] { approverUser.getUsername() }));
		}

		LOGGER.debug("AckMessage: " + getAckMessage() + "\nExiting from prepareAckMsg");
	}

	/**
	 * Creates voucher whenever there is change(increase/decrease) in demand.
	 */
	private void createVoucher() {

		LOGGER.debug("Entered into Create Voucher method, ModelProperty: " + propertyModel);
		Map<Installment, Map<String, BigDecimal>> amtsOld = propService
				.prepareRsnWiseDemandForOldProp(basicProp.getProperty());
		LOGGER.info("createVoucher: Old property demands===> " + amtsOld);
		Map<Installment, Map<String, BigDecimal>> amtsNew = propService
				.populateTaxesForVoucherCreation(propertyModel);
		LOGGER.info("createVoucher: New property demands===>" + amtsNew);
		Map<Installment, Map<String, BigDecimal>> amounts = new HashMap<Installment, Map<String, BigDecimal>>();
		BigDecimal amt = ZERO;
		for (Installment inst : amtsNew.keySet()) {
			Map<String, BigDecimal> amtMap = new HashMap<String, BigDecimal>();
			if (amtsOld.get(inst) != null) {
				for (String dmdRsn : amtsNew.get(inst).keySet()) {
					if (amtsOld.get(inst).get(dmdRsn) != null) {
						// subtracting the old value from the new value
						amt = amtsNew.get(inst).get(dmdRsn).subtract(amtsOld.get(inst).get(dmdRsn));
					} else {
						amt = amtsNew.get(inst).get(dmdRsn);
					}
					// if demand change amount not equal to zero then adding
					// that amount to amtMap.
					if (!amt.setScale(2, ROUND_HALF_UP).equals(BigDecimal.ZERO.setScale(2))) {
						amtMap.put(dmdRsn, amt);
					}
				}
				// to check whether the amtMap contains all the old demand
				// reasons or not
				for (String rsn : amtsOld.get(inst).keySet()) {
					// if map doesn't contain the old dmdrsn and
					// new property doesn't have the old demand reason
					if (!amtMap.containsKey(rsn) && amtsNew.get(inst).get(rsn) == null) {
						amtMap.put(rsn, amtsOld.get(inst).get(rsn).negate());
					}
				}
				if (amtMap.size() > 0) {
					amounts.put(inst, amtMap);
				}
			} else {
				amounts.put(inst, amtsNew.get(inst));
			}
		}

		if (PROPERTY_MODIFY_REASON_DATA_ENTRY.equalsIgnoreCase(
				propertyModel.getPropertyDetail().getPropertyMutationMaster().getCode())) {
			amounts = amtsNew;
		}

		// If Change in demand ( Either increment or decrement )
		LOGGER.info("createVoucher: Amounts===>" + amounts);
		if (amounts.size() > 0) {
			financialUtil.createVoucher(propertyModel.getBasicProperty().getUpicNo(), amounts,
					propertyModel.getPropertyDetail().getPropertyMutationMaster()
							.getMutationName());
		} else {
			LOGGER.info("createVoucher: No demand change : Voucher is not created");
		}

		LOGGER.debug("Exiting from createVoucher");
	}

	private String getReason(String modifyReason) {
		if (PROPERTY_MODIFY_REASON_MODIFY.equals(modifyRsn)) {
			return DOCS_MODIFY_PROPERTY;
		} else if (PROPERTY_MODIFY_REASON_BIFURCATE.equals(modifyRsn)) {
			return DOCS_BIFURCATE_PROPERTY;
		} else if (PROPERTY_MODIFY_REASON_AMALG.equals(modifyRsn)) {
			return DOCS_AMALGAMATE_PROPERTY;
		}
		return StringUtils.EMPTY;
	}

	public PropertyImpl updatePropertyForMigratedProp(PropertyImpl property, String areaOfPlot,
			String mutationCode, String propTypeId, String propUsageId, String propOccId,
			String docnumber, String nonResPlotArea, boolean isfloorDetailsRequired) {
		LOGGER.debug("Entered into modifyPropertyForMigratedProp");
		LOGGER.debug("modifyPropertyForMigratedProp: Property: " + property + ", areaOfPlot: "
				+ areaOfPlot + ", mutationCode: " + mutationCode + ",propTypeId: " + propTypeId
				+ ", propUsageId: " + propUsageId + ", propOccId: " + propOccId);

		if (areaOfPlot != null && !areaOfPlot.isEmpty()) {
			Area area = new Area();
			area.setArea(new Float(areaOfPlot));
			property.getPropertyDetail().setSitalArea(area);
		}

		if (nonResPlotArea != null && !nonResPlotArea.isEmpty()) {
			Area area = new Area();
			area.setArea(new Float(nonResPlotArea));
			property.getPropertyDetail().setNonResPlotArea(area);
		}

		property.getPropertyDetail().setFieldVerified('Y');
		property.getPropertyDetail().setProperty(property);
		PropertyTypeMaster propTypeMstr = (PropertyTypeMaster) persistenceService
				.find("from PropertyTypeMaster PTM where PTM.id = ?", Long.valueOf(propTypeId));
		String propTypeCode = propTypeMstr.getCode();
		if (propTypeMstr != null) {
			if (!(propTypeCode.equals(PROPTYPE_NON_RESD) || propTypeCode.equals(PROPTYPE_RESD)
					|| propTypeCode.equals(PROPTYPE_OPEN_PLOT))) {
				// extra_field5 contains the property type category, so setting
				// to null for other than
				// NR, R & OP i.e., for Govt. Property & Mixed
				property.getPropertyDetail().setExtra_field5(null);
			}
		}

		boolean isNofloors = (PROPTYPE_OPEN_PLOT.equals(propTypeCode)
				|| (PROPTYPE_CENTRAL_GOVT.equals(propTypeCode)
						|| PROPTYPE_STATE_GOVT.equals(propTypeCode) && isfloorDetailsRequired));

		if (propUsageId != null && isNofloors) {
			PropertyUsage usage = (PropertyUsage) persistenceService
					.find("from PropertyUsage pu where pu.id = ?", Long.valueOf(propUsageId));
			property.getPropertyDetail().setPropertyUsage(usage);
		} else {
			property.getPropertyDetail().setPropertyUsage(null);
		}

		if (propOccId != null && isNofloors) {
			PropertyOccupation occupancy = (PropertyOccupation) persistenceService
					.find("from PropertyOccupation po where po.id = ?", Long.valueOf(propOccId));
			property.getPropertyDetail().setPropertyOccupation(occupancy);
		} else {
			property.getPropertyDetail().setPropertyOccupation(null);
		}

		if (propTypeMstr.getCode().equals(PROPTYPE_OPEN_PLOT)) {
			property.getPropertyDetail().setPropertyType(VACANT_PROPERTY);
		} else {
			property.getPropertyDetail().setPropertyType(BUILT_UP_PROPERTY);
		}

		property.getPropertyDetail().setPropertyTypeMaster(propTypeMstr);
		propertyModel.getPropertyDetail().setPropertyTypeMaster(propTypeMstr);
		propertyModel.getPropertyDetail().setPropertyMutationMaster(
				property.getPropertyDetail().getPropertyMutationMaster());
		property.getPropertyDetail().setUpdatedTime(new Date());

		propService.createFloors(propertyModel, mutationCode, propUsageId, propOccId,
				isfloorDetailsRequired);

		for (Floor floor : property.getPropertyDetail().getFloorDetails()) {
			for (Floor newFloorInfo : propertyModel.getPropertyDetail().getFloorDetails()) {
				if (floor.getId().equals(newFloorInfo.getId())) {
					floor.setExtraField1(newFloorInfo.getExtraField1());
					floor.setUnitType(newFloorInfo.getUnitType());
					floor.setUnitTypeCategory(newFloorInfo.getUnitTypeCategory());
					floor.setFloorNo(newFloorInfo.getFloorNo());
					floor.setExtraField7(newFloorInfo.getExtraField7());
					floor.setExtraField2(newFloorInfo.getExtraField2());
					floor.setBuiltUpArea(newFloorInfo.getBuiltUpArea());
					floor.setPropertyUsage(newFloorInfo.getPropertyUsage());
					floor.setPropertyOccupation(newFloorInfo.getPropertyOccupation());
					floor.setWaterRate(newFloorInfo.getWaterRate());
					floor.setExtraField3(newFloorInfo.getExtraField3());
					floor.setStructureClassification(newFloorInfo.getStructureClassification());
					floor.setDepreciationMaster(newFloorInfo.getDepreciationMaster());
					floor.setRentPerMonth(newFloorInfo.getRentPerMonth());
					floor.setExtraField4(newFloorInfo.getExtraField4());
					floor.setExtraField5(newFloorInfo.getExtraField5());
					floor.setExtraField6(newFloorInfo.getExtraField6());
					floor.setManualAlv(newFloorInfo.getManualAlv());
					break;
				}
			}
		}
		property.getPropertyDetail()
				.setNo_of_floors(property.getPropertyDetail().getFloorDetails().size());
		property.setDocNumber(docnumber);
		LOGGER.debug("Exiting from createProperty");
		return property;
	}

	private void updateBasicPropForMigratedProp(String docNumber, PropertyImpl existingProp) {
		LOGGER.debug("Entered into modifyBasicPropForMigratedProp, BasicProperty: " + basicProp);
		LOGGER.debug("modifyBasicPropForMigratedProp: PropTypeId: " + propTypeId + ", PropUsageId: "
				+ propUsageId + ", PropOccId: " + propOccId + ", statusModifyRsn: " + modifyRsn
				+ ", ReasonForModify: " + reasonForModify);

		Date propCompletionDate = null;
		PropertyTypeMaster proptypeMstr = propertyTypeMasterDAO
				.getPropertyTypeMasterById(Integer.valueOf(propTypeId));
		if (!proptypeMstr.getCode().equalsIgnoreCase(PROPTYPE_OPEN_PLOT)) {
			if ((proptypeMstr.getCode().equalsIgnoreCase(PROPTYPE_STATE_GOVT)
					|| proptypeMstr.getCode().equalsIgnoreCase(PROPTYPE_CENTRAL_GOVT))
					&& isfloorDetailsRequired) {
				propCompletionDate = propService.getPropOccupatedDate(getDateOfCompletion());
			} else {
				propCompletionDate = propService.getLowestDtOfCompFloorWise(
						propertyModel.getPropertyDetail().getFloorDetails());
			}

		} else {
			propCompletionDate = propService.getPropOccupatedDate(getDateOfCompletion());
		}

		basicProp.setExtraField1(isAuthProp);
		basicProp.setPropOccupationDate(propCompletionDate);
		existingProp = updatePropertyForMigratedProp(existingProp, getAreaOfPlot(), PROP_CREATE_RSN,
				propTypeId, propUsageId, propOccId, propertyModel.getDocNumber(), null,
				isfloorDetailsRequired);
		existingProp.setBasicProperty(basicProp);
		existingProp.setExtra_field2(propertyModel.getExtra_field2());
		existingProp.setEffectiveDate(propCompletionDate);
		existingProp.getPropertyDetail().setEffective_date(propCompletionDate);
		existingProp.getPropertyDetail()
				.setExtra_field1(propertyModel.getPropertyDetail().getExtra_field1());
		existingProp.getPropertyDetail()
				.setExtra_field2(propertyModel.getPropertyDetail().getExtra_field2());
		existingProp.getPropertyDetail()
				.setExtra_field3(propertyModel.getPropertyDetail().getExtra_field3());
		existingProp.getPropertyDetail()
				.setExtra_field4(propertyModel.getPropertyDetail().getExtra_field4());
		existingProp.getPropertyDetail()
				.setExtra_field5(propertyModel.getPropertyDetail().getExtra_field5());
		existingProp.getPropertyDetail()
				.setExtra_field6(propertyModel.getPropertyDetail().getExtra_field6());
		existingProp.getPropertyDetail()
				.setManualAlv(propertyModel.getPropertyDetail().getManualAlv());
		existingProp.getPropertyDetail()
				.setOccupierName(propertyModel.getPropertyDetail().getOccupierName());

		existingProp.setDocNumber(docNumber);
		updateAddress();
		basicProp.setGisReferenceNo(parcelId);
		basicProp.getPropertyID().setNorthBoundary(northBound);
		basicProp.getPropertyID().setSouthBoundary(southBound);
		basicProp.getPropertyID().setEastBoundary(eastBound);
		basicProp.getPropertyID().setWestBoundary(westBound);

		propertyImplService.merge(existingProp);
		basicPropertyService.update(basicProp);

		LOGGER.debug("Exiting modifyBasicPropForMigratedProp");
	}

	@ValidationErrorPage(value = "new")
	public String updateData() {
		LOGGER.debug("updateData: Property modification started for Migrated Property, PropertyId: "
				+ propertyModel);
		long startTimeMillis = System.currentTimeMillis();

		if (ASSISTANT_ROLE.equals(userRole)) {

			PropertyImpl nonHistoryProperty = (PropertyImpl) basicProp.getProperty();
			processAndStoreDocumentsWithReason(basicProp, getReason(modifyRsn));
			updateBasicPropForMigratedProp(getDocNumber(), nonHistoryProperty);
			setAckMessage("Migrated Property updated Successfully in System with Index Number: ");

			long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
			LOGGER.info("updateData: Property modified successfully in system with Index Number: "
					+ basicProp.getUpicNo() + "; Time taken(ms) = " + elapsedTimeMillis);
		}

		return RESULT_ACK;
	}

	@SkipValidation
	public String modifyOrDataUpdateForm() {
		LOGGER.debug("Entered into modifyOrDataUpdateForm");
		String resultPage = "";
		if (PROPERTY_MODIFY_REASON_DATA_UPDATE.equals(modifyRsn)
				&& basicProp.getIsMigrated().equals('N')) {
			setErrorMessage(" This is not a migrated property ");
			resultPage = RESULT_ERROR;
		} else {
			resultPage = populateFormData(Boolean.FALSE);
		}

		LOGGER.debug("Exiting from modifyOrDataUpdateForm");
		return resultPage;
	}

	private void updateAddress() {
		LOGGER.debug("Entered into updateAddress");

		PropertyAddress addr = basicProp.getAddress();
		if (propertyAddr != null) {
			addr.setHouseNoBldgApt(propertyAddr.getHouseNoBldgApt());
			addr.setLandmark(propertyAddr.getLandmark());
			addr.setPinCode(propertyAddr.getPinCode());
		}
		LOGGER.debug("Exiting from updateAddress");
	}

	@SkipValidation
	@Action(value="/modifyProperty-printAck")
	public String printAck(){
		PTISCacheManagerInteface ptisCacheMgr = new PTISCacheManager();
		HttpServletRequest request = ServletActionContext.getRequest();
		String url= WebUtils.extractRequestDomainURL(request, false);
		String imagePath = url.concat(PropertyTaxConstants.IMAGES_BASE_PATH).concat(ReportUtil.fetchLogo());
		PropertyAckNoticeInfo ackBean = new PropertyAckNoticeInfo();
		Map<String, Object> reportParams = new HashMap<String, Object>();
		ackBean.setOwnerName(ptisCacheMgr.buildOwnerFullName(basicProp));
		ackBean.setOwnerAddress(ptisCacheMgr.buildAddressFromAddress(basicProp.getAddress()));
		ackBean.setApplicationDate(basicProp.getCreatedDate());
		ackBean.setApplicationNo(basicProp.getApplicationNo());
		ackBean.setApprovedDate(propWF.getState().getCreatedDate().toDate());
		Date noticeDueDate = DateUtils.add(propWF.getState().getCreatedDate().toDate(), Calendar.DAY_OF_MONTH, 15);
		ackBean.setNoticeDueDate(noticeDueDate);
		reportParams.put("logoPath", imagePath);
		reportParams.put("loggedInUsername", propertyTaxUtil.getLoggedInUser(getSession()).getName());
		ReportRequest reportInput = new ReportRequest(MODIFY_ACK_TEMPLATE,ackBean, reportParams);
		reportInput.setReportFormat(FileFormat.PDF);
		ReportOutput reportOutput = reportService.createReport(reportInput);  
		reportId = ReportViewerUtil.addReportToSession(reportOutput,getSession());
		return PRINTACK;
	}
	
	public BasicProperty getBasicProp() {
		return basicProp;
	}

	public void setBasicProp(BasicProperty basicProp) {
		this.basicProp = basicProp;
	}

	public boolean isChkIsTaxExempted() {
		return chkIsTaxExempted;
	}

	public void setChkIsTaxExempted(boolean chkIsTaxExempted) {
		this.chkIsTaxExempted = chkIsTaxExempted;
	}

	public String getTaxExemptReason() {
		return taxExemptReason;
	}

	public void setTaxExemptReason(String taxExemptReason) {
		this.taxExemptReason = taxExemptReason;
	}

	public String getModifyRsn() {
		return modifyRsn;
	}

	public void setModifyRsn(String modifyRsn) {
		this.modifyRsn = modifyRsn;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPropAddress() {
		return propAddress;
	}

	public void setPropAddress(String propAddress) {
		this.propAddress = propAddress;
	}

	public String getAreaOfPlot() {
		return areaOfPlot;
	}

	public void setAreaOfPlot(String areaOfPlot) {
		this.areaOfPlot = areaOfPlot;
	}

	public Map<String, String> getWaterMeterMap() {
		return waterMeterMap;
	}

	public void setWaterMeterMap(Map<String, String> waterMeterMap) {
		this.waterMeterMap = waterMeterMap;
	}

	public boolean isGeneralTax() {
		return generalTax;
	}

	public void setGeneralTax(boolean generalTax) {
		this.generalTax = generalTax;
	}

	public boolean isSewerageTax() {
		return sewerageTax;
	}

	public void setSewerageTax(boolean sewerageTax) {
		this.sewerageTax = sewerageTax;
	}

	public boolean isLightingTax() {
		return lightingTax;
	}

	public void setLightingTax(boolean lightingTax) {
		this.lightingTax = lightingTax;
	}

	public boolean isFireServTax() {
		return fireServTax;
	}

	public void setFireServTax(boolean fireServTax) {
		this.fireServTax = fireServTax;
	}

	public boolean isBigResdBldgTax() {
		return bigResdBldgTax;
	}

	public void setBigResdBldgTax(boolean bigResdBldgTax) {
		this.bigResdBldgTax = bigResdBldgTax;
	}

	public boolean isEducationCess() {
		return educationCess;
	}

	public void setEducationCess(boolean educationCess) {
		this.educationCess = educationCess;
	}

	public boolean isEmpGuaCess() {
		return empGuaCess;
	}

	public void setEmpGuaCess(boolean empGuaCess) {
		this.empGuaCess = empGuaCess;
	}

	public TreeMap<Integer, String> getFloorNoMap() {
		return floorNoMap;
	}

	public void setFloorNoMap(TreeMap<Integer, String> floorNoMap) {
		this.floorNoMap = floorNoMap;
	}

	public String getReasonForModify() {
		return reasonForModify;
	}

	public void setReasonForModify(String reasonForModify) {
		this.reasonForModify = reasonForModify;
	}

	public String getDateOfCompletion() {
		return dateOfCompletion;
	}

	public void setDateOfCompletion(String dateOfCompletion) {
		this.dateOfCompletion = dateOfCompletion;
	}

	public Map<String, String> getModifyReasonMap() {
		return modifyReasonMap;
	}

	public void setModifyReasonMap(Map<String, String> modifyReasonMap) {
		this.modifyReasonMap = modifyReasonMap;
	}

	public String[] getAmalgPropIds() {
		return amalgPropIds;
	}

	public void setAmalgPropIds(String[] amalgPropIds) {
		this.amalgPropIds = amalgPropIds;
	}

	public PropertyService getPropService() {
		return propService;
	}

	public void setPropService(PropertyService propService) {
		this.propService = propService;
	}

	public String getCourtOrdNum() {
		return courtOrdNum;
	}

	public void setCourtOrdNum(String courtOrdNum) {
		this.courtOrdNum = courtOrdNum;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getJudgmtDetails() {
		return judgmtDetails;
	}

	public void setJudgmtDetails(String judgmtDetails) {
		this.judgmtDetails = judgmtDetails;
	}

	public PropertyImpl getOldProperty() {
		return oldProperty;
	}

	public void setOldProperty(PropertyImpl oldProperty) {
		this.oldProperty = oldProperty;
	}

	@Override
	public PropertyImpl getProperty() {
		return propertyModel;
	}

	@Override
	public void setProperty(PropertyImpl property) {
		this.propertyModel = property;
	}

	public String getIsAuthProp() {
		return isAuthProp;
	}

	public void setIsAuthProp(String isAuthProp) {
		this.isAuthProp = isAuthProp;
	}

	public void setPropertyImplService(PersistenceService<Property, Long> propertyImplService) {
		this.propertyImplService = propertyImplService;
	}

	public String getAmalgStatus() {
		return amalgStatus;
	}

	public void setAmalgStatus(String amalgStatus) {
		this.amalgStatus = amalgStatus;
	}

	public BasicProperty getAmalgPropBasicProp() {
		return amalgPropBasicProp;
	}

	public void setAmalgPropBasicProp(BasicProperty amalgPropBasicProp) {
		this.amalgPropBasicProp = amalgPropBasicProp;
	}

	public String getOldpropId() {
		return oldpropId;
	}

	public void setOldpropId(String oldpropId) {
		this.oldpropId = oldpropId;
	}

	public String getOldOwnerName() {
		return oldOwnerName;
	}

	public void setOldOwnerName(String oldOwnerName) {
		this.oldOwnerName = oldOwnerName;
	}

	public String getOldPropAddress() {
		return oldPropAddress;
	}

	public void setOldPropAddress(String oldPropAddress) {
		this.oldPropAddress = oldPropAddress;
	}

	public Map<String, String> getAmenitiesMap() {
		return amenitiesMap;
	}

	public void setAmenitiesMap(Map<String, String> amenitiesMap) {
		this.amenitiesMap = amenitiesMap;
	}

	public String getPropTypeId() {
		return propTypeId;
	}

	public void setPropTypeId(String propTypeId) {
		this.propTypeId = propTypeId;
	}

	public String getPropUsageId() {
		return propUsageId;
	}

	public void setPropUsageId(String propUsageId) {
		this.propUsageId = propUsageId;
	}

	public String getPropOccId() {
		return propOccId;
	}

	public void setPropOccId(String propOccId) {
		this.propOccId = propOccId;
	}

	public String getCorrsAddress() {
		return corrsAddress;
	}

	public void setCorrsAddress(String corrsAddress) {
		this.corrsAddress = corrsAddress;
	}

	public String[] getFloorNoStr() {
		return floorNoStr;
	}

	public void setFloorNoStr(String[] floorNoStr) {
		this.floorNoStr = floorNoStr;
	}

	public String getAckMessage() {
		return ackMessage;
	}

	public void setAckMessage(String ackMessage) {
		this.ackMessage = ackMessage;
	}

	public Map<String, String> getPropTypeCategoryMap() {
		return propTypeCategoryMap;
	}

	public void setPropTypeCategoryMap(Map<String, String> propTypeCategoryMap) {
		this.propTypeCategoryMap = propTypeCategoryMap;
	}

	public String getPropTypeCategoryId() {
		return propTypeCategoryId;
	}

	public void setPropTypeCategoryId(String propTypeCategoryId) {
		this.propTypeCategoryId = propTypeCategoryId;
	}

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public Category getPropertyCategory() {
		return propertyCategory;
	}

	public void setPropertyCategory(Category propertyCategory) {
		this.propertyCategory = propertyCategory;
	}

	public boolean isIsfloorDetailsRequired() {
		return isfloorDetailsRequired;
	}

	public void setIsfloorDetailsRequired(boolean isfloorDetailsRequired) {
		this.isfloorDetailsRequired = isfloorDetailsRequired;
	}

	public boolean isUpdateData() {
		return updateData;
	}

	public void setUpdateData(boolean updateData) {
		this.updateData = updateData;
	}

	public PersistenceService<Floor, Long> getFloorService() {
		return floorService;
	}

	public void setFloorService(PersistenceService<Floor, Long> floorService) {
		this.floorService = floorService;
	}

	public PropertyAddress getPropertyAddr() {
		return propertyAddr;
	}

	public void setPropertyAddr(PropertyAddress propertyAddr) {
		this.propertyAddr = propertyAddr;
	}

	public String getNorthBound() {
		return northBound;
	}

	public void setNorthBound(String northBound) {
		this.northBound = northBound;
	}

	public String getSouthBound() {
		return southBound;
	}

	public void setSouthBound(String southBound) {
		this.southBound = southBound;
	}

	public String getEastBound() {
		return eastBound;
	}

	public void setEastBound(String eastBound) {
		this.eastBound = eastBound;
	}

	public String getWestBound() {
		return westBound;
	}

	public void setWestBound(String westBound) {
		this.westBound = westBound;
	}

	public String getParcelId() {
		return parcelId;
	}

	public void setParcelId(String parcelId) {
		this.parcelId = parcelId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public PropertyTaxNumberGenerator getPropertyTaxNumberGenerator() {
		return propertyTaxNumberGenerator;
	}

	public void setPropertyTaxNumberGenerator(
			PropertyTaxNumberGenerator propertyTaxNumberGenerator) {
		this.propertyTaxNumberGenerator = propertyTaxNumberGenerator;
	}

	public List<PropertyOwnerInfo> getPropertyOwners() {
		return propertyOwners;
	}

	public void setPropertyOwners(List<PropertyOwnerInfo> propertyOwners) {
		this.propertyOwners = propertyOwners;
	}

	public WorkflowDetails getWorkflowAction() {
		return workflowAction;
	}

	public void setWorkflowAction(WorkflowDetails workflowAction) {
		this.workflowAction = workflowAction;
	}

	public String getPartNo() {
		return partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}

	public String getModificationType() {
		return modificationType;
	}

	public void setModificationType(String modificationType) {
		this.modificationType = modificationType;
	}

	public boolean getIsTenantFloorPresent() {
		return this.isTenantFloorPresent;
	}

	public void setIsTenantFloorPresent(boolean isTenantFloorPresent) {
		this.isTenantFloorPresent = isTenantFloorPresent;
	}

	public String getMode() {
		return this.mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Integer getBuildingPermissionNo() {
		return buildingPermissionNo;
	}

	public void setBuildingPermissionNo(Integer buildingPermissionNo) {
		this.buildingPermissionNo = buildingPermissionNo;
	}

	public Date getBuildingPermissionDate() {
		return buildingPermissionDate;
	}

	public void setBuildingPermissionDate(Date buildingPermissionDate) {
		this.buildingPermissionDate = buildingPermissionDate;
	}

	public Long getFloorTypeId() {
		return floorTypeId;
	}

	public void setFloorTypeId(Long floorTypeId) {
		this.floorTypeId = floorTypeId;
	}

	public Long getRoofTypeId() {
		return roofTypeId;
	}

	public void setRoofTypeId(Long roofTypeId) {
		this.roofTypeId = roofTypeId;
	}

	public Long getWallTypeId() {
		return wallTypeId;
	}

	public void setWallTypeId(Long wallTypeId) {
		this.wallTypeId = wallTypeId;
	}

	public Long getWoodTypeId() {
		return woodTypeId;
	}

	public void setWoodTypeId(Long woodTypeId) {
		this.woodTypeId = woodTypeId;
	}

	public void setBasicPropertyDAO(BasicPropertyDAO basicPropertyDAO) {
		this.basicPropertyDAO = basicPropertyDAO;
	}

	public void setPropertyTypeMasterDAO(PropertyTypeMasterDAO propertyTypeMasterDAO) {
		this.propertyTypeMasterDAO = propertyTypeMasterDAO;
	}

	public void setPropertyStatusValuesDAO(PropertyStatusValuesDAO propertyStatusValuesDAO) {
		this.propertyStatusValuesDAO = propertyStatusValuesDAO;
	}

	public void setPtDemandDAO(PtDemandDao ptDemandDAO) {
		this.ptDemandDAO = ptDemandDAO;
	}

	public void setbasicPropertyService(PropertyPersistenceService basicPropertyService) {
		this.basicPropertyService = basicPropertyService;
	}

	public void setSecurityUtils(SecurityUtils securityUtils) {
		this.securityUtils = securityUtils;
	}

	public void setAssignmentService(AssignmentService assignmentService) {
		this.assignmentService = assignmentService;
	}

	public Long getApartmentId() {
		return apartmentId;
	}

	public void setApartmentId(Long apartmentId) {
		this.apartmentId = apartmentId;
	}

	public List<DocumentType> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(List<DocumentType> documentTypes) {
		this.documentTypes = documentTypes;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
}
