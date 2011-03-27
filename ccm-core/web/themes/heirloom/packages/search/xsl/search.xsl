<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  version="1.0">

  <xsl:output method="html"/>

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

  <xsl:template match="search:paginator" mode="results-summary">
    <div>
      <xsl:choose>
        <xsl:when test="@objectCount = 0">
          <xsl:text>There were no results for your search</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>Displaying results </xsl:text>
          <xsl:value-of select="@objectBegin"/>
          <xsl:text> through </xsl:text>
          <xsl:value-of select="@objectEnd"/>
          <xsl:if test="@pageCount > 1">
            <xsl:text> (total: </xsl:text>
            <xsl:value-of select="@objectCount"/>
            <xsl:text>)</xsl:text>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="search:paginator" mode="pages">
    <xsl:if test="@objectCount > 0">
      <div>
        <xsl:if test="@pageNumber > 1">
          <a href="{@baseURL}&amp;{@pageParam}={@pageNumber - 1}">&lt;&lt;&lt;Previous </a>
        </xsl:if>
        <xsl:text>Page </xsl:text>
        <xsl:value-of select="@pageNumber"/>
        <xsl:text> of </xsl:text>
        <xsl:value-of select="@pageCount"/>
        <xsl:if test="@pageNumber &lt; @pageCount">
          <a href="{@baseURL}&amp;{@pageParam}={@pageNumber + 1}"> Next &gt;&gt;&gt;</a>
        </xsl:if>
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
      <br/>
      <input type="checkbox" value="true">
        <xsl:attribute name="name">
          <xsl:value-of select="search:includeSubCats/@name"/>
        </xsl:attribute>
        <xsl:if test="search:includeSubCats/@value = 'true'">
          <xsl:attribute name="checked">checked</xsl:attribute>
        </xsl:if>
      </input>
      Include subcategories
    </td>
  </xsl:template>

</xsl:stylesheet>
