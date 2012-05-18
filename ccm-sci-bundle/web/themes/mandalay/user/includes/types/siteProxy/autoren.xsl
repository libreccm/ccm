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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
		xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
		xmlns:mandalay="http://mandalay.quasiweb.de"
		xmlns:dabin="http://dabin.quasiweb.de"
		exclude-result-prefixes="xsl bebop cms"
		version="1.0">

  <!-- DE DaBIn Person-Detailausgabe -->
  <xsl:template match="dabin:autoren" mode="detailed_view">
    <!--     <ul class="autorenliste"> -->
    <xsl:for-each select="dabin:autor">
      <!--         <li class="autor"> -->
      <xsl:choose>
        <xsl:when test="dabin:homepage">
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="dabin:homepage"/>
            </xsl:attribute>
            <xsl:value-of select="dabin:vorname" disable-output-escaping="yes"/>
            &nbsp;
            <xsl:value-of select="dabin:nachname" disable-output-escaping="yes"/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="dabin:vorname" disable-output-escaping="yes"/>
          &nbsp;
          <xsl:value-of select="dabin:nachname" disable-output-escaping="yes"/>          
        </xsl:otherwise>
      </xsl:choose>
      <!--         </li> --><br/>
    </xsl:for-each>
    <!--     </ul> -->
  </xsl:template>


  <!-- DE DaBIn Autoren-Listausgabe -->
  <xsl:template match="dabin:autoren" mode="list_view">
    <span class="autorenliste">
      <xsl:for-each select="dabin:autor[position()&lt;last()]">
        <span class="autor">
          <xsl:value-of select="dabin:vorname" disable-output-escaping="yes"/>
          &nbsp;
          <xsl:value-of select="dabin:nachname" disable-output-escaping="yes"/>
          <xsl:text>, </xsl:text>
        </span>
      </xsl:for-each>
      <span class="autor">
        <xsl:value-of select="dabin:autor[last()]/dabin:vorname" disable-output-escaping="yes"/>
        &nbsp;
        <xsl:value-of select="dabin:autor[last()]/dabin:nachname" disable-output-escaping="yes"/>
        <xsl:text> </xsl:text>
      </span>
    </span>
  </xsl:template>

</xsl:stylesheet>
