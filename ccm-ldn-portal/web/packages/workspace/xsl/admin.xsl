<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0">
  
  <!-- rules needed to process the admin components properly. -->
  
  <xsl:param name="dispatcher-prefix"/>

  <xsl:template match="portal:admin">
    <h3>Workspace Members</h3>
    <xsl:apply-templates select="*[@id='memberDisplay']"/>
    <xsl:apply-templates select="*[@id='memberUserPicker']"/>
    
    <h3>Administrators</h3>
    <xsl:apply-templates select="*[@id='adminDisplay']"/>
    <xsl:apply-templates select="*[@id='adminUserPicker']"/>
    
    <h3>Assigned Categories</h3>
    <xsl:apply-templates select="*[@id='categoryComponent']"/>

    <xsl:apply-templates select="portal:workspaceDelete"/>
  </xsl:template>

  <xsl:template match="portal:sitemap">
    <xsl:apply-templates select="portal:applicationPane"/>
    <h3>All Applications</h3>
    <xsl:apply-templates select="portal:applicationList"/>
  </xsl:template>

  <xsl:template match="portal:workspaceDelete">
    <h3>Extreme Action</h3>
    <a>
    <xsl:attribute name="href">
     <xsl:value-of select="bebop:link/@href"/>
    </xsl:attribute>
    <xsl:value-of select="bebop:link/bebop:label"/>
    </a>
    <br />
    <xsl:value-of select="bebop:label"/>
  </xsl:template> 
    
  
  <xsl:template match="portal:applicationList">
    <table>
      <tr>
        <th>Title</th>
        <th>Type</th>
        <th>URL</th>
      </tr>
      <xsl:for-each select="portal:application">
        <xsl:sort select="primaryURL"/>
        <tr>
          <td title="{./title}"><xsl:value-of select="title"/></td>
          <td title="{./@appType}"><xsl:value-of select="@appClass"/></td>
          <td><a href="{$dispatcher-prefix}{./primaryURL}"><xsl:value-of select="primaryURL"/></a></td>
          <td><a href="{@viewURL}">View</a></td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="portal:applicationDetails">
    <table>
      <tr>
        <th align="right">Title:</th><td><xsl:value-of select="title"/></td>
      </tr>
      <tr>
        <th align="right">Description:</th><td><xsl:value-of select="description"/></td>
      </tr>
      <tr>
        <th align="right">URL:</th><td><xsl:value-of select="primaryURL"/></td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="portal:applicationPane">
    <h3>Details</h3>
    <xsl:apply-templates select="portal:applicationDetails"/>
    <xsl:apply-templates select="bebop:link"/>
    <xsl:if test="bebop:form[@name='newApp']">
      <table>
        <tr>
          <td><xsl:text>Create new child application </xsl:text></td>
          <td><xsl:apply-templates select="bebop:form[@name='newApp']"/></td>
        </tr>
      </table>
    </xsl:if>
    <xsl:apply-templates select="bebop:form[not(@name='newApp')]"/>
  </xsl:template>

</xsl:stylesheet>
