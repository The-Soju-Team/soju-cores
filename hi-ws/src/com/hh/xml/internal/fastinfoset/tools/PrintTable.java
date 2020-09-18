/*
 * Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * THIS FILE WAS MODIFIED BY SUN MICROSYSTEMS, INC.
 */

package com.hh.xml.internal.fastinfoset.tools;

import com.hh.xml.internal.fastinfoset.QualifiedName;
import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.hh.xml.internal.fastinfoset.util.CharArrayArray;
import com.hh.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.hh.xml.internal.fastinfoset.util.PrefixArray;
import com.hh.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.hh.xml.internal.fastinfoset.util.StringArray;
import com.hh.xml.internal.fastinfoset.vocab.ParserVocabulary;


public class PrintTable {

    /** Creates a new instance of PrintTable */
    public PrintTable() {
    }

    public static void printVocabulary(ParserVocabulary vocabulary) {
        printArray("Attribute Name Table", vocabulary.attributeName);
        printArray("Attribute Value Table", vocabulary.attributeValue);
        printArray("Character Content Chunk Table", vocabulary.characterContentChunk);
        printArray("Element Name Table", vocabulary.elementName);
        printArray("Local Name Table", vocabulary.localName);
        printArray("Namespace Name Table", vocabulary.namespaceName);
        printArray("Other NCName Table", vocabulary.otherNCName);
        printArray("Other String Table", vocabulary.otherString);
        printArray("Other URI Table", vocabulary.otherURI);
        printArray("Prefix Table", vocabulary.prefix);
    }

    public static void printArray(String title, StringArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }
    }

    public static void printArray(String title, PrefixArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }
    }

    public static void printArray(String title, CharArrayArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }
    }

    public static void printArray(String title, ContiguousCharArrayArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            System.out.println("" + (i + 1) + ": " + a.getString(i));
        }
    }

    public static void printArray(String title, QualifiedNameArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            QualifiedName name = a.getArray()[i];
            System.out.println("" + (name.index + 1) + ": " +
                    "{" + name.namespaceName + "}" +
                    name.prefix + ":" + name.localName);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);

            SAXParser saxParser = saxParserFactory.newSAXParser();

            ParserVocabulary referencedVocabulary = new ParserVocabulary();

            VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(referencedVocabulary);
            File f = new File(args[0]);
            saxParser.parse(f, vocabularyGenerator);

            printVocabulary(referencedVocabulary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
