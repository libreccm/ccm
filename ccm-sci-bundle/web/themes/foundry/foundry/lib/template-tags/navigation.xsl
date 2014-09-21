<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:func="http://exslt.org/functions"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop foundry nav"
                version="1.0">

    <xsl:template match="navigation-layout">
        <xsl:apply-templates>
            <xsl:with-param name="navigation-id" 
                            select="foundry:get-attribute-value('navigation-id', 'categoryMenu')"/>
            <xsl:with-param name="with-colorset" 
                            select="foundry:get-attribute-value('with-colorset', 'false')"/>
            <xsl:with-param name="min-level" 
                            select="foundry:get-attribute-value('min-level', '1')"/>
            <xsl:with-param name="max-level" 
                            select="foundry:get-attribute-value('max-level', '999')"/>
            <xsl:with-param name="show-description-text" 
                            select="foundry:get-attribute-value('show-description-text', 'true')"/>
            <xsl:with-param name="current-level-tree"
                            select="$data-tree//nav:categoryMenu[@id=foundry:get-attribute-value('navigation-id', 'categoryMenu')]/nav:category/nav:category"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="navigation-layout//navigation-home-link">
        <xsl:param name="navigation-id"/>
        <xsl:param name="show-description-text"/>
        
        <a href="{$data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@url}">
            <xsl:attribute name="title">
                <xsl:choose>
                    <xsl:when test="./@show-description-text = 'false'">
                        <xsl:choose>
                            <xsl:when test="./@use-static-title = 'false'">
                                <xsl:value-of select="$data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@title"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="foundry:get-static-text('navigation', 
                                                                      $data-tree//nav:categoryMenu[@id=$navigation-id]/@navigation-id,
                                                                      'false')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@description"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="foundry:shying($data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@title)"/>
        </a>
    </xsl:template>
    
    <xsl:template match="navigation-layout//navigation-link-list"
                  name="navigation-link-list">
        <xsl:param name="navigation-id"/>
        <xsl:param name="with-colorset"/>
        <xsl:param name="min-level"/>
        <xsl:param name="max-level"/>
        <xsl:param name="show-description-text"/>
        <xsl:param name="current-level" select="1"/>
        <xsl:param name="current-level-tree"/>
        <xsl:param name="navigation-link-layout" select="."/>
        
        <xsl:message>
            navigation-link-list applied
        </xsl:message>
        
        <xsl:apply-templates>
            <xsl:with-param name="navigation-id" select="$navigation-id"/>
            <xsl:with-param name="with-colorset" select="$with-colorset"/>
            <xsl:with-param name="min-level" select="$min-level"/>
            <xsl:with-param name="max-level" select="$max-level"/>
            <xsl:with-param name="show-description-text" select="$show-description-text"/>
            <xsl:with-param name="current-level" select="$current-level"/>
            <xsl:with-param name="current-level-tree" select="$current-level-tree"/>
            <xsl:with-param name="navigation-link-layout" select="$navigation-link-layout"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="navigation-link-list//navigation-link-layout">
        <xsl:param name="navigation-id"/>
        <xsl:param name="with-colorset"/>
        <xsl:param name="min-level"/>
        <xsl:param name="max-level"/>
        <xsl:param name="show-description-text"/>
        <xsl:param name="current-level" select="1"/>
        <xsl:param name="current-level-tree" 
                   select="$data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/nav:category"/>
        <xsl:param name="navigation-link-layout" select="."/>
        
        <dl>
            <dt>navigation-id</dt>
            <dd><xsl:value-of select="$navigation-id"/></dd>
        </dl>
        
        
        <dt>
            <dt>navigation-id</dt>
            <dd><xsl:value-of select="$navigation-id"/></dd>
            <dt>min-level</dt>
            <dd><xsl:value-of select="$min-level"/></dd>
            <xsl:value-of select="concat('navigation-id = ', $navigation-id)"/>
            <xsl:value-of select="concat('min-level = ', $min-level)"/>
            <xsl:value-of select="concat('max-level = ', $max-level)"/>
            <xsl:value-of select="concat('current-level = ', $current-level, '; ')"/>
            <xsl:value-of select="count($current-level-tree)"/>
        </dt>
        
        <xsl:if test="$current-level &gt;= min-level">
        
            <xsl:message>
                Processing current level categories...
                
            </xsl:message>
            
        
            <xsl:apply-templates select="$current-level-tree">
                <xsl:with-param name="with-colorset"/>
                <xsl:with-param name="show-description-text"/>
                <xsl:with-param name="navigation-link-layout" select="$navigation-link-layout"/>
            </xsl:apply-templates>
        </xsl:if>
        
        <xsl:if test="($current-level &lt; $max-level) and $current-level-tree/nav:category">
        
            <xsl:message>Processing sub level categories</xsl:message>
        
            <xsl:call-template name="navigation-link-list">
                <xsl:with-param name="navigation-id" select="$navigation-id"/>
                <xsl:with-param name="with-colorset" select="$with-colorset"/>
                <xsl:with-param name="min-level" select="$min-level"/>
                <xsl:with-param name="max-level" select="$max-level"/>
                <xsl:with-param name="show-description-text" select="$show-description-text"/>
                <xsl:with-param name="current-level" select="$current-level + 1"/>
                <xsl:with-param name="current-level-tree" select="$current-level-tree/nav:category"/>
                <xsl:with-param name="navigation-link-layout" select="$navigation-link-layout"/>
            </xsl:call-template>
        </xsl:if>
        
        
    </xsl:template>
    
    <xsl:template match="nav:category">
        <xsl:param name="with-colorset"/>
        <xsl:param name="show-description-text"/>
        <xsl:param name="navigation-link-layout"/>
        
        <xsl:message>nav:category applied</xsl:message>
        
        <xsl:apply-templates select="$navigation-link-layout">
            <xsl:with-param name="url" select="./@url"/>
            <xsl:with-param name="title" select="./@title"/>
            <xsl:with-param name="description" select="./@description"/>
            <xsl:with-param name="with-colorset" select="$with-colorset"/>
            <xsl:with-param name="show-description-text" select="$show-description-text"/>
        </xsl:apply-templates>
        
    </xsl:template>
    
    <xsl:template match="navigation-link">
        <xsl:param name="url"/>
        <xsl:param name="title"/>
        <xsl:param name="description"/>
        <xsl:param name="with-colorset"/>
        <xsl:param name="show-description-text"/>
        
        <xsl:message>navigation-link applied</xsl:message>
        
        <a href="{$url}">
            <xsl:attribute name="title">
                <xsl:choose>
                    <xsl:when test="$show-description-text = 'true'">
                        <xsl:value-of select="$description"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$title"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="$title"/>
        </a>
        
    </xsl:template>

</xsl:stylesheet>