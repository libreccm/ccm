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
  Hier wird das Portal / Workpspace verarbeitet 
-->

<!-- EN
  Processing portal / workspace
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

  <!-- DE Importiere wichtige Funktionen -->
  <!-- EN using toolbox -->

  <xsl:template match="portal:workspace">
    <xsl:apply-templates select="*[not(self::bebop:link)]"/>
  </xsl:template>
  
  <xsl:template match="portal:workspaceDetails">
    <div class="workspaceDetails">
      <xsl:choose>
        <xsl:when test="../@id != 'view'">
          <a>
            <xsl:attribute name="href">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link" select="./primaryURL"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'portal'"/>
                <xsl:with-param name="id" select="'workspace/view/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'portal'"/>
              <xsl:with-param name="id" select="'workspace/view/link'"/>
            </xsl:call-template>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'portal'"/>
            <xsl:with-param name="id" select="'workspace/view/text'"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
      &nbsp;
      <xsl:choose>
        <xsl:when test="../@id != 'edit' and @canEdit = 'true'">
          <a>
            <xsl:attribute name="href">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link" select="concat(./primaryURL, 'edit.jsp')"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'portal'"/>
                <xsl:with-param name="id" select="'workspace/edit/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'portal'"/>
              <xsl:with-param name="id" select="'workspace/edit/link'"/>
            </xsl:call-template>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'portal'"/>
            <xsl:with-param name="id" select="'workspace/edit/text'"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
      &nbsp;
      <xsl:choose>
        <xsl:when test="../@id != 'admin' and @canAdmin = 'true'">
          <a>
            <xsl:attribute name="href">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link" select="concat(./primaryURL, 'admin/index.jsp')"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'portal'"/>
                <xsl:with-param name="id" select="'workspace/admin/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'portal'"/>
              <xsl:with-param name="id" select="'workspace/admin/link'"/>
            </xsl:call-template>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'portal'"/>
            <xsl:with-param name="id" select="'workspace/admin/text'"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>
  
  <!-- Ist eigendlich überflüssig im XML, bietet keine zusatzliche Funktionalität -->
  <xsl:template match="portal:homepageWorkspace">
    <div class="workspaceColumn">
      <xsl:call-template name="mandalay:processAttributes"/>
      <xsl:apply-templates/>
    </div>
    <xsl:if test="position() != last()">
      <div class="workspaceColumnSpacer">
        &nbsp;
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
