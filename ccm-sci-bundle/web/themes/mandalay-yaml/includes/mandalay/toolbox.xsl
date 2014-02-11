<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>]>

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
Hier werden globale Funktionen definiert z.B. getStaticText und getSetting
-->

<!-- EN
Setting up global templates like getStaticText and getSetting
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns:mandalay="http://mandalay.quasiweb.de"
                exclude-result-prefixes="xsl bebop cms ui nav mandalay"
                version="1.0">

  <!-- DE Lade die Settings -->
  <!-- DE Parameter:
        node: Konfigurationsnode aus der Layoutdatei
        modul: Name des Moduls aka des Files (z.B. breadcrumbs)
        setting: Name der gesuchten Einstellung
        default: Standardwert, wenn mit den o. g. Angaben keine Einstellung gefunden werden kann
  -->
  <!-- EN Loading settings -->
  <!-- EN Parameter:
        node: Configuration node from layout file
        modul: Name of module aka filename (e.g. breadcrumbs)
        setting: Name of needed setting
        default: Default value, if there is no setting matching the parameters above
  -->
  <xsl:template name="mandalay:getSetting">
    <xsl:param name="node"/>
    <xsl:param name="module"/>
    <xsl:param name="setting" select="name($node)"/>
    <xsl:param name="default"/>

    <xsl:variable name="set">
      <xsl:choose>

        <!-- 1. -->
        <xsl:when test="$node and $node != ''">
          <xsl:value-of select="$node"/>
        </xsl:when>

        <xsl:when test="$module != ''">
          <xsl:choose>

            <!-- 2.1.a -->
            <xsl:when test="document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=$setting]">
              <xsl:value-of select="document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=$setting]"/>
            </xsl:when>

            <!-- 2.1.b -->
            <xsl:when test="not(document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=$setting]) and
                            ( substring-after($setting, '/') and 
                              document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=substring-after($setting, '/')]
                            )">
              <xsl:value-of select="document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=substring-after($setting, '/')]"/>
            </xsl:when>

            <!-- 2.2.a -->
            <xsl:when test="not(document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=$setting]) and 
                            ( substring-after($setting, '/') and 
                              not(document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=substring-after($setting, '/')]) 
                            ) and
                            document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=concat($module, '/', $setting)]">
              <xsl:value-of select="document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=concat($module, '/', $setting)]"/>
            </xsl:when>

            <!-- 2.2.b -->
            <xsl:when test="not(document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=$setting]) and 
                            not(document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=concat($module, '/', $setting)]) and
                            ( substring-after($setting, '/') and 
                              not(document(concat($theme-prefix, '/settings/', $module, '.xml'))/settings/setting[@id=substring-after($setting, '/')]) and
                              document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=concat($module, '/', substring-after($setting, '/'))]
                            )">
              <xsl:value-of select="document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=concat($module, '/', substring-after($setting, '/'))]"/>
            </xsl:when>

            <xsl:otherwise/>
          </xsl:choose>
        </xsl:when>

        <xsl:when test="$module = ''">
          <xsl:choose>

            <!-- 3.a -->
            <xsl:when test="document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=$setting]">
              <xsl:value-of select="document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=$setting]"/>
            </xsl:when>

            <!-- 3.b -->
            <xsl:when test="not(document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=$setting]) and
                            ( substring-after($setting, '/') and 
                              document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=substring-after($setting, '/')]
                            )">
              <xsl:value-of select="document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id=substring-after($setting, '/')]"/>
            </xsl:when>
            <xsl:otherwise/>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$set != ''">
        <xsl:value-of select="$set"/>
      </xsl:when>
      <xsl:otherwise>

        <!-- 4. -->
        <xsl:value-of select="$default"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>


  <!-- DE Template zum übersetzen der statischen Texte -->
  <!-- DE Parameter:
        modul: Modulname aka Filename (Default: global)
        id: ID des Strings
  -->
  <!-- EN Template to load localized statix text -->
  <!-- EN Parameter:
        modul: Name of module aka filename (default: global)
        id: ID of localized string
  -->
  <xsl:template name="mandalay:getStaticText">
    <xsl:param name="module"/>
    <xsl:param name="id"/>
    <xsl:param name="lang" select="$lang"/>

    <!-- DE Generiere vollständigen Pfad zu dem gewünschten String (inkl. Dateiname) und gebe den Inhalt zurück -->
    <xsl:variable name="line">
      <xsl:choose>

        <xsl:when test="$module!=''">
          <xsl:choose>

            <!-- 1.1 -->
            <xsl:when test="document(concat($theme-prefix, '/lang/', $lang, '/global.xml'))/translations/line[@id=concat($module, '/', $id)]">
              <xsl:value-of select="document(concat($theme-prefix, '/lang/', $lang, '/global.xml'))/translations/line[@id=concat($module, '/', $id)]"/>
            </xsl:when>

            <!-- 1.2 -->
            <xsl:when test="not(document(concat($theme-prefix, '/lang/', $lang, '/global.xml'))/translations/line[@id=concat($module, '/', $id)]) and 
                            document(concat($theme-prefix, '/lang/', $lang, '/', $module, '.xml'))/translations/line[@id=$id]">
              <xsl:value-of select="document(concat($theme-prefix, '/lang/', $lang, '/', $module, '.xml'))/translations/line[@id=$id]"/>
            </xsl:when>

            <xsl:otherwise/>
          </xsl:choose>
        </xsl:when>

        <xsl:when test="$module=''">
          <xsl:choose>

            <!-- 2 -->
            <xsl:when test="document(concat($theme-prefix, '/lang/', $lang, '/global.xml'))/translations/line[@id=$id]">
              <xsl:value-of select="document(concat($theme-prefix, '/lang/', $lang, '/global.xml'))/translations/line[@id=$id]"/>
            </xsl:when>

            <xsl:otherwise/>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$line != ''">
        <xsl:value-of select="$line"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$showDebug = 'true'">
          <span id="#debug">
            <b>
              Missing Translation:
            </b>
            <xsl:if test="$module!=''">
              <xsl:value-of select="concat($theme-prefix, '/lang/', $lang, '/', $module, '.xml/translations/line[@id=' , $id , ']')"/>
            </xsl:if>
            <xsl:if test="$module=''">
              <xsl:value-of select="concat($theme-prefix, '/lang/', $lang, '/global.xml/translations/line[@id=' , $id , ']')"/>
            </xsl:if>
          </span>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>


  <!-- DE Dieses Template weißt die CSS-Klasse für das colorset zu in Abhängigket des aktiven Menüpunktes -->
  <!-- Parameter:
          addClass: Klassennamen, die zusätzlich gesetzt werden sollen, da man xsl:attribute nicht mehrfach
                    mit dem gleichen Name-Parameter verwenden kann, space-separierte Liste
  -->
  <!-- EN This template sets the colorset css-class depended on active navigation menu-->
  <!-- Parameter:
          addClass: classnames to add, space separated list
  -->
  <xsl:template name="mandalay:getColorset">
    <xsl:for-each select="$resultTree/nav:categoryMenu/nav:category/nav:category">
      <xsl:if test="@isSelected = 'true'">
        <xsl:text>colorset_</xsl:text><xsl:value-of select="position()"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  <!-- DE Hier werden Links angepasst, so daß absolute und relative Links sowie Anchors verwendet werden können -->
  <!-- EN Processing links to ensure the correct coding of absolute and relative links, as well as anchors -->
  <xsl:template name="mandalay:linkParser">
    <xsl:param name="link"/>
    <xsl:param name="prefix" select="$dispatcher-prefix"/>
    <xsl:choose>

      <xsl:when test="starts-with($link, 'http://')">
        <xsl:value-of select="$link"/>
      </xsl:when>

      <xsl:when test="starts-with($link, '#')">
        <xsl:value-of select="$link"/>
      </xsl:when>

      <xsl:when test="starts-with($link, '?')">
        <xsl:value-of select="$link"/>
      </xsl:when>

      <xsl:when test="starts-with($link, '*/')">
        <xsl:value-of select="substring($link, 2)"/>
      </xsl:when>

      <xsl:when test="starts-with($link, '/')">
        <xsl:choose>
          <!-- DE Workaround für die unterschiedliche Angabe der Links (einige beinhalten das Prefix, andere nicht) -->
          <!-- EN Workaround for different kind of link generation (some include the prefix, some don't) -->
          <xsl:when test="starts-with($link, $prefix)">
            <xsl:value-of select="$link"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="concat($prefix, $link)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select="concat($prefix, '/', $link)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- DE Setze den Titel. Beachte dabei einige Sonderfälle -->
  <!-- EN Set page titel. Respect some special cases -->
  <xsl:template name="mandalay:title">
    <xsl:choose>

      <!-- DE Lokalisierte feste Titel für bestimmte ContentItems -->
      <!-- EN localized fixed title for some contentItems -->
      <xsl:when test="$resultTree//cms:contentPanel">
        <xsl:choose>

          <!-- DE Glossar -->
          <!-- EN Glossary -->
          <xsl:when test="$resultTree/cms:contentPanel/cms:item/type/label = 'Glossary Item'">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="id" select="'layout/page/title/glossary'"/>
            </xsl:call-template>
          </xsl:when>

          <!-- DE FAQ -->
          <!-- EN FAQ -->
          <xsl:when test="$resultTree/cms:contentPanel/cms:item/type/label = 'FAQ Item'">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="id" select="'layout/page/title/faq'"/>
            </xsl:call-template>
          </xsl:when>

          <!-- DE Sonst benutze den Titel des CI-->
          <!-- EN Else use title of CI -->
          <xsl:otherwise>
            <xsl:call-template name="mandalay:shying">
              <xsl:with-param name="title">
                <xsl:value-of select="$resultTree//cms:contentPanel/cms:item/title"/>
              </xsl:with-param>
              <xsl:with-param name="mode">dynamic</xsl:with-param>
            </xsl:call-template>
          </xsl:otherwise>

        </xsl:choose>
      </xsl:when>

      <!-- DE Lokalisierter Titel für A-Z Liste -->
      <!-- EN localized title for A-Z list -->
      <xsl:when test="$resultTree/bebop:title = 'AtoZ'">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="id" select="'layout/page/title/atoz'"/>
        </xsl:call-template>
      </xsl:when>

      <!-- DE Lokalisierte Titel für Suche -->
      <!-- EN Localized title for search -->
      <xsl:when test="$resultTree/bebop:title = 'Search'">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="id" select="'layout/page/title/search'"/>
        </xsl:call-template>
      </xsl:when>

      <!-- DE Lokalisierte Titel für Login -->
      <!-- EN Localized title for log in -->
      <xsl:when test="$resultTree/@application = 'login'">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="id" select="'layout/page/title/login'"/>
        </xsl:call-template>
      </xsl:when>

      <!-- DE Lokalisierter Titel für Sitemap -->
      <!-- EN Localited title for sitemap -->
      <xsl:when test="$resultTree/@id = 'sitemapPage'">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="id" select="'layout/page/title/sitemap'"/>
        </xsl:call-template>
      </xsl:when>

      <!-- DE Titel für die ContentSection-->
      <!-- EN title for content section-->
      <xsl:otherwise>
        <xsl:for-each select="$resultTree/nav:categoryMenu//nav:category[@isSelected='true']">
          <xsl:choose>

            <!-- DE Spezielle Regel: Für die Willkommen-Seite die Überschrift des Artikels anzeigen -->
            <!-- EN Special rule: Use content item title für root-page in navigation -->
            <xsl:when test="position() = last() and position() = 1">
              <xsl:call-template name="mandalay:shying">
                <xsl:with-param name="title">
                  <xsl:value-of select="/bebop:page//title"/>
                </xsl:with-param>
                <xsl:with-param name="mode">dynamic</xsl:with-param>
              </xsl:call-template>
            </xsl:when>

            <!-- DE Sonst zeige als Überschrift den Namen der Kategorie an -->
            <!-- EN Else use the name of the category -->
            <xsl:when test="position() = last()">
              <xsl:call-template name="mandalay:shying">
                <xsl:with-param name="title">
                  <xsl:value-of select="@title"/>
                </xsl:with-param>
                <xsl:with-param name="mode">dynamic</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
          </xsl:choose>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- DE Hole den Contenttyp als String -->
  <!-- EN Get string representation of content type-->
  <xsl:template name="mandalay:getContentTypeName">
    <xsl:value-of select="$resultTree//cms:item/type/label"/>
  </xsl:template>

  <!-- DE Verarbeite die Attribute -->
  <!-- EN Process all attributes -->
  <xsl:template name="mandalay:processAttributes">
    <xsl:for-each select="@*">
      <xsl:if test="name()!='href_no_javascript'">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="name() = 'bebop:formWidget' and (not(@id) and @name)">
      <xsl:attribute name="id">
        <xsl:value-of select="@name"/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  <!-- DE Behandle die Sortierpfeile -->
  <!-- EN Process sorting arrows -->
  <xsl:template name="mandalay:moveButtons">
    <span class="moveButton">
      <xsl:if test="@prevURL">
        <a>
          <xsl:attribute name="href"><xsl:value-of select="./@prevURL"/></xsl:attribute>
          <xsl:attribute name="title"><xsl:value-of select="'moveUp'"/></xsl:attribute>
          <img>
            <xsl:attribute name="src"><xsl:value-of select="'/assets/gray-triangle-up.gif'"/></xsl:attribute>
            <xsl:attribute name="title"><xsl:value-of select="'moveUp'"/></xsl:attribute>
          </img>
        </a>
      </xsl:if>
    </span>
    <span class="moveButton">
      <xsl:if test="@nextURL">
        <a>
          <xsl:attribute name="href"><xsl:value-of select="./@nextURL"/></xsl:attribute>
          <xsl:attribute name="title"><xsl:value-of select="'moveDown'"/></xsl:attribute>
          <img>
            <xsl:attribute name="src"><xsl:value-of select="'/assets/gray-triangle-down.gif'"/></xsl:attribute>
            <xsl:attribute name="title"><xsl:value-of select="'moveDown'"/></xsl:attribute>
          </img>
        </a>
      </xsl:if>
    </span>
  </xsl:template>
  
  <!-- DE String-Replace Template, da es diese nicht als XSL-Funktion gibt -->
  <!-- EN String replace template because there isn't a xsl function for that -->
  <xsl:template name="mandalay:string-replace">
    <xsl:param name="string" select="''"/>
    <xsl:param name="from" select="''"/>
    <xsl:param name="to" select="''"/>
    <xsl:choose>
      <xsl:when test="contains($string,$from)">
        <xsl:value-of select="substring-before($string,$from)"/>
        <xsl:value-of select="$to"/>
        <xsl:call-template name="mandalay:string-replace">
          <xsl:with-param name="string" select="substring-after($string,$from)"/>
          <xsl:with-param name="from" select="$from"/>
          <xsl:with-param name="to" select="$to"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$string"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- DE -->
  <!-- EN -->
  <xsl:template name="mandalay:shying">
    <xsl:param name="title"/>
    <xsl:param name="mode" select="'dynamic'"/>
    
    <xsl:choose>
      <xsl:when test="$firefox_version >= 3 or 
        $opera_version1 > 7 or 
        $opera_version2 > 7 or
        $webkit_version >= 417 or 
        $msie_version >= 5">
        <!-- DE Dies ist der einfache Fall. Alle diese Browser unterstützen &shy;,
                daher wird unabhängig von mode zu &shy; ersetzt -->
        <!-- EN This is the easy case. All these browsers support &shy;, so we
                will replace to &shy; regardless of mode -->
        <xsl:call-template name="mandalay:string-replace">
          <xsl:with-param name="string" select="$title"/>
          <xsl:with-param name="from" select="'\-'"/>
          <xsl:with-param name="to" select="'&shy;'"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="mode='force'">
            <xsl:call-template name="mandalay:string-replace">
              <xsl:with-param name="string" select="$title"/>
              <xsl:with-param name="from" select="'\-'"/>
              <xsl:with-param name="to" select="'- '"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="mandalay:string-replace">
              <xsl:with-param name="string" select="$title"/>
              <xsl:with-param name="from" select="'\-'"/>
              <xsl:with-param name="to" select="''"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
        
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- DE Hier muß noch was gemacht werden -->
  <!-- EN Some work to do here -->

  <!-- DE Timestamp to Date -->
  <!-- DE Format:
        s - dd.mm.yyyy
        S - WW, dd.mm.yyyy
        l - dd. Monat yyyy
        L - Wochentag, dd. Monat yyyy
  -->
  <!-- EN Timestamp to Date -->
  <!-- EN Format:
        s - dd.mm.yyyy
        S - WW, dd.mm.yyyy
        l - dd. Month yyyy
        L - DoW, dd. Month yyyy
  -->
  <xsl:template name="timestamp2date">
    <xsl:param name="timestamp"/>
    <xsl:param name="format"/>

    <!-- DE Wochentag -->
    <!-- EN Day of Week -->
    <xsl:variable name="weekday" select="substring($timestamp, 1, 3)"/>
    <xsl:choose>
      <xsl:when test="$format='S'">
        <xsl:choose>
          <xsl:when test="$weekday='Mon'">Mo</xsl:when>
          <xsl:when test="$weekday='Tue'">Di</xsl:when>
          <xsl:when test="$weekday='Wed'">Mi</xsl:when>
          <xsl:when test="$weekday='Thu'">Do</xsl:when>
          <xsl:when test="$weekday='Fri'">Fr</xsl:when>
          <xsl:when test="$weekday='Sat'">Sa</xsl:when>
          <xsl:when test="$weekday='Sun'">So</xsl:when>
        </xsl:choose>
        <xsl:text>., </xsl:text>
      </xsl:when>
      <xsl:when test="$format='L'">
        <xsl:choose>
          <xsl:when test="$weekday='Mon'">Montag</xsl:when>
          <xsl:when test="$weekday='Tue'">Dienstag</xsl:when>
          <xsl:when test="$weekday='Wed'">Mittwoch</xsl:when>
          <xsl:when test="$weekday='Thu'">Donnerstag</xsl:when>
          <xsl:when test="$weekday='Fri'">Freitag</xsl:when>
          <xsl:when test="$weekday='Sat'">Samstag</xsl:when>
          <xsl:when test="$weekday='Sun'">Sonntag</xsl:when>
        </xsl:choose>
        <xsl:text>, </xsl:text>
      </xsl:when>
    </xsl:choose>
    <!-- DE Tag -->
    <!-- EN Day -->
    <xsl:value-of select="substring($timestamp, 9, 2)"/>
    <xsl:text>.</xsl:text>
    <!-- DE Monat -->
    <!-- EN Month -->
    <xsl:variable name="month" select="substring($timestamp, 5, 3)"/>
    <xsl:choose>
      <xsl:when test="$format='S' or $format='s'">
        <xsl:choose>
          <xsl:when test="$month='Jan'">01</xsl:when>
          <xsl:when test="$month='Feb'">02</xsl:when>
          <xsl:when test="$month='Mar'">03</xsl:when>
          <xsl:when test="$month='Apr'">04</xsl:when>
          <xsl:when test="$month='May'">05</xsl:when>
          <xsl:when test="$month='Jun'">06</xsl:when>
          <xsl:when test="$month='Jul'">07</xsl:when>
          <xsl:when test="$month='Aug'">08</xsl:when>
          <xsl:when test="$month='Sep'">09</xsl:when>
          <xsl:when test="$month='Oct'">10</xsl:when>
          <xsl:when test="$month='Nov'">11</xsl:when>
          <xsl:when test="$month='Dec'">12</xsl:when>
        </xsl:choose>
        <xsl:text>.</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> </xsl:text>
        <xsl:choose>
          <xsl:when test="$month='Jan'">Januar</xsl:when>
          <xsl:when test="$month='Feb'">Februar</xsl:when>
          <xsl:when test="$month='Mar'">März</xsl:when>
          <xsl:when test="$month='Apr'">April</xsl:when>
          <xsl:when test="$month='May'">Mai</xsl:when>
          <xsl:when test="$month='Jun'">Juni</xsl:when>
          <xsl:when test="$month='Jul'">Juli</xsl:when>
          <xsl:when test="$month='Aug'">August</xsl:when>
          <xsl:when test="$month='Sep'">September</xsl:when>
          <xsl:when test="$month='Oct'">Oktober</xsl:when>
          <xsl:when test="$month='Nov'">November</xsl:when>
          <xsl:when test="$month='Dec'">Dezember</xsl:when>
        </xsl:choose>
        <xsl:text> </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <!-- DE Jahr -->
    <!-- EN Year -->
    <xsl:value-of select="substring($timestamp, string-length($timestamp) - 3)"/>
  </xsl:template>

</xsl:stylesheet>
