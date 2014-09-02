#/bin/bash
# This is helper script for the upgrade of the intranet installation
# APLAWS+ at the London Bourough of Camden from 1.0.4-r5 to 2.2.x
# The script requrires Bash version 4 or newer. Before running this script
# set the CCM_BIN_DIR variable to the correct value (the bin dir of your
# CCM/APLAWS+ installation)

CCM_BIN_DIR=/srv/lbc-intranet/webapps/ROOT/WEB-INF/bin

# Declare names for the upgrades. This also defines the order in which the 
# upgrades are executed.
UPGRADES=('core-6.5.2-6.5.3'
          'core-6.5.3-6.5.4'
          'core-6.5.4-6.5.5'
          'core-6.5.5-6.5.6'
          'core-6.6.0-6.6.1'
          'core-6.6.1-6.6.2'
          'core-6.6.2-6.6.3'
          'atoz-6.5.0-6.6.0'
          'atoz-6.6.0-6.6.1'
          'cms-6.5.0-6.5.1'
          'cms-6.5.2-6.5.3'
          'cms-6.5.3-6.5.4'
          'cms-6.6.6-6.6.7'
          'forum-6.6.1-6.6.2'
          'search-6.6.0-6.6.1'
          'terms-6.5.0-6.5.1'
          'terms-6.5.1-6.5.2'
          'terms-6.6.0-6.6.1'
          'terms-6.6.1-6.6.2'
          'portalworkspace-6.6.0-6.6.1'          
          'rssfeed-6.6.0-6.6.1'
          'shortcuts-6.6.0-6.6.1'
          'subsite-6.6.0-6.6.1'
          'themedirector-6.6.0-6.6.1'
          'themedirector-6.6.1-6.6.2'
          'core-6.6.5-6.6.6'
          'cms-6.6.9-6.6.10'
          'decisiontree-1.0.3-6.6.0'
          'xmlfeed-6.6.0-6.6.1'
          'atoz-6.6.1-6.6.2'
          'atoz-6.6.2-6.6.3'
          'atoz-siteproxy-6.6.1-6.6.2'
          'ldn-atoz-6.6.1-6.6.2'
          'ldn-aplaws-0.0.0-6.6.1'
          'lbc-aplaws-1.0.4-6.6.1'
          'retention-1.0.4-6.6.0'
          'search-6.6.1-6.6.2'
          'rssfeed-6.6.1-6.6.2'
          'shortcuts-6.6.1-6.6.2'
          'subsite-6.6.1-6.6.2'
          'terms-6.6.2-6.6.3'
          'themedirector-6.6.1-6.6.2'
          'navigation-6.6.2-6.6.3'
          'cms-6.6.0-6.6.1'
          'cms-6.6.1-6.6.2'
          'cms-6.6.3-6.6.4' 
          'relatedlink-6.6.0-6.6.1' 
          'cms-6.6.4-6.6.5'
          'cms-6.6.5-6.6.6'
          'cms-6.6.7-6.6.8'
          'cms-6.6.8-6.6.9'
          'portalworkspace-6.5.0-6.5.1'
          'portalworkspace-6.5.1-6.5.2'
          'portalworkspace-6.5.2-6.5.3'
          'portalworkspace-6.6.1-6.6.2'
          'navigation-6.6.1-6.6.2'
          'navigation-6.6.0-6.6.1'
          'forum-6.6.0-6.6.1'
          'forum-6.5.0-6.5.1'
          'forum-6.5.1-6.5.2'
          'core-6.6.3-6.6.4' 
          'core-6.6.4-6.6.5' 
          'mpa-6.6.0-6.6.1',
          'cms-6.6.10-6.6.11')

# Define the packages to upgrade for each upgrade. The package name is given
# without version, exactly as the first parameter for the ccm upgrade command.
declare -A PACKAGES
PACKAGES=( [atoz-6.5.0-6.6.0]=ccm-atoz
           [atoz-6.6.0-6.6.1]=ccm-atoz
           [atoz-6.6.1-6.6.2]=ccm-atoz
           [atoz-6.6.2-6.6.3]=ccm-atoz
           [atoz-siteproxy-6.6.1-6.6.2]=ccm-atoz-siteproxy
           [cms-6.5.0-6.5.1]=ccm-cms
           [cms-6.5.2-6.5.3]=ccm-cms
           [cms-6.5.3-6.5.4]=ccm-cms
           [cms-6.6.0-6.6.1]=ccm-cms
           [cms-6.6.1-6.6.2]=ccm-cms
           [cms-6.6.3-6.6.4]=ccm-cms
           [cms-6.6.4-6.6.5]=ccm-cms
           [cms-6.6.5-6.6.6]=ccm-cms
           [cms-6.6.6-6.6.7]=ccm-cms
           [cms-6.6.7-6.6.8]=ccm-cms
           [cms-6.6.8-6.6.9]=ccm-cms
           [cms-6.6.9-6.6.10]=ccm-cms
           [cms-6.6.10-6.6.11]=ccm-cms
           [relatedlink-6.6.0-6.6.1]=ccm-cms-assets-relatedlink
           [article-6.6.0-6.6.1]=ccm-cms-types-article
           [decisiontree-1.0.3-6.6.0]=ccm-cms-types-decisiontree
           [event-6.6.0-6.6.1]=ccm-cms-types-event
           [mpa-6.6.0-6.6.1]=ccm-cms-types-mparticle
           [newsitem-6.6.0-6.6.1]=ccm-cms-types-newsitem
           [xmlfeed-6.6.0-6.6.1]=ccm-cms-types-xmlfeed
           [core-6.5.2-6.5.3]=ccm-core
           [core-6.5.3-6.5.4]=ccm-core
           [core-6.5.4-6.5.5]=ccm-core
           [core-6.5.5-6.5.6]=ccm-core
           [core-6.6.0-6.6.1]=ccm-core
           [core-6.6.1-6.6.2]=ccm-core
           [core-6.6.2-6.6.3]=ccm-core
           [core-6.6.3-6.6.4]=ccm-core
           [core-6.6.4-6.6.5]=ccm-core
           [core-6.6.5-6.6.6]=ccm-core 
           [forum-6.5.0-6.5.1]=ccm-forum
           [forum-6.5.1-6.5.2]=ccm-forum
           [forum-6.6.0-6.6.1]=ccm-forum
           [forum-6.6.1-6.6.2]=ccm-forum
           [lbc-aplaws-1.0.4-6.6.1]=ccm-lbc-aplaws
           [ldn-aplaws-0.0.0-6.6.1]=ccm-ldn-aplaws
           [ldn-atoz-6.6.1-6.6.2]=ccm-ldn-atoz
           [retention-1.0.4-6.6.0]=ccm-lbc-retention
           [search-6.6.0-6.6.1]=ccm-ldn-search
           [search-6.6.1-6.6.2]=ccm-ldn-search
           [navigation-6.5.0-6.5.1]=ccm-navigation
           [navigation-6.6.0-6.6.1]=ccm-navigation
           [navigation-6.6.1-6.6.2]=ccm-navigation
           [navigation-6.6.2-6.6.3]=ccm-navigation
           [portalworkspace-6.5.0-6.5.1]=ccm-portalworkspace
           [portalworkspace-6.5.1-6.5.2]=ccm-portalworkspace
           [portalworkspace-6.5.2-6.5.3]=ccm-portalworkspace
           [portalworkspace-6.6.0-6.6.1]=ccm-portalworkspace
           [portalworkspace-6.6.1-6.6.2]=ccm-portalworkspace
           [rssfeed-6.6.0-6.6.1]=ccm-rssfeed
           [rssfeed-6.6.1-6.6.2]=ccm-rssfeed
           [shortcuts-6.6.0-6.6.1]=ccm-shortcuts
           [shortcuts-6.6.1-6.6.2]=ccm-shortcuts
           [subsite-6.6.0-6.6.1]=ccm-subsite
           [subsite-6.6.1-6.6.2]=ccm-subsite
           [terms-6.5.0-6.5.1]=ccm-ldn-terms
           [terms-6.5.1-6.5.2]=ccm-ldn-terms
           [terms-6.6.0-6.6.1]=ccm-ldn-terms
           [terms-6.6.1-6.6.2]=ccm-ldn-terms
           [terms-6.6.2-6.6.3]=ccm-ldn-terms
           [themedirector-6.6.0-6.6.1]=ccm-themedirector
           [themedirector-6.6.1-6.6.2]=ccm-themedirector
           [themedirector-6.6.2-6.6.3]=ccm-themedirector )

# Define the from versions for each upgrade
declare -A VERSION_FROM
VERSION_FROM=( [atoz-6.5.0-6.6.0]=6.5.0
               [atoz-6.6.0-6.6.1]=6.6.0
               [atoz-6.6.1-6.6.2]=6.6.1
               [atoz-6.6.2-6.6.3]=6.6.2
               [atoz-siteproxy-6.6.1-6.6.2]=6.6.1
               [cms-6.5.0-6.5.1]=6.5.0
               [cms-6.5.2-6.5.3]=6.5.2
               [cms-6.5.3-6.5.4]=6.5.3
               [cms-6.6.0-6.6.1]=6.6.0
               [cms-6.6.1-6.6.2]=6.6.1
               [cms-6.6.3-6.6.4]=6.6.3
               [cms-6.6.4-6.6.5]=6.6.4
               [cms-6.6.5-6.6.6]=6.6.5
               [cms-6.6.6-6.6.7]=6.6.6
               [cms-6.6.7-6.6.8]=6.6.7
               [cms-6.6.8-6.6.9]=6.6.8
               [cms-6.6.9-6.6.10]=6.6.9
               [cms-6.6.10-6.6.11]=6.6.10
               [relatedlink-6.6.0-6.6.1]=6.6.0
               [article-6.6.0-6.6.1]=6.6.0
               [decisiontree-1.0.3-6.6.0]=1.0.3
               [event-6.6.0-6.6.1]=6.6.0
               [mpa-6.6.0-6.6.1]=6.6.0
               [newsitem-6.6.0-6.6.1]=6.6.0
               [xmlfeed-6.6.0-6.6.1]=6.6.0
               [core-6.5.2-6.5.3]=6.5.2
               [core-6.5.3-6.5.4]=6.5.3
               [core-6.5.4-6.5.5]=6.5.4
               [core-6.5.5-6.5.6]=6.5.5
               [core-6.6.0-6.6.1]=6.6.0
               [core-6.6.1-6.6.2]=6.6.1
               [core-6.6.2-6.6.3]=6.6.2
               [core-6.6.3-6.6.4]=6.6.3
               [core-6.6.4-6.6.5]=6.6.4
               [core-6.6.5-6.6.6]=6.6.5
               [forum-6.5.0-6.5.1]=6.5.0
               [forum-6.5.1-6.5.2]=6.5.1
               [forum-6.6.0-6.6.1]=6.6.0
               [forum-6.6.1-6.6.2]=6.6.1
               [lbc-aplaws-1.0.4-6.6.1]=1.0.4
               [ldn-aplaws-0.0.0-6.6.1]=0.0.0
               [ldn-atoz-6.6.1-6.6.2]=6.6.1
               [retention-1.0.4-6.6.0]=1.0.4
               [search-6.6.0-6.6.1]=6.6.0
               [search-6.6.1-6.6.2]=6.6.1
               [navigation-6.5.0-6.5.1]=6.5.0
               [navigation-6.6.0-6.6.1]=6.6.0
               [navigation-6.6.1-6.6.2]=6.6.1
               [navigation-6.6.2-6.6.3]=6.6.2
               [portalworkspace-6.5.0-6.5.1]=6.5.0
               [portalworkspace-6.5.1-6.5.2]=6.5.1
               [portalworkspace-6.5.2-6.5.3]=6.5.2
               [portalworkspace-6.6.0-6.6.1]=6.6.0
               [portalworkspace-6.6.1-6.6.2]=6.6.1
               [rssfeed-6.6.0-6.6.1]=6.6.0
               [rssfeed-6.6.1-6.6.2]=6.6.1
               [shortcuts-6.6.0-6.6.1]=6.6.0
               [shortcuts-6.6.1-6.6.2]=6.6.1
               [subsite-6.6.0-6.6.1]=6.6.0
               [subsite-6.6.1-6.6.2]=6.6.1
               [terms-6.5.0-6.5.1]=6.5.0
               [terms-6.5.1-6.5.2]=6.5.1
               [terms-6.6.0-6.6.1]=6.6.0
               [terms-6.6.1-6.6.2]=6.6.1
               [terms-6.6.2-6.6.3]=6.6.2
               [themedirector-6.6.0-6.6.1]=6.6.0
               [themedirector-6.6.1-6.6.2]=6.6.1
               [themedirector-6.6.2-6.6.3]=6.6.2 )

# Define the the target versions for each upgrade
declare -A VERSION_TO
VERSION_TO=( [atoz-6.5.0-6.6.0]=6.6.0
             [atoz-6.6.0-6.6.1]=6.6.1
             [atoz-6.6.1-6.6.2]=6.6.2
             [atoz-6.6.2-6.6.3]=6.6.3
             [atoz-siteproxy-6.6.1-6.6.2]=6.6.2
             [cms-6.5.0-6.5.1]=6.5.1
             [cms-6.5.2-6.5.3]=6.5.3
             [cms-6.5.3-6.5.4]=6.5.4
             [cms-6.6.0-6.6.1]=6.6.1
             [cms-6.6.1-6.6.2]=6.6.2
             [cms-6.6.3-6.6.4]=6.6.4
             [cms-6.6.4-6.6.5]=6.6.5
             [cms-6.6.5-6.6.6]=6.6.6
             [cms-6.6.6-6.6.7]=6.6.7
             [cms-6.6.7-6.6.8]=6.6.8
             [cms-6.6.8-6.6.9]=6.6.9
             [cms-6.6.9-6.6.10]=6.6.10
             [cms-6.6.10-6.6.11]=6.6.11
             [relatedlink-6.6.0-6.6.1]=6.6.1
             [article-6.6.0-6.6.1]=6.6.1
             [decisiontree-1.0.3-6.6.0]=6.6.0
             [event-6.6.0-6.6.1]=6.6.1
             [mpa-6.6.0-6.6.1]=6.6.1
             [newsitem-6.6.0-6.6.1]=6.6.1
             [xmlfeed-6.6.0-6.6.1]=6.6.1
             [core-6.5.2-6.5.3]=6.5.3
             [core-6.5.3-6.5.4]=6.5.4
             [core-6.5.4-6.5.5]=6.5.5
             [core-6.5.5-6.5.6]=6.5.6
             [core-6.6.0-6.6.1]=6.6.1
             [core-6.6.1-6.6.2]=6.6.2
             [core-6.6.2-6.6.3]=6.6.3
             [core-6.6.3-6.6.4]=6.6.4
             [core-6.6.4-6.6.5]=6.6.5
             [core-6.6.5-6.6.6]=6.6.6
             [forum-6.5.0-6.5.1]=6.5.1
             [forum-6.5.1-6.5.2]=6.5.2
             [forum-6.6.0-6.6.1]=6.6.1
             [forum-6.6.1-6.6.2]=6.6.2
             [lbc-aplaws-1.0.4-6.6.1]=6.6.1
             [ldn-aplaws-0.0.0-6.6.1]=6.6.1
             [ldn-atoz-6.6.1-6.6.2]=6.6.2
             [retention-1.0.4-6.6.0]=6.6.0
             [search-6.6.0-6.6.1]=6.6.1
             [search-6.6.1-6.6.2]=6.6.2
             [navigation-6.5.0-6.5.1]=6.5.1
             [navigation-6.6.0-6.6.1]=6.6.1
             [navigation-6.6.1-6.6.2]=6.6.2
             [navigation-6.6.2-6.6.3]=6.6.3
             [portalworkspace-6.5.0-6.5.1]=6.5.1
             [portalworkspace-6.5.1-6.5.2]=6.5.2
             [portalworkspace-6.5.2-6.5.3]=6.5.3
             [portalworkspace-6.6.0-6.6.1]=6.6.1
             [portalworkspace-6.6.1-6.6.2]=6.6.2
             [rssfeed-6.6.0-6.6.1]=6.6.1
             [rssfeed-6.6.1-6.6.2]=6.6.2
             [shortcuts-6.6.0-6.6.1]=6.6.1
             [shortcuts-6.6.1-6.6.2]=6.6.2
             [subsite-6.6.0-6.6.1]=6.6.1
             [subsite-6.6.1-6.6.2]=6.6.2
             [terms-6.5.0-6.5.1]=6.5.1
             [terms-6.5.1-6.5.2]=6.5.2
             [terms-6.6.0-6.6.1]=6.6.1
             [terms-6.6.1-6.6.2]=6.6.2
             [terms-6.6.2-6.6.3]=6.6.3
             [themedirector-6.6.0-6.6.1]=6.6.1
             [themedirector-6.6.1-6.6.2]=6.6.2
             [themedirector-6.6.2-6.6.3]=6.6.3 )

# Define the current versions for each package. This is used by this script
# to check if a module is installed. (The script checks if the JAR file exists).
declare -A CURRENT_VERSION
CURRENT_VERSION=( [atoz-6.5.0-6.6.0]=6.6.3
                  [atoz-6.6.0-6.6.1]=6.6.3
                  [atoz-6.6.1-6.6.2]=6.6.3
                  [atoz-6.6.2-6.6.3]=6.6.3
                  [atoz-siteproxy-6.6.1-6.6.2]=6.6.2
                  [cms-6.5.0-6.5.1]=6.6.11
                  [cms-6.5.2-6.5.3]=6.6.11
                  [cms-6.5.3-6.5.4]=6.6.11
                  [cms-6.6.0-6.6.1]=6.6.11
                  [cms-6.6.1-6.6.2]=6.6.11
                  [cms-6.6.3-6.6.4]=6.6.11
                  [cms-6.6.4-6.6.5]=6.6.11
                  [cms-6.6.5-6.6.6]=6.6.11
                  [cms-6.6.6-6.6.7]=6.6.11
                  [cms-6.6.7-6.6.8]=6.6.11
                  [cms-6.6.8-6.6.9]=6.6.11
                  [cms-6.6.9-6.6.10]=6.6.11
                  [cms-6.6.10-6.6.11]=6.6.11
                  [relatedlink-6.6.0-6.6.1]=6.6.1
                  [article-6.6.0-6.6.1]=6.6.1
                  [decisiontree-1.0.3-6.6.0]=6.6.0
                  [event-6.6.0-6.6.1]=6.6.1
                  [mpa-6.6.0-6.6.1]=6.6.1
                  [newsitem-6.6.0-6.6.1]=6.6.1
                  [xmlfeed-6.6.0-6.6.1]=6.6.1
                  [core-6.5.2-6.5.3]=6.6.6
                  [core-6.5.3-6.5.4]=6.6.6
                  [core-6.5.4-6.5.5]=6.6.6
                  [core-6.5.5-6.5.6]=6.6.6
                  [core-6.6.0-6.6.1]=6.6.6
                  [core-6.6.1-6.6.2]=6.6.6
                  [core-6.6.2-6.6.3]=6.6.6
                  [core-6.6.3-6.6.4]=6.6.6
                  [core-6.6.4-6.6.5]=6.6.6
                  [core-6.6.5-6.6.6]=6.6.6
                  [forum-6.5.0-6.5.1]=6.6.2
                  [forum-6.5.1-6.5.2]=6.6.2
                  [forum-6.6.0-6.6.1]=6.6.2
                  [forum-6.6.1-6.6.2]=6.6.2
                  [lbc-aplaws-1.0.4-6.6.1]=6.6.1
                  [ldn-aplaws-0.0.0-6.6.1]=6.6.1
                  [ldn-atoz-6.6.1-6.6.2]=6.6.2
                  [search-6.6.0-6.6.1]=6.6.2
                  [search-6.6.1-6.6.2]=6.6.2
                  [navigation-6.5.0-6.5.1]=6.6.3
                  [navigation-6.6.0-6.6.1]=6.6.3
                  [navigation-6.6.1-6.6.2]=6.6.3
                  [navigation-6.6.2-6.6.3]=6.6.3
                  [portalworkspace-6.5.0-6.5.1]=6.6.3
                  [portalworkspace-6.5.1-6.5.2]=6.6.3
                  [portalworkspace-6.5.2-6.5.3]=6.6.3
                  [portalworkspace-6.6.0-6.6.1]=6.6.3
                  [portalworkspace-6.6.1-6.6.2]=6.6.3
                  [rssfeed-6.6.0-6.6.1]=6.6.2
                  [rssfeed-6.6.1-6.6.2]=6.6.2
                  [shortcuts-6.6.0-6.6.1]=6.6.2
                  [shortcuts-6.6.1-6.6.2]=6.6.2
                  [subsite-6.6.0-6.6.1]=6.6.2
                  [subsite-6.6.1-6.6.2]=6.6.2
                  [terms-6.5.0-6.5.1]=6.6.3
                  [terms-6.5.1-6.5.2]=6.6.3
                  [terms-6.6.0-6.6.1]=6.6.3
                  [terms-6.6.1-6.6.2]=6.6.3
                  [terms-6.6.2-6.6.3]=6.6.3
                  [themedirector-6.6.0-6.6.1]=6.6.3
                  [themedirector-6.6.1-6.6.2]=6.6.3
                  [themedirector-6.6.2-6.6.3]=6.6.3
                  [retention-1.0.4-6.6.0]=6.6.0 )

NUMBER_OF_UPGRADES=${#UPGRADES[@]}
COUNTER=1;

# Change to the WEB-INF/bin/ directory.
pushd $CCM_BIN_DIR

# Perform the upgrades
for UPGRADE in ${UPGRADES[@]}
do
    echo "Processing upgrade $UPGRADE (${COUNTER}/${NUMBER_OF_UPGRADES})..."
    echo "Current package: ${PACKAGES[$UPGRADE]}"
    echo "Version from   : ${VERSION_FROM[$UPGRADE]}"
    echo "Version to     : ${VERSION_TO[$UPGRADE]}"
    echo "Current version: ${CURRENT_VERSION[$UPGRADE]}"
    # Check if the JAR of the module is present
	if [ -f	"../lib/${PACKAGES[$UPGRADE]}-${CURRENT_VERSION[$UPGRADE]}.jar" ]; then
		echo "Upgrading ${PACKAGES[$UPGRADE]} from version ${VERSION_FROM[$UPGRADE]} to version ${VERSION_TO[$UPGRADE]}..."
		bash ccm upgrade ${PACKAGES[$UPGRADE]} --from-version=${VERSION_FROM[$UPGRADE]} --to-version=${VERSION_TO[$UPGRADE]}
		
		# Check the return value from ccm upgrade. If the upgrade failed, 
		# exit the script.
        if [ $? -eq 0 ]; then
        	echo -e "Upgrade ${UPGRADE} finished successfully.\n\n"
	    else
	    	echo "Upgrade ${UPGRADE} failed. Aborting."
	    	exit 1
	    fi
    else
        # JAR was not found, skip upgrade
		echo "${PACKAGES[$UPGRADE]}-${CURRENT_VERSION[$UPGRADE]}.jar not found. Skiping upgrade."
		exit 1
	fi
	
	let "COUNTER++"
	
done

# Change back to start directory.
popd

