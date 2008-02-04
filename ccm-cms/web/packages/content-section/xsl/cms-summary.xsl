<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:cms="http://www.arsdigita.com/cms/1.0"
	xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
	exclude-result-prefixes="cms">

  <xsl:template match="cms:workflowSummary">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
              <td>
                <xsl:text>&#160;</xsl:text>
              </td>
            </tr>
</table>
<div class="section">
                <div class="heading">Workflow Comments</div>
                              <div class="body">
                                 <div class="action-group">
                                    <div class="subject">
<table class="property-list">
<xsl:if test="@restartWorkflowURL"><tr><td><a href="{@restartWorkflowURL}">Restart editing</a></td></tr></xsl:if>
      <xsl:for-each select="cms:task">
      <tr>
	<td class="form_label">
	  <xsl:value-of select="@name"/> <font size="-1">(<xsl:value-of select="@state" />)</font><br /><br />
        </td>
      </tr>
      <xsl:if test="not(cms:taskComment)">
            <tr>
              <td valign="top" class="form_label">
                <i>No comments</i><br /><br />
              </td>
            </tr>
      </xsl:if>
	<xsl:for-each select="cms:taskComment">
            <tr>
              <td valign="top" class="form_label">
                <xsl:value-of select="@comment" /> - <i><xsl:value-of select="@author"/>, <xsl:value-of select="@date"/></i>
		<xsl:if test="position() = last()">
		  <br /><br />
		</xsl:if>
              </td>
            </tr>
	    </xsl:for-each>
      </xsl:for-each>
    </table>
</div></div></div></div>
  </xsl:template>

  <xsl:template match="cms:lifecycleSummary">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
              <td>
                <xsl:text>&#160;</xsl:text>
              </td>
            </tr>
</table>
<div class="section">
                <div class="heading">Lifecycle</div>
                              <div class="body">
                                 <div class="action-group">
                                    <div class="subject">
<table class="property-list">
  <xsl:choose>
    <xsl:when test="@noLifecycle">
	<tr>
              <td valign="top" class="form_label">
                <i>No lifecycle</i>
              </td>
	</tr>
    </xsl:when>
    <xsl:otherwise>
	<tr>
              <td valign="top" class="form_label">
                <xsl:value-of select="@name"/>
              </td>
	</tr>
	<tr>
	      <td valign="top" class="form_label">
		<xsl:choose>
		  <xsl:when test="@hasBegun='false'">
		    This item is scheduled to be published on <xsl:value-of select="@startDate" /> and will <xsl:value-of select="@endDateString" />.
		  </xsl:when>
		  <xsl:when test="@hasEnded='true'">
		    This item was published on <xsl:value-of select="@startDate" /> and expired on <xsl:value-of select="@endDate" />.
		  </xsl:when>
		  <xsl:otherwise>
		    This item was published on <xsl:value-of select="@startDate" /> and will <xsl:value-of select="@endDateString" />.
		  </xsl:otherwise>
		</xsl:choose>
	      </td>
	</tr>
    </xsl:otherwise>
  </xsl:choose>
</table>
</div></div></div></div>
  </xsl:template>

  <xsl:template match="cms:transactionSummary">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
              <td>
                <xsl:text>&#160;</xsl:text>
              </td>
            </tr>
</table>
<div class="section">
                <div class="heading">Revisions</div>
                              <div class="body">
                                 <div class="action-group">
                                    <div class="subject">
<table class="property-list" cellspacing="5">
      <tr><td valign="top" class="form_label">Current Revision</td><td valign="top" class="form_label"><xsl:value-of select="@lastModifiedDate" /></td></tr>
      <xsl:for-each select="cms:transaction">
            <tr>
              <td valign="top" class="form_label">
                <xsl:value-of select="@author"/>
              </td>
              <td valign="top" class="form_label">
                <xsl:value-of select="@date"/>
              </td>
              <td valign="top" class="form_label">
                <a href="/ccm{@url}">View</a>
              </td>
            </tr>
      </xsl:for-each>
      <tr><td valign="top" class="form_label">Initial Revision</td><td valign="top" class="form_label"><xsl:value-of select="@creationDate" /></td></tr>
    </table>
</div></div></div></div>
  </xsl:template>

  <xsl:template match="cms:categorySummary">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
              <td>
                <xsl:text>&#160;</xsl:text>
              </td>
            </tr>
</table>
<div class="section">
                <div class="heading">Categories</div>
                              <div class="body">
                                 <div class="action-group">
                                    <div class="subject">
<table>
 <xsl:for-each select="cms:category">
            <tr>
              <td valign="top" class="form_label">
                <xsl:value-of  disable-output-escaping="yes" select="."/>
              </td>
            </tr>
      </xsl:for-each>
   </table>
</div></div></div></div>
</xsl:template>

  <xsl:template match="cms:itemSummary">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
              <td>
                <xsl:text>&#160;</xsl:text>
              </td>
            </tr>
</table>
<div class="section">
                <div class="heading">Item Summary</div>
                              <div class="body">
                                 <div class="action-group">
                                    <div class="subject">
<table>
 	    <tr>       
              <td valign="top" class="form_label">
                <b>Name:</b> <xsl:value-of select="@name"/>
              </td>
            </tr>

            <tr>
              <td valign="top" class="form_label">
                <b>Title:</b> <xsl:value-of select="@title"/>
              </td>
            </tr>

            <tr>
              <td valign="top" class="form_label">
                <table>
                  <tr>
                    <td class="form_label">
                      <b>Subject Categories:</b> 
                    </td>
                    <td class="form_label">
                      <xsl:for-each select="cms:subjectCategories/cms:subjectCategory">
                        <xsl:if test="position()!=1"><br/></xsl:if>
                        <xsl:value-of  disable-output-escaping="yes" select="."/>
                      </xsl:for-each>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>

            <tr>
              <td valign="top" class="form_label">
                <b>Description:</b> <xsl:value-of disable-output-escaping="yes" select="@description" />
              </td>
            </tr>

    </table>
</div></div></div></div>
  </xsl:template>

  <xsl:template match="cms:linkSummary">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
              <td>
                <xsl:text>&#160;</xsl:text>
              </td>
            </tr>
</table>
<div class="section">
                <div class="heading">Stable Link</div>
                              <div class="body">
                                 <div class="action-group">
                                    <div class="subject">
<table>
 	    <tr>       
              <td valign="top" class="form_label">
                <a href="{@url}" class="form_label"><xsl:value-of select="@url" /></a>
              </td>
            </tr>
    </table>
</div></div></div></div>
  </xsl:template>

</xsl:stylesheet>

