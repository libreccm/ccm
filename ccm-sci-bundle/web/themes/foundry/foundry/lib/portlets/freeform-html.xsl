<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
                xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop foundry nav portal portlet"
                version="2.0">
    

    <xsl:template match="bebop:portlet[@bebop:classname = 'com.arsdigita.portalworkspace.ui.portlet.FreeformHTMLPortletRenderer']">
        <div class="portlet portlet-freeform-html">
            <xsl:value-of disable-output-escaping="yes" select="./*"/>
        </div>
    </xsl:template>
    
</xsl:stylesheet>
