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
package org.egov.web.actions.bill;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.dao.ChartOfAccountsDAO;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.service.CommonsService;
import org.egov.commons.utils.EntityType;
import org.egov.egf.bills.model.Cbill;
import org.egov.egf.commons.EgovCommon;
import org.egov.eis.service.EisCommonService;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.config.dao.AppConfigValuesDAO;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.SequenceGenerator;
import org.egov.infstr.workflow.Action;
import org.egov.model.bills.EgBillregister;
import org.egov.model.voucher.CommonBean;
import org.egov.model.voucher.VoucherDetails;
import org.egov.pims.commons.Position;
import org.egov.pims.service.EisUtilService;
import org.egov.services.bills.BillsService;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.CheckListHelper;
import org.egov.utils.FinancialConstants;
import org.egov.utils.VoucherHelper;
import org.egov.web.actions.voucher.BaseVoucherAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
@Transactional(readOnly=true)
public class BaseBillAction extends BaseVoucherAction {
        protected static final long     serialVersionUID        = 6627521670678057404L;
        protected EisCommonService      eisCommonService;
        protected CommonBean commonBean;
        protected static final String           REQUIRED        = "required";
        protected static final String   VIEW    = "view";
        protected static final String   REVERSE = "reverse";
        protected List<Accountdetailtype> accountDetailTypeList;
        protected List<EntityType> entitiesList;
        protected List<VoucherDetails>  billDetailslist;
        protected List<VoucherDetails>  subledgerlist;
        protected List<VoucherDetails>  billDetailsTableSubledger;
        protected List<VoucherDetails> billDetailTableFinallist;
        protected List<VoucherDetails> billDetailsTableNetFinalList;
        protected List<VoucherDetails> billDetailsTableCreditFinalist;
        protected List<VoucherDetails> billDetailsTableFinal;
        protected List<VoucherDetails> billDetailsTableNetFinal;
        protected List<VoucherDetails> billDetailsTableCreditFinal;
        protected List<CheckListHelper>checkListsTable;
        protected SimpleWorkflowService<Cbill> billRegisterWorkflowService;
        protected PersistenceService<EgBillregister, Long> billRegisterService;
        protected PersistenceService<Cbill, Long> cbillService;
        protected EgovCommon egovCommon;
        @Autowired
        protected AppConfigValuesDAO appConfigValuesDAO;
        protected CChartOfAccounts      defaultNetPayCode;
        protected Long billRegisterId;
        protected static final String   FALSE   = "false";
        protected static final String   TRUE    = "true";
        protected List<CChartOfAccounts>        netPayList;
        protected SequenceGenerator sequenceGenerator;
        protected ScriptService scriptExecutionService;
        protected CommonsService commonsService;
        protected String detailTypeIdandName="";
        protected BillsService billsManager;
        protected String button;
        protected boolean billNumberGenerationAuto;
        protected final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        protected VoucherService voucherService;
        protected String mode;
        protected String nextLevel;
        protected List<Action> validButtons;
        protected Integer departmentId;
        protected EisUtilService eisUtilService;
        protected VoucherHelper voucherHelper;
        @Autowired
        protected ChartOfAccountsDAO coaDAO;
        @Autowired
        protected FunctionDAO functionDAO;
        @Autowired
        protected FinancialYearDAO financialYearDAO;
        
        protected Date getDefaultDate() {
                final Date currDate = new Date();
                
                try {
                        return(sdf.parse(sdf.format(currDate)));
                } catch (final ParseException e) {
                        throw new ValidationException(Arrays.asList(new ValidationError("Exception while formatting voucher date","Transaction failed")));
                }
        }
        
        public boolean isBillNumberGenerationAuto()
        {
                List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesDAO.getConfigValuesByModuleAndKey("EGF","Bill_Number_Geneartion_Auto");
                billNumberGenerationAuto=false;
                if(configValuesByModuleAndKey.size()>0)
                {
                        billNumberGenerationAuto=configValuesByModuleAndKey.get(0).getValue().equalsIgnoreCase("y")?true:false;
                }
                        return billNumberGenerationAuto;
        }
        
        
        public EisCommonService getEisCommonService() {
                return eisCommonService;
        }
        public void setEisCommonService(EisCommonService eisCommonService) {
                this.eisCommonService = eisCommonService;
        }
        public Position getPosition()throws EGOVRuntimeException
        {
                return  eisCommonService.getPositionByUserId(EgovThreadLocals.getUserId());
        }
        public CommonBean getCommonBean() {
                return commonBean;
        }
        
        public void setCommonBean(CommonBean commonBean) {
                this.commonBean = commonBean;
        }
        public List<Accountdetailtype> getAccountDetailTypeList() {
                return accountDetailTypeList;
        }
        
        public void setAccountDetailTypeList(List<Accountdetailtype> accountDetailTypeList) {
                this.accountDetailTypeList = accountDetailTypeList;
        }
        
        public List<EntityType> getEntitiesList() {
                return entitiesList;
        }
        
        public void setEntitiesList(List<EntityType> entitiesList) {
                this.entitiesList = entitiesList;
        }
        
        public List<VoucherDetails> getBillDetailslist() {
                return billDetailslist;
        }
        
        public void setBillDetailslist(List<VoucherDetails> billDetailslist) {
                this.billDetailslist = billDetailslist;
        }
        
        public List<VoucherDetails> getSubLedgerlist() {
                return subledgerlist;
        }
        
        public void setSubLedgerlist(List<VoucherDetails> subLedgerlist) {
                this.subledgerlist = subLedgerlist;
        }
        
        public EgovCommon getEgovCommon() {
                return egovCommon;
        }
        
        public void setEgovCommon(EgovCommon egovCommon) {
                this.egovCommon = egovCommon;
        }
        
        public List<CChartOfAccounts> getNetPayList() {
                return netPayList;
        }
        
        public void setNetPayList(List<CChartOfAccounts> netPayList) {
                this.netPayList = netPayList;
        }
        
        public CChartOfAccounts getDefaultNetPayCode() {
                return defaultNetPayCode;
        }
        
        public void setDefaultNetPayCode(CChartOfAccounts defaultNetPayCode) {
                this.defaultNetPayCode = defaultNetPayCode;
        }
        public List<VoucherDetails> getBillDetailsTableFinal() { 
                return billDetailsTableFinal;
        }
        
        public void setBillDetailsTableFinal(List<VoucherDetails> billDetailsTableFinal) {
                this.billDetailsTableFinal = billDetailsTableFinal;
        }
        
        public List<VoucherDetails> getBillDetailsTableNetFinal() {
                return billDetailsTableNetFinal;
        }
        
        public void setBillDetailsTableNetFinal(List<VoucherDetails> billDetailsTableNetFinal) {
                this.billDetailsTableNetFinal = billDetailsTableNetFinal;
        }
        
        public List<VoucherDetails> getBillDetailsTableCreditFinal() {
                return billDetailsTableCreditFinal;
        }
        
        public void setBillDetailsTableCreditFinal(List<VoucherDetails> billDetailsTableCreditFinal) {
                this.billDetailsTableCreditFinal = billDetailsTableCreditFinal;
        }
        public List<VoucherDetails> getBillDetailTableFinallist() {
                return billDetailTableFinallist;
        }
        
        public void setBillDetailTableFinallist(List<VoucherDetails> billDetailTableFinallist) {
                this.billDetailTableFinallist = billDetailTableFinallist;
        }
        
        public List<VoucherDetails> getBillDetailsTableNetFinalList() {
                return billDetailsTableNetFinalList;
        }
        
        public void setBillDetailsTableNetFinalList(List<VoucherDetails> billDetailsTableNetFinalList) {
                this.billDetailsTableNetFinalList = billDetailsTableNetFinalList;
        }
        
        public List<VoucherDetails> getBillDetailsTableCreditFinalist() {
                return billDetailsTableCreditFinalist;
        }
        
        public void setBillDetailsTableCreditFinalist(List<VoucherDetails> billDetailsTableCreditFinalist) {
                this.billDetailsTableCreditFinalist = billDetailsTableCreditFinalist;
        }
        public List<VoucherDetails> getSubledgerlist() {
                return subledgerlist;
        }
        public void setSubledgerlist(List<VoucherDetails> subledgerlist) {
                this.subledgerlist = subledgerlist;
        }
        public List<VoucherDetails> getBillDetailsTableSubledger() {
                return billDetailsTableSubledger;
        }
        public void setBillDetailsTableSubledger(List<VoucherDetails> billDetailsTableSubledger) {
                this.billDetailsTableSubledger = billDetailsTableSubledger;
        }       
        public CommonsService getCommonsService() {
                return commonsService;
        }
        public void setCommonsService(CommonsService commonsService) {
                this.commonsService = commonsService;
        }       

        public BillsService getBillsService() {
                return billsManager;
        }
        public void setBillsService(BillsService billsManager) {
                this.billsManager = billsManager;
        }       
        public List getBillSubTypes() {
                return persistenceService.findAllBy("from EgBillSubType where expenditureType=? order by name",FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT );
        }
        public PersistenceService<EgBillregister, Long> getBillRegisterService() {
                return billRegisterService;
        }
        public void setBillRegisterService(PersistenceService<EgBillregister, Long> billRegisterService) {
                this.billRegisterService = billRegisterService;
        }
        public Long getBillRegisterId() {
                return billRegisterId;
        }
        public void setBillRegisterId(Long billRegisterId) {
                this.billRegisterId = billRegisterId;
        }
        public String getDetailTypeIdandName() {
                return detailTypeIdandName;
        }
        public void setDetailTypeIdandName(String detailTypeIdandName) {
                this.detailTypeIdandName = detailTypeIdandName;
        }
        public SimpleWorkflowService<Cbill> getBillRegisterWorkflowService() {
                return billRegisterWorkflowService;
        }
        public void setBillRegisterWorkflowService(SimpleWorkflowService<Cbill> billRegisterWorkflowService) {
                this.billRegisterWorkflowService = billRegisterWorkflowService;
        }
        public PersistenceService<Cbill, Long> getCbillService() {
                return cbillService;
        }
        public void setCbillService(PersistenceService<Cbill, Long> cbillService) {
                this.cbillService = cbillService;
        }
        public String getButton() {
                return button;
        }
        public ScriptService getScriptExecutionService() {
                return scriptExecutionService;
        }

        public void setScriptExecutionService(ScriptService scriptExecutionService) {
                this.scriptExecutionService = scriptExecutionService;
        }

        public void setButton(String button) {
                this.button = button;
        }

        public SequenceGenerator getSequenceGenerator() {
                return sequenceGenerator;
        }

        public void setVoucherHelper(VoucherHelper voucherHelper) {
                this.voucherHelper = voucherHelper;
        }

        public void setSequenceGenerator(SequenceGenerator sequenceGenerator) {
                this.sequenceGenerator = sequenceGenerator;
        }

        public VoucherService getVoucherService() {
                return voucherService;
        }

        public void setVoucherService(VoucherService voucherService) {
                this.voucherService = voucherService;
        }

        public String getMode() {
                return mode;
        }

        public void setMode(String mode) {
                this.mode = mode;
        }

        public String getNextLevel() {
                return nextLevel;
        }

        public void setNextLevel(String nextLevel) {
                this.nextLevel = nextLevel;
        }

        public List<Action> getValidButtons() {
                return validButtons;
        }

        public void setValidButtons(List<Action> validButtons) {
                this.validButtons = validButtons;
        }

        public Integer getDepartmentId() {
                return departmentId;
        }

        public void setDepartmentId(Integer departmentId) {
                this.departmentId = departmentId;
        }

        public EisUtilService getEisUtilService() {
                return eisUtilService;
        }

        public void setEisUtilService(EisUtilService eisUtilService) {
                this.eisUtilService = eisUtilService;
        }
        
}