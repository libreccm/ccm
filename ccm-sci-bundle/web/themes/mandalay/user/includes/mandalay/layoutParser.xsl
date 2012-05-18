<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

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

<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:ppp="http://www.arsdigita.com/PublicPersonalProfile/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav search portal ppp mandalay"
  version="1.0">

  <!-- Autor: Sören Bernstein -->

  <!-- DE Hier können Erweiterungen für den LayoutParser integriert werden, z.B. für eigene Module -->
  <!-- DE Die Templates werden über xsl:apply-templates aufgerufen, also müssen match-Angaben vorhanden sein -->

  <!-- EN this file is for integrating user coded extentions for layoutParser -->
  <!-- EN templates are called by xsl:apply-templates, so there must be a match-attribute for the template -->

  <!-- DE Beispiel template: Zum testen, hier Kommentar entfernen und in der Layout-Datei <userFunction/> eintragen-->
  <!-- EN Example template : to test, remove comment and insert <userFunction/> in layout file-->
  
  <!--<xsl:template match="userFunction">
    <h2>UserFunction</h2>
  </xsl:template>
  -->

  <xsl:template match="showHeaderImage">
    <div>
      <xsl:call-template name="mandalay:setParameters"/>
      <xsl:call-template name="mandalay:headerImage"/>
    </div>
  </xsl:template>

  <xsl:template match="useHomepageTitle">
    <xsl:call-template name="mandalay:homepageTitle"/>
  </xsl:template>

  <xsl:template match="useNavigationHeading">
    <xsl:call-template name="mandalay:navigationHeading"/>
  </xsl:template>

  <xsl:template match="showPublicationExportLinks">
    <xsl:call-template name="showPublicationExportLinks"/>
  </xsl:template>

  <xsl:template match="showPPPOwnerName">
    <xsl:apply-templates select="$resultTree//ppp:ownerName"/>
  </xsl:template>

  <xsl:template match="useOrgaUnitTab">
    <xsl:apply-templates select="$resultTree/orgaUnitTabs/selectedTab/." mode="tabs"/>
  </xsl:template>

   <xsl:template match="piwikJsTracker">
     <xsl:call-template name="piwikJsTracker">
       <xsl:with-param name="piwikUrl" select="./@piwikUrl"/>
       <xsl:with-param name="idSite" select="./@idSite"/>
     </xsl:call-template>
   </xsl:template>
   
   <xsl:template match="piwikImageTracker">
     <xsl:call-template name="piwikImageTracker">
       <xsl:with-param name="piwikUrl" select="./@piwikUrl"/>
       <xsl:with-param name="idSite" select="./@idSite"/>
     </xsl:call-template>       
  </xsl:template>

</xsl:stylesheet> 