<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.joda.org/joda/time/tags" prefix="joda"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script src="<c:url value='/resources/js/app/designation.js'/>"></script>
<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title text-center no-float">
					<strong>${message}</strong>
				</div>
			</div>
			<form:form id="designationForm" method="post"
				class="form-horizontal form-groups-bordered">
				<div class="panel-body">
					<div class="row">
						<div class="col-md-3 col-xs-6 add-margin">
							<spring:message code="lbl.name" />
						</div>
						<div class="col-md-3 col-xs-6 add-margin view-content" id="emp-name">
							<c:out value="${employeeBean.employee.name }"></c:out>
							<input id="employeeName" type="hidden"	value="<c:out value="${employeeBean.employee.name }" />" />
						</div>
						</div>
                   
					<div class="row text-center">
						<div class="row">
								<button type="submit" id="buttonCreate" class="btn btn-success">
									<spring:message code="lbl.create" />
								</button>
								<button type="submit" id="buttonEdit" class="btn btn-success">
									<spring:message code="lbl.edit" />
								</button>
								<a href="javascript:void(0)" class="btn btn-default"
									onclick="self.close()"><spring:message code="lbl.close" /></a>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
</div>

