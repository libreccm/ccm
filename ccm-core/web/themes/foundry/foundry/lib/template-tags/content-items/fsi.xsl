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
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">

    <xsl:template match="/content-item-layout//fsi-description">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/description">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/description"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'description']">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/nav:attribute[@name = 'description']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//fsi-link">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="mode" 
                      select="if (./@mode = 'stream')
                              then 'stream'
                              else 'download'"/>

        <xsl:apply-templates>
            <xsl:with-param name="href" tunnel="yes">
                <xsl:choose>
                    <xsl:when test="foundry:boolean(./@use-filename)">
                        <xsl:value-of select="concat($dispatcher-prefix, 
                                             '/cms-service/',
                                             $mode, 
                                             '/asset/',
                                             $contentitem-tree/file/name,
                                             '?asset_id=', 
                                             $contentitem-tree/file/id)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($dispatcher-prefix, 
                                             '/cms-service/',
                                             $mode,
                                             '/asset/?asset_id=', 
                                             $contentitem-tree/file/id)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param> 
        </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>