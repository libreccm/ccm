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
  Hier wird die cmsSummary verarbeitet 
-->

<!-- EN
  Processing cmsSummary
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
  
  <!-- DE Zeigt die Zusammenfassung an -->
  <!-- EN Show item summary-->
  <xsl:template match="cms:itemSummary">
    <div class="cmsSummarySection">
      <h3 class="cmsSummaryHeading">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'summary/itemSummary/header'"/>
        </xsl:call-template>
      </h3>
      <div class="cmsSummaryBody table">
        <div class="tableRow">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/itemSummary/type'"/>
            </xsl:call-template>
          </span>
          <span class="value">
            <xsl:value-of select="@objectType"/>
          </span>
        </div>
        <div class="tableRow">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/itemSummary/name'"/>
            </xsl:call-template>
          </span>
          <span class="value">
            <xsl:value-of select="@name"/>
          </span>
        </div>
        <div class="tableRow">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/itemSummary/title'"/>
            </xsl:call-template>
          </span>
          <span class="value">
            <xsl:value-of select="@title"/>
          </span>
        </div>
        <xsl:apply-templates select="cms:subjectCategories"/>
        <div class="tableRow">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/itemSummary/description'"/>
            </xsl:call-template>
          </span>
          <span class="value">
            <xsl:value-of disable-output-escaping="yes" select="@description"/>
          </span>
        </div>
      </div>
    </div>
  </xsl:template>
  
  <!-- DE Zeigt die subjectCategories an -->
  <!-- EN show subject categories -->
  <xsl:template match="cms:subjectCategories">
    <div class="tableRow">
      <span class="key">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'summary/itemSummary/subjectCategories'"/>
        </xsl:call-template>
      </span>
      <span class="value">
        <xsl:apply-templates/>
      </span>
    </div>
  </xsl:template>
  
  <!-- DE Zeigt eine subjectCategory an -->
  <!-- EN Shows a subject category -->
  <xsl:template match="cms:subjectCategory">
    <ul>
      <li>
        <xsl:value-of disable-output-escaping="yes" select="."/>
      </li>
    </ul>
  </xsl:template>
  
  <!-- DE Zeigt die Kategorien an-->
  <!-- EN Shows the categories -->
  <xsl:template match="cms:categorySummary">
    <div class="cmsSummarySection">
      <h3 class="cmsSummaryHeading">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'summary/categorySummary/header'"/>
        </xsl:call-template>
      </h3>
      <div class="cmsSummaryBody">
        <xsl:apply-templates mode="summary"/>
      </div>
    </div>
  </xsl:template>
  
  <!-- DE Erzeugt einen neuen Abschnitt -->
  <!-- EN Creates a new paragraph -->
  <!-- DE cms:category wird in zwei verschiedenen Syntax verwendet. Die andere ist in
          cmsCategroyStep zu finden. -->
  <!-- EN cms:category is using to different syntax. The other one is located
          in cmsCategoryStep. -->
  <xsl:template match="cms:category" mode="summary">
    <ul class="categoryList">
      <li>
        <xsl:value-of disable-output-escaping="yes" select="."/>
      </li>
    </ul>
  </xsl:template>
  
  <!-- DE Zeigt den stabilen Link an-->
  <!-- EN shows the stable link -->
  <xsl:template match="cms:linkSummary">
    <div class="cmsSummarySection">
      <h3 class="cmsSummaryHeading">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'summary/stableLink/header'"/>
        </xsl:call-template>
      </h3>
      <div class="cmsSummaryBody">
        <a href="{@url}">
          <xsl:value-of select="@url"/>
        </a>
      </div>
    </div>
  </xsl:template>
  
  <!-- DE Zeigt den Lifecylce an -->
  <!-- EN Shows the lifecycle -->
  <xsl:template match="cms:lifecycleSummary">
    <div class="cmsSummarySection">
      <h3 class="cmsSummaryHeading">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'summary/lifecycle/header'"/>
        </xsl:call-template>
      </h3>
      <div class="cmsSummaryBody table">
        <xsl:choose>
          <xsl:when test="@noLifecycle">
            <span class="noInfo">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'cms'"/>
                <xsl:with-param name="id" select="'summary/lifecycle/noLifecycle'"/>
              </xsl:call-template>
            </span>
          </xsl:when>
          <xsl:otherwise>
            <span class="key">
              <xsl:value-of select="@name"/>
            </span>
            <span class="value">
              <xsl:choose>
                <xsl:when test="@hasBegun='false'">
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemNotYetPublished/startText'"/>
                  </xsl:call-template>
                  <xsl:value-of select="@startDate"/>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemNotYetPublished/middleText'"/>
                  </xsl:call-template>
                  <xsl:value-of select="@endDateString"/>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemNotYetPublished/endText'"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:when test="@hasEnded='true'">
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemAlreadyEnded/startText'"/>
                  </xsl:call-template>
                  <xsl:value-of select="@startDate"/> 
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemAlreadyEnded/middleText'"/>
                  </xsl:call-template>
                  <xsl:value-of select="@endDate"/>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemAlreadyEnded/endText'"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemPublished/startText'"/>
                  </xsl:call-template>
                  <xsl:value-of select="@startDate"/> 
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemPublished/middleText'"/>
                  </xsl:call-template>
                  <xsl:value-of select="@endDateString"/>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="id" select="'summary/lifecycle/itemPublished/endText'"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </span>
          </xsl:otherwise>
        </xsl:choose>
      </div>
    </div>    
  </xsl:template>
  
  <!-- DE Zeigt den Workflow an -->
  <!-- EN Shows the workflow -->
  <xsl:template match="cms:workflowSummary">
    <div class="cmsSummarySection">
      <h3 class="cmsSummaryHeading">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'summary/workflow/header'"/>
        </xsl:call-template>
      </h3>
      <div class="cmsSummaryBody">
        <xsl:if test="@restartWorkflowURL">
          <a href="{@restartWorkflowURL}">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/workflow/restartEditing'"/>
            </xsl:call-template>
          </a>
        </xsl:if>
        <xsl:apply-templates/>
      </div>
    </div>
  </xsl:template>

  <!-- DE Zeigt einen Task an -->
  <!-- EN Shows a task -->
  <xsl:template match="cms:task">
    <xsl:variable name="setComments">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'cms'"/>
        <xsl:with-param name="setting" select="'summary/setComments'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setNoComment">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'cms'"/>
        <xsl:with-param name="setting" select="'summary/setNoComment'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <div class="cmsTask tableRow">
      <span class="tableCell">
        <span class="key">
        <xsl:value-of select="@name"/>
        </span>
        <span class="status tableCell">
          (
          <xsl:value-of select="@state"/>
          )
        </span>
      </span>
      <xsl:if test="$setComments = 'true'">
        <xsl:if test="not(cms:taskComment)">
          <xsl:if test="setNoComment = 'true'">
            <span class="noInfo tableCell">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'cms'"/>
                <xsl:with-param name="id" select="'summary/workflow/noComment'"/>
              </xsl:call-template>
            </span>
          </xsl:if>
        </xsl:if>
        <xsl:apply-templates/>
      </xsl:if>
    </div>
  </xsl:template>
  
  <!-- DE Zeigt einen nicht-leeren Kommentar an -->
  <!-- EN Shows a non-empty comment -->
  <xsl:template match="cms:taskComment">
    <xsl:if test="@comment != '' and @comment != ' '">
      <span class="cmsTaskComment">
        <xsl:value-of select="@comment"/>
        <br />
        <span class="cmsTaskCommentCredentials">
          <xsl:value-of select="@date"/>
          &nbsp;
          <xsl:value-of select="@author"/>
        </span>
      </span>
    </xsl:if>
  </xsl:template>
  
  <!-- DE Zeigt Informationen über den Verlauf an -->
  <!-- EN Shows information about revisions -->
  <xsl:template match="cms:transactionSummary">
    <div class="cmsSummarySection">
      <h3 class="cmsSummaryHeading">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'summary/revisionSummary/header'"/>
        </xsl:call-template>
      </h3>
      <div class="cmsSummaryBody table">
        <div class="tableRow">
          <span class="cmsCurrentRevision tableCell">
            <xsl:value-of select="@lastModifiedDate"/>
          </span>
          <span class="cmsCurrentRevision tableCell">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/revisionSummary/currentRevision'"/>
            </xsl:call-template>
          </span>
        </div>
        <xsl:apply-templates/>
        <div class="tableRow">
          <span class="cmsInitialRevision tableCell">
            <xsl:value-of select="@creationDate"/>
          </span>
          <span class="cmsInitialRevision tableCell">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/revisionSummary/initialRevision'"/>
            </xsl:call-template>
          </span>
        </div>
      </div>
    </div>
  </xsl:template>
  
  <!-- DE Zeigt Informationen über eine Transaktion an -->
  <!-- EN Shows information about transactions -->
  <xsl:template match="cms:transaction">
    <div class="cmsTransaction tableRow">
      <span class="cmsTransactionDate tableCell">
        <a href="/ccm{@url}">
          <xsl:attribute name="title">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/revisionSummary/viewRevision'"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:attribute name="alt">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'summary/revisionSummary/viewRevision'"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:value-of select="@date"/>
        </a>
      </span>
      <span class="cmsTransactionAuthor tableCell">
        <xsl:value-of select="@author"/>
      </span>
      <span class="cmsTransactionLink tableCell">
      </span>
    </div>
  </xsl:template>
    
</xsl:stylesheet>
