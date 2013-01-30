--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: upgrade-mime-types.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.writer', 'OpenOffice Writer', 'sxw', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.writer.template', 'OpenOffice Writer Template', 'stw', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.calc', 'OpenOffice SpreadSheets', 'sxc', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.calc.template', 'OpenOffice SpreadSheets Template', 'stc', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.draw', 'OpenOffice Draw', 'sxd', 'com.arsdigita.mimetypes.MimeType', 'com.arsdigita.cms.MimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.draw.template', 'OpenOffice Draw Template', 'std', 'com.arsdigita.mimetypes.MimeType', 'com.arsdigita.cms.MimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.impress', 'OpenOffice Impress', 'sxi', 'com.arsdigita.mimetypes.MimeType', 'com.arsdigita.cms.MimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.impress.template', 'OpenOffice Impress Template', 'sti', 'com.arsdigita.mimetypes.MimeType', 'com.arsdigita.cms.MimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.writer.global', 'OpenOffice Writer Global', 'sxg', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.math', 'OpenOffice Math', 'sxm', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');


insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.writer', 'sxw');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.writer.template', 'stw');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.calc', 'sxc');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.calc.template', 'stc');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.draw', 'sxd');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.draw.template', 'std');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.impress', 'sxi');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.impress.template', 'sti');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.writer.global', 'sxg');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.math', 'sxm');
