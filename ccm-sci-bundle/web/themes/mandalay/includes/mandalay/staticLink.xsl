<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<!-- DE
  Hier wird ein Link aus den XML-Settings erzeugt
-->

<!-- EN
  Creates a link from XML-Settings
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0">

  <xsl:template name="mandalay:staticLink">

    <!-- DE Hole den aktuellen Modulenamen aus dem LayoutTree -->
    <!-- EN Get current module name from layoutTree-->
    <xsl:variable name="module">
      <xsl:choose>
        <xsl:when test="./@name">
          <xsl:value-of select="./@name"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'staticLinks'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <a>
      <xsl:attribute name="href">
        <xsl:call-template name="mandalay:linkParser">
          <xsl:with-param name="link" select="./link"/>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:if test="./accesskey and ./acceskey != ''">
        <xsl:attribute name="accesskey"><xsl:value-of select="./accesskey"/></xsl:attribute>
      </xsl:if>
      <xsl:attribute name="title">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="$module"/>
          <xsl:with-param name="id" select="concat('menu/name/', ./name)"/>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="$module"/>
        <xsl:with-param name="id" select="concat('menu/name/', ./name)"/>
      </xsl:call-template>
    </a>
  </xsl:template>

</xsl:stylesheet>
