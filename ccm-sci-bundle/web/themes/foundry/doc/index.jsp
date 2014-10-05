<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

    <jsp:directive.page import="javax.xml.transform.TransformerFactory"/>
    <jsp:directive.page import="javax.xml.transform.Transformer"/>
    <jsp:directive.page import="javax.xml.transform.stream.StreamSource"/>
    <jsp:directive.page import="javax.xml.transform.stream.StreamResult"/>
    
    <jsp:scriptlet>

    response.setContentType("text/html;charset=utf-8");

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer(new StreamSource("../start.xsl"));
    System.out.println(request.getContextPath());
    transformer.setParameter("theme-prefix", request.getRequestURI() + "/../");
    transformer.transform(new StreamSource("foundry-documentation.xml"), 
                          new StreamResult(response.getOutputStream()));
    
    </jsp:scriptlet>
</jsp:root>
