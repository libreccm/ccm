<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getDescription item>
    <#return item["./form/description"]>
</#function>

<#function getFormAction item>
    <#if (item["./remote"] == "true")>
        <#return item["./remoteURL"]>
    <#else>
        <#return item["./@formAction"]>
    </#if>
</#function>

<#function hasHoneypot item>
    <#return (item["./honeypot"]?size > 0)>
</#function>

<#function getHoneypotName item>
    <#return item["./honeypot/@name"]>
</#function>

<#function hasMinTimeCheck item>
    <#return (item["./minTimeCheck"]?size > 0)>
</#function>

<#function getMinTimeCheckValue item>
    <#return item["./minTimeCheck/@generated"]>
</#function>

<#function hasVisitedField item>
    <#return (item["./remote"] != "true")>
</#function>

<#function getVisitedFieldName item>
    <#return item["./name"]>
</#function>

<#function getPageStateFieldName item>
    <#return item["./formbuilder:pageState/bebop:pageState/@name"]>
</#function>

<#function getPageStateFieldValue item>
    <#return item["./formbuilder:pageState/bebop:pageState/@value"]>
</#function>

<#function getComponents item>
    <#return item["./form/component[(objectType != 'com.arsdigita.formbuilder.Widget' and objectType != 'com.arsdigita.formbuilder.DataDrivenSelect' or (defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit' or defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden' or defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator')]"]>
</#function>

<#function getComponentName component>
    <#return component["./parameterName"]>
</#function>

<#function getComponentDefaultValue component>
    <#return component["./defaultValue"]>
</#function>

<#function getComponentDescription component>
    <#return component["./description"]>
</#function>

<#function getComponentParameterName component>
    <#return component["./parameterName"]>
</#function>

<#function getComponentType component>
    <#return component["./defaultDomainClass"]>
</#function>

<#function getFormSectionTitle formSection>
    <#return formSection["./formSectionItem/title]>
</#function>

<#function getFormSectionComponents formSection>
    <#return formSection["./formSectionItem/formSection/component[(objectType != 'com.arsdigita.formbuilder.Widget' and objectType != 'com.arsdigita.formbuilder.DataDrivenSelect' or (defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit' or defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden' or defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator')]"]>
</#function>

<#function getLabelComponents label>
    <#return label["./widget"]>
</#function>

<#function getButtonGroupComponents buttonGroup>
    <#return buttonGroup["./component"]>
</#function>

<#function isMultipleSelect select>
    <#return select["./multiple"] == "true">
</#function>

<#function isDataDrivenSelect select>
    <#return (select["./defaultDomainClass"] == "com.arsdigita.formbuilder.DataDrivenSelect")> 
</#function>

<#function getDataOptions select>
    <#return select["./selectOptions/option"]>
</#function>

<#function getDataOptionLabel option>
    <#return select["./@label"]>
</#function>

<#function getDataOptionId option>
    <#return select["./@id"]>
</#function>

<#function getOptionComponents select>
    <#return select["./component"]>
</#function>

<#function hasOtherOption select>
    <#return (select["./optiongroupother"] == "true")>
</#function>

<#function getOtherOptionLabel select>
    <#return select["./optiongroupotherlabel"]>
</#function>

<#function getOtherOptionValue select>
    <#return select["./optiongroupothervalue"]>
</#function>

<#function hasMaxLength component>
    <#return (component["./maxlength"]?size > 0)>
</#function>

<#function getMaxLength component>
    <#return component["./maxlength"]>
</#function>

<#function hasSize component>
    <#return (component["./size"]?size > 0)>
</#function>

<#function getSize component>
    <#return component["./size"]>
</#function>

<#function isRequired component>
    <#return (component["./widgetrequired"] == "true")>
</#function>

<#function getDateFieldDayParamName component>
    <#return component["./parameterName"] + ".day">
</#function>

<#function getDateFieldMonthParamName component>
    <#return component["./parameterName"] + ".month">
</#function>

<#function getDateFieldYearParamName component>
    <#return component["./parameterName"] + ".year">
</#function>

<#function getDateFieldDefaultValueDay component">
    <#return component["./defaultValue/day"]>
</#function>

<#function getDateFieldDefaultValueMonth component">
    <#return component["./monthList/month[@selected='selected']"]>
</#function>

<#function getDateFieldDefaultValueYear component">
    <#return component["./yearList/year[@selected='selected']"]>  
</#function>

<#function getDateFieldMonthList component>
    <#return component["./monthList/month"]>
</#function>

<#function getDateFieldYearList component>
    <#return component["./yearList/year"]>
</#function>

<#function getMonthLabel month>
    <#return month["."]>
</#function>

<#function getMonthValue month>
    <#return month["./@value"]>
</#function>

<#function getYearLabel year>
    <#return year["."]>
</#function>

<#function getYearValue year>
    <#return year["./@value"]>
</#function>

<#function getTextAreaRows textArea>
    <#return textArea["./rows"]>
</#function>

<#function getTextAreaCols textArea>
    <#return textArea["./cols"]>
</#function>
