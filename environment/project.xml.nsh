<?xml version="1.0" encoding="ISO-8859-1"?>

<!--    project.xml -NSH- containing only those modules used for 
                          Netzwerk Selbsthilfe Hirnverletzte      -->

<ccm:project   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
               xmlns:ccm="http://ccm.redhat.com/ccm-project" 
              ccmVersion="6.1" 
                    name="aplaws-gen" 
              prettyName="APLAWS plus NSH" 
                 version="1-0-9" 
                 release="rc-3" 
                  webxml="web.xml-aplaws" 
                  webapp="ROOT" 
      xsi:schemaLocation="http://ccm.redhat.com/ccm-project file:tools-ng/common/xsd/project.xsd">

<ccm:databases>
	<ccm:database name="postgres"/>
</ccm:databases>


<ccm:build>
<!--   Main apps   -->
<!--   - - - - -   -->
        <ccm:application name="ccm-core"/>

        <ccm:application name="ccm-cms"/>

        <ccm:application name="ccm-cms-assets-fileattachment"/>
        <ccm:application name="ccm-cms-assets-imagestep"/>
        <ccm:application name="ccm-cms-assets-notes"/>
        <ccm:application name="ccm-cms-assets-relatedlink"/>

<!--    <ccm:application name="ccm-cms-types-address"/>              -->
<!--    <ccm:application name="ccm-cms-types-agenda"/>               -->
        <ccm:application name="ccm-cms-types-article"/>
        <ccm:application name="ccm-cms-types-bookmark"/>
        <ccm:application name="ccm-cms-types-contact"/>
<!--    <ccm:application name="ccm-cms-types-esdservice"/>           -->
        <ccm:application name="ccm-cms-types-event"/>
        <ccm:application name="ccm-cms-types-faqitem"/>
        <ccm:application name="ccm-cms-types-filestorageitem"/>
        <ccm:application name="ccm-cms-types-formitem"/>
        <ccm:application name="ccm-cms-types-formsectionitem"/>
        <ccm:application name="ccm-cms-types-glossaryitem"/>
<!--    <ccm:application name="ccm-cms-types-htmlform"/>             -->
<!--    <ccm:application name="ccm-cms-types-inlinesite"/>           -->
<!--    <ccm:application name="ccm-cms-types-job"/>                  -->
<!--    <ccm:application name="ccm-cms-types-legalnotice"/>          -->
<!--    <ccm:application name="ccm-cms-types-minutes"/>              -->
<!--    <ccm:application name="ccm-cms-types-motditem"/>             -->
        <ccm:application name="ccm-cms-types-mparticle"/>
        <ccm:application name="ccm-cms-types-newsitem"/>
<!--    <ccm:application name="ccm-cms-types-organization"/>         -->
<!--    <ccm:application name="ccm-cms-types-pressrelease"/>         -->
<!--    <ccm:application name="ccm-cms-types-service"/>              -->
<!-- Fuer OpenCCM aktivieren:                                        -->
<!--    <ccm:application name="ccm-cms-types-siteproxy"/>            -->
<!--    <ccm:application name="ccm-cms-types-xmlfeed"/>              -->

<!--  Applications   -->
<!--  - - - - -  -   -->
<!--    <ccm:application name="ccm-auth-http"/>                      -->
<!--    <ccm:application name="ccm-bookmarks"/>                      -->
<!--    temporarily excluded, produces errors in the build process
        <ccm:application name="ccm-docmngr"/>                        -->

<!--    <ccm:application name="ccm-formbuilder-pdf"/>                -->
        <ccm:application name="ccm-forum"/>
        <ccm:application name="ccm-forum-categorised"/>
<!--    <ccm:application name="ccm-simplesurvey"/>                   -->
        <ccm:application name="ccm-user-preferences"/>
<!--    <ccm:application name="ccm-weblog"/>                         -->
<!--    <ccm:application name="ccm-webpage"/>                        -->

<!--  LDN extension  -->
<!--  - - - - - - -  -->
<!--    <ccm:application name="ccm-ldn-aplaws"/>                     -->
<!--    <ccm:application name="ccm-ldn-atoz"/>                       -->
<!--    <ccm:application name="ccm-ldn-dublin"/>                     -->
<!--    <ccm:application name="ccm-ldn-exporter"/>                   -->
<!--    <ccm:application name="ccm-ldn-freeform"/>                   -->
<!--    <ccm:application name="ccm-ldn-image-step"/>                 -->
<!--    <ccm:application name="ccm-ldn-importer"/>                   -->
        <ccm:application name="ccm-ldn-navigation"/>
        <ccm:application name="ccm-ldn-portal"/>
        <ccm:application name="ccm-ldn-rss"/>                        
        <ccm:application name="ccm-ldn-search"/>
        <ccm:application name="ccm-ldn-shortcuts"/>
        <ccm:application name="ccm-ldn-subsite"/>                    
        <ccm:application name="ccm-ldn-terms"/>
        <ccm:application name="ccm-ldn-theme"/>
        <ccm:application name="ccm-ldn-util"/>


<!--  GEN extension  -->
<!--  - - - - - - -  -->
        <ccm:application name="ccm-gen-aplaws"/>



<!-- tools will be downloaded from trunk, but does not
     contain java code to be compiled!
        <ccm:application name="tools"/>           -->

  </ccm:build>


</ccm:project>
