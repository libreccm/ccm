#!/bin/sh

for i in `find $1 -name '*.xml'`
do
  mv $i $i.orig
  xsltproc fix-namespace.xsl $i.orig > $i
done
