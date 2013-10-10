<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
     Copyright 2011, Jens Pelzetter
         
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

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:ppp="http://www.arsdigita.com/PublicPersonalProfile/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav ppp mandalay"
  version="1.0"
  >

  <xsl:template match="ppp:profile">
    <xsl:apply-templates select="ppp:ownerName"/>
    <xsl:apply-templates select="ppp:profileImage"/>
  </xsl:template>

  <xsl:template match="ppp:ownerName">
    <div class="profileName">
      <h1><xsl:value-of select="."/></h1>
    </div>
<!--
    <xsl:choose>
      <xsl:when test="name(..) = 'title'">
        <xsl:call-template name="mandalay:title"/>
      </xsl:when>
      <xsl:otherwise>
        <h2>
          <xsl:call-template name="mandalay:title"/>
        </h2>
      </xsl:otherwise>
    </xsl:choose>
-->
  </xsl:template>

  <xsl:template match="ppp:profileImage">
    <xsl:variable name="showProfileImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'showProfileImage'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="$showProfileImage = 'true'">
      <div class="profileImage">
        <xsl:call-template name="mandalay:imageAttachment">
          <xsl:with-param name="showCaption" select="'false'"/>
          <xsl:with-param name="setZoomLink" select="'false'"/>
          <xsl:with-param name="setTextZoomLink" select="'false'"/>
          <xsl:with-param name="maxWidth">
            <xsl:call-template name="mandalay:getSetting">
              <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
              <xsl:with-param name="setting" select="'showProfileMaxWidth'"/>
              <xsl:with-param name="default" select="'200'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="maxHeight">
            <xsl:call-template name="mandalay:getSetting">
              <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
              <xsl:with-param name="setting" select="'showProfileMaxHeight'"/>
              <xsl:with-param name="default" select="'200'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
