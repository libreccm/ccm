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
      <p>
        Sie sind hier:
        <xsl:choose>
          <xsl:when test="count(nav:categoryPath/nav:category) > 1">
            <a href="{$dispatcher-prefix}/navigation/" title="Start">Start</a><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt;
          </xsl:when>
          <xsl:otherwise>
            <span class="breadHi">Start</span>
          </xsl:otherwise>
        </xsl:choose>
        <span class="hide">|</span>
        <xsl:for-each select="nav:categoryPath/nav:category[not(position()=1)]">
          <xsl:choose>
            <xsl:when test="not(position()=last())">
              <a>
                <xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute>
                <xsl:attribute name="title"><xsl:value-of select="@description" /></xsl:attribute>
                <xsl:value-of select="@title" />
              </a>
              <span class="hide">|</span>
              <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<span class="breadArrow"><xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt; </span>
            </xsl:when>
            <xsl:otherwise>
              <!-- Zeige auch die ContentItems in den Breadcrumbs an -->
              <xsl:choose>
                <!-- Wenn es eine Navigationsseite ist, dann ist die Liste damit zu Ende -->
                <xsl:when test="/bebop:page/bebop:title = 'Navigation'">
                  <span class="breadHi"><xsl:value-of select="@title" /></span>
                </xsl:when>
                <!-- Wenn Application = AtoZ, dann zeige das an-->
                <!-- Wenn Application = Suche, dann zeige das an-->
                <!-- Wenn Application = Sitemap, dann zeige das an-->
                <!-- Sonst wird der letzte Navigationspunkt auch ein Link und der Content wird angehängt -->
                <xsl:otherwise>
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute>
                    <xsl:attribute name="title"><xsl:value-of select="@description" /></xsl:attribute>
                    <xsl:value-of select="@title" />
                  </a>
                  <span class="hide">|</span>
                  <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<span class="breadArrow"><xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt; </span>
                  <span class="breadHi"><xsl:value-of select="/bebop:page/cms:contentPanel/cms:item/title" /></span>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </p>
      <span class="hide">|</span>
    </div>
  </xsl:template>

  <xsl:template name="shp:navigation">
    <div id="menu">
      <ul>
        <xsl:for-each select="/bebop:page/nav:categoryMenu/nav:category/nav:category">
          <!-- Alle Root Kategorien -->
          <xsl:if test="not(@isSelected)">
            <li class="root">
              <a href="{@url}" title="{@title}">
                <xsl:value-of select="@title" />
              </a>
            </li>
          </xsl:if>
          <xsl:if test="@isSelected">
            <li class="rootSelected">
<!--
              <xsl:if test="/bebop:page/nav:categoryPath/nav:category/@id'">
-->
                <a href="{@url}" title="{@title}">
                  <xsl:value-of select="@title" />
                </a>
<!--
              </xsl:if>
-->
<!--
              <xsl:if test="">
                <xsl:value-of select="@title" />
              </xsl:if>
-->
            </li>
          </xsl:if>
          <xsl:if test="./nav:category">
            <ul>
              <xsl:for-each select="./nav:category">
                <!-- Alle Child Kategorien -->
                <xsl:if test="@isSelected">
                  <li class="selected">
                    <xsl:if test="/bebop:page/bebop:title = 'Navigation'">
                      <xsl:value-of select="@title" />
                    </xsl:if>
                    <xsl:if test="not(/bebop:page/bebop:title = 'Navigation')">
                      <a href="{@url}" title="{@title}">
                        <xsl:value-of select="@title" />
                      </a>
                    </xsl:if>
                  </li>
                </xsl:if>
                <xsl:if test="not(@isSelected)">
                  <li>
                    <a href="{@url}" title="{@title}">
                      <xsl:value-of select="@title" />
                    </a>
                  </li>
                </xsl:if>
              </xsl:for-each>
            </ul>
          </xsl:if>
        </xsl:for-each>
      </ul>
      <br />
      <img id="menuEnd" src="{$theme-prefix}/images/menu_bottom.jpg" name="Menu_Bottom" alt="[Menu Ende]" />
    </div>
  </xsl:template>

</xsl:stylesheet>