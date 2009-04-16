<?xml version="1.0"?>

<!-- 
This is the main stylesheet for Bebop.  It imports all of the .xsl
files with xsl.import.  This stylesheet is locale-independent. 
--> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

<!-- catch-all rules must have lowest precedence -->
<xsl:import href="plain.xsl"/>

<!-- templates for each component -->
<xsl:import href="Page.xsl"/>
<xsl:import href="BoxPanel.xsl"/>
<xsl:import href="CheckBoxGroup.xsl"/>
<xsl:import href="ColumnPanel.xsl"/>
<xsl:import href="DataTable.xsl"/>
<xsl:import href="DimensionalNavbar.xsl"/>
<xsl:import href="Form.xsl"/>
<xsl:import href="GridPanel.xsl"/>
<xsl:import href="Image.xsl"/>
<xsl:import href="Label.xsl"/>
<xsl:import href="Link.xsl"/>
<xsl:import href="List.xsl"/>
<xsl:import href="PageErrorDisplay.xsl"/>
<xsl:import href="ListPanel.xsl"/>
<xsl:import href="MultipleSelect.xsl"/>
<xsl:import href="PageState.xsl"/>
<xsl:import href="RadioGroup.xsl"/>
<xsl:import href="Select.xsl"/>
<xsl:import href="SplitPanel.xsl"/>
<xsl:import href="TabbedPane.xsl"/>
<xsl:import href="Table.xsl"/>
<xsl:import href="TextArea.xsl"/>
<xsl:import href="DHTMLEditor.xsl"/>
<xsl:import href="FCKEditor.xsl"/>
<xsl:import href="Tree.xsl"/>
<xsl:import href="Widget.xsl"/>
<xsl:import href="SegmentedPanel.xsl"/>
<xsl:import href="DateTime.xsl"/>
<xsl:import href="dcp.xsl"/>

<!-- solid line -->
<xsl:template match="bebop:solidLine">
  <hr />
</xsl:template>


</xsl:stylesheet>




