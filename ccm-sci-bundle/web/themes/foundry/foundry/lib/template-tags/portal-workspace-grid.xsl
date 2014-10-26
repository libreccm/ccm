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

    <foundry:doc-file>
        <foundry:doc-file-title>Tags for portal-workspace-grid</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>ToDo</p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <xsl:template match="portal-grid-workspace">
        <!--<pre>grid-workspace</pre>-->
        
        <xsl:apply-templates>
            <xsl:with-param name="use-default-styles" 
                            tunnel="yes"
                            select="foundry:boolean(./@use-default-styles)"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace//portal-grid-workspace-rows">
        <!--<pre>grid-workspace-rows</pre>-->
        
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace//portal-grid-workspace-rows//portal-grid-workspace-row">
        <xsl:param name="use-default-styles" 
                   as="xs:boolean" 
                   tunnel="yes" 
                   select="true()"/>
        
        <xsl:variable name="class">
            <xsl:choose>
                <xsl:when test="./@class">
                    <xsl:value-of select="./@class"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="''"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="row-layout-tree"> 
            <xsl:copy-of select="./*"/>
        </xsl:variable>
        
        <xsl:for-each select="$data-tree/portal:gridWorkspace/portal:rows/portal:row">
            <!--<pre>grid-workspace-row</pre>-->
            <!--<pre><xsl:value-of select="concat('layout = ', ./@layout)"/></pre>-->
            <div>
                <xsl:if test="$use-default-styles">
                    <!-- Nothing at the moment -->
                </xsl:if>
                <xsl:if test="$class != ''">
                    <xsl:attribute name="class" select="$class"/>
                </xsl:if>
                
                <xsl:apply-templates select="$row-layout-tree/*">
                    <xsl:with-param name="row-data-tree" tunnel="yes" select="current()"/>
                </xsl:apply-templates>
            </div>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace-columns">
        <!--<pre>grid-workspace-row-columns</pre>-->
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace-columns//portal-grid-workspace-column">
        <xsl:param name="use-default-styles"
                   as="xs:boolean"
                   tunnel="yes"
                   select="true()"/>
        <xsl:param name="row-data-tree" tunnel="yes"/>
        
        <xsl:variable name="class">
            <xsl:choose>
                <xsl:when test="./@class">
                    <xsl:value-of select="./@class"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="''"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="column-layout-tree" select="./*"/>
        
        <!--<pre>
            <xsl:value-of select="concat('count(row-data-tree/*) = ', count($row-data-tree/*))"/>
        </pre>
        <pre>
            <xsl:value-of select="count($row-data-tree/portal:portlets)"/>
        </pre>
        <pre>
            <xsl:value-of select="concat('cols = ', $row-data-tree/@layout)"/>
        </pre>-->
        
        <xsl:for-each select="tokenize($row-data-tree/@layout, ',')">
            <!--<pre>grid-workspace-row-column</pre>-->
            <div>
                <xsl:if test="$use-default-styles">
                    <xsl:attribute name="style" 
                                   select="concat('float:left; width = ', current(), ';')"/>
                </xsl:if>
                <xsl:if test="$class != ''">
                    <xsl:attribute name="class" select="$class"/>
                </xsl:if>
                    
                <xsl:variable name="col-number" select="position()"/>
                    
                <xsl:apply-templates select="$column-layout-tree">
                    <xsl:with-param name="column-portlets"
                                    tunnel="yes"
                                    select="$row-data-tree/portal:portlets/bebop:portlet[@cellNumber = $col-number]"/>
                </xsl:apply-templates>
            </div>
        </xsl:for-each>
        
        <xsl:if test="$use-default-styles">
            <div style="clear:both"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace-columns//portal-grid-workspace-column//portal-grid-workspace-column-portlets">
        <xsl:param name="column-portlets" tunnel="yes"/>
        
        <xsl:apply-templates select="$column-portlets"/>
    </xsl:template>

</xsl:stylesheet>