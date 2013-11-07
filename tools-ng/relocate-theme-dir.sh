#!/bin/bash

# Support filenames with spaces
IFS=$'\n'

function list_dir() {
	echo "Set working dir to $(pwd)"
	for fh in *; do
		if [[ -d "${fh}" ]]; then
			pushd "${fh}"
			list_dir
			popd
		else
			if [[ -f "${fh}" ]]; then
				case "${fh}" in
					*.xsl)	change_xsl_file "${fh}";;
				esac
			fi
		fi
	done
}

function change_xsl_file() {
	filename="$1"
	echo "Change file ${filename}"
	sed -i "s/__ccm__\///g" "${filename}"
	sed -i "s/\.\.\/ROOT/themes\/heirloom/g" "${filename}"
	sed -i "s/http\:\/\/ccm\.redhat\.com\/london\/navigation/http\:\/\/ccm.redhat.com\/navigation/g" "${filename}"
#	sed -i "s/http\:\/\/ccm\.redhat\.com\/london\/navigation/http\:\/\/ccm.redhat.com\/themedirector/g" "${filename}"
}

pushd $1
list_dir
popd
