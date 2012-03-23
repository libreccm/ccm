<?xml version="1.0"?>
<xsl:stylesheet 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  exclude-result-prefixes="cms"
                  version="1.0"
>


<xsl:import href="../../bebop/xsl/DimensionalNavbar.xsl"/>
<xsl:import href="CMSContainer.xsl"/>
<xsl:import href="../../bebop/xsl/DataTable.xsl"/>
<xsl:import href="../../admin/xsl/split-panel.xsl"/>
<xsl:import href="admin/cms-admin.xsl"/>

<xsl:param name="context-prefix"/>
<xsl:param name="static-prefix"/>
<xsl:param name="internal-theme"/>

<!-- GENERAL CMS ADMIN STYLING -->

<!-- Reusable template to style a table header cell, when the title 
     doesn't contain special stuff -->
     
<!--
<xsl:template name="styled-table-header-cell-simple">
<xsl:param name="title"/>
<td>
<table cellpadding="0" cellspacing="0" border="0">
<tr>
 <td class="table_cell"><xsl:text>&#160;</xsl:text><xsl:text>&#160;</xsl:text></td>
 <th class="table_header" nowrap="nowrap">
   <xsl:value-of select="$title"/>
 </th>
 <td class="table_cell"><xsl:text>&#160;</xsl:text><xsl:text>&#160;</xsl:text></td>
</tr>
</table>
</td>
</xsl:template>
-->

<!-- Stylesheets cut and pasted from permissions.xsl; couldn't just import
     permissions.xsl, because it imports cms.xsl, causing a loop -->
<xsl:template match="bebop:link[@class='checkBoxChecked']">
  <a href="{@href}" onclick="{@onclick}">
    <img src="{$internal-theme}/images/checkbox-checked.gif" 
         border="0" width="12" height="12" alt="{./bebop:label}">
    </img>
  </a>
  <xsl:text>&#160;</xsl:text>
  <a href="{@href}" onclick="{@onclick}" class="action_link">
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="bebop:link[@class='checkBoxUnchecked']">
  <a href="{@href}" onclick="{@onclick}">
    <img src="{$internal-theme}/images/checkbox-unchecked.gif" 
         border="0" width="12" height="12" alt="{./bebop:label}">
    </img>
  </a>
  <xsl:text>&#160;</xsl:text>
  <a href="{@href}" onclick="{@onclick}" class="action_link">
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="bebop:label[@class='checkBoxGreyChecked']">
  <img src="{$internal-theme}/images/checkbox-checked-gray.gif" 
       border="0" width="12" height="12" alt="{./bebop:label}">
  </img>
  <xsl:text>&#160;</xsl:text>
</xsl:template>

<xsl:template match="bebop:label[@class='checkBoxGreyUnchecked']">
  <img src="{$internal-theme}/images/checkbox-unchecked-gray.gif" 
       border="0" width="12" height="12" alt="{./bebop:label}">
  </img>
  <xsl:text>&#160;</xsl:text>
</xsl:template>


</xsl:stylesheet>
