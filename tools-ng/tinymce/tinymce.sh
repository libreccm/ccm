#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"

CORE_PATH="../../ccm-core/web/assets/tinymce/js/tinymce/plugins"
RUNTIME_PATH="../../runtime/apache-tomcat-8.5.15/webapps/ROOT/assets/tinymce/js/tinymce/plugins"

cd $SCRIPTPATH

if [[ $# -eq 0 ]]; then
    npm install
    grunt
elif [[ $1 == "build" ]]; then
    grunt
elif [[ $1 == "setup" ]]; then
    npm install
elif [[ $1 == "plugin" ]]; then
    grunt validateVersion
    grunt shell:tsc
    grunt tslint
    grunt globals
    grunt rollup:$2-plugin
    grunt uglify:$2-plugin
    grunt copy:plugins
    
    cp -rv $SCRIPTPATH/js/tinymce/plugins/$1 $CORE_PATH
elif [[ $1 == "test" ]]; then
    grunt validateVersion
    grunt shell:tsc
    grunt globals
    grunt rollup:$2-plugin
    grunt uglify:$2-plugin
    grunt copy:plugins
    
    cp -rv $SCRIPTPATH/js/tinymce/plugins/$1 $RUNTIME_PATH
fi