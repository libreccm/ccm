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
  Hier werden die Suchwörter verarbeitet 
-->

<!-- EN
  Processing search terms
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav mandalay" 
  version="1.0">
  
  <!-- DE Erzeuge das Eingabefeld für die Suchwörter -->
  <!-- EN Create a widget for the search terms -->
  <xsl:template match="search:terms">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setQueryPrefix">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setQueryPrefix"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setQueryPrefix'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setQueryPrefix = 'true'">
      <span class="query">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'query'"/>
        </xsl:call-template>
      </span>
    </xsl:if>
    <span class="terms">
      <input size="30" type="text" name="{@param}" value="{@value}" title="Enter one or more search terms"/>
      <xsl:apply-templates select="../bebop:formWidget"/>
    </span>
  </xsl:template>
  
</xsl:stylesheet>
