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
                version="1.0">
    
    <xsl:output method="html"
                indent="yes"/>
    
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="/foundry:documentation">
        <!--<xsl:text>&lt;!DOCTYPE HTML&gt;</xsl:text>-->
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

    <xsl:template match="/foundry:documentation/foundry:doc-chapter">
        <article id="{./@id}">
            <h1>
                <xsl:value-of select="./@title"/>
            </h1>
            <xsl:apply-templates/>
        </article>
    </xsl:template>
    
    <xsl:template match="/foundry:documentation//foundry:doc-static-text">
        <section id="{./@href}">
            <xsl:choose>
                <xsl:when test="document(concat('../static-texts/', ./@href, '.html'))">
                    <xsl:copy-of select="document(concat('../static-texts/', ./@href, '.html'))/html/body/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="message" select="concat('The static text file &quot;', ./@href, '.html&quot; was not found')"/>
                    <div class="missing-static-text">
                        <xsl:value-of select="$message"/>
                        <xsl:message>
                            <xsl:value-of select="$message"/>
                        </xsl:message>
                    </div>
                    
                </xsl:otherwise>
            </xsl:choose>
        </section>
    </xsl:template>
    
    <xsl:template match="/foundry:documentation//foundry:doc-generated">
        <section id="{./@href}">
            <h1>
                <xsl:value-of select="./@title"/>
            </h1>
            
            <xsl:apply-templates select="document(concat('../../foundry/', ./@href, '.xsl'))"/>
            
        </section>
    </xsl:template>
    
    <xsl:template match="xsl:import | xsl:include">
        <xsl:apply-templates select="document(./@href)"/>
    </xsl:template>
    
    <xsl:template match="foundry:doc[@section='user' and @type='template-tag']">
        <h2>
            <xsl:value-of select="./following::xsl:template/@match"/>
        </h2>
        <dl>
            <xsl:apply-templates/>
        </dl>
    </xsl:template>
    
    <xsl:template match="foundry:doc-attributes">
        <dt>
            Attributes
        </dt>
        <dd>
            <dl>
                <xsl:apply-templates select="foundry:doc-attribute"/>
            </dl>
        </dd>
    </xsl:template>
    
    <xsl:template match="foundry:doc-attributes/foundry:doc-attribute">
        <dt>
            <code>
                <xsl:value-of select="./@name"/>
            </code>
        </dt>
        <dd>
            <xsl:apply-templates/>
        </dd>
    </xsl:template>
    
    <xsl:template match="foundry:doc-desc">
        <dt>
            Description
        </dt>
        <dd>
            <!--<xsl:copy-of select="./*"/>-->
            <xsl:apply-templates/>
        </dd>
    </xsl:template>
    
    <xsl:template match="foundry:doc-desc//code | foundry:doc-attribute//code">
        <code>
            <xsl:value-of select="."/>
        </code>
    </xsl:template>
    
    <xsl:template match="foundry:doc-desc//p | foundry:doc-attribute//p">
        <p>
            <xsl:value-of select="."/>
            <xsl:apply-templates/>
        </p>
    </xsl:template>
    
    <xsl:template match="foundry:doc-desc//pre | foundry:doc-attribute//pre">
        <pre>
            <xsl:value-of select="."/>
        </pre>
    </xsl:template>
    
    <xsl:template match="foundry:doc-see-also">
        <dt>See also</dt>
        <dd>
            <ul>
                <xsl:apply-templates select="foundry:doc-link"/>
            </ul>
        </dd>
        
    </xsl:template>

    <xsl:template match="foundry:doc-see-also/foundry:doc-link">
        <li>
            <a href="{./@href}">
                <xsl:choose>
                    <xsl:when test="string-length(.) &gt; 0">
                        <xsl:value-of select="."/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="./@href"/>
                    </xsl:otherwise>
                </xsl:choose>
            </a>
        </li>
    </xsl:template>

</xsl:stylesheet>