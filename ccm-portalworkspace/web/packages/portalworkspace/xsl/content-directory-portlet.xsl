<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0" version="1.0">

  <xsl:template match="portlet:contentDirectoryEntry" mode="grid">
    <table border="0" cellspacing="2" cellpadding="2" width="100%">
      <tr>
        <th class="directoryEntry"><a class="directoryEntryLink" href="{@url}"><xsl:value-of select="@name" /></a></th>
      </tr>
      <tr>
        <td align="left" valign="top" class="directorySubentry">
          <xsl:for-each select="portlet:contentDirectorySubentry">
            <a class="directorySubentryLink" href="{@url}"><xsl:value-of select="@name" /></a>
            <xsl:if test="not(position() = last())">
              |
            </xsl:if>
          </xsl:for-each>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="portlet:contentDirectoryEntry" mode="panel">
    <tr>
      <th class="directoryEntry"><a class="directoryEntryLink" href="{@url}"><xsl:value-of select="@name" /></a></th>
    </tr>
    <xsl:for-each select="portlet:contentDirectorySubentry">
      <tr>
        <td align="left" valign="top" class="directorySubentry">
          <a class="directorySubentryLink" href="{@url}"><xsl:value-of select="@name" /></a>
        </td>
      </tr>
    </xsl:for-each>
  </xsl:template>


  <xsl:template match="portlet:contentDirectory[@layout='grid']">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
      <xsl:for-each select="portlet:contentDirectoryEntry">
        <xsl:if test="position() mod 3 = 1">
          <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
        </xsl:if>
        <td valign="top" width="33%">
          <xsl:apply-templates select="." mode="grid"/>
        </td>
        <xsl:if test="position() mod 3 = 0 or position() = last()">
          <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
        </xsl:if>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="portlet:contentDirectory[@layout='panel']">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
      <xsl:for-each select="portlet:contentDirectoryEntry">
        <xsl:apply-templates select="." mode="panel"/>
      </xsl:for-each>
    </table>
  </xsl:template>

</xsl:stylesheet>
