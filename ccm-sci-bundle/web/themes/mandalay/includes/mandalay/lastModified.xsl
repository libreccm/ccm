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
  Hier werden die Auditing-Angeben verabeitet
-->

<!-- EN
  Processing auditing informations (lastModified)
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

  <!--<xsl:template name="mandalay:lastModified">-->
  <xsl:template match="auditing" mode="auditing">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:variable name="setDateAndTime">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setDateAndTime"/>
        <xsl:with-param name="module"  select="'lastModified'"/>
        <xsl:with-param name="setting" select="'setDateAndTime'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMailto">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setMailto"/>
        <xsl:with-param name="module"  select="'lastModified'"/>
        <xsl:with-param name="setting" select="'setMailto'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowEditor">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setShowEditor"/>
        <xsl:with-param name="module"  select="'lastModified'"/>
        <xsl:with-param name="setting" select="'setShowEditor'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowCreator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setShowCreator"/>
        <xsl:with-param name="module"  select="'lastModified'"/>
        <xsl:with-param name="setting" select="'setShowCreator'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowLongDate">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setShowLongDate"/>
        <xsl:with-param name="module"  select="'lastModified'"/>
        <xsl:with-param name="setting" select="'setShowLongDate'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/separator" />
        <xsl:with-param name="module" select="'lastModified'" />
        <xsl:with-param name="setting" select="'separator'" />
        <xsl:with-param name="default" select="' '" />
      </xsl:call-template>
    </xsl:variable>
    
    
    <!-- DE Nur anzeigen, wenn ein audition-Tag vorhanden ist -->
    <!-- EN Show only, if there is a auditing tag -->
    <!--<xsl:if test=".">-->
      <div id="lastModifiedDate">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'lastModified'"/>
          <xsl:with-param name="id" select="'lastModifiedAt'"/>
        </xsl:call-template>
        <xsl:choose>
          <xsl:when test="$setShowLongDate = 'true'">
            <xsl:value-of select="./lastModifiedDate/@longDate"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="./lastModifiedDate/@date"/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setDateAndTime = 'true'">
          &nbsp;
          <xsl:value-of select="./lastModifiedDate/@time"/>
        </xsl:if>

        <!-- DE Die erweiterten Angaben nur anzeigen, wenn der User angemeldet ist -->
        <!-- EN Show detailed information only for registered users -->
        <xsl:if test="$setShowEditor = 'true' and not($userName = '')">
          <div id="lastModifiedDetails">
            <span id="lastModifiedEditor">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'lastModified'"/>
                <xsl:with-param name="id" select="'lastModifiedBy'"/>
              </xsl:call-template>
              <xsl:choose>
                <xsl:when test="./lastModifiedUser">
                  <xsl:choose>
                    <xsl:when test="$setMailto = 'true'">
                      <a>
                        <xsl:attribute name="href">
                          <xsl:value-of select="concat('mailto:', ./lastModifiedUser/primaryEmail)"/>
                        </xsl:attribute>
                        <xsl:value-of select="./lastModifiedUser/displayName"/>
                      </a>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="./lastModifiedUser/displayName"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:choose>
                    <xsl:when test="$setMailto = 'true'">
                      <a>
                        <xsl:attribute name="href">
                          <xsl:value-of select="concat('mailto:', ./creationUser/primaryEmail)"/>
                        </xsl:attribute>
                        <xsl:value-of select="./creationUser/displayName"/>
                      </a>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="./creationUser/displayName"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:otherwise>
              </xsl:choose>
            </span>
            <xsl:if test="$setShowCreator = 'true' and not(./creationUser/id = ./lastModifiedUser/id)">
              <xsl:value-of select="$separator"/>
              <span id="lastModifiedCreator">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'lastModified'"/>
                  <xsl:with-param name="id" select="'createdBy'"/>
                </xsl:call-template>
                <xsl:choose>
                  <xsl:when test="$setMailto = 'true'">
                    <a>
                      <xsl:attribute name="href">
                        <xsl:value-of select="concat('mailto:', ./creationUser/primaryEmail)"/>
                      </xsl:attribute>
                      <xsl:value-of select="./creationUser/displayName"/>
                    </a>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="./creationUser/displayName"/>
                  </xsl:otherwise>
                </xsl:choose>
              </span>
            </xsl:if>
          </div>
        </xsl:if>
      </div>
    <!--</xsl:if>-->
  </xsl:template>

</xsl:stylesheet>
