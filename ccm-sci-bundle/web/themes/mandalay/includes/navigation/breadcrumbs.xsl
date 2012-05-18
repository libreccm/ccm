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
  Hier werden die Breadcrumbs verarbeitet 
-->

<!-- EN
     Processing Breadcrumbs
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  xmlns:forum="http://www.arsdigita.com/forum/1.0"
  exclude-result-prefixes="xsl bebop cms forum nav mandalay"
  version="1.0">

  <xsl:template name="mandalay:breadcrumbs">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/separator" />
        <xsl:with-param name="module" select="'breadcrumbs'" />
        <xsl:with-param name="setting" select="'separator'" />
        <xsl:with-param name="default" select="' -> '" />
      </xsl:call-template>
    </xsl:variable>
    
    <div id="breadcrumbs">
      <span class="breadPath">
      <span class="breadPrefix">
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'breadcrumbs'"/>
        <xsl:with-param name="id" select="'prefix'"/>
      </xsl:call-template>
      &nbsp;
      </span>
      <xsl:choose>
        <xsl:when test="count($resultTree//nav:categoryPath/nav:category) > 1 or not($resultTree/bebop:title = 'Navigation')">
          <a>
            <xsl:attribute name="href">
              <xsl:choose>
                <xsl:when test="$resultTree/nav:categoryPath/nav:category/@url">
                  <xsl:value-of select="$resultTree/nav:categoryPath/nav:category/@url"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$resultTree/ui:userBanner/@workspaceURL"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'breadcrumbs'"/>
                <xsl:with-param name="id" select="'root'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'breadcrumbs'"/>
              <xsl:with-param name="id" select="'root'"/>
            </xsl:call-template>
          </a>
          <span class="breadArrow">
            <xsl:value-of select="$separator"/>
          </span>
        </xsl:when>
        <xsl:otherwise>
          <span class="breadHi">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'breadcrumbs'"/>
              <xsl:with-param name="id" select="'root'"/>
            </xsl:call-template>
          </span>
        </xsl:otherwise>
      </xsl:choose>
      <!-- DE Behandle Sonderfälle -->
      <!-- EN Special cases-->
      <xsl:choose>
        <xsl:when test="count($resultTree//nav:categoryPath/nav:category) &lt; 2 and not($resultTree/bebop:title = 'Navigation')">

          <!-- DE Unterscheide die Sonderfälle -->
          <!-- EN choose special case -->
          <xsl:choose>

            <!-- DE Wenn Application = AtoZ, dann zeige das an -->
            <!-- EN if application = AtoZ, then set breadcrumb to AtoZ -->
            <xsl:when test="$resultTree/bebop:title = 'AtoZ'">
              <span class="breadHi">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'breadcrumbs'"/>
                  <xsl:with-param name="id" select="'atoz'"/>
                </xsl:call-template>
              </span>
            </xsl:when>

            <!-- DE Wenn Application = Suche, dann zeige das an -->
            <!-- EN If application = Search, then set breadcrumb to search -->
            <xsl:when test="$resultTree/bebop:title = 'Search'">
              <span class="breadHi">
                <xsl:choose>
                  <xsl:when test="$resultTree/@id='search'">
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'breadcrumbs'"/>
                      <xsl:with-param name="id" select="'search'"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:when test="$resultTree/@id='advanced'">
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'breadcrumbs'"/>
                      <xsl:with-param name="id" select="'advsearch'"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise/>
                </xsl:choose>
              </span>
            </xsl:when>

            <!-- DE Wenn Application = Sitemap, dann zeige das an -->
            <!-- EN If application = Sitemap, then set breadcrumb to sitemap -->
            <xsl:when test="$resultTree/@id = 'sitemapPage'">
              <span class="breadHi">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'breadcrumbs'"/>
                  <xsl:with-param name="id" select="'sitemap'"/>
                </xsl:call-template>
              </span>
            </xsl:when>

            <!-- DE Wenn Title = Log In, dann zeige das an -->
            <!-- EN If title = Log In, then set breadcrumb to login -->
            <xsl:when test="$resultTree/bebop:title = 'Log in'">
              <span class="breadHi">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'breadcrumbs'"/>
                  <xsl:with-param name="id" select="'login'"/>
                </xsl:call-template>
              </span>
            </xsl:when>

            <!-- DE Wenn Application = forum, dann zeige das an -->
            <!-- EN If application = forum, then set breadcrumb to login -->
            <xsl:when test="$resultTree/@id = 'forumPage'">
              <span class="breadHi">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'breadcrumbs'"/>
                  <xsl:with-param name="id" select="'forum'"/>
                </xsl:call-template>
                <xsl:value-of select="$resultTree//forum:name"/>
              </span>
            </xsl:when>

            <xsl:when test="$resultTree/@id = 'forumThreadPage'">
              <a>
                <xsl:attribute name="href">
                  <xsl:value-of select="'index.jsp'"/>
                </xsl:attribute>
                <xsl:attribute name="title">
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'breadcrumbs'"/>
                    <xsl:with-param name="id" select="'forum'"/>
                  </xsl:call-template>
                  <xsl:value-of select="$resultTree//forum:name"/>
                </xsl:attribute>
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'breadcrumbs'"/>
                  <xsl:with-param name="id" select="'forum'"/>
                </xsl:call-template>
                <xsl:value-of select="$resultTree//forum:name"/>
              </a>

              <span class="breadArrow">
                <xsl:value-of select="$separator"/>
              </span>

              <span class="breadHi">
                <xsl:value-of select="$resultTree//forum:threadDisplay/forum:message/subject"/>
              </span>
            </xsl:when>

            <!-- DE ContentItem in der Root-Ebene-->
            <xsl:otherwise>
              <span class="breadHi">
                <xsl:value-of select="$resultTree//cms:contentPanel/cms:item/title"/>
              </span>
            </xsl:otherwise>

          </xsl:choose>
        </xsl:when>

        <!-- DE Standardbehandlung -->
        <!-- EN Default -->
        <xsl:otherwise>

          <xsl:for-each select="$resultTree//nav:categoryPath/nav:category[not(position()=1)]">
            <xsl:choose>
              <xsl:when test="not(position()=last())">
                <a>
                  <xsl:attribute name="href">
                    <xsl:value-of select="@url"/>
                  </xsl:attribute>
                  <xsl:attribute name="title">
                    <xsl:value-of select="concat(@title, ' : ', @description)"/>
                  </xsl:attribute>
                  <xsl:call-template name="mandalay:breadcrumbText">
                    <xsl:with-param name="text">
                      <xsl:value-of select="@title"/>
                    </xsl:with-param>
                  </xsl:call-template>
                </a>
                <span class="breadArrow">
                  <xsl:value-of select="$separator"/>
                </span>
              </xsl:when>
              <xsl:otherwise>
                <!-- DE Zeige auch die ContentItems in den Breadcrumbs an -->
                <!-- EN Show contentitems in breadcrumb -->
                <xsl:choose>
                  <!-- DE Wenn es eine Navigationsseite ist, dann ist die Liste damit zu Ende -->
                  <!-- EN If indexpage, stop list here -->
                  <xsl:when test="$resultTree/bebop:title = 'Navigation' or $resultTree/@application = 'PublicPersonalProfile'">
                    <span class="breadHi">
                      <xsl:call-template name="mandalay:shying">
                        <xsl:with-param name="title">
                          <xsl:value-of select="@title"/>
                        </xsl:with-param>
                        <xsl:with-param name="mode">dynamic</xsl:with-param>
                      </xsl:call-template>
                    </span>
                  </xsl:when>

                  <!-- DE Sonst wird der letzte Navigationspunkt auch ein Link und der Content wird angehängt -->
                  <!-- EN else set last navpoint as link and append name of contentitem-->
                  <xsl:otherwise>
                    <a>
                      <xsl:attribute name="href">
                        <xsl:value-of select="@url"/>
                      </xsl:attribute>
                      <xsl:attribute name="title">

                        <xsl:call-template name="mandalay:shying">
                          <xsl:with-param name="title">
                            <xsl:value-of select="concat(@title, ' : ', @description)"/>
                          </xsl:with-param>
                          <xsl:with-param name="mode">dynamic</xsl:with-param>
                        </xsl:call-template>
                      </xsl:attribute>
                      <xsl:call-template name="mandalay:breadcrumbText">
                        <xsl:with-param name="text">

                          <xsl:call-template name="mandalay:shying">
                            <xsl:with-param name="title">
                              <xsl:value-of select="@title"/>
                            </xsl:with-param>
                            <xsl:with-param name="mode">dynamic</xsl:with-param>
                          </xsl:call-template>
                        </xsl:with-param>
                      </xsl:call-template>
                    </a>
                    <span class="breadArrow">
                      <xsl:value-of select="$separator"/>
                    </span>
                    <span class="breadHi">
<!--
                      <xsl:call-template name="mandalay:breadcrumbText">
                        <xsl:with-param name="text">
-->
                      <xsl:call-template name="mandalay:shying">
                        <xsl:with-param name="title">
                          <!-- DE Kürze letzten Eintrag beim ersten Satzzeichen -->
                          <!-- EN Cut last entry to first occurrence of a punctuation mark -->
<!--                          <xsl:value-of select="substring-before(substring-before(substring-before(substring-before(substring-before(substring-before($resultTree//cms:contentPanel/cms:item/title, '.'), '?'), '!'). ':'), '-'), ';')"/> -->
                          <xsl:choose>
                            <xsl:when test="string-length(substring-before($resultTree//cms:contentPanel/cms:item/title, '.')) > 10">
                              <xsl:value-of select="substring-before($resultTree//cms:contentPanel/cms:item/title, '.')"/>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:value-of select="$resultTree//cms:contentPanel/cms:item/title"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:with-param>
                        <xsl:with-param name="mode">dynamic</xsl:with-param>
                      </xsl:call-template>
<!--  
                        </xsl:with-param>
                      </xsl:call-template>
-->
                    </span>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </span>
    </div>
  </xsl:template>

  <xsl:template name="mandalay:breadcrumbText">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:param name="text"/>
    
    <xsl:variable name="limit">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/limit" />
        <xsl:with-param name="module" select="'breadcrumbs'" />
        <xsl:with-param name="setting" select="'limit'" />
        <xsl:with-param name="default" select="'15'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="minOmit">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/minOmit" />
        <xsl:with-param name="module" select="'breadcrumbs'" />
        <xsl:with-param name="setting" select="'minOmit'" />
        <xsl:with-param name="default" select="'5'" />
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="length">
      <xsl:value-of select="string-length($text)"/>
    </xsl:variable>
    
    <xsl:choose>
      <!-- DE Text muß auf length - minOmit gekürzt werden -->
      <!-- EN Shorten text to length - minOmit -->
      <xsl:when test="($length > $limit) and ($length - $limit &lt; $minOmit)">
        <xsl:variable name="partLength">
          <xsl:value-of select="(($length - $minOmit) div 2) - 1"/>
        </xsl:variable>
        <xsl:value-of select="substring($text, 1, ceiling($partLength))"/>
        ...
        <xsl:value-of select="substring($text, $length - floor($partLength))"/>        
      </xsl:when>
      <!-- DE Text muß auf limit gekürzt werden -->
      <!-- EN Shorten text to limit-->
      <xsl:when test="$length - $limit > $minOmit">
        <xsl:variable name="partLength">
          <xsl:value-of select="(($limit - 3) div 2) - 1"/>
        </xsl:variable>
        <xsl:value-of select="substring($text, 1, floor($partLength))"/>
        ...
        <xsl:value-of select="substring($text, $length - ceiling($partLength))"/>        
      </xsl:when>
      <!-- DE Text muß micht gekürzt werden -->
      <!-- EN No need to shorten the text -->
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>

</xsl:stylesheet>
