<%@page isErrorPage="true"
        import="com.arsdigita.kernel.Kernel,
                com.arsdigita.sitenode.ServletErrorReport"
%>

<pre>
CCM issue report code: <%= (String) request.getAttribute(ServletErrorReport.GURU_MEDITATION_CODE) %>
</pre>

<% if (Kernel.getConfig().isDebugEnabled()) { %>

<pre>
<%= (String) request.getAttribute(ServletErrorReport.GURU_ERROR_REPORT) %>
</pre>
<% } %>
