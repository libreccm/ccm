<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

<xsl:template name="segment">
  <xsl:param name="header"/>
  <xsl:param name="headerNodes"/>
  <xsl:param name="body"/>

  <tr>
    <td>
      <table border="0" cellpadding="2" cellspacing="0" width="100%">
        <tr>
          <th class="split_pane_header"   cellpadding="0" cellspacing="0">
            <xsl:choose>
              <xsl:when test="$header">
                <xsl:value-of select="$header"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="$headerNodes"/>               
	      </xsl:otherwise>
            </xsl:choose>
          </th>
        </tr>
      </table>
    </td>
  </tr> 
  <tr>
    <td bgcolor="#ffffff">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td height="1"/>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td class="split_pane_right_body">
      <xsl:apply-templates select="$body"/>
    </td>
  </tr>
      
</xsl:template>

<xsl:template match="bebop:segmentedPanel">
  <table width="100%" cellspacing="0" cellpadding="0" border="0" >
    <xsl:for-each select="bebop:segment">
      <xsl:call-template name="segment">
        <xsl:with-param name="headerNodes" select="bebop:segmentHeader"/>
        <xsl:with-param name="body" select="bebop:segmentBody"/>
      </xsl:call-template>
    </xsl:for-each>
  </table>
</xsl:template>

<xsl:template match="bebop:segmentHeader|bebop:segmentBody">
  <xsl:apply-templates/>
</xsl:template>


</xsl:stylesheet>




