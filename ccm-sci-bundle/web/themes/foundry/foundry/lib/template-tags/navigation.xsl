<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop foundry nav"
                version="1.0">

    <xsl:template match="breadcrumbs">
        <xsl:apply-templates select="./*[not(name = 'breadcrumb-separator')]">
            <xsl:with-param name="breadcrumb-separator" tunnel="yes">
                <xsl:apply-templates select="./breadcrumb-separator/*"/>
            </xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="breadcrumb-link">
        <xsl:param name="breadcrumb-separator" tunnel="yes"/>
        
        <xsl:variable name="show-description" 
                      select="foundry:boolean(foundry:get-attribute-value(current(), 
                                                                          'show-description', 
                                                                          'false'))"/>
        <xsl:variable name="breadcrumb-link-tree" select="current()"/>
        
        <xsl:apply-templates select="$breadcrumb-link-tree/*">
            <xsl:with-param name="href" 
                            tunnel="yes"
                            select="$data-tree//nav:categoryPath/nav:category[position() = 1]/@url"/>
            <xsl:with-param name="title" 
                            tunnel="yes"
                            select="foundry:get-static-text('breadcrumbs', 'root')"/>
            <xsl:with-param name="breadcrumb-label" 
                            tunnel="yes"
                            select="foundry:get-static-text('breadcrumbs', 'root')"/>
        </xsl:apply-templates>
        <xsl:if test="count($data-tree//nav:categoryPath/nav:category) &gt; 1">
            <xsl:copy-of select="$breadcrumb-separator/*"/>
        </xsl:if>
        <xsl:for-each select="$data-tree//nav:categoryPath/nav:category[not(position() = 1)]">
            <xsl:apply-templates select="$breadcrumb-link-tree/*">
                <xsl:with-param name="href" tunnel="yes" select="./@url"/>
                <xsl:with-param name="title" tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="$show-description">
                            <xsl:value-of select="./@description"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./@title"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="breadcrumb-label" tunnel="yes" select="./@title"/>
            </xsl:apply-templates>
            <xsl:if test="position() != last()">
                <xsl:copy-of select="$breadcrumb-separator/*"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="breadcrumb-link//breadcrumb-label">
        <xsl:param name="breadcrumb-label" tunnel="yes"/>
        
        <xsl:variable name="limit" select="foundry:get-attribute-value(current(), 'limit', 30)"/>
        
        
        <xsl:value-of select="foundry:truncate-text($breadcrumb-label, 
                                                    foundry:get-attribute-value(current(), 
                                                                                'limit', 
                                                                                30),
                                                    foundry:get-attribute-value(current(), 
                                                                                'min-length',
                                                                                5),
                                                    foundry:get-attribute-value(current(), 
                                                                                'mode', 
                                                                                'center'))"/>
    </xsl:template>
    
    <xsl:template match="breadcrumbs//breadcrumb-separator">
        
    </xsl:template>
    
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
            only initialises the context. The link itself has to be rendered using the <code>a</code>
            HTML tag. The title of the navigation is printed using the <code>navigation-title</code>
            tag.
        </foundry:doc-desc>
        <foundry:doc-see-also>#a</foundry:doc-see-also>
        <foundry:doc-see-also>#navigation-title</foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="navigation-home-link">
        <xsl:variable name="navigation-id" 
                      select="foundry:get-attribute-value(current(), 'navigation-id', 'categoryMenu')"/>
        <xsl:apply-templates>
            <xsl:with-param name="href" 
                            select="$data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@url"
                            tunnel="yes"/>
            <xsl:with-param name="navigation-id" select="$navigation-id" tunnel="yes"/>
            <xsl:with-param name="title" tunnel="yes">
                <xsl:choose>
                    <xsl:when test="./@show-description-text = 'false'">
                        <xsl:choose>
                            <xsl:when test="./@use-static-title = 'false'">
                                <xsl:value-of select="$data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@title"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="foundry:get-static-text('navigation', $data-tree//nav:categoryMenu[@id=$navigation-id]/@navigation-id, false())"/>
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
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the title of the current navigation inside a 
                <code>navigation-home-link</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="navigation-home-link//navigation-title">
        <xsl:param name="navigation-id" tunnel="yes"/>
        
        <xsl:value-of select="foundry:shying($data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/@title)"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for rendering a navigation menu. This element has several attributes
                which control some aspects of the rendering.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="navigation-id" mandatory="no">
                <p>
                    The ID of the navigation/category system to use for the menu. If not set
                    the default value <code>categoryMenu</code> is used which should be sufficent
                    in most use cases.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="with-colorset" mandatory="no">
                <p>
                    Decides if the navigation elements get colorset classes. Default value is
                    <code>false</code>
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="min-level">
                <p>
                    The minimum level to render. If set to a value higher than <code>1</code> all
                    levels below this value will be skipped. Use this if you want to split your
                    navigation menu. For example: First level as horizontal menu at the top, 
                    other levels as horizontal menu on the left. Default value is <code>1</code>.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="max-level">
                <p>
                    The maximum level to render. Only levels below and including this level will 
                    be used for the menu.. Use this if you want to split your
                    navigation menu. For example: First level as horizontal menu at the top, 
                    other levels as horizontal menu on the left. Default value is <code>999</code>.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="show-description-text">
                <p>
                    Decides if the description of each category is passed to the link 
                    (<code>a</code> element) for the <code>title</code> attribute. Default value
                    is <code>true</code>.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="navigation">
        <xsl:apply-templates>
            <xsl:with-param name="navigation-id" 
                            select="foundry:get-attribute-value(current(), 'navigation-id', 'categoryMenu')"
                            tunnel="yes"/>
            <xsl:with-param name="with-colorset" 
                            select="foundry:get-attribute-value(current(), 'with-colorset', 'false')"
                            tunnel="yes"/>
            <xsl:with-param name="min-level" 
                            select="foundry:get-attribute-value(current(), 'min-level', 1)"
                            tunnel="yes"/>
            <xsl:with-param name="max-level" 
                            select="foundry:get-attribute-value(current(), 'max-level', 999)"
                            tunnel="yes"/>
            <xsl:with-param name="show-description-text" 
                            select="foundry:get-attribute-value(current(), 'show-description-text', 'true')"
                            tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="navigation//navigation-links">
        <xsl:param name="navigation-id" tunnel="yes"/>
        <xsl:param name="with-colorset" tunnel="yes"/>
        <xsl:param name="min-level" tunnel="yes"/>
        <xsl:param name="max-level" tunnel="yes"/>
        <xsl:param name="show-description-text" tunnel="yes"/>
        <xsl:param name="current-level" select="1" tunnel="yes"/>
        <xsl:param name="current-level-tree" 
                   select="$data-tree//nav:categoryMenu[@id=$navigation-id]/nav:category/nav:category"
                   tunnel="yes"/>
        
        <xsl:message>
            <xsl:value-of select="'navigation-links template called with these parameters:'"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('    navigation-id = ', $navigation-id)"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('    with-colorset = ', $with-colorset)"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('    min-level = ', $min-level)"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('    max-level = ', $max-level)"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('    current-level = ', $current-level)"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('    count(category)', count($current-level-tree))"/>
        </xsl:message>
        
        <!--        <dl>
            <dt>navigation-id</dt>
            <dd>
                <xsl:value-of select="$navigation-id"/>
            </dd>
            <dt>with-colorset</dt>
            <dd>
                <xsl:value-of select="$with-colorset"/>
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
        </dl>-->
        
        <xsl:choose>
            <xsl:when test="($current-level &gt;= $min-level) and ($current-level &lt;= $max-level)">
                <xsl:apply-templates>
                    <xsl:with-param name="navigation-id" select="$navigation-id" tunnel="yes"/>
                    <xsl:with-param name="with-colorset" select="$with-colorset" tunnel="yes"/>
                    <xsl:with-param name="min-level" select="$min-level" tunnel="yes"/>
                    <xsl:with-param name="max-level" select="$max-level" tunnel="yes"/>
                    <xsl:with-param name="show-description-text" 
                                    select="$show-description-text"
                                    tunnel="yes"/>
                    <xsl:with-param name="current-level" select="1" tunnel="yes"/>
                    <xsl:with-param name="current-level-tree" 
                                    select="$current-level-tree"
                                    tunnel="yes"/>
                    <xsl:with-param name="navigation-links-tree" select="." tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="($current-level &lt; $min-level) and $current-level-tree/nav:category">
                <xsl:apply-templates select=".">
                    <xsl:with-param name="navigation-id" select="$navigation-id" tunnel="yes"/>
                    <xsl:with-param name="with-colorset" select="$with-colorset" tunnel="yes"/>
                    <xsl:with-param name="min-level" select="$min-level" tunnel="yes"/>
                    <xsl:with-param name="max-level" select="$max-level"/>
                    <xsl:with-param name="show-description-text" 
                                    select="$show-description-text" 
                                    tunnel="yes"/>
                    <xsl:with-param name="current-level" select="$current-level + 1" 
                                    tunnel="yes"/>
                    <xsl:with-param name="current-level-tree" 
                                    select="$current-level-tree/nav:category"
                                    tunnel="yes"/>
                    <xsl:with-param name="navigation-links-tree" select="."
                                    tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="navigation-links//navigation-link">
        <xsl:param name="navigation-id" tunnel="yes"/>
        <xsl:param name="with-colorset" tunnel="yes"/>
        <xsl:param name="min-level" tunnel="yes"/>
        <xsl:param name="max-level" tunnel="yes"/>
        <xsl:param name="show-description-text" tunnel="yes"/>
        <xsl:param name="current-level" tunnel="yes"/>
        <xsl:param name="current-level-tree" tunnel="yes"/>
        <xsl:param name="navigation-links-tree" tunnel="yes"/>
        
        <xsl:variable name="link-tree" select="current()"/>
        
        <xsl:for-each select="$current-level-tree">
            <xsl:apply-templates select="$link-tree/*">
                <xsl:with-param name="navigation-id" select="$navigation-id" tunnel="yes"/>
                <xsl:with-param name="with-colorset" select="$with-colorset" tunnel="yes"/>
                <xsl:with-param name="min-level" select="$min-level" tunnel="yes"/>
                <xsl:with-param name="max-level" select="$max-level" tunnel="yes"/>
                <xsl:with-param name="show-description-text" 
                                select="show-description-text" 
                                tunnel="yes"/>
                <xsl:with-param name="current-level" select="$current-level" tunnel="yes"/>
                <xsl:with-param name="current-level-tree" 
                                select="current()" 
                                tunnel="yes"/>
                <xsl:with-param name="navigation-links-tree" 
                                select="$navigation-links-tree" 
                                tunnel="yes"/>
                <xsl:with-param name="href" select="./@url" tunnel="yes"/>
                <xsl:with-param name="title" tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="$show-description-text and ./@description">
                            <xsl:value-of select="./@description"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./@title"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="link-label" select="./@title" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="navigation-link//navigation-link-label">
        <xsl:param name="link-label" tunnel="yes"/>
        
        <xsl:value-of select="$link-label"/>
    </xsl:template>
    
    <xsl:template match="navigation-link//navigation-sublinks">
        <xsl:param name="navigation-id" tunnel="yes"/>
        <xsl:param name="with-colorset" tunnel="yes"/>
        <xsl:param name="min-level" tunnel="yes"/>
        <xsl:param name="max-level" tunnel="yes"/>
        <xsl:param name="show-description-text" tunnel="yes"/>
        <xsl:param name="current-level" tunnel="yes"/>
        <xsl:param name="current-level-tree" tunnel="yes"/>
        <xsl:param name="navigation-links-tree" tunnel="yes"/>
        
        
        <xsl:if test="($current-level &lt;= $max-level) and $current-level-tree/nav:category">
            <xsl:apply-templates select="$navigation-links-tree">
                <xsl:with-param name="navigation-id" select="$navigation-id"/>
                <xsl:with-param name="with-colorset" select="$with-colorset"/>
                <xsl:with-param name="min-level" select="$min-level"/>
                <xsl:with-param name="max-level" select="$max-level"/>
                <xsl:with-param name="show-description-text" select="$show-description-text"/>
                <xsl:with-param name="current-level" select="$current-level + 1"/>
                <xsl:with-param name="current-level-tree" 
                                select="$current-level-tree/nav:category" 
                                tunnel="yes"/>
                <xsl:with-param name="navigation-links-tree" select="$navigation-links-tree"/>
            </xsl:apply-templates>
        </xsl:if>
        
    </xsl:template>
    
</xsl:stylesheet>