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
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">

    <foundry:doc-file>
        <foundry:doc-file-title>Root template tags</foundry:doc-file-title>
        <foundry:doc-file-desc>
            These tags are the root elements of a layout template.
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element of a template. Generates the
                <code>&lt;html&gt;</code> root element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="page-layout">
        <html xmlns="http://www.w3.org/1999/xhtml">
            <xsl:attribute name="lang">
                <xsl:value-of select="$language"/>
            </xsl:attribute>
            <xsl:attribute name="id">
                <xsl:choose>
                    <xsl:when test="@application = 'admin' 
                                    or @application = 'content-center' 
                                    or @application = 'content-section' 
                                    or @application = 'theme' 
                                    or @application = 'shortcuts' 
                                    or @application = 'subsite' 
                                    or @application = 'terms' 
                                    or @application = 'atoz' 
                                    or @application = 'ds'
                                    or @class = 'cms-admin' 
                                    or @class = 'admin'">
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
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for generating a HTML fragment instead of a complete HTML document.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="fragment-layout">
        <div class="ccm-fragment">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
    
    
    <!-- 
        ========================================================
        Common helper templates/functions for all templates tags
    -->

    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper functions for generating the name of the colorset class.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:get-colorset" as="xs:string">
        <xsl:for-each select="$data-tree/nav:categoryMenu/nav:category/nav:category">
            <xsl:if test="@isSelected = 'true'">
                <xsl:text>colorset_</xsl:text>
                <xsl:value-of select="position()"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:function>
    
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper functions for retrieving the name of the content type of the current content item
            from the result tree XML.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:get-content-type-name" as="xs:string">
        <xsl:value-of select="$data-tree//cms:item/type/label"/>
    </xsl:function>
    
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper template for processing arrows/links for sorting items.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:move-buttons">
        <span class="move-button">
            <xsl:if test="@prevURL">
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="./@prevURL"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="'moveUp'"/>
                    </xsl:attribute>
                    <img>
                        <xsl:attribute name="src">
                            <xsl:value-of select="concat($context-prefix, 
                                                         '/assets/gray-triangle-up.gif')"/>
                        </xsl:attribute>
                        <xsl:attribute name="title">
                            <xsl:value-of select="'moveUp'"/>
                        </xsl:attribute>
                    </img>
                </a>
            </xsl:if>
        </span>
        <span class="move-button">
            <xsl:if test="@nextURL">
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="./@nextURL"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="'moveDown'"/>
                    </xsl:attribute>
                    <img>
                        <xsl:attribute name="src">
                            <xsl:value-of select="concat($context-prefix, 
                                                         '/assets/gray-triangle-down.gif')"/>
                        </xsl:attribute>
                        <xsl:attribute name="title">
                            <xsl:value-of select="'moveDown'"/>
                        </xsl:attribute>
                    </img>
                </a>
            </xsl:if>
        </span>
    </xsl:template>
    
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper template for processing additional attributes in the data tree XML. They copied
            literally from the XML the HTML.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:process-attributes">
        <xsl:for-each select="@*">
            <xsl:if test="(name() != 'href_no_javascript')
                       and (name() != 'hint')
                       and (name() != 'label')">
                <xsl:attribute name="{name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:if>
        </xsl:for-each>
        <xsl:if test="name() = 'bebop:formWidget' and (not(@id) and @name)">
            <xsl:attribute name="id">
                <xsl:value-of select="@name"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>

    <foundry:doc section="devel" type="function-template">
        <foundry:doc-param name="template-file"
                           mandantory="yes">
            The name of the template file to process.
        </foundry:doc-param>
        <foundry:doc-desc>
            This template is the entry point for the template parser.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:process-template">
        <xsl:param name="template-file" as="xs:string"/>
        <xsl:param name="internal" as="xs:boolean" select="false()"/>
        
        <xsl:choose>
            <xsl:when test="$internal = true()">
                <xsl:apply-templates select="document(foundry:gen-path(
                                                         concat('foundry/templates/', 
                                                                normalize-space($template-file))))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="document(foundry:gen-path(
                                                          concat('/templates/', 
                                                          normalize-space($template-file))))"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper template for setting the <code>id</code> and <code>class</code> attributes
            on a HTML element.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:set-id-and-class">
        <xsl:param name="id" select="''"/>
        <xsl:param name="current-layout-node" select="."/>
    
        <xsl:variable name="cond-class">
            <xsl:if test="$current-layout-node/@classIf">
                <!-- DE Funktioniert leider nicht in einer Zeile, daher die Hilfsvariable -->
                <xsl:variable name="key" 
                              select="substring-before($current-layout-node/@classIf, ',')"/>
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
                <xsl:value-of select="foundry:get-content-type-name()"/>
            </xsl:if>
        </xsl:variable>
    
        <xsl:variable name="color-class">
            <xsl:if test="$current-layout-node/@withColorset='true'">
                <xsl:value-of select="foundry:get-colorset()"/>
            </xsl:if>
        </xsl:variable>
    
        <xsl:if test="$id != ''">
            <xsl:attribute name="id">
                <xsl:value-of select="$id"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="$current-layout-node/@id">
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="$current-layout-node/@class 
                      or $cond-class != '' 
                      or $type-class != '' 
                      or $color-class != ''">
            <xsl:attribute name="class">
                <xsl:value-of select="normalize-space(concat($current-layout-node/@class, ' 
                                                             ', $cond-class, 
                                                             ' ', 
                                                             $type-class, 
                                                             ' ', 
                                                             $color-class))"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>

    

</xsl:stylesheet>