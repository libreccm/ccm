<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:common="http://exslt.org/common"
  xmlns:xalan="http://xml.apache.org/xslt"
  xmlns:ccm="http://ccm.redhat.com/ccm-project"
  exclude-result-prefixes="ccm common">

  <xsl:output method="text" />

  <xsl:template match="ccm:project">
    <xsl:text>version=</xsl:text><xsl:value-of select="@version"/><xsl:text>&#13;</xsl:text>  
    <xsl:text>release=</xsl:text><xsl:value-of select="@release"/><xsl:text>&#13;</xsl:text>
  </xsl:template>
  
</xsl:stylesheet>
