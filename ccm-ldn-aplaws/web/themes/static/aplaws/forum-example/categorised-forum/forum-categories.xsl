<xsl:stylesheet xmlns:forum="http://www.arsdigita.com/forum/1.0"
				xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:nav="http://ccm.redhat.com/navigation" 
				xmlns:search="http://rhea.redhat.com/search/1.0" 
				xmlns:portal="http://www.uk.arsdigita.com/portal/1.0" 				
				xmlns:cms="http://www.arsdigita.com/cms/1.0" 	
				exclude-result-prefixes="xsl bebop nav search portal forum cms" 
				version="1.0">

  <!-- IMPORT DEFINITIONS ccm-forum installed into the main CCM webapp -->
  <xsl:import href="../../category-step.xsl"/>
  <xsl:import href="../../../heirloom/packages/cms/xsl/admin/category-step/category-step.xsl"/>	

  <xsl:output method="html" indent="yes"/>

	
  <xsl:template match="forum:categoryStepSummary">
   	<div class="content">
   		
	    <h3>Assign Categories</h3>
		<br/>	   
	    <xsl:for-each select="forum:categoryRoots/forum:categoryRoot">
	      <xsl:sort  select="@name"/>
	      <xsl:variable name="name">
	        <xsl:value-of select="@name"/>
	      </xsl:variable>
	      <h5><xsl:value-of select="$name"/></h5>
	      <br/>
	      <xsl:if test="@addAction">
	        <script LANGUAGE="JavaScript">
	          <![CDATA[ <!-- begin script ]]>
	          <![CDATA[ document.write('<a href="]]><xsl:value-of select="@addJSAction"/><![CDATA["><img src="/assets/category-step/action-add.png" border="0"/></a>')]]>
	          <![CDATA[ document.write("\<!--") ]]>
	          <![CDATA[ // end script --> ]]>
	        </script>
	        <a href="{@addAction}">
	          <img src="/assets/category-step/action-add.png" border="0"/>
	        </a>
	        <script LANGUAGE="JavaScript">
	          <![CDATA[ <!-- begin script ]]>
	          <![CDATA[ document.write("--\>") ]]>
	          <![CDATA[ // end script --> ]]>
	        </script>
	        
	        <script LANGUAGE="JavaScript">
	          <![CDATA[ <!-- begin script ]]>
	          <![CDATA[ document.write('<a href="]]><xsl:value-of select="@addJSAction"/><![CDATA[">Add Categories</a>')]]>
	          <![CDATA[ document.write("\<!--") ]]>
	          <![CDATA[ // end script --> ]]>
	        </script>
	        <xsl:text>&#160;</xsl:text>
	        <a href="{@addAction}">
	          <xsl:text>Add categories</xsl:text>
	        </a>
	        <script LANGUAGE="JavaScript">
	          <![CDATA[ <!-- begin script ]]>
	          <![CDATA[ document.write("--\>") ]]>
	          <![CDATA[ // end script --> ]]>
	        </script>
	      </xsl:if>
	       
	      <xsl:choose>
	        <xsl:when test="count(../../forum:itemCategories/forum:itemCategory[starts-with(@path, $name)]) = 0">
	          <div>
	            There are no categories assigned in this context
	          </div>
	        </xsl:when>
	        <xsl:otherwise>
	          <ul>
	            <xsl:for-each select="../../forum:itemCategories/forum:itemCategory[starts-with(@path, $name)]">
	              <xsl:sort select="@path"/>
	              <li>
	                <xsl:value-of select="substring(@path, string-length($name) + 5)"/>&#160;
	                <xsl:if test="@deleteAction">
	                  <a href="{@deleteAction}"><img src="/assets/category-step/action-delete.png" border="0"/></a>
	                  <xsl:text>&#160;</xsl:text>
	                  <a href="{@deleteAction}">Remove</a>
	                </xsl:if>
	              </li>
	            </xsl:for-each>
	          </ul>
	        </xsl:otherwise>
	      </xsl:choose>
	    </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template match="forum:categoryWidget">
    <xsl:choose>
      <xsl:when test="@mode = 'javascript'">
        <xsl:apply-templates select="." mode="forum:javascript"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="." mode="forum:plain"/>        
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="forum:categoryWidget" mode="forum:javascript">
    <script language="JavaScript">
      <![CDATA[
      <!-- Begin hiding
      function catToggle(id) {
        var elImg = document.getElementById("catTog"+id);
        var elChildren = document.getElementById("catCh"+id);

        if (elChildren.style.display != "block") {
           elChildren.style.display = "block";
           elImg.src = "/assets/category-step/action-delete.png";
        } else {
           elChildren.style.display = "none";
           elImg.src = "/assets/category-step/action-add.png";
        }
        return true;
      }
      function catSelect(id, name, node) {
        var elWidget = document.getElementById("catWd");
        var elWidgetHidden = document.getElementById("catWdHd");
        var found = 0;
        for (var i = 0 ; i < elWidget.options.length ; i++) {
          if (elWidget.options[i].value == id) {
            found = 1;
          }
        }
        if (!found) {
          var opt = new Option(name, id);
          elWidget.options[elWidget.options.length] = opt;

          var optHidden = new Option(node, id, false, true);
          elWidgetHidden.options[elWidgetHidden.options.length] = optHidden;
        }

        var elLink = document.getElementById("catLn"+node);
        var elName = document.getElementById("catNm"+node);
        elLink.style.display="none";
        elName.style.display="inline";
        return true;
      }
      function catDeselect() {
      	var elWidget = document.getElementById("catWd");
        var elWidgetHidden = document.getElementById("catWdHd");
        var idx = elWidget.selectedIndex;
        if (idx  != -1) {
            //var id = elWidget.options[idx].value;
            var node = elWidgetHidden.options[idx].text;
            var elLink = document.getElementById("catLn"+node);
            var elName = document.getElementById("catNm"+node);
            elLink.style.display="inline";
            elName.style.display="none";

            elWidget.options[idx] = null;
            elWidgetHidden.options[idx] = null;
        }
        return true;
      }
      // End hiding -->
      ]]>
    </script>
    <div>
      <xsl:apply-templates select="forum:category[@isEnabled = '1']" mode="forum:javascriptCat">
        <xsl:with-param name="expand" select="'block'"/>
      </xsl:apply-templates>
    </div>
    <br/>
    <div class="content">    	
	    <h4>Selected Categories</h4>
	    <select id="catWd" size="5" onClick="catDeselect()" style="width: 400px; height=200px">
	    </select>
	    <select id="catWdHd" name="{@name}" size="5" multiple="multiple" style="display: none">
	    </select>			
  	 </div>
  </xsl:template>

  <xsl:template match="forum:category" mode="forum:javascriptCat">
    <xsl:param name="expand" select="'none'"/>
    <xsl:variable name="linkStyle">
      <xsl:choose>
        <xsl:when test="@isAbstract != '1' and @isSelected != '1'">
          <xsl:value-of select="'inline'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'none'"/>        
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="nameStyle">
      <xsl:choose>
        <xsl:when test="@isAbstract != '1' and @isSelected != '1'">
          <xsl:value-of select="'none'"/>        
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'inline'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <div id="catSelf{@pid}">
    	<div class="content">
	      <xsl:choose>
	        <xsl:when test="count(forum:category[@isEnabled = '1']) > 0 and $expand='none'">
	          <a href="#" onClick="catToggle('{@nodeID}');">
	             <img id="catTog{@nodeID}" src="/assets/category-step/action-add.png" width="14" height="14" border="0"/></a>
	        </xsl:when>
	        <xsl:when test="count(forum:category[@isEnabled = '1']) > 0 and $expand!='none'">
	          <a href="#" onClick="catToggle('{@nodeID}');"><img id="catTog{@nodeID}" src="/assets/category-step/action-delete.png" 
	                      width="14" height="14" border="0"/></a>
	        </xsl:when>
	        <xsl:otherwise>
	          <img src="/assets/category-step/action-generic.png" width="14" height="14" border="0"/>
	        </xsl:otherwise>
	      </xsl:choose>
	      <a id="catLn{@nodeID}" href="#" style="padding-left: 6px; display: {$linkStyle}">
	        <xsl:attribute name="onclick">catSelect('<xsl:value-of select="@id"/>', '<xsl:call-template name="escape-apostrophes">
	          <xsl:with-param name="text" select="@fullname"/>
	        </xsl:call-template>', '<xsl:value-of select="@nodeID"/>')</xsl:attribute>
	
	        <xsl:if test="@description">
	          <xsl:attribute name="title">
	            <xsl:value-of select="@description"/>
	          </xsl:attribute>
	        </xsl:if>
	        <xsl:value-of select="@name"/>
	      </a>
	      <span id="catNm{@nodeID}" style="padding-left: 6px; display: {$nameStyle}">
	        <xsl:if test="@description">
	          <xsl:attribute name="title">
	            <xsl:value-of select="@description"/>
	          </xsl:attribute>
	        </xsl:if>
	        <xsl:value-of select="@name"/>
	      </span>
      </div>
    </div>
    <div id="catCh{@nodeID}" style="margin-left: 20px; display: {$expand}">
      <xsl:apply-templates select="forum:category[@isEnabled = '1']" mode="forum:javascriptCat">
        <xsl:sort data-type="number" select="@sortKey"/>
      </xsl:apply-templates>
    </div>
  </xsl:template>

  <xsl:template match="forum:categoryWidget" mode="forum:plain">
    <select name="{@name}" size="30" multiple="multiple">
      <xsl:apply-templates select="forum:category[position() = 1]/forum:category[@isEnabled = '1' and @isAbstract = '0']" mode="forum:plainCat"/>
    </select>
  </xsl:template>

  <xsl:template match="forum:category" mode="forum:plainCat">
    <xsl:if test="@isSelected != '1' and @isAbstract != '1'">
      <option value="{@sortKey}"><xsl:value-of select="@fullname"/></option>
    </xsl:if>
    
    <xsl:apply-templates select="forum:category[@isEnabled = '1' and @isAbstract = '0']" mode="forum:plainCat">
      <xsl:sort data-type="number" select="@sortKey"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="escape-apostrophes">
    <xsl:param name="text"/>
    <xsl:variable name="apostrophe">'</xsl:variable>
    <xsl:variable name="escaped-apostrophe">\'</xsl:variable>
    <xsl:call-template name="do-replace">
      <xsl:with-param name="text" select="$text"/>
      <xsl:with-param name="replace" select="$apostrophe"/>
      <xsl:with-param name="by" select="$escaped-apostrophe"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="do-replace">
    <xsl:param name="text"/>
    <xsl:param name="replace"/>
    <xsl:param name="by"/>
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text, $replace)"/>
        <xsl:value-of select="$by"/>
        <xsl:call-template name="do-replace">
          <xsl:with-param name="text" select="substring-after($text, $replace)"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="by" select="$by"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>    
  </xsl:template>
  
  <!--<xsl:template match="bebop:formAction[@name='category'] ">
	<div class="content">
		<form action="{@action}" name="{@name}" method="get">
			<xsl:apply-templates select="bebop:gridPanel/bebop:formWidget[@type='hidden']" />
			<xsl:apply-templates select="bebop:pageState" />
			<xsl:apply-templates />		
			<div class="forumButton">											
				<xsl:apply-templates select="bebop:boxPanel/bebop:cell/bebop:boxPanel/bebop:cell/bebop:formWidget" />		
				<br/>
			</div>
		</form>
	</div>
  </xsl:template>-->

</xsl:stylesheet>
