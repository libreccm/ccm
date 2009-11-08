#!/usr/bin/perl -w

use strict;
use File::Basename;
use File::Path;

sub usage() {
    print "Process a set of ESDService items in UK Government Interchange format\n";
    print "into a format suitable for import into the Red Hat CMS.\n\n";

    print "Usage: $0 [source dir] [dest dir] <content section>\n";
    print "  source dir: The directory containing the XML files to process.\n";
    print "  dest dir: The directory to write the processed files and directories into.\n";
    print "  content section: The content section that content will be imported to.\n";
    print "                   (optional, defaults to \"content\")\n";
    exit(0);
}

if (!$ARGV[1]) {
    usage();
}


my $sourceDir = $ARGV[0];
my $destDir = $ARGV[1];
my $contentSection = $ARGV[2] ? $ARGV[2] : "content";
my $xslFile = File::Basename::dirname($0) . "/servicecontent-import.xsl";
my $xslAssetFile = File::Basename::dirname($0) . "/filestorage-import.xsl";

open(TERMS, "> $destDir/term-mapping.xml") || die("Can't open file: $!");
print TERMS <<EOF;
<?xml version="1.0" encoding="UTF-8"?>
<terms:itemMapping xmlns:terms="http://xmlns.redhat.com/london/terms/1.0">
  <terms:domain resource="http://www.esd.org.uk/standards/lgsl/2.00/lgsltermslist.xml"/>
EOF
;

# A mapping from DirectoryLevel1 names to a
# DirectoryLevel1 title and hash of DirectoryLevel2 info
my %level1Dirs = ();

for my $xmlSource (glob("$sourceDir/*.xml")) {
    my $xmlFile = File::Basename::basename($xmlSource);

    open(XMLSOURCE, "< $xmlSource") || die("Couldn't open file: $!");
    my $xmlText;
    {
        local $/ = undef;
        $xmlText = <XMLSOURCE>;
    }
    close(XMLSOURCE);
    
    my $dirLevel1Title = $xmlText;
    # Get the value of the DirectoryLevel1 tag
    $dirLevel1Title =~ s|.*<ns2:DirectoryLevel1>([^<]*)</ns2:DirectoryLevel1>.*|$1|s;
    # Convert it to a filename-friendly format
    my $dirLevel1Name = toFilename($dirLevel1Title);

    # Same for DirectoryLevel2
    my $dirLevel2Title = $xmlText;
    $dirLevel2Title =~ s|.*<ns2:DirectoryLevel2>([^<]*)</ns2:DirectoryLevel2>.*|$1|s;
    my $dirLevel2Name = toFilename($dirLevel2Title);

    my $level1;
    if (exists $level1Dirs{$dirLevel1Name}) {
        # Get a reference to the level 1 list
        $level1 = $level1Dirs{$dirLevel1Name};
    } else {
        # Create the level 1 list and populate it with
        # the level 1 title and an empty hash
        $level1 = [$dirLevel1Title, {}];
        $level1Dirs{$dirLevel1Name} = $level1;
    }

    # Get a reference to the hash that will store level 2 info
    my $level2 = $level1->[1];
    my $level2Files;
    if (exists $level2->{$dirLevel2Name}) {
        # Get a reference to the list of files in the level 2 dir
        $level2Files = $level2->{$dirLevel2Name}->[1];
    } else {
        # Create a list with the level 2 title and 
        # an empty list of level 2 files.
        $level2Files = [];
        $level2->{$dirLevel2Name} = [$dirLevel2Title, $level2Files];
    }

    my $targetDir = "$destDir/$dirLevel1Name/$dirLevel2Name";
    if (! -d $targetDir) {
        # Create the destination directory
        File::Path::mkpath($targetDir);
      }

    my $targetFile = "$targetDir/$xmlFile";

    my $cleanedXML = $xmlText;
    # We need to strip off the "xmlns" attribute or no xsl:templates will match
#    $cleanedXML =~ s|xmlns:ns2="http://www.esd.org.uk/standards"||g;
#    $cleanedXML =~ s|ns2:||g;
    $cleanedXML =~ s|ServiceContent|ns2:ServiceContent|g;
    $cleanedXML =~ s|ns3:||g;
    $cleanedXML =~ s|xmlns:ns3="http://www.esd.org.uk/standards/esdbody"||g;

#    $cleanedXML =~ s| xmlns="http://www.esd.org.uk/standards/xmlschemas/draft/servicecontent.xsd"||g;
    open(CLEANEDXML, "> ${targetFile}.tmp") || die("Couldn't open file: $!");
    print CLEANEDXML $cleanedXML;
    close(CLEANEDXML);

    # This assumes the XML file name has the pattern [0-9]+\.xml.  We use this rather than a synthetic
    # value for the OIDs so we can reliably track which items have already been imported.
    my $esdID = $xmlFile;
    $esdID =~ s|([0-9]+)\.xml|$1|;

    # Process the file using "xsltproc", provided by the libxslt package
    system(("xsltproc", "-o",  $targetFile, "--param", "esdID", $esdID, $xslFile, "${targetFile}.tmp")) == 0 ||
        die("Error processing XML file $xmlSource: $!");

    #unlink("${targetFile}.tmp") || die("Could not delete ${targetFile}.tmp: $!");
    print "Wrote $targetFile\n";

    if ($cleanedXML =~ m|a\s+href="(?:forms/)?(\d+.*.pdf)|) {
        my $targetAssetFile = "$targetDir/form-$xmlFile";
        system(("xsltproc", "-o",  $targetAssetFile, "--param", "esdID", $esdID, "--param", "assetName", "'$1'", $xslAssetFile, "${targetFile}.tmp")) == 0 ||
            die("Error processing XML file $xmlSource: $!");
        print "Wrote $targetAssetFile\n";
        push(@{$level2Files}, "/$dirLevel1Name/$dirLevel2Name/form-$xmlFile");
    }

    # Append the path to the current file to the level 2 file list
    push(@{$level2Files}, "/$dirLevel1Name/$dirLevel2Name/$xmlFile");


    my $termID = $xmlText;
    # Get the value of the LGSLService tag (if any) and use it to generate the term mapping.
    if ($termID =~ s|.*<ns2:LGSLService Id="([0-9]+)">.*?</ns2:LGSLService>.*|$1|s) {
        open(OUTPUTXML, "< $targetFile") || die("Couldn't open file: $!");
        my $outputXML;
        {
            local $/ = undef;
            $outputXML = <OUTPUTXML>;
        }
        close(XMLSOURCE);
        my $itemName = $outputXML;
        # Get the first instance of "cms:item" from the output XML, and use this to generate
        # the path to the item.
        $itemName =~ s|.*<cms:item [^>]+>\s*<cms:name>([^<]+)</cms:name>.*|$1|s;
        print TERMS "  <terms:mapping>\n";
        print TERMS "    <terms:term id=\"$termID\"/>\n";
        print TERMS "    <terms:item path=\"/$contentSection/generic-content/$dirLevel1Name/$dirLevel2Name/$itemName\"/>\n";
        print TERMS "  </terms:mapping>\n";
    }

}

print TERMS "</terms:itemMapping>\n";
close(TERMS);

# The header for the import index
open(INDEX, "> $destDir/index.xml") || die("Couldn't open file: $!");
print INDEX <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<imp:import xmlns:imp="http://xmlns.redhat.com/waf/london/importer/1.0" source="www.esd.org.uk">
  <cms:folder xmlns:cms="http://www.arsdigita.com/cms/1.0" label="Root Folder" name="/" oid="[com.arsdigita.cms.Folder:{id=1}]">
    <cms:folder xmlns:cms="http://www.arsdigita.com/cms/1.0" label="Generic Content" name="generic-content" oid="[com.arsdigita.cms.Folder:{id=2}]">
EOF
;

# Generate the import index
my $id = 3;
for my $level1Name (keys %level1Dirs) {
    my $level1 = $level1Dirs{$level1Name};
    my $level1Title = $level1->[0];
    print INDEX "      <cms:folder label=\"$level1Title\" name=\"$level1Name\" oid=\"[com.arsdigita.cms.Folder:{id=$id}]\">\n";
    $id++;

    my $level2 = $level1->[1];
    for my $level2Name (keys %{$level2}) {
        my $level2Title = $level2->{$level2Name}->[0];
        print INDEX "        <cms:folder label=\"$level2Title\" name=\"$level2Name\" oid=\"[com.arsdigita.cms.Folder:{id=$id}]\">\n";
        $id++;
        my $level2Files = $level2->{$level2Name}->[1];
        for my $importFile (@{$level2Files}) {
            print INDEX "          <cms:external source=\"$importFile\"/>\n";
        }
        print INDEX "        </cms:folder>\n";
    }
    print INDEX "      </cms:folder>\n";
}

print INDEX <<EOF
    </cms:folder>
  </cms:folder>
</imp:import>
EOF
;

close(INDEX);

# Convert the first argument into a filename-friendly format.
# The argument is lowercased, spaces and slashes are converted to
# hyphens, and special characters are removed.
sub toFilename {
    my $result = lc(shift);
    #$result =~ tr|/\\ *?!%^+=#"&:;,`~$'\s|\-\-\-|d;
    $result =~ s/[^-a-zA-Z0-9_]/-/g;
    # Replace any instances of "---" we may have generated, because
    # 3 hyphens in a ro is just too much.
    $result =~ s/-+/-/g;
    #$result =~ s|---|--|g;
    return $result;
}
