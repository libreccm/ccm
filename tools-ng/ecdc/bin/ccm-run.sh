#!/bin/sh

SCRIPTDIR=`dirname $0`
ant -f $SCRIPTDIR/../../../build.xml ccm-run -Dccm.classname=$1 -Dccm.parameters="$2 $3 $4 $5 $6 $7 $8"
