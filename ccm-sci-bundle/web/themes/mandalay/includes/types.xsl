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
  Dies ist die Importdatei für die ContentTypen. Sie importiert alle XSL-Dateien
  aus dem Verzeichnis types. Dies ist der einzige Ort in diesem Theme, indem die
  Dateien importiert werden dürfen.
-->

<!-- EN
  This is the import file for the content types. It is importing all xsl files
  from the types folder. This is the only place in this theme where these files
  are allowed to be imported.
--> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                exclude-result-prefixes="xsl bebop cms"
  version="1.0">

  <xsl:import href="types/GenericAddress.xsl"/>
  <xsl:import href="types/GenericContact.xsl"/>
  <xsl:import href="types/GenericPerson.xsl"/>
  <xsl:import href="types/GenericOrganizationalUnit.xsl"/>

  <!--<xsl:import href="/__ccm__/servlet/content-type/index.xsl"/>-->
  <xsl:import href="types/Address.xsl"/>
  <!-- <xsl:import href="types/Agenda.xsl"/> -->
  <xsl:import href="types/Article.xsl"/>
  <xsl:import href="types/Bookmark.xsl"/>
  <xsl:import href="types/Contact.xsl"/>
  <xsl:import href="types/Event.xsl"/>
  <xsl:import href="types/FAQItem.xsl"/>
  <xsl:import href="types/FileStorageItem.xsl"/>
  <xsl:import href="types/Form.xsl"/>
  <xsl:import href="types/GlossaryItem.xsl"/>
  <xsl:import href="types/Image.xsl"/>
  <!-- <xsl:import href="types/InlineSite.xsl"/> -->
  <!-- <xsl:import href="types/Job.xsl"/> -->
  <!-- <xsl:import href="types/LegalNotice.xsl"/> -->
  <xsl:import href="types/Member.xsl"/>
  <!-- <xsl:import href="types/Minutes.xsl"/> -->
  <xsl:import href="types/MultiPartArticle.xsl"/>
  <xsl:import href="types/NewsItem.xsl"/>
  <xsl:import href="types/Person.xsl"/>
  <!-- <xsl:import href="types/PressRelease.xsl"/> -->
  <!-- <xsl:import href="types/Project.xsl"/> -->
  <!-- <xsl:import href="types/ResearchNetwork.xsl"/> -->
  <!-- <xsl:import href="types/Service.xsl"/> -->
  <xsl:import href="types/SiteProxy.xsl"/>
  <xsl:import href="types/Survey.xsl"/>
  
  <!-- DE Rufe die Templates auf -->
  <!-- EN Matching the templates -->
  <xsl:template match="cms:item[objectType and not (@useContext = 'itemAdminSummary')]">
    <xsl:apply-templates select="." mode="detailed_view"/>
  </xsl:template>

</xsl:stylesheet>
