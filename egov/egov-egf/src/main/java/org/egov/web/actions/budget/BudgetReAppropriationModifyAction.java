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
package org.egov.web.actions.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.script.ScriptContext;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.eis.service.EisCommonService;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.script.entity.Script;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.service.WorkflowService;
import org.egov.infstr.config.dao.AppConfigValuesDAO;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.budget.BudgetGroup;
import org.egov.model.budget.BudgetReAppropriation;
import org.egov.model.budget.BudgetReAppropriationMisc;
import org.egov.pims.commons.Position;
import org.egov.pims.service.EisUtilService;
import org.egov.services.budget.BudgetDetailService;
import org.egov.services.budget.BudgetReAppropriationService;
import org.egov.services.budget.BudgetService;
import org.egov.utils.BudgetDetailConfig;
import org.egov.utils.BudgetDetailHelper;
import org.egov.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
@Transactional(readOnly=true)
@ParentPackage("egov")
public class BudgetReAppropriationModifyAction extends BaseFormAction{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER=Logger.getLogger(BudgetReAppropriationModifyAction.class);
	protected BudgetDetailConfig budgetDetailConfig;
	BudgetDetail budgetDetail;
	protected Budget budget;
	protected List<String> headerFields = new ArrayList<String>();
	protected List<String> gridFields = new ArrayList<String>();
	protected List<String> mandatoryFields = new ArrayList<String>();
	BudgetDetailHelper budgetDetailHelper;
	BudgetDetailService budgetDetailService;
	BudgetReAppropriationService budgetReAppropriationService;
	WorkflowService<BudgetReAppropriation> budgetReAppropriationWorkflowService;
	CFinancialYear financialYear;
	BudgetService budgetService;
	String isBeRe = Constants.BE;
	List<BudgetReAppropriationView> savedBudgetReAppropriationList = new ArrayList<BudgetReAppropriationView>();
	@Autowired AppConfigValuesDAO appConfigValuesDAO;
	String message = "";
	boolean deleted = false;
	BudgetReAppropriation budgetReAppropriation;
	EisCommonService eisCommonService;
	Long miscId;
	WorkflowService<BudgetReAppropriationMisc> miscWorkflowService;
	private List<Action> validActions = new ArrayList<Action>();
	private String comment = "";
	private BudgetReAppropriationMisc workFlowItem;
	private String actionName = "";
	private boolean enableApprovedAmount = false;
	private boolean enableOriginalAmount = false;
	protected EisUtilService eisService;
	private ScriptService scriptService;
	
	public void setMiscWorkflowService(WorkflowService<BudgetReAppropriationMisc> miscWorkflowService) {
		this.miscWorkflowService = miscWorkflowService;
	}

	public Long getMiscId() {
		return miscId;
	}

	public void setMiscId(Long miscId) {
		this.miscId = miscId;
	}

	public void setEisCommonService(EisCommonService eisCommonService) {
		this.eisCommonService = eisCommonService;
	}

	public BudgetReAppropriation getBudgetReAppropriation() {
		return budgetReAppropriation;
	}

	public void setBudgetReAppropriation(BudgetReAppropriation budgetReAppropriation) {
		this.budgetReAppropriation = budgetReAppropriation;
	}

	public String getMessage() {
		return message;
	}

	public void setBudgetDetailService(BudgetDetailService budgetDetailService) {
		this.budgetDetailService = budgetDetailService;
	}

	public List<BudgetReAppropriationView> getSavedBudgetReAppropriationList() {
		return savedBudgetReAppropriationList;
	}
	
	public String getIsBeRe() {
		return isBeRe;
	}
	public void setBudgetReAppropriationService(BudgetReAppropriationService budgetReAppropriationService) {
		this.budgetReAppropriationService = budgetReAppropriationService;
	}
	public void setIsBeRe(String beRe) {
		this.isBeRe = beRe;
	}

	public void setBudgetService(BudgetService budgetService) {
		this.budgetService = budgetService;
	}
	
	public void setBudgetDetailHelper(BudgetDetailHelper budgetDetailHelper) {
		this.budgetDetailHelper = budgetDetailHelper;
	}

	public void setFinancialYear(CFinancialYear financialYear) {
		this.financialYear = financialYear;
	}

	public BudgetDetail getBudgetDetail() {
		return budgetDetail;
	}

	public void setBudgetDetail(BudgetDetail budgetDetail) {
		this.budgetDetail = budgetDetail;
	}

	
	public CFinancialYear getFinancialYear() {
		return financialYear;
	}

	public Budget getBudget() {
		return budget;
	}

	public List<String> getHeaderFields() {
		return headerFields;
	}

	public List<String> getGridFields() {
		return gridFields;
	}

	public List<String> getMandatoryFields() {
		return mandatoryFields;
	}
	public BudgetReAppropriationModifyAction(BudgetDetailConfig budgetDetailConfig){
		this.budgetDetailConfig = budgetDetailConfig;
		headerFields = budgetDetailConfig.getHeaderFields();
		gridFields = budgetDetailConfig.getGridFields();
		mandatoryFields = budgetDetailConfig.getMandatoryFields();
		addRelatedEntity("budgetGroup", BudgetGroup.class);
		if(shouldShowField(Constants.FUNCTIONARY))
			addRelatedEntity(Constants.FUNCTIONARY, Functionary.class);
		if(shouldShowField(Constants.FUNCTION))
			addRelatedEntity(Constants.FUNCTION, CFunction.class);
		if(shouldShowField(Constants.SCHEME))
			addRelatedEntity(Constants.SCHEME, Scheme.class);
		if(shouldShowField(Constants.SUB_SCHEME))
			addRelatedEntity(Constants.SUB_SCHEME, SubScheme.class);
		if(shouldShowField(Constants.FUND))
			addRelatedEntity(Constants.FUND, Fund.class);
		if(shouldShowField(Constants.EXECUTING_DEPARTMENT))
			addRelatedEntity(Constants.EXECUTING_DEPARTMENT, Department.class);
		if(shouldShowField(Constants.BOUNDARY))
			addRelatedEntity(Constants.BOUNDARY, Boundary.class);
	}
	
	protected void setupDropdownsInHeader() {
		EgovMasterDataCaching masterCache = EgovMasterDataCaching.getInstance();
		setupDropdownDataExcluding(Constants.SUB_SCHEME);
		dropdownData.put("finYearList", getPersistenceService().findAllBy("from CFinancialYear where isActive=1 order by finYearRange desc "));
		dropdownData.put("budgetGroupList", masterCache.get("egf-budgetGroup"));
		if(shouldShowField(Constants.SUB_SCHEME))
			dropdownData.put("subSchemeList", Collections.EMPTY_LIST);
		if(shouldShowField(Constants.FUNCTIONARY))
			dropdownData.put("functionaryList", masterCache.get("egi-functionary"));
		if(shouldShowField(Constants.FUNCTION))
			dropdownData.put("functionList",  masterCache.get("egi-function"));
		if(shouldShowField(Constants.SCHEME))
			dropdownData.put("schemeList", persistenceService.findAllBy("from Scheme where isActive=1 order by name"));
		if(shouldShowField(Constants.EXECUTING_DEPARTMENT)) 
			dropdownData.put("executingDepartmentList", masterCache.get("egi-department"));
		if(shouldShowField(Constants.FUND))
			dropdownData.put("fundList", persistenceService.findAllBy("from Fund where isNotLeaf=0 and isActive=1 order by name"));
		if(shouldShowField(Constants.BOUNDARY))
			dropdownData.put("boundaryList", persistenceService.findAllBy("from Boundary order by name"));
	}
	
	public final boolean shouldShowField(String fieldName) {
		if(headerFields.isEmpty() && gridFields.isEmpty())
			return true;
		return budgetDetailConfig.shouldShowField(headerFields,fieldName) || budgetDetailConfig.shouldShowField(gridFields,fieldName);
	}
	
	public boolean shouldShowHeaderField(String fieldName) {
		return budgetDetailConfig.shouldShowField(headerFields,fieldName);
	}

	public boolean shouldShowGridField(String fieldName) {
		return budgetDetailConfig.shouldShowField(gridFields,fieldName);
	}

	public String execute() throws Exception {
		return "modify";
	}

	@Override
	public void prepare() {
		super.prepare();
		headerFields = budgetDetailConfig.getHeaderFields();
		gridFields = budgetDetailConfig.getGridFields();
		mandatoryFields = budgetDetailConfig.getMandatoryFields();
		setupDropdownsInHeader();
		EgovMasterDataCaching masterCache = EgovMasterDataCaching.getInstance();
		addDropdownData("departmentList", masterCache.get("egi-department"));
		addDropdownData("designationList", Collections.EMPTY_LIST);
		addDropdownData("userList", Collections.EMPTY_LIST);
	}
	public Object getModel() {
		return budgetDetail;
	}
	@Transactional
	public String update(){
		for (BudgetReAppropriationView entry : savedBudgetReAppropriationList) {
			BudgetReAppropriation reApp = budgetReAppropriationService.findBySequenceNumberAndBudgetDetail(entry.getSequenceNumber(), entry.getBudgetDetail().getId());
			if("Addition".equalsIgnoreCase(entry.changeRequestType)){
				reApp.setOriginalAdditionAmount(entry.getDeltaAmount());
				reApp.setAdditionAmount(entry.getApprovedDeltaAmount());
			}
			else{
				reApp.setOriginalDeductionAmount(entry.getDeltaAmount());
				reApp.setDeductionAmount(entry.getApprovedDeltaAmount());
			}
			budgetReAppropriationService.persist(reApp);
		}
		if(!savedBudgetReAppropriationList.isEmpty())
			addActionMessage(getText("reapp.modified.successfully"));
		savedBudgetReAppropriationList = Collections.EMPTY_LIST;
		return "modify";
	}
	
	public String list(){
		savedBudgetReAppropriationList.clear();
		
		budgetDetail =budgetReAppropriationService.setRelatedValues(budgetDetail);
		List<BudgetReAppropriation> results = budgetReAppropriationService.getNonApprovedReAppByUser(EgovThreadLocals.getUserId(),budgetDetail,financialYear);
		for (BudgetReAppropriation row : results) {
			BudgetReAppropriationView budgetReAppropriationView = new BudgetReAppropriationView();
			budgetReAppropriationView.setBudgetDetail(row.getBudgetDetail());
			BigDecimal approvedReAppropriationsTotal = row.getBudgetDetail().getApprovedReAppropriationsTotal();
			budgetReAppropriationView.setAppropriatedAmount(approvedReAppropriationsTotal==null?BigDecimal.ZERO.setScale(2):approvedReAppropriationsTotal.setScale(2));
			BigDecimal actuals = budgetDetailHelper.getTotalActualsFor(budgetDetailHelper.constructParamMap(getValueStack(),budgetReAppropriationView.getBudgetDetail()), new Date());
			budgetReAppropriationView.setActuals(actuals.setScale(2));
			budgetReAppropriationView.setApprovedAmount(budgetDetail.getApprovedAmount().setScale(2));
			budgetReAppropriationView.setAvailableAmount(budgetReAppropriationView.getApprovedAmount()
					.add(budgetReAppropriationView.getAppropriatedAmount()).subtract(budgetReAppropriationView.getActuals()).setScale(2));
			budgetReAppropriationView.setSequenceNumber(row.getReAppropriationMisc().getSequenceNumber());
			if(row.getOriginalAdditionAmount() == null || BigDecimal.ZERO.equals(row.getOriginalAdditionAmount())){
				budgetReAppropriationView.setChangeRequestType("Deduction");
				budgetReAppropriationView.setDeltaAmount(row.getOriginalDeductionAmount());
				if(row.getDeductionAmount() == null || BigDecimal.ZERO.equals(row.getDeductionAmount()))
					budgetReAppropriationView.setApprovedDeltaAmount(row.getOriginalDeductionAmount()==null?BigDecimal.ZERO:row.getOriginalDeductionAmount());
				else
					budgetReAppropriationView.setApprovedDeltaAmount(row.getDeductionAmount());
			}
			else{
				budgetReAppropriationView.setChangeRequestType("Addition");
				budgetReAppropriationView.setDeltaAmount(row.getOriginalAdditionAmount()==null?BigDecimal.ZERO.setScale(2):row.getOriginalAdditionAmount().setScale(2));
				if(row.getAdditionAmount() == null || BigDecimal.ZERO.equals(row.getAdditionAmount()))
					budgetReAppropriationView.setApprovedDeltaAmount(row.getOriginalAdditionAmount()==null?BigDecimal.ZERO.setScale(2):row.getOriginalAdditionAmount().setScale(2));
				else
					budgetReAppropriationView.setApprovedDeltaAmount(row.getAdditionAmount().setScale(2));
			}
			savedBudgetReAppropriationList.add(budgetReAppropriationView);
		}
		if(savedBudgetReAppropriationList.isEmpty())
			message = getText("no.data.found");
		return "modify";
	}
	
	protected ValueStack getValueStack() {
		return ActionContext.getContext().getValueStack();
	}
	
	public String ajaxDeleteBudgetReAppropriation(){
		Long budgetDetailId = Long.valueOf(parameters.get("id")[0]);
		String sequenceNumber = String.valueOf(parameters.get("sequenceNumber")[0]);
		BudgetReAppropriation reApp = budgetReAppropriationService.findBySequenceNumberAndBudgetDetail(sequenceNumber, budgetDetailId);
		if(reApp != null){
			budgetReAppropriationService.delete(reApp);
			deleted = true;
		}
		return "deleted";
	}

	public boolean isDeleted() {
		return deleted;
	}
	@Action(value="/budget/budgetReAppropriationModify-approvalList")
	public String approvalList(){
		
		if(budgetReAppropriation == null)
			return "approvalList";
		miscId = budgetReAppropriation.getReAppropriationMisc().getId();
		BudgetReAppropriationMisc  misc = (BudgetReAppropriationMisc) persistenceService.find("from BudgetReAppropriationMisc where id=?", budgetReAppropriation.getReAppropriationMisc().getId());
		if(!validateOwner(misc.getState()))
		{
			throw new EGOVRuntimeException("Invalid Access");
		}       
		workFlowItem = misc;
		setEnablingAmounts(misc);
		comment = misc.getCurrentState().getComments();
		//This fix is for Phoenix Migration.setValidActions(miscWorkflowService.getValidActions(misc));
		List<BudgetReAppropriation> nonApprovedReAppropriations = misc.getNonApprovedReAppropriations();
		for (BudgetReAppropriation row : nonApprovedReAppropriations) {
			BudgetReAppropriationView budgetReAppropriationView = new BudgetReAppropriationView();
			budgetReAppropriationView.setId(row.getId());
			budgetReAppropriationView.setBudgetDetail(row.getBudgetDetail());
			String actuals = budgetDetailHelper.getActualsFor(budgetDetailHelper.constructParamMap(getValueStack(),budgetReAppropriationView.getBudgetDetail()), new Date());
			budgetReAppropriationView.setActuals(new BigDecimal(actuals));
			budgetReAppropriationView.setApprovedAmount(row.getBudgetDetail().getApprovedAmount());
			budgetReAppropriationView.setAddedReleased(row.getBudgetDetail().getApprovedReAppropriationsTotal()==null?BigDecimal.ZERO:row.getBudgetDetail().getApprovedReAppropriationsTotal());
			budgetReAppropriationView.setAvailableAmount(budgetReAppropriationView.getApprovedAmount().add(budgetReAppropriationView.getAddedReleased())
					.subtract(budgetReAppropriationView.getActuals()));
			budgetReAppropriationView.setSequenceNumber(row.getReAppropriationMisc().getSequenceNumber());
			if(row.getOriginalAdditionAmount() == null || BigDecimal.ZERO.equals(row.getOriginalAdditionAmount())){
				budgetReAppropriationView.setChangeRequestType("Deduction");
				budgetReAppropriationView.setDeltaAmount(row.getOriginalDeductionAmount());
				if(row.getDeductionAmount() == null || BigDecimal.ZERO.equals(row.getDeductionAmount()))
					budgetReAppropriationView.setApprovedDeltaAmount(row.getOriginalDeductionAmount()==null?BigDecimal.ZERO:row.getOriginalDeductionAmount());
				else
					budgetReAppropriationView.setApprovedDeltaAmount(row.getDeductionAmount());
			}
			else{
				budgetReAppropriationView.setChangeRequestType("Addition");
				budgetReAppropriationView.setDeltaAmount(row.getOriginalAdditionAmount()==null?BigDecimal.ZERO.setScale(2):row.getOriginalAdditionAmount().setScale(2));
				if(row.getAdditionAmount() == null || BigDecimal.ZERO.equals(row.getAdditionAmount()))
					budgetReAppropriationView.setApprovedDeltaAmount(row.getOriginalAdditionAmount()==null?BigDecimal.ZERO.setScale(2):row.getOriginalAdditionAmount().setScale(2));
				else
					budgetReAppropriationView.setApprovedDeltaAmount(row.getAdditionAmount().setScale(2));
			}
			savedBudgetReAppropriationList.add(budgetReAppropriationView);
			budgetDetail = row.getBudgetDetail();
			financialYear = row.getBudgetDetail().getBudget().getFinancialYear();
		}
		return "approvalList";
	}
	public boolean enableApprovedAmount(){
		return enableApprovedAmount;
	}
	public boolean enableOriginalAmount(){
		return enableOriginalAmount;
	}

	public String forward() {
		actionName = actionName.replace(",", "").replace(" ", "").trim();
		BudgetReAppropriationMisc misc = null;
		for (BudgetReAppropriationView detail : savedBudgetReAppropriationList) {
			BudgetReAppropriation reApp = budgetReAppropriationService.findBySequenceNumberAndBudgetDetail(detail.getSequenceNumber(), detail.getBudgetDetail().getId());
			misc = approveReApp(reApp.getReAppropriationMisc(),reApp);
		}
		setEnablingAmounts(misc);
		update();
		return "success";
	}
	
	public String performAction() {
		if(miscId != null){
			BudgetReAppropriationMisc  misc = (BudgetReAppropriationMisc) persistenceService.find("from BudgetReAppropriationMisc where id=?", miscId);	
			if(misc != null){
				List<BudgetReAppropriation> reApps = budgetReAppropriationService.findAllBy("from BudgetReAppropriation where reAppropriationMisc.id=?", misc.getId());
				actionName = actionName.replace(",", "").replace(" ", "").trim();
				setEnablingAmounts(misc);
				if(actionName!=null && "forward".equalsIgnoreCase(actionName.trim()) || actionName.contains("approv")|| actionName.contains("eject") || actionName.contains("ancel")){  //if mode is approve move the object
					for (BudgetReAppropriation detail : reApps) {
						setAmounts(detail, getReAppById(detail.getId(),savedBudgetReAppropriationList));
					}
					misc = approve(misc,reApps);
				}else{ 
					for (BudgetReAppropriation detail : reApps) {
						setAmounts(detail, getReAppById(detail.getId(),savedBudgetReAppropriationList));
						budgetReAppropriationService.persist(detail);
					}
					addActionMessage(getText("budget.reapp.saved"));  
				}
			}
			if("END".equalsIgnoreCase(misc.getCurrentState().getValue())){
				for (BudgetReAppropriation entry : misc.getBudgetReAppropriations()) {
					budgetReAppropriationService.updatePlanningBudget(entry);
				}
			}
		}
		savedBudgetReAppropriationList = Collections.EMPTY_LIST;
		return "success";
	}

	private void setAmounts(BudgetReAppropriation detail,BudgetReAppropriationView reAppById) {
		if(reAppById!=null){
			if(enableOriginalAmount){
				if("Addition".equalsIgnoreCase(reAppById.getChangeRequestType()))
					detail.setOriginalAdditionAmount(reAppById.getDeltaAmount());
				else
					detail.setOriginalDeductionAmount(reAppById.getDeltaAmount());
			}
			if(enableApprovedAmount){
				if("Addition".equalsIgnoreCase(reAppById.getChangeRequestType()))
					detail.setAdditionAmount(reAppById.getApprovedDeltaAmount());
				else
					detail.setDeductionAmount(reAppById.getApprovedDeltaAmount());
			}
		}
	}
	
	private BudgetReAppropriationView getReAppById(Long id,List<BudgetReAppropriationView> savedBudgetReAppropriationList2) {
		for (BudgetReAppropriationView budgetReAppropriationView : savedBudgetReAppropriationList2) {
			if(id!=null && id.equals(budgetReAppropriationView.getId()))
				return budgetReAppropriationView;
		}
		return null;
	}

	private void setEnablingAmounts(BudgetReAppropriationMisc misc){
		Script script = (Script) persistenceService.findAllByNamedQuery(Script.BY_NAME, "BudgetDetail.enable.amounts").get(0);
		ScriptContext scriptContext = ScriptService.createContext("wfItem",misc,"persistenceService",budgetService);
		String value = (String) scriptService.executeScript(script,scriptContext );
		if("approved".equalsIgnoreCase(value))
			enableApprovedAmount = true;
		else if("original".equalsIgnoreCase(value))
			enableOriginalAmount = true;
	}

	private BudgetReAppropriationMisc approve(BudgetReAppropriationMisc misc, List<BudgetReAppropriation> reApps) {
		Integer userId = fetchUserId();
		for (BudgetReAppropriation detail : reApps) {
			budgetReAppropriationWorkflowService.transition(actionName+"|"+userId, detail, comment);
		}
		misc = transformAndSetActionMessage(misc, userId);  
		return misc;
	}
		private BudgetReAppropriationMisc approveReApp(BudgetReAppropriationMisc misc, BudgetReAppropriation reApp) {
		Integer userId = fetchUserId();
		budgetReAppropriationWorkflowService.transition(actionName+"|"+userId, reApp, comment);
		misc = transformAndSetActionMessage(misc, userId);  
		return misc;
	}

	private BudgetReAppropriationMisc transformAndSetActionMessage(BudgetReAppropriationMisc misc, Integer userId) {
		misc = budgetReAppropriationService.performActionOnMisc(actionName+"|"+userId,misc,comment);
		Position owner = misc.getState().getOwnerPosition();
		if(actionName.contains("approv")){
			if("END".equalsIgnoreCase(misc.getCurrentState().getValue())){
				addActionMessage(getText("budget.reapp.approved.end"));
			}else{
				addActionMessage(getText("budget.reapp.approved")+budgetService.getEmployeeNameAndDesignationForPosition(owner));
			}
		}else if(actionName.contains("eject")) {
			addActionMessage(getText("budget.reapp.rejected")+budgetService.getEmployeeNameAndDesignationForPosition(owner));
		}
		else if(actionName.contains("ancel")) {
			addActionMessage(getText("budget.reapp.cancelled")+" "+budgetService.getEmployeeNameAndDesignationForPosition(owner));
		}
		else
		{
			addActionMessage(getText("budget.reapp.forwarded")+budgetService.getEmployeeNameAndDesignationForPosition(owner));
		}
		return misc;
	}

	private Integer fetchUserId() {
		Integer userId = null;
		if (null != parameters.get("approverUserId") && Integer.valueOf(parameters.get("approverUserId")[0])!=-1 ) {
			userId = Integer.valueOf(parameters.get("approverUserId")[0]);
		}else {
			userId = EgovThreadLocals.getUserId().intValue();
		}
		return userId;
	}
	public void setValidActions(List<Action> validActions) {
		this.validActions = validActions;
	}

	public List<Action> getValidActions() {
		return validActions;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setWorkFlowItem(BudgetReAppropriationMisc workFlowItem) {
		this.workFlowItem = workFlowItem;
	}

	public BudgetReAppropriationMisc getWorkFlowItem() {
		return workFlowItem;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setEnableApprovedAmount(boolean enableApprovedAmount) {
		this.enableApprovedAmount = enableApprovedAmount;
	}

	public void setEnableOriginalAmount(boolean enableOriginalAmount) {
		this.enableOriginalAmount = enableOriginalAmount;
	}

	public String capitalize(String value){
		if (value == null || value.length() == 0) return value;
		return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
	}
	protected Boolean validateOwner(State state)
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("validating owner for user "+EgovThreadLocals.getUserId());
		List<Position> positionsForUser=null;
		positionsForUser = eisService.getPositionsForUser(EgovThreadLocals.getUserId(), new Date());
		if(positionsForUser.contains(state.getOwnerPosition()))      
		{
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Valid Owner :return true");
			return true;
		}else
		{
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Invalid  Owner :return false");
			return false;
		}
	}

	public void setEisService(EisUtilService eisService) {
		this.eisService = eisService;
	}
	public void setBudgetReAppropriationWorkflowService(WorkflowService<BudgetReAppropriation> budgetReAppropriationWorkflowService) {
		this.budgetReAppropriationWorkflowService = budgetReAppropriationWorkflowService;
	}

	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}


}
	
