<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0">

  <xsl:template match="filterControls"
		name="mandalay:filterControls">
    <form action=".">
      <xsl:attribute name="accept-charset">UTF-8</xsl:attribute>
	<xsl:if test="string-length(./@show) &gt; 0">
	  <input type="hidden" name="show">	    
	    <xsl:attribute name="value"><xsl:value-of select="./@show"/></xsl:attribute>
	  </input>
	</xsl:if>
      <xsl:choose>
	<xsl:when test="string-length=(./@customName) &gt; 0">
	  <xsl:attribute name="class">filterControls <xsl:value-of select="./@customName"/>FilterControls</xsl:attribute>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:attribute name="class">filterControls</xsl:attribute>
	</xsl:otherwise>
      </xsl:choose>
      <xsl:variable name="sortAndFilterListText">
	<xsl:call-template name="mandalay:getStaticText">
	  <xsl:with-param name="module" select="'filterControls'"/>
	  <xsl:with-param name="id" select="concat(./@customName, 'sortAndFilterList')"/>
	</xsl:call-template>
      </xsl:variable>
      <xsl:choose>
	<xsl:when test="string-length($sortAndFilterListText) > 0">
	  <fieldset>
	    <legend>
	      <xsl:value-of select="$sortAndFilterListText"/>
	    </legend>	   
	    <xsl:call-template name="filterControlsFiltersBody"/>
	    <xsl:call-template name="filterControlsSortFieldsBody"/>
	  </fieldset>
	</xsl:when>
	<xsl:otherwise>
	  <div>
	    <xsl:call-template name="filterControlsFiltersBody"/>
	    <xsl:call-template name="filterControlsSortFieldsBody"/>
	  </div>
	</xsl:otherwise>
      </xsl:choose>
    </form>
  </xsl:template>

  <xsl:template match="filterControls/filters"
		name="filterControlsFiltersBody">    
    <fieldset>
      <legend>
	<xsl:call-template name="mandalay:getStaticText">
	  <xsl:with-param name="module" select="'filterControls'"/>
	  <xsl:with-param name="id" select="concat(./@customName, 'filterList')"/>
	</xsl:call-template>	
      </legend>
      <xsl:call-template name="filterControlsFilters"/>
      <input type="submit">
	<xsl:attribute name="value">
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'filterControls'"/>
	    <xsl:with-param name="id" select="concat(./@customName, 'SubmitFilters')"/>
	  </xsl:call-template>
	</xsl:attribute>
      </input>
    </fieldset>
  </xsl:template>


  <xsl:template match="filterControls/sortFields"
		name="filterControlsSortFieldsBody">
    <xsl:if test="count(./sortFields/sortField) &gt; 1">
      <fieldset>
	<legend>
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'filterControls'"/>
	    <xsl:with-param name="id" select="concat(./@customName, 'sortList')"/>
	  </xsl:call-template>	
	</legend>
	<xsl:call-template name="filterControlsSortFields"/>
	<input type="submit">
	  <xsl:attribute name="value">
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'filterControls'"/>
	      <xsl:with-param name="id" select="concat(./@customName, 'SortList')"/>
	    </xsl:call-template>
	  </xsl:attribute>
	</input>
      </fieldset>
    </xsl:if>
  </xsl:template>

  <xsl:template name="filterControlsFilters">
   <!--<code>Current node: <xsl:value-of select="name()"/></code>-->
    <xsl:for-each select="./filters/filter">
      <!--<code><xsl:value-of select="name()"/></code>
      <p><code><xsl:value-of select="./@type"/></code></p>-->
      <xsl:choose>
	<xsl:when test="./@type='text'">
	  <span>
	    <xsl:attribute name="class"><xsl:value-of select="'textFilter'"/></xsl:attribute>
	    <xsl:attribute name="id"><xsl:value-of select="concat(../../@customName, ./@label, 'Filter')"/></xsl:attribute>
	    <label>
	      <xsl:attribute name="for">
		<xsl:value-of select="concat(./@label, 'Filter')"/>
	      </xsl:attribute>
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'filterControls'"/>
		<xsl:with-param name="id" select="concat(../../@customName, ./@label)"/>
	      </xsl:call-template>		  
	    </label>
	    <xsl:element name="input">
	      <xsl:attribute name="type">text</xsl:attribute>
	      <xsl:attribute name="size">20</xsl:attribute>
	      <xsl:attribute name="maxlength">512</xsl:attribute>
	      <xsl:attribute name="id"><xsl:value-of select="concat(./@label, 'Filter')"/></xsl:attribute>
	      <xsl:attribute name="name"><xsl:value-of select="./@label"/></xsl:attribute>
	      <xsl:attribute name="value"><xsl:value-of select="./@value"/></xsl:attribute>
	    </xsl:element>
	  </span>
	</xsl:when>
	<xsl:when test="(./@type='select') or (./@type='compare')">
	  <span>
	    <xsl:attribute name="class"><xsl:value-of select="'selectFilter'"/></xsl:attribute>
	    <xsl:attribute name="id"><xsl:value-of select="concat(../../@customName, ./@label, 'Filter')"/></xsl:attribute>	    
	    <label>
	      <xsl:attribute name="for">
		<xsl:value-of select="concat(./@label, 'Filter')"/>
	      </xsl:attribute>
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'filterControls'"/>
		<xsl:with-param name="id" select="concat(../../@customName, ./@label)"/>
	      </xsl:call-template>		  	      
	    </label>
	    <xsl:element name="select">	 
	      <xsl:attribute name="size">1</xsl:attribute>
	      <xsl:attribute name="id"><xsl:value-of select="concat(./@label, 'Filter')"/></xsl:attribute>
	      <xsl:attribute name="name"><xsl:value-of select="./@label"/></xsl:attribute>
	      <xsl:for-each select="./option">
		<xsl:element name="option">
		  <xsl:attribute name="value"><xsl:value-of select="./@label"/></xsl:attribute>
		  <xsl:if test="./@label = ../@selected">
		    <xsl:attribute name="selected">selected</xsl:attribute>
		  </xsl:if>
		  <xsl:choose>
		    <xsl:when test="./@label='--ALL--'">
		      <xsl:call-template name="mandalay:getStaticText">
			<xsl:with-param name="module" select="'filterControls'"/>
			<xsl:with-param name="id" select="concat(../../../@customName, ../@label, 'All')"/>
		      </xsl:call-template>		  		
		    </xsl:when>
		    <xsl:otherwise>
		      <xsl:choose>
			<xsl:when test="(../@type='compare') or (./@valueType = 'text')">
			  <xsl:call-template name="mandalay:getStaticText">
			    <xsl:with-param name="module" select="'filterControls'"/>
			    <xsl:with-param name="id" select="concat(../../../@customName, ../@label, ./@label)"/>
			  </xsl:call-template>		  		      			  
			</xsl:when>
			<xsl:otherwise>
			  <xsl:value-of select="./@label"/>
			</xsl:otherwise>
		      </xsl:choose>
		    </xsl:otherwise>
		  </xsl:choose>
		</xsl:element>
	      </xsl:for-each>
	    </xsl:element>
	  </span>
	</xsl:when>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="filterControlsSortFields">
   <!-- <xsl:if test="count(./controls/sortFields/sortField) &gt; 1">-->
<!--   <code>sortFields</code>-->
      <label for="selectSortBy">	
	<xsl:call-template name="mandalay:getStaticText">
	  <xsl:with-param name="module" select="'filterControls'"/>
	  <xsl:with-param name="id" select="concat(./@customName, 'SortBy')"/>
	</xsl:call-template>
      </label>
      <select>
	<xsl:attribute name="size">1</xsl:attribute>
	<xsl:attribute name="id"><xsl:value-of select="concat(../../@customName, 'SortBy')"/></xsl:attribute>
	<xsl:attribute name="name">sort</xsl:attribute>	     
	<xsl:for-each select="./sortFields">
	  <xsl:for-each select="./sortField">
	    <xsl:element name="option">
	      <xsl:attribute name="value"><xsl:value-of select="./@label"/></xsl:attribute>
	      <xsl:if test="./@label = ../@sortBy">
		<xsl:attribute name="selected">selected</xsl:attribute>
	      </xsl:if>
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'filterControls'"/>
		<xsl:with-param name="id" select="concat(../../@customName, 'SortBy', ./@label)"/>
	      </xsl:call-template>		  		      
	    </xsl:element>
	  </xsl:for-each>
	</xsl:for-each>
      </select>
   <!-- </xsl:if> -->
  </xsl:template>

</xsl:stylesheet>
