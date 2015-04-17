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
    
    <xsl:template match="content-item-layout//scipublications//authors">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/authors">
            <xsl:apply-templates>
                <xsl:with-param name="authors" 
                                tunnel="yes" 
                                select="$contentitem-tree/authors"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//authors//author">
        <xsl:param name="authors" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$authors/author">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="author" tunnel="yes" select="."/>
                <xsl:with-param name="href" 
                                tunnel="yes" 
                                select="foundry:generate-contentitem-link(./@oid)"/>
                <xsl:with-param name="position" tunnel="yes" select="position()"/>
                <xsl:with-param name="last" tunnel="yes" select="last()"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//authors//author//surname">
        <xsl:param name="author" tunnel="yes"/>
        
        <xsl:value-of select="concat(./@before, $author/surname, ./@after)"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//authors//author//givenname">
        <xsl:param name="author" tunnel="yes"/>
        
        <xsl:value-of select="concat(./@before, $author/givenname, ./@after)"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//authors//author//editor">
        <xsl:param name="author" tunnel="yes"/>
        
        <xsl:if test="foundry:boolean($author/@isEditor)">
            <xsl:value-of select="concat(./@before,
                                          foundry:get-static-text('scipublications', 
                                                                 ./@text),
                                         ./@after)"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//authors//author//separator">
        <xsl:param name="author" tunnel="yes"/>
        <xsl:param name="position" tunnel="yes"/>
        <xsl:param name="last" tunnel="yes"/>
        
        <xsl:if test="$position != $last">
            <xsl:value-of select="."/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//publisher">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/publisher">
            <xsl:apply-templates>
                <xsl:with-param name="publisher" 
                                tunnel="yes"
                                select="$contentitem-tree/publisher"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//publisher//name">
        <xsl:param name="publisher" tunnel="yes"/>
        
        <xsl:value-of select="concat(./@before, $publisher/publisherName, ./@after)"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//publisher//place">
        <xsl:param name="publisher" tunnel="yes"/>
        
        <xsl:value-of select="concat(./@before, $publisher/place, ./@after)"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//year-of-publication">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/yearOfPublication"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//if-year-of-publication">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/yearOfPublication">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//number-of-pages">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/numberOfPages"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-number-of-pages">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/numberOfPages">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//number-of-volumes">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/numberOfVolumes"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-number-of-volumes">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/numberOfVolumes">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//volume">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/volume"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//if-volume">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/volume">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="content-item-layout//edition">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/volume"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//if-edition">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/volume">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//isbn">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/isbn"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//if-isbn">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/isbn">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="content-item-layout//scipublications//series">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/series">
            <xsl:apply-templates>
                <xsl:with-param name="series" 
                                tunnel="yes" 
                                select="$contentitem-tree/series"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//series//series">
        <xsl:param name="series" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$series/series">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="series-obj" 
                                tunnel="yes"
                                select="."/>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="foundry:generate-contentitem-link(./@oid)"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//series//series//series-name">
        <xsl:param name="series-obj" tunnel="yes"/>
        
        <xsl:value-of select="$series-obj/title"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//series//series//series-volume">
        <xsl:param name="series-obj" tunnel="yes"/>
        
        <xsl:value-of select="$series-obj/@volume"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-reviewed">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="foundry:boolean($contentitem-tree/reviewed)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//abstract">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="foundry:boolean(./@disable-output-escaping)">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/abstract"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$contentitem-tree/abstract"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-misc">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/misc">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//misc">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="foundry:boolean(./@disable-output-escaping)">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/misc"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$contentitem-tree/misc"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//export-links">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/publicationExportLink">
            <xsl:apply-templates>
                <xsl:with-param name="export-links"
                                tunnel="yes"
                                select="$contentitem-tree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//export-links//export-link">
        <xsl:param name="export-links" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$export-links/publicationExportLink">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="concat($dispatcher-prefix,
                                               '/scipublications/export/?format=', 
                                               ./formatKey, 
                                               '&amp;publication=', ./publicationId)"/>
               <xsl:with-param name="export-formatkey" 
                               tunnel="yes"
                               select="./formatKey"/>
               <xsl:with-param name="export-formatname" 
                               tunnel="yes"
                               select="./formatName"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//export-links//export-link//export-link-formatname">
        <xsl:param name="export-formatname" tunnel="yes"/>
        
        <xsl:value-of select="$export-formatname"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-place">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/place">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//place">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/place"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-pages-from">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/pagesFrom">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//pages-from">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/pagesFrom"/>
    </xsl:template>
        
    <xsl:template match="content-item-layout//scipublications//if-pages-to">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/pagesTo">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//pages-to">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/pagesTo"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-number">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/number">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//number">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/number"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-year-first-published">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/yearFirstPublished">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//year-first-published">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/yearFirstPublished"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//library-signatures">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/librarysignatures">
            <xsl:apply-templates>
                <xsl:with-param name="signatures" 
                                tunnel="yes" 
                                select="$contentitem-tree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//library-signatures//signature">
        <xsl:param name="signatures" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$signatures/librarysignatures">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="signature" tunnel="yes" select="."/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//library-signatures//signature//library">
        <xsl:param name="signature" tunnel="yes"/>
        
        <xsl:value-of select="$signature/library"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//library-signatures//signature//signature-text">
        <xsl:param name="signature" tunnel="yes"/>
        
        <xsl:value-of select="$signature/signature"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//library-signatures//signature//library-link">
        <xsl:param name="signature" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="href" 
                            tunnel="yes" 
                            select="$signature/librarylink"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//library-signatures//signature//signature-misc">
        <xsl:param name="signature" tunnel="yes"/>
        
        <xsl:value-of select="$signature/misc"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-organization">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/organization">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//organization">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/organization/title"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//if-orderer">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/orderer">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//scipublications//orderer">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/orderer/title"/>
    </xsl:template>
    
</xsl:stylesheet>