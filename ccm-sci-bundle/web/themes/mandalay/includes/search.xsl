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
  Dies ist die Importdatei für Search. Sie importiert alle XSL-Dateien aus
  dem Unterverzeichnis search. Dies ist der einzige Ort in diesem Theme,
  indem die Dateien importiert werden dürfen.
-->

<!-- EN
  This is the import file for search. It is importing all xsl files from
  the search subfolder. This is the only place in this theme where these
  files are allowed to be imported.
--> 

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav mandalay" 
  version="1.0">
  
  <xsl:import href="search/searchDocuments.xsl"/>
  <xsl:import href="search/searchFilter.xsl"/>
  <xsl:import href="search/searchObject.xsl"/>
  <xsl:import href="search/searchPartyText.xsl"/>
  <xsl:import href="search/searchQuery.xsl"/>
  <xsl:import href="search/searchResults.xsl"/>
  <xsl:import href="search/searchTerms.xsl"/>
  <xsl:import href="search/filterControls.xsl"/>
  
</xsl:stylesheet>