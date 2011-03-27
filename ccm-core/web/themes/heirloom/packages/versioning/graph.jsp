<%@ page
  import="com.arsdigita.versioning.VersioningServlet.GraphPrinter"
%>

<%@ include file="header.jspf" %>

<h1>Versioning Dependence Graph</h1>

<blockquote><pre class="programlisting">
<%
  GraphPrinter printer = (GraphPrinter) request.getAttribute("graphPrinter");

  if ( printer == null ) {
      throw new ServletException("couldn't find the printer attribute");
  }
  printer.setWriter(out);
  printer.printGraph();
%>
</pre></blockquote>

<div align="center">
  <a href="main">Main</a>
</div>

<%@ include file="footer.jspf" %>
