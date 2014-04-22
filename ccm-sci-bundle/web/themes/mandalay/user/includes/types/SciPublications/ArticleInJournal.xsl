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
    version="1.0"
>

    <!--
        **************************************************************************
        ** Templates for an ArticleInJournal publication                        **
        **************************************************************************
    -->

    <!-- 
         Detail view 
         ===========
    -->
    <!-- DE Leadtext -->
    <!-- EN lead text view -->
    <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ArticleInJournal']" mode="lead">
        <xsl:variable name="setLeadText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setLeadText'"/>
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
    <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ArticleInJournal']" mode="image">

        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setImageCaption'"/>
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

    <xsl:template name="CT_ArticleInJournal_graphics"
                  match="cms:item[objectType='com.arsdigita.cms.contenttypes.ArticleInJournal']"
                  mode="detailed_view">

        <xsl:variable name="setAbstract">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setAbstract'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setAssignedTerms">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="setting" select="'articleInJournal/setAssignedTerms'" />
                <xsl:with-param name="default" select="'false'" />
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setAuthors">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setAuthors'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setIssn">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setIssn'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setIssue">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setIssue'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>    
        <xsl:variable name="setJournal">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setJournal'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="linkJournal">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/linkJournal'"/>
                <xsl:with-param name="default" select="'false'"/>
            </xsl:call-template>      
        </xsl:variable>
        <xsl:variable name="setLanguageOfPublication">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="setting" select="'articleInJournal/setLanguageOfPublication'" />
                <xsl:with-param name="default" select="'true'" />
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMisc">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setMisc'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setPages">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setPages'"/>
                <xsl:with-param name="default" select="'true'"/>		
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setPublicationDate">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setPublicationDate'"/>
                <xsl:with-param name="default" select="'true'"/>		
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setReviewed">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setReviewed'"/>
                <xsl:with-param name="default" select="'true'"/>		
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setUrl">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setUrl'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setVolume">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setVolume'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setYear">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'"/>
                <xsl:with-param name="setting" select="'articleInJournal/setYear'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setYearFirstPublished">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="setting" select="'articleInJournal/setYearFirstPublished'" />
                <xsl:with-param name="default" select="'true'" />
            </xsl:call-template>
        </xsl:variable>

        <div class="mainBody publication publicationDetails publicationArticleInJournalDetails">

            <dl>
                <xsl:if test="($setAuthors = 'true') and (string-length(./authors) &gt; 0)">
                    <xsl:call-template name="scipublicationsAuthors">
                        <xsl:with-param name="authors" select="./authors/author"/>
                        <xsl:with-param name="authorText">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciPublications'"/>
                                <xsl:with-param name="id" select="'articleInJournal/author'"/>
                            </xsl:call-template>	      
                        </xsl:with-param>
                        <xsl:with-param name="authorsText">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciPublications'"/>
                                <xsl:with-param name="id" select="'articleInJournal/authors'"/>
                            </xsl:call-template>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>

                <xsl:if test="($setYear = 'true') and (string-length(./yearOfPublication) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/year'"/>
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
                            <xsl:with-param name="id" select="'articleInJournal/yearFirstPublished'"/>
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
                            <xsl:with-param name="id" select="'articleInJournal/languageOfPublication'" />
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

                <xsl:if test="($setJournal = 'true') and (string-length(./journal) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/journal'"/>
                        </xsl:call-template>	    
                    </dt>
                    <dd>
                        <xsl:choose>
                            <xsl:when test="$linkJournal = 'true'">
                                <a>
                                    <xsl:attribute name="href">/redirect/?oid=<xsl:value-of select="./journal/@oid"/></xsl:attribute>
                                    <xsl:value-of select="./journal/title"/>
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="./journal/title"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </dd>
                </xsl:if>

                <xsl:if test="($setIssn = 'true') and (string-length(./issn) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/issn'"/>	      
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:value-of select="./issn"/>
                    </dd>
                </xsl:if>

                <xsl:if test="($setUrl = 'true') and (string-length(./url) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/url'"/>	      
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <a>
                            <xsl:attribute name="href">
                                <xsl:value-of select="./url"/>
                            </xsl:attribute>
                            <xsl:value-of select="./url"/>
                        </a>
                    </dd>
                </xsl:if>
	
                <xsl:if test="($setIssue = 'true') and (string-length(./issue) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/issue'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:value-of select="./issue"/>
                    </dd>
                </xsl:if>
	
                <xsl:if test="($setVolume = 'true') and (string-length(./volume) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/volume'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:value-of select="./volume"/>
                    </dd>
                </xsl:if>

                <xsl:if test="($setReviewed = 'true') and (string-length(./reviewed) &gt; 0) and (./reviewed = 'true')">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/reviewed'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>&nbsp;</dd>
                </xsl:if>

                <xsl:if test="($setPages = 'true') and (string-length(./pagesFrom) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/pages'"/>	      
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:value-of select="./pagesFrom"/>
                        <xsl:if test="string-length(./pagesTo) &gt; 0">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciPublications'"/>
                                <xsl:with-param name="id" select="'articleInJournal/pagesSeparator'"/>	      
                            </xsl:call-template>	   
                            <xsl:value-of select="./pagesTo"/>	    
                        </xsl:if>
                    </dd>
                </xsl:if>

                <xsl:if test="($setPublicationDate = 'true') and (string-length(./publicationDate) &gt; 0)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/publicationDate'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:value-of select="./publicationDate/@date"/>
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
                            <xsl:with-param name="id" select="'articleInJournal/abstract'"/>	    
                        </xsl:call-template>
                    </h3>
                    <div class="abstract">
                        <xsl:variable name="abstract">
                            <xsl:call-template name="mandalay:string-replace">
                                <xsl:with-param name="string" select="./abstract"/>
                                <xsl:with-param name="from" select="'&#xA;'"/>
                                <xsl:with-param name="to" select="'&lt;br/>'"/>
                            </xsl:call-template>
                        </xsl:variable>
                        <xsl:value-of disable-output-escaping="yes" select="$abstract"/>    
                    </div>
                </div>
            </xsl:if>

            <xsl:if test="($setMisc = 'true') and (string-length(normalize-space(./misc)) &gt; 0)">
                <div class="publicationMisc">
                    <h3>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciPublications'"/>
                            <xsl:with-param name="id" select="'articleInJournal/misc'"/>	    
                        </xsl:call-template>
                    </h3>
                    <xsl:value-of disable-output-escaping="yes" select="./misc"/>
                </div>
            </xsl:if>      

            <!-- <xsl:call-template name="scipublicationsDownload"/> -->
        </div>
    
    </xsl:template>


    <!--
        List view
        =========
    -->
    <xsl:template match="publication[objectType='com.arsdigita.cms.contenttypes.ArticleInJournal']"
                  mode="list_view">
        <xsl:param name="useRelativeUrl" select="'false'"/>
        <xsl:call-template name="CT_ArticleInJournal_List">
            <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="publications[objectType='com.arsdigita.cms.contenttypes.ArticleInJournal']"
                  mode="list_view">
        <xsl:param name="useRelativeUrl" select="'false'"/>
        <xsl:call-template name="CT_ArticleInJournal_List">
            <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template 
        name="CT_ArticleInJournal_List" 
        match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.ArticleInJournal']"
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
    
        <!-- Processing the journal data using the standard format for publications -->
        <xsl:variable name="journal">
            <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefJournalFormat">
                <xsl:with-param name="issn" select="./journal/issn"/>
                <xsl:with-param name="misc" select="./journal/misc"/>
                <xsl:with-param name="reviewed" select="./journal/reviewed"/>
                <xsl:with-param name="symbol" select="./journal/symbol"/>
                <xsl:with-param name="title" select="./journal/title"/>
            </xsl:apply-templates>
        </xsl:variable>

        <!-- 
             Procecess the format specification and use the values from the XML
        -->
        <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefArticleInJournalFormat">
            <xsl:with-param name="authors" select="$authors"/>
            <xsl:with-param name="issue" select="./issue"/>		
            <xsl:with-param name="journal" select="$journal"/>
            <xsl:with-param name="misc" select="./misc"/>
            <xsl:with-param name="pagesFrom" select="./pagesFrom"/>
            <xsl:with-param name="pagesTo" select="./pagesTo"/>	
            <xsl:with-param name="publicationDate" select="./publicationDate"/>
            <xsl:with-param name="reviewed" select="./reviewed"/>
            <xsl:with-param name="title" select="./title"/>
            <xsl:with-param name="volume" select="./volume"/>
            <xsl:with-param name="year" select="./yearOfPublication"/>
            <xsl:with-param name="oid" select="./@oid"/>
            <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
        </xsl:apply-templates>
   
    </xsl:template>

    <!-- Link view -->
    <xsl:template 
        name="CT_ArticleInJournal" 
        match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.ArticleInJournal']"
        mode="link_view">
        <!-- Simply call template for Publications, because there is not difference for list view between these
        two types -->
        <xsl:call-template name="CT_Publication_Link"/>
    </xsl:template>
  
</xsl:stylesheet>
