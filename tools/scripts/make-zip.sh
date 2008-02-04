#!/bin/bash
#
# Author: dgregor@redhat.com

BUILD_HOME=buildwin
BUILD_ROOT=
BUILD_DIR=$BUILD_HOME/$BUILD_ROOT

VERSION=`cat configure.in.tmp | grep "^VERSION=" | sed -e 's/VERSION=//'`
FILENAME="ccm-scripts-${VERSION}.zip"

echo "Building Windows version of ccm-scripts..."

rm -rf "$BUILD_HOME"

mkdir -p "$BUILD_DIR/usr/share/ccm-scripts/bin"
cp bin/*.cmd "$BUILD_DIR/usr/share/ccm-scripts/bin"
cp bin/*.pl "$BUILD_DIR/usr/share/ccm-scripts/bin"
cp bin/*.sh "$BUILD_DIR/usr/share/ccm-scripts/bin"
cp bin/env-conf "$BUILD_DIR/usr/share/ccm-scripts/bin"
cp bin/make-zip "$BUILD_DIR/usr/share/ccm-scripts/bin"
cp bin/make-source "$BUILD_DIR/usr/share/ccm-scripts/bin"

cp --parents `find pkg -name '.svn' -prune -o -type f -not -name "Makefile*" -not -name "*~" -not -name "pl.test" -not -name '.cvsignore' -printf '%p\n'` "$BUILD_DIR/usr/share/ccm-scripts"

mkdir -p "$BUILD_DIR/usr/share/ccm-devel"
cp --parents template/rollingbuild.cmd "$BUILD_DIR/usr/share/ccm-devel"

mkdir -p "$BUILD_DIR/etc/profile.d"
cp etc/*.cmd "$BUILD_DIR/etc/profile.d/"
cat etc/ccm-scripts.sh.in  | sed -e 's!\@datadir\@/\@PACKAGE\@!\"\$CCM_ZIP_ROOT\"!' > "$BUILD_DIR/etc/profile.d/ccm-scripts.sh"

mkdir -p "$BUILD_DIR/usr/share/doc/ccm-scripts-$VERSION"

for file in AUTHORS ChangeLog INSTALL NEWS README TODO
do
  cp "$file" "$BUILD_DIR/usr/share/doc/ccm-scripts-$VERSION/"
done

(
  cd "$BUILD_HOME";
  zip -r "$FILENAME" *
)

mv "$BUILD_HOME/$FILENAME" .

if [ "x$X_CCM_DIST_ZIP_DIR" != "x" ]; then
  cp "$FILENAME" "$X_CCM_DIST_ZIP_DIR"
fi

echo "Wrote $FILENAME"

rm -rf "$BUILD_DIR"

