<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2013 Jens Pelzetter
  
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
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0">

	
	<xsl:template match="nav:categorisedObjectList">
		<h1>TEST</h1>
	</xsl:template>

	<xsl:template match="category"
	     name="mandalay:categorisedObjectList">
		<h1>CategorisedObjectList</h1>
		<xsl:for-each select="./category">
			<h2><xsl:value-of select="./@name"/></h2>
			<xsl:apply-templates/>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>