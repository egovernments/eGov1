package org.egov.web.actions.brs;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.infra.web.struts.actions.SearchFormAction;
import org.egov.infra.web.utils.EgovPaginatedList;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.search.SearchQuerySQL;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.services.instrument.InstrumentService;
import org.egov.services.receipt.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;

import com.exilant.eGov.src.domain.BankBranch;
import com.exilant.exility.common.TaskFailedException;

@ParentPackage("egov")
@Results({
    @Result(name = DishonoredChequeAction.SEARCH, location = "dishonoredCheque-search.jsp"),
    @Result(name = DishonoredChequeAction.SUCCESS, location = "dishonoredCheque-success.jsp")
   })
public class DishonoredChequeAction extends SearchFormAction {

	private static final long serialVersionUID = 1998083631926900402L;
	public static final String SEARCH = "search";
    private static final Logger LOGGER = Logger.getLogger(DishonoredChequeAction.class);
    protected DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private String bankBranchId;
    private Long accountCodes;
    private String instrumentMode;
    private String chequeNo;
    private Date chqDDDate;
    private EgovPaginatedList paginatedList;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private InstrumentService instrumentService;
    private String installmentIds;
    protected List<DishonoredChequeBean> dishonoredChequeDisplayList = new ArrayList<DishonoredChequeBean>();
    @Override
    public Object getModel() {
        // TODO Auto-generated method stub
        return null;
    }
   
    public void prepare()
    {
        super.prepare();
        addDropdownData("bankBranchList", persistenceService.findAllBy("select bb from Bankbranch bb where bb.isactive=1 order by bb.bank.name"));   
        final AjaxDishonoredAction ajaxDishonoredAction = new AjaxDishonoredAction();
        ajaxDishonoredAction.setPersistenceService(getPersistenceService());
        populateAccountCodes(ajaxDishonoredAction);
    }
    
    public List getBankBranch(){
        BankBranch bb=new BankBranch();
        try{
            return (List) bb.getBankBranch();
        }catch(TaskFailedException tf){
            LOGGER.error(tf. getMessage());
        }
        return null;
    }
    
    private void populateAccountCodes(final AjaxDishonoredAction ajaxDishonoredAction) {
        if (bankBranchId != null && bankBranchId != "-1" && bankBranchId!="") {
            ajaxDishonoredAction.setBankBranchId(bankBranchId);
            ajaxDishonoredAction.populateAccountCodes();
            addDropdownData("accountCodeList", ajaxDishonoredAction.getBankAccountList());
        } else
            addDropdownData("accountCodeList", Collections.emptyList());
    }
    
    @Actions({
        @Action(value="/brs/dishonoredCheque-search")
        })
    public String show(){
        return SEARCH;
    }
    
    @Action(value = "/brs/dishonoredCheque-list")
    public String list() throws Exception {
        setPageSize(30);
        super.search();
        prepareResults();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("EBConsumerAction | list | End");
        return SEARCH;
    }
    
    @Override
    public SearchQuery prepareQuery(final String sortField, final String sortOrder) {
       
        Long bankId = null;
        if (!bankBranchId.equals("-1") && bankBranchId != null && bankBranchId!=""){
            String id[] = bankBranchId.split("-");
            bankId = Long.parseLong(id[0]);
        }
        String searchQuery=receiptService.getReceiptHeaderforDishonor(instrumentMode,accountCodes,bankId,chequeNo,chqDDDate.toString());
        String srchQry = "select rpt.id as receiptheaderid,ih.id as instrumentheaderid,rpt.receiptnumber as receiptnumber,rpt.receiptdate as receiptdate,ih.instrumentnumber as instrumentnumber,"
        		+ "ih.instrumentdate as instrumentdate,ih.instrumentamount as instrumentamount,b.name as bankname,ba.accountnumber as accountnumber,ih.payto as payto,status.description as description "+searchQuery+" ORDER BY rpt.receiptnumber, rpt.receiptdate ";
        String countQry = "select count(distinct rpt) "+searchQuery+"";
        return new SearchQuerySQL(srchQry,countQry, null);
      
    }
    
    @Action(value = "/brs/dishonoredCheque-dishonorCheque")
    public String dishonorCheque() throws Exception {
        final String installmentIdsStr[] = installmentIds.split(",");
            for (final String installmentIdStr : installmentIdsStr) {
                InstrumentHeader ih= new InstrumentHeader();
                 ih= (InstrumentHeader)getPersistenceService().find("from InstrumentHeader where id=?",Long.valueOf(installmentIdStr));
                 instrumentService.cancelInstrument(ih);
            }
        return SUCCESS;
    }
private void prepareResults() {
		
		LOGGER.debug("Entering into prepareResults");
		paginatedList = (EgovPaginatedList) searchResult;
		List<Object[]> list = paginatedList.getList();
		
		for(Object[] object : list) {
			DishonoredChequeBean chequeBean = new DishonoredChequeBean();
			chequeBean.setReceiptHeaderid(getLongValue(object[0]));
			chequeBean.setInstrumentHeaderid(getLongValue(object[1]));
			chequeBean.setReceiptNumber(getStringValue(object[2]));
			chequeBean.setReceiptDate(getDateValue(object[3]));
			chequeBean.setInstrumentNumber(getStringValue(object[4]));
			chequeBean.setInstrumentDate(getDateValue(object[5]));
			chequeBean.setInstrumentAmount(getBigDecimalValue(object[6]));
			chequeBean.setBankName(getStringValue(object[7]));
			chequeBean.setAccountNumber(getStringValue(object[8]));
			chequeBean.setPayTo(getStringValue(object[9]));
			chequeBean.setDescription(getStringValue(object[10]));
			
			dishonoredChequeDisplayList.add(chequeBean);
		}
		paginatedList.setList(dishonoredChequeDisplayList);
		LOGGER.debug("Exiting from prepareResults");
	}
	protected String getStringValue(Object object) {
		return object != null?object.toString():"";
	}
	protected String getDateValue(Object object) {
	
		return object != null?formatter.format((Date) object):"";
	}
	protected Long getLongValue(Object object) {
		
		return object != null?Long.valueOf(object.toString()):null;
	}
	private BigDecimal getBigDecimalValue(Object object) {
		return object!= null? new BigDecimal(object.toString()).setScale(2):BigDecimal.ZERO.setScale(2);
	}
    public String getBankBranchId() {
        return bankBranchId;
    }

    public void setBankBranchId(String bankBranchId) {
        this.bankBranchId = bankBranchId;
    }

    public Long getAccountCodes() {
        return accountCodes;
    }

    public void setAccountCodes(Long accountCodes) {
        this.accountCodes = accountCodes;
    }

    public String getInstrumentMode() {
        return instrumentMode;
    }

    public void setInstrumentMode(String instrumentMode) {
        this.instrumentMode = instrumentMode;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public Date getChqDDDate() {
        return chqDDDate;
    }

    public void setChqDDDate(Date chqDDDate) {
        this.chqDDDate = chqDDDate;
    }

    public String getInstallmentIds() {
        return installmentIds;
    }

    public void setInstallmentIds(String installmentIds) {
        this.installmentIds = installmentIds;
    }

    public EgovPaginatedList getPaginatedList() {
		return paginatedList;
	}

	public void setPaginatedList(EgovPaginatedList paginatedList) {
		this.paginatedList = paginatedList;
	}

	public List<DishonoredChequeBean> getDishonoredChequeDisplayList() {
		return dishonoredChequeDisplayList;
	}

	public void setDishonoredChequeDisplayList(
			List<DishonoredChequeBean> dishonoredChequeDisplayList) {
		this.dishonoredChequeDisplayList = dishonoredChequeDisplayList;
	}

}
