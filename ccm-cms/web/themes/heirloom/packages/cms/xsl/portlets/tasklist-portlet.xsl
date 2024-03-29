<?xml version="1.0"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
             xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
                   version="1.0">

<xsl:template match="portlet:taskListTask">
  <td class="table_cell">
    <a href="{@summaryURL}">
      <xsl:value-of select="@pageTitle"/>
    </a>
  </td>
  <td class="table_cell">
    <a href="{@authoringURL}">
      <xsl:value-of select="@taskType"/>
    </a>
  </td>
  <td class="table_cell">
    <xsl:value-of select="@dueDate"/>
  </td>
  <td class="table_cell">
    <xsl:value-of select="@isLocked"/>,
    <xsl:if test="@assigneeCount='0'">
        No assigned users
    </xsl:if>
    <xsl:if test="@assigneeCount='1'">
        1 assigned user
    </xsl:if>
    <xsl:if test="@assigneeCount>'1'">
        <xsl:value-of select="@assigneeCount"/> assigned users
    </xsl:if>
  </td>
</xsl:template>

<xsl:template match="portlet:taskList">
  <xsl:if test="count(portlet:taskListTask)='0'">
    <p>No tasks to display.</p>
  </xsl:if>
  <xsl:if test="count(portlet:taskListTask)>'0'">
  <table cellpadding="2" cellspacing="2" border="0">
    <tr><td>Item Title</td><td>Action</td><td>Due Date</td><td>Status</td></tr>
    <xsl:for-each select="portlet:taskListTask">
      <tr>
        <xsl:attribute name="class">
          <xsl:choose>
            <xsl:when test="position() mod 2">table_row_odd</xsl:when>
            <xsl:otherwise>table_row_even</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:apply-templates select="."/>
      </tr>
    </xsl:for-each>
  </table>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
