<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getLink author keyId>
    <#return author["./contacts/contact/contactentries[./keyId = '${keyId}']/value"]>
</#function>

<#function getId author>
    <#return author["./masterVersion/id"] + "_" + author["./@name"]> 
</#function>

<#function getPosition author>
    <#return author["./position()"]>
</#function>

<#function isLast author>
    <#return author["./last()"]>
</#function>

<#function getSurname author>
    <#return author["./surname"]>
</#function>

<#function getGivenName author>
    <#return author["./givenname"]>
</#function>

<#function isEditor author>
    <#return author["./@isEditor"] == "true">
</#function>

