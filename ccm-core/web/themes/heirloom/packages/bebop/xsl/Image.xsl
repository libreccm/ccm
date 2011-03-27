<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>

 <xsl:template match="bebop:image" mode="javascript-mode"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <xsl:call-template name="bebop:image" />
 </xsl:template>

 <xsl:template name="bebop:image" match="bebop:image"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <img>
        <xsl:for-each select="@*">
            <xsl:attribute name="{name()}">
                <xsl:value-of select="."/>
            </xsl:attribute>
        </xsl:for-each>
    </img>
 </xsl:template>


</xsl:stylesheet>
