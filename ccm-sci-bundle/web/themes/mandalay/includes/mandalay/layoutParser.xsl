<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                 xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
                 xmlns:ui="http://www.arsdigita.com/ui/1.0"
                 xmlns:cms="http://www.arsdigita.com/cms/1.0"
                 xmlns:docs="http://www.redhat.com/docs/1.0"
                 xmlns:nav="http://ccm.redhat.com/navigation"
                 xmlns:search="http://rhea.redhat.com/search/1.0"
                 xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
                 xmlns:forum="http://www.arsdigita.com/forum/1.0"
                 xmlns:subsite="http://ccm.redhat.com/subsite/1.0"
                 xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
                 xmlns:ppp="http://www.arsdigita.com/PublicPersonalProfile/1.0"
                 xmlns:mandalay="http://mandalay.quasiweb.de"
                 xmlns:atoz="http://xmlns.redhat.com/atoz/1.0"
                 exclude-result-prefixes="xsl aplaws atoz bebop cms docs forum mandalay nav portal ppp search subsite terms ui"
                 version="1.0">

    <!-- Autor: Sören Bernstein -->
  
    <!-- DE Layout-Parser: hier beginnt die Erzeugung des XHTML-Codes -->
    <!-- EN Layout parser: beginning xhtml code production -->
    <xsl:template name="mandalay:layoutParser">
        <xsl:param name="layoutFile"/>
        <xsl:apply-templates select="document(concat($theme-prefix, '/layout/', $layoutFile))"/>
    </xsl:template>

    <xsl:template match="pageLayout">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
        <html xmlns="http://www.w3.org/1999/xhtml">
            <xsl:attribute name="lang">
                <xsl:value-of select="$lang"/>
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

    <!-- DE Hilfsfunktion zum Verabreiten von zusätzlichen Angaben in verschiedenen blockerzeugenden Tags -->
    <!-- DE Wichtig für die CSS-Auszeichung mit id und class und für die Colorset-Funktion -->
    <!-- EN Helper function for block creating tags -->
    <!-- EN needed for css-attributes id and class and for the colorset feature -->
    <xsl:template name="mandalay:setIdAndClass">
        <xsl:param name="currentLayoutNode" select="."/>
    
        <xsl:variable name="condClass">
            <xsl:if test="$currentLayoutNode/@classIf">
                <!-- DE Funktioniert leider nicht in einer Zeile, daher die Hilfsvariable -->
                <xsl:variable name="key" select="substring-before($currentLayoutNode/@classIf, ',')"/>
                <xsl:variable name="condition">
                    <xsl:apply-templates select="//*[@id=$key]"/>
                </xsl:variable>
        
                <xsl:if test="normalize-space($condition)">
                    <xsl:value-of select="substring-after($currentLayoutNode/@classIf, ', ')"/>
                </xsl:if>
            </xsl:if>
        </xsl:variable>
    
        <xsl:variable name="typeClass">
            <xsl:if test="$currentLayoutNode/@setTypeClass='true'">
                <xsl:call-template name="mandalay:getContentTypeName"/>
            </xsl:if>
        </xsl:variable>
    
        <xsl:variable name="colorClass">
            <xsl:if test="$currentLayoutNode/@withColorset='true'">
                <xsl:call-template name="mandalay:getColorset"/>
            </xsl:if>
        </xsl:variable>
    
        <xsl:if test="$currentLayoutNode/@id">
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="$currentLayoutNode/@class or $condClass != '' or $typeClass != '' or $colorClass != ''">
            <xsl:attribute name="class">
                <xsl:value-of select="normalize-space(concat($currentLayoutNode/@class, ' ', $condClass, ' ', $typeClass, ' ', $colorClass))"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>


    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                      Head-Section                         *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->

    <xsl:template match="head">
        <head>
            <meta name="XSL-Theme-Name" content="Mandalay"/>
            <meta name="XSL-Theme-Version" content="{$version}"/>
      
            <!-- Metainformation about LibreCCM -->
            <meta name="generator">
                <xsl:attribute name="content">
                    <xsl:value-of select="concat($resultTree/bebop:systemInformation/@appname, ' ', $resultTree/bebop:systemInformation/@version)"/>
                </xsl:attribute>
            </meta>
            
            <!-- DE Diese Metainformationen sind für den WIA Level 3 notwendig -->
            <!-- EN These meta informations are needed to get Level 3 WAI -->
            <meta name="language" content="{$lang}"/>
            <meta name="keywords">
                <xsl:attribute name="content">
                    <xsl:call-template name="mandalay:keywords"/>
                </xsl:attribute>
            </meta>
            <meta name="description">
                <xsl:attribute name="content">
                    <xsl:call-template name="mandalay:description"/>
                </xsl:attribute>
            </meta>
      
            <xsl:apply-templates/>
      
            <!-- DE Lade den Doppelklick-Schutz -->
            <!-- EN Load double click protection -->
            <xsl:call-template name="bebop:doubleClickProtection"/>
      
            <xsl:apply-templates select="$resultTree//script"/>

            <!-- DE Setze Favicon, falls vorhanden -->
            <!-- EN Set favicon if exists -->
            <!--      <xsl:if test=""> -->
            <link href="{$theme-prefix}/images/favicon.png" type="image/x-icon" rel="shortcut icon"/>
            <!--      </xsl:if> -->
        </head>
    </xsl:template>

    <!-- DE Verarbeite die Metadaten in der Layout-Datei-->
    <!-- EN Processing meta data from layout file -->
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

    <!-- DE cssLoader aufrufen -->
    <!-- EN calling cssLoader -->
    <xsl:template match="useCSSLoader">
        <xsl:call-template name="mandalay:cssLoader"/>
    </xsl:template>

    <xsl:template match="useFancybox">
        <xsl:call-template name="mandalay:fancybox"/>
    </xsl:template>

    <xsl:template match="useJQuery">
        <script type="text/javascript" src="/assets/jquery.js"/>
    </xsl:template>

    <xsl:template match="useJQueryUI">
        <script type="text/javascript" src="/assets/jquery-ui.min.js"/>
    </xsl:template>

    <xsl:template match="useMathJax">
        <script type="text/javascript" src="/assets/mathjax/MathJax.js?config=TeX-MML-AM_HTMLorMML"/>
    </xsl:template>
    
    <xsl:template match="useHTML5shiv">
        <xsl:text disable-output-escaping="yes">
&lt;!--[if lt IE 9]&gt;
&lt;script src="/assets/html5shiv.js"/&gt;
&lt;![endif]--&gt;
        </xsl:text>
    </xsl:template>

    <!-- DE Setze den lokalisierten Seitentitel -->
    <!-- EN Setup localized page title -->
    <xsl:template match="title">
        <title>
            <xsl:for-each select="showText | usePageTitle">
                <xsl:apply-templates select="."/>
                <xsl:if test="position()!=last()">
                    <xsl:call-template name="mandalay:getSetting">
                        <xsl:with-param name="node" select="../separator"/>
                        <xsl:with-param name="module" select="'layoutParser'"/>
                        <xsl:with-param name="setting" select="'title/separator'"/>
                        <xsl:with-param name="default" select="' - '"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </title>
    </xsl:template>


    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                      Body-Section                         *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->

    <xsl:template match="body">
        <body>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <span id="top"/>
            <a href="#startcontent" accesskey="S" class="navHide">
                <xsl:attribute name="title">
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="id" select="'layout/page/skipnav/title'"/>
                    </xsl:call-template>
                </xsl:attribute>
                <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="id" select="'layout/page/skipnav/link'"/>
                </xsl:call-template>
            </a>
            <xsl:apply-templates/>
        </body>
    </xsl:template>

    <xsl:template match="span">
        <span>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="div">
        <div>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="divIfNotEmpty">
        <xsl:variable name="divContent">
            <xsl:apply-templates/>
        </xsl:variable>

        <xsl:if test="normalize-space($divContent)">
            <div>
                <xsl:call-template name="mandalay:setIdAndClass"/>
                <xsl:apply-templates/>
            </div>
        </xsl:if>
   
    </xsl:template>
    
    <!-- HTML5 elements -->
    
    <xsl:template match="header">
        <header>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:apply-templates/>
        </header>
    </xsl:template>
    
    <xsl:template match="footer">
        <footer>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:apply-templates/>
        </footer>
    </xsl:template>
    
    <xsl:template match="main">
        <main>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:apply-templates/>
        </main>
    </xsl:template>
    
    <xsl:template match="nav">
        <nav>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:apply-templates/>
        </nav>
    </xsl:template>
    
    <xsl:template match="aside">
        <aside>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:apply-templates/>
        </aside>
    </xsl:template>
    
    <!-- HTML5 elements end  -->
  
  
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                    Modules-Section                        *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->

    <!-- DE Rufe die einzelnen Container auf -->
    <!-- EN Calling all page content containers -->
    <xsl:template match="showBreadcrumbs">
        <xsl:call-template name="mandalay:breadcrumbs"/>
    </xsl:template>

    <xsl:template match="showQuicksearch">
        <xsl:call-template name="mandalay:quicksearch"/>
    </xsl:template>

    <xsl:template match="showNavigationMenu">
        <xsl:call-template name="mandalay:navigation"/>
    </xsl:template>

    <xsl:template match="showNotes">
        <xsl:call-template name="mandalay:notes"/>
    </xsl:template>

    <xsl:template match="showRelatedLinks">
        <xsl:call-template name="mandalay:relatedLinks"/>
    </xsl:template>

    <xsl:template match="showFileAttachments">
        <xsl:call-template name="mandalay:fileAttachments"/>
    </xsl:template>

    <xsl:template match="showRelatedItems">
        <xsl:call-template name="mandalay:relatedItems"/>
    </xsl:template>

    <xsl:template match="showUserBanner">
        <xsl:call-template name="mandalay:userBanner"/>
    </xsl:template>

    <xsl:template match="showForm">
        <xsl:apply-templates select="$resultTree//bebop:form"/>
    </xsl:template>

    <xsl:template match="showLanguageSelector">
        <span class="languageSelector">
            <xsl:call-template name="mandalay:languageSelector"/>
        </span>
    </xsl:template>

    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                     Content-Section                       *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->

    <xsl:template match="showContent">
        <span id="startcontent"/>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="useLastModified">
        <!--<xsl:call-template name="mandalay:lastModified"/>-->
        <xsl:choose>
            <!-- Detail view of a content item -->
            <xsl:when test="$resultTree/cms:contentPanel/cms:item/masterVersion/auditing">
                <xsl:apply-templates select="$resultTree/cms:contentPanel/cms:item/masterVersion/auditing" 
                                     mode="auditing"/>
            </xsl:when>
            <!-- Greeting Item -->
            <xsl:when test="$resultTree/nav:greetingItem/cms:item/masterVersion/auditing">
                <xsl:apply-templates select="$resultTree/nav:greetingItem/cms:item/masterVersion/auditing" 
                                     mode="auditing"/>
                        
            </xsl:when>
            <xsl:when test="$resultTree/cms:item/masterVersion/auditing">
                <xsl:apply-templates select="$resultTree/cms:item/masterVersion/auditing" 
                                     mode="auditing"/>
            </xsl:when>
            <!-- Fallback -->
            <xsl:otherwise>
                <!-- Don't use the fallback when an object list is present -->
                <xsl:if test="not($resultTree//nav:objectList)">
                    <xsl:apply-templates select="$resultTree//auditing" 
                                         mode="auditing"/>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="usePageTitle">
        <xsl:choose>
            <xsl:when test="name(..) = 'title'">
                <xsl:call-template name="mandalay:title"/>
            </xsl:when>
            <xsl:otherwise>
                <h1>
                    <xsl:call-template name="mandalay:title"/>
                </h1>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="useContent">
        <xsl:choose>
            <xsl:when test="$resultTree/@class = 'cms-admin'">
                <xsl:apply-templates select="$resultTree//bebop:currentPane"/>
            </xsl:when>
            <xsl:when test="$resultTree/@class = 'admin'">
                <xsl:apply-templates select="$resultTree//nav:categoryPanel"/>
            </xsl:when>
            <xsl:when test="$resultTree/@application = 'admin'">
                <xsl:apply-templates select="$resultTree//bebop:currentPane"/>
            </xsl:when>
            <xsl:when test="$resultTree/@application = 'portal'">
                <xsl:apply-templates select="$resultTree//portal:homepageWorkspace | $resultTree//portal:workspace | $resultTree//portal:admin | $resultTree//portal:sitemap"/>
            </xsl:when>
            <xsl:when test="$resultTree/@application = 'forum'">
                <xsl:apply-templates select="$resultTree/forum:forum | $resultTree/forum:name | $resultTree/forum:introduction | $resultTree/forum:threadOptions | $resultTree/forum:threadDisplay | $resultTree/bebop:form"/>
            </xsl:when>
            <xsl:when test="$resultTree/@application = 'theme'">
                <xsl:apply-templates select="$resultTree//bebop:layoutPanel"/>
            </xsl:when>
            <xsl:when test="$resultTree/@application = 'shortcuts'">
                <xsl:apply-templates select="$resultTree//bebop:form | $resultTree//bebop:table"/>
            </xsl:when>
            <xsl:when test="$resultTree/@application = 'subsite'">
                <xsl:apply-templates select="$resultTree//subsite:controlCenter"/>
            </xsl:when>
            <xsl:when test="$resultTree/@application = 'terms'">
                <xsl:apply-templates select="$resultTree//terms:domainPanel"/>
            </xsl:when>
            <xsl:when test="$resultTree/@class = 'DOCS'">
                <xsl:apply-templates select="$resultTree//docs:header | $resultTree//docs:body | $resultTree//docs:footer"/>
            </xsl:when>
            <!--      <xsl:when test="$resultTree/@application = 'PublicPersonalProfile'">
              <xsl:apply-templates select="$resultTree//ppp:profile | $resultTree//cms:item | $resultTree//bebop:form"/>
            </xsl:when>       -->
            <xsl:when test="$resultTree/@application = 'PublicPersonalProfile'">
                <xsl:apply-templates select="$resultTree//ppp:profile | $resultTree//ppp:profile/personalPublications | $resultTree//ppp:profile/personalProjects | $resultTree//cms:item | $resultTree//bebop:form"/>
            </xsl:when>      
            <xsl:when test="$resultTree/@application = 'atoz'">
                <xsl:apply-templates select="$resultTree//atoz:adminPane"/>
            </xsl:when>
            <xsl:when test="$resultTree/@application = 'ds'">
                <xsl:apply-templates select="$resultTree//ui:debugPanel | $resultTree//bebop:boxPanel"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="$resultTree/nav:categoryHierarchy | $resultTree//cms:item | $resultTree//bebop:form"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="useImage">
        <xsl:apply-templates select="$resultTree//cms:item" mode="image"/>
    </xsl:template>

    <xsl:template match="useLeadText">
        <xsl:apply-templates select="$resultTree//cms:item" mode="lead"/>
    </xsl:template>

    <xsl:template match="useLogin">
        <xsl:call-template name="mandalay:loginLogout"/>
    </xsl:template>


    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                      List-Section                         *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->

    <!-- DE Listenverarbeitung Anfang -->
    <!-- DE Hier werden die diversen ObjectLists verarbeitet: simpleObjectList, atozObjectList... -->

    <!-- DE Div-Container für die Liste -->
    <xsl:template match="showList">
        <xsl:variable name="list" select="useList"/>
        <!-- DE Wenn die Liste nicht leer ist -->
        <xsl:if test="$resultTree//*[@id=$list]/*[name()!='nav:noContent']">
            <div>
                <xsl:call-template name="mandalay:setIdAndClass"/>
                <xsl:attribute name="id">
                    <xsl:value-of select="$list"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- DE Kopf der Listen -->
    <xsl:template match="useListHeader">
        <xsl:variable name="list" select="../useList"/>
        <xsl:apply-templates select="$resultTree//*[@id=$list]">
            <xsl:with-param name="layoutTree" select="."/>
            <xsl:with-param name="addID" select="$list"/>
        </xsl:apply-templates>
    </xsl:template>

    <!-- DE Inhalt der Listen -->
    <xsl:template match="useList">
        <xsl:variable name="list" select="."/>
        <xsl:apply-templates select="$resultTree//*[@id=$list]/nav:objectList"/>
    </xsl:template>
    <!-- DE Listenverabeitung Ende -->

    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                     Search-Section                        *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->

    <!-- DE Div-Container für die Suchergebnisse -->
    <xsl:template match="showSearch">
        <!--<div>-->
        <xsl:call-template name="mandalay:setIdAndClass"/>
        <xsl:apply-templates/>
        <!--</div>-->
    </xsl:template>

    <!-- DE Suchformular -->
    <xsl:template match="useSearchForm">
        <!--<div>-->
        <xsl:call-template name="mandalay:setIdAndClass"/>
        <xsl:apply-templates select="$resultTree//bebop:form">
            <xsl:with-param name="layoutTree" select="."/>
        </xsl:apply-templates>
        <!--</div>-->
    </xsl:template>

    <!-- DE Ergebnisse der Suche -->
    <xsl:template match="useSearchResults">
        <!--<div>-->
        <xsl:call-template name="mandalay:setIdAndClass"/>
        <xsl:apply-templates select="$resultTree//search:results">
            <xsl:with-param name="layoutTree" select="."/>
        </xsl:apply-templates>
        <!--</div>-->
    </xsl:template>

    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                   Paginator-Section                       *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->

    <!-- DE Paginator -->
    <xsl:template match="usePaginatorHeader">
        <xsl:choose>
            <xsl:when test="../useList">
                <xsl:variable name="list" select="../useList"/>
                <xsl:apply-templates select="$resultTree//*[@id=$list]/nav:objectList/nav:paginator" mode="header">
                    <xsl:with-param name="layoutTree" select="."/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="$resultTree//search:paginator" mode="header">
                    <xsl:with-param name="layoutTree" select="."/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="usePaginatorNavbar">
        <xsl:choose>
            <xsl:when test="../useList">
                <xsl:variable name="list" select="../useList"/>
                <xsl:apply-templates select="$resultTree//*[@id=$list]/nav:objectList/nav:paginator" mode="navbar">
                    <xsl:with-param name="layoutTree" select="."/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="$resultTree//search:paginator" mode="navbar">
                    <xsl:with-param name="layoutTree" select="."/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                     Static-Section                        *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->

    <xsl:template match="showImage">
        <div>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:call-template name="mandalay:staticImage"/>
        </div>
    </xsl:template>

    <xsl:template match="showStaticMenu">
        <div>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:call-template name="mandalay:staticMenu"/>
        </div>
    </xsl:template>

    <xsl:template match="showText">
        <xsl:choose>
            <xsl:when test="@id!='' or @class!='' or @withColorset='true'">
                <span>
                    <xsl:call-template name="mandalay:setIdAndClass"/>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="id" select="line"/>
                    </xsl:call-template>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="id" select="line"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="showLink">
        <xsl:choose>
            <xsl:when test="@id!='' or @class!='' or @withColorset='true'">
                <span>
                    <xsl:call-template name="mandalay:setIdAndClass"/>
                    <xsl:call-template name="mandalay:staticLink"/>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="mandalay:staticLink"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
  
    <!--
      <xsl:template match="show">
        <xsl:call-template name="mandalay:"/>
      </xsl:template>
    -->

    <!-- DE Fehler im Parser, erkennt nicht, daß dieses Template nur aufgerufen wird, wenn /bebop:page/@application = 'portal' -->
    <!-- DE in workspace-index.xsl ist das template vorhanden, hier ist es eigentlich überflüssig MIST -->
    <!-- EN Parser bug workaround for original aplaws theme, probably not needed anymore-->
    <!--
    <xsl:template name="wsBody">
    </xsl:template>
    -->
  
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
    <!-- **************************                  Content-Center-Section                   *********************** -->
    <!-- ************************************************************************************************************ -->
    <!-- ************************************************************************************************************ -->
  
  <xsl:template match="showLeftColumn">
    <xsl:choose>
<!--
      <xsl:when test="">
        <xsl:apply-templates select=""/>
      </xsl:when>
      
-->
      <xsl:when test="$resultTree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:left[//bebop:formWidget]">
        <form>
          <xsl:if test="not(@method)">
            <xsl:attribute name="method">post</xsl:attribute>
          </xsl:if>
          <xsl:call-template name="mandalay:processAttributes"/>
          <xsl:apply-templates select="$resultTree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:left"/>
        </form>
      </xsl:when>
      
      <xsl:when test="$resultTree//bebop:currentPane/cms:container/cms:container">
        <xsl:apply-templates select="$resultTree//bebop:currentPane/cms:container/cms:container"/>
      </xsl:when>
      
      <xsl:when test="$resultTree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:left">
        <xsl:apply-templates select="$resultTree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:left"/>
      </xsl:when>
      
      <xsl:otherwise>
        <xsl:apply-templates select="$resultTree//bebop:currentPane/bebop:layoutPanel/bebop:left"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="showBodyColumn">
    <xsl:choose>
<!--
      <xsl:when test="">
        <xsl:apply-templates select=""/>
      </xsl:when>
      
            -->
            <xsl:when test="$resultTree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:body[//bebop:formWidget] | 
                      $resultTree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:right[//bebop:formWidget]">
                <form>
                    <xsl:if test="not(@method)">
                        <xsl:attribute name="method">post</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="mandalay:processAttributes"/>
                    <xsl:apply-templates select="$resultTree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:body |
                                       $resultTree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:right"/>
                </form>
            </xsl:when>
      
            <xsl:when test="$resultTree//bebop:currentPane/bebop:form[not(//bebop:layoutPanel)]">
                <xsl:apply-templates select="$resultTree//bebop:currentPane/bebop:form"/>
            </xsl:when>
      
            <xsl:when test="$resultTree//bebop:currentPane/cms:container/*[name() != 'cms:container']">
                <xsl:apply-templates select="$resultTree//bebop:currentPane/cms:container/*[name() != 'cms:container']"/>
            </xsl:when>
      
            <xsl:when test="$resultTree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:body | 
                      $resultTree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:right">
                <xsl:apply-templates select="$resultTree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:body | 
                                     $resultTree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:right"/>
            </xsl:when>
      
            <xsl:otherwise>
                <xsl:apply-templates select="$resultTree//bebop:currentPane/bebop:layoutPanel/bebop:body |
                                     $resultTree//bebop:currentPane/bebop:layoutPanel/bebop:right |
                                     $resultTree//bebop:currentPane/cms:itemSummary | 
                                     $resultTree//bebop:currentPane/cms:categorySummary | 
                                     $resultTree//bebop:currentPane/cms:linkSummary | 
                                     $resultTree//bebop:currentPane/cms:lifecycleSummary | 
                                     $resultTree//bebop:currentPane/cms:workflowSummary | 
                                     $resultTree//bebop:currentPane/cms:transactionSummary"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="showCMSGreeting">
        <xsl:call-template name="mandalay:cmsGreeting">
            <xsl:with-param name="resultTree" select="$resultTree"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="showContentType">
        <span id="contenttype">
            <xsl:value-of select="$resultTree/bebop:contentType"/>
        </span>
    </xsl:template>
  
    <xsl:template match="showBebopContextBar">
        <xsl:apply-templates select="$resultTree/bebop:contextBar">
            <xsl:with-param name="layoutTree" select="."/>
        </xsl:apply-templates>
    </xsl:template>
  
    <xsl:template match="showSystemInformation">
        <div class="systemInformation">
            <xsl:apply-templates select="$resultTree/bebop:systemInformation"/>
        </div>
    </xsl:template>
  
    <xsl:template match="showCMSGlobalNavigation">
        <div class="cmsGlobalNavigation">
            <xsl:choose>
                <xsl:when test="*">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$resultTree/cms:globalNavigation"/>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template match="useContentCenterLink">
        <xsl:apply-templates select="$resultTree/cms:globalNavigation/cms:contentCenter"/>
    </xsl:template>
  
    <xsl:template match="useAdminCenterLink">
        <xsl:apply-templates select="$resultTree/cms:globalNavigation/cms:adminCenter"/>
    </xsl:template>
  
    <xsl:template match="useWorkspaceLink">
        <xsl:apply-templates select="$resultTree/cms:globalNavigation/cms:workspace"/>
    </xsl:template>
    
    <xsl:template match="useChangePasswordLink">
        <xsl:choose>
            <xsl:when test="$resultTree/cms:globalNavigation">
                <xsl:apply-templates select="$resultTree/cms:globalNavigation/cms:changePassword"/>
            </xsl:when>
            <xsl:when test="$resultTree/ui:userBanner">
                <span class="cmsGlobalNavigationChangePassword">
                    <a href="{$resultTree/ui:userBanner/@changePasswordURL}">
                        <xsl:apply-templates select="$resultTree/ui:userBanner/@changePasswordLabel"/>
                    </a>
                </span>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
  
    <xsl:template match="useLogoutLink">
        <xsl:choose>
            <xsl:when test="$resultTree/cms:globalNavigation">
                <xsl:apply-templates select="$resultTree/cms:globalNavigation/cms:signOut"/>
            </xsl:when>
            <xsl:when test="$resultTree/ui:userBanner">
                <span class="cmsGlobalNavigationSignOut">
                    <a href="{$resultTree/ui:userBanner/@logoutURL}">
                        <xsl:apply-templates select="$resultTree/ui:userBanner/@signoutLabel"/>
                    </a>
                </span>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
  
    <xsl:template match="useHelpLink">
        <xsl:apply-templates select="$resultTree/cms:globalNavigation/cms:help"/>
    </xsl:template>

    <xsl:template match="usePreviewLink">
        <span class="cmsPreview">
            <xsl:apply-templates select="$resultTree/bebop:link[@id='preview_link']"/>
        </span>
    </xsl:template>

    <xsl:template match="showBebopPageTitle">
        <xsl:apply-templates select="$resultTree/bebop:title"/>
    </xsl:template>

    <xsl:template match="showTabbedPane">
        <xsl:apply-templates select="$resultTree/bebop:tabbedPane">
            <xsl:with-param name="layoutTree" select="."/>
        </xsl:apply-templates>
    </xsl:template>
  
    <xsl:template match="cmsFooter">
    
    </xsl:template>
  
</xsl:stylesheet>
