<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

<!-- 
    Copyright: 2014 Jens Pelzetter
  
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
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:mandalay="http://mandalay.quasiweb.de"
                exclude-result-prefixes="xsl bebop cms ui mandalay nav"
                version="1.0">

    <xsl:template name="mandalay:minimizeHeader">
        <xsl:param name="imageClass" select="''"/>
        <xsl:param name="linkClass" select="''"/>
        <xsl:param name="minimizeText" select="concat($imageClass, '/minimize')"/>
        <xsl:param name="maximizeText" select="concat($imageClass, '/maximize')"/>
        
        <xsl:variable name="minimizeLabel">
            <xsl:choose>
                <xsl:when test="string-length($minimizeText) &gt; 0"> 
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="id" select="$minimizeText"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="id" select="concat($imageClass, '/minimize')"/>
                    </xsl:call-template>                    
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="maximizeLabel">
            <xsl:choose>
                <xsl:when test="string-length($minimizeText) &gt; 0"> 
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="id" select="$maximizeText"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="id" select="concat($imageClass, '/maximize')"/>
                    </xsl:call-template>                    
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>          
                
        <a>
            <xsl:attribute name="class">
                <xsl:value-of select="concat($linkClass, ' maximized')"/>
            </xsl:attribute>
            <xsl:attribute name="href">
                <xsl:value-of select="'#'"/>
            </xsl:attribute>
            <xsl:attribute name="onClick">
                <!--<xsl:value-of select="concat('$(&quot;.', $imageID, ' img&quot;).attr(&quot;src&quot;, &quot;', $minimizedImage, '&quot;);return false;')"/>-->
                <xsl:value-of select="concat('minimizeImage.minimize(&quot;', $imageClass, '&quot;, &quot;', $linkClass ,'&quot;, &quot;', $minimizeLabel, '&quot;, &quot;', $maximizeLabel, '&quot;)')"/>
            </xsl:attribute>
            <xsl:value-of select="$minimizeLabel"/>
            
        </a>
        <script type="text/javascript">
            <xsl:value-of select="'$(document).ready(function() {'"/>          
            <xsl:value-of select="concat('minimizeImage.restore(&quot;', $imageClass, '&quot;, &quot;', $linkClass ,'&quot;, &quot;', $minimizeLabel, '&quot;, &quot;', $maximizeLabel, '&quot;)')"/>            
            <xsl:value-of select="'});'"/>
        </script>
    </xsl:template>

</xsl:stylesheet>
