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
  Hier werden die Theme-Verzeichnisse verarbeitet 
-->

<!-- EN
  Processing theme folder
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
  
  <xsl:template match="theme:folder">
    <xsl:variable name="setFolderFirst">
        <xsl:value-of select="'true'" />
      <!--<xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'theme'"/>
        <xsl:with-param name="setting" select="'setFolderFirst'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>-->
    </xsl:variable>
    
    <li>
      <b class="folder">
        <xsl:value-of select="@name"/>
      </b>
      <ul>
        <xsl:choose>
          <xsl:when test="$setFolderFirst = 'true'">
            <xsl:apply-templates select="theme:folder">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="theme:file">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="theme:folder | theme:file">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </xsl:otherwise>
        </xsl:choose>
      </ul>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
