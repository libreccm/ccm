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
  Hier werden die FileAttachments verarbeitet 
-->

<!-- En
  Processing fileAttachments
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

  <xsl:template name="mandalay:fileAttachments">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:if test="$resultTree//cms:item/fileAttachments">

      <!-- DE Hole alle benötigten Einstellungen-->
      <!-- EN Getting all needed setting-->
      <xsl:variable name="setHeading">
        <xsl:call-template name="mandalay:getSetting">
          <xsl:with-param name="node"  select="$layoutTree/setHeading"/>
          <xsl:with-param name="module"  select="'fileAttachments'"/>
          <xsl:with-param name="setting" select="'setHeading'"/>
          <xsl:with-param name="default" select="'true'"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="setDescription">
        <xsl:call-template name="mandalay:getSetting">
          <xsl:with-param name="node"  select="$layoutTree/setDescription"/>
          <xsl:with-param name="module"  select="'fileAttachments'"/>
          <xsl:with-param name="setting" select="'setDescription'"/>
          <xsl:with-param name="default" select="'true'"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="setDownload">
        <xsl:call-template name="mandalay:getSetting">
          <xsl:with-param name="node"  select="$layoutTree/setDownload"/>
          <xsl:with-param name="module"  select="'fileAttachments'"/>
          <xsl:with-param name="setting" select="'setDownload'"/>
          <xsl:with-param name="default" select="'true'"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="includeFileNameIntoFileLinks">
	<xsl:call-template name="mandalay:getSetting">
	  <xsl:with-param name="module"  select="'global'"/>
        <xsl:with-param name="setting" select="'includeFileNameIntoFileLinks'"/>
	<xsl:with-param name="default" select="'false'"/>	
	</xsl:call-template>
      </xsl:variable>


      <div class="fileAttachments">
        <xsl:if test="$setHeading='true'">
          <h2>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'fileAttachments'"/>
              <xsl:with-param name="id" select="'heading'"/>
            </xsl:call-template>
          </h2>
        </xsl:if>
        <ul class="linklist">
          <xsl:for-each select="$resultTree//cms:item/fileAttachments">
            <xsl:sort data-type="number" select="fileOrder"/>
            <li>
	      <a>
		<xsl:choose>
		  <xsl:when test="$includeFileNameIntoFileLinks = 'true'">
		  <xsl:attribute name="href">
		    <xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/asset/<xsl:value-of select="./name"/>?asset_id=<xsl:value-of select="./id"/>
		  </xsl:attribute>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:attribute name="href">
		      <xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/asset/?asset_id=<xsl:value-of select="./id"/>
		    </xsl:attribute>
		  </xsl:otherwise>
		</xsl:choose>
		<!-- <a href="{$dispatcher-prefix}/cms-service/stream/asset/{./name}/?asset_id={./id}"> -->
                <xsl:attribute name="title"><xsl:value-of select="name"/></xsl:attribute>
                <xsl:value-of select="name"/>
              </a>
              <xsl:if test="$setDescription='true'">
                <br />
                <xsl:value-of select="description"/>
              </xsl:if>
              <xsl:if test="$setDownload='true'">
                <br />
		<a>
		  <xsl:choose>
		    <xsl:when test="$includeFileNameIntoFileLinks = 'true'">
		      <xsl:attribute name="href">
			<xsl:value-of select="$dispatcher-prefix"/>/cms-service/download/asset/<xsl:value-of select="./name"/>?asset_id=<xsl:value-of select="./id"/>
		      </xsl:attribute>
		    </xsl:when>
		    <xsl:otherwise>
		      <xsl:attribute name="href">
			<xsl:value-of select="$dispatcher-prefix"/>/cms-service/download/asset/?asset_id=<xsl:value-of select="./id"/>
		      </xsl:attribute>
		    </xsl:otherwise>
		  </xsl:choose>
		  <!--<a href="{$dispatcher-prefix}/cms-service/download/asset/{./name}/?asset_id={./id}">-->
		  <xsl:attribute name="title">
		  <xsl:call-template name="mandalay:getStaticText">
		    <xsl:with-param name="module" select="'fileAttachments'"/>
		    <xsl:with-param name="id" select="'download/title'" />
		  </xsl:call-template>
		  </xsl:attribute>
		  <xsl:call-template name="mandalay:getStaticText">
		    <xsl:with-param name="module" select="'fileAttachments'"/>
		  <xsl:with-param name="id" select="'download/link'" />
		  </xsl:call-template>
		</a>
	      </xsl:if>
            </li>
          </xsl:for-each>
        </ul>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
