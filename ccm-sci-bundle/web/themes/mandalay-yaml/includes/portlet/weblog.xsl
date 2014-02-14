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
  Hier wird das Portlet Weblog verarbeitet 
-->

<!-- EN
  Processing portlet weblog
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

  <xsl:template match="portlet:weblog" mode="setHeading">
    <xsl:variable name="setHeading">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'portletWeblog'"/>
        <xsl:with-param name="setting" select="'setHeading'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:value-of select="$setHeading"/>
  </xsl:template>
  
  <xsl:template match="bebop:portlet[@bebop:classname='org.undp.weblog.ui.WebLogPortlet' and //bebop:page/@id='viewWorkspace']">
    <div class="portletWeblog">
      <h4>
        <xsl:value-of select="@title"/>
      </h4>
      <xsl:apply-templates/>
      <xsl:for-each select="@applicationlink">
        <a>
          <xsl:attribute name="href">
            <xsl:call-template name="mandalay:linkParser">
              <xsl:with-param name="link" select="."/>
              <xsl:with-param name="prefix" select="''"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'portlet'"/>
              <xsl:with-param name="id" select="'weblog/viewAll/title'"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'portlet'"/>
            <xsl:with-param name="id" select="'weblog/viewAll/link'"/>
          </xsl:call-template>
        </a>
        <br/>
      </xsl:for-each>
    </div>
  </xsl:template>

</xsl:stylesheet>