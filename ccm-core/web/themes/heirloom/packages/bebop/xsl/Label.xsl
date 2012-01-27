<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


 <xsl:output method="html" indent="yes"/>

<!-- ************************************* -->

 <xsl:template match="bebop:label[@color!='' and @weight!='']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:element name="{@weight}">
     <xsl:element name="font">
       <xsl:attribute name="color">
         <xsl:value-of select="@color"/>
       </xsl:attribute>
       <xsl:call-template name="bebop-label-text">
          <xsl:with-param name="text" select="text()"/>
          <xsl:with-param name="escape" select="@escape"/>
       </xsl:call-template>
     </xsl:element>
   </xsl:element>
 </xsl:template>

 <xsl:template match="bebop:label[@color!='' and @weight!='']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
	       mode="javascript-mode">
   <xsl:element name="{@weight}">
     <xsl:element name="font">
       <xsl:attribute name="color">
         <xsl:value-of select="@color"/>
       </xsl:attribute>
       <xsl:call-template name="bebop-label-javascript-text">
          <xsl:with-param name="text" select="text()"/>
          <xsl:with-param name="escape" select="@escape"/>
       </xsl:call-template>
     </xsl:element>
   </xsl:element>
 </xsl:template>

<!-- ************************************* -->

 <xsl:template match="bebop:label[@weight!='']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:element name="{@weight}">
     <xsl:call-template name="bebop-label-text">
        <xsl:with-param name="text" select="text()"/>
        <xsl:with-param name="escape" select="@escape"/>
     </xsl:call-template>
   </xsl:element>
 </xsl:template>

 <xsl:template match="bebop:label[@weight!='']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
	       mode="javascript-mode">
   <xsl:element name="{@weight}">
     <xsl:call-template name="bebop-label-javascript-text">
        <xsl:with-param name="text" select="text()"/>
        <xsl:with-param name="escape" select="@escape"/>
     </xsl:call-template>
   </xsl:element>
 </xsl:template>

<!-- ************************************* -->

 <xsl:template match="bebop:label[@color!='']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:element name="font">
     <xsl:attribute name="color">
       <xsl:value-of select="@color"/>
     </xsl:attribute>
     <xsl:call-template name="bebop-label-text">
        <xsl:with-param name="text" select="text()"/>
        <xsl:with-param name="escape" select="@escape"/>
     </xsl:call-template>
   </xsl:element>
 </xsl:template>

 <xsl:template match="bebop:label[@color!='']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
	       mode="javascript-mode">
   <xsl:element name="font">
     <xsl:attribute name="color">
       <xsl:value-of select="@color"/>
     </xsl:attribute>
     <xsl:call-template name="bebop-label-javascript-text">
        <xsl:with-param name="text" select="text()"/>
        <xsl:with-param name="escape" select="@escape"/>
     </xsl:call-template>
   </xsl:element>
 </xsl:template>

<!-- ************************************* -->

 <xsl:template match="bebop:label"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
     <xsl:call-template name="bebop-label-text">
        <xsl:with-param name="text" select="text()"/>
        <xsl:with-param name="escape" select="@escape"/>
     </xsl:call-template>
 </xsl:template>

 <xsl:template match="bebop:label"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
	       mode="javascript-mode">
     <xsl:call-template name="bebop-label-javascript-text">
        <xsl:with-param name="text" select="text()"/>
        <xsl:with-param name="escape" select="@escape"/>
     </xsl:call-template>
 </xsl:template>

<!-- ************************************* -->

 <xsl:template name="bebop-label-text">
   <xsl:param name="text"/>
   <xsl:param name="escape"/>
   <xsl:variable name="realtext">
     <xsl:choose>
       <xsl:when test="normalize-space($text)=''">&#160;</xsl:when>
       <xsl:otherwise><xsl:value-of select="$text"/></xsl:otherwise>
     </xsl:choose>
   </xsl:variable>
     
   <xsl:choose>
     <xsl:when test="$escape='yes'">
       <xsl:value-of disable-output-escaping="yes" select="$realtext"/>
     </xsl:when>
     <xsl:otherwise>
       <xsl:value-of disable-output-escaping="no" select="$realtext"/>
     </xsl:otherwise>
   </xsl:choose> 
 </xsl:template>

<!-- ************************************* -->

<xsl:template name="bebop-label-javascript-text">
  <xsl:param name="text" />
  <xsl:param name="escape" />
  <xsl:variable name="lapos">&apos;</xsl:variable>
  <xsl:variable name="output-text">
    <xsl:call-template name="string-replace">
      <xsl:with-param name="from" select="$lapos"/>
      <xsl:with-param name="to" select="concat('\', $lapos)"/> 
      <xsl:with-param name="string" select="$text"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:call-template name="bebop-label-text">
     <xsl:with-param name="text" select="$output-text"/>
     <xsl:with-param name="escape" select="$escape"/>
  </xsl:call-template>
</xsl:template>

<!-- replace all occurences of the character(s) `from'
     by the string `to' in the string `string'.-->
<xsl:template name="string-replace" >
  <xsl:param name="string"/>
  <xsl:param name="from"/>
  <xsl:param name="to"/>
  <xsl:choose>
    <xsl:when test="contains($string,$from)">
      <xsl:value-of select="substring-before($string,$from)"/>
      <xsl:value-of select="$to"/>
      <xsl:call-template name="string-replace">
        <xsl:with-param name="string" select="substring-after($string,$from)"/>
        <xsl:with-param name="from" select="$from"/>
        <xsl:with-param name="to" select="$to"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$string"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
