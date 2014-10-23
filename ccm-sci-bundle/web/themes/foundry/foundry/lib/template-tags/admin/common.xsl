<?xml version="1.0" encoding="utf-8"?>
<!--
    Copyright 2014 Jens Pelzetter for the LibreCCM Foundation
    
    This file is part of the Foundry Theme Engine for LibreCCM
    
    Foundry is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Foundry is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foundry  If not, see <http://www.gnu.org/licenses/>.

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry ui"
                version="2.0">

    <xsl:template match="show-bebop-contextbar">
        <xsl:apply-templates select="$data-tree/bebop:contextBar">
            <xsl:with-param name="layout-tree" select="."/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="show-left-column">
        <xsl:choose>
            <xsl:when test="$data-tree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:left[//bebop:formWidget]">
                <form>
                    <xsl:if test="not(@method)">
                        <xsl:attribute name="method">post</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="foundry:process-attributes"/>
                    <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:left"/>
                </form>
            </xsl:when>
      
            <xsl:when test="$data-tree//bebop:currentPane/cms:container/cms:container">
                <xsl:apply-templates select="$data-tree//bebop:currentPane/cms:container/cms:container"/>
            </xsl:when>
      
            <xsl:when test="$data-tree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:left">
                <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:left"/>
            </xsl:when>
      
            <xsl:otherwise>
                <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:layoutPanel/bebop:left"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="show-tabbed-pane">
        <xsl:apply-templates select="$data-tree/bebop:tabbedPane">
            <xsl:with-param name="layout-tree" select="."/>
        </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>