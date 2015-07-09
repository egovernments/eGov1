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
package org.egov.infra.web.controller.common;

import org.apache.commons.lang.RandomStringUtils;
import org.egov.infra.aadhaar.contract.AadhaarInfo;
import org.egov.infra.web.rest.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/aadhaar")
public class AadhaarInfoController {

    @RequestMapping(value = "/{aadhaarNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> aadhaarInfo(@PathVariable final String aadhaarNo) {
        try {
            // TODO info has to be collected from uiadi server
            final AadhaarInfo aadhaarInfo = new AadhaarInfo();
            aadhaarInfo.setAadhaarNo(aadhaarNo);
            aadhaarInfo.setEmail(RandomStringUtils.randomAlphabetic(20) + "@gmail.com");
            aadhaarInfo.setMobile(RandomStringUtils.randomNumeric(9));
            aadhaarInfo.setOwnerName(RandomStringUtils.randomAlphabetic(5));
            return new ResponseEntity<AadhaarInfo>(aadhaarInfo, HttpStatus.OK);
        } catch (final Exception e) {

            return new ResponseEntity<ErrorResponse>(new ErrorResponse("INFRA-001",
                    "User detail not found in uidai server for aadhaar no : " + aadhaarNo, HttpStatus.NOT_FOUND),
                    HttpStatus.NOT_FOUND);
        }
    }
}
