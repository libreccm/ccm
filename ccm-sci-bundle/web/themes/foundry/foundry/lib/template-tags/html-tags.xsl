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
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl bebop foundry ui"
                version="2.0">

    <foundry:doc-file>
        <foundry:doc-file-title>HTML tags</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                These tags are generating the equivalent HTML tags. In most cases the tags have
                the same name and same attributes as their HTML counterparts, but some work
                in a slightly different way, for example by using values provided by other 
                surrounding tags which are passed the them as XSL parameters.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a HTML <code>a</code> element. There are some differences to the 
                <code>a</code> element in HTML. First, there two attribute for the URL:
            </p>
            <dl>
                <dt>
                    <code>href-property</code>
                </dt>
                <dd>
                    The name of a property of the current object which contains the URL for the 
                    link.
                </dd>
                <dt>
                    <dt>
                        <code>href-static</code>
                    </dt>
                    <dd>
                        A static URL. 
                    </dd>
                </dt>
            </dl>
            <p>
                The third variant for providing an URL is to call the template with a href 
                parameter in the XSL.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="download">
                <p>
                    Value for the HTML5 <code>download</code> attribute.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="href-property">
                <p>
                    The name of a property (aka the name of an XML element in the data-tree) 
                    containing the URL of the link.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="href">
                <p>
                    A static URL for the link.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="href-lang">
                <p>
                    The language of the target of the link.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="rel">
                <p>
                    The relationship of the linking document with the target document.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="title-static">
                <p>
                    A key which identifies the translated title in <code>lang/global.xml</code>.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="title">
                <p>
                    Static, not translated title of the link.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="type">
                <p>
                    Value for the <code>title</code> attribute of the link.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-a-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="a">
        <xsl:param name="href" select="''" tunnel="yes"/>
        <xsl:param name="title" select="''" tunnel="yes"/>
        
        <a>
            <xsl:if test="./@download">
                <xsl:attribute name="download">
                    <xsl:value-of select="./@download"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@href-property">
                <xsl:attribute name="href">
                    <xsl:value-of select="$data-tree/*[name = ./@href-property]"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@href-static">
                <xsl:attribute name="href">
                    <xsl:value-of select="./@href-static"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$href != ''">
                <xsl:attribute name="href">
                    <xsl:value-of select="$href"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@href-lang">
                <xsl:attribute name="hreflang">
                    <xsl:value-of select="./@href-lang"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@rel">
                <xsl:attribute name="rel">
                    <xsl:value-of select="./@rel"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./title-static">
                <xsl:attribute name="title">
                    <xsl:value-of select="foundry:get-static-text('', ./@static-title)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$title != ''">
                <xsl:attribute name="title">
                    <xsl:value-of select="$title"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@type">
                <xsl:attribute name="type">
                    <xsl:value-of select="./@type"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </a>
    </xsl:template>
    
    <foundry:doc>
        <foundry:doc-desc>
            <p>
                Generates the HTML5 <code>article</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-article-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="article">
        <xsl:param name="article-id" select="''"/>
        
        <article>
            <xsl:call-template name="foundry:set-id-and-class">
                <xsl:with-param name="id" select="$article-id"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </article>
    </xsl:template>
    
    <foundry:doc  section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML5 <code>aside</code> element. 
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-aside-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="aside">
        <aside>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </aside>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates the HTML <code>body</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-body-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="body">
        <body>
            <xsl:call-template name="foundry:set-id-and-class"/>

            <xsl:if test="foundry:debug-enabled()">
                <div id="foundry-debug-panel">
                    <div id="foundry-debug-panel-content">
                        <h1>Foundry Debug Panel</h1>
                        <div>
                            <h2>Foundry system information</h2>
                            <dl>
                                <dt>Version</dt>
                                <dd>
                                    <xsl:value-of select="$foundry-version"/>
                                </dd>
                            </dl>
                        </div>
                        <div>
                            <h2>Server related environment variables</h2>
                            <dl>
                                <dt>theme-prefix</dt>
                                <dd>
                                    <xsl:value-of select="$theme-prefix"/>
                                </dd>
                                <dt>context-prefix</dt>
                                <dd>
                                    <xsl:value-of select="$context-prefix"/>
                                </dd>
                                <dt>dispatcher-prefix</dt>
                                <dd>
                                    <xsl:value-of select="$dispatcher-prefix"/>
                                </dd>
                            </dl>
                        </div>
                    </div>
                </div>
            </xsl:if>

            <span id="top"/>
            <a href="#startcontent" accesskey="S" class="nav-hide">
                <xsl:attribute name="title"> 
                    <xsl:value-of select="foundry:get-static-text('', 'layout/page/skipnav/title')"/>
                </xsl:attribute>
                <xsl:value-of select="foundry:get-static-text('', 'layout/page/skipnav/link')"/>
            </a>

            <xsl:apply-templates/>
        </body>
    </xsl:template>
    
    <foundry:doc section="user" 
                 type="template-tag">
        <foundry:doc-desc>
            Generates a HTML <code>div</code> element. 
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-div-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="div">
        <div>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML <code>div</code> element, but only if the content is not empty. 
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#div"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="divIfNotEmpty">
        <xsl:variable name="divContent">
            <xsl:apply-templates/>
        </xsl:variable>

        <xsl:if test="normalize-space($divContent)">
            <div>
                <xsl:call-template name="foundry:set-id-and-class"/>
                <xsl:apply-templates/>
            </div>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a definition list.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-dl-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="dl">
        <dl>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </dl>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                A term in a definition list.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-dt-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="dt">
        <dt>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </dt>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                A definition of term in a definition list.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-dd-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="dd">
        <dd>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </dd>
    </xsl:template>
    
    <xsl:template name="figcaption">
        <figcaption>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </figcaption>
    </xsl:template>
    
    <xsl:template name="figure">
        <figure>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </figure>
    </xsl:template>
    
    <xsl:template match="h1">        
        <h1>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </h1>
    </xsl:template>
    
    <xsl:template match="h2">
        <h2>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </h2>
    </xsl:template>
    
    <xsl:template match="h3">
        <h3>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </h3>
    </xsl:template>
    
    <xsl:template match="h4">
        <h4>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </h4>
    </xsl:template>
    
    <xsl:template match="h5">
        <h5>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </h5>
    </xsl:template>
    
    <xsl:template match="h6">
        <h6>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </h6>
    </xsl:template>
    
    <foundry:doc section="user"  type="template-tag">
        <foundry:doc-desc>
            Creates a HTML5 footer element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-footer-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="footer">
        <footer>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </footer>
    </xsl:template>
    
    <foundry:doc section="user"  type="template-tag">
        <foundry:doc-desc>
            Creates the HTML <code>head</code> element which may contain meta data and stylesheets
            etc. It also generates some meta data like the generator meta information or the 
            language meta information.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/document-metadata.html#the-head-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="head">
        <head>
            <meta name="generator">
                <xsl:attribute name="content">
                    <xsl:value-of select="concat($data-tree/bebop:systemInformation/@appname, ' ', $data-tree/bebop:systemInformation/@version)"/>
                </xsl:attribute>
            </meta>
            
            <!-- These meta informations are needed to get Level 3 WAI -->
            <!--<meta http-equiv="content-language" content="{$language}"/>-->
            <!-- ToDo
            <meta name="keywords">
                <xsl:attribute name="content">
                    <xsl:call-template name="foundry:keywords"/>
                </xsl:attribute>
            </meta>
            <meta name="description">
                <xsl:attribute name="content">
                    <xsl:call-template name="foundry:description"/>
                </xsl:attribute>
            </meta>-->
      
            <xsl:apply-templates/>
            
            <!-- Load the CSS files for Foundry's debug mode if debug mode is active -->
            <xsl:if test="foundry:debug-enabled()">
                <link rel="stylesheet" type="text/css" href="{foundry:gen-path('foundry/styles/debug-mode.css')}"/>
            </xsl:if>
            
            <!-- Not implemented yet <xsl:call-template name="bebop:double-click-protection"/> -->
      
            <xsl:apply-templates select="$data-tree//script"/>
            <!-- 
                Set favicon if exists. This three different variants for including the favicon
                are necessary to satisfy all browsers.
            -->
            <link href="{$theme-prefix}/images/favicon.png" 
                  type="image/png" 
                  rel="shortcut icon"/>
        </head>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML5 <code>header</code> element. 
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-header-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="header">
        <header>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </header>
    </xsl:template>

    <foundry:doc>
        <foundry:doc-desc>
            <p>
                The <code>img</code> tag produces an HTML <code>img</code> in the HTML output.
                The source URL of the image can either be provided using the attributes 
                <code>src-static</code> or <code>src-property</code> or by a surrounding tag
                like <code>image-attachment</code>. If the image URL is provided by an surrouding 
                tag these tag usally provides values with the orginal width and height of the image.
            </p>
            <p>
                Depending of the format for URL, the URL is modified. There are three possible 
                cases:
            </p>
            <ol>
                <li>
                    The URL starts with <code>http://</code> or <code>https://</code>. 
                    In this case the URL is used literally.
                </li>
                <li>
                    The URL starts with a slash (<code>/</code>). In this case the URL points to
                    an image provided by CCM (from the database). In this case, the 
                    <code>dispatcher-prefix</code> is appended before the URL. Also width and 
                    height parameters are appended to the URL for CCM server side resizing 
                    function.
                </li>
                <li>
                    Otherwise is is assumed that the image is provided by the theme. In this
                    cases the <code>theme-prefix</code> is added before the image URL. 
                </li>
            </ol>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="src-static">
                <p>
                    An URL to an static image resource. 
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="src-property">
                <p>
                    Name of an XML in the <code>data-tree</code> providing the URL of the image.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="width">
                <p>
                    The (maximum) width of the image.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="height">
                <p>
                    The (maximum) height of the image.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="img">
        <!-- Source URL of the image provided by a surrounding tag. -->
        <xsl:param name="src" tunnel="yes" as="xs:string" select="''"/>
        <!-- Width of the image the URL the src parameter is pointing to (pixel) -->
        <xsl:param name="img-width" tunnel="yes" as="xs:integer" select="(-1)"/>
        <!-- Height of the image the URL the src parameter is pointing to (pixel) -->
        <xsl:param name="img-height" tunnel="yes" as="xs:integer" select="(-1)"/>
        <!-- Content of the alt attribute if provided by surrounding tag -->
        <xsl:param name="alt" tunnel="yes" as="xs:string" select="''"/>
        
        <xsl:variable name="src-raw">
            <xsl:choose>
                <xsl:when test="$src != ''">
                    <xsl:value-of select="$src"/>
                </xsl:when>
                <xsl:when test="./@src-static">
                    <xsl:value-of select="./@src"/>
                </xsl:when>
                <xsl:when test="./@src-property">
                    <xsl:value-of select="$data-tree/*[name = ./@src-property]"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <!-- 
            Value of width attribute of the img element in the layout template if set. 
            If there is no width attribute the value is set to -1.
        -->
        <xsl:variable name="max-width" 
                      as="xs:integer" 
                      select="foundry:get-attribute-value(current(), 'width', -1)"/>
        
        <!--
            Value of the height attriibute of the img element in the layout template if set.
            If there is no attribute the value is set to -1.
        -->
        <xsl:variable name="max-height" 
                      as="xs:integer"
                      select="foundry:get-attribute-value(current(), 'height', -1)"/>
        
        <xsl:variable name="width">
            <xsl:choose>
                <xsl:when test="$max-width &gt; 0 
                                and $max-height &gt; 0 
                                and $img-width &gt; $max-width 
                                and $img-height &gt; $max-height ">
                    <xsl:choose>
                        <xsl:when test="$max-width div $img-width &gt; $max-height div $img-height">
                            <xsl:value-of select="round($max-height div $img-height * $img-width)"/>
                        </xsl:when>
                        <xsl:when test="$max-height div $img-height &gt;= $max-width div $img-width">
                            <xsl:value-of select="round($max-width)"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="$max-width &gt; 0 and $img-width &gt; $max-width">
                    <xsl:value-of select="$max-width"/>
                </xsl:when>
                <xsl:when test="$max-height &gt; 0 and $img-height &gt; $max-height" >
                    <xsl:value-of select="round($max-height div $img-height * $img-width)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$img-width"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="height">
            <xsl:choose>
                <xsl:when test="$max-width &gt; 0 
                                and $max-height &gt; 0 
                                and $img-width &gt; $max-width 
                                and $img-height &gt; $max-height">
                    <xsl:choose>
                        <xsl:when test="$max-height div $img-height &gt; $max-width div $img-width">
                            <xsl:value-of select="round($max-width div width * height)"/>
                        </xsl:when>
                        <xsl:when test="$max-width div $img-width &gt;= $max-height div $img-height">
                            <xsl:value-of select="$max-height"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="$max-height &gt; 0 and $img-height &gt; $max-height">
                    <xsl:value-of select="$max-height"/>
                </xsl:when>
                <xsl:when test="$max-width &gt; 0 and $img-width &gt; $max-width">
                    <xsl:value-of select="round($max-width div $img-width * $img-height)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$img-height"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <!-- 
            Generate src URL of the image. If the path is not an external path (a path starting
            with http:// or https://) add the theme-prefix or the dispatcher-prefix. The 
            dispatcher-prefix is added if the path does start with a slash (/). This indicates 
            that the image is served from the CCM database. If the path does *not* start with a
            slash the image is part of the theme and the theme-prefix is added.
        -->
        <xsl:variable name="img-src">
            <xsl:choose>
                <xsl:when test="substring($src-raw, 1, 7) = 'http://' 
                                or substring($src-raw, 1, 8) = 'https://'" >
                    <xsl:value-of select="$src-raw"/>
                </xsl:when>
                <xsl:when test="substring($src-raw, 1, 1) = '/'">
                    <xsl:variable name="img-dimensions" 
                                  select="concat('width=', $width, '&amp;height=', $height)"/>
                    
                    <xsl:choose>
                        <xsl:when test="contains($src-raw, '?')">
                            <xsl:value-of select="concat($dispatcher-prefix,
                                                  $src-raw,
                                                  '&amp;',
                                                  $img-dimensions)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat($dispatcher-prefix, 
                                                         $src-raw, 
                                                         '?', 
                                                         $img-dimensions)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="foundry:gen-path($src-raw)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <img>
            
            <xsl:attribute name="src" select="$img-src"/>
            
            <xsl:if test="$alt != ''">
                <xsl:attribute name="alt">
                    <xsl:value-of select="$alt"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@alt">
                <xsl:attribute name="alt">
                    <xsl:value-of select="foundry:get-static-text('', ./@alt, false())"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@title">
                <xsl:attribute name="title">
                    <xsl:value-of select="foundry:get-static-text('', ./@title, false())"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$width &gt; 0">
                <xsl:attribute name="width">
                    <xsl:value-of select="$width"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$height &gt; 0">
                <xsl:attribute name="height">
                    <xsl:value-of select="$height"/>
                </xsl:attribute>
            </xsl:if>
        </img>
    </xsl:template>

    <xsl:template match="li">
        <li>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </li>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML5 <code>main</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-main-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="main">
        <main>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </main>
    </xsl:template>
    

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a meta data field in in the <code>head</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#head"/>
            <foundry:doc-link href="http://www.w3.org/TR/html5/document-metadata.html#the-meta-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="meta">
        <meta>
            <xsl:if test="@name">
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@http-equiv">
                <xsl:attribute name="http-equiv">
                    <xsl:value-of select="@http-equiv"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@content">
                <xsl:attribute name="content">
                    <xsl:value-of select="@content"/>
                </xsl:attribute>
            </xsl:if>
        </meta>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML5 <code>nav</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-nav-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="nav">
        <nav>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </nav>
    </xsl:template>
    
    <xsl:template match="ol">
        <ul>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    
    <xsl:template match="p">
        <p>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </p>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-attributes>
            <foundry:doc-attribute name="absolute">
                <p>
                    If set to <code>true</code> the path in the <code>src</code> is used as it is.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="src">
                <p>
                    The path of the script to include. If the <code>absolute></code> attribute is not
                    set (or not set to <code>true</code> the path is interpreted relative to the 
                    theme directory. For example the path of a script included using 
                </p>
                <pre>
                &lt;script type="text/javascript" src="scripts/example.js"/>
                </pre>
                <p>
                    in the a theme named <code>my-theme</code> at the server 
                    <code>http://www.example.org</code> is altered to the absolute path 
                    <code>http://www.example.org/themes/published-themedir/itb/scripts/example.js</code>.
                    If the <code>absolute</code> attribute is set to <code>true</code> the path is not 
                    altered. One usecase for an absolute path is to load an script from a content delivery
                    network.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="type">
                <p>
                    The type of the script. Usally this is <code>text/javascript</code>. If the attribute
                    is not set in the layout template, it is automatically set to 
                    <code>text/javascript</code>.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-desc>
            <p>
                Used to include a script (usally a JavaScript). The script is either provided 
                a content of the element or as an external file. Embedded scripts should only be used
                for small parts of code, like the code for activating jQuery plugins for some elements.
                Everything which is longer than five or six lines should be put into a external file 
                in the scripts directory of the theme.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/scripting-1.html#the-script-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="script">
        <script>
            <xsl:attribute name="type">
                <xsl:choose>
                    <xsl:when test="./@type">
                        <xsl:value-of select="./@type"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl-value-of select="'text/javascript'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:if test="./@src">
                <xsl:attribute name="src">
                    <xsl:choose>
                        <xsl:when test="./@absolute = 'true'">
                            <xsl:value-of select="./@src"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="foundry:gen-path(./@src)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                   
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(.)">
                <xsl:value-of select="."/>
            </xsl:if>
        </script>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML5 <code>section</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-span-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="section">
        <xsl:param name="section-id" select="''"/>
        
        <section>
            <xsl:call-template name="foundry:set-id-and-class">
                <xsl:with-param name="id" select="$section-id"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a <code>span</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-span-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="span">
        <span>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="table">
        <table>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    
    <xsl:template match="tbody">
        <tbody>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </tbody>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates the title in the HTML head. The other elements are allowed in the 
                <code>&lt;title&gt;</code> tag: 
            </p>
            <ul>
                <li>
                    <code>show-text</code>
                </li>
                <li>
                    <code>show-page-title</code>
                </li>
            </ul>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/document-metadata.html#the-title-element"/>
            <foundry:doc-link href="#show-text"/>
            <foundry:doc-link href="#show-page-title"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="title">
        <title>
            <xsl:choose>
                <xsl:when test="./show-text | ./show-page-title">
                    <xsl:for-each select="show-text | show-page-title">
                        <xsl:apply-templates select="."/>
                        <xsl:if test="position() != last()">
                            <xsl:value-of select="foundry:get-setting('layout-parser', 
                                                              'title/separator', 
                                                              ' - ',
                                                              ../separator)"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="current()"/>
                </xsl:otherwise>
            </xsl:choose>
        </title>
    </xsl:template>
    
    <xsl:template match="td">
        <td>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </td>
    </xsl:template>
    
    <xsl:template match="th">
        <th>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </th>
    </xsl:template>
    
    <xsl:template match="thead">
        <thead>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </thead>
    </xsl:template>
    
    <xsl:template match="tr">
        <td>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </td>
    </xsl:template>
    
    <xsl:template match="ul">
        <ul>
            <xsl:call-template name="foundry:set-id-and-class"/>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>

 


    
    
    
    


</xsl:stylesheet>