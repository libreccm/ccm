<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.StringParameter"/>
  <jsp:directive.page import="com.arsdigita.atoz.ui.AtoZPane"/>
  
  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="atozPage" application="atoz" 
    title="A-Z" cache="true">

    <jsp:scriptlet>
        StringParameter letter = new StringParameter("letter");
        atozPage.add(new AtoZPane(letter));
        atozPage.addGlobalStateParam(letter);
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
