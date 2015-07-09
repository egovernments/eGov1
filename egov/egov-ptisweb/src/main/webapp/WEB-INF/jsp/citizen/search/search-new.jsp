<!--  #-------------------------------------------------------------------------------
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
#-------------------------------------------------------------------------------  -->
<%@ include file="/includes/taglibs.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<html>
	<head>
		<sx:head />
		<script type="text/javascript">
			function populateWard() {
				populatewardId( {
					zoneId : document.getElementById("zoneId").value
				});
			}

			function gotoSearch(){
				document.assessmentform.action='${pageContext.request.contextPath}/citizen/search/search-srchByAssessment.action';
				document.assessmentform.submit(); 
			}
			
		</script>
		<title><s:text name="searchProp.title"></s:text></title>
	</head>
	<body>
		<div class="formmainbox">
			<s:if test="%{hasErrors()}">
				<div align="left">
					<s:actionerror />
				</div>
			</s:if>
			<center>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<s:form action="search" name="assessmentform" theme="simple">
							<div class="formheading"></div>
							
							<tr>
								<td width="100%" colspan="4" class="headingbg">												
									<div class="headingbg">					
										<s:text name="search.assessment.num" />									
									</div>									
								</td>
							</tr>					
														
							<tr>
								<td class="bluebox">&nbsp;</td>
								<td class="bluebox">
									<s:text name="prop.Id" />
									<span class="mandatory">*</span> :
								</td>
								
								<td class="bluebox">
									<s:textfield name="assessmentNum" id="assessmentNum" value="%{assessmentNum}" maxlength="50"/>
								</td>
								<td class="bluebox">&nbsp;</td>
							</tr>
							
							
							<tr>
								<td class="bluebox" colspan="4">
									&nbsp; &nbsp; &nbsp;
								</td>
							</tr>
							<tr>
								<td class="greybox">&nbsp;</td>
								<td class="greybox" colspan="2">
									<div class="greybox" style="text-align:center">
										<s:hidden id="mode" name="mode" value="assessment"></s:hidden>
										<input type="submit" value="Search" class="button"
											onClick="gotoSearch();" />
									</div>
								</td>								
								<td class="greybox">&nbsp;</td>
							</tr>
						</s:form>
					</table>			

					
			<div align="left" class="mandatory" style="font-size: 11px">
			* <s:text name="mandtryFlds"></s:text>
			</div>
					
			</center>
		</div>
	</body>
</html>
