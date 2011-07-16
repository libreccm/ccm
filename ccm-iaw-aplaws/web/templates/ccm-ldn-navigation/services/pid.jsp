<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          version="1.2"> 

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.IntegerParameter"/>
  <jsp:directive.page import="com.arsdigita.london.terms.Domain"/>
  <jsp:directive.page import="com.arsdigita.london.terms.Term"/>
  <jsp:directive.page import="com.arsdigita.kernel.URLService"/>
  <jsp:directive.page import="com.arsdigita.web.RedirectSignal"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheForWorld( response );
    Integer pid = (Integer)new IntegerParameter("pid").transformValue(request);

    Domain lgcl = Domain.retrieve("LGSL");
    Term service = lgcl.getTerm(pid);

    String url = URLService.locate(service.getOID());
    throw new RedirectSignal(url, false);
  </jsp:scriptlet>
</jsp:root>
