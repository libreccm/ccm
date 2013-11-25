<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
     Copyright 2011, Jens Pelzetter

         
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
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0"
  >

  <!-- 
       ************************************************
       ** Template for the DescTab for a SciProject  **
       ************************************************
  -->

  <xsl:template name="CT_SciDepartment_graphics"
		match="text"
		mode="tabs">

    <div class="activeTab">

    <h2>
        <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'GenericOrganizationalUnit'"/>
            <xsl:with-param name="id" select="concat('textTab/',./@key)"/>
        </xsl:call-template>      
       </h2>


    <div class="orgaUnitText">
      <xsl:value-of disable-output-escaping="yes" select="."/>
    </div>

    <div class="endFloat"/>
    </div>

  </xsl:template>
</xsl:stylesheet>
