<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
     Copyright 2010, 2011 Jens Pelzetter

         
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
  version="1.0">

  <!--
      *********************************************************************
      ** XSL Templates for parsing the format definitions for displaying **
      ** publications in lists.                                          **
      *********************************************************************
  -->

  <!-- Process the format for an article in a collected volume -->
  <xsl:template name="bibrefArticleInCollectedVolume"
		match="bibrefArticleInCollectedVolumeFormat">   
    <!-- Parameters for publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="chapter"/>
    <xsl:param name="collectedVolume"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="pagesFrom"/>
    <xsl:param name="pagesTo"/>   
    <xsl:param name="reviewed"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="year"/>
    <!-- Technical parameters -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- Publication data -->
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="chapter" select="$chapter"/>
      <xsl:with-param name="collectedVolume" select="$collectedVolume"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="pagesFrom" select="$pagesFrom"/>
      <xsl:with-param name="pagesTo" select="$pagesTo"/>
      <xsl:with-param name="reviewed" select="$reviewed"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="year" select="$year"/>
      <!-- Technical data -->
      <xsl:with-param name="oid" select="$oid"/>      
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>       
  </xsl:template>

  <!-- Process the format for an article in a journal -->
  <xsl:template name="bibrefArticleInJournal" 
		match="bibrefArticleInJournalFormat"> 
    <!-- Publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="issue"/>
    <xsl:param name="journal"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="pagesFrom"/>
    <xsl:param name="pagesTo"/>
    <xsl:param name="publicationDate"/>
    <xsl:param name="reviewed"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="volume"/>
    <xsl:param name="year"/>
    <!-- Technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- Publication data -->
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="issue" select="$issue"/>
      <xsl:with-param name="journal" select="$journal"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="pagesFrom" select="$pagesFrom"/>
      <xsl:with-param name="pagesTo" select="$pagesTo"/>  
      <xsl:with-param name="publicationDate" select="$publicationDate"/>
      <xsl:with-param name="reviewed" select="$reviewed"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="volume" select="$volume"/>
      <xsl:with-param name="year" select="$year"/>
      <!-- Technical data -->
      <xsl:with-param name="oid" select="$oid"/>
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- Format for a collected volume -->
  <xsl:template name="bibrefCollectedVolume"
		match="bibrefCollectedVolumeFormat">
    <!-- publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="edition"/>
    <xsl:param name="isbn"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="numberOfPages"/>
    <xsl:param name="numberOfVolumes"/>
    <xsl:param name="publisher"/>
    <xsl:param name="reviewed"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="volume"/>
    <xsl:param name="year" select="''"/>
    <!-- Technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- publication data -->
      <xsl:with-param name="authors" select="$authors"/> 
      <xsl:with-param name="edition" select="$edition"/>
      <xsl:with-param name="isbn" select="$isbn"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="numberOfVolumes" select="$numberOfVolumes"/>
      <xsl:with-param name="numberOfPages" select="$numberOfVolumes"/>
      <xsl:with-param name="publisher" select="$publisher"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="reviewed" select="$reviewed"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="volume" select="$volume"/>
      <xsl:with-param name="year" select="$year"/>
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid"/>
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>    
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="bibrefExpertise"
		match="bibrefExpertiseFormat">
    <!-- publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="numberOfPages"/>
    <xsl:param name="place"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="year"/>        
    <!-- technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- publication data -->
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="numberOfPages" select="$numberOfPages"/>
      <xsl:with-param name="place" select="$place"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="year" select="$year"/>              
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid"/>
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="bibrefGreyLiterature"
		match="bibrefGreyLiteratureFormat">
    <!-- publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="number"/>
    <xsl:param name="numberOfPages"/>
    <xsl:param name="organization"/>
    <xsl:param name="pagesFrom"/>
    <xsl:param name="pagesTo"/>
    <xsl:param name="place"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="year"/>        
    <!-- technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="number" select="$number"/>
      <xsl:with-param name="numberOfPages" select="$numberOfPages"/>
      <xsl:with-param name="pagesFrom" select="$pagesFrom"/>
      <xsl:with-param name="pagesTo" select="$pagesTo"/>
      <xsl:with-param name="place" select="$place"/>
      <xsl:with-param name="organization" select="$organization"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="year" select="$year"/>              
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid"/>
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- Process the format for an in proceedings publication -->
  <xsl:template name="bibrefInProceedings"
		match="bibrefInProceedingsFormat">
    <!-- publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="pagesFrom"/>
    <xsl:param name="pagesTo"/>
    <xsl:param name="proceedings"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="year"/>    
    <!-- Technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- publication data -->
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="pagesFrom" select="$pagesFrom"/>
      <xsl:with-param name="pagesTo" select="$pagesTo"/>
      <xsl:with-param name="proceedings" select="$proceedings"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="year" select="$year"/>
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid"/>
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="bibrefInternetArticle"
		match="bibrefInternetArticleFormat">
    <!-- publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="doi"/>
    <xsl:param name="edition"/>
    <xsl:param name="issn"/>
    <xsl:param name="lastAccessed"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="number"/>
    <xsl:param name="numberOfPages"/>
    <xsl:param name="organization"/>
    <xsl:param name="place"/>
    <xsl:param name="publicationDate"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="url"/>
    <xsl:param name="urn"/>
    <xsl:param name="year"/>
    <!-- technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates> 
      <!-- publication data -->
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="doi" select="$doi"/>
      <xsl:with-param name="edition" select="$edition"/>
      <xsl:with-param name="issn" select="$edition"/>
      <xsl:with-param name="lastAccessed" select="$lastAccessed"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="number" select="$number"/>
      <xsl:with-param name="numberOfPages" select="$numberOfPages"/>
      <xsl:with-param name="organization" select="$organization"/>
      <xsl:with-param name="place" select="$place"/>
      <xsl:with-param name="publicationDate" select="$publicationDate"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="url" select="$url"/>
      <xsl:with-param name="urn" select="$urn"/>
      <xsl:with-param name="year" select="$year"/>
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid" />
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- Process the format definition for a journal -->
  <xsl:template name="bibrefJournal"
		match="bibrefJournalFormat">
    <!-- publication data -->
    <xsl:param name="issn"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="reviewed"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <!-- technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- publication data -->
      <xsl:with-param name="issn" select="$issn"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="reviewed" select="$reviewed"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid" />
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="bibrefMonograph"
		match="bibrefMonographFormat">
    <!-- publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="edition"/>
    <xsl:param name="isbn"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="numberOfPages"/>
    <xsl:param name="numberOfVolumes"/>
    <xsl:param name="publisher"/>
    <xsl:param name="reviewed"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>  
    <xsl:param name="volume"/>
    <xsl:param name="year"/>  
    <!-- technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- publication data -->
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="edition" select="$edition"/>
      <xsl:with-param name="isbn" select="$isbn"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="numberOfPages" select="$numberOfPages"/>
      <xsl:with-param name="numberOfVolumes" select="$numberOfVolumes"/>
      <xsl:with-param name="publisher" select="$publisher"/>
      <xsl:with-param name="reviewed" select="$reviewed"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>  
      <xsl:with-param name="volume" select="$volume"/>
      <xsl:with-param name="year" select="$year"/>  
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid"/>
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>      
    </xsl:apply-templates>
  </xsl:template>

  <!-- Process the format definition for proceedings -->
  <xsl:template name="bibrefProceedings"
		match="bibrefProceedingsFormat">
    <!-- Publication data -->
    <xsl:param name="dateFromOfConference"/>
    <xsl:param name="dateToOfConference"/> 
    <xsl:param name="isbn"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="nameOfConference"/>
    <xsl:param name="numberOfPages"/>
    <xsl:param name="numberOfVolumes"/>
    <xsl:param name="organizerOfConference"/>
    <xsl:param name="placeOfConference"/>
    <xsl:param name="publisher"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="volume"/>
    <xsl:param name="year"/>
    <!-- technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- publication data -->
      <xsl:with-param name="dateFromOfConference" select="$dateFromOfConference"/>
      <xsl:with-param name="dateToOfConference" select="$dateToOfConference"/>
      <xsl:with-param name="isbn" select="$isbn"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="nameOfConference" select="$nameOfConference"/>
      <xsl:with-param name="numberOfVolumes" select="$numberOfVolumes"/>
      <xsl:with-param name="numberOfPages" select="$numberOfPages"/>
      <xsl:with-param name="organizerOfConference" select="$organizerOfConference"/>
      <xsl:with-param name="placeOfConference" select="$placeOfConference"/>
      <xsl:with-param name="publisher" select="$publisher"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="volume" select="$volume"/>
      <xsl:with-param name="year" select="$year"/>
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid" />
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- Process the format definition for reviews -->
  <xsl:template name="bibrefReview" 
		match="bibrefReviewFormat"> 
    <!-- publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="issue"/>
    <xsl:param name="journal"/>
    <xsl:param name="misc"/>
    <xsl:param name="pagesFrom"/>
    <xsl:param name="pagesTo"/>
    <xsl:param name="publicationDate"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="volume"/>
    <xsl:param name="year"/>
    <!-- technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>

    <xsl:apply-templates>
      <!-- publication data -->
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="issue" select="$issue"/>
      <xsl:with-param name="journal" select="$journal"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="pagesFrom" select="$pagesFrom"/>
      <xsl:with-param name="pagesTo" select="$pagesTo"/>  
      <xsl:with-param name="publicationDate" select="$publicationDate"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="volume" select="$volume"/>
      <xsl:with-param name="year" select="$year"/>
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid"/>
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- Process format definition for working papers-->
  <xsl:template name="bibrefWorkingPaper"
		match="bibrefWorkingPaperFormat">
    <!-- publication data -->
    <xsl:param name="authors"/>
    <xsl:param name="misc"/>
    <xsl:param name="number"/>
    <xsl:param name="numberOfPages"/>
    <xsl:param name="organization"/>
    <xsl:param name="place"/>
    <xsl:param name="series"/>
    <xsl:param name="title"/>
    <xsl:param name="year"/>
    <!-- technical data -->
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:apply-templates>
      <!-- publication data -->
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="misc" select="$misc"/>
      <xsl:with-param name="number" select="$number"/>
      <xsl:with-param name="numberOfPages" select="$numberOfPages"/>
      <xsl:with-param name="organization" select="$organization"/>
      <xsl:with-param name="place" select="$place"/>
      <xsl:with-param name="series" select="$series"/>
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="year" select="$year"/>
      <!-- technical data -->
      <xsl:with-param name="oid" select="$oid"/>
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- Format substiutions -->
  <!-- 
       Outputs the authors of the publication (preprocessed).
  -->
  <xsl:template match="bibrefFormat//authors">
    <xsl:param name="authors"/>    
    <xsl:choose>
      <xsl:when test="string-length($authors) &gt; 0">      
	<span class="publicationAuthorInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <span class="publicationAuthors"><xsl:copy-of select="$authors"/></span>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">	 
	  <span class="publicationAuthorInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Processes the chapter info -->
  <xsl:template match="bibrefFormat/bibrefArticleInCollectedVolumeFormat/chapter | bibrefFormat/structDefs//chapter">
    <xsl:param name="chapter"/>
    <xsl:choose>
      <xsl:when test="string-length($chapter &gt; 0)">
	<span class="publicationChapterInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationChapter">
	    <xsl:value-of select="$chapter"/>
	  </span>	  
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>		  
	  <xsl:if test="./@after">
	    <span class="after">
	      <xsl:value-of select="./@after"/>
	    </span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationChapterInfo">
	    <span class="ifEmpty">
	      <xsl:value-of select="./@ifEmpty"/>
	    </span>
	  </span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Insert a character in the processed format -->
  <xsl:template match="bibrefFormat//char">
    <xsl:value-of select="./@value"/>
  </xsl:template>

  <!-- Output the collected volume of an article in a collected volume (preprocessed) -->
  <xsl:template match="bibrefFormat/bibrefArticleInCollectedVolumeFormat/collectedVolume | bibrefFormat/structDefs//collectedVolume">
    <xsl:param name="collectedVolume"/>
    <xsl:choose>
      <xsl:when test="string-length(normalize-space($collectedVolume)) &gt; 1">
	<span class="publicationCollectedVolumeInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationCollectedVolume"><xsl:copy-of select="$collectedVolume"/></span>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>	      
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>	
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationCollectedVolumeInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Process the date of the conference -->
  <xsl:template match="bibrefFormat/bibrefProeccedingsFormat/dateOfConference | bibrefFormat/structDefs/dateOfConference">
    <xsl:param name="dateFromOfConference"/>
    <xsl:param name="dateToOfConference"/>    
    <xsl:choose>
      <xsl:when test="string-length($dateFromOfConference) &gt; 0">
	<span class="publicationProceedingsDateOfConference">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>     
	  <span class="publicationInProceedingsDateFromOfConference">
	    <xsl:value-of select="$dateFromOfConference"/>
	  </span>
	  <xsl:if test="string-length($dateToOfConference) &gt; 0">	   
	    <xsl:if test="./@betweenText">
	      <span class="betweenText">
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'SciPublications'"/>
		  <xsl:with-param name="id" select="./@betweenText"/>
		</xsl:call-template>
	      </span>
	    </xsl:if>	    
	    <span class="publicationInProceedingsDateToOfConference">
	      <xsl:value-of select="$dateToOfConference"/>
	    </span>
	  </xsl:if>      
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		  <xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>
	    </xsl:if>      
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationProceedingsDateOfConference"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="bibrefFormat/bibrefInternetArticleFormat/doi | bibrefFormat/structDefs//doi">
    <xsl:param name="doi"/>
    <xsl:choose>
      <xsl:when test="string-length($doi) &gt; 0">
	<span class="publicationDoiInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>     
	  <a>
	    <xsl:attribute name="href"><xsl:value-of select="$doi"/></xsl:attribute>
	    <span class="publicationDoi"><xsl:value-of select="$doi"/></span>
	  </a>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>      
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>	    
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationDoiInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
      Proceses the edition of a publication
  -->
  <xsl:template match="bibrefFormat//edition">
    <xsl:param name="edition"/>
    <xsl:choose>
      <xsl:when test="string-length($edition) &gt; 0">
	<span class="publicationEditionInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationEdition"><xsl:value-of select="$edition"/></span>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>	  
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationEditionInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Proceeses the last accessed value of a internet article -->
  <xsl:template match="bibrefFormat/bibrefInternetArticleFormat/lastAccessed | bibrefFormat/structDefs/lastAccessed">
    <xsl:param name="lastAccessed"/>
    <xsl:choose>
      <xsl:when test="string-length($lastAccessed) &gt; 0">
	<span class="lastAccessedInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="lastAccessed"><xsl:value-of select="$lastAccessed"/></span>
	  <xsl:if test="./@postText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="lastAccessedInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
      Outputs the ISBN of the publication, if present.
  -->
  <xsl:template match="bibrefFormat//isbn">
    <xsl:param name="isbn"/>
    <xsl:choose>
      <xsl:when test="string-length($isbn) &gt; 0">   
	<span class="publicationIsbnInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">	  
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationIsbn"><xsl:value-of select="$isbn"/></span>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationIsbnInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
      Outputs the ISSN of the publication, if present.
  -->
  <xsl:template match="bibrefFormat//issn">
    <xsl:param name="issn"/>
    <xsl:choose>
      <xsl:when test="string-length($issn) &gt; 0">    
	<span class="publicationIssnInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationIssn"><xsl:value-of select="$issn"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationIssnInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Output the issue of an article in journal or review -->
  <xsl:template match="bibrefFormat/bibrefArticleInJournalFormat/issue | bibrefFormat/bibrefReviewFormat/issue | bibrefFormat/structDefs/issue">    
    <xsl:param name="issue"/>
    <xsl:choose>
      <xsl:when test="string-length($issue) &gt; 0">
	<span class="publicationIssueInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationIssue"><xsl:value-of select="$issue"/></span>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>	    
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>	  
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationIssueInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>      
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
      Outputs the name of the journal
  -->
  <xsl:template match="bibrefFormat/bibrefArticleInJournalFormat/journal | bibrefFormat/bibrefReviewFormat/journal | bibrefFormat/structDefs//journal">
    <xsl:param name="journal"/>
    <xsl:choose>
      <!-- We use 1 here because if the string contains empty spaces normalize-space will keep one -->
      <xsl:when test="string-length(normalize-space($journal)) &gt; 1">
	<span class="publicationJournalInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationJournal"><xsl:value-of select="$journal"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationJournalInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Output misc information -->
  <xsl:template match="bibrefFormat//misc">
    <xsl:param name="misc"/>    
    <xsl:choose>      
      <xsl:when test="string-length(normalize-space($misc)) &gt; 1">	
	<span class="publicationMiscInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationMisc"><xsl:value-of select="normalize-space($misc)"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationMiscInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>
  </xsl:template>

  <!-- Process the name of a conference -->
  <xsl:template match="bibrefFormat/bibrefProceedingsFormat/nameOfConference | bibrefFormat/structDefs//nameOfConference">
    <xsl:param name="nameOfConference"/>
    <xsl:choose>
      <xsl:when test="string-length($nameOfConference) &gt; 0">
	<span class="publicationProceedingsNameOfConferenceInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>	  
	  <xsl:if test="./@preText">
	    <span class="preText">
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="./@preText"/>
	    </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationProceedingsNameOfConference">
	    <xsl:value-of select="$nameOfConference"/>
	  </span>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>      
	</span>
	<xsl:if test="./@after">
	  <span class="after"><xsl:value-of select="./@after"/></span>
	</xsl:if>	
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationProceedingsNameOfConferenceInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Output number information -->
  <xsl:template match="bibrefFormat//number">
    <xsl:param name="number"/>    
    
    <xsl:choose>      
      <xsl:when test="string-length($number) &gt; 0">	
	<span class="publicationNumberInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	    </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationNumber"><xsl:value-of select="normalize-space($number)"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationNumberInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>
  </xsl:template>

  <!--
      Processes the number of pages
  -->
  <xsl:template match="bibrefFormat//numberOfPages">
    <xsl:param name="numberOfPages"/>
    <xsl:choose>
      <xsl:when test="string-length($numberOfPages) &gt; 0">
	<span class="publicationNumberOfPagesInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>		  
	  <span class="publicationNumberOfPages"><xsl:value-of select="normalize-space($numberOfPages)"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="./@postText"/>
	    </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>	
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationNumberOfPagesInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>	
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
      Processes the number of volumes
  -->
  <xsl:template match="bibrefFormat//numberOfVolumes">
    <xsl:param name="numberOfVolumes"/>
    <xsl:choose>
      <xsl:when test="string-length($numberOfVolumes) &gt; 0">
	<span class="publicationNumberOfVolumesInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	    </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationNumberOfVolumes"><xsl:value-of select="$numberOfVolumes"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationNumberOfVolumesInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Procsses the organization info -->
  <xsl:template match="bibrefFormat//organization">
    <xsl:param name="organization"/>
    <xsl:choose>
      <xsl:when test="string-length($organization) &gt; 0">
	<span class="publicationOrganizationInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	    </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationOrganization"><xsl:value-of select="$organization"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationOrganizationInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>   
  </xsl:template>

  <!-- Process the organizer of a conference-->
  <xsl:template match="bibrefFormat/bibrefProceedingsFormat/organizer | bibrefFormat/structDefs//organizer">
    <xsl:param name="organizerOfConference"/>
    <xsl:choose>
      <xsl:when test="string-length($organizerOfConference) &gt; 0">
	<span class="publicationProceedingsOrganizerOfConferenceInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	    <xsl:with-param name="id" select="./@preText"/>
	    </xsl:call-template>
	  </xsl:if>	  
	  <span class="publicationInProceedingsOrganizerOfConference"><xsl:value-of select="$organizerOfConference"/></span>	  
	  <xsl:if test="./@postText">
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="./@postText"/>
	    </xsl:call-template>
	  </xsl:if>      
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationOrganizerOfConferenceInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Processes the page from to information, if present -->
  <xsl:template match="bibrefFormat//pages">
    <xsl:param name="pagesFrom"/>
    <xsl:param name="pagesTo"/>
    <xsl:choose>
      <xsl:when test="(string-length($pagesFrom) &gt; 0)">
	<span class="publicationPagesInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>	    
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationPagesFrom"><xsl:value-of select="normalize-space($pagesFrom)"/></span>
	  <xsl:if test="string-length($pagesTo) &gt; 0">
	    <xsl:if test="./@betweenText">
	      <span class="betweenText">
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'SciPublications'"/>
		  <xsl:with-param name="id" select="./@betweenText"/>
		</xsl:call-template>
	      </span>
	    </xsl:if>
	    <span class="publicationPagesTo"><xsl:value-of select="normalize-space($pagesTo)"/></span>	
	  </xsl:if>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationPagesInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Procsses the place of publication -->
  <xsl:template match="bibrefFormat//place">
    <xsl:param name="place"/>
    <xsl:choose>
      <xsl:when test="string-length($place) &gt; 0">
	<span class="publicationPlaceInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationPlace"><xsl:value-of select="$place"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationPlaceInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>
  </xsl:template>

  <!-- Outputs the place of the conference -->
  <xsl:template match="bibrefFormat/bibrefProcceedingsFormat/placeOfConference | bibrefFormat/structDefs//placeOfConference">
    <xsl:param name="placeOfConference"/>
    <xsl:choose>
      <xsl:when test="string-length($placeOfConference) &gt; 0">
	<span class="publicationProceedingsPlaceOfConferenceInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	    </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationProceedingsPlaceOfConference"><xsl:value-of select="$placeOfConference"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationProceedingsPlaceOfConferenceInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>    
  </xsl:template>

  <!-- Outputs the procceedings part of a in proccedings publications -->
  <xsl:template match="bibrefFormat/bibrefInProceedingsFormat/proceedings | bibrefFormat/structDefs/proceedings">
    <xsl:param name="proceedings"/>
    <xsl:choose>
      <xsl:when test="string-length($proceedings) &gt; 0">
	<span class="publicationProceedingsInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="./@preText"/>
	    </xsl:call-template>
	  </xsl:if>
	  <span class="publicationProceedings"><xsl:value-of select="$proceedings"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationProceedingsInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Output the date of publication of a publication -->
  <xsl:template match="bibrefFormat//publicationDate">
    <xsl:param name="publicationDate"/>
    <xsl:choose>
      <xsl:when test="string-length($publicationDate) &gt; 0">
	<span class="publicationDateInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	    </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationDate"><xsl:value-of select="$publicationDate"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationDateInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>
  </xsl:template>

  <!--
      Outputs the publisher of the publication.
  -->
  <xsl:template match="bibrefFormat//publisher">
    <xsl:param name="publisher"/>
    <xsl:choose>
      <xsl:when test="string-length($publisher) &gt; 0">
	<span class="publicationPublisherInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationPublisher"><xsl:value-of select="normalize-space($publisher)"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
    </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationMiscInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>
  </xsl:template>

  <!-- 
       Outputs if a publication is reviewed or not. This tag is a bit special since it replaces the value provided
       with a text and does not output it. If the parameter reviewed is 'true', then the localized text
       identified by the id provided with the true attribute is printed, else the localized value of
       the false attribute. If the variable is empty, the ifEmpty attribute is used
  -->
  <xsl:template match="bibrefFormat//reviewed">
    <xsl:param name="reviewed"/>
    <xsl:choose>
      <xsl:when test="string-length($reviewed) &gt; 0">
	<span class="publicationReviewedInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <span class="publicationReviewed">
	    <xsl:choose>
	      <xsl:when test="$reviewed = 'true'">
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'SciPublications'"/>
		  <xsl:with-param name="id" select="./@true"/>
		</xsl:call-template>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'SciPublications'"/>
		  <xsl:with-param name="id" select="./@false"/>
		</xsl:call-template>
	      </xsl:otherwise>
	    </xsl:choose>
	  </span>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationReviewedInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>
  </xsl:template>

  <!-- Output the preproceessed series information, if any -->
  <xsl:template match="bibrefFormat//series">
    <xsl:param name="series"/>
    <xsl:choose>
      <xsl:when test="string-length($series)&gt; 0">
	<span class="publicationSeriesInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationSeries"><xsl:value-of select="$series"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>

      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationSerieseInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>
  </xsl:template>

  <!--
      Outputs the title of the publication. Note: The value of the after attribute is only printed if
      the last character of the title is *not* a '.', a '!' or a '?'.
  -->
  <xsl:template match="bibrefFormat//title">
    <xsl:param name="title"/>
    <xsl:param name="oid"/>
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:variable name="setDetailLink">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications_PublicationList'"/>
	<xsl:with-param name="setting" select="'setDetailLink'"/>
	<xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>   

    <xsl:variable name="titleLastChar">
      <xsl:value-of select="substring($title, string-length($title))"/>
    </xsl:variable>

    <span class="publicationTitleInfo">
      <xsl:if test="./@before">
	<span class="before"><xsl:value-of select="./@before"/></span>
      </xsl:if>
            
      <xsl:choose>
	<xsl:when test="$setDetailLink = 'true'">
	  <span class="publicationName">
	    <a>
	      <xsl:choose>
		<xsl:when test="$useRelativeUrl = 'true'">
		  <xsl:attribute name="href"><xsl:value-of select="$oid"/></xsl:attribute>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:attribute name="href">/redirect/?oid=<xsl:value-of select="$oid"/></xsl:attribute>
		</xsl:otherwise>
	      </xsl:choose>
	      <xsl:choose>
		<xsl:when test="./@highlight = 'true'">
		  <em><xsl:value-of select="$title"/></em>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="$title"/>
		</xsl:otherwise>
	      </xsl:choose>
	    </a>
	</span>
	</xsl:when>
	<xsl:otherwise>
	  <span class="publicationTitle">
	    <xsl:choose>
	      <xsl:when test="./@highlight = 'true'">
		<em><xsl:value-of select="$title"/></em>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:value-of select="$title"/>
	      </xsl:otherwise>
	    </xsl:choose>
	  </span>
	</xsl:otherwise>
      </xsl:choose>

      <xsl:if test="($titleLastChar != '.') and ($titleLastChar != '!') and titleLastChar != '?'">
	<xsl:value-of select="./@after"/>
      </xsl:if>
    </span>

  </xsl:template>

  <!--
      Processes the URL of an publication, if present.
  -->
  <xsl:template match="bibrefFormat/bibrefInternetArticleFormat/url | bibrefFormat/structDefs//url">
    <xsl:param name="url" />
    <xsl:choose>
      <xsl:when test="string-length($url) &gt; 0">
	<span class="publicationUrlInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>     
	  <a>
	    <xsl:attribute name="href"><xsl:value-of select="$url"/></xsl:attribute>
	    <xsl:value-of select="$url"/>
	  </a>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>      
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>	    
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationUrlInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
      Processes the URN of an publication, if present.
  -->
  <xsl:template match="bibrefFormat/bibrefInternetArticleFormat/urn | bibrefFormat/structDefs//urn">
    <xsl:param name="urn" />
    <xsl:choose>
      <xsl:when test="string-length($urn) &gt; 0">
	<span class="publicationUrnInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>     
	  <a>
	    <xsl:attribute name="href"><xsl:value-of select="$urn"/></xsl:attribute>
	    <xsl:value-of select="$urn"/>
	  </a>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>      
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>	    
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationUrnInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
      Outputs the volume of an publication.
  -->
  <xsl:template match="bibrefFormat//volume">
    <xsl:param name="volume"/>
    <xsl:choose>
      <xsl:when test="string-length($volume) &gt; 0">
	<span class="publicationVolumeInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>	  
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <span class="publicationVolume"><xsl:value-of select="$volume"/></span>
	  <xsl:if test="./@postText">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="./@postText"/>	  
	      </xsl:call-template>
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>	  
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationVolumeInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>      
    </xsl:choose>
  </xsl:template>

  <!--
      Outputs the year of publication of the publication.
  -->
  <xsl:template match="bibrefFormat//year">
    <xsl:param name="year"/>
    <xsl:choose>
      <xsl:when test="string-length($year) &gt; 0">
	<span class="publicationYearInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationYear"><xsl:value-of select="$year"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationYearInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  
  <!-- Templates for preprocessed fields, like authors or a publisher -->
  <!--
      Entry point for processing an author of a publication. 
  -->
  <xsl:template name="bibrefAuthor" 
		match="bibrefAuthorFormat">
    <xsl:param name="surname"/>
    <xsl:param name="givenName"/>
    <xsl:param name="isEditor"/>
    <xsl:param name="authorsCount"/>
    <xsl:param name="position"/>    

    <!-- 
	 If this is not the first author, put the definied separator before
	 the author
    -->
    <xsl:if test="($authorsCount &gt; 1) and ($position &gt; 1)">
      <xsl:choose>
        <xsl:when test="./@separatorKey">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'SciPublications'"/>
            <xsl:with-param name="id" select="./@separatorKey"/>
          </xsl:call-template>
        </xsl:when>        
        <xsl:otherwise>
	  <span><xsl:value-of select="./@separator"/></span>
        </xsl:otherwise>        
      </xsl:choose>
    </xsl:if>
    
    <!-- 
	 Put the author string into an variable. This is necessary, because
	 apply-templates creates a line break, which causes an space in the
	 browser. To avoid this, the author string is put into a variable,
	 which is put into the HTML output using value-of and the 
	 XPath function normalize-space, which removes spaces from the
	 begeginning and the end of its argument.
    -->
    <xsl:variable name="author">
      <span class="publicationAuthor">
	<!-- Call the subsequent templates for surname, given name and editor -->
	<xsl:apply-templates>
	  <xsl:with-param name="surname" select="$surname"/>
	  <xsl:with-param name="givenName" select="$givenName"/>      
	  <xsl:with-param name="isEditor" select="$isEditor"/>
	</xsl:apply-templates>
      </span>
    </xsl:variable>
    <xsl:value-of select="normalize-space($author)"/>
  </xsl:template>

  <!-- Processes the surname of an author -->
  <xsl:template match="bibrefAuthorFormat/surname">
    <xsl:param name="surname"/>   
    <xsl:if test="string-length($surname) &gt; 0">
      <span class="publicationAuthorSurnameInfo">      
	<xsl:if test="./@before">
	  <span class="before"><xsl:value-of select="./@before"/></span>
	</xsl:if>
	<span class="publicationAuthorSurname">
	  <xsl:value-of select="$surname"/>
	</span>
	<xsl:if test="./@after">
	  <span class="after"><xsl:value-of select="./@after"/></span>
	</xsl:if>
      </span>
    </xsl:if>       
  </xsl:template>

  <!-- 
       Processes the given name of an author. If the initialsOnly attribute 
       is the "true" in the format definition, only the first letter of the name
       is shown.
  -->
  <xsl:template match="bibrefAuthorFormat/givenName">
    <xsl:param name="givenName"/>
      <xsl:if test="string-length($givenName) &gt; 0">
	<span class="publicationAuthorGivenNameInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>	  
	  <span class="publicationAuthorGivenName">
	    <xsl:choose>
	      <xsl:when test="./@initialsOnly='true'">
		<xsl:value-of select="substring($givenName, 1, 2)"/>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:value-of select="$givenName"/>
	      </xsl:otherwise>
	    </xsl:choose>
	  </span>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>	  
	</span>
      </xsl:if>
  </xsl:template>

  <!-- 
       Proceeses the editor tag, which is replaced with the text for editor, e.g. (Hrsg.) 
       for authors which are editors.
  -->
  <xsl:template match="bibrefAuthorFormat/editor">
    <xsl:param name="isEditor"/>    
    <xsl:if test="$isEditor = 'true'">
      <span class="publicationAuthorEditorInfo">
	<xsl:if test="./@before">
	  <span class="before"><xsl:value-of select="./@before"/></span>
	</xsl:if>	  
	<span class="publicationAuthorEditor">
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'SciPublications'"/>
	    <xsl:with-param name="id" select="./@text"/>        
	  </xsl:call-template>
	</span>
	<xsl:if test="./@after">
	  <span class="after"><xsl:value-of select="./@after"/></span>
	</xsl:if>	  
      </span>
    </xsl:if>
  </xsl:template>

  <!-- 
       ***************************************************************
       ** Entry point for processing the publisher of a publication **
       ***************************************************************
  -->
  <xsl:template match="bibrefPublisherFormat">
    <xsl:param name="name"/>
    <xsl:param name="place"/>
    <xsl:apply-templates>
	<xsl:with-param name="name" select="$name"/>
	<xsl:with-param name="place" select="$place"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- Outputs the name of the publisher -->
  <xsl:template match="bibrefPublisherFormat/name">
    <xsl:param name="name"/>
    <xsl:if test="string-length($name) &gt; 0">
      <span class="publicationPublisherNameInfo">
	<xsl:if test="./@before">
	  <span class="before"><xsl:value-of select="./@before"/></span>
	</xsl:if>
	<span class="publicationPublisherName"><xsl:value-of select="$name"/></span>
	<xsl:if test="./@after">
	  <span class="after"><xsl:value-of select="./@after"/></span>
	</xsl:if>
      </span>
    </xsl:if>
  </xsl:template>

  <!-- Outputs the place of a publisher -->
  <xsl:template match="bibrefPublisherFormat/place">
    <xsl:param name="place"/>
    <xsl:if test="string-length($place)">
      <span class="publisherPlaceInfo">
	<xsl:if test="./@before">
	  <span class="before"><xsl:value-of select="./@before"/></span>
	</xsl:if>	
	<span class="publisherPlace"><xsl:value-of select="$place"/></span>
	<xsl:if test="./@after">
	  <span class="after"><xsl:value-of select="./@after"/></span>
	</xsl:if>
      </span>
  </xsl:if></xsl:template>


  <!-- Entry point for processing a in series information -->
<!--  <xsl:template name="inSeries" match="bibrefFormat/bibrefInSeriesFormat">
    <xsl:param name="editor"/>
    <xsl:param name="title"/>
    <xsl:param name="volume"/>

    <span class="publicationInSeries">
      <xsl:if test="./preText">
	<span class="preText">
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'SciPublications'"/>
	    <xsl:with-param name="id" select="@preText"/>
	  </xsl:call-template>
	</span>
      </xsl:if>
      
      <xsl:apply-templates>
	<xsl:with-param name="editor" select="$editor"/>
	<xsl:with-param name="title" select="$title"/>
	<xsl:with-param name="volume" select="$volume"/>
      </xsl:apply-templates>
      
      <xsl:if test="./@postText">
	<span class="postText">
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'SciPublications'"/>
	    <xsl:with-param name="id" select="'@postText'"/>
	  </xsl:call-template>
	</span>
      </xsl:if>
    </span>
  </xsl:template>-->

  <!-- Entry point for processing the editor of a series -->
<!--  <xsl:template name="bibrefSeriesEditor" 
		match="bibrefFormat/bibrefSeriesEditorFormat">
    <xsl:param name="surname"/>
    <xsl:param name="givenName"/>
    
    <span class="publicationSeriesEditorInfo">
      <xsl:if test="./preText = 'true'">
	<xsl:call-template name="mandalay:getStaticText">
	  <xsl:with-param name="module" select="'SciPublications'"/>
	  <xsl:with-param name="id" select="'@preText'"/>
	</xsl:call-template>
      </xsl:if>
      
      <span class="publicationSeriesEditor">
	<xsl:apply-templates>
	  <xsl:with-param name="surname" select="$surname"/>
	  <xsl:with-param name="givenName" select="$givenName"/>      
	</xsl:apply-templates>
      </span>

      <xsl:if test="./@postTex">
	<xsl:call-template name="mandalay:getStaticText">
	  <xsl:with-param name="module" select="'SciPublications'"/>
	  <xsl:with-param name="id" select="@postText"/>
	</xsl:call-template>
      </xsl:if>         
    </span>
    <xsl:if test="(position() &gt; 2) and (position() != last())">
      <xsl:value-of select="./@separator"/>
    </xsl:if>
  </xsl:template>-->

  <!-- Processes the surname of an editor of a series -->
<!--  <xsl:template match="bibrefInSeriesEditorFormat/surname">
    <xsl:param name="surname"/>   
    <xsl:if test="string-length($surname) &gt; 0">
      <span class="publicationAuthorSurnameInfo">      
	<xsl:if test="./@before">
	  <span class="before"><xsl:value-of select="./@before"/></span>
	</xsl:if>
	<span class="publicationAuthorSurname">
	  <xsl:value-of select="$surname"/>
	</span>
	<xsl:if test="./@after">
	  <span class="after"><xsl:value-of select="./@after"/></span>
	</xsl:if>
      </span>
    </xsl:if>       
  </xsl:template>-->

  <!-- Processes the given name of an editor of a series -->
<!--  <xsl:template match="bibrefInSeriesEditorFormat/givenName">
    <xsl:param name="givenName"/>
      <xsl:if test="string-length($givenName) &gt; 0">
	<span class="publicationAuthorGivenNameInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>	  
	  <span class="publicationAuthorGivenName">
	    <xsl:choose>
	      <xsl:when test="./@initialsOnly='true'">
		<xsl:value-of select="substring($givenName, 1, 2)"/>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:value-of select="$givenName"/>
	      </xsl:otherwise>
	    </xsl:choose>
	  </span>
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>	  
	</span>
      </xsl:if>
  </xsl:template>-->

  <!-- Proceess structs -->
  <xsl:template match="bibrefFormat//struct">
    <xsl:param name="authors" select="''"/>
    <xsl:param name="chapter" select="''"/>
    <xsl:param name="collectedVolume" select="''"/>
    <xsl:param name="dateFromOfConference" select="''"/>
    <xsl:param name="dateToOfConference" select="''"/>
    <xsl:param name="doi" select="''"/>
    <xsl:param name="edition" select="''"/>
    <xsl:param name="isbn" select="''"/>
    <xsl:param name="issn" select="''"/>
    <xsl:param name="issue" select="''"/>
    <xsl:param name="journal" select="''"/>
    <xsl:param name="lastAccessed" select="''"/>
    <xsl:param name="misc" select="''"/>
    <xsl:param name="nameOfConference" select="''"/>
    <xsl:param name="number" select="''"/>
    <xsl:param name="numberOfPages" select="''"/>
    <xsl:param name="numberOfVolumes" select="''"/>
    <xsl:param name="organization" select="''"/>
    <xsl:param name="organizerOfConference" select="''"/>
    <xsl:param name="pagesFrom" select="''"/>
    <xsl:param name="pagesTo" select="''"/>
    <xsl:param name="place" select="''"/>
    <xsl:param name="placeOfConference" select="''"/>
    <xsl:param name="proceedings" select="''"/>
    <xsl:param name="publicationDate" select="''"/>
    <xsl:param name="publisher" select="''"/>
    <xsl:param name="reviewed" select="''"/>
    <xsl:param name="series" select="''"/>
    <xsl:param name="title" select="''"/>
    <xsl:param name="url" select="''"/>
    <xsl:param name="urn" select="''"/>
    <xsl:param name="volume" select="''"/>
    <xsl:param name="year" select="''"/>
    
    <xsl:variable name="structName" select="./@name"/>

    <xsl:variable name="structData">
      <xsl:apply-templates select="../../structDefs/structDef[@name=$structName]/*">   
	<xsl:with-param name="authors" select="$authors"/>
	<xsl:with-param name="chapter" select="$chapter"/>
	<xsl:with-param name="collectedVolume" select="$collectedVolume"/>
	<xsl:with-param name="dateFromOfConference" select="$dateToOfConference"/>
	<xsl:with-param name="dateToOfConference" select="$dateToOfConference"/>
	<xsl:with-param name="doi" select="$doi"/>
	<xsl:with-param name="edition" select="$edition"/>
	<xsl:with-param name="isbn" select="$isbn"/>
	<xsl:with-param name="issn" select="$issn"/>
	<xsl:with-param name="issue" select="$issue"/>
	<xsl:with-param name="journal" select="$journal"/>
	<xsl:with-param name="lastAccessed" select="$lastAccessed"/>
	<xsl:with-param name="misc" select="$misc"/>
	<xsl:with-param name="nameOfConference" select="$nameOfConference"/>
	<xsl:with-param name="number" select="$number"/>
	<xsl:with-param name="numberOfPages" select="$numberOfPages"/>
	<xsl:with-param name="numberOfVolumes" select="$numberOfVolumes"/>
	<xsl:with-param name="organization" select="$organization"/>
	<xsl:with-param name="organizerOfConference" select="$organizerOfConference"/>
	<xsl:with-param name="pagesFrom" select="$pagesFrom"/>
	<xsl:with-param name="pagesTo" select="$pagesTo"/>
	<xsl:with-param name="place" select="$place"/>
	<xsl:with-param name="placeOfConference" select="$placeOfConference"/>
	<xsl:with-param name="proceedings" select="$proceedings"/>
	<xsl:with-param name="publicationDate" select="$publicationDate"/>
	<xsl:with-param name="publisher" select="$publisher"/>
	<xsl:with-param name="reviewed" select="$reviewed"/>
	<xsl:with-param name="series" select="$series"/>
	<xsl:with-param name="title" select="$title"/>
	<xsl:with-param name="url" select="$url"/>
	<xsl:with-param name="urn" select="$urn"/>
	<xsl:with-param name="volume" select="$volume"/>
	<xsl:with-param name="year" select="$year"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="string-length(normalize-space($structData)) &gt; 0">
	<span class="publicationDataStructInfo">
	  <xsl:if test="./@before">
	    <span class="before"><xsl:value-of select="./@before"/></span>
	  </xsl:if>
	  <xsl:if test="./@preText">
	    <span class="preText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@preText"/>
	      </xsl:call-template>
	    </span>
	  </xsl:if>	
	  <span class="publicationDataStruct"><xsl:value-of select="$structData"/></span>
	  <xsl:if test="./@postText = 'true'">
	    <span class="postText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="./@postText"/>
	      </xsl:call-template>      
	    </span>
	  </xsl:if>	  
	  <xsl:if test="./@after">
	    <span class="after"><xsl:value-of select="./@after"/></span>
	  </xsl:if>
	</span>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="./@ifEmpty">
	  <span class="publicationDataStructInfo"><span class="ifEmpty"><xsl:value-of select="./@ifEmpty"/></span></span>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>