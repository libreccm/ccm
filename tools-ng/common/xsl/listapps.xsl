<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:common="http://exslt.org/common"
  xmlns:xalan="http://xml.apache.org/xslt"
  xmlns:ccm="http://ccm.redhat.com/ccm-project"
  exclude-result-prefixes="ccm common">

  <xsl:output method="text"
    encoding="UTF-8"
    indent="no" />

  <!-- gets bundle's file project.xml as input and extracts the names of the
       modules to be included in the build (tag ccm:application child of
       ccm:build) into a (temporary) output file .tmp.applications.list as a 
       space delimited list of names for further processing                  -->
  <xsl:template match="ccm:project">
    <xsl:element name="ccm:project">
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:value-of select="concat(@name,' ')"/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
