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
package org.egov.web.actions.voucher;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.service.CommonsService;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infra.admin.master.entity.AppConfig;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.script.entity.Script;
import org.egov.model.bills.EgBillregister;
import org.egov.model.voucher.VoucherTypeBean;
import org.egov.pims.service.EisUtilService;
import org.egov.services.bills.BillsService;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.Constants;
import org.egov.utils.VoucherHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author manoranjan
 *
 */
@Transactional(readOnly=true)
public class BillVoucherAction extends BaseVoucherAction {

        
        private static final long serialVersionUID = 1L;
        private static final Logger     LOGGER  = Logger.getLogger(BillVoucherAction.class);
        private static BillsService billsMgr ; 
        @Autowired
        private EgwStatusHibernateDAO egwStatusDAO;
        private EisCommonService eisCommonService;
        private VoucherService voucherService;
        private String expType;
        private String billNumber;
        private List<EgBillregister> preApprovedVoucherList;
        private VoucherTypeBean voucherTypeBean;
        private EisUtilService eisUtilService;
        private VoucherHelper voucherHelper;
        public VoucherTypeBean getVoucherTypeBean() {
                return voucherTypeBean;
        }
        public void setVoucherTypeBean(VoucherTypeBean voucherTypeBean) {
                this.voucherTypeBean = voucherTypeBean;
        }
        @Override
        public void prepare() {
                super.prepare();
        //  If the department is mandatory show the logged in users assigned department only.
                if(mandatoryFields.contains("department")){
                        addDropdownData("departmentList", voucherHelper.getAllAssgnDeptforUser());
                }
        }
@Action(value="/voucher/billVoucher-newform")
        public String newform(){
                if(getValidActions("designation").size()==0)
                {
                        addActionError(getText("pjv.designation.notmatching"));
                        
                }else if(null == voucherHeader.getVouchermis().getDepartmentid() ){
                        voucherHeader.getVouchermis().setDepartmentid(voucherService.getCurrentDepartment());
                }
                if(LOGGER.isDebugEnabled())     LOGGER.debug("BillVoucherAction | newform | START");
                List<String> listBillReg= billsMgr.getDistExpType();
                Map<String, String> expTypeList = new LinkedHashMap<String, String>();
                for (String expType : listBillReg) {
                        expTypeList.put(expType, expType);
                }
                
                addDropdownData("expTypeList", listBillReg);
                return NEW;
        }
    @SuppressWarnings("unchecked")
   
        public String lists() throws ValidationException{
        StringBuffer query = new StringBuffer(300);
        if(LOGGER.isDebugEnabled())     LOGGER.debug("Expenditure Type selected :="+ expType);
        String statusid = getApprovalStatusForBills();
                query.append("from EgBillregister br where br.status.id in(").append(statusid).append(")and ( br.egBillregistermis.voucherHeader is null or br.egBillregistermis.voucherHeader in (from CVoucherHeader vh where vh.status =? ))");
                if(null != billNumber && StringUtils.isNotEmpty(billNumber)){
                        query.append(" and br.billnumber='").append(billNumber).append("'");
                }
                
                try {
                        if(null != voucherHeader.getVouchermis().getDepartmentid()){
                        query.append(" and br.egBillregistermis.egDepartment.id=").append(voucherHeader.getVouchermis().getDepartmentid().getId());
                        
                }if(null != voucherTypeBean.getVoucherDateFrom() && StringUtils.isNotEmpty(voucherTypeBean.getVoucherDateFrom())){
                        
                        query.append(" and br.billdate>='").append(Constants.DDMMYYYYFORMAT1.format(Constants.DDMMYYYYFORMAT2.
                                                parse(voucherTypeBean.getVoucherDateFrom()))).append("'");
                }
                if(null != voucherTypeBean.getVoucherDateTo() && StringUtils.isNotEmpty(voucherTypeBean.getVoucherDateTo())){
                        query.append(" and br.billdate<='").append(Constants.DDMMYYYYFORMAT1.format(Constants.DDMMYYYYFORMAT2.
                                                parse(voucherTypeBean.getVoucherDateTo()))).append("'");
                }
                preApprovedVoucherList = (List<EgBillregister>) persistenceService.findAllBy(query.toString(),4);
                        } catch (ParseException e) {
                                throw new ValidationException(Arrays.asList(new ValidationError("not a valid date", "not a valid date")));
                        }
        return newform();
    }
        public List<Action> getValidActions(String purpose) {
                List<Action> validButtons = new ArrayList<Action>();
                Script validScript = (Script) getPersistenceService().findAllByNamedQuery(Script.BY_NAME,"pjv.validbuttons").get(0);
                //script service 
                List<String> list = null;/*(List<String>) validScript.eval(Script.createContext("eisCommonServiceBean", eisCommonService,"userId",Integer.valueOf(EgovThreadLocals.getUserId().trim()),"date",new Date(),"purpose",purpose));*/
                for(Object s:list) 
                {
                        if("invalid".equals(s))
                                break;
                        Action action = (Action) getPersistenceService().find(" from org.egov.infstr.workflow.Action where type='CVoucherHeader' and name=?", s.toString());
                        validButtons.add(action);
                }
                return validButtons;
        }
        private String getApprovalStatusForBills(){
                  String statusid="";
                  AppConfig appConfig = null;
                  String  query = "from AppConfig where key_name = '"+expType+"BillApprovalStatus'";
                  if(LOGGER.isDebugEnabled())     LOGGER.debug(">>>>>Query=:"+ query);
                  appConfig = (AppConfig) persistenceService.find(query);
                 if(LOGGER.isDebugEnabled())     LOGGER.debug("Total app config values = "+ appConfig.getAppDataValues().size());
                 if(appConfig.getAppDataValues().size() == 0){
                         
                         throw new ValidationException(Arrays.asList(new ValidationError("Status for bill approval",
                                "App Config value is missing for exp type :"+ expType)));
                 }
                for (AppConfigValues appConfigVal : appConfig.getAppDataValues()) {
                        
                        String configvalue = appConfigVal.getValue();
                        EgwStatus egwstatus = egwStatusDAO.getStatusByModuleAndCode(configvalue.substring(0, configvalue.indexOf("|"))
                                                                              , configvalue.substring(configvalue.indexOf("|")+1));
                        if(null == egwstatus || null == egwstatus.getId()){
                                throw new ValidationException(Arrays.asList(new ValidationError("Status for bill approval",
                " status for "+expType + " approval is not present in Egwstatus for app config value : "+configvalue)));
                        }else {
                                statusid = statusid.isEmpty()?egwstatus.getId().toString():statusid+","+ egwstatus.getId();
                        }
                }       
                
                return statusid;
                
        }
        public EisCommonService getEisCommonService() {
                return eisCommonService;
        }

        public void setEisCommonService(EisCommonService eisCommonService) {
                this.eisCommonService = eisCommonService;
        }
        public VoucherService getVoucherService() {
                return voucherService;
        }

        public void setVoucherService(VoucherService voucherService) {
                this.voucherService = voucherService;
        }

        public String getExpType() {
                return expType;
        }
        public void setExpType(String expType) {
                this.expType = expType;
        }

        public List<EgBillregister> getPreApprovedVoucherList() {
                return preApprovedVoucherList;
        }
        public void setPreApprovedVoucherList(
                        List<EgBillregister> preApprovedVoucherList) {
                this.preApprovedVoucherList = preApprovedVoucherList;
        }
        public String getBillNumber() {
                return billNumber;
        }
        public void setBillNumber(String billNumber) {
                this.billNumber = billNumber;
        }
        public EisUtilService getEisUtilService() {
                return eisUtilService;
        }
        public void setEisUtilService(EisUtilService eisUtilService) {
                this.eisUtilService = eisUtilService;
        }
        public void setVoucherHelper(VoucherHelper voucherHelper) {
                this.voucherHelper = voucherHelper;
        }
}