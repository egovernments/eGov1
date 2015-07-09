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
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.admin.master.entity.User;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.pims.commons.Position;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EG_WF_STATES")
@NamedQueries({
        @NamedQuery(name = State.WORKFLOWTYPES_QRY, query = "select distinct s.type from State s where s.ownerPosition.id in (?0)  and s.status is not 2"),
        @NamedQuery(name = State.WORKFLOWTYPES_BY_ID, query = "select s from State s where s.id=?") })
@SequenceGenerator(name = State.SEQ_STATE, sequenceName = State.SEQ_STATE, allocationSize = 1)
public class State extends AbstractAuditable {

    private static final long serialVersionUID = -9159043292636575746L;

    public static final String DEFAULT_STATE_VALUE_CREATED = "Created";
    public static final String DEFAULT_STATE_VALUE_CLOSED = "Closed";
    public static final String STATE_REOPENED = "Reopened";
    public static final String STATE_UPDATED = "Updated";
    public static final String STATE_FORWARDED = "Forwarded";
    public static final String WORKFLOWTYPES_QRY = "WORKFLOWTYPES";
    public static final String WORKFLOWTYPES_BY_ID = "WORKFLOWTYPES_BY_ID";
    public static final String SEQ_STATE = "SEQ_EG_WF_STATES";

    public static enum StateStatus {
        STARTED, INPROGRESS, ENDED
    }

    @DocumentId
    @Id
    @GeneratedValue(generator = SEQ_STATE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private String type;

    @NotNull
    @Length(min = 1)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_POS")
    private Position ownerPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_USER")
    private User ownerUser;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "state")
    @OrderBy("id")
    private List<StateHistory> history = Collections.emptyList();

    private String senderName;
    private String nextAction;
    private String comments;
    private String extraInfo;
    private Date dateInfo;
    private Date extraDateInfo;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private StateStatus status;

    protected State() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(final Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    protected void setType(final String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    protected void setValue(final String value) {
        this.value = value;
    }

    public Position getOwnerPosition() {
        return ownerPosition;
    }

    protected void setOwnerPosition(final Position ownerPosition) {
        this.ownerPosition = ownerPosition;
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    protected void setOwnerUser(final User ownerUser) {
        this.ownerUser = ownerUser;
    }

    public List<StateHistory> getHistory() {
        return history;
    }

    protected void setHistory(final List<StateHistory> history) {
        this.history = history;
    }

    protected void addStateHistory(final StateHistory history) {
        getHistory().add(history);
    }

    public String getSenderName() {
        return senderName;
    }

    protected void setSenderName(final String senderName) {
        this.senderName = senderName;
    }

    public String getNextAction() {
        return nextAction;
    }

    protected void setNextAction(final String nextAction) {
        this.nextAction = nextAction;
    }

    public String getComments() {
        return comments;
    }

    protected void setComments(final String comments) {
        this.comments = comments;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    protected void setExtraInfo(final String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Date getDateInfo() {
        return dateInfo;
    }

    protected void setDateInfo(final Date dateInfo) {
        this.dateInfo = dateInfo;
    }

    public Date getExtraDateInfo() {
        return extraDateInfo;
    }

    protected void setExtraDateInfo(final Date extraDateInfo) {
        this.extraDateInfo = extraDateInfo;
    }

    protected StateStatus getStatus() {
        return status;
    }

    protected void setStatus(final StateStatus status) {
        this.status = status;
    }

    @Override
    public boolean isNew() {
        return status.equals(StateStatus.STARTED);
    }

    public boolean isEnded() {
        return status.equals(StateStatus.ENDED);
    }
}
