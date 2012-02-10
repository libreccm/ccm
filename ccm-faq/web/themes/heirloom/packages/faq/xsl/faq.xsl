<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
    xmlns:faq="http://www.redhat.com/faq/1.0"
    xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
    xmlns:cms="http://www.arsdigita.com/cms/1.0">

<!-- The order of imports below is important. Please don't change
     them without appropriate testing. -->

<xsl:import href="../../acs-admin/xsl/admin_en.xsl"/>
<xsl:import href="../../categorization/xsl/categorization.xsl"/>
<xsl:import href="../../bebop/xsl/DimensionalNavbar.xsl"/>
<xsl:import href="../../toolbox/xsl/ControlBar.xsl"/>
<!-- <xsl:import href="../../faq/xsl/comments.xsl"/> -->


<xsl:output method="html" indent="yes"/>

<xsl:param name="internal-theme"/>


<xsl:template match="bebop:page[@class='faq']">
  <html>
    <head>
      <title><xsl:value-of select="bebop:title"/></title>
      <link href="{$internal-theme}/css/faq.css" rel="stylesheet" type="text/css"/>
       <style type="text/css">
         BODY { background: white; color: black}
         .main   {background-color: #ffffff;}
         table.globalHeader { background-color: rgb(225,225,225);}
         table.bottomRule { background: rgb(162,30,30);}
         table.topRuleNoTabs { background: rgb(162,30,30);}
         </style>

        <xsl:for-each select="bebop:stylesheet">
          <link href="{@href}" rel="stylesheet" type="{@type}"/>
        </xsl:for-each>

<xsl:value-of select="faq:styleblock" disable-output-escaping="yes"/>

    </head>
    <body>
      <xsl:apply-templates select="faq:header"/>

      <xsl:apply-templates select="faq:body"/>

      <xsl:apply-templates select="faq:footer"/>

      <xsl:apply-templates select="faq:styleblock"/>

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

<xsl:template match="faq:header">

  <table class="globalHeader">
    <tr>
      <td class="globalNavigation">
        <xsl:if test="bebop:dimensionalNavbar[@class = 'portalNavbar']">
          <xsl:apply-templates select="bebop:dimensionalNavbar[@class = 'portalNavbar']"/>
        </xsl:if>
      </td>
      <td style="margin: 0; border: 0; padding: 0;">
        <table align="right" style="margin: 0; border: 0; padding: 0;">
          <tr>
            <td style="margin: 0; border: 0; padding: 0; padding-right: 18px;">
            </td>
<!-- Removing help link but maintaining code -->
<!--
                  <td class="global-link-icon"><a href="/assets/cw/help/toc_main.html"><img src="{$internal-theme}/images/lifesaver.png" height="18" width="21"/></a></td>
                  <td class="global-link"><a href="/assets/cw/help/toc_main.html">Help</a></td>
-->
            <td class="global-link-icon">
              <a href="{../faq:global/bebop:link[@class = 'signoutLink']/@href}">
                <img src="{$internal-theme}/images/lock.png" height="18" width="14"/>
              </a>
            </td>
            <td class="global-link">
              <a href="{../faq:global/bebop:link[@class = 'signoutLink']/@href}">Sign out</a>
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

<xsl:template match="faq:body">
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
                      <tr height="3"><td><img src="{$internal-theme}/images/spacer.gif" height="3"/></td></tr>
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

  <xsl:template match="faq:question-answer-pair" xmlns:faq="http://www.arsdigita.com/faq/1.0">
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
  <table class="splitPanel">
    <xsl:if test="not(bebop:cell/bebop:label[text() = '&amp;nbsp;'])">
      <tr>
        <td colspan="3" class="splitPanelHeader">
          <table width="100%" cellspacing="4" cellpadding="0" border="0">
            <tr><td><xsl:apply-templates select="bebop:cell[1]"/></td></tr>
          </table>
        </td>
      </tr>
      <tr><td colspan="3" class="inactiveTabColor" height="2"><img src="{$internal-theme}/images/spacer.gif" height="2"/></td></tr>
    </xsl:if>
    <tr>
      <td class="splitPanelLeft">
        <table width="100%" cellspacing="4" cellpadding="0" border="0">
          <tr><td><xsl:apply-templates select="bebop:cell[2]"/></td></tr>
        </table>
      </td>
      <td class="inactiveTabColor" width="2"><img src="{$internal-theme}/images/spacer.gif" width="2"/></td>
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

<xsl:template match="faq:styleblock">
</xsl:template>

</xsl:stylesheet>
