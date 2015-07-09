<!-- #-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#------------------------------------------------------------------------------- -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<link rel="stylesheet"
	href="<c:url value='/resources/global/css/font-icons/entypo/css/entypo.css' context='/egi'/>">
<link rel="stylesheet"
	href="<c:url value='/resources/global/css/bootstrap/typeahead.css' context='/egi'/>">

<script type="text/javascript"
	src="<c:url value='/commonjs/ajaxCommonFunctions.js' context='/egi'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/app/escalationview.js'/>"></script>
<script>
	function deleteRow(obj) {
		var tb1 = document.getElementById("escalationTable");
		var lastRow = (tb1.rows.length) - 1;
		var j;
		var curRow = getRow(obj).rowIndex;
		//alert('cur row '+curRow +' lastRow'+lastRow);
		if (lastRow == 1) {
			alert('You cannot delete this row.');
			return false;
		} else {
		
			for (j =curRow; j < lastRow; j++) {
				$("#positionHierarchyFromPositionId"+j).attr('name',"positionHierarchyList["+(j-1)+"].fromPosition.id");
				$("#positionHierarchyFromPositionId"+j).attr('id',"positionHierarchyFromPositionId"+(j-1));

				$("#positionHierarchyfromPositionName"+j).attr('name',"positionHierarchyList["+(j-1)+"].fromPosition.name");
				$("#positionHierarchyfromPositionName"+j).attr('id',"positionHierarchyfromPositionName"+(j-1));

				$("#positionHierarchySubType"+j).attr('name',"positionHierarchyList["+(j-1)+"].objectSubType");
				$("#positionHierarchySubType"+j).attr('class',"form-control positionHierarchySubType"+(j-1));
				$("#positionHierarchySubType"+j).attr('id',"positionHierarchySubType"+(j-1));

				$("#positionHierarchyobjectType"+j).attr('name',"positionHierarchyList["+(j-1)+"].objectType.id");
				$("#positionHierarchyobjectType"+j).attr('class',"form-control is_valid_alphanumeric positionHierarchyobjectType"+(j-1));
				$("#positionHierarchyobjectType"+j).attr('id',"positionHierarchyobjectType"+(j-1));

				$("#approvalDepartment"+j).attr('class',"form-control approvalDepartment"+(j-1));
				$("#approvalDepartment"+j).attr('id',"approvalDepartment"+(j-1));

				$("#approvalDesignation"+j).attr('class',"form-control approvalDesignation"+(j-1));
				$("#approvalDesignation"+j).attr('id',"approvalDesignation"+(j-1));
			
				$("#positionHierarchyToPositionid"+j).attr('class',"form-control positionHierarchyToPositionid"+(j-1));
				$("#positionHierarchyToPositionid"+j).attr('name',"positionHierarchyList["+(j-1)+"].toPosition.id");
				$("#positionHierarchyToPositionid"+j).attr('id',"positionHierarchyToPositionid"+(j-1));
					
					
			}

			
			tb1.deleteRow(curRow);
			//resetSrNo();
			return true;
		}
	}
</script>		
<div class="row">
	<div class="col-md-12">
		<div class="panel " data-collapsed="0">
			<c:if test="${not empty message}"> 
                	<div class="alert alert-danger" role="alert">${message}</div>
           		</c:if>
           		
           		<div class="panel-body">
				<form:form id="viewEscalation" method="post" class="form-horizontal form-groups-bordered" modelAttribute="escalationForm">
					<div class="panel panel-primary" data-collapsed="0">
						<div class="panel-heading ">
							<div class="panel-title">
								<strong><spring:message code="lbl.escalation.heading.view"/></strong>
							</div>
						</div> 
						<div class="panel-body custom-form">
							<div class="form-group">
								<label class="col-sm-3 control-label"><spring:message code="lbl.escalation.position" /><small><i
										class="entypo-star error-msg"></i></small></label>
								<div class="col-sm-6">
								<form:input id="com_position" path="position.name" type="text"
										class="form-control typeahead is_valid_alphabet"
										placeholder="" autocomplete="off" required="required" />
											
									<input type=hidden id="mode" value="${mode}">
									<form:hidden path="position.id" id="positionId" 	value="${position.id}" />
									<form:errors path="position.id"	cssClass="add-margin error-msg" />
									<div class="error-msg eithererror all-errors display-hide"></div>
									
								</div>
							</div>
							
						
					    <div class="form-group">
								<div class="text-center">
									<button type="submit" id="escalationCreateSearch"
										class="btn btn-success">
										<spring:message code="lbl.escalation.button.search" />
									</button>
												<a href="javascript:void(0)" class="btn btn-default"
										onclick="self.close()"> <spring:message code="lbl.close" /></a>
								</div>
							</div>
					    </div>
					    
					</div>
					<div id="noescalationDataFoundDiv" class="row container-msgs">
						<c:if test="${mode == 'noDataFound'}">
							<div class="form-group">
								<div class="text-center">
									<div class="panel-title view-content">
										<spring:message code="lbl.escalation.label.NodataFound" />
									</div>
									<br>
									<button type="button" id="escalationnewEscalation"
										class="btn btn-success">
										<spring:message
											code="lbl.escalation.button.addnewEscalation" />
									</button>
								</div>
							</div>
						</c:if>
					</div>
				</form:form>
					
				<form:form id="saveEscalationForm" method="post" class="form-horizontal form-groups-bordered"
					modelAttribute="escalationForm">
					<div id="escalationDiv" class="hidden">	
					
					<form:hidden path="position.id"
							id="formpositionId"
							value="${position.id}" />
							<form:hidden path="position.name"
							id="formpositionName"
							value="${position.name}" />			
						<table id="escalationTable" table width="100%" border="0"
							cellpadding="0" cellspacing="0" class="table table-bordered">
							<thead>
								<tr>
									<%-- <th><spring:message code="lbl.escalationTime.button.srNo" /></th> --%>
									<th><spring:message code="lbl.escalation.heading.fromPosition" /></th>
									<th><spring:message code="lbl.escalation.complaintType" /></th>
										<th><spring:message code="lbl.escalation.heading.department" /></th>
										<th><spring:message code="lbl.escalation.heading.designation" /></th>
									<th><spring:message code="lbl.escalation.heading.toPosition" /></th>
								</tr>
							</thead>
							<tbody>
							
								<c:forEach var="contact" items="${escalationForm.positionHierarchyList}" varStatus="status">
									<tr>
										<%-- <td id="">${status.count}</td> --%>
										<td>
											
											<input type=hidden
											id="positionHierarchyFromPositionId${status.index}"
											name="positionHierarchyList[${status.index}].fromPosition.id"
											value="${contact.fromPosition.id}"> 
											
									       <input type="text" class="form-control is_valid_alphabet"
											id="positionHierarchyfromPositionName${status.index}"
											value="${contact.fromPosition.name}" 
											name="positionHierarchyList[${status.index}].fromPosition.name"
											 autocomplete="off" required="required"  readonly="readonly">
											<%-- 
											<input type=hidden
											id="positionHierarchyId${status.index}"
											name="positionHierarchyList[${status.index}].id"
											value="${contact.id}">
											 --%>
										</td>
										<td> 	
										<%-- <form:select path="" data-first-option="false"  value="${contact.objectSubType}"
												id="positionHierarchySubType${status.index}" cssClass="form-control positionHierarchySubType${status.index}"
												cssErrorClass="form-control error">
												<form:option value="">Select </form:option>		
												   <c:forEach items="${complaintTypes}" var="comType">
										            <option <c:if test="${comType.code eq contact.objectSubType}">selected="selected"</c:if>    value="${comType.code}">${comType.name} </option>
										         </c:forEach>
										       
										       </form:select>   --%>
										<select name="positionHierarchyList[${status.index}].objectSubType"  data-optvalue="${contact.objectSubType}"
											id="positionHierarchySubType${status.index}"  
											class="form-control positionHierarchySubType${status.index}" >
												<option value="">Select</option>
												 <c:forEach items="${complaintTypes}" var="comType">
										            <option <c:if test="${comType.code eq contact.objectSubType}">selected="selected"</c:if>    value="${comType.code}">${comType.name} </option>
										         </c:forEach>
										</select>      
										       
										</td>
									<td>
										<%-- <input type="hidden"
											id="positionHierarchySubType${status.index}"
											class="form-control is_valid_alphanumeric positionHierarchySubType${status.index}"
											value="${contact.objectSubType}" 
											name="positionHierarchyList[${status.index}].objectSubType"
											>
									 --%>		<input type="hidden"
											id="positionHierarchyobjectType${status.index}"
											class="form-control is_valid_alphanumeric positionHierarchyobjectType${status.index}"
											value="${contact.objectType.id}" 
											name="positionHierarchyList[${status.index}].objectType.id">		
											
											<form:select path="" data-first-option="false"  value="${contact.toPosition.deptDesig.department.id}"
												id="approvalDepartment${status.index}" cssClass="form-control approvalDepartment${status.index}"
												cssErrorClass="form-control error">
												<form:option value="">Select </form:option>		
												<%-- <form:options items="${approvalDepartmentList}" itemValue="id"
														itemLabel="name"  value="${contact.toPosition.deptDesig.department.id}"/>     
												 --%>
												   <c:forEach items="${approvalDepartmentList}" var="dept">
										            <option <c:if test="${dept.id eq contact.toPosition.deptDesig.department.id}">selected="selected"</c:if>    value="${dept.id}">${dept.name} </option>
										         </c:forEach>
										       
										       </form:select>  
											<%-- <select  value="${contact.toPosition.deptDesig.department.id}"
											name="positionHierarchyList[${status.index}].toPosition.deptDesig.department.id"
												id="approvalDepartment${status.index}" class="form-control approvalDepartment${status.index}"
												cssErrorClass="form-control error">
												<option value="">Select </option>		
												<c:forEach items="${approvalDepartmentList}" var="dept">
                       								<option value="${dept.id}"> ${dept.name}</option>
                       								</c:forEach>
												</select>
												
										 --%>		
											</td>
										<td>	
											<form:select path="" data-first-option="false"  data-optvalue="${contact.toPosition.deptDesig.designation.id}"
												id="approvalDesignation${status.index}" cssClass="form-control approvalDesignation${status.index}"
												cssErrorClass="form-control error">  
												<form:option value="">
													Select
												</form:option>
												<%-- <form:option value="${contact.toPosition.deptDesig.designation.id}">
													<c:out value="${contact.toPosition.deptDesig.designation.name}"/> 
												</form:option> --%>
												<%-- <form:options items="${approvalDesignationList}" itemValue="id"
													itemLabel="name" /> --%> 
												   <c:forEach items="${approvalDesignationList}" var="desig">
										            <option <c:if test="${desig.id eq contact.toPosition.deptDesig.designation.id}">selected="selected"</c:if>    value="${desig.id}">${desig.name} </option>
										         </c:forEach>
										         	
													
											</form:select>	
											</td>
										<td>	
									<select name="positionHierarchyList[${status.index}].toPosition.id" 
											id="positionHierarchyToPositionid${status.index}" data-optvalue="${contact.toPosition.id}" 
											class="form-control positionHierarchyToPositionid${status.index}" required="required">
												<option value="">Select</option>
										</select>
											<script>calltypeahead('${status.count}'-1); 
											 $(function () {
 												$('#approvalDepartment'+('${status.count}'-1)).trigger('change');
 												$('#approvalDesignation'+('${status.count}'-1)).trigger('change');
 											}); 
											</script>
									<%-- 	<input type=hidden
											id="positionHierarchyToPositionId${status.index}"
											name="positionHierarchyList[${status.index}].toPosition.id"
											value="${contact.toPosition.id}">  --%>
											</td>
											<td>
											<button type="button" onclick="deleteRow(this)" id="Add"
												class="btn btn-success">Delete</button>
										</td>
									</tr>
								</c:forEach>
				
							</tbody>
						</table>
						
					
					<div class="form-group">
								<div class="text-center">
								<br><br>
																					
									<button type="button" id="btn-add" 
												class="btn btn-success btn-add">Add New Row</button>
										
										<button type="submit" class="btn btn-success escalationBtn" onclick="return checkUniqueDesignationSelected();" id="escalationBtn">
										<spring:message code="lbl.update"/>
										</button>
										<a href="javascript:void(0);" onclick="self.close()" class="btn btn-default">Close</a>
								</div>
					</div>
							</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
		

		