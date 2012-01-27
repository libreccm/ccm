<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
                xmlns:docs="http://www.redhat.com/docs/1.0">
  

  <xsl:import href="../../content-section/xsl/cms.xsl"/> 
  <xsl:import href="../../acs-admin/xsl/admin_en.xsl"/>
  <xsl:import href="../../categorization/xsl/categorization.xsl"/>
  <xsl:import href="../../bebop/xsl/DimensionalNavbar.xsl"/>
  <xsl:import href="../../toolbox/xsl/ControlBar.xsl"/>
  
  <xsl:output method="html" indent="yes"/>
  
  <xsl:param name="internal-theme"/>

  
  <xsl:template match="bebop:page[@class='DOCS']" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <html>
      <head>
        <title><xsl:value-of select="bebop:title"/></title>
        <!--
        <link href="/css/acs-master.css" rel="stylesheet" type="text/css"/>
        <link href="/packages/portalserver/www/css/portalserver.css" rel="stylesheet" type="text/css"/>
        <style type="text/css">
          BODY { background: white; color: black}
          .main   {background-color: #ffffff;}
          table.globalHeader { background-color: rgb(225,225,225);}
          table.bottomRule { background: rgb(162,30,30);}
          table.topRuleNoTabs { background: rgb(162,30,30);}
          table.topRuleUnderTabs { background: rgb(162,30,30);}
        </style>
        -->
        <link rel="stylesheet" type="text/css" 
              href="{$internal-theme}/css/acs-master.css" />
        <link rel="stylesheet" type="text/css" 
              href="{$internal-theme}/packages/cms/xml/admin/cms-admin.css" />
      </head>

      <body>
        <xsl:apply-templates select="docs:header"/>
        <xsl:apply-templates select="docs:body"/>
        <xsl:apply-templates select="docs:footer"/>
        <xsl:apply-templates select="bebop:structure"/>
      </body>
   </html>
 </xsl:template>


 <!-- ContextBar formatting -->
 <xsl:template match="bebop:boxPanel[@class='ContextBar']">
   <xsl:apply-imports/>
   <hr />
 </xsl:template>
 
 <!-- Table with alternate color for each column. -->
 <xsl:template match="bebop:table[@class='AlternateTable']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table>
     <xsl:for-each select="@*">
       <xsl:attribute name="{name()}">
         <xsl:value-of select="."/>
       </xsl:attribute>
     </xsl:for-each>
     <xsl:apply-templates select="bebop:thead"/>
     <xsl:for-each select="bebop:tbody">
         <xsl:call-template name="AlternateTableBody"/>
     </xsl:for-each>
   </table>
 </xsl:template>

 <xsl:template name="AlternateTableBody"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <tbody>
     <xsl:for-each select="bebop:trow">
       <tr>
         <xsl:attribute name="bgcolor">
            <xsl:choose>
               <xsl:when test="position() mod 2">#e1d5b0</xsl:when>
               <xsl:otherwise>#ffffff</xsl:otherwise>
            </xsl:choose>
         </xsl:attribute>
         <xsl:for-each select="bebop:cell">
           <td>
             <xsl:for-each select="@align|@valign|@colspan|@width">
               <xsl:attribute name="{local-name()}">
                <xsl:value-of select="."/>
               </xsl:attribute>
             </xsl:for-each>
             <xsl:apply-templates/>
           </td>
         </xsl:for-each>
       </tr>
     </xsl:for-each>
   </tbody>
 </xsl:template>

 <xsl:template match="bebop:label[@class='isFile']">
    <img src="{$internal-theme}/images/file.gif" border="0">
       <xsl:attribute name="alt">
         <xsl:apply-templates/>
       </xsl:attribute>
    </img>
  <xsl:text>&#160;</xsl:text>
 </xsl:template>  
   
 <xsl:template match="bebop:label[@class='isFolder']">
    <img src="{$internal-theme}/images/folder.gif" border="0">
       <xsl:attribute name="alt">
         <xsl:apply-templates/>
       </xsl:attribute>
    </img>
  <xsl:text>&#160;</xsl:text>
 </xsl:template>     


 <xsl:template name="write-node">
   <xsl:param name="node"/>
   <xsl:param name="total-indent"/>
   <xsl:param name="level-indent">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:param>
   <xsl:for-each select="$node">
     <tr>
       <td>
         <xsl:attribute name="class">
           <xsl:choose>
             <xsl:when test="count(./bebop:label[@weight='b'])>0">split_pane_left_item_selected</xsl:when>
             <xsl:otherwise>split_pane_left_item</xsl:otherwise>
           </xsl:choose>
         </xsl:attribute>
         <xsl:value-of disable-output-escaping="yes" select="$total-indent"/>

         <xsl:choose>
           <xsl:when test="@collapsed='t'">
             <a href="{@href}">
               <img alt="Expand" border="0" height="11" width="11">
                 <xsl:attribute name="src">
                   <xsl:value-of select="//@assets"/>{$internal-theme}/images/plus-box.gif</xsl:attribute>
               </img>
             </a>
           </xsl:when>
           <xsl:when test="@expanded='t'">
             <a href="{@href}">
               <img alt="Collapse" border="0" height="11" width="11">
                 <xsl:attribute name="src">
                   <xsl:value-of select="//@assets"/>{$internal-theme}/images/minus-box.gif</xsl:attribute>
               </img>
             </a>
           </xsl:when>
           <xsl:when test="@radioGroup='t'">
             <input type="radio">
                <xsl:attribute name="name">
                  <xsl:value-of select="@radioGroupName"/>
                 </xsl:attribute>         
                <xsl:attribute name="value">
                  <xsl:value-of select="@resourceID"/>
                 </xsl:attribute>
             </input>                        
             <img alt="folder" border="0" height="24" width="24">
                <xsl:attribute name="src">
                <xsl:value-of select="//@assets"/>{$internal-theme}/images/folder.gif</xsl:attribute>
             </img>
           </xsl:when>
           <xsl:when test="@radioGroup='f'"> 
             <xsl:text>&#160;&#160;&#160;&#160;
             </xsl:text>                  
             <img alt="folder" border="0" height="24" width="24">
                <xsl:attribute name="src">
                <xsl:value-of select="//@assets"/>{$internal-theme}/images/folder.gif</xsl:attribute>
             </img>
           </xsl:when>
   
           <xsl:otherwise>
             <xsl:text>&#160;</xsl:text>
           </xsl:otherwise>
         </xsl:choose>

         <xsl:text>&#160;</xsl:text>
         <xsl:apply-templates select="*[position()=1]"/>
       </td>
     </tr>
     <xsl:for-each select="bebop:t_node">
       <xsl:call-template name="write-node">
         <xsl:with-param name="node" select="."/>
         <xsl:with-param name="total-indent">
           <xsl:copy-of select="$total-indent"/>
           <xsl:copy-of select="$level-indent"/>
         </xsl:with-param>
       </xsl:call-template>
     </xsl:for-each>

     <!-- This adds a little space each time we move up in the hierarchy. -->
     <xsl:if test="count(bebop:t_node)>0">
       <tr>
         <td>
           <table cellspacing="0" cellpadding="0" border="0">
             <tr>
               <td height="2"></td>
             </tr>
           </table>
         </td>
       </tr>
     </xsl:if>
   </xsl:for-each>
 </xsl:template>

 <xsl:template match="docs:error-label"
               xmlns:docs="http://www.arsdigita.com/docs/1.0">
    <xsl:choose>
        <xsl:when test="@action='copy'">
           Unable to copy on following items.
        </xsl:when>
        <xsl:when test="@action='move'">
           Unable to move on following items.
        </xsl:when>
        <xsl:when test="@action='delete'">
           Unable to delete on following items.
        </xsl:when>
    </xsl:choose>
    <ul>    
    <xsl:for-each select="docs:item">
       <li><b><xsl:value-of select="@name"/></b></li>
    </xsl:for-each>
    </ul>
 </xsl:template>

 <xsl:template match="docs:file-info">
   <table>
     <tr>
       <td>Name:</td>
       <td><xsl:value-of select="docs:name"/></td>
     </tr>
     <tr>
       <td>Description:</td>
       <td><xsl:value-of select="docs:description"/></td>
     </tr>
     <tr>
       <td>Size:</td>
       <td><xsl:value-of select="docs:size"/></td>
     </tr>
     <tr>
       <td>Type:</td>
       <td><xsl:value-of select="docs:type"/></td>
     </tr>
     <tr>
       <td>Last Modified:</td>
       <td><xsl:value-of select="docs:last-modified"/></td>
     </tr>

     <tr>
       <td>Revision:</td>
       <td><xsl:value-of select="docs:revision"/></td>
     </tr>
     <tr>
       <td>Author:</td>
       <td><xsl:value-of select="docs:author"/></td>
     </tr>

     <tr>
       <td>URI:</td>
       <td>
         <a href="{docs:uri}">
           <xsl:value-of select="docs:uri"/>
         </a>
       </td>
     </tr>

   </table>

 </xsl:template>

 <xsl:template match="docs:header">

  <table id="global-header">
    <tr>
      <td id="context">
        <xsl:if test="bebop:dimensionalNavbar[@class = 'portalNavbar']">
          <xsl:apply-templates select="bebop:dimensionalNavbar[@class = 'portalNavbar']"/>
        </xsl:if>
      </td>
      <td>
        <table id="global-links">
          <tr>
            <td style="margin: 0; border: 0; padding: 0; padding-right: 18px;">
            </td>
            <td class="global-link-icon">
             <a href="{../docs:global/bebop:link[@class = 'signoutLink']/@href}">
              <img src="{$internal-theme}/images/lock.png" height="18" width="14"/>
             </a>
            </td>
            <td class="global-link">
             <a href="{../docs:global/bebop:link[@class = 'signoutLink']/@href}">
               Sign out
              </a>
             </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>

  <table width="100%" cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#878175">
        <table cellspacing="0" cellpadding="0" border="0">
          <tr>
            <td height="1"></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>

  <table class="setInside">
    <tr>
      <td class="setInside">
        <table class="localHeader">
          <tr>
            <td class="localTitle">
              <xsl:value-of select="../bebop:title"/>
            </td>
            <td class="localControl">
              <xsl:apply-templates select="bebop:link[@class = 'portalControlProfileLink']"/>
              <xsl:apply-templates select="bebop:link[@class = 'portalControl']"/>
            </td>
          </tr>
        </table>      
      </td>
    </tr>
  </table>
 </xsl:template>
 
 <xsl:template match="docs:body">
   <table class="setInside"><tr><td class="setInside">

    <!-- XXX once we make portal tabs generate XML just like ordinary tabs, this rule repeat can go away. -->

    <xsl:variable name="portalTabs" select="bebop:list[@class = 'portalTabs']"/>
    <xsl:choose>
      <xsl:when test="count($portalTabs/bebop:cell) > 1">
        <!--
        If there is only one tab in this portal, don't bother to
        display it.
        -->

        <table cellpadding="0" cellspacing="0" border="0" class="tabs">
          <tr valign="bottom">
            <td class="trimSpace"><xsl:text>&#160;</xsl:text></td>
            <xsl:for-each select="$portalTabs/bebop:cell">
              <xsl:choose>
                <xsl:when test="@selected">
                  <!-- start currently active tab -->
                  <td class="tabBeginning"><xsl:text>&#160;&#160;</xsl:text></td>
                  <td class="activeTab" nowrap="nowrap"><xsl:value-of select="bebop:portal/@title"/></td>
                    <td class="tabEnd"><xsl:text>&#160;&#160;</xsl:text></td>
                    <td class="trimSpace"><xsl:text>&#160;</xsl:text></td>
                  <!-- end currently active tab -->
                </xsl:when>
                <xsl:otherwise>
                  <!-- start inactive tab -->
                  <td>
                    <table cellpadding="0" cellspacing="0" border="0">
                      <tr height="3">
                        <td>
                          <img src="{$internal-theme}/images/spacer.gif" height="3"/>
                        </td>
                      </tr>
                      <tr height="23">
                         <td class="tabBeginningOff"><xsl:text>&#160;&#160;</xsl:text></td>
                         <td class="inactiveTab" nowrap="nowrap"><xsl:apply-templates select="bebop:link"/></td>
                      <td class="tabEndOff"><xsl:text>&#160;&#160;</xsl:text></td>
                      </tr>
                    </table>
                    <td class="trimSpace"><xsl:text>&#160;</xsl:text></td>
                  </td>
                  <!-- end inactive tab -->
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
            <td class="trimSpace" width="100%"><xsl:text>&#160;</xsl:text></td>
          </tr>
        </table>

        <table class="topRuleUnderTabs">
          <tr><td></td></tr>
        </table>
      </xsl:when>
      <xsl:when test="not(bebop:tabbedPane)">
        <table class="topRuleNoTabs">
          <tr><td></td></tr>
        </table>
      </xsl:when>
    </xsl:choose>

    <xsl:apply-templates/>

  <!-- end of div class="setInside" -->
  </td></tr></table>

    <table class="bottomRule">
      <tr><td></td></tr>
    </table>

</xsl:template>

<xsl:template match="bebop:splitPanel">
  <table class="splitPanel">
    <xsl:if test="not(bebop:cell/bebop:label[text() = '&amp;nbsp;'])">
      <tr>
        <td colspan="3" class="splitPanelHeader">
          <table width="100%" cellspacing="4" cellpadding="0" border="0">
            <tr><td><xsl:apply-templates select="bebop:cell[1]"/></td></tr>
          </table>
        </td>
      </tr>
      <tr>
        <td colspan="3" class="inactiveTabColor" height="2">
          <img src="{$internal-theme}/images/spacer.gif" height="2"/>
        </td>
      </tr>
    </xsl:if>
    <tr>
      <td class="splitPanelLeft">
        <table width="100%" cellspacing="4" cellpadding="0" border="0">
          <tr><td><xsl:apply-templates select="bebop:cell[2]"/></td></tr>
        </table>
      </td>
      <td class="inactiveTabColor" width="2">
        <img src="{$internal-theme}/images/spacer.gif" width="2"/>
      </td>
      <td class="splitPanelRight">
        <table width="100%" cellspacing="4" cellpadding="0" border="0">
          <tr><td><xsl:apply-templates select="bebop:cell[3]"/></td></tr>
        </table>
      </td>
    </tr>
  </table>
</xsl:template>

<xsl:template match="bebop:tabbedPane">
  <xsl:variable name="tabs" select="bebop:tabStrip/bebop:tab"/>

  <table cellpadding="0" cellspacing="0" border="0" class="tabs">
    <tr>
      <td class="trimSpace"><xsl:text>&#160;</xsl:text></td>
      <xsl:for-each select="$tabs">
        <xsl:choose>
          <xsl:when test="@current = 't'">
            <!-- start currently active tab -->
            <td class="tabBeginning"><xsl:text>&#160;&#160;</xsl:text></td>
            <td class="activeTab" nowrap="nowrap"><xsl:value-of select="bebop:label"/></td>
            <td class="tabEnd"><xsl:text>&#160;&#160;</xsl:text></td>
            <td class="trimSpace"><xsl:text>&#160;</xsl:text></td>
            <!-- end currently active tab -->
          </xsl:when>
          <xsl:otherwise>
            <!-- start inactive tab -->
            <td>
              <table cellpadding="0" cellspacing="0" border="0">
                <tr height="3"><td></td></tr>
                <tr height="23">
                  <td class="tabBeginningOff"><xsl:text>&#160;&#160;</xsl:text></td>
                  <td class="inactiveTab" nowrap="nowrap"><a href="{@href}"><xsl:apply-templates select="bebop:label"/></a>
                  </td>
                  <td class="tabEndOff"><xsl:text>&#160;&#160;</xsl:text></td>
                </tr>
                </table>
               <td class="trimSpace"><xsl:text>&#160;</xsl:text></td>
            </td>
            <!-- end inactive tab -->
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <td class="trimSpace" width="100%"><xsl:text>&#160;</xsl:text></td>
    </tr>
  </table>

  <table class="topRuleUnderTabs">
    <tr><td></td></tr>
  </table>

  <xsl:apply-templates select="bebop:currentPane/*"/>
</xsl:template>

<xsl:template match="bebop:table"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
 <table class="fancy">
   <xsl:for-each select="@*">
     <xsl:attribute name="{name()}">
       <xsl:value-of select="."/>
     </xsl:attribute>
   </xsl:for-each>
   <xsl:apply-templates select="bebop:thead"/>
   <xsl:apply-templates select="bebop:tbody"/>
 </table>
</xsl:template>

<xsl:template match="bebop:table[@class='plain']"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table>
     <xsl:for-each select="@*">
       <xsl:attribute name="{name()}">
         <xsl:value-of select="."/>
       </xsl:attribute>
     </xsl:for-each>
     <xsl:apply-templates select="bebop:thead"/>
     <xsl:apply-templates select="bebop:tbody"/>
   </table>
</xsl:template>

<!-- The template below overrides bebop Link.xsl action link spec so that
we can have a different image -->
<xsl:template name="bebop:actionLink" match="bebop:link[@class='actionLink']">
       <!-- Begin Image -->

       <!-- Image JavaScript  -->
       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>

       <!-- This is ugly, but I need the whole output on one line =p -->
       <![CDATA[ document.write(']]><a href="{@href}" onclick="{@onclick}"><img src="{$internal-theme}/images/action-generic.png" border="0" width="11" height="11"><xsl:attribute name="alt"><xsl:apply-templates mode="javascript-mode"/></xsl:attribute></img><![CDATA[')]]>
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
         <img src="{{$internal-theme}/images/action-generic.png" border="0" width="11" height="11">
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
       <![CDATA[ document.write(']]><a><xsl:for-each select="@*[name() != 'href_no_javascript']"><xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute></xsl:for-each><xsl:apply-templates mode="javascript-mode"/><![CDATA[')]]>
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
