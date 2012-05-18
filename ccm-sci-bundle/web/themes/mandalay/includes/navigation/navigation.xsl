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
  Hier wird das Menu von Navigation erzeugt
  Dazu wird eine rekursive Funktion verwendet, so daß die Schachtelungstiefe beliebig ist.
  Erzeugt wird eine <ul><li>-Struktur die mittels CSS formatiert werden kann.
  Zudem wird eine Call-Back-Funktion aufgerufen, mit deren Hilfe zusätzliche Menüpunkte
  dem Menü vorangestellt werden können.
  
  Hinweis:
  Hier kann die Funktion mandalay:getColorset nicht verwendet werden wenn die verschiedenen
  Menüpunkte in ihren jeweiligen Farben eingefärbt werden sollen.
-->

<!-- EN
  Processing navigation-tags to build menu
  Using a recursive function to build an <ul><li>-structur of any levels formated by css.
  Also, a call back function is used to manually prepend some menu entries
-->

<!-- Autor: Sören Bernstein -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop nav mandalay" version="1.0">

  <!-- Menu -->
  <xsl:template name="mandalay:navigation" match="nav:categoryMenu">
    <xsl:param name="layoutTree" select="."/>

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="withColorset">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/withColorset"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/withColorset'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMinLevel">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setMinLevel"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setMinLevel'"/>
        <xsl:with-param name="default" select="'1'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMaxLevel">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setMaxLevel"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setMaxLevel'"/>
        <xsl:with-param name="default" select="'99'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setDescription"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFirstLevelMode">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setFirstLevelMode"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setFirstLevelMode'"/>
        <xsl:with-param name="default" select="'vertical'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setRootLevelStaticText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setRootLevelStaticText"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setRootLevelStaticText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Callback-Funktion -->
    <!-- EN Call back function-->
    <!--      <xsl:call-template name="mandalay:navAddOn"/>-->

    <!-- DE Erzeuge das Menu aus den übergebenen Daten -->
    <!-- EN Building menu -->
    <xsl:choose>
      <xsl:when test="$setFirstLevelMode = 'horizontal'">
        <xsl:apply-templates select="$resultTree/nav:categoryMenu/nav:category" mode="horizontal">
          <xsl:with-param name="level" select="0"/>
          <xsl:with-param name="csNum" select="0"/>
          <xsl:with-param name="minLevel" select="$setMinLevel"/>
          <xsl:with-param name="maxLevel" select="$setMaxLevel"/>
          <xsl:with-param name="withColorset" select="$withColorset"/>
          <xsl:with-param name="setDescription" select="$setDescription"/>
          <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <ul>
          <xsl:apply-templates select="$resultTree/nav:categoryMenu/nav:category" mode="vertical">
            <xsl:with-param name="level" select="0"/>
            <xsl:with-param name="csNum" select="0"/>
            <xsl:with-param name="minLevel" select="$setMinLevel"/>
            <xsl:with-param name="maxLevel" select="$setMaxLevel"/>
            <xsl:with-param name="withColorset" select="$withColorset"/>
            <xsl:with-param name="setDescription" select="$setDescription"/>
            <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
          </xsl:apply-templates>
        </ul>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Sitemap -->
  <xsl:template name="mandalay:sitemap" match="nav:categoryHierarchy">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="withColorset">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/withColorset"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'sitemap/withColorset'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMinLevel">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setMinLevel"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setMinLevel'"/>
        <xsl:with-param name="default" select="'1'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMaxLevel">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setMaxLevel"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setMaxLevel'"/>
        <xsl:with-param name="default" select="'99'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setDescription"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'sitemap/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setRootLevelStaticText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setRootLevelStaticText"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setRootLevelStaticText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Erzeuge das Menu aus den übergebenen Daten -->
    <!-- EN Building menu -->
    <ul>
      <xsl:apply-templates select="$resultTree/nav:categoryHierarchy/nav:category" mode="vertical">
        <xsl:with-param name="level" select="1"/>
        <xsl:with-param name="csNum" select="0"/>
        <xsl:with-param name="minLevel" select="$setMinLevel"/>
        <xsl:with-param name="maxLevel" select="$setMaxLevel"/>
        <xsl:with-param name="withColorset" select="$withColorset"/>
        <xsl:with-param name="setDescription" select="$setDescription"/>
        <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
      </xsl:apply-templates>
    </ul>
  </xsl:template>

  <!-- DE Rekursive Funktion zum erstellen der Menupunkte -->
  <!-- EN recursive function to set menu entries -->
  <xsl:template match="nav:category" mode="vertical">
    <xsl:param name="level"/>
    <xsl:param name="csNum"/>
    <xsl:param name="minLevel"/>
    <xsl:param name="maxLevel"/>
    <xsl:param name="withColorset"/>
    <xsl:param name="setDescription"/>
    <xsl:param name="setRootLevelStaticText"/>

    <xsl:choose>
      <xsl:when test="$level = 1 and $level >= $minLevel">
        <li>
          <xsl:apply-templates select="." mode="menu">
            <xsl:with-param name="level" select="$level"/>
            <xsl:with-param name="csNum" select="position()"/>
            <xsl:with-param name="minLevel" select="$minLevel"/>
            <xsl:with-param name="maxLevel" select="$maxLevel"/>
            <xsl:with-param name="withColorset" select="$withColorset"/>
            <xsl:with-param name="setDescription" select="$setDescription"/>
            <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
          </xsl:apply-templates>
        </li>
      </xsl:when>
      
      <xsl:when test="$level >= $minLevel">
        <li>
          <xsl:apply-templates select="." mode="menu">
            <xsl:with-param name="level" select="$level"/>
            <xsl:with-param name="csNum" select="$csNum"/>
            <xsl:with-param name="minLevel" select="$minLevel"/>
            <xsl:with-param name="maxLevel" select="$maxLevel"/>
            <xsl:with-param name="withColorset" select="$withColorset"/>
            <xsl:with-param name="setDescription" select="$setDescription"/>
            <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
          </xsl:apply-templates>
        </li>
      </xsl:when>
      
      <!-- DE Überspringe diese Ebene bis minLevel erreicht ist -->
      <!-- EN Skip level until minLevel is reached -->
      <xsl:otherwise>
        <xsl:apply-templates mode="vertical">
          <xsl:with-param name="level" select="$level + 1"/>
          <xsl:with-param name="csNum" select="$csNum"/>
          <xsl:with-param name="minLevel" select="$minLevel"/>
          <xsl:with-param name="maxLevel" select="$maxLevel"/>
          <xsl:with-param name="withColorset" select="$withColorset"/>
          <xsl:with-param name="setDescription" select="$setDescription"/>
          <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- DE Rekursive Funktion zum erstellen der Menupunkte -->
  <!-- EN recursive function to set menu entries -->
  <xsl:template match="nav:category" mode="horizontal">
    <xsl:param name="level"/>
    <xsl:param name="csNum"/>
    <xsl:param name="minLevel"/>
    <xsl:param name="maxLevel"/>
    <xsl:param name="withColorset"/>
    <xsl:param name="setDescription"/>
    <xsl:param name="setRootLevelStaticText"/>

    <xsl:choose>
      <xsl:when test="$level >= $minLevel">
        <span>
          <xsl:apply-templates select="." mode="menu">
            <xsl:with-param name="level" select="$level"/>
            <xsl:with-param name="csNum" select="$csNum"/>
            <xsl:with-param name="minLevel" select="$minLevel"/>
            <xsl:with-param name="maxLevel" select="$maxLevel"/>
            <xsl:with-param name="withColorset" select="$withColorset"/>
            <xsl:with-param name="setDescription" select="$setDescription"/>
            <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
          </xsl:apply-templates>
        </span>
        <xsl:if test="last() > position()"> </xsl:if>
      </xsl:when>

      <!-- DE Überspringe diese Ebene bis minLevel erreicht ist -->
      <!-- EN Skip level until minLevel is reached -->
      <xsl:otherwise>
        <xsl:apply-templates mode="horizontal">
          <xsl:with-param name="level" select="$level + 1"/>
          <xsl:with-param name="csNum" select="$csNum"/>
          <xsl:with-param name="minLevel" select="$minLevel"/>
          <xsl:with-param name="maxLevel" select="$maxLevel"/>
          <xsl:with-param name="withColorset" select="$withColorset"/>
          <xsl:with-param name="setDescription" select="$setDescription"/>
          <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="nav:category" mode="menu">
    <xsl:param name="level"/>
    <xsl:param name="csNum"/>
    <xsl:param name="minLevel"/>
    <xsl:param name="maxLevel"/>
    <xsl:param name="withColorset"/>
    <xsl:param name="setDescription"/>
    <xsl:param name="setRootLevelStaticText"/>

    <!-- DE Titel ohne &shy; -->
    <xsl:variable name="cleanTitle">
      <xsl:call-template name="mandalay:shying">
        <xsl:with-param name="title">
          <xsl:choose>
            <xsl:when test="$level = 0 and $setRootLevelStaticText = 'true'">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'navigation'"/>
                <xsl:with-param name="id" select="'homeButton/title'"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:choose>
                <xsl:when test="$setDescription = 'true' and @description != ''">
                  <xsl:value-of select="@description"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="@title"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
        <xsl:with-param name="mode">dynamic</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Titel unverändert oder mit manuell ersetztem &shy; -->
    <xsl:variable name="title">
      <xsl:call-template name="mandalay:shying">
        <xsl:with-param name="title">
          <xsl:choose>
            <xsl:when test="$level = 0 and $setRootLevelStaticText = 'true'">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'navigation'"/>
                <xsl:with-param name="id" select="'homeButton/link'"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@title"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
        <xsl:with-param name="mode">force</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Unterscheide zwischen aktivierten und nicht aktivierten Links -->
    <!-- EN Decide between active and non-active link -->
    <xsl:choose>
      <xsl:when test="@isSelected='true'">
        <xsl:attribute name="class">
          <xsl:choose>
            <xsl:when test="$withColorset='true'">
              <xsl:value-of select="concat('menulevel_', $level, ' colorset_', $csNum, ' selected')"
              />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="concat('menulevel_', $level, ' selected')"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>

        <!-- DE Setze die CSS-Klassen für aktiver Menüpunkt und das Farbset -->
        <!-- EN Set css-classes for activated menu and colorset -->
        <xsl:choose>

          <!-- DE  
              Wenn dieser Menüpunkt der aktuelle ist, dann keinen Link erzeugen
              Das ist der Fall, wenn wir eine Naviagtionsseite bearbeiten und es keine Nav-Unterpunkte gibt,
              die als Selected markiert sind. Um die Formtierung einfacher zu machen, wird der Text in einen
              span Element gesetzt.
            -->
          <!-- EN
              If this entry is the current one, don't make a link
              this is the case, if we are processing a index page without selected Nav-Subentries.
              To simplify to formating, the text is put into a span element.
            -->
          <xsl:when
            test="(/bebop:page/bebop:title = 'Navigation') and (not(./nav:category[@isSelected='true']))">
            <span>
              <span>
                <xsl:value-of select="$title"/>
              </span>
            </span>
          </xsl:when>

          <!-- DE In allen anderen Fällen, erzeuge einen Link -->
          <!-- EN making a link in any other case -->
          <xsl:otherwise>
            <a href="{@url}" title="{$cleanTitle}">
              <span>
                <xsl:value-of select="$title"/>
              </span>
            </a>
          </xsl:otherwise>

        </xsl:choose>
      </xsl:when>

      <xsl:otherwise>
        <xsl:attribute name="class">
          <xsl:choose>
            <xsl:when test="$withColorset='true'">
              <xsl:value-of select="concat('menulevel_', $level, ' colorset_', $csNum)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="concat('menulevel_', $level)"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <a href="{@url}" title="{$cleanTitle}">
          <span>
            <xsl:value-of select="$title"/>
          </span>
        </a>
      </xsl:otherwise>
    </xsl:choose>

    <!-- DE Rekursion: Unterpunkte verarbeiten -->
    <!-- EN Recursion: processing subs -->
    <xsl:if test="$maxLevel > $level and ./nav:category">
      <ul>
        <xsl:apply-templates mode="vertical">
          <xsl:with-param name="level" select="$level + 1"/>
          <xsl:with-param name="csNum" select="$csNum"/>
          <xsl:with-param name="minLevel" select="$minLevel"/>
          <xsl:with-param name="maxLevel" select="$maxLevel"/>
          <xsl:with-param name="withColorset" select="$withColorset"/>
          <xsl:with-param name="setDescription" select="$setDescription"/>
          <xsl:with-param name="setRootLevelStaticText" select="$setRootLevelStaticText"/>
        </xsl:apply-templates>

        <!-- DE Erzeuge bei Bedarf den mehr-Button-->
        <!-- EN Create the more-Button, if wanted-->
        <xsl:if test="./@showMore">
          <li class="menulevel_{$level+1}">
            <xsl:attribute name="class">
              <xsl:choose>
                <xsl:when test="$withColorset='true'">
                  <xsl:value-of
                    select="concat('menulevel_', $level+1, ' colorset_', $csNum, ' more')"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="concat('menulevel_', $level+1, ' more')"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>

            <a href="{@url}" title="{$cleanTitle}">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'navigation'"/>
                <xsl:with-param name="id" select="'moreButton'"/>
              </xsl:call-template>
            </a>
          </li>
        </xsl:if>
      </ul>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
