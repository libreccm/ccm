<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>
                                                                                       
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">
                                                                                       
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ESDService']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_ESDService">
    <table width="300" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="250" align="left" valign="top">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td class="contentTitle" align="left" valign="top">
                <xsl:value-of select="./title"/>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" valign="top" align="left">Service Times:</td>
                    <td width="60%" class="contentSynopsis" align="right" valign="top">                     <xsl:value-of select="./serviceTimes"/></td>
                  </tr>
               </table>
             </td>
           </tr>
         </table>
       </td>
     </tr>
     <tr>
        <td width="250" align="left" valign="top">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td class="contentTitle" align="left" valign="top">
                <b>Contact Information</b></td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" valign="top" align="left">Given Name:</td>
                    <td width="60%" class="contentSynopsis" align="right" valign="top">                      <xsl:value-of select="./serviceContact/givenName"/></td>
                  </tr>
                  <tr>
                    <td width="40%" class="contentSynopsis" valign="top" align="left">Family Name:</td>
                    <td width="60%" class="contentSynopsis" align="right" valign="top">                      <xsl:value-of select="./serviceContact/familyName"/></td>
                  </tr>
               </table>
             </td>
           </tr>
         </table>
       </td>
     </tr>
     <tr>
       <td width="250" align="left" valign="top">
         <table width="100%" border="0" cellspacing="1" cellpadding="0">
           <tr>
             <td class="contentText" align="left" valign="top">
              <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
             </td>
           </tr>
         </table>
       </td>
     </tr>
   </table>
 </xsl:template>

 <xsl:template 
      match="cms:item[objectType='com.arsdigita.cms.contenttypes.ESDService']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_ESDService">
    <h1 class="mainTitle">ESDSERVICE<xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="subtitle">contact given name <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./serviceTimes"/></span><br/>
 </xsl:template>
</xsl:stylesheet>

