/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
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
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
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
 *
 */
package org.egov.eventnotification.repository;

import static org.egov.eventnotification.utils.Constants.ACTIVE;
import static org.egov.eventnotification.utils.Constants.EVENT_HOST;
import static org.egov.eventnotification.utils.Constants.NAME;
import static org.egov.eventnotification.utils.Constants.UPCOMING;
import static org.egov.infra.utils.DateUtils.endOfGivenDate;
import static org.egov.infra.utils.DateUtils.endOfToday;
import static org.egov.infra.utils.DateUtils.startOfToday;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.eventnotification.entity.Event;
import org.egov.eventnotification.entity.contracts.EventSearch;
import org.egov.eventnotification.repository.custom.EventRepositoryCustom;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

public class EventRepositoryImpl implements EventRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> searchEvent(EventSearch eventSearch) {
        Date startDate;
        Date endDate;
        if (eventSearch.getEventDateType().equalsIgnoreCase(UPCOMING)) {
            startDate = startOfToday().plusDays(7).toDate();
            endDate = endOfToday().plusDays(6).toDate();
        } else {
            startDate = startOfToday().toDate();
            endDate = endOfGivenDate(new DateTime(startDate)).plusDays(6).toDate();
        }

        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Event.class, "evnt");
        criteria.createAlias("evnt.eventType", "eventType");
        if (eventSearch.getEventType() != null)
            criteria.add(
                    Restrictions.ilike("eventType.name", eventSearch.getEventType(), MatchMode.ANYWHERE));
        if (eventSearch.getName() != null)
            criteria.add(Restrictions.ilike(NAME, eventSearch.getName(), MatchMode.ANYWHERE));
        if (eventSearch.getEventHost() != null)
            criteria.add(Restrictions.ilike(EVENT_HOST, eventSearch.getEventHost(), MatchMode.ANYWHERE));

        criteria.add(Restrictions.le("evnt.startDate", startDate));
        criteria.add(Restrictions.ge("evnt.endDate", endDate));
        criteria.add(Restrictions.eq("evnt.status", ACTIVE.toUpperCase()));

        criteria.addOrder(Order.desc("evnt.id"));
        return criteria.list();
    }

}
