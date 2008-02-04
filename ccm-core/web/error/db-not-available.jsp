<%@ page isErrorPage="true"
    import="com.arsdigita.dispatcher.DispatcherHelper"
%><% DispatcherHelper.forceCacheDisable(response); %>
<%-- NB. The above cacheDisable command must be on the first line of the jsp
         since the header must be written before any data is output --%>

<jsp:include page="error-header.jsp">
  <jsp:param name="title" value="The database is unavailable"/>
</jsp:include>

<p>The database is unavailable. If this continues to be a problem for
you please contact the administrators of the site.</p>

<jsp:include page="error-footer.jsp"/>
