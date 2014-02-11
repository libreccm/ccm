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
  Hier werden die Informationen aus dem UserBanner verarbeitet 
-->

<!-- EN
  Processing informations from user banner
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav ui mandalay"
  version="1.0">

  <xsl:template name="mandalay:userBanner">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setGreetingString">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGreetingString"/>
        <xsl:with-param name="module"  select="'userBanner'"/>
        <xsl:with-param name="setting" select="'setGreetingString'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDefaultGreetingString">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setDefaultGreetingString"/>
        <xsl:with-param name="module"  select="'userBanner'"/>
        <xsl:with-param name="setting" select="'setDefaultGreetingString'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLoginString">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setLoginString"/>
        <xsl:with-param name="module"  select="'userBanner'"/>
        <xsl:with-param name="setting" select="'setLoginString'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="not($userName = '')">
        <div id="welcomeText">
          <xsl:if test="$setGreetingString = 'true' and $setDefaultGreetingString = 'true'">
            <xsl:value-of select="$resultTree/ui:userBanner/@greeting"/>
          </xsl:if>
          <xsl:if test="$setGreetingString = 'true' and $setDefaultGreetingString = 'false'">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'userBanner'"/>
              <xsl:with-param name="id" select="'greetingString'"/>
            </xsl:call-template>
          </xsl:if>
          <xsl:value-of select="$userName"/>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$setLoginString = 'true'">
          <div id="welcomeText">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'userBanner'"/>
              <xsl:with-param name="id" select="'loginString'"/>
            </xsl:call-template>
          </div>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- DE Zeige den Login- bzw. Logout-Link an, abhängig vom aktuellen ui:userBanner Status -->
  <!-- EN Show login or logout link depending of current ui:userBanner status -->
  <xsl:template name="mandalay:loginLogout">
    <xsl:param name="layoutTree" select="."/>
    
    <span>
      <xsl:choose>
        <xsl:when test="$userName != ''">
          <xsl:attribute name="class">
            <xsl:call-template name="mandalay:getSetting">
              <xsl:with-param name="node"  select="$layoutTree/logoutClass"/>
              <xsl:with-param name="module"  select="'userBanner'"/>
              <xsl:with-param name="setting" select="'logoutClass'"/>
              <xsl:with-param name="default" select="'logout'"/>
            </xsl:call-template>
          </xsl:attribute>
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="$resultTree/ui:userBanner/@logoutURL"/>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'userBanner'"/>
                <xsl:with-param name="id" select="'useLogin/logout/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'userBanner'"/>
              <xsl:with-param name="id" select="'useLogin/logout/link'"/>
            </xsl:call-template>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class">
            <xsl:call-template name="mandalay:getSetting">
              <xsl:with-param name="node"  select="$layoutTree/loginClass"/>
              <xsl:with-param name="module"  select="'userBanner'"/>
              <xsl:with-param name="setting" select="'loginClass'"/>
              <xsl:with-param name="default" select="'login'"/>
            </xsl:call-template>
          </xsl:attribute>
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="$resultTree/ui:userBanner/@loginURL"/>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'userBanner'"/>
                <xsl:with-param name="id" select="'useLogin/login/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'userBanner'"/>
              <xsl:with-param name="id" select="'useLogin/login/link'"/>
            </xsl:call-template>
          </a>
        </xsl:otherwise>
      </xsl:choose>
    </span>
  </xsl:template>

</xsl:stylesheet>
