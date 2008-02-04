<%@ page
  import="com.arsdigita.versioning.Tag,
          com.arsdigita.versioning.Transaction,
          com.arsdigita.versioning.TransactionCollection,
          com.arsdigita.persistence.OID,
          com.arsdigita.developersupport.Debug,
          java.math.BigInteger,
          java.util.Date"
%>

<%@ include file="header.jspf" %>

<%
  TransactionCollection txnColl =
      (TransactionCollection) request.getAttribute("txns");
  if ( txnColl == null ) {
      throw new ServletException("couldn't find the txns attribute");
  }
  OID oid = (OID) request.getAttribute("oid");
  if ( oid == null ) {
      throw new ServletException("couldn't find the oid attribute");
  }
  String encodedOID = (String) request.getAttribute("encodedOID");
 %>

<h1>Txns for <%= oid.toString() %></h1>

<table border="1" cellpadding="2" cellspacing="0" align="center" width="80%">

<%
  while ( txnColl.next() ) {
      Transaction txn = txnColl.getTransaction();
      BigInteger id = txn.getID();
      String strID = id.toString();
      Date date = txn.getTimestamp();
      String strDate = String.valueOf(date);
      String href = "rollback?cmd=rollback&amp;txnID=" + id + "&amp;oid=" + encodedOID;
%>
  <tr>
    <td><a href="<%= href %>"><%= id %></a></td>
    <td><%= date %></td>
  </tr>

<%
  }
%>

</table>

<%@ include file="footer.jspf" %>
