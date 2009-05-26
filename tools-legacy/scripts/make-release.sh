#!/bin/sh

TAGNAME=RELEASE

VERSION=`grep VERSION= configure.in  | sed -e 's/VERSION=//'`
RELEASE=`grep RELEASE= configure.in  | sed -e 's/RELEASE=//'`

OLD_VERSION=$VERSION
OLD_RELEASE=$RELEASE

MAJOR=`echo $OLD_VERSION | awk -F . '{print $1}'`
MINOR=`echo $OLD_VERSION | awk -F . '{print $2}'`
REVISION=`echo $OLD_VERSION | awk -F . '{print $3}'`


NEXT_MAJOR=`expr $MAJOR + 1`
NEXT_MINOR=`expr $MINOR + 1`
NEXT_REVISION=`expr $REVISION + 1`
NEXT_RELEASE=`expr $RELEASE + 1`

NEW_MAJOR="${NEXT_MAJOR}.0.0"
NEW_MINOR="${MAJOR}.${NEXT_MINOR}.0"
NEW_REVISION="${MAJOR}.${MINOR}.${NEXT_REVISION}"


CONFIRM=x
while test "$CONFIRM" != 'y' -a "$CONFIRM" != 'Y' 
do
  echo
  echo "Current release is $MAJOR.$MINOR.$REVISION-$RELEASE"
  echo
  echo "Select release increment:"
  echo
  echo " 1) Package  -> $VERSION-$NEXT_RELEASE	(for RPM changes)"
  echo " 2) Revision -> $NEW_REVISION-1	(for bug fixes)"
  echo " 3) Minor    -> $NEW_MINOR-1	(for new functionality)"
  echo " 4) Major    -> $NEW_MAJOR-1	(for large rewrites)"
  echo 
  echo -n "> "

  ANSWER=x
  while test "$ANSWER" != 1 -a "$ANSWER" != 2 -a "$ANSWER" != 3 -a "$ANSWER" != 4
  do
    read ANSWER
    if test "x$ANSWER" = "x" ; then ANSWER=x ; fi
  
    case $ANSWER in 
      1) NEW_VERSION=$VERSION
         NEW_RELEASE=$NEXT_RELEASE;;
      2) NEW_VERSION=$NEW_REVISION
         NEW_RELEASE=1;;
      3) NEW_VERSION=$NEW_MINOR
         NEW_RELEASE=1;;
      4) NEW_VERSION=$NEW_MAJOR
         NEW_RELEASE=1;;
      *) echo -n "> ";;
    esac
  done
  
  echo "New version will be $NEW_VERSION-$NEW_RELEASE"
  echo "Confirm y/N"
  echo -n "> "
  
  read CONFIRM
  if test "x$CONFIRM" = "x" ; then CONFIRM=x ; fi
done


echo "Setting version to $NEW_VERSION-$NEW_RELEASE"

perl -i -p -e "s/VERSION=$OLD_VERSION/VERSION=$NEW_VERSION/" configure.in
perl -i -p -e "s/RELEASE=$OLD_RELEASE/RELEASE=$NEW_RELEASE/" configure.in


TAG_VERSION=`echo $NEW_VERSION | sed -e 's/\./_/g'`
TAG_RELEASE=$NEW_RELEASE

cvs commit -m "Release $NEW_VERSION-$NEW_RELEASE" configure.in
cvs tag ${TAGNAME}-${TAG_VERSION}-${TAG_RELEASE}

echo "Release tagged as $TAGNAME-$TAG_VERSION-$TAG_RELEASE"

exit 0
