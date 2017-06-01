/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
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
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.infra.microservice.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.RoleService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.properties.ApplicationProperties;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.contract.CreateUserRequest;
import org.egov.infra.microservice.contract.UserDetailResponse;
import org.egov.infra.microservice.contract.UserRequest;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.microservice.models.UserInfo;
import org.egov.infra.persistence.entity.enums.UserType;
import org.egov.infra.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MicroserviceUtils {

    private static final Logger LOGGER = Logger.getLogger(MicroserviceUtils.class);
    private static final String CLIENT_ID = "client.id";
    private static final String ROLE_CITIZEN = "CITIZEN";

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private Environment environment;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationProperties applicationProperties;

    public RequestInfo createRequestInfo() {
        final RequestInfo requestInfo = new RequestInfo();
        requestInfo.setApiId("apiId");
        requestInfo.setVer("ver");
        requestInfo.setTs(new Date());
        requestInfo.setUserInfo(getUserInfo());
        return requestInfo;
    }

    public UserInfo getUserInfo() {
        final User user = securityUtils.getCurrentUser();
        final List<org.egov.infra.microservice.models.RoleInfo> roles = new ArrayList<org.egov.infra.microservice.models.RoleInfo>();
        user.getRoles().forEach(authority -> roles.add(new org.egov.infra.microservice.models.RoleInfo(authority.getName())));

        return new UserInfo(roles, user.getId(), user.getUsername(), user.getName(),
                user.getEmailId(), user.getMobileNumber(), user.getType().toString(),
                getTanentId());
    }

    public String getTanentId() {
        final String clientId = environment.getProperty(CLIENT_ID);
        String tenantId = ApplicationThreadLocals.getTenantID();
        if (StringUtils.isNotBlank(clientId)) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(clientId).append('.').append(tenantId);
            tenantId = stringBuilder.toString();
        }
        return tenantId;
    }

    public void createUserMicroservice(final User user) {
        final String createUserServiceUrl = applicationProperties.getCreateUserServiceUrl();
        if (StringUtils.isNotBlank(createUserServiceUrl)) {

            if (user.getRoles().isEmpty() && user.getType().equals(UserType.CITIZEN))
                user.addRole(roleService.getRoleByName(ROLE_CITIZEN));

            final CreateUserRequest createUserRequest = new CreateUserRequest();
            final UserRequest userRequest = new UserRequest(user, getTanentId());
            createUserRequest.setUserRequest(userRequest);
            createUserRequest.setRequestInfo(createRequestInfo());

            final RestTemplate restTemplate = new RestTemplate();
            try {
                restTemplate.postForObject(createUserServiceUrl, createUserRequest, UserDetailResponse.class);
            } catch (final Exception e) {
                final String errMsg = "Exception while creating User in microservice ";
                LOGGER.error(errMsg, e);
                throw new ApplicationRuntimeException(errMsg, e);
            }
        }
    }
}