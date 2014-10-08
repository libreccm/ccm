<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

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

<!-- DE
  Hier wird die Portal Admin-Sitemap verarbeitet 
-->

<!-- EN
  Processing portal admin sitemap
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  exclude-result-prefixes="xsl bebop cms nav mandalay portal portlet"
  version="1.0">

  <xsl:template match="portal:sitemap">
    <div class="portalSitemap">
      <xsl:apply-templates select="portal:applicationPane"/>
      <xsl:apply-templates select="*[not(self::portal:applicationPane)]"/>
    </div>
  </xsl:template>

  <xsl:template match="portal:applicationList">
    <h3>
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'portal'"/>
        <xsl:with-param name="id" select="'sitemap/allApplications'"/>
      </xsl:call-template>
    </h3>
    <ul class="portalApplicationList">
      <xsl:apply-templates>
        <!-- ZZZ SortList -->
        <xsl:sort select="primaryURL"/>
      </xsl:apply-templates>
    </ul>
  </xsl:template>
  
  <xsl:template match="portal:applicationPane">
    <div class="portalApplicationPane">
      <h3>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'portal'"/>
          <xsl:with-param name="id" select="'admin/applicationDetails/header'"/>
        </xsl:call-template>
      </h3>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="portal:application">
    <li class="portalApplication">
      <h4>
        <a>
          <xsl:attribute name="href">
            <xsl:call-template name="mandalay:linkParser">
              <xsl:with-param name="link" select="@primaryURL"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:value-of select="title"/>
          </xsl:attribute>
          <xsl:value-of select="title"/>
        </a>
        &nbsp;
        <span class="portalApplicationEditLink">
          <a>
            <xsl:attribute name="href">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link" select="@viewURL"/>
                <xsl:with-param name="prefix" select="''"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'portal'"/>
              <xsl:with-param name="id" select="'sitemap/editDetails'"/>
            </xsl:call-template>
          </a>
        </span>
      </h4>
      <span class="appClass">
        <xsl:value-of select="@appClass"/>
      </span>
    </li>
  </xsl:template>
  
  <xsl:template match="portal:applicationDetails">
    <div class="portalApplicationDetails">
      <b>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'portal'"/>
          <xsl:with-param name="id" select="'sitemap/applicationDetails/title'"/>
        </xsl:call-template>
      </b>
      <xsl:value-of select="title"/>
      <br/>
      <b>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'portal'"/>
          <xsl:with-param name="id" select="'sitemap/applicationDetails/description'"/>
        </xsl:call-template>
      </b>
      <xsl:value-of select="description"/>
      <br/>
      <b>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'portal'"/>
          <xsl:with-param name="id" select="'sitemap/applicationDetails/url'"/>
        </xsl:call-template>
      </b>
      <xsl:value-of select="primaryURL"/>
      <br/>
    </div>
  </xsl:template>
  
  <xsl:template match="portal:newApplication">
    <div class="portalNewApplication">
      <h5>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'portal'"/>
          <xsl:with-param name="id" select="'sitemap/newApplication'"/>
        </xsl:call-template>
      </h5>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

</xsl:stylesheet>
