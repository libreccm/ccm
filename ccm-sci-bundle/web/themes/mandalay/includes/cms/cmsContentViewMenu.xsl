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
  Hier wird die globale Navigation des CMS verarbeitet 
-->

<!-- EN
  Processing global navigation for cms
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
  
  <!-- DE Das Menü -->
  <!-- EN The menu -->
  <xsl:template match="showCMSContentViewMenu">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:variable name="setLayout">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setLayout"/>
        <xsl:with-param name="module"  select="'cms'"/>
        <xsl:with-param name="setting" select="'contentViewMenu/setLayout'"/>
        <xsl:with-param name="default" select="'horizontal'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$setLayout = 'horizontal'">
        <xsl:apply-templates select="$resultTree//*[contains(@class, 'cmsContentViewMenu')]"/>
      </xsl:when>
      <xsl:otherwise>
        <ul>
        <xsl:for-each select="$resultTree//*[contains(@class, 'cmsContentViewMenu')]">
          <li>
            <xsl:apply-templates/>
          </li>
        </xsl:for-each>
        </ul>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
