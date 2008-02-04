<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/london/navigation"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav"
  version="1.0">

  <xsl:template name="leftNav">
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
            <xsl:if test="">
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
              <xsl:if test="not(@isSelected)">
                <li>
                  <a href="{@url}" title="{@title}">
                    <xsl:value-of select="@title" />
                  </a>
                </li>
              </xsl:if>
              <xsl:if test="@isSelected">
                <li class="selected">
                  <xsl:value-of select="@title" />
                </li>
              </xsl:if>
            </xsl:for-each>
          </ul>
        </xsl:if>

            <!-- category above -->
<!--
            <xsl:for-each select="../../nav:category[@isSelected='true']">
              <xsl:if test="../../nav:categoryMenu">
                <p>
                <a href="{$dispatcher-prefix}/portal/">
                  <xsl:attribute name="title">up to homepage</xsl:attribute>
                  <xsl:text disable-output-escaping="yes">&amp;</xsl:text>#094;
                  <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;home
                </a>
                </p>
              </xsl:if>
              <xsl:if test="not(../../nav:categoryMenu)">
                <p>
                  <a href="{@url}">
                    <xsl:attribute name="title"><xsl:value-of select="@title" /></xsl:attribute>
                    <xsl:value-of select="@title" />
                  </a>
                </p>
              </xsl:if>
            </xsl:for-each>
-->
          <!-- category above if homepage/root-->
<!--
            <xsl:for-each select="../../nav:categoryMenu">
              <a href="{$dispatcher-prefix}/portal/">
                <xsl:attribute name="title">up to homepage</xsl:attribute>
                <xsl:text disable-output-escaping="yes">&amp;</xsl:text>#094;
                <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;home
              </a>
            </xsl:for-each>
            <span class="hide">|</span>
-->
          <!-- selected category -->
<!--
            <p>
              <a href="{@url}" title="{@title}"><xsl:value-of select="@title" /></a>
            </p>
            <span class="hide">|</span>
-->
        </xsl:for-each>
      </ul>
      <br />
      <img id="menuEnd" src="{$theme-prefix}/images/menu_bottom.jpg" name="Menu_Bottom" alt="[Menu Ende]" />
    </div>
  </xsl:template>

</xsl:stylesheet>