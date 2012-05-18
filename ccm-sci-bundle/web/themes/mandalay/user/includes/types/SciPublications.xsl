<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

<!-- 
    Copyright: 2010 Jens Pelzetter
  
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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
		xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
		xmlns:mandalay="http://mandalay.quasiweb.de"
		xmlns:dabin="http://dabin.quasiweb.de"
		exclude-result-prefixes="xsl bebop cms mandalay dabin"
		version="1.0">

  <xsl:import href="SciPublications/common.xsl"/>
  <xsl:import href="SciPublications/exportLinks.xsl"/>
  <xsl:import href="SciPublications/formatParser.xsl"/>

  <xsl:import href="SciPublications/SciPublicationsList.xsl"/>

  <xsl:import href="SciPublications/ArticleInCollectedVolume.xsl"/>
  <xsl:import href="SciPublications/ArticleInJournal.xsl"/>
  <xsl:import href="SciPublications/CollectedVolume.xsl"/>
  <xsl:import href="SciPublications/Expertise.xsl"/>
  <xsl:import href="SciPublications/GreyLiterature.xsl"/>
  <xsl:import href="SciPublications/InProceedings.xsl"/>
  <xsl:import href="SciPublications/InternetArticle.xsl"/>
  <xsl:import href="SciPublications/Journal.xsl"/>
  <xsl:import href="SciPublications/Monograph.xsl"/>
  <xsl:import href="SciPublications/Proceedings.xsl"/>
  <xsl:import href="SciPublications/Publisher.xsl"/>
  <xsl:import href="SciPublications/Review.xsl"/>
  <xsl:import href="SciPublications/SciAuthor.xsl"/>
  <xsl:import href="SciPublications/Series.xsl"/>
  <xsl:import href="SciPublications/UnPublished.xsl"/>
  <xsl:import href="SciPublications/WorkingPaper.xsl"/>

</xsl:stylesheet>