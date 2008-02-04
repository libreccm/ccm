<%@ page
  autoFlush="true"
  contentType="text/plain"
  import="com.arsdigita.versioning.VersioningServlet.RollbackLogger,
          com.arsdigita.persistence.OID,
          com.arsdigita.util.AssertionError,
          java.io.IOException,
          java.io.PrintWriter,
          java.math.BigInteger"
%>

<%
  RollbackLogger rbl = (RollbackLogger) request.getAttribute("logger");

  if ( rbl == null ) {
      throw new ServletException("couldn't find the logger attribute");
  }
%>

Rolling back <%= rbl.getOID() %> to txnID=<%= rbl.getTxnID() %>

<%
  rbl.setWriter(out);
  try {
      rbl.rollback();
  } catch (RuntimeException ex) {
      rbl.printException(ex);
  } catch (AssertionError er) {
      rbl.printException(er);
  }
%>
