#!/bin/sh

for file in *.spec.in; do
    sed "s/^Release: .*/&${SVN_REVISION}/" < $file > ${file%.in}
done

if [ -n "$RPM_DIR" ]
then
  rpmbuild --define "_topdir $RPM_DIR" --define "_sourcedir `pwd`/SOURCES" --clean -ba *.spec
else
  mkdir -p BUILD SRPMS RPMS/noarch
  rpmbuild --define "_sourcedir `pwd`/SOURCES" --clean -ba *.spec
fi

