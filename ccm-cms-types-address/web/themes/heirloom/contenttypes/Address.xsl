<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

    <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Address']" 
                   mode="cms:CT_graphics"
                   name="cms:CT_graphics_com_arsdigita_cms_contenttypes_Address">
        <table width="435" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td class="contentTitle" align="left" valign="top">
                    <xsl:value-of select="./title"/>
                </td>
            </tr>
            <tr>
                <td>
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <xsl:if test="./address">
                            <tr>
                                <th>Address:</th>
                                <td class="contentText" valign="top" align="left">
                                    <xsl:value-of select="./address"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <xsl:if test="./postalCode">
                            <tr>
                                <th>Postal Code:</th>
                                <td class="contentText" valign="top" align="left">
                                    <xsl:value-of select="./postalCode"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <xsl:if test="./city">
                            <tr>
                                <th>City:</th>
                                <td class="contentText" valign="top" align="left">
                                    <xsl:value-of select="./city"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <xsl:if test="./isoCountryCode/countryName">
                            <tr>
                                <th>Country:</th>
                                <td class="contentText" valign="top" align="left">
                                    <xsl:value-of select="./country"/>
                                </td>
                            </tr>
                        </xsl:if>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Address']" 
                   mode="cms:CT_text"
                   name="cms:CT_text_com_arsdigita_cms_contenttypes_Address">
        <h1 class="title"><xsl:value-of select="./title"/></h1>
        <xsl:if test="./address">
            <span class="subtitle">Address</span>
            <span class="text"><xsl:value-of select="./address"/></span><br/>
        </xsl:if>
        <xsl:if test="./postalCode">
            <span class="subtitle">Postal Code</span>
            <span class="text"><xsl:value-of select="./postalCode"/></span><br/>
        </xsl:if>
        <xsl:if test="./city">
            <span class="subtitle">City</span>
            <span class="text"><xsl:value-of select="./city"/></span><br/>
        </xsl:if>
        <xsl:if test="./isoCountryCode/countryName">
            <span class="subtitle">Country</span>
            <span class="text"><xsl:value-of select="./country"/></span><br/>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
