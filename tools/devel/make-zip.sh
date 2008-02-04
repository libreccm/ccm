#!/bin/bash
#
# Author: dgregor@redhat.com

BUILD_HOME=buildwin
BUILD_ROOT=
BUILD_DIR=$BUILD_HOME/$BUILD_ROOT

VERSION=`cat configure.in.tmp | grep "^VERSION=" | sed -e 's/VERSION=//'`
FILENAME=ccm-devel-${VERSION}.zip

echo "Building Windows version of ccm-devel..."

rm -rf $BUILD_HOME

mkdir -p $BUILD_DIR/bin
cp bin/*.pl $BUILD_DIR/bin/
cp bin/*.cmd $BUILD_DIR/bin/
cp bin/*.sh $BUILD_DIR/bin/
cp bin/ccm-configure $BUILD_DIR/bin/

mkdir -p $BUILD_DIR/usr/share/ccm-devel/lib
cp lib/*.jar $BUILD_DIR/usr/share/ccm-devel/lib/

mkdir -p $BUILD_DIR/usr/share/ccm-devel/xsl
cp xsl/*.xsl $BUILD_DIR/usr/share/ccm-devel/xsl/

mkdir -p $BUILD_DIR/usr/share/ccm-devel/xsd
cp xsd/*.xsd $BUILD_DIR/usr/share/ccm-devel/xsd/

mkdir -p $BUILD_DIR/etc/ccm-devel
mkdir -p $BUILD_DIR/etc/ccm-devel/project.d
cp etc/resin.conf.in $BUILD_DIR/etc/ccm-devel/
cp etc/server.xml.in $BUILD_DIR/etc/ccm-devel/
cp etc/project*.cmd $BUILD_DIR/etc/ccm-devel/
cp etc/project*.pl $BUILD_DIR/etc/ccm-devel/
cp etc/project*.sh $BUILD_DIR/etc/ccm-devel/

mkdir -p $BUILD_DIR/etc/profile.d
cp etc/ccm-devel.cmd $BUILD_DIR/etc/profile.d/
cp etc/ccm-devel.sh $BUILD_DIR/etc/profile.d/

mkdir -p $BUILD_DIR/var/lib/ccm-devel
cp etc/portalloc.txt $BUILD_DIR/var/lib/ccm-devel/

mkdir -p $BUILD_DIR/usr/share/ccm-devel/projects
mkdir -p $BUILD_DIR/usr/share/ccm-devel/applications

for file in `find template/ -name '.svn' -prune -o -type f -not -name "Makefile*" -not -name "*~" -not -name '.cvsignore' -printf '%p\n'`
do
  target="usr/share/ccm-devel/`echo $file | sed -e 's/custom/@@appname@@/'`"
  dir=`echo $target | sed -e 's/\/[^\/]*$//'`
  mkdir -p $BUILD_DIR/$dir
  cp $file $BUILD_DIR/$dir
done

mkdir -p "$BUILD_DIR/usr/share/doc/ccm-devel-$VERSION/"
for file in AUTHORS ChangeLog INSTALL NEWS README TODO
do
  cp $file "$BUILD_DIR/usr/share/doc/ccm-devel-$VERSION/"
done

(
  cd $BUILD_HOME;
  zip -r $FILENAME * -x "*.cvsignore"
)

mv $BUILD_HOME/$FILENAME .

if [ "x$X_CCM_DIST_ZIP_DIR" != "x" ]; then
  cp $FILENAME $X_CCM_DIST_ZIP_DIR
fi

echo "Wrote $FILENAME"

rm -rf $BUILD_DIR

