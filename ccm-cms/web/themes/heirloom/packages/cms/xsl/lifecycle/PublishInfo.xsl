<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  exclude-result-prefixes="cms"
                  version="1.0">
                      
  <!-- Display text for an item that has been scheduled for publication
       but has not gone live. -->
  <xsl:template name="scheduledItem">
    <xsl:param name="startDate"/>
    <xsl:param name="endDate"/>

    <p>This item has been scheduled for publication.</p>

    <p>
      It is scheduled to go live on <xsl:value-of select="@startDate"/> and
      <xsl:choose>
        <xsl:when test="not($endDate='')">
          expire on <xsl:value-of select="$endDate"/>.
        </xsl:when>
        <xsl:otherwise>
          never expire.
        </xsl:otherwise>
      </xsl:choose>
    </p>
  </xsl:template>

  <!-- Display text for an item that has been published and is live. -->
  <xsl:template name="liveItem">
    <xsl:param name="startDate"/>
    <xsl:param name="endDate"/>

    <p>This item is <font color="red"><b>LIVE</b></font>.</p>

    <p>
      It was published on <xsl:value-of select="@startDate"/>
      and is scheduled to
      <xsl:choose>
        <xsl:when test="not($endDate='')">
          expire on <xsl:value-of select="$endDate"/>.
        </xsl:when>
        <xsl:otherwise>
          never expire.
        </xsl:otherwise>
      </xsl:choose>
    </p>
  </xsl:template>

  <!-- Display text for an item that has been published and is expired. -->
  <xsl:template name="expiredItem">
    <xsl:param name="startDate"/>
    <xsl:param name="endDate"/>

    <p>This item has <b>expired</b>.</p>

    <p>It was published on <xsl:value-of select="$startDate"/>
    and expired on <xsl:value-of select="$endDate"/>.</p>
  </xsl:template>
  
</xsl:stylesheet>
