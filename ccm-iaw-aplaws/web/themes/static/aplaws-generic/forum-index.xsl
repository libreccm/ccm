<xsl:stylesheet xmlns:forum="http://www.arsdigita.com/forum/1.0"
				xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:nav="http://ccm.redhat.com/london/navigation" 
				xmlns:search="http://rhea.redhat.com/search/1.0" 
				xmlns:portal="http://www.uk.arsdigita.com/portal/1.0" 				
				xmlns:cms="http://www.arsdigita.com/cms/1.0" 	
				exclude-result-prefixes="xsl bebop nav search portal forum cms" 
				version="1.0">

        <!-- IMPORT DEFINITIONS ccm-forum  installed as separate web application
	<xsl:import href="../../../../ROOT/packages/bebop/xsl/bebop.xsl" />
	<xsl:import href="../../../../ROOT/packages/ui/xsl/ui.xsl" />
	<xsl:import href="../../../../ROOT/packages/bebop/xsl/bebop.xsl" />
	-->
 	<!-- IMPORT DEFINITIONS ccm-forum installed into the main CCM webapp
	-->
	<xsl:import href="../../../packages/bebop/xsl/bebop.xsl" />
	<xsl:import href="../../../packages/ui/xsl/ui.xsl" />
	<xsl:import href="../../../packages/bebop/xsl/bebop.xsl" />

	<xsl:import href="lib/header.xsl" />
	<xsl:import href="lib/lib.xsl" />
	<xsl:import href="lib/leftNav.xsl" />
        <!-- IMPORT DEFINITIONS ccm-forum  installed as separate web application
	<xsl:import href="../../../../ROOT/packages/bebop/xsl/dcp.xsl"/>
	-->
 	<!-- IMPORT DEFINITIONS ccm-forum installed into the main CCM webapp
	-->
	<xsl:import href="../../../packages/bebop/xsl/dcp.xsl"/>
  


	<xsl:param name="theme-prefix" />
	<xsl:param name="context-prefix" />
	<xsl:param name="dispatcher-prefix" />

	<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" 
		    doctype-system="http://www.w3.org/TR/html4/loose.dtd" indent="yes" />

<!-- ********************* MAIN PAGE LAYOUT TEMPLATES ********************* -->

	<xsl:template match="bebop:page[@class='simplePage']">
		<html lang="en">
			<head>
				<title>
					Forum
				</title>
				<link rel="stylesheet" href="{$theme-prefix}/forum-index.css" type="text/css" media="screen" />
				<link rel="stylesheet" href="{$theme-prefix}/css/print.css" type="text/css" media="print" />
				<script type="text/javascript" src="{$theme-prefix}/css/date.js"></script>
				<xsl:call-template name="bebop:dcpJavascript"/>
			</head>

			<body>
 				<a name="Ptop"></a>
				<a class="navHide" href="#startcontent" title="Go directly to main content" accesskey="S">Skip over navigation</a>
				<span class="hide">|</span>
				<xsl:call-template name="header" />
				<xsl:call-template name="mainContent" />

				<xsl:call-template name="footer" />

			</body>
		</html>
	</xsl:template>


	<xsl:template name="mainContent">

		<table id="mainLayout" border="0" cellspacing="0" cellpadding="0" summary="navigation and content">
			<tr>
				<td>
					<img class="lSpacer" src="{$theme-prefix}/images/spacer.gif" alt="" />
				</td>
				<td>
					<img class="rSpacer" src="{$theme-prefix}/images/spacer.gif" alt="" />
				</td>
			</tr>
			<tr>
				<td id="leftNav" align="left" valign="top">
					<!--LHS NAVIGATION -->

					<xsl:call-template name="leftNav" />

				</td>

				<td valign="top">
					<span class="hide">|</span>

					<!--CONTENT -->
					<a id="startcontent" class="navHide" title="Start of content"></a>
					<span class="hide">|</span>
					<!--<xsl:call-template name="pageContent" />-->
					<div class="pageTitle">
        				<h1><xsl:value-of select="/bebop:page/forum:name"/></h1>
        			</div>        			
					<div class="forum">
						<xsl:apply-templates select="//forum:forum | //forum:threadDisplay | /bebop:page/bebop:form " />
					</div>
				</td>
			</tr>
		</table>
	</xsl:template>
	
<!-- ******************** POST TEMPLATES ******************************* -->
	
	 <xsl:template name="displayPost">	
		<xsl:param name="preview" />	
		
		<xsl:variable name="class">
			<xsl:choose>
				<xsl:when test="position() mod 2">
					<xsl:text>postOdd</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>postEven</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>			
		
		<xsl:variable name="indent">
			<xsl:choose>					
				<xsl:when test="$preview='true'">
					<xsl:value-of select="'0'" />				
				</xsl:when>
				<xsl:otherwise>
						<xsl:choose>					
							<xsl:when test="count(sortKey) = 0">					
								<xsl:value-of select="'0'" />																			
							</xsl:when>																
							<xsl:otherwise>
								<xsl:value-of select="string-length(sortKey) div 3" />
							</xsl:otherwise>
						</xsl:choose>			
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>					
				
		<xsl:if test="count(sortKey) = 0 and $preview='false'">
			<div id="topic" style="margin-left: {$indent}em;">Thread: <xsl:value-of select="subject" /></div>	
			
			<xsl:apply-templates select="../../forum:threadOptions" />
		</xsl:if>									
				
		<div class="post" style="margin-left: {$indent}em;">			
			<fieldset class="{$class}">									
				<div class="clearGroup">
					<div class="header">
	 					<xsl:value-of select="subject" />
						<xsl:if test="status != 'approved' and $preview = 'false'">								
							(
							<xsl:value-of select="status" />
							)								
						</xsl:if>	
					</div>							
				 
				 	<xsl:if test="$preview = 'false'">
						<div class="actions">
							<xsl:if test="@approveURL">
								<a href="{@approveURL}">
									<img alt="Approve" border="0" src="{$theme-prefix}/images/forum/reward-16x16.gif" />
								</a>
								<!--<xsl:text>&#160;</xsl:text>
								<a href="{@approveURL}">Approve</a>-->
								<xsl:text>&#160;</xsl:text>
	
							</xsl:if>
							<xsl:if test="@rejectURL">
								<a href="{@rejectURL}" >
									<img alt="Reject" border="0" src="{$theme-prefix}/images/forum/delete-16x16.gif" />
								</a>
								<!--<xsl:text>&#160;</xsl:text>
								<a href="{@rejectURL}">Reject</a>-->
								<xsl:text>&#160;</xsl:text>
							</xsl:if>
							<xsl:if test="@editURL">
								<a href="{@editURL}">
									<img alt="Edit" border="0" src="{$theme-prefix}/images/forum/edit-16x16.gif" />
								</a>
								<!--<xsl:text>&#160;</xsl:text>
								<a href="{@editURL}">Edit</a>-->
								<xsl:text>&#160;</xsl:text>
							</xsl:if>
							<xsl:if test="@deleteURL">
								<a href="{@deleteURL}">
									<img alt="Delete" border="0" src="{$theme-prefix}/images/forum/archived-16x16.gif" />
								</a>
								<!--<xsl:text>&#160;</xsl:text>
								<a href="{@deleteURL}">Delete</a>-->
								<xsl:text>&#160;</xsl:text>
							</xsl:if>					
							<xsl:if test="@replyURL">
								<xsl:text>&#160;</xsl:text>
								<xsl:text>&#160;</xsl:text>
								<xsl:text>&#160;</xsl:text>
								<a href="{@replyURL}">
									<img alt="Reply" border="0" src="{$theme-prefix}/images/forum/reply-16x16.gif" />
								</a>
								<xsl:text>&#160;</xsl:text>
								<a href="{@replyURL}">Reply</a>
								<xsl:text>&#160;</xsl:text>
							</xsl:if>						
						</div>	
					</xsl:if>
				</div>				
				
				<div class="postDetails">
					<xsl:text>Posted:&#160;</xsl:text>
					<xsl:value-of select="sent" />
					<xsl:text>&#160;by&#160;</xsl:text>
					<a href="mailto:{sender/primaryEmail}">
						<xsl:value-of select="sender/displayName" />
					</a>																		
				</div>	
				
				<div class="clearGroup">
					<xsl:for-each select="files">
						<xsl:sort select="fileOrder"/>
						<div class="fileLink">	
							 <xsl:variable name="encodedFileName">
				            	<xsl:call-template name="url-encode">
			    		     		 <xsl:with-param name="str" select="./name"/>
				        		</xsl:call-template>
				            </xsl:variable>						
							<a href="{$dispatcher-prefix}/cms-service/stream/asset/?asset_id={./id}&amp;file=/{$encodedFileName}">
							<!-- show file in new window for preview to avoid losing the form -->
							<xsl:if test="$preview='true'">
								<xsl:attribute name="target">_blank</xsl:attribute>
							</xsl:if>
							
							<xsl:value-of select="name"/></a>
						</div>                    
						<div class="fileDescription">
						
						</div>
					</xsl:for-each>
				</div>
				
				<div class="bar">
					<hr/>
				</div>
															
				<div class="message">												
					<xsl:value-of select="body" disable-output-escaping="yes" />						
				</div>									
				
				<div id="images">					
					<xsl:for-each select="images">						
						<xsl:sort select="link/imageOrder"/>
						<div class="clearGroup">															 					          				
							<img src="{$dispatcher-prefix}/cms-service/stream/image/?image_id={./id}" alt="{./description}"/>
						</div>							
					</xsl:for-each>	
				</div>				
			</fieldset>
		</div>				
		<table class="data">
			<tfoot>
				<tr>
					<th colspan="5">						
						<xsl:apply-templates select="forum:paginator" mode="page-links" />
					</th>
				</tr>
			</tfoot>
		</table>
	</xsl:template>

	<xsl:template match="forum:paginator" mode="page-links">
		<xsl:if test="@pageCount > 1">			
			<div class="clearGroup">
				<br/>
				<xsl:if test="@pageNumber > 1">					
					<a>
						<xsl:attribute name="href">
							<xsl:call-template name="make-url">
								<xsl:with-param name="base-url" select="@baseURL" />
								<xsl:with-param name="name" select="@param" />
								<xsl:with-param name="value" select="@pageNumber - 1" />
							</xsl:call-template>
						</xsl:attribute>
						<xsl:text>&lt;&lt;&lt; Previous</xsl:text>
					</a>
					<xsl:text>&#160;</xsl:text>
					<xsl:text>&#160;</xsl:text>
				</xsl:if>
				<xsl:text>Page&#160;</xsl:text>
				<xsl:value-of select="@pageNumber" /><xsl:text>&#160;</xsl:text>
				<xsl:text>of&#160;</xsl:text>
				<xsl:value-of select="@pageCount" /><xsl:text>&#160;</xsl:text>
				<xsl:if test="@pageNumber &lt; @pageCount">
					<xsl:text>&#160;</xsl:text>
					<xsl:text>&#160;</xsl:text>
					<a>
						<xsl:attribute name="href">
							<xsl:call-template name="make-url">
								<xsl:with-param name="base-url" select="@baseURL" />
								<xsl:with-param name="name" select="@param" />
								<xsl:with-param name="value" select="@pageNumber + 1" />
							</xsl:call-template>
						</xsl:attribute>
						<xsl:text>Next &gt;&gt;&gt;</xsl:text>
					</a>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>
    
	<xsl:template match="forum:topicSelector">
		<xsl:if test="count(forum:topic) > 0">
			<div class="content">
				<form action="{@baseURL}" method="get">
	
					<div id="selectCategory">
						<div id="selectLabel">Filter by topic:</div>
						<select name="{@param}" class="forumSelect">
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
									<xsl:value-of select="name" />
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
						<div class="forumButton">
							<input type="submit" value="Apply" />
						</div>
					</div>	
				</form>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="forum:threadList">
		<div class="content">
			<div id="threadHead">
				<div class="topic">Thread</div>
				<div class="author">Author</div>
				<div class="replies">Replies</div>
				<div class="lastPost">Last Post</div>
			</div>
			<xsl:if test="count(forum:thread) = 0">
				<div id="noMessages">No messages have been posted yet</div>
			</xsl:if>
			<xsl:for-each select="forum:thread">

				<div>
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


					<div class="subject">
						<a href="{@url}">
							<xsl:value-of select="root/subject" />
						</a>
					</div>
					<div class="author">
						<xsl:value-of select="author/displayName" />
					</div>
					<div class="replies">
						<xsl:value-of select="numReplies" />
					</div>
					<div class="lastPost">
						<xsl:value-of select="lastUpdate" />
					</div>

				</div>
			</xsl:for-each>
		</div>

		<xsl:apply-templates select="forum:paginator" mode="page-links" />

	</xsl:template>

	<xsl:template match="forum:threadAlertList">
		<div class="clearGroup">
			<div id="threadHead">
				<div class="threadSubject">Subject</div>
				<div class="replies">Replies</div>
				<div class="author">Author</div>
				<div class="lastPost">Last Post</div>
				<div class="status">Status</div>
				<div class="delete">Delete</div>
			</div>
		</div>	
		<xsl:if test="count(forum:threadAlert) = 0">
			<div>
				<em>You are not subscribed to any threads</em>
			</div>
		</xsl:if>
		<xsl:for-each select="forum:threadAlert">
			<xsl:variable name="class">
				<xsl:choose>
					<xsl:when test="position() mod 2">
						<xsl:text>postOdd</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>postEven</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<div class="{$class}">				
				<div class="threadSubject">
					<a href="{@url}"><xsl:value-of select="thread/root/subject" /></a>
				</div>						
				<div class="replies">
					<xsl:value-of select="thread/numReplies" />
				</div>
				<div class="author">
					<xsl:value-of select="thread/author/displayName" />
				</div>
				<div class="lastPost">
					<xsl:value-of select="thread/lastUpdate" />
				</div>
				<div class="status">
					<xsl:value-of select="thread/root/status" />
				</div>
				<div class="delete">
					<input type="checkbox" name="{@param}" value="{id}" />
				</div>
			</div>			
		</xsl:for-each>		
	</xsl:template>

	<xsl:template match="forum:threadDisplay">	
		<div class="content">			
			<xsl:for-each select="forum:message">		
				<xsl:call-template name="displayPost">
					<xsl:with-param name="preview">
						<xsl:text>false</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>	
		</div>
	</xsl:template>
	


	<xsl:template match="forum:topicList">
		<div class="content">			
			<div id="threadHead">	
				<div class="threadSubject">Topic</div>
				<div class="threads">Threads</div>
				<div class="lastPost">Last Post</div>		
			</div>
					
			<xsl:for-each select="forum:topicSummary">
				<xsl:variable name="class">
					<xsl:choose>
						<xsl:when test="position() mod 2">
							<xsl:text>postOdd</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>postEven</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<div class="{$class}">	
					<div class="threadSubject">				
						<xsl:choose>
							<xsl:when test="numThreads > 0">														
								<a>
									<xsl:attribute name="href">
										<xsl:call-template name="make-url">
											<xsl:with-param name="base-url" select="../@baseURL" />
											<xsl:with-param name="name" select="../@param" />
											<xsl:with-param name="value" select="id" />
										</xsl:call-template>
									</xsl:attribute>
									<xsl:value-of select="name" />
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="name" />
							</xsl:otherwise>
						</xsl:choose>
					</div>
					<div class="threads">
						<xsl:value-of select="numThreads" />
					</div>
					<div class="lastPost">
						<xsl:choose>
							<xsl:when test="numThreads > 0">
								<xsl:value-of select="latestPost" />
							</xsl:when>
							<xsl:otherwise>
								<em><xsl:text>n/a</xsl:text></em>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</div>
			</xsl:for-each>
			<xsl:for-each select="forum:noTopicSummary">
				<xsl:variable name="class">				
					<xsl:choose>
						<xsl:when test="count(../forumTopicSummary) mod 2">
							<xsl:text>postOdd</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>postEven</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<div class="{$class}">
					<div class="threadSubject">	
						<xsl:choose>
							<xsl:when test="numThreads > 0">
								<a>
									<xsl:attribute name="href">
										<xsl:call-template name="make-url">
											<xsl:with-param name="base-url" select="../@baseURL" />
											<xsl:with-param name="name" select="../@param" />
											<xsl:with-param name="value" select="id" />
										</xsl:call-template>
									</xsl:attribute>
									<em><xsl:text>None</xsl:text></em>
								</a>
							</xsl:when>
							<xsl:otherwise>
								<em><xsl:text>None</xsl:text></em>
							</xsl:otherwise>
						</xsl:choose>
					</div>				
					<div class="threads">
						<xsl:value-of select="numThreads" />
					</div>
					<div class="lastPost">
						<xsl:choose>
							<xsl:when test="numThreads > 0">
								<xsl:value-of select="latestPost" />
							</xsl:when>
							<xsl:otherwise>
								<em><xsl:text>n/a</xsl:text></em>
							</xsl:otherwise>
						</xsl:choose>
					</div>			
				</div>
			</xsl:for-each>
		</div>	
	</xsl:template>	

	<xsl:template match="forum:forumAlerts">
		<div class="content">
			<h5>Forum Alerts</h5>
			<br/>	
			<xsl:apply-templates select="*" />			
		</div>
	</xsl:template>
	
<!--	<xsl:template match="bebop:form[@name='instantAlerts' or @name='dailyAlerts']//bebop:panelRow">				
		<xsl:for-each select="bebop:cell[not(bebop:formWidget)]">
			<xsl:apply-templates />
		</xsl:for-each>	
		<div class="forumButton">			
			<xsl:apply-templates select="bebop:cell/bebop:formWidget[@name='Save' or @name='forum.ui.delete']" />
		</div>
	</xsl:template>-->

	<xsl:template match="forum:threadAlerts">
		<div class="content">
			<h5>Thread Alerts</h5>
			<br/>		
			<xsl:apply-templates select="*" />
		</div>
	</xsl:template>

	<xsl:template match="forum:forum">	
			<xsl:if test="not(descendant::bebop:form/@name = 'newPostForm')">	
				<div class="tabbed-pane">
					<table class="tab-set">
						<tr>				
							<xsl:apply-templates select="forum:forumMode" />			
						</tr>
					</table>
					<table class="rule">
						<tr>
							<td></td>
						</tr>
					</table>
				</div>
				<div>
					<xsl:text>&#160;</xsl:text>
				</div>
			</xsl:if>	
			<div class="clearGroup">
				<div class="content">
					<xsl:if test="forum:forumMode[@mode = 'threads' and @selected='1'] and not(bebop:form/@name='newPostForm')">
						<xsl:call-template name="br-replace">
							<xsl:with-param name="text" select="/bebop:page/forum:introduction"/>
							
						</xsl:call-template> 
					</xsl:if>
				</div>
			</div>		
			<xsl:apply-templates select="*[not(name() = 'forum:forumMode')]" />						
	</xsl:template>

	<xsl:template match="forum:forumMode">
		<xsl:variable name="title">
			<xsl:choose>
				<xsl:when test="@mode = 'threads'">
					<xsl:value-of select="'Threads'" />
				</xsl:when>
				<xsl:when test="@mode = 'topics'">
					<xsl:value-of select="'Topics'" />
				</xsl:when>
				<xsl:when test="@mode = 'alerts'">
					<xsl:value-of select="'Alerts'" />
				</xsl:when>
				<xsl:when test="@mode = 'moderation'">
					<xsl:value-of select="'Moderation'" />
				</xsl:when>
				<xsl:when test="@mode = 'permissions'">
					<xsl:value-of select="'Permissions'" />
				</xsl:when>
				   <xsl:when test="@mode = 'setup'">
		          <xsl:value-of select="'Setup'"/>
		        </xsl:when>
		        <xsl:when test="@mode = 'categories'">
		          <xsl:value-of select="'Categories'"/>
		        </xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@selected = 1">
				<td class="current-tab-label">
					<xsl:value-of select="$title" />
				</td>
				<td class="current-tab-end" />
			</xsl:when>
			<xsl:otherwise>
				<td class="tab-label">
					<a href="{@url}">
						<xsl:value-of select="$title" />
					</a>
				</td>
				<td class="tab-end" />
			</xsl:otherwise>
		</xsl:choose>
		<td class="tab-spacer" />				
	</xsl:template>

	<xsl:template match="forum:memberList">
		<xsl:comment>forum member list</xsl:comment>
		<div class="content">	
			<fieldset>
				<legend><xsl:value-of select="@group" /></legend>
				<xsl:apply-templates />
				<xsl:apply-templates select="following-sibling::bebop:form[1]" mode="show"/>							
			</fieldset>
			<br/>
		</div>
	</xsl:template>
	

	<xsl:template match="forum:forumOptions">
		<div class="content">
			<xsl:apply-templates select="*" />
		</div>
		<div>
			<xsl:text>&#160;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template match="forum:topicOptions">
		<div class="content">
			<xsl:apply-templates select="*" />
		</div>
		<div>
			<xsl:text>&#160;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template name="make-url">
		<xsl:param name="base-url" />
		<xsl:param name="name" />
		<xsl:param name="value" />

		<xsl:choose>
			<xsl:when test="contains($base-url, '?')">
				<xsl:value-of select="concat($base-url, '&amp;', $name, '=', $value)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($base-url, '?', $name, '=', $value)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	

	
<!-- ********************** FORM TEMPLATES ********************** -->
	
	
	<xsl:template name="ForumPostForm">
		<xsl:param name="title" select="'Post message'" />
		
		<!--<form action="{@action}" name="{@name}" method="get">-->
		<form action="{@action}" name="{@name}" enctype="{@enctype}">
           <xsl:attribute name="method">
              <xsl:choose>
                 <xsl:when test="string-length(@method)=0">post</xsl:when>
                 <xsl:otherwise><xsl:value-of select="@method"/></xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>

			<xsl:apply-templates select="bebop:formWidget[@type='hidden']" />
			<xsl:apply-templates select="bebop:pageState" />
				
			<div id="title">
				<h2 class="pageSubTitle"><xsl:value-of select="$title" /></h2>	
				<!--<xsl:if test="@name = 'newPostForm'">-->
					<br/>
				<!--</xsl:if>-->						
			</div>		
			<div class="content">	
				<xsl:apply-templates select="forum:postForm" />
				<xsl:apply-templates select="forum:postConfirm" />	
				<xsl:apply-templates select="forum:postFormImages" />	
				<xsl:apply-templates select="forum:postFormFiles" />												
				
				<div class="clearGroup">	
					<div class="validationError">
						<xsl:apply-templates select="bebop:formErrors" />												
					</div>
				</div>
									
				<div class="clearGroup">	
					<div class="forumButton">			
						<br/>
						<xsl:apply-templates select="bebop:boxPanel/bebop:cell/bebop:formWidget[@name = 'Cancel']" />			
						<xsl:apply-templates select="bebop:boxPanel/bebop:cell/bebop:formWidget[@name = '&lt;&lt;_Back']" />
						<xsl:apply-templates select="bebop:boxPanel/bebop:cell/bebop:formWidget[@name = 'Next_>>']" />
						<xsl:apply-templates select="bebop:boxPanel/bebop:cell/bebop:formWidget[@name = 'Finish']" />
						<br/>
					</div>
				</div>	
				<br/>			
			</div>		
		</form>	
	</xsl:template>

	<xsl:template match="forum:postForm">
		<xsl:apply-templates select="bebop:formWidget[@type='hidden']" />
        
		<div class="clearGroup">
			<div class="columnOne">Subject:</div>
			<div class="columnTwo">
				<xsl:apply-templates select="bebop:formWidget[@name='subject']" />
				<div class="validationError">
					<xsl:apply-templates select="bebop:formErrors[@id='subject']" />												
				</div>
			</div>							
		</div>				
		<div class="clearGroup">
			<div class="columnOne">Message:</div>
			<div class="columnTwo">
				<xsl:apply-templates select="bebop:*[@name='message']" />
				<div class="validationError">
					<xsl:value-of select="bebop:formErrors[@id='message']/@message" disable-output-escaping="yes"/>																	
				</div>
			</div>
		</div>								
		<!--<div class="clearGroup">
			<div class="columnOne">Format:</div>
			<div class="columnTwo">
				<xsl:apply-templates select="bebop:select[@name='bodyType']" />
			</div>
		</div>-->				
		<xsl:choose>
			<xsl:when test="bebop:select[@name='postTopic']">
				<div class="clearGroup">
					<div class="columnOne">Topic:</div>
					<div class="columnTwo">
						<xsl:apply-templates select="bebop:select[@name='postTopic']" />						
					</div>
				</div>
			</xsl:when>
			<xsl:when test="forum:message">				
				<div id="plainText">
					Original Message:					
				</div>
				<div class="postOdd">			
					<fieldset>							
						<xsl:apply-templates select="forum:message" />														
					</fieldset>
				</div>
			</xsl:when>	
		</xsl:choose>		
	
	</xsl:template>
	
	<xsl:template match="bebop:formErrors">		
		<xsl:value-of select="@message" />
	</xsl:template>

	<xsl:template match="forum:message">
		<div class="clearGroup">
			<div class="columnOneWide">
				<a href="mailto:{sender/primaryEmail}">
					<xsl:value-of select="sender/displayName" />
				</a>							
			</div>
			<div class="columnTwo">
				<xsl:value-of select="subject" />
				<br/>
				<xsl:text>Posted</xsl:text>				
				<xsl:value-of select="sent" />	
				<div class="replyBar">
					<hr/>
				</div>
				<xsl:value-of select="body" disable-output-escaping="yes" />			
			</div>
		</div>
	</xsl:template>

	<xsl:template match="forum:postConfirm">	
		<xsl:call-template name="displayPost">			
			<xsl:with-param name="preview" select="'true'" />
		</xsl:call-template>		
	</xsl:template>
	
	<xsl:template match="forum:postFormImages">
		<xsl:apply-templates select="bebop:formWidget[@type='hidden']" />	
		<xsl:apply-templates select="forum:attachedImages" />
		
			
		<div class="clearGroup">
			<div class="columnOne">Image:</div>
			<div class="columnTwo">
				<xsl:apply-templates select="bebop:formWidget[@name='image']" />				
				<div class="validationError">
					<xsl:apply-templates select="bebop:formErrors[@id='image']" />												
				</div>
			</div>
		</div>								
		<div class="clearGroup">
			<div class="columnOne">Description:</div>
			<div class="columnTwo">
				<xsl:apply-templates select="bebop:textarea" />
				<div class="validationError">
					<xsl:apply-templates select="bebop:formErrors[@id='imageDescription']" />												
				</div>
			</div>
		</div>				
		<div class="submit">
			<div class="forumButton">
				<xsl:apply-templates select="bebop:formWidget[@name='Add_Image']" />										
			</div>						
		</div>							
	</xsl:template>	

	<xsl:template match="forum:attachedImages">	
	
	
		<xsl:choose>
			<xsl:when test="not(forum:image)">
				<div id="plainText">			
					No images attached to this post												
				</div>		
			</xsl:when>
			<xsl:otherwise>
			
				<xsl:for-each select="forum:image">
					<xsl:variable name="class">
						<xsl:choose>
							<xsl:when test="position() mod 2">
								<xsl:text>postOdd</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>postEven</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>		
					<div class="clearGroup">
						<fieldset class="{$class}">									
							<div class="image">
								<img src="{$dispatcher-prefix}{@src}" alt="{@caption}" />
							</div>
							<div class="imageName">
								<xsl:value-of select="@name"/>
							</div>
							<div class="imageDelete">
								<a href="{@deleteLink}">Remove</a>
							</div>				
						</fieldset>
					</div>
				</xsl:for-each>	
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="forum:postFormFiles">
		<xsl:apply-templates select="bebop:formWidget[@type='hidden']" />			
		
		<xsl:apply-templates select="forum:attachedFiles" />	
		
		<div id="plainText">			
			<xsl:apply-templates select="bebop:label " />												
		</div>					
		
		<div class="clearGroup">
			<div class="columnOne">File:</div>
			<div class="columnTwo">
				<xsl:apply-templates select="bebop:formWidget[@name='file']" />
				<div class="validationError">
					<xsl:apply-templates select="bebop:formErrors[@id='file']" />												
				</div>
			</div>
		</div>								
		<div class="clearGroup">
			<div class="columnOne">Description:</div>
			<div class="columnTwo">
				<xsl:apply-templates select="bebop:textarea" />
				<div class="validationError">
					<xsl:apply-templates select="bebop:formErrors[@id='fileDescription']" />												
				</div>
			</div>
		</div>		
		<div class="submit">
			<div class="forumButton">	
				<xsl:apply-templates select="bebop:formWidget[@name='Add_File']" />						
			</div>
		</div>						
	</xsl:template>	
	
	<xsl:template match="forum:attachedFiles" >	
		<xsl:choose>
			<xsl:when test="not(forum:file)">
				<div id="plainText">			
					No files attached to this post												
				</div>		
			</xsl:when>
			<xsl:otherwise>
				<div class="clearGroup">
					<table class="fileTable">
						<tr>
							<td><h5>File</h5></td>
							<td><h5>Description</h5></td>					
							<td/>
						</tr>
						<xsl:for-each select="forum:file">
							<tr>
								<td>
									<xsl:variable name="encodedFileName">
				            			<xsl:call-template name="url-encode">
			    		     		 		<xsl:with-param name="str" select="@name"/>
				        				</xsl:call-template>
				           			 </xsl:variable>						
									
									<a href="{$dispatcher-prefix}{@url}&amp;file=/{$encodedFileName}" target = "_blank"><xsl:value-of select="@name"/></a></td>
									<td>
										<xsl:if test="not(contains(@description, 'nbsp'))">
											<xsl:value-of select="@description"/>
										</xsl:if>
									</td>
									<td><a href="{@deleteLink}">Remove</a></td>
							</tr>
						</xsl:for-each>	
					</table>
					<br/>
				</div>	
			</xsl:otherwise>
		
		</xsl:choose>
			
	</xsl:template>	
	
	<xsl:template match="threadOptions">
		<div id="threadOptions">
			<xsl:apply-templates />
		</div>
	</xsl:template>	
    
    <xsl:template match="bebop:form[@name='newPostForm']">
		<xsl:call-template name="ForumPostForm">
			<xsl:with-param name="title">
				<xsl:choose>
					<xsl:when test="forum:postForm">
						<xsl:text>Post New Message</xsl:text>
					</xsl:when>
					<xsl:when test="forum:postFormImages">
						<xsl:text>Add Images (optional)</xsl:text>
					</xsl:when>
					<xsl:when test="forum:postFormFiles">
						<xsl:text>Add Files (optional)</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Preview New Message</xsl:text>
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
						<xsl:text>Edit Message</xsl:text>
					</xsl:when>	
					<xsl:when test="forum:postFormImages">
						<xsl:text>Edit Images</xsl:text>
					</xsl:when>
					<xsl:when test="forum:postFormFiles">
						<xsl:text>Edit Files</xsl:text>
					</xsl:when>				
					<xsl:otherwise>
						<xsl:text>Preview Changes</xsl:text>
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
					<xsl:when test="forum:postFormImages">
						<xsl:text>Add Images (optional)</xsl:text>
					</xsl:when>
					<xsl:when test="forum:postFormFiles">
						<xsl:text>Add Files (optional)</xsl:text>
					</xsl:when>	
					<xsl:otherwise>
						<xsl:text>Preview Reply</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="bebop:form[@name='categoryAdd']">
		<xsl:param name="title" select="'Create New Topic'" />
		<div class="content">
			<form action="{@action}" name="{@name}" method="get">
				<xsl:apply-templates select="bebop:gridPanel/bebop:formWidget[@type='hidden']" />
				<xsl:apply-templates select="bebop:pageState" />
					
				<div id="title">
					<h2 class="pageSubTitle"><xsl:value-of select="$title" /></h2>			
					<br/>
				</div>		
			
				<div class="clearGroup">
					<div class="columnOne">Name:</div>
					<div class="columnTwo">
						<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:formWidget[@name='name']" />
						<div class="validationError">
							<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:formErrors[@id='name']" />												
						</div>
					</div>
				</div>								
				<div class="clearGroup">
					<div class="columnOne">Description:</div>
					<div class="columnTwo">
						<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:textarea" />
						<div class="validationError">
							<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:formErrors[@id='description']" />												
						</div>
					</div>
				</div>					
				<div class="submit">
					<div class="forumButton">						
						<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:formWidget[@type='submit']" />						
						<br/>
					</div>
				</div>		
			</form>	
		</div>				
	</xsl:template>
	
	<xsl:template match="bebop:form[@name='postRejectionForm']">
		<xsl:param name="title" select="'Reject Post'" />
		
		<form action="{@action}" name="{@name}" method="get">
			<xsl:apply-templates select="bebop:gridPanel/bebop:formWidget[@type='hidden']" />
			<xsl:apply-templates select="bebop:formWidget[@type='hidden']" />
			<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:formWidget[@type='hidden']" />
			<xsl:apply-templates select="bebop:pageState" />
				
			<div id="title">
				<h2 class="pageSubTitle"><xsl:value-of select="$title" /></h2>	
				<br/>		
			</div>					
			<div class="content">					
				<xsl:choose>
					<xsl:when test="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:textarea" >
						<div class="clearGroup">
							<div class="columnOne"><xsl:value-of select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:formWidget[@name='recipient']/@metadata.label" /></div>					
							<div class="columnTwo">
								<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:formWidget[@name='recipient']" />
								<div class="validationError">
									<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:formErrors[@id='recipient']" />												
								</div>
							</div>
						</div>	
						<div class="clearGroup">
							<div class="columnOne"><xsl:value-of select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:textarea[@name='bodyText']/@metadata.label" /></div>					
							<div class="columnTwo">
								<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:textarea[@name='bodyText']" />
								<div class="validationError">
									<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:formErrors[@id='bodyText']" />												
								</div>
							</div>
						</div>	
					</xsl:when>	
					<xsl:otherwise>
						<xsl:for-each select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:label" >
							<div class="clearGroup">
								<xsl:call-template name="br-replace" >
									<xsl:with-param name="text" select=".">
									</xsl:with-param>
								</xsl:call-template>
								
							</div>
						</xsl:for-each>											
					</xsl:otherwise>
				</xsl:choose>
				
				<div class="clearGroup">
					<div class="forumButton">
						<br/>	
						<xsl:apply-templates select="bebop:gridPanel/bebop:panelRow/bebop:cell/bebop:boxPanel/bebop:cell/bebop:formWidget[@type='submit']" />												
						<br/>
					</div>
				</div>	
			</div>			
		</form>					
	</xsl:template>
	
	<xsl:template match="bebop:form[@name='setupForm']">	
		<div class="content">
			<form action="{@action}" name="{@name}" method="get">
				<xsl:apply-templates select="forum:setup/bebop:formWidget[@type='hidden']" />
				<xsl:apply-templates select="bebop:pageState" />		
				
				<div class="clearGroup">					
					<xsl:value-of select="forum:setup/bebop:formWidget[@name='title']/@metadata.label"/>										
				</div>
				<div class="clearGroup">
					<xsl:apply-templates select="forum:setup/bebop:formWidget[@name='title']"/>
				</div>
				<div class="clearGroup">
					<xsl:value-of select="forum:setup/bebop:textarea/@metadata.label"/>				
				</div>
				<div class="clearGroup">
					<xsl:apply-templates select="forum:setup/bebop:textarea"/>				
				</div>
				<div class="clearGroup">
					<xsl:apply-templates select="descendant::bebop:checkbox" />
					
					<div class="expiry">		
						<xsl:apply-templates select="descendant::bebop:formWidget[@name='expiry']" />
						<xsl:value-of select="descendant::bebop:formWidget[@name='expiry']/@metadata.label"/>
					</div>
				</div>			
				
				<div class="clearGroup">
					<div class="forumButton">								
						<xsl:apply-templates select="forum:setup/bebop:boxPanel/bebop:cell/bebop:formWidget[@name='save']" />														
						<br/>
					</div>
				</div>					
			</form>					
		</div>
	</xsl:template>
    
    <xsl:template match="bebop:form[@id = 'adminMemberUserPicker']" mode="show">
		<xsl:call-template name="permissionForm" />
	</xsl:template>
	
	<xsl:template match="bebop:form[@id = 'adminMemberUserPicker']">				
	</xsl:template>
	
	<xsl:template match="bebop:form[@id = 'memberUserPicker']" mode="show">
		<xsl:call-template name="permissionForm" />		
	</xsl:template>
	
	<xsl:template match="bebop:form[@id = 'memberUserPicker']">				
	</xsl:template>
		
	<xsl:template match="bebop:form[@id = 'threadCreateMemberUserPicker']" mode="show">
		<xsl:call-template name="permissionForm" />	
	</xsl:template>
	
	<xsl:template match="bebop:form[@id = 'threadCreateMemberUserPicker']">				
	</xsl:template>
	
	<xsl:template match="bebop:form[@id = 'threadReplyMemberUserPicker']" mode="show">
		<xsl:call-template name="permissionForm" />		
	</xsl:template>
	
	<xsl:template match="bebop:form[@id = 'threadReplyMemberUserPicker']">				
	</xsl:template>
	
	<xsl:template match="bebop:form[@id = 'readerMemberUserPicker']" mode="show">
		<xsl:call-template name="permissionForm" />	
	</xsl:template>
	
	<xsl:template match="bebop:form[@id = 'readerMemberUserPicker']">				
	</xsl:template>
	
	<xsl:template name="permissionForm">		
		<form action="{@action}" name="{@name}" method="get">
			<xsl:apply-templates select="bebop:formWidget[@type='hidden']" />
			<xsl:apply-templates select="bebop:pageState" />
			<xsl:apply-templates  />			
		</form>		
	</xsl:template>
	

<!-- *********************** LINK TEMPLATES ******************** -->
	
	
	<xsl:template match="bebop:link[bebop:label/text() = 'New thread']">
		<br/>
		<div id="newTopicLink">
			<a href="{@href}">Start New Thread</a>
		</div>
	</xsl:template>	
	
	<xsl:template match="bebop:link[bebop:label/text() = 'New topic']">
		<div id="newTopicLink">
			<a href="{@href}">Create New Topic</a>
		</div>
	</xsl:template>	

	<xsl:template match="bebop:link[bebop:label/text() = 'View all threads']">
		<div id="viewAllTopicsLink">
			<a href="{@href}">Back to Thread List</a>
		</div>
	</xsl:template>
	
	<xsl:template match="bebop:link[bebop:label/text() = 'Subscribe to thread']">
		<div id="watchTopicLink">
			<a href="{@href}">Watch this Thread</a>
		</div>
	</xsl:template>
	
	<xsl:template match="bebop:link[bebop:label/text() = 'Unsubscribe to thread']">
		<div id="info">
			<xsl:text>You are watching this thread. To stop watching this thread, click "Stop Watching Thread" below.</xsl:text>
		</div>		
		<div id="watchTopicLink">
			<a href="{@href}">Stop Watching Thread</a>			
		</div>		
	</xsl:template>		


<xsl:template name="br-replace">
    <xsl:param name="text"/>
    <xsl:variable name="cr" select="'&#xa;'"/>
    <xsl:choose>
      <!-- If the value of the $text parameter contains a carriage return... -->
      	<xsl:when test="contains($text,$cr)">
        	<!-- Return the substring of $text before the carriage return -->
        	<xsl:value-of select="substring-before($text,$cr)" disable-output-escaping="yes"/>
        	<!-- And construct a <br/> element -->
        	<br/>
	        <!--
	         | Then invoke this same br-replace template again, passing the
	         | substring *after* the carriage return as the new "$text" to
	         | consider for replacement
	         +-->
	        <xsl:call-template name="br-replace">
	          	<xsl:with-param name="text" select="substring-after($text,$cr)"/>
	        </xsl:call-template>
      	</xsl:when>
    	<xsl:otherwise>
        	<xsl:value-of select="$text" disable-output-escaping="yes"/>
    	</xsl:otherwise>
   	</xsl:choose>
</xsl:template>
</xsl:stylesheet>


