#-------------------------------------------------------------------------------
# <!-- #-------------------------------------------------------------------------------
# # eGov suite of products aim to improve the internal efficiency,transparency, 
# #    accountability and the service delivery of the government  organizations.
# # 
# #     Copyright (C) <2015>  eGovernments Foundation
# # 
# #     The updated version of eGov suite of products as by eGovernments Foundation 
# #     is available at http://www.egovernments.org
# # 
# #     This program is free software: you can redistribute it and/or modify
# #     it under the terms of the GNU General Public License as published by
# #     the Free Software Foundation, either version 3 of the License, or
# #     any later version.
# # 
# #     This program is distributed in the hope that it will be useful,
# #     but WITHOUT ANY WARRANTY; without even the implied warranty of
# #     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# #     GNU General Public License for more details.
# # 
# #     You should have received a copy of the GNU General Public License
# #     along with this program. If not, see http://www.gnu.org/licenses/ or 
# #     http://www.gnu.org/licenses/gpl.html .
# # 
# #     In addition to the terms of the GPL license to be adhered to in using this
# #     program, the following additional terms are to be complied with:
# # 
# # 	1) All versions of this program, verbatim or modified must carry this 
# # 	   Legal Notice.
# # 
# # 	2) Any misrepresentation of the origin of the material is prohibited. It 
# # 	   is required that all modified versions of this material be marked in 
# # 	   reasonable ways as different from the original version.
# # 
# # 	3) This license does not grant any rights to any user of the program 
# # 	   with regards to rights under trademark law for use of the trade names 
# # 	   or trademarks of eGovernments Foundation.
# # 
# #   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
# #------------------------------------------------------------------------------- -->
#-------------------------------------------------------------------------------
<%@ taglib prefix="s" uri="/WEB-INF/taglibs/struts-tags.tld"%>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<%@	taglib uri="http://displaytag.sf.net" prefix="display" %>

<%@ include file="/includes/taglibs.jsp" %>
<html>
<title> SEARCH DCR</title>
<head>

</head>
 
<script type="text/javascript">
function returnBackToParent(autoDcrNum,applicant_name,address,email,mobileno,plotno,doorno,village,surveyno,blockno,plotarea
) 
{

window.opener.callSetAutoDCR(autoDcrNum,applicant_name,address,email,mobileno,plotno,doorno,village,surveyno,blockno,plotarea
);
		window.close();
	
}



</script>

</head>
<body>
<div class="errorstyle" id="searchRecords_error" style="display: none;"></div>
	<div class="errorstyle" style="display: none" ></div>
 <s:form action="searchAutoDcrExtn" name="searchAutoDcrActionForm" theme="simple">
      
		<div class="formheading"/></div>
		<table width="100%" border="0" cellspacing="0" cellpadding="2">
		<tr>
				<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
				<td class="bluebox">&nbsp;</td>
				<td class="bluebox" width="10%"></td>
				<td class="bluebox"><s:text name="autoDCRNo.lbl"/> </td>
				<td class="bluebox"> <s:textfield  id="autoDcrNum" name="autoDcrNum" value="%{autoDcrNum}" /> 	
		                             </td>  
				</tr>
				</table>
			<div class="buttonbottom" align="center">
		 <td class="bluebox"><s:submit cssClass="buttonsubmit" id="search" name="search" value="Search"  method="searchResults"  /></td>
			<td class="bluebox">&nbsp;</td>
			<td class="bluebox">&nbsp;</td>
			<td class="bluebox">&nbsp;</td>  		
	   </div>	   
		
		
	 <div id="tableData">
	   <div class="infostyle" id="search_error" style="display:none;"></div> 
		 <s:if test="%{searchMode=='result'}">
          		 <div id="displaytbl">	
          		 <display:table name="searchResult"  export="false" requestURI="" id="regListid"  class="its" uid="currentRowObject" >
    		<display:column title=" Sl No" style="text-align:center;"  >
 						 	 		<s:property value="#attr.currentRowObject_rowNum + (page-1)*pageSize"/>
 						 	</display:column>
 			   <display:column  title="AutoDcr Number" headerClass="bluebgheadtd" class="blueborderfortd" style="width:10%;text-align:center">
 			   <a href="#" onclick="returnBackToParent('${currentRowObject.autoDcrNum}','${currentRowObject.applicant_name}','${currentRowObject.address}','${currentRowObject.email}','${currentRowObject.mobileno}','${currentRowObject.plotno}','${currentRowObject.doorno}','${currentRowObject.village}','${currentRowObject.surveyno}','${currentRowObject.blockno}','${currentRowObject.plotarea}')">
 						 			 ${currentRowObject.autoDcrNum}
 					</a>
 					</display:column>
			  <display:column property="applicant_name"  title="Applicant_Name" headerClass="bluebgheadtd" class="blueborderfortd" style="width:10%;text-align:center"></display:column>
				<display:column property="address" title="Application Address" headerClass="bluebgheadtd" class="blueborderfortd" style="width:10%;text-align:center"/>
				<display:column property="email"  title="Email Id" headerClass="bluebgheadtd" class="blueborderfortd" style="width:17%;text-align:left" />	
				<display:column property="mobileno"  title="Contact No"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:35%;text-align:left" />
				<display:column property="zone"  title="Zone"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:15%;text-align:center"/>
				<display:column property="ward"  title="Ward"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:10%;text-align:center"/>
				<display:column property="plotno"  title="Plot No"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:13%;text-align:center" />
				<display:column property="doorno"  title="Door No"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:15%;text-align:center"/>
				<display:column property="village"  title="Village Name"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:10%;text-align:center"/>
				<display:column property="surveyno"  title="Survey No"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:13%;text-align:center" />
				<display:column property="blockno"  title="Block No"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:10%;text-align:center"/>
				<display:column property="plotarea"  title="Plot Area"  headerClass="bluebgheadtd" class="blueborderfortd" style="width:13%;text-align:center" />
			  
										 		
			</div>
			
    		</display:table>
    		
							</div>
							
    </s:if>
		</div>
		
</s:form>
</body>
</html>
