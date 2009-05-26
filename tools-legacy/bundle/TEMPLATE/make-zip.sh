##!/bin/bash
#
# Author: dgregor@redhat.com

# Bundle specific values (must be executed in Bundle directory)
. ./bundle.in

BUILD_HOME=buildwin
BUILD_ROOT=
BUILD_DIR=$BUILD_HOME/$BUILD_ROOT

VERSION=`cat configure.in | grep "^VERSION=" | sed -e 's/VERSION=//'`
FILENAME="ccm-bundle-${BUNDLE_NAME}-${VERSION}.zip"
mkdir_and_cp () {
    # Copy a file to a directory, creating the directory if necessary
    file="$1"
    directory="$2"

    mkdir -p "$directory"
    cp "$file" "$directory"
}

echo "Building zip version of ccm-bundle-${BUNDLE_NAME}..."

rm -rf "$BUILD_HOME"

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/bundles/${BUNDLE_NAME}"
find cfg/ -name '.svn' -prune -o -type f -not -name "Makefile*" -a -not -name "*~" -exec cp {} "$BUILD_DIR/usr/share/ccm-tools/bundles/${BUNDLE_NAME}/" \;

(
  cd "$BUILD_HOME";
  zip -r "$FILENAME" *
)

mv "$BUILD_HOME/$FILENAME" .

if [ "x$X_CCM_DIST_ZIP_DIR" != "x" ]; then
  cp "$FILENAME" "$X_CCM_DIST_ZIP_DIR"
fi

rm -rf "$BUILD_HOME"

echo "Wrote $FILENAME"

