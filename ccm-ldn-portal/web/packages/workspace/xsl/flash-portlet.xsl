<?xml version="1.0" encoding="utf-8"?>
<!--
  Copyright (C) 2008 Permeance Technologies Pty Ltd. All Rights Reserved.
  
  This library is free software; you can redistribute it and/or modify it under
  the terms of the GNU Lesser General Public License as published by the Free
  Software Foundation; either version 2.1 of the License, or (at your option)
  any later version.
  
  This library is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
  details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0" version="1.0" exclude-result-prefixes="portlet">

  <xsl:template match="portlet:flash">
    <xsl:variable name="playerId">player_<xsl:value-of select="generate-id(.)"/></xsl:variable>
    <div>
        <xsl:attribute name="id"><xsl:value-of select="$playerId"/></xsl:attribute>
        <script type="text/javascript">
            var var_<xsl:value-of select="$playerId"/> = new SWFObject("<xsl:value-of select="@swfFile"/>", "<xsl:value-of select="$playerId"/>", "<xsl:value-of select="@width"/>", "<xsl:value-of select="@height"/>", "<xsl:value-of select="@version"/>", "<xsl:value-of select="@backgroundColour"/>", "<xsl:value-of select="@quality"/>", "<xsl:value-of select="@xiRedirectUrl"/>", "<xsl:value-of select="@redirectUrl"/>", "<xsl:value-of select="@detectKey"/>");
            <xsl:for-each select="portlet:parameters/portlet:parameter">
            var_<xsl:value-of select="$playerId"/>.addParam("<xsl:value-of select="@name"/>","<xsl:value-of select="@value"/>");
            </xsl:for-each>
            <xsl:for-each select="portlet:variables/portlet:variable">
            var_<xsl:value-of select="$playerId"/>.addVariable("<xsl:value-of select="@name"/>","<xsl:value-of select="@value"/>");
            </xsl:for-each>
            var_<xsl:value-of select="$playerId"/>.write('<xsl:value-of select="$playerId"/>');
        </script>
    </div>    
  </xsl:template>
  
</xsl:stylesheet>
