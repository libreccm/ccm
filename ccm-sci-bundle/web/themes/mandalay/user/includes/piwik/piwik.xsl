<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!--

Copyright: 2012 Jens Pelzetter

    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.

    This file contains code from Piwik, an application for Web statistics.
    See piwik.org for more Details. 

-->

<!--
 This file provides integration with Piwik for OpenCCM/ScientificCMS/Mandalay. 
To use the templates definied here follow these steps:

1. Copy the file user/piwik.xsl and the user/piwik directory (with all its content) to 
   the user directory of your theme.

2. Add the piwik.xsl file to the imports in user/start.xsl:
   <xsl:import href="includes/piwik.xsl"/>

3. Copy the following templates to the 
   user/includes/mandalay/layoutParser.xsl file:

   <xsl:template match="piwikJsTracker">
     <xsl:call-template name="piwikJsTracker">
     <xsl:with-param name="piwikUrl" select="./@piwikUrl"/>
     <xsl:with-param name="idSite" select="./@idSite"/>
    </xsl:call-template>
   </xsl:template>

   <xsl:template match="piwikImageTracker">
     <xsl:call-template name="piwikImageTracker">
     <xsl:with-param name="piwikUrl" select="./@piwikUrl"/>
     <xsl:with-param name="idSite" select="./@idSite"/>
   </xsl:call-template>  
  </xsl:template>

4. Add the trackers to your layout files using the XML-Tags

  <piwikJsTracker piwikUrl="piwik.example.org" idSite="42"/>
  
  and

  <piwikImageTracker piwikUrl="piwik.example.org" idSite="42"/>

  with the correct values for the piwikUrl (without protocol!) and idSite.
  piwikUrl is the URL of your Piwik installation, idSite is the
  the ID of your site in Piwik. These value can be found in settings
  of Piwik itself. 

  The first tag includes the Piwik JavaScrip tracker, the second one
  the fallback which uses a counter pixel. Please refer to the Piwik 
  documentation for a detailed explanation. 

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                exclude-result-prefixes="xsl bebop cms"
                version="1.0">

    <xsl:template name="piwikJsTracker">
        <xsl:param name="piwikUrl"/>
        <xsl:param name="idSite"/>
        <script type="text/javascript">
            <xsl:value-of select="concat('var piwikUrl = &quot;', $piwikUrl, '&quot;;')" />
            <xsl:value-of select="concat('var idSite = ', $idSite, ';')" />
            var _paq = _paq || [];
            _paq.push(["trackPageView"]);
            _paq.push(["enableLinkTracking"]);

            (function() {
            var u=(("https:" == document.location.protocol) ? "https" : "http") + '://' + piwikUrl + "/";
            _paq.push(["setTrackerUrl", u+"piwik.php"]);
            _paq.push(["setSiteId", idSite]);
            var d=document, g=d.createElement("script"), s=d.getElementsByTagName("script")[0]; g.type="text/javascript";
            g.defer=true; g.async=true; g.src=u+"piwik.js"; s.parentNode.insertBefore(g,s);
            })();
        </script>
    </xsl:template>

    <xsl:template name="piwikImageTracker">
        <xsl:param name="piwikUrl"/>
        <xsl:param name="idSite"/>
        <noscript>
            <img>
                <xsl:attribute name="href">
                    <xsl:value-of disable-output-escaping="no" 
                                  select="concat('http://', $piwikUrl, '/piwik.php?idsite=', $idSite, '&amp;rec=1')"/>
                </xsl:attribute>
                <xsl:attribute name="style">border:0</xsl:attribute>
                <xsl:attribute name="alt"></xsl:attribute>
            </img>
        </noscript>

    </xsl:template>

</xsl:stylesheet>
