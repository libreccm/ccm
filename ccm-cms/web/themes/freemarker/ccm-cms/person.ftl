<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getSurname item>
    <#return item["./surname"]>
</#function>

<#function getGivenName item>
    <#return item["./givenname"]>
</#function>

<#function getTitlePre item>
    <#return item["./titlepre"]>
</#function>

<#function getTitlePost item>
    <#return item["./titlepost"]>
</#function>

<#function getHomepageLink item contactType="commonContact" entry="homepage">
    <#return item["./contacts/contact[./@contactType='${contactType}']/contactentries[./keyId='${entry}]/value'"]>
</#function>

<#function getContactEntries item contactType="commonContact">
    <#return item["./contacts/contact[@contactType='${contactType}']/contactentries"]>
</#function>

<#function getAddress item contactType="commonContact">
    <#return item["./contacts/contact[@contactType='${contactType}']/address]
</#function>
