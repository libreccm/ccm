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
  Hier werden die Portlet Actions verarbeitet 
-->

<!-- EN
  Processing portlet actions
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  exclude-result-prefixes="xsl bebop cms nav mandalay portal portlet"
  version="1.0">

  <xsl:template match="portlet:action">
    <xsl:param name="src"/>
    <xsl:param name="title"/>
    <xsl:param name="alt"/>
    
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="@url"/>
      </xsl:attribute>
      <xsl:attribute name="title">
<!-- XXX -->        
        <xsl:value-of select="@name"/>
      </xsl:attribute>
      <img class="portletAction">
        <xsl:attribute name="src">
          <xsl:call-template name="mandalay:linkParser">
            <xsl:with-param name="link" select="$src"/>
            <xsl:with-param name="prefix" select="$theme-prefix"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:value-of select="$title"/>
        </xsl:attribute>
        <xsl:attribute name="alt">
          <xsl:value-of select="$alt"/>
        </xsl:attribute>
      </img>
    </a>
  </xsl:template>

</xsl:stylesheet>
