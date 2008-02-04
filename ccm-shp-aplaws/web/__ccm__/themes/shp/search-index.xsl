<?xml version="1.0"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/london/navigation"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:shp="http://www.shp.de"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav search"
  version="1.0">

  <xsl:import href="lib/header.xsl"/>
  <xsl:import href="lib/lib.xsl"/>
  <xsl:import href="lib/pageLayout.xsl"/>
  <xsl:import href="lib/leftNav.xsl"/>

  <xsl:import href="../../../../ROOT/packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../ROOT/__ccm__/apps/content-section/xsl/index.xsl"/>

  <xsl:param name="context-prefix"/>
  <xsl:param name="dispatcher-prefix" />
  <xsl:param name="theme-prefix" />

  <xsl:output 
    method="html"
    doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
    doctype-system="http://www.w3.org/TR/html4/loose.dtd"
    indent="yes" 
  />

  <xsl:template match="bebop:page[@application='search']">
    <xsl:call-template name="shp:pageLayout"/>
  </xsl:template>

  <xsl:template name="mainContent">
    <span class="hide">|</span>
    <!--CONTENT -->
    <a id="startcontent" title="Start of content"></a>
    <span class="hide">|</span>
    <xsl:call-template name="searchMain" />	
<!--
    <div id="related">
      <h2>Optionen</h2>
      <xsl:choose>
        <xsl:when test="@id='search'">
          <a href="{$dispatcher-prefix}/search/advanced.jsp" title="Erweiterte Suche">Erweiterte Suche</a><span class="hide">|</span>
        </xsl:when>
        <xsl:when test="@id='advanced'">
          <a href="{$dispatcher-prefix}/search/" title="Suche">Einfache Suche</a><span class="hide">|</span>
        </xsl:when>
        <xsl:otherwise />
      </xsl:choose>
    </div>
-->
  </xsl:template>


  <!-- Callback-Funktion -->
  <xsl:template name="shp:navAddOn">
    <li id="menulevel_1" class="selected">
      Suche
    </li>
<!--
    <ul>
      <xsl:choose>
        <xsl:when test="@id='search'">
          <li class="selected">
            Suche
          </li>
-->
<!--
          <li>
            <a href="{$dispatcher-prefix}/search/advanced.jsp">Erweiterte Suche</a>
          </li>
-->
<!--
        </xsl:when>
-->
<!--
        <xsl:when test="@id='advanced'">
          <li>
            <a href="{$dispatcher-prefix}/search/">Suche</a>
          </li>
          <li class="selected">
            Erweiterte Suche
          </li>
        </xsl:when>
-->
<!--
        <xsl:otherwise />
      </xsl:choose>
    </ul>
-->

  </xsl:template>

  <xsl:template name="searchMain">
  
    <!-- Der einheitliche Kopf des Suchformulars -->
    <div id="searchArea">
      <div id="resultsInfo">
        <xsl:choose>
          <xsl:when test="//search:results"><xsl:apply-templates select="//search:paginator" mode="results-summary"/></xsl:when>
          <xsl:otherwise>Geben Sie den Suchbegriff ein. Verwenden Sie + für Und-Verknüpfungen und | für Oder-Verknüpfungen</xsl:otherwise>
        </xsl:choose>
      </div>
      
      <!-- Rufe das Passende Suchformular auf -->
      <xsl:choose>
        <xsl:when test="@id='search'">
          <xsl:call-template name="basicSearch" />
        </xsl:when>
<!--
        <xsl:when test="@id='advanced'">
          <xsl:call-template name="advancedSearch" />
        </xsl:when>
        <xsl:when test="@id='reindex'">
          <xsl:apply-templates/>
        </xsl:when>
-->
        <xsl:otherwise />
      </xsl:choose>
    </div>
    
    <!-- Rufe das Tempalte für die Ergebnisse auf -->
    <xsl:call-template name="searchResults" />
  </xsl:template>

  <!-- Standard-Suche -->
  <xsl:template name="basicSearch">
    <xsl:for-each select="bebop:form[@name='search']">
      <form name="{@name}" method="get" action="{@action}">
        <input id="box" name="terms">
          <xsl:attribute name="value"><xsl:value-of select="./search:query/search:terms/@value" /></xsl:attribute>
        </input>
        <input id="go" type="submit" name="Submit" value="Suche" />
        <xsl:apply-templates select="bebop:pageState" />
      </form>
    </xsl:for-each>
  </xsl:template>

  <!-- Erweiterte Suche -->
<!--
  <xsl:template name="advancedSearch">
    <xsl:for-each select="bebop:form[@name='search']">
      <form name="{@name}" method="get" action="{@action}">
        <input class="searchBox" id="advancedSearch" name="terms">
          <xsl:attribute name="value"><xsl:value-of select="./search:query/search:terms/@value" /></xsl:attribute>
        </input>	
        <div class="searchExplanation">Select the content types you would like to search</div>
        <xsl:for-each select="./search:query/search:filter[@param='contentType']">	
          <label class="searchLabel" for="advancedS">Content Types</label>
          <select id="advancedS" size="10" name="{@param}" multiple="multiple">
          <label class="filterTerm" for="advlisthead">adv list head</label>
          <option id="advlisthead">Select Content Types</option>
          <xsl:for-each select="search:contentType">
            <xsl:sort select="@title"/>
              <label class="filterTerm" for="{@title}"><xsl:value-of select="@title" /></label>
              <option id="{@title}" value="{@name}">
              <xsl:if test="@isSelected">
                <xsl:attribute name="selected">selected</xsl:attribute>
              </xsl:if>
              <xsl:value-of select="@title"/>
              </option>
            </xsl:for-each>
          </select>
        </xsl:for-each>
        <div id="advGo">
          <label class="searchLabel" for="advancedSearchGo">Go</label>
          <input type="submit" name="Submit" id="advancedSearchGo" value="search" class="adgo" />
        </div>
        <xsl:apply-templates select="bebop:pageState" />
      </form>
    </xsl:for-each>
  </xsl:template>
-->

  <!-- Ergebnisliste -->
  <xsl:template name="searchResults">
    <xsl:for-each select="search:results">
      <div id="searchResult">
        <div><xsl:apply-templates select="search:paginator"  mode="current-summary"/></div>
        <ul>
          <xsl:for-each select="search:documents/search:object">
            <li>
              <a href="{@url}" title="{@title}">
                <xsl:value-of select="@title" />
              </a>
              <xsl:if test="not(@summary = '')">
                <br />
                <span>
                  <xsl:value-of select="@summary" />
                </span>
              </xsl:if>
            </li>
          </xsl:for-each>
        </ul>
      </div>
      <xsl:apply-templates select="search:paginator"  mode="pages"/>
    </xsl:for-each>
  </xsl:template>

  <!-- Anzeige der Ergebnisanzahl -->
  <xsl:template match="search:paginator" mode="results-summary">
    Ihre Suche nach 
    <span id="searchTerm"><xsl:value-of select="//search:query/search:terms/@value" /></span>
    hat
    <xsl:choose>
      <xsl:when test="@objectCount = 0">
        leider keine Ergebnisse geliefert.
      </xsl:when>
      <xsl:when test="@objectCount = 1">
        <span id="resultsTotal">
          ein
        </span>
        Ergebnis geliefert.
      </xsl:when>
      <xsl:otherwise>
        <span id="resultsTotal">
          <xsl:value-of select="@objectCount"/>
        </span>
        Ergebnisse geliefert.
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Anzeige der Ergebnis-Indizes -->
  <xsl:template match="search:paginator" mode="current-summary">
    <div>
      <xsl:choose>
        <xsl:when test="@objectCount = 0">
          <xsl:text>Es wurden leider keine Ergebnisse zu der Suche gefunden.</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="@pageCount > 1">
            <xsl:text>Zeige Ergebnisse </xsl:text>
            <xsl:value-of select="@objectBegin"/>
            <xsl:text> bis </xsl:text>
            <xsl:value-of select="@objectEnd"/>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <!-- Ergebnis-Unterseiten-Navigation -->
  <xsl:template match="search:paginator" mode="pages">
    <div id="searchResultPage">
      <xsl:if test="@objectCount > 0">
        <xsl:if test="@pageCount > 1">

          <!-- Zurück-Button -->
          <xsl:if test="@pageNumber > 1">
            <span id="last">
              <a href="{@baseURL}&amp;{@pageParam}={@pageNumber - 1}" title="zurück"><xsl:text disable-output-escaping="yes">&amp;</xsl:text>lt; zurück</a>
            </span>
            <span class="hide">|</span>
          </xsl:if>

          <!-- Seitenangabe -->
<!--
          <span id="page">
 -->
             Seite <xsl:value-of select="@pageNumber"/> von <xsl:value-of select="@pageCount"/>
<!--
          </span>
 -->

          <!-- Weiter-Button -->
          <xsl:if test="@pageNumber &lt; @pageCount">
            <span class="hide">|</span>
            <span id="next">
              <a href="{@baseURL}&amp;{@pageParam}={@pageNumber + 1}" title="weiter">weiter <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt;</a>
            </span>
          </xsl:if>

        </xsl:if>
      </xsl:if>
    </div>
  </xsl:template>

</xsl:stylesheet>
