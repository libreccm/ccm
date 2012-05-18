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
  Hier wird die Verabeitung des Forums begonnen 
-->

<!-- EN
  Start processing of forum
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
  xmlns:forum="http://www.arsdigita.com/forum/1.0"
  exclude-result-prefixes="xsl bebop cms nav mandalay portal portlet forum"
  version="1.0">

  <xsl:template match="forum:name">
    <h1>
      <xsl:value-of select="."/>
    </h1>
  </xsl:template>
  
  <xsl:template match="forum:introduction">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:variable name="setIntroduction">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setIntroduction"/>
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="setting" select="'setIntroduction'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setIntroduction = 'true' and . != ''">
      <div class="forumIntroduction">
        <xsl:value-of disable-output-escaping="yes" select="."/>
      </div>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="forum:forum">
    <div class="forumTabbedPane">
      <xsl:apply-templates select="forum:forumMode"/>
    </div>
    &nbsp;
    <xsl:apply-templates select="*[not(self::forum:forumMode)]"/>
<!--
    <xsl:apply-templates select="*[not(self::forum:forumMode or self::bebop:form[@name = 'adminUserPicker'] or self::bebop:form[@name = 'userPicker'])]"/>
-->
  </xsl:template>
  
  <xsl:template match="forum:forumAlerts">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:variable name="setAlerts">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setAlerts"/>
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="setting" select="'setAlerts'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setAlerts = 'true'">
      <h3>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'forum'"/>
          <xsl:with-param name="id" select="'alerts/forum/heading'"/>
        </xsl:call-template>
      </h3>
      <xsl:apply-templates/>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
