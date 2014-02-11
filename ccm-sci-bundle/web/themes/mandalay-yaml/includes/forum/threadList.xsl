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
  Hier wird die Thread Liste verarbeitet 
-->

<!-- EN
  Processing thread list
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

  <xsl:template match="forum:threadList">
    <table class="forum">
      <thead>
        <tr>
          <th width="50%">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/subject'"/>
            </xsl:call-template>
          </th>
          <th>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/replies'"/>
            </xsl:call-template>
          </th>
          <th>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/topic'"/>
            </xsl:call-template>
          </th>
          <th>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/lastPost'"/>
            </xsl:call-template>
          </th>
          <th>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/status'"/>
            </xsl:call-template>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:if test="count(forum:thread) = 0">
          <td colspan="5">
            <em>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'list/noPosts'"/>
              </xsl:call-template>
            </em>
          </td>
        </xsl:if>
        <xsl:for-each select="forum:thread">
          <tr>
            <xsl:attribute name="class">
              <xsl:choose>
                <xsl:when test="position() mod 2">
                  <xsl:value-of select="'odd'"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'even'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
            <td>
              <a href="{@url}">
                <xsl:value-of select="root/subject"/>
              </a>
              <br/>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'list/authorPrefix'"/>
              </xsl:call-template>
              <span class="author">
<!--
                <a>
                  <xsl:attribute name="href">
                  <xsl:value-of select="concat('mailto:', author/primaryEmail)"/>
                  </xsl:attribute>
-->
                  <xsl:value-of select="author/displayName"/>
<!--
                </a>
-->
              </span>
            </td>
            <td>
              <xsl:value-of select="numReplies"/>
            </td>
            <xsl:choose>
              <xsl:when test="root/categories">
                <td>
                  <xsl:value-of select="root/categories/name"/>
                </td>
              </xsl:when>
              <xsl:otherwise>
                <td>
                  <em>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'forum'"/>
                      <xsl:with-param name="id" select="'list/noTopic'"/>
                    </xsl:call-template>
                  </em>
                </td>
              </xsl:otherwise>
            </xsl:choose>
            <td>
              <xsl:value-of select="lastUpdate"/>
            </td>
            <td>
              <xsl:value-of select="root/status"/>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
      <tfoot>
        <tr>
          <th colspan="5">
            <xsl:apply-templates select="forum:paginator" mode="page-links"/>
          </th>
        </tr>
      </tfoot>
    </table>
  </xsl:template>
  
</xsl:stylesheet>
