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
  Hier werden die Forum Thread-Benachrichtigungen verarbeitet 
-->

<!-- EN
  Processing forum thread alerts
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

  <xsl:template match="forum:threadAlerts">
    <h3>
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="id" select="'alerts/thread/heading'"/>
      </xsl:call-template>
    </h3>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="forum:threadAlertList">
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
              <xsl:with-param name="id" select="'header/author'"/>
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
          <th>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'header/delete'"/>
            </xsl:call-template>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:if test="count(forum:threadAlert) = 0">
          <td colspan="6">
            <em>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'alerts/notSubscribed'"/>
              </xsl:call-template>
            </em>
          </td>
        </xsl:if>
        <xsl:for-each select="forum:threadAlert">
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
                <xsl:value-of select="thread/root/subject"/>
              </a>
            </td>
            <td>
              <xsl:value-of select="thread/numReplies"/>
            </td>
            <td>
              <xsl:value-of select="thread/author/displayName"/>
            </td>
            <td>
              <xsl:value-of select="thread/lastUpdate"/>
            </td>
            <td>
              <xsl:value-of select="thread/root/status"/>
            </td>
            <td>
              <input type="checkbox" name="{@param}" value="{id}"/>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>
  
</xsl:stylesheet>
