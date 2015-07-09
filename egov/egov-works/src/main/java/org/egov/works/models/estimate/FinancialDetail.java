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
package org.egov.works.models.estimate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.infstr.ValidationError;
import org.egov.infstr.models.BaseModel;
import org.egov.model.budget.BudgetGroup;

public class FinancialDetail extends BaseModel {

    private static final long serialVersionUID = 9007144591203838584L;
    
    private AbstractEstimate abstractEstimate;
    private Fund fund;
    private CFunction function;
    private Functionary functionary;
    private Scheme scheme;
    private SubScheme subScheme;
    private BudgetGroup budgetGroup;   
    private CChartOfAccounts coa;
    private transient String apprYear;// values will be previous or running

    @Valid
    private List<FinancingSource> financingSources = new LinkedList<FinancingSource>();

    public FinancialDetail() {
    }

    public FinancialDetail(final AbstractEstimate estimate, final Fund fund, final BudgetGroup budgetGroup) {
        abstractEstimate = estimate;
        this.fund = fund;
        this.budgetGroup = budgetGroup;
    }

    // for testing
    public FinancialDetail(final Fund fund, final CFunction function, final Functionary functionary) {
        this.function = function;
        this.fund = fund;
        this.functionary = functionary;
    }

    public AbstractEstimate getAbstractEstimate() {
        return abstractEstimate;
    }

    public void setAbstractEstimate(final AbstractEstimate estimate) {
        abstractEstimate = estimate;
    }

    public BudgetGroup getBudgetGroup() {
        return budgetGroup;
    }

    public void setBudgetGroup(final BudgetGroup budgetGroup) {
        this.budgetGroup = budgetGroup;
    }

    @NotNull(message = "financial.fund.null")
    public Fund getFund() {
        return fund;
    }

    public void setFund(final Fund fund) {
        this.fund = fund;
    }

    public CFunction getFunction() {
        return function;
    }

    public void setFunction(final CFunction function) {
        this.function = function;
    }

    public Functionary getFunctionary() {
        return functionary;
    }

    public void setFunctionary(final Functionary functionary) {
        this.functionary = functionary;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(final Scheme scheme) {
        this.scheme = scheme;
    }

    public SubScheme getSubScheme() {
        return subScheme;
    }

    public void setSubScheme(final SubScheme subScheme) {
        this.subScheme = subScheme;
    }

    public List<FinancingSource> getFinancingSources() {
        return financingSources;
    }

    public void setFinancingSources(final List<FinancingSource> financingSources) {
        this.financingSources = financingSources;
    }

    public void addFinancingSource(final FinancingSource financingSource) {
        financingSources.add(financingSource);
    }

    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> validationErrors = new ArrayList<ValidationError>();

        double total = 0;
        boolean finSourceError = false;

        if (fund == null)
            validationErrors.add(new ValidationError("fund_null", "financial.fund.null"));

        if (financingSources == null || financingSources.isEmpty())
            validationErrors.add(new ValidationError("financingsource_null", "financingsource.null"));

        final int errorCnt = validationErrors.size();

        if (financingSources != null)
            for (final FinancingSource financingSource : financingSources) {
                if (!finSourceError)
                    validationErrors.addAll(financingSource.validate());

                // if one financial source row has invalid values, same check
                // need not be done
                // for the remaining objects, and duplicate error messages can
                // be avoided
                if (!finSourceError && errorCnt < validationErrors.size())
                    finSourceError = true;

                total += financingSource.getPercentage();
            }

        if (financingSources != null && !financingSources.isEmpty() && total != 100)
            validationErrors.add(new ValidationError("percentageequalto100",
                    "financingsource.percentage.percentageequalto100"));

        return validationErrors;
    }

    /**
     * This method is invoked from the script to generate the budget
     * appropriation number
     *
     * @return an instance of <code>FinancingSource</code> having the maximum of
     *         the financial sources selected
     */
    public FinancingSource getMaxFinancingSource() {
        double max = 0.0;
        FinancingSource maxFinSource = null;
        for (final FinancingSource finSource : financingSources)
            if (finSource.getPercentage() > max) {
                max = finSource.getPercentage();
                maxFinSource = finSource;
            }

        return maxFinSource;
    }

    public CChartOfAccounts getCoa() {
        return coa;
    }

    public void setCoa(final CChartOfAccounts coa) {
        this.coa = coa;
    }

    public String getApprYear() {
        return apprYear;
    }

    public void setApprYear(final String apprYear) {
        this.apprYear = apprYear;
    }
}
