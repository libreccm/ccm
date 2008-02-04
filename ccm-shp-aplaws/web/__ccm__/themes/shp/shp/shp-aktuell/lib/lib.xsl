<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/london/navigation"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="xsl bebop aplaws ui nav cms terms"
  version="1.0">

  <xsl:param name="theme-prefix"/>

<!--HEADER -->
<!-- nicht mehr benötigt -->
  <xsl:template name="header">
    <div id="header">
      <div id="logo">
        <a href="{$dispatcher-prefix}/portal/" title="APLAWS+ home">
          <img src="{$theme-prefix}/images/aplawsplus.gif" hspace="5" width="158" height="30" alt="APLAWS+ logo" />
        </a>
      </div>
      <div id="utils">
        <span class="hide">|</span>
        <a href="{$dispatcher-prefix}/portal/" title="home" accesskey="1">home</a><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;|<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;
        <a href="{$dispatcher-prefix}/atoz" title="a-z">a-z</a><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;|<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;
        <a href="{$dispatcher-prefix}/navigation/sitemap.jsp" title="site map" accesskey="3">site map</a><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;|<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;
        <a href="/contact" title="contact us" accesskey="9">contact us</a><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;|<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;
        <a href="/help" title="help" accesskey="6">help</a><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp; 
      </div>
      <div id="search">
        <form name="search" method="get" action="{$dispatcher-prefix}/search/">
          <label for="topSearch" accesskey="4">Search</label>
          <input class="searchBox" id="topSearch" name="terms" value="search" />
          <label for="topGo">Go</label>
          <input type="submit" name="Submit" id="topGo" value="GO" class="go" />
          <xsl:apply-templates select="bebop:pageState" />
        </form>
      </div>
    <br id="clear" />
    </div>
  </xsl:template>


<!-- BREADCRUMBS -->
  <xsl:template name="breadcrumb">
    <span class="hide">|</span>
    <div class="breadcrumbs">
      <p>
        Sie sind hier:
        <xsl:choose>
          <xsl:when test="count(nav:categoryPath/nav:category) > 1">
            <a href="{$dispatcher-prefix}/portal/" title="Start">Start</a><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt;
          </xsl:when>
          <xsl:otherwise>
            <span class="breadHi">Start</span>
          </xsl:otherwise>
        </xsl:choose>
        <span class="hide">|</span>
        <xsl:for-each select="nav:categoryPath/nav:category[not(position()=1)]">
          <xsl:choose>
            <xsl:when test="not(position()=last())">
              <a>
                <xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute>
                <xsl:attribute name="title"><xsl:value-of select="@description" /></xsl:attribute>
                <xsl:value-of select="@title" />
              </a>
              <span class="hide">|</span>
              <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<span class="breadArrow"><xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt; </span>
            </xsl:when>
            <xsl:otherwise>
              <!-- Zeige auch die ContentItems in den Breadcrumbs an -->
              <xsl:choose>
                <!-- Wenn es eine Navigationsseite ist, dann ist die Liste damit zu Ende -->
                <xsl:when test="/bebop:page/bebop:title = 'Navigation'">
                  <span class="breadHi"><xsl:value-of select="@title" /></span>
                </xsl:when>
                <!-- Sonst wird der letzte Navigationspunkt auch ein Link und der Content wird angehängt -->
                <xsl:otherwise>
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute>
                    <xsl:attribute name="title"><xsl:value-of select="@description" /></xsl:attribute>
                    <xsl:value-of select="@title" />
                  </a>
                  <span class="hide">|</span>
                  <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<span class="breadArrow"><xsl:text disable-output-escaping="yes">-&amp;</xsl:text>gt; </span>
                  <span class="breadHi"><xsl:value-of select="/bebop:page/cms:contentPanel/cms:item/title" /></span>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </p>
      <span class="hide">|</span>
    </div>
  </xsl:template>

<!-- PAGE CONTENT -->
  <xsl:template name="pageContent">
    <a class="intLink" name="top" />
    <xsl:choose>
    <!-- CIs -->
      <xsl:when test="cms:contentPanel">
        <xsl:apply-templates select="cms:contentPanel/cms:item"/>
        <xsl:call-template name="fileAttachments" />
        <xsl:call-template name="associatedLinks" />
      </xsl:when>
    <!-- A-Z -->
      <xsl:when test="cms:alphabetNavigation">
        <xsl:apply-templates select="ui:simplePageContent/cms:alphabetNavigation"/>
        <div id="navSpace"><img src="{$theme-prefix}/images/spacer.gif" alt="*" /></div>
      </xsl:when>
    <!-- Nav pages -->
      <xsl:otherwise>
        <xsl:call-template name="greeting" />
        <xsl:call-template name="contentLinks" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<!-- TITLE -->
  <xsl:template name="Title">
    <xsl:choose>
      <xsl:when test="cms:contentPanel">
        <xsl:value-of select="cms:contentPanel/cms:item/title"/>
      </xsl:when>
      <xsl:when test="cms:alphabetNavigation">A - Z</xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="/bebop:page/nav:categoryMenu//nav:category[@isSelected='true']">
          <xsl:choose>
            <xsl:when test="position() = last() and position() = 1">
              <xsl:value-of select="/bebop:page//title"/>
            </xsl:when>
            <xsl:when test="position() = last()">
              <xsl:value-of select="@title"/>
            </xsl:when>
          </xsl:choose>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<!-- GREETING -->
  <xsl:template name="greeting">
    <xsl:for-each select="nav:greetingItem[@id='greetingItem']/cms:item">
      <div id="greeting">
        <xsl:for-each select="imageCaptions">
          <div id="CI_Image">
            <img align="right">
              <xsl:attribute name="src"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="imageAsset/id" /></xsl:attribute>
              <xsl:attribute name="alt"><xsl:value-of select="caption" /></xsl:attribute>
            </img>
          </div>
        </xsl:for-each>
        <p>
          <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
        </p>
      </div>
    </xsl:for-each>
  </xsl:template>

<!-- CONTENT LINKS -->
  <xsl:template name="contentLinks">
    <xsl:if test="nav:simpleObjectList/nav:objectList/nav:item">
      <div id="contentLinks">
        <h2>
          Quick Links
        </h2>
        <xsl:for-each select="nav:simpleObjectList/nav:objectList">
          <div id="contentLinks">
            <ul>
              <xsl:for-each select="nav:item">
                <li>
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="nav:path" /></xsl:attribute>
                    <xsl:attribute name="title"><xsl:value-of select="nav:attribute[@name='title']" /></xsl:attribute>
                    <xsl:value-of select="nav:attribute[@name='title']" />
                  </a>
                  <span class="hide">|</span>
                </li>
              </xsl:for-each>
            </ul>
          </div>
        </xsl:for-each>
      </div>
    </xsl:if>
  </xsl:template>

<!-- FILE ATTACHMENTS -->
  <xsl:template name="fileAttachments">
    <xsl:if test="cms:contentPanel/cms:item/fileAttachments">
      <div class="fileAttachments">
        <h2>Downloads</h2>
        <ul class="linklist">
          <xsl:for-each select="cms:contentPanel/cms:item/fileAttachments">
            <li>
              <xsl:value-of select="name"/> - 
              <xsl:value-of select="description"/>
              <a href="{$dispatcher-prefix}/cms-service/stream/asset/?asset_id={./id}" title="view file">[View]</a>
              <a href="{$dispatcher-prefix}/cms-service/download/asset/?asset_id={./id}" title="save file to your computer">[Save]</a>
            </li>
          </xsl:for-each>
        </ul>
      </div>
    </xsl:if>
  </xsl:template>	

<!-- ASSOCIATED LINKS -->
  <xsl:template name="associatedLinks">
    <xsl:if test="cms:contentPanel/cms:item/links">
      <div class="associatedLinks">
        <h2>Siehe auch:</h2>
        <ul>
          <xsl:for-each select="cms:contentPanel/cms:item/links">
            <xsl:sort select="linkOrder" data-type="number" />
            <li>
              <a>
                <xsl:attribute name="href">
                  <xsl:choose>
                    <xsl:when test="targetType='internalLink'">
                      <xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="targetItem/@oid"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="targetURI"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:attribute>
                <xsl:attribute name="title"><xsl:value-of select="./linkDescription" /></xsl:attribute>
                <xsl:value-of disable-output-escaping="yes" select="./linkTitle" />
              </a>
              - <xsl:value-of select="./linkDescription" />
            </li>
          </xsl:for-each>
        </ul>
      </div>
    </xsl:if>
  </xsl:template>	

<!-- RELATED ITEM -->
  <xsl:template name="relatedItems">
    <xsl:for-each select="nav:relatedItems">
      <xsl:if test="nav:relatedItems/*">
        <h2>Related Items</h2>
        <ul>
          <xsl:for-each select="nav:relatedItem">
            <li>
              <a href="{@path}" title="{@title}"><xsl:value-of select="@title" /></a>
            </li>
          </xsl:for-each>
          <span class="hide">|</span>
        </ul>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

<!-- META DATA -->
  <xsl:template name="metaData">
    <meta name="eGMS.accessibility" scheme="WCAG" content="Double-A" />
    <meta name="DCTERMS.audience" content="" scheme="LGAL" href="http://www.esd.org.uk/standards/lgal/" />
    <xsl:for-each select="descendant::dublinCore">
      <meta name="DC.coverage.spatial" scheme="ONS SNAC" content="{./dcCoverageSpatialRef}" />
      <meta name="DC.coverage.temporal.beginnningDate" scheme="ISO 639-2" ><xsl:attribute name="content"><xsl:call-template name="metaDate"><xsl:with-param name="date" select="./dcTemporalBegin" /></xsl:call-template></xsl:attribute></meta>
      <meta name="DC.coverage.temporal.endDate"  scheme="ISO 639-2" ><xsl:attribute name="content"><xsl:call-template name="metaDate"><xsl:with-param name="date" select="./dcTemporalEnd" /></xsl:call-template></xsl:attribute></meta>
      <meta name="DC.creator" content="{./dcCreatorOwner}" />
      <meta name="Dc.date.valid" scheme="ISO8601" ><xsl:attribute name="content"><xsl:call-template name="metaDate"><xsl:with-param name="date" select="./dcDateValid" /></xsl:call-template></xsl:attribute></meta>
      <meta name="eGMS.disposal.review" scheme="ISO8601" ><xsl:attribute name="content"><xsl:call-template name="metaDate"><xsl:with-param name="date" select="./dcDisposalReview" /></xsl:call-template></xsl:attribute></meta>
      <meta name="DC.identifier" content="" schmeme="URI"/>
      <meta name="DC.language" content="{./dcLanguage}" scheme="ISO 639-2" />
      <meta name="DC.publisher" content="{./dcPublisher}" />
      <meta name="DC.rights" content="{./dcRights}" />
      <meta name="DC.relation.isFormatOf" content="{$context-prefix}{//bebop:page/@url}?output=xml" />
      <meta name="DC.subject"><xsl:attribute name="content"><xsl:call-template name="metaTranslate"><xsl:with-param name="toTranslate" select="./dcKeywords" /></xsl:call-template></xsl:attribute></meta>
    </xsl:for-each>
    <xsl:for-each select="terms:assignedTerms/terms:term">
      <xsl:choose>
        <xsl:when test="@domain='GCL'">
          <meta name="eGMS.subject.category" content="{@name}" scheme="GCL" href="http://www.esd.org.uk/standards/gcl/gcl.xml" />
        </xsl:when>
        <xsl:when test="@domain='LGCL'">
          <meta name="eGMS.subject.category" content="{@name}" scheme="LGCL" href="http://www.esd.org.uk/standards/lgcl/lgcl.xml" />
        </xsl:when>
        <xsl:when test="@domain='LGSL'">
          <meta name="eGMS.subject.service" content="{@id}" scheme="LGSL" href="http://www.esd.org.uk/standards/lgsl/lgsl.xml" />
        </xsl:when>
        <xsl:when test="@domain='LGIL'">
          <meta name="eGMS.subject.interaction" content="{@name}" scheme="LGIL" href="http://www.esd.org.uk/standards/lgil/lgil.xml" />
        </xsl:when>
      </xsl:choose>
    </xsl:for-each>
    <meta name="DC.title">
      <xsl:attribute name="content">APLAWS+: <xsl:call-template name="Title" /></xsl:attribute>
    </meta>
  </xsl:template>

<!-- META TRANSLATE -->
  <xsl:template name="metaTranslate">
    <xsl:param name="toTranslate" />
    <xsl:value-of select="translate($toTranslate,',' , ';')" />
  </xsl:template>

<!-- META DATE -->
  <xsl:template name="metaDate"><xsl:param name="date" />20<xsl:value-of select="substring($date, 8, 2)" />
    <xsl:text>-</xsl:text>
    <xsl:variable name="month" select="substring($date, 4, 3)"/>
    <xsl:choose>
      <xsl:when test="$month='Jan'">01</xsl:when>
      <xsl:when test="$month='Feb'">02</xsl:when>
      <xsl:when test="$month='Mar'">03</xsl:when>
      <xsl:when test="$month='Apr'">04</xsl:when>
      <xsl:when test="$month='May'">05</xsl:when>
      <xsl:when test="$month='Jun'">06</xsl:when>
      <xsl:when test="$month='Jul'">07</xsl:when>
      <xsl:when test="$month='Aug'">08</xsl:when>
      <xsl:when test="$month='Sep'">09</xsl:when>
      <xsl:when test="$month='Oct'">10</xsl:when>
      <xsl:when test="$month='Nov'">11</xsl:when>
      <xsl:when test="$month='Dec'">12</xsl:when>
    </xsl:choose>
    <xsl:text>-</xsl:text>
    <!-- Day -->
    <xsl:value-of select="number(substring($date, 1, 2))" />
  </xsl:template>

<!-- BODYDEBUG -->
  <xsl:template name="aplaws:bodyDebug">
    <div class="bodyDebug">
      <xsl:apply-templates select="ui:debugPanel"/>
      <xsl:apply-templates select="bebop:structure"/>
    </div>
  </xsl:template>

</xsl:stylesheet>
