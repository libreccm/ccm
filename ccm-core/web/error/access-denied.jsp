<%@ page isErrorPage="true"
         import="com.arsdigita.dispatcher.DispatcherHelper, 
                 com.arsdigita.cms.dispatcher.AccessDeniedException"
%><% DispatcherHelper.forceCacheDisable(response); %>
<%-- NB. The above cacheDisable command must be on the first line of the jsp
         since the header must be written before any data is output --%>

<jsp:include page="error-header.jsp">
  <jsp:param name="title" value="You do not have access to the requested resource"/>
</jsp:include>

<p>You do not have access to the requested resource.  This may be
because you have not been granted sufficient privileges.</p>

<table cellspacing="0" cellpadding="4" border="0">
  <tr>
    <td class="form_label" valign="top" nowrap="nowrap">Original URL:</th>
    <td class="form_value" valign="top">
      <%= (String) request.getAttribute(AccessDeniedException.ACCESS_DENIED) %>
    </td>
  </tr>
</table>

<p>If this continues to be a problem for you please contact the administrators of 
the site quoting the following:</p>

<jsp:include page="error-code.jsp"/>

<jsp:include page="error-footer.jsp"/>

