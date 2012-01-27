<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                 xmlns:cms="http://www.arsdigita.com/cms/1.0"
                   version="1.0">
                       
  <xsl:template name="write-node">
    <xsl:param name="node"/>
    <xsl:param name="total-indent"/>
    <xsl:param name="level-indent">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:param>

    <xsl:for-each select="$node">
      <tr>
        <td>
          <xsl:value-of disable-output-escaping="yes" select="$total-indent"/>

          <xsl:choose>
            <xsl:when test="@collapsed = 't'">
              <a href="{@href}">+</a>
            </xsl:when>

            <xsl:when test="@expanded='t'">
              <a href="{@href}">-</a>
            </xsl:when>

            <xsl:otherwise>
              <xsl:text>&#160;</xsl:text>
            </xsl:otherwise>
          </xsl:choose>

          <xsl:text>&#160;</xsl:text>

          <xsl:apply-templates select="*[position() = 1]"/>
        </td>
      </tr>

      <xsl:for-each select="bebop:t_node">
        <xsl:call-template name="write-node">
          <xsl:with-param name="node" select="."/>

          <xsl:with-param name="total-indent">
            <xsl:copy-of select="$total-indent"/>
            <xsl:copy-of select="$level-indent"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>
   
  <xsl:template match="bebop:tree">
    <table>
      <xsl:for-each select="bebop:t_node">
        <xsl:call-template name="write-node">
          <xsl:with-param name="node" select="."/>
        </xsl:call-template>
      </xsl:for-each>
    </table>
  </xsl:template>
  
</xsl:stylesheet>