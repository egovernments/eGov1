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
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
	<head>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	    <meta name="description" content="eGov ERP System" />
	    <meta name="author" content="eGovernments Foundation" />
	
	    <title><tiles:insertAttribute name="title"/></title>
		<link rel="icon" href="<c:url value='/resources/global/images/favicon.png" sizes="32x32' context='/egi'/>">
	    <link rel="stylesheet" href="<c:url value='/resources/global/css/bootstrap/bootstrap.css' context='/egi'/>">
		<link rel="stylesheet" href="<c:url value='/resources/global/css/egov/custom.css' context='/egi'/>">
		<link rel="stylesheet" href="<c:url value='/resources/global/css/egov/header-custom.css' context='/egi'/>">
		
		<script src="<c:url value='/resources/global/js/jquery/jquery.js' context='/egi'/>"></script>
		<script src="<c:url value='/resources/global/js/bootstrap/bootstrap.js' context='/egi'/>"></script>
		<script src="<c:url value='/resources/global/js/bootstrap/bootbox.min.js' context='/egi'/>"></script>
		<script src="<c:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/egi'/>"></script>
		<script src="<c:url value='/resources/global/js/egov/custom.js' context='/egi'/>"></script>
		<script src="<c:url value='/resources/js/app/homepage.js' context='/egi'/>"></script>
		<script src="<c:url value='/resources/js/app/homepagecitizen.js' context='/egi'/>"></script>
	    <!--[if lt IE 9]><script src="resources/js/ie8-responsive-file-warning.js"></script><![endif]-->
		
		<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
		<!--[if lt IE 9]>
			<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
			<script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
		<![endif]-->
	
	</head>
    <body class="page-body" onload="onBodyLoad()" oncontextmenu="return false;">
    	<div class="page-container login-page">
    	<tiles:insertAttribute name="header"/>
                <div class="main-content login-page">
                    <tiles:insertAttribute name="body"/>
                </div>
             <tiles:insertAttribute name="footer"/>
        </div>
        <div class="modal fade change-password" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">Change Password</h4>
					</div>
					
					<div class="modal-body">
						<form id="password-form" class="form-horizontal form-groups-bordered">
							<div class="form-group">
								<div class="col-md-4">
									<label class="control-label">Old Password</label>
								</div>
								<div class="col-md-8 add-margin">
									<input type="password" class="form-control" id="old-pass">
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-4">
									<label class="control-label">New Password</label>
								</div>
								<div class="col-md-8 add-margin">
									<input type="password" class="form-control check-password" id="new-pass">
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-4">
									<label class="control-label">Re-type Password</label>
								</div>
								<div class="col-md-8 add-margin">
									<input type="password" class="form-control check-password" id="retype-pass">
									<div class="password-error error-msg display-hide">Password is incorrect</div>
								</div>
							</div>
							<div class="form-group text-right">
								<button type="submit" class="btn btn-primary">Change Password</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
        
        <div class="modal fade loader-class" data-backdrop="static">
			<div class="modal-dialog">
					<div class="modal-body">
						<div class="row spinner-margin text-center">
							<div class="col-md-12 ">
								<div class="spinner">
									<div class="rect1"></div>
									<div class="rect2"></div>
									<div class="rect3"></div>
									<div class="rect4"></div>
									<div class="rect5"></div>
								</div>
							</div>
							
							<div class="col-md-12 spinner-text">
								Processing your request. Please wait..
							</div>
						</div>
					</div>
			</div>
		</div>
    </body>
</html>