<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  version="1.0">

  <xsl:import href="../../../../packages/workspace/xsl/portal.xsl"/>

  <xsl:template match="portal:workspaceDetails">
    <a href="index.jsp">View</a>
    <xsl:if test="@canAdmin = 'true'">
      <a href="admin/index.jsp">Admin</a>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
