#!/bin/sh
#
# Outputs a sorted list of apps suitable for
# use to build-all.sh
#
# Usage: build-ordering.sh */*/application.xml

tsort="/var/tmp/building-ordering-tsort-$$.txt"

for app in $@
do
  name=`echo $app | sed -e 's/\/.*//'`
  echo $name $name >> $tsort
  package=`grep '<ccm:application' $app | perl -e 'while(<STDIN>) { s/^.*name="(.*?)".*$/$1/; print }'`
  echo $name > $tsort-$package
done

for app in $@
do
  name=`echo $app | sed -e 's/\/.*//'`
  for dep in `grep '<ccm:requires' $app | perl -e 'while (<STDIN>) {s,^.*<ccm:requires\s+name="(.*?)".*$,$1,e; print}'`
  do
    dname=`cat $tsort-$dep`
    echo $name $dname >> $tsort
  done
done

for app in $@
do
  name=`echo $app | sed -e 's/\/.*//'`
  rm -f $tsort-$name
done

tsort $tsort | tac

rm -f $tsort
