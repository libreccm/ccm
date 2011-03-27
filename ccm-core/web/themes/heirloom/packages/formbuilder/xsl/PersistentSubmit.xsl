<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
        xmlns:formbuilder="http://www.arsdigita.com/formbuilder/1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0">


<!-- The standard submit -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentSubmit']">
  <td colspan="2" align="center">
  <input type="submit"> 
    <xsl:attribute name="onclick">if(this.value == 'null') { this.value = 'Please Wait'; this.form.submit();  }</xsl:attribute>
    <xsl:attribute name="name"><xsl:value-of select="parameterName"/></xsl:attribute>
    <xsl:attribute name="value">
      <xsl:choose>
        <xsl:when test="defaultValue">
          <xsl:value-of select="defaultValue"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="parameterName"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </input>
  </td>
</xsl:template>

</xsl:stylesheet>