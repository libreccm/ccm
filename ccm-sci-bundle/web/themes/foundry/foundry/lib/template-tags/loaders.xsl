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
    
    <foundry:doc section="user"
                 type="template-tag">
        <foundry:doc-desc>
            Invokes the foundry CSS loader. The CSS loader will parse the file 
            <code>conf/css-files.xml</code> to determine for which CSS an 
            <code>&lt;link&gt;</code> element should be added to the HTML output. For a full
            explanation please refer to the <a href="#user_css-files">CSS files section</a>.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="load-css-files">
        <xsl:variable name="application">
            <xsl:choose>
                <xsl:when test="$data-tree/@application">
                    <xsl:value-of select="$data-tree/@application"/>
                </xsl:when>
                <xsl:when test="$data-tree/@class">
                    <xsl:value-of select="$data-tree/@class"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'none'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="document(foundry:gen-path('conf/css-files.xml'))/css-files/application[@name = $application]">
                <xsl:for-each select="document(foundry:gen-path('conf/css-files.xml'))/css-files/application[@name = $application]/css-file">
                    <xsl:call-template name="foundry:load-css-file">
                        <xsl:with-param name="filename" select="."/>
                        <xsl:with-param name="media" select="./@media"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:for-each select="document(foundry:gen-path('conf/css-files.xml'))/css-files/default/css-file">
                    <xsl:call-template name="foundry:load-css-file">
                        <xsl:with-param name="filename" select="."/>
                        <xsl:with-param name="media" select="./@media"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>
        
        <!-- Include IE Hacks only for very old IEs (IE 6) -->
        <!-- jensp 2014-09-16 This is copied from Mandalay. Maybe remove and relay and use 
        conditional comments in the other CSS files instead? -->
        <xsl:if test="$msie_version >= '5' and $msie_version &lt; '7'">
            <xsl:choose>
                <xsl:when test="document(foundry:gen-path('/conf/css-files.xml'))/css-files/application[@name=$application]">
                    <xsl:for-each select="document(foundry:gen-path('conf/css-files.xml'))/css-files/application[@name=$application]/iehacks">
                        <xsl:call-template name="foundry:load-css-file">
                            <xsl:with-param name="filename" select="."/>
                            <xsl:with-param name="media" select="./@media"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="document(foundry:gen-path('/conf/css-files.xml'))/css-files/default/iehacks">
                        <xsl:call-template name="foundry:load-css-file">
                            <xsl:with-param name="filename" select="."/>
                            <xsl:with-param name="media" select="./@media"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        
    </xsl:template>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            A helper template for generating the 
            <code>&lt;link rel="stylesheet" href="..."/&gt; </code> elements for loading the CSS 
            files. 
        </foundry:doc-desc>
        <foundry:doc-param name="filename" mandatory="yes">
            The name of the CSS file to load
        </foundry:doc-param>
        <foundry:doc-param name="media" mandatory="no">
            The media for which the file should be loaded. If no set, the CSS file is used for all
            media types.
        </foundry:doc-param>
    </foundry:doc>
    <xsl:template name="foundry:load-css-file">
        <xsl:param name="filename"/>
        <xsl:param name="media" select="''"/>
        
        <xsl:choose>
            <xsl:when test="string-length($media) &gt; 0">
                <!--<link rel="stylesheet" 
                type="text/css" 
                href="{$theme-prefix}/styles/{$media}/{$filename}" 
                media="{$media}" />-->
                <link rel="stylesheet" 
                      type="text/css" 
                      href="{foundry:gen-path(concat('styles/', $media, '/', $filename))}" 
                      media="{$media}" />
            </xsl:when>
            <xsl:otherwise>
                <!--<link rel="stylesheet" 
                type="text/css" 
                href="{$theme-prefix}/styles/{$filename}" />-->
                <link rel="stylesheet" 
                      type="text/css" 
                      href="{foundry:gen-path(concat('styles/', $filename))}" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="load-fancybox">
        <!-- Add mousewheel plugin (this is optional) -->
        <script type="text/javascript" 
                src="{$context-prefix}/assets/fancybox2/lib/jquery.mousewheel-3.0.6.pack.js"/>
        
        <!-- Add fancyBox main JS and CSS files -->
        <script type="text/javascript" 
                src="{$context-prefix}/assets/fancybox2/source/jquery.fancybox.js"/>
        <link rel="stylesheet" 
              href="{$context-prefix}/assets/fancybox2/source/jquery.fancybox.css" 
              type="text/css" 
              media="screen"/>

        <!-- Add Button helper (this is optional) -->
        <link rel="stylesheet" 
              type="text/css" 
              href="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-buttons.css" />
        <script type="text/javascript"
                src="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-buttons.js"/>

        <!-- Add Thumbnail helper (this is optional) -->
        <link rel="stylesheet" 
              type="text/css" 
              href="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-thumbs.css" />
        <script type="text/javascript" 
                src="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-thumbs.js"/>

        <!-- Add Media helper (this is optional) -->
        <script type="text/javascript"
                src="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-media.js"/>
        
        <!-- Apply fancybox -->
        <script type="text/javascript"
                src="{$theme-prefix}/scripts/apply-fancybox.js"/>
        
    </xsl:template>
    
    <xsl:template match="load-jquery">
        <script type="text/javascript" src="{$context-prefix}/assets/jquery.js"/>
    </xsl:template>
    
    <xsl:template match="load-jquery-ui">
        <script type="text/javascript" src="{$context-prefix}/assets/jquery-ui.min.js"/>
    </xsl:template>
    
    <xsl:template match="load-mathjax">
        <script type="text/javascript" 
                src="{$context-prefix}/assets/mathjax/MathJax.js?config=TeX-MML-AM_HTMLorMML"/>
    </xsl:template>
    
    <xsl:template match="load-html5shiv">
        <xsl:value-of select="concat('
        &lt;!--
        &lt;!-[if lt IE 9]&gt;
        &lt;script src=&quot;', $context-prefix, '/assets/html5shiv.js&quot;/&gt;
        &lt;![endif]
        --&gt;')"/>
    </xsl:template>
    
</xsl:stylesheet>