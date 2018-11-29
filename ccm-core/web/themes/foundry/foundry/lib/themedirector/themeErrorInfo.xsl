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

<!-- DE
  Hier werden die Theme-Fehler verarbeitet 
-->

<!-- EN
  Processing theme errors
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:foundry="http://foundry.libreccm.org"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:mandalay="http://mandalay.quasiweb.de"
  xmlns:theme="http://ccm.redhat.com/themedirector/1.0"
  exclude-result-prefixes="xsl bebop cms nav mandalay theme" 
  version="1.0">
  
  <xsl:template match="theme:xslWarnings">
    <xsl:variable name="setWarnings">
        <xsl:value-of select="'true'" />
      <!--<xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'theme'"/>
        <xsl:with-param name="setting" select="'setWarnings'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>-->
    </xsl:variable>
    
    <xsl:if test="$setWarnings = 'true'">
      <div>
        <h5>
            <xsl:value-of select="foundry:get-internal-static-text('theme', 'errorInfo/warning')" />
        </h5>
        <ol>
          <xsl:apply-templates/>
        </ol>
      </div>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="theme:xslErrors">
    <div>
      <h5>
        <xsl:value-of select="foundry:get-internal-static-text('theme', 'errorInfo/error')" />
     </h5>
      <ol>
        <xsl:apply-templates/>
      </ol>
    </div>
  </xsl:template>
  
  <xsl:template match="theme:xslFatals">
    <div>
      <h5>
        <xsl:value-of select="foundry:get-internal-static-text('theme', 'errorInfo/fatal')" />
     </h5>
      <ol>
        <xsl:apply-templates/>
      </ol>
    </div>
  </xsl:template>
  
  <xsl:template match="theme:xslErrorInfo">
    <li>
    <xsl:choose>
      <xsl:when test="@message">
        <xsl:value-of select="@message"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@location"/>
      </xsl:otherwise>
    </xsl:choose>
    <blockquote>
      <xsl:if test="@message and @location">
        <strong>
          <xsl:value-of select="foundry:get-internal-static-text('theme', 'errorInfo/location')" />
       </strong>
        <xsl:value-of select="@location"/>
        <br/>
      </xsl:if>
      <xsl:if test="@column and @line and (@column>-1 or @test>-1)">
        <strong>
          <xsl:value-of select="foundry:get-internal-static-text('theme', 'errorInfo/line')" />
       </strong>
        <xsl:value-of select="@line"/>
        <xsl:text> - </xsl:text>
        <strong>
          <xsl:value-of select="foundry:get-internal-static-text('theme', 'errorInfo/column')" />
       </strong>
        <xsl:value-of select="@column"/>
        <br/>
      </xsl:if>
      <xsl:if test="@causeMessage and not(@causeMessage = @message)">
        <strong>
          <xsl:value-of select="foundry:get-internal-static-text('theme', 'errorInfo/cause')" />
       </strong>
        <xsl:value-of select="@causeMessage"/>
      </xsl:if>
    </blockquote>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
