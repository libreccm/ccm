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
  Dies ist die Importdatei für {Name}. Sie importiert alle XSL-Dateien aus
  dem Unterverzeichnis {Dir}. Dies ist der einzige Ort in diesem Theme,
  indem die Dateien importiert werden dürfen.
-->

<!-- EN
  This is the import file for {Name}. It is importing all xsl files from
  the {Dir} subfolder. This is the only place in this theme where these
  files are allowed to be imported.
--> 

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:docs="http://www.redhat.com/docs/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0">
  
  <xsl:template match="docs:header">
    <div class="docsHeader">
      <div class="docsGlobalNavigation">
        <xsl:call-template name="docs:dimensionalNavbar"/>
        <xsl:call-template name="docs:globalNavigation"/>
        <xsl:apply-templates select="$resultTree//bebop:tabbedPane"/>
      </div>
    </div>
    
    <div class="localHeader">
      <xsl:apply-templates select="bebop:link[@class = 'portalControlProfileLink']"/>
      <xsl:apply-templates select="bebop:link[@class = 'portalControl']"/>
    </div>
  </xsl:template>

  <xsl:template name="docs:dimensionalNavbar">
    <xsl:param name="layoutTree" select="."/>
    
    <div class="globalNavigation">
      <xsl:if test="bebop:dimensionalNavbar[@class = 'portalNavbar']">
        <xsl:for-each select="bebop:dimensionalNavbar[@class = 'portalNavbar']/*">
          <span class="bebopContextBar">
            <xsl:apply-templates select="."/>
          </span>
          <xsl:if test="position() != last()">
            <span class="bebopContextBarSpacer">
              <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="node" select="$layoutTree/separator" />
                <xsl:with-param name="module" select="'contextBar'" />
                <xsl:with-param name="setting" select="'separator'" />
                <xsl:with-param name="default" select="' -> '" />
              </xsl:call-template>
            </span>
          </xsl:if>
        </xsl:for-each>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template name="docs:globalNavigation">
    <div class="docsGlobalNavigation">
      <span class="docsGlobalNavigationHelp">
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="$resultTree//docs:global/bebop:link[class='helpLink']/@href"/>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="docs"/>
              <xsl:with-param name="id" select="help"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="docs"/>
            <xsl:with-param name="id" select="help"/>
          </xsl:call-template>
        </a>
      </span>
      <span class="docsGlobalNavigationSignOut">
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="$resultTree//docs:global/bebop:link[class='signoutLink']/@href"/>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="docs"/>
              <xsl:with-param name="id" select="signOut"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="docs"/>
            <xsl:with-param name="id" select="signOut"/>
          </xsl:call-template>
        </a>
      </span>
    </div>          
  </xsl:template>

</xsl:stylesheet>
