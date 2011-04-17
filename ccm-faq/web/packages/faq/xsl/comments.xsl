<?xml version="1.0"?>
<xsl:stylesheet  xmlns:comments="http://www.arsdigita.com/comments/1.0"
  	xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
     	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


 <xsl:template match="comments:comment-detail-view" xmlns:comments="http://www.arsdigita.com/comments/1.0">
    <table width="90%" cellpadding="10">
         <tr>
           <td align="left">
                 <b>Title :</b><xsl:text>&#160;</xsl:text>
           </td>
           <td align="left">
                  <xsl:value-of select="comments:one-comment-view/@title"/>
           </td>
         </tr>
         <tr>
           <td valign="top">
                 <b>Comment:</b> <xsl:text>&#160;</xsl:text>
           </td>
           <td valign="top">
                 <xsl:value-of disable-output-escaping="yes" select="comments:one-comment-view/@comment"/>
           </td>
         </tr>
         <tr>
           <td align="left">       
                 <b>By :</b><xsl:text>&#160;</xsl:text>
           </td>
           <td align="left">
                  <xsl:value-of select="comments:one-comment-view/@name"/>
           </td>
         </tr>
         <tr>
           <td align="left">
                 <b>Format :</b><xsl:text>&#160;</xsl:text>
           </td>
           <td align="left">
                 <xsl:value-of select="comments:one-comment-view/@format"/>
           </td>
         </tr>
         
           <xsl:if test="comments:one-comment-view/@checkAdmin = 1">
          <tr>
           <td align="left">     
                 <b>Status:</b><xsl:text>&#160;</xsl:text>
           </td>
           <td align="left">
                 <a href="comment-setstatus.jsp?{@query-string}&amp;message={comments:one-comment-view/@message}"><xsl:value-of select="comments:one-comment-view/@status"/></a>
           </td>
         </tr>
         <tr>
           <td>
                 <a href="comment-delete.jsp?{@query-string}" onClick='return confirm("The comment will be deleted permanently. Do you want to continue?");'>Delete this Comment</a>
           </td>
           <td align="right">
                 <a href="index.jsp?{@query-string}">Return to all Comments</a>
           </td>
         </tr>
           </xsl:if>
           <xsl:if test="@checkAdmin = 0">
          <tr>
           <td align="left">     
                 <b>Status:</b><xsl:text>&#160;</xsl:text>
           </td>
           <td align="left">
                 <xsl:value-of select="comments:one-comment-view/@status"/>
           </td>
         </tr>
         <tr>
           <td align="right">
                 <a href="index.jsp?{@query-string}">Return to all Comments</a>
           </td>
         </tr>
        </xsl:if>

    </table>
 </xsl:template>


 <xsl:template match="comments:comments-view" xmlns:comments="http://www.arsdigita.com/comments/1.0">

    <hr />
    <center>
    <h3>Comments</h3>
    </center>

    <table width="90%" cellpadding="20">
         <xsl:if test="count(comments:comments-list/comments:comment/@commentID)=0">
                 No comments have been made.
         </xsl:if>
         
         <xsl:for-each select="comments:comments-list/comments:comment" xmlns:comments="http://www.arsdigita.com/comments/1.0">
             <tr>
               <td>
                  <xsl:if test="@checkAdmin = 1">
                  <h3><i><xsl:value-of disable-output-escaping="yes" select="@title"/></i></h3>(<a href="comments/edit.jsp?commentID={@commentID}&amp;{ancestor::comments:comments-view/@query-string}&amp;return-url={ancestor::comments:comments-view/@return-url}">edit</a>)
                 </xsl:if>
                 <xsl:if test="@checkAdmin = 0">
                      <h3><i><xsl:value-of disable-output-escaping="yes" select="@title"/></i></h3>
                 </xsl:if>
                 <blockquote>
                 <xsl:value-of disable-output-escaping="yes" select="@comment"/>
                 <p/>
                 -- <i>by <xsl:value-of select="@name"/> on DATE </i><xsl:value-of select="@date"/>
                 </blockquote>
               </td>
            </tr>
          </xsl:for-each>
      </table>
         
      <center>
         <a href="comments/add.jsp?{@query-string}&amp;return-url={@return-url}">Add a Comment</a>                        
      </center>
 </xsl:template>



 <xsl:template match="comments:commentsTab" xmlns:comments="http://www.arsdigita.com/comments/1.0">

    <table width="90%" cellpadding="20">

         <xsl:if test="count(comments:singlecomment/@commentID)= 0">
                 <b>There is no comment available under this section.</b>
         </xsl:if>
   <xsl:if test="count(comments:singlecomment/@commentID)> 0">
         
     <tr>
       <th width="5%"><a href="?{@query-string}&amp;order-by=id">ID</a></th>
       <th width="20%"><a href="?{@query-string}&amp;order-by=subject">Title</a></th>
       <th width="30%">Last Modified</th>
       <th width="40%">By</th>

     </tr>
   </xsl:if>
         <xsl:for-each select="comments:singlecomment" xmlns:comments="http://www.arsdigita.com/comments/1.0">
             <tr>
               <td align="center" width="5%">
                 <xsl:value-of select="@commentID"/>aaaaaaa
               </td>
               <td align="center" width="20%">
                 <a href="one-object.jsp?commentID={@commentID}&amp;{ancestor::comments:commentsTab/@query-string}"><xsl:value-of select="@title"/></a>                           
               </td>
               <td align="center" width="30%">
                 <xsl:value-of select="@lastModifiedDate"/>
               </td>
               <td align="center" width="40%">
                 <xsl:value-of select="@lastModifiedUser"/>
               </td>
              
             </tr>
          </xsl:for-each>
      </table>
         
         
 </xsl:template>


 <xsl:template match="comments:comment" xmlns:comments="http://www.arsdigita.com/comments/1.0">
         <xsl:apply-templates />
 </xsl:template>



 <xsl:template match="comments:section">
   <table width="100%" border="0" cellspacing="0" cellpadding="0">
     <xsl:apply-templates select="comments:section_top"/>
     <xsl:apply-templates select="comments:section_bottom"/>
  </table>
 </xsl:template>

 <xsl:template match="comments:section_top"  xmlns:gc="http://www.arsdigita.com/comments/1.0">
   <tr>
    <td>
     <table width="100%" cellspacing="0" cellpadding="2" border="0">
     <tr>
        <th class="section_header" align="left">
            <xsl:value-of select="@section_title"/>
          </th>
           <td class="section_header" align="right">
             <xsl:apply-templates/>
           </td>
       </tr> 
      </table>
     </td>
   </tr>
   <tr>
     <td bgcolor="#ffffff">
       <table border="0" cellspacing="0" cellpadding="0">
         <tr>
           <td height="1"></td>
         </tr>
       </table>
     </td>
   </tr>
 </xsl:template>


 <xsl:template match="comments:confirm-element"  xmlns:gc="http://www.arsdigita.com/comments/1.0">
 <p/>
 <blockquote>
  <font color="red"><xsl:apply-templates/></font>
 </blockquote>
 <p/>
 </xsl:template>

 <xsl:template match="comments:section_bottom"  xmlns:gc="http://www.arsdigita.com/comments/1.0">
   <tr>
     <td class="section_body">
       <br/>
         <xsl:apply-templates/>
       <br/>
     </td> 
   </tr>
 </xsl:template>

 <xsl:template match="comments:comment-information-added"  xmlns:gc="http://www.arsdigita.com/comments/1.0">
 The Comment has been added. 
 </xsl:template>

 <xsl:template match="comments:comment-information-changed"  xmlns:gc="http://www.arsdigita.com/comments/1.0">
 The Comment information has been changed and should be reflected below. 
 </xsl:template>

 <xsl:template match="comments:comment-active"  xmlns:gc="http://www.arsdigita.com/comments/1.0">
 This Comment is now approved. 
 </xsl:template>

 <xsl:template match="comments:comment-inactive"  xmlns:gc="http://www.arsdigita.com/comments/1.0">
 This Comment is now unapproved. 
 </xsl:template>


</xsl:stylesheet>


