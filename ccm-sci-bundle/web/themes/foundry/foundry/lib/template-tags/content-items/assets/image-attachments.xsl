<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>]>
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
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl bebop cms foundry nav ui"
                version="2.0">
    
    <xsl:template match="image-attachments">
        <xsl:if test="$data-tree/cms:contentPanel/cms:item/imageAttachments
                      or $data-tree/nav:greetingItem/cms:item/imageAttachments">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="images-attachments//image-attachment">
        <xsl:variable name="images-layout-tree" select="current()"/>
        
        <xsl:variable name="contentitem-tree">
            <xsl:choose>
                <xsl:when test="$data-tree/nav:greetingItem">
                    <xsl:copy-of select="$data-tree/nav:greetingItem/cms:item/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$data-tree/cms:contentPanel/cms:item/*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        
        <xsl:variable name="from" as="xs:integer">
            <xsl:choose>
                <xsl:when test="..//imageAttachments/@from">
                    <xsl:value-of select="..//imageAttachments/@from"/>
                </xsl:when>
                <xsl:when test="..//imageAttachment/@select">
                    <xsl:value-of select="..//imageAttachments/@select"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="min($contentitem-tree/imageAttachments/sortKey)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
                
        <xsl:variable name="to" as="xs:integer">
            <xsl:choose>
                <xsl:when test="..//imageAttachments/@to">
                    <xsl:value-of select="..//imageAttachments/@to"/>
                </xsl:when>
                <xsl:when test="..//imageAttachment/@select">
                    <xsl:value-of select="..//imageAttachments/@select"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="max($contentitem-tree/imageAttachments/sortKey)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
                
        <xsl:for-each select="$contentitem-tree/imageAttachments[sortKey &gt;= from and sortKey &lt;= sortKey]">
            <xsl:sort select="sortKey"/>
            
            <xsl:apply-templates select="$images-layout-tree/*">
                <xsl:with-param name="src" 
                                tunnel="yes" 
                                select="concat('/cms-service/stream/image/?image_id=', ./images/id)"/>
                <xsl:with-param name="img-width"
                                tunnel="yes"
                                select="./image/width"/>
                <xsl:with-param name="img-height"
                                tunnel="yes"
                                select="./images/height"/>
                <xsl:with-param name="alt"
                                tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="string-length(./caption) &gt; 0">
                            <xsl:value-of select="./caption"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./image/displayName"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:for-each>
         
        
        
    </xsl:template>
    
</xsl:stylesheet>