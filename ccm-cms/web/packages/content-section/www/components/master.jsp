<%@ taglib uri="/WEB-INF/jsp-template.tld" prefix="acs" %>

<!doctype html public "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <title>ArsDigita Corporation Home Page</title>
  <meta name="KEYWORDS" content="ACS, ArsDigita Community System">
  <meta name="DESCRIPTION" content="ArsDigita builds and distributes the ArsDigita Community System (ACS), an open-source application development platform and suite of enterprise applications">
  <link rel="StyleSheet" href="/css/master.css"  type="text/css">
</head>

<body bgcolor="#FFFFFF" text="#000000" link="#0085c0" vlink="#800080" alink="#0085c0" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >

<table width="100%" cellpadding="0" cellspacing="0" border="0">
<tr>
  <td colspan="2" valign="bottom">
    <table width="100%" cellpadding="4" cellspacing="0" border="0">
      <tr>
        <td><img src="/assets/logotag.gif" width="176" height="44" border="0" 
             alt="ArsDigita -- Open for e-business"></td>
        <!-- nav text on the top right corner -->
        <td class="navtext" valign="top" align="right"><a 
            href="/register/login.jsp" class="nav">Sign In</a>&nbsp;</td>
      </tr>
    <tr><td valign="bottom" align="left"></td></tr>
    </table>
  </td>
</tr>

<tr><td align="right">
      <table width="100%" cellpadding="0" cellspacing="1" border="0">
        <tr><td bgcolor="#999999" class="stripes">&nbsp;</td></tr>
        <tr><td bgcolor="#0085c0" class="stripes">&nbsp;</td></tr>
      </table></td>
    <td colspan="2" width="80%">
      <table width="100%" cellpadding="0" cellspacing="1" border="0">
        <tr><td bgcolor="#666666" width="100%" class="stripes">

            <!-- START MAIN NAV -->
<table cellpadding="0" cellspacing="0" border="0">
<tr>

<!-- Begin iterate over navbar -->

<%@ include file="section-tabs.jsp" %>

<!-- Done iterate over navbar -->

</tr>
</table>
            <!-- END MAIN NAV -->

            </td></tr>
        <tr><td bgcolor="#0085c0" width="100%" class="stripes">&nbsp;</td></tr>
      </table>
    </td>
</tr>

<!-- BEGIN BODY ROW -->
<tr>
  <!-- begin left column -->
  <td valign="top">
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr><td valign="top">
	  <div class="leftcolgutter">

         <!-- START SEARCH FORM -->
           <form action="/search/search" method="post">
            <b>Search:</b><br>
            <input type="text" size="7" name="query_string">&nbsp;
            <input type="image" src="/assets/go.gif" 
                   align="absmiddle" alt="Go" border="0">
           </form>
         <!-- START NAV BLOCK -->

<%@ include file="folder-links.jsp" %>

          </td>
      </tr>
    </table>
    <!-- spacer image necessary to maintain integrity at all screen sizes -->
    <img src="/assets/blank.gif" width="50" height="1" border="0" alt="spacer">
  </td>
  <!-- end left column -->

        <!-- begin middle column -->
        <td valign="top" align="left">

<!-- BEGIN PAGE BODY -->
<br>
<table border="0" cellpadding="0" cellspacing="0">
  <tr>   
    <td valign="top">
      <div class="textpromo">
        <acs:slave />
      </div>
    </td>
  </tr>
</table>
<!-- END DUMMY HOME PAGE CONTENT -->

    </td>
  </tr>

</table>
<hr>
</body>
</html>
<!-- END PUBLIC FOOTER -->
