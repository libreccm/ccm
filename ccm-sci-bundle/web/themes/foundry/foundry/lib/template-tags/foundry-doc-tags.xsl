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
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl bebop foundry ui"
                version="2.0">

    <xsl:template match="doc-chapter-list">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="doc-chapter-entry">
        <xsl:variable name="doc-chapter-entry-tree" select="./*"/>
        
        <xsl:for-each select="$foundry-doc-tree/foundry:doc-chapter">
            <xsl:apply-templates select="$doc-chapter-entry-tree">
                <xsl:with-param name="href" tunnel="yes" select="concat('#', ./@id)"/>
                <xsl:with-param name="title" tunnel="yes" select="./@title"/>
                <xsl:with-param name="doc-sections" 
                                tunnel="yes" 
                                select="./foundry:doc-section"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-chapter-title">
        <xsl:param name="title" tunnel="yes" select="''"/>
        
        <xsl:value-of select="$title"/>
    </xsl:template>

    <xsl:template match="doc-section-list">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="doc-section-entry">
        <xsl:param name="doc-sections" tunnel="yes"/>
        
        <xsl:variable name="doc-section-entry-tree" select="./*"/>
        
        <xsl:for-each select="$doc-sections">
            <xsl:apply-templates select="$doc-section-entry-tree">
                <xsl:with-param name="href" tunnel="yes" select="concat('#', ./@id)"/>
                <xsl:with-param name="title" tunnel="yes" select="./@title"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-section-title">
        <xsl:param name="title" tunnel="yes" select="''"/>
        
        <xsl:value-of select="$title"/>
    </xsl:template>
    
</xsl:stylesheet>