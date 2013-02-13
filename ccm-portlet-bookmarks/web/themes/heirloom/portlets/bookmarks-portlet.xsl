<?xml version="1.0"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
             xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0" 
    xmlns:bookmark-portlet="http://www.aplaws.org/portlet/bookmarks/1.0"
                   version="1.0" >

    <xsl:param name="context-prefix" />


    <xsl:template match="portlet:bookmarks">
        <xsl:if test="not(bookmark-portlet:bookmark)">
            <div class="noLinks">
              No links defined yet	
            </div>
        </xsl:if>
            <div class="portletLinkWrap">
              <xsl:apply-templates select="bookmark-portlet:bookmark" />
            </div>		
    </xsl:template>


    <xsl:template match="bookmark-portlet:bookmark">

        <a>
            <xsl:attribute name="href">
              <xsl:value-of select="@url" />
            </xsl:attribute>
            <xsl:attribute name="target">
              <xsl:value-of select="@target-window" />
            </xsl:attribute>

            <xsl:value-of select="@title" /><span class="hide">|</span>
        </a>


    </xsl:template>



</xsl:stylesheet>