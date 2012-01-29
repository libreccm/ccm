<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.SiteProxy']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_SiteProxy">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td class="contentTitle" align="left" valign="top">
          <xsl:value-of select="./title"/>
        </td>
      </tr>            
    </table>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.SiteProxy']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_SiteProxy">
    <h1 class="mainTitle"><xsl:value-of select="./title"/></h1>
  </xsl:template>

  <xsl:template match="cms:siteProxyPanel">
    <xsl:choose>
      <xsl:when test="@dataType='cdata'">
         <xsl:value-of disable-output-escaping="yes" select="."/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:apply-templates select="*"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="CINDEX_collection_list">
       <xsl:choose> 
         <xsl:when test="count(organisation)&gt;1">
           <xsl:call-template name="cms:siteProxyJS"/>
           <xsl:apply-templates select="organisation" mode="multiple"/>
         </xsl:when>
         <xsl:otherwise>
           <xsl:apply-templates select="organisation" mode="single"/>
         </xsl:otherwise>
       </xsl:choose>
  </xsl:template>

  <xsl:template match="organisation" mode="single">
      Organisation:
      <table border="1">
       <xsl:for-each select="*">
          <tr>
          <td>
            <xsl:value-of select="name()"/>
          </td>
          <td>
            <xsl:value-of select="."/>
          </td>
          </tr>
       </xsl:for-each>
      </table>
  </xsl:template>

  <xsl:template match="organisation" mode="multiple">

      <a id="itemLn{ProvId}" href="#itemTable{ProvId}" onClick="itemToggle('{ProvId}')" style="padding-left: 6px;"><xsl:value-of select="OrganisationName"/></a><br />

      <a name="#itemTable{ProvId}">
      <table border="1" style="display: none" id="itemTable{ProvId}">
       <xsl:for-each select="*">
          <tr>
          <td>
            <xsl:value-of select="name()"/>
          </td>
          <td>
            <xsl:value-of select="."/>
          </td>
          </tr>
       </xsl:for-each>
      </table>
      </a>
  </xsl:template>


  <xsl:template name="cms:siteProxyJS">
    <script language="JavaScript">
      <![CDATA[
      <!-- Begin hiding
      function itemToggle(id) {
        var elTable = document.getElementById("itemTable"+id);

        if (elTable.style.display != "block") {
           elTable.style.display = "block";
        } else {
           elTable.style.display = "none";
        }
        return true;
      }
      // End hiding -->
      ]]>
    </script>
   </xsl:template>
</xsl:stylesheet>

