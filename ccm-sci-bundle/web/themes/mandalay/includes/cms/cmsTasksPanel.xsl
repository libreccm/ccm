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
  Hier werden die cmsTaskPanel verarbeitet 
-->

<!-- EN
  Processing cmsTaskPanel
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav mandalay" 
  version="1.0">
  
  <!-- DE Erzeugt die Liste mit den zugewiesenen Aufgaben -->
  <!-- EN Create a list of tasks -->
  <xsl:template match="cms:tasksPanel">
    <xsl:choose>
      <xsl:when test="count(cms:tasksPanelTask) = 0">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'taskPanel/noTasks'"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <table>
          <thead>
            <tr>
              <xsl:apply-templates select="bebop:link | bebop:label" mode="tableHeadCell"/>
            </tr>
          </thead>
          <tbody>
            <xsl:apply-templates select="cms:tasksPanelTask"/>
          </tbody>
        </table>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- DE Erzeugt einen Eintrag in der Liste -->
  <!-- EN Creates an entry to the list -->
  <xsl:template match="cms:tasksPanelTask">
    <tr>
      <td>
        <a>
          <xsl:attribute name="href">
            <xsl:call-template name="mandalay:linkParser">
              <xsl:with-param name="link" select="concat(@sectionPath, '/admin/item.jsp?item_id=' , @itemID)"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:value-of select="@pageTitle"/>
        </a>
      </td>
      <td>
        <a title="{@taskDescription}">
            <xsl:attribute name="href">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link" select="@actionURL"/>
              </xsl:call-template>
            </xsl:attribute>
          <xsl:value-of select="@taskLabel"/>
        </a>
      </td>
      <td>
        <xsl:value-of select="@dueDate"/>
      </td>
      <td>
        <xsl:choose>
          <xsl:when test="@status=1">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'taskPanel/lockedByYou'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="@status=2">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'taskPanel/notLocked'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="@status=3">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'taskPanel/lockedBySomeoneElse'"/>
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>
      </td>
      <td>
        <xsl:choose>
          <xsl:when test="@assignee">
            <xsl:value-of select="@assignee"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'taskPanel/notAssigned'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:value-of select="@processLabel"/>
      </td>
    </tr>
  </xsl:template>
  
  <!-- DE Besondere Behandlung für einen Link oder ein Label in einem Tabellenkopf -->
  <!-- EN Special mode for a link or label in a table head -->
  <xsl:template match="bebop:link | bebop:label" mode="tableHeadCell">
    <th>
      <xsl:apply-templates select="."/>
    </th>
  </xsl:template>
</xsl:stylesheet>
