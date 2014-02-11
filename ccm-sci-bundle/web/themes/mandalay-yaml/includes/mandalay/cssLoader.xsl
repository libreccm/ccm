<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2014 Jens Pelzetter
  
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

<!--
  The templates in this file are loading the CSS files. For each applications different CSS files 
  can be loaded.
  
  The CSS files to load are defined using the file settings/css-files.xml.
-->


<!-- Autor: Jens Pelzetter -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:mandalay="http://mandalay.quasiweb.de"
                exclude-result-prefixes="xsl bebop cms nav mandalay"
                version="1.0">

  <xsl:template name="mandalay:cssLoader">
    <xsl:variable name="application">
      <xsl:choose>
        <xsl:when test="$resultTree/@application">
          <xsl:value-of select="$resultTree/@application"/>
        </xsl:when>
        <xsl:when test="$resultTree/@class">
          <xsl:value-of select="$resultTree/@class"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'none'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/application[@name=$application]">
        <xsl:for-each select="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/application[@name=$application]/css-file">
          <xsl:call-template name="mandalay:loadCssFile">
            <xsl:with-param name="filename" select="."/>
            <xsl:with-param name="media" select="./@media"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
          <xsl:for-each select="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/default/css-file">
          <xsl:call-template name="mandalay:loadCssFile">
            <xsl:with-param name="filename" select="."/>
            <xsl:with-param name="media" select="./@media"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
      
    <!-- Include IE Hacks only for very old IEs (IE 6) -->
    <xsl:if test="$msie_version >= '5' and $msie_version &lt; '7'">
      <xsl:choose>
      <xsl:when test="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/application[@name=$application]">
        <xsl:for-each select="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/application[@name=$application]/iehacks">
          <xsl:call-template name="mandalay:loadCssFile">
            <xsl:with-param name="filename" select="."/>
            <xsl:with-param name="media" select="./@media"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
          <xsl:for-each select="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/default/iehacks">
          <xsl:call-template name="mandalay:loadCssFile">
            <xsl:with-param name="filename" select="."/>
            <xsl:with-param name="media" select="./@media"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
    </xsl:if>
    
  </xsl:template>
  
  <xsl:template name="mandalay:loadCssFile">
    <xsl:param name="filename"/>
    <xsl:param name="media" select="''"/>
    
    <xsl:choose>
      <xsl:when test="string-length($media) &gt; 0">
        <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/{$media}/{$filename}" media="{$media}" />
      </xsl:when>
      <xsl:otherwise>
        <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/{$filename}" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
