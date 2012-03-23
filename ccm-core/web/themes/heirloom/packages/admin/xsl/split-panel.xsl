<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                  version="1.0">
 
 <xsl:param name="internal-theme"/>
 

<!-- Sidebar Nav Panel -->
<xsl:template match="*[@class='sidebarNavPanel']">
  <xsl:call-template name="sidebarNavPanel">
    <xsl:with-param name="heading" select="//*[@class='heading']"/>
    <xsl:with-param name="navbar" select="//*[@class='navbar']"/>
    <xsl:with-param name="main" select="//*[@class='main']"/>
    <xsl:with-param name="navbar-title" select="@navbar-title"/>
  </xsl:call-template>
</xsl:template>

<!-- sidebarNavPanel: This is the outer framing for spli-panels.
     The heading param is actually the heading for the first section on 
     the right side -->
<xsl:template name="sidebarNavPanel">
  <xsl:param name="heading">The Heading Goes Here</xsl:param>
  <xsl:param name="navbar">This is the Navbar</xsl:param>
  <xsl:param name="main">This is the Main Display Panel</xsl:param>
  <xsl:param name="navbar-title">Title</xsl:param>
  
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td class="split_pane_left_background" valign="top" width="20%">
        <!-- Left Panel -->

        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td>
              <table border="0" cellpadding="2" cellspacing="0" width="100%">
                <tr>
                  <th class="split_pane_header">
                    <xsl:value-of select="$navbar-title"/>
                  </th>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td bgcolor="#ffffff">
              <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td height="1"/>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td>
              <!-- Left Panel: Navigation, list of objects -->
              <xsl:apply-templates select="$navbar"/>
            </td>
          </tr>
        </table>
      </td>
      <td width="1"/>
      <td valign="top">
        <!-- Right Panel: Where the contents go -->
        <xsl:apply-templates select="$main"/>
      </td>
    </tr>
  </table>
</xsl:template>


<!-- Split-panel: This wraps the navigation in the left panel -->
<xsl:template match="bebop:boxPanel[@class='navbar']">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td>
 <table border="0" cellpadding="1" cellspacing="0" width="100%">
   <tr>
     <td height="8">
       <table border="0" cellpadding="0" cellspacing="0">
	 <tr>
	   <td height="8"/>
	 </tr>
       </table>
     </td>
   </tr>
   <xsl:apply-templates select=".//bebop:list[@class='sidebarNavList']"/>
   <xsl:apply-templates select=".//bebop:tree"/>
   <tr>
     <td height="8">
       <table border="0" cellpadding="0" cellspacing="0">
	 <tr>
	   <td height="8"/>
	 </tr>
       </table>
     </td>
   </tr>
   <xsl:for-each select="bebop:cell/bebop:link">
     <tr>
       <td class="split_pane_left_item">
	 <xsl:apply-templates select="."/>
       </td>
     </tr>
   </xsl:for-each>
 </table>
</td>
</tr>
</table>
</xsl:template>



<!-- Split-panel: The list of objects in the left navigational panel -->
<xsl:template match="bebop:list[@class='sidebarNavList']">
  <xsl:for-each select="bebop:cell"> 
    <xsl:choose>
      <xsl:when test="@selected='selected'">  
      <tr>
        <td class="split_pane_left_item_selected" nowrap="1">
          <xsl:apply-templates/><xsl:text>&#160;</xsl:text><img border="0" height="9" width="5">
            <xsl:attribute name="src">
              <xsl:value-of select="//@assets"/>{$internal-theme}/images/arrow-right.gif</xsl:attribute>
            </img>
          </td>
        </tr>
      </xsl:when>
      <xsl:otherwise>
        <tr>
          <td class="split_pane_left_item" nowrap="1">
            <xsl:apply-templates/>
          </td>
        </tr>
      </xsl:otherwise>
    </xsl:choose>
    <tr>
      <td>
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td height="4"/>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:for-each>
</xsl:template>


<!-- Split-panel: The individual objects in the left navigational panel -->
<xsl:template match="bebop:list[@class='sidebarNavList']/bebop:cell/bebop:link">
<a class="split_pane_left_item" href="{@href}">
<xsl:apply-templates/>
</a>
</xsl:template>



<!-- Format the nav bar when the contents is a tree -->
<xsl:template match="bebop:boxPanel[@class='navbar']//bebop:tree">
<xsl:for-each select="bebop:t_node">
<xsl:call-template name="write-node">
<xsl:with-param name="node" select="."/>
</xsl:call-template>
</xsl:for-each>
</xsl:template>

<!-- pads the label of the submit buttons -->
<xsl:template match="bebop:formWidget[@type='submit']">
  <input value="&#160;&#160;&#160;{@value}&#160;&#160;&#160;">
    <xsl:if test="boolean(@onclick) = false()">
      <xsl:attribute name="onclick">
        <xsl:value-of select="'dcp_hide(this);'"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:for-each select="@*[name() != 'value']">
      <xsl:attribute name="{name()}">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:for-each>
  </input>
</xsl:template>

</xsl:stylesheet>
