<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for retrieving informations about subsites.
-->

<#--doc
    Gets the name of the current sub site.
-->
<#function getSubsiteName>
    <#return model["//ui:siteBanner[@bebop:classname='com.arsdigita.subsite.ui.SubSiteBanner']/@sitename)]"]>
</#function>

