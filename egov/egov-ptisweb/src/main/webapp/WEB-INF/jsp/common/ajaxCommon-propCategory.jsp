<%@page contentType="text/json" %>
<%@taglib prefix="s" uri="/WEB-INF/taglib/struts-tags.tld" %>
{ "ResultSet": { "Result":[
<s:iterator var="s" value="propTypeCategoryMap" status="status">
    {"Text":"<s:property value="%{value}" />",
    "Value":"<s:property value="%{key}" />"
    }<s:if test="!#status.last">,</s:if>
</s:iterator>
] } }
