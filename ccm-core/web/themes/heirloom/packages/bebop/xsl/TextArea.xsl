<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html" indent="yes"/>


    <xsl:template match="bebop:textarea"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
         <xsl:if test="@label">
            <label for="{@name}">
                <xsl:value-of select="@label"/>
            </label>
        </xsl:if>
        <textarea>
            <xsl:for-each select="@*[not(name()='value')]">
                <xsl:attribute name="{name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:if test="string-length(@value)=0">&#160;</xsl:if>
            <xsl:value-of select="@value"/>
        </textarea>
    </xsl:template>


</xsl:stylesheet>
