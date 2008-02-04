<%@ page isErrorPage="true"
         import="com.arsdigita.dispatcher.DispatcherHelper"
         import="com.arsdigita.templating.WrappedTransformerException"
         import="com.arsdigita.templating.Templating"
         import="javax.xml.transform.TransformerException"
         import="javax.xml.transform.SourceLocator"
         import="java.util.Iterator"
         import="java.util.Collection"
%><% DispatcherHelper.forceCacheDisable(response); %>
<%-- NB. The above cacheDisable command must be on the first line of the jsp
         since the header must be written before any data is output --%>

<jsp:include page="error-header.jsp">
  <jsp:param name="title" value="An unexpected error has occurred"/>
</jsp:include>

<%
    String genericErrorMessage = 
    "<p>An unexpected error has occurred. If this continues to be a problem for you please contact the administrators of the site quoting the following:</p>";
    
    String xslErrorString = "<p>An unexpected error has occurred when trying to parse the XSL templates.  The error is presented below.  If this continues to be a problem for you please contact the administrators of the quite quoting the CCM issue report code.</p>";

    if (exception != null && (exception instanceof WrappedTransformerException)) {
        Collection errors = 
        (Collection)request.getAttribute(Templating.FANCY_ERROR_COLLECTION);
        if (errors != null) {
            Iterator iter = errors.iterator();
            out.println(xslErrorString);
            int count = 1;
            int totalErrors = errors.size();
            while(iter.hasNext()) {
                TransformerException ex = (TransformerException)iter.next();
                out.println("<b><u>XSL Error " + count + " of " + 
                            totalErrors + "</u></b><br/>");
                out.println("<b>Message:</b> " + ex.getMessage() + "<br/>");
                count++;
                SourceLocator locator = ex.getLocator();
                Throwable root = ex.getCause();
                if (root == null) {
                    root = ex.getException();
                }
                if (root != null) {
                    out.println("<b>Cause:</b> " + root.getMessage() + 
                                "<br/>");
                }
                out.println("<b>Location:</b> " + ex.getLocationAsString() +
                            "<br/>");
                if (locator != null) {
                    out.println("<b>Line:</b> " + locator.getLineNumber() + 
                                "<br/>");
                    out.println("<b>Column:</b> " + 
                                locator.getColumnNumber() + "<br/>");
                }
                out.println("<p/>");
            }
        } else {
            out.println(genericErrorMessage);
        }
    } else {
        out.println(genericErrorMessage);
    }
%>

<jsp:include page="error-code.jsp"/>

<jsp:include page="error-footer.jsp"/>

