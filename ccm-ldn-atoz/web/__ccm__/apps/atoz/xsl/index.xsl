<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:atoz="http://xmlns.redhat.com/atoz/1.0"
  version="1.0">

  <!-- IMPORT DEFINITIONS ccm-ldn-atoz installed as separate web application
  <xsl:import href="../../../../../ROOT/packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../../ROOT/packages/ui/xsl/ui.xsl"/>
  <xsl:import href="../../../../../ROOT/__ccm__/static/cms/admin/category-step/category-step.xsl"/>
  -->
  <!-- IMPORT DEFINITIONS ccm-ldn-atoz installed into the main CCM webapp
  -->
  <xsl:import href="../../../../packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../packages/ui/xsl/ui.xsl"/>
  <xsl:import href="../../../../__ccm__/static/cms/admin/category-step/category-step.xsl"/>
  <xsl:output method="html"/>

  <xsl:template name="bebop:pageCSS">
    <xsl:call-template name="bebop:pageCSSMain"/>
    <xsl:call-template name="atoz:pageCSSMain"/>
  </xsl:template>


  <xsl:template name="atoz:pageCSSMain">
    <link href="/__ccm__/static/atoz/atoz.css" rel="stylesheet" type="text/css"/>
  </xsl:template>

  <xsl:template match="atoz:atoz">
    <table class="data">
      <thead>
        <tr>
          <xsl:for-each select="atoz:letter">
            <th><a href="?letter={text()}"><xsl:value-of select="."/></a></th>
          </xsl:for-each>
        </tr>
      </thead>
    </table>

    <xsl:apply-templates select="atoz:provider"/>
  </xsl:template>

  <xsl:template match="atoz:provider">
    <table class="data">
      <thead>
        <tr>
          <th title="{@description}"><xsl:value-of select="@title"/></th>
        </tr>
      </thead>
      <tbody>
        <xsl:for-each select="atoz:atomicEntry">
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
              <a title="{@description}" href="{@url}"><xsl:value-of select="@title"/></a>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
      <xsl:for-each select="atoz:compoundEntry">
        <thead>
          <tr>
            <th>
              <xsl:value-of select="@title"/>
              <br/>
              <xsl:value-of select="@description"/>
            </th>
          </tr>
        </thead>
        <tbody>
          <xsl:for-each select="atoz:atomicEntry">
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
                <a href="{@url}"><xsl:value-of select="@title"/></a>
                <br/>
                <xsl:value-of select="@description"/>
              </td>
            </tr>
          </xsl:for-each>
        </tbody>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="atoz:adminPane">
    <div>
      <xsl:apply-templates select="*"/>
    </div>
  </xsl:template>

  <xsl:template match="atoz:providerList">
    <h3>Provider List</h3>
    <table class="data">
      <tr>
        <th>Title</th>
        <th>Description</th>
        <th colspan="2">Actions</th>
      </tr>
      <xsl:if test="count(atoz:provider) = 0">
        <tr>
          <td colspan="4"><em>No providers have been added yet</em></td>
        </tr>
      </xsl:if>
      <xsl:for-each select="atoz:provider">
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
          <td><xsl:value-of select="title"/></td>
          <td><xsl:value-of select="description"/></td>
          <td>
            <xsl:call-template name="atoz:link">
              <xsl:with-param name="url" select="@editURL"/>
              <xsl:with-param name="body" select="'Edit'"/>
            </xsl:call-template>
          </td>
          <td>
            <xsl:call-template name="atoz:link">
              <xsl:with-param name="url" select="@deleteURL"/>
              <xsl:with-param name="body" select="'Delete'"/>
              <xsl:with-param name="type" select="'delete'"/>
            </xsl:call-template>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="bebop:form[@name='providerCreate']">
    <form action="{@action}" name="{@name}">
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
      <table class="property">
        <tr class="even">
          <th>Add a</th>
          <td>
            <xsl:apply-templates select="bebop:select[@name='providers']"/>
            <xsl:apply-templates select="bebop:formErrors[@id='providers']"/>
          </td>
          <td><xsl:apply-templates select="bebop:formWidget[@name='create']"/></td>
        </tr>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="bebop:form">
    <form action="{@action}" name="{@name}">
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
      <table class="property">
        <thead>
          <tr>
            <th colspan="2"><xsl:value-of select="@metadata.title"/></th>
          </tr>
        </thead>
        <tbody>
          <xsl:for-each select="*[not(name() = 'bebop:pageState') and not(name() = 'bebop:formWidget' and (@type = 'hidden' or @type = 'submit')) and not(name() = 'bebop:formErrors')]">
            <xsl:variable name="class">
              <xsl:choose>
                <xsl:when test="position() mod 2 = 0">
                  <xsl:text>odd</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>even</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <tr class="{$class}">
              <th><xsl:value-of select="@metadata.label"/></th>
              <td>
                <xsl:variable name="key">
                  <xsl:value-of select="@name"/>
                </xsl:variable>
                <xsl:apply-templates select="."/>
                <xsl:apply-templates select="../bebop:formErrors[@id=$key]"/>
              </td>
            </tr>
          </xsl:for-each>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="2">
              <xsl:apply-templates select="bebop:formWidget[@type='submit']"/>
            </td>
          </tr>
        </tfoot>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="cms:emptyPage[@title='childCategories']">
    <xsl:choose>
      <xsl:when test="cms:category/@order='sortKey'">
    <xsl:apply-templates select="cms:category/cms:category" mode="cms:javascriptCat">
      <xsl:with-param name="expand" select="'none'"/>
          <xsl:sort data-type="number" select="@sortKey"/>
    </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="cms:category/cms:category" mode="cms:javascriptCat">
          <xsl:sort data-type="text" select="@name"/>
          <xsl:with-param name="expand" select="'none'"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="cms:categoryWidget" mode="cms:javascript">
    <script type="text/javascript" src="/assets/prototype.js"/>
    <script type="text/javascript" src="/assets/category-step.js"/>
    <script type="text/javascript" src="/resource/ccm-ldn-atoz/__ccm__/static/atoz/category-widget.js"/>
    <div>
      <xsl:apply-templates select="cms:category" mode="cms:javascriptCat">
        <xsl:with-param name="expand" select="'block'"/>
      </xsl:apply-templates>
    </div>
    <h3>Selected categories</h3>
    <select id="catWd" size="5" onClick="catDeselect()" style="width: 400px; height=200px">
    </select>
    <select id="catWdHd" name="{@name}" size="5" multiple="multiple" style="display: none">
    </select>
  </xsl:template>

  <xsl:template match="bebop:form" mode="atoz:categoryAliasForm">
    <form action="{@action}" name="{@name}">
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
      <table class="property">
        <thead>
          <tr>
            <th colspan="2">Add category alias</th>
          </tr>
        </thead>
        <tbody>
          <tr class="odd">
            <th>Title:</th>
            <td>
              <xsl:apply-templates select="bebop:formWidget[@name='title']"/>
              <xsl:apply-templates select="bebop:formErrors[@id='title']"/>
            </td>
          </tr>
          <tr class="even">
            <th>Letter:</th>
            <td>
              <xsl:apply-templates select="bebop:select[@name='letter']"/>
              <xsl:apply-templates select="bebop:formErrors[@id='letter']"/>
            </td>
          </tr>
          <tr class="odd">
            <th>Category:</th>
            <td>
              <xsl:apply-templates select="cms:categoryWidget[@name='category']"/>
              <xsl:apply-templates select="bebop:formErrors[@id='category']"/>
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="odd">
            <td></td>
            <td><xsl:apply-templates select="bebop:formWidget[@type='submit']"/></td>
          </tr>
        </tfoot>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="bebop:form" mode="atoz:itemAliasForm">
    <form action="{@action}" name="{@name}">
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
      <table class="property">
        <thead>
          <tr>
            <th colspan="2">Add item alias</th>
          </tr>
        </thead>
        <tbody>
          <tr class="odd">
            <th>Title:</th>
            <td>
              <xsl:apply-templates select="bebop:formWidget[@name='title']"/>
              <xsl:apply-templates select="bebop:formErrors[@id='title']"/>
            </td>
          </tr>
          <tr class="even">
            <th>Letter:</th>
            <td>
              <xsl:apply-templates select="bebop:select[@name='letter']"/>
              <xsl:apply-templates select="bebop:formErrors[@id='letter']"/>
            </td>
          </tr>
          <tr class="odd">
            <th>Item:</th>
            <td>
              <xsl:apply-templates select="bebop:select[@name='item']"/>
              <xsl:apply-templates select="bebop:formErrors[@id='item']"/>
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="odd">
            <td></td>
            <td><xsl:apply-templates select="bebop:formWidget[@type='submit']"/></td>
          </tr>
        </tfoot>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="bebop:form" mode="atoz:categoryBlockForm">
    <form action="{@action}" name="{@name}">
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
      <table class="property">
        <thead>
          <tr>
            <th colspan="2">Block category</th>
          </tr>
        </thead>
        <tbody>
          <tr class="odd">
            <th>Category:</th>
            <td>
              <xsl:apply-templates select="bebop:select[@name='category']"/>
              <xsl:apply-templates select="bebop:formErrors[@id='category']"/>
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="odd">
            <td></td>
            <td><xsl:apply-templates select="bebop:formWidget[@type='submit']"/></td>
          </tr>
        </tfoot>
      </table>
    </form>
  </xsl:template>


  <xsl:template match="bebop:form" mode="atoz:contentTypeBlockForm">
    <form action="{@action}" name="{@name}">
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
      <table class="property">
        <thead>
          <tr>
            <th colspan="2">Block content type</th>
          </tr>
        </thead>
        <tbody>
          <tr class="odd">
            <th>Content Type:</th>
            <td>
              <xsl:apply-templates select="bebop:select[@name='tid']"/>
              <xsl:apply-templates select="bebop:formErrors[@id='tid']"/>
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="odd">
            <td></td>
            <td><xsl:apply-templates select="bebop:formWidget[@type='submit']"/></td>
          </tr>
        </tfoot>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="atoz:categoryProviderAdmin">
    <xsl:apply-templates select="bebop:link[@id='return']"/>

    <xsl:apply-templates select="atoz:providerDetails"/>
    <xsl:apply-templates select="bebop:form[@name='categoryProvider']"/>
    <xsl:apply-templates select="bebop:form[@name='itemProvider']"/>
    <xsl:apply-templates select="bebop:link[@id='edit']"/>

    <xsl:apply-templates select="atoz:itemProviderAliasList"/>
    <xsl:apply-templates select="bebop:form[@name='itemAliasForm']" mode="atoz:itemAliasForm"/>

    <xsl:apply-templates select="atoz:categoryProviderAliasList"/>
    <xsl:apply-templates select="bebop:form[@name='categoryAliasForm']" mode="atoz:categoryAliasForm"/>

    <xsl:apply-templates select="bebop:link[@id='addAlias']"/>

    <xsl:apply-templates select="atoz:categoryProviderBlackList"/>
    <xsl:apply-templates select="bebop:form[@name='categoryBlockForm']" mode="atoz:categoryBlockForm"/>
    <xsl:apply-templates select="bebop:link[@id='addBlock']"/>

    <xsl:apply-templates select="atoz:categoryProviderContentTypeBlackList"/>
    <xsl:apply-templates select="bebop:form[@name='contentTypeBlockForm']" mode="atoz:contentTypeBlockForm"/>
    <xsl:apply-templates select="bebop:link[@id='addContentTypeBlock']"/>
  </xsl:template>

  <xsl:template match="atoz:categoryProviderBlackList">
    <h3>Black List</h3>

    <table class="data">
      <thead>
        <tr>
          <th>Category</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>

        <xsl:if test="count(atoz:categoryProviderBlock) = 0">
          <tr><td colspan="2"><em>There are no blocked categories</em></td></tr>
        </xsl:if>
        <xsl:for-each select="atoz:categoryProviderBlock">
          <tr>
            <td><xsl:value-of select="name"/></td>
            <td>
              <xsl:call-template name="atoz:link">
                <xsl:with-param name="url" select="@deleteURL"/>
                <xsl:with-param name="body" select="'Delete'"/>
                <xsl:with-param name="type" select="'delete'"/>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template match="atoz:categoryProviderContentTypeBlackList">
    <h3>Content Types - Black List</h3>

    <table class="data">
      <thead>
        <tr>
          <th>Content Type</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>

        <xsl:if test="count(atoz:categoryProviderContentTypeBlock) = 0">
          <tr><td colspan="2"><em>There are no blocked content types</em></td></tr>
        </xsl:if>
        <xsl:for-each select="atoz:categoryProviderContentTypeBlock">
          <tr>
            <td><xsl:value-of select="label"/></td>
            <td>
              <xsl:call-template name="atoz:link">
                <xsl:with-param name="url" select="@deleteURL"/>
                <xsl:with-param name="body" select="'Delete'"/>
                <xsl:with-param name="type" select="'delete'"/>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:for-each>
      </tbody> 
    </table>
  </xsl:template>

  <xsl:template match="atoz:categoryProviderAliasList">
    <h3>Category Aliases</h3>

    <table class="data">
      <thead>
        <tr>
          <th>Category</th>
          <th>Letter</th>
          <th>Alias</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>

        <xsl:if test="count(atoz:categoryProviderAlias) = 0">
          <tr><td colspan="4"><em>There are no category aliases</em></td></tr>
        </xsl:if>
        <xsl:for-each select="atoz:categoryProviderAlias">
          <tr>
            <td><xsl:value-of select="name"/></td>
            <td><xsl:value-of select="@letter"/></td>
            <td><xsl:value-of select="@title"/></td>
            <td>
              <xsl:call-template name="atoz:link">
                <xsl:with-param name="url" select="@deleteURL"/>
                <xsl:with-param name="body" select="'Delete'"/>
                <xsl:with-param name="type" select="'delete'"/>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template match="atoz:itemProviderAliasList">
    <h3>Item Aliases</h3>

    <table class="data">
      <thead>
        <tr>
          <th>Item</th>
          <th>Letter</th>
          <th>Alias</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>

        <xsl:if test="count(atoz:itemProviderAlias) = 0">
          <tr><td colspan="4"><em>There are no item aliases</em></td></tr>
        </xsl:if>
        <xsl:for-each select="atoz:itemProviderAlias">
          <tr>
            <td><xsl:value-of select="@itemName"/></td>
            <td><xsl:value-of select="@letter"/></td>
            <td><xsl:value-of select="@title"/></td>
            <td>
              <xsl:call-template name="atoz:link">
                <xsl:with-param name="url" select="@deleteURL"/>
                <xsl:with-param name="body" select="'Delete'"/>
                <xsl:with-param name="type" select="'delete'"/>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template match="atoz:providerDetails">
    <xsl:choose>
      <xsl:when test="objectType = 'com.arsdigita.london.atoz.AtoZCategoryProvider'">
        <table class="property">
          <thead>
            <th colspan="2">Category Provider Details</th>
          </thead>
          <tr class="odd">
            <th>Title:</th>
            <td><xsl:value-of select="title"/></td>
          </tr>
          <tr class="even">
            <th>Description:</th>
            <td><xsl:value-of select="description"/></td>
          </tr>
          <tr class="odd">
            <th>Show items:</th>
            <td><xsl:value-of select="isCompound"/></td>
          </tr>
          <tr class="even">
            <th>Root category:</th>
            <td><xsl:value-of select="rootUseContext/rootCategory/name"/></td>
          </tr>
        </table>
      </xsl:when>
      <xsl:when test="objectType = 'com.arsdigita.london.atoz.AtoZItemProvider'">
        <table class="property">
          <thead>
            <th colspan="2">Item Provider Details</th>
          </thead>
          <tr class="odd">
            <th>Title:</th>
            <td><xsl:value-of select="title"/></td>
          </tr>
          <tr class="even">
            <th>Description:</th>
            <td><xsl:value-of select="description"/></td>
          </tr>
          <tr class="odd">
            <th>Attributes to retrieve:</th>
            <td><xsl:value-of select="loadPaths"/></td>
          </tr>
          <tr class="even">
            <th>Category filter:</th>
            <td><xsl:value-of select="category/name"/></td>
          </tr>
        </table>
      </xsl:when>
      <xsl:when test="objectType = 'com.arsdigita.london.atoz.AtoZSiteProxyProvider'">
        <table class="property">
          <thead>
            <th colspan="2">SiteProxy Provider Details</th>
          </thead>
          <tr class="odd">
            <th>Title:</th>
            <td><xsl:value-of select="title"/></td>
          </tr>
          <tr class="even">
            <th>Description:</th>
            <td><xsl:value-of select="description"/></td>
          </tr>
          <tr class="odd">
            <th>Root category:</th>
            <td><xsl:value-of select="category/name"/></td>
          </tr>
        </table>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- the default bebop template is crap -->
  <xsl:template match="bebop:formErrors">
    <br/><span class="formErrors"><xsl:value-of disable-output-escaping="yes" select="@message"/></span>
  </xsl:template>

  <xsl:template name="atoz:link">
    <xsl:param name="url" select="'url'"/>
    <xsl:param name="body" select="'body'"/>
    <xsl:param name="type" select="'generic'"/>

    <a href="{$url}">
      <img src="/assets/action-{$type}.png" border="0" width="14" height="14">
        <xsl:attribute name="alt">
          <xsl:choose>
            <xsl:when test="$type = 'delete'">
              <xsl:value-of select="'-'"/>
            </xsl:when>
            <xsl:when test="$type = 'add'">
              <xsl:value-of select="'+'"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="'*'"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </img>
    </a>
    <xsl:text>&#160;</xsl:text>
    <a href="{$url}">
      <xsl:value-of select="$body"/>
    </a>
  </xsl:template>

</xsl:stylesheet>
