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
      ** Templates for an Journal publication                **
      **************************************************************************
  -->

  <!-- 
       Detail view 
       ===========
  -->
  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Journal']" mode="lead">
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'journal/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="./lead and $setLeadText = 'true'">
      <div class="lead">
        <xsl:value-of disable-output-escaping="yes" select="./lead"/>
      </div>
    </xsl:if>
  </xsl:template>

  <!-- DE Bild -->
  <!-- EN image -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Journal']" mode="image">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'journal/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'journal/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'journal/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'journal/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="$setImage = 'true'">
      <xsl:call-template name="mandalay:imageAttachment">
        <xsl:with-param name="showCaption" select="$setImageCaption"/>
        <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
        <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="CT_CollectedVolume_graphics"
		match="cms:item[objectType='com.arsdigita.cms.contenttypes.Journal']"
		mode="detailed_view">

    <xsl:variable name="setArticles">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'journal/setArticles'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSymbol">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'journal/setSymbol'"/>
	<xsl:with-param name="default" select="'false'"/>	
      </xsl:call-template>
      </xsl:variable>
    <xsl:variable name="setIssn">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'journal/setIssn'"/>
	<xsl:with-param name="default" select="'false'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPeriodOfPublication">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'journal/setIssn'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setAbstract">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'journal/setAbstract'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMisc">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'journal/setMisc'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>

    <div class="mainBody publication publicationDetails publicationJournalDetails">

      <dl>
      <xsl:if test="($setSymbol = 'true') and (string-length(./symbol) &gt; 0)">
        <dt>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'SciPublications'"/>
            <xsl:with-param name="id" select="'journal/symbol'"/>
          </xsl:call-template>
        </dt>
        <dd>
          <xsl:value-of select="./symbol"/>
        </dd>
      </xsl:if>
      <xsl:if test="($setIssn = 'true') and (string-length(./issn) &gt; 0)">
	<dt>
	  <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'journal/issn'"/>	      
	  </xsl:call-template>
	</dt>
	<dd>
	  <xsl:value-of select="./issn"/>
	</dd>
      </xsl:if>

      <xsl:if test="($setPeriodOfPublication = 'true') and (string-length(./firstYear) &gt; 0)">
	<dt>
	  <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'journal/periodOfPublication'"/>	      
	  </xsl:call-template>	  
	</dt>	
	<dd>
	  <xsl:value-of select="./firstYear"/>
	  <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'journal/periodOfPublicationYearsSeparator'"/>
	  </xsl:call-template>	  	  
	  <xsl:if test="string-length(./lastYear) &gt; 0">
	    <xsl:value-of select="./lastYear"/>
	  </xsl:if>
	</dd>
      </xsl:if>
      	
      </dl>

      <xsl:if test="($setAbstract = 'true') and (string-length(./abstract) &gt; 0)">
	<div class="publicationAbstract">
	  <h3>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'journal/abstract'"/>	    
	    </xsl:call-template>
	  </h3>
	  <div class="abstract">
	    <xsl:value-of disable-output-escaping="yes" select="./abstract"/>
	  </div>
	</div>
      </xsl:if>      

      <xsl:if test="($setMisc = 'true') and (string-length(normalize-space(./misc)) &gt; 0)">
	<div class="publicationMisc">
	  <h3>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'journal/misc'"/>	    
	    </xsl:call-template>
	  </h3>
	  <xsl:value-of select="./misc"/>
	</div>
      </xsl:if>      
	
      <xsl:if test="($setArticles = 'true') and (count(./articles/article) &gt; 0)">	
	<h3>
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'SciPublications'"/>
	    <xsl:with-param name="id" select="'journal/articles'"/>	    
	  </xsl:call-template>	    
	</h3>
	<ul class="articlesList">
	  <xsl:for-each select="./articles/article">
	    <xsl:sort select="./yearOfPublication" data-type="number" order="descending"/>
	    <li>
	      <xsl:call-template name="CT_ArticleInJournal_List"/>
	    </li>
	  </xsl:for-each>
	</ul>
      </xsl:if>
    </div>
    
  </xsl:template>


  <!--
      List view
      =========
  -->
  <xsl:template match="publications[objectType='com.arsdigita.cms.contenttypes.Journal']"
		mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:call-template name="CT_Journal_List">
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="publication[objectType='com.arsdigita.cms.contenttypes.Journal']"
		mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:call-template name="CT_Journal_List">
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template  name="CT_Journal_List"
		 match="nav:item[nav:attribute[@name='objectType'] ='com.arsdigita.cms.contenttypes.Journal']"
		 mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:variable name="formatDefFile">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'formatDefFile'"/>
	<xsl:with-param name="default" select="'SciPublicationsDefaultFormat.xml'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <!-- Call template for journal format -->
    <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefJournalFormat">
      <xsl:with-param name="issn" select="./issn"/>
      <xsl:with-param name="misc" select="./misc"/>
      <xsl:with-param name="reviewed" select="./reviewed"/>
      <xsl:with-param name="title" select="./title"/>    
      <xsl:with-param name="oid" select="./@oid"/>     
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- Link view -->
  <xsl:template 
    name="CT_Journal_Link" 
    match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.Journal']"
    mode="link_view">
    <!-- Simply call template for Publications , because there is 
	 no difference for list view between these
         two types -->
    <xsl:call-template name="CT_Publication_Link"/>
  </xsl:template>

</xsl:stylesheet>
