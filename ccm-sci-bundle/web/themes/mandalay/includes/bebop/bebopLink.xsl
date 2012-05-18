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
    Hier werden die Bebop-Links verarbeitet 
    Hier werden zwei verschiedene Links verwendet. href_no_javascript ist dabei 
    der Link, der im folgenden kein Javascript verwendet. href verwendet hingegen 
    Javascript. Der onClick-Event-Handler überschreibt dabei den Standardlink mit 
    der Javascript-Variante, wenn Javascript ausgeführt wird.
    Zudem wird der Mandalay Linkparser verwendet, um die korrekte Formatierung der
    Links zu gewährleisten und processAttributes, um alle angegebenen Attribute zu
    übernehmen.
-->

<!-- EN
    Processing bebop links
    There are to types of links. href_no_javascript is the links, that will not use
    javascript in the following. The onClick event handler is used to overwrite the
    standard link wih the javascript version, if javascript is running.
    Additionally the link parser of mandalay is called to check the formatting of 
    the link and processAttributes to insert all attributes to the html. 
-->

<!-- Autor: Sören Bernstein -->


<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav" 
  version="1.0">

  <!-- DE Hier werden die Links verarbeitet -->
  <!-- EN Processing links -->
  <xsl:template name="bebop:link" match="bebop:link">
    <xsl:param name="alt"/>
    <xsl:param name="title"/>
    <xsl:param name="src"/>
    
    <!-- DE  -->
    <!-- EN  -->
    <xsl:variable name="onclick">
      <xsl:choose>
        <xsl:when test="boolean(@onclick)=true() and not(starts-with(@onclick, 'return'))">
          <xsl:value-of select="@onclick" disable-output-escaping="yes"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>this.href='</xsl:text>
          <xsl:value-of select="@href" disable-output-escaping="yes"/>
          <xsl:text>'; </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <!-- DE DoubleClickProtection für Links, wenn es keinen OnClick-Handler gibt -->
    <!-- EN DoubleClickProtection for links without an onclick handler -->
    <xsl:variable name="dcp">
      <xsl:if test="$dcp-on-links and boolean(@onclick)=false()">
        <xsl:text>doubleClickProtect(this); </xsl:text>
      </xsl:if>
    </xsl:variable>
    
    <!-- DE Wenn es ein Link mit Bestätigung ist -->
    <!-- EN A link with confirmation -->
    <xsl:variable name="confirm">
      <xsl:if test="boolean(@confirm)=true() or starts-with(@onclick, 'return')">
        <xsl:call-template name="mandalay:string-replace">
          <xsl:with-param name="string" select="@onclick"/>
          <xsl:with-param name="from" select="'\'"/>
          <xsl:with-param name="to" select="''"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    
    <a>
      <xsl:call-template name="mandalay:processAttributes"/>
      <xsl:attribute name="href">
        <xsl:call-template name="mandalay:linkParser">
          <xsl:with-param name="link" select="@href_no_javascript"/>
        </xsl:call-template>
      </xsl:attribute>
      
      <xsl:attribute name="onclick">
        <xsl:value-of select="$onclick"/>  
        <xsl:value-of select="$dcp"/>
        <xsl:value-of select="$confirm"/>  
      </xsl:attribute>
      
      <xsl:if test="$src">
        <img alt="{$alt}" title="{$title}" src="{$src}"/>
      </xsl:if>
      <xsl:apply-templates/>
    </a>
  </xsl:template>
    
</xsl:stylesheet>
