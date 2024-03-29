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
    
    <foundry:doc-file>
        <foundry:doc-file-title>
            Tags for ccm-cms-types-event
        </foundry:doc-file-title>
        <foundry:doc-desc>
            <p>
                This tags are used to output the values of special properties 
                of the Event type provided by the ccm-cms-types-event module.
            </p>
        </foundry:doc-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the start date of an event. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
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
    
    <xsl:template match="/content-item-layout//start-datetime">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/startDate">
                <xsl:choose>
                    <xsl:when test="$contentitem-tree/startTime">
                        <xsl:variable name="year" select="$contentitem-tree/startDate/@year"/>
                        <xsl:variable name="month">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/startDate/@month) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/startDate/@month)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/startDate/@month"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="day">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/startDate/@day) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/startDate/@day)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/startDate/@day"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="hour">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/startTime/@hour) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/startTime/@hour)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/startTime/@hour"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="minute">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/startTime/@minute) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/startTime/@minute)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/startTime/@minute"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>

                        <xsl:apply-templates>
                            <xsl:with-param name="datetime"
                                            tunnel="yes"
                                            select="concat($year, '-', $month, '-', $day, 'T', $hour, ':', $minute)"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="year" select="$contentitem-tree/startDate/@year"/>
                        <xsl:variable name="month">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/startDate/@month) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/startDate/@month)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/startDate/@month"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="day">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/startDate/@day) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/startDate/@day)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/startDate/@day"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        
                        <xsl:apply-templates>
                            <xsl:with-param name="datetime"
                                            tunnel="yes"
                                            select="concat($year, '-', $month, '-', $day)"/>
                        </xsl:apply-templates>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'startDate']">
                <xsl:choose>
                    <xsl:when test="$contentitem-tree/nav:attribute[@name = 'startTime']">
                        <xsl:variable name="year" select="$contentitem-tree/nav:attribute[@name = 'startDate']/@year"/>
                        <xsl:variable name="month">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'startDate']/@month) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'startDate']/@month)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']/@month"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="day">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'startDate']/@day) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'startDate']/@day)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']/@day"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="hour">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'startDate']/@hour) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'startDate']/@hour)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']/@hour"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="minute">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'startDate']/@minute) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'startDate']/@minute)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']/@minute"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>

                        <xsl:apply-templates>
                            <xsl:with-param name="datetime"
                                            tunnel="yes"
                                            select="concat($year, '-', $month, '-', $day, 'T', $hour, ':', $minute)"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="year" select="$contentitem-tree/nav:attribute[@name = 'startDate']/@year"/>
                        <xsl:variable name="month">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'startDate']/@month) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'startDate']/@month)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']/@month"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="day">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'startDate']/@day) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'startDate']/@day)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']/@day"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        
                        <xsl:apply-templates>
                            <xsl:with-param name="datetime"
                                            tunnel="yes"
                                            select="concat($year, '-', $month, '-', $day)"/>
                        </xsl:apply-templates>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//if-end-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="start-date">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/startDate">
                    <xsl:value-of select="$contentitem-tree/startDate"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'startDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="end-date">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/endDate">
                    <xsl:value-of select="$contentitem-tree/endDate"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'endDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:if test="($start-date != $end-date)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//if-no-end-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="start-date">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/startDate">
                    <xsl:value-of select="$contentitem-tree/startDate"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'startDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="end-date">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/endDate">
                    <xsl:value-of select="$contentitem-tree/endDate"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'endDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:if test="($start-date = $end-date)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the end date of an event. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//end-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="start-date">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/startDate">
                    <xsl:value-of select="$contentitem-tree/startDate"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'startDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'startDate']"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="end-date">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/endDate">
                    <xsl:value-of select="$contentitem-tree/endDate"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'endDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:if test="($start-date != $end-date) or foundry:boolean(./@show-always)">
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
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//end-datetime">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/endDate">
                <xsl:choose>
                    <xsl:when test="$contentitem-tree/endTime">
                        <xsl:variable name="year" select="$contentitem-tree/endDate/@year"/>
                        <xsl:variable name="month">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/endDate/@month) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/endDate/@month)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/endDate/@month"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="day">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/endDate/@day) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/endDate/@day)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/endDate/@day"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="hour">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/endTime/@hour) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/endTime/@hour)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/endTime/@hour"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="minute">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/endTime/@minute) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/endTime/@minute)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/endTime/@minute"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>

                        <xsl:apply-templates>
                            <xsl:with-param name="datetime"
                                            tunnel="yes"
                                            select="concat($year, '-', $month, '-', $day, 'T', $hour, ':', $minute)"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="year" select="$contentitem-tree/endDate/@year"/>
                        <xsl:variable name="month">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/endDate/@month) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/endDate/@month)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/endDate/@month"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="day">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/endDate/@day) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/endDate/@day)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/endDate/@day"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        
                        <xsl:apply-templates>
                            <xsl:with-param name="datetime"
                                            tunnel="yes"
                                            select="concat($year, '-', $month, '-', $day)"/>
                        </xsl:apply-templates>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'endDate']">
                <xsl:choose>
                    <xsl:when test="$contentitem-tree/nav:attribute[@name = 'endTime']">
                        <xsl:variable name="year" select="$contentitem-tree/nav:attribute[@name = 'endDate']/@year"/>
                        <xsl:variable name="month">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'endDate']/@month) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'endDate']/@month)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']/@month"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="day">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'endDate']/@day) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'endDate']/@day)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']/@day"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="hour">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'endDate']/@hour) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'endDate']/@hour)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']/@hour"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="minute">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'endDate']/@minute) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'endDate']/@minute)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']/@minute"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>

                        <xsl:apply-templates>
                            <xsl:with-param name="datetime"
                                            tunnel="yes"
                                            select="concat($year, '-', $month, '-', $day, 'T', $hour, ':', $minute)"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="year" select="$contentitem-tree/nav:attribute[@name = 'endDate']/@year"/>
                        <xsl:variable name="month">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'endDate']/@month) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'endDate']/@month)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']/@month"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="day">
                            <xsl:choose>
                                <xsl:when test="string-length($contentitem-tree/nav:attribute[@name = 'endDate']/@day) = 1">
                                    <xsl:value-of select="concat('0', $contentitem-tree/nav:attribute[@name = 'endDate']/@day)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'endDate']/@day"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        
                        <xsl:apply-templates>
                            <xsl:with-param name="datetime"
                                            tunnel="yes"
                                            select="concat($year, '-', $month, '-', $day)"/>
                        </xsl:apply-templates>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>

    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the start time of an event. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
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
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the end time of an event. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//end-time">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/endTime">
                <xsl:apply-templates/>
                <xsl:call-template name="foundry:format-time">
                    <xsl:with-param name="time-elem" select="$contentitem-tree/endTime"/>
                    <xsl:with-param name="style-param" select="./style"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'endTime']">
                <xsl:apply-templates/>
                <xsl:call-template name="foundry:format-time">
                    <xsl:with-param name="time-elem" 
                                    select="$contentitem-tree/nav:attribute[@name = 'endTime']"/>
                    <xsl:with-param name="style-param" select="./style"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of teh location property of an event.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="location">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="(string-length($contentitem-tree/location) &gt; 0)
                      or (string-length($contentitem-tree/nav:attribute[@name = 'location']) &gt; 0)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the location text property of an event.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="location//location-text">
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
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the main contributor property of an event.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="main-contributor">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="(string-length($contentitem-tree/mainContributor) &gt; 0)
                      or (string-length($contentitem-tree/nav:attribute[@name = 'mainContributor']) &gt; 0)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the main contributor text property of an 
                event.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="main-contributor//main-contributor-text">
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
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the date addendum property of an event.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="event-date-addendum">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="(string-length($contentitem-tree/eventDate) &gt; 0)
                      or (string-length($contentitem-tree/nav:attribute[@name = 'eventDate']) &gt; 0)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the event date addendum text of an event.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="event-date-addendum//event-date-addendum-text">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/eventDate">
                <xsl:value-of select="$contentitem-tree/eventDate"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'eventDate']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'eventDate']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="event-type">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="(string-length($contentitem-tree/eventType) &gt; 0)
                      or (string-length($contentitem-tree/nav:attribute[@name = 'eventType']) &gt; 0)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="event-type//event-type-text">
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