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
    Dies ist die Importdatei für Bebop. Sie importiert alle XSL-Dateien aus
    dem Unterverzeichnis bebop. Dies ist der einzige Ort in diesem Theme,
    indem die Dateien importiert werden dürfen.
-->

<!-- EN
    This is the import file for bebop. It is importing all xsl files from
    the bebop subfolder. This is the only place in this theme where these
    files are allowed to be imported.
--> 

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0">
  

<!-- catch-all rules must have lowest precedence -->
<!--
<xsl:import href="plain.xsl"/>
-->

  <xsl:import href="bebop/bebopActionGroup.xsl"/>
  <xsl:import href="bebop/bebopContextBar.xsl"/>
  <xsl:import href="bebop/bebopDebug.xsl"/>
  <xsl:import href="bebop/bebopDoubleClickProtection.xsl"/>
  <xsl:import href="bebop/bebopEditors.xsl"/>
  <xsl:import href="bebop/bebopForm.xsl"/>
  <xsl:import href="bebop/bebopImage.xsl"/>
  <xsl:import href="bebop/bebopLabel.xsl"/>
  <xsl:import href="bebop/bebopLink.xsl"/>
  <xsl:import href="bebop/bebopList.xsl"/>
  <xsl:import href="bebop/bebopPadding.xsl"/>
  <xsl:import href="bebop/bebopPage.xsl"/>
  <xsl:import href="bebop/bebopPanel.xsl"/>
  <xsl:import href="bebop/bebopPropertyList.xsl"/>
  <xsl:import href="bebop/bebopTabbedPane.xsl"/>
  <xsl:import href="bebop/bebopTable.xsl"/>
  <xsl:import href="bebop/bebopTree.xsl"/>
  <xsl:import href="bebop/bebopWidget.xsl"/>

</xsl:stylesheet>
