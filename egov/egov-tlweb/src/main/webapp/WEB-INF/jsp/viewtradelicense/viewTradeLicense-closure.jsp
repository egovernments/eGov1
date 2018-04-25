<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

<%@ include file="/includes/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<html>
<head>
    <title><s:text name="page.title.closuretrade"/></title>
</head>
<body>
<div id="content" class="printable">
    <div class="row">
        <div class="col-md-12">
            <s:form action="viewTradeLicense-cancelLicense" theme="simple" name="viewForm" method="POST">
            <table>
                <tr>
                    <td align="left" style="color: #FF0000">
                        <s:actionerror cssStyle="color: #FF0000"/>
                        <s:fielderror/>
                        <s:actionmessage/>
                    </td>
                </tr>
            </table>

            <s:push value="model">
                <s:if test="%{enableState}">
                    <s:hidden name="currentState" value="%{state.value}"/>
                </s:if>
                <s:hidden name="actionName" value="create"/>
                <s:hidden name="licenseid" id="licenseId" value="%{id}"/>
                <s:hidden id="detailChanged" name="detailChanged"></s:hidden>
                <s:hidden id="url" name="url" value="%{url}"></s:hidden>
                <input type="hidden" name="applicationNo" value="${param.applicationNo}" id="applicationNo"/>
                <c:set var="trclass" value="greybox"/>
                <div class="text-right error-msg" style="font-size:14px;">
                    <s:text name="dateofapplication.lbl"/> : <s:date name="applicationDate"
                                                                     format="dd/MM/yyyy"/></div>
                <s:if test="%{applicationNumber!=null}">
                    <div class="text-right error-msg" style="font-size:14px;">
                        <s:text name="application.num"/> : <s:property value="%{applicationNumber}"/>
                    </div>
                </s:if>
                <div class="panel panel-primary" data-collapsed="0">
                    <div class="panel-heading">
                        <div class="subheadnew text-center" id="headingdiv">
                            <s:text name="page.title.closuretrade"/>
                        </div>
                        <ul class="nav nav-tabs" id="settingstab">
                            <li class="active"><a data-toggle="tab" href="#tradedetails" data-tabidx="0"
                                                  aria-expanded="true"><s:text name="license.tradedetail"/></a></li>
                            <li class=""><a data-toggle="tab" href="#tradeattachments" id="getdocuments" data-tabidx="1"
                                            aria-expanded="false"><s:text name="license.support.docs"/></a></li>
                        </ul>
                    </div>
                    <div class="panel-body">
                        <div class="tab-content">
                            <div class="tab-pane fade active in" id="tradedetails">
                                <%@ include file='../common/license-detail-view.jsp' %>
                            </div>
                            <div class="tab-pane fade" id="tradeattachments">
                                <br/><br/>
                                <%@include file="../common/supportdocs-view.jsp" %>
                            </div>
                        </div>
                    </div>
                </div>
                <%@ include file='../common/license-fee-detail-view.jsp' %>
                <s:if test="%{!licenseHistory.isEmpty}">
                    <div class="panel panel-primary">
                        <%@ include file='../common/license-workflow-history.jsp' %>
                    </div>
                </s:if>
                <s:if test="!hasCSCPublicRole()">
                    <div class="panel panel-primary" id="workflowDiv">
                        <%@ include file='../common/license-workflow-dropdown.jsp' %>
                        <%@ include file='../common/license-workflow-button.jsp' %>
                    </div>
                </s:if>
                <s:else>
                    <s:hidden id="additionalRule" name="additionalRule" value="%{additionalRule}"/>
                    <div class="row"/>
                    <div class="row">
                        <div class="text-center">
                            <button type="submit" id="btnsave" class="btn btn-primary" onclick="return onSubmit();">
                                Save
                            </button>
                            <button type="button" id="btnclose" class="btn btn-default" onclick="window.close();">
                                Close
                            </button>
                        </div>
                    </div>
                </s:else>
            </s:push>
        </div>
        </s:form>
    </div>
</div>

<jsp:include page="../common/process-owner-reassignment.jsp"/>
<script>
    function onSubmitValidations() {
        return true;
    }

    function onSubmit() {
        var licid = $('#licenseId').val();
        var url = $('#url').val();
        document.viewForm.action = '${pageContext.request.contextPath}/' + url + licid;
        return true;
    }

</script>
<script src="<cdn:url  value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script src="<cdn:url  value='/resources/js/app/view-support-documents.js?rnd=${app_release_no}'/>"></script>
</body>
</html>