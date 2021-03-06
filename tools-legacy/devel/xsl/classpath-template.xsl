<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xslt"
  xmlns:ccm="http://ccm.redhat.com/ccm-project"
  exclude-result-prefixes="ccm">

  <xsl:output method="text"
    encoding="UTF-8"
    indent="yes"
    xalan:indent-amount="4"/>

  <xsl:param name="shared.lib.dist.dir" select="/usr/share/java"/>
  <xsl:param name="base.dir" select="/usr/share/java"/>

  <xsl:template match="ccm:project">
    <xsl:value-of select="'## DO NOT EDIT THIS FILE&#10;'"/>
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
      <xsl:apply-templates select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application">
        <xsl:with-param name="projectappname" select="$name"/>
      </xsl:apply-templates>
    </xsl:for-each>
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
      <xsl:apply-templates select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application/ccm:dependencies/ccm:runRequires">
        <xsl:with-param name="projectappname" select="$name"/>
      </xsl:apply-templates>
    </xsl:for-each>
    <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="version" select="@version"/>
      <xsl:value-of select="concat($shared.lib.dist.dir,'/',$name,'-',$version,'.jar&#10;')"/>
      <xsl:value-of select="concat($shared.lib.dist.dir,'/',$name,'-',$version,'&#10;')"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="ccm:application">
    <xsl:param name="projectappname" select="@name"/>
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
    <xsl:value-of select="concat($base.dir,'/',$projectappname,'/build/classes&#10;')"/>
    <xsl:value-of select="concat($base.dir,'/',$projectappname,'/build/sql&#10;')"/>
    <xsl:value-of select="concat($base.dir,'/',$projectappname,'/lib&#10;')"/>
    <xsl:value-of select="concat($base.dir,'/',$projectappname,'/pdl&#10;')"/>
  </xsl:template>

  <xsl:template match="ccm:runRequires">
    <xsl:param name="projectappname" select="@name"/>
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:value-of select="concat($shared.lib.dist.dir,'/',$name,'.jar&#10;')"/>
  </xsl:template>

</xsl:stylesheet>
