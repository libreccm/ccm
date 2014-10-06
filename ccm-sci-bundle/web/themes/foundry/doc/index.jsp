<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

    <jsp:directive.page import="javax.xml.transform.TransformerFactory"/>
    <jsp:directive.page import="javax.xml.transform.Transformer"/>
    <jsp:directive.page import="javax.xml.transform.stream.StreamSource"/>
    <jsp:directive.page import="javax.xml.transform.stream.StreamResult"/>
    
    <jsp:scriptlet>

    response.setContentType("text/html;charset=utf-8");

    String requestURL = request.getRequestURL().toString();
    String themeURL;
    if (requestURL.endsWith("/doc/")) {
        themeURL = requestURL.substring(0, requestURL.length() - 5);
    } else if(requestURL.endsWith("/doc/index.jsp")) {
        themeURL = requestURL.substring(0, requestURL.length() - 14);
    } else {
        themeURL = requestURL.substring(0, requestURL.length() - 4);
    }

    /*response.getOutputStream().print("themeURL = " + themeURL);
    response.getOutputStream().print("requestURI = " + request.getRequestURI());
    response.getOutputStream().print("requestURL = " + request.getRequestURL());*/

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer(new StreamSource(themeURL + "/start.xsl"));
    transformer.setParameter("theme-prefix", themeURL);
    transformer.transform(new StreamSource(themeURL + "/doc/foundry-documentation.xml"), 
                          new StreamResult(response.getOutputStream()));
    
    </jsp:scriptlet>
</jsp:root>
