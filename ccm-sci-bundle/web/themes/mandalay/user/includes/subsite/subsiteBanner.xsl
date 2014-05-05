<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:mandalay="http://mandalay.quasiweb.de"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl bebop cms mandalay ui"
                version="1.0">

    <xsl:template name="subsiteBanner">
        <xsl:param name="layoutTree" select="."/>
        
        <xsl:if test="$resultTree//ui:siteBanner[@bebop:classname='com.arsdigita.subsite.ui.SubSiteBanner']">
            <div class="siteBanner subSiteBanner">
                <xsl:variable name="subsiteBannerText">
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'subSiteBanner'"/>
                        <xsl:with-param name="id" 
                                        select="$resultTree//ui:siteBanner[@bebop:classname='com.arsdigita.subsite.ui.SubSiteBanner']/@sitename"/>
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="exclude">
                    <xsl:call-template name="mandalay:getSetting">
                        <xsl:with-param name="module" 
                                        select="'subSiteBanner'"/>
                        <xsl:with-param name="setting" 
                                        select="concat($resultTree//ui:siteBanner[@bebop:classname='com.arsdigita.subsite.ui.SubSiteBanner']/@sitename, '/exclude')"/>
                        <xsl:with-param name="default" 
                                        select="'false'"/>
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:if test="$exclude != 'true'">
                    <xsl:choose>
                        <xsl:when test="(string-length($subsiteBannerText) &lt; 1) or (contains(subsiteBannerText, 'Missing translation'))">
                            <xsl:value-of select="$resultTree//ui:siteBanner[@bebop:classname='com.arsdigita.subsite.ui.SubSiteBanner']/@sitename"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$subsiteBannerText"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </div>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>