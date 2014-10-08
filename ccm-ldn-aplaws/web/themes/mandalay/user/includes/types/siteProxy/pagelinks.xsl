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

  <xsl:template match="dabin:pages" mode="list_view">
    <div class="dabinPageLinks">
      <xsl:if test="dabin:lastOffset">
        <a>
          <xsl:attribute name="href">
            <xsl:text>?offset=</xsl:text>
            <xsl:value-of select="dabin:lastOffset"/>
            <xsl:text>&amp;limit=</xsl:text>
            <xsl:value-of select="dabin:limit"/>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'DaBIn'" />
            <xsl:with-param name="id" select="'lastPage'" />
          </xsl:call-template>
        </a>
      </xsl:if>
      <xsl:if test="dabin:lastYear">
        <a>
          <xsl:attribute name="href">
            <xsl:text>?year=</xsl:text>
            <xsl:value-of select="dabin:lastYear"/>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'DaBIn'" />
            <xsl:with-param name="id" select="'lastPage'" />
          </xsl:call-template>
        </a>
      </xsl:if>
      <div class="cssComboBox">
        <span class="cssComboBoxClosed">
          <xsl:choose>
            <xsl:when test="../dabin:arbeitspapier">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'DaBIn'" />
                <xsl:with-param name="id" select="'showingWorkingPapersYear'" />
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'DaBIn'" />
                <xsl:with-param name="id" select="'showingPublicationsYear'" />
              </xsl:call-template>              
            </xsl:otherwise>
          </xsl:choose>
          <!-- <xsl:choose>
            <xsl:when test="dabin:lastOffset">
              <xsl:value-of select="dabin:lastOffset+dabin:limit+1" />
            </xsl:when>
            <xsl:otherwise>
               1
            </xsl:otherwise>
          </xsl:choose>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'DaBIn'" />
            <xsl:with-param name="id" select="'to'" />
          </xsl:call-template> 
          <xsl:choose>
            <xsl:when test="dabin:lastOffset">
            <xsl:choose>
               <xsl:when test="dabin:offset_max">
                  <xsl:choose>
                     <xsl:when test="dabin:lastOffset+dabin:limit+dabin:limit&gt;dabin:offset_max">
                        <xsl:value-of select="dabin:lastOffset+dabin:limit+dabin:offset_max"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="dabin:lastOffset+dabin:limit+dabin:limit"/>
                     </xsl:otherwise>
                  </xsl:choose>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="dabin:lastOffset+dabin:limit+dabin:limit"/>
               </xsl:otherwise>
            </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="dabin:limit"/>
            </xsl:otherwise> 
          </xsl:choose> -->
          &nbsp;<xsl:value-of select="dabin:currentYear"/>
        </span>
        <ul class="cssComboBoxOpen">
          <xsl:for-each select="dabin:page">
            <li>
              <!-- <xsl:element  name="a">
                <xsl:attribute name="href">
                  <xsl:text>?offset=</xsl:text>
                  <xsl:value-of select="dabin:offset"/>
                  <xsl:text>&amp;limit=</xsl:text>
                  <xsl:value-of select="../dabin:limit"/>
                </xsl:attribute>
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'DaBIn'" />
                  <xsl:with-param name="id" select="'showing'" /> 
               </xsl:call-template>                          
                <xsl:value-of select="dabin:offset+1" />
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'DaBIn'" />
                  <xsl:with-param name="id" select="'to'" />
                </xsl:call-template>                          
                <xsl:value-of select="dabin:offset+../dabin:limit" />
              </xsl:element> -->
              <!-- <xsl:element name="a">
                <xsl:attribute name="href">
                  <xsl:text>?year=</xsl:text>
                  <xsl:value-of select="dabin:year"/>
                </xsl:attribute>
              </xsl:element> -->
              <a>
                <xsl:attribute name="href">
                  <xsl:text>?year=</xsl:text>
                  <xsl:value-of select="dabin:year"/>
                </xsl:attribute>
                <xsl:choose>
                  <xsl:when test="../../dabin:arbeitspapier">
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'DaBIn'" />
                      <xsl:with-param name="id" select="'showingWorkingPapersYear'" />
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'DaBIn'" />
                      <xsl:with-param name="id" select="'showingPublicationsYear'" />
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
                &nbsp;<xsl:value-of select="dabin:year"/>
              </a>
            </li>
          </xsl:for-each>
      </ul>
    </div>
      <xsl:if test="dabin:nextOffset">
        <a>
          <xsl:attribute name="href">
            <xsl:text>?offset=</xsl:text>
            <xsl:value-of select="dabin:nextOffset"/>
            <xsl:text>&amp;limit=</xsl:text>
            <xsl:value-of select="dabin:limit"/>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'DaBIn'" />
            <xsl:with-param name="id" select="'nextPage'" />
          </xsl:call-template>
        </a>            
      </xsl:if>
      <xsl:if test="dabin:nextYear">
        <a>
          <xsl:attribute name="href">
            <xsl:text>?year=</xsl:text>
            <xsl:value-of select="dabin:nextYear"/>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'DaBIn'" />
            <xsl:with-param name="id" select="'nextPage'" />
          </xsl:call-template>          
        </a>
      </xsl:if>
    </div>
  </xsl:template>
</xsl:stylesheet>