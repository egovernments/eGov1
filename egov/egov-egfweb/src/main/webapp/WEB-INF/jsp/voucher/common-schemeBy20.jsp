<%@ page contentType="text/json"%><%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%><s:if test="fundId==0">Please select Fund</s:if>
<s:else>
   <s:if test="schemeList.size() == 0"> Nothing found to display</s:if> 
   <s:else>
      <s:iterator var="ss" value="schemeList" status="status">
         <s:property value="%{code}" />`-`<s:property value="%{name}" />`~`<s:property value="%{id}" />~^</s:iterator>
   </s:else>
</s:else>
