<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ldn.Contact']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_ldn_Contact">
    <table width="100%" border="0" cellspacing="1" cellpadding="1">
      <tr>
        <td class="contentTitle" align="left" valign="top"><xsl:value-of select="./title"/></td>
        <td>Contact Address Details</td>
        <td>Contact Phones</td>
      </tr>
      <tr>
        <td>
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <th valign="top">Given Name:</th>
              <td valign="top" class="contentText" align="left"><xsl:value-of select="./givenName"/></td>
            </tr>
            <tr>
              <th valign="top">Family Name:</th>
              <td valign="top" class="contentText" align="left"><xsl:value-of select="./familyName"/></td>
            </tr>
            <tr>
              <th valign="top">Contact Type:</th>
              <td valign="top" class="contentText" align="left"><xsl:value-of select="./contactType/typeName"/></td>
            </tr>
            <tr>
              <th valign="top">Description:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of disable-output-escaping="yes" select="./description"/></td>
            </tr>
            <tr>
              <th valign="top">Contact Emails:</th>
              <td valign="top" class="contentText" align="left"><xsl:value-of select="./emails"/></td>
            </tr>
            <tr>
              <th valign="top">Suffix:</th>
              <td valign="top" class="contentText" align="left"><xsl:value-of select="./suffix"/></td>
            </tr>
            <tr>
              <th valign="top" nowrap="true">Organisation Name:</th>
              <td valign="top" class="contentText" align="left"><xsl:value-of select="./orgName"/></td>
            </tr>
            <tr>
              <th valign="top">Department Name:</th>
              <td valign="top" class="contentText" align="left"><xsl:value-of select="./deptName"/></td>
            </tr>
            <tr>
              <th valign="top">Role:</th>
              <td valign="top" class="contentText" align="left"><xsl:value-of select="./role"/></td>
            </tr>
          </table>
        </td>
        <td>
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <th valign="top">SAON:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of select="./contactAddress/saon"/></td>
            </tr>
            <tr>
              <th valign="top">PAON:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of select="./contactAddress/paon"/></td>
            </tr>
            <tr>
              <th valign="top">Street Description:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of select="./contactAddress/streetDesc"/></td>
            </tr>
            <tr>
              <th valign="top">Locality:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of select="./contactAddress/locality"/></td>
            </tr>
            <tr>
              <th valign="top">Town:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of select="./contactAddress/town"/></td>
            </tr>
            <tr>
              <th valign="top">Postal Code:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of select="./contactAddress/postCode"/></td>
            </tr>
            <tr>
              <th valign="top">Postal Town:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of select="./contactAddress/postTown"/></td>
            </tr>
            <tr>
              <th valign="top" nowrap="true">Administrative Area:</th>
              <td valign="top" class="contentText" align="left">
                <xsl:value-of select="./contactAddress/administrativeArea"/></td>
            </tr>
          </table>
        </td>
        <td valign="top">
          <table width="100%" border="0" cellspacing="2" cellpadding="1">
            <tr bgcolor="#eeeee">
              <th nowrap="true" align="left">Phone Type</th>
              <th nowrap="true">Phone Number</th>
            </tr>
            <xsl:for-each select="./phones">
              <tr>
                <td class="contentText" valign="top" align="left"><xsl:value-of select="./phoneType"/></td>
                <td class="contentText" valign="top" align="left"><xsl:value-of select="./phoneNumber"/></td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ldn.Contact']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_ldn_Contact">
    <h1 class="title">CONTACT<xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="subtitle">Given Name</span>
	  <span class="text"><xsl:value-of select="./givenName"/></span><br/>
    <span class="subtitle">Family Name</span>
	  <span class="text"><xsl:value-of select="./familyName"/></span><br/>
    <span class="subtitle">Contact Suffix</span>
	  <span class="text"><xsl:value-of select="./suffix"/></span><br/>
    <span class="subtitle">Contact Type</span>
	  <span class="text"><xsl:value-of select="./contactType"/></span><br/>
    <span class="subtitle">Description</span>
	  <span class="text"><xsl:value-of select="./description"/></span><br/>
    <span class="subtitle">Organisation Name</span>
	  <span class="text"><xsl:value-of select="./orgName"/></span><br/>
    <span class="subtitle">Department Name</span>
	  <span class="text"><xsl:value-of select="./deptName"/></span><br/>
    <span class="subtitle">Role</span>
	  <span class="text"><xsl:value-of select="./role"/></span><br/>
    <br/>
  </xsl:template>

  <xsl:template
    match="cms:item[objectType='com.arsdigita.cms.contenttypes.ldn.ContactPhone']"
    mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_ldn_ContactPhone">
    <span class="subtitle">Contact Phone Type</span>
	  <span class="textCap"><xsl:value-of select="./phoneType"/></span><br/>
    <span class="subtitle">Contact Phone Number</span>
	  <span class="textCap"><xsl:value-of select="./phoneNumber"/></span><br/>
  </xsl:template>

</xsl:stylesheet>
