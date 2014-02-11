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
  Hier werden die Theme-Dateien verarbeitet 
-->

<!-- EN
  Processing theme files
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:mandalay="http://mandalay.quasiweb.de"
  xmlns:theme="http://ccm.redhat.com/themedirector/1.0"
  exclude-result-prefixes="xsl bebop cms nav mandalay theme" 
  version="1.0">
  
  <xsl:template match="theme:file">
    <xsl:variable name="setConfirmDelete">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'theme'"/>
        <xsl:with-param name="setting" select="'setConfirmDelete'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFileInfo">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'theme'"/>
        <xsl:with-param name="setting" select="'setFileInfo'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <li>
      <span class="file">
        <xsl:choose>
          <xsl:when test="@inWhiteList='false'">
            <strike>
              <i>
                <xsl:value-of select="@name"/>
              </i>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'theme'"/>
                <xsl:with-param name="id" select="'filemode/ignored'"/>
              </xsl:call-template>
            </strike>
          </xsl:when>

          <xsl:when test="@isDeleted='true'">
            <strike>
                <xsl:value-of select="@name"/>
            </strike>
            <span style="color:red;">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'theme'"/>
                <xsl:with-param name="id" select="'filemode/deleted'"/>
              </xsl:call-template>
            </span>
          </xsl:when>

          <xsl:when test="@removeURL">
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="@removeURL"/>
              </xsl:attribute>
              <xsl:if test="$setConfirmDelete = 'true'">
                <xsl:attribute name="onclick">
                  <xsl:text>return confirm('</xsl:text>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'theme'"/>
                    <xsl:with-param name="id" select="'delete/confirmMessage'"/>
                  </xsl:call-template>
                  <xsl:text>');</xsl:text>
                </xsl:attribute>
              </xsl:if>
              <xsl:attribute name="title">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'theme'"/>
                  <xsl:with-param name="id" select="'delete/title'"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:value-of select="@name"/>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'theme'"/>
                <xsl:with-param name="id" select="'delete/link'"/>
              </xsl:call-template>
            </a>
          </xsl:when>
          
          <xsl:otherwise>
            <xsl:value-of select="@name"/>
          </xsl:otherwise>
        </xsl:choose>
      </span>
      <xsl:if test="$setFileInfo = 'true'">
        <span class="fileInfo">
          <br/>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'theme'"/>
            <xsl:with-param name="id" select="'fileinfo/size'"/>
          </xsl:call-template>
          <xsl:value-of select="@size"/>
          <xsl:text> - </xsl:text> 
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'theme'"/>
            <xsl:with-param name="id" select="'fileinfo/date'"/>
          </xsl:call-template>
          <xsl:value-of select="@lastModified"/>
        </span>
      </xsl:if>
    </li>  
  </xsl:template>
  
</xsl:stylesheet>
