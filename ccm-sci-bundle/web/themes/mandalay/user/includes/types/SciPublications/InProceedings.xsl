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
      ** Templates for an InProceedings publication                           **
      **************************************************************************
  -->

  <!-- 
       Detail view 
       ===========
  -->
  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.InProceedings']" mode="lead">
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'inProceedings/setLeadText'"/>
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
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.InProceedings']" mode="image">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'inProceedings/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'inProceedings/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'inProceedings/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'inProceedings/setImageCaption'"/>
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

  <xsl:template name="CT_InProceedings_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.InProceedings']" mode="detailed_view">

    <xsl:variable name="setAbstract">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setAbstract'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setAuthors">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setAuthors'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMisc">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setMisc'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPages">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setPages'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setProceedings">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setProceedings'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSeries">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setSeries'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSeriesLink">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setSeriesLink'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSeriesVolume">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setSeriesVolume'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setYear">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'inProceedings/setYear'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>

    <div class="mainBody publication publicationDetails publicationInProceedingsDetails">

      <dl>
        <xsl:if test="($setAuthors = 'true') and (string-length(./authors) &gt; 0)">
          <xsl:call-template name="scipublicationsAuthors">
            <xsl:with-param name="authors" select="./authors/author" />
            <xsl:with-param name="authorText">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="id" select="'inProceedings/author'" />
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="authorsText">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="id" select="'inProceedings/authors'" />
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="($setYear = 'true') and (string-length(./yearOfPublication) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'inProceedings/year'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./yearOfPublication" />
          </dd>
        </xsl:if>
        <xsl:if test="($setPages = 'true') and (string-length(./pagesFrom) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'inProceedings/pages'" />
            </xsl:call-template>
          </dt>
          <dd>
            <xsl:value-of select="./pagesFrom" />
            <xsl:if test="string-length(./pagesTo) &gt; 0">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'SciPublications'" />
                <xsl:with-param name="id" select="'inProceedings/pagesSeparator'" />
              </xsl:call-template>
              <xsl:value-of select="./pagesTo" />
            </xsl:if>
          </dd>
        </xsl:if>
        <xsl:if test="($setProceedings = 'true') and (count(./proceedings) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'inProceedings/proceedings'" />
            </xsl:call-template>
          </dt>
          <dd>
            <!--<a>
              <xsl:attribute name="href">/redirect/?oid=
              <xsl:value-of select="./proceedings/@oid" /></xsl:attribute>
              <xsl:value-of select="./proceedings/title" />
            </a>-->
            <xsl:for-each select="./proceedings">
                <xsl:call-template name="CT_Proceedings_List"/>
            </xsl:for-each>
          </dd>
        </xsl:if>
        <xsl:if test="($setSeries = 'true') and (string-length(../series/series) &gt; 0)">
          <dt>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'SciPublications'" />
              <xsl:with-param name="id" select="'inProceedings/series'" />
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
                        <xsl:with-param name="id" select="'inProceedings/seriesVolumePre'" />
                    </xsl:call-template>
                    <xsl:value-of select="./series/series/@volume" />
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications'" />
                        <xsl:with-param name="id" select="'inProceedings/seriesVolumePost'" />
                    </xsl:call-template>
                  </xsl:if>
                </a>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="./series/series/title" />
                <xsl:if test="$setSeriesVolume = 'true'">
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications'" />
                        <xsl:with-param name="id" select="'inProceedings/seriesVolumePre'" />
                    </xsl:call-template>
                    <xsl:value-of select="./series/series/@volume" />
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications'" />
                        <xsl:with-param name="id" select="'inProceedings/seriesVolumePost'" />
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
              <xsl:with-param name="id" select="'inProceedings/abstract'" />
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
              <xsl:with-param name="id" select="'inProceedings/misc'" />
            </xsl:call-template>
          </h3>
          <xsl:value-of select="./misc" />
        </div>
      </xsl:if>
      <!-- <xsl:call-template name="scipublicationsDownload"/> -->
    </div>

  </xsl:template>



  <!--
      List view
      =========
  -->
  <xsl:template match="publications[objectType='com.arsdigita.cms.contenttypes.InProceedings']" mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'" />
    <xsl:call-template name="CT_InProceedings_List">
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl" />
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="publication[objectType='com.arsdigita.cms.contenttypes.InProceedings']" mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'" />
    <xsl:call-template name="CT_InProceedings_List">
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl" />
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="CT_InProceedings_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.InProceedings']" mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'" />
    <!-- EN Get all settings needed -->
    <xsl:variable name="formatDefFile">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'formatDefFile'" />
        <xsl:with-param name="default" select="'SciPublicationsDefaultFormat.xml'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'listView/setLinkToDetails'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'listView/setLeadText'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'" />
        <xsl:with-param name="default" select="'0'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciPublications'" />
        <xsl:with-param name="setting" select="'listView/setMoreButton'" />
        <xsl:with-param name="default" select="'false'" />
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
          <xsl:with-param name="isEditor" select="./@isEditor" />
          <xsl:with-param name="authorsCount" select="count(../author)" />
          <xsl:with-param name="position" select="position()" />
        </xsl:apply-templates>
      </xsl:for-each>
    </xsl:variable>
    
    <!-- Process the publisher of the proceedings -->
    <xsl:variable name="proceedingsPublisher">
      <xsl:for-each select="./proceedings/publisher">
        <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefPublisherFormat">
          <xsl:with-param name="name" select="./publisherName" />
          <xsl:with-param name="place" select="./place" />
        </xsl:apply-templates>
      </xsl:for-each>
    </xsl:variable>

    <!-- Process the authors of the proceedings -->
    <xsl:variable name="proceedingsAuthors">
        <xsl:for-each select="./proceedings/authors/author">
            <xsl:sort select="./@order" data-type="number" />
       <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefAuthorFormat">
                <xsl:with-param name="surname" select="./surname"/>
                <xsl:with-param name="givenName" select="./givenname" />
                <xsl:with-param name="isEditor" select="false" />
                <xsl:with-param name="authorsCount" select="count(../author)" />
                <xsl:with-param name="position" select="position()" />
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:variable>

    <!-- Processing the proceedings -->
    <xsl:variable name="proceedings">
      <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefProceedingsFormat">
        <xsl:with-param name="authors" select="$proceedingsAuthors"/>
        <xsl:with-param name="dateFromOfConference" select="./proceedings/dateFromOfConference" />
        <xsl:with-param name="dateToOfConference" select="./proceedings/dateToOfConference" />
        <xsl:with-param name="isbn" select="./proceedings/isbn" />
        <xsl:with-param name="misc" select="./proceedings/misc" />
        <xsl:with-param name="nameOfConference" select="./proceedings/nameOfConference" />
        <xsl:with-param name="numberOfPages" select="./proceedings/numberOfPages" />
        <xsl:with-param name="numberOfVolumes" select="./proceedings/numberOfVolumes" />
        <xsl:with-param name="organizerOfConference" select="./proceedings/organizerOfConference" />
        <xsl:with-param name="placeOfConference" select="./proceedings/placeOfConference" />
        <xsl:with-param name="publisher" select="$proceedingsPublisher" />
        <xsl:with-param name="title" select="./proceedings/title" />
        <xsl:with-param name="volume" select="./proceedings/volume" />
        <xsl:with-param name="year" select="''" />
        <xsl:with-param name="oid" select="./proceedings/@oid" />
        <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl" />
      </xsl:apply-templates>
    </xsl:variable>

    <!-- Process the format specification -->
    <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefInProceedingsFormat">
      <xsl:with-param name="authors" select="$authors" />
      <xsl:with-param name="misc" select="./misc" />
      <xsl:with-param name="pagesFrom" select="./pagesFrom" />
      <xsl:with-param name="pagesTo" select="./pagesTo" />
      <xsl:with-param name="proceedings" select="$proceedings" />
      <xsl:with-param name="title" select="./title" />
      <xsl:with-param name="year" select="./yearOfPublication" />
      <xsl:with-param name="oid" select="./@oid" />
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl" />
    </xsl:apply-templates>

  </xsl:template>

  <!-- Link view -->
  <xsl:template name="CT_InProceedings_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.InProceedings']" mode="link_view">
    <!-- Simply call template for Publications, because there is not difference for list view between these
         two types -->
    <xsl:call-template name="CT_Publication_Link" />
  </xsl:template>


</xsl:stylesheet>
