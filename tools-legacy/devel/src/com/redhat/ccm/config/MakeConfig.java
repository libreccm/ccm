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


import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class MakeConfig {

    public static void main(String args[]) {
        try {
            TreeMap vars[] = new TreeMap[args.length];
            String labels[] = new String[args.length];

            for (int i = 1 ; i < args.length ; i++) {
                BufferedReader in = new BufferedReader(new FileReader(args[i]));
                TreeMap theseVars = new TreeMap();

                labels[i - 1] = ConfigHelper.extractVariables(in, theseVars);

                vars[i - 1] = theseVars;

                in.close();
            }

            String projectVars = System.getProperty("projectVars");
            TreeMap theseVars = new TreeMap();
            ConfigHelper.loadVariablesFromString(projectVars,theseVars);
            labels[args.length - 1] = "System Properties";
            vars[args.length - 1] = theseVars;

            ConfigHelper.interpolateVars(vars, vars);

            BufferedWriter out = new BufferedWriter(new FileWriter(args[0]));
            printVariables(out, vars, labels);
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
                                      TreeMap[] vars,
                                      String[] labels)
        throws IOException {

        HashSet allvars = new HashSet();
        String text = "";

        for (int i = vars.length - 1; i >= 0; i--) {
            StringBuffer sb = new StringBuffer();

            if (labels[i] != null && vars[i].size() > 0) {
                sb.append("# " + labels[i] + System.getProperty("line.separator"));
            }

            Iterator keys = vars[i].keySet().iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                if (! allvars.contains(key)) { 
                    allvars.add(key);
                    String value = (String)vars[i].get(key);
                    String line = key + " = " + value;
                    
                    sb.append(line + System.getProperty("line.separator"));
                }
            }
            if (vars[i].size() > 0) {
                sb.append(System.getProperty("line.separator"));
            }
            text = sb + text;
        }
        out.write(text, 0, text.length());
    }
}
