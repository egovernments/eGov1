<%@ page contentType="text/json" %>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld" %> 
{
"ResultSet": {
    "Result":[
    <s:iterator var="s" value="accNumList" status="status">
   {"Text":"<s:property value="%{chartofaccounts.glcode+'--'+accountnumber+'--'+accounttype}" />",
    "Value":"<s:property value="%{id}" />"
    }<s:if test="!#status.last">,</s:if>
    </s:iterator>       
    ]
  }
}
