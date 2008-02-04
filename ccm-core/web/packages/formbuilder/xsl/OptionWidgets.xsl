<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
        xmlns:formbuilder="http://www.arsdigita.com/formbuilder/1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
        xmlns:cms="http://www.arsdigita.com/cms/1.0">

<!-- This contains option widgets such as checkbox and radio as well
     as select and multi-select -->

<!-- The standard checkbox group -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentCheckboxGroup']">
   <xsl:call-template name="optionGroup">
     <xsl:with-param name="optionType">checkbox</xsl:with-param>
   </xsl:call-template>
</xsl:template>


<!-- The standard option group -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentRadioGroup']">
   <xsl:call-template name="optionGroup">
     <xsl:with-param name="optionType">radio</xsl:with-param>
   </xsl:call-template>
</xsl:template>

<xsl:template name="optionGroup">
   <xsl:param name="optionType"/>
   <th align="left" valign="top">
     <xsl:call-template name="printLabel"/>
   </th>
   <td align="left" valign="top">
     <xsl:call-template name="printErrors"/>
   <xsl:for-each select="component">
     <xsl:sort select="link/orderNumber"/>
     <xsl:apply-templates select=".">
       <xsl:with-param name="optionType" select="$optionType"/>
     </xsl:apply-templates>
     <br />
   </xsl:for-each>
   </td>
</xsl:template>


<!-- The standard option  -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentOption']">
    <xsl:param name="optionType">checkbox</xsl:param>
    <xsl:variable name="paramName"><xsl:value-of select="../parameterName"/></xsl:variable>
    <input>
      <xsl:attribute name="value"><xsl:value-of select="parameterValue"/></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$paramName" />:<xsl:value-of select="parameterValue"/></xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="$paramName"/></xsl:attribute>
      <xsl:attribute name="type"><xsl:value-of select="$optionType"/></xsl:attribute>
      <xsl:if test="parameterValue=//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formDefaults[@parameterName=$paramName]/formbuilder:formDefaultValue">
         <xsl:attribute name="checked">checked</xsl:attribute>
      </xsl:if>
      <label>
         <xsl:attribute name="for"><xsl:value-of select="../parameterName"/>:<xsl:value-of select="parameterValue"/></xsl:attribute>
        <xsl:value-of select="label"/>
      </label>
    </input>
</xsl:template>


<!-- The Persistent Single/Multiple Select -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentSingleSelect' or defaultDomainClass='com.arsdigita.formbuilder.PersistentMultipleSelect']">
   <xsl:variable name="paramName"><xsl:value-of select="parameterName"/></xsl:variable>
   <th align="left" valign="top">
     <xsl:call-template name="printLabel"/>
   </th>
   <td align="left" valign="top">
     <xsl:call-template name="printErrors"/>
   <select>
     <xsl:attribute name="name"><xsl:value-of select="parameterName"/></xsl:attribute>
     <xsl:choose>
       <xsl:when test="defaultDomainClass='com.arsdigita.formbuilder.PersistentMultipleSelect'">
          <xsl:attribute name="multiple">multiple</xsl:attribute>
       </xsl:when>
       <xsl:otherwise>
         <option value="">-- Select --</option>
       </xsl:otherwise>
     </xsl:choose>
     <xsl:for-each select="component">
        <option>
          <xsl:attribute name="value"><xsl:value-of select="parameterValue"/></xsl:attribute>
          <xsl:if test="parameterValue=//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formDefaults[@parameterName=$paramName]/formbuilder:formDefaultValue">
             <xsl:attribute name="selected">selected</xsl:attribute>
          </xsl:if>
          <xsl:value-of select="label"/>
        </option>
     </xsl:for-each>
   </select>
   </td>
</xsl:template>


<!-- The DataDrivenSelect -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.DataDrivenSelect']">
   <xsl:variable name="paramName"><xsl:value-of select="parameterName"/></xsl:variable>
   <th align="left" valign="top">
     <xsl:call-template name="printLabel"/>
   </th>
   <td align="left" valign="top">
     <select>
      <xsl:attribute name="name"><xsl:value-of select="parameterName"/></xsl:attribute>
      <xsl:if test="selectOptions/@multiple">
         <xsl:attribute name="multiple">multiple</xsl:attribute>
      </xsl:if>
     <xsl:for-each select="selectOptions/option">
        <option>
          <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
          <xsl:if test="@id=//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formDefaults[@parameterName=$paramName]/formbuilder:formDefaultValue">
             <xsl:attribute name="selected">selected</xsl:attribute>
          </xsl:if>
          <xsl:value-of select="@label"/>
        </option>
     </xsl:for-each>                          
     </select>
     <xsl:call-template name="printErrors"/>
   </td>
</xsl:template>

</xsl:stylesheet>