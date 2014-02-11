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
  Hier werden die Portale verarbeitet 
-->

<!-- EN
  Processing portals
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  exclude-result-prefixes="xsl bebop cms nav mandalay portal portlet"
  version="1.0">

  <xsl:template match="portal:portal">
    <xsl:param name="layout" select="@layout"/>
    <xsl:param name="column" select="'1'"/>
    
    <xsl:variable name="style">
      <xsl:value-of select="(substring-before(@style, ' ') - 1) div substring-before(@style, ' ')"/>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="contains($layout, ',')">
        <div class="portalColumn">
          <xsl:attribute name="style">
            <xsl:value-of select="concat('width:',substring-before(substring-before($layout, ','), '%') - $style,'%;')"/>
          </xsl:attribute>
          <xsl:apply-templates select="*[@cellNumber = $column]"/>
        </div>
        <div class="portalColumnSpacer">
          &nbsp;
        </div>
        <xsl:apply-templates select=".">
          <xsl:with-param name="layout" select="substring-after($layout, ',')"/>
          <xsl:with-param name="column" select="$column + 1"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <div class="portalColumn">
          <xsl:attribute name="style">
            <xsl:choose>
              <xsl:when test="$column > 1">
                <xsl:value-of select="concat('width:', substring-before($layout, '%') - $style,'%;')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="concat('width:', $layout, ';')"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:apply-templates select="*[@cellNumber = $column]"/>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="portal:portalList">
    <div class="portalList">
      <xsl:apply-templates select="portal:portalDetails"/>
      <div class="portalListEnd"/>
    </div>
  </xsl:template>
  
  <xsl:template match="portal:portalDetails">
    <xsl:variable name="setConfirmDelete">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'portal'"/>
        <xsl:with-param name="setting" select="'setConfirmDelete'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <div class="portalPane">
      <xsl:if test="position() != 1">
        <a href="{@moveLeftAction}">
          <img border="0" style="margin-left: 5px">
            <xsl:attribute name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'portal'"/>
                    <xsl:with-param name="setting" select="'setImage/moveLeft'"/>
                    <xsl:with-param name="default" select="'/images/portal/moveLeft.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:attribute>
          </img>
        </a>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="@isSelected = 'true'">
          <div>
            <xsl:apply-templates select="../bebop:form"/>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <a href="{@selectAction}">
            <xsl:value-of select="title"/>
          </a>
        </xsl:otherwise>
      </xsl:choose>
      
      <!-- XXX -->      
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="@deleteAction"/>
        </xsl:attribute>
        <xsl:if test="$setConfirmDelete = 'true'">
          <xsl:attribute name="onclick">
            <xsl:text>return confirm('</xsl:text>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'portal'"/>
              <xsl:with-param name="id" select="'pane/delete/confirmMessage'"/>
            </xsl:call-template>
            <xsl:text>');</xsl:text>
          </xsl:attribute>
        </xsl:if>
        <img border="0" style="margin-left: 5px">
          <xsl:attribute name="src">
            <xsl:call-template name="mandalay:linkParser">
              <xsl:with-param name="link">
                <xsl:call-template name="mandalay:getSetting">
                  <xsl:with-param name="module" select="'portal'"/>
                  <xsl:with-param name="setting" select="'setImage/delete'"/>
                  <xsl:with-param name="default" select="'/images/portal/delete.gif'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="prefix" select="$theme-prefix"/>
            </xsl:call-template>
          </xsl:attribute>
        </img>
      </a>
      <xsl:if test="position() != last()">
        <a href="{@moveRightAction}">
          <img border="0" style="margin-left: 5px">
            <xsl:attribute name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'portal'"/>
                    <xsl:with-param name="setting" select="'setImage/delete'"/>
                    <xsl:with-param name="default" select="'/images/portal/moveRight.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:attribute>
          </img>
        </a>
      </xsl:if>
    </div>
    <xsl:if test="position() != last()">
      <div class="portalPaneSpacer"/>
    </xsl:if>
  </xsl:template>  
  
  <xsl:template match="portlet:simple | bebop:portlet">
    <div class="portlet">
      <xsl:variable name="setHeading">
        <xsl:apply-templates mode="setHeading"/>
      </xsl:variable>
      <xsl:if test="$setHeading = 'true'">
        <h2>
          <xsl:value-of select="@title"/>
        </h2>
      </xsl:if>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="bebop:portlet[portlet:action]">
    <div class="editPortlet">
      <div class="startLeftFloat customize">
        <xsl:if test="portlet:action[@name='customize']">
          <xsl:apply-templates select="portlet:action[@name='customize']">
            <xsl:with-param name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'portal'"/>
                    <xsl:with-param name="setting" select="'setImage/customize'"/>
                    <xsl:with-param name="default" select="'/images/portal/customize.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="title" select="@name"/>
            <xsl:with-param name="alt" select="'C'"/>
          </xsl:apply-templates>
        </xsl:if>
      </div>
      <div class="addLeftFloat moveUp">
        <xsl:if test="portlet:action[@name='moveUp'] and position() != '1'">
          <xsl:apply-templates select="portlet:action[@name='moveUp']">
            <xsl:with-param name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'portal'"/>
                    <xsl:with-param name="setting" select="'setImage/moveUp'"/>
                    <xsl:with-param name="default" select="'/images/portal/moveUp.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="title" select="@name"/>
            <xsl:with-param name="alt" select="'^'"/>
          </xsl:apply-templates>
        </xsl:if>
      </div>
      <div class="addLeftFloat delete">
        <xsl:if test="portlet:action[@name='delete']">
          <xsl:apply-templates select="portlet:action[@name='delete']">
            <xsl:with-param name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'portal'"/>
                    <xsl:with-param name="setting" select="'setImage/delete'"/>
                    <xsl:with-param name="default" select="'/images/portal/delete.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="title" select="@name"/>
            <xsl:with-param name="alt" select="'X'"/>
          </xsl:apply-templates>
        </xsl:if>
      </div>
      <div class="startLeftFloat moveLeft">
        <xsl:if test="portlet:action[@name='moveLeft'] and @cellNumber != '1'">
          <xsl:apply-templates select="portlet:action[@name='moveLeft']">
            <xsl:with-param name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'portal'"/>
                    <xsl:with-param name="setting" select="'setImage/moveLeft'"/>
                    <xsl:with-param name="default" select="'/images/portal/moveLeft.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="title" select="@name"/>
            <xsl:with-param name="alt" select="'&lt;'"/>
          </xsl:apply-templates>
        </xsl:if>
      </div>
      <div class="addLeftFloat portlet">
        <!-- DE Wähle alle Kindknoten mit Ausnahme von portlet:action aus -->
        <!-- EN Select all child nodes except portlet:action -->
        <xsl:apply-templates select="*[not(self::portlet:action)]"/>
      </div>
      <div class="addLeftFloat moveRight">
        <xsl:if test="portlet:action[@name='moveRight'] and @cellNumber != substring-before(../@style, ' ')">
          <xsl:apply-templates select="portlet:action[@name='moveRight']">
            <xsl:with-param name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'portal'"/>
                    <xsl:with-param name="setting" select="'setImage/moveRight'"/>
                    <xsl:with-param name="default" select="'/images/portal/moveRight.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="title" select="@name"/>
            <xsl:with-param name="alt" select="'&gt;'"/>
          </xsl:apply-templates>
        </xsl:if>
      </div>
      <div class="startLeftFloat bottomLeft">
      </div>
      <div class="addLeftFloat moveDown">
        <!-- DE Das letzte bebop:portlet ist immer das Widget zum hinzugügen weiterer Portlets,
                daher wird hier einer abgezogen, damit das letze verschiebare Portlet kein
                Verschieben nach unten zuläßt. -->
        <!-- EN The last bebop:portlet is always the widget to add another portlet, so
                we substract 1 to ensure that the last movable portlet doesn't have a
                move down button. -->
        <xsl:if test="portlet:action[@name='moveDown'] and position() &lt; last() - 1">
          <xsl:apply-templates select="portlet:action[@name='moveDown']">
            <xsl:with-param name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'portal'"/>
                    <xsl:with-param name="setting" select="'setImage/moveDown'"/>
                    <xsl:with-param name="default" select="'/images/portal/moveDown.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="title" select="@name"/>
            <xsl:with-param name="alt" select="'v'"/>
          </xsl:apply-templates>
        </xsl:if>
      </div>
      <div class="addLeftFloat bottomRight">
      </div>
      <div class="endFloat horizontalSpacerSmall"/>   
    </div>
  </xsl:template>

</xsl:stylesheet>
