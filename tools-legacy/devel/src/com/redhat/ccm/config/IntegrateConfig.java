/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.ccm.config;

import java.util.TreeMap;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class IntegrateConfig {
    public static void main(String args[]) {
	try {
	    BufferedReader conf = new BufferedReader(new FileReader(args[0]));

	    TreeMap vars = new TreeMap();
            ConfigHelper.loadVariables(conf, vars);
	    conf.close();
	    
	    for (int i = 2 ; i < args.length ; i++) {
		BufferedReader in = new BufferedReader(new FileReader(args[i]));
		
		ConfigHelper.extractVariables(in, vars);
		
		in.close();
	    }
	    
	    BufferedWriter out = new BufferedWriter(new FileWriter(args[1]));
	    printVariables(out, vars);
	    out.close();
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	    throw new RuntimeException(ex.getMessage());
	} catch (IOException ex) {
	    ex.printStackTrace();
	    throw new RuntimeException(ex.getMessage());
	}
    }
    

    public static void printVariables(BufferedWriter out,
				      TreeMap vars) 
	throws IOException {
	
	Iterator keys = vars.keySet().iterator();
	while (keys.hasNext()) {
	    String key = (String)keys.next();
	    String value = (String)vars.get(key);
	    
	    String line = key + " = " + value;
	    
	    out.write(line, 0, line.length());
	    out.newLine();
	}
    }

}
