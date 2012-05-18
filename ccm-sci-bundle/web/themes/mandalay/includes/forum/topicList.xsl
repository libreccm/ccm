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
  Hier wird die Topic List verarbeitet 
-->

<!-- EN
  Processing topic list
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  xmlns:forum="http://www.arsdigita.com/forum/1.0"
  exclude-result-prefixes="xsl bebop cms nav mandalay portal portlet forum"
  version="1.0">

  <xsl:template match="forum:topicList">
    <table class="forum">
      <thead>
        <tr>
          <th width="50%">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/topic'"/>
            </xsl:call-template>
          </th>
          <th>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/threads'"/>
            </xsl:call-template>
          </th>
          <th>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/lastPost'"/>
            </xsl:call-template>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:for-each select="forum:topicSummary">
          <xsl:variable name="class">
            <xsl:choose>
              <xsl:when test="position() mod 2">
                <xsl:text>odd</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>even</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <tr class="{$class}">
            <td>
              <xsl:choose>
                <xsl:when test="numThreads > 0">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:call-template name="make-url">
                        <xsl:with-param name="base-url" select="../@baseURL"/>
                        <xsl:with-param name="name" select="../@param"/>
                        <xsl:with-param name="value" select="id"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:value-of select="name"/>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="name"/>                  
                </xsl:otherwise>
              </xsl:choose>
            </td>
            <td>
              <xsl:value-of select="numThreads"/>
            </td>
            <td>
              <xsl:choose>
                <xsl:when test="numThreads > 0">
                  <xsl:value-of select="latestPost"/>
                </xsl:when>
                <xsl:otherwise>
                  <em>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'forum'"/>
                      <xsl:with-param name="id" select="'list/noPosts'"/>
                    </xsl:call-template>
                  </em>
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
        </xsl:for-each>
        <xsl:for-each select="forum:noTopicSummary">
          <xsl:variable name="class">
            <xsl:choose>
              <xsl:when test="count(../forumTopicSummary) mod 2">
                <xsl:text>odd</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>even</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <tr class="{$class}">
            <td>
              <xsl:choose>
                <xsl:when test="numThreads > 0">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:call-template name="make-url">
                        <xsl:with-param name="base-url" select="../@baseURL"/>
                        <xsl:with-param name="name" select="../@param"/>
                        <xsl:with-param name="value" select="id"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <em>
                      <xsl:text>None</xsl:text>
                    </em>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <em>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'forum'"/>
                      <xsl:with-param name="id" select="'list/noThreads'"/>
                    </xsl:call-template>
                  </em>
                </xsl:otherwise>
              </xsl:choose>
            </td>
            <td><xsl:value-of select="numThreads"/></td>
            <td>
              <xsl:choose>
                <xsl:when test="numThreads > 0">
                  <xsl:value-of select="latestPost"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'forum'"/>
                    <xsl:with-param name="id" select="'list/noLatestPost'"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>    
  </xsl:template>

</xsl:stylesheet>
