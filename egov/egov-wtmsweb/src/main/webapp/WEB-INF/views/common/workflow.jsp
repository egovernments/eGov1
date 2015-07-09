<!--
	eGov suite of products aim to improve the internal efficiency,transparency, 
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
-->
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">				
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.approverdetails"/>
				</div>					
			</div>
			<div class="panel-body">
				<div class="row show-row">
					<div class="col-md-3 col-xs-6 add-margin">
						<spring:message code="lbl.approverdepartment"/><span class="mandatory"></span>
					</div>
					<div class="col-md-3 col-xs-6 add-margin">
						<form:select path="" data-first-option="false"
							id="approvalDepartment" cssClass="form-control"
							cssErrorClass="form-control error" required="required">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${approvalDepartmentList}" itemValue="id"
								itemLabel="name" />     
						</form:select>
					</div>
					<div class="col-md-3 col-xs-6 add-margin">
						<spring:message code="lbl.approverdesignation"/><span class="mandatory"></span>
					</div>
					<div class="col-md-3 col-xs-6 add-margin">
						<form:select path="" data-first-option="false"
							id="approvalDesignation" cssClass="form-control"
							cssErrorClass="form-control error" required="required">  
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${approvalDesignationList}" itemValue="id"
								itemLabel="name" />
						</form:select>					
					</div>
				</div>
				<div class="row show-row">
					<div class="col-md-3 col-xs-6 add-margin">
						<spring:message code="lbl.approver"/><span class="mandatory"></span>
					</div>
					<div class="col-md-3 col-xs-6 add-margin">
					<form:select path="" data-first-option="false"
						id="approvalPosition" name="approvalPosition" cssClass="form-control"
						cssErrorClass="form-control error" required="required">  
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
					</form:select>		
					</div> 
				</div>
				<div class="row">
					<div class="col-md-3 col-xs-6 add-margin">
						<spring:message code="lbl.comments"/>
					</div>
					<div class="col-md-6 col-xs-6 add-margin">
						<form:textarea class="form-control" path=""  id="approvalComent" name="approvalComent" />
					</div>
				</div>
			</div>				
		</div>
	</div>
</div>
<script src="<c:url value='/resources/js/app/workflow.js'/>"></script>