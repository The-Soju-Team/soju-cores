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

package com.hh.xml.internal.fastinfoset.sax;

import com.hh.xml.internal.fastinfoset.EncodingConstants;
import com.hh.xml.internal.fastinfoset.QualifiedName;
import com.hh.xml.internal.fastinfoset.util.KeyIntMap;
import com.hh.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.hh.xml.internal.fastinfoset.util.StringIntMap;
import java.io.IOException;
import java.util.HashMap;
import org.xml.sax.SAXException;
import java.util.Map;
import com.hh.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.hh.xml.internal.org.jvnet.fastinfoset.RestrictedAlphabet;
import com.hh.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.xml.sax.Attributes;

/**
 * The Fast Infoset SAX serializer that maps prefixes to user specified prefixes
 * that are specified in a namespace URI to prefix map.
 * <p>
 * This serializer will not preserve the original prefixes and this serializer
 * should not be used when prefixes need to be preserved, such as the case
 * when there are qualified names in content.
 * <p>
 * A namespace URI to prefix map is utilized such that the prefixes
 * in the map are utilized rather than the prefixes specified in
 * the qualified name for elements and attributes.
 * <p>
 * Any namespace declarations with a namespace URI that is not present in
 * the map are added.
 * <p>
 */
public class SAXDocumentSerializerWithPrefixMapping extends SAXDocumentSerializer {
    protected Map _namespaceToPrefixMapping;
    protected Map _prefixToPrefixMapping;
    protected String _lastCheckedNamespace;
    protected String _lastCheckedPrefix;

    protected StringIntMap _declaredNamespaces;

    public SAXDocumentSerializerWithPrefixMapping(Map namespaceToPrefixMapping) {
        // Use the local name to look up elements/attributes
        super(true);
        _namespaceToPrefixMapping = new HashMap(namespaceToPrefixMapping);
        _prefixToPrefixMapping = new HashMap();

        // Empty prefix
        _namespaceToPrefixMapping.put("", "");
        // 'xml' prefix
        _namespaceToPrefixMapping.put(EncodingConstants.XML_NAMESPACE_NAME, EncodingConstants.XML_NAMESPACE_PREFIX);

        _declaredNamespaces = new StringIntMap(4);
    }

    public final void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            if (_elementHasNamespaces == false) {
                encodeTermination();

                // Mark the current buffer position to flag attributes if necessary
                mark();
                _elementHasNamespaces = true;

                // Write out Element byte with namespaces
                write(EncodingConstants.ELEMENT | EncodingConstants.ELEMENT_NAMESPACES_FLAG);

                _declaredNamespaces.clear();
                _declaredNamespaces.obtainIndex(uri);
            } else {
                if (_declaredNamespaces.obtainIndex(uri) != KeyIntMap.NOT_PRESENT) {
                    final String p = getPrefix(uri);
                    if (p != null) {
                        _prefixToPrefixMapping.put(prefix, p);
                    }
                    return;
                }
            }

            final String p = getPrefix(uri);
            if (p != null) {
                encodeNamespaceAttribute(p, uri);
                _prefixToPrefixMapping.put(prefix, p);
            } else {
                putPrefix(uri, prefix);
                encodeNamespaceAttribute(prefix, uri);
            }

        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }

    protected final void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.elementName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            if (encodeElementMapEntry(entry, namespaceURI)) return;
            // Check the entry is a member of the read only map
            if (_v.elementName.isQNameFromReadOnlyMap(entry._value[0])) {
                entry = _v.elementName.obtainDynamicEntry(localName);
                if (entry._valueIndex > 0) {
                    if (encodeElementMapEntry(entry, namespaceURI)) return;
                }
            }
        }

        encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, getPrefix(namespaceURI),
                localName, entry);
    }

    protected boolean encodeElementMapEntry(LocalNameQualifiedNamesMap.Entry entry, String namespaceURI) throws IOException {
        QualifiedName[] names = entry._value;
        for (int i = 0; i < entry._valueIndex; i++) {
            if ((namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                encodeNonZeroIntegerOnThirdBit(names[i].index);
                return true;
            }
        }
        return false;
    }


    protected final void encodeAttributes(Attributes atts) throws IOException, FastInfosetException {
        boolean addToTable;
        boolean mustToBeAddedToTable;
        String value;
        if (atts instanceof EncodingAlgorithmAttributes) {
            final EncodingAlgorithmAttributes eAtts = (EncodingAlgorithmAttributes)atts;
            Object data;
            String alphabet;
            for (int i = 0; i < eAtts.getLength(); i++) {
                final String uri = atts.getURI(i);
                if (encodeAttribute(uri, atts.getQName(i), atts.getLocalName(i))) {
                    data = eAtts.getAlgorithmData(i);
                    // If data is null then there is no algorithm data
                    if (data == null) {
                        value = eAtts.getValue(i);
                        addToTable = isAttributeValueLengthMatchesLimit(value.length());
                        mustToBeAddedToTable = eAtts.getToIndex(i);
                        alphabet = eAtts.getAlpababet(i);
                        if (alphabet == null) {
                            if (uri == "http://www.w3.org/2001/XMLSchema-instance" ||
                                    uri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                                value = convertQName(value);
                            }
                            encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable, mustToBeAddedToTable);
                        } else if (alphabet == RestrictedAlphabet.DATE_TIME_CHARACTERS) {
                            encodeDateTimeNonIdentifyingStringOnFirstBit(
                                    value, addToTable, mustToBeAddedToTable);
                        } else if (alphabet == RestrictedAlphabet.NUMERIC_CHARACTERS) {
                            encodeNumericNonIdentifyingStringOnFirstBit(
                                    value, addToTable, mustToBeAddedToTable);
                        } else {
                            encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable, mustToBeAddedToTable);
                        }
                    } else {
                        encodeNonIdentifyingStringOnFirstBit(eAtts.getAlgorithmURI(i),
                                eAtts.getAlgorithmIndex(i), data);
                    }
                }
            }
        } else {
            for (int i = 0; i < atts.getLength(); i++) {
                final String uri = atts.getURI(i);
                if (encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) {
                    value = atts.getValue(i);
                    addToTable = isAttributeValueLengthMatchesLimit(value.length());

                    if (uri == "http://www.w3.org/2001/XMLSchema-instance" ||
                            uri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                        value = convertQName(value);
                    }
                    encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable, false);
                }
            }
        }
        _b = EncodingConstants.TERMINATOR;
        _terminate = true;
    }

    private String convertQName(String qName) {
        int i = qName.indexOf(':');
        String prefix = "";
        String localName = qName;
        if (i != -1) {
            prefix = qName.substring(0, i);
            localName = qName.substring(i + 1);
        }

        String p = (String)_prefixToPrefixMapping.get(prefix);
        if (p != null) {
            if (p.length() == 0)
                return localName;
            else
                return p + ":" + localName;
        } else {
            return qName;
        }
    }

    protected final boolean encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.attributeName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            if (encodeAttributeMapEntry(entry, namespaceURI)) return true;
            // Check the entry is a member of the read only map
            if (_v.attributeName.isQNameFromReadOnlyMap(entry._value[0])) {
                entry = _v.attributeName.obtainDynamicEntry(localName);
                if (entry._valueIndex > 0) {
                    if (encodeAttributeMapEntry(entry, namespaceURI)) return true;
                }
            }
        }

        return encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, getPrefix(namespaceURI),
                localName, entry);
    }

    protected boolean encodeAttributeMapEntry(LocalNameQualifiedNamesMap.Entry entry, String namespaceURI) throws IOException {
        QualifiedName[] names = entry._value;
        for (int i = 0; i < entry._valueIndex; i++) {
            if ((namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                return true;
            }
        }
        return false;
    }

    protected final String getPrefix(String namespaceURI) {
        if (_lastCheckedNamespace == namespaceURI) return _lastCheckedPrefix;

        _lastCheckedNamespace = namespaceURI;
        return _lastCheckedPrefix = (String)_namespaceToPrefixMapping.get(namespaceURI);
    }

    protected final void putPrefix(String namespaceURI, String prefix) {
        _namespaceToPrefixMapping.put(namespaceURI, prefix);

        _lastCheckedNamespace = namespaceURI;
        _lastCheckedPrefix = prefix;
    }
}
