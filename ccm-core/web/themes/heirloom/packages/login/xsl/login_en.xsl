<?xml version="1.0"?>

<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" >
<!ENTITY copy   "&#169;" >
]>

<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
             xmlns:subsite="http://www.arsdigita.com/subsite/1.0"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                  xmlns:ui="http://www.arsdigita.com/ui/1.0"
                   version="1.0">

  <xsl:import href="../../bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../ui/xsl/ui.xsl"/>

  <xsl:param name="dispatcher-prefix"/>
  <xsl:param name="internal-theme"/>

  <!-- new version of login page -->
  <xsl:template match="bebop:page[@application='login']">
  <!-- DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" -->
  <html>
  <head>
    <title>Log in</title>

    <link href="{$internal-theme}/css/acs-master.css" type="text/css" rel="stylesheet"/>
  </head>

  <body link="#000066" vlink="#333399" bgcolor="#FFFFFF">
    <xsl:call-template name="bebop:dcpJavascript"/>
    <xsl:apply-templates select="ui:userBanner" />

    <p/>&nbsp;

    <table width="75%" align="center" border="0" cellspacing="0" cellpadding="5">
      <tr bgcolor="#cc000000">
        <td align="center">
          <div class="page-title"><font color="#ffffff"><xsl:value-of select="ui:siteBanner/@sitename" /></font></div>
        </td>
      </tr>
      <tr bgcolor="#ffffff">
        <td heigth="2"></td>
      </tr>
      <tr bgcolor="#e1e1e1">
        <td>
          <xsl:choose>
            <xsl:when test="count(bebop:form)>0">
              <xsl:apply-templates select="bebop:form"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="*[not(@metadata.tag)]"/>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
    </table>
    <p/>
    &nbsp;

    <xsl:apply-templates select="ui:siteBanner"/>
    <xsl:apply-templates select="ui:debugPanel"/>
    <xsl:apply-templates select="bebop:structure"/>
  </body>
  </html>
  </xsl:template>

  <!-- this rule matches one user -->
  <xsl:template match="subsite:userInfo">
    <hr />

    <h3>What we tell other users about you</h3>

      In general we identify content that you've posted by your full name.  In
      an attempt to protect you from unsolicited bulk email (spam), we keep your
      email address hidden except from other registered users.

    <h4>Basic Information</h4>

    <ul>
      <li> Name: <xsl:value-of select="@name"/> </li>
      <li> User ID:  <xsl:value-of select="@id"/> </li>
      <li> Email Address:  <xsl:value-of select="@email"/> </li>
      <li> Personal URL:  <xsl:value-of select="@URI"/> </li>
      <li> Screen Name:  <xsl:value-of select="@screenName"/> </li>
    </ul>

    <xsl:apply-templates />

  </xsl:template>

  <xsl:template match="subsite:explainPersistentCookies">
      Our server can tell your browser to remember certain things, such as your
      email address and password.  This is convenient for you because, if you're
      the only person who uses your computer, you won't have to keep telling us
      your email address and password.
      <p/>
      It would be a very bad idea to choose this option if you're using a
      shared computer in a library or school.  Any subsequent user of this machine
      would be able to masquerade as you on our service.
      <p/>
      Note that you can erase your saved email address and password by choosing
      the "log out" option from your workspace.
      <p/>
  </xsl:template>

  <xsl:template match="subsite:loginPromptMsg[@class='email']">
    <b>Current users:</b> Please enter your email and password below.
    <p/>
    <b>New users:</b> Please begin the registration process
    by entering a valid email address and leaving the password field blank.
    You will be directed to another form to complete your registration.
    <p/>&nbsp;
  </xsl:template>

  <xsl:template match="subsite:loginPromptMsg[@class='screenName']">
    <b>Current users:</b> Please enter your screen name and password below.
    <p/>
    <b>New users:</b> Please begin the registration process
    by entering your desired screen name and leaving the password field blank.
    You will be directed to another form to complete your registration.
    <p/>&nbsp;
  </xsl:template>

  <xsl:template match="subsite:promptToEnableCookiesMsg">
    <br />
    <b>Note:</b> Our personalized web services require that your browser be enabled for JavaScript and cookies.
  </xsl:template>

  <xsl:template match="subsite:recoverPasswordMailSent">
    You have been sent an email that will allow you to change your password.
  </xsl:template>

  <xsl:template match="subsite:recoverPasswordMailFailed">
    Unable to send password recovery information via email.
    <p/>
    Please contact the system administrator for further help in recovering your password.
  </xsl:template>

  <xsl:template match="subsite:explainLoginExpired">
    <p></p>
    <font size="-1">
      <table cellpadding="5" cellspacing="1" border="1">
        <tr>
          <td bgcolor="#ffffcc"><b>Note:</b> For security reasons, the login page
            expires after a certain interval to prevent malicious attackers from logging
            in as you using cached browser passwords.</td>
        </tr>
      </table>
    </font>
  </xsl:template>


  <xsl:template match="subsite:contentCenters">
    <h4>My Control Centers</h4>
    <ul>
    <xsl:for-each select="subsite:center">
        <li>
            <a>
                <xsl:attribute name="href">
                    <xsl:for-each select="subsite:url">
                      <xsl:if test="position()=1">
                        <xsl:value-of select="node()|text()" />
                      </xsl:if>
                    </xsl:for-each>
                 </xsl:attribute>
                <xsl:value-of select="@name" />
            </a>
        </li>
    </xsl:for-each>
    </ul>
  </xsl:template>

</xsl:stylesheet>
