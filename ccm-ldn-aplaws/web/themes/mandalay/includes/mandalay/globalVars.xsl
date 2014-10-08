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

<!--
Hier werden die globalen Variablen eingerichtet
Das sind zum einen die Variablen, die aus CCM stammen, wie z.B. $dispatcher-prefix,
zum anderen Variablen und Einstellungen speziell für das Mandalay-Theme. 
-->


<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns:mandalay="http://mandalay.quasiweb.de"
                exclude-result-prefixes="xsl bebop cms ui mandalay"
                version="1.0">

  <!-- DE Parameter aus CCM -->
  <!-- EN CCM environment parameter -->
  <xsl:param name="theme-prefix"/>
  <xsl:param name="context-prefix"/>
  <xsl:param name="dispatcher-prefix"/>
  
  <!-- DE ...für die Browserweiche -->
  <!-- EN ...for browser switch -->
  <xsl:param name="user-agent"/>

  <!-- DE ...für die ausgehandelte Sprache -->
  <!-- EN ...for negotiated language -->
  <xsl:param name="negotiated-language"/>

  <!-- DE ...für die Double Click Protection -->
  <!-- EN ...for double click protection -->
  <xsl:param name="dcp-on-buttons"/>
  <xsl:param name="dcp-on-links"/>
  

  <!-- DE Variable für die unterstützten Sprachen, die erste ist die Default-Sprache -->
  <!-- EN Supported Languages. First is default language -->
  <xsl:variable name="languages">
    <xsl:call-template name="mandalay:getSetting">
      <xsl:with-param name="setting" select="'languages'"/>
      <xsl:with-param name="default" select="'[de] en'"/>
    </xsl:call-template>
  </xsl:variable>

  <!-- DE Hole den Inhalt von language -->
  <!-- EN Get content of language -->
  <xsl:variable name="lang">
    
    <!-- DE Wähle den besten Wert für die Sprachwahl -->
    <xsl:variable name="language">
      <xsl:choose>
        <xsl:when test="string-length($negotiated-language) > 0">
          <xsl:value-of select="$negotiated-language"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="//language"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- DE Prüfe, ob die Sprache unterstützt wird -->
    <!-- EN Test, if language is supported -->
    <xsl:choose>
      <xsl:when test="contains($languages, $language) and $language != ''">
        <xsl:value-of select="$language"/>
      </xsl:when>

      <!-- DE Wenn nicht, setze die in eckigen Klammern [] definierte Standardsprache -->
      <!-- EN else, set default language (defined in Brackets []) -->
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="contains($languages, '[') and contains($languages, ']')">
            <xsl:value-of select="substring-before(substring-after($languages, '['), ']')"/>
          </xsl:when>
          
          <!-- DE Falls die Standardsprache nicht definiert ist, dann nehme die erste in der Liste -->
          <!-- EN If there isn't set a default language, choose the first entry -->
          <xsl:when test="contains($languages, ' ')">
            <xsl:value-of select="substring-before($languages, ' ')"/>
          </xsl:when>
          
          <!-- DE Das selbe wie oben, nur Semikolon separierte Liste -->
          <!-- EN Same as above, only semicolon separated list -->
          <xsl:when test="contains($languages, ';')">
            <xsl:value-of select="substring-before($languages, ';')"/>
          </xsl:when>
          
          <!-- DE Nur ein Eintrag in der Liste -->
          <!-- EN Only one entry in list -->
          <xsl:otherwise>
            <xsl:value-of select="$languages"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:variable>

  <!-- DE Speichere den Result-Tree, damit ich später darauf zugreifen kann. Wird durch den LayoutParser nötig -->
  <!-- EN Saving the result tree for access later on. This is needed because of the layoutParser -->
  <xsl:variable name="resultTree" select="/bebop:page"/>
  
  <!-- DE Speichere den Usernamen des eingelogten User. Wird auch als Test-Variable verwendet -->
  <!-- EN Setting loggedin users name. Also used as test variable -->
  <xsl:variable name="userName">
    <xsl:choose>
      <xsl:when test="/bebop:page/ui:userBanner/@screenName">
        <xsl:value-of select="concat(/bebop:page/ui:userBanner/@givenName, ' ', /bebop:page/ui:userBanner/@familyName)"/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>

  <!-- DE Setze die Versionsnummer -->
  <!-- EN Set version -->
  <xsl:variable name="version" select="'0.9 RC 2'"/>

  <!-- DE Setze den Debug-Modus -->
  <!-- EN Setting debug mode -->
  <xsl:variable name="showDebug">
    <xsl:call-template name="mandalay:getSetting">
      <xsl:with-param name="setting" select="'showDebug'"/>
      <xsl:with-param name="default" select="'false'"/>
    </xsl:call-template>
  </xsl:variable>

  <!-- DE Die interessanten Bereiche des user-agent isolieren -->
  <!-- EN Isolate interesting parts of user-agent-->
  
  <!-- Mozilla -->
  <xsl:variable name="mozilla_version">
    <xsl:value-of select="substring(substring-after($user-agent, 'Mozilla/'), 1, 1)"/>
  </xsl:variable>
  
  <!-- Firefox -->
  <xsl:variable name="firefox_version">
    <xsl:value-of select="substring(substring-after($user-agent, 'Firefox/'), 1, 1)"/>
  </xsl:variable>
  
  <!-- Konqueror -->
  <xsl:variable name="konqueror_version">
    <xsl:value-of select="substring(substring-after($user-agent, 'Konqueror/'), 1, 1)"/>
  </xsl:variable>
  
  <!-- Opera -->
  <xsl:variable name="opera_version1">
    <xsl:value-of select="substring(substring-after($user-agent, 'Opera/'), 1, 1)"/>
  </xsl:variable>
  
  <xsl:variable name="opera_version2">
    <xsl:value-of select="substring(substring-after($user-agent, 'Opera '), 1, 1)"/>
  </xsl:variable>
  
  <!-- MSIE -->
  <xsl:variable name="msie_version">
    <xsl:value-of select="substring(substring-after($user-agent, 'MSIE '), 1, 1)"/>
  </xsl:variable>
  
  <!-- AppleWebKit -->
  <xsl:variable name="webkit_version">
    <xsl:value-of select="substring(substring-after($user-agent, 'AppleWebKit/'), 1, 3)"/>
  </xsl:variable>
  
</xsl:stylesheet>
