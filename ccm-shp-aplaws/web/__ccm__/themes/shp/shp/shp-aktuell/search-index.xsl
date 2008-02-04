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
    <h1>Search</h1>
    <span class="hide">|</span>
    <!--CONTENT -->
    <a id="startcontent" title="Start of content"></a>
    <span class="hide">|</span>
    <xsl:call-template name="searchMain" />	
<!--
    <div id="related">
      <h2>Search Options</h2>
      <xsl:choose>
        <xsl:when test="@id='search'">
          <a href="{$dispatcher-prefix}/search/advanced.jsp" title="Search with more options">Advanced search</a><span class="hide">|</span>
          <a href="{$dispatcher-prefix}/search/remote.jsp" title="Search other borough's LAWs websites">Remote search</a><span class="hide">|</span>
        </xsl:when>
        <xsl:when test="@id='advanced'">
          <a href="{$dispatcher-prefix}/search/" title="Basic search">Search</a><span class="hide">|</span>
          <a href="{$dispatcher-prefix}/search/remote.jsp" title="Search other borough's LAWs websites">Remote search</a><span class="hide">|</span>
        </xsl:when>
        <xsl:when test="@id='remote'">
          <a href="{$dispatcher-prefix}/search/" title="Basic search">Search</a><span class="hide">|</span>
          <a href="{$dispatcher-prefix}/search/advanced.jsp" title="Search with more options">Advanced search</a><span class="hide">|</span>
        </xsl:when>
        <xsl:otherwise />
      </xsl:choose>
    </div>
-->
  </xsl:template>


  <xsl:template name="searchBreadcrumb">
  <!--BREADCRUMB -->
    <div id="bread">
      <p>
        <b><a href="{$dispatcher-prefix}/portal/" title="home">home</a></b>
        <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt;
        <span class="breadHi">
          <xsl:choose>
            <xsl:when test="@id='search'">Search</xsl:when>
            <xsl:when test="@id='advanced'">Advanced Search</xsl:when>
            <xsl:when test="@id='remote'">Remote Search</xsl:when>
            <xsl:otherwise />
          </xsl:choose>
        </span>
      </p>
    </div>
  </xsl:template>

  <xsl:template name="searchNav">
    <div id="nav">
      <div class="navUp">
        <a href="{$dispatcher-prefix}/portal/">
          <xsl:attribute name="title">up to homepage</xsl:attribute>
          <xsl:text disable-output-escaping="yes">&amp;</xsl:text>#094;
          <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;home
        </a>
      </div>
      <xsl:choose>
        <xsl:when test="@id='search'">
          <div class="navHere"><p>Search</p></div>
        </xsl:when>
        <xsl:when test="@id='advanced'">
          <div class="navHere"><p>Advanced search</p></div>
        </xsl:when>
        <xsl:when test="@id='remote'">
          <div class="navHere"><p>Remote search</p></div>		
        </xsl:when>
        <xsl:otherwise />
      </xsl:choose>
      <a href="/searchhelp" class="navChild" title="Hints and tips" >Hints and Tips</a><span class="hide">|</span>
    </div>
  </xsl:template>

  <xsl:template name="searchMain">
    <xsl:choose>
      <xsl:when test="@id='search'">
        <xsl:call-template name="basicSearch" />
      </xsl:when>
      <xsl:when test="@id='advanced'">
        <xsl:call-template name="advancedSearch" />
      </xsl:when>
      <xsl:when test="@id='remote'">
        <xsl:call-template name="remoteSearch" />
      </xsl:when>
      <xsl:when test="@id='reindex'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise />
    </xsl:choose>
  </xsl:template>

  <xsl:template name="basicSearch">
    <xsl:for-each select="bebop:form[@name='search']">
      <div id="searchArea">
        <div id="resultsInfo">
          <xsl:choose>
            <xsl:when test="//search:results"><xsl:apply-templates select="//search:paginator"  mode="results-summary"/></xsl:when>
            <xsl:otherwise>Explanation of Search</xsl:otherwise>
          </xsl:choose>
        </div>
        <form name="{@name}" method="get" action="{@action}">
          <span class="searchAgain">Type your query:</span>
          <label class="searchLabel" for="mainSearch">Search</label>
          <input class="searchBox" id="mainSearch" name="terms">
            <xsl:attribute name="value"><xsl:value-of select="./search:query/search:terms/@value" /></xsl:attribute>
          </input>
          <div id="advGo">
            <label class="searchLabel" for="basicSearchGo">Go</label>
            <input type="submit" name="Submit" id="basicSearchGo" value="search" class="adgo" />
          </div>
          <xsl:apply-templates select="bebop:pageState" />
        </form>
      </div>
    </xsl:for-each>
    <xsl:call-template name="searchResults" />
  </xsl:template>

  <xsl:template name="advancedSearch">
    <xsl:for-each select="bebop:form[@name='search']">
      <div id="searchArea">
        <div id="resultsInfo">
          <xsl:choose>
            <xsl:when test="//search:results"><xsl:apply-templates select="//search:paginator"  mode="results-summary"/></xsl:when>
            <xsl:otherwise>Explanation of Advanced Search</xsl:otherwise>
          </xsl:choose>
        </div>	
        <form name="{@name}" method="get" action="{@action}">
          <span class="searchAgain">Type your query:</span>
          <label class="searchLabel" for="advancedSearch">Advanced Search</label>
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
      </div>
    </xsl:for-each>
    <xsl:call-template name="searchResults" />
  </xsl:template>

  <xsl:template name="remoteSearch">
    <xsl:for-each select="bebop:form[@name='search']">
      <div id="searchArea">
        <div id="resultsInfo">
          <xsl:choose>
            <xsl:when test="//search:results"><xsl:apply-templates select="//search:paginator"  mode="results-summary"/></xsl:when>
            <xsl:otherwise>Explanation of Remote Search</xsl:otherwise>
          </xsl:choose>
        </div>
        <form name="{@name}" method="get" action="{@action}">
          <span class="searchAgain">Type your query:</span>
          <label class="searchLabel" for="remoteSearch">Remote Search</label>
          <input class="searchBox" id="remoteSearch" name="terms">
            <xsl:attribute name="value"><xsl:value-of select="./search:query/search:terms/@value" /></xsl:attribute>
          </input>
          <div class="searchExplanation">Select the connected websites you would like to search</div>
            <xsl:for-each select="./search:query/search:filter[@param='searchHost']">
              <label class="searchLabel" for="remoteS">Site Select</label>
              <select id="remoteS" size="5" name="{@param}" multiple="multiple">
              <label class="filterTerm" for="remotelisthead">remote list head</label>
              <option id="remotelisthead">Select remote site</option>
              <xsl:for-each select="search:searchHost">
                <label class="filterTerm" for="{@title}"><xsl:value-of select="@title" /></label>
                <option id="{@title}" value="{@oid}">
                  <xsl:if test="@isSelected">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="@title"/>
                </option>
              </xsl:for-each>
            </select>
          </xsl:for-each>
          <div id="advGo">
            <label class="searchLabel" for="remoteSearchGo">Go</label>
            <input type="submit" name="Submit" id="remoteSearchGo" value="search" class="adgo" />
          </div>
          <xsl:apply-templates select="bebop:pageState" />
        </form>
      </div>
    </xsl:for-each>
    <xsl:call-template name="searchResults" />
  </xsl:template>

  <xsl:template name="sponsoredLinks">
    <xsl:for-each select="./bebop:table[@id='SponsoredLinks']">
      <div id="sLinks">
        <h2>Quick Matches</h2>
        <xsl:for-each select="bebop:tbody/bebop:trow">
          <div class="sLink">
            <a href="{./bebop:cell[1]/bebop:link/@href}" title="{./bebop:cell[1]/bebop:link/@href}"><xsl:value-of select="./bebop:cell[1]/bebop:link/bebop:label" /></a>
            <xsl:value-of select="./bebop:cell[2]/bebop:label" />
          </div>
        </xsl:for-each>
      </div>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="searchResults">
    <xsl:for-each select="search:results">
      <div id="resultsList">
        <div><xsl:apply-templates select="search:paginator"  mode="current-summary"/></div>
          <xsl:for-each select="search:documents/search:object">
            <div class="searchResult">
              <a href="{@url}" title="{@title}">
                <xsl:value-of select="@title" />
              </a>
              <xsl:value-of select="@summary" />
            </div>
            <span class="hide">|</span>
          </xsl:for-each>
        </div>
      <xsl:apply-templates select="search:paginator"  mode="pages"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="search:paginator" mode="results-summary">
    You searched for <b><xsl:value-of select="//search:query/search:terms/@value" /></b> <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt;
    <xsl:choose>
      <xsl:when test="@objectCount = 0">
        <xsl:text>There were no results for your search</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <span class="resultsTotal"><xsl:value-of select="@objectCount"/>
          <xsl:text> results found</xsl:text>
        </span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="search:paginator" mode="current-summary">
    <div>
      <xsl:choose>
        <xsl:when test="@objectCount = 0">
          <xsl:text>There were no results for your search</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="@pageCount > 1">
            <xsl:text>displaying results </xsl:text>
            <xsl:value-of select="@objectBegin"/>
            <xsl:text> to </xsl:text>
            <xsl:value-of select="@objectEnd"/>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="search:paginator" mode="pages">
    <div id="resultsPage">
      <xsl:if test="@objectCount > 0">
        <xsl:if test="@pageNumber > 1">
          <div id="pLeft">
            <a href="{@baseURL}&amp;{@pageParam}={@pageNumber - 1}" title="previous page">
              <xsl:text disable-output-escaping="yes">&amp;</xsl:text>lt; Previous page
            </a>
          </div>
          <xsl:if test="@pageNumber = @pageCount">
<!--
            <div id="searchCenterL"> 
              <xsl:text> page </xsl:text>
              <xsl:value-of select="@pageNumber"/>
              <xsl:text> of </xsl:text>
              <xsl:value-of select="@pageCount"/>
            </div>
-->
          </xsl:if>
<!--
          <a href="{@baseURL}&amp;{@pageParam}={@pageNumber - 1}">previous page</a>
-->
        </xsl:if>
        <xsl:if test="@pageNumber &lt; @pageCount">
          <div id="pRight">
            <a href="{@baseURL}&amp;{@pageParam}={@pageNumber + 1}" title="next page">
              Next page <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt;
            </a>
          </div>
<!--
          <div id="searchCenterR"> 
            <xsl:text> page </xsl:text>
            <xsl:value-of select="@pageNumber"/>
            <xsl:text> of </xsl:text>
            <xsl:value-of select="@pageCount"/>
          </div>
-->
<!--
          <a href="{@baseURL}&amp;{@pageParam}={@pageNumber + 1}">next page</a>
-->
        </xsl:if>
      </xsl:if>
    </div>
  </xsl:template>

  <!-- IMPORTED REDHAT -->
  <xsl:template match="search:results">
<!--
    <xsl:apply-templates select="search:paginator" mode="page-summary"/>
-->
    <xsl:apply-templates select="search:paginator" mode="results-summary"/>
    <xsl:if test="search:paginator/@objectCount > 0">
      <xsl:apply-templates select="search:documents"/>
      <xsl:if test="search:paginator/@pageCount > 1">
        <xsl:apply-templates select="search:paginator" mode="pages"/>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template match="search:paginator" mode="page-summary">
    <xsl:if test="@objectCount > 0">
      <div>
        <xsl:text>Displaying page </xsl:text>
        <xsl:value-of select="@pageNumber"/>
        <xsl:text> of </xsl:text>
        <xsl:value-of select="@pageCount"/>
        <xsl:text> (maximum of </xsl:text>
        <xsl:value-of select="@pageSize"/>
        <xsl:text> results per page)</xsl:text>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="search:documents">
    <table class="data" >
      <thead>
        <tr>
          <th>Score</th>
          <th>Title</th>
          <th>Summary</th>
        </tr>
      </thead>
      <tbody>
        <xsl:for-each select="search:object">
          <xsl:apply-templates select=".">
            <xsl:with-param name="class">
              <xsl:choose>
                <xsl:when test="position() mod 2">odd</xsl:when>
                <xsl:otherwise>even</xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:apply-templates>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template match="search:object">
    <xsl:param name="class" select="none"/>

    <tr class="{$class}">
      <td><xsl:value-of select="@score"/></td>
      <td><a href="{@url}"><xsl:value-of select="@title"/></a></td>
      <td><em><xsl:value-of select="@summary"/></em></td>
    </tr>
  </xsl:template>

  <xsl:template match="search:query">
    <table>
      <xsl:for-each select="search:*">
        <tr valign="top">
          <xsl:apply-templates select="."/>
          <td>
            <!-- Pull in the Submit button, next to the terms field -->
            <xsl:if test="name() = 'search:terms'">
              <xsl:apply-templates select="../bebop:formWidget"/>
            </xsl:if>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="search:terms">
    <th align="right">
      <xsl:text>Query:</xsl:text>
    </th>
    <td>
      <input size="30" type="text" name="{@param}" value="{@value}" title="Enter one or more search terms"/>
    </td>
  </xsl:template>

  <xsl:template match="search:filter[@type='objectType']">
    <th align="right">
      <xsl:text>Types:</xsl:text>
    </th>
    <td>
      <select size="10" name="{@param}" multiple="multiple">
        <xsl:for-each select="search:objectType">
          <xsl:sort select="@name"/>
          <option value="{@name}">
            <xsl:if test="@isSelected">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@name"/>
          </option>
        </xsl:for-each>
      </select>
    </td>
  </xsl:template>

  <xsl:template match="search:filter[@type='category']">
    <th align="right">
      <xsl:text>Categories:</xsl:text>
    </th>
    <td>
      <select size="10" name="{@param}" multiple="multiple">
        <xsl:for-each select="search:category">
          <xsl:sort select="@title"/>
          <option value="{@oid}">
            <xsl:if test="@isSelected">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@title"/>
          </option>
        </xsl:for-each>
      </select>
    </td>
  </xsl:template> 

  <xsl:template match="bebop:formWidget">
    <xsl:element name="input">
      <xsl:for-each select="@*">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template match="bebop:pageState">
    <input>
      <xsl:attribute name="type">hidden</xsl:attribute>
      <xsl:for-each select="@*">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
    </input>
  </xsl:template>

</xsl:stylesheet>
