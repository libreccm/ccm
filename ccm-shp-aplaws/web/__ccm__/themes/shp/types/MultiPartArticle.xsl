<!DOCTYPE stylesheet [<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns:shp="http://www.shp.de"
                exclude-result-prefixes="xsl bebop cms ui"
                version="1.0">

  <xsl:template name="CT_MultiPartArticle_graphics">
    <div id="greeting">
      <xsl:if test="./summary">
        <div id="lead">
          <xsl:value-of disable-output-escaping="yes" select="./summary"/>
        </div>
      </xsl:if>
    </div>
    <span class="hide">|</span>
    <div id="sectionList">	
      <div id="caption">Teile</div>
      <xsl:for-each select="./sections">
        <xsl:sort select="./rank" data-type="number"/>
        <xsl:variable name="currentRank" select="./rank"/>
        <xsl:variable name="prevRank" select="number($currentRank -1)"/>
        <xsl:variable name="Page" select="count(../sections[./pageBreak ='true' and ./rank &lt; $currentRank]) + 1"/>
        <xsl:variable name="prevPage" select="count(../sections[./pageBreak ='true' and ./rank &lt; $prevRank]) + 1"/>
        <xsl:variable name="internalRank" select="count(../sections[./pageBreak ='false' and ./rank &lt; $currentRank]) + 1"/>
        <xsl:choose>
          <xsl:when test="@oid = ../../../cms:articleSectionPanel/cms:item[position() = 1]/@oid">
            <xsl:value-of select="title"/>
          </xsl:when>
          <xsl:when test="@oid = ../../../cms:articleSectionPanel/cms:item[position() != 1]/@oid">
            <a>
              <xsl:attribute name="href">#internalSection<xsl:value-of select="$internalRank" /></xsl:attribute>
              <xsl:attribute name="title">Abschnitt <xsl:value-of select="$internalRank" /></xsl:attribute>
              <xsl:value-of select="title"/>
            </a>
          </xsl:when>
          <xsl:when test="($Page = $prevPage) and $Page!='1'">
            <a>
              <xsl:attribute name="href">?page=<xsl:value-of select="$Page" />#internalSection<xsl:value-of select="$internalRank" /></xsl:attribute>
              <xsl:attribute name="title">Seite <xsl:value-of select="$Page" /> Abschnitt <xsl:value-of select="$internalRank" /></xsl:attribute>
              <xsl:value-of select="title"/>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <a href="?page={$Page}" title="Seite {$Page}">
              <xsl:value-of select="title"/>
            </a>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="not(position()=last())"><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;|<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;</xsl:if>
      </xsl:for-each>
    </div>
    <xsl:for-each select="/bebop:page/cms:articleSectionPanel/cms:item">
      <div class="mainBody">
        <xsl:call-template name="shp:imageAttachments">
          <xsl:with-param name="showCaption" select="'true'" />
        </xsl:call-template>
        <xsl:choose>
          <xsl:when test="position() =1">
            <h2><xsl:value-of disable-output-escaping="yes" select="title"/></h2>
          </xsl:when>
          <xsl:otherwise>
            <h2>
              <a class="intLink">
                <xsl:attribute name="name">internalSection<xsl:value-of select="position()" /></xsl:attribute>
              </a>
              <xsl:value-of disable-output-escaping="yes" select="title"/>
            </h2>
            <div class="top"><a href="#top" class="topLink" title="Seitenanfang">Seitenanfang</a></div>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:for-each select="text">
          <xsl:value-of disable-output-escaping="yes" select="content"/>
        </xsl:for-each>
      </div>
    </xsl:for-each>
    <div id="mpaDirection">
      <xsl:for-each select="./sections">
        <xsl:sort select="./rank"/>
        <xsl:variable name="currentRank" select="./rank"/>
        <xsl:variable name="Page" select="count(../sections[./pageBreak ='true' and ./rank &lt; $currentRank]) + 1"/>
        <xsl:variable name="internalRank" select="count(../sections[./pageBreak ='false' and ./rank &lt; $currentRank]) + 1"/>
        <xsl:choose>
          <xsl:when test="@oid = ../../../cms:articleSectionPanel/cms:item[position() = 1]/@oid">
            <xsl:if test="position() !='1'">
              <a class="prev" href="?page={$Page -1}" title="Vorherige Seite">
                Vorherige Seite
              </a>
            </xsl:if>
            |
            <xsl:if test="(number(rank))!=(count(../sections))">
              <a class="next" href="?page={$Page +1}" title="Nächste Seite">
                Nächste Seite
              </a>
            </xsl:if>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </div>
  </xsl:template>

</xsl:stylesheet>