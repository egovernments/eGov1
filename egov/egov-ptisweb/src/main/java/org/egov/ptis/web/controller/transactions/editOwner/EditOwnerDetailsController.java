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
package org.egov.ptis.web.controller.transactions.editOwner;

import javax.servlet.http.HttpServletRequest;

import org.egov.infra.persistence.entity.enums.Gender;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.service.property.PropertyPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/editowner/{assessmentNo}")
public class EditOwnerDetailsController {

    protected static final String OWNERDETAILS_FROM = "ownerdetails-form";

    @Autowired
    private BasicPropertyDAO basicPropertyDAO;
    
    @Autowired
    private PropertyPersistenceService basicPropertyService; 
   
    @ModelAttribute
    public Property propertyModel(@PathVariable final String assessmentNo) {
        BasicProperty basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(assessmentNo);
        PropertyImpl property = null;
        if (null != basicProperty) {
            property = (PropertyImpl) basicProperty.getProperty();
            basicProperty.setPropertyOwnerInfoProxy(basicProperty.getPropertyOwnerInfo());
        }
        
        return property;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String newForm(final Model model, @PathVariable String assessmentNo) {
        model.addAttribute("guardianRelationMap", PropertyTaxConstants.GUARDIAN_RELATION);
        model.addAttribute("gender", Gender.values());
        return OWNERDETAILS_FROM;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateOwnerDetails(@ModelAttribute final Property property, final RedirectAttributes redirectAttrs,final BindingResult errors,
            final Model model, final HttpServletRequest request,@RequestParam String doorNumber) {
        
        basicPropertyService.updateOwners(property, property.getBasicProperty(), doorNumber);
        return OWNERDETAILS_FROM;
    }

}
