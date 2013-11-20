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
					*.css)	change_css_file "${fh}";;
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
	sed -i "s/\/ccm-ldn-theme\/themes-dev/\.\.\/\.\.\/devel-themedir/g" "${filename}"

	sed -i "s/ccm-ldn-atoz/heirloom/g" "${filename}"
	sed -i "s/ccm-forum/heirloom/g" "${filename}"
	sed -i "s/ccm-ldn-theme\/themes-dev/heirloom/g" "${filename}" #????
	sed -i "s/ccm-ldn-search/heirloom/g" "${filename}"
	sed -i "s/ccm-ldn-shortcuts/heirloom/g" "${filename}"
	sed -i "s/ccm-ldn-subsite/heirloom/g" "${filename}"
	sed -i "s/ccm-ldn-terms/heirloom/g" "${filename}"
	sed -i "s/ccm-ldn-theme/heirloom/g" "${filename}"
#	sed -i "s/ / /g" "${filename}"
}

function change_css_file() {
	filename="$1"
	echo "Change file ${filename}"
	sed -i "s/__ccm__\/static\/atoz/..\/..\/heirloom\/apps\/atoz\/lib/g" "${filename}"
	sed -i "s/__ccm__\/static\/navigation/..\/..\/heirloom\/apps\/navigation\/lib/g" "${filename}"
	sed -i "s/__ccm__\/static\/terms/..\/..\/heirloom\/apps\/terms\/lib/g" "${filename}"
}

pushd $1
list_dir
popd
