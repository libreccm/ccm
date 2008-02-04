<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  version="1.0">

  <xsl:param name="static-prefix"/>

  <xsl:template match="cms:notesDisplay">
    <xsl:for-each select="object">
      <div style="border: 1px solid black; background-color: lightgrey; padding: .5em .5em .5em .5em; margin-bottom: .5em;">
        <xsl:value-of select="content" disable-output-escaping="yes"/>
      </div>

      <div title="Actions which can be carried out on the above note"
           style="margin-bottom: 1em;">
        <xsl:variable name="oid" select="@oid"/>
        <xsl:for-each select="../cms:notesAction[@oid=$oid]">
          <img>
            <xsl:attribute name="src"><xsl:call-template name="notes-action-icon"/></xsl:attribute>
          </img>&nbsp;
          <a href="{@href}">
            <xsl:call-template name="notes-action-text"/>
          </a>&nbsp;
        </xsl:for-each>
      </div>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="notes-action-icon">
    <xsl:choose>
      <xsl:when test="@action='delete'"><xsl:value-of select="$static-prefix"/>/cms/admin/action-group/action-delete.png</xsl:when>

      <xsl:when test="@action='edit'"><xsl:value-of select="$static-prefix"/>/cms/admin/action-group/action-edit.png</xsl:when>

      <xsl:otherwise><xsl:value-of select="$static-prefix"/>/cms/admin/action-group/action-generic.png</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="notes-action-text">
    <xsl:choose>
      <xsl:when test="@action='delete'">
        Delete
      </xsl:when>

      <xsl:when test="@action='edit'">
        Edit
      </xsl:when>

      <xsl:when test="@action='up'">
        Move up
      </xsl:when>

      <xsl:when test="@action='down'">
        Move down
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="ca_notes">
    <div style="border: 1px solid black; background-color: lightgrey; padding: .5em .5em .5em .5em; margin-bottom: .5em;">
      <xsl:value-of select="content" disable-output-escaping="yes"/>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
