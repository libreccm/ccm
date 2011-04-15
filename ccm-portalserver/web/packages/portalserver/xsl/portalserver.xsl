<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
    xmlns:portalserver="http://www.redhat.com/portalserver/1.0"
    xmlns:portlet="http://www.arsdigita.com/portlet/1.0"
    xmlns:km="http://www.arsdigita.com/km/1.0"
    xmlns:deditor="http://www.arsdigita.com/deditor/1.0"
    xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
    xmlns:cms="http://www.arsdigita.com/cms/1.0">

<!-- The order of imports below is important. Please don't change
     them without appropriate testing. -->

<xsl:import href="../../acs-admin/xsl/admin_en.xsl"/>
<xsl:import href="../../categorization/xsl/categorization.xsl"/>
<xsl:import href="../../bebop/xsl/DimensionalNavbar.xsl"/>
<xsl:import href="../../toolbox/xsl/ControlBar.xsl"/>
<xsl:import href="../../portlets/xsl/freeform-html-portlet.xsl"/>
<xsl:import href="../../portlets/xsl/time-of-day-portlet.xsl"/>
<xsl:import href="../../portlets/xsl/rss-feed-portlet.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:param name="contextPath"/>

<xsl:variable name="assets-dir">/packages/portalserver/www/assets</xsl:variable>
<xsl:variable name="css-dir">/packages/portalserver/www/css</xsl:variable>

<xsl:template match="bebop:page[@class='portalserver']">
  <html>
    <head>
      <title><xsl:value-of select="bebop:title"/></title>
      <link href="{$css-dir}/portalserver.css" rel="stylesheet" type="text/css"/>
        <xsl:for-each select="bebop:stylesheet">
          <link href="{@href}" rel="stylesheet" type="{@type}"/>
        </xsl:for-each>

<xsl:value-of select="portalserver:styleblock" disable-output-escaping="yes"/>

    </head>
    <body>
      <xsl:apply-templates select="portalserver:header"/>

      <xsl:apply-templates select="portalserver:body"/>

      <xsl:apply-templates select="portalserver:footer"/>

      <xsl:apply-templates select="portalserver:styleblock"/>

      <!--
      This is here so the the bebop page structure is displayed under /debug/
      -->
      <xsl:apply-templates select="*[position()>4]"/>
    </body>
  </html>
</xsl:template>

<xsl:template match="bebop:page[@class='portalserveradmin']">
  <html>
    <head>
      <title><xsl:value-of select="bebop:title"/></title>
      <link href="{$css-dir}/portalserver.css" rel="stylesheet" type="text/css"/>
        <xsl:for-each select="bebop:stylesheet">
          <link href="{@href}" rel="stylesheet" type="{@type}"/>
        </xsl:for-each>

<xsl:value-of select="portalserver:styleblock" disable-output-escaping="yes"/>

    </head>
    <body>
      <xsl:apply-templates select="portalserver:header"/>

      <xsl:apply-templates select="portalserver:body"/>

      <xsl:apply-templates select="portalserver:footer"/>

      <xsl:apply-templates select="portalserver:styleblock"/>

      <!--
      This is here so the the bebop page structure is displayed under /debug/
      -->
      <xsl:apply-templates select="*[position()>4]"/>
    </body>
  </html>
</xsl:template>

<xsl:template match="km:search">
  <form class="globalSearch" action="{@url}" method="get" name="SimpleSearchForm">
    <input type="text" name="searchString" value="Search" size="12" onClick="this.value = '';"></input>
  </form>
</xsl:template>

<xsl:template match="bebop:link[@class='portalControlProfileLink']">
       <xsl:call-template name="bebop:link" />
 <br />
</xsl:template>

<xsl:template match="portalserver:header">

  <table class="globalHeader">
    <tr>
     <xsl:if test="@id='admin'"> <!-- show logo for admin page headers -->
      <td class="globalLogo"></td>
     </xsl:if>
      <td class="globalNavigation">
        <xsl:if test="bebop:dimensionalNavbar[@class = 'portalNavbar']">
          <xsl:apply-templates select="bebop:dimensionalNavbar[@class = 'portalNavbar']"/>
        </xsl:if>
      </td>
      <td style="margin: 0; border: 0; padding: 0;">
        <table align="right" style="margin: 0; border: 0; padding: 0;">
          <tr>
            <td style="margin: 0; border: 0; padding: 0; padding-right: 18px;">
              <xsl:apply-templates select="km:search"/>
            </td>
             <!-- Help link currently commented out, but code retained -->
             <!--     <td class="global-link-icon"><a href="/assets/help/toc_main.html"><img src="/assets/lifesaver.png" height="18" width="21"/></a></td> 
                  <td class="global-link"><a href="/assets/help/toc_main.html">Help</a></td> -->

            <xsl:if test="../portalserver:global/bebop:link[@id='personalize_link']">
             <td>
              <xsl:apply-templates select="../portalserver:global/bebop:link[@id='personalize_link']"/>
             </td>
            </xsl:if>

                  <td class="global-link-icon"><a href="{../portalserver:global/bebop:link[@class = 'signoutLink']/@href}"><img src="/assets/lock.png" height="18" width="14"/></a></td>
                  <td class="global-link"><a href="{../portalserver:global/bebop:link[@class = 'signoutLink']/@href}">Sign out</a></td>

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


  <table class="setInside"><tr><td class="setInside">

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

  </td></tr></table>
</xsl:template>

<xsl:template match="portalserver:body">
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
                      <tr height="3"><td><img src="/assets/general/spacer.gif" height="3"/></td></tr>
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

<xsl:template match="bebop:list[@class = 'tabLayouts']">
  <table class="tabLayouts">
    <tr>
      <xsl:for-each select="*">
        <td>
          <xsl:apply-templates/>
        </td>
      </xsl:for-each>
    </tr>
  </table>
</xsl:template>

<xsl:template match="bebop:link[@class = 'W']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/W_off.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:link[@class = 'NW']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/NW_off.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:link[@class = 'WN']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/WN_off.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:link[@class = 'NWN']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/NWN_off.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:link[@class = 'NNN']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/NNN_off.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:label[@class = 'W']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/W_on.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:label[@class = 'NW']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/NW_on.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:label[@class = 'WN']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/WN_on.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:label[@class = 'NWN']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/NWN_on.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:label[@class = 'NNN']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="/assets/cw/NNN_on.png" border="0" width="70" height ="30">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
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

<xsl:template match="bebop:splitPanel[@class='archiver']">
  <table class="splitPanel">
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
      <td width="35%">
        <table width="100%" cellspacing="4" cellpadding="0" border="0">
          <tr><td><xsl:apply-templates select="bebop:cell[2]"/></td></tr>
        </table>
      </td>
      <td class="inactiveTabColor" width="2"><img src="/assets/general/spacer.gif" width="2"/></td>
      <td width="65%">
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
   <table class="tab-set">
    <tr>
      <xsl:for-each select="bebop:tabStrip/bebop:tab">
        <xsl:choose>
          <xsl:when test="@current">
                <td class="current-tab-label"><xsl:apply-templates/></td>
                <td class="current-tab-end"/>
          </xsl:when>
          <xsl:otherwise>
                  <td class="label"><a href="{@href}"><xsl:apply-templates/></a></td>
                  <td class="end"/>
          </xsl:otherwise>
        </xsl:choose>
       <td class="tab-spacer"/>
      </xsl:for-each>
    </tr>
   </table> 
  <table class="rule"><tr><td></td></tr></table>
  <xsl:apply-templates select="bebop:currentPane"/>
 </div>
</xsl:template>

<xsl:template match="bebop:portal">
  <xsl:choose>
    <xsl:when test="sum(bebop:portlet) = 0">
      <p align="center"><em>This tab does not have any portlets yet.</em></p>
    </xsl:when>
    <xsl:otherwise>
      <table class="portalLayoutNW" cellspacing="0">
        <tr>
          <td class="narrowColumn">
            <xsl:for-each select="bebop:portlet[@cellNumber = '1']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
          <td class="columnSeparator"><img src="/assets/general/spacer.gif" width="5"/></td>
          <td class="wideColumn">
            <xsl:for-each select="bebop:portlet[@cellNumber = '2']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="bebop:portal[@style = 'W']">
  <xsl:choose>
    <xsl:when test="sum(bebop:portlet) = 0">
      <p align="center"><em>This tab does not have any portlets yet.</em></p>
    </xsl:when>
    <xsl:otherwise>
      <table class="portalLayoutW" cellspacing="0">
        <tr>
          <td class="VeryWideColumn">
              <xsl:apply-templates/>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="bebop:portal[@style = 'NW']">
  <xsl:choose>
    <xsl:when test="sum(bebop:portlet) = 0">
      <p align="center"><em>This tab does not have any portlets yet.</em></p>
    </xsl:when>
    <xsl:otherwise>
      <table class="portalLayoutNW" cellspacing="0">
        <tr>
          <td class="narrowColumn">
            <xsl:for-each select="bebop:portlet[@cellNumber = '1' or @cellNumber = '3']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
          <td class="columnSeparator"><img src="/assets/general/spacer.gif" width="5"/></td>
          <td class="wideColumn">
            <xsl:for-each select="bebop:portlet[@cellNumber = '2']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="bebop:portal[@style = 'WN']">
  <xsl:choose>
    <xsl:when test="sum(bebop:portlet) = 0">
      <p align="center"><em>This tab does not have any portlets yet.</em></p>
    </xsl:when>
    <xsl:otherwise>
      <table class="portalLayoutWN" cellspacing="0">
        <tr>
          <td class="wideColumn">
            <xsl:for-each select="bebop:portlet[@cellNumber = '1' or @cellNumber = '3']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
          <td class="columnSeparator"><img src="/assets/general/spacer.gif" width="5"/></td>
          <td class="narrowColumn">
            <xsl:for-each select="bebop:portlet[@cellNumber = '2']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="bebop:portal[@style = 'NWN']">
  <xsl:choose>
    <xsl:when test="sum(bebop:portlet) = 0">
      <p align="center"><em>This tab does not have any portlets yet.</em></p>
    </xsl:when>
    <xsl:otherwise>
      <table class="portalLayoutNWN" cellspacing="0">
        <tr>
          <td class="narrowColumnLeft">
            <xsl:for-each select="bebop:portlet[@cellNumber = '1']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
          <td class="columnSeparator"><img src="/assets/general/spacer.gif" width="5"/></td>
          <td class="wideColumn">
            <xsl:for-each select="bebop:portlet[@cellNumber = '2']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
          <td class="columnSeparator"><img src="/assets/general/spacer.gif" width="5"/></td>
          <td class="narrowColumnRight">
            <xsl:for-each select="bebop:portlet[@cellNumber = '3']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="bebop:portal[@style = 'NNN']">
  <xsl:choose>
    <xsl:when test="sum(bebop:portlet) = 0">
      <p align="center"><em>This tab does not have any portlets yet.</em></p>
    </xsl:when>
    <xsl:otherwise>
      <table class="portalLayoutNNN" cellspacing="0">
        <tr>
          <td class="narrowColumnLeft">
            <xsl:for-each select="bebop:portlet[@cellNumber = '1']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
          <td class="columnSeparator"><img src="/assets/general/spacer.gif" width="5"/></td>
          <td class="narrowColumnCenter">
            <xsl:for-each select="bebop:portlet[@cellNumber = '2']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
          <td class="columnSeparator"><img src="/assets/general/spacer.gif" width="5"/></td>
          <td class="narrowColumnRight">
            <xsl:for-each select="bebop:portlet[@cellNumber = '3']">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="bebop:portlet">
  <table class="portlet" cellspacing="0">  <!-- IE5.5 ignores border-spacing CSS prop, so cellspacing attr is necesssary -->
    <tr>
      <td class="portletHeader"><xsl:value-of select="@title"/></td>
      <td class="portletIcon">
        <xsl:choose>
          <xsl:when test="@configure">
            <xsl:if test="@leftURL">
              <a href="{@leftURL}"><img src="{$assets-dir}/Left16.gif" width="16" height="16" alt="Move Portlet left" align="bottom" border="0"/></a>
            </xsl:if>

            <xsl:if test="@prevURL">
              <a href="{@prevURL}"><img src="{$assets-dir}/Up16.gif" width="16" height="16" alt="Move Portlet Up" align="bottom" border="0"/></a>
            </xsl:if>

            <xsl:if test="@nextURL">
              <a href="{@nextURL}"><img src="{$assets-dir}/Down16.gif" width="16" height="16" alt="Move Portlet Down" align="bottom" border="0"/></a>
            </xsl:if>

            <xsl:if test="@rightURL">
              <a href="{@rightURL}"><img src="{$assets-dir}/Right16.gif" width="16" height="16" alt="Move Portlet right" align="bottom" border="0"/></a>
            </xsl:if>

            <xsl:if test="@cfgURL">
              <a href="{@cfgURL}"><img src="/assets/general/Edit16.gif" width="16" height="16" alt="Customize Portlet" align="bottom" border="0"/></a>
            </xsl:if>

            <a href="{@delURL}" onclick="return confirm('Are you sure you want to delete this portlet?')"><img src="{$assets-dir}/Delete16.gif" width="16" height="16" alt="Remove Portlet" align="bottom" border="0"/></a>
          </xsl:when>

          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="@applicationlink">
                <a href="{@applicationlink}"><img src="{$assets-dir}/ZoomIn16.gif" width="16" height="16" alt="Zoom In" align="bottom" border="0"/></a>
              </xsl:when>

              <xsl:otherwise>
                <!-- replace with grayed out button -->
                <img src="/assets/general/spacer.gif" width="16" height="16" align="bottom" border="0"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
    <tr>
      <td class="portletBody" colspan="2">
        <xsl:apply-templates/>
      </td>
    </tr>
  </table>
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

<xsl:template match="portalserver:appsDisplay">
  <table class="sectionHeader" width="100%">
    <tbody>
      <tr><td>View and Manage <xsl:value-of select="@name"/></td></tr>
    </tbody>
  </table>
  <table width="100%">
    <tbody>
      <tr>
        <td align="right"><xsl:apply-templates select="bebop:link"/></td>
      </tr>
    </tbody>
  </table>
  <br/>
  <table border="0" width="100%" class="fancy">
    <thead>
      <tr>
        <th><xsl:value-of select="@name"/></th>
        <th>Description</th>
        <th class="icon">Configure Tool</th>
        <th><xsl:text>&#160;</xsl:text></th>
        <th class="numeric"><xsl:text>&#160;</xsl:text></th>
        <th>Portlets</th>
        <th class="icon">Configure Portlet</th>
      </tr>
    </thead>
    <tbody>
      <xsl:for-each select="portalserver:appsDisplayApp">
        <xsl:if test="position() > 1">
          <tr><td class="subDivider" colspan="7">
            <img src="/packages/jigsaw/www/assets/spacer.gif"
              width="1" height="2"/>
          </td></tr>
        </xsl:if>
        <tr>
          <xsl:variable name="portletCount" select="count(portalserver:appsDisplayAppPortlet)"/>
          <td rowspan="{$portletCount}">
            <xsl:apply-templates select="bebop:link[1]"/>
          </td>
          <td rowspan="{$portletCount}">
            <xsl:value-of select="portalserver:appsDisplayAppDescription"/>
            <xsl:text>&#160;</xsl:text>
          </td>
          <td rowspan="{$portletCount}" class="icon">
            <xsl:apply-templates select="bebop:link[2]"/>
          </td>
          <td rowspan="{$portletCount}">
            <xsl:text>&#160;</xsl:text>
          </td>
          <xsl:choose>
            <xsl:when test="$portletCount > 0">
              <td class="numeric">1.</td>
              <td><xsl:value-of select="portalserver:appsDisplayAppPortlet[1]/@name"/></td>
              <td class="icon"><xsl:apply-templates select="portalserver:appsDisplayAppPortlet[1]/bebop:link"/></td>
            </xsl:when>
            <xsl:otherwise>
              <td class="numeric"><xsl:text>&#160;</xsl:text></td>
              <td><i>None</i></td>
              <td class="icon"><xsl:text>&#160;</xsl:text></td>
            </xsl:otherwise>
          </xsl:choose>
        </tr>
        <xsl:for-each select="portalserver:appsDisplayAppPortlet[position() > 1]">
          <tr>
            <td class="numeric"><xsl:value-of select="position()+1"/>.</td>
            <td><xsl:value-of select="@name"/></td>
            <td class="icon"><xsl:apply-templates select="bebop:link"/></td>
          </tr>
        </xsl:for-each>
      </xsl:for-each>
    </tbody>
  </table>
</xsl:template>

<xsl:template match="km:portletSimpleSearch">
  <form action="{@url}search.jsp" method="get" name="SimpleSearchForm">
    <table border="0" cellpadding="0" cellspacing="2" width="100%">
      <tr>
        <td>
          <table width="100%" cellspacing="2" cellpadding="0" border="0">
            <tr>
              <td>
                <input name="searchString" type="text" size="40" value=""></input>
                <xsl:text>&#160;</xsl:text>
                <input value="  Search   " name="search" type="submit"/>
              </td>
            </tr>
            <tr>
              <td>
                <input type="radio" name="scope" value="this" checked="checked"/> This Workspace
                <input type="radio" name="scope" value="all"/> Entire System
              </td>
            </tr>
          </table>
        </td>
        <td><a href="{@url}search-advanced.jsp">Advanced Search</a></td>
      </tr>
    </table>
  </form>
</xsl:template>

<xsl:template match="bebop:link[@class = 'downloadLink']">
  <a href="{@href}" onclick="{@onclick}">
    <img border="0">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
      <xsl:attribute name="src">
        <xsl:value-of select="//@assets"/>/assets/download.gif</xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:link[@class = 'shiftleft']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="{$assets-dir}/Up16.gif" border="0" width="16" height ="16">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
</xsl:template>

<xsl:template match="bebop:link[@class = 'shiftright']">
  <a href="{@href}" onclick="{@onclick}">
    <img src ="{$assets-dir}/Down16.gif" border="0" width="16" height ="16">
      <xsl:attribute name="alt">
        <xsl:apply-templates/>
      </xsl:attribute>
    </img>
  </a>
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

<xsl:template match="portalserver:styleblock">
</xsl:template>

<xsl:template match="bebop:boxPanel[@class='portaladminapps']">
    <!--This template is for the portal-admin table of admin apps -->
    <table border="0" width="100%" class="fancy">
      <thead>
        <tr>
          <th>Application Link</th>
        </tr>
      </thead>
      <tbody>
         <xsl:for-each select="./bebop:cell">
          <tr><td> 
          <xsl:apply-templates select="bebop:link"/>
          </td></tr> 
         </xsl:for-each>
       </tbody>
       </table>
</xsl:template>

<xsl:template match="bebop:radioGroup[@class='colorchoices']">
  <table class="colorband">
    <tr>
      <xsl:for-each select="bebop:radio">
        <td bgcolor="{@value}"></td>
      </xsl:for-each>
    </tr>
    <tr>
      <xsl:for-each select="bebop:radio">
        <td><xsl:apply-templates select="."/></td>
      </xsl:for-each>
    </tr>
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
       <![CDATA[ document.write(']]><a href="{@href}" onclick="{@onclick}"><img src="{$contextPath}/assets/action-generic.png" border="0" width="11" height="11"><xsl:attribute name="alt"><xsl:apply-templates mode="javascript-mode"/></xsl:attribute></img><![CDATA[')]]>
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
         <img src="{$contextPath}/assets/action-generic.png" border="0" width="11" height="11">
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


<xsl:template match="bebop:list[@class='onlinelist']">
 <table border="1px" width="100%"><tr><td>
  <table width="100%">
    <tr bgcolor="#A21E1E">
      <td><font color="#E1E1E1">Portal Name</font></td>
      <td><font color="#E1E1E1">Creation Date</font></td>
    </tr>
  <xsl:for-each select="bebop:cell">
    <tr>
      <xsl:choose>
        <xsl:when test="@selected">
          <td><xsl:value-of select="bebop:label"/></td>
          <td><xsl:value-of select="bebop:label/@style"/></td> 
        </xsl:when>
        <xsl:otherwise>
          <td><xsl:apply-templates select="bebop:link"/></td>
          <td><xsl:value-of select="bebop:link/@style"/></td> 
        </xsl:otherwise>
      </xsl:choose>
    </tr>
   </xsl:for-each>
  </table>
  </td></tr></table>
</xsl:template>

<xsl:template match="bebop:list[@class='archivelist']">
 <table border="1px" width="100%"><tr><td>
  <table width="100%">
    <tr bgcolor="#A21E1E">
      <td><font color="#E1E1E1">Portal Name</font></td>
      <td><font color="#E1E1E1">Archive Date</font></td>
    </tr>
  <xsl:for-each select="bebop:cell">
    <tr>
      <xsl:choose>
        <xsl:when test="@selected">
          <td><xsl:value-of select="bebop:label"/></td>
          <td><xsl:value-of select="bebop:label/@style"/></td> 
        </xsl:when>
        <xsl:otherwise>
          <td><xsl:apply-templates select="bebop:link"/></td>
          <td><xsl:value-of select="bebop:link/@style"/></td> 
        </xsl:otherwise>
      </xsl:choose>
    </tr>
   </xsl:for-each>
  </table>
  </td></tr></table>
</xsl:template>

</xsl:stylesheet>
