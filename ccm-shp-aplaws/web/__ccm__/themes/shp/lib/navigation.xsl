<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/london/navigation"
  xmlns:shp="http://www.shp.de"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav"
  version="1.0">

  <xsl:template name="shp:breadcrumb">
    <span class="hide">|</span>
    <div class="breadcrumbs">
      Sie sind hier:
      <xsl:choose>
        <xsl:when test="count(nav:categoryPath/nav:category) > 1 or not(/bebop:page/bebop:title = 'Navigation')">
          <a href="{$dispatcher-prefix}/navigation/" title="Start">Start</a><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt;
        </xsl:when>
        <xsl:otherwise>
          <span class="breadHi">Start</span>
        </xsl:otherwise>
      </xsl:choose>

      <!-- Behandle Sonderfälle -->
      <xsl:choose>
        <xsl:when test="count(nav:categoryPath/nav:category) &lt; 2 and not(/bebop:page/bebop:title = 'Navigation')">

          <!-- Unterscheide die Sonderfälle -->
          <xsl:choose>

            <!-- Wenn Application = AtoZ, dann zeige das an-->
            <xsl:when test="/bebop:page/bebop:title = 'AtoZ'">
              <span class="breadHi">A-Z Liste</span>
            </xsl:when>

            <!-- Wenn Application = Suche, dann zeige das an-->
            <xsl:when test="/bebop:page/bebop:title = 'Search'">
              <span class="breadHi">
                <xsl:choose>
                  <xsl:when test="@id='search'">Suche</xsl:when>
                  <xsl:when test="@id='advanced'">Erweiterte Suche</xsl:when>
                  <xsl:otherwise />
                </xsl:choose>
              </span>
            </xsl:when>

            <!-- Wenn Application = Sitemap, dann zeige das an-->
            <xsl:when test="/bebop:page/bebop:title = 'Sitemap'">
              <span class="breadHi">Sitemap</span>
            </xsl:when>

          </xsl:choose>
        </xsl:when>

        <!-- Standardbehandlung-->
        <xsl:otherwise>

          <xsl:for-each select="nav:categoryPath/nav:category[not(position()=1)]">
            <xsl:choose>
              <xsl:when test="not(position()=last())">
                <a>
                  <xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute>
                  <xsl:attribute name="title"><xsl:value-of select="@description" /></xsl:attribute>
                  <xsl:value-of select="@title" />
                </a>
                <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<span class="breadArrow"><xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt; </span>
              </xsl:when>
              <xsl:otherwise>
                <!-- Zeige auch die ContentItems in den Breadcrumbs an -->
                <xsl:choose>
                  <!-- Wenn es eine Navigationsseite ist, dann ist die Liste damit zu Ende -->
                  <xsl:when test="/bebop:page/bebop:title = 'Navigation'">
                    <span class="breadHi"><xsl:value-of select="@title" /></span>
                  </xsl:when>

                  <!-- Sonst wird der letzte Navigationspunkt auch ein Link und der Content wird angehÃ€ngt -->
                  <xsl:otherwise>
                    <a>
                      <xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute>
                      <xsl:attribute name="title"><xsl:value-of select="@description" /></xsl:attribute>
                      <xsl:value-of select="@title" />
                    </a>
                    <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<span class="breadArrow"><xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt; </span>
                    <span class="breadHi"><xsl:value-of select="/bebop:page/cms:contentPanel/cms:item/title" /></span>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>

    </div>
  </xsl:template>

  <xsl:template name="shp:navigation">
    <div id="menu">
      <ul>

        <!-- Callback-Funktion -->
        <xsl:call-template name="shp:navAddOn"/>

        <!-- Erzeuge das Menu aus den übergebenen Daten -->
        <xsl:for-each select="/bebop:page/nav:categoryMenu/nav:category/nav:category">

          <xsl:call-template name="shp:nav">
            <xsl:with-param name="level" select="1" />
          </xsl:call-template>

        </xsl:for-each>

      </ul>
      <br />
      <div id="formalia">
        <img class="top" src="{$theme-prefix}/images/helpbox_top.gif" name="Helpbox_Top" alt="[Helpbox Beginn]" /><br />
        <img class="logo" border="0" src="{$theme-prefix}/images/helpbox_logo.gif" name="Helpbox" alt="[Helpbox]" />
        <a href="/help" title="Hilfe">Hilfe</a><br />
        <a href="/spenden" title="Spenden">Spenden</a><br />
        <a href="/beitreten" title="Mitglied werden">Beitreten</a><br />
        <a href="/impressum" title="Impressum">Impressum</a><br />
        <a href="/haftungsausschluss" title="Haftungsausschluss">
           Haftungs-<br />
           <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;
           <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;
           <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;
           ausschluss
        </a>
        <img class="bottom" src="{$theme-prefix}/images/helpbox_bottom.gif" name="Helpbox_Bottom" alt="[Helpbox Ende]" />
      </div>
      <img id="menuEnd" src="{$theme-prefix}/images/menu_bottom.jpg" name="Menu_Bottom" alt="[Menu Ende]" />
        <a href="bla.html">
        </a>
    </div>
  </xsl:template>

  <xsl:template name="shp:nav">
    <xsl:param name="level"/>

    <!-- Unterscheide zwischen aktivierten und nicht aktivierten Links -->
    <xsl:if test="@isSelected">
      <li id="menulevel_{$level}">
        <xsl:choose>

          <!-- Wenn dieser Menüpunkt der aktuelle ist, dann keinen Link erzeugen -->
          <!-- Das ist der Fall, wenn wir eine Naviagtionsseite bearbeiten und es keine Nav-Unterpunkte unterhalb -->
          <!-- des aktuellen gibt, die als Selected markiert sind. -->
          <xsl:when test="(/bebop:page/bebop:title = 'Navigation') and (not(./nav:category[@isSelected]))">
            <xsl:attribute name="class">selected</xsl:attribute>
            <div>
              <xsl:value-of select="@title" />
            </div>
          </xsl:when>

          <!-- In allen anderen Fällen, erzeuge einen Link  -->
          <xsl:otherwise>

            <!-- Wenn ein ContentLink active ist, dann setze die zusätzliche Klasse -->
            <xsl:choose>
              <xsl:when test="not(/bebop:page/bebop:title = 'Navigation') and (not(./nav:category[@isSelected]))">
                <xsl:attribute name="class">contentLink</xsl:attribute>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="class">selected</xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
            <div>
              <a href="{@url}" title="{@title}">
                <xsl:value-of select="@title" />
              </a>
            </div>
          </xsl:otherwise>

        </xsl:choose>
      </li>
    </xsl:if>
    <xsl:if test="not(@isSelected)">
      <li id="menulevel_{$level}" >
        <a href="{@url}" title="{@title}">
          <xsl:value-of select="@title" />
        </a>
      </li>
    </xsl:if>

    <!-- Rekursion: Unterpunkte verarbeiten -->
    <xsl:if test="./nav:category">
      <ul>
        <xsl:for-each select="./nav:category">
          <xsl:call-template name="shp:nav">
            <xsl:with-param name="level" select="$level + 1" />
          </xsl:call-template>
        </xsl:for-each>
      </ul>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
