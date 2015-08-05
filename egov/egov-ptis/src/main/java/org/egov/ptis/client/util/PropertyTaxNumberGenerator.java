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
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org
 ******************************************************************************/
package org.egov.ptis.client.util;

import java.util.Date;

import org.egov.commons.dao.InstallmentDao;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.persistence.utils.SequenceNumberGenerator;
import org.egov.infra.utils.ApplicationNumberGenerator;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.entity.property.PropertyID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PropertyTaxNumberGenerator {
    private static final String SEQ_EGPT_ASSESSMENT_NUMBER = "seq_egpt_assessment_number";
    private static final String SEQ_EGPT_NOTICE_NUMBER = "SEQ_EGPT_NOTICE_NUMBER";
    private static final String SEQ_EG_BILL = "SEQ_EG_BILL";
    @Autowired
    private SequenceNumberGenerator sequenceNumberGenerator;
    @Autowired
    private ModuleService moduleDao;
    @Autowired
    private InstallmentDao installmentDao;
    @Autowired
    private CityService cityService;

    @Autowired
    private ApplicationNumberGenerator applicationNumberGenerator;

    public String generateNoticeNumber(final String noticeType) {
        final StringBuffer noticeNo = new StringBuffer();
        try {
            if (org.apache.commons.lang.StringUtils.isNotBlank(noticeType))
                if (noticeType.equalsIgnoreCase(PropertyTaxConstants.NOTICE_TYPE_SPECIAL_NOTICE)) {
                    final String cityCode = EgovThreadLocals.getCityCode();
                    noticeNo.append(cityCode);
                    final String index = sequenceNumberGenerator.getNextSequence(SEQ_EGPT_NOTICE_NUMBER).toString();
                    noticeNo.append(org.apache.commons.lang.StringUtils.leftPad(index, 6, "0"));
                }
        } catch (final Exception e) {
            throw new EGOVRuntimeException("Exception : " + e.getMessage(), e);
        }

        return noticeNo.toString();
    }

    public String generateBillNumber(final String wardNo) {
        final StringBuffer billNo = new StringBuffer();
        final Module module = moduleDao.getModuleByName(PropertyTaxConstants.PTMODULENAME);
        installmentDao.getInsatllmentByModuleForGivenDate(module, new Date());
        // FIX ME
        /*
         * String index = sequenceNumberGenerator.getNextNumberWithFormat( BILLGEN_SEQNAME_PREFIX + wardNo, 7, '0',
         * Long.valueOf(1)) .getFormattedNumber(); billNo.append(wardNo); billNo.append("/"); billNo.append(index);
         * billNo.append("/"); billNo.append(finYear.getDescription());
         */
        return billNo.toString();
    }

    public String generateManualBillNumber(final PropertyID propertyID) {
        final StringBuffer billNo = new StringBuffer();
        try {
            // reading from service to support bulkbillgeneration through schedular
            final String cityCode = cityService.findAll().get(0).getCode();
            billNo.append(cityCode);
            final String bill = sequenceNumberGenerator.getNextSequence(SEQ_EG_BILL).toString();
            billNo.append(org.apache.commons.lang.StringUtils.leftPad(bill, 6, "0"));
        } catch (final Exception e) {
            throw new EGOVRuntimeException("Exception : " + e.getMessage(), e);
        }
        return billNo.toString();
    }

    public String generateRecoveryNotice(final String noticeType) {
        final StringBuffer noticeNo = new StringBuffer();
        return noticeNo.toString();
    }

    public String generateAssessmentNumber() {

        final StringBuffer indexNum = new StringBuffer();
        try {
            final String cityCode = EgovThreadLocals.getCityCode();
            indexNum.append(cityCode);
            final String index = sequenceNumberGenerator.getNextSequence(SEQ_EGPT_ASSESSMENT_NUMBER).toString();
            indexNum.append(org.apache.commons.lang.StringUtils.leftPad(index, 6, "0"));
        } catch (final Exception e) {
            throw new EGOVRuntimeException("Exception : " + e.getMessage(), e);
        }
        return indexNum.toString();
    }

    public String generateMemoNumber() {
        return "";
    }

    public String getRejectionLetterSerialNum() {
        return "";
    }

    public String generateUnitIdentifierPrefix() {
        /*
         * return sequenceNumberGenerator .getNextNumber(UNIT_IDENTIFIER_SEQ_STR, 1).getFormattedNumber();
         */
        return null;
    }

    public void setSequenceNumberGenerator(final SequenceNumberGenerator sequenceNumberGenerator) {
        this.sequenceNumberGenerator = sequenceNumberGenerator;
    }

}