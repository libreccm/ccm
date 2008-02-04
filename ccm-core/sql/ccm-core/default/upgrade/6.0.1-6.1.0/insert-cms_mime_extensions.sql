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
-- $Id: insert-cms_mime_extensions.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
insert into cms_mime_extensions (file_extension, mime_type) select 'bin', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'bin');
insert into cms_mime_extensions (file_extension, mime_type) select 'uu', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'uu');
insert into cms_mime_extensions (file_extension, mime_type) select 'saveme', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'saveme');
insert into cms_mime_extensions (file_extension, mime_type) select 'dump', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'dump');
insert into cms_mime_extensions (file_extension, mime_type) select 'hqx', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'hqx');
insert into cms_mime_extensions (file_extension, mime_type) select 'arc', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'arc');
insert into cms_mime_extensions (file_extension, mime_type) select 'o', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'o');
insert into cms_mime_extensions (file_extension, mime_type) select 'a', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'a');
insert into cms_mime_extensions (file_extension, mime_type) select 'exe', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'exe');
insert into cms_mime_extensions (file_extension, mime_type) select 'z', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'z');
insert into cms_mime_extensions (file_extension, mime_type) select 'jar', 'application/octet-stream' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'jar');
insert into cms_mime_extensions (file_extension, mime_type) select 'dvi', 'application/x-dvi' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'dvi');
insert into cms_mime_extensions (file_extension, mime_type) select 'latex', 'application/x-latex' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'latex');
insert into cms_mime_extensions (file_extension, mime_type) select 'tex', 'application/x-tex' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'tex');
insert into cms_mime_extensions (file_extension, mime_type) select 'texinfo', 'application/x-texinfo' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'texinfo');
insert into cms_mime_extensions (file_extension, mime_type) select 'texi', 'application/x-texinfo' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'texi');
insert into cms_mime_extensions (file_extension, mime_type) select 'tr', 'application/x-troff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'tr');
insert into cms_mime_extensions (file_extension, mime_type) select 't', 'application/x-troff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 't');
insert into cms_mime_extensions (file_extension, mime_type) select 'troff', 'application/x-troff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'troff');
insert into cms_mime_extensions (file_extension, mime_type) select 'man', 'application/x-troff-man' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'man');
insert into cms_mime_extensions (file_extension, mime_type) select 'me', 'application/x-troff-me' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'me');
insert into cms_mime_extensions (file_extension, mime_type) select 'ms', 'application/x-troff-ms' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'ms');
insert into cms_mime_extensions (file_extension, mime_type) select 'src', 'application/x-wais-source' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'src');
insert into cms_mime_extensions (file_extension, mime_type) select 'wsrc', 'application/x-wais-source' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'wsrc');
insert into cms_mime_extensions (file_extension, mime_type) select 'zip', 'application/zip' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'zip');
insert into cms_mime_extensions (file_extension, mime_type) select 'gz', 'application/gzip' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'gz');
insert into cms_mime_extensions (file_extension, mime_type) select 'tar', 'application/x-tar' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'tar');
insert into cms_mime_extensions (file_extension, mime_type) select 'snd', 'audio/basic' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'snd');
insert into cms_mime_extensions (file_extension, mime_type) select 'au', 'audio/basic' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'au');
insert into cms_mime_extensions (file_extension, mime_type) select 'sound', 'audio/x-aiff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'sound');
insert into cms_mime_extensions (file_extension, mime_type) select 'aifc', 'audio/x-aiff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'aifc');
insert into cms_mime_extensions (file_extension, mime_type) select 'aif', 'audio/x-aiff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'aif');
insert into cms_mime_extensions (file_extension, mime_type) select 'aiff', 'audio/x-aiff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'aiff');
insert into cms_mime_extensions (file_extension, mime_type) select 'wav', 'audio/x-wav' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'wav');
insert into cms_mime_extensions (file_extension, mime_type) select 'mpg', 'video/mpeg' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'mpg');
insert into cms_mime_extensions (file_extension, mime_type) select 'mpe', 'video/mpeg' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'mpe');
insert into cms_mime_extensions (file_extension, mime_type) select 'mov', 'video/quicktime' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'mov');
insert into cms_mime_extensions (file_extension, mime_type) select 'qt', 'video/quicktime' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'qt');
insert into cms_mime_extensions (file_extension, mime_type) select 'avi', 'application/x-msvideo' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'avi');
insert into cms_mime_extensions (file_extension, mime_type) select 'mv', 'video/x-sgi-movie' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'mv');
insert into cms_mime_extensions (file_extension, mime_type) select 'movie', 'video/x-sgi-movie' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'movie');
insert into cms_mime_extensions (file_extension, mime_type) select 'vsd', 'application/vnd.visio' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'vsd');
insert into cms_mime_extensions (file_extension, mime_type) select 'vst', 'application/vnd.visio' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'vst');
insert into cms_mime_extensions (file_extension, mime_type) select 'vsw', 'application/vnd.visio' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'vsw');
insert into cms_mime_extensions (file_extension, mime_type) select 'vss', 'application/vnd.visio' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'vss');
insert into cms_mime_extensions (file_extension, mime_type) select 'pdf', 'application/pdf' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'pdf');
insert into cms_mime_extensions (file_extension, mime_type) select 'doc', 'application/msword' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'doc');
insert into cms_mime_extensions (file_extension, mime_type) select 'mdb', 'application/msaccess' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'mdb');
insert into cms_mime_extensions (file_extension, mime_type) select 'xls', 'application/msexcel' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'xls');
insert into cms_mime_extensions (file_extension, mime_type) select 'ppt', 'application/ms-powerpoint' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'ppt');
insert into cms_mime_extensions (file_extension, mime_type) select 'pps', 'application/ms-powerpoint' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'pps');
insert into cms_mime_extensions (file_extension, mime_type) select 'pot', 'application/ms-powerpoint' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'pot');
insert into cms_mime_extensions (file_extension, mime_type) select 'ps', 'application/postscript' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'ps');
insert into cms_mime_extensions (file_extension, mime_type) select 'eps', 'application/postscript' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'eps');
insert into cms_mime_extensions (file_extension, mime_type) select 'ai', 'application/postscript' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'ai');
insert into cms_mime_extensions (file_extension, mime_type) select 'rtf', 'application/rtf' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'rtf');
insert into cms_mime_extensions (file_extension, mime_type) select 'xml', 'text/xml' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'xml');
insert into cms_mime_extensions (file_extension, mime_type) select 'xsl', 'text/xml' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'xsl');
insert into cms_mime_extensions (file_extension, mime_type) select 'xsd', 'text/xml' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'xsd');
insert into cms_mime_extensions (file_extension, mime_type) select 'html', 'text/html' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'html');
insert into cms_mime_extensions (file_extension, mime_type) select 'htm', 'text/html' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'htm');
insert into cms_mime_extensions (file_extension, mime_type) select 'css', 'text/css' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'css');
insert into cms_mime_extensions (file_extension, mime_type) select 'txt', 'text/plain' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'txt');
insert into cms_mime_extensions (file_extension, mime_type) select 'text', 'text/plain' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'text');
insert into cms_mime_extensions (file_extension, mime_type) select 'c', 'text/plain' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'c');
insert into cms_mime_extensions (file_extension, mime_type) select 'cc', 'text/plain' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'cc');
insert into cms_mime_extensions (file_extension, mime_type) select 'c++', 'text/plain' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'c++');
insert into cms_mime_extensions (file_extension, mime_type) select 'h', 'text/plain' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'h');
insert into cms_mime_extensions (file_extension, mime_type) select 'pl', 'text/plain' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'pl');
insert into cms_mime_extensions (file_extension, mime_type) select 'java', 'text/plain' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'java');
insert into cms_mime_extensions (file_extension, mime_type) select 'sgm', 'text/sgml' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'sgm');
insert into cms_mime_extensions (file_extension, mime_type) select 'sgml', 'text/sgml' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'sgml');
insert into cms_mime_extensions (file_extension, mime_type) select 'jsp', 'text/x-jsp' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'jsp');
insert into cms_mime_extensions (file_extension, mime_type) select 'gif', 'image/gif' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'gif');
insert into cms_mime_extensions (file_extension, mime_type) select 'ief', 'image/ief' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'ief');
insert into cms_mime_extensions (file_extension, mime_type) select 'jpg', 'image/jpeg' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'jpg');
insert into cms_mime_extensions (file_extension, mime_type) select 'jpe', 'image/jpeg' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'jpe');
insert into cms_mime_extensions (file_extension, mime_type) select 'jpeg', 'image/jpeg' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'jpeg');
insert into cms_mime_extensions (file_extension, mime_type) select 'tif', 'image/tiff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'tif');
insert into cms_mime_extensions (file_extension, mime_type) select 'tiff', 'image/tiff' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'tiff');
insert into cms_mime_extensions (file_extension, mime_type) select 'ras', 'image/x-cmu-rast' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'ras');
insert into cms_mime_extensions (file_extension, mime_type) select 'pnm', 'image/x-portable-anymap' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'pnm');
insert into cms_mime_extensions (file_extension, mime_type) select 'pbm', 'image/x-portable-bitmap' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'pbm');
insert into cms_mime_extensions (file_extension, mime_type) select 'pgm', 'image/x-portable-graymap' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'pgm');
insert into cms_mime_extensions (file_extension, mime_type) select 'ppm', 'image/x-portable-pixmap' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'ppm');
insert into cms_mime_extensions (file_extension, mime_type) select 'rgb', 'image/x-rgb' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'rgb');
insert into cms_mime_extensions (file_extension, mime_type) select 'xbm', 'image/x-xbitmap' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'xbm');
insert into cms_mime_extensions (file_extension, mime_type) select 'xwd', 'image/x-xwindowdump' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'xwd');
insert into cms_mime_extensions (file_extension, mime_type) select 'bmp', 'image/bmp' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'bmp');
insert into cms_mime_extensions (file_extension, mime_type) select 'png', 'image/png' from dual where not exists (select 1 from cms_mime_extensions where file_extension = 'png');

