<%@ page contentType="text/json" %><%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld" %><s:if test="glCode==null">Please select account code </s:if><s:else><s:if test="glCodesList.size == 0 ">
</s:if><s:else><s:iterator var="s" value="glCodesList" status="status"><s:property value="%{glcode}" /> `~` <s:property value="%{name}" />~^</s:iterator></s:else></s:else>
