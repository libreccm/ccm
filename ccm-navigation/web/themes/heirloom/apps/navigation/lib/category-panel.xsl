<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                  version="1.0">

  <xsl:template match="nav:categoryPanel">
    <table class="categoryPanel">
      <tr>
        <td class="tree">
          <xsl:apply-templates select="bebop:tree"/>
        </td>
        <td class="details">
          <xsl:if test="bebop:form[@name='categoryAdd']">
            <h3>Add template</h3>
            <xsl:apply-templates select="bebop:form[@name='categoryAdd']"/>
            <hr/>
          </xsl:if>
          
          <xsl:if test="bebop:form[@name='category']">
            <h3>Assigned templates</h3>
            <xsl:apply-templates select="bebop:form[@name='category']"/>
            <hr/>
          </xsl:if>
        
          <xsl:if test="nav:quickLinkPanel">
            <h3>Quick links</h3>
            <xsl:apply-templates select="nav:quickLinkPanel"/>
            <hr/>
          </xsl:if>
        </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
