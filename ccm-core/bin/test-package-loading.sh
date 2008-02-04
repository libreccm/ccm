#!/bin/bash

dbname=$1

if [ "x$dbname" = "x" ]; then
    echo "Usage: test-package-loading.sh <database-name>"
    exit
fi

echo "Testing package loading on postgres database $dbname"

dropdb $dbname && createdb $dbname && \
ant run -Drun.classname=com.arsdigita.loader.CoreLoader -Drun.args="waf.admin.email=admin@example.com waf.admin.name.screen=admin waf.admin.name.given=Justin waf.admin.name.family=Ross waf.admin.password=123456 waf.admin.password.question=12345 waf.admin.password.answer=6 waf.login.stylesheet=login_en.xsl waf.login.dispatcher=com.arsdigita.ui.login.SubsiteDispatcher waf.mime.resource=com/arsdigita/loader/mimetypes.properties name=localhost port=9000" && \
ant run -Drun.classname=com.arsdigita.cms.Loader
