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
                <xsl:with-param name="href" tunnel="yes" select="concat('#', ./@chapter-id)"/>
                <xsl:with-param name="chapter-title" tunnel="yes" select="./@chapter-title"/>
                <xsl:with-param name="doc-sections" 
                                tunnel="yes" 
                                select="./foundry:doc-section"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-chapter-title">
        <xsl:param name="chapter-title" tunnel="yes" select="''"/>
        
        <xsl:value-of select="$chapter-title"/>
    </xsl:template>

    <xsl:template match="doc-section-list">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="doc-section-entry">
        <xsl:param name="doc-sections" tunnel="yes"/>
        
        <xsl:variable name="doc-section-entry-tree" select="./*"/>
        
        <xsl:for-each select="$doc-sections">
            <xsl:apply-templates select="$doc-section-entry-tree">
                <xsl:with-param name="href" tunnel="yes" select="concat('#', ./@section-id)"/>
                <xsl:with-param name="section-title" tunnel="yes" select="./@section-title"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-section-title">
        <xsl:param name="section-title" tunnel="yes" select="''"/>
        
        <xsl:value-of select="$section-title"/>
    </xsl:template>
    
    <xsl:template match="doc-chapters">
        <xsl:for-each select="$foundry-doc-tree/foundry:doc-chapter">
            
            <xsl:apply-templates select="document('../../templates/doc/foundry-doc-chapter.xml')/*">
                <xsl:with-param name="chapter-id" tunnel="yes" select="./@chapter-id"/>
                <xsl:with-param name="chapter-title" tunnel="yes" select="./@chapter-title"/>
                <xsl:with-param name="doc-chapter-tree" tunnel="yes" select="./*"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-chapter-layout">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="doc-chapter-id">
        <xsl:param name="chapter-id" tunnel="yes"/>
        
        <xsl:attribute name="id">
            <xsl:value-of select="$chapter-id"/>
        </xsl:attribute>
    </xsl:template>
    
    <xsl:template match="doc-sections">
        <xsl:param name="doc-chapter-tree" tunnel="yes"/>
        
        <xsl:for-each select="$doc-chapter-tree[name()='foundry:doc-section']">
            <xsl:apply-templates select="document('../../templates/doc/foundry-doc-section.xml')/*">
                <xsl:with-param name="section-id" tunnel="yes" select="./@section-id"/>
                <xsl:with-param name="section-title" tunnel="yes" select="./@section-title"/>
                <xsl:with-param name="section-static" tunnel="yes" select="./@static"/>
                <xsl:with-param name="section-generate" tunnel="yes" select="./@generate"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-section-layout">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="doc-section-id">
        <xsl:param name="section-id" tunnel="yes"/>
        
        <xsl:attribute name="id">
            <xsl:value-of select="$section-id"/>
        </xsl:attribute>
    </xsl:template>
  
    <xsl:template match="doc-section-content">
        <xsl:param name="section-static" tunnel="yes" select="''"/>
        <xsl:param name="section-generate" tunnel="yes" select="''"/>
        
        <xsl:choose>
            <xsl:when test="$section-static != ''">
                <xsl:choose>
                    <xsl:when test="document(concat('../../../doc/static-texts/', $section-static))">
                        <xsl:copy-of select="document(concat('../../../doc/static-texts/', $section-static))//main"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <div class="doc-error">
                            <xsl:value-of select="concat('Missing static text doc/static-texts/', $section-static)"/>
                        </div>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$section-generate != ''">
                <xsl:apply-templates select="document(concat($theme-prefix, '/foundry/main.xsl'))" 
                                     mode="doc">
                    <xsl:with-param name="section-generate" 
                                    tunnel="yes" 
                                    select="$section-generate"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <div class="doc-error">
                    None of the attributes <code>static</code> or <code>generate</code> has been
                    set at the definition of this section.
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="xsl:import" mode="doc">
        <xsl:param name="section-generate" tunnel="yes"/>
        
        <xsl:message>
            <xsl:value-of select="concat('parsing ', ./@href)"/>
        </xsl:message>
        <xsl:apply-templates select="document(./@href)//xsl:import" mode="doc"/>
        <xsl:apply-templates select="document(./@href)//foundry:doc[@section=$section-generate]" 
                             mode="doc">
            <xsl:sort select="./following::xsl:template[1]/@match"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="foundry:doc[@type='template-tag']" mode="doc">
        <xsl:apply-templates select="document(concat($theme-prefix, '/foundry/templates/doc/template-tag-layout.xml'))">
            <xsl:with-param name="matches" 
                            tunnel="yes" 
                            select="./following::xsl:template[1]/@match"/>
            <xsl:with-param name="doc-desc" tunnel="yes" select="./foundry:doc-desc"/>
            <xsl:with-param name="doc-attributes" tunnel="yes" select="./foundry:doc-attributes"/>
            <xsl:with-param name="doc-see-also" tunnel="yes" select="./foundry:doc-see-also"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-layout">
        <xsl:param name="matches" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="section-id" select="$matches"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-name">
        <xsl:param name="matches" tunnel="yes"/>
        
        <xsl:value-of select="$matches"/>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-desc">
        <xsl:param name="doc-desc" tunnel="yes"/>
        
        <xsl:copy-of select="$doc-desc"/>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-attributes">
        <xsl:param name="doc-attributes" tunnel="yes"/>
        
        <xsl:if test="$doc-attributes">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-attribute">
        <xsl:param name="doc-attributes" tunnel="yes"/>
        
        <xsl:variable name="doc-template-tag-attribute-layout" select="current()"/>
        
        <xsl:for-each select="$doc-attributes/foundry:doc-attribute">
            <xsl:apply-templates select="$doc-template-tag-attribute-layout/*">
                <xsl:with-param name="attribute-name" tunnel="yes" select="./@name"/>
                <xsl:with-param name="attribute-desc" tunnel="yes" select="./*"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-attribute-name">
        <xsl:param name="attribute-name" tunnel="yes"/>
        
        <xsl:value-of select="$attribute-name"/>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-attribute-desc">
        <xsl:param name="attribute-desc" tunnel="yes"/>
        
        <xsl:copy-of select="$attribute-desc"/>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-see-also">
        <xsl:param name="doc-see-also" tunnel="yes"/>
        
         <xsl:if test="$doc-see-also">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-see-also-link">
        <xsl:param name="doc-see-also" tunnel="yes"/>
        
        <xsl:variable name="doc-template-tag-see-also-layout" select="current()"/>
        
        <xsl:for-each select="$doc-see-also/foundry:doc-link">
            <xsl:apply-templates select="$doc-template-tag-see-also-layout/*">
                <xsl:with-param name="href" tunnel="yes" select="./@href"/>
                <xsl:with-param name="title" tunnel="yes" select="./@href"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-see-also-link-title">
        <xsl:param name="title" tunnel="yes"/>
        
        <xsl:value-of select="$title"/>
    </xsl:template>
    
</xsl:stylesheet>