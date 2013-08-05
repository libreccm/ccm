<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
     Copyright 2010, Jens Pelzetter

         
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

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav"
  version="1.0"
  >

  <!--
      **************************************************************************
      ** Templates for an ArticleInCollectedVolume publication                **
      **************************************************************************
  -->

  <!-- 
       Detail view 
       ===========
  -->
  
  <xsl:template name="CT_Series_graphics"
		match="cms:item[objectType='com.arsdigita.cms.contenttypes.Series']"
		mode="detailed_view">
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'series/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'series/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'series/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'series/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="setAbstract">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'series/setAbstract'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setEditors">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'series/setEditors'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setVolumes">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'series/setVolumes'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    
    <xsl:if test="($setAbstract = 'true') and (string-length(./abstract) &gt; 0)">
      <div class="publicationAbstract">
	<h3>
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'SciPublications'"/>
	    <xsl:with-param name="id" select="'series/abstract'"/>
	  </xsl:call-template>
	</h3>
	<pre class="abstract">
	<xsl:value-of select="./abstract"/>
	</pre>
      </div>
    </xsl:if>

    <xsl:if test="($setEditors = 'true') and (count(./editors/editor) &gt; 0)">
      <table class="publication publicationSeriesEditors">
	<caption>
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'SciPublications'"/>
	    <xsl:with-param name="id" select="'series/editors'"/>
	  </xsl:call-template>
	</caption>
	<xsl:for-each select="./editors/editor">
	  <tr>
	    <td>
	      <xsl:value-of select="./@fromYear"/>
	      <xsl:if test="./@toYear">
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'SciPublications'"/>
		  <xsl:with-param name="id" select="'series/editorsPeriodSeparator'"/>
		</xsl:call-template>
	      </xsl:if>
	      <xsl:value-of select="./@toYear"/>
	    </td>
	    <td>
	      <xsl:value-of select="./givenname"/><xsl:text> </xsl:text> <xsl:value-of select="./surname"/>
	    </td>
	  </tr>
	</xsl:for-each>
      </table>
    </xsl:if>

    <xsl:if test="($setVolumes = 'true') and (count(./volumes/publication) &gt; 0)">
        <h3>
            <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'SciPublications'"/>
            <xsl:with-param name="id" select="'series/volumes'"/>
            </xsl:call-template>	
        </h3>
        
        <xsl:if test="./filters">
            <form action="" method="get" accept-charset="UTF-8">
                <fieldset>
                    <legend>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'series/filters/heading'"/>
                        </xsl:call-template>
                    </legend>
                    
                    <xsl:for-each select="./filters/filter">
                        <xsl:choose>
                            <xsl:when test="./@type = 'select'">
                                <label>
                                    <xsl:attribute name="for">
                                        <xsl:value-of select="./@label"/>
                                    </xsl:attribute>
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'SciPublications'"/>
                                        <xsl:with-param name="id" select="concat('series/filters/', ./@label, '/label')"/>                                        
                                    </xsl:call-template>
                                </label>
                                <select size="1">
                                    <xsl:attribute name="id">
                                        <xsl:value-of select="./@label"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="name">
                                        <xsl:value-of select="./@label"/>
                                    </xsl:attribute>
                                    <xsl:for-each select="./option">
                                        <option>
                                            <xsl:if test="./@label = ../@selected">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if>
                                            <xsl:attribute name="value">
                                                <xsl:value-of select="./@label"/>
                                            </xsl:attribute>
                                            <xsl:choose>
                                                <xsl:when test="./@label = '--ALL--'">
                                                    <xsl:call-template name="mandalay:getStaticText">
                                                        <xsl:with-param name="module" select="'SciPublications'"/>
                                                        <xsl:with-param name="id" select="concat('series/filters/', ../@label, '/all')"/>
                                                    </xsl:call-template>
                                                </xsl:when>
                                                <xsl:when test="./@label = '--NONE--'">
                                                    <xsl:text> </xsl:text>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="@label"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </option>
                                    </xsl:for-each>
                                </select>                                
                            </xsl:when>
                            <xsl:when test="./@type = 'text'">
                                <label>
                                    <xsl:attribute name="for">
                                        <xsl:value-of select="./@label"/>                                        
                                    </xsl:attribute>
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'SciPublications'"/>
                                        <xsl:with-param name="id" select="concat('series/filters/', ./@label, '/label')"/>
                                    </xsl:call-template>
                                </label>
                                <input type="text">
                                    <xsl:attribute name="id">
                                        <xsl:value-of select="./@label"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="name">
                                        <xsl:value-of select="./@label"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="size">
                                        <xsl:call-template name="mandalay:getSetting">
                                            <xsl:with-param name="module" select="'SciPublications'"/>
                                            <xsl:with-param name="setting" select="concat('series/filters/', ./@label, '/size')"/>
                                            <xsl:with-param name="default" select="'16'"/>
                                        </xsl:call-template>
                                    </xsl:attribute>
                                    <xsl:attribute name="maxlength">
                                        <xsl:call-template name="mandalay:getSetting">
                                            <xsl:with-param name="module" select="'SciPublications'"/>
                                            <xsl:with-param name="setting" select="concat('series/filters/', ./@label, '/maxlength')"/>
                                            <xsl:with-param name="default" select="'256'"/>
                                        </xsl:call-template>      
                                    </xsl:attribute>
                                    <xsl:if test="./@value">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="./@value"/>
                                        </xsl:attribute>
                                    </xsl:if>
                                </input>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:for-each>
                    
                    <div class="filterSubmitResetSection">
                        <input type="submit">
                            <xsl:attribute name="value">
                                <xsl:call-template name="mandalay:getStaticText">
                                    <xsl:with-param name="module" select="'SciInstitute'"/>
                                    <xsl:with-param name="id" select="'publicationsTab/filters/submit'"/>
                                </xsl:call-template>
                            </xsl:attribute>
                        </input>
                        <a class="completeResetButtonLink">
                            <xsl:attribute name="href">?</xsl:attribute>
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciInstitute'"/>
                                <xsl:with-param name="id" select="'projectsTab/filters/reset'"/>
                            </xsl:call-template>                            
                        </a>
                    </div>
                    
                </fieldset>
            </form>
        </xsl:if>
      
      <ul>
    <xsl:for-each select="./volumes/publication">
        <xsl:sort select="./@volume" data-type="number" order="descending"/>
        <xsl:sort select="./yearOfPublication" data-type="number" order="descending"/>
        <xsl:sort select="./title" data-type="text"/>
	  <li>
		<xsl:apply-templates select="." mode="list_view"/>
	  </li>  
	</xsl:for-each>
      </ul>
<!--
      <ul class="seriesVolumes">	
	<xsl:for-each select="./volumes/publication">
	  <li>
	    <xsl:apply-templates select="." mode="list_view"/>
	  </li>
	</xsl:for-each>
      </ul>-->
    </xsl:if>

  </xsl:template>

  <!--
      List view
      =========
  -->
  <xsl:template 
    name="CT_Series_List" 
    match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Series']"
    mode="list_view">
    <!-- DE Hole alle benötigten Einstellungen -->
    <!-- EN Get all settings needed -->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Series'"/>
        <xsl:with-param name="setting" select="'listView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Series'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Series'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Series'"/>
        <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$setLinkToDetails = 'true' or (string-length(nav:attribute[@name='lead']) > $setLeadTextLength and $setLeadTextLength != '0')">
        <a>
          <xsl:attribute name="href"><xsl:value-of select="nav:path"/></xsl:attribute>
          <xsl:attribute name="title">
            <xsl:call-template name="mandalay:shying">
              <xsl:with-param name="title">
                <xsl:value-of select="nav:attribute[@name='title']"/>
              </xsl:with-param>
              <xsl:with-param name="mode">dynamic</xsl:with-param>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:call-template name="mandalay:shying">
            <xsl:with-param name="title">
              <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='title']"/>
            </xsl:with-param>
            <xsl:with-param name="mode">dynamic</xsl:with-param>
          </xsl:call-template>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='title']" />
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="nav:attribute[@name='abstract'] and $setLeadText = 'true'">
      <br />
      <span class="intro">
        <xsl:choose>
          <xsl:when test="$setLeadTextLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='abstract']" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes" select="substring(nav:attribute[@name='abstract'], 1, $setLeadTextLength)" />
            <xsl:if test="string-length(nav:attribute[@name='abstract']) > $setLeadTextLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'true'">
                <span class="moreButton">
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="nav:path"/></xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications_Series'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'SciPublications'"/>
                      <xsl:with-param name="id" select="'series/moreButton'"/>
                    </xsl:call-template>
                  </a> 
                </span>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </span>
    </xsl:if>    
  </xsl:template>

  <!-- Link view -->
    <xsl:template 
    name="CT_Series_Link" 
    match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.Series']" 
    mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublcations_Series'"/>
        <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
    <!-- EN -->
    <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
      <xsl:choose>
        <xsl:when test="$setLinkToDetails = 'true' or (string-length(./linkDescription) > $setDescriptionLength and $setDescriptionLength != '0')">
          <a>
            <xsl:attribute name="href"><xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="./targetItem/@oid"/></xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:shying">
                <xsl:with-param name="title">
                  <xsl:value-of select="./linkTitle"/>
                </xsl:with-param>
                <xsl:with-param name="mode">dynamic</xsl:with-param>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:for-each select="./targetItem">
              <xsl:call-template name="mandalay:imageAttachment">
                <xsl:with-param name="showCaption" select="$setImageCaption" />
                <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
                <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
              </xsl:call-template>
            </xsl:for-each>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="./targetItem">
            <xsl:call-template name="mandalay:imageAttachment">
              <xsl:with-param name="showCaption" select="$setImageCaption" />
              <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
              <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
            </xsl:call-template>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="$setImageAndText = 'true' or not(./targetItem/imageAttachments) or $setImage = 'false'">
      <xsl:choose>
        <xsl:when test="$setLinkToDetails = 'true' or (string-length(./linkDescription) > $setDescriptionLength and $setDescriptionLength != '0')">
          <a>
            <xsl:attribute name="href"><xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="./targetItem/@oid"/></xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:shying">
                <xsl:with-param name="title">
                  <xsl:value-of select="./linkTitle"/>
                </xsl:with-param>
                <xsl:with-param name="mode">dynamic</xsl:with-param>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:shying">
              <xsl:with-param name="title">
                <xsl:value-of disable-output-escaping="yes" select="./linkTitle"/>
              </xsl:with-param>
              <xsl:with-param name="mode">dynamic</xsl:with-param>
            </xsl:call-template>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="mandalay:shying">
            <xsl:with-param name="title">
              <xsl:value-of disable-output-escaping="yes" select="./linkTitle"/>
            </xsl:with-param>
            <xsl:with-param name="mode">dynamic</xsl:with-param>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="./linkDescription and $setDescription">
        <br />
        <xsl:choose>
          <xsl:when test="$setDescriptionLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="./linkDescription" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes" select="substring(./linkDescription, 1, $setDescriptionLength)" />
            <xsl:if test="string-length(./linkDescription) > $setDescriptionLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'true'">
                <span class="moreButton">
                  <a>
                    <xsl:attribute name="href"><xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="./targetItem/@oid"/></xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublcations_Series'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'SciPublcations_Series'"/>
                      <xsl:with-param name="id" select="'moreButton'"/>
                    </xsl:call-template>
                  </a> 
                </span>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:if>
  </xsl:template>


</xsl:stylesheet>