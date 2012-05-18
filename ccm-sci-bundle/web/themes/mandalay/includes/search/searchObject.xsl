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
  Hier werden die  verarbeitet 
-->

<!-- EN
  Processing 
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
  
  <!-- DE Zeige die Suchergebnisse an -->
  <!-- EN Show the search results -->
  <xsl:template match="search:object">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setScore">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setScore"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setScore'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScore">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScore"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScore'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScoreWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScoreWidth"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScoreWidth'"/>
        <xsl:with-param name="default" select="'50'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScoreHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScoreHeight"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScoreHeight'"/>
        <xsl:with-param name="default" select="'10'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScoreEmptyImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="setGraphicScoreEmptyImage"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScoreEmptyImage'"/>
        <xsl:with-param name="default" select="'images/search/scoreEmpty.gif'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScoreFullImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScoreFullImage"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScoreFullImage'"/>
        <xsl:with-param name="default" select="'images/search/scoreFull.gif'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSummary">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setSummary"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setSummary'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <li>
      <span class="re">
        <xsl:if test="$setScore = 'true'">
          <div class="score">
            <xsl:choose>
              <xsl:when test="$setGraphicScore = 'true'">
                <xsl:attribute name="style">
                  <xsl:text>background-image: url(</xsl:text>
                  <xsl:call-template name="mandalay:linkParser">
                    <xsl:with-param name="link" select="$setGraphicScoreEmptyImage"/>
                    <xsl:with-param name="prefix" select="$theme-prefix"/>
                  </xsl:call-template>
                  <xsl:text>); background-repeat: no-repeat; width: </xsl:text>
                  <xsl:value-of select="$setGraphicScoreWidth"/>
                  <xsl:text>px;</xsl:text>
                </xsl:attribute>
                <div class="imgFull">
                  <xsl:attribute name="style">font-size: 0px; overflow: hidden; width: <xsl:value-of select="@score"/>%; height: <xsl:value-of select="$setGraphicScoreHeight"/>px;</xsl:attribute>
                  <img>
                    <xsl:attribute name="src">
                      <xsl:call-template name="mandalay:linkParser">
                        <xsl:with-param name="link" select="$setGraphicScoreFullImage"/>
                        <xsl:with-param name="prefix" select="$theme-prefix"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:attribute name="alt"><xsl:value-of select="@score"/>%</xsl:attribute>
                    <xsl:attribute name="title"><xsl:value-of select="@score"/>%</xsl:attribute>
                  </img>
                </div>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="@score"/>%&nbsp;
              </xsl:otherwise>
            </xsl:choose>
          </div>
        </xsl:if>
        <span>
          <a href="{@url}"><xsl:value-of select="@title"/></a>
        </span>
      </span>
      <xsl:if test="$setSummary = 'true'">
        <br />
        <span class="summary">
          <xsl:value-of select="@summary" disable-output-escaping="yes"/>
        </span>
      </xsl:if>
    </li>
  </xsl:template>
  
  <!-- DE Zeige die Suchergebnisse in der Admin-Oberfläche an. Die braucht noch Tabellen,
          daher eine gesonderte Behandlung. -->
  <!-- EN Show search results for admin pages. These a still using tables, so there is a
          special processing for the results. -->
  <xsl:template match="search:object" mode="admin">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setScore">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setScore"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setScore'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScore">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScore"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScore'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScoreWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScoreWidth"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScoreWidth'"/>
        <xsl:with-param name="default" select="'50'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScoreHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScoreHeight"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScoreheight'"/>
        <xsl:with-param name="default" select="'10'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScoreEmptyImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScoreEmptyImage"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScoreEmptyImage'"/>
        <xsl:with-param name="default" select="'images/search/scoreEmpty.gif'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGraphicScoreFullImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setGraphicScoreFullImage"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setGraphicScoreFullImage'"/>
        <xsl:with-param name="default" select="'images/search/scoreFull.gif'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setActionImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setActionImage"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setActionImage'"/>
        <xsl:with-param name="default" select="'images/search/action.png'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSummary">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setSummary"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setSummary'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
  
    <div class="searchResults">
      <xsl:choose>
        <xsl:when test="position() mod 2 = 0">
          <xsl:attribute name="class">result even</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class">result odd</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="$setScore = 'true'">
        <span class="score">
          <xsl:choose>
            <xsl:when test="$setGraphicScore = 'true'">
              <div class="score">
                <xsl:attribute name="style">
                  <xsl:text>background-image: url(</xsl:text>
                  <xsl:call-template name="mandalay:linkParser">
                    <xsl:with-param name="link" select="$setGraphicScoreEmptyImage"/>
                    <xsl:with-param name="prefix" select="$theme-prefix"/>
                  </xsl:call-template>
                  <xsl:text>); background-repeat: no-repeat; width: </xsl:text>
                  <xsl:value-of select="$setGraphicScoreWidth"/>
                  <xsl:text>px;</xsl:text>
                </xsl:attribute>
                <div class="imgFull">
                  <xsl:attribute name="style">font-size: 0px; overflow: hidden; width: <xsl:value-of select="@score"/>%; height: <xsl:value-of select="$setGraphicScoreHeight"/>px;</xsl:attribute>
                  <img>
                    <xsl:attribute name="src">
                      <xsl:call-template name="mandalay:linkParser">
                        <xsl:with-param name="link" select="$setGraphicScoreFullImage"/>
                        <xsl:with-param name="prefix" select="$theme-prefix"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:attribute name="alt"><xsl:value-of select="@score"/>%</xsl:attribute>
                    <xsl:attribute name="title"><xsl:value-of select="@score"/>%</xsl:attribute>
                  </img>
                </div>
              </div>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@score"/>%
            </xsl:otherwise>
          </xsl:choose>
        </span>
      </xsl:if>
      <span>
        <a href="{@url}&amp;context=draft">
          <xsl:value-of select="concat(@title, ' (', @locale, ')')"/>
        </a>
      </span>
      <xsl:if test="$setSummary = 'true'">
        <span><em><xsl:value-of select="@summary"/></em></span>
      </xsl:if>
      <xsl:if test="@class='jsButton' or @class='radioButton'">
        <span>
          <xsl:value-of disable-output-escaping="yes" select="search:jsAction"/>
          <a onClick="{search:jsAction/@name}" href="javascript:{search:jsAction/@name}">
            <img>
              <xsl:attribute name="src">
                <xsl:call-template name="mandalay:linkParser">
                  <xsl:with-param name="link" select="$setActionImage"/>
                  <xsl:with-param name="prefix" select="$theme-prefix"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="alt">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'search'"/>
                  <xsl:with-param name="id" select="'resultlist/select'"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="title">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'search'"/>
                  <xsl:with-param name="id" select="'resultlist/select'"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'search'"/>
              <xsl:with-param name="id" select="'resultlist/select'"/>
            </xsl:call-template>
          </a>
        </span>
      </xsl:if>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
