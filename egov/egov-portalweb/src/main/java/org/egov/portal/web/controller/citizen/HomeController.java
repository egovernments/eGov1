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
package org.egov.portal.web.controller.citizen;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.egov.infra.admin.master.entity.User;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.portal.entity.CitizenInbox;
import org.egov.portal.service.CitizenInboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

	@Autowired
	private CitizenInboxService citizenInboxService;

	@Autowired
	private SecurityUtils securityUtils;

	@RequestMapping(method = RequestMethod.GET)
	public String LoginForm(final HttpServletRequest request, final HttpSession session, final ModelMap modelData) {
		return setupHomePage(request, session, modelData);

	}
	
	@RequestMapping(value = "/refreshInbox", method = RequestMethod.GET)
    public @ResponseBody Integer refreshInbox(@RequestParam Long citizenInboxId) {
		CitizenInbox citizenInbox = citizenInboxService.getInboxMessageById(citizenInboxId);
		citizenInbox.setRead(true);
		citizenInboxService.updateMessage(citizenInbox);
		return citizenInboxService.findUnreadMessagesCount(securityUtils.getCurrentUser());
    }

	private String setupHomePage(final HttpServletRequest request, final HttpSession session, final ModelMap modelData) {
		final User user = securityUtils.getCurrentUser();
		modelData.addAttribute("userName", user.getName());
		modelData.addAttribute("unreadMessageCount", getUnreadMessageCount());
		modelData.addAttribute("inboxMessages", getAllInboxMessages());
		modelData.addAttribute("myAccountMessages", getMyAccountMessages());
		modelData.addAttribute("cityLogo", session.getAttribute("citylogo"));
        modelData.addAttribute("cityName", session.getAttribute("cityname"));
        modelData.addAttribute("userName", user.getName() == null ? "Anonymous" : user.getName());
		return "citizen-home";
	}

	private List<CitizenInbox> getMyAccountMessages() {
		return citizenInboxService.findMyAccountMessages(securityUtils.getCurrentUser());
	}

	private List<CitizenInbox> getAllInboxMessages() {
		return citizenInboxService.findAllInboxMessage(securityUtils.getCurrentUser());
	}

	private Integer getUnreadMessageCount() {
		return citizenInboxService.findUnreadMessagesCount(securityUtils.getCurrentUser());
	}

}
