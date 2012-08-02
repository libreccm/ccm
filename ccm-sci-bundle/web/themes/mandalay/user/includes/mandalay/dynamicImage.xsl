<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
    xmlns:cms="http://www.arsdigita.com/cms/1.0"
    xmlns:nav="http://ccm.redhat.com/navigation"
    xmlns:mandalay="http://mandalay.quasiweb.de"
    exclude-result-prefixes="xsl bebop cms mandalay nav"
    version="1.0">

  <xsl:template name="mandalay:dynamicImage">
   
    <xsl:variable name="path">
      <xsl:call-template name="mandalay:dynamicImagePath"/>
    </xsl:variable>

    <xsl:variable name="src">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="''"/>
        <xsl:with-param name="module" select="@class"/>
        <xsl:with-param name="setting" select="concat($path, 'src')"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="alt">
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="@class"/>
        <xsl:with-param name="id" select="concat($path, 'alt')"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="title">
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="@class"/>
        <xsl:with-param name="id" select="concat($path, 'title')"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="name">
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="@class"/>
        <xsl:with-param name="id" select="concat($path, 'name')"/>
      </xsl:call-template>
    </xsl:variable>

    <img>
      <xsl:if test="$src != ''">
        <xsl:attribute name="src">
          <xsl:call-template name="mandalay:linkParser">
            <xsl:with-param name="link" select="$src"/>
            <xsl:with-param name="prefix" select="$theme-prefix"/>
          </xsl:call-template>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$alt != ''">
        <xsl:attribute name="alt">
          <xsl:value-of select="$alt"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$title != ''">
        <xsl:attribute name="title">
          <xsl:value-of select="$title"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$name != ''">
        <xsl:attribute name="name">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>
  
  <xsl:template name="mandalay:dynamicImagePath">
    <xsl:param name="position" select="count($resultTree//nav:categoryPath/nav:category)"/>
    <xsl:variable name="path" select="substring-after($resultTree//nav:categoryPath/nav:category[position() = $position]/@url, 'ccm/')"/>

    <xsl:variable name="foundSetting">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="''"/>
        <xsl:with-param name="module" select="@class"/>
        <xsl:with-param name="setting" select="concat($path, 'src')"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="newPath">
      <xsl:choose>

        <xsl:when test="$foundSetting = '' and $position > 0 ">
          <xsl:call-template name="mandalay:dynamicImagePath">
            <xsl:with-param name="position" select="$position -1"/>
          </xsl:call-template>
        </xsl:when>

        <xsl:when test="$position = 0">
          <xsl:value-of select="'default'"/>
        </xsl:when>

        <xsl:otherwise>
          <xsl:value-of select="$path"/>
        </xsl:otherwise>

      </xsl:choose>
    </xsl:variable>
    
    <xsl:value-of select="$newPath"/>
  </xsl:template>
</xsl:stylesheet>
