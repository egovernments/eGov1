<%@ page contentType="text/json" %>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld" %> 
{
"ResultSet": {
    "Result":[
    <s:iterator var="s" value="bankBranchList" status="status">
    {"Text":"<s:property value="%{bankBranchName}" />",
    "Value":"<s:property value="%{bankBranchId}" />"
    }<s:if test="!#status.last">,</s:if>
    </s:iterator>       
    ]
  }
}
