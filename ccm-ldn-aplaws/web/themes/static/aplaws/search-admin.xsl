<?xml version="1.0"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav search"
	version="1.0">

  <!-- path if installed in its own web context ccm-ldn-search
    <xsl:import href="../../../../ccm-ldn-search/themes/heirloom/apps/search/xsl/index.xsl"/>
  -->
  <!-- path to xsl if installed in the main (common) web context   
         -->
  <xsl:import href="../../../themes/heirloom/apps/search/xsl/index.xsl"/>
 
</xsl:stylesheet>
