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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;
import org.apache.oro.text.regex.MalformedPatternException;

public class ConfigHelper {

    public static void loadVariables(BufferedReader in,
                                     Map vars)
        throws IOException {

        Perl5Util perl5 = new Perl5Util();

        String line;
        int number = 0;
        while ((line = in.readLine()) != null) {
            number++;

            if (perl5.match("/^\\s*$/", line) ||
                perl5.match("/^\\s*#/", line)) {
                continue;
            } else if (perl5.match("/\\s*((?:\\w|-)+)\\s*=\\s*(.*?)\\s*$/", line)) {
                MatchResult result = perl5.getMatch();
                String key = result.group(1);
                String value = result.group(2);

                vars.put(key, value);
            } else {
                throw new RuntimeException("syntax error in config file at " + 
                                           "line " + line);
            }
        }
    }

    public static String extractVariables(BufferedReader in,
                                          Map vars)
        throws IOException {

        String firstLine = "";
        Perl5Util perl5 = new Perl5Util();

        String line;
        int number = 0;
        while ((line = in.readLine()) != null) {
            if (perl5.match("/^\\s*\\/\\/\\s*::((?:\\w|-)+)::\\s*->\\s*(.*?)\\s*$/", 
                            line)) {
                MatchResult result = perl5.getMatch();
                String key = result.group(1);
                String value = result.group(2);

                if (!vars.containsKey(key)) {
                    vars.put(key, value);
                }
            } else if (perl5.match("/^\\s*\\/\\/\\s*(.*?)\\s*$/",
                                   line) && number == 0) {
                MatchResult result = perl5.getMatch();
                firstLine = result.group(1);
            }
            number++;
        }
        return (firstLine);
    }


    public static String interpolate(String text,
                                     Map vars) {
        HashSubstitution subst = new HashSubstitution(vars);
        Perl5Matcher matcher = new Perl5Matcher();
        Perl5Compiler compiler = new Perl5Compiler();
        StringBuffer result = new StringBuffer();
        PatternMatcherInput input = new PatternMatcherInput(text);

        try {
            Util.substitute(result,
                            matcher,
                            compiler.compile("(::(?:\\w|-)+::)"),
                            subst,
                            input,
                            Util.SUBSTITUTE_ALL);
        } catch (MalformedPatternException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot perform substitution: " + 
                                       e.getMessage());
        }
        return result.toString();
    }

    public static void loadVariablesFromString(String in, Map vars) {
        Perl5Util perl5 = new Perl5Util();
        ArrayList arraylist = new ArrayList();

        if (in != null) {
            perl5.split(arraylist, "/,/", in);

            Iterator iter = arraylist.iterator();
            while (iter.hasNext()) {
                String key = (String)iter.next();
                if (!iter.hasNext()) {
                    throw new RuntimeException("mismatched number of " + 
                                               "elements in: " + in);
                }
                String value = (String)iter.next();
                vars.put(key, value);
            }
        }
    }

    public static void interpolateVars(Map vars1, Map vars2) {
        Iterator keys = vars1.keySet().iterator();

        while (keys.hasNext()) {
            Object key = keys.next();
            vars1.put(key, interpolate((String)vars1.get(key), vars2));
        }
    }

    public static void interpolateVars(Map[] vars1, Map[] vars2) {
        Map combined = new TreeMap();
        for (int i = 0; i < vars2.length; i++) {
            combined.putAll(vars2[i]);
        }

        for (int i = 0; i < vars1.length; i++) {
        
            Iterator keys = vars1[i].keySet().iterator();

            while (keys.hasNext()) {
                Object key = keys.next();
                vars1[i].put(key, interpolate((String)vars1[i].get(key), combined));
            }
        }
    }


    private static class HashSubstitution implements Substitution {
        private Map m_hash;

        public HashSubstitution(Map hash) {
            m_hash = hash;
        }

        public void appendSubstitution(StringBuffer appendBuffer,
                                       MatchResult match,
                                       int substitutionCount,
                                       PatternMatcherInput originalInput,
                                       PatternMatcher matcher,
                                       Pattern pattern) {
            String placeholder = match.toString();
            String key = placeholder.substring(2, placeholder.length()-2);

            Object value = (m_hash.containsKey(key) ?
                            m_hash.get(key) :
                            placeholder);
            String val;
            try {
                PlaceholderValueGenerator gen = (PlaceholderValueGenerator)value;
                val = gen.generate();
            } catch (ClassCastException ex) {
                val = (String)value;
            }

            appendBuffer.append(val);
        }
    }

    public interface PlaceholderValueGenerator {
        public String generate();
    }

}
