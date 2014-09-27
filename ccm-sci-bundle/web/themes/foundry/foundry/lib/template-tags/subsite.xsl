<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl bebop cms foundry ui"
                version="2.0">

    <xsl:template match="subsite-name">
        <xsl:variable name="subsite-banner-text">
            <xsl:value-of select="foundry:get-static-text('subsite-banner', $data-tree//ui:siteBanner[@bebop:classname='com.arsdigita.subsite.ui.SubSiteBanner']/@sitename)"/>
        </xsl:variable>
        
        <xsl:variable name="exclude">
            <xsl:value-of select="foundry:get-setting('subsite-banner', concat($data-tree//ui:siteBanner[@bebop:classname='com.arsdigita.subsite.ui.SubSiteBanner']/@sitename, '/exclude'), 'false')"/>
        </xsl:variable>
        
        <xsl:if test="$exclude != 'true'">
            <xsl:choose>
                <xsl:when test="(string-length($subsite-banner-text) &lt; 1) or (contains(subsite-banner-text, 'missing translation'))">
                    <xsl:value-of select="$data-tree//ui:siteBanner[@bebop:classname='com.arsdigita.subsite.ui.SubSiteBanner']/@sitename"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$subsite-banner-text"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        
    </xsl:template>

</xsl:stylesheet>