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
  version="1.0">

  <!--
      **************************************************************************
      ** Templates for an Proceedings publication                             **
      **************************************************************************
  -->

  <!-- 
       Detail view 
       ===========
  -->
  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Proceedings']" mode="lead">
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'proceedings/setLeadText'"/>
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
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Proceedings']" mode="image">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'proceedings/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'proceedings/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'proceedings/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'proceedings/setImageCaption'"/>
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

  <xsl:template name="CT_Proceedings_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.Proceedings']" mode="detailed_view">

    <xsl:variable name="setAbstract">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setAbstract'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setAuthors">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setAuthors'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setEdition">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setEdition'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setIsbn">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setIsbn'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMisc">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setMisc'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setNumberOfPages">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setNumberOfPages'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setNumberOfVolumes">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setNumberOfVolumes'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPapers">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setPapers'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPublisher">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setPublisher'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSeries">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setSeries'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSeriesLink">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setSeriesLink'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSeriesVolume">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setSeriesVolume'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setUrl">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setUrl'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setVolume">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setVolume'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setYear">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setYear'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setOrganizerOfConference">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setOrganizerOfConference'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setNameOfConference">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setNameOfConference'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDateOfConference">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setDateOfConference'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPlaceOfConference">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'proceedings/setPlaceOfConference'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="formatDefFile">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'formatDefFile'" />
        <xsl:with-param name="default" select="'SciPublicationsDefaultFormat.xml'" />
      </xsl:call-template>
    </xsl:variable>

    <div class="mainBody publication publicationDetails publicationProceedingsDetails">

      <dl>
        <xsl:if test="($setAuthors = 'true') and (string-length(./authors) &gt; 0)">
          <xsl:call-template name="scipublicationsAuthors">
            <xsl:with-param name="authors" select="./authors/author" />
            <xsl:with-param name="authorText">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="id" select="'proceedings/author'" />
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="authorsText">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="id" select="'proceedings/authors'" />
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="($setYear = 'true') and (string-length(./yearOfPublication) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/year'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./yearOfPublication" />
          </dd>
        </xsl:if>
        <xsl:if test="($setPublisher = 'true') and (string-length(./publisher) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/publisher'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:variable name="publisherName" select="./publisher/publisherName" />
            <xsl:variable name="place" select="./publisher/place" />
            <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefPublisherFormat">
              <xsl:with-param name="name" select="$publisherName" />
              <xsl:with-param name="place" select="$place" />
            </xsl:apply-templates>
          </dd>
        </xsl:if>
        <xsl:if test="($setIsbn = 'true') and (string-length(./isbn) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/isbn'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./isbn" />
          </dd>
        </xsl:if>
        <xsl:if test="($setUrl = 'true') and (string-length(./url) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/url'" />
            </xsl:call-template>
          </dt>
          <dd>
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="./url" />
              </xsl:attribute>
              <xsl:value-of select="./url" />
            </a>
          </dd>
        </xsl:if>
        <xsl:if test="($setNameOfConference = 'true') and (string-length(./nameOfConference) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/nameOfConference'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./nameOfConference" />
          </dd>
        </xsl:if>
        <xsl:if test="($setOrganizerOfConference = 'true') and (string-length(./organizer/title) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/organizerOfConference'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./organizer/title" />
          </dd>
        </xsl:if>
        <xsl:if test="($setPlaceOfConference = 'true') and (string-length(./PlaceOfConference) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/placeOfConference'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./placeOfConference" />
          </dd>
        </xsl:if>
        <xsl:if test="($setDateOfConference = 'true') and (string-length(./dateFromOfConference) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/dateOfConference'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./dateFromOfConference/@date" />
            <xsl:if test="string-length(./dateToOfConference/@date) &gt; 0">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="id" select="'proceedings/dateToOfConference'" />
              </xsl:call-template>
              <xsl:value-of select="./dateToOfConference/@date" />
            </xsl:if>
          </dd>
        </xsl:if>
        <xsl:if test="($setVolume = 'true') and (string-length(./volume) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/volume'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./volume" />
          </dd>
        </xsl:if>
        <xsl:if test="($setNumberOfVolumes = 'true') and (string-length(./numberOfVolumes) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/numberOfVolumes'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./numberOfVolumes" />
          </dd>
        </xsl:if>
        <xsl:if test="($setNumberOfPages = 'true') and (string-length(./numberOfPages) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/numberOfPages'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./numberOfPages" />
          </dd>
        </xsl:if>
        <xsl:if test="($setSeries = 'true') and (string-length(./series/series) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/series'" />
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
                        <xsl:with-param name="id" select="'proceedings/seriesVolumePre'" />
                    </xsl:call-template>
                    <xsl:value-of select="./series/series/@volume" />
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications'" />
                        <xsl:with-param name="id" select="'proceedings/seriesVolumePost'" />
                    </xsl:call-template>
                  </xsl:if>
                </a>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="./series/series/title" />
                <xsl:if test="$setSeriesVolume = 'true'">
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications'" />
                        <xsl:with-param name="id" select="'proceedings/seriesVolumePre'" />
                    </xsl:call-template>
                    <xsl:value-of select="./series/series/@volume" />
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications'" />
                        <xsl:with-param name="id" select="'proceedings/seriesVolumePost'" />
                    </xsl:call-template>
                  </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </dd>
        </xsl:if>
      </dl>
   
      <xsl:if test="($setAbstract = 'true') and (string-length(./abstract) &gt; 0)">
        <div class="publicationAbstract">
          <h3>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/abstract'" />
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
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/misc'" />
            </xsl:call-template>
          </h3>
          <xsl:value-of select="./misc" />
        </div>
      </xsl:if>
      <xsl:if test="($setPapers = 'true') and (count(./papers/paper) &gt; 0)">
        <div class="proceedingsPapers">
          <h3>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'proceedings/papers'" />
            </xsl:call-template>
          </h3>
          <ul class="proceedingsPapersList">
            <xsl:for-each select="./papers/paper">
              <li>
                <xsl:call-template name="CT_InProceedings_List" />
              </li>
            </xsl:for-each>
          </ul>
        </div>
      </xsl:if>
    </div>

  </xsl:template>
  


  <!--
      List view
      =========
  -->
  <xsl:template match="publications[objectType='com.arsdigita.cms.contenttypes.Proceedings']" mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'" />
    <xsl:call-template name="CT_Proceedings_List">
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl" />
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="publication[objectType='com.arsdigita.cms.contenttypes.Proceedings']" mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'" />
    <xsl:call-template name="CT_Proceedings_List">
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl" />
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="CT_Proceedings_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Proceedings']" mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'" />
    <xsl:variable name="formatDefFile">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'formatDefFile'" />
        <xsl:with-param name="default" select="'SciPublicationsDefaultFormat.xml'" />
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
        <xsl:sort select="./@order" data-type="number" />
        <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefAuthorFormat">
          <xsl:with-param name="surname" select="./surname" />
          <xsl:with-param name="givenName" select="./givenname" />
          <xsl:with-param name="isEditor" select="@isEditor" />
          <xsl:with-param name="authorsCount" select="count(../author)" />
          <xsl:with-param name="position" select="position()" />
        </xsl:apply-templates>
      </xsl:for-each>
    </xsl:variable>
    <!-- 
         Process publisher part, and store in a variable. The format for 
         the publisher part is definied in a separate XML file which also 
         processed here.
    -->
    <xsl:variable name="publisher">
      <xsl:for-each select="./publisher">
        <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefPublisherFormat">
          <xsl:with-param name="name" select="./title" />
          <xsl:with-param name="place" select="./place" />
        </xsl:apply-templates>
      </xsl:for-each>
    </xsl:variable>

    <!-- Call template for the proceedings format -->
    <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefProceedingsFormat">
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="dateFromOfConference" select="./dateFromOfConference" />
      <xsl:with-param name="dateToOfConference" select="./dateToOfConference" />
      <xsl:with-param name="isbn" select="./isbn" />
      <xsl:with-param name="misc" select="./misc" />
      <xsl:with-param name="nameOfConference" select="./nameOfConference" />
      <xsl:with-param name="numberOfPages" select="./numberofpages" />
      <xsl:with-param name="numberOfVolumes" select="./numberofvolumes" />
      <xsl:with-param name="organizerOfConference" select="./organizerOfConference" />
      <xsl:with-param name="placeOfConference" select="./placeOfConference" />
      <xsl:with-param name="publisher" select="$publisher" />
      <xsl:with-param name="title" select="./title" />
      <xsl:with-param name="volume" select="./volume" />
      <xsl:with-param name="year" select="./yearOfPublication" />
      <xsl:with-param name="oid" select="./@oid" />
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl" />
    </xsl:apply-templates>

  </xsl:template>
</xsl:stylesheet>
