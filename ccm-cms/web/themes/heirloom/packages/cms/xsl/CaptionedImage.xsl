<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  exclude-result-prefixes="cms" 
                  version="1.0">

  <xsl:output method="html" indent="yes"/>

  <xsl:template match="cms:imageDisplay">
    <table>
      <tr>
        <td class="form_label" valign="top">Name:</td>
        <td class="form_value" valign="top">
          <xsl:value-of select="@name"/>
        </td>
      </tr>
      <tr>
        <td class="form_label" valign="top">Image Type:</td>
        <td class="form_value" valign="top">
          <xsl:choose>
            <xsl:when test="@mime_type">
              <xsl:value-of select="@mime_type"/>
            </xsl:when>
            <xsl:otherwise>
              <em>Unknown</em>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
      <tr>
        <td class="form_label" valign="top">Width:</td>
        <td class="form_value" valign="top">
          <xsl:choose>
            <xsl:when test="@width">
              <xsl:value-of select="@width"/>
            </xsl:when>
            <xsl:otherwise>
              <em>Unknown</em>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
      <tr>
        <td class="form_label" valign="top">Height:</td>
        <td class="form_value" valign="top">
          <xsl:choose>
            <xsl:when test="@height">
              <xsl:value-of select="@height"/>
            </xsl:when>
            <xsl:otherwise>
              <em>Unknown</em>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
      <tr>
        <td class="form_label" valign="top">Caption:</td>
        <td class="form_value" valign="top">
          <xsl:choose>
            <xsl:when test="@caption">
              <xsl:value-of select="@caption"/>
            </xsl:when>
            <xsl:otherwise>
              <em>No caption</em>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <img src="{@src}" alt="{@name}">
            <xsl:if test="@width">
              <xsl:attribute name="width">
                <xsl:value-of select="@width"/></xsl:attribute>              
            </xsl:if>
            <xsl:if test="@height">
              <xsl:attribute name="height">
                <xsl:value-of select="@height"/></xsl:attribute>              
            </xsl:if>
          </img>
        </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
