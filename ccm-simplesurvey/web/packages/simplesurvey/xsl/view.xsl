<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
        xmlns:simplesurvey="http://www.arsdigita.com/simplesurvey/1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

  <xsl:template match="bebop:label[@id='surveyNotLive']">
    This survey is currently not active.
  </xsl:template>

  <xsl:template match="bebop:label[@id='surveyAlreadySubmitted']">
    You have already submitted a response to this survey and only one response is permitted.
  </xsl:template>
</xsl:stylesheet>
