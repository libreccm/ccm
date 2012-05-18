<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2011 Jens Pelzetter
  
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

<!-- Generation of the export links for publications  -->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0">

  <xsl:template name="showPublicationExportLinks">
    <xsl:param name="layoutTree" select="."/>

    <xsl:if test="$resultTree//publicationExportLink">
      <!-- Get all needed settings -->
      <xsl:variable name="setHeading">
	<xsl:call-template name="mandalay:getSetting">
	  <xsl:with-param name="node" select="$layoutTree/setHeading"/>
	  <xsl:with-param name="module" select="'SciPublications'"/>
	  <xsl:with-param name="setting" select="'exportLinks/setHeading'"/>
	  <xsl:with-param name="default" select="'true'"/>
	</xsl:call-template>
      </xsl:variable>

      <div class="publicationExportLinks">
	<xsl:if test="$setHeading='true'">
	  <h2>
	    <xsl:call-template name="mandalay:getStaticText">	      
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'exportLinks/heading'"/>
	    </xsl:call-template>	    
	  </h2>
	  <ul class="linklist">
	    <xsl:for-each select="$resultTree//publicationExportLink">
	      <xsl:sort data-type="text" select="formatName"/>
	      <li>
		<a> 
		  <xsl:attribute name="href"><xsl:value-of select="$dispatcher-prefix"/>/scipublications/export/?format=<xsl:value-of select="./formatKey"/>&amp;publication=<xsl:value-of select="./publicationId"/></xsl:attribute>
		  <xsl:attribute name="title">
		    <xsl:call-template name="mandalay:getStaticText">
		      <xsl:with-param name="module" select="'SciPublications'"/>
		      <xsl:with-param name="id" select="concat('downloadAs',./formatName)"/>
		    </xsl:call-template>
		  </xsl:attribute>
		    <xsl:call-template name="mandalay:getStaticText">
		      <xsl:with-param name="module" select="'SciPublications'"/>
		      <xsl:with-param name="id" select="./formatName"/>
		    </xsl:call-template>		  
		</a>
	      </li>
	    </xsl:for-each>
	  </ul>
	</xsl:if>
      </div>
      
    </xsl:if>

  </xsl:template>

</xsl:stylesheet>
