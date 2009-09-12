#!/bin/sh


rm -rf xml assets items
mkdir xml assets items

(
  cd xml
  unzip ../aplaws.zip
)

(
  cd assets
  unzip ../forms.zip
  rm -f *.htm
)

echo Fixing namespaces
./fix-namespace.sh xml
./process-esdservice-xml.pl xml items
