<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [
<!ENTITY nbsp "&#160;">
<!-- no-break space = non-breaking space, U+00A0 ISOnum -->]>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  xmlns:util="http://xmlns.redhat.com/london/util/1.0"
  exclude-result-prefixes="terms util"
  version="1.0">

  <xsl:template match="terms:domainDetails">
    <xsl:if test="starts-with(terms:url, 'http://www.esd.org.uk')">
      <h3>WARNING: Modifications to ESD Toolkit domains will be lost at next upgrade</h3>
    </xsl:if>

    <table class="domainDetails">
      <thead>
        <tr><th colspan="2">Domain details</th></tr>
      </thead>
      <tbody>
        <tr class="odd">
          <th>Title</th>
          <td><xsl:value-of select="terms:title"/></td>
        </tr>
        <tr class="even">
          <th>Key:</th>
          <td><xsl:value-of select="terms:key"/></td>
        </tr>
        <tr class="odd">
          <th>URL:</th>
          <td><xsl:value-of select="terms:url"/></td>
        </tr>
        <tr class="even">
          <th>Description:</th>
          <td><xsl:value-of select="terms:description"/></td>
        </tr>
        <tr class="odd">
          <th>Version:</th>
          <td><xsl:value-of select="terms:version"/></td>
        </tr>
        <tr class="even">
          <th>Released:</th>
          <td><xsl:value-of select="terms:released"/></td>
        </tr>
      </tbody>
      <tfoot>
        <tr>
          <td>&nbsp;</td>
          <td>
            <a href="{terms:action[@name='edit']/@url}"><img src="/__ccm__/static/cms/admin/action-group/action-generic.png" width="14" height="14" border="0"/></a>
            <a href="{terms:action[@name='edit']/@url}">Edit</a>
            <xsl:text>&#160;</xsl:text>
            <a href="{terms:action[@name='delete']/@url}"><img src="/__ccm__/static/cms/admin/action-group/action-delete.png" width="14" height="14" border="0"/></a>
            <a href="{terms:action[@name='delete']/@url}">Delete</a>
            <xsl:text>&nbsp;</xsl:text>
          </td>
        </tr>
        <xsl:if test="util:errorMessage/util:message">
          <td>&nbsp;</td>
          <td>
            <xsl:for-each select="util:errorMessage/util:message">
              <div class="domainActionErrorMessage">
                <xsl:value-of select="text()"/>
              </div>
            </xsl:for-each>
          </td>
        </xsl:if>
      </tfoot>
    </table>
  </xsl:template>
  
</xsl:stylesheet>
