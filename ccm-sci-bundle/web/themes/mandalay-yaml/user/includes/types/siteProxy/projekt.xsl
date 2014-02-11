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


  <xsl:template match="p">
    <p><xsl:apply-templates/></p>
  </xsl:template>

  <!-- DE DaBIn Projekt-Detailausgabe -->
  <xsl:template match="dabin:projekt" mode="detailed_view">
   
    <h2><xsl:value-of select="dabin:name" disable-output-escaping="yes"/></h2>
<!--    Ohne Table -->
<!--    <dl>
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'members'" />
        </xsl:call-template>
      </dt>
      <dd><xsl:apply-templates select="dabin:projektMitarbeiter" mode="detailed_view"/></dd>
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'begin'" />
        </xsl:call-template>
        :&nbsp;&nbsp;<xsl:value-of select="dabin:beginn"/>
      </dt>
      <xsl:if test="string-length(dabin:ende) &gt; 0">
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'end'" />
        </xsl:call-template>
      :&nbsp;&nbsp;<xsl:value-of select="dabin:ende"/>
      </dt>
      </xsl:if>
    <xsl:if test="string-length(dabin:link) &gt; 0">
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'link'" />
        </xsl:call-template>
       :&nbsp;&nbsp;<xsl:value-of select="dabin:link" disable-output-escaping="yes"/>
      </dt>
      </xsl:if>
    <xsl:if test="string-length(dabin:finanzierung) &gt; 0">
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'finanzierung'" />
        </xsl:call-template>
       :&nbsp;&nbsp;<xsl:value-of select="dabin:finanzierung"/>
      </dt>
      </xsl:if>
    <xsl:if test="string-length(dabin:email) &gt; 0">
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'kontakt'" />
        </xsl:call-template>
         :&nbsp;&nbsp;<xsl:value-of select="dabin:email" disable-output-escaping="yes"/>
      </dt>
      </xsl:if>
    </dl>-->


<!--    Mit Table -->
    <table width="100%" class="dabinDesc">
      <tr>
      <td valign="top">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'members'" />
        </xsl:call-template>:
      </td>
      <td colspan="2"><xsl:apply-templates select="dabin:projektMitarbeiter" mode="detailed_view"/></td>
      </tr>
      <tr>
      <td>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'begin'" />
        </xsl:call-template>:
      </td>
      <td>
         <xsl:value-of select="dabin:beginn"/>
      </td>
      <xsl:if test="string-length(dabin:ende) &gt; 0">
      <td>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'end'" />
        </xsl:call-template>:
      </td>
      <td>
         <xsl:value-of select="dabin:ende"/>
      </td>
      </xsl:if>
      </tr>
    <xsl:if test="string-length(dabin:finanzierung) &gt; 0">
      <tr>
      <td valign="top">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'finanzierung'" />
        </xsl:call-template>:
       </td>
       <td colspan="2"> 
         <xsl:value-of select="dabin:finanzierung" disable-output-escaping="no"/>
       </td>
       </tr>
      </xsl:if>
    <xsl:if test="string-length(dabin:email) &gt; 0">
      <tr>
      <td valign="top">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'kontakt'" />
        </xsl:call-template>:
      </td>
      <td colspan="2">
         <xsl:value-of select="dabin:email" disable-output-escaping="yes"/>
      </td>
      </tr>
      </xsl:if>
    <xsl:if test="string-length(dabin:link) &gt; 0">
      <tr>
      <td valign="top">  
      <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'link'" />
      </xsl:call-template>:
      </td>
      <td colspan="2">
         <xsl:value-of select="dabin:link" disable-output-escaping="yes"/>
      </td>
      </tr>
      </xsl:if>
    </table>


    <p>
      <xsl:value-of select="dabin:beschreibung" disable-output-escaping="yes"/>
    </p>

  </xsl:template>

  <!-- DE DaBIn Projekt-Listausgabe -->
  <xsl:template match="dabin:projekt" mode="list_view">
    <li class="dabinProjekt">
      <span class="fullname">
        <a>
          <xsl:attribute name="title">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'DaBIn'" />
              <xsl:with-param name="id" select="'details'" />
            </xsl:call-template>
            <xsl:value-of select="dabin:name" disable-output-escaping="no"/>
          </xsl:attribute>
          <xsl:attribute name="href">
            <xsl:text>?id=</xsl:text>
            <xsl:value-of select="dabin:projektid"/>
<!--            <xsl:text>&amp;lang=</xsl:text>
            <xsl:value-of select="//dabin:lang"/>-->
          </xsl:attribute>
          <xsl:value-of select="dabin:name" disable-output-escaping="yes"/>
        </a>
      </span>
      <!-- <xsl:if test="dabin:beginn or dabin:ende or dabin:abgeschlossen">
	<span class="angaben">
	  <xsl:if test="dabin:beginn">
	    <span class="beginn">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'DaBIn'" />
                <xsl:with-param name="id" select="'begin'" />
              </xsl:call-template>
	      <xsl:value-of select="dabin:beginn"/>
	    </span>
	  </xsl:if>
	  <xsl:if test="dabin:ende">
	    <span class="ende">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'DaBIn'" />
                <xsl:with-param name="id" select="'end'" />
              </xsl:call-template>
	      <xsl:value-of select="dabin:ende"/>
	    </span>
	  </xsl:if>
	  <xsl:if test="dabin:abgeschlossen">
	    <span class="abgeschlossen">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'DaBIn'" />
                <xsl:with-param name="id" select="'finished'" />
              </xsl:call-template>
	      <xsl:if test="dabin:abgeschlossen='YES'">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'DaBIn'" />
                  <xsl:with-param name="id" select="'yes'" />
                </xsl:call-template>
	      </xsl:if>
	      <xsl:if test="dabin:abgeschlossen='NO'">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'DaBIn'" />
                  <xsl:with-param name="id" select="'no'" />
                </xsl:call-template>
	      </xsl:if>
	      !-<xsl:value-of select="dabin:abgeschlossen"/>-
	    </span>
	  </xsl:if>
	</span>
      </xsl:if> -->
      <xsl:apply-templates select="dabin:projektMitarbeiter" mode="list_view"/>
    </li>
  </xsl:template>

  <!-- DE DaBIn MitarbeiterListe-Listausgabe -->
  <xsl:template match="dabin:projektMitarbeiter"  mode="list_view">
    <span class="mitarbeiterliste">
      <xsl:for-each select="dabin:mitarbeiter">
        <span class="mitarbeiter">
          <xsl:value-of select="dabin:vorname" disable-output-escaping="yes"/>&nbsp;<xsl:value-of select="dabin:name" disable-output-escaping="yes"/>
          <xsl:if test="@auftrag = 'Projektleitung'">&nbsp;(Projektleitung)</xsl:if>
          <xsl:choose>
            <xsl:when test="last() &gt; 1 and position() &lt; last()">, </xsl:when>
            <xsl:otherwise>
              <xsl:text> </xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </span>
      </xsl:for-each>
    </span>
  </xsl:template>

  <!-- DE DaBIn MitarbeiterListe-Detailausgabe -->  
  <xsl:template name="projektMitarbeiter" match="dabin:projektMitarbeiter" mode="detailed_view">
    <ul class="mitarbeiterliste">
      <xsl:for-each select="dabin:mitarbeiter">
        <li class="mitarbeiter">
          <xsl:choose>
            <xsl:when test="dabin:homepage">
              <a>
                <xsl:attribute name="href">
                  <xsl:value-of select="dabin:homepage" />
                </xsl:attribute>
                <xsl:value-of select="dabin:vorname"/>
                &nbsp;
                <xsl:value-of select="dabin:name"/>
              </a>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="dabin:vorname"/>
              &nbsp;
              <xsl:value-of select="dabin:name"/>
            </xsl:otherwise>
          </xsl:choose>
          &nbsp;
          <xsl:if test="@auftrag = 'Projektleitung'"> (Projektleitung)</xsl:if>
        </li>
      </xsl:for-each>
    </ul>
  </xsl:template>
</xsl:stylesheet>
