<%@ page contentType="text/json" %>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld" %> 
{
"ResultSet": {
    "Result":[
    <s:iterator var="s" value="nameList" status="status">
    {"Text":"<s:property value="%{key}" />",
    "Value":"<s:property value="%{val}" />"
    }<s:if test="!#status.last">,</s:if>
    </s:iterator>       
    ]
  }
}
