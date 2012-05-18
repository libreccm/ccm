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

<!-- DE
  Hier werden das cmsImagesDisplay verarbeitet 
-->

<!-- EN
  Processing cmsImageDisplay
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
  
  <xsl:template match="cms:imageDisplay">
    <xsl:variable name="setImageName">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'cms'"/>
        <xsl:with-param name="setting" select="'imageDisplay/setImageName'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMimeType">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'cms'"/>
        <xsl:with-param name="setting" select="'imageDisplay/setMimeType'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDimensions">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'cms'"/>
        <xsl:with-param name="setting" select="'imageDisplay/setDimensions'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'cms'"/>
        <xsl:with-param name="setting" select="'imageDisplay/setCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <img class="cmsImageDisplay">
      <xsl:attribute name="src">
        <xsl:value-of select="@src"/>
      </xsl:attribute> 
      <xsl:attribute name="alt">
        <xsl:value-of select="@name"/>
      </xsl:attribute>
      <xsl:if test="@width">
        <xsl:attribute name="width">
          <xsl:value-of select="@width"/>
        </xsl:attribute>              
      </xsl:if>
      <xsl:if test="@height">
        <xsl:attribute name="height">
          <xsl:value-of select="@height"/>
        </xsl:attribute>              
      </xsl:if>
      <div class="cmsImageInfoOverlay">
        <xsl:if test="$setImageName = 'true'">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'imageDisplay/infoOverlay/name'"/>
            </xsl:call-template>
          </span>
          <span class="value"><xsl:value-of select="@name"/></span>
          <br/>
        </xsl:if>
        <xsl:if test="$setMimeType = 'true'">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'imageDisplay/infoOverlay/type'"/>
            </xsl:call-template>
          </span>
          <span class="value">
            <xsl:choose>
              <xsl:when test="@mime_type">
                <xsl:value-of select="@mime_type"/>
              </xsl:when>
              <xsl:otherwise>
                <em>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'imageDisplay/infoOverlay/typeUnknown'"/>
                  </xsl:call-template>
                </em>
              </xsl:otherwise>
            </xsl:choose>
          </span>
          <br/>
        </xsl:if>
        <xsl:if test="$setDimensions = 'true'">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'imageDisplay/infoOverlay/dimensions'"/>
            </xsl:call-template>
          </span>
          <span class="value"><xsl:value-of select="@height"/>x<xsl:value-of select="@width"/></span>
          <br/>
        </xsl:if>
        <xsl:if test="$setCaption = 'true'">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'imageDisplay/infoOverlay/caption'"/>
            </xsl:call-template>
          </span>
          <span class="value">
            <xsl:value-of select="@caption"/>
          </span>
        </xsl:if>
      </div>
    </img>
  </xsl:template>
  
</xsl:stylesheet>
