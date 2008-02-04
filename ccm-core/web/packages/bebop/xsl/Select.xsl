<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:output method="html" indent="yes"/>

  <xsl:template match="bebop:select[@class='displayOneOptionAsLabel']"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <xsl:choose>
      <xsl:when test="count(bebop:option)=1">
        <xsl:choose>
          <xsl:when test="bebop:option/@label">
            <xsl:value-of select="bebop:option/@label"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="bebop:option/bebop:label"/>
          </xsl:otherwise>
        </xsl:choose>
        <input type="hidden" name="{@name}" value="{bebop:option/@value}" />
      </xsl:when>
      <xsl:otherwise>
        <!-- copy-pasting the code here is wrong ! -->
        <select name="{@name}">
          <xsl:for-each select="@size|@*[starts-with(name(), 'on')]">
            <xsl:attribute name="{name()}">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:for-each>
          <xsl:apply-templates/>
        </select>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>
  
  <xsl:template match="bebop:select"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <select name="{@name}">
      <xsl:for-each select="@disabled|@size|@title|@*[starts-with(name(), 'on')]">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates/>
    </select>
  </xsl:template>
  
  <xsl:template match="bebop:option"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <option>
      <xsl:for-each select="@*[not(name()='label')]">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="bebop:label" />
    </option>
  </xsl:template>

</xsl:stylesheet>
