<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
                xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop cms foundry nav portal portlet"
                version="2.0">
    
    <xsl:template match="bebop:portlet[@bebop:classname = 'com.arsdigita.cms.ui.portlet.ContentItemPortletRenderer']">
        
        <xsl:call-template name="process-content-item-detail">
            <xsl:with-param name="contentitem-tree">
                <xsl:copy-of select="./portlet:contentItem/cms:item/*"/>
            </xsl:with-param>
        </xsl:call-template>
        
    
        <!--<div>
            <pre>
                Content Item Portlet
            </pre>
        </div>-->
        
    </xsl:template>
</xsl:stylesheet>