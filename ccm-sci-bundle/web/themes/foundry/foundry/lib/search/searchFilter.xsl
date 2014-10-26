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

<!-- DE
  Hier werden die Suchfilter verarbeitet 
-->

<!-- EN
  Processing search filter
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav mandalay" 
  version="1.0">
  
  <!-- DE Suchfilter für Content Typ -->
  <!-- EN Search filter for content type -->
  <xsl:template match="search:filter[@type='contentType']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setContentType">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setContentType"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setContentType'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setContentType = 'true'">
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
    </xsl:if>
  </xsl:template>
  
  <!-- DE Suchfilter für Object Typ -->
  <!-- EN Search filter for object type -->
  <xsl:template match="search:filter[@type='objectType']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setObjectType">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setObjectType"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setObjectType'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setObjectType = 'true'">
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
    </xsl:if>
  </xsl:template>
  
  <!-- DE Suchfilter für Host -->
  <!-- EN Search filter for host -->
  <xsl:template match="search:filter[@type='host']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setHost">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setHost"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setHost'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setHost = 'true'">
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
    </xsl:if>
  </xsl:template>
  
  <!-- DE Suchfilter für Kategorie -->
  <!-- EN Search filter for category -->
  <xsl:template match="search:filter[@type='category']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setCategory">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setCategory"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setCategory'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setCategory = 'true'">
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
    </xsl:if>
  </xsl:template>
  
  <!-- DE Suchfilter für Autor -->
  <!-- EN Search filter for author -->
  <xsl:template match="search:filter[@type='creationUser']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setCreationUser">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setCreationUser"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setCreationUser'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setCreationUser = 'true'">
      <xsl:apply-templates select="search:partyText">
        <xsl:with-param name="filterName">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'searchfilter/creationUser'"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
  
  <!-- DE Suchfilter für letzten Editor -->
  <!-- EN Search filter for last dditor -->
  <xsl:template match="search:filter[@type='lastModifiedUser']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLastModifiedUser">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setLastModifiedUser"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setLastModifiedUser'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setLastModifiedUser = 'true'">
      <xsl:apply-templates select="search:partyText">
        <xsl:with-param name="filterName">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'searchfilter/lastModUser'"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
  
  <!-- DE Suchfilter für Veröffentlichungsdatum -->
  <!-- EN Search filter for lauch date -->
  <xsl:template match="search:filter[@type='launchDate']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLaunchDate">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setLaunchDate"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setLaunchDate'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setLaunchDate">
      <xsl:call-template name="search:dateRangeFilter">
        <xsl:with-param name="filterName">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'searchfilter/lauchDate'"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  
  <!-- DE Suchfilter für Datum der letzten Änderung -->
  <!-- EN Search filter for last modified date -->
  <xsl:template match="search:filter[@type='lastModifiedDate']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLastModifiedDate">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setLastModifiedDate"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setLastModifiedDate'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setLastModifiedDate = 'true'">
      <xsl:call-template name="search:dateRangeFilter">
        <xsl:with-param name="filterName">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'searchfilter/lastModDate'"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  
  <!-- DE Suchfilter für Erzeugungsdatum -->
  <!-- EN Search filter for creation date -->
  <xsl:template match="search:filter[@type='creationDate']">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setCreationDate">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setCreationDate"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'filter/setCreationDate'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:if test="$setCreationDate = 'true'">
      <xsl:call-template name="search:dateRangeFilter">
        <xsl:with-param name="filterName">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'searchfilter/creationDate'"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  
  <!-- DE Erzeuge Eingabefelder für ein Zeitraum-Filter -->
  <!-- EN Create widgets for a date range filter -->
  <xsl:template name="search:dateRangeFilter">
    <xsl:param name="filterName"/>
    <div class="filter">
      <span class="filterName">
        <xsl:value-of select="$filterName"/>
      </span>
      <span class="filterParam">
        <xsl:choose>
          <xsl:when test="@format">
            <xsl:call-template name="search:newStyleDateRangeFilter"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="search:oldStyleDateRangeFilter"/>
          </xsl:otherwise>
        </xsl:choose>
        
        
      </span>
    </div>
  </xsl:template>

  <xsl:template name="search:newStyleDateRangeFilter">  
    <xsl:variable name="fragment1">
      <xsl:value-of select="substring-before(@format, ' ')"/>
    </xsl:variable>
    <xsl:variable name="fragment2">
      <xsl:value-of select="substring-before(substring-after(@format, concat($fragment1, ' ')), ' ')"/>
    </xsl:variable>
    <xsl:variable name="fragment3">
      <xsl:value-of select="substring-after(@format, concat($fragment1, ' ', $fragment2, ' '))"/>
    </xsl:variable>
    <table>
      <tr>
        <td/>
        <th>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="concat('searchfilter/dateRange/', $fragment1)"/>
          </xsl:call-template>
        </th>
        <th>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="concat('searchfilter/dateRange/', $fragment2)"/>
          </xsl:call-template>
        </th>
        <th>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="concat('searchfilter/dateRange/', $fragment3)"/>
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
        <xsl:call-template name="search:dateRangeFragment">
          <xsl:with-param name="fragment" select="$fragment1"/>
          <xsl:with-param name="mode" select="'start'"/>
        </xsl:call-template>
        <xsl:call-template name="search:dateRangeFragment">
          <xsl:with-param name="fragment" select="$fragment2"/>
          <xsl:with-param name="mode" select="'start'"/>
        </xsl:call-template>
        <xsl:call-template name="search:dateRangeFragment">
          <xsl:with-param name="fragment" select="$fragment3"/>
          <xsl:with-param name="mode" select="'start'"/>
        </xsl:call-template>
      </tr>
      <tr>
        <th align="right">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'search'"/>
            <xsl:with-param name="id" select="'searchfilter/dateRange/to'"/>
          </xsl:call-template>
        </th>
        <xsl:call-template name="search:dateRangeFragment">
          <xsl:with-param name="fragment" select="$fragment1"/>
          <xsl:with-param name="mode" select="'end'"/>
        </xsl:call-template>
        <xsl:call-template name="search:dateRangeFragment">
          <xsl:with-param name="fragment" select="$fragment2"/>
          <xsl:with-param name="mode" select="'end'"/>
        </xsl:call-template>
        <xsl:call-template name="search:dateRangeFragment">
          <xsl:with-param name="fragment" select="$fragment3"/>
          <xsl:with-param name="mode" select="'end'"/>
        </xsl:call-template>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template name="search:dateRangeFragment">
    <xsl:param name="fragment" select="''"/>
    <xsl:param name="mode" select="''"/>
    
    <xsl:choose>
      
      <xsl:when test="$fragment = 'day'">
        <td>
          <input size="2" type="text" name="{@param}.start.day">
            <xsl:choose>
              <xsl:when test="$mode = 'start' and search:day/@startDay">
                <xsl:attribute name="value">
                  <xsl:value-of select="@search:day/@startDay"/>
                </xsl:attribute>
              </xsl:when>
              <xsl:when test="$mode = 'end' and search:day/@endDay">
                <xsl:attribute name="value">
                  <xsl:value-of select="@search:day/@endDay"/>
                </xsl:attribute>
              </xsl:when>
              <xsl:otherwise/>
            </xsl:choose>
          </input>
        </td>
      </xsl:when>
      
      <xsl:when test="$fragment = 'month'">
        <td>
          <select name="{@param}.start.month">
            <xsl:for-each select="search:month">
              <option value="{@value}">
                <xsl:choose>
                  <xsl:when test="$mode = 'start' and @startMonth">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:when>
                  <xsl:when test="$mode = 'end' and @endMonth">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise/>
                </xsl:choose>
                <xsl:value-of select="@title"/>
              </option>
            </xsl:for-each>
          </select>
        </td>
      </xsl:when>

      <xsl:when test="$fragment = 'year'">
        <td>
          <select name="{@param}.start.year">
            <xsl:for-each select="search:year">
              <option value="{@value}">
                <xsl:choose>
                  <xsl:when test="$mode = 'start' and @startYear">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:when>
                  <xsl:when test="$mode = 'end' and @endYear">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise/>
                </xsl:choose>
                <xsl:value-of select="@title"/>
              </option>
            </xsl:for-each>
          </select>
        </td>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>

  </xsl:template>
  
  <xsl:template name="search:oldStyleDateRangeFilter">
    <table>
      <tr>
        <td/>
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
  </xsl:template>

</xsl:stylesheet>
