<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0">

  <xsl:param name="dispatcher-prefix"/>

<xsl:template match="portlet:login">
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="portlet:loginuser">
  <h3>Welcome <xsl:value-of select="@givenName"/> &#160; <xsl:value-of select="@familyName"/></h3>
  <ul>
    <xsl:for-each select="bebop:link">
      <li><xsl:apply-templates select="."/></li>
    </xsl:for-each>
  </ul>
</xsl:template>

<xsl:template match="portlet:loginform">
  <xsl:apply-templates />
  <form method="post" action="{$dispatcher-prefix}/{@url}" name="user-login">
    <table cellpadding="2" cellspacing="0" border="0">
      <tr>
        <th>Email:</th>
        <td><input type="text" size="20" name="email"/></td>
      </tr>
      <tr>
        <th>Password:</th>
        <td><input type="password" size="20" name="password"/></td>
      </tr>
      <tr>
        <td colspan="2">
          <input type="checkbox" checked="checked" name="persistentCookieP"
                value="1" id="persistentCookieP:1"/>
          <label for="persistentCookieP:1">Remember this login?</label>
        </td>
      </tr>
      <tr>
        <td></td>
        <td><input type="submit" name="login.submit" value="Login"/></td>
      </tr>

      <input name="form.user-login" type="hidden" value="visited"/>
      <input name="timestamp" type="hidden" value="{@timestamp}"/>
    </table>
  </form>
</xsl:template>

</xsl:stylesheet>
