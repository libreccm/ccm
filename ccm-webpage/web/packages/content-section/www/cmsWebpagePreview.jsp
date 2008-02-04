<%@ page 
    import ="com.arsdigita.cms.webpage.ui.WebpagePreviewPage,com.arsdigita.dispatcher.*" %>
<%DispatcherHelper.cacheDisable(response);%>
    <!-- NB. The above cacheDisable command must be on the first line of the jsp
             since the header must be written before any data is output -->

<%
    WebpagePreviewPage sectionPage = new WebpagePreviewPage();
    RequestContext context = DispatcherHelper.getRequestContext(request);
    sectionPage.init();
    sectionPage.dispatch(request, response, context);
%>
