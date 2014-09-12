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
                version="1.0">

    <foundry:doc section="devel">
        <foundry:doc-param name="template-file"
                           mandantory="yes">
            The name of the template file to process.
        </foundry:doc-param>
        <foundry:doc-desc>
            This template is the entry point for the template parser.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:process-template">
        <xsl:param name="template-file"/>
        
        <xsl:apply-templates select="document(concat($theme-prefix, '/templates/', $template-file))"/>
    </xsl:template>

    <foundry:doc section="user">
        <foundry:doc-desc>
            Root element of a template. Generates the doctype statement and and 
            <code>&lt;html&gt;</code> root element.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="page-layout">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
         <html xmlns="http://www.w3.org/1999/xhtml">
            <xsl:attribute name="lang">
                <xsl:value-of select="$language"/>
            </xsl:attribute>
            <xsl:attribute name="id">
                <xsl:choose>
                    <xsl:when test="@application = 'admin' or @application = 'content-center' or @application = 'content-section' 
                          or @application = 'theme' or @application = 'shortcuts' or @application = 'subsite' or @application = 'terms' or @application = 'atoz' or @application = 'ds'
                          or @class = 'cms-admin' or @class = 'admin'">
                        <xsl:text>cms</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>site</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates/>
        </html>
    </xsl:template>
    
    <foundry:doc section="user">
        <foundry:doc-desc>
            Root element for generating a HTML fragment instead of a complete HTML document.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="fragment-layout">
        <div class="ccm-fragment">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template name="mandalay:set-id-and-class">
        <xsl:param name="current-layout-node" select="."/>
    
        <xsl:variable name="cond-class">
            <xsl:if test="$current-layout-node/@classIf">
                <!-- DE Funktioniert leider nicht in einer Zeile, daher die Hilfsvariable -->
                <xsl:variable name="key" select="substring-before($current-layout-node/@classIf, ',')"/>
                <xsl:variable name="condition">
                    <xsl:apply-templates select="//*[@id=$key]"/>
                </xsl:variable>
        
                <xsl:if test="normalize-space($condition)">
                    <xsl:value-of select="substring-after($current-layout-node/@classIf, ', ')"/>
                </xsl:if>
            </xsl:if>
        </xsl:variable>
    
        <xsl:variable name="type-class">
            <xsl:if test="$current-layout-node/@setTypeClass='true'">
                <xsl:call-template name="mandalay:getContentTypeName"/>
            </xsl:if>
        </xsl:variable>
    
        <xsl:variable name="color-class">
            <xsl:if test="$current-layout-node/@withColorset='true'">
                <xsl:call-template name="mandalay:getColorset"/>
            </xsl:if>
        </xsl:variable>
    
        <xsl:if test="$current-layout-node/@id">
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="$current-layout-node/@class or $cond-class != '' or $type-class != '' or $color-class != ''">
            <xsl:attribute name="class">
                <xsl:value-of select="normalize-space(concat($current-layout-node/@class, ' ', $cond-class, ' ', $type-class, ' ', $color-class))"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>