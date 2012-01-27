<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

 <xsl:param name="internal-theme"/>

 <xsl:output method="html" indent="yes"/>


 <xsl:template match="bebop:link">
   <xsl:choose>
     <xsl:when test="@confirm">
       <xsl:call-template name="bebop:link"/>
     </xsl:when>

     <!--  The item search page should not use the dcp_hide function -->
     <xsl:when test="/bebop:page[@bebop:classname='com.arsdigita.cms.ui.ItemSearchPage']">
       <xsl:call-template name="bebop:link"/>
     </xsl:when>

     <xsl:otherwise>
       <a>
         <xsl:if test="boolean(@onclick) = false()">
           <xsl:attribute name="onclick">
             <xsl:text>dcp_disable_link(this);</xsl:text>
           </xsl:attribute>
         </xsl:if>
         <xsl:copy-of select="@*[name() != 'href_no_javascript']"/>
         <xsl:apply-templates/>
       </a>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

 <xsl:template match="bebop:link[@class = 'actionLink']">
   <xsl:choose>
     <xsl:when test="@confirm">
       <xsl:call-template name="bebop:actionLink"/>
     </xsl:when>

     <xsl:otherwise>
       <a href="{@href_no_javascript}">
         <img src="{$internal-theme}/images/action-generic.png" border="0" width="14" height="14">
           <xsl:attribute name="alt">
             <xsl:apply-templates/>
           </xsl:attribute>
         </img>
       </a>
       <xsl:text>&#160;</xsl:text>
       <a>
         <xsl:if test="boolean(@onclick) = false()">
           <xsl:attribute name="onclick">
             <xsl:text>dcp_disable_link(this);</xsl:text>
           </xsl:attribute>
         </xsl:if>
         <xsl:copy-of select="@*[name() != 'href_no_javascript']"/>
         <xsl:apply-templates/>
       </a>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

  <xsl:template match="bebop:link[@class = 'title']">
   <xsl:choose>
     <xsl:when test="@confirm">
       <xsl:call-template name="bebop:actionLink"/>
     </xsl:when>

     <xsl:otherwise>
       <a href="{@href_no_javascript}">
         <img src="{$internal-theme}/images/action-generic.png" border="0" width="14" height="14">
           <xsl:attribute name="alt">
             <xsl:apply-templates/>
           </xsl:attribute>
         </img>
       </a>
       <xsl:text>&#160;</xsl:text>
       <a onclick="{@onclick}">
         <xsl:copy-of select="@*[name() != 'href_no_javascript' and name() != 'onclick']"/>
         <xsl:apply-templates/>
       </a>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

 <xsl:template name="bebop:link"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>

       <!-- This is ugly, but I need the whole output on one line =p -->
       <![CDATA[ document.write(']]><a><xsl:for-each select="@*[name() != 'href_no_javascript' and name()!= 'confirm']"><xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute></xsl:for-each><xsl:apply-templates mode="javascript-mode"/><![CDATA[')]]>
       <![CDATA[ document.write(']]></a><![CDATA[')]]>
       <![CDATA[ // end script --> ]]>
       </script>

       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>
       <![CDATA[document.write("\<!--") ]]>
       <![CDATA[ // end script --> ]]>
       </script>

       <a>
        <xsl:for-each select="@*[name() != 'href']">
	    <xsl:choose>
	     <xsl:when test="name() = 'href_no_javascript'">
	      <xsl:attribute name="href">
	       <xsl:value-of select="."/>
	      </xsl:attribute>
	     </xsl:when>
	     <xsl:when test="name() = 'onclick'">
	     </xsl:when>
	     <xsl:otherwise>
              <xsl:attribute name="{name()}">
	       <xsl:value-of select="."/>
	      </xsl:attribute>
	     </xsl:otherwise>
	    </xsl:choose>
        </xsl:for-each>
        <xsl:apply-templates />
       </a>

       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>
       <![CDATA[ document.write("--\>") ]]>
       <![CDATA[ // end script --> ]]>
       </script>
 </xsl:template>

<xsl:template name="bebop:actionLink">
       <!-- Begin Image -->

       <!-- Image JavaScript  -->
       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>

       <!-- This is ugly, but I need the whole output on one line =p -->
       <![CDATA[ document.write(']]><a href="{@href}" onclick="{@onclick}"><img src="{$internal-theme}/images/action-generic.png" border="0" width="14" height="14"><xsl:attribute name="alt"><xsl:apply-templates mode="javascript-mode"/></xsl:attribute></img><![CDATA[')]]>
       <![CDATA[ document.write(']]></a><![CDATA[')]]>
       <![CDATA[ // end script --> ]]>
       </script>

       <!-- Image No JavaScript  -->
       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>
       <![CDATA[document.write("\<!--") ]]>
       <![CDATA[ // end script --> ]]>
       </script>

       <a href="{@href_no_javascript}">
         <img src="{$internal-theme}/images/action-generic.png" border="0" width="14" height="14">
           <xsl:attribute name="alt">
             <xsl:apply-templates/>
           </xsl:attribute>
         </img>
       </a>

       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>
       <![CDATA[document.write("\<!--") ]]>
       <![CDATA[ // end script --> ]]>
       </script>

       <!-- Begin Link -->

       <xsl:text>&#160;</xsl:text>

       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>

       <!-- This is ugly, but I need the whole output on one line =p -->
       <![CDATA[ document.write(']]><a><xsl:for-each select="@*[name() != 'href_no_javascript' and name()!= 'confirm']"><xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute></xsl:for-each><xsl:apply-templates mode="javascript-mode"/><![CDATA[')]]>
       <![CDATA[ document.write(']]></a><![CDATA[')]]>
       <![CDATA[ // end script --> ]]>
       </script>

       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>
       <![CDATA[document.write("\<!--") ]]>
       <![CDATA[ // end script --> ]]>
       </script>

       <a>
        <xsl:for-each select="@*[name() != 'href']">
	    <xsl:choose>
	     <xsl:when test="name() = 'href_no_javascript'">
	      <xsl:attribute name="href">
	       <xsl:value-of select="."/>
	      </xsl:attribute>
	     </xsl:when>
	     <xsl:when test="name() = 'onclick'">
	     </xsl:when>
	     <xsl:otherwise>
              <xsl:attribute name="{name()}">
	       <xsl:value-of select="."/>
	      </xsl:attribute>
	     </xsl:otherwise>
	    </xsl:choose>
        </xsl:for-each>
        <xsl:apply-templates />
       </a>

       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>
       <![CDATA[ document.write("--\>") ]]>
       <![CDATA[ // end script --> ]]>
       </script>
</xsl:template>

</xsl:stylesheet>
