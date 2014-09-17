<?xml version="1.0"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

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
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                version="1.0">

    <xsl:import href="lib.xsl"/>
    <xsl:import href="../doc/xsl/foundry-documentation.xsl"/>
    
    <xsl:output method="html"
                indent="yes"
                encoding="utf-8"/>
    
    <xsl:template match="bebop:page">
        
        <xsl:variable name="application">
            <xsl:choose>
                <xsl:when test="./@application">
                    <xsl:value-of select="./@application"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'none'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="class" select="@class" />
        
        <xsl:choose>
            <xsl:when test="document(concat($theme-prefix, '/settings/templates.xml'))/applications/application[@name=$application and @class=$class]">
                <xsl:call-template name="foundry:parse-template">
                    <xsl:with-param name="template-file"
                                    select="document(concat($theme-prefix, '/settings/templates.xml'))/applications/application[@name=$application and @class=$class]"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="foundry:process-template">
                    <xsl:with-param name="template-file"
                                    select="document(concat($theme-prefix, '/settings/templates.xml'))/applications/default"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        
        <!--<div>
            <h1>Hello from Foundry</h1>
        </div>-->
        
    </xsl:template>

    <fondry:doc section="devel">
        <foundry:doc-desc>
            The entry point for creating Foundry documentation.
        </foundry:doc-desc>
    </fondry:doc>
    <xsl:template match="/foundry:documentation">
        <xsl:value-of select="'&lt;!DOCTYPE HTML&gt;'"
                      disable-output-escaping="yes" />
        <html>
            <head>
                <meta http-equiv="content-type" 
                      content="text/html; charset=UTF-8"/>
                <title>Foundry Theming Engine for LibreCCM - Documentation</title>
                <style type="text/css">
                    .missing-static-text {
                    border: 2px solid red;
                    color: red;
                    font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <header>
                    Header
                </header>
                <nav>
                    Navigation
                </nav>
                <main>
                    <xsl:apply-templates/>
                </main>
                <footer>
                    Footer
                </footer>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>