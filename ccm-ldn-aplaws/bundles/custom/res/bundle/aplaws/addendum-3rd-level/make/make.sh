#!/bin/sh

java com.icl.saxon.StyleSheet lgcl-1.03.xml make-domain-nav-1.03-addendum.xsl > ../domain-nav-1.03-addendum.xml
java com.icl.saxon.StyleSheet lgcl-1.03.xml make-hierarchy-nav-1.03-addendum.xsl > ../hierarchy-nav-1.03-addendum.xml
