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
  Hier wird das Portlet Tasklist verarbeitet 
-->

<!-- EN
  Processing portlet tasklist
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
  exclude-result-prefixes="xsl bebop cms nav mandalay portal portlet"
  version="1.0">

  <xsl:template match="portlet:taskList" mode="setHeading">
    <xsl:variable name="setHeading">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'portletTaskList'"/>
        <xsl:with-param name="setting" select="'setHeading'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:value-of select="$setHeading"/>
  </xsl:template>
  
  <xsl:template match="portlet:taskList">
    <div class="taskList">
      <xsl:choose>
        <xsl:when test="count(portlet:taskListTask) > '0'">
          <dl>
            <xsl:apply-templates/>
          </dl>        
        </xsl:when>
        
        <xsl:otherwise>
          <span>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'portlet'"/>
              <xsl:with-param name="id" select="'taskList/noTasks'"/>
            </xsl:call-template>
          </span>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>
    
  <xsl:template match="portlet:taskListTask">
    <dt>
      <xsl:if test="position() mod 2 = 1">
        <xsl:attribute name="class">
          <xsl:value-of select="'odd'"/>
        </xsl:attribute>
      </xsl:if>
      <a>
        <xsl:attribute name="href">
          <xsl:call-template name="mandalay:linkParser">
            <xsl:with-param name="link" select="@summaryURL"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:value-of select="@pageTitle"/>
      </a>
    </dt>
    <dd>
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'portlet'"/>
        <xsl:with-param name="id" select="'taskList/action'"/>
      </xsl:call-template>
      <a>
        <xsl:attribute name="href">
          <xsl:call-template name="mandalay:linkParser">
            <xsl:with-param name="link" select="@authoringURL"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:value-of select="@taskType"/>
        </xsl:attribute>
        <xsl:value-of select="@taskType"/>
      </a>
    </dd>
    <dd>
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'portlet'"/>
        <xsl:with-param name="id" select="'taskList/dueDate'"/>
      </xsl:call-template>
      <xsl:value-of select="@dueDate"/>
    </dd>
    <dd>
      <xsl:value-of select="@isLocked"/>,
      <xsl:if test="@assigneeCount='0'">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'portlet'"/>
          <xsl:with-param name="id" select="'taskList/assignedUsers/none'"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="@assigneeCount='1'">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'portlet'"/>
          <xsl:with-param name="id" select="'taskList/assignedUsers/one'"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="@assigneeCount>'1'">
        <xsl:value-of select="@assigneeCount"/>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'portlet'"/>
          <xsl:with-param name="id" select="'taskList/assignedUsers/more'"/>
        </xsl:call-template>
      </xsl:if>
    </dd>
  </xsl:template>
  
</xsl:stylesheet>
