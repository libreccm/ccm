<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getPortals>
    <#return model["/portal:workspace/portal:portalList/portal:portalDetails"]>
</#function>

<#function isSelected portal>
    <#return (portal["./@isSelected"] == "true">
</#function>

<#function getPortalLink portal>
    <#return portal["./@selectAction"]>
</#function>

<#function getPortalTitle portal>
    <#return portal["./title"]>
</#function>

<#function getPortalEditForm>
    <#return model["./portal:workspace/portal:portalList/bebop:form[@name='editPortal']"]>
</#function>

<#function getPortalLayoutForm>
    <#return model["./portal:workspace/portal:portalList/bebop:form[@name='editLayout']"]>
</#function>

<#function getAddPageLink>
    <#return model["./portal:workspace/bebop:link[1]"]>
</#function>

<#function getBasicPropertiesLink>
    <#return model["./portal:workspace/bebop:link[2]"]>
</#function>

<#function getPortletsFromColumn colNumber>
    <#return model["./portal:workspace/portal:portal/bebop:portlet[@cellNumber = ${colNumber}]"]>
</#fuction>

<#function getWorkspacePrimaryUrl>
    <#return model["./portal:workspace/portal:workspaceDetails/primaryURL"]>
</#function>

<#function getMovePortletLeftLink portlet>
    <#return portlet["./portletAction[@name='moveLeft']"]>
</#function>

<#function getMovePortletRightLink portlet>
    <#return portlet["./portletAction[@name='moveRight']"]>
</#function>

<#function getMovePortletUpLink portlet>
    <#return portlet["./portletAction[@name='moveUp']"]>
</#function>

<#function getMovePortletDownLink portlet>
    <#return portlet["./portletAction[@name='moveDown']"]>
</#function>

<#function getCustomizePortletLink portlet>
    <#return portlet["./portletAction[@name='customize']"]>
</#function>

<#function getDeletePortletLink portlet>
    <#return portlet["./portletAction[@name='delete']"]>
</#function>