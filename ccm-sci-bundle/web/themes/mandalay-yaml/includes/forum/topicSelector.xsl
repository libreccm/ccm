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
  Hier wird die Auswahlliste der Topics verarbeitet 
-->

<!-- EN
  Processing topic selector
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

  <xsl:template match="forum:topicSelector">
    <xsl:if test="count(forum:topic) > 0">
      <form action="{@baseURL}" method="get">
        <label for="{@param}">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'forum'"/>
            <xsl:with-param name="id" select="'topicSelector/prefix'"/>
          </xsl:call-template>
          &nbsp;
        </label>
        <select name="{@param}" id="{@param}">
          <option value="{@anyTopicID}">
            <xsl:if test="@anyTopicID = @currentTopicID">
              <xsl:attribute name="selected">
                <xsl:text>selected</xsl:text>
              </xsl:attribute>
            </xsl:if>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'topicSelector/allTopics'"/>
            </xsl:call-template>
          </option>
          <xsl:apply-templates/>
          <option value="{@noTopicID}">
            <xsl:if test="@noTopicID = @currentTopicID">
              <xsl:attribute name="selected">
                <xsl:text>selected</xsl:text>
              </xsl:attribute>
            </xsl:if>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'topicSelector/noTopic'"/>
            </xsl:call-template>
          </option>
        </select>
        &nbsp;
        <input type="submit">
          <xsl:attribute name="value">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'topicSelector/apply'"/>
            </xsl:call-template>
          </xsl:attribute>
        </input>
      </form>
    </xsl:if>
  </xsl:template>

  <xsl:template match="forum:topic">
    <option value="{id}">
      <xsl:if test="id = ../@currentTopicID">
        <xsl:attribute name="selected">
          <xsl:text>selected</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <xsl:value-of select="name"/>
    </option>
  </xsl:template>

</xsl:stylesheet>
