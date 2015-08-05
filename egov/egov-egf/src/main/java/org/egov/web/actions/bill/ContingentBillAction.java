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
 *      1) All versions of this program, verbatim or modified must carry this 
 *         Legal Notice.
 * 
 *      2) Any misrepresentation of the origin of the material is prohibited. It 
 *         is required that all modified versions of this material be marked in 
 *         reasonable ways as different from the original version.
 * 
 *      3) This license does not grant any rights to any user of the program 
 *         with regards to rights under trademark law for use of the trade names 
 *         or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
/**
 * 
 */
package org.egov.web.actions.bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptContext;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.EgwStatus;
import org.egov.commons.utils.EntityType;
import org.egov.egf.bills.model.Cbill;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.script.entity.Script;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.models.EgChecklists;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.infstr.utils.NumberToWord;
import org.egov.infstr.workflow.Action;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBillSubType;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.EgBillregistermis;
import org.egov.model.voucher.VoucherDetails;
import org.egov.pims.commons.Designation;
import org.egov.utils.CheckListHelper;
import org.egov.utils.FinancialConstants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * @author mani
 *
 */
@ParentPackage("egov")
@Results({
	@Result(name = ContingentBillAction.NEW, location = "contingentBill-new.jsp"),
	@Result(name = "messages", location = "contingentBill-messages.jsp"),
	@Result(name = ContingentBillAction.VIEW, location = "contingentBill-view.jsp")
	})
public class ContingentBillAction extends BaseBillAction {
        public class COAcomparator implements Comparator<CChartOfAccounts> {
                @Override
                public int compare(CChartOfAccounts o1, CChartOfAccounts o2) {
                   return o1.getGlcode().compareTo(o2.getGlcode());
                        
                }

        }
        private static final String     ACCOUNT_DETAIL_TYPE_LIST        = "accountDetailTypeList";
        private static final String     BILL_SUB_TYPE_LIST      = "billSubTypeList";
        private static final String     USER_LIST       = "userList";
        private static final String     DESIGNATION_LIST        = "designationList";
        private static final String     MODE    = "mode";
        private static final String     APPROVER_USER_ID        = "approverUserId";
        private static final String     END     = "END";
        private static final String     APPROVE = "approve";
        private static final String     ACTION_NAME     = "actionName";
        private static final String     WFITEMSTATE     = "wfitemstate";
        private static final long       serialVersionUID        = 1L;
        private static final Logger     LOGGER  = Logger.getLogger(ContingentBillAction.class);
        private BigDecimal      debitSum=BigDecimal.ZERO;
        BigDecimal billAmount = BigDecimal.ZERO;
        private boolean showPrintPreview;
        private String  sanctionedMessge;
        private Department primaryDepartment ;
        
  
        @Override
        public Object getModel() {
                return super.getModel();
        }
        @Override
        public void prepare() {
                super.prepare();
                accountDetailTypeList=persistenceService.findAllBy("from Accountdetailtype where isactive=1 order by name");
                addDropdownData(ACCOUNT_DETAIL_TYPE_LIST, accountDetailTypeList);
                addDropdownData(BILL_SUB_TYPE_LIST,getBillSubTypes());
                addDropdownData(USER_LIST, Collections.EMPTY_LIST);
                addDropdownData(DESIGNATION_LIST, Collections.EMPTY_LIST);
                getNetPayableCodes();
                billDetailslist = new ArrayList<VoucherDetails>();
                billDetailslist.add(new VoucherDetails()); 
                Map<String,String> mp=new LinkedHashMap<String,String>();
                mp.put("na", getText("na"));
                mp.put("yes",getText("yes"));
                mp.put("no", getText("no"));
                commonBean.setCheckListValuesMap(mp);
        //  If the department is mandatory show the logged in users assigned department only.
                if(mandatoryFields.contains("department")){
                        List<Department> deptList;
                        deptList = voucherHelper.getAllAssgnDeptforUser();
                        addDropdownData("departmentList", deptList);
                      /*  if(deptList == null || deptList.isEmpty())
                        {
                                LOGGER.error("User is not assigned any departments! ");
                                throw new ValidationException(Arrays.asList(new ValidationError("User is not assigned any departments","User is not assigned any departments")));
                        }*/
                                
                        //primaryDepartment = deptList.get(0);//need to fix Phoenix
                        addDropdownData("billDepartmentList", persistenceService.findAllBy("from Department order by name"));
                }
        }
        
        public void     prepareNewform()
        {
                billDetailslist = new ArrayList<VoucherDetails>();
                billDetailslist.add(new VoucherDetails()); 
                billDetailsTableFinal=null;
                billDetailsTableNetFinal=null;
                billDetailsTableCreditFinal=null;
                checkListsTable=null;
                subledgerlist=null;
        }
        
        private void getNetPayableCodes() {
                List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey("EGF","contingencyBillPurposeIds");
                List<AppConfigValues>  configValuesByModuleAndKeydefault = appConfigValuesService.getConfigValuesByModuleAndKey("EGF","contingencyBillDefaultPurposeId");
                String tempCBillDefaulPurposeId = configValuesByModuleAndKeydefault.get(0).getValue();
                Long cBillDefaulPurposeId = Long.valueOf(tempCBillDefaulPurposeId);
                netPayList = new ArrayList<CChartOfAccounts>();
                //CChartOfAccounts coa;
                List<CChartOfAccounts> accountCodeByPurpose= new ArrayList<CChartOfAccounts>();
                for(int i=0;i<configValuesByModuleAndKey.size();i++)
                {
                        try {
                                 accountCodeByPurpose = chartOfAccountsHibernateDAO.getAccountCodeByPurpose(Integer.valueOf(configValuesByModuleAndKey.get(i).getValue()));
                        } catch (NumberFormatException e) {
                                LOGGER.error("Inside getNetPayableCodes"+e.getMessage(), e);
                        } catch (Exception e) {
                                LOGGER.error("inside getNetPayableCodes"+e.getMessage());
                        }
                        for(CChartOfAccounts coa:accountCodeByPurpose)
                        {
                                        //defaultNetPayCode=coa;
                                        detailTypeIdandName=coa.getGlcode()+"~"+getDetailTypesForCoaId(coa.getId())+"^"+detailTypeIdandName;
                        }
                        
                        if(configValuesByModuleAndKey.get(i).getValue().equals(cBillDefaulPurposeId))
                        {
                                for(CChartOfAccounts coa:accountCodeByPurpose)
                                {
                                        if(coa.getPurposeId().compareTo(cBillDefaulPurposeId)==0)
                                        {
                                                defaultNetPayCode=coa;
//                                              detailTypeIdandName=coa.getGlcode()+"~"+getDetailTypesForCoaId(coa.getId())+"^"+detailTypeIdandName;
                                        }
                                }
                        
                        }
                        
                        netPayList.addAll(accountCodeByPurpose);   
                        
                }
                Collections.sort(netPayList, new COAcomparator());
                for(CChartOfAccounts c:netPayList)
                {
                        if(LOGGER.isInfoEnabled())     LOGGER.info(c.getGlcode());
                }
                if(LOGGER.isDebugEnabled())     LOGGER.debug("netPayList............................."+netPayList.size());
                getSession().put("netPayList", netPayList);
        }
        @SuppressWarnings("unchecked")
        @SkipValidation       
@org.apache.struts2.convention.annotation.Action(value="/bill/contingentBill-newform")
        public String newform()
        {
                reset();
                commonBean.setBillDate(getDefaultDate());  
                if(LOGGER.isDebugEnabled())     LOGGER.debug("bigllDetailslist.............................."+billDetailslist.size());
                if(true/*getValidActions("authentication",null).size()==0*/)
                {
                        addActionMessage(getText("cbill.user.authenticate"));
                }
                else
                {
                        Map<String, Object>  map = voucherService.getDesgBYPassingWfItem("cbill.nextUser",null,null);
                        addDropdownData(DESIGNATION_LIST, (List<Designation>)map.get(DESIGNATION_LIST)); 
                        addDropdownData(USER_LIST, Collections.EMPTY_LIST);
                        nextLevel = map.get(WFITEMSTATE)!=null?map.get(WFITEMSTATE).toString():null;
                }
                return NEW;
        }
        
        private  List<Action> getValidActions(String purpose , EgBillregister cbill) {
                validButtons = new ArrayList<Action>();
                Script validScript = (Script) getPersistenceService().findAllByNamedQuery(Script.BY_NAME,"cbill.validation").get(0);
                List<String> list = null;/*(List<String>) validScript.eval(Script.createContext("eisCommonServiceBean", eisCommonService,"userId",Integer.valueOf(EgovThreadLocals.getUserId().trim()),
                                                                        "date",new Date(),"purpose",purpose,"wfitem",cbill));*///This fix is for Phoenix Migration.
                for(Object s:list) 
                {
                        if("invalid".equals(s))
                                break;
                        Action action = (Action) getPersistenceService().find(" from org.egov.infstr.workflow.Action where type='EgBillregister' and name=?", s.toString());
                        validButtons.add(action);
                }
                return validButtons;
        }
        @Transactional
        @SkipValidation
        @org.apache.struts2.convention.annotation.Action(value="/bill/contingentBill-update")
        public String  update(){
                if(LOGGER.isDebugEnabled())     LOGGER.debug("Contingent Bill Action  | update | start");
                Integer userId = -1;
                Cbill cbill=(Cbill) getPersistenceService().find(" from Cbill where id=?", Long.valueOf(parameters.get("billRegisterId")[0]));
                if(cbill != null && cbill.getState() != null){
                        if(!validateOwner(cbill.getState())){
                                throw new EGOVRuntimeException("Invalid Aceess");
                        }
                }
                if(null == cbill.getEgBillregistermis().getSourcePath()){
                        cbill.getEgBillregistermis().setSourcePath("/EGF/bill/contingentBill!beforeView.action?billRegisterId="+cbill.getId());
                        persistenceService.setType(Cbill.class);
                        persistenceService.update(cbill);
                }
                if(parameters.get(ACTION_NAME)[0].contains(APPROVE))  
                {
                           if(nextLevel.equalsIgnoreCase(END))
                           {
                                   userId =EgovThreadLocals.getUserId().intValue();
                           }else
                           {
                                   userId = Integer.valueOf(parameters.get(APPROVER_USER_ID)[0]);
                           }
                }
                else if(parameters.get(ACTION_NAME)[0].contains("reject"))
                {
                        userId = cbill.getCreatedBy().getId().intValue();
                }
                billRegisterWorkflowService.transition(parameters.get(ACTION_NAME)[0]+"|"+userId, cbill,parameters.get("comments")[0]);
                if(parameters.get(ACTION_NAME)[0].contains("aa_reject"))
                {
                        addActionMessage(getText("billVoucher.file.canceled"));
                }
                else if(parameters.get(ACTION_NAME)[0].contains(APPROVE))
                {
                                if(END.equals(cbill.getState().getValue()))
                                {
                                        showPrintPreview=true;
                                        addActionMessage(getText("pjv.voucher.final.approval",new String[]{"The File has been approved"}));
                                }
                                else{
                                        addActionMessage(getText("pjv.voucher.approved",new String[]{voucherService.getEmployeeNameForPositionId(cbill.getState().getOwnerPosition())}));
                                }
                        }
                else{
                        addActionMessage(getText("pjv.voucher.rejected",new String[]{voucherService.getEmployeeNameForPositionId(cbill.getState().getOwnerPosition())}));
                }
                return "messages";
        }
        private void reset() {
                voucherHeader.reset();
                commonBean.reset();
                billDetailsTableCreditFinal=null;
                billDetailsTableFinal=null;
                billDetailsTableNetFinal=null;
                subledgerlist=null;
                billDetailsTableSubledger=null;
                checkListsTable=null;   
        }
        public void prepareCreate()
        {
                loadSchemeSubscheme();
        }
        @Validations(requiredFields = { @RequiredFieldValidator(fieldName = "fundId", message = "", key = REQUIRED),
                        @RequiredFieldValidator(fieldName = "commonBean.billNumber", message = "", key = REQUIRED),
                        @RequiredFieldValidator(fieldName = "commonBean.billDate", message = "", key = REQUIRED),
                        @RequiredFieldValidator(fieldName = "commonBean.billSubType",message="",key=REQUIRED),
                        @RequiredFieldValidator(fieldName = "commonBean.payto",message="",key=REQUIRED)
                        //Commenting function to revert onefunction center mandatory option
                        //@RequiredFieldValidator(fieldName = "commonBean.functionName",message="",key=REQUIRED)
         }) 
        @Transactional
        @ValidationErrorPage(value=NEW)
        @org.apache.struts2.convention.annotation.Action(value="/bill/contingentBill-create")
        public String create()  
        {
                if(LOGGER.isInfoEnabled())     LOGGER.info(billDetailsTableCreditFinal);
                Cbill cbill=null;
                
                try {
                        voucherHeader.setVoucherDate(commonBean.getBillDate());
                        voucherHeader.setVoucherNumber(commonBean.getBillNumber());
                        // this code should be removed when we enable single function centre change
                        /*if(commonBean.getFunctionId()!=null){ 
                                //CFunction function=(CFunction) getPersistenceService().find(" from CFunction where id=?", commonBean.getFunctionId().longValue());
                                CFunction function = commonsService.getCFunctionById(commonBean.getFunctionId().longValue());
                                voucherHeader.getVouchermis().setFunction(function);              
                        }*/        
                        validateFields();
                        if(!isBillNumberGenerationAuto())
                        {
                                if(!isBillNumUnique(commonBean.getBillNumber()))
                                {
                                        throw new ValidationException(Arrays.asList(new ValidationError("bill number","Duplicate Bill Number : "+commonBean.getBillNumber())));
                                }
                        }
                                
                        cbill =(Cbill) createBill();
                        createCheckList(cbill);
                       // cbill.start().withOwner(getPosition());
                        addActionMessage(getText("cbill.transaction.succesful")+cbill.getBillnumber());
                        billRegisterId=cbill.getId();
                      //  forwardBill(cbill); Phoenix
                        if(cbill.getEgBillregistermis().getBudgetaryAppnumber()!=null)
                        {
                                addActionMessage(getText("budget.recheck.sucessful",new String[]{cbill.getEgBillregistermis().getBudgetaryAppnumber()}));
                        }
                } catch (ValidationException e) {
                        if(LOGGER.isInfoEnabled())     LOGGER.info("Inside catch block");
                       /* Map<String, Object>  map = voucherService.getDesgBYPassingWfItem("cbill.nextUser",cbill,null);
                        addDropdownData(DESIGNATION_LIST, (List<Designation>)map.get(DESIGNATION_LIST)); *///Phoenix
//                        getValidActions("authentication",cbill);                 
                        if(billDetailsTableSubledger==null)              
                        {             
                                billDetailsTableSubledger=new ArrayList<VoucherDetails>();
                        }
                        if(billDetailsTableSubledger.size()==0)
                        {
                                billDetailsTableSubledger.add(new VoucherDetails());
                        }
                        prepare(); //  session gets closed due to the transaction roll back while creating the sequence for the 1st time 
                        // required to call the prepare method again to populate the data to the screen.
                         throw e;
                }
                
                return "messages";
        }   
        @Transactional
        @SkipValidation
        @ValidationErrorPage(value=EDIT)
        public String edit()
        {
                Cbill cbill=null;
                if(getButton().toLowerCase().contains("cancel"))
                {
                        cancelBill();
                        
                }
                else
                {       
                try {
                        cbill=(Cbill)persistenceService.find("from Cbill where id=?",billRegisterId);
                        if(cbill != null && cbill.getState() != null){
                                if(!validateOwner(cbill.getState())){
                                        throw new EGOVRuntimeException("Invalid Aceess");
                                }
                        }
                        voucherHeader.setVoucherDate(commonBean.getBillDate());
                        voucherHeader.setVoucherNumber(commonBean.getBillNumber());
                /* should be removed when enabling single function centre
                 *      if(commonBean.getFunctionId()!=null){
                                //CFunction function=(CFunction) getPersistenceService().find(" from CFunction where id=?", commonBean.getFunctionId().longValue());
                                CFunction function = commonsService.getCFunctionById(commonBean.getFunctionId().longValue());
                                voucherHeader.getVouchermis().setFunction(function);              
                        }                   */
                        validateFields();
                        cbill =(Cbill)updateBill(cbill);
                        validateLedgerAndSubledger();
                        recreateCheckList(cbill);   
                        forwardBill(cbill);
                        
                } catch (ValidationException e) {
                        LOGGER.error("Inside catch block"+e.getMessage());
                        beforeViewWF(cbill); 
                        if(billDetailsTableSubledger==null)
                        {
                                billDetailsTableSubledger=new ArrayList<VoucherDetails>();
                        }
                        if(billDetailsTableSubledger.size()==0)
                        {
                                billDetailsTableSubledger.add(new VoucherDetails());
                        }
                        throw e;
                }
                }
                return "messages";
        }   
        /**
         * 
         */
        private void cancelBill() {
                
                Integer userId=null;
                Cbill cbill=null;
                cbill=(Cbill)persistenceService.find("from Cbill where id=?",billRegisterId);
                if(cbill != null && cbill.getState() != null){
                        if(!validateOwner(cbill.getState())){
                                throw new EGOVRuntimeException("Invalid Aceess");
                        }
                }
                if(parameters.get(ACTION_NAME)[0].contains("reject"))
                {
                        userId = cbill.getCreatedBy().getId().intValue();
                }
        //      billRegisterWorkflowService.transition(parameters.get(ACTION_NAME)[0]+"|"+userId, cbill,parameters.get("comments")[0]);
                cbill.transition(true).end().withOwner(getPosition()).withComments(parameters.get("comments")[0]);
                String statusQury="from EgwStatus where upper(moduletype)=upper('"+FinancialConstants.CONTINGENCYBILL_FIN+"') and  upper(description)=upper('"+FinancialConstants.CONTINGENCYBILL_CANCELLED_STATUS+"')";
                EgwStatus egwStatus=(EgwStatus)persistenceService.find(statusQury);
                cbill.setStatus(egwStatus);
                cbill.setBillstatus(FinancialConstants.CONTINGENCYBILL_CANCELLED_STATUS);
                persistenceService.setType(Cbill.class);
                persistenceService.persist(cbill);
            HibernateUtil.getCurrentSession().flush();
                addActionMessage(getText("cbill.cancellation.succesful"));
        }
        private void removeEmptyRows() {
                List<VoucherDetails> trash=new ArrayList<VoucherDetails>();
                if(billDetailsTableCreditFinal!=null)
                {
                        for(VoucherDetails vd:billDetailsTableCreditFinal)
                        {
                                if(vd==null)
                                {
                                        trash.add(vd);
                                }
                                else if(vd.getGlcodeDetail()==null)
                                {
                                        trash.add(vd);
                                }
                                
                        }
                }
                
                for(VoucherDetails vd:trash)
                {
                        billDetailsTableCreditFinal.remove(vd);
                }
                trash.clear();          
                if(billDetailsTableFinal!=null)
                {
                        for(VoucherDetails vd:billDetailsTableFinal)
                        {
                                if(vd==null)
                                {       
                                        trash.add(vd);
                                }
                                else if(vd.getGlcodeDetail()==null )
                                {
                                        trash.add(vd);
                                }
                        
                        }
                }
                for(VoucherDetails vd:trash)
                {
                        billDetailsTableFinal.remove(vd);
                }
                trash.clear();
                
                
                if(billDetailsTableSubledger!=null)
                {
                        for(VoucherDetails vd:billDetailsTableSubledger)
                        {
                                if(vd==null)
                                {
                                        trash.add(vd);
                                }
                                else if(vd.getSubledgerCode()==null || vd.getSubledgerCode().equals(""))
                                {
                                        trash.add(vd);
                                }
                                
                        }
                }   
                for(VoucherDetails vd:trash)
                {
                        billDetailsTableSubledger.remove(vd);
                }
                   
        }
                
        private void validateLedgerAndSubledger() { 
                List<VoucherDetails> finalList=new ArrayList<VoucherDetails>();
      removeEmptyRows();
      if(billDetailsTableFinal!=null)
      finalList.addAll(billDetailsTableFinal);
      if(billDetailsTableCreditFinal!=null)
      finalList.addAll(billDetailsTableCreditFinal);
      if(billDetailsTableNetFinal!=null)
      finalList.addAll(billDetailsTableNetFinal);
      billDetailsTableSubledger=rearrangeSubledger(billDetailsTableSubledger);
      if(billDetailsTableSubledger==null)
      {
          billDetailsTableSubledger=new ArrayList<VoucherDetails>();
      }
      if(validateData(finalList,billDetailsTableSubledger))
      {
          throw new ValidationException(Arrays.asList(new ValidationError("Ledger.validation.failed","Ledger.validation.failed")));
      }
        }
        /**
         * @param billDetailsTableSubledger
         * @return 
         */
        private List<VoucherDetails> rearrangeSubledger(List<VoucherDetails> billDetailsTableSubledger) {
                if(billDetailsTableSubledger!=null )
                {
                if(commonBean.getSubledgerType()!=null &&commonBean.getSubledgerType()>0)
                {
                Accountdetailtype detailType = (Accountdetailtype)persistenceService.find("from Accountdetailtype where id=? order by name",commonBean.getSubledgerType());
                for( VoucherDetails vd:billDetailsTableSubledger )
                {
                        vd.setAmount(vd.getDebitAmountDetail());
                        CChartOfAccounts coa =(CChartOfAccounts) persistenceService.find("from CChartOfAccounts where glcode=?",vd.getSubledgerCode());
                        vd.setGlcode(coa);
                        vd.setDetailType(detailType);
                        vd.setDetailKeyId(Integer.valueOf(vd.getDetailKey()));
                }
                }
                }                                                                       
                return billDetailsTableSubledger;
        }
        @Transactional
        @SuppressWarnings("unchecked")
        private void recreateCheckList(Cbill bill) {
                List<EgChecklists> checkLists = persistenceService.findAllBy("from org.egov.infstr.models.EgChecklists where objectid=?",billRegisterId);
                for(EgChecklists chk:checkLists) 
                {
                        persistenceService.delete(chk);
                }               
                createCheckList(bill);
        }
        
        @Transactional
        private Cbill updateBill(Cbill bill) {
                final HashMap<String, Object> headerDetails = createHeaderAndMisDetails();
                headerDetails.put(VoucherConstant.SOURCEPATH, "/EGF/bill/contingentBill!beforeView.action?billRegisterId=");
                Boolean recreateBillnumber=false;
                if(bill.getEgBillregistermis().getEgDepartment()!=null && voucherHeader.getVouchermis().getDepartmentid()!=null)
                {
                        if(bill.getEgBillregistermis().getEgDepartment().getId()!=voucherHeader.getVouchermis().getDepartmentid().getId())
                                recreateBillnumber=true;
                }
                bill=setBillDetailsFromHeaderDetails(bill,bill.getEgBillregistermis(),recreateBillnumber);
                Set<EgBilldetails> EgBillSet = bill.getEgBilldetailes();
                Iterator billDetItr = EgBillSet.iterator();   
                int i = 0;
                EgBilldetails billDet = null;
                for (; billDetItr.hasNext();) {    
                        try {   
                                billDet = (EgBilldetails) billDetItr.next();
                                // if(LOGGER.isDebugEnabled())     LOGGER.debug(" billDet "+ billDet.getId());
                                billDetItr.remove();
                        } catch (Exception e) {
                                LOGGER.error("Inside updateBill"+e.getMessage(), e);
                                 
                        }
                }   
        HibernateUtil.getCurrentSession().flush();
                bill.setEgBilldetailes(EgBillSet);
                EgBillSet.addAll(updateBillDetails(bill));
                checkBudgetandGenerateNumber(bill);
                HibernateUtil.getCurrentSession().refresh(bill);                       
                persistenceService.setType(Cbill.class);
                persistenceService.persist(bill);
        HibernateUtil.getCurrentSession().flush();
                return bill;                     
        }
        private Cbill checkBudgetandGenerateNumber(Cbill bill) {
                Boolean budgetCheck=false;
                ScriptContext scriptContext = ScriptService.createContext("voucherService",voucherService,"bill",bill);
                budgetCheck=(Boolean) scriptService.executeScript( "egf.bill.budgetcheck", scriptContext);
                return bill;   
        }
        private void forwardBill(Cbill cbill )
        {
                Integer userId = null;
                if(null != parameters.get(APPROVER_USER_ID) &&  Integer.valueOf(parameters.get(APPROVER_USER_ID)[0])!=-1)
                {
                        userId = Integer.valueOf(parameters.get(APPROVER_USER_ID)[0]);
                }else 
                {
                        userId = EgovThreadLocals.getUserId().intValue();
                }
                if(LOGGER.isDebugEnabled())     LOGGER.debug("User selected id is : "+userId);
                billRegisterWorkflowService.transition(parameters.get(ACTION_NAME)[0]+"|"+userId, cbill,parameters.get("comments")[0]);
                addActionMessage(getText("pjv.voucher.approved",new String[]{voucherService.getEmployeeNameForPositionId(cbill.getState().getOwnerPosition())}));
        }
        @SkipValidation
@org.apache.struts2.convention.annotation.Action(value="/bill/contingentBill-beforeView")
        public String beforeView() throws ClassNotFoundException {
                EgBillregister cbill=billRegisterService.find("from EgBillregister where id=?",billRegisterId);
                if((cbill.getState()!=null) && cbill.getState().getValue()!=null){
                if((cbill.getState().getValue().contains("REJECT")||cbill.getState().getValue().contains("reject")) &&( null != parameters.get(MODE) && parameters.get(MODE)[0].equalsIgnoreCase(APPROVE)))
                {
                        return beforeEdit();  
                }
                }
                cbill = prepareForViewModifyReverse();
                addDropdownData(USER_LIST, Collections.EMPTY_LIST);
                addDropdownData("billDepartmentList", persistenceService.findAllBy("from Department order by name"));
                if(null != parameters.get(MODE) && parameters.get(MODE)[0].equalsIgnoreCase(APPROVE)){
                        if(!validateOwner(cbill.getState())) 
                        {
                                throw new EGOVRuntimeException("Invalid Aceess");  
                        }
                        beforeViewWF(cbill);
                        mode =APPROVE;
                }else{
                        mode = VIEW;
                }
                
                return VIEW;  
        }
        
        @SuppressWarnings("unchecked")
        private void beforeViewWF(EgBillregister cbill){
                
                Map<String, Object>  map ;
                //This was previously loading the designation list according to the bill department since,
                //the bill department was by default loaded to the department dropdown
                //Now, we are loading the primary assignment department as the default in the dropdown(see mingle 2103,2102, 2104)
                //Hence the primary department is passed here 
                if(primaryDepartment!=null && primaryDepartment.getId()!=null)
                        map = voucherService.getDesgBYPassingWfItem("cbill.nextUser",cbill, primaryDepartment.getId().intValue());
                else
                        map = voucherService.getDesgBYPassingWfItem("cbill.nextUser",cbill, voucherHeader.getVouchermis().getDepartmentid().getId().intValue());
                addDropdownData(DESIGNATION_LIST, (List<Map<String, Object>>)map.get(DESIGNATION_LIST)); 
                addDropdownData(USER_LIST, Collections.EMPTY_LIST);
                nextLevel = map.get(WFITEMSTATE)!=null?map.get(WFITEMSTATE).toString():null;
                getValidActions("",cbill);
        }

        private EgBillregister prepareForViewModifyReverse() throws ClassNotFoundException {
                billDetailsTableNetFinal=new ArrayList<VoucherDetails>();
                billDetailsTableCreditFinal=new ArrayList<VoucherDetails>();
                billDetailsTableFinal=new ArrayList<VoucherDetails>();
                billDetailsTableSubledger=new ArrayList<VoucherDetails>();
                checkListsTable=new ArrayList<CheckListHelper>();
                //getNetPayableCodes();
                EgBillregister cbill=billRegisterService.find("from EgBillregister where id=?",billRegisterId);
                getHeadersFromBill(cbill);
                billAmount = cbill.getBillamount();
                Set<EgBilldetails> egBilldetailes = cbill.getEgBilldetailes();
                for(EgBilldetails detail:egBilldetailes)
                {
                        //getAll Credits incuding net pay 
                                VoucherDetails vd=new VoucherDetails();
                                BigDecimal glcodeid = detail.getGlcodeid();
                                CChartOfAccounts coa =(CChartOfAccounts) persistenceService.find("from CChartOfAccounts where id=?",Long.valueOf(glcodeid.toString()));
                                vd.setGlcodeDetail(coa.getGlcode());
                                vd.setGlcodeIdDetail(coa.getId());
                                vd.setAccounthead(coa.getName());
                                vd.setCreditAmountDetail(detail.getCreditamount());
                                if(detail.getFunctionid()!=null)
                                {
                                CFunction functionById = (CFunction) functionHibernateDAO.findById(detail.getFunctionid().longValue(),false);
                                commonBean.setFunctionName(functionById.getName());
                                commonBean.setFunctionId(functionById.getId().intValue());
                                }
                                if(coa.getChartOfAccountDetails().size()>0)
                                {
                                        vd.setIsSubledger(TRUE);
                                }
                                else     
                                {
                                        vd.setIsSubledger(FALSE);
                                }
                                if(netPayList.contains(coa))
                                {
                                        vd.setCreditAmountDetail(detail.getCreditamount());
                                        billDetailsTableNetFinal.add(vd);
                                        
                                }
                                else if(detail.getCreditamount()!=null && !detail.getCreditamount().equals(BigDecimal.ZERO))
                                {
                                        vd.setCreditAmountDetail(detail.getCreditamount());
                                        billDetailsTableCreditFinal.add(vd);    
                                }
                        
                                if(detail.getDebitamount()!=null && !detail.getDebitamount().equals(BigDecimal.ZERO))
                                {
                                        vd.setDebitAmountDetail(detail.getDebitamount());
                                        billDetailsTableFinal.add(vd);
                                }
                                Set<EgBillPayeedetails> egBillPaydetailes = detail.getEgBillPaydetailes();
                                for(EgBillPayeedetails payeedetail:egBillPaydetailes)
                                {
                                        VoucherDetails subVd=new VoucherDetails();
                                        subVd.setDetailKey(payeedetail.getAccountDetailKeyId().toString());
                                        subVd.setAccounthead(coa.getName());
                                        subVd.setGlcodeDetail(coa.getGlcode());
                                        subVd.setSubledgerCode(coa.getGlcode());
                                        commonBean.setSubledgerType(payeedetail.getAccountDetailTypeId());
                                        Accountdetailtype detailType = (Accountdetailtype)persistenceService.find("from Accountdetailtype where id=? order by name",payeedetail.getAccountDetailTypeId());
                                        String table=detailType.getFullQualifiedName();
                                        Class<?> service = Class.forName(table);
                                        String simpleName = service.getSimpleName();
                                        String tableName=simpleName;
                                        //simpleName=simpleName.toLowerCase()+"Service";
                                        simpleName = simpleName.substring(0,1).toLowerCase()+simpleName.substring(1)+"Service";
                                        WebApplicationContext wac= WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext());
                                //      EntityTypeService entityService=        (EntityTypeService)wac.getBean(simpleName);
                                        PersistenceService entityPersistenceService=(PersistenceService)wac.getBean(simpleName);
                                        //it may give error since it is finding from session
                                //      entityPersistenceService.
                                        String dataType = "";
                                        try {
                                                Class aClass = Class.forName(table);
                                                java.lang.reflect.Method method = aClass.getMethod("getId");
                                                dataType = method.getReturnType().getSimpleName();
                                        } catch (Exception e) {
                                                LOGGER.error("prepareForViewModifyReverse"+e.getMessage(), e);
                                                throw new EGOVRuntimeException(e.getMessage());
                                        }
                                        EntityType entity = null;
                                        if ( dataType.equals("Long") )
                                        {
                                                entity=(EntityType)entityPersistenceService.findById(Long.valueOf(payeedetail.getAccountDetailKeyId().toString()),false);
                                        }else{
                                                entity=(EntityType)entityPersistenceService.findById(payeedetail.getAccountDetailKeyId(),false);
                                        }
                                    subVd.setDetailName(entity.getName());
                                    subVd.setDetailCode(entity.getCode());
                                        if(detail.getCreditamount()!=null && !detail.getCreditamount().equals(BigDecimal.ZERO))
                                        {
                                                subVd.setDebitAmountDetail(payeedetail.getCreditAmount());
                                                
                                        }
                                        else
                                        {
                                                subVd.setDebitAmountDetail(payeedetail.getDebitAmount());
                                                
                                        }
                                        
                                        billDetailsTableSubledger.add(subVd);
                                }
                        
                }
                if(billDetailsTableSubledger.size()==0)
                {
                        billDetailsTableSubledger.add(new VoucherDetails());
                }
                if(cbill.getStatus().getDescription().equalsIgnoreCase(FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS) && null != cbill.getState())
                {
                        BigDecimal amt = cbill.getPassedamount().setScale(2);
                        String amountInWords=NumberToWord.convertToWord(amt.toString());
                        sanctionedMessge=getText("cbill.getsanctioned.message",new String[] {amountInWords,cbill.getPassedamount().toString(),voucherService.getEmployeeNameForPositionId(cbill.getState().getOwnerPosition())});
                
                }
                else{
                        BigDecimal amt = cbill.getPassedamount().setScale(2);
                        String amountInWords=NumberToWord.convertToWord(amt.toString());
                        sanctionedMessge=getText("cbill.getsanctioned.message",new String[] {amountInWords,cbill.getPassedamount().toString()});
                        sanctionedMessge=sanctionedMessge.substring(0, sanctionedMessge.length()-15);
                }
                persistenceService.setType(EgChecklists.class);
                List<EgChecklists> checkLists = persistenceService.findAllBy("from org.egov.infstr.models.EgChecklists where objectid=?",billRegisterId);
                for(EgChecklists chk:checkLists) 
                {
                        CheckListHelper chkHelper=new CheckListHelper();
                        chkHelper.setName(chk.getAppconfigvalue().getValue());
                        chkHelper.setVal(chk.getChecklistvalue());
                        chkHelper.setId((chk.getAppconfigvalue().getId()));  
                        checkListsTable.add(chkHelper);
                }
                return cbill;
        }
        /**
         * @param cbill
         */
        private void getHeadersFromBill(EgBillregister cbill) {
                voucherHeader.setFundId(cbill.getEgBillregistermis().getFund());
                voucherHeader.getVouchermis().setDepartmentid(cbill.getEgBillregistermis().getEgDepartment());
                voucherHeader.getVouchermis().setDivisionid(cbill.getEgBillregistermis().getFieldid());
                voucherHeader.getVouchermis().setSchemeid(cbill.getEgBillregistermis().getScheme());
                voucherHeader.getVouchermis().setSubschemeid(cbill.getEgBillregistermis().getSubScheme());
                voucherHeader.getVouchermis().setFundsource(cbill.getEgBillregistermis().getFundsource());
                //voucherHeader.getVouchermis().setFunction(cbill.getEgBillregistermis().getFunction());
                voucherHeader.setDescription(cbill.getNarration());
                commonBean.setBillDate(cbill.getBilldate());
                commonBean.setBillNumber(cbill.getBillnumber());
                if(null != cbill.getEgBillregistermis().getEgBillSubType()){
                        commonBean.setBillSubType(cbill.getEgBillregistermis().getEgBillSubType().getId());
                }
                commonBean.setInwardSerialNumber(cbill.getEgBillregistermis().getInwardSerialNumber());
                commonBean.setPartyBillNumber(cbill.getEgBillregistermis().getPartyBillNumber());
                commonBean.setPartyBillDate(cbill.getEgBillregistermis().getPartyBillDate());
                commonBean.setPayto(cbill.getEgBillregistermis().getPayto());
                if(null != cbill.getState()){
                        commonBean.setStateId(cbill.getState().getId());   
                }
                commonBean.setBudgetReappNo(cbill.getEgBillregistermis().getBudgetaryAppnumber());
                if(cbill.getStatus().getDescription().equalsIgnoreCase(FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS) && (null != cbill.getState()))
                {
                        String amountInWords=NumberToWord.amountInWords(cbill.getPassedamount().doubleValue());
                                
                        sanctionedMessge=getText("cbill.getsanctioned.message",new String[] {amountInWords,cbill.getPassedamount().toString(),voucherService.getEmployeeNameForPositionId(cbill.getState().getOwnerPosition())});
                }       
                else{
                        String amountInWords=NumberToWord.amountInWords(cbill.getPassedamount().doubleValue());
                        sanctionedMessge=getText("cbill.getsanctioned.message",new String[] {amountInWords,cbill.getPassedamount().toString()});
                        sanctionedMessge=sanctionedMessge.substring(0, sanctionedMessge.length()-15);
                }
        }
        @SkipValidation
@org.apache.struts2.convention.annotation.Action(value="/bill/contingentBill-beforeEdit")
        public String beforeEdit() throws ClassNotFoundException {
                EgBillregister cbill = prepareForViewModifyReverse();
                if(getValidActions("authentication",cbill).size()==0)
                {
                        addActionMessage(getText("cbill.user.authenticate"));
                }
                addDropdownData(USER_LIST, Collections.EMPTY_LIST);
                if(null != parameters.get(MODE) && parameters.get(MODE)[0].equalsIgnoreCase(APPROVE)){
                        beforeViewWF(cbill);
                        mode =APPROVE;
                }else{
                        mode = "view";  
                }
                return EDIT;
        }
        
        @SkipValidation
        public String beforeReverse() throws ClassNotFoundException {
                prepareForViewModifyReverse();
                return REVERSE;
        }
        private void createCheckList(EgBillregister bill) {
        if(checkListsTable!=null)
        {
        for(CheckListHelper clh:checkListsTable)
         {
                 EgChecklists checkList=new EgChecklists();
                 AppConfigValues configValue=(AppConfigValues)persistenceService.find("from AppConfigValues where id=?",clh.getId());
                 checkList.setObjectid(bill.getId());
                 checkList.setAppconfigvalue(configValue);
                 checkList.setChecklistvalue(clh.getVal());
                 HibernateUtil.getCurrentSession().saveOrUpdate(checkList);
         }
        }       
        }
        public List<CheckListHelper> getCheckListsTable() {
                return checkListsTable;
        }
        public void setCheckListsTable(List<CheckListHelper> checkListsTable) {
                this.checkListsTable = checkListsTable;
        }
        @Override
        public String execute()  {
                // TODO Auto-generated method stub
                try {
                        super.execute();
                } catch (Exception e) {
                        LOGGER.error("Inside execute"+e.getMessage(), e);
                        throw new EGOVRuntimeException(e.getMessage());
                }
                billDetailslist = new ArrayList<VoucherDetails>();
                billDetailslist.add(new VoucherDetails());                
                billDetailslist.add(new VoucherDetails());
                return NEW;
        }
        @SuppressWarnings("unchecked")
        private  Cbill createBill() {
                
                final HashMap<String, Object> headerDetails = createHeaderAndMisDetails();
                // update DirectBankPayment source path
                headerDetails.put(VoucherConstant.SOURCEPATH, "/EGF/bill/contingentBill-beforeView.action?billRegisterId=");
                Cbill bill=new Cbill();
                EgBillregistermis mis=new EgBillregistermis();
                bill=setBillDetailsFromHeaderDetails(bill,mis,true);
                bill=createBillDetails(bill);
                validateLedgerAndSubledger();
                bill=checkBudgetandGenerateNumber(bill);
                //billsManager.createBillRegister(bill);      
                //billRegisterService.persist(bill);
                cbillService.applyAuditing(bill);
                cbillService.persist(bill);
                //Setting the sourcepath
                bill.getEgBillregistermis().setSourcePath("/EGF/bill/contingentBill-beforeView.action?billRegisterId="+bill.getId().toString());
                return bill;                        
        }

        private Cbill createBillDetails(Cbill bill) {
                EgBilldetails billdetails;
                EgBillPayeedetails payeedetails;
                Set<EgBillPayeedetails> payeedetailsSet;
                Set<EgBilldetails> billdetailsSet=new HashSet<EgBilldetails>();
        //if entity count is 1 or 0 save the payto in billregistermis else dont save
                String entityKey=null;  
                int entityCount=0;
                for(VoucherDetails vd:billDetailsTableFinal )
                {
                        billdetails=new EgBilldetails();
                        billdetails.setGlcodeid(BigDecimal.valueOf(vd.getGlcodeIdDetail()));
                        if(commonBean.getFunctionId()!=null)
                        {
                                billdetails.setFunctionid(BigDecimal.valueOf(commonBean.getFunctionId()));
                        }
                        billdetails.setDebitamount(vd.getDebitAmountDetail());
                        debitSum=debitSum.add(vd.getDebitAmountDetail());
                        billdetails.setEgBillregister(bill);
                        
                        if(vd.getIsSubledger().equalsIgnoreCase(TRUE))
                        {
                                payeedetailsSet=new HashSet<EgBillPayeedetails>();
                                for(VoucherDetails sub:billDetailsTableSubledger)
                                {
                                        if(vd.getGlcodeDetail().equalsIgnoreCase(sub.getSubledgerCode()))
                                        {
                                                payeedetails=new EgBillPayeedetails();
                                                payeedetails.setDebitAmount(sub.getDebitAmountDetail());
                                                
                                                payeedetails.setAccountDetailKeyId(Integer.valueOf(sub.getDetailKey()));
                                                payeedetails.setAccountDetailTypeId(commonBean.getSubledgerType());
                                                payeedetails.setEgBilldetailsId(billdetails);
                                                payeedetailsSet.add(payeedetails);
                                                if(entityKey==null)
                                                {
                                                        entityKey=sub.getDetailKey();
                                                }
                                                if(!entityKey.equals(sub.getDetailKey()))
                                                {
                                                        entityCount++;
                                                }
                                                
                                        }
                                        
                                }
                                billdetails.setEgBillPaydetailes(payeedetailsSet);
                                
                        }
                        billdetailsSet.add(billdetails);
                }
                if(billDetailsTableCreditFinal!=null)
                {
                for(VoucherDetails vd:billDetailsTableCreditFinal )
                {
                        billdetails=new EgBilldetails();
                        billdetails.setGlcodeid(BigDecimal.valueOf(vd.getGlcodeIdDetail()));
                        if(commonBean.getFunctionId()!=null)
                        {
                                billdetails.setFunctionid(BigDecimal.valueOf(commonBean.getFunctionId()));
                        }
                        billdetails.setCreditamount(vd.getCreditAmountDetail());
                        billdetails.setEgBillregister(bill);
                        
                        if(vd.getIsSubledger().equalsIgnoreCase(TRUE))
                        {
                                payeedetailsSet=new HashSet<EgBillPayeedetails>();
                                for(VoucherDetails sub:billDetailsTableSubledger)
                                {
                                        if(vd.getGlcodeDetail().equalsIgnoreCase(sub.getSubledgerCode()))
                                        {
                                                payeedetails=new EgBillPayeedetails();
                                                payeedetails.setCreditAmount(sub.getDebitAmountDetail());
                                                payeedetails.setAccountDetailKeyId(Integer.valueOf(sub.getDetailKey()));
                                                payeedetails.setAccountDetailTypeId(commonBean.getSubledgerType());
                                                payeedetails.setEgBilldetailsId(billdetails);
                                                payeedetailsSet.add(payeedetails);
                                                if(entityKey==null)
                                                {
                                                        entityKey=sub.getDetailKey();
                                                }
                                                if(!entityKey.equals(sub.getDetailKey()))
                                                {
                                                        entityCount++;
                                                }
                                                
                                        }
                                }
                                billdetails.setEgBillPaydetailes(payeedetailsSet);
                                
                        }
                        billdetailsSet.add(billdetails);
                }
                }
                
                for(VoucherDetails vd:billDetailsTableNetFinal )
                {
                        billdetails=new EgBilldetails();
                        String netGlCode=vd.getGlcodeDetail();
                        String[] netGl=netGlCode.split("-");
                        
                        CChartOfAccounts netCoa =(CChartOfAccounts) persistenceService.find("from CChartOfAccounts where glcode=?",netGl[0]);
                        billdetails.setGlcodeid(BigDecimal.valueOf(netCoa.getId()));
                        vd.setGlcodeIdDetail(netCoa.getId());
                        if(isOneFunctionCenter()){   
                                if(commonBean.getFunctionId()!=null)
                                {
                                        billdetails.setFunctionid(BigDecimal.valueOf(commonBean.getFunctionId()));
                                }
                        } // commented - msahoo Function  is not required against the liability codes in the Bil
                          // uncommenting the above code to implement one function center mandatory code.
                        billdetails.setCreditamount(vd.getCreditAmountDetail());
                        bill.setBillamount(debitSum);
                        bill.setPassedamount(debitSum);
                        billdetails.setEgBillregister(bill);
                        if(vd.getIsSubledger().equalsIgnoreCase(TRUE))
                        {
                                payeedetailsSet=new HashSet<EgBillPayeedetails>();
                                for(VoucherDetails sub:billDetailsTableSubledger)
                                {
                                        if(vd.getGlcodeDetail().equalsIgnoreCase(sub.getSubledgerCode()))
                                        {
                                                payeedetails=new EgBillPayeedetails();
                                                payeedetails.setCreditAmount(sub.getDebitAmountDetail());
                                                payeedetails.setAccountDetailKeyId(Integer.valueOf(sub.getDetailKey()));
                                                payeedetails.setAccountDetailTypeId(commonBean.getSubledgerType());
                                                payeedetails.setEgBilldetailsId(billdetails);
                                                payeedetailsSet.add(payeedetails);
                                                if(entityKey==null)
                                                {
                                                        entityKey=sub.getDetailKey();
                                                }
                                                if(!entityKey.equals(sub.getDetailKey()))
                                                {
                                                        entityCount++;
                                                }
                                        }
                                }
                                billdetails.setEgBillPaydetailes(payeedetailsSet);
                                
                        }
                        billdetailsSet.add(billdetails);
                }
                
                bill.setEgBilldetailes(billdetailsSet);
                if(entityCount<2)
                {
                        bill.getEgBillregistermis().setPayto(commonBean.getPayto());
                }else
                {
                        bill.getEgBillregistermis().setPayto(FinancialConstants.MULTIPLE);
                }
                return bill;
        }
        
        private Set<EgBilldetails> updateBillDetails(Cbill bill)
        {
                        EgBilldetails billdetails;
                        EgBillPayeedetails payeedetails;
                        Set<EgBillPayeedetails> payeedetailsSet;
                        Set<EgBilldetails> billdetailsSet=new HashSet<EgBilldetails>();
                //if entity count is 1 or 0 save the payto in billregistermis else dont save
                        String entityKey=null;  
                        int entityCount=0;
                        for(VoucherDetails vd:billDetailsTableFinal )
                        {
                                billdetails=new EgBilldetails();
                                billdetails.setGlcodeid(BigDecimal.valueOf(vd.getGlcodeIdDetail()));
                                if(commonBean.getFunctionId()!=null)
                                {
                                        billdetails.setFunctionid(BigDecimal.valueOf(commonBean.getFunctionId()));
                                }
                                billdetails.setDebitamount(vd.getDebitAmountDetail());
                                debitSum=debitSum.add(vd.getDebitAmountDetail());
                                billdetails.setEgBillregister(bill);
                                
                                if(vd.getIsSubledger().equalsIgnoreCase(TRUE))
                                {
                                        payeedetailsSet=new HashSet<EgBillPayeedetails>();
                                        for(VoucherDetails sub:billDetailsTableSubledger)
                                        {
                                                if(sub==null)
                                                {
                                                        continue;  
                                                }
                                                if(vd.getGlcodeDetail().equalsIgnoreCase(sub.getSubledgerCode()))
                                                {
                                                        payeedetails=new EgBillPayeedetails();
                                                        payeedetails.setDebitAmount(sub.getDebitAmountDetail());
                                                        
                                                        payeedetails.setAccountDetailKeyId(Integer.valueOf(sub.getDetailKey()));
                                                        payeedetails.setAccountDetailTypeId(commonBean.getSubledgerType());
                                                        payeedetails.setEgBilldetailsId(billdetails);
                                                        payeedetailsSet.add(payeedetails);
                                                        if(entityKey==null)
                                                        {
                                                                entityKey=sub.getDetailKey();
                                                        }
                                                        if(!entityKey.equals(sub.getDetailKey()))
                                                        {
                                                                entityCount++;
                                                        }
                                                        
                                                }
                                                
                                        }
                                        billdetails.setEgBillPaydetailes(payeedetailsSet);
                                        
                                }
                                billdetailsSet.add(billdetails);
                        }
                        if(billDetailsTableCreditFinal!=null)
                        {
                        for(VoucherDetails vd:billDetailsTableCreditFinal )
                        {
                                billdetails=new EgBilldetails();
                                billdetails.setGlcodeid(BigDecimal.valueOf(vd.getGlcodeIdDetail()));
                                if(commonBean.getFunctionId()!=null)
                                {
                                        billdetails.setFunctionid(BigDecimal.valueOf(commonBean.getFunctionId()));
                                }
                                billdetails.setCreditamount(vd.getCreditAmountDetail());
                                billdetails.setEgBillregister(bill);
                                
                                if(vd.getIsSubledger().equalsIgnoreCase(TRUE))
                                {
                                        payeedetailsSet=new HashSet<EgBillPayeedetails>();
                                        for(VoucherDetails sub:billDetailsTableSubledger)
                                        {
                                                if(sub==null)
                                                {
                                                        continue;  
                                                }
                                                if(vd.getGlcodeDetail().equalsIgnoreCase(sub.getSubledgerCode()))
                                                {
                                                        payeedetails=new EgBillPayeedetails();
                                                        payeedetails.setCreditAmount(sub.getDebitAmountDetail());
                                                        payeedetails.setAccountDetailKeyId(Integer.valueOf(sub.getDetailKey()));
                                                        payeedetails.setAccountDetailTypeId(commonBean.getSubledgerType());
                                                        payeedetails.setEgBilldetailsId(billdetails);
                                                        payeedetailsSet.add(payeedetails);
                                                        if(entityKey==null)
                                                        {
                                                                entityKey=sub.getDetailKey();
                                                        }
                                                        if(!entityKey.equals(sub.getDetailKey()))
                                                        {
                                                                entityCount++;
                                                        }
                                                        
                                                }
                                        }
                                        billdetails.setEgBillPaydetailes(payeedetailsSet);
                                        
                                }
                                billdetailsSet.add(billdetails);
                        }
                        }
                        
                        for(VoucherDetails vd:billDetailsTableNetFinal )
                        {
                                billdetails=new EgBilldetails();
                                String netGlCode=vd.getGlcodeDetail();
                                String[] netGl=netGlCode.split("-");
                                
                                CChartOfAccounts netCoa =(CChartOfAccounts) persistenceService.find("from CChartOfAccounts where glcode=?",netGl[0]);
                                billdetails.setGlcodeid(BigDecimal.valueOf(netCoa.getId()));
                                vd.setGlcodeIdDetail(netCoa.getId());
                                // commented - msahoo Function  is not required against the liability codes in the Bill
                                /*
                                 * Uncommenting- Shamili :- Uncommented when one function center is made mandatory. 
                                 */
                                if(voucherHeader.getIsRestrictedtoOneFunctionCenter()){
                                        if(commonBean.getFunctionId()!=null)
                                        {
                                                billdetails.setFunctionid(BigDecimal.valueOf(commonBean.getFunctionId()));
                                                
                                        }
                                } 
                                billdetails.setCreditamount(vd.getCreditAmountDetail());
                                bill.setBillamount(debitSum);
                                bill.setPassedamount(debitSum);
                                billdetails.setEgBillregister(bill);
                                if(vd.getIsSubledger().equalsIgnoreCase(TRUE))
                                {
                                        payeedetailsSet=new HashSet<EgBillPayeedetails>();
                                        for(VoucherDetails sub:billDetailsTableSubledger)
                                        {
                                                if(sub==null)
                                                {
                                                        continue;  
                                                }
                                                if(vd.getGlcodeDetail().equalsIgnoreCase(sub.getSubledgerCode()))
                                                {
                                                        payeedetails=new EgBillPayeedetails();
                                                        payeedetails.setCreditAmount(sub.getDebitAmountDetail());
                                                        payeedetails.setAccountDetailKeyId(Integer.valueOf(sub.getDetailKey()));
                                                        payeedetails.setAccountDetailTypeId(commonBean.getSubledgerType());
                                                        payeedetails.setEgBilldetailsId(billdetails);
                                                        payeedetailsSet.add(payeedetails);
                                                        if(entityKey==null)
                                                        {
                                                                entityKey=sub.getDetailKey();
                                                        }
                                                        if(!entityKey.equals(sub.getDetailKey()))
                                                        {
                                                                entityCount++;
                                                        }
                                                }
                                        }
                                        billdetails.setEgBillPaydetailes(payeedetailsSet);
                                        
                                }
                                billdetailsSet.add(billdetails);
                        }
                        
                        //bill.setEgBilldetailes(billdetailsSet);
                        if(entityCount<2)
                        {
                                bill.getEgBillregistermis().setPayto(commonBean.getPayto());
                        }
                        return billdetailsSet;
                }
        
        
        /**
         * @param bill
         * @param headerDetails
         */
        private Cbill setBillDetailsFromHeaderDetails(Cbill bill,EgBillregistermis mis,boolean generateBill) 
        {
                
                mis.setEgDepartment(voucherHeader.getVouchermis().getDepartmentid());
                mis.setFund(voucherHeader.getFundId());
                mis.setScheme(voucherHeader.getVouchermis().getSchemeid());
                mis.setSubScheme(voucherHeader.getVouchermis().getSubschemeid());
                mis.setFieldid(voucherHeader.getVouchermis().getDivisionid());
                mis.setFundsource(voucherHeader.getVouchermis().getFundsource());
                mis.setFunction(voucherHeader.getVouchermis().getFunction());
                bill.setNarration(voucherHeader.getDescription());
                //mis.setSourcePath("/EGF/bill/contingentBill!beforeView.action?billRegisterId=");
                EgBillSubType egBillSubType =(EgBillSubType)persistenceService.find("from EgBillSubType where id=?",commonBean.getBillSubType());
                mis.setEgBillSubType(egBillSubType);
                mis.setInwardSerialNumber(commonBean.getInwardSerialNumber());
                mis.setPartyBillNumber(commonBean.getPartyBillNumber());
                mis.setPartyBillDate(commonBean.getPartyBillDate());
                bill.setBilldate(commonBean.getBillDate());
                bill.setExpendituretype(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT);
                mis.setEgBillregister(bill); 
                mis.setLastupdatedtime(new Date());
                bill.setEgBillregistermis(mis);
                if(generateBill)
                {
                if(isBillNumberGenerationAuto())
                {
                        commonBean.setBillNumber(getNextBillNumber(bill));  
                }
                
                
                bill.setBillnumber(commonBean.getBillNumber());
                }
                
                bill.setBillstatus(FinancialConstants.CONTINGENCYBILL_CREATED_STATUS);
                String statusQury="from EgwStatus where upper(moduletype)=upper('"+FinancialConstants.CONTINGENCYBILL_FIN+"') and  upper(description)=upper('"+FinancialConstants.CONTINGENCYBILL_CREATED_STATUS+"')";
                EgwStatus egwStatus=(EgwStatus)persistenceService.find(statusQury);
                bill.setStatus(egwStatus);
                bill.setBilltype("Final Bill");
                
                return bill;
        }
        
        /**
         * @param bill 
         * @return
         */
        private String getNextBillNumber(Cbill bill) {
                String billNumber=null;
                CFinancialYear financialYear = financialYearDAO.getFinancialYearByDate(bill.getBilldate());  
                String year=financialYear!=null?financialYear.getFinYearRange():"";
                Script billNumberScript=(Script)persistenceService.findAllByNamedQuery(Script.BY_NAME, "egf.bill.number.generator").get(0);
                ScriptContext scriptContext = ScriptService.createContext("sequenceGenerator",sequenceGenerator,"sItem",bill,"year",year);
                billNumber =(String)    scriptService.executeScript(billNumberScript.getName(), scriptContext);
                if(billNumber==null)  
                {
                        throw new ValidationException(Arrays.asList(new ValidationError("unable.to.generate.bill.number","No Financial Year for bill date"+sdf.format(bill.getBilldate()))));
                }
                return billNumber;
        }
        @SuppressWarnings("unchecked")
        public String getDetailTypesForCoaId(Long id)
        {
        StringBuffer detailTypeIdandName1= new StringBuffer(500);
                List<CChartOfAccountDetail> coaDetails =(List<CChartOfAccountDetail>) persistenceService.findAllBy("from CChartOfAccountDetail where glCodeId.id=?",id);
        for(CChartOfAccountDetail coad:coaDetails)
        {
                detailTypeIdandName1.append(coad.getDetailTypeId().getId()).append("`-`");
        }
        return detailTypeIdandName1.toString();
                
        }
        
          
        public String getSanctionedMessge() {
                return sanctionedMessge;
        }
        public void setSanctionedMessge(String sanctionedMessge) {
                this.sanctionedMessge = sanctionedMessge;
        }
//setters
        
        public String getComments(){
                if(!BigDecimal.ZERO.equals(billAmount))
                {
                        return getText("bill.comments", new String[]{billAmount.toPlainString()});
                }
                else
                {
                        return "";
                }
        }

        public boolean isShowPrintPreview() 
        {
                return showPrintPreview;
        }
        public void setShowPrintPreview(boolean showPrintPreview)
        {
                this.showPrintPreview = showPrintPreview;
        }
        
        
public boolean isBillNumUnique(String billNumber){
                
                String billNum = (String)persistenceService.find("select billnumber from EgBillregister where upper(billnumber)='"+billNumber.toUpperCase()+"'");
                if(null == billNum){
                        return true;
                }else{
                        return false;
                }
        }
public Integer getPrimaryDepartment() {
        return primaryDepartment.getId().intValue();
}
        
}