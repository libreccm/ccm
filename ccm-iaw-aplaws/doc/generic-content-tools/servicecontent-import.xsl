<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns="http://www.esd.org.uk/standards/esdbody"
  version="1.0" exclude-result-prefixes="esd xsl">

  <xsl:output method="xml"/>

  <xsl:param name="esdID"/>

  <xsl:template match="text()|comment()|processing-instruction()">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="/">
    <xsl:apply-templates select="esd:ServiceContent"/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:element name="{name()}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="esd:ServiceContent">
    <cms:item xmlns:cms="http://www.arsdigita.com/cms/1.0">
      <xsl:attribute name="oid">
        <xsl:value-of select="concat('[com.arsdigita.cms.contenttypes.ESDService:{id=', $esdID, '}]')"/>
      </xsl:attribute>
      <xsl:variable name="name">
        <xsl:call-template name="ConvertToFilename">
          <xsl:with-param name="name">
            <xsl:value-of select="concat(esd:Name, '-', $esdID)"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <cms:name><xsl:value-of select="$name"/></cms:name>
      <cms:language>en</cms:language>
      <cms:title><xsl:value-of select="esd:Title"/></cms:title>
      <xsl:apply-templates select="esd:Metadata">
        <xsl:with-param name="name">
          <xsl:value-of select="$name"/>
        </xsl:with-param>
      </xsl:apply-templates>
      <cms:textAsset>
        <xsl:attribute name="oid">
          <xsl:value-of select="concat('[com.arsdigita.cms.TextAsset:{id=', $esdID, '}]')"/>
        </xsl:attribute>
        <cms:content>
          <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
          <xsl:apply-templates select="esd:Body/*"/>
          <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
        </cms:content>
      </cms:textAsset>

      <xsl:if test="//a[contains(@href, '.pdf') and not(starts-with(@href, 'http') or starts-with(@href, 'www.'))]">
          <cms:links>
            <xsl:attribute name="oid">
              <xsl:value-of select="concat('[com.arsdigita.cms.contentassets.RelatedLink:{id=', $esdID, '}]')"/>
            </xsl:attribute>
            <cms:targetType>internalLink</cms:targetType>
            <cms:linkOrder>1</cms:linkOrder>
            <cms:linkTitle>PDF Form</cms:linkTitle>
            <cms:targetItem>
              <xsl:attribute name="oid">
                <xsl:value-of select="concat('[com.arsdigita.cms.contenttypes.FileStorageItem:{id=', $esdID, '}]')"/>
              </xsl:attribute>
            </cms:targetItem>
          </cms:links>
      </xsl:if>
    </cms:item>
  </xsl:template>

  <xsl:template match="esd:Metadata">
    <xsl:param name="name"/>
    <cms:dublinCore>
      <xsl:attribute name="oid">
        <xsl:value-of select="concat('[com.arsdigita.london.cms.dublin.DublinCoreItem:{id=', $esdID, '}]')"/>
      </xsl:attribute>
      <cms:name><xsl:value-of select="concat($name, '-dublin-metadata')"/></cms:name>
      <cms:dcAudience><xsl:value-of select="esd:Audience"/></cms:dcAudience>
      <cms:dcCoverage><xsl:value-of select="normalize-space(string(esd:Coverage.Spatial))"/></cms:dcCoverage>
      <cms:dcCoveragePostcode></cms:dcCoveragePostcode>
      <cms:dcCoverageSpatialRef></cms:dcCoverageSpatialRef>
      <cms:dcCoverageUnit></cms:dcCoverageUnit>
      <cms:dcDateValid></cms:dcDateValid>
      <cms:dcDisposalReview></cms:dcDisposalReview>
      <cms:dcLanguage>en</cms:dcLanguage>
      <cms:dcTemporalBegin></cms:dcTemporalBegin>
      <cms:dcTemporalEnd></cms:dcTemporalEnd>
      <cms:dcCreatorOwner><xsl:value-of select="esd:Creator"/></cms:dcCreatorOwner>
      <cms:dcCreatorContact><xsl:value-of select="esd:Creator"/></cms:dcCreatorContact>
      <cms:dcPublisher><xsl:value-of select="esd:Publisher"/></cms:dcPublisher>
      <cms:dcRights></cms:dcRights>
      <cms:dcKeywords><xsl:value-of select="esd:Subject.Keyword"/></cms:dcKeywords>
    </cms:dublinCore>
  </xsl:template>

  <!-- Convert the name to a filename-compatible format -->
  <xsl:template name="ConvertToFilename">
    <xsl:param name="name"/>
    <xsl:call-template name="RemoveApos">
      <xsl:with-param name="name" select="translate($name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ /\*?!#%^+=&#34;&#38;&#58;&#59;$,`~', 'abcdefghijklmnopqrstuvwxyz---')"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="RemoveApos">
    <xsl:param name="name"/>
    <xsl:call-template name="RemoveTripleHyphens">
      <xsl:with-param name="name" select='translate($name, "&#39;", "")'/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="RemoveTripleHyphens">
    <xsl:param name="name"/>
    <xsl:choose>
      <xsl:when test="contains($name, '---')">
        <xsl:call-template name="RemoveTripleHyphens">
          <xsl:with-param name="name" select="concat(substring-before($name, '---'), '--', substring-after($name, '---'))"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$name"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="a">
    <xsl:choose>
      <xsl:when test="starts-with(@href, 'http://') or starts-with(@href, '#')">
        <a href="{@href}">
          <xsl:apply-templates select="node()"/>
        </a>
      </xsl:when>
      <xsl:when test="contains(@href, '.htm')">
        <a href="/ccm/services/pid.jsp?pid={substring(@href, 0, string-length(@href) - 3)}">
          <xsl:apply-templates select="node()"/>
        </a>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
