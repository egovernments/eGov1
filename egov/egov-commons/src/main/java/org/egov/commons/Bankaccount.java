/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

        1) All versions of this program, verbatim or modified must carry this 
           Legal Notice.

        2) Any misrepresentation of the origin of the material is prohibited. It 
           is required that all modified versions of this material be marked in 
           reasonable ways as different from the original version.

        3) This license does not grant any rights to any user of the program 
           with regards to rights under trademark law for use of the trade names 
           or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.commons;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.egov.commons.utils.BankAccountType;

public class Bankaccount implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        private Integer id;
        private Bankbranch bankbranch;
        private CChartOfAccounts chartofaccounts;
        private Fund fund;
        private String accountnumber;
        private String accounttype;
        private String narration;
        private int isactive;
        private Date created;
        private BigDecimal modifiedby;
        private Date lastmodified;
        private BigDecimal currentbalance;
        private String payTo;
        private BankAccountType type;
        private Set<EgSurrenderedCheques> egSurrenderedChequeses = new HashSet<EgSurrenderedCheques>(0);

        public Bankaccount() {
                //For hibernate to work
        }

        public Bankaccount(Bankbranch bankbranch, String accountnumber, String accounttype, int isactive, Date created, BigDecimal modifiedby, Date lastmodified, BigDecimal currentbalance, String payTo, BankAccountType type) {
                this.bankbranch = bankbranch;
                this.accountnumber = accountnumber;
                this.accounttype = accounttype;
                this.isactive = isactive;
                this.created = created;
                this.modifiedby = modifiedby;
                this.lastmodified = lastmodified;
                this.currentbalance = currentbalance;
                this.payTo = payTo;
                this.type = type;
        }

        public Bankaccount(Bankbranch bankbranch, CChartOfAccounts chartofaccounts, Fund fund, String accountnumber, String accounttype, String narration, int isactive, Date created, BigDecimal modifiedby, Date lastmodified, BigDecimal currentbalance,
                        String payTo, Set<EgSurrenderedCheques> egSurrenderedChequeses) {
                this.bankbranch = bankbranch;
                this.chartofaccounts = chartofaccounts;
                this.fund = fund;
                this.accountnumber = accountnumber;
                this.accounttype = accounttype;
                this.narration = narration;
                this.isactive = isactive;
                this.created = created;
                this.modifiedby = modifiedby;
                this.lastmodified = lastmodified;
                this.currentbalance = currentbalance;
                this.payTo = payTo;
                this.egSurrenderedChequeses = egSurrenderedChequeses;
        }

        public Integer getId() {
                return this.id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public Bankbranch getBankbranch() {
                return this.bankbranch;
        }

        public void setBankbranch(Bankbranch bankbranch) {
                this.bankbranch = bankbranch;
        }

        public CChartOfAccounts getChartofaccounts() {
                return this.chartofaccounts;
        }

        public void setChartofaccounts(CChartOfAccounts chartofaccounts) {
                this.chartofaccounts = chartofaccounts;
        }

        public Fund getFund() {
                return this.fund;
        }

        public void setFund(Fund fund) {
                this.fund = fund;
        }

        public String getAccountnumber() {
                return this.accountnumber;
        }

        public void setAccountnumber(String accountnumber) {
                this.accountnumber = accountnumber;
        }

        public String getAccounttype() {
                return this.accounttype;
        }

        public void setAccounttype(String accounttype) {
                this.accounttype = accounttype;
        }

        public String getNarration() {
                return this.narration;
        }

        public void setNarration(String narration) {
                this.narration = narration;
        }

        

        public Date getCreated() {
                return this.created;
        }

        public void setCreated(Date created) {
                this.created = created;
        }

        public BigDecimal getModifiedby() {
                return this.modifiedby;
        }

        public void setModifiedby(BigDecimal modifiedby) {
                this.modifiedby = modifiedby;
        }

        public Date getLastmodified() {
                return this.lastmodified;
        }

        public void setLastmodified(Date lastmodified) {
                this.lastmodified = lastmodified;
        }

        public BigDecimal getCurrentbalance() {
                return this.currentbalance;
        }

        public void setCurrentbalance(BigDecimal currentbalance) {
                this.currentbalance = currentbalance;
        }
        
        public BankAccountType getType() {
                return type;
        }

        public void setType(BankAccountType type) {
                this.type = type;
        }
        
        public Set<EgSurrenderedCheques> getEgSurrenderedChequeses() {
                return this.egSurrenderedChequeses;
        }

        public void setEgSurrenderedChequeses(Set<EgSurrenderedCheques> egSurrenderedChequeses) {
                this.egSurrenderedChequeses = egSurrenderedChequeses;
        }

        public String getPayTo() {
                return payTo;
        }

        public void setPayTo(String payTo) {
                this.payTo = payTo;
        }

    public int getIsactive() {
        return isactive;
    }

    public void setIsactive(int isactive) {
        this.isactive = isactive;
    }

}