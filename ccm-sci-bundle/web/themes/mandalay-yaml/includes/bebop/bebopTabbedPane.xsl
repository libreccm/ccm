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
    Hier werden die Tabellenreiter verarbeitet 
-->

<!-- EN
    Processing tabbed panes
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

  <!-- DE Erzeuge einen Div-Container mit Tabellenreitern -->
  <!-- EN Create a div container with tab -->
  <xsl:template name="mandalay:bebopTabbedPane" match="bebop:tabbedPane">
    <xsl:param name="layoutTree" select="."/>
    
    <div id="bebopTabbedPane">
      <xsl:call-template name="mandalay:setIdAndClass">
        <xsl:with-param name="currentLayoutNode" select="$layoutTree"/>
      </xsl:call-template>
      <xsl:apply-templates select="bebop:tabStrip">
        <xsl:with-param name="setTabPaneMode">
          <!-- DE Hole alle benötigten Einstellungen-->
          <!-- EN Getting all needed setting-->
          <xsl:call-template name="mandalay:getSetting">
            <xsl:with-param name="node"  select="$layoutTree/setTabPaneMode"/>
            <xsl:with-param name="module"  select="'bebop'"/>
            <xsl:with-param name="setting" select="'tabPane/setMode'"/>
            <xsl:with-param name="default" select="'horizontal'"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:apply-templates>
    </div>
  </xsl:template>
  
  <!-- DE Erzeuge die Tabellenreiter, optional auch vertikal -->
  <!-- EN Create the tabstrib, vertically if wanted -->
  <xsl:template name="mandalay:bebopTabStrip" match="bebop:tabStrip">
    <xsl:param name="setTabPaneMode" select="'horizontal'"/>
        
    <xsl:choose>
      <!-- DE Eine vertikale Tabulatorsammlung wird mit einer UL erzeugt -->
      <!-- EN A vrertical tab strib is made with an ul -->
      <xsl:when test="$setTabPaneMode='vertical'">
        <ul>
          <xsl:apply-templates mode="vertical"/>
        </ul>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates mode="horizontal"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- DE Kapsle einen vertikalen Tabulator in li -->
  <!-- EN enclose a vertikal tab in li -->
  <xsl:template match="bebop:tab" mode="vertical">
    <li class="bebopTab">
      <xsl:apply-templates select="."/>
    </li>   
  </xsl:template>
  
  <!-- DE Kapsle die einzelen Tabulatoren in spans -->
  <!-- EN Enclose the tabs in spans --> 
  <xsl:template match="bebop:tab" mode="horizontal">
    <span class="bebopTab">
      <xsl:apply-templates select="."/>
    </span>
    &nbsp;
  </xsl:template>
  
  <!-- DE Verarbeite den Inhalt eines Tabs -->
  <!-- EN Process the contents of a tab -->
  <xsl:template match="bebop:tab">
    <xsl:attribute name="class">
      <xsl:text>bebopTab </xsl:text>
      <xsl:value-of select="@key"/>
      <xsl:if test="@current='t'">
        <xsl:text> current</xsl:text>
      </xsl:if>
    </xsl:attribute>
    <xsl:choose>
      <xsl:when test="@current='t'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <a href="{@href}">
          <xsl:attribute name="title"><xsl:value-of select="bebop:label"/></xsl:attribute>
          <xsl:apply-templates/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- DE Erzeuge den Div-Container für Inhalt des aktuellen Tabellenreiters -->
  <!-- EN Create the div container for the content of the current tab -->
  <xsl:template match="bebop:currentPane">
    <div id="bebopCurrentPane">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
    
</xsl:stylesheet>
