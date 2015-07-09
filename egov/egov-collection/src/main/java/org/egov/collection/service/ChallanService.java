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
package org.egov.collection.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.Challan;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.commons.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides services related to receipt header
 */
@Transactional(readOnly=true)
public class ChallanService extends PersistenceService<Challan, Long> {
	
	private static final Logger LOGGER = Logger.getLogger(ChallanService.class);

	/*private final ChallanRepository challanRepository;

	@Autowired
	public ChallanService(final ChallanRepository challanRepository) {
		this.challanRepository = challanRepository;
	}*/

	@Autowired
	private CollectionsUtil collectionsUtil;

	/**
	 * This method performs the Challan workflow transition. The challan status
	 * is updated and transitioned to the next state. At the end of the
	 * transition the challan will be available in the inbox of the user of the
	 * position specified.
	 * 
	 * @param challan
	 *            the <code>Challan</code> instance which has to under go the
	 *            workflow transition
	 * 
	 * @param nextPosition
	 *            the position of the user to whom the challan must next be
	 *            assigned to.
	 * 
	 * @param actionName
	 *            a <code>String</code> representing the state to which the
	 *            challan has to transition.
	 * 
	 * @param remarks
	 *            a <code>String</code> representing the remarks for the current
	 *            action
	 * 
	 * @throws EGOVRuntimeException
	 */
	public void workflowtransition(Challan challan, Position nextPosition,
			String actionName, String remarks) throws EGOVRuntimeException {
		// to initiate the workflow
		if (challan.getState() == null) {
			challan.transition()
					.start()
					.withSenderName(challan.getCreatedBy().getName())
					.withComments("Challan Workflow Started")
					.withStateValue(CollectionConstants.WF_STATE_NEW)
					.withOwner(
							collectionsUtil.getPositionOfUser(challan
									.getCreatedBy())).withDateInfo(new Date());
			LOGGER.debug("Challan Workflow Started.");

		}

		if (CollectionConstants.WF_ACTION_NAME_NEW_CHALLAN.equals(actionName)
				|| CollectionConstants.WF_ACTION_NAME_MODIFY_CHALLAN
						.equals(actionName)) {
			challan.setStatus(collectionsUtil.getEgwStatusForModuleAndCode(
					CollectionConstants.MODULE_NAME_CHALLAN,
					CollectionConstants.CHALLAN_STATUS_CODE_CREATED));
			challan.transition(true)
					.withComments(CollectionConstants.CHALLAN_CREATION_REMARKS)
					.withStateValue(CollectionConstants.WF_STATE_CREATE_CHALLAN)
					.withSenderName(challan.getCreatedBy().getName())
					.withDateInfo(new Date()).transition();
		}

		if (CollectionConstants.WF_ACTION_NAME_APPROVE_CHALLAN
				.equals(actionName)) {
			challan.setStatus(collectionsUtil.getEgwStatusForModuleAndCode(
					CollectionConstants.MODULE_NAME_CHALLAN,
					CollectionConstants.CHALLAN_STATUS_CODE_APPROVED));
			challan.transition(true)
					.withComments(remarks)
					.withStateValue(
							CollectionConstants.WF_STATE_APPROVE_CHALLAN)
					.withSenderName(challan.getCreatedBy().getName())
					.withDateInfo(new Date()).transition();
		}

		// on reject, the challan has to go to inbox of the creator
		if (CollectionConstants.WF_ACTION_NAME_REJECT_CHALLAN
				.equals(actionName)) {
			Position createdByPosition = collectionsUtil
					.getPositionOfUser(challan.getCreatedBy());
			challan.setStatus(collectionsUtil.getEgwStatusForModuleAndCode(
					CollectionConstants.MODULE_NAME_CHALLAN,
					CollectionConstants.CHALLAN_STATUS_CODE_REJECTED));
			// the next action can be modify or cancel challan
			challan.transition(true)
					.withComments(remarks)
					.withStateValue(
							CollectionConstants.WF_STATE_REJECTED_CHALLAN)
					.withSenderName(challan.getCreatedBy().getName())
					.withDateInfo(new Date()).transition();
		}

		if (CollectionConstants.WF_ACTION_NAME_CANCEL_CHALLAN
				.equals(actionName)) {
			challan.setStatus(collectionsUtil.getEgwStatusForModuleAndCode(
					CollectionConstants.MODULE_NAME_CHALLAN,
					CollectionConstants.CHALLAN_STATUS_CODE_CANCELLED));
			challan.transition(true)
					.withComments(remarks)
					.withStateValue(CollectionConstants.WF_STATE_CANCEL_CHALLAN)
					.withSenderName(challan.getCreatedBy().getName())
					.withDateInfo(new Date()).transition();
		}
		persist(challan);

		LOGGER.debug("Challan workflow transition completed. Challan transitioned to : "
				+ challan.getCurrentState().getValue());

		if (CollectionConstants.WF_ACTION_NAME_CANCEL_CHALLAN
				.equals(actionName)
				|| CollectionConstants.WF_ACTION_NAME_VALIDATE_CHALLAN
						.equals(actionName)) {
			challan.transition(true).withComments("End of challan worklow")
					.withStateValue(CollectionConstants.WF_STATE_END)
					.withSenderName(challan.getCreatedBy().getName())
					.withDateInfo(new Date()).end();
			LOGGER.debug("End of Challan Workflow.");
		}
	}

	public CollectionsUtil getCollectionsUtil() {
		return collectionsUtil;
	}

	public void setCollectionsUtil(CollectionsUtil collectionsUtil) {
		this.collectionsUtil = collectionsUtil;
	}
}
