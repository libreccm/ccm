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
  Dies ist die Importdatei für die benutzerdefinierten ContentTypen. Sie importiert
  alle XSL-Dateien aus dem Verzeichnis types. Dies ist der einzige Ort in diesem Theme,
  indem die Dateien importiert werden dürfen.
-->

<!-- EN
  This is the import file for the user-defined content types. It is importing all
  xsl files from the types folder. This is the only place in this theme where these
  files are allowed to be imported.
--> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                exclude-result-prefixes="xsl bebop cms"
  version="1.0">

  <!--
    <xsl:import href="types/XXX.xsl"/>
  -->
  <xsl:import href="types/ContentBundle.xsl"/>
  <xsl:import href="types/DecisionTree.xsl"/>  
  <xsl:import href="types/PublicPersonalProfile.xsl"/>
  <xsl:import href="types/SciDepartment.xsl"/>
  <xsl:import href="types/SciInstitute.xsl"/>
  <xsl:import href="types/SciMember.xsl"/>
  <xsl:import href="types/SciProject.xsl"/>
  <xsl:import href="types/SciPublications.xsl"/>
  <xsl:import href="types/SimpleOrganization.xsl"/>
  <xsl:import href="types/SiteProxy.xsl"/>
  
</xsl:stylesheet>
