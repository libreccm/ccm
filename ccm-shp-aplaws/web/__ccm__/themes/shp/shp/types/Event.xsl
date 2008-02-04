<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
		xmlns:cms="http://www.arsdigita.com/cms/1.0"
		xmlns:shp="http://www.shp.de"
		exclude-result-prefixes="xsl bebop cms"
		version="1.0">		


  <xsl:template name="CT_Event_graphics">
  
      <xsl:if test="./lead">
        <div id="lead">
          <xsl:value-of disable-output-escaping="yes" select="./lead"/>
        </div>
      </xsl:if>
    <div id="details">
      <xsl:call-template name="shp:imageAttachments">
        <xsl:with-param name="showCaption" select="'true'" />
      </xsl:call-template>
      
      <xsl:if test="./location">
        <div id="location">
          <b>Veranstaltungsort</b><br /><br />
          <xsl:value-of disable-output-escaping="yes" select="./location"/>
        </div>
      </xsl:if>
      
      <xsl:choose>
        <xsl:when test="not(./endDate) or ./startDate = ./endDate">
          <!-- Zeige nur das StartDate an -->
          <p>
            <b>Termin:</b><br />
            <xsl:value-of disable-output-escaping="yes" select="./startDate"/>
            <xsl:if test="./startTime and ./endTime and not(./startTime = ./endTime)">
              ::
              <xsl:value-of disable-output-escaping="yes" select="./startTime"/>
              -
              <xsl:value-of disable-output-escaping="yes" select="./endTime"/>
              Uhr
            </xsl:if>
          </p>
          <xsl:if test="not(./endTime) or ./startTime = ./endTime">
            <!-- Zeige nur die StartTime an-->
            <p>
            <b>Beginn:</b><br />
              <xsl:value-of disable-output-escaping="yes" select="./startTime"/>
            </p>
          </xsl:if>
        </xsl:when>

        <xsl:otherwise>
          <p>
            <b>Beginn:</b><br />
            <xsl:value-of disable-output-escaping="yes" select="./startDate"/>
            <xsl:if test="./startTime">
              um
              <xsl:value-of disable-output-escaping="yes" select="./startTime"/>
              Uhr
            </xsl:if>
          </p>
          <p>
            <b>Ende:</b><br />
            <xsl:value-of disable-output-escaping="yes" select="./endDate"/>
            <xsl:if test="./endTime">
              um
              <xsl:value-of disable-output-escaping="yes" select="./endTime"/>
              Uhr
            </xsl:if>
          </p>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="./eventDate">
        <p>
          <xsl:value-of disable-output-escaping="yes" select="./eventDate"/>
        </p>
      </xsl:if>


      <xsl:if test="./eventType">
        <p>
          <b>Veranstaltungsart:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./eventType"/>
        </p>
      </xsl:if>
      <xsl:if test="./mainContributor">
        <p>
          <b>Veranstalter:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./mainContributor"/>
        </p>
      </xsl:if>
      <xsl:if test="./cost">
        <p>
          <b>Kosten :</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./cost"/>
        </p>
      </xsl:if>
      <xsl:if test="./mapLink">
        <a>
          <xsl:attribute name="href"><xsl:value-of select="./mapLink"/></xsl:attribute>
          Anfahrtsskizze
        </a>
      </xsl:if>
    </div>

    <div id="belowFloats">
      <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
    </div>

  </xsl:template>
</xsl:stylesheet>