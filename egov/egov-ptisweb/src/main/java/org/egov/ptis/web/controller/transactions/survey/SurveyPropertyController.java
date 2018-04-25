package org.egov.ptis.web.controller.transactions.survey;

import static org.egov.ptis.constants.PropertyTaxConstants.LOCALITY;
import static org.egov.ptis.constants.PropertyTaxConstants.LOCATION_HIERARCHY_TYPE;
import static org.egov.ptis.constants.PropertyTaxConstants.REVENUE_HIERARCHY_TYPE;
import static org.egov.ptis.constants.PropertyTaxConstants.SURVEY_APPLICATION_TYPES;
import static org.egov.ptis.constants.PropertyTaxConstants.WARD;

import java.util.List;
import java.util.Map;

import org.egov.eis.service.JurisdictionService;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.entity.property.survey.SearchSurveyPropertyAdaptor;
import org.egov.ptis.domain.entity.property.survey.SearchSurveyRequest;
import org.egov.ptis.domain.service.survey.SurveyApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping(value = "/survey/properties")
public class SurveyPropertyController {

    @Autowired
    private BoundaryService boundaryService;

    @Autowired
    private SurveyApplicationService surveyApplicationService;

    @Autowired
    private SearchSurveyPropertyAdaptor searchSurveyPropertyAdaptor;

    @Autowired
    private SecurityUtils securityUtils;
    
    @Autowired
    private JurisdictionService JurisdictionService;

    @ModelAttribute("applicationTypes")
    public Map<String, String> getApplicationTypes() {
        return SURVEY_APPLICATION_TYPES;
    }

    @ModelAttribute("localitylist")
    public List<Boundary> localities() {
        return boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(LOCALITY, LOCATION_HIERARCHY_TYPE);
    }

    @ModelAttribute("electionwardlist")
    public List<Boundary> electionWards() {
        return JurisdictionService.getEmployeeJuridictions(securityUtils.getCurrentUser().getId());
    }

    @RequestMapping(value = "/searchform", method = RequestMethod.GET)
    public String search(final Model model) {
        model.addAttribute("surveyApplication", new SearchSurveyRequest());
        return "surveyApplication-form";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String searchSurveyApplications(@ModelAttribute final SearchSurveyRequest searchSurveyRequest) {
        List<PropertyImpl> propList = surveyApplicationService.searchSurveyProperties(searchSurveyRequest);
        return new StringBuilder("{ \"data\":").append(toSearchPropertyeJson(propList))
                .append("}").toString();

    }

    public Object toSearchPropertyeJson(final Object object) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.registerTypeAdapter(PropertyImpl.class, searchSurveyPropertyAdaptor).create();
        return gson.toJson(object);
    }

    @RequestMapping(value = "/updateworkflow", method = RequestMethod.POST)
    public String update(@RequestParam String[] applicationNumbersArray, final Model model) {
        try {
            User user = securityUtils.getCurrentUser();
            for (String applicationNo : applicationNumbersArray) {
                if (surveyApplicationService.updateWorkflow(applicationNo, user))
                    model.addAttribute("successMessage",
                            "Property workflow updated and application : " + applicationNo + " is moved to user : "
                                    + user.getName() + " inbox");
            }

        } catch (Exception e) {
            throw new ApplicationRuntimeException("Error occured while updating survey property application:" + e.getMessage(), e);
        }
        return "surveyApplication-success";
    }

}
