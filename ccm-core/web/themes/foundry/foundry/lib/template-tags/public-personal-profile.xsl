<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:xs="http://www.w3.org/2001/XMLSchema"
            xmlns:foundry="http://foundry.libreccm.org"
            xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
            xmlns:nav="http://ccm.redhat.com/navigation"
            xmlns:ppp="http://www.arsdigita.com/PublicPersonalProfile/1.0"
            exclude-result-prefixes="xsl xs bebop foundry nav ppp"
            version="2.0">

    <xsl:template match="public-personal-profile">
        <xsl:if test="$data-tree/ppp:profile">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="public-personal-profile//profile-image">
        <xsl:if test="$data-tree/ppp:profile/ppp:profileImage">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="public-personal-profile//profile-image//caption">
        <xsl:value-of select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/caption"/>
    </xsl:template>

    <xsl:template match="public-personal-profile//profile-image//image">
        <xsl:apply-templates>
            <xsl:with-param name="src" 
                            tunnel="yes"
                            select="concat('/cms-service/stream/image/?image_id=', $data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/image/id)"/>
            <xsl:with-param name="href"
                            tunnel="yes"
                            select="concat('/cms-service/stream/image/?image_id=', $data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/image/id)"/>
            <xsl:with-param name="img-width"
                            tunnel="yes"
                            select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/image/width"/>
            <xsl:with-param name="img-height"
                            tunnel="yes"
                            select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/image/height"/>
            <xsl:with-param name="alt"
                            tunnel="yes">
                <xsl:choose>
                    <xsl:when test="string-length($data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/caption) &gt; 0">
                        <xsl:value-of select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/caption"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/image/displayName"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="title"
                            tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="string-length($data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/caption) &gt; 0">
                            <xsl:value-of select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/caption"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/image/displayName"/>
                        </xsl:otherwise>
                    </xsl:choose>
           </xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="public-personal-profile//owner-name">
        <xsl:value-of select="$data-tree/ppp:profile/ppp:ownerName"/>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner">
        <xsl:param name="contentitem-tree" tunnel="yes"/>

        <xsl:if test="$contentitem-tree/profileOwner">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
