<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:mandalay="http://mandalay.quasiweb.de"
                exclude-result-prefixes="xsl bebop cms"
                version="1.0">

    <xsl:template name="mandalay:itemEditLink">
        <xsl:param name="editUrl" select="''"/>
        <xsl:param name="itemTitle" select="''"/>
        <xsl:variable name="useItemTitleInEditLinkText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'editLinks'"/>                               
                <xsl:with-param name="setting" select="'useItemTitleInEditLink'"/>
                <xsl:with-param name="default" select="'true'"/>            
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="editLinkIcon">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'editLinks'"/>    
                <xsl:with-param name="setting" select="'editLinkIcon'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>                      
        </xsl:variable>
        <xsl:variable name="editLinkTextBeforeItemTitle">
            <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'editLinks'"/>
                <xsl:with-param name="id" select="'editLinkTextBeforeItemTitle'"/>
            </xsl:call-template>            
        </xsl:variable>
        <xsl:variable name="editLinkTextAfterItemTitle">
            <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'editLinks'"/>
                <xsl:with-param name="id" select="'editLinkTextAfterItemTitle'"/>
            </xsl:call-template>                               
        </xsl:variable>
        <xsl:variable name="editLinkText">
            <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'editLinks'"/>
                <xsl:with-param name="id" select="'editLinkText'"/>
            </xsl:call-template>                               
        </xsl:variable>
                
        <xsl:variable name="editLinkLabel">
          <xsl:choose>
            <xsl:when test="($useItemTitleInEditLinkText = 'true') and (string-length($itemTitle) &gt; 0)">
              <xsl:value-of select="$editLinkTextBeforeItemTitle"/>
              <xsl:value-of select="$itemTitle"/>
              <xsl:value-of select="$editLinkTextAfterItemTitle"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$editLinkText"/>
            </xsl:otherwise>
          </xsl:choose>      
        </xsl:variable>
        

            <div class="editLink">
                <a>
                    <xsl:attribute name="href"> 
                        <xsl:value-of select="concat('/ccm', $editUrl)"/>
                    </xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="string-length($editLinkIcon) &gt; 0">
                            <img>
                                <xsl:attribute name="src">
                                    <xsl:value-of select="concat($theme-prefix, $editLinkIcon)"/>
                                </xsl:attribute>
                                <xsl:attribute name="alt">
                                  <xsl:value-of select="$editLinkLabel"/>
                                </xsl:attribute>
                            </img>                          
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$editLinkLabel"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </a>
            </div>        
    </xsl:template>   
    
</xsl:stylesheet>
