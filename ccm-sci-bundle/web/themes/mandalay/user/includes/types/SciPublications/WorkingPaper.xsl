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
        ** Templates for an WorkingPaper publication                **
        **************************************************************************
    -->

    <!-- 
         Detail view 
         ===========
    -->
    <!-- DE Leadtext -->
    <!-- EN lead text view -->
    <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.WorkingPaper']" mode="lead">
        <xsl:variable name="setLeadText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setLeadText'"/>
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
    <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.WorkingPaper']" mode="image">

        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
    </xsl:template>

    <xsl:template name="CT_WorkingPaper_graphics"
                  match="cms:item[objectType='com.arsdigita.cms.contenttypes.WorkingPaper']"
                  mode="detailed_view">
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setImageCaption'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
    
        <xsl:variable name="setAbstract">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setAbstract'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setAuthors">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setAuthors'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setAssignedTerms">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="setting" select="'workingPaper/setAssignedTerms'" />
                <xsl:with-param name="default" select="'false'" />
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLanguageOfPublication">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="setting" select="'workingPaper/setLanguageOfPublication'" />
                <xsl:with-param name="default" select="'true'" />
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMisc">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setMisc'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setNumber">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setNumber'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>      
        <xsl:variable name="setNumberOfPages">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setNumberOfPages'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setOrganization">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setPages'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>      
        <xsl:variable name="setPlace">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setPlace'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>      
        <xsl:variable name="setSeries">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setSeries'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setSeriesLink">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setSeriesLink'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setSeriesVolume">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setSeriesVolume'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setYear">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'workingPaper/setYear'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setYearFirstPublished">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="setting" select="'workingPaper/setYearFirstPublished'" />
                <xsl:with-param name="default" select="'true'" />
            </xsl:call-template>
        </xsl:variable>

        <div class="mainBody publication publicationDetails publicationGreyLiteratureDetails">
            <xsl:if test="$setImage = 'true'">
                <xsl:call-template name="mandalay:imageAttachment">
                    <xsl:with-param name="showCaption" select="$setImageCaption" />
                    <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
                    <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
                </xsl:call-template>
            </xsl:if>

            <dl>
                <xsl:if test="($setAuthors = 'true') and (string-length(./authors) &gt; 0)">
                    <xsl:call-template name="scipublicationsAuthors">
                        <xsl:with-param name="authors" select="./authors/author"/>
                        <xsl:with-param name="authorText">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciPublications'"/>
                                <xsl:with-param name="id" select="'workingPaper/author'"/>
                            </xsl:call-template>	      
                        </xsl:with-param>
                        <xsl:with-param name="authorsText">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciPublications'"/>
                                <xsl:with-param name="id" select="'workingPaper/authors'"/>
                            </xsl:call-template>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>

                <xsl:if test="($setYear = 'true') and (string-length(./yearOfPublication) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'workingPaper/year'"/>
                        </xsl:call-template>	    
                    </dt>
                    <dd>
                        <xsl:value-of select="./yearOfPublication"/>
                    </dd>
                </xsl:if>

                <xsl:if test="($setYearFirstPublished = 'true') and (string-length(./yearFirstPublished) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'monograph/yearFirstPublished'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:value-of select="./yearFirstPublished" />
                    </dd>
                </xsl:if>
                <xsl:if test="($setLanguageOfPublication = 'true') and (string-length(./languageOfPublication) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'" />
                            <xsl:with-param name="id" select="'workingPaper/languageOfPublication'" />
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:variable name="langText">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'languageCodes'" />
                                <xsl:with-param name="id" select="./languageOfPublication" />
                            </xsl:call-template>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="(string-length($langText) &gt; 0) and (contains($langText, 'Missing Translation') = false())">
                                <xsl:value-of select="$langText"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="./languageOfPublication"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </dd>
                </xsl:if>

                <xsl:if test="($setOrganization = 'true') and (string-length(./organization/title) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'workingPaper/organization'"/>
                        </xsl:call-template>	    	    
                    </dt>
                    <dd>
                        <xsl:value-of select="./organization/title"/>
                    </dd>
                </xsl:if>

                <xsl:if test="($setPlace = 'true') and (string-length(./place) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'workingPaper/place'"/>
                        </xsl:call-template>	    	    
                    </dt>
                    <dd>
                        <xsl:value-of select="./place"/>
                    </dd>
                </xsl:if>

                <xsl:if test="($setNumber = 'true') and (string-length(./number) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'workingPaper/number'"/>
                        </xsl:call-template>	    	    
                    </dt>
                    <dd>
                        <xsl:value-of select="./number"/>
                    </dd>
                </xsl:if>
	
                <xsl:if test="($setNumberOfPages = 'true') and (string-length(./numberOfPages) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'workingPaper/numberOfPages'"/>
                        </xsl:call-template>	    
                    </dt>
                    <dd>
                        <xsl:value-of select="./numberOfPages"/>
                    </dd>
                </xsl:if>

                <xsl:if test="($setSeries = 'true') and (string-length(./series) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'workingPaper/series'"/>
                        </xsl:call-template>	    	    
                    </dt>
                    <dd>
                        <xsl:choose>
                            <xsl:when test="$setSeriesLink = 'true'">
                                <a>
                                    <xsl:attribute name="href">/redirect/?oid=<xsl:value-of select="./series/series/@oid" /></xsl:attribute>
                                    <xsl:value-of select="./series/series/title" />
                  
                                    <xsl:if test="$setSeriesVolume = 'true'">
                                        <xsl:call-template name="mandalay:getStaticText">
                                            <xsl:with-param name="module" select="'SciPublications'" />
                                            <xsl:with-param name="id" select="'workingPaper/seriesVolumePre'" />
                                        </xsl:call-template>
                                        <xsl:value-of select="./series/series/@volume" />
                                        <xsl:call-template name="mandalay:getStaticText">
                                            <xsl:with-param name="module" select="'SciPublications'" />
                                            <xsl:with-param name="id" select="'workingPaper/seriesVolumePost'" />
                                        </xsl:call-template>
                                    </xsl:if>
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="./series/series/title" />
                                <xsl:if test="$setSeriesVolume = 'true'">
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'SciPublications'" />
                                        <xsl:with-param name="id" select="'workingPaper/seriesVolumePre'" />
                                    </xsl:call-template>
                                    <xsl:value-of select="./series/series/@volume" />
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'SciPublications'" />
                                        <xsl:with-param name="id" select="'workingPaper/seriesVolumePost'" />
                                    </xsl:call-template>
                                </xsl:if>
                            </xsl:otherwise>
                        </xsl:choose>
                    </dd>
                </xsl:if>
                <xsl:if test="$setAssignedTerms = 'true'">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'assignedTerms'" />
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:call-template name="scipublications_assigned_terms"/>
                    </dd>
                </xsl:if>
            </dl>

            <xsl:if test="($setAbstract = 'true') and (string-length(./abstract) &gt; 0)">
                <div class="publicationAbstract">
                    <h3>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'workingPaper/abstract'"/>
                        </xsl:call-template>
                    </h3>
                    <div class="abstract">
                        <xsl:value-of disable-output-escaping="yes" select="./abstract" />
                    </div>
                </div>
            </xsl:if>
      
            <xsl:if test="($setMisc = 'true') and (string-length(normalize-space(./misc)) &gt; 0)">
                <div class="publicationMisc">
                    <h3>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'workingPaper/misc'"/>	    
                        </xsl:call-template>
                    </h3>
                    <xsl:value-of disable-output-escaping="yes" 
                                  select="./misc"/>
                </div>
            </xsl:if>      

            <!-- <xsl:call-template name="scipublicationsDownload"/>	-->
        </div>

    </xsl:template>
  


    <!--
        List view
        =========
    -->
    <xsl:template match="publications[objectType='com.arsdigita.cms.contenttypes.WorkingPaper']"
                  mode="list_view">
        <xsl:param name="useRelativeUrl" select="'false'"/>
        <xsl:call-template name="CT_WorkingPaper_List">
            <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="publication[objectType='com.arsdigita.cms.contenttypes.WorkingPaper']"
                  mode="list_view">
        <xsl:param name="useRelativeUrl" select="'false'"/>
        <xsl:call-template name="CT_WorkingPaper_List">
            <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template 
        name="CT_WorkingPaper_List" 
        match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.WorkingPaper']"
        mode="list_view">
        <xsl:param name="useRelativeUrl" select="'false'"/>

        <!-- EN Get all settings needed -->
        <xsl:variable name="formatDefFile">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'formatDefFile'"/>
                <xsl:with-param name="default" select="'SciPublicationsDefaultFormat.xml'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLinkToDetails">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'listView/setLinkToDetails'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLeadText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'listView/setLeadText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLeadTextLength">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
                <xsl:with-param name="default" select="'0'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMoreButton">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
                <xsl:with-param name="default" select="'false'"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- 
             Process authors first, and store the result (complete author part) 
             in a variable. The format for the authors part is defined in a 
             seperate XML file, which is also processed 
             here.
        -->
        <xsl:variable name="authors">
            <xsl:for-each select="./authors/author">                   
                <xsl:sort select="./@order" data-type="number"/>
                <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefAuthorFormat">
                    <xsl:with-param name="surname" select="./surname"/>
                    <xsl:with-param name="givenName" select="./givenname"/>
                    <xsl:with-param name="isEditor" select="./@isEditor"/>
                    <xsl:with-param name="authorsCount" select="count(../author)"/>
                    <xsl:with-param name="position" select="position()"/>
                </xsl:apply-templates>
            </xsl:for-each>
        </xsl:variable>

        <!--
             Procecess the format specification and use the values from the XML
        -->
        <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefWorkingPaperFormat">
            <xsl:with-param name="authors" select="$authors"/>
            <xsl:with-param name="misc" select="./misc"/>
            <xsl:with-param name="number" select="./number"/>
            <xsl:with-param name="numberOfPages" select="./numberOfPages"/>
            <xsl:with-param name="organization" select="./organization/title"/>
            <xsl:with-param name="place" select="./place"/>
            <xsl:with-param name="series" select="./series/series/title"/>
            <xsl:with-param name="title" select="./title"/>
            <xsl:with-param name="year" select="./yearOfPublication"/>
            <xsl:with-param name="oid" select="./@oid"/>
            <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
        </xsl:apply-templates>
    </xsl:template>

    <!-- link view -->
    <xsl:template 
        name="CT_WorkingPaper_Link" 
        match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper']"
        mode="link_view">
        <!-- Simply call template for Publications, because there is not difference for list view between these
        two types -->
        <xsl:call-template name="CT_Publication_Link"/>
    </xsl:template>



</xsl:stylesheet>
