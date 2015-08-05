<%@ include file="/includes/taglibs.jsp" %>
<script>
	function validateWorkFlowApprover(name,errorDivId) {
		document.getElementById("workFlowAction").value=name;
	    var approverPosId = document.getElementById("approverPositionId");
	    if(approverPosId) {
			var approver = approverPosId.options[approverPosId.selectedIndex].text; 
			document.getElementById("approverName").value= approver.split('~')[0];
		}     
	   return  onSubmit();
	}

	function validateWorkFlowApprover(name) {
	    document.getElementById("workFlowAction").value=name;
	    var approverPosId = document.getElementById("approverPositionId");
	    if(approverPosId && approverPosId.value != -1) {
			var approver = approverPosId.options[approverPosId.selectedIndex].text; 
			document.getElementById("approverName").value= approver.split('~')[0];
		}   
		<s:if test="%{getNextAction()!='END'}">
	    if((name=="Forward" || name=="forward") && document.getElementById('approverPositionId').value=="-1") {
	        alert("Please Select the Approver ");
			return false;
	    }
	    </s:if>
	    return  onSubmit();
	}
</script>
<div class="buttonbottom" align="center">
	<s:hidden id="workFlowAction" name="workFlowAction"/>
	<table>
		<tr>
			<td><s:iterator value="%{getValidActions()}" var="name">
					<s:if test="%{name!=''}">
						<s:submit type="submit" cssClass="buttonsubmit" value="%{name}"
							id="%{name}" name="%{name}"
							onclick="return validateWorkFlowApprover('%{name}','jsValidationErrors');" />
					</s:if>
				</s:iterator> <input type="button" name="button2" id="button2" value="Close"
				class="button" onclick="window.close();" /></td>
		</tr>
	</table>
</div>