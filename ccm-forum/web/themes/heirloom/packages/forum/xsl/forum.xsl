<xsl:stylesheet 
  xmlns:forum="http://www.arsdigita.com/forum/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0">

  <!-- IMPORT DEFINITIONS ccm-forum installed into the main CCM webapp  -->
  <xsl:import href="../../bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../ui/xsl/ui.xsl"/>

  <xsl:import href="recent-postings-portlet.xsl"/>

  <xsl:param name="internal-theme"/>

  <xsl:template name="bebop:pageCSS">
    <xsl:call-template name="bebop:pageCSSMain"/>
    <xsl:call-template name="forum:pageCSSMain"/>
  </xsl:template>
  
  <xsl:template name="forum:pageCSSMain">
    <link href="{$internal-theme}/css/forum.css" rel="stylesheet" type="text/css"/>
  </xsl:template>

  <xsl:template match="forum:topicSelector">
    <xsl:if test="count(forum:topic) > 0">
      <form action="{@baseURL}" method="get">
        <table>
          <tr>
            <td>Filter by topic:</td>
            <td>
              <select name="{@param}">
                <option value="{@anyTopicID}">
                  <xsl:if test="@anyTopicID = @currentTopicID">
                    <xsl:attribute name="selected">
                      <xsl:text>selected</xsl:text>
                    </xsl:attribute>
                  </xsl:if>
                  <xsl:text>All topics</xsl:text>
                </option>
                <xsl:for-each select="forum:topic">
                  <option value="{id}">
                    <xsl:if test="id = ../@currentTopicID">
                      <xsl:attribute name="selected">
                        <xsl:text>selected</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="name"/>
                  </option>
                </xsl:for-each>
                <option value="{@noTopicID}">
                  <xsl:if test="@noTopicID = @currentTopicID">
                    <xsl:attribute name="selected">
                      <xsl:text>selected</xsl:text>
                    </xsl:attribute>
                  </xsl:if>
                  <xsl:text>No topic</xsl:text>
                </option>
              </select>
            </td>
            <td>
              <input type="submit" value="Apply"/>
            </td>
          </tr>
        </table>
      </form>
    </xsl:if>
  </xsl:template>

  <xsl:template match="forum:threadList">
    <table class="data">
      <thead>
        <tr>
          <th width="50%">Subject</th>
          <th>Replies</th>
          <th>Topic</th>
          <th>Last Post</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <xsl:if test="count(forum:thread) = 0">
          <td colspan="5"><em>No messages have been posted yet</em></td>
        </xsl:if>
        <xsl:for-each select="forum:thread">
          <xsl:variable name="class">
            <xsl:choose>
              <xsl:when test="position() mod 2">
                <xsl:text>odd</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>even</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <tr class="{$class}">
            <td><a href="{@url}"><xsl:value-of select="root/subject"/></a></td>
            <td><xsl:value-of select="numReplies"/></td>
            <xsl:choose>
              <xsl:when test="root/categories">
                <td><xsl:value-of select="root/categories/name"/></td>
              </xsl:when>
              <xsl:otherwise>
                <td><em><xsl:text>None</xsl:text></em></td>
              </xsl:otherwise>
            </xsl:choose>
            <td><xsl:value-of select="lastUpdate"/></td>
            <td><xsl:value-of select="root/status"/></td>
          </tr>
        </xsl:for-each>
      </tbody>
      <tfoot>
        <tr>
          <th colspan="5">
            <xsl:apply-templates select="forum:paginator" mode="page-links"/>
          </th>
        </tr>
      </tfoot>
    </table>
  </xsl:template>

  <xsl:template match="forum:threadAlertList">
    <table class="data">
      <thead>
        <tr>
          <th width="50%">Subject</th>
          <th>Replies</th>
          <th>Author</th>
          <th>Last Post</th>
          <th>Status</th>
          <th>Delete</th>
        </tr>
      </thead>
      <tbody>
        <xsl:if test="count(forum:threadAlert) = 0">
          <td colspan="6"><em>You are not subscribed to any threads</em></td>
        </xsl:if>
        <xsl:for-each select="forum:threadAlert">
          <xsl:variable name="class">
            <xsl:choose>
              <xsl:when test="position() mod 2">
                <xsl:text>odd</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>even</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <tr class="{$class}">
            <td><a href="{@url}"><xsl:value-of select="thread/root/subject"/></a></td>
            <td><xsl:value-of select="thread/numReplies"/></td>
            <td><xsl:value-of select="thread/author/displayName"/></td>
            <td><xsl:value-of select="thread/lastUpdate"/></td>
            <td><xsl:value-of select="thread/root/status"/></td>
            <td><input type="checkbox" name="{@param}" value="{id}"/></td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>
  
  <xsl:template match="forum:threadDisplay">
    <xsl:for-each select="forum:message">
      <xsl:variable name="indent">
        <xsl:choose>
          <xsl:when test="count(sortKey) = 0">
            <xsl:value-of select="'0'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="string-length(sortKey) div 3"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <table class="data" style="margin-left: {$indent}em;">
        <thead>
          <tr>
            <th width="100%" class="{status}">
              <xsl:if test="@approveURL">
                <nobr>
                  <a href="{@approveURL}">
                      <img border="0" src="{$internal-theme}/images/action-generic.png"/>
                  </a>
                  <xsl:text>&#160;</xsl:text>
                  <a href="{@approveURL}">Approve</a>
                  <xsl:text>&#160;</xsl:text>
                </nobr>
              </xsl:if>
              <xsl:if test="@rejectURL">
                <nobr>
                  <a href="{@rejectURL}"><img border="0" src="{$internal-theme}/images/action-generic.png"/></a>
                  <xsl:text>&#160;</xsl:text>
                  <a href="{@rejectURL}">Reject</a>
                  <xsl:text>&#160;</xsl:text>
                </nobr>
              </xsl:if>
              <xsl:if test="status != 'approved'">
                <em><strong><xsl:value-of select="status"/></strong></em>
                <xsl:text>&#160;</xsl:text>
              </xsl:if>
              <xsl:value-of select="subject"/>
            </th>
            <xsl:if test="@editURL">
              <th>
                <nobr>
                  <a href="{@editURL}"><img border="0" src="/{$internal-theme}s/images/action-generic.png"/></a>
                  <xsl:text>&#160;</xsl:text>
                  <a href="{@editURL}">Edit</a>
                </nobr>
              </th>
            </xsl:if>
            <xsl:if test="@replyURL">
              <th>
                <nobr>
                  <a href="{@replyURL}"><img border="0" src="/{$internal-theme}/images/action-generic.png"/></a>
                  <xsl:text>&#160;</xsl:text>
                  <a href="{@replyURL}">Reply</a>
                </nobr>
              </th>
            </xsl:if>
            <xsl:if test="@deleteURL">
              <th>
                <nobr>
                  <a href="{@deleteURL}"><img border="0" src="/{$internal-theme}/images/action-generic.png"/></a>
                  <xsl:text>&#160;</xsl:text>
                  <a href="{@deleteURL}">Delete</a>
                </nobr>
              </th>
            </xsl:if>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td colspan="4"><xsl:value-of select="body" disable-output-escaping="yes"/></td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <th colspan="4">
              <xsl:text>by </xsl:text>
              <a href="mailto:{sender/primaryEmail}">
                <xsl:value-of select="sender/displayName"/>
              </a>
              <xsl:text> on </xsl:text>
              <xsl:value-of select="sent"/>
            </th>
          </tr>
        </tfoot>
      </table>
      <xsl:text>&#160;</xsl:text>
    </xsl:for-each>
    <table class="data">
      <tfoot>
        <tr>
          <th colspan="5">
            <xsl:apply-templates select="forum:paginator" mode="page-links"/>
          </th>
        </tr>
      </tfoot>
    </table>
  </xsl:template>
  
  <xsl:template match="forum:paginator" mode="page-links">
    <xsl:if test="@pageCount > 1">
      <div>
        <xsl:if test="@pageNumber > 1">
          <a>
            <xsl:attribute name="href">
              <xsl:call-template name="make-url">
                <xsl:with-param name="base-url" select="@baseURL"/>
                <xsl:with-param name="name" select="@param"/>
                <xsl:with-param name="value" select="@pageNumber - 1"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:text>&lt;&lt;&lt; Previous</xsl:text>
          </a>
          <xsl:text>&#160;</xsl:text>
          <xsl:text>&#160;</xsl:text>
        </xsl:if>
        <xsl:text>Page </xsl:text>
        <xsl:value-of select="@pageNumber"/>
        <xsl:text> of </xsl:text>
        <xsl:value-of select="@pageCount"/>
        <xsl:if test="@pageNumber &lt; @pageCount">
          <xsl:text>&#160;</xsl:text>
          <xsl:text>&#160;</xsl:text>
          <a>
            <xsl:attribute name="href">
              <xsl:call-template name="make-url">
                <xsl:with-param name="base-url" select="@baseURL"/>
                <xsl:with-param name="name" select="@param"/>
                <xsl:with-param name="value" select="@pageNumber + 1"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:text>Next &gt;&gt;&gt;</xsl:text>
          </a>
        </xsl:if>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="forum:topicList">
    <table class="data">
      <thead>
        <tr>
          <th width="50%">Topic</th>
          <th>Threads</th>
          <th>Last Post</th>
        </tr>
      </thead>
      <tbody>
        <xsl:for-each select="forum:topicSummary">
          <xsl:variable name="class">
            <xsl:choose>
              <xsl:when test="position() mod 2">
                <xsl:text>odd</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>even</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <tr class="{$class}">
            <td>
              <xsl:choose>
                <xsl:when test="numThreads > 0">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:call-template name="make-url">
                        <xsl:with-param name="base-url" select="../@baseURL"/>
                        <xsl:with-param name="name" select="../@param"/>
                        <xsl:with-param name="value" select="id"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:value-of select="name"/>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="name"/>                  
                </xsl:otherwise>
              </xsl:choose>
            </td>
            <td><xsl:value-of select="numThreads"/></td>
            <td>
              <xsl:choose>
                <xsl:when test="numThreads > 0">
                  <xsl:value-of select="latestPost"/>
                </xsl:when>
                <xsl:otherwise>
                  <em><xsl:text>n/a</xsl:text></em>
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
        </xsl:for-each>
        <xsl:for-each select="forum:noTopicSummary">
          <xsl:variable name="class">
            <xsl:choose>
              <xsl:when test="count(../forumTopicSummary) mod 2">
                <xsl:text>odd</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>even</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <tr class="{$class}">
            <td>
              <xsl:choose>
                <xsl:when test="numThreads > 0">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:call-template name="make-url">
                        <xsl:with-param name="base-url" select="../@baseURL"/>
                        <xsl:with-param name="name" select="../@param"/>
                        <xsl:with-param name="value" select="id"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <em><xsl:text>None</xsl:text></em>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <em><xsl:text>None</xsl:text></em>
                </xsl:otherwise>
              </xsl:choose>
            </td>
            <td><xsl:value-of select="numThreads"/></td>
            <td>
              <xsl:choose>
                <xsl:when test="numThreads > 0">
                  <xsl:value-of select="latestPost"/>
                </xsl:when>
                <xsl:otherwise>
                  <em><xsl:text>n/a</xsl:text></em>
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>    
  </xsl:template>

  <xsl:template match="bebop:form[@name='newPostForm']">
    <xsl:call-template name="ForumPostForm">
      <xsl:with-param name="title">
        <xsl:choose>
          <xsl:when test="forum:postForm">
            <xsl:text>Post new message</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>Confirm new message</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="bebop:form[@name='editPostForm']">
    <xsl:call-template name="ForumPostForm">
      <xsl:with-param name="title">
        <xsl:choose>
          <xsl:when test="forum:postForm">
            <xsl:text>Edit message</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>Confirm changes</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="bebop:form[@name='replyPostForm']">
    <xsl:call-template name="ForumPostForm">
      <xsl:with-param name="title">
        <xsl:choose>
          <xsl:when test="forum:postForm">
            <xsl:text>Post reply</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>Confirm reply</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="ForumPostForm">
    <xsl:param name="title" select="'Post message'"/>

    <form action="{@action}" name="{@name}">
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
      <table class="data">
        <thead>
          <th colspan="2"><xsl:value-of select="$title"/></th>
        </thead>
        <tbody>
          <xsl:apply-templates select="forum:postForm"/>
          <xsl:apply-templates select="forum:postConfirm"/>
        </tbody>
        <tfoot>
          <tr>
            <th colspan="2"><xsl:apply-templates select="bebop:boxPanel"/></th>
          </tr>
        </tfoot>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="forum:postForm">
    <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
    <tr class="odd">
      <td>Subject:</td>
      <td width="100%"><xsl:apply-templates select="bebop:formWidget[@name='subject']"/></td>
    </tr>
    <tr class="even">
      <td>Message:</td>
      <td width="100%"><xsl:apply-templates select="bebop:textarea[@name='message']"/></td>
    </tr>
    <tr class="odd">
      <td>Format:</td>
      <td width="100%"><xsl:apply-templates 
                            select="bebop:select[@name='bodyType']"/>
      </td>
    </tr>
    <xsl:choose>
      <xsl:when test="bebop:select[@name='postTopic']">
        <tr class="even">
          <td>Topic:</td>
          <td width="100%"><xsl:apply-templates select="bebop:select[@name='postTopic']"/></td>
        </tr>
      </xsl:when>
      <xsl:when test="forum:message">
        <tr class="even">
          <td>In reply to:</td>
          <td width="100%"><xsl:apply-templates select="forum:message"/></td>
        </tr>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="forum:message">
    <table class="data">
      <thead>
        <tr>
          <th width="100%">
            <xsl:value-of select="subject"/>
          </th>
        </tr>
      </thead>
      <tbody>
        <td><xsl:value-of select="body" disable-output-escaping="yes"/></td>
      </tbody>
      <tfoot>
        <tr>
          <th>
            <xsl:text>by </xsl:text>
            <a href="mailto:{sender/primaryEmail}">
              <xsl:value-of select="sender/displayName"/>
            </a>
            <xsl:text> on </xsl:text>
            <xsl:value-of select="sent"/>
          </th>
        </tr>
      </tfoot>
    </table>    
  </xsl:template>
  
  <xsl:template match="forum:postConfirm">
    <tr class="odd">
      <td>Subject:</td>
      <td width="100%"><xsl:value-of select="subject"/></td>
    </tr>
    <tr class="even">
      <td>Message:</td>
      <td width="100%">
        <div>
          <xsl:value-of select="body" disable-output-escaping="yes"/>
        </div>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="forum:forumAlerts">
    <h3>Forum alerts</h3>
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="forum:threadAlerts">
    <h3>Thread alerts</h3>
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="forum:forum">
    <div class="tabbed-pane">
      <table class="tab-set">
        <tr>
          <xsl:apply-templates select="forum:forumMode"/>
        </tr>
      </table>
      <table class="rule">
        <tr><td></td></tr>
      </table>
    </div>
    <div><xsl:text>&#160;</xsl:text></div>
    <xsl:apply-templates select="*[not(name() = 'forum:forumMode')]"/>
  </xsl:template>
  
  <xsl:template match="forum:forumMode">
    <xsl:variable name="title">
      <xsl:choose>
        <xsl:when test="@mode = 'threads'">
          <xsl:value-of select="'Threads'"/>
        </xsl:when>
        <xsl:when test="@mode = 'topics'">
          <xsl:value-of select="'Topics'"/>
        </xsl:when>
        <xsl:when test="@mode = 'alerts'">
          <xsl:value-of select="'Alerts'"/>
        </xsl:when>
        <xsl:when test="@mode = 'moderation'">
          <xsl:value-of select="'Moderation'"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="@selected = 1">
        <td class="current-tab-label"><xsl:value-of select="$title"/></td>
        <td class="current-tab-end"/>
      </xsl:when>
      <xsl:otherwise>
        <td class="tab-label"><a href="{@url}"><xsl:value-of select="$title"/></a></td>
        <td class="tab-end"/>
      </xsl:otherwise>
    </xsl:choose>
    <td class="tab-spacer"/>
  </xsl:template>

  <xsl:template match="forum:forumOptions">
    <div>
      <xsl:apply-templates select="*"/>
    </div>
    <div><xsl:text>&#160;</xsl:text></div>
  </xsl:template>

  <xsl:template match="forum:topicOptions">
    <div>
      <xsl:apply-templates select="*"/>
    </div>
    <div><xsl:text>&#160;</xsl:text></div>
  </xsl:template>

  <xsl:template name="make-url">
    <xsl:param name="base-url"/>
    <xsl:param name="name"/>
    <xsl:param name="value"/>
    
    <xsl:choose>
      <xsl:when test="contains($base-url, '?')">
        <xsl:value-of select="concat($base-url, '&amp;', $name, '=', $value)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat($base-url, '?', $name, '=', $value)"/>        
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
 
</xsl:stylesheet>
