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
    
    <xsl:template match="/content-item-layout//start-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/startDate">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" select="$contentitem-tree/startDate"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'startDate']">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$contentitem-tree/nav:attribute[@name = 'startDate']"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//end-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/endDate">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" select="$contentitem-tree/endDate"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'endDate']">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$contentitem-tree/nav:attribute[@name = 'endDate']"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//start-time">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/startTime">
                <xsl:call-template name="foundry:format-time">
                    <xsl:with-param name="time-elem" select="$contentitem-tree/startTime"/>
                    <xsl:with-param name="style-param" select="./style"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'startTime']">
                <xsl:call-template name="foundry:format-time">
                    <xsl:with-param name="time-elem" 
                                    select="$contentitem-tree/nav:attribute[@name = 'startTime']"/>
                    <xsl:with-param name="style-param" select="./style"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//end-time">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/endTime">
                <xsl:call-template name="foundry:format-time">
                    <xsl:with-param name="time-elem" select="$contentitem-tree/endTime"/>
                    <xsl:with-param name="style-param" select="./style"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'endTime']">
                <xsl:call-template name="foundry:format-time">
                    <xsl:with-param name="time-elem" 
                                    select="$contentitem-tree/nav:attribute[@name = 'endTime']"/>
                    <xsl:with-param name="style-param" select="./style"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="location">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/location">
                <xsl:value-of disable-output-escaping="yes" 
                      select="$contentitem-tree/location"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'location']">
                <xsl:value-of disable-output-escaping="yes"
                              select="$contentitem-tree/nav:attribute[@name = 'location']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="main-contributor">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/mainContributor">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/mainContributor"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'mainContributor']">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/nav:attribute[@name = 'mainContributor']"/>
            </xsl:when>
        </xsl:choose>
        
    </xsl:template>
    
    <xsl:template match="event-type">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/eventType">
                <xsl:value-of disable-output-escaping="yes" select="$contentitem-tree/eventType"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'eventType']">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/nav:attribute[@name = 'eventType']"/>
            </xsl:when>
        </xsl:choose>
        
    </xsl:template>
    
</xsl:stylesheet>