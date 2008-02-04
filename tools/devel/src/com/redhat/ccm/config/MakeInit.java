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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class MakeInit {

    public static void main(String args[]) {
        try {
            BufferedReader conf = new BufferedReader(new FileReader(args[0]));

            TreeMap vars = new TreeMap();
            ConfigHelper.loadVariables(conf, vars);
            conf.close();

            BufferedWriter out1 = new BufferedWriter(new FileWriter(args[1]));
            BufferedWriter out2 = new BufferedWriter(new FileWriter(args[2]));

            for (int i = 3 ; i < args.length ; i++) {
                BufferedReader in = new BufferedReader(new FileReader(args[i]));

                interpolate(in, out1, out2, vars);

                in.close();
            }

            out1.close();
            out2.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }


    public static void interpolate(BufferedReader in,
                                   BufferedWriter out1,
                                   BufferedWriter out2,
                                   TreeMap vars)
        throws IOException {

        String line;
        while ((line = in.readLine()) != null) {
            String result = line.startsWith("//") ?
                line :
                ConfigHelper.interpolate(line, vars);
            out1.write(result, 0, result.length());
            out1.newLine();
            out2.write(line, 0, line.length());
            out2.newLine();
        }
    }

}
