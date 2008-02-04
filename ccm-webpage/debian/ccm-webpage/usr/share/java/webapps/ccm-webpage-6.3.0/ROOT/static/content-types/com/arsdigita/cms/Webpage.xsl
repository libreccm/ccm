<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns:nav="http://ccm.redhat.com/london/navigation"
                xmlns:webpage="http://www.undp.org/webpage/1.0"
                version="1.0">

  <xsl:param name="dispatcher-prefix"/>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.webpage.Webpage']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_webpage_Webpage">

    <xsl:if test="count(/bebop:page/ui:simplePageContent/nav:categoryPathLinks/nav:categoryPathLink) &gt; 1">
    	<h2 id="section">
	 <xsl:if test="count(/bebop:page/ui:simplePageContent/nav:categoryPathLinks/nav:categoryPathLink) = 3">
		<xsl:value-of select="/bebop:page/ui:simplePageContent/nav:categoryPathLinks/nav:categoryPathLink[2]/@title"/> - 
	 </xsl:if>
<xsl:value-of select="/bebop:page/ui:simplePageContent/nav:categoryPathLinks/nav:categoryPathLink[position() = last()]/@title"/></h2>
    </xsl:if>

	<xsl:apply-templates select="webpage:button" />
	
	<h3><xsl:value-of select="./title"/></h3>
	
	<xsl:value-of disable-output-escaping="yes" select="./body"/>
  </xsl:template>
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.webpage.Webpage']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_webpage_Webpage">
    <h1 class="mainTitle">WEBPAGE <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./body"/></span>
  </xsl:template>
    
</xsl:stylesheet>
