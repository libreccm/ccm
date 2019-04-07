<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [
    <!ENTITY nbsp '&#160;'>
    <!ENTITY shy '&#173;'>
    <!ENTITY ndash '&#8211;'>
]>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
    xmlns:foundry="http://foundry.libreccm.org"
    xmlns:nav="http://ccm.redhat.com/navigation"
    xmlns:ui="http://www.arsdigita.com/ui/1.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xsl xs bebop foundry ui"
    version="2.0">

<!--
      ToDelete!

      The templates object-list-has-content and welcome-aside-has-content 
      are not used anymore. Their functions to exclude empty lists are
      now integrated into foundry lib.

      The template have no effect at all.

      As soon as the tags are removed from the theme this file is to be
      deleted.
-->

    <xsl:template match="object-list-has-content">

        <xsl:if
            test="not($data-tree/nav:simpleObjectList[@id=./@id]/nav:noContent)
                      or not($data-tree/nav:complexObjectList[@id=./@id]/nav:noContent)
                      or not($data-tree/nav:customizableObjectList[@id=./@id]/nav:noContent)
                      or not($data-tree/nav:atozObjectList[@id=./@id]/nav:noContent)
                      or not($data-tree/nav:filterObjectList[@id=./@id]/nav:noContent)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="welcome-aside-has-content">

        <xsl:if test="not($data-tree/nav:simpleObjectList[@id='itemList']/nav:noContent)
                      or not($data-tree/nav:complexObjectList[@id='eventList']/nav:noContent)">

            <xsl:apply-templates />
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
