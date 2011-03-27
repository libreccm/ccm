<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

  <xsl:output method="html" indent="yes"/>

  <xsl:template match="bebop:list[@class='pageErrorDisplay']"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <xsl:if test="count(bebop:cell)">      
      <table border="0" cellpadding="4" cellspacing="0">
        <tr><td nowrap="1">
          <font color="{@color}">Please correct the following errors:</font>
        </td></tr>
        <tr><td> 
          <ul>
            <xsl:for-each select="bebop:cell">
              <li><font color="{../@color}"><xsl:apply-templates/></font></li>
            </xsl:for-each>
          </ul>
        </td></tr>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
