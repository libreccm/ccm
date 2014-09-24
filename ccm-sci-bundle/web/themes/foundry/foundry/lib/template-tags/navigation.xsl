<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:func="http://exslt.org/functions"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop foundry nav"
                version="1.0">

    <foundry:doc>
        <foundry:doc-attribute name="navigation-id">
           The id of the navigation/category system from which URL should be retrieved. Default 
            value is <code>categoryMenu</code>, which is suitable in most cases.
        </foundry:doc-attribute>
        <foundry:doc-attribute name="show-description-text">
            If set to <code>true</code> (true) the description text for the category system from the 
            data tree XML will be used as value of the title attribute. 
            If set to <code>false</code>, the translated name of the category system will be used.
        </foundry:doc-attribute>
        <foundry:doc-attribute name="use-static-title">
            if set the to <code>true</code> (default) Foundry will try to translate the title of the 
            navigation/category system using the language file <code>lang/navigation.xml</code>.
            If set to <code>false</code> the title is retrieved from the data tree XML.
        </foundry:doc-attribute>
        <foundry:doc-desc>
            Environment for outputting the home link for a navigation/category system. This tag
            only intializes the context. The link itself has to be rendered using the <code>a</code>
            HTML tag. The title of the navigation is printed using the <code>navigation-title</code>
            tag.
        </foundry:doc-desc>
        <foundry:doc-see-also>#a</foundry:doc-see-also>
        <foundry:doc-see-also>#navigation-title</foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="navigation-home-link">
        <xsl:variable name="navigation-id" 
                      select="foundry:get-attribute-value('navigation-id', 'categoryMenu')"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="href" 
                            select="$data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@url"/>
            <xsl:with-param name="navigation-id" select="$navigation-id"/>
            <xsl:with-param name="title">
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
            </xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="navigation-home-link//navigation-title">
        <xsl:param name="navigation-id"/>
        <xsl:value-of select="foundry:shying($data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@title)"/>
    </xsl:template>
    
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
        
        <h1>applied navigation-link-list with these values:</h1>
        <dl>
            <dt>navigation-id</dt>
            <dd>
                <xsl:value-of select="$navigation-id"/>
            </dd>
            <dt>min-level</dt>
            <dd>
                <xsl:value-of select="$min-level"/>
            </dd>
            <dt>max-level</dt>
            <dd>
                <xsl:value-of select="$max-level"/>
            </dd>
            <dt>current-level</dt>
            <dd>
                <xsl:value-of select="$current-level"/>
            </dd>
        </dl>
        
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