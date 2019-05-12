<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getAddressText item>
    <#return item["./address"]>
</#function>

<#function getCity item>
    <#return item["./city"]>
</#function>

<#function getPostalCode item>
    <#return item["./postalCode"]>
</#function>

<#function getState item>
    <#return item["./state"]>
</#function>

<#function getCountry item>
    <#return item["./country"]>
</#function>

<#function getIsoCountryCode item>
    <#return item["./isoCountryCode"]>
</#function>