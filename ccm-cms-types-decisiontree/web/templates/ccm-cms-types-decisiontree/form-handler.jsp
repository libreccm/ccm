<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="java.net.URLEncoder"/>
  <jsp:directive.page import="java.util.Enumeration"/>
  <jsp:directive.page import="com.arsdigita.persistence.OID"/>
  <jsp:directive.page import="com.arsdigita.cms.contenttypes.DecisionTree"/>
  <jsp:directive.page import="com.arsdigita.cms.contenttypes.DecisionTreeSection"/>
  <jsp:directive.page import="com.arsdigita.cms.contenttypes.DecisionTreeOptionTarget"/>
  <jsp:directive.page import="com.arsdigita.cms.contenttypes.DecisionTreeOptionTargetCollection"/>
  <jsp:directive.page import="com.arsdigita.cms.contenttypes.DecisionTreeSection"/>
  <jsp:directive.page import="com.arsdigita.web.BaseApplicationServlet"/>
  <jsp:directive.page import="com.arsdigita.web.RedirectSignal"/>

  <jsp:directive.page extends="com.arsdigita.web.BaseJSP"/>

  <jsp:directive.page contentType="text/html"/>

  <jsp:scriptlet>
    String sectionOID = request.getParameter(DecisionTree.PARAM_SECTION_OID);
    DecisionTreeSection section = new DecisionTreeSection(OID.valueOf(sectionOID));
    DecisionTree tree = section.getTree();

    String parameterName = section.getParameterName(); 
    String selectedOption = request.getParameter(parameterName);
    String returnURL = request.getParameter("return_url");

    Enumeration parameterNames = request.getParameterNames();
    String parameters = "";
    while (parameterNames.hasMoreElements()) {
      String name = (String) parameterNames.nextElement();
        	
       if (DecisionTree.preserveParameter(name)) {
         if (!"".equals(parameters))
           parameters += "&amp;";
         parameters += URLEncoder.encode(name) + "=" + URLEncoder.encode(request.getParameter(name));
       }
    }     

    if (request.getParameter("cancel") != null) {
      String cancelURL = tree.getCancelURL();
      if (cancelURL == null || "".equals(cancelURL)) {
  </jsp:scriptlet>

<html>
  <body>
    <p>Error: No cancel URL has been defined for the decision tree.</p>
  </body>
</html>
  
  <jsp:scriptlet>
      } else {
        throw new RedirectSignal(cancelURL, false);
      }
    } else {
      // Find out which option has been selected.
      DecisionTreeOptionTargetCollection targets = tree.getTargets();
      targets.addEqualsFilter("matchOption.treeSection.id", section.getID());
      targets.addEqualsFilter("matchOption.value",  selectedOption);

      if (targets.next()) { //XXX this is false
        DecisionTreeOptionTarget target = targets.getTarget();
        String targetURL = target.getTargetURL();
        if (targetURL == null || "".equals(targetURL)) {
          targetURL = returnURL + "?" + 
            DecisionTree.PARAM_SECTION_ID + "=" + target.getTargetSection().getID();
        }
        
        if (!"".equals(parameters)) {
          if (targetURL.contains("?"))
            targetURL += "&amp;";
          else
            targetURL += "?";
          targetURL += parameters; 
        }        

        targets.close();
        throw new RedirectSignal(targetURL, false);
      } else {
  </jsp:scriptlet>

<html>
  <body>
    <p>Error: No target has been defined for the selected option.</p>
  </body>
</html>

  <jsp:scriptlet>
      }

      targets.close();
    }
  </jsp:scriptlet>
</jsp:root>
