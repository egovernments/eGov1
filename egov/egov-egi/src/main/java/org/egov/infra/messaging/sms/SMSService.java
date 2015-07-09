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
package org.egov.infra.messaging.sms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.egov.infra.config.properties.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSService.class);
    private static final String SENDERID_PARAM_NAME = "sms.sender.req.param.name";
    private static final String USERNAME_PARAM_NAME = "sms.sender.username.req.param.name";
    private static final String PASSWORD_PARAM_NAME = "sms.sender.password.req.param.name";
    private static final String DEST_MOBILENUM_PARAM_NAME = "sms.destination.mobile.req.param.name";
    private static final String DEST_MESSAGE_PARAM_NAME = "sms.message.req.param.name";

    @Autowired
    private ApplicationProperties applicationProperties;

    public boolean sendSMS(final String message, final String mobileNumber) {

        if (applicationProperties.smsEnabled()) {
            try {
                final HttpClient client = HttpClientBuilder.create().build();
                final HttpPost post = new HttpPost(applicationProperties.smsProviderURL());
                final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                urlParameters.add(new BasicNameValuePair(applicationProperties.getProperty(USERNAME_PARAM_NAME),
                        applicationProperties.smsSenderUsername()));
                urlParameters.add(new BasicNameValuePair(applicationProperties.getProperty(PASSWORD_PARAM_NAME),
                        applicationProperties.smsSenderPassword()));
                urlParameters.add(new BasicNameValuePair(applicationProperties.getProperty(SENDERID_PARAM_NAME),
                        applicationProperties.smsSender()));
                urlParameters
                        .add(new BasicNameValuePair(applicationProperties.getProperty(DEST_MOBILENUM_PARAM_NAME), mobileNumber));
                urlParameters.add(new BasicNameValuePair(applicationProperties.getProperty(DEST_MESSAGE_PARAM_NAME), message));
                if (StringUtils.isNotBlank(applicationProperties.getProperty("sms.extra.req.params"))) {
                    final String[] extraParms = applicationProperties.getProperty("sms.extra.req.params").split("&");
                    if (extraParms.length > 0)
                        for (final String extraParm : extraParms) {
                            final String[] paramNameValue = extraParm.split("=");
                            urlParameters.add(new BasicNameValuePair(paramNameValue[0], paramNameValue[1]));
                        }
                }
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
                final HttpResponse response = client.execute(post);
                final String responseCode = IOUtils.toString(response.getEntity().getContent());
                LOGGER.info("SMS sending completed with response code [{}] - [{}]", responseCode,
                        applicationProperties.smsResponseMessageForCode(responseCode));
                return applicationProperties.smsErrorCodes().parallelStream()
                        .noneMatch(errorCode -> responseCode.startsWith(errorCode));
            } catch (UnsupportedOperationException | IOException e) {
                LOGGER.error("Error occurred while sending SMS [%s]", e.getMessage());
                return false;
            }
        } else {
            return false;
        }

    }
}
