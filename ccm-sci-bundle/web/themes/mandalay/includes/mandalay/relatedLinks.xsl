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
  Hier werden die relatedLinks verarbeitet 
-->

<!-- EN
  Processing relatedLinks
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

  <xsl:template name="mandalay:relatedLinks">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:variable name="linkListName">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/linkListName"/>
        <xsl:with-param name="module"  select="'relatedLinks'"/>
        <xsl:with-param name="setting" select="'linkListName'"/>
        <xsl:with-param name="default" select="'NONE'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="$resultTree//cms:contentPanel/cms:item/links[linkListName = $linkListName] or $resultTree//nav:greetingItem/cms:item/links[linkListName = $linkListName] or $resultTree//cms:item/links[linkListName = $linkListName]">

      <!-- DE Hole alle benötigten Einstellungen-->
      <!-- EN Getting all needed setting-->
      <xsl:variable name="setHeading">
        <xsl:call-template name="mandalay:getSetting">
          <xsl:with-param name="node"  select="$layoutTree/setHeading"/>
          <xsl:with-param name="module"  select="'relatedLinks'"/>
          <xsl:with-param name="setting" select="'setHeading'"/>
          <xsl:with-param name="default" select="'true'"/>
        </xsl:call-template>
      </xsl:variable>

      <div class="relatedLinks">
        <xsl:if test="$setHeading='true'">
          <h2>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'relatedLinks'"/>
            <xsl:with-param name="id" select="'heading'"/>
          </xsl:call-template>
          </h2>
        </xsl:if>
        <ul>
          <xsl:for-each select="$resultTree//cms:item/links[linkListName = $linkListName]">
            <xsl:sort select="linkOrder" data-type="number" />
            <xsl:if test="(targetType='internalLink' and targetItem) or (targetType='externalLink' and targetURI)">
              <li>
                <xsl:choose>
                  <xsl:when test="targetType='externalLink'">
                    <!-- DE Extere Links haben keinen Contenttyp, deshalb muß hier eine Sonderbebandlung vorgenommen werden -->
                    <!-- EN -->
                    <a>
                      <xsl:attribute name="href"><xsl:value-of select="targetURI"/></xsl:attribute>
                      <xsl:attribute name="title"><xsl:value-of select="./linkDescription" /></xsl:attribute>
                      <xsl:value-of disable-output-escaping="yes" select="./linkTitle" />
                    </a>
                    <xsl:if test="./linkDescription">
                      <br />
                      <xsl:value-of select="./linkDescription" />
                    </xsl:if>
                  </xsl:when>
                  <xsl:otherwise>
                    <!-- DE Die Darstellung des Inhaltes wird durch die XSL-Datei des Contenttyps übernommen -->
                    <!-- EN -->
                    <xsl:apply-templates select="." mode="link_view"/>
                  </xsl:otherwise>
                </xsl:choose>
              </li>
            </xsl:if>
          </xsl:for-each>
        </ul>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
