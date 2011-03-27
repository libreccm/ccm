<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
        xmlns:formbuilder="http://www.arsdigita.com/formbuilder/1.0"
        xmlns:ui="http://www.arsdigita.com/ui/1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

<!-- This contains text widgets such as textfield, password, and textarea -->

<!-- The standard password form -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentPassword']">
   <th align="left" valign="top">
     <xsl:call-template name="printLabel"/>
   </th>
   <td align="left" valign="top">
     <xsl:call-template name="textFieldTemplate">
        <xsl:with-param name="type" select="'password'"/>
     </xsl:call-template>
     <xsl:call-template name="printErrors"/>
   </td>
</xsl:template>

<!-- The standard textField form -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentTextField']">
   <th align="left" valign="top">
     <xsl:call-template name="printLabel"/>
   </th>
   <td align="left" valign="top">
   <xsl:call-template name="textFieldTemplate">
      <xsl:with-param name="type" select="'text'"/>
   </xsl:call-template>
     <xsl:call-template name="printErrors"/>
   </td>
</xsl:template>


<!--      added by CS Gupta	-->
<!-- The standard Email Field form -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentEmailField']">
   <th align="left" valign="top">
     <xsl:call-template name="printLabel"/>
   </th>
   <xsl:variable name="defaultValue"><xsl:call-template name="printDefaultValue"/></xsl:variable>
   <xsl:variable name="value">
     <xsl:choose>
       <xsl:when test="string-length($defaultValue) > 0">
         <xsl:value-of select="$defaultValue"/>
       </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select="/bebop:page/ui:userBanner/@primaryEmail"/>
       </xsl:otherwise>
     </xsl:choose>
   </xsl:variable>
   <td align="left" valign="top">
     <xsl:call-template name="textFieldTemplate">
        <xsl:with-param name="type" select="'text'"/>
        <xsl:with-param name="value" select="$value"/>
     </xsl:call-template>
     <xsl:call-template name="printErrors"/>
   </td>
</xsl:template>


<!-- a template for all of the single line text fields -->
<xsl:template name="textFieldTemplate">
  <xsl:param name="type"/>
  <xsl:param name="value">
    <xsl:call-template name="printDefaultValue"/>
  </xsl:param>

  <input value="{$value}">
  <!-- Default values for the dimension of a single line text field are specified here -->
    <xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
    <xsl:attribute name="maxlength"><xsl:value-of select="maxlength"/></xsl:attribute>
	<xsl:if test="maxlength=0">
		<xsl:attribute name="maxlength">30</xsl:attribute>
	</xsl:if>
    <xsl:attribute name="name"><xsl:value-of select="parameterName"/></xsl:attribute>
    <xsl:attribute name="size"><xsl:value-of select="size"/></xsl:attribute>
	<xsl:if test="size=0">
		<xsl:attribute name="size">30</xsl:attribute>
	</xsl:if>
  </input>
</xsl:template>


<!-- Persistent Text Area -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentTextArea']">
   <th align="left" valign="top">
     <xsl:call-template name="printLabel"/>
   </th>
   <td align="left" valign="top">
    <xsl:call-template name="printErrors"/>
    <textarea>
    <!-- Default values for the dimension of the textarea are specified here -->
      <xsl:attribute name="name"><xsl:value-of select="parameterName"/></xsl:attribute>
      <xsl:attribute name="rows"><xsl:value-of select="rows"/></xsl:attribute>
     	<xsl:if test="rows=0">
					<xsl:attribute name="rows">5</xsl:attribute>
			</xsl:if>
      <xsl:attribute name="cols"><xsl:value-of select="cols"/></xsl:attribute>
	  	<xsl:if test="cols=0">
	    <xsl:attribute name="cols">3</xsl:attribute>
	</xsl:if>
      <xsl:call-template name="printDefaultValue"/>
    </textarea>
    
   </td>
</xsl:template>

</xsl:stylesheet>
