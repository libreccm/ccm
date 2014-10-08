<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

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

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  version="1.0">

  <xsl:import href="../includes/search.xsl"/>

  <!-- DE Zeige das Suchformular für die Admin-Oberfläche an -->
  <xsl:template match="search:query">
    <style type="text/css" media="screen">
      #resultList {display: table; }
      #resultList .resultListHeader {display: table-row; background-color: #eee; width: 100%; color: #999; font-weight: bold; }
      #resultList .resultListHeader span {display: table-cell; padding-top: 0.1em; padding-bottom: 0.1em;}
      #resultList .result {display: table-row; }
      #resultList .result span {display: table-cell; padding-bottom: 0.7em; padding-right: 1em; }
      #resultList .result.even {background-color: #f6f6f6;}
      #search {display: table;}
      #search .query {display: table-cell; text-align: right; vertical-align: top; font-weight: bold; padding-right: 1em;}
      #search .terms {display: table-cell; padding-bottom: 1.2em;}
      #search .filter {display: table-row;}
      #search .filterName {display: table-cell; text-align: right; vertical-align: top; font-weight: bold; padding-right: 1em;}
      #search .filterParam {display: table-cell; padding-bottom: 1.2em;}
    </style>
    <div id="search">
      <!-- DE Nicht anzeigen, wenn bereits ein Suchergebnis vorliegt -->
      <xsl:if test="not(../search:results)">
        <xsl:apply-templates select="search:*"/>
      </xsl:if>
    </div>
  </xsl:template>

  <!-- DE Zeigt das Ergebnis in der Admin-Oberfläche an -->
  <xsl:template match="search:results">
    <xsl:apply-templates select="search:paginator" mode="header"/>
    <xsl:apply-templates select="search:documents" mode="admin"/>
    <xsl:apply-templates select="search:paginator" mode="navbar"/>
  </xsl:template>

  <xsl:template match="search:documents" mode="admin">
    <div id="resultList">
      <div class="resultListHeader">
        <span style="width: 4em;">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'listheader/score'"/>
          </xsl:call-template>
        </span>
        <span>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'listheader/title'"/>
          </xsl:call-template>
        </span>
        <span>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'listheader/summary'"/>
          </xsl:call-template>
        </span>
        <span style="width: 10em;">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'listheader/addlink'"/>
          </xsl:call-template>
        </span>
      </div>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="search:object[@class='jsButton']">
    <div>
      <xsl:choose>
        <xsl:when test="position() mod 2 = 0">
          <xsl:attribute name="class">result even</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class">result odd</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <span style="text-align: center;"><xsl:value-of select="@score"/>%</span>
      <span><a href="{@url}"><xsl:value-of select="@title"/></a></span>
      <span><em><xsl:value-of select="@summary"/></em></span>
      <span>
        <xsl:value-of disable-output-escaping="yes" select="search:jsAction"/>
        <a onClick="{search:jsAction/@name}" href="javascript:{search:jsAction/@name}">
          <img src="/__ccm__/static/cms/admin/action-group/action-generic.png" width="14" height="14" border="0"/>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'resultlist/select'"/>
          </xsl:call-template>
        </a>
      </span>
    </div>
  </xsl:template>

  <xsl:template match="search:object[@class='radioButton']">
    <div>
      <xsl:choose>
        <xsl:when test="position() mod 2 = 0">
          <xsl:attribute name="class">result even</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class">result odd</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <span style="text-align: center;"><xsl:value-of select="@score"/>%</span>
      <span><input type="radio" name="{@field}" value="{@oid}"/><a href="{@url}"><xsl:value-of select="@title"/></a></span>
      <span><em><xsl:value-of select="@summary"/></em></span>
      <span>
        <xsl:value-of disable-output-escaping="yes" select="search:jsAction"/>
        <a onClick="{search:jsAction/@name}" href="javascript:{search:jsAction/@name}">
          <img src="/__ccm__/static/cms/admin/action-group/action-generic.png" width="14" height="14" border="0"/>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'resultlist/select'"/>
          </xsl:call-template>
        </a>
      </span>
    </div>
  </xsl:template>

  <xsl:template match="search:partyText">
    <xsl:param name="filterName"/>
    <div class="filter">
      <span class="filterName">
        <xsl:value-of select="$filterName"/>
      </span>
      <span class="filterParam">
        <input size="30">
          <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
          <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
        </input>
      </span>
    </div>
  </xsl:template>

  <!-- ****** -->
  <!-- Filter -->
  <!-- ****** -->
  <xsl:template match="search:filter[@type='contentType']">
    <div class="filter">
      <span class="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/types'"/>
        </xsl:call-template>
      </span>
      <span class="filterParam">
        <select size="10" name="{@param}" multiple="multiple">
          <xsl:for-each select="search:contentType">
            <xsl:sort select="@title"/>
            <option value="{@name}">
              <xsl:if test="@isSelected">
                <xsl:attribute name="selected">selected</xsl:attribute>
              </xsl:if>
              <xsl:value-of select="@title"/>
            </option>
          </xsl:for-each>
        </select>
      </span>
    </div>
  </xsl:template>

  <xsl:template match="search:filter[@type='objectType']">
    <div class="filter">
      <span class="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/types'"/>
        </xsl:call-template>
      </span>
      <span class="filterParam">
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
      </span>
    </div>
  </xsl:template>

  <xsl:template match="search:filter[@type='host']">
    <div class="filter">
      <span class="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/sites'"/>
        </xsl:call-template>
      </span>
      <span class="filterParam">
        <select size="10" name="{@param}" multiple="multiple">
          <xsl:for-each select="search:remoteHost">
            <option value="{@oid}">
              <xsl:if test="@isSelected">
                <xsl:attribute name="selected">selected</xsl:attribute>
              </xsl:if>
              <xsl:value-of select="@title"/>
            </option>
          </xsl:for-each>
        </select>
      </span>
    </div>
  </xsl:template>

  <xsl:template match="search:filter[@type='category']">
    <div class="filter">
      <span class="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/categories'"/>
        </xsl:call-template>
      </span>
      <span class="filterParam">
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
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/searchRecursiv'"/>
        </xsl:call-template>
      </span>
    </div>
  </xsl:template>

  <xsl:template match="search:filter[@type='creationUser']">
    <xsl:apply-templates select="search:partyText">
      <xsl:with-param name="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/creationUser'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="search:filter[@type='lastModifiedUser']">
    <xsl:apply-templates select="search:partyText">
      <xsl:with-param name="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/lastModUser'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="search:filter[@type='launchDate']">
    <xsl:call-template name="search:dateRangeFilter">
      <xsl:with-param name="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/lauchDate'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="search:filter[@type='lastModifiedDate']">
    <xsl:call-template name="search:dateRangeFilter">
      <xsl:with-param name="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/lastModDate'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="search:filter[@type='creationDate']">
    <xsl:call-template name="search:dateRangeFilter">
      <xsl:with-param name="filterName">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'search'"/>
          <xsl:with-param name="id" select="'searchfilter/creationDate'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="search:dateRangeFilter">
    <xsl:param name="filterName"/>
    <div class="filter">
      <span class="filterName">
        <xsl:value-of select="$filterName"/>
      </span>
      <span class="filterParam">

        <table>
          <tr>
            <td></td>
            <th>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'search'"/>
                <xsl:with-param name="id" select="'searchfilter/dateRange/day'"/>
              </xsl:call-template>
            </th>
            <th>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'search'"/>
                <xsl:with-param name="id" select="'searchfilter/dateRange/month'"/>
              </xsl:call-template>
            </th>
            <th>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'search'"/>
                <xsl:with-param name="id" select="'searchfilter/dateRange/year'"/>
              </xsl:call-template>
            </th>
          </tr>
          <tr>
            <th align="right">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'search'"/>
                <xsl:with-param name="id" select="'searchfilter/dateRange/from'"/>
              </xsl:call-template>
            </th>
            <td>
              <input size="2" type="text" name="{@param}.start.day">
                <xsl:if test="search:day/@startDay">
                  <xsl:attribute name="value">
                    <xsl:value-of select="@search:day/@startDay"/>
                  </xsl:attribute>
                </xsl:if>
              </input>
            </td>
            <td>
              <select name="{@param}.start.month">
                <xsl:for-each select="search:month">
                  <option value="{@value}">
                    <xsl:if test="@startMonth">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="@title"/>
                  </option>
                </xsl:for-each>
              </select>
            </td>
            <td>
              <select name="{@param}.start.year">
                <xsl:for-each select="search:year">
                  <option value="{@value}">
                    <xsl:if test="@startYear">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="@title"/>
                  </option>
                </xsl:for-each>
              </select>
            </td>
          </tr>
          <tr>
            <th align="right">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'search'"/>
                <xsl:with-param name="id" select="'searchfilter/dateRange/to'"/>
              </xsl:call-template>
            </th>
            <td>
              <input size="2" type="text" name="{@param}.end.day">
                <xsl:if test="search:day/@endDay">
                  <xsl:attribute name="value">
                    <xsl:value-of select="@search:day/@endDay"/>
                  </xsl:attribute>
                </xsl:if>
              </input>
            </td>
            <td>
              <select name="{@param}.end.month">
                <xsl:for-each select="search:month">
                  <option value="{@value}">
                    <xsl:if test="@endMonth">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="@title"/>
                  </option>
                </xsl:for-each>
              </select>
            </td>
            <td>
              <select name="{@param}.end.year">
                <xsl:for-each select="search:year">
                  <option value="{@value}">
                    <xsl:if test="@endYear">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="@title"/>
                  </option>
                </xsl:for-each>
              </select>
            </td>
          </tr>
        </table>

      </span>
    </div>
  </xsl:template>

</xsl:stylesheet>
