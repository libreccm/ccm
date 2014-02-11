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

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  version="1.0" exclude-result-prefixes="bebop cms ui aplaws xsl">

  <xsl:template match="cms:emptyPage[@title='childCategories']">
    <xsl:apply-templates select="cms:category/cms:category" mode="cms:javascriptCat">
      <xsl:with-param name="expand" select="'none'"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="cms:emptyPage[@title='autoCategories']">
    <xsl:apply-templates select="cms:category" mode="cms:javascriptCat" />
  </xsl:template>

</xsl:stylesheet>
