<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page" 
  xmlns:define="/WEB-INF/bebop-define.tld"
  xmlns:show="/WEB-INF/bebop-show.tld"
  version="1.2">

  <jsp:directive.page import="com.arsdigita.bebop.parameters.StringParameter"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BooleanParameter"/>
  <jsp:directive.page import="com.arsdigita.aplaws.ObjectTypeSchemaGenerator"/>
  <jsp:directive.page import="com.arsdigita.xml.Document"/>
  <jsp:directive.page import="java.io.Writer"/>

  <jsp:scriptlet>
     StringParameter typeParam = new StringParameter("type");
     String type = (String)typeParam.transformValue(request);

     StringParameter contextParam = new StringParameter("context");
     String context = (String)contextParam.transformValue(request);

     BooleanParameter wrapAttrParam = new BooleanParameter("wrapAttr");
     Boolean wrapAttr = (Boolean)wrapAttrParam.transformValue(request);

     BooleanParameter wrapRootParam = new BooleanParameter("wrapRoot");
     Boolean wrapRoot = (Boolean)wrapRootParam.transformValue(request);

     BooleanParameter wrapObjectParam = new BooleanParameter("wrapObject");
     Boolean wrapObject = (Boolean)wrapObjectParam.transformValue(request);

     String ns = type.replace('.', '/');

     ObjectTypeSchemaGenerator gen = new ObjectTypeSchemaGenerator("object", 
       "http://aplaws.org/schemas/content-types/" + ns);
     if (Boolean.TRUE.equals(wrapObject)) {
       gen.setWrapObjects(true);
     } else if (Boolean.FALSE.equals(wrapObject)) {
       gen.setWrapObjects(false);
     }
     if (Boolean.TRUE.equals(wrapAttr)) {
       gen.setWrapAttributes(true);
     } else if (Boolean.FALSE.equals(wrapAttr)) {
       gen.setWrapAttributes(false);
     }
     if (Boolean.TRUE.equals(wrapRoot)) {
       gen.setWrapRoot(true);
     } else if (Boolean.FALSE.equals(wrapRoot)) {
       gen.setWrapRoot(false);
     }
     gen.walk(type, context);

     Document doc = new Document(gen.getRoot());

     response.setContentType("text/plain; charset=UTF-8");
     Writer writer = response.getWriter();
     writer.write(doc.toString());
  </jsp:scriptlet>
</jsp:root>
