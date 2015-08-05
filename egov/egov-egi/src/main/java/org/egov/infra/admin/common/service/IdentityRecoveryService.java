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
package org.egov.infra.admin.common.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.egov.infra.admin.common.entity.IdentityRecovery;
import org.egov.infra.admin.common.repository.IdentityRecoveryRepository;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.messaging.MessagingService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IdentityRecoveryService {
    private static final String USER_PWD_RECOVERY_TMPLTE = "user.pwd.recovery";

    @Autowired
    private IdentityRecoveryRepository identityRecoveryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<IdentityRecovery> getByToken(final String token) {
        return Optional.ofNullable(identityRecoveryRepository.findByToken(token));
    }

    @Transactional
    public IdentityRecovery generate(final User user, final Date timeToExpire) {
        final IdentityRecovery identityRecovery = new IdentityRecovery();
        identityRecovery.setToken(UUID.randomUUID().toString());
        identityRecovery.setUser(user);
        identityRecovery.setExpiry(timeToExpire);
        return identityRecoveryRepository.save(identityRecovery);
    }
    
    @Transactional
    public boolean generateAndSendUserPasswordRecovery(final String identity, final String urlToSent) {
        final Optional<User> user = userService.checkUserWithIdentity(identity);
        if (user.isPresent()) {
            final IdentityRecovery identityRecovery = generate(user.get(), new DateTime().plusMinutes(5).toDate());
            messagingService.sendEmail(identityRecovery.getUser(), "Password Recovery", USER_PWD_RECOVERY_TMPLTE, urlToSent,
                    identityRecovery.getToken(), System.getProperty("line.separator"));
        }
        return true;
    }

    @Transactional
    public boolean validateAndResetPassword(final String token, final String newPassword) {
        boolean recoverd = false;
        final Optional<IdentityRecovery> identityRecovery = getByToken(token);
        if (identityRecovery.isPresent()) {
            final IdentityRecovery idRecovery = identityRecovery.get();
            if (idRecovery.getExpiry().isAfterNow()) {
                final User user = idRecovery.getUser();
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.updateUser(user);
                recoverd = true;
            }
            identityRecoveryRepository.delete(idRecovery);
        }
        return recoverd;
    }

}
