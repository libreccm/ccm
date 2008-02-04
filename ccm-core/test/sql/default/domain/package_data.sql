--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: package_data.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


-- Test data for package test cases.
-- Generated from JDepend run on a version of persistence subsystem.
-- Would probably be better to put in a data file & load via sqlldr.

-- Create packages
insert into t_package values (1, 'com.arsdigita.persistence');
insert into t_package values (2, 'com.arsdigita.persistence.metadata');
insert into t_package values (3, 'com.arsdigita.persistence.pdl');
insert into t_package values (4, 'com.arsdigita.persistence.pdl.ast');
insert into t_package values (5, 'com.arsdigita.db');
insert into t_package values (6, 'com.arsdigita.initializer');
insert into t_package values (7, 'org.apache.log4j');
insert into t_package values (8, 'org.apache.xerces.dom');
insert into t_package values (9, 'org.apache.xerces.framework');
insert into t_package values (10, 'org.w3c.dom');
insert into t_package values (11, 'org.xml.sax');

-- Classes for com.arsdigita.persistence
insert into t_class values (1, 1, 'AbstractDataOperation', 1);
insert into t_class values (2, 1, 'DataAssociation', 1);
insert into t_class values (3, 1, 'DataCollection', 1);
insert into t_class values (4, 1, 'DataObject', 1);
insert into t_class values (5, 1, 'DataQuery', 1);
insert into t_class values (6, 1, 'EventBuilder', 1);
insert into t_class values (7, 1, 'SQLEnvironment', 1);
insert into t_class values (8, 1, 'SQLOperation', 1);
insert into t_class values (9, 1, 'SQLQuery', 1);

insert into t_class values (10, 1, 'AttributeTypes', 0);
insert into t_class values (11, 1, 'DataAssociationImpl', 0);
insert into t_class values (12, 1, 'DataCollectionImpl', 0);
insert into t_class values (13, 1, 'DataContainer', 0);
insert into t_class values (14, 1, 'DataOperation', 0);
insert into t_class values (15, 1, 'DataQueryImpl', 0);
insert into t_class values (16, 1, 'DataStore', 0);
insert into t_class values (17, 1, 'Event', 0);
insert into t_class values (18, 1, 'Filter', 0);
insert into t_class values (19, 1, 'GenericDataObject', 0);
insert into t_class values (20, 1, 'GenericDataObjectFactory', 0);
insert into t_class values (21, 1, 'GenericDataQuery', 0);
insert into t_class values (22, 1, 'Initializer', 0);
insert into t_class values (23, 1, 'Link', 0);
insert into t_class values (24, 1, 'OID', 0);
insert into t_class values (25, 1, 'PersistenceException', 0);
insert into t_class values (26, 1, 'SQL', 0);
insert into t_class values (27, 1, 'SQLBinder', 0);
insert into t_class values (28, 1, 'SQLSource', 0);
insert into t_class values (29, 1, 'SQLTarget', 0);
insert into t_class values (30, 1, 'SQLToken', 0);
insert into t_class values (31, 1, 'Session', 0);
insert into t_class values (32, 1, 'SessionManager', 0);
insert into t_class values (33, 1, 'StaticEventBuilder', 0);
insert into t_class values (34, 1, 'StaticSQLOp', 0);
insert into t_class values (35, 1, 'StaticSQLQuery', 0);
insert into t_class values (36, 1, 'StaticSQLUpdateOp', 0);
insert into t_class values (37, 1, 'TransactionContext', 0);

-- Classes for com.arsdigita.persistence.metadata
insert into t_class values (38, 2, 'AbstractEvent', 1);
insert into t_class values (39, 2, 'Key', 1);
insert into t_class values (40, 2, 'MetadataElement', 1);
insert into t_class values (41, 2, 'Nameable', 1);
insert into t_class values (42, 2, 'PropertyType', 1);
insert into t_class values (43, 2, 'Association', 0);
insert into t_class values (44, 2, 'AssociationMap', 0);
insert into t_class values (45, 2, 'AssociationRole', 0);
insert into t_class values (46, 2, 'Attribute', 0);
insert into t_class values (47, 2, 'AttributeDatatype', 0);
insert into t_class values (48, 2, 'Column', 0);
insert into t_class values (49, 2, 'ColumnDatatype', 0);
insert into t_class values (50, 2, 'DOMGenerator', 0);
insert into t_class values (51, 2, 'DOMParser', 0);
insert into t_class values (52, 2, 'ExtendedMapEntry', 0);
insert into t_class values (53, 2, 'ExtensionTable', 0);
insert into t_class values (54, 2, 'ForeignKey', 0);
insert into t_class values (55, 2, 'LinkAttribute', 0);
insert into t_class values (56, 2, 'Loader', 0);
insert into t_class values (57, 2, 'LoaderUtility', 0);
insert into t_class values (58, 2, 'MappingException', 0);
insert into t_class values (59, 2, 'MetadataException', 0);
insert into t_class values (60, 2, 'MetadataRoot', 0);
insert into t_class values (61, 2, 'Model', 0);
insert into t_class values (62, 2, 'ModelException', 0);
insert into t_class values (63, 2, 'MultiHashMap', 0);
insert into t_class values (64, 2, 'MultiValueMapEntry', 0);
insert into t_class values (65, 2, 'Multiplicity', 0);
insert into t_class values (66, 2, 'NamedSQLEvent', 0);
insert into t_class values (67, 2, 'ObjectEvent', 0);
insert into t_class values (68, 2, 'ObjectKey', 0);
insert into t_class values (69, 2, 'ObjectMap', 0);
insert into t_class values (70, 2, 'ObjectType', 0);
insert into t_class values (71, 2, 'PropertyEvent', 0);
insert into t_class values (72, 2, 'Restructure', 0);
insert into t_class values (73, 2, 'RoleReference', 0);
insert into t_class values (74, 2, 'SQLBlock', 0);
insert into t_class values (75, 2, 'Schema', 0);
insert into t_class values (76, 2, 'SchemaException', 0);
insert into t_class values (77, 2, 'Table', 0);
insert into t_class values (78, 2, 'TagInfo', 0);
insert into t_class values (79, 2, 'Timer', 0);
insert into t_class values (80, 2, 'UniqueKey', 0);
insert into t_class values (81, 2, 'Utilities', 0);
insert into t_class values (82, 2, 'Variables', 0);
insert into t_class values (83, 2, 'XMLBuildMain', 0);
insert into t_class values (84, 2, 'XMLException', 0);
insert into t_class values (85, 2, 'XMLInstantiator', 0);
insert into t_class values (86, 2, 'XMLUtilities', 0);

-- Classes for com.arsdigita.persistence.pdl
insert into t_class values (87, 3, 'PDL', 0);
insert into t_class values (88, 3, 'PDLException', 0);

-- Classes for com.arsdigita.persistence.pdl.ast
insert into t_class values (89, 4, 'Element', 1);
insert into t_class values (90, 4, 'MapStatement', 1);
insert into t_class values (91, 4, 'NamedSQLDef', 1);

insert into t_class values (92, 4, 'AST', 0);
insert into t_class values (93, 4, 'AssociationDef', 0);
insert into t_class values (94, 4, 'BindingDef', 0);
insert into t_class values (95, 4, 'ColumnDef', 0);
insert into t_class values (96, 4, 'DMLDef', 0);
insert into t_class values (97, 4, 'DataTypeDef', 0);
insert into t_class values (98, 4, 'EventDef', 0);
insert into t_class values (99, 4, 'FlexFieldDef', 0);
insert into t_class values (100, 4, 'Identifier', 0);
insert into t_class values (101, 4, 'MappingDef', 0);
insert into t_class values (102, 4, 'ModelDef', 0);
insert into t_class values (103, 4, 'MultiplicityDef', 0);
insert into t_class values (104, 4, 'ObjectDef', 0);
insert into t_class values (105, 4, 'ObjectKeyDef', 0);
insert into t_class values (106, 4, 'PropertyDef', 0);
insert into t_class values (107, 4, 'QueryDef', 0);
insert into t_class values (108, 4, 'SQLBlockDef', 0);

-- Dependencies for com.arsdigita.persistence
insert into t_package_depends_on values( 1, 5 );
insert into t_package_depends_on values( 1, 6 );
insert into t_package_depends_on values( 1, 2 );
insert into t_package_depends_on values( 1, 7 );

insert into t_package_used_by values( 1, 2 );

-- Dependencies for com.arsdigita.persistence.metadata
insert into t_package_depends_on values( 2, 1 );
insert into t_package_depends_on values( 2, 8 );
insert into t_package_depends_on values( 2, 9 );
insert into t_package_depends_on values( 2, 10 );
insert into t_package_depends_on values( 2, 11 );

insert into t_package_used_by values( 2, 1 );
insert into t_package_used_by values( 2, 3 );
insert into t_package_used_by values( 2, 4 );

-- Dependencies for com.arsdigita.persistence.pdl
insert into t_package_depends_on values( 3, 2 );
insert into t_package_depends_on values( 3, 4 );

insert into t_package_used_by values( 3, 4 );

-- Dependencies for com.arsdigita.persistence.pdl.ast
insert into t_package_depends_on values( 4, 2 );
insert into t_package_depends_on values( 4, 3 );

insert into t_package_used_by values( 4, 3 );
