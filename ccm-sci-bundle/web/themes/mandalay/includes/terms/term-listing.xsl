<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  version="1.0">

  <xsl:template match="terms:allTermListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>All Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:rootTermListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>Root Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:orphanedTermListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>Orphaned Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:relatedTermListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>Related Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:preferredTermListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>Preferred Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:nonPreferredTermListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>Non-preferred Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:narrowerTermListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>Narrower Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:broaderTermListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>Broader Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:termFilteredListing">
    <xsl:call-template name="terms:termListing">
      <xsl:with-param name="heading"><xsl:text>Search Terms</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="terms:termListing">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template name="terms:termListing">
    <xsl:param name="heading" select="'Terms'"/>

    <table class="termListing">
      <thead>
        <tr><th colspan="3"><xsl:value-of select="$heading"/></th></tr>
      </thead>
      <tbody>
        <xsl:for-each select="terms:object">
          <xsl:variable name="class">
            <xsl:choose>
              <xsl:when test="position() mod 2 = 0">
                <xsl:text>even</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>odd</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          
          <tr class="{$class}">
            <td width="20%">
              <xsl:value-of select="terms:uniqueID"/>
            </td>
            <xsl:choose>
              <xsl:when test="@isSelected">
                <th>
                  <xsl:value-of select="terms:model/terms:name"/>
                  <xsl:if test="terms:domain/terms:key">
                    <xsl:text> (</xsl:text>
                    <xsl:value-of select="terms:domain/terms:key"/>
                    <xsl:text>)</xsl:text>
                  </xsl:if>
                </th>
              </xsl:when>
              <xsl:otherwise>
                <td>
                  <a href="{terms:action[@name='view']/@url}"><xsl:value-of select="terms:model/terms:name"/></a>
                  <xsl:if test="terms:domain/terms:key">
                    <xsl:text> (</xsl:text>
                    <xsl:value-of select="terms:domain/terms:key"/>
                    <xsl:text>)</xsl:text>
                  </xsl:if>
                </td>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="terms:action[@name='remove']">
              <td width="20%">
                <a href="{terms:action[@name='remove']/@url}">Remove</a>
              </td>
            </xsl:if>
          </tr>
        </xsl:for-each>
        <xsl:if test="count(terms:object) = 0">
          <tr><td colspan="3">There are no terms</td></tr>
        </xsl:if>
      </tbody>
      <xsl:if test="terms:paginator">
        <tfoot>
          <tr>
            <td colspan="3">
              <xsl:apply-templates select="terms:paginator"/>
            </td>
          </tr>
        </tfoot>
      </xsl:if>
    </table>
  </xsl:template>

  <xsl:template match="terms:paginator">
    <xsl:if test="@pageNumber > 1">
      <a href="{@baseURL}&amp;{@pageParam}={@pageNumber - 1}">&lt;&lt;&lt;Previous</a><xsl:text> </xsl:text>
    </xsl:if>
    <xsl:text>Page </xsl:text>
    <xsl:value-of select="@pageNumber"/>
    <xsl:text> of </xsl:text>
    <xsl:value-of select="@pageCount"/>
    <xsl:if test="@pageNumber &lt; @pageCount">
      <xsl:text> </xsl:text><a href="{@baseURL}&amp;{@pageParam}={@pageNumber + 1}">Next&gt;&gt;&gt;</a>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
