package com.arsdigita.docmgr;

/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */


/**
 * TestRepository
 *
 */
public class TestRepository {
    static Repository s_repository = null;

    public static Repository get() {
        if (null == s_repository) {
            s_repository = Repository.create("docmgr", "My Doc Mgr", null);
        }
        return s_repository;
    }

    public static void clear() {
        s_repository = null;
    }
}

