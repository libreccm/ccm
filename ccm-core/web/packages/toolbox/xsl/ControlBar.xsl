<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

<xsl:template match="bebop:controlBar">
<table width="100%"><tr>
    <xsl:for-each select="bebop:dimensionBar">
    <td>
        <table>
        <tr>
            <td align="center"><b><xsl:value-of select="bebop:label[@class='dimensionBarTitle']"/></b></td>
        </tr>
        <tr>
            <td align="center">
            <xsl:variable name="options" select="*[(name()='bebop:label' and @class='dimensionBarOption') or name()='bebop:link']"/>
                <!-- xsl:choose>
                    <xsl:when test="count($options) > 10">
                        You probably don't want to use a slider bar here.
                    </xsl:when>
                    <xsl:otherwise>
                    </xsl:otherwise>
                </xsl:choose -->
                    <xsl:for-each select="$options">
                    <xsl:apply-templates select="."/>
                    <xsl:if test="position()!=last()"> | </xsl:if>
                    </xsl:for-each>
            </td> 
        </tr>
        </table>
    </td>
    </xsl:for-each>
</tr>
</table>
</xsl:template>

</xsl:stylesheet>
