<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>


 <xsl:template match="bebop:pageState"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <input>
        <xsl:attribute name="type">hidden</xsl:attribute>
        <xsl:for-each select="@*">
            <xsl:attribute name="{name()}">
                <xsl:value-of select="."/>
            </xsl:attribute>
        </xsl:for-each>
    </input>
 </xsl:template>


</xsl:stylesheet>
