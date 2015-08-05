/* eGov suite of products aim to improve the internal efficiency,transparency,
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
package org.egov.infra.config.properties;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = {
        "classpath:config/application-config.properties",
        "classpath:config/egov-erp-override.properties",
        "classpath:config/egov-erp-${user.name}.properties" }, ignoreResourceNotFound = true)
@Order(0)
public class ApplicationProperties {

    @Autowired
    private Environment environment;

    public String fileStoreBaseDir() {
        return environment.getProperty("filestore.base.dir");
    }

    public String filestoreServiceBeanName() {
        return environment.getProperty("filestoreservice.beanname");
    }

    public String defaultDatePattern() {
        return environment.getProperty("default.date.pattern");
    }

    public Integer mailPort() {
        return environment.getProperty("mail.port", Integer.class);
    }

    public String mailHost() {
        return environment.getProperty("mail.host");
    }

    public String mailProtocol() {
        return environment.getProperty("mail.protocol");
    }

    public String mailSenderUsername() {
        return environment.getProperty("mail.sender.username");
    }

    public String mailSenderPassword() {
        return environment.getProperty("mail.sender.password");
    }

    public String mailSMTPSAuth() {
        return environment.getProperty("mail.smtps.auth");
    }

    public String mailStartTLSEnabled() {
        return environment.getProperty("mail.smtps.starttls.enable");
    }

    public String mailSMTPSDebug() {
        return environment.getProperty("mail.smtps.debug");
    }

    public String smsProviderURL() {
        return environment.getProperty("sms.provider.url");
    }

    public String smsSenderUsername() {
        return environment.getProperty("sms.sender.username");
    }

    public String smsSenderPassword() {
        return environment.getProperty("sms.sender.password");
    }

    public String smsSender() {
        return environment.getProperty("sms.sender");
    }

    public String[] commonMessageFiles() {
        return environment.getProperty("common.properties.files").split(",");
    }

    public boolean devMode() {
        return environment.getProperty("dev.mode", Boolean.class);
    }

    public boolean emailEnabled() {
        return environment.getProperty("mail.enabled", Boolean.class);
    }

    public boolean smsEnabled() {
        return environment.getProperty("sms.enabled", Boolean.class);
    }

    public Integer userPasswordExpiryInDays() {
        return environment.getProperty("user.pwd.expiry.days", Integer.class);
    }

    public List<String> smsErrorCodes() {
        return Arrays.asList(environment.getProperty("sms.error.codes").split(","));
    }

    public String smsResponseMessageForCode(final String errorCode) {
        return environment.getProperty(errorCode, "No Message");
    }

    public boolean multiTenancyEnabled() {
        return environment.getProperty("multitenancy.enabled", Boolean.class);
    }

    public String getProperty(final String propCode) {
        return environment.getProperty(propCode, "");
    }
}
