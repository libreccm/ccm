<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  version="1.0">

  <xsl:import href="../../../packages/portal-workspace/xsl/portal.xsl"/>

  <xsl:template match="portal:workspaceDetails">
    <xsl:if test="@canEdit = 'true'">
      <a href="edit.jsp">Edit</a>
    </xsl:if>
    <xsl:if test="@canAdmin = 'true'">
      <a href="admin/index.jsp">Admin</a>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
