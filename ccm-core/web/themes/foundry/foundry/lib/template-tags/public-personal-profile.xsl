<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ppp="http://www.arsdigita.com/PublicPersonalProfile/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ppp"
                version="2.0">

    <xsl:template match="public-personal-profile">
        <xsl:if test="$data-tree/ppp:profile">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="public-personal-profile//profile-image">
        <xsl:if test="$data-tree/ppp:profile/ppp:profileImage or $data-tree/nav:greetingItem/cms:item/profileOwner/owner/imageAttachments">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="public-personal-profile//profile-image//caption">
        <xsl:choose>
            <xsl:when test="$data-tree/ppp:profile/ppp:profileImage">
                <xsl:value-of select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/caption" />
            </xsl:when>
            <xsl:when test="$data-tree/nav:greetingItem/cms:item/profileOwner/owner/imageAttachments">
               <xsl:value-of select="$data-tree/nav:greetingItem/cms:item/profileOwner/owner/imageAttachments[1]/caption" />
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="public-personal-profile//profile-image//image">

        <xsl:variable name="profile-image">
            <xsl:choose>
                <xsl:when test="$data-tree/ppp:profile/ppp:profileImage">
                    <xsl:copy-of select="$data-tree/ppp:profile/ppp:profileImage/imageAttachments[1]/*" />
                </xsl:when>
                <xsl:when test="$data-tree/nav:greetingItem/cms:item/profileOwner/owner/imageAttachments">
                    <xsl:copy-of select="$data-tree/nav:greetingItem/cms:item/profileOwner/owner/imageAttachments[1]/*" />
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

        <xsl:apply-templates>
            <xsl:with-param name="src" 
                            tunnel="yes"
                            select="concat('/cms-service/stream/image/?image_id=', $profile-image/image/id)"/>
            <xsl:with-param name="href"
                            tunnel="yes"
                            select="concat('/cms-service/stream/image/?image_id=', $profile-image/image/id)"/>
            <xsl:with-param name="img-width"
                            tunnel="yes"
                            select="$profile-image/image/width"/>
            <xsl:with-param name="img-height"
                            tunnel="yes"
                            select="$profile-image/image/height"/>
            <xsl:with-param name="alt"
                            tunnel="yes">
                <xsl:choose>
                    <xsl:when test="string-length($profile-image/caption) &gt; 0">
                        <xsl:value-of select="$profile-image/caption"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$profile-image/image/displayName"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="title"
                            tunnel="yes">
                <xsl:choose>
                    <xsl:when test="string-length($profile-image/caption) &gt; 0">
                        <xsl:value-of select="$profile-image/caption"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$profile-image/image/displayName"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="public-personal-profile//owner-name">
        <xsl:value-of select="$data-tree/ppp:profile/ppp:ownerName"/>
    </xsl:template>

    <xsl:template match="public-personal-profile//personal-publications">
        <xsl:if test="$data-tree/ppp:profile/personalPublications">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//available-publication-groups">
        <xsl:if test="$data-tree/ppp:profile/personalPublications/availablePublicationGroups">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//available-publication-groups//publication-group">
        <xsl:variable name="layout-tree" 
                      select="./*"/>

        <xsl:for-each select="$data-tree/ppp:profile/personalPublications/availablePublicationGroups/availablePublicationGroup">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="publication-group-id"
                                tunnel="yes"
                                select="./@name"/>
                <xsl:with-param name="href" tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="$data-tree/ppp:profile/personalPublications/publications[@all='all']">
                            <xsl:value-of select="concat('#', ./@name)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat('?group=', ./@name)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//available-publication-groups//publication-group-label
                         | public-personal-profile//personal-publications//publications//publication-groups//publication-group//publication-group-label">
        <xsl:param name="publication-group-id" 
                   tunnel="yes"/>
        
        <xsl:value-of select="foundry:get-static-text('public-personal-profile', 
                                                      concat('publication-groups/', $publication-group-id))"/>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications">
        <xsl:if test="$data-tree/ppp:profile/personalPublications/publications">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups">
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="publication-group-id" 
                                tunnel="yes"
                                select="./@name"/>
                <xsl:with-param name="publication-group-tree"
                                tunnel="yes"
                                select="./*"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//publication-group-header">
        <xsl:param name="publication-group-id" 
                   tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="id" 
                            select="$publication-group-id"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//publication">
        <xsl:variable name="layout-tree" 
                      select="./*"/>
        
        <xsl:for-each select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup/publications">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="contentitem-tree"
                                tunnel="yes"
                                select="current()"/>
                <xsl:with-param name="id"
                                tunnel="yes"
                                select="concat(./masterVersion/id, '_', ./name)"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="./masterVersion/@oid"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator">

        <xsl:if test="(number($data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageCount) &gt; 1)
                      or ./@show = 'always'">
            <xsl:apply-templates>
                <xsl:with-param name="paginator-baseurl"
                                tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="contains($data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL, '?')">
                            <xsl:value-of select="concat($data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL, '&amp;')"/>
                        </xsl:when>
                        <xsl:when test="not(contains($data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL, '?'))">
                            <xsl:value-of select="concat($data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL, '?')"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="paginator-object-begin"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectBegin"/>
                <xsl:with-param name="paginator-object-count"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectCount"/>
                <xsl:with-param name="paginator-object-end"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectEnd"/>
                <xsl:with-param name="paginator-page-count"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageCount"/>
                <xsl:with-param name="paginator-page-number"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageNumber"/>
                <xsl:with-param name="paginator-page-param"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageParam"/>
                <xsl:with-param name="paginator-page-size"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageSize"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    <xsl:template match="public-personal-profile//personal-projects//projects//project-groups//project-group//paginator">
        <xsl:if test="(number($data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageCount) &gt; 1)
                      or ./@show = 'always'">
            <xsl:apply-templates>
                <xsl:with-param name="paginator-baseurl"
                                tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="contains($data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL, '?')">
                            <xsl:value-of select="concat($data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL, '&amp;')"/>
                        </xsl:when>
                        <xsl:when test="not(contains($data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL, '?'))">
                            <xsl:value-of select="concat($data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL, '?')"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="paginator-object-begin"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectBegin"/>
                <xsl:with-param name="paginator-object-count"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectCount"/>
                <xsl:with-param name="paginator-object-end"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectEnd"/>
                <xsl:with-param name="paginator-page-count"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageCount"/>
                <xsl:with-param name="paginator-page-number"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageNumber"/>
                <xsl:with-param name="paginator-page-param"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageParam"/>
                <xsl:with-param name="paginator-page-size"
                                tunnel="yes"
                                select="$data-tree/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageSize"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//object-begin
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//paginator//object-begin">
        <xsl:param name="paginator-object-begin" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-begin != ''">
            <xsl:value-of select="$paginator-object-begin"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//object-end
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//paginator//object-end">
        <xsl:param name="paginator-object-end" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-end != ''">
            <xsl:value-of select="$paginator-object-end"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//object-count
                         | public-personal-profile//personal-project//projects//project-groups//project-group//paginator//object-count">
        <xsl:param name="paginator-object-count" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-count != ''">
            <xsl:value-of select="$paginator-object-count"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//page-count
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//paginator//page-count">
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-count != ''">
            <xsl:value-of select="$paginator-page-count"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//current-page
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//paginator//current-page">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-number != ''">
            <xsl:value-of select="$paginator-page-number"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//page-size
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//paginator//page-size">
        <xsl:param name="paginator-page-size" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-size != ''">
            <xsl:value-of select="$paginator-page-size"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//prev-page-link
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//paginator//prev-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') and (number($paginator-page-number) &gt; 1)">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-number -1)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//next-page-link
                         | public-personal-profile//project-publications//projects//project-groups//project-group//paginator//next-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
            <xsl:if test="($paginator-page-number != '') 
                      and (number($paginator-page-number) &lt; number($paginator-page-count))">
            <xsl:apply-templates>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-number + 1)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//first-page-link
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//paginator//first-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') 
                       and (number($paginator-page-number) &gt; 1)">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, '=1')"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-publications//publications//publication-groups//publication-group//paginator//last-page-link
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//paginator//last-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') 
                      and (number($paginator-page-number) &lt; number($paginator-page-count))">
            <xsl:apply-templates>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-count)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-projects//available-project-groups">
        <xsl:if test="$data-tree/ppp:profile/personalProjects/availableProjectGroups">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-projects//available-project-groups//project-group">
        <xsl:variable name="layout-tree" 
                      select="./*"/>

        <xsl:for-each select="$data-tree/ppp:profile/personalProjects/availableProjectGroups/availableProjectGroup">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="project-group-id"
                                tunnel="yes"
                                select="./@name"/>
                <xsl:with-param name="href" tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="$data-tree/ppp:profile/personalProjects/projects[@all='all']">
                            <xsl:value-of select="concat('#', ./@name)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat('?group=', ./@name)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-projects//available-project-groups//project-group-label
                         | public-personal-profile//personal-projects//projects//project-groups//project-group//project-group-label">
        <xsl:param name="project-group-id" 
                   tunnel="yes"/>
        
        <xsl:value-of select="foundry:get-static-text('public-personal-profile', 
                                                      concat('project-groups/', $project-group-id))"/>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-projects//projects">
        <xsl:if test="$data-tree/ppp:profile/personalProjects/projects">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-projects//projects//project-groups">
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$data-tree/ppp:profile/personalProjects/projects/projectGroup">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="project-group-id" 
                                tunnel="yes"
                                select="./@name"/>
                <xsl:with-param name="project-group-tree"
                                tunnel="yes"
                                select="./*"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="public-personal-profile//personal-projects//projects//project-groups//project-group//project-group-header">
        <xsl:param name="project-group-id" 
                   tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="id" 
                            select="$project-group-id"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="public-personal-profile//personal-projects//projects//project-groups//project-group//project">
        <xsl:variable name="layout-tree" 
                      select="./*"/>
        
        <xsl:for-each select="$data-tree/ppp:profile/personalProjects/projects/projectGroup/project">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="contentitem-tree"
                                tunnel="yes"
                                select="current()"/>
                <xsl:with-param name="id"
                                tunnel="yes"
                                select="concat(./masterVersion/id, '_', ./name)"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="./masterVersion/@oid"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    

</xsl:stylesheet>
