<?xml version="1.0"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  exclude-result-prefixes="cms" version="1.0">

  <xsl:import href="../../bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../search/xsl/search.xsl"/>
  <xsl:import href="../../../__ccm__/static/cms/admin/search/search.xsl"/>
  <xsl:import href="cms-admin.xsl"/>

  <!-- Task Listing for one User in Workspace -->
  <xsl:template match="cms:tasksPanel">

    <xsl:choose>
      <xsl:when test="count(cms:tasksPanelTask)=0">
        <em>You have no tasks on your task list.</em>
      </xsl:when>
      <xsl:otherwise>
        <table class="data">
          <thead>
            <tr>
              <th><xsl:apply-templates select="bebop:link[@class='title']"/></th>
              <th><xsl:apply-templates select="bebop:link[@class='action']"/></th>
              <th><xsl:apply-templates select="bebop:link[@class='date']"/></th>
              <th><xsl:apply-templates select="bebop:link[@class='status']"/></th>
              <th><xsl:apply-templates select="bebop:link[@class='user']"/></th>
              <th><xsl:apply-templates select="bebop:link[@class='workflow']"/></th>
            </tr>
          </thead>
          <tbody>
            <xsl:for-each select="cms:tasksPanelTask">
              <tr>
                <xsl:attribute name="class">
                  <xsl:choose>
                    <xsl:when test="position() mod 2">odd</xsl:when>
                    <xsl:otherwise>even</xsl:otherwise>
                  </xsl:choose>
                </xsl:attribute>
                <td>
                  <a href="{$dispatcher-prefix}{@sectionPath}/admin/item.jsp?item_id={@itemID}">
                    <xsl:value-of select="@pageTitle"/>
                  </a>
                </td>
                <td>
                  <a title="{@taskDescription}" href="{@actionURL}">
                    <xsl:value-of select="@taskLabel"/>
                  </a>
                </td>
                <td>
                  <xsl:value-of select="@dueDate"/>
                </td>
                <td>
                  <xsl:choose>
                    <xsl:when test="@status=1">
                      Locked by you
                    </xsl:when>
                    <xsl:when test="@status=2">
                      Not locked
                    </xsl:when>
                    <xsl:when test="@status=3">
                      Locked by someone else
                    </xsl:when>
                  </xsl:choose>
                </td>
                <td>
                  <xsl:choose>
                    <xsl:when test="@assignee">
                      <xsl:value-of select="@assignee"/>
                    </xsl:when>
                   <xsl:otherwise>--</xsl:otherwise>
                  </xsl:choose>
                </td>
                <td><xsl:value-of select="@processLabel"/></td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template> 
</xsl:stylesheet>
