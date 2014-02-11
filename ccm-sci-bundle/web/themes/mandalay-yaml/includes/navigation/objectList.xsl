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
  Hier werden die ContentLinks verarbeitet 
-->

<!-- EN
  Processing ContentLinks
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0">

  <xsl:template match="nav:simpleObjectList | nav:complexObjectList | nav:customizableObjectList" name="mandalay:objectList">
    <xsl:param name="layoutTree"/>

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setHeading">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"    select="$layoutTree/setHeading"/>
        <xsl:with-param name="module"  select="'objectList'"/>
        <xsl:with-param name="setting" select="concat(@id, '/setHeading')"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="nav:objectList/nav:item and $setHeading='true'">
      <h2>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module"  select="'objectList'"/>
          <xsl:with-param name="id" select="concat(@id, '/heading')"/>
        </xsl:call-template>
      </h2>
    </xsl:if>

    <xsl:if test="name() = 'nav:customizableObjectList'">
      <!--      <xsl:call-template name="mandalay:customizableObjectList"/>-->
      <xsl:for-each select="./filterControls">
        <xsl:call-template name="mandalay:filterControls"/>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template match="nav:atozObjectList" name="mandalay:atozObjectList">
    <xsl:param name="layoutTree"/>

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setHeading">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"    select="$layoutTree/setHeading"/>
        <xsl:with-param name="module"  select="'objectList'"/>
        <xsl:with-param name="setting" select="concat(@id, '/setHeading')"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"    select="$layoutTree/separator"/>
        <xsl:with-param name="module"  select="'objectList'"/>
        <xsl:with-param name="setting" select="concat(@id, '/separator')"/>
        <xsl:with-param name="default" select="' | '"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setInfoLine">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"    select="$layoutTree/setInfoLine"/>
        <xsl:with-param name="module"  select="'objectList'"/>
        <xsl:with-param name="setting" select="concat(@id, '/atoz/setInfoLine')"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="$setHeading='true'">
      <h2>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module"  select="'objectList'"/>
          <xsl:with-param name="id" select="concat(@id, '/atoz/heading')"/>
        </xsl:call-template>
      </h2>
    </xsl:if>

    <!-- Die Auswahlliste erzeugen -->
    <div id="atozArea">
      <div id="atozSelector">
        <xsl:for-each select="nav:letters/nav:letter">

        <xsl:choose>
          <xsl:when test="@selected='1'">
            <span class="letterSelected">
              <xsl:choose>
                <xsl:when test="@letter = 'any'">
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module"  select="'objectList'"/>
                    <xsl:with-param name="id" select="concat(../../@id, '/atoz/any')"/>
                  </xsl:call-template>
                </xsl:when>

                <xsl:otherwise>
                  <xsl:value-of select="@letter" />
                </xsl:otherwise>
              </xsl:choose>
            </span>
          </xsl:when>

          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="@url"/>
              </xsl:attribute>
              <xsl:choose>
                <xsl:when test="@letter = 'any'">
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module"  select="'objectList'"/>
                    <xsl:with-param name="id" select="concat(../../@id, '/atoz/any')"/>
                  </xsl:call-template>
                </xsl:when>

                <xsl:otherwise>
                  <xsl:value-of select="@letter" />
                </xsl:otherwise>
              </xsl:choose>
            </a>
          </xsl:otherwise>
        </xsl:choose>

        <!-- Separator -->
        <xsl:if test="not(position()=last())">
          <xsl:value-of select="$separator"/>
        </xsl:if>
        </xsl:for-each>
      </div>

      <xsl:if test="$setInfoLine='true'">
        <div id="atozInfo">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module"  select="'objectList'"/>
            <xsl:with-param name="id" select="concat(@id, '/atoz/infoLine')"/>
          </xsl:call-template>
          <span class="letterSelected">
            <xsl:choose>
              <xsl:when test="nav:letters/nav:letter[@selected='1']/@letter = 'any'">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module"  select="'objectList'"/>
                  <xsl:with-param name="id" select="concat(@id, '/atoz/any')"/>
                </xsl:call-template>
              </xsl:when>

              <xsl:otherwise>
                <xsl:value-of select="nav:letters/nav:letter[@selected='1']/@letter" />
              </xsl:otherwise>
            </xsl:choose>
          </span>
        </div>
      </xsl:if>
    </div>

  </xsl:template>

  <!-- DE Verarbeite die ObjectList -->
  <xsl:template match="nav:objectList">
    <xsl:variable name="useEditLinks">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'objectList'"/>
        <xsl:with-param name="setting" select="'useEditLinks'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
  
    <ul>
      <xsl:if test="../@customName">
        <xsl:attribute name="class">
          <xsl:value-of select="../@customName"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:for-each select="nav:section">
        <xsl:sort select="./@sortKey" data-type="number"/>
      	<li>
	  <h2><xsl:value-of select="./@title"/></h2>
	  <ul>
	    <xsl:for-each select="nav:item">
	      <li>
	        <xsl:apply-templates select="." mode="list_view" />
		    <div class="endFloat"/>
	      </li>
	    </xsl:for-each>
	  </ul>
	</li>
      </xsl:for-each>

      <xsl:for-each select="nav:item">
        <li>
          <!-- DE Ruft die Templates aus den ContentTypen auf (Listenansicht) -->
          <!-- EN Calling template from contenttype (listview) -->
          <xsl:apply-templates select="." mode="list_view"/>
          <xsl:if test="($useEditLinks = 'true') and ./editLink">
            <xsl:call-template name="mandalay:itemEditLink">
              <xsl:with-param name="editUrl" select="./editLink"/>
              <xsl:with-param name="itemTitle" select="./title"/>
            </xsl:call-template>
          </xsl:if>
          <div class="endFloat"/>
        </li>
      </xsl:for-each>
    </ul>
  </xsl:template>


</xsl:stylesheet>
