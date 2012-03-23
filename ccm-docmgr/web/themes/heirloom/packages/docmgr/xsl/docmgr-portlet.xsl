<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:forum="http://www.arsdigita.com/forum/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  xmlns:portalserver="http://www.redhat.com/portalserver/1.0"
  version="1.0">

  <xsl:output method="html"/>

  <xsl:template match="bebop:portlet[@bebop:classname='com.arsdigita.cms.docmgr.ui.LegacyCategoryDocsNavigatorPortlet' and //bebop:page/@id='viewWorkspace']">
    <H3><xsl:value-of select="@title" /></H3>
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="bebop:portlet[@bebop:classname='com.arsdigita.cms.docmgr.ui.RecentUpdatedDocsPortletRenderer' and //bebop:page/@id='viewWorkspace']">
    <xsl:choose>
      <xsl:when test="(../@style='W')  or
                      ((../@style='WN') and (@cellNumber='1')) or
                      ((../@style='NW') and (@cellNumber='2')) or
                      ((../@style='WN') and (@cellNumber='3')) or
                      ((../@style='NWN') and (@cellNumber='2')) or
                      ((../@style='NNN') and (@cellNumber='1'))">
        <H3><xsl:value-of select="@title" /></H3>

        <table width="100%" border="0" cellpadding="0" cellspacing="0">

          <tr>
            <td class="bglight"><b>5 Most Recent Items</b></td>
            <td width="100" align="right" class="bglight"><b>Size</b></td>
            <td width="100" align="right" class="bglight"><b>Modified</b></td>
          </tr>

          <xsl:for-each select="descendant-or-self::bebop:tbody/bebop:trow">
            <xsl:if test="position()&lt;='5'">
              <tr>
                <td class="bglight">
                  <a href="{./bebop:cell[6]/bebop:link/@href}" title="Download this document"><xsl:value-of select="./bebop:cell[1]/bebop:link/bebop:label" /></a>
                </td>
                <td width="100" align="right" class="bglight">
                  <xsl:apply-templates select="./bebop:cell[3]"/>
                </td>
                <td width="100" align="right" class="bglight">
                  <xsl:call-template name="docmgrDate">
                    <xsl:with-param name="date" select="./bebop:cell[5]/bebop:label" />
                  </xsl:call-template>
                </td>
              </tr>
            </xsl:if>
          </xsl:for-each>
          
          <xsl:if test="contains(descendant-or-self::bebop:link[@class='actionLink']/bebop:label, 'New Document')">
            <tr>
              <td colspan="3">
                <a href="{./bebop:gridPanel/bebop:panelRow[1]/bebop:cell/bebop:link[@class='actionLink']/@href}">add a document</a>
              </td>
            </tr>
          </xsl:if>

          <tr>
            <td colspan="3">
              <a href="{@applicationlink}">view all documents</a>
            </td>
          </tr>

        </table>

      </xsl:when>
      <xsl:otherwise>

        <H3><xsl:value-of select="@title" /></H3>
        <p>5 Most Recent Items</p>
        <ul>

          <xsl:for-each select="descendant-or-self::bebop:tbody/bebop:trow">
            <xsl:if test="position()&lt;='5'">
              <li>
                <a href="{./bebop:cell[6]/bebop:link/@href}" title="Download this document"><xsl:value-of select="./bebop:cell[1]/bebop:link/bebop:label" /></a>
              </li>
            </xsl:if>
          </xsl:for-each>

        </ul>

        <xsl:if test="contains(descendant-or-self::bebop:link[@class='actionLink']/bebop:label, 'New Document')">
          <div>
            <a href="{./bebop:gridPanel/bebop:panelRow[1]/bebop:cell/bebop:link[@class='actionLink']/@href}">add a document</a>
          </div>
        </xsl:if>

        <div>
          <a href="{@applicationlink}">view all documents</a>
        </div>

      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template name="docmgrDate">
    <xsl:param name="date" />
    
    <xsl:if test="string($date)!='null'">
      <xsl:if test="string-length($date)!='0'">

        <!-- Day -->
        <xsl:variable name="day-month" select="substring-before($date, '/0')"/>
        <xsl:variable name="day" select="substring-after($day-month, '/')"/>
        <xsl:variable name="month" select="substring-before($day-month, '/')"/>
        <xsl:variable name="year" select="substring-after($date, '/0')"/>

        <xsl:if test="string-length($day)='1'">
          <xsl:text>0</xsl:text>
        </xsl:if>
        <xsl:value-of select="$day" />
        <xsl:text>-</xsl:text>

        <!-- Month -->
        <xsl:choose>
          <xsl:when test="$month='1'">01</xsl:when>
          <xsl:when test="$month='2'">02</xsl:when>
          <xsl:when test="$month='3'">03</xsl:when>
          <xsl:when test="$month='4'">04</xsl:when>
          <xsl:when test="$month='5'">05</xsl:when>
          <xsl:when test="$month='6'">06</xsl:when>
          <xsl:when test="$month='7'">07</xsl:when>
          <xsl:when test="$month='8'">08</xsl:when>
          <xsl:when test="$month='9'">09</xsl:when>
          <xsl:when test="$month='10'">10</xsl:when>
          <xsl:when test="$month='11'">11</xsl:when>
          <xsl:when test="$month='12'">12</xsl:when>
          <xsl:otherwise></xsl:otherwise>
          </xsl:choose><xsl:text>-</xsl:text>200<xsl:value-of select="substring($year, 1, 2 )" />
      </xsl:if>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
