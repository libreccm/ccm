<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:define="/WEB-INF/bebop-define.tld"
      xmlns:show="/WEB-INF/bebop-show.tld"
        version="1.2">

  <!--jsp:output doctype-root-element="html" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/-->
  <jsp:directive.page extends="com.arsdigita.web.BaseJSP"/>

<jsp:directive.page import="java.io.File"/>
<jsp:directive.page import="java.util.Enumeration"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="org.apache.commons.fileupload.FileItem"/>
<jsp:directive.page import="org.apache.commons.fileupload.DiskFileUpload"/>
<jsp:directive.page import="org.apache.commons.fileupload.FileUpload"/>
<jsp:directive.page import="com.arsdigita.cms.ImageAsset"/>
<jsp:directive.page import="com.arsdigita.cms.ReusableImageAsset"/>
<jsp:directive.page import="com.arsdigita.dispatcher.MultipartHttpServletRequest"/>
<jsp:directive.page import="com.arsdigita.web.WebConfig"/>
<jsp:directive.page import="com.arsdigita.web.ParameterMap"/>
<jsp:directive.page import="com.arsdigita.web.Web"/>
<jsp:directive.page import="com.arsdigita.util.servlet.HttpHost"/>

  <jsp:directive.page contentType="text/html" />
<html>
    <body>
      <p>

  <jsp:scriptlet>
    // first check if the upload request coming in is a multipart request
    boolean isMultipart = FileUpload.isMultipartContent(request);

    // if not, send to message page with the error message

    if(!isMultipart){
      out.println("Request was not multipart!");
      return;
    }

    Enumeration e = request.getParameterNames();
      while (e.hasMoreElements()) {
        String name = (String) e.nextElement();
        String values[] = request.getParameterValues(name);
    }

    MultipartHttpServletRequest mreq = (MultipartHttpServletRequest)request;
    ReusableImageAsset image = new ReusableImageAsset();
    String filename = mreq.getParameter("myfile");
    File imageFile = mreq.getFile("myfile");
    image.loadFromFile( filename, imageFile, ImageAsset.MIME_JPEG );
    image.createLiveVersion();
    final ParameterMap params = new ParameterMap();
    params.setParameter("oid", image.getLiveVersion());

    final WebConfig myConfig = Web.getConfig();
    final HttpHost server = myConfig.getServer();

    com.arsdigita.web.URL url = new com.arsdigita.web.URL("http", server.getName(), server.getPort(), myConfig.getDispatcherContextPath(), "", "/redirect/", params);
  </jsp:scriptlet>
    An image has been uploaded.  This is shown below.  If the link is broken or you no longer want this image, select cancel and try again.
    <jsp:text>
      &lt;p&gt;&lt;img id="image" src="</jsp:text>
      <jsp:expression>url.toString()</jsp:expression>
      <jsp:text>"/&gt;
      &lt;/p&gt;

      </jsp:text>
      </p>
    </body>
    </html>
</jsp:root>
