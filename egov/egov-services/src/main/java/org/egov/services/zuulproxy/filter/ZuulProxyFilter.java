/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2017>  eGovernments Foundation
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
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.services.zuulproxy.filter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.security.authentication.SecureUser;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.services.config.properties.ServicesApplicationProperties;
import org.egov.services.wrapper.CustomRequestWrapper;
import org.egov.services.zuulproxy.models.Role;
import org.egov.services.zuulproxy.models.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class ZuulProxyFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(ZuulProxyFilter.class);
    // private static final String USER_INFO = "x-user-info";
    private static final String CLIENT_ID = "client.id";
    private static final String GET = "GET";
    private static final String FILESTORE_REGEX = "^/filestore/.*";
    private static final String REQUEST_INFO_FIELD_NAME = "RequestInfo";
    private static final String USER_INFO_FIELD_NAME = "userInfo";
    private ObjectMapper mapper = null;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null;
    }

    @Override
    public Object run() {

        mapper = new ObjectMapper();
        final RequestContext ctx = RequestContext.getCurrentContext();
        final HttpServletRequest request = ctx.getRequest();

        final WebApplicationContext springContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(request.getServletContext());

        final HashMap<String, String> zuulProxyRoutingUrls;
        final ServicesApplicationProperties applicationProperties = (ServicesApplicationProperties) springContext
                .getBean("servicesApplicationProperties");
        try {
            zuulProxyRoutingUrls = (HashMap<String, String>) applicationProperties
                    .zuulProxyRoutingUrls();
            if (log.isInfoEnabled())
                log.info("Zuul Proxy Routing Mapping Urls... " + zuulProxyRoutingUrls);
        } catch (final Exception e) {
            throw new ApplicationRuntimeException("Could not get valid routing url mapping for mirco services", e);
        }
        try {
            final URL requestURL = new URL(request.getRequestURL().toString());

            String endPointURI;
            if (requestURL.getPath().startsWith("/services"))
                endPointURI = requestURL.getPath().split("/services")[1];
            else
                endPointURI = requestURL.getPath();

            String mappingURL = "";
            for (final Entry<String, String> entry : zuulProxyRoutingUrls.entrySet()) {
                final String key = entry.getKey();
                if (endPointURI.contains(key)) {
                    mappingURL = entry.getValue();
                    break;
                }
            }
            if (log.isInfoEnabled())
                log.info(String.format("%s request to the url %s", request.getMethod(), request.getRequestURL().toString()));

            if (StringUtils.isNoneBlank(request.getQueryString()))
                endPointURI = endPointURI + "?" + request.getQueryString();

            final URL routedHost = new URL(mappingURL + endPointURI);
            ctx.setRouteHost(routedHost);
            ctx.set("requestURI", routedHost.getPath());
            final String userInfo = getUserInfo(request, springContext);
            if (shouldPutUserInfoOnHeaders(ctx))
                ctx.addZuulRequestHeader(USER_INFO_FIELD_NAME, userInfo);
            else
                appendUserInfoToRequestBody(ctx, userInfo);
        } catch (final MalformedURLException e) {
            throw new ApplicationRuntimeException("Could not form valid URL", e);
        } catch (final IOException ex) {
            ctx.setSendZuulResponse(false);
        }
        return null;
    }

    private boolean shouldPutUserInfoOnHeaders(final RequestContext ctx) {
        return Objects.equals(ctx.getRequest().getMethod().toUpperCase(), GET) ||
                ctx.getRequest().getRequestURI().matches(FILESTORE_REGEX);
    }

    @SuppressWarnings("unchecked")
    private void appendUserInfoToRequestBody(final RequestContext ctx, final String userInfo) throws IOException {
        final HashMap<String, Object> requestBody = getRequestBody(ctx);
        final HashMap<String, Object> requestInfo = (HashMap<String, Object>) requestBody.get(REQUEST_INFO_FIELD_NAME);
        requestInfo.put(USER_INFO_FIELD_NAME, userInfo);
        requestBody.put(REQUEST_INFO_FIELD_NAME, requestInfo);
        final CustomRequestWrapper requestWrapper = new CustomRequestWrapper(ctx.getRequest());
        requestWrapper.setPayload(mapper.writeValueAsString(requestBody));
        ctx.setRequest(requestWrapper);
    }

    private HashMap<String, Object> getRequestBody(final RequestContext ctx) throws IOException {
        final String payload = IOUtils.toString(ctx.getRequest().getInputStream());
        return mapper.readValue(payload, new TypeReference<HashMap<String, Object>>() {
        });
    }

    private String getUserInfo(final HttpServletRequest request, final WebApplicationContext springContext) {
        final HttpSession session = request.getSession();
        String userInfoJson = null;

        if (session.getAttribute(USER_INFO_FIELD_NAME) != null)
            userInfoJson = session.getAttribute(USER_INFO_FIELD_NAME).toString();

        if (log.isInfoEnabled())
            log.info("userInfo is from the session... " + userInfoJson);

        if (StringUtils.isBlank(userInfoJson)) {
            final UserService userService = (UserService) springContext.getBean("userService");
            final SecureUser userDetails = new SecureUser(
                    userService.getUserByUsername(request.getRemoteUser()));

            final User user = userDetails.getUser();

            final List<Role> roles = new ArrayList<Role>();
            userDetails.getUser().getRoles().forEach(authority -> roles.add(new Role(authority.getName())));

            final Environment environment = (Environment) springContext.getBean("environment");
            final String clientId = environment.getProperty(CLIENT_ID);

            String tenantId = ApplicationThreadLocals.getTenantID();
            if (StringUtils.isNoneBlank(clientId))
                tenantId = clientId + "." + tenantId;

            final UserInfo userInfo = new UserInfo(roles, userDetails.getUserId(), userDetails.getUsername(), user.getName(),
                    user.getEmailId(), user.getMobileNumber(), userDetails.getUserType().toString(),
                    tenantId);
            final ObjectMapper mapper = new ObjectMapper();
            try {
                userInfoJson = mapper.writeValueAsString(userInfo);
            } catch (final JsonProcessingException e) {
                throw new ApplicationRuntimeException("Could not convert object to json string", e);
            }
            if (log.isInfoEnabled())
                log.info("Read userInfo from the DB and set it to the session... " + userInfoJson);
            session.setAttribute(USER_INFO_FIELD_NAME, userInfoJson);
        }

        return userInfoJson;
    }

}
