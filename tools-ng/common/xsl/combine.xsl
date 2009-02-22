<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:common="http://exslt.org/common"
  xmlns:xalan="http://xml.apache.org/xslt"
  xmlns:ccm="http://ccm.redhat.com/ccm-project"
  exclude-result-prefixes="ccm common">

  <xsl:output method="xml"
    encoding="UTF-8"
    indent="yes"
    xalan:indent-amount="4"/>

  <xsl:template match="ccm:project">
    <xsl:element name="ccm:project">
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="*"/>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:copy-of select="document(concat(@name,'/application.xml'),/ccm:project)"/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
