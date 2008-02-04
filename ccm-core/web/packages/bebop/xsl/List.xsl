<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>


 <xsl:template match="bebop:list[@layout='vertical']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <table>
      <xsl:for-each select="@*">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>

      <xsl:for-each select="bebop:cell">
        <tr><td><xsl:apply-templates/></td></tr>
      </xsl:for-each>
    </table>
 </xsl:template>


 <xsl:template match="bebop:list[@layout='horizontal']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <table>
      <xsl:for-each select="@*">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>

      <tr>
        <xsl:for-each select="bebop:cell">
          <td><xsl:apply-templates/></td>
        </xsl:for-each>
      </tr>
    </table>
 </xsl:template>

</xsl:stylesheet>
