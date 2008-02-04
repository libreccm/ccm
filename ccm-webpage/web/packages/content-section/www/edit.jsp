<%@ page 
    import ="com.arsdigita.cms.webpage.ui.WebpagePortletEditorPage,com.arsdigita.cms.dispatcher.ContentSectionDispatcher,com.arsdigita.cms.ContentSection,com.arsdigita.cms.dispatcher.Utilities,com.arsdigita.dispatcher.*,java.util.Date" %><%DispatcherHelper.cacheDisable(response);%>
    <!-- NB. The above cacheDisable command must be on the first line of the jsp
             since the header must be written before any data is output -->
	     

<%!
    private WebpagePortletEditorPage sectionPage = new WebpagePortletEditorPage();
%>

<%
    ContentSection section = 
      ContentSectionDispatcher.getContentSection(request);

    RequestContext context = DispatcherHelper.getRequestContext(request);
    sectionPage.init();
    sectionPage.dispatch(request, response, context);
%>
