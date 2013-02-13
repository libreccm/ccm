<?xml version="1.0"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        xmlns:news-portlet="http://wsgfl.westsussex.gov.uk/portlet/news/1.0" 
             xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0" 
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                   version="1.0" >

    <xsl:param name="internal-theme"/>

    <xsl:template match="portlet:news">
        <xsl:if test="@personalised= 'true'">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
              <tr>
                <td width="3">
                  <img alt="" src="{$internal-theme}/images/spacer.gif" width="100%" />
                </td>
                <td class="portletText">
                    Latest articles for you...
                </td>
              </tr>
            </table>
        </xsl:if>

        <table width="100%" cellpadding="0" cellspacing="0" border="0">
            <xsl:apply-templates />
              <tr>
                <td colspan="3" width="100%" height="7">
                  <img alt="" src="{$internal-theme}/images/spacer.gif" 
                       width="100%" height="3" />
                </td>
              </tr>
        </table>

        <table width="100%" cellpadding="0" cellspacing="0" border="0">
          <tr>
            <td height="20" width="" valign="middle" class="portletText">
              <br/>
              <img alt = "" src="/STATIC/portlet/spacer.gif" width="8" />
              <a href="{@newsroom-shortcut}">News Room</a>
              <img alt="" src="{$internal-theme}/images/spacer.gif" width="10" 
                          height="1" />
              <a href="{@newsroom-shortcut}">
                <img src="{$internal-theme}/images/portlets/news_arrow.gif" 
                     alt="More News" />
              </a>
            </td>
          </tr>
        </table>

    </xsl:template>

	<xsl:template match="news-portlet:newsItem">
      <tr>
        <td colspan="3" width="100%" height="7">
          <img alt="" src="{$internal-theme}/images/spacer.gif" width="100%" height="3" />
        </td>
      </tr>
      <tr>
        <td height="20" width="" valign="middle" class="portletText">
          <img alt = "" src="{$internal-theme}/images/portlets/arrow_bullet.gif" />
        </td>
        <td height="20" width="423" align="left" valign="middle" class="portletText">
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="@url" />
            </xsl:attribute>
            <xsl:value-of select="@title" />
          </a>
          -
          <xsl:value-of select="@date" />
        </td>
        <td height="20" valign="middle" width="2" class="portletText"></td>
      </tr>
      <tr>
        <td></td>
        <td width="423" align="left" valign="middle" class="portletText">
          <xsl:value-of select="@lead" />
        </td>
      </tr>

    </xsl:template>

</xsl:stylesheet>