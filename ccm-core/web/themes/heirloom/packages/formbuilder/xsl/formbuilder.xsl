<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
        xmlns:formbuilder="http://www.arsdigita.com/formbuilder/1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
        xmlns:cms="http://www.arsdigita.com/cms/1.0">

<!-- In this example code, we set things up so that only the actual
     widgets are called and then it is up to that widget to decide which,
     if any, WidgetLabel to include. -->

<xsl:import href="PersistentDate.xsl"/>
<xsl:import href="PersistentSubmit.xsl"/>
<xsl:import href="OptionWidgets.xsl"/>
<xsl:import href="TextWidgets.xsl"/>

<xsl:template match="form">
  <form>
    <xsl:attribute name="name"><xsl:value-of select="name"/></xsl:attribute>
    <xsl:attribute name="method">post</xsl:attribute>
    <xsl:choose>
      <xsl:when test="../remote = 'true'">
        <xsl:attribute name="action"><xsl:value-of select="../remoteUrl"/></xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="action"><xsl:value-of select="../@formAction"/></xsl:attribute>
        <input type="hidden" value="visited">
          <xsl:attribute name="name">form.<xsl:value-of select="name"/></xsl:attribute>
        </input>
      </xsl:otherwise>
    </xsl:choose>

    <table>
        <xsl:for-each select="component[not(defaultDomainClass='com.arsdigita.formbuilder.WidgetLabel')]" >
          <xsl:sort select="link/orderNumber" data-type="number"/>
          <tr>
            <xsl:apply-templates select="."/>
          </tr>
        </xsl:for-each>
    </table>

    <xsl:apply-templates select="../formbuilder:pageState/bebop:pageState"/>
  </form>
</xsl:template>

<!-- the stand alone form sections -->
<xsl:template match="formSection">
  <form>
    <xsl:attribute name="name"><xsl:value-of select="name"/></xsl:attribute>
    <xsl:attribute name="method">post</xsl:attribute>
    <xsl:attribute name="action"><xsl:value-of select="../formAction"/></xsl:attribute>
    <xsl:for-each select="component" >
      <xsl:sort select="link/orderNumber" data-type="number"/>
      <tr>
        <xsl:apply-templates select="."/>
      </tr>
    </xsl:for-each>
    <xsl:apply-templates select="../formbuilder:pageState/bebop:pageState"/>
  </form>
</xsl:template>

<!-- process through the form sections that are embeded within forms -->
<xsl:template match="component[objectType='com.arsdigita.cms.formbuilder.FormSectionWrapper']">  
   <xsl:for-each select="formSectionItem/formSection/component[not(defaultDomainClass='com.arsdigita.formbuilder.WidgetLabel')]">
     <xsl:sort select="link/orderNumber" data-type="number"/>
      <tr>
        <xsl:apply-templates select="."/>
      </tr>    
   </xsl:for-each>
</xsl:template>


<!-- a utility template to allow components to find and print 
     the appropriate label -->
<xsl:template name="printLabel">
   <!-- not always the next following-sibling, so checking for previous orderNumber -->
   <xsl:variable name="correspondingLabelOrderNumber" select="./link/orderNumber - 1"/>
   <xsl:if test="../component[defaultDomainClass='com.arsdigita.formbuilder.WidgetLabel' and link/orderNumber = $correspondingLabelOrderNumber]">
     <xsl:apply-templates select="../component[defaultDomainClass='com.arsdigita.formbuilder.WidgetLabel' and link/orderNumber = $correspondingLabelOrderNumber]" />
   </xsl:if>
</xsl:template>

<!-- a utility template to print out error messages -->
<xsl:template name="printErrors">
  <xsl:param name="parameterName"><xsl:value-of select="parameterName"/></xsl:param>   
   <xsl:if test="//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formError[@id=$parameterName]">
      <font color="red"><xsl:value-of select="//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formError/@message"/></font><br/>
   </xsl:if>
</xsl:template>


<!-- This prints out the default value for the parameter
     When there is only one value, it prints it.  If there are multiple values,
     it prints them seperated by a ";" -->
<xsl:template name="printDefaultValue">
   <xsl:param name="currentDefault"><xsl:value-of select="defaultValue"/></xsl:param>
   <xsl:param name="paramName"><xsl:value-of select="parameterName"/></xsl:param>
   <xsl:choose>
     <xsl:when test="//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formDefaults[@parameterName=$paramName]"><xsl:for-each select="//bebop:page/cms:contentPanel/cms:item/formbuilder:formInfo/formbuilder:formDefaults[@parameterName=$paramName]/formbuilder:formDefaultValue"><xsl:if test="not(position()=1)">;</xsl:if><xsl:value-of select="text()"/></xsl:for-each></xsl:when>
     <xsl:when test="not($currentDefault='')"><xsl:value-of select="$currentDefault"/></xsl:when>
     <xsl:otherwise></xsl:otherwise>
   </xsl:choose>
</xsl:template>



<!-- The standard label -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.WidgetLabel']">
   <xsl:value-of select="label"/>
</xsl:template>



<!-- The hidden variable  -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentHidden']">
    <input type="hidden">
      <xsl:attribute name="value"><xsl:value-of select="defaultValue"/></xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="parameterName"/></xsl:attribute>
    </input>
</xsl:template>

<!-- Unique ID generator -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.HiddenIDGenerator']">
    <input type="hidden">
      <xsl:attribute name="value"><xsl:value-of select="defaultValue"/></xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="parameterName"/></xsl:attribute>
    </input>
</xsl:template>

<!-- The Persistent Text Description -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentText']">
    <td colspan="2" align="left">
    <p><xsl:value-of disable-output-escaping="yes" select="description"/></p>
    </td>
</xsl:template>

<!-- The Persistent Text Heading -->
<xsl:template match="component[defaultDomainClass='com.arsdigita.formbuilder.PersistentHeading']">
    <td colspan="2" align="left">
      <p><xsl:value-of disable-output-escaping="yes" select="description"/></p>
    </td>
</xsl:template>

<!--
PersistentDeditor.java
PersistentLabel.java
-->
</xsl:stylesheet>
