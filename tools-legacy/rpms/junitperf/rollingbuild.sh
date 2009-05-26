#!/bin/sh

if [ -n "$RPM_DIR" ]
then
  rpmbuild --define "_topdir $RPM_DIR" --define "_sourcedir `pwd`/SOURCES" --clean -ba *.spec
else
  mkdir -p BUILD SRPMS RPMS/noarch
  rpmbuild --define "_sourcedir `pwd`/SOURCES" --clean -ba *.spec
fi

BUILD_HOME=zip
BUILD_ROOT=
BUILD_DIR=$BUILD_HOME/$BUILD_ROOT
NAME=`cat *.spec | grep "^Name:" | sed -e 's/Name: *//'`
VERSION=`cat *.spec | grep "^Version:" | sed -e 's/Version: *//'`
FILENAME="${NAME}-${VERSION}.zip"

echo "Building zip version of ${NAME}-${VERSION}..."
rm -rf "$BUILD_HOME"
mkdir -p "$BUILD_DIR/usr/share/java"
cp SOURCES/*.jar "$BUILD_DIR/usr/share/java"
(
  cd "$BUILD_HOME";
  zip -r "$FILENAME" *
)
mv "$BUILD_HOME/$FILENAME" .

if [ "x$X_CCM_DIST_ZIP_DIR" != "x" ]; then
  cp "$FILENAME" "$X_CCM_DIST_ZIP_DIR"
fi
