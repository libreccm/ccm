#!/bin/bash
#
# Author: dgregor@redhat.com

BUILD_HOME=buildwin
BUILD_ROOT=
BUILD_DIR=$BUILD_HOME/$BUILD_ROOT

VERSION=`cat configure.in.tmp | grep "^VERSION=" | sed -e 's/VERSION=//'`
FILENAME="ccm-tools-${VERSION}.zip"
mkdir_and_cp () {
    # Copy a file to a directory, creating the directory if necessary
    file="$1"
    directory="$2"

    mkdir -p "$directory"
    cp "$file" "$directory"
}

echo "Building zip version of ccm-tools..."

rm -rf "$BUILD_HOME"

mkdir -p "$BUILD_DIR/bin"
find bin/ -maxdepth 1 -type f -name "ccm*" -not -name "*~" -exec cp {} "$BUILD_DIR/bin/" \;

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/bin"
cp bin/javaconfig "$BUILD_DIR/usr/share/ccm-tools/bin/"

mkdir -p "$BUILD_DIR/usr/share/doc/ccm-tools-${VERSION}"
cp AUTHORS ChangeLog INSTALL NEWS README TODO "$BUILD_DIR/usr/share/doc/ccm-tools-${VERSION}/"

mkdir -p "$BUILD_DIR/usr/share/ccm"
mkdir -p "$BUILD_DIR/usr/share/ccm/conf"
mkdir -p "$BUILD_DIR/usr/share/ccm/conf/registry"
mkdir -p "$BUILD_DIR/usr/share/ccm/data"
mkdir -p "$BUILD_DIR/usr/share/ccm/data/p2fs"
mkdir -p "$BUILD_DIR/usr/share/ccm/logs"
mkdir -p "$BUILD_DIR/usr/share/ccm/tmp"
mkdir -p "$BUILD_DIR/usr/share/ccm/webapps"
mkdir -p "$BUILD_DIR/usr/share/ccm/webapps/ROOT/packages/content-section/templates"

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/commands"
find commands/ -maxdepth 1 -type f -not -name "Makefile*" -not -name "*~" -exec cp {} "$BUILD_DIR/usr/share/ccm-tools/commands/" \;

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/lib/CCM"
cp lib/CCM/*.pm "$BUILD_DIR/usr/share/ccm-tools/lib/CCM"

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/lib/security"
cp lib/security/*.jar "$BUILD_DIR/usr/share/ccm-tools/lib/security"

mkdir -p "$BUILD_DIR/etc/profile.d"
cp etc/profile.d/ccm-tools.cmd "$BUILD_DIR/etc/profile.d/"
cp etc/profile.d/ccm-tools.sh "$BUILD_DIR/etc/profile.d/"

mkdir -p "$BUILD_DIR/etc/init.d"
cp etc/init.d/ccm* "$BUILD_DIR/etc/init.d/"

cp etc/envvars "$BUILD_DIR/usr/share/ccm/conf"

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

## ccm-tools-servlet-resin
FILENAME="ccm-tools-servlet-resin-${VERSION}.zip"
echo "Building zip version of ccm-tools-servlet-resin..."

rm -rf "$BUILD_HOME"

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/server/resin/bin"
mkdir -p "$BUILD_DIR/usr/share/ccm-tools/server/resin/conf"
find server/resin/bin -name '.svn' -prune -o -type f -not -name "Makefile*" -not -name "*~" -exec cp {} "$BUILD_DIR/usr/share/ccm-tools/{}" \;
find server/resin/conf -name '.svn' -prune -o -type f -not -name "Makefile*" -not -name "*~" -exec cp {} "$BUILD_DIR/usr/share/ccm-tools/{}" \;

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/lib/CCM/Server"
cp lib/CCM/Server/Resin.pm "$BUILD_DIR/usr/share/ccm-tools/lib/CCM/Server"

mkdir -p "$BUILD_DIR/etc/profile.d"
cp etc/profile.d/ccm-tools-resin.cmd "$BUILD_DIR/etc/profile.d/"
cp etc/profile.d/ccm-tools-resin.sh "$BUILD_DIR/etc/profile.d/"

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

## ccm-tools-servlet-tomcat
FILENAME="ccm-tools-servlet-tomcat-${VERSION}.zip"
echo "Building zip version of ccm-tools-servlet-tomcat..."

rm -rf "$BUILD_HOME"

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/server/tomcat/bin"
mkdir -p "$BUILD_DIR/usr/share/ccm-tools/server/tomcat/conf"
find server/tomcat/bin -name '.svn' -prune -o -type f -not -name "Makefile*" -not -name "*~" -exec cp {} "$BUILD_DIR/usr/share/ccm-tools/{}" \;
find server/tomcat/conf -name '.svn' -prune -o -type f -not -name "Makefile*" -not -name "*~" -exec cp {} "$BUILD_DIR/usr/share/ccm-tools/{}" \;

mkdir -p "$BUILD_DIR/usr/share/ccm-tools/lib/CCM/Server"
cp lib/CCM/Server/Tomcat.pm "$BUILD_DIR/usr/share/ccm-tools/lib/CCM/Server"

mkdir -p "$BUILD_DIR/etc/profile.d"
cp etc/profile.d/ccm-tools-tomcat.cmd "$BUILD_DIR/etc/profile.d/"
cp etc/profile.d/ccm-tools-tomcat.sh "$BUILD_DIR/etc/profile.d/"

for file in `find server/tomcat/src/build/ -name *.class`
do
  target="classes/`echo $file | sed -e 's/server\/tomcat\/src\/build//'`"
  dir=`echo $target | sed -e 's/\/[^\/]*$//'`
  mkdir_and_cp $file "$BUILD_DIR/usr/share/ccm-tools/server/tomcat/$dir"
done

(
  cd "$BUILD_HOME";
  zip -r "$FILENAME" *
)


if [ "x$X_CCM_DIST_ZIP_DIR" != "x" ]; then
  mkdir -p "$X_CCM_DIST_ZIP_DIR"
  mv "$BUILD_HOME/$FILENAME" "$X_CCM_DIST_ZIP_DIR"
  echo "Wrote $X_CCM_DIST_ZIP_DIR/$FILENAME"
else
  mv "$BUILD_HOME/$FILENAME" .
  echo "Wrote $FILENAME"
fi

rm -rf "$BUILD_HOME"
