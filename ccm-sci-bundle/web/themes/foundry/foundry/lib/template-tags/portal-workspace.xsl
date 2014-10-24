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
        <foundry:doc-file-title>Tags for portal-workspace</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                ToDo
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <xsl:template match="portal-workspace">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-workspace-edit-links">
        <xsl:if test="$data-tree/portal:workspace/portal:workspaceDetails/@canEdit = 'true'
                      or $data-tree/portal:workspace/portal:workspaceDetails/@canAdmin = 'true'">
            <xsl:choose>
                <xsl:when test="$data-tree/portal:workspace/@id = 'view'">
                    <xsl:value-of select="foundry:get-static-text('portal-workspace', 'view/link')"/>
                </xsl:when>
                <xsl:otherwise>
                    <a href="{foundry:parse-link($data-tree/portal:workspace/portal:workspaceDetails//primaryURL)}"
                       title="{foundry:get-static-text('portal-workspace', 'view/title')}">
                        <xsl:value-of select="foundry:get-static-text('portal-workspace', 'view/link')"/>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="$data-tree/portal:workspace/portal:workspaceDetails/@canEdit = 'true'">
                &nbsp;
                <xsl:choose>
                    <xsl:when test="$data-tree/portal:workspace/@id = 'edit'">
                        <xsl:value-of select="foundry:get-static-text('portal-workspace', 'edit/link')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <a href="{foundry:parse-link(concat($data-tree/portal:workspace/portal:workspaceDetails//primaryURL, 'edit.jsp'))}"
                           title="{foundry:get-static-text('portal-workspace', 'edit/title')}">
                            <xsl:value-of select="foundry:get-static-text('portal-workspace', 'edit/link')"/>
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$data-tree/portal:workspace/portal:workspaceDetails/@canAdmin = 'true'">
                &nbsp;
                <xsl:choose>
                    <xsl:when test="$data-tree/portal:workspace/@id = 'admin'">
                        <xsl:value-of select="foundry:get-static-text('portal-workspace', 'admin/link')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <a href="{foundry:parse-link(concat($data-tree/portal:workspace/portal:workspaceDetails//primaryURL, 'admin/index.jsp'))}"
                           title="{foundry:get-static-text('portal-workspace', 'admin/title')}">
                            <xsl:value-of select="foundry:get-static-text('portal-workspace', 'admin/link')"/>
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-workspace-columns">
        <xsl:apply-templates>
            <xsl:with-param name="use-default-styles"
                            tunnel="yes"
                            select="./@use-default-styles = 'true'"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-workspace-columns/portal-workspace-column">
        <xsl:param name="use-default-styles"
                   as="xs:boolean"
                   tunnel="yes"
                   select="true()"/>
        
        <xsl:variable name="column-layout-tree" select="./*"/>
        
        <!--<pre>
            <xsl:value-of select="concat('cell-number per split(layout) = ', 
                                                    count(tokenize($data-tree/portal:workspace/portal:portal/@layout, ',')))"/>
        </pre>
        <pre>
            <xsl:value-of select="concat('cell-number per max(cellNumber) = ', 
                                                    max($data-tree/portal:workspace/portal:portal/bebop:portlet/@cellNumber))"/>
        </pre>
        
        <xsl:for-each select="tokenize($data-tree/portal:workspace/portal:portal/@layout, ',')">
            <pre>
                <xsl:value-of select="concat('column: ', position(), '; width = ', current())"/>
            </pre>
        </xsl:for-each>-->
        
        <xsl:for-each select="tokenize($data-tree/portal:workspace/portal:portal/@layout, ',')">
            <div>
                <xsl:if test="$use-default-styles">
                    <xsl:attribute name="style" 
                                   select="concat('float:left; width = ', current(), ';')"/>
                </xsl:if>
                
                <xsl:variable name="col-number" select="position()"/>
                
                <xsl:apply-templates select="$column-layout-tree">
                    <xsl:with-param name="workspace-portlets"
                                    tunnel="yes"
                                    select="$data-tree/portal:workspace/portal:portal/bebop:portlet[@cellNumber = $col-number]"/>
                </xsl:apply-templates>
            </div>
        </xsl:for-each>
        
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-workspace-columns/portal-workspace-column//portal-workspace-portlets">
        <xsl:param name="workspace-portlets" tunnel="yes"/>
        
        <xsl:apply-templates select="$workspace-portlets"/>
    </xsl:template>
    
</xsl:stylesheet>