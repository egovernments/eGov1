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
package org.egov.infra.workflow.entity;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.workflow.entity.State.StateStatus;
import org.egov.pims.commons.Position;
import org.egov.search.domain.Searchable;

@MappedSuperclass
@Searchable
public abstract class StateAware extends AbstractAuditable {
    private static final long serialVersionUID = 5776408218810221246L;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "STATE_ID")
    private State state;

    /**
     * Need to overridden by the implementing class to give details about the
     * State <I>Used by Inbox to fetch the State Detail at runtime</I>
     *
     * @return String Detail
     */
    public abstract String getStateDetails();

    /**
     * To set the Group Link, Any State Aware Object which needs Grouping should
     * override this method
     **/
    public String myLinkId() {
        return this.getId().toString();
    }

    public State getState() {
        return state;
    }

    protected void setState(final State state) {
        this.state = state;
    }

    public final State getCurrentState() {
        return state;
    }

    public final List<StateHistory> getStateHistory() {
        return state == null ? Collections.emptyList() : state.getHistory();
    }

    public final String getStateType() {
        return this.getClass().getSimpleName();
    }

    public final boolean stateIsNew() {
        return hasState() && getCurrentState().isNew();
    }

    public final boolean stateIsEnded() {
        return hasState() && getCurrentState().isEnded();
    }

    public final boolean hasState() {
        return getCurrentState() != null;
    }

    public final StateAware transition() {
        if (hasState()) {
            state.addStateHistory(new StateHistory(state));
            state.setStatus(StateStatus.INPROGRESS);
            resetState();
        }
        return this;
    }

    public final StateAware transition(final boolean clone) {
        if (hasState() && clone) {
            state.addStateHistory(new StateHistory(state));
            state.setStatus(StateStatus.INPROGRESS);
        } else
            transition();
        return this;
    }

    public final StateAware start() {
        if (hasState())
            throw new EGOVRuntimeException("Workflow already started state.");
        else {
            state = new State();
            state.setType(getStateType());
            state.setStatus(StateStatus.STARTED);
            state.setValue(State.DEFAULT_STATE_VALUE_CREATED);
            state.setComments(State.DEFAULT_STATE_VALUE_CREATED);
        }

        return this;
    }

    public final StateAware end() {
        if (stateIsEnded())
            throw new EGOVRuntimeException("Workflow already ended state.");
        else {
            state.setValue(State.DEFAULT_STATE_VALUE_CLOSED);
            state.setStatus(StateStatus.ENDED);
            state.setComments(State.DEFAULT_STATE_VALUE_CLOSED);
        }
        return this;
    }

    public final StateAware reopen(final boolean clone) {
        if (stateIsEnded()) {
            final StateHistory stateHistory = new StateHistory(state);
            stateHistory.setValue(State.STATE_REOPENED);
            state.setStatus(StateStatus.INPROGRESS);
            state.addStateHistory(stateHistory);
            if (!clone)
                resetState();
        } else
            throw new EGOVRuntimeException("Workflow not ended.");
        return this;
    }

    public final StateAware withOwner(final User owner) {
        state.setOwnerUser(owner);
        return this;
    }

    public final StateAware withOwner(final Position owner) {
        state.setOwnerPosition(owner);
        return this;
    }

    public final StateAware withStateValue(final String currentStateValue) {
        state.setValue(currentStateValue);
        return this;
    }

    public final StateAware withNextAction(final String nextAction) {
        state.setNextAction(nextAction);
        return this;
    }

    public final StateAware withComments(final String comments) {
        state.setComments(comments);
        return this;
    }

    public final StateAware withExtraInfo(final String extraInfo) {
        state.setExtraInfo(extraInfo);
        return this;
    }

    public final StateAware withDateInfo(final Date dateInfo) {
        state.setDateInfo(dateInfo);
        return this;
    }

    public final StateAware withExtraDateInfo(final Date extraDateInfo) {
        state.setExtraDateInfo(extraDateInfo);
        return this;
    }

    public final StateAware withSenderName(final String senderName) {
        state.setSenderName(senderName);
        return this;
    }

    private void resetState() {
        state.setComments("");
        state.setDateInfo(null);
        state.setExtraDateInfo(null);
        state.setExtraInfo("");
        state.setNextAction("");
        state.setValue("");
        state.setSenderName("");
        state.setOwnerUser(null);
        state.setOwnerPosition(null);
    }
}
