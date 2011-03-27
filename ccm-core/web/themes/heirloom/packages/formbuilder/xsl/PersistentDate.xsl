<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
        xmlns:formbuilder="http://www.arsdigita.com/formbuilder/1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
        xmlns:cms="http://www.arsdigita.com/cms/1.0">

<!-- The Persistent Date -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentDate']">
   <xsl:variable name="defaultYear">
    <xsl:call-template name="printDateDefaultValue">
       <xsl:with-param name="currentDefault" select="defaultValue/year"/>
       <xsl:with-param name="paramName"><xsl:value-of select="parameterName"/>.year</xsl:with-param>
       <xsl:with-param name="dateParamName"><xsl:value-of select="parameterName"/></xsl:with-param>
    </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="defaultMonth">
    <xsl:call-template name="printDateDefaultValue">
       <xsl:with-param name="currentDefault" select="defaultValue/month"/>
       <xsl:with-param name="paramName"><xsl:value-of select="parameterName"/>.month</xsl:with-param>
       <xsl:with-param name="dateParamName"><xsl:value-of select="parameterName"/></xsl:with-param>
    </xsl:call-template>
   </xsl:variable>

   <th align="left" valign="top">
     <xsl:call-template name="printLabel"/>
   </th>
   <td align="left" valign="top">
   <select>
    <xsl:attribute name="name"><xsl:value-of select="parameterName"/>.month</xsl:attribute>
   <xsl:for-each select="monthList/month">
      <option>
        <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
        <xsl:if test="$defaultMonth=@value">
           <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="text()"/>
      </option>
   </xsl:for-each>
   </select>

  <input type="text" size="2" maxlength="2">
    <xsl:attribute name="name"><xsl:value-of select="parameterName"/>.day</xsl:attribute>
    <xsl:attribute name="value">
      <xsl:call-template name="printDateDefaultValue">
         <xsl:with-param name="currentDefault" select="defaultValue/day"/>
         <xsl:with-param name="paramName"><xsl:value-of select="parameterName"/>.day</xsl:with-param>
         <xsl:with-param name="dateParamName"><xsl:value-of select="parameterName"/></xsl:with-param>
      </xsl:call-template>
    </xsl:attribute>
  </input>

  <select>
    <xsl:attribute name="name"><xsl:value-of select="parameterName"/>.year</xsl:attribute>
   <xsl:for-each select="yearList/year">
      <option>
        <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
        <xsl:if test="@value=$defaultYear">
           <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="text()"/>
      </option>
   </xsl:for-each>
  </select>
     <xsl:call-template name="printErrors"/>
   </td>
</xsl:template>

<!-- This prints out the default value for a date -->
<xsl:template name="printDateDefaultValue">
   <xsl:param name="currentDefault"><xsl:value-of select="defaultValue"/></xsl:param>
   <xsl:param name="paramName"><xsl:value-of select="parameterName"/></xsl:param>
   <xsl:param name="dateParamName"><xsl:value-of select="parameterName"/></xsl:param>
   <xsl:choose>
     <xsl:when test="//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formDefaults[@parameterName=$dateParamName]/formbuilder:formDefaultValue[@type=$paramName]"><xsl:value-of select="//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formDefaults[@parameterName=$dateParamName]/formbuilder:formDefaultValue[@type=$paramName]"/></xsl:when>
     <xsl:when test="not($currentDefault='')"><xsl:value-of select="$currentDefault"/></xsl:when>
     <xsl:otherwise></xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>