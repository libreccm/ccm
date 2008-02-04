#!/usr/local/bin/perl

sub usage {
    my $string = shift(@_) || '';
    my $programName = $0;
    print "\n";
    print "$string\n\n" if $string;
    print <<"eof";

Generates an XML file of all the object types in the system.
Currently (4/5/2001) only supports table/column definitions

\@author Michael Bryzek (mbryzek\@arsdigita.com)
\@creation-date 2001-04-05
\@cvs-id $Id: generate-xml-from-db.pl 287 2005-02-22 00:29:02Z sskracic $

USAGE: $programName oracle_username oracle_password xml_file_stub

   oracle_username/oracle_password specifies how we connect to oracle
   xml_file_stub is used to create two files:
     * xml_file_stub.xml
     * xml_file_stub.dtd

eof
 
    exit(1);
}

my $username = shift(@ARGV) || usage();
my $password = shift(@ARGV) || usage();

my $xmlFileBase = shift(@ARGV) || usage();

# These two variables used to format xml
local $SPACES = "  ";
local $XML_DEPTH = 0;

# What's the name of this schema?
local $schemaName = "oracle";
local $modelName = "model";

# MAX_THINGS  is the  max  number of  any  type of  element to  output
# (e.g. no more than 5 tables)
local $MAX_THINGS = 0;

# With which object type do we start exporting?
my $STARTING_OBJECT_TYPE = "acs_object";
# my $STARTING_OBJECT_TYPE = "party";


### END OF CONFIGURATION ###

$QUIET = 1;

use DBI;
use Data::Dumper;

main($username, $password, $xmlFileBase);


sub main {
    my ($username, $password, $xmlFileBase) = @_;

    local %DTD_TYPE_MAP = ('ID' => 'CDATA',
		       'IDREF' => 'CDATA');

    local %DATATYPE_MAP = ('boolean' => 'Boolean',
			   'number' => 'Integer',
			   'text' => 'String',
			   'keyword' => 'String',
			   'integer' => 'Integer',
			   'date' => 'Date',
			   'string' => 'String');


    local %TABLE_COLUMN_OVERRIDE;
    # Override columnnames
    $TABLE_COLUMN_OVERRIDE{'ACS_ATTRIBUTE_VALUES'}{'ATTRIBUTE_ID'}{COLUMN} = 'ATTRIBUTE_NAME';
    $TABLE_COLUMN_OVERRIDE{'ACS_ATTRIBUTE_VALUES'}{'ATTRIBUTE_ID'}{DATATYPE} = 'VARCHAR2';

    local (%OBJECT_TYPE_MAP, %PRIMARY_KEY_MAP, %OBJECT_TYPE_COLUMN_ATTR_MAP, 
	   %FOREIGN_KEYS, %ATTRIBUTE_STORAGE, %CONSTRAINT_NAME_MAP, %TABLE_OBJECT_TYPE_MAP,
	   %USER_TAB_COLUMNS);

    # ------------------------------------------------------------------------
    # TABLE_OBJECT_TYPE_MAP: Maps tables to object types
    # ------------------------------------------------------------------------
    # CONSTRAINT_NAME_MAP: Maps foreign key names to their table and columns
    #     'USERS_USER_ID_FK' => {
    #                              'ID' => '',
    #                              'TABLE' => 'USERS',
    #                              'COLUMN' => [
    #                                            'USER_ID'
    #                                          ]
    #                            }
    # ------------------------------------------------------------------------
    # ATTRIBUTE_STORAGE: OBJECT_TYPE => ATTRIBUTE => {TABLE => table where stored,
    #                                                 COLUMN => column where stored}
    # 'cr_item_rel' => {
    #                         'order_n' => {
    #                                        'TABLE' => 'CR_ITEM_RELS',
    #                                        'COLUMN' => 'ORDER_N'
    #                                        'ID' => ''
    #                                      }
    # ------------------------------------------------------------------------
    # FOREIGN_KEYS: Table=> { column => name of unique key }
    #      'SITE_NODES' => {
    #                        'NODE_ID' => 'ACS_OBJECTS_PK',
    #                        'OBJECT_ID' => 'ACS_OBJECTS_PK',
    #                        'PARENT_ID' => 'SITE_NODES_NODE_ID_PK'
    #                      },
    # ------------------------------------------------------------------------
    # OBJECT_TYPE_COLUMN_ATTR_MAP: Maps object types columns to their analog attributes
    #      'party' => {
    #                   'EMAIL' => {
    #                                'NAME' => 'email',
    #                                'ID' => 'model:party:email'
    #                              },
    # ------------------------------------------------------------------------
    # OBJECT_TYPE_MAP: Maps object types to their xml id's
    # <object_type> => hash
    #    TABLE_NAME => Table name for this object type
    #    ID => ID for this object type
    #      'party' => {
    #                   'TABLE_NAME' => 'parties',
    #                   'ID' => 'model:party'
    #                 }
    # ------------------------------------------------------------------------
    # PRIMARY_KEY_MAP:
    # <table name> => hash of:
    #   CONSTRAINT_NAME => name of constraint for the primary key
    #   COLUMNS => array reference of hashes for column name and id
    #    'PARTIES' => {
    #                     'COLUMNS' => [
    #                                    {
    #                                      'NAME' => 'PARTY_ID',
    #                                      'ID' => 'oracle:PARTIES:PARTY_ID'
    #                                    }
    #                                  ],
    #                     'CONSTRAINT_NAME' => 'PARTIES_PK'
    #                   }
    # ------------------------------------------------------------------------

    # open up database connection
    local $dbh = DBI->connect('dbi:Oracle:', $username, $password) 
	|| die "Couldn't connect"; 
    $dbh->{AutoCommit} = 0;
    
    # set up file names
    $xmlFileBase =~ s/\.xml$//;
    local $xmlFile = "${xmlFileBase}.xml";
    local $dtdFile = "${xmlFileBase}.dtd";

    my $xmlString = generateXML();

    my $dtdString = generateDTD();

    printToFile($xmlFile, $xmlString);
    printToFile($dtdFile, $dtdString);

    print <<"eof"
Translation complete.
  XML: $xmlFile
  DTD: $dtdFile

eof

}


sub generateXML {
    
    # Print out the xml header and a few tags
    my $xmlString = xmlHeader();
    my $metadataRootId = generateXMLId("acs");
    $xmlString .=  xmlTag("metadataroot", {id => $metadataRootId});

    my $schemaId = generateXMLId("acs", $schemaName);
    $xmlString .=  xmlTag("schema", {name=>$schemaName, id=>$schemaId});
    $xmlString .= generateTableXML();
    $xmlString .= generateForeignKeyXML();    
    $xmlString .=  xmlTagClose("schema");

    my $schemaId = generateXMLId("acs", "model");
    $xmlString .=  xmlTag("model", {name => $modelName, id => $schemaId});
    $xmlString .= generateObjectXML();
    $xmlString .= generateAssociationXML();
    $xmlString .= generateRelTypesXML();
    $xmlString .=  xmlTagClose("model");

    $xmlString .=  xmlTagClose("metadataroot");
    
    return $xmlString;
}


# Generates the object metadata stuff
sub generateObjectXML {
    my %sql;
    
    # All columns defined for each table
    my $allColumns = getAllColumns();

    $sql{objectTypes} = generateObjectTypeSql($STARTING_OBJECT_TYPE);
    $sth = $dbh->prepare($sql{objectTypes}) || die $dbh->errstr;
    $sth->execute();

    ## select out all the attributes for this object type
    $sql{attributes} = <<"eof";
select attr.*
  from acs_attributes attr
 where attr.object_type = ?
 order by attr.sort_order
eof

    $sthAttr = $dbh->prepare($sql{attributes}) || die $dbh->errstr . "\nSQL: $sql{attributes}\n";

    my ($row);
    my $xmlString = '';
    my $objTypes;
    while ($row = $sth->fetchrow_hashref) {
	my $type = $row->{OBJECT_TYPE};

	if (uc($type) eq "RELATIONSHIP" || 
	    uc($row->{SUPERTYPE}) eq "RELATIONSHIP") {
	    # Don't include rel types here. They get link attributes
	    next;
	}

	if (defined($objTypes->{$type})) {
	    die("Duplicate object type: $type\n");
	}
	$objTypes->{$type}=1;

	my $typeTableName = uc($row->{TABLE_NAME});
	$TABLE_OBJECT_TYPE_MAP{$typeTableName} = $type;

	my $id = generateXMLId($modelName, $type);
	my $string = xmlTag("objecttype", {name=>$type, id=>$id});

	# Add the supertype, if there is one
	my $supertype = $row->{SUPERTYPE};
	if ($supertype) {
	    my $idref = $OBJECT_TYPE_MAP{$supertype}{ID};
	    $string .= xmlTagSimple("supertyperef", {idref=>$idref});
	}

	my (@attributes, @attributeIdList);
	my $ctr = 0;

	if (uc($type) eq "OBJECT_TYPE") {
	    my $col = {COLUMN => 'object_type',
		       DATATYPE => 'NUMBER'};
	    $string .= attributeEntryXML($row, attributeHashFromColumn($typeTableName, $col),
					 generateXMLId($modelName, $type, $col->{COLUMN}));
	    $ctr++;
	} else {
	    $sthAttr->execute($type);
	    while ($attr = $sthAttr->fetchrow_hashref) {
		# Generate the attributes within this objecttype. Also
		# build up a list of attribute ids for the objectmap
		$string .= attributeEntryXML($row, $attr, 
					     generateXMLId($modelName, $type, $attr->{ATTRIBUTE_NAME}));
		push @attributes, $attr->{ATTRIBUTE_NAME};
		$ctr++;
	    }
	    
	    # Now get all the columns on the table that aren't ACS attributes
	    foreach $col ( getColumnsNotInAttributes($allColumns->{uc($typeTableName)}{COLUMN_LIST}, \@attributes) ) {
		$string .= attributeEntryXML($row, attributeHashFromColumn($typeTableName, $col),
					     generateXMLId($modelName, $type, $col->{COLUMN}));
		$ctr++;
	    }
	}

	# If $ctr == 0, then we have no attributes

	my $mapString = xmlTag("objectmap");

	# Add attributerefs to the objectmap
	my $columnHash = $OBJECT_TYPE_COLUMN_ATTR_MAP{$type};

	foreach $column (keys(%$columnHash)) {
	    my $attrIdRef = $columnHash->{$column}{ID};
	    my $attrName = $columnHash->{$column}{NAME};

	    my $h = $ATTRIBUTE_STORAGE{$type};
	    my $columnIdRef = $ATTRIBUTE_STORAGE{$type}{uc($attrName)}{ID};
	    die("No ID for column ($column) of object type($type)\n") unless $columnIdRef;
	    # generateXMLIdRef($schemaName, $typeTableName, $column);

	    if ($column eq "ATTR_VALUE") {
		# This is an extendedmapentry
		$mapString .= xmlTag("extendedmapentry");
		$mapString .= xmlTagSimple("attributeref", {idref=>$attrIdRef});
		$mapString .= xmlTag("extensiontable");

		# TODO: Unhardcode this
		
		my $attrValuesObjectIdRef = generateXMLIdRef($schemaName, 'ACS_ATTRIBUTE_VALUES', 'OBJECT_ID');
		my $attrValuesUniqueKeyRef = generateXMLIdRef($schemaName, 'ACS_OBJECTS_PK');
		my $attrValuesAttrNameRef = generateXMLIdRef($schemaName, 'ACS_ATTRIBUTE_VALUES', 'ATTRIBUTE_NAME');

		$mapString .= generateForeignKeyTag($attrValuesObjectIdRef, $attrValuesUniqueKeyRef);

		$mapString .= xmlTagSimple("attributecolumnref", {idref=>$attrValuesAttrNameRef});
		$mapString .= xmlTagSimple("valuecolumnref", {idref=>$columnIdRef});
		
		$mapString .= xmlTagClose("extensiontable");
		$mapString .= xmlTagClose("extendedmapentry");

	    } else {
		$mapString .= xmlTag("objectmapentry");
		$mapString .= xmlTagSimple("attributeref", {idref=>$attrIdRef});
		$mapString .= xmlTagSimple("columnref", {idref=>$columnIdRef});
		$mapString .= xmlTagClose("objectmapentry");
	    }
	}

	my $myColumns = getPrimaryKeyColumnsForTable($typeTableName);

	# We need to setup the foreign key for this object to its supertype's primarykey
	if ($supertype) {
	    my $temp = xmlTag("foreignkey");

	    my $supertypeTable = $OBJECT_TYPE_MAP{$supertype}{TABLE_NAME};
	    while (uc($supertypeTable) eq $typeTableName) {
		# Supertype has same table as object type. Use the
		# supertype's supertype
		$supertype = $OBJECT_TYPE_MAP{$supertype}{SUPERTYPE};
		$supertypeTable = $OBJECT_TYPE_MAP{$supertype}{TABLE_NAME};
		if ($supertype eq "") {
		    die("Table name conflict for object type ($type) and its supertype. Could not find a valid supertype to use\n");
		}
	    }
	    my $supertypePrimaryKey = getPrimaryKeyNameForType($supertype);
	
	    my $ctr = 0;
	    foreach $colHash (@$myColumns) {
		$temp .= xmlTagSimple("columnref", {idref=>$colHash->{ID}});
		$ctr ||= 1;
	    }
	
	    if ($ctr) {
		$temp .= xmlTagSimple("uniquekeyref", {idref=>$supertypePrimaryKey});
		$ctr = 2;
	    }
	
	    $temp .= xmlTagClose("foreignkey");
	
	    if ($ctr < 2) {
		printWarning("Foreign key for object type($type) is undefined\n");
	    } else {
		$mapString .= $temp;
	    }
	}

	# Now add the objectkey. We can lookup the table in PRIMARY_KEY_MAP
	my $temp = xmlTag("objectkey");
	my $keyCtr = 0;
	foreach $colHash (@$myColumns) {
	    my $column = $colHash->{NAME};
	    my $attributeId = $OBJECT_TYPE_COLUMN_ATTR_MAP{$type}{$column}{ID};
	    if (! xmlIdExists($attributeId)) {
		$keyCtr = 0;
		printWarning("Object key for type ($type) has unknown attribute for column($column)");
		last;
	    }
	    $temp .= xmlTagSimple("attributeref", {idref=>$attributeId});
	    $keyCtr ||= 1;
	}
	$temp .= xmlTagClose("objectkey");
	$mapString .= $temp if $keyCtr;

	# If $ctr == 0, then we have no attributes and no foreign
	# keys. This is a null objectmap entry which is not
	# allowed. Skip it 
	$mapString .= xmlTagClose("objectmap");
	if ($ctr == 0) {
	    printWarning("objectmap for object type($type) has no data. Skipping");
	} else {
	    $string .= $mapString;
	}

	$string .= xmlTagClose("objecttype");

	$OBJECT_TYPE_MAP{$type}{TABLE_NAME} = $typeTableName;
	$OBJECT_TYPE_MAP{$type}{SUPERTYPE} = $supertype;
	$OBJECT_TYPE_MAP{$type}{ID} = $id;
	$xmlString .= $string;
    }

    return $xmlString;
    
}


sub attributeHashFromColumn {
    my $tableName = shift(@_);
    my $hash = shift(@_);
    my $h;
    $h->{ATTRIBUTE_NAME} = $hash->{COLUMN};
    $h->{DATATYPE} = $hash->{DATATYPE};
    $h->{STORAGE} = "type-specific";
    $h->{TABLE_NAME} = $tableName;
    $h->{MIN_N_VALUES} = (uc($h->{NULLABLE}) eq "Y") ? 0 : 1;
    # One column has max value of 1
    $h->{MAX_N_VALUES} = 1;
    return $h;
}



# Figures out the right table and column in which this attribute is stored
sub getStorageForAttribute {
    my ($typeHash, $attrHash) = @_;

    # Specify storage
    if ($attr->{STORAGE} eq "generic") {
	return ("ACS_ATTRIBUTE_VALUES", "ATTR_VALUE");
    }

    my $column = uc($attrHash->{COLUMN_NAME} || $attrHash->{ATTRIBUTE_NAME});

    my $typeTable = uc($typeHash->{TABLE_NAME});
    my $typeExtTable = uc($typeHash->{TYPE_EXTENSION_TABLE});

    # Either in the type extension table or the type table
    my $oracleColumns = getAllColumns();

    return ($typeTable, $column) if defined($oracleColumns->{$typeTable}{$column});

    if ($typeExtTable ne "") {
	return ($typeExtTable, $column) if defined($oracleColumns->{$typeExtTable}{$column});
    }

    die("Can't find storage for $typeHash->{OBJECT_TYPE}:$attrHash->{ATTRIBUTE_NAME}\n");
}


sub attributeEntryXML {
    my $objectType = shift(@_);
    my $attr = shift(@_);
    my $attrId = shift(@_);
    my $name = $attr->{ATTRIBUTE_NAME};
    my $type = $objectType->{OBJECT_TYPE};

    die("No name for attribute for objectType ($type): ".Dumper $objectType) unless $name;

    my ($table, $column) = getStorageForAttribute($objectType, $attr);

    if ($table eq "" || $column eq "") {
	printWarning("Skipping attribute $type:$name as no table/column ($table/$column)\n");
	return '';
    }
    $ATTRIBUTE_STORAGE{$type}{uc($name)}{TABLE} = uc($table);
    $ATTRIBUTE_STORAGE{$type}{uc($name)}{COLUMN} = uc($column);
    $ATTRIBUTE_STORAGE{$type}{uc($name)}{ID} = generateXMLId($schemaName, $table, $column);

    $OBJECT_TYPE_COLUMN_ATTR_MAP{$type}{$column}{NAME} = $name;
    $OBJECT_TYPE_COLUMN_ATTR_MAP{$type}{$column}{ID} = $attrId;
    
    my $referenceName = attributeIsReference($type, $name);
    if ($referenceName) {
	# This attribute is a reference to some other row in the datamodel
	$attr->{ID} = $attrId;
	addAssociation($objectType, $attr);
	# $s = xmlTag("rolereference");
	#$s .= xmlTagSimple("name", $name);
	#$s .= xmlTagClose();
	# die("Attribute $name is a foreign key to $referenceName\n");
	# return;
    } 
    my $s = xmlTag("attribute", {id=>$attrId, name=>$name, 
				 datatype=>translateAttributeType($attr->{DATATYPE})});

    # Specify multiplicity
    my $low = $attr->{MIN_N_VALUES} || 0;
    my $upper = $attr->{MAX_N_VALUES};
    $s .= xmlTagSimple("multiplicity", {lowerbound=>$low, upperbound=>$upper});
    $s .= xmlTagClose("attribute");

    my $idref = generateXMLIdRef($schemaName, $table, $column);

    if (! xmlIdExists($idref) ) {
	printInvalidIdRef($attrId, $idref);
	return '';
    }
    
    return $s;
}


# Capitlizes the first letter of the type
sub translateAttributeType {
    my $type = lc(shift(@_));
    if (! defined($DATATYPE_MAP{$type})) {
	die("No translation defined for datatype \"$type\"\n");
    }
    return $DATATYPE_MAP{$type};
}

sub generateForeignKeyXML {

    # Oracle sucks with performance for these queries... if you join
    # to acs_object_types, the query takes 15 minutes. We just pull
    # the tables out ourselves and filter in the second query manually

    # Pull out all the tables we care about
    $sql = generateObjectTypeSql($STARTING_OBJECT_TYPE);
    $sth = $dbh->prepare($sql) || die $dbh->errstr;
    $sth->execute();

    my ($row, %tables);
    while ($row = $sth->fetchrow_hashref) {
	$tables{$row->{UPPER_TABLE_NAME}} = 1;
    }

    # Get all the foreign key references
    my $sql = <<"eof";
 select cols.table_name, cols.column_name, con.constraint_name,
        con.r_constraint_name as to_constraint_name
   from user_cons_columns cols, user_constraints con
  where cols.constraint_name = con.constraint_name
    and con.constraint_type = upper('R')
 order by lower(con.constraint_name), cols.table_name
eof

    local $sth = $dbh->prepare($sql) || die $dbh->errstr;
    $sth->execute();
    
    my %allConstraints;
    while ($constraint = $sth->fetchrow_hashref()) { 
	next unless $tables{$constraint->{TABLE_NAME}};

	# Foreignkeys have columns and a unique key
	my $name = $constraint->{CONSTRAINT_NAME};
	$allConstraints{$name}{TO_CONSTRAINT_NAME} = $constraint->{TO_CONSTRAINT_NAME};
	$allConstraints{$name}{BASE_TABLE_NAME} = $constraint->{TABLE_NAME};
	push @{$allConstraints{$name}{TABLES}}, $constraint->{TABLE_NAME};
	push @{$allConstraints{$name}{COLUMNS}}, $constraint->{COLUMN_NAME};

	$FOREIGN_KEYS{$constraint->{TABLE_NAME}}{$constraint->{COLUMN_NAME}} = $constraint->{TO_CONSTRAINT_NAME};

	$CONSTRAINT_NAME_MAP{$name}{TABLE} = $constraint->{TABLE_NAME};
	$CONSTRAINT_NAME_MAP{$name}{ID} = generateXMLIdRef($schemaName, $name);
	push @{$CONSTRAINT_NAME_MAP{$name}{COLUMNS}}, $constraint->{COLUMN_NAME};
    }

    my $ctr = 0;
    my $xmlString = '';
  CONSTRAINT: foreach $constraintName (sort (keys(%allConstraints))) {
	my $h = $allConstraints{$constraintName};
	$toConstraint = $h->{TO_CONSTRAINT_NAME};

	my $tables = $h->{TABLES};
	my $columns = $h->{COLUMNS};

	my $string = xmlTag("foreignkey");

	# Now the columns
	my $i = 0;
	for ($i = 0; $i < @$columns; $i++) {
	    my $columnId = generateXMLIdRef($schemaName, $tables->[$i], $columns->[$i]);
	    if (! xmlIdExists($columnId)) {
		xmlTagClose("foreignkey");
		printInvalidIdRef("foreignkey", $columnId);
		next CONSTRAINT;
	    }
	    $string .= xmlTagSimple("columnref", {idref=>$columnId});
	}
	if ($i == 0) {
	    xmlTagClose("foreignkey"); # close the tag to keep the counter correct
	    printWarning("Foreign key ($constraintName) has no columns that can be referenced. Skipping");
	    next CONSTRAINT;
	}

	# now the unique key
	my $uniqueKeyId = $CONSTRAINT_NAME_MAP{$toConstraint}{ID};
	if (! xmlIdExists($uniqueKeyId)) {
	    xmlTagClose("foreignkey"); # close the tag to keep the counter correct
	    # Can't have a foreign key without a unique key
	    if ($uniqueKey) {
		printWarning("Foreign key ($constraintName) references non-existent unique key($uniqueKeyId). Skipping");
	    } else {
		printWarning("Unique Key ID not found for constraint named ($constraintName). Skipping.");
	    }
	    next CONSTRAINT;
	}
	$string .= xmlTagSimple("uniquekeyref", {idref=>$uniqueKeyId});
	
	$string .= xmlTagClose("foreignkey");

	$xmlString .= $string;

	$ctr++;
	last if ($MAX_THINGS > 0 && $MAX_THINGS <= $ctr);
    }
    return $xmlString;
}


sub generateTableXML {
    # Query to pull out all object types in the hierarchy starting with
    # STARTING_OBJECT_TYPE. Also pull out all type-extension tables and
    # do them first
    $sql{object_type} = <<"eof";
select * 
  from 
(select 'ACS_OBJECT_TYPES' as table_name, 0 as sort_order from dual
 UNION ALL
 select 'ACS_ATTRIBUTE_VALUES' as table_name, 2 as sort_order from dual
 UNION ALL
 select 'ACS_RELS' as table_name, 2 as sort_order from dual
 UNION ALL
 select distinct upper(type_extension_table) as table_name, 1 as sort_order
   from acs_object_types
  where type_extension_table is not null
 UNION ALL
  select distinct(upper(t.table_name)) as table_name, 3 as sort_order
    from acs_object_types t
 connect by prior t.object_type = t.supertype
   start with t.object_type = '$STARTING_OBJECT_TYPE')
order by sort_order
eof

    $sth_object_type = $dbh->prepare($sql{object_type}) || die $dbh->errstr;
    $sth_object_type->execute();

    # Query to pull out all the columns for a given table
    $sql{columns} = <<"eof";
 select *
   from user_tab_columns 
  where table_name = upper(?)
  order by lower(column_name)
eof

    $sth_columns = $dbh->prepare($sql{columns}) || die $dbh->errstr;

    # Query to pull out all constraints of a given type for a table
    $sql{constraints} = <<"eof";
 select cols.column_name, con.constraint_name
   from user_cons_columns cols, user_constraints con
  where cols.table_name = upper(?)
    and cols.constraint_name = con.constraint_name
    and con.constraint_type = upper(?)
  order by con.constraint_name
eof
    

    local $sth_constraints = $dbh->prepare($sql{constraints}) || die $dbh->errstr;

    $ctr{table}{num} = 0;

  OBJECT_TYPE: while ($objectType = $sth_object_type->fetchrow_hashref()) {
	# objectType is a reference to data in the acs_objects table
	local $tableName = $objectType->{TABLE_NAME};
	
	my $tableId = generateXMLId($schemaName, $tableName);
	if (skip($tableId)) {
	    printSkip($tableId);
	    next OBJECT_TYPE;
	}
	my $string =  xmlTag("table", {name=>$tableName, id=>$tableId});
	
	# Reset the counters on this table
	$ctr{table}{column} = 0;
	
	if ($tableName eq "ACS_OBJECT_TYPES") {
	    my $columnName = "OBJECT_TYPE";
	    my $columnId = generateXMLId($schemaName, $tableName, $columnName);
	    $string .=  xmlTagSimple("column", {name=>$columnName, id=>$columnId, 
						datatype=>getColumnDatatype($tableName, $columnName)});
	    $ctr{table}{column}++;
	} else {
	    # Let's pull out all the columns for this table
	    $sth_columns->execute($tableName) || die $dbh->errstr;
	    while ($columns = $sth_columns->fetchrow_hashref()) {
		my $columnName = $columns->{COLUMN_NAME};
		my $datatype = getColumnDatatype($tableName, $columnName);
		if (defined($TABLE_COLUMN_OVERRIDE{$tableName}) &&
		    defined($TABLE_COLUMN_OVERRIDE{$tableName}{$columnName})) {
		    $datatype = $TABLE_COLUMN_OVERRIDE{$tableName}{$columnName}{DATATYPE};
		    $columnName = $TABLE_COLUMN_OVERRIDE{$tableName}{$columnName}{COLUMN};
		}
		my $columnId = generateXMLId($schemaName, $tableName, $columnName);
		$string .=  xmlTagSimple("column", {name=>$columnName, id=>$columnId, 
						    datatype=>$datatype});
		$ctr{table}{column}++;
		last if ($MAX_THINGS > 0 && $MAX_THINGS <= $ctr{table}{column});
	    }
	}

	if ($ctr{table}{column} == 0) {
	    # Close the tag to get the counter right
	    $string .= xmlTagClose("table");
	    printWarning("Skipping table ($tableName) as it doesn't have any columns");
	    next OBJECT_TYPE;
	}
	
	# Map from constraint name to list of columns that are unique
	$string .=  generateConstraintXML($tableName, "uniquekey", "U") unless $tableName eq "ACS_OBJECT_TYPES";
	$string .=  generateConstraintXML($tableName, "primarykey", "P");

	$string .=  xmlTagClose("table");

	$xmlString .= $string;

	$ctr{table}{num}++;
	last if ($MAX_THINGS > 0 && $MAX_THINGS <= $ctr{table}{num});

    }

    return $xmlString;

}


sub xmlHeader {
    return <<"eof";
<?xml version='1.0' encoding='utf-8'?> 
<!DOCTYPE metadataroot SYSTEM "$dtdFile">

eof
}


# Output an open xml tag with all props converted to key=value pairs
sub xmlTag {
    my $tag = shift(@_) || die("I need a tag\n");
    my $props = shift(@_) || {};
    my $noNewLine  = shift(@_) || 0;
    my $string = xmlSpaces();
    $string .= '<' . lc($tag);
    
    # name property must come first
    # id/idref properties go last
    # all other properties alphabetized

    $string .= " name=\"$props->{name}\"" if defined($props->{name});

    foreach $n (sort (keys (%$props))) {
	next if ($n eq "name" || $n eq "id" || $n eq "idref");
	my $v = $props->{$n};
	$string .= ' '.$n.'='."\"$v\"";
	$ALL_TYPES{$XML_DEPTH}{attr}{$tag}{$n}{exists} = 1;
	$ALL_TYPES{$XML_DEPTH}{attr}{$tag}{$n}{values}{$v} = 1;
    }
    $string .= " id=\"$props->{id}\"" if defined($props->{id});
    $string .= " idref=\"$props->{idref}\"" if defined($props->{idref});
    $string .= ">";
    $string .= "\n" unless $noNewLine;

    $ALL_TYPES{$XML_DEPTH}{tag}{$tag} = 1;
    if ($XML_DEPTH > 0) {
	$ALL_TYPES{$XML_DEPTH-1}{children}{$tag} = 1;
    }

    # Note this tag as a child of the last parent
    if (@ALL_PARENTS > 0) {
	my $parentTag = lastTag();
        $ALL_CHILDREN{$parentTag}{$tag} = 1;
    }

    die("Circular tag ($tag)\n") if $tag eq $ALL_PARENTS[@ALL_PARENTS-1];

    $XML_DEPTH++;
    push @ALL_PARENTS, $tag;
    
    return $string;
}


# Outputs a close xml tag
sub xmlTagClose {
    my $tag = shift(@_) || $ALL_PARENTS[@ALL_PARENTS-1];

    if ($tag ne $ALL_PARENTS[@ALL_PARENTS-1]) {
	die("Closing tag ($tag) doesn't match last open tag ($ALL_PARENTS[@ALL_PARENTS-1])\n");
    }

    my $noSpaces  = shift(@_) || 0;

    pop @ALL_PARENTS;
    $XML_DEPTH--;

    my $string = '';
    $string .= xmlSpaces() unless $noSpaces;
    $string .= "</$tag>\n";
    return $string;
}


# Outputs an open and close xml tag
sub xmlTagSimple {
    my $tag = shift(@_) || die("I need a tag\n");
    my $props = shift(@_) || {};
    my $id = '';
    if (defined($props->{id})) {
	$id = $props->{id};
	condPrint("  ID($id)" , $id);
    } elsif (defined($props->{idref})) {
	$id = $props->{idref};
	# Check if there is an idref, and if so, that the id exists
	if (! xmlIdExists($id)) {
	    printInvalidIdRef($tag, $id);
	    return '';
	} 
    }

    if (skip($id)) {
	printSkip($id);
	return '';
    }
    my $t = xmlTag($tag, $props, 1) . xmlTagClose($tag, 1);
    # Simple tags can use the xml shorthand for closing
    $t =~ s!></$tag>!/>!;
    return $t;
}

# Inserts the appropriate number of spaces for our xml file
sub xmlSpaces {
    return $SPACES x $XML_DEPTH;
}


sub generateConstraintXML {
    my (@types) = (@_);
    my $table = shift(@types);
    my $tag = shift(@types);
    return constraintHashToXML($tag, generateConstraintHash($tableName, @types));
}


# pulls out all the constraints of the specified types for the
# table. Returns a hash of:
#   <contraint name> => [list of columns that make up the constraint]
sub generateConstraintHash {
    my @types = @_;
    my $tableName = shift(@types);

    my ($type, $constraints);
    my $allConstraints = {};

    foreach $type (@types) {
	$sth_constraints->execute($tableName, $type) || die $dbh->errstr;
	# First group all columns in a constraint name
	while ($constraints = $sth_constraints->fetchrow_hashref()) {
	    my $conName = $constraints->{CONSTRAINT_NAME};
	    $allConstraints->{$conName}{TABLE_NAME} = $tableName;
	    push @{$allConstraints->{$conName}{COLUMNS}}, $constraints->{COLUMN_NAME};
	    $CONSTRAINT_NAME_MAP{$conName}{TABLE} = $tableName;
	    if (! defined($CONSTRAINT_NAME_MAP{$name}{ID})) {
		$CONSTRAINT_NAME_MAP{$name}{ID} = generateXMLId($schemaName, $conName);
	    }
	    push @{$CONSTRAINT_NAME_MAP{$conName}{COLUMNS}}, $constraints->{COLUMN_NAME};
	}
    }
    return $allConstraints;
}


# Converts the hash from generateConstraintHash to xml
sub constraintHashToXML {
    my $tag = shift(@_) || die("I need a tag\n");
    my $h = shift(@_) || die("I need a hash\n");

    my $ctr = 0;
    my $string = '';

    # Now $xmlString .=  out all the columns in each unique key
    while (($constraintName, $tableColumnHash) = each(%$h)) {
	my $tableName = $tableColumnHash->{TABLE_NAME};
	my $columns = $tableColumnHash->{COLUMNS};
	my $xmlId = $CONSTRAINT_NAME_MAP{$constraintName}{ID};
	if (!$xmlId) {
	    $xmlId = generateXMLId($schemaName, $constraintName);
	    $CONSTRAINT_NAME_MAP{$constraintName}{ID} = $xmlId;
	}
	    
	$string .= xmlTag($tag, {id=>$xmlId});
	foreach $columnName (@{$columns}) {
	    if (defined($TABLE_COLUMN_OVERRIDE{$tableName}) &&
		defined($TABLE_COLUMN_OVERRIDE{$tableName}{$columnName})) {
		$columnName = $TABLE_COLUMN_OVERRIDE{$tableName}{$columnName}{COLUMN};
	    }
	    my $columnId = generateXMLIdRef($schemaName, $tableName, $columnName);
	    $string .= xmlTagSimple("columnref", {idref=> $columnId});
	    if ($tag eq "primarykey") {
		$PRIMARY_KEY_MAP{$tableName}{CONSTRAINT_NAME} = $constraintName;
		push @{$PRIMARY_KEY_MAP{$tableName}{COLUMNS}}, {NAME=>$columnName, ID=>$columnId};
	    }
	}
	$string .= xmlTagClose($tag);
	$ctr++;
	last if ($MAX_THINGS > 0 && $MAX_THINGS <= $ctr);
    }
    return $string;
}



sub printToFile {
    my ($file, $string) = @_;
    open FILE, ">$file" || die("can't open file $file: $!\n");
    print FILE $string;
    close FILE;
}




sub generateDTD {
    my $string = dtdHeader();
    my %uniqueTags;
    for (sort {$a <=> $b} (keys(%ALL_TYPES))) {
	my $h = $ALL_TYPES{$_};
	my $tagHash = $h->{tag};
	# my $childrenHash = $h->{children};
	foreach $tag (sort(keys(%$tagHash))) {
	    next if defined($uniqueTags{$tag});
	    $uniqueTags{$tag} = 1;
	    my $childrenHash = $ALL_CHILDREN{$tag};
	    $string .= dtdEntity($tag, (sort(keys(%$childrenHash))));
	    $string .= dtdAttributes($tag, $h->{attr}{$tag});
	    $string .= "\n";
	}
    }
    return $string;
}

sub dtdHeader {
    return <<"eof";
<?xml version='1.0' encoding='us-ascii'?>

<!-- DTD for the metadata system -->

eof
}



sub dtdEntity {
    my ($tag, @acceptable) = @_;
    my $acceptString = join "|", @acceptable;
    if ($acceptString eq "") {
	$acceptString = "EMPTY";
    } else {
	$acceptString = "($acceptString)+";
    }
    return "<!ELEMENT $tag $acceptString>\n";
}



sub dtdAttributes {
    my ($tag, $h) = @_;
    return '' unless defined $h;    
    my $string = "<!ATTLIST $tag\n";
    foreach $attr (sort(keys(%$h))) {
	my $type = dtdAttributeToType($attr,$h->{$attr}{values});
	$string .= "     $attr    $type    \#REQUIRED\n";
    }
    $string .= ">\n";
    return $string;
}


sub dtdAttributeToType {
    my $type = uc(shift(@_));
    my $valueHash = shift(@_);

    return $DTD_TYPE_MAP{$type} if defined ($DTD_TYPE_MAP{$type});

    my @allValues = (keys(%$valueHash));
    # If there are more than 10 options, we do nothing
    if (@allValues < 10) {
	# Check if they are all strings (is there a number?)
	my $allStrings = 1;
	for (@allValues) {
	    if ($_ =~ /\d/) {
		$allStrings = 0;
		last;
	    }
	}
	if ($allStrings) {
	    # There are fewer than 10 valid strings. Write out the
	    # options
	    my $s = "(";
	    $s .= join " | ", @allValues;
	    $s .= ")";
	    return $s;
	}
    } 

    return "CDATA";
}


# Generates xml id's. Keeps track of id's we've generated
sub generateXMLId {
    my $id = lc(join ":", @_);
    $id =~ s/\s/\_/g;
    # die("Duplicate XML ID: $id\n") if defined($ALL_IDS{$id});
    $ALL_IDS{$id} = 1;
    return $id;
}


# Generates xml id's. 
sub generateXMLIdRef {
    return lc(join ":", @_);
}

sub xmlIdExists {
    my $id = shift(@_);
    return (defined($ALL_IDS{$id}));
}


sub printInvalidIdRef {
    my $tag = shift(@_);
    my $id = shift(@_);
    printWarning(lastTag() . ":$tag references $id which is undefined. Skipping.");
}


# Returns 1 if we're skipping this id
sub skip {
    # Not skipping anything right now.
    return 0;
 }

sub printSkip {
    my $id = shift(@_);
    my $tag = lastTag();
    print "Tag($tag): Skipping id ($id) as requested.\n";
}


sub lastTag {
    return $ALL_PARENTS[@ALL_PARENTS-1];
}


sub condPrint {
    my $string = shift(@_);
    my $arg = shift(@_) || '';
    if ( lastTag() eq "foreignkey" && $arg =~ /MIME_TYPES_PK/i ) {
	print  "$string\n";
	return 1;
    } 
    return 0;
}

sub generateObjectTypeSql {
    my $STARTING_OBJECT_TYPE = shift(@_);
    return <<"eof";
select * 
  from 
(select 'object_type' as object_type, 
        '' as supertype, 
         'ACS_OBJECT_TYPES' as table_name,
        'ACS_OBJECT_TYPES' as upper_table_name,
        'Object Type' as pretty_name,
        '' as type_extension_table,
        1 as sort_order
   from dual
 UNION ALL
 select distinct 
        t.object_type || '_type' as object_type,
        'object_type' as supertype, 
        t.type_extension_table as table_name, 
        upper(t.type_extension_table) as upper_table_name, 
        t.pretty_name || ' Type' as pretty_name,
        '' as type_extension_table,
        2 as sort_order
   from acs_object_types t
  where type_extension_table is not null
 UNION ALL
 select t.object_type, t.supertype, 
        t.table_name, upper(t.table_name) as upper_table_name, 
        t.pretty_name, t.type_extension_table, 3 as sort_order
   from acs_object_types t
connect by prior t.object_type = t.supertype
  start with t.object_type = '$STARTING_OBJECT_TYPE')
order by sort_order
eof
}



sub printWarning {
    my $string = shift(@_);
    print "*** WARNING: $string\n" unless $QUIET;
}


# Returns the constraint name for the primary key for the specified object type
sub getPrimaryKeyNameForType {
    my $type = shift(@_);
    my $tableName = $OBJECT_TYPE_MAP{$type}{TABLE_NAME};
    if ($tableName eq "") {
	die("No table for object type \"$type\"\n");
    }
    return generateXMLIdRef($schemaName,$PRIMARY_KEY_MAP{$tableName}{CONSTRAINT_NAME});
}

# Returns a list of hashes representing columns in the primary key of
# the specified object type
sub getPrimaryKeyColumnsForTable {
    my $tableName = uc(shift(@_));
    return $PRIMARY_KEY_MAP{$tableName}{COLUMNS} || [];
}


# Need this because oracle sucks
sub getAllColumns {
    if ((keys (%USER_TAB_COLUMNS))>0) {
	return \%USER_TAB_COLUMNS;
    }
    ## select out all the columns for this object type that aren't
    ## represented as attributes
    $sql = "select table_name, lower(column_name) as column_name, data_type, nullable from user_tab_columns";
    $sth = $dbh->prepare($sql) || die $dbh->errstr;
    $sth->execute();
    while (my $h = $sth->fetchrow_hashref) {
	my $columnHash = {COLUMN           => uc($h->{COLUMN_NAME}),
			  ORACLE_DATATYPE  => $h->{DATA_TYPE},
			  NULLABLE         => $h->{NULLABLE},
			  DATATYPE         => translateOracleDatatype($h->{DATA_TYPE})};

	push @{$USER_TAB_COLUMNS{uc($h->{TABLE_NAME})}{COLUMN_LIST}}, $columnHash;
	$USER_TAB_COLUMNS{$h->{TABLE_NAME}}{uc($h->{COLUMN_NAME})} = $columnHash;
    }
    return \%USER_TAB_COLUMNS;
}

sub getColumnDatatype {
    my ($tableName, $columnName) = @_;
    my $cols = getAllColumns();
    if (defined($cols->{uc($tableName)}{uc($columnName)})) {
	my $t = $cols->{uc($tableName)}{uc($columnName)}{ORACLE_DATATYPE};
	return "VARCHAR" if $t =~ /^VARCHAR/;
	return "BIGINT" if $t eq "NUMBER";
	return "BLOB" if $t eq "RAW";
	return "VARCHAR" if $t eq "ROWID";
	return $t;
    }
    die("Can't find datatype for $tableName:$columnName\n");
}

sub translateOracleDatatype {
    my $type = uc(shift(@_));
    return "Date" if $type eq "DATE";
    return "Number" if $type eq "NUMBER";
    return "String";
}

sub getColumnsNotInAttributes {
    my $tableColumns = shift(@_);
    my $tableAttributes = shift(@_);
    my (%attr, @cols);
    for (@$tableAttributes) {
	$attr{lc($_)} = 1;
    }
    foreach $colHash (@$tableColumns) {
	push @cols, $colHash unless $attr{lc($colHash->{COLUMN})};
    }
    return @cols;
}


# Returns name of the unique key this attribute reference, if this
# attribute references another row. 0 otherwise
sub attributeIsReference {
    my ($type, $attr) = @_;
    my $table = $ATTRIBUTE_STORAGE{$type}{uc($attr)}{TABLE};
    my $column = $ATTRIBUTE_STORAGE{$type}{uc($attr)}{COLUMN};
    # die("Don't know the table and column for $type:$attr\n") unless $table && $column;
    if (defined ($FOREIGN_KEYS{$table}{$column})) {
	return $FOREIGN_KEYS{$table}{$column};
    }
    return 0;
}


sub addAssociation {
    my ($objectTypeHash, $attrHash) = @_;
    push @ASSOCIATIONS, {OBJECT_TYPE => $objectTypeHash,
			 ATTRIBUTE => $attrHash,
		     };
}

sub generateAssociationXML {
    my ($assoc);
    my $s = '';

  OUTER: foreach $assoc (@ASSOCIATIONS) {
	my $objHash = $assoc->{OBJECT_TYPE};
	my $objectType = $objHash->{OBJECT_TYPE};
	my $objectTypeName = $objHash->{PRETTY_NAME};
	my $objectTypeId = $OBJECT_TYPE_MAP{$objectType}{ID};

	my $attrHash = $assoc->{ATTRIBUTE};
	my $attrName = $attrHash->{ATTRIBUTE_NAME};

	my $refName = attributeIsReference($objectType, $attrName);
	if (!defined($CONSTRAINT_NAME_MAP{$refName}{TABLE})) {
	    # This constraint must come from a table outside of the
	    # acs_object_types table... 
	    next OUTER;
	}

	my $toTable = $CONSTRAINT_NAME_MAP{$refName}{TABLE};
	my $toColumns = $CONSTRAINT_NAME_MAP{$refName}{COLUMNS};

	my $toObjectType = $TABLE_OBJECT_TYPE_MAP{$toTable};
	my $toObjectTypeId = $OBJECT_TYPE_MAP{$toObjectType}{ID};
	next if ($toObjectTypeId eq "");
	my $objectTypeColumnAttrMap = $OBJECT_TYPE_COLUMN_ATTR_MAP{$toObjectType};

	my $assocName = "$objectTypeName $attrName";
	my $assocId = generateXMLId($modelName, $assocName);	
	$s .= xmlTag("association", { name => $assocName, id => $assocId });

	my $roleIdOne = generateXMLId($assocId, $attrName);
	my $roleIdTwo = generateXMLId($assocId, "object");

	die("Empty role id\n") unless $roleIdTwo;

	$s .= generateAssociationRole( { name => $attrName, 
					 id => $roleIdOne,
					 objecttyperef => $toObjectTypeId,
					 lowerbound => 0,
					 upperbound => "" });

	$s .= generateAssociationRole( { name => "object", 
					 id => $roleIdTwo,
					 objecttyperef => $objectTypeId,
					 lowerbound => 0,
					 upperbound => 1 });
	
	# Now set up the foreign key
	my $columnRef = generateXMLIdRef($schemaName, 
					 $ATTRIBUTE_STORAGE{$objectType}{uc($attrName)}{TABLE},
					 $ATTRIBUTE_STORAGE{$objectType}{uc($attrName)}{COLUMN});
	my $refNameId = $CONSTRAINT_NAME_MAP{$refName}{ID};

	$s .= xmlTag("associationmap");
	$s .= generateForeignKeyTag($columnRef, $refNameId);
	$s .= xmlTagClose("associationmap");	

	$s .= xmlTagClose("association");
	
	$s .= generateRoleReference($objectTypeId, $roleIdOne);
	
    }
    return $s;

}


sub generateRelTypesXML {
    my $sql = <<"eof";
select t.rel_type, 
       t.object_type_one, t.role_one, t.min_n_rels_one, t.max_n_rels_one, t.role_one,
       t.object_type_two, t.role_two, t.min_n_rels_two, t.max_n_rels_two, t.role_two,
       o.object_type, o.supertype, o.pretty_name, level as sort_order,
       o.table_name
  from acs_rel_types t, 
      (select o.object_type, o.supertype, o.pretty_name, o.table_name, level as sort_order
         from acs_object_types o
      connect by prior o.object_type = o.supertype
        start with o.object_type ='$STARTING_OBJECT_TYPE') o
 where o.object_type = t.rel_type
 order by o.sort_order, lower(t.rel_type)
eof

    my $sth = $dbh->prepare($sql) || die $dbh->errstr;
    $sth->execute();
    my $s = '';
    while (my $h = $sth->fetchrow_hashref) {
	
	my $relTypeTable = uc($h->{TABLE_NAME});
	my $assocName = "$h->{PRETTY_NAME}";
	my $assocId = generateXMLId($modelName, $assocName); 

	my $roleTypeOne = $h->{OBJECT_TYPE_ONE};
	my $roleTypeOneRef = $OBJECT_TYPE_MAP{$roleTypeOne}{ID} || 
	    die("Can't find idref for object type($roleTypeOne)\n");
	my $roleNameOne = $h->{ROLE_ONE};
	$roleNameOne = $h->{OBJECT_TYPE_ONE} unless $roleNameTwo;
	my $roleIdOne = generateXMLId($assocId, $roleNameOne);

	my $roleTypeTwo = $h->{OBJECT_TYPE_TWO};
	my $roleTypeTwoRef = $OBJECT_TYPE_MAP{$roleTypeTwo}{ID} || 
	    die("Can't find idref for object type($roleTypeOne)\n");
	my $roleNameTwo = $h->{ROLE_ONE};
	$roleNameTwo = $h->{OBJECT_TYPE_TWO} unless $roleNameTwo;
	if ($roleNameTwo eq $roleNameOne) {
	    printWarning("role names equal ($roleNameOne). Appending 2 to second name");
	    $roleNameTwo .= "2";
	}
	my $roleIdTwo = generateXMLId($assocId, $roleNameTwo);


	$s .= xmlTag("association", { name => $assocName, id => $assocId });

	# linkattributes haves name and attribute datatype
	my %linkAttributes;

	# The relati
	# Get all the string columns of this rel_type. 
	my $allColumns = getAllColumns();
	my $relTypeColumns = $allColumns->{$relTypeTable};
	
	while (($column, $h) = each(%$relTypeColumns)) {
	    next unless (ref($h) eq "HASH");
	    next if uc($h->{DATATYPE}) ne "STRING" || uc($column) eq "REL_TYPE";
	    # Now we have a link attribute
	    $column = lc($column);
	    my $linkId = generateXMLId($assocName, $column);
	    $linkAttributes{$column} = $linkId;
	    $s .= xmlTagSimple("linkattribute", { name => $column, 
						  id => $linkId,
						  datatype => 'String'});
	}

	$s .= generateAssociationRole( { name => $roleNameOne, 
					 id => $roleIdOne, 
					 objecttyperef => $roleTypeOneRef,
					 lowerbound => $h->{MIN_N_RELS_ONE},
					 upperbound => $h->{MAX_N_RELS_ONE},
				     });

	$s .= generateAssociationRole( { name => $roleNameTwo, 
					 id => $roleIdTwo, 
					 objecttyperef => $roleTypeTwoRef,
					 lowerbound => $h->{MIN_N_RELS_TWO},
					 upperbound => $h->{MAX_N_RELS_TWO},
				     });

	# Now set up the foreign key
	# for the persons <-> group example, we have two foreign keys:
	#   acs_rels.object_id_one = groups.group_id
	#   acs_rels.object_id_two = persons.person_id

	my $acs_rels_rel_id_one_ref = generateXMLIdRef($schemaName, "ACS_RELS", "OBJECT_ID_ONE");
	my $acs_rels_rel_id_two_ref = generateXMLIdRef($schemaName, "ACS_RELS", "OBJECT_ID_TWO");
	my $object_id_one_pk = $PRIMARY_KEY_MAP{$OBJECT_TYPE_MAP{$roleTypeOne}{TABLE_NAME}}{CONSTRAINT_NAME};
	my $object_id_two_pk = $PRIMARY_KEY_MAP{$OBJECT_TYPE_MAP{$roleTypeTwo}{TABLE_NAME}}{CONSTRAINT_NAME};

        $s .= xmlTag("associationmap");

	# now do mapentry for any link attributes
	while (($attr, $attrId) = each(%linkAttributes)) {
	    my $columnIdRef = generateXMLIdRef($schemaName, $relTypeTable, $attr);
	    $s .= xmlTag("associationmapentry");
	    $s .= xmlTagSimple("linkattributeref", { idref => $attrId });
	    $s .= xmlTagSimple("columnref", { idref => $columnIdRef });
	    $s .= xmlTagClose("associationmapentry");
	}

	$s .= generateForeignKeyTag($acs_rels_rel_id_one_ref, $CONSTRAINT_NAME_MAP{$object_id_one_pk}{ID});
	$s .= generateForeignKeyTag($acs_rels_rel_id_two_ref, $CONSTRAINT_NAME_MAP{$object_id_two_pk}{ID});	

	$s .= xmlTagClose("associationmap");

	$s .= xmlTagClose("association");
	
	$s .= generateRoleReference($roleTypeOneRef, $roleIdTwo);
	$s .= generateRoleReference($roleTypeTwoRef, $roleIdOne);

    }
    return $s;

}


sub generateForeignKeyTag {
    my ($colRef, $keyRef) = @_;
    if (! xmlIdExists($colRef) ) {
	printWarning("Reference to column ($colRef) is not defined. Foreign key will not be generated");
	return '';
    }
    if (! xmlIdExists($keyRef) ) {
	printWarning("Reference to unique key ($keyRef) is not defined. Unique key will not be generated");
	return '';
    }

    my $s = xmlTag("foreignkey");
    $s .= xmlTagSimple("columnref", {idref => $colRef});
    $s .= xmlTagSimple("uniquekeyref", {idref => $keyRef});
    $s .= xmlTagClose("foreignkey");

    return $s;
}



sub generateAssociationRole {
    my $h = shift(@_);
    my $s = xmlTag("associationrole", { name => $h->{name},
					id => $h->{id} });
    
    $s .= xmlTagSimple("objecttyperef", { idref => $h->{objecttyperef} });
    $s .= xmlTagSimple("multiplicity", { lowerbound => $h->{lowerbound},
					 upperbound => $h->{upperbound} });
    $s .= xmlTagClose("associationrole");

    return $s;
}


sub generateRoleReference {
    my ($objectTypeRef, $roleRef) = @_;
    my $s = xmlTag("objecttype", { id => $objectTypeRef });
    $s .= xmlTag("rolereference");
    $s .= xmlTagSimple("associationroleref", {idref => $roleRef});
    $s .= xmlTagClose();
    $s .= xmlTagClose();
    return $s;
}


sub defineConstraint {
    my $constraintName = shift(@_);
    my $sql = "select table_name from user_constraints where constraint_name = ?";
    $sth = $dbh->prepare($sql) || die $dbh->errstr;
    $sth->execute($constraintName);
    my $tableName = $sth->fetchrow;

    # Query to pull out all constraints of a given type for a table
    $sql = <<"eof";
 select cols.column_name, con.constraint_name
   from user_cons_columns cols, user_constraints con
  where cols.table_name = upper(?)
    and cols.constraint_name = con.constraint_name
    and con.constraint_type in ('U','R','P')
  order by con.constraint_name
eof
    
    my $sth_constraints = $dbh->prepare($sql) || die $dbh->errstr;
    $sth_constraints->execute($tableName);
    while ($row = $sth_constraints->fetchrow_hashref) {
	my $name = $row->{CONSTRAINT_NAME};
	$CONSTRAINT_NAME_MAP{$name}{TABLE} = $tableName;
	$CONSTRAINT_NAME_MAP{$name}{ID} = generateXMLId($schemaName, $name);
	push @{$CONSTRAINT_NAME_MAP{$name}{COLUMN}}, $row->{COLUMN_NAME};
    }
    
}
