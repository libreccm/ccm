<?xml version="1.0"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:subsite="http://ccm.redhat.com/london/subsite/1.0"
  version="1.0">
  
  <!-- IMPORT DEFINITIONS ccm-ldn-subsite installed as separate web application
  <xsl:import href="../../../../../ROOT/packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../../ROOT/packages/ui/xsl/ui.xsl"/>
  -->

  <!-- IMPORT DEFINITIONS ccm-ldn-shortcuts installed into the main CCM webapp
  -->
  <xsl:import href="../../../../packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../packages/ui/xsl/ui.xsl"/>
  
  <xsl:param name="dispatcher-prefix"/>

  <xsl:template match="bebop:form[@class='simpleForm']">
    <form>
      <xsl:for-each select="@*[not(self::method)]">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
        <xsl:attribute name="method">
          <xsl:choose> 
            <xsl:when test="string-length(../@method)=0">post</xsl:when>
            <xsl:otherwise><xsl:value-of select="../@method"/></xsl:otherwise>
          </xsl:choose> 
        </xsl:attribute>
      </xsl:for-each>
      <table>
        <xsl:for-each select="*[not(name() = 'bebop:pageState') and not(name() = 'bebop:formWidget' and @type = 'hidden')]">
          <tr>
            <th align="right"><xsl:if test="@metadata.title"><xsl:value-of select="@metadata.title"/>:</xsl:if></th>
            <td><xsl:apply-templates select="."/></td>
          </tr>
        </xsl:for-each>
      </table>
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
    </form>

  </xsl:template>

  <xsl:template match="subsite:controlCenter">
    <h3>Subsite listing</h3>
    <xsl:apply-templates select="subsite:siteListing"/>
    <xsl:choose>
      <xsl:when test="subsite:siteListing/@selected">
        <h3>Edit subsite details</h3>
      </xsl:when>
      <xsl:otherwise>
        <h3>Create new subsite</h3>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="bebop:form"/>
  </xsl:template>

  <xsl:template match="subsite:siteListing">
    <table>
      <xsl:for-each select="object">
        <tr>
          <td><xsl:value-of select="title"/></td>
          <td><a title="View the site" href="http://{hostname}/"><xsl:value-of select="hostname"/></a></td>
          <td><a href="?site={id}">[edit]</a></td>
        </tr>
        <tr>
          <td colspan="3" style="font-size: smaller; font-style: italic; text-indent: 2em"><xsl:value-of select="description"/></td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

</xsl:stylesheet>
