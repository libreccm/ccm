<?xml version="1.0" encoding="UTF-8"?>
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
                xmlns:forum="http://www.arsdigita.com/forum/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">
    
    <foundry:doc-file>
        <foundry:doc-file-title>Forum</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                This file defines several tags for displaying a Forum 
                (ccm-forum).
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Show the name of the forum.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="show-forum-name">
        <xsl:value-of select="$data-tree/forum:name"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Applies the enclosed tags only if there is a forum introduction.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="if-forum-introducation">
        <xsl:if test="string-length($data-tree/forum:introducation) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Shows the introducation text of a forum.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="show-forum-introduction">
        <xsl:value-of select="$data-tree/forum:introduction"/>
    </xsl:template>
    
    <xsl:template match="forum-tabs">
        <xsl:if test="$data-tree/forum:forum">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="forum-tabs//forum-tab">
        <xsl:variable name="tab-layout-tree" select="./*"/>
        
        <xsl:for-each select="$data-tree/forum:forum/forum:forumMode">
            <xsl:apply-templates select="$tab-layout-tree">
                <xsl:with-param name="href" 
                                tunnel="yes" 
                                select="./@url"/>
                <xsl:with-param name="label"
                                tunnel="yes"
                                select="./@label"/>
                <xsl:with-param name="class"
                                tunnel="yes"
                                select="if (./@selected = 1)
                                   then 'selected'
                                   else ''"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="forum-tabs//forum-tab//tab-label">
        <xsl:param name="label" tunnel="yes"/>
        
        <xsl:value-of select="$label"/>
    </xsl:template>
    
    <xsl:template match="forum-current-tab">
        <xsl:variable name="forum-data-tree" select="$data-tree/forum:forum/*"/>
        
        <xsl:variable name="selected-tab" 
                      select="$data-tree/forum:forum/forum:forumMode[@selected = 1]/@mode"/>
        
        <xsl:apply-templates select="./forum-tab[@mode = $selected-tab]">
            <xsl:with-param name="forum-data-tree" 
                            tunnel="yes" 
                            select="$forum-data-tree"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="forum-options">
        
    </xsl:template>
    
    <xsl:template match="forum-options//forum-option">
    </xsl:template>
    
    <xsl:template match="forum-threads">
        
    </xsl:template>
    
    <xsl:template match="forum-threads//forum-thread">
        
    </xsl:template>
    
    
</xsl:stylesheet>