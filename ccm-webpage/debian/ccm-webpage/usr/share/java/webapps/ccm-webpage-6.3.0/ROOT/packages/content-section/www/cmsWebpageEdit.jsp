<%@ page 
    import ="com.arsdigita.cms.webpage.ui.WebpageCMSEditorPage,com.arsdigita.cms.dispatcher.ContentSectionDispatcher,com.arsdigita.dispatcher.*" %>
<%DispatcherHelper.cacheDisable(response);%>
    <!-- NB. The above cacheDisable command must be on the first line of the jsp
             since the header must be written before any data is output -->

<%
    WebpageCMSEditorPage sectionPage = new WebpageCMSEditorPage();
    RequestContext context = DispatcherHelper.getRequestContext(request);
    sectionPage.init();
    sectionPage.dispatch(request, response, context);
%>
