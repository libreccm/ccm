<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getAddress item>
    <#return item["./address/address"]>
</#function>

<#function getPerson item>
    <#return item["./person"]>
</#function>

<#function getContactEntries item>
    <#return item["./contactentries"]>
</#function>

<#function getContactEntry item keyId>
    <#return item["./contactentries[keyId='${keyId}']"]>
</#function>

<#function getContactEntryLabel entry>
    <#return entry["./key"]>
</#function>

<#function getContactEntryValue entry>
    <#return entry["./value"]>
</#function>

