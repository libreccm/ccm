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
  Hier werden die staticImages aus dem Layout verarbeitet 
-->

<!-- EN
  Processing staticImages from layout
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

  <xsl:template name="mandalay:staticImage">
    <xsl:choose>
      <xsl:when test="link">
        <a>
          <xsl:attribute name="href">
            <xsl:call-template name="mandalay:linkParser">
              <xsl:with-param name="link" select="link"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:attribute name="name">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="id" select="name"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="id" select="title"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:call-template name="mandalay:setImage"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="mandalay:setImage"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="mandalay:setImage">
    <img>
      <xsl:if test="src">
        <xsl:attribute name="src">
          <xsl:call-template name="mandalay:linkParser">
            <xsl:with-param name="link" select="src"/>
            <xsl:with-param name="prefix" select="$theme-prefix"/>
          </xsl:call-template>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="alt">
        <xsl:attribute name="alt">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="id" select="alt"/>
          </xsl:call-template>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="title">
        <xsl:attribute name="title">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="id" select="title"/>
          </xsl:call-template>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="name">
        <xsl:attribute name="name">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="id" select="name"/>
          </xsl:call-template>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="width">
        <xsl:attribute name="width">
          <xsl:value-of select="width"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="height">
        <xsl:attribute name="height">
          <xsl:value-of select="height"/>
        </xsl:attribute>
      </xsl:if>
<!--
      <xsl:if test="">
        <xsl:attribute name="">
          <xsl:value-of select=""/>
        </xsl:attribute>
      </xsl:if>
-->
    </img>
  </xsl:template>

</xsl:stylesheet>
