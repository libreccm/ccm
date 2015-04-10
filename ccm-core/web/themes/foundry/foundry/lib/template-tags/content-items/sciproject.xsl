<?xml version="1.0" encoding="utf-8"?>
<!--
    Copyright 2015 Jens Pelzetter for the LibreCCM Foundation
    
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
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciproject-begin">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/projectBegin">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/projectBegin"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$orgaunit-data/lifeSpan/begin">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/lifeSpan/begin"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$orgaunit-data/nav:attribute[@name = 'projectBegin']">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/nav:attribute[@name = 'projectBegin']"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciproject-end">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/projectEnd">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/projectEnd"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$orgaunit-data/lifeSpan/end">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/lifeSpan/end"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$orgaunit-data/nav:attribute[@name = 'projectEnd']">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/nav:attribute[@name = 'projectEnd']"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciproject-desc">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/projectDesc">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data/projectDesc"/>
            </xsl:when>
            <xsl:when test="$orgaunit-data/description">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data/description"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciproject-shortdesc">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/projectShortDesc">
                <xsl:value-of select="$orgaunit-data/projectShortDesc"/>
            </xsl:when>
            <xsl:when test="$orgaunit-data/shortDesc">
                <xsl:value-of select="$orgaunit-data/shortDesc"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="$orgaunit-data/sponsors">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors//sponsor">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$orgaunit-data/sponsors/sponsor">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="sponsor-name" 
                                tunnel="yes" 
                                select="."/>
                <xsl:with-param name="funding-code" 
                                tunnel="yes" 
                                select="./@fundingCode"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors//sponsor//sponsor-name">
        <xsl:param name="sponsor-name" tunnel="yes"/>
        
        <xsl:value-of select="$sponsor-name"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors//sponsor//funding-code">
        <xsl:param name="funding-code" tunnel="yes"/>
        
        <xsl:value-of select="$funding-code"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors//sponsor/if-funding-code">
        <xsl:param name="funding-code" tunnel="yes"/>
        
        <xsl:if test="string-length($funding-code) &gt; 0"/>
    </xsl:template>
        
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//funding">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:value-of select="$orgaunit-data/funding"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//funding-volume">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:value-of select="$orgaunit-data/fundingVolume"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//if-funding">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="string-length($orgaunit-data/funding) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//if-funding-volume">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="string-length($orgaunit-data/fundingVolume) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    
</xsl:stylesheet>