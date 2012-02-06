<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">
  <jsp:directive.page contentType="text/html"/>

  <jsp:scriptlet>
    // Since:   2003-11-06
    // Version: $Revision: #1 $ $DateTime: 2003/11/06 11:50:38 $
    // See:     https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=108722
    // FIXME: the text below needs to be prettified and globalized.
    //
    // Wild Guess: JSP to handle the legacy presentation based on either folder
    // structure or content section specific categories. Now replaced by
    // navigation and terms, therefore no longer used.
    // Link to this jsp is still part of CMS UI (part of content section
    // listing in content-center but hidden by configuration parameter
    //     com.arsdigita.cms.hide_legacy_public_site_link
    // in CMSConfig      
  </jsp:scriptlet>

  <jsp:scriptlet>
    response.setStatus(response.SC_NOT_FOUND);
  </jsp:scriptlet>

<html>
  <head>
    <title>Nothing published</title>
  </head>
  <body>
  <h1>Nothing published</h1>

  <p>No content has been published yet.</p>
  </body>
</html>
</jsp:root>
