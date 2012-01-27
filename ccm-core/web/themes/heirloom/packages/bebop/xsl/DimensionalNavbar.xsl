<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	          xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  exclude-result-prefixes="bebop"
                  version="1.0">

  <xsl:output method="html" indent="yes"/>

  <xsl:template match="bebop:dimensionalNavbar">
    <xsl:comment>bebop:dimensionalNavbar</xsl:comment>
    <xsl:value-of select="@startTag"/>
    <xsl:for-each select="*">
      <xsl:apply-templates select="."/>
      <xsl:if test="position()!=last()">
        <xsl:choose>
          <xsl:when test="string-length(../@delimiter)=0">
            &#160;&gt;&#160;
          </xsl:when>
          <xsl:otherwise><xsl:value-of select="../@delimiter"/></xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:for-each>
    <xsl:value-of select="@endTag"/>
    <xsl:comment>/bebop:dimensionalNavbar</xsl:comment>
  </xsl:template>

  <xsl:template match="bebop:dimensionalNavbar[@id='global-navbar']">
    <xsl:comment>bebop:dimensionalNavbar @id='global-navbar'</xsl:comment>
    <xsl:for-each select="*">
      <xsl:apply-templates select="."/>
      <xsl:if test="position()!=last()">&#160;-&#160;</xsl:if>
    </xsl:for-each>
    <xsl:comment>/bebop:dimensionalNavbar @id='global-navbar'</xsl:comment>
  </xsl:template>

  <!-- Top-right links, in the form of a bebop:dimensionalNavbar -->
  <xsl:template match="bebop:dimensionalNavbar[@class='top-right']">
    <xsl:value-of select="@startTag"/>
    <xsl:for-each select="*">
      <xsl:apply-templates select="."/>
      <xsl:if test="not(position()=last())">
        <xsl:value-of select="../@delimiter"/>
      </xsl:if>
    </xsl:for-each>
    <xsl:value-of select="@endTag"/>
  </xsl:template>


  <!-- dimensional navbar
  <xsl:template match="bebop:dimensionalNavbar">
    <table border="0" cellpadding="2" cellspacing="0" width="100%">
      <tr>
        <td align="{@align}">
          <font size="2" face="tahoma,verdana,arial,helvetica">
            <xsl:value-of select="@startTag"/>
            <xsl:for-each select="*">
              <xsl:apply-templates select="."/>
              <xsl:if test="not(position()=last())">
                <xsl:value-of select="../@delimiter"/>
              </xsl:if>
            </xsl:for-each>
            <xsl:value-of select="@endTag"/>
          </font>
        </td>
      </tr>
    </table>   
  </xsl:template>
  -->

</xsl:stylesheet>
