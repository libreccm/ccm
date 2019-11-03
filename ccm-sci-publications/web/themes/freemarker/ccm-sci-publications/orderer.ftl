<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing the orderer of an expertise.
-->

<#--doc
    Gets the name of the orderer.

    @param orderer The orderer to use.

    @return The name of the orderer.
-->
<#function getName orderer>
    <#return orderer["./title"]>
</#function>