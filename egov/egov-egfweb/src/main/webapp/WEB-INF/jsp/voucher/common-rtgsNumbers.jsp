<%@ page contentType="text/json" %><%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld" %><s:if test="bankaccountId==0">Please select account number </s:if><s:else><s:if test="numberList.size == 0 "> Nothing found to display
</s:if><s:else><s:iterator var="s" value="numberList" status="status"><s:property value="%{s}" />~^</s:iterator></s:else></s:else>
