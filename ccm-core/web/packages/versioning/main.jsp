<%@ page
  import="com.arsdigita.versioning.VersioningServlet.VersionedObjects,
          com.arsdigita.persistence.OID,
          java.net.URLEncoder"
%>

<%@ include file="header.jspf" %>

<form action="main" method="GET">
<span>Object type: <input type="text" name="objectType" maxlength="40"> </span>
<span><input type="submit" name="search" value=" Search ">
<input type="hidden" name="cmd" value="typeSearch">
</form>

<%
  VersionedObjects oids = (VersionedObjects) request.getAttribute("versionedObjects");
  if ( oids!= null && oids.size() > 0 ) {
%>

<h1>OIDs</h1>

<%
  }
%>

<table border="1" cellpadding="2" cellspacing="0" align="center" width="80%">
<tr>
  <th>Data Object</th>
  <th colspan="2">Show Txns</th>
</tr>
<%

  if ( oids != null ) {
      while ( oids.hasNext() ) {
          String serOID = (String) oids.next();
          String strOID = oids.getOID().toString();
          String encoded = URLEncoder.encode(serOID);
          String href = "txns?cmd=showTxns&amp;oid=" + encoded;
          String taggedHref = href + "&amp;tagged=yes";
%>
  <tr>
    <td><%= strOID %></a></td>
    <td><a href="<%= href %>">affecting</a></td>
    <td><a href="<%= taggedHref %>">tagged</a></td>
  </tr>
<%
      }
  }
%>
</table>

<div align="center">
  <a href="graph?cmd=graph&amp;graphType=versioning">Versioning Dependency Graph</a>
</div>

<%@ include file="footer.jspf" %>
