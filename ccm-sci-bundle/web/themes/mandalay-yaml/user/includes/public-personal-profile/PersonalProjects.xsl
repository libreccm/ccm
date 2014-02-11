<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
     Copyright 2011, Jens Pelzetter
         
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

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:ppp="http://www.arsdigita.com/PublicPersonalProfile/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav"
  version="1.0"
  >

  <xsl:template match="personalProjects">
    <div id="mainBody">      
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="availableProjectGroups">
    <xsl:variable name="groupSeparator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'PersonalProjects'"/>
        <xsl:with-param name="setting" select="'groupSeparator'"/>
        <xsl:with-param name="default" select="' | '"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="not(../projects/@all)">
      <ul class="availableData">
        <xsl:for-each select="availableProjectGroup">
          <li>
            <xsl:choose>
              <xsl:when test="@name = ../../projects/projectGroup/@name">
                <xsl:attribute name="class">
                  <xsl:value-of select="'active'"/>
                </xsl:attribute>
                <span>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'PersonalProjects'"/>
                    <xsl:with-param name="id" select="concat('projectGroup', @name)"/>
                  </xsl:call-template>
                </span>
              </xsl:when>
              <xsl:otherwise>
                <a>
                  <xsl:choose>
                    <xsl:when test="../../projects/@all = 'all'">
                      <xsl:attribute name="href">#<xsl:value-of select="@name"/></xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:attribute name="href">?group=<xsl:value-of select="@name"/></xsl:attribute>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:attribute name="title">
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'PersonalProjects'"/>
                      <xsl:with-param name="id" select="concat('projectGroupLinkTitle', @name)"/>
                  </xsl:call-template><xsl:value-of select="../../../ownerName"/></xsl:attribute>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'PersonalProjects'"/>
                    <xsl:with-param name="id" select="concat('projectGroup', @name)"/>
                  </xsl:call-template>	 
                </a>
              </xsl:otherwise>
            </xsl:choose>
          </li>
        </xsl:for-each>
      </ul>
    </xsl:if>
  </xsl:template>

  <xsl:template match="projects/projectGroup">
    <xsl:variable name="linkProjects">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'PersonalProjects'"/>
        <xsl:with-param name="setting" select="'linkProjects'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="showMembers">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'PersonalProjects'"/>
        <xsl:with-param name="setting" select="'showMembers'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="emphHead">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'PersonalProjects'"/>
        <xsl:with-param name="setting" select="'emphHead'"/>
        <xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="projectHeadText">
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'PersonalProjects'"/>
        <xsl:with-param name="id" select="'projectHeadText'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="projectMemberSeparator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'PersonalProjects'"/>
        <xsl:with-param name="setting" select="'projectMemberSeparator'"/>
        <xsl:with-param name="default" select="', '"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="showShortDesc">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'PersonalProjects'"/>
        <xsl:with-param name="setting" select="'showShortDesc'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>      
    </xsl:variable>

    <xsl:if test="./*">
      <xsl:if test="../../projects/@all">
        <h2 class="sectionHeading">
          <xsl:attribute name="id">
            <xsl:value-of select="@name"/>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'PersonalProjects'"/>
            <xsl:with-param name="id" select="concat('projectGroup', @name)"/>
          </xsl:call-template>
        </h2>
      </xsl:if>

      <div class="activeTab">
        <ul class="itemList projectList">
          <xsl:for-each select="project">
	    <xsl:call-template name="CT_SciProject_UlList">
              <xsl:with-param name="linkProject" select="$linkProjects"/>
              <xsl:with-param name="showMembers" select="$showMembers"/>
              <xsl:with-param name="emphHead" select="$emphHead"/>
              <xsl:with-param name="projectHeadText" select="$projectHeadText"/>
              <xsl:with-param name="projectMemberSeparator" select="$projectMemberSeparator"/>
              <xsl:with-param name="showShortDesc" select="$showShortDesc"/>
            </xsl:call-template>
          </xsl:for-each>
        </ul>
      </div>
      
    </xsl:if>
    
  </xsl:template>

</xsl:stylesheet>