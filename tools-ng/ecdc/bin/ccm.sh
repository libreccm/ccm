#!/bin/sh

SCRIPTDIR=`dirname $0`
ant -f $SCRIPTDIR/../../../build.xml ccm -Dccm.parameters="$*"
