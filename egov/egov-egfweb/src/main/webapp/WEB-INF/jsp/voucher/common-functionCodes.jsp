<%@ page contentType="text/json" %>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld" %>
<s:if test="function==null">
Please select function 
</s:if><s:else>
<s:if test="functionCodesList.size == 0 ">
</s:if>
<s:else>
<s:iterator var="s" value="functionCodesList" status="status">
<s:property value="%{code}" /> `~` <s:property value="%{name}" /> `~` <s:property value="%{id}" />~^
</s:iterator>
</s:else>
</s:else>
