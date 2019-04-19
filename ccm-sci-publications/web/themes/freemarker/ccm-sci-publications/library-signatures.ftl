<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getLibrary signature>
    <#return signature["./library"]>
</#function>

<#function getSignature signature>
    <#return signature["./signature"]>
</#function>

<#function getLibraryLink signature>
    <#return signature["./libraryLink"]>
</#function>

<#function getMisc signature>
    <#return signature["./misc"]>
</#function>


