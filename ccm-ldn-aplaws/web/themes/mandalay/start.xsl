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

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:atoz="http://xmlns.redhat.com/atoz/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  xmlns:theme="http://ccm.redhat.com/themedirector"
    exclude-result-prefixes="xsl atoz bebop nav mandalay theme" 
  version="1.0">

  <!-- DE globalVars.xsl importieren, um alle wichtigen Einstellungen und Variablen zu erhalten -->
  <!-- EN Importing globalVars.xsl to get important settings and variables -->
<!--  <xsl:import href="includes/mandalay/globalVars.xsl"/> -->

  <xsl:import href="includes/bebop.xsl"/>
  <xsl:import href="includes/cms.xsl"/>
  <xsl:import href="includes/docs.xsl"/>
  <xsl:import href="includes/forum.xsl"/>
  <xsl:import href="includes/mandalay.xsl"/>
  <xsl:import href="includes/navigation.xsl"/>
  <xsl:import href="includes/portal.xsl"/>
  <xsl:import href="includes/portlet.xsl"/>
  <xsl:import href="includes/search.xsl"/>
  <xsl:import href="includes/subsite.xsl"/>
  <xsl:import href="includes/terms.xsl"/>
  <xsl:import href="includes/theme.xsl"/>
  <xsl:import href="includes/types.xsl"/>
  
  <!-- DE fallbackEntryPoints.xsl importieren. Dort werden alle Einstiegspunkte definiert, die Mandalay noch nicht selber unterstützt-->
  <!-- EN Importing fallbackEntryPoints.xsl for entrypoint not yet supported by Mandalay -->
<!--
  <xsl:import href="fallback/fallbackEntryPoints.xsl"/>
-->

  <!-- DE Importiere ggf. weitere benutzerspezifische Module aus user Verzeichnis -->
  <!-- EN Import user specific modules from user directory -->
  <xsl:import href="user/start.xsl"/>

  <!-- Output-Methode -->
  <!-- Wegen der Problmatik bei XHTML auf HTML 4.01 Strict geändert -->
<!--
  <xsl:output 
    method="html"
    doctype-public="-//W3C//DTD HTML 4.01//EN"
    doctype-system="http://www.w3.org/TR/html4/strict.dtd"
    indent="yes"
    encoding="utf-8"
  />
-->
  <xsl:output 
    method="html"
    indent="yes"
    encoding="utf-8"
  />

  <!-- DE Seiten-Layout aufrufen. Alles weitere wird von dort aus geregelt (Ändert sich evt. noch ?) -->
  <!-- EN Call page layout. Everything else goes from there (Will be changed maybe ?) -->
  <xsl:template match="bebop:page">

    <xsl:variable name="application">
      <xsl:choose>
        <xsl:when test="./@application">
          <xsl:value-of select="@application"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>none</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="class" select="@class"/>

    <!-- DEBUG only -->
    <xsl:if test="$showDebug='true'">
      Application: <xsl:value-of select="$application"/> <br/>
      Class: <xsl:value-of select="@class"/> <br/>
      Layout-File: <xsl:value-of select="document(concat($theme-prefix, '/settings/start.xml'))/settings/entrypoint[@application=$application and @class=$class]"/> <br />
      Lang: <xsl:value-of select="$lang"/> <br />
      Negotiated Locale: <xsl:value-of select="$negotiated-language"/> <br />
      User-Agent: <xsl:value-of select="$user-agent"/><br />
      Theme-Prefix: <xsl:value-of select="$theme-prefix"/><br />
      Context-Prefix: <xsl:value-of select="$context-prefix"/><br />
      Dispatcher-Prefix: <xsl:value-of select="$dispatcher-prefix"/><br />
      html strict doctype
    </xsl:if>

    <!-- DE Lese die Layout-Dateien aus der start.xml aus -->
    <!-- EN Get the layout-files from start.xml -->
    <xsl:choose>
      <xsl:when test="document(concat($theme-prefix, '/settings/start.xml'))/settings/entrypoint[@application=$application and @class=$class]">
        <xsl:call-template name="mandalay:layoutParser">
          <xsl:with-param name="layoutFile" select="document(concat($theme-prefix, '/settings/start.xml'))/settings/entrypoint[@application=$application and $class=@class]"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>Nicht unterstützt</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- DE navigation.xsl call-back-Funktion um spezielle Navigationspunkte vor der eigentlichen Navigation einzufügen -->
  <!-- EN navigation.xsl call-back function to add some special navigation elements prior the standard navigation -->
  <xsl:template name="mandalay:navAddOn">
  </xsl:template>

</xsl:stylesheet>
