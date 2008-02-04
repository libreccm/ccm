#!/bin/sh
#
# Author: Fabrice Retkowsky <fabrice@runtime-collective.com>, 2005
#
# $Id: ccm-build.sh,v 1.1.1.1 2004/11/12 09:30:12 fabrice Exp $

# make a jar with the bundle cfg files
echo "Building bundle jar"

# get the version number
cd build
VER_NUM=`ls *-sql.jar | sort -r | xargs echo | cut -d ' ' -f 1`
VER_NUM=${VER_NUM:15}
VER_NUM=${VER_NUM%-sql.jar}
echo "Version number: $VER_NUM"
cd ..

# make the bundles jar
find * | grep -v "svn" | grep "cfg/" | xargs jar cvf bundles.jar
mv bundles.jar debian/tmp/usr/share/java/ccm-ldn-aplaws-${VER_NUM}-bundles.jar
