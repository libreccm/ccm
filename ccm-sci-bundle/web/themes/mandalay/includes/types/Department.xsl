<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2010, Jens Pelzetter
  
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

<xsl:stylesheet   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
		  xmlns:nav="http://ccm.redhat.com/navigation"
		  xmlns:cms="http://www.arsdigita.com/cms/1.0"
		  xmlns:mandalay="http://mandalay.quasiweb.de"
		  exclude-result-prefixes="xsl bebop cms nav"
		  version="1.0">

  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Department']" mode="lead">
    <xsl:call-template name="CT_OrganizationalUnit_lead"/>
  </xsl:template>

  <!-- DE Bild -->
  <!-- EN image -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Department']" mode="image">
    <xsl:call-template name="CT_OrganizationalUnit_image"/>
  </xsl:template>

  <!-- DE Vollansicht -->
  <!-- EN Detailed view -->
  <xsl:template name="CT_Department_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.Department']" mode="detailed_view">
    <!-- Simply call the template for the GenericOrganization -->
    <xsl:call-template name="CT_GenericOrganizationalUnit_graphics"/>
  </xsl:template>

  <!-- DE Listenansicht -->
  <!-- EN List view -->
  <xsl:template name="CT_Department_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Department']" mode="list_view">
    <xsl:call-template name="CT_GenericOrganizationalUnit_List"/>
  </xsl:template>

  <xsl:template name="CT_Department_Link" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Department']" mode="link_view">
    <xsl:call-template name="CT_GenericOrganizationalUnit_List"/>
  </xsl:template>

</xsl:stylesheet>
