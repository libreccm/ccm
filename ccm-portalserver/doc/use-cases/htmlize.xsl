<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<xsl:output method="html" indent="yes"/> 
<xsl:variable name="release-target">2.0</xsl:variable>


<xsl:template match="use_case">
<HTML>
<HEAD>
<title>Use Case <xsl:value-of select="id/@number"/>:<xsl:value-of select="title"/></title>
<link rel="stylesheet" type="text/css"
      href="./developer.css"></link>
<link rel="stylesheet" type="text/css"
      href="./cw-use-cases.css"></link>
</HEAD>
<BODY>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td>
        <a href="..">CCM Portalserver Project</a> &gt;
        <a href="./index.html">Use Cases Index Page</a> &gt;
    </td>
    <td align="right">
        <a href="http://www.redhat.com/ccm">CCM Central</a> |
        <a href="http://www.redhat.com/">Red Hat</a>
    </td>
  </tr>
</table>

<xsl:choose>
<xsl:when test="(number(release_target/@release) &gt; 2.0)">
<h1><font color="red">Use Case <xsl:value-of select="id/@number"/>:<xsl:value-of select="title"/></font></h1>
<p></p>
<h3><font color="red">Release Target: <xsl:value-of select="release_target/@release"/></font></h3>
</xsl:when>
<xsl:otherwise>
<h1>Use Case <xsl:value-of select="id/@number"/>:<xsl:value-of select="title"/></h1>
<p></p>
<h3>Release Target: <xsl:value-of select="release_target/@release"/></h3>
</xsl:otherwise>
</xsl:choose>
<p></p>
<hr/>
<p></p>
<table border="2" cellpadding ="1" width="100%" align="top">
<tr>
<td>
<span class="optional">Description:  </span> 
</td>
<td>
<xsl:value-of select="description" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="required">Goal Level:  </span>
</td>
<td>
<xsl:value-of select="goal_level/@value"/>
</td>
</tr>
<tr>
<td>
<span class="required">Actors:  </span>
</td>
<td>
<ul>
<xsl:for-each select="actors/actor">
<li><xsl:value-of select="@name"/></li>
</xsl:for-each>
</ul>
</td>
</tr>
<tr>
<td>
<span class="required">Trigger:  </span>
</td>
<td>
<xsl:value-of select="trigger/@value"/>
</td>
</tr>
<tr>
<td>
<span class="optional">Preconditions:  </span>
</td>
<td>
<xsl:value-of select="preconditions" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="required">Basic Path:  </span>
</td>
<td>
<xsl:value-of select="basic_path" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="optional">Postconditions:  </span>
</td>
<td>
<xsl:value-of select="postconditions" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="required">Succesful End Conditions:  </span>
</td>
<td>
<xsl:value-of select="success_end_conditions" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="optional">Alternate Paths:  </span>
</td>
<td>
<xsl:value-of select="alternative_paths" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="optional">Exception Paths:  </span>
</td>
<td>
<xsl:value-of select="exception_paths" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="optional">Failure End Conditions:  </span>
</td>
<td>
<xsl:value-of select="failure_end_conditions" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="optional">Sample Narrative:  </span>
</td>
<td>
<xsl:value-of select="sample_narrative" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="optional">Open Issues:  </span>
</td>
<td>
<xsl:value-of select="open_issues" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="required">Iteration:  </span>
</td>
<td>
<xsl:value-of select="iteration" disable-output-escaping="yes"/>
</td>
</tr>
<tr>
<td>
<span class="required">Status:  </span>
</td>
<td>
<xsl:value-of select="status/@value" disable-output-escaping="yes"/>
</td>
</tr>
</table>
<p></p>
by <xsl:value-of select="author/@name"/>
<p></p>
Last Modified: <xsl:value-of select="last_modified/@date"/>
</BODY>
</HTML>

</xsl:template>

</xsl:stylesheet>	
