<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
    xmlns:bmrk="http://www.redhat.com/bmrk/1.0"
    xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
    xmlns:cms="http://www.arsdigita.com/cms/1.0">

<!-- The order of imports below is important. Please don't change
     them without appropriate testing. -->

<xsl:import href="../../acs-admin/xsl/admin_en.xsl"/>
<xsl:import href="../../categorization/xsl/categorization.xsl"/>
<xsl:import href="../../bebop/xsl/DimensionalNavbar.xsl"/>
<xsl:import href="../../toolbox/xsl/ControlBar.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:param name="contextPath"/>

<xsl:template match="bebop:page[@class='bmrk']">
  <html>
    <head>
      <title><xsl:value-of select="bebop:title"/></title>
<!--
      <link href="/packages/portalserver/www/css/portalserver.css" rel="stylesheet" type="text/css"/>

       <style type="text/css">
         BODY { background: white; color: black}
         .main   {background-color: #ffffff;}
         table.globalHeader { background-color: rgb(225,225,225);}
         table.bottomRule { background: rgb(162,30,30);}
         table.topRuleNoTabs { background: rgb(162,30,30);}
         </style>
-->

      <link rel="stylesheet" type="text/css" href="/css/acs-master.css" />
      <link rel="stylesheet" type="text/css" href="/__ccm__/static/cms/admin/cms-admin.css" />

      <xsl:for-each select="bebop:stylesheet">
        <link href="{@href}" rel="stylesheet" type="{@type}"/>
      </xsl:for-each>

<xsl:value-of select="bmrk:styleblock" disable-output-escaping="yes"/>

    </head>
    <body>
      <xsl:apply-templates select="bmrk:header"/>

      <xsl:apply-templates select="bmrk:body"/>

      <xsl:apply-templates select="bmrk:footer"/>

      <xsl:apply-templates select="bmrk:styleblock"/>

      <!--
      This is here so the the bebop page structure is displayed under /debug/
      -->
      <xsl:apply-templates select="*[position()>4]"/>
    </body>
  </html>
</xsl:template>

<xsl:template match="bebop:link[@class='portalControlProfileLink']">
       <xsl:call-template name="bebop:link" />
 <br />
</xsl:template>

<xsl:template match="bmrk:header">

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
            <td class="global-link-icon">
             <a href="{../bmrk:global/bebop:link[@class = 'signoutLink']/@href}">
              <img src="/assets/lock.png" height="18" width="14"/>
             </a>
            </td>
            <td class="global-link">
             <a href="{../bmrk:global/bebop:link[@class = 'signoutLink']/@href}">
               Sign out
              </a>
             </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>

  <div id="page-title">
    <xsl:value-of select="../bebop:title"/>
    <table class="localHeader">
      <tr>
        <td class="localControl">
          <xsl:apply-templates select="bebop:link[@class = 'portalControlProfileLink']"/>
          <xsl:apply-templates select="bebop:link[@class = 'portalControl']"/>
        </td>
      </tr>
    </table>
  </div>

</xsl:template>

<xsl:template match="bmrk:body">
  <div>
    <div class="tabbed-pane">

    <xsl:variable name="portalTabs" select="bebop:list[@class = 'portalTabs']"/>

    <xsl:choose>
      <xsl:when test="count($portalTabs/bebop:cell) > 1">
        <!--
        If there is only one tab in this portal, don't bother to
        display it.
        -->

        <table class="tab-set">

          <tr valign="bottom">
            <td class="trimSpace"><xsl:text>&#160;</xsl:text></td>
            <xsl:for-each select="$portalTabs/bebop:cell">
              <xsl:choose>
                <xsl:when test="@selected">
                  <!-- start currently active tab -->
                  <td class="tabBeginning"><xsl:text>&#160;&#160;</xsl:text></td>
                  <td class="current-tab-label" nowrap="nowrap"><xsl:value-of select="bebop:portal/@title"/></td>
                  <td class="current-tab-end"><xsl:text>&#160;&#160;</xsl:text></td>
                  <td class="trimSpace"><xsl:text>&#160;</xsl:text></td>
                  <!-- end currently active tab -->
                </xsl:when>
                <xsl:otherwise>
                  <!-- start inactive tab -->
                  <td>
                    <table cellpadding="0" cellspacing="0" border="0">
                      <tr height="3"><td><img src="/assets/general/spacer.gif" height="3"/></td></tr>
                      <tr height="23">
                         <td class="tabBeginningOff"><xsl:text>&#160;&#160;</xsl:text></td>
                         <td class="tab-label" nowrap="nowrap"><xsl:apply-templates select="bebop:link"/></td>
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

    </div>

    <xsl:apply-templates/>

  </div>

  <table class="bottomRule">
    <tr><td></td></tr>
  </table>

</xsl:template>

  <xsl:template match="bmrk:question-answer-pair" xmlns:bmrk="http://www.arsdigita.com/bmrk/1.0">
    <p>
      <a name="{../@key}"/>
      <h2>
        Q.
        <xsl:apply-templates select="bebop:label[@class='question']"/>
      </h2>
      <blockquote>
        <b>A.</b>
        <xsl:apply-templates select="bebop:label[@class='answer']"/>
      </blockquote>
    </p>
  </xsl:template>


<xsl:template match="cms:dimensionalNavbar[@class = 'portalNavbar']">
  <xsl:for-each select="*">
    <xsl:choose>
      <xsl:when test="position() != last()">
        <xsl:apply-templates select="."/>
        <span class="contextBarSeparator"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;gt;&amp;nbsp;</xsl:text></span>
      </xsl:when>
      <xsl:otherwise>
        <span class="immediateContext"><xsl:apply-templates select="."/></span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template match="bebop:list[@class = 'portalTabs']">
  <xsl:apply-templates select="bebop:cell/bebop:portal"/>
</xsl:template>

<xsl:template match="bebop:splitPanel">
  <table class="current-pane">
    <xsl:if test="not(bebop:cell/bebop:label[text() = '&amp;nbsp;'])">
      <tr>
        <td colspan="3" class="splitPanelHeader">
          <table width="100%" cellspacing="4" cellpadding="0" border="0">
            <tr><td><xsl:apply-templates select="bebop:cell[1]"/></td></tr>
          </table>
        </td>
      </tr>
      <tr><td colspan="3" class="inactiveTabColor" height="2"><img src="/assets/general/spacer.gif" height="2"/></td></tr>
    </xsl:if>
    <tr>
      <td class="splitPanelLeft">
        <table width="100%" cellspacing="4" cellpadding="0" border="0">
          <tr><td><xsl:apply-templates select="bebop:cell[2]"/></td></tr>
        </table>
      </td>
      <td class="inactiveTabColor" width="2"><img src="/assets/general/spacer.gif" width="2"/></td>
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

<xsl:template match="bebop:tabbedPane[@class='portalserver_admin']">
<div class="tabs">
      <xsl:for-each select="bebop:tabStrip/bebop:tab">
        <xsl:choose>
          <xsl:when test="@current">
            <table class="selected">
              <tr>
                <td class="label"><xsl:apply-templates/></td>
                <td class="end"/>
              </tr>
            </table>
          </xsl:when>
          <xsl:otherwise>
            <a href="{@href}">
              <table class="unselected">
                <tr>
                  <td class="label"><xsl:apply-templates/></td>
                  <td class="end"/>
                </tr>
              </table>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <div class="rule"/>
      <xsl:apply-templates select="bebop:currentPane"/>
    </div>
</xsl:template>

<xsl:template match="bebop:itemDetail">
  <table width="100%" cellpadding="2" cellspacing="0" border="0">
    <xsl:for-each select="bebop:property">
      <tr>
        <td width="25%" align="right" valign="top"><b><small><xsl:value-of select="bebop:name"/>:</small></b></td>
        <td width="75%"><xsl:apply-templates select="*[name()!='bebop:name']"/></td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="bebop:action">
      <tr>
        <td width="25%" align="right" valign="top">
          <xsl:if test="position() = 1"><b><small>Actions:</small></b></xsl:if>
        </td>
        <td width="75%"><xsl:apply-templates select="bebop:link"/></td>
      </tr>
    </xsl:for-each>
  </table>
</xsl:template>

<xsl:template match="bebop:label[@class='nullArrow']">
    <img src ="/assets/general/spacer.gif" border="0" width="16" height ="16">
    </img>
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

<xsl:template match="admin:groupInfo[@class='ecm']">
<ul>
  <li> Group Name: <xsl:value-of select="@name"/> </li>
  <li> Primary Email: <xsl:value-of select="@email"/> </li>
</ul>
</xsl:template>

<xsl:template match="bmrk:styleblock">
</xsl:template>

<xsl:template name="bebop:actionLink" match="bebop:link[@class='actionLink']">
       <!-- Begin Image -->

       <!-- Image JavaScript  -->
       <script LANGUAGE="JavaScript">
       <![CDATA[ <!-- begin script ]]>

       <!-- This is ugly, but I need the whole output on one line =p -->
       <![CDATA[ document.write(']]><a href="{@href}" onclick="{@onclick}"><img src="/__ccm__/static/cms/admin/action-group/action-generic.png" border="0" width="11" height="11"><xsl:attribute name="alt"><xsl:apply-templates mode="javascript-mode"/></xsl:attribute></img><![CDATA[')]]>
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
         <img src="/__ccm__/static/cms/admin/action-group/action-generic.png" border="0" width="11" height="11">
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
