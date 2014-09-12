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
    
    <xsl:template match="load-css-files">
        <xsl:call-template name="foundry:load-css-files"/>
    </xsl:template>
    
    <xsl:template match="load-fancybox">
        <xsl:call-template name="foundry:load-fancy-box"/>
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
        <xsl:value-of disable-output-escaping="yes" select="concat('
        &lt;!--
        &lt;!-[if lt IE 9]&gt;
        &lt;script src=&quot;', $context-prefix, '/assets/html5shiv.js&quot;/&gt;
        &lt;![endif]
        --&gt;')"/>
    </xsl:template>
    
</xsl:stylesheet>