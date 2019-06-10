# Freemarker functions for language related tasks.

Import Path
    :`/ccm-themedirector/language.ftl`

### `getAvailableLanguages(): Sequence<String> `

Returns the available languages for the current document as sequence. This 
sequence can be used for creating links for selecting the language:

    <ul class="language-selector">
        <#list Lang.getAvailableLanguages()?sort as lang>
            <li class="${(lang==negotiatedLanguage)?then('selected', '')}">${lang}</li>
        </#list>
    </ul>

This example uses the `list` directive from Freemarker to iterate over 
the available languages returned by `getAvailableLanguages` The 
Freemarker build-in `?then` is used together with the 
`negotiatedLanguage` variable to check if the curent language is the 
selected language. If this is the case  a CSS class is added to the 
HTML. 

